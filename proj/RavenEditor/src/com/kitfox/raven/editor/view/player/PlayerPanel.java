/*
 * Copyright 2011 Mark McKay
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * TrackPlayerPanel.java
 *
 * Created on Aug 11, 2009, 3:22:23 AM
 */

package com.kitfox.raven.editor.view.player;

import com.kitfox.raven.editor.RavenDocument;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.RavenEditorListener;
import com.kitfox.raven.editor.RavenEditorWeakListener;
import com.kitfox.raven.util.tree.ChildWrapperEvent;
import com.kitfox.raven.util.tree.NodeDocumentEvent;
import com.kitfox.raven.util.tree.NodeDocumentListener;
import com.kitfox.raven.util.tree.NodeDocumentWeakListener;
import com.kitfox.raven.util.tree.NodeSymbol;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.NodeObjectListener;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.tree.NodeObjectProviderIndex;
import com.kitfox.raven.util.tree.NodeObjectWeakListener;
import com.kitfox.raven.util.tree.PropertyDataReference;
import com.kitfox.raven.util.tree.PropertyTrackChangeEvent;
import com.kitfox.raven.util.tree.PropertyTrackKeyChangeEvent;
import com.kitfox.raven.util.tree.PropertyWrapperListener;
import com.kitfox.raven.util.tree.PropertyWrapperWeakListener;
import com.kitfox.raven.util.tree.Track;
import com.kitfox.raven.util.tree.TrackLibrary;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.util.EventObject;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

/**
 *
 * @author kitfox
 */
public class PlayerPanel extends javax.swing.JPanel
        implements RavenEditorListener, NodeDocumentListener,
        PropertyWrapperListener
{
    private static final long serialVersionUID = 1;

    final RavenEditor editor;
    RavenEditorWeakListener listenerEditor;
    NodeDocumentWeakListener listenerRavenDoc;
    PropertyWrapperWeakListener listenerTrackLibFrame;
    PropertyWrapperWeakListener listenerTrackLibTrack;
    PropertyWrapperWeakListener listenerTrackLibFps;

    TrackLibManager trackLibManager;

    int updating = 0;


    /** Creates new form TrackPlayerPanel */
    public PlayerPanel(RavenEditor editor)
    {
        this.editor = editor;

        initComponents();

        listenerEditor = new RavenEditorWeakListener(this, editor);
        editor.addRavenEditorListener(listenerEditor);
        updateDocument();

        combo_track.setRenderer(new TrackRenderer());
    }

    @Override
    public void recentFilesChanged(EventObject evt)
    {
    }

    @Override
    public void documentChanged(EventObject evt)
    {
        updateDocument();
    }

    @Override
    public void propertyWrapperDataChanged(PropertyChangeEvent evt)
    {
        updateDocument();
    }

    @Override
    public void propertyWrapperTrackChanged(PropertyTrackChangeEvent evt)
    {
    }

    @Override
    public void propertyWrapperTrackKeyChanged(PropertyTrackKeyChangeEvent evt)
    {
    }

    private void updateDocument()
    {
        if (listenerRavenDoc != null)
        {
            listenerRavenDoc.remove();
            listenerRavenDoc = null;
        }

        RavenDocument doc = editor.getDocument();
        if (doc != null)
        {
            listenerRavenDoc = new NodeDocumentWeakListener(this, doc);
            doc.addNodeDocumentListener(listenerRavenDoc);
        }
        
        
        updateSymbol();
    }

    private void updateSymbol()
    {
        if (trackLibManager != null)
        {
            trackLibManager.dispose();
            trackLibManager = null;

            listenerTrackLibTrack.remove();
            listenerTrackLibTrack = null;

            listenerTrackLibFrame.remove();
            listenerTrackLibFrame = null;

            listenerTrackLibFps.remove();
            listenerTrackLibFps = null;
        }

        RavenDocument doc = editor.getDocument();
        if (doc != null)
        {
            NodeSymbol root = doc.getCurSymbol();
            TrackLibrary trackLib = root.getRoot().getTrackLibrary();
            trackLibManager = new TrackLibManager(trackLib);

            listenerTrackLibFrame = new PropertyWrapperWeakListener(this, trackLib.curFrame);
            trackLib.curFrame.addPropertyWrapperListener(listenerTrackLibFrame);

            listenerTrackLibTrack = new PropertyWrapperWeakListener(this, trackLib.curTrack);
            trackLib.curTrack.addPropertyWrapperListener(listenerTrackLibTrack);

            listenerTrackLibFps = new PropertyWrapperWeakListener(this, trackLib.fps);
            trackLib.fps.addPropertyWrapperListener(listenerTrackLibFps);
        }

        SwingUtilities.invokeLater(
            new Runnable() {
                @Override
                public void run()
                {
                    updateModelSwing();
                }
            }
        );
    }
    
    private void updateModelSwing()
    {
        ++updating;

        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            text_curFrame.setText("");
            text_firstFrame.setText("");
            text_fps.setText("");
            text_lastFrame.setText("");
            slider_frame.setEnabled(false);
            combo_track.removeAllItems();
            --updating;
            return;
        }


        TrackLibrary trackLib = doc.getCurSymbol().getRoot().getTrackLibrary();
        text_curFrame.setText("" + trackLib.curFrame.getValue());
        text_fps.setText("" + trackLib.fps.getValue());
        check_loop.setSelected(trackLib.loop.getValue());
        combo_track.removeAllItems();
        for (int i = 0; i < trackLib.tracks.size(); ++i)
        {
            combo_track.addItem(trackLib.tracks.get(i));
        }

        Track track = trackLib.getCurTrack();
        combo_track.setSelectedItem(track);
        if (track == null)
        {
            text_firstFrame.setText("");
            text_lastFrame.setText("");
            slider_frame.setEnabled(false);
        }
        else
        {
            int min = track.frameStart.getValue();
            int max = track.frameEnd.getValue();
            text_firstFrame.setText("" + min);
            text_lastFrame.setText("" + max);
            slider_frame.setEnabled(true);
            slider_frame.setMinimum(min);
            slider_frame.setMaximum(max);
            updateTickSpacing();
            slider_frame.setValue(trackLib.getCurFrame());
        }

        --updating;
    }

    private void createNewTrack()
    {
        if (trackLibManager == null)
        {
            return;
        }

        String name = JOptionPane.showInputDialog(this, "Create New Track", "Untitled");
        if (name == null)
        {
            return;
        }

        NodeSymbol doc = trackLibManager.trackLib.getSymbol();
        doc.getHistory().beginTransaction("New track");
        NodeObjectProvider<Track> prov =
                NodeObjectProviderIndex.inst().getProvider(Track.class);
        Track newTrack = prov.createNode(doc);

        name = doc.createUniqueName(name);
        newTrack.setName(name);
        trackLibManager.trackLib.tracks.add(newTrack);
        trackLibManager.trackLib.curTrack.setData(
                new PropertyDataReference<Track>(newTrack.getUid()));

        doc.getHistory().commitTransaction();
    }

    private void renameCurTrack()
    {
        Track track = (Track)combo_track.getSelectedItem();
        if (track == null)
        {
            return;
        }

        String name = JOptionPane.showInputDialog(this, "Rename Track", track.getName());
        if (name == null)
        {
            return;
        }

        track.setName(name);
    }

    private void deleteCurTrack()
    {
        Track track = (Track)combo_track.getSelectedItem();
        if (track == null)
        {
            return;
        }

        TrackLibrary lib = (TrackLibrary)track.getParent().getNode();
        lib.tracks.remove(track);
    }

    private void pause()
    {
        editor.getPlayer().pause();
    }

    private void playBackward()
    {
        editor.getPlayer().playBackward();
    }

    private void playForward()
    {
        editor.getPlayer().playForward();
    }

    private void setCurFrame()
    {
        if (isUpdating())
        {
            return;
        }

        Track track = (Track)combo_track.getSelectedItem();
        if (track == null)
        {
            return;
        }
        TrackLibrary lib = (TrackLibrary)track.getParent().getNode();

        try
        {
            int value = Integer.parseInt(text_curFrame.getText());
            lib.curFrame.setValue(value, false);
        }
        catch (NumberFormatException ex)
        {
        }
    }

    private void setFps()
    {
        if (isUpdating())
        {
            return;
        }

        Track track = (Track)combo_track.getSelectedItem();
        if (track == null)
        {
            return;
        }
        TrackLibrary lib = (TrackLibrary)track.getParent().getNode();

        try
        {
            float value = Float.parseFloat(text_fps.getText());
            lib.fps.setValue(value, false);
        }
        catch (NumberFormatException ex)
        {
        }
    }
    
    private void setLoop()
    {
        if (isUpdating())
        {
            return;
        }

        Track track = (Track)combo_track.getSelectedItem();
        if (track == null)
        {
            return;
        }
        TrackLibrary lib = (TrackLibrary)track.getParent().getNode();

        lib.loop.setValue(check_loop.isSelected(), false);
    }

    private void stepBackward()
    {
        Track track = (Track)combo_track.getSelectedItem();
        if (track == null)
        {
            return;
        }
        TrackLibrary lib = (TrackLibrary)track.getParent().getNode();

        int frameStart = track.frameStart.getValue();
        int frameEnd = track.frameEnd.getValue();
        boolean loop = lib.loop.getValue();
        int curFrame = lib.curFrame.getValue();

        int newFrame = curFrame - 1;
        if (newFrame < frameStart)
        {
            newFrame = loop ? frameEnd : frameStart;
        }
        lib.curFrame.setValue(newFrame, false);
    }

    private void stepForward()
    {
        Track track = (Track)combo_track.getSelectedItem();
        if (track == null)
        {
            return;
        }
        TrackLibrary lib = (TrackLibrary)track.getParent().getNode();

        int frameStart = track.frameStart.getValue();
        int frameEnd = track.frameEnd.getValue();
        boolean loop = lib.loop.getValue();
        int curFrame = lib.curFrame.getValue();

        int newFrame = curFrame + 1;
        if (newFrame > frameEnd)
        {
            newFrame = loop ? frameStart : frameEnd;
        }
        lib.curFrame.setValue(newFrame, false);
    }

    private void stepKeyBackward()
    {
        Track track = (Track)combo_track.getSelectedItem();
        if (track == null)
        {
            return;
        }
        TrackLibrary lib = (TrackLibrary)track.getParent().getNode();

        int frameStart = track.frameStart.getValue();
        int curFrame = lib.curFrame.getValue();

        NodeSymbol sym = lib.getSymbol();
        int newFrame = Math.max(frameStart,
                sym.getRoot().getPrevKeyFrame(curFrame, track.getUid()));

        lib.curFrame.setValue(newFrame, false);
    }

    private void stepKeyForward()
    {
        Track track = (Track)combo_track.getSelectedItem();
        if (track == null)
        {
            return;
        }
        TrackLibrary lib = (TrackLibrary)track.getParent().getNode();

        int frameEnd = track.frameEnd.getValue();
        int curFrame = lib.curFrame.getValue();

        NodeSymbol sym = lib.getSymbol();
        int newFrame = Math.min(frameEnd,
                sym.getRoot().getNextKeyFrame(curFrame, track.getUid()));

        lib.curFrame.setValue(newFrame, false);
    }

    private void toFirstFrame()
    {
        Track track = (Track)combo_track.getSelectedItem();
        if (track == null)
        {
            return;
        }
        TrackLibrary lib = (TrackLibrary)track.getParent().getNode();

        int frameStart = track.frameStart.getValue();
        lib.curFrame.setValue(frameStart, false);
    }

    private void toFrameLast()
    {
        Track track = (Track)combo_track.getSelectedItem();
        if (track == null)
        {
            return;
        }
        TrackLibrary lib = (TrackLibrary)track.getParent().getNode();

        int frameEnd = track.frameEnd.getValue();
        lib.curFrame.setValue(frameEnd, false);
    }

    private void setFrameSlider()
    {
        if (isUpdating())
        {
            return;
        }

        Track track = (Track)combo_track.getSelectedItem();
        if (track == null)
        {
            return;
        }
        TrackLibrary lib = (TrackLibrary)track.getParent().getNode();

        lib.curFrame.setValue(slider_frame.getValue(), false);
    }

    private void setFirstFrame()
    {
        if (isUpdating())
        {
            return;
        }

        Track track = (Track)combo_track.getSelectedItem();
        if (track == null)
        {
            return;
        }

        try
        {
            int value = Integer.parseInt(text_firstFrame.getText());
            track.frameStart.setValue(value);
        }
        catch (NumberFormatException ex)
        {
        }
    }

    private void setLastFrame()
    {
        if (isUpdating())
        {
            return;
        }

        Track track = (Track)combo_track.getSelectedItem();
        if (track == null)
        {
            return;
        }

        try
        {
            int value = Integer.parseInt(text_lastFrame.getText());
            track.frameEnd.setValue(value);
        }
        catch (NumberFormatException ex)
        {
        }
    }

    private void setCurTrack()
    {
        if (isUpdating())
        {
            return;
        }

        Track track = (Track)combo_track.getSelectedItem();
        if (track == null)
        {
            return;
        }
        TrackLibrary lib = (TrackLibrary)track.getParent().getNode();

        lib.curTrack.setData(new PropertyDataReference<Track>(track.getUid()));
        lib.synchDocumentToFrame();
    }

    private boolean isUpdating()
    {
        return updating > 0;
    }

    public static final int MAJOR_TICK_PIXEL_SPAN = 50;

    private void updateTickSpacing()
    {

        int min = slider_frame.getMinimum();
        int max = slider_frame.getMaximum();
        int span = max - min + 1;

        double framesPerMajTick = (float)(MAJOR_TICK_PIXEL_SPAN * span) / slider_frame.getWidth();
        double pow1 = Math.log(framesPerMajTick) / Math.log(10);
        double pow2 = Math.log(framesPerMajTick / 2) / Math.log(10);
        double pow5 = Math.log(framesPerMajTick / 5) / Math.log(10);

        double pow1Whole = Math.floor(pow1);
        double pow1Frac = pow1 - pow1Whole;

        double pow2Whole = Math.floor(pow2);
        double pow2Frac = pow2 - pow2Whole;

        double pow5Whole = Math.floor(pow5);
        double pow5Frac = pow5 - pow5Whole;

        int majTick;
        if (pow1Frac > pow2Frac && pow1Frac > pow5Frac)
        {
            majTick = (int)Math.pow(10, pow1Whole + 1);
        }
        else if (pow2Frac > pow5Frac)
        {
            majTick = 2 * (int)Math.pow(10, pow2Whole + 1);
        }
        else
        {
            majTick = 5 * (int)Math.pow(10, pow5Whole + 1);
        }

        slider_frame.setLabelTable(null);
        slider_frame.setMajorTickSpacing(majTick);
        slider_frame.setMinorTickSpacing(majTick / 5);
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        group_playCtrls = new javax.swing.ButtonGroup();
        panel_frameSlider = new javax.swing.JPanel();
        text_firstFrame = new javax.swing.JTextField();
        slider_frame = new javax.swing.JSlider();
        text_curFrame = new javax.swing.JTextField();
        text_lastFrame = new javax.swing.JTextField();
        panel_playbackCtrl = new javax.swing.JPanel();
        bn_toFirst = new javax.swing.JButton();
        bn_playBack = new javax.swing.JToggleButton();
        bn_pause = new javax.swing.JToggleButton();
        bn_playForward = new javax.swing.JToggleButton();
        bn_toLast = new javax.swing.JButton();
        check_loop = new javax.swing.JCheckBox();
        text_fps = new javax.swing.JTextField();
        bn_stepBackward = new javax.swing.JButton();
        bn_stepForward = new javax.swing.JButton();
        bn_stepKeyBackward = new javax.swing.JButton();
        bn_stepKeyForward = new javax.swing.JButton();
        panel_trackSelector = new javax.swing.JPanel();
        combo_track = new javax.swing.JComboBox();
        bn_newTrack = new javax.swing.JButton();
        bn_deleteTrack = new javax.swing.JButton();
        bn_renameTrack = new javax.swing.JButton();

        text_firstFrame.setText("0");
        text_firstFrame.setToolTipText("First Frame");
        text_firstFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_firstFrameActionPerformed(evt);
            }
        });
        text_firstFrame.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                text_firstFrameFocusLost(evt);
            }
        });

        slider_frame.setMajorTickSpacing(10);
        slider_frame.setMinorTickSpacing(1);
        slider_frame.setPaintLabels(true);
        slider_frame.setPaintTicks(true);
        slider_frame.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider_frameStateChanged(evt);
            }
        });

        text_curFrame.setText("0");
        text_curFrame.setToolTipText("Current Frame");
        text_curFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_curFrameActionPerformed(evt);
            }
        });
        text_curFrame.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                text_curFrameFocusLost(evt);
            }
        });

        text_lastFrame.setText("0");
        text_lastFrame.setToolTipText("Last Frame");
        text_lastFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_lastFrameActionPerformed(evt);
            }
        });
        text_lastFrame.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                text_lastFrameFocusLost(evt);
            }
        });

        javax.swing.GroupLayout panel_frameSliderLayout = new javax.swing.GroupLayout(panel_frameSlider);
        panel_frameSlider.setLayout(panel_frameSliderLayout);
        panel_frameSliderLayout.setHorizontalGroup(
            panel_frameSliderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_frameSliderLayout.createSequentialGroup()
                .addComponent(text_firstFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(slider_frame, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(text_curFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(text_lastFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panel_frameSliderLayout.setVerticalGroup(
            panel_frameSliderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(text_firstFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(panel_frameSliderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(text_lastFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(text_curFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(slider_frame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        bn_toFirst.setText("<<");
        bn_toFirst.setToolTipText("To First");
        bn_toFirst.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bn_toFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bn_toFirstActionPerformed(evt);
            }
        });

        group_playCtrls.add(bn_playBack);
        bn_playBack.setText("<");
        bn_playBack.setToolTipText("Play Backward");
        bn_playBack.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bn_playBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bn_playBackActionPerformed(evt);
            }
        });

        group_playCtrls.add(bn_pause);
        bn_pause.setSelected(true);
        bn_pause.setText("||");
        bn_pause.setToolTipText("Pause");
        bn_pause.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bn_pause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bn_pauseActionPerformed(evt);
            }
        });

        group_playCtrls.add(bn_playForward);
        bn_playForward.setText(">");
        bn_playForward.setToolTipText("Play Forward");
        bn_playForward.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bn_playForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bn_playForwardActionPerformed(evt);
            }
        });

        bn_toLast.setText(">>");
        bn_toLast.setToolTipText("To Last");
        bn_toLast.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bn_toLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bn_toLastActionPerformed(evt);
            }
        });

        check_loop.setSelected(true);
        check_loop.setText("Loop");
        check_loop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                check_loopActionPerformed(evt);
            }
        });

        text_fps.setText("0");
        text_fps.setToolTipText("FPS");
        text_fps.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_fpsActionPerformed(evt);
            }
        });
        text_fps.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                text_fpsFocusLost(evt);
            }
        });

        bn_stepBackward.setText("|<");
        bn_stepBackward.setToolTipText("Back one frame");
        bn_stepBackward.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bn_stepBackward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bn_stepBackwardActionPerformed(evt);
            }
        });

        bn_stepForward.setText(">|");
        bn_stepForward.setToolTipText("Forward one frame");
        bn_stepForward.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bn_stepForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bn_stepForwardActionPerformed(evt);
            }
        });

        bn_stepKeyBackward.setText("#<");
        bn_stepKeyBackward.setToolTipText("Last Key Frame");
        bn_stepKeyBackward.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bn_stepKeyBackward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bn_stepKeyBackwardActionPerformed(evt);
            }
        });

        bn_stepKeyForward.setText(">#");
        bn_stepKeyForward.setToolTipText("Next Key Frame");
        bn_stepKeyForward.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bn_stepKeyForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bn_stepKeyForwardActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_playbackCtrlLayout = new javax.swing.GroupLayout(panel_playbackCtrl);
        panel_playbackCtrl.setLayout(panel_playbackCtrlLayout);
        panel_playbackCtrlLayout.setHorizontalGroup(
            panel_playbackCtrlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_playbackCtrlLayout.createSequentialGroup()
                .addComponent(bn_toFirst)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bn_stepKeyBackward)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bn_stepBackward)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bn_playBack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bn_pause)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bn_playForward)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bn_stepForward)
                .addGap(6, 6, 6)
                .addComponent(bn_stepKeyForward)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bn_toLast)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(check_loop)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(text_fps, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(226, Short.MAX_VALUE))
        );
        panel_playbackCtrlLayout.setVerticalGroup(
            panel_playbackCtrlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_playbackCtrlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(bn_toFirst)
                .addComponent(bn_playBack)
                .addComponent(bn_pause)
                .addComponent(bn_playForward)
                .addComponent(bn_toLast)
                .addComponent(check_loop)
                .addComponent(text_fps, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(bn_stepBackward)
                .addComponent(bn_stepForward)
                .addComponent(bn_stepKeyForward)
                .addComponent(bn_stepKeyBackward))
        );

        combo_track.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_trackActionPerformed(evt);
            }
        });

        bn_newTrack.setText("New");
        bn_newTrack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bn_newTrackActionPerformed(evt);
            }
        });

        bn_deleteTrack.setText("Delete");
        bn_deleteTrack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bn_deleteTrackActionPerformed(evt);
            }
        });

        bn_renameTrack.setText("Rename");
        bn_renameTrack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bn_renameTrackActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_trackSelectorLayout = new javax.swing.GroupLayout(panel_trackSelector);
        panel_trackSelector.setLayout(panel_trackSelectorLayout);
        panel_trackSelectorLayout.setHorizontalGroup(
            panel_trackSelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_trackSelectorLayout.createSequentialGroup()
                .addComponent(combo_track, 0, 355, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bn_newTrack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bn_renameTrack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bn_deleteTrack))
        );
        panel_trackSelectorLayout.setVerticalGroup(
            panel_trackSelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_trackSelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(combo_track, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(bn_deleteTrack)
                .addComponent(bn_renameTrack)
                .addComponent(bn_newTrack))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel_trackSelector, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel_frameSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel_playbackCtrl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel_trackSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_frameSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_playbackCtrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void bn_newTrackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bn_newTrackActionPerformed
        createNewTrack();
    }//GEN-LAST:event_bn_newTrackActionPerformed

    private void bn_deleteTrackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bn_deleteTrackActionPerformed
        deleteCurTrack();
    }//GEN-LAST:event_bn_deleteTrackActionPerformed


    private void text_firstFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_firstFrameActionPerformed
        setFirstFrame();
    }//GEN-LAST:event_text_firstFrameActionPerformed

    private void text_firstFrameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_text_firstFrameFocusLost
        setFirstFrame();
    }//GEN-LAST:event_text_firstFrameFocusLost

    private void text_curFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_curFrameActionPerformed
        setCurFrame();
    }//GEN-LAST:event_text_curFrameActionPerformed

    private void text_curFrameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_text_curFrameFocusLost
        setCurFrame();
    }//GEN-LAST:event_text_curFrameFocusLost

    private void text_lastFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_lastFrameActionPerformed
        setLastFrame();
    }//GEN-LAST:event_text_lastFrameActionPerformed

    private void text_lastFrameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_text_lastFrameFocusLost
        setLastFrame();
    }//GEN-LAST:event_text_lastFrameFocusLost

    private void combo_trackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_trackActionPerformed
        setCurTrack();        
    }//GEN-LAST:event_combo_trackActionPerformed

    private void slider_frameStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider_frameStateChanged
        setFrameSlider();
}//GEN-LAST:event_slider_frameStateChanged

    private void bn_toFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bn_toFirstActionPerformed
        toFirstFrame();
    }//GEN-LAST:event_bn_toFirstActionPerformed

    private void bn_playBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bn_playBackActionPerformed
        playBackward();
    }//GEN-LAST:event_bn_playBackActionPerformed

    private void bn_pauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bn_pauseActionPerformed
        pause();
    }//GEN-LAST:event_bn_pauseActionPerformed

    private void bn_playForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bn_playForwardActionPerformed
        playForward();
    }//GEN-LAST:event_bn_playForwardActionPerformed

    private void bn_toLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bn_toLastActionPerformed
        toFrameLast();
    }//GEN-LAST:event_bn_toLastActionPerformed

    private void check_loopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_check_loopActionPerformed
        setLoop();
    }//GEN-LAST:event_check_loopActionPerformed

    private void text_fpsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_fpsActionPerformed
        setFps();
    }//GEN-LAST:event_text_fpsActionPerformed

    private void text_fpsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_text_fpsFocusLost
        setFps();
    }//GEN-LAST:event_text_fpsFocusLost

    private void bn_stepBackwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bn_stepBackwardActionPerformed
        stepBackward();
    }//GEN-LAST:event_bn_stepBackwardActionPerformed

    private void bn_stepForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bn_stepForwardActionPerformed
        stepForward();
    }//GEN-LAST:event_bn_stepForwardActionPerformed

    private void bn_renameTrackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bn_renameTrackActionPerformed
        renameCurTrack();
    }//GEN-LAST:event_bn_renameTrackActionPerformed

    private void bn_stepKeyBackwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bn_stepKeyBackwardActionPerformed
        stepKeyBackward();
    }//GEN-LAST:event_bn_stepKeyBackwardActionPerformed

    private void bn_stepKeyForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bn_stepKeyForwardActionPerformed
        stepKeyForward();
    }//GEN-LAST:event_bn_stepKeyForwardActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bn_deleteTrack;
    private javax.swing.JButton bn_newTrack;
    private javax.swing.JToggleButton bn_pause;
    private javax.swing.JToggleButton bn_playBack;
    private javax.swing.JToggleButton bn_playForward;
    private javax.swing.JButton bn_renameTrack;
    private javax.swing.JButton bn_stepBackward;
    private javax.swing.JButton bn_stepForward;
    private javax.swing.JButton bn_stepKeyBackward;
    private javax.swing.JButton bn_stepKeyForward;
    private javax.swing.JButton bn_toFirst;
    private javax.swing.JButton bn_toLast;
    private javax.swing.JCheckBox check_loop;
    private javax.swing.JComboBox combo_track;
    private javax.swing.ButtonGroup group_playCtrls;
    private javax.swing.JPanel panel_frameSlider;
    private javax.swing.JPanel panel_playbackCtrl;
    private javax.swing.JPanel panel_trackSelector;
    private javax.swing.JSlider slider_frame;
    private javax.swing.JTextField text_curFrame;
    private javax.swing.JTextField text_firstFrame;
    private javax.swing.JTextField text_fps;
    private javax.swing.JTextField text_lastFrame;
    // End of variables declaration//GEN-END:variables
    // End of variables declaration

    @Override
    public void symbolAdded(NodeDocumentEvent evt)
    {
    }

    @Override
    public void symbolRemoved(NodeDocumentEvent evt)
    {
    }

    @Override
    public void currentSymbolChanged(NodeDocumentEvent evt)
    {
        updateSymbol();
    }


    //------------------------------
    class TrackManager implements NodeObjectListener
    {
        NodeObjectWeakListener listenerTrack;
        final Track track;

        public TrackManager(Track track)
        {
            this.track = track;
            listenerTrack = new NodeObjectWeakListener(this, track);
            track.addNodeObjectListener(listenerTrack);
        }

        @Override
        public void nodeNameChanged(EventObject evt)
        {
            repaint();
        }

        @Override
        public void nodePropertyChanged(PropertyChangeEvent evt)
        {
            if (Track.PROP_FRAMESTART.equals(evt.getPropertyName()))
            {
                ++updating;
                int frameStart = track.frameStart.getValue();
                int frameEnd = track.frameEnd.getValue();
                text_firstFrame.setText("" + frameStart);
                slider_frame.setMinimum(
                        Math.min(slider_frame.getMaximum(), frameStart));
                updateTickSpacing();
                --updating;
            }
            if (Track.PROP_FRAMEEND.equals(evt.getPropertyName()))
            {
                ++updating;
                int frameStart = track.frameStart.getValue();
                int frameEnd = track.frameEnd.getValue();
                text_lastFrame.setText("" + frameEnd);
                slider_frame.setMaximum(
                        Math.max(slider_frame.getMinimum(), frameEnd));
                updateTickSpacing();
                --updating;
            }
        }

        @Override
        public void nodeChildAdded(ChildWrapperEvent evt)
        {
        }

        @Override
        public void nodeChildRemoved(ChildWrapperEvent evt)
        {
        }

        private void dispose()
        {
            listenerTrack.remove();
        }
    }

    class TrackLibManager implements NodeObjectListener
    {
        NodeObjectWeakListener listenerTrackLib;
        final TrackLibrary trackLib;

        TrackManager trackManager;

        public TrackLibManager(TrackLibrary trackLib)
        {
            this.trackLib = trackLib;
            listenerTrackLib = new NodeObjectWeakListener(this, trackLib);
            trackLib.addNodeObjectListener(listenerTrackLib);

            Track track = trackLib.curTrack.getValue();
            if (track != null)
            {
                trackManager = new TrackManager(track);
            }
        }

        @Override
        public void nodeNameChanged(EventObject evt)
        {
        }

        @Override
        public void nodePropertyChanged(PropertyChangeEvent evt)
        {
            if (TrackLibrary.PROP_CURFRAME.equals(evt.getPropertyName()))
            {
                ++updating;
                int value = trackLib.getCurFrame();
                text_curFrame.setText("" + value);
                slider_frame.setValue(value);
                --updating;
            }
            else if (TrackLibrary.PROP_FPS.equals(evt.getPropertyName()))
            {
                ++updating;
                text_fps.setText("" + trackLib.fps.getValue());
                --updating;
            }
            else if (TrackLibrary.PROP_LOOP.equals(evt.getPropertyName()))
            {
                ++updating;
                check_loop.setSelected(trackLib.loop.getValue());
                --updating;
            }
            else if (TrackLibrary.PROP_CURTRACK.equals(evt.getPropertyName()))
            {
                if (trackManager != null)
                {
                    trackManager.dispose();
                    trackManager = null;
                }

                Track track = trackLib.curTrack.getValue();
                if (track != null)
                {
                    if (track != null)
                    {
                        trackManager = new TrackManager(track);
                    }

                    ++updating;
                    combo_track.setSelectedItem(track);
                    --updating;
                }
            }
        }

        @Override
        public void nodeChildAdded(ChildWrapperEvent evt)
        {
            buildTrackCombo();
        }

        @Override
        public void nodeChildRemoved(ChildWrapperEvent evt)
        {
            buildTrackCombo();
        }

        private void buildTrackCombo()
        {
            ++updating;

            combo_track.removeAllItems();
            for (int i = 0; i < trackLib.tracks.size(); ++i)
            {
                NodeObject obj = trackLib.tracks.get(i);
                combo_track.addItem(obj);
            }
            combo_track.setSelectedItem(trackLib.curTrack.getValue());

            --updating;
        }

        private void dispose()
        {
            listenerTrackLib.remove();
        }

    }

    class TrackRenderer extends JLabel implements ListCellRenderer
    {
        private static final long serialVersionUID = 1;

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
        {
            if (value instanceof String)
            {
                //Empty lists will provide an empty string
                setText("");
                return this;
            }

            Track track = (Track)value;
            setOpaque(true);
            if (isSelected)
            {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else
            {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setText(track == null ? "" : track.getName());
            return this;
        }
    }

}
