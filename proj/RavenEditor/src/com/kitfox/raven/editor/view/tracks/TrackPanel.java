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
 * TrackEditorPanel.java
 *
 * Created on Aug 20, 2009, 3:31:03 AM
 */

package com.kitfox.raven.editor.view.tracks;

import com.kitfox.raven.editor.RavenDocument;
import com.kitfox.raven.editor.RavenDocumentEvent;
import com.kitfox.raven.editor.RavenDocumentListener;
import com.kitfox.raven.editor.RavenDocumentWeakListener;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.RavenEditorListener;
import com.kitfox.raven.editor.RavenEditorWeakListener;
import com.kitfox.raven.editor.node.tools.Tool;
import com.kitfox.raven.editor.node.tools.ToolPalette;
import com.kitfox.raven.editor.node.tools.ToolPaletteEvent;
import com.kitfox.raven.editor.node.tools.ToolPaletteListener;
import com.kitfox.raven.editor.node.tools.ToolProvider;
import com.kitfox.raven.editor.node.tools.ToolProviderIndex;
import com.kitfox.raven.editor.node.tools.common.ToolPan;
import com.kitfox.raven.editor.node.tools.common.ToolPropertyCurveEditor;
import com.kitfox.raven.editor.node.tools.common.ToolZoom;
import com.kitfox.raven.editor.view.ViewProviderListener;
import com.kitfox.raven.editor.view.ViewProviderWeakListener;
import com.kitfox.raven.util.PropertyChangeWeakListener;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.SelectionEvent;
import com.kitfox.raven.util.SelectionListener;
import com.kitfox.raven.util.SelectionSubEvent;
import com.kitfox.raven.util.SelectionWeakListener;
import com.kitfox.raven.util.tree.NodeSymbol;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.PropertyProvider;
import com.kitfox.raven.util.tree.PropertyProviderIndex;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperAdapter;
import com.kitfox.raven.util.tree.PropertyWrapperWeakListener;
import com.kitfox.raven.util.tree.SelectionRecord;
import com.kitfox.raven.util.tree.Track;
import com.kitfox.raven.util.tree.TrackCurve;
import com.kitfox.raven.util.tree.TrackLibrary;
import com.kitfox.xml.schema.ravendocumentschema.TrackTransferableType;
import com.kitfox.xml.schema.ravendocumentschema.TrackType;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.TransferHandler;

/**
 *
 * @author kitfox
 */
public class TrackPanel extends javax.swing.JPanel
        implements RavenEditorListener, RavenDocumentListener,
        ViewProviderListener, ToolPaletteListener
{
    private static final long serialVersionUID = 1;

    final RavenEditor editor;
    final TrackViewProvider provider;

    CurveEditorPanel curvePanel;

    PropertyChangeWeakListener trackPropListener;

//    boolean updating = false;
    RavenEditorWeakListener listenerEditor;
    private RavenDocumentWeakListener listenerRavenDoc;

    boolean updatingPropList;

    ToolPalette toolPalette = new ToolPalette();

    final ToolProvider TOOL_CONTROL_POINT =
            ToolProviderIndex.inst().getProvider(ToolPropertyCurveEditor.Provider.class);
    final ToolProvider TOOL_ZOOM =
            ToolProviderIndex.inst().getProvider(ToolZoom.Provider.class);
    final ToolProvider TOOL_PAN =
            ToolProviderIndex.inst().getProvider(ToolPan.Provider.class);

    ViewProviderWeakListener viewListener;

    static final String ACTION_DELETE = "delete";
    
    CurFrameWatcher curFrameWatch;
    CurTrackWatcher curTrackWatch;
    SelectionWatcher selectionWatch;

    /** Creates new form TrackEditorPanel */
    public TrackPanel(RavenEditor editor, TrackViewProvider provider)
    {
        this.editor = editor;
        this.provider = provider;

        curvePanel = new CurveEditorPanel(editor);

        listenerEditor = new RavenEditorWeakListener(this, editor);
        editor.addRavenEditorListener(listenerEditor);
        
        initComponents();

        list_curves.setCellRenderer(new Renderer());

        viewListener = new ViewProviderWeakListener(this, provider);
        provider.addViewProviderListener(viewListener);



        list_curves.setTransferHandler(new TrackTransferHandler());
        list_curves.setDropMode(DropMode.ON_OR_INSERT);

        list_curves.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), ACTION_DELETE);
        list_curves.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK),
                TransferHandler.getCutAction().getValue(Action.NAME));
        list_curves.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK),
                TransferHandler.getCopyAction().getValue(Action.NAME));
        list_curves.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK),
                TransferHandler.getPasteAction().getValue(Action.NAME));

        list_curves.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), ACTION_DELETE);
        list_curves.getActionMap().put(ACTION_DELETE,
                new AbstractAction()
                {
                    private static final long serialVersionUID = 0;
                    @Override
                    public void actionPerformed(ActionEvent evt)
                    {
                        throw new RuntimeException("Not impl");
//                        deleteSelectedCurves();
                    }
                }
        );
        list_curves.getActionMap().put(TransferHandler.getCutAction().getValue(Action.NAME),
                TransferHandler.getCutAction());
        list_curves.getActionMap().put(TransferHandler.getCopyAction().getValue(Action.NAME),
                TransferHandler.getCopyAction());
        list_curves.getActionMap().put(TransferHandler.getPasteAction().getValue(Action.NAME),
                TransferHandler.getPasteAction());



        toolPalette.addProvider(TOOL_CONTROL_POINT);
        toolPalette.addProvider(TOOL_ZOOM);
        toolPalette.addProvider(TOOL_PAN);
        toolPalette.setCurrentTool(TOOL_CONTROL_POINT);
        toolPalette.addToolPaletteListener(this);

        panel_curveArea.add(curvePanel, BorderLayout.CENTER);
//        curvePanel.setToolPalette(toolPalette);
        updateTool();
        
        updateDocument();
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
            listenerRavenDoc = new RavenDocumentWeakListener(this, doc);
            doc.addRavenDocumentListener(listenerRavenDoc);
        }
        
        updateSymbol();
    }

    private void updateSymbol()
    {
        if (curFrameWatch != null)
        {
            curFrameWatch.remove();
            curFrameWatch = null;
            curTrackWatch.remove();
            curTrackWatch = null;
            selectionWatch.remove();
            selectionWatch = null;
        }

        RavenDocument doc = editor.getDocument();
        if (doc != null)
        {
            NodeSymbol root = doc.getCurSymbol();

            TrackLibrary trackLib = root.getTrackLibrary();
            curFrameWatch = new CurFrameWatcher(trackLib.curFrame);
            curTrackWatch = new CurTrackWatcher(trackLib.curTrack);

            Selection<NodeObject> sel = root.getSelection();
            selectionWatch = new SelectionWatcher(sel);
        }

//        updateWorkTrackListener();
//        updateCurves();
        updatePropertyList();
        updateTrack();
        updateTrackTime();
    }

    private void curTrackChanged()
    {
        updateTrack();
    }

    private void ravenSelectionChanged()
    {
        updatePropertyList();
    }

    private void curFrameChanged()
    {
        updateTrackTime();
    }

    private void updateTrack()
    {
        RavenDocument doc = editor.getDocument();
        if (doc != null)
        {
            NodeSymbol root = doc.getCurSymbol();

            TrackLibrary trackLib = root.getTrackLibrary();
            Track track = trackLib.curTrack.getValue();
            curvePanel.setTrack(track);
        }
        else
        {
            curvePanel.setTrack(null);
        }
    }

    private void updateTrackTime()
    {
        RavenDocument doc = editor.getDocument();
        if (doc != null)
        {
            NodeSymbol root = doc.getCurSymbol();

            TrackLibrary trackLib = root.getTrackLibrary();
            int frame = trackLib.curFrame.getValue();
            curvePanel.setFrame(frame);
        }
    }

    private void updatePropertyList()
    {
        RavenDocument doc = editor.getDocument();
        if (doc != null)
        {
            updatingPropList = true;
            Object[] oldSelection = list_curves.getSelectedValues();

            NodeSymbol root = doc.getCurSymbol();
//            Track track = root.getTrackLibrary().curTrack.getValue();

            Selection<NodeObject> sel = root.getSelection();

            ArrayList<PropertyWrapper> propList = new ArrayList<PropertyWrapper>();

            for (NodeObject node: sel.getSelection())
            {
                for (PropertyWrapper wrapper: node.getPropertyWrappers())
                {
                    Class type = wrapper.getPropertyType();
                    PropertyProvider prov = PropertyProviderIndex.inst().getProviderBest(type);
                    if (prov != null && prov.isNumeric())
                    {
                        propList.add(wrapper);
                    }
                }
            }

            list_curves.setListData(propList.toArray());
            
            //Preserve selection
            HashSet selSet = new HashSet();
            selSet.addAll(Arrays.asList(oldSelection));
            ArrayList<Integer> indices = new ArrayList<Integer>();
            for (int i = 0; i < propList.size(); ++i)
            {                
                if (selSet.contains(propList.get(i)))
                {
                    indices.add(i);
                }
            }
            int[] idxList = new int[indices.size()];
            for (int i = 0; i < indices.size(); ++i)
            {
                idxList[i] = indices.get(i);
            }
            list_curves.setSelectedIndices(idxList);

            updatingPropList = false;
            
            updateEditableCurves();
        }
    }

    private void updateEditableCurves()
    {
        ArrayList<PropertyWrapper> propList = new ArrayList<PropertyWrapper>();

        for (Object obj: list_curves.getSelectedValues())
        {
            propList.add((PropertyWrapper)obj);
        }

        curvePanel.setPropertyWrappers(propList);
    }

    @Override
    public void viewProviderPreferencesChanged(EventObject evt)
    {
        Properties pref = provider.getPreferences();
        {
            int val = Integer.parseInt(
                    pref.getProperty(TrackViewProvider.PREF_SPLIT_BAR, "100"));
            splitPane_main.setDividerLocation(val);
        }
    }

    @Override
    public void currentToolChanged(ToolPaletteEvent evt)
    {
        updateTool();
    }

    private void updateTool()
    {
        ToolProvider toolProv = toolPalette.getCurrentTool();
        Tool tool = toolProv.create(curvePanel);
        curvePanel.setTool(tool);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup_tools = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        bn_ctrlPt = new javax.swing.JToggleButton();
        bn_pan = new javax.swing.JToggleButton();
        bn_zoom = new javax.swing.JToggleButton();
        bn_frame = new javax.swing.JButton();
        splitPane_main = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        list_curves = new javax.swing.JList();
        panel_curveArea = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        buttonGroup_tools.add(bn_ctrlPt);
        bn_ctrlPt.setSelected(true);
        bn_ctrlPt.setText("Control Point");
        bn_ctrlPt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bn_ctrlPtActionPerformed(evt);
            }
        });
        jPanel2.add(bn_ctrlPt);

        buttonGroup_tools.add(bn_pan);
        bn_pan.setText("Pan");
        bn_pan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bn_panActionPerformed(evt);
            }
        });
        jPanel2.add(bn_pan);

        buttonGroup_tools.add(bn_zoom);
        bn_zoom.setText("Zoom");
        bn_zoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bn_zoomActionPerformed(evt);
            }
        });
        jPanel2.add(bn_zoom);

        bn_frame.setText("Fit");
        bn_frame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bn_frameActionPerformed(evt);
            }
        });
        jPanel2.add(bn_frame);

        jPanel1.add(jPanel2, java.awt.BorderLayout.WEST);

        add(jPanel1, java.awt.BorderLayout.PAGE_START);

        splitPane_main.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                splitPane_mainPropertyChange(evt);
            }
        });

        list_curves.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                list_curvesValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(list_curves);

        splitPane_main.setLeftComponent(jScrollPane1);

        panel_curveArea.setLayout(new java.awt.BorderLayout());
        splitPane_main.setRightComponent(panel_curveArea);

        add(splitPane_main, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void bn_ctrlPtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bn_ctrlPtActionPerformed
        toolPalette.setCurrentTool(TOOL_CONTROL_POINT);
}//GEN-LAST:event_bn_ctrlPtActionPerformed

    private void bn_panActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bn_panActionPerformed
        toolPalette.setCurrentTool(TOOL_PAN);
    }//GEN-LAST:event_bn_panActionPerformed

    private void bn_zoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bn_zoomActionPerformed
        toolPalette.setCurrentTool(TOOL_ZOOM);
    }//GEN-LAST:event_bn_zoomActionPerformed

    private void list_curvesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_list_curvesValueChanged
        if (updatingPropList)
        {
            return;
        }

        updateEditableCurves();
}//GEN-LAST:event_list_curvesValueChanged

    private void bn_frameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bn_frameActionPerformed
        curvePanel.fitToFrame();
    }//GEN-LAST:event_bn_frameActionPerformed

    private void splitPane_mainPropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_splitPane_mainPropertyChange
    {//GEN-HEADEREND:event_splitPane_mainPropertyChange
        Properties pref = provider.getPreferences();

        pref.setProperty(TrackViewProvider.PREF_SPLIT_BAR,
                "" + splitPane_main.getDividerLocation());
    }//GEN-LAST:event_splitPane_mainPropertyChange


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton bn_ctrlPt;
    private javax.swing.JButton bn_frame;
    private javax.swing.JToggleButton bn_pan;
    private javax.swing.JToggleButton bn_zoom;
    private javax.swing.ButtonGroup buttonGroup_tools;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList list_curves;
    private javax.swing.JPanel panel_curveArea;
    private javax.swing.JSplitPane splitPane_main;
    // End of variables declaration//GEN-END:variables

    @Override
    public void documentSourceChanged(EventObject evt)
    {
    }

    @Override
    public void symbolAdded(RavenDocumentEvent evt)
    {
    }

    @Override
    public void symbolRemoved(RavenDocumentEvent evt)
    {
    }

    @Override
    public void currentSymbolChanged(RavenDocumentEvent evt)
    {
        updateSymbol();
    }

    //--------------------------------

    class TrackTransferHandler extends TransferHandler
    {
        private static final long serialVersionUID = 0;

        @Override
        public boolean canImport(TransferHandler.TransferSupport info)
        {
            return info.isDataFlavorSupported(TrackTransferable.FLAVOR)
                    || info.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport info)
        {
            JList curveList = (JList)info.getComponent();
            int index;
            int dropAction = 0;
            if (info.isDrop())
            {
                //Drag & drop
                JList.DropLocation dl = (JList.DropLocation)info.getDropLocation();
                index = dl.getIndex();
                dropAction = info.getDropAction();
            }
            else
            {
                //Cut & paste
                index = curveList.getSelectedIndex();
            }

            TrackTransferableType xferCurves = null;

            try {
                if (info.isDataFlavorSupported(TrackTransferable.FLAVOR))
                {
                    xferCurves = (TrackTransferableType)info.getTransferable().getTransferData(TrackTransferable.FLAVOR);
                }
                else if (info.isDataFlavorSupported(DataFlavor.stringFlavor))
                {
                    TrackTransferable xfer = new TrackTransferable(
                            (String)info.getTransferable()
                            .getTransferData(DataFlavor.stringFlavor));

                    xferCurves = (TrackTransferableType)xfer.getTransferData(TrackTransferable.FLAVOR);
                }
                else
                {
                    return false;
                }
            } catch (UnsupportedFlavorException ex) {
                Logger.getLogger(TrackPanel.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            } catch (IOException ex) {
                Logger.getLogger(TrackPanel.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

            PropertyWrapper wrap = (PropertyWrapper)curveList.getModel().getElementAt(index);

            TrackType tt = xferCurves.getTrack();
            TrackCurve tc = wrap.createTrackCurve(tt);

            int trackUid = wrap.getNode().getSymbol().getTrackLibrary().getCurTrackUid();
            wrap.setTrackCurve(trackUid, tc);
            
            return true;
        }

        @Override
        public int getSourceActions(JComponent c)
        {
            return COPY_OR_MOVE;
        }

        @Override
        public Transferable createTransferable(JComponent c)
        {
            JList curveList = (JList)c;

            TrackTransferableType xferCurves = new TrackTransferableType();

            PropertyWrapper wrap = (PropertyWrapper)curveList.getSelectedValue();
            if (wrap == null)
            {
                return null;
            }

            int trackUid = wrap.getNode().getSymbol().getTrackLibrary().getCurTrackUid();
//            TrackCurve tc = wrap.getTrackCurve(trackUid);
            xferCurves.setTrack(wrap.exportTrack(trackUid));
//            Object[] values = curveList.getSelectedValues();
//            for (Object value: values)
//            {
//                xferCurves.getCurve().add(((Curve)value).export());
//            }

            return new TrackTransferable(xferCurves);
        }

        @Override
        public void exportDone(JComponent c, Transferable t, int action)
        {
        }


    }

    class Renderer extends JLabel implements ListCellRenderer
    {
        private static final long serialVersionUID = 1;

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean cellHasFocus)
        {
            if (selected)
            {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else
            {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            setOpaque(true);

            PropertyWrapper wrapper = (PropertyWrapper)value;
            setForeground(wrapper.getDisplayColor());
            String text = wrapper.getNode().getName() + "."
                    + wrapper.getName();
            setText(text);

            return this;
        }
    }

    class CurFrameWatcher extends PropertyWrapperAdapter
    {
        PropertyWrapperWeakListener listener;

        public CurFrameWatcher(PropertyWrapper wrapper)
        {
            listener = new PropertyWrapperWeakListener(this, wrapper);
            wrapper.addPropertyWrapperListener(listener);
        }

        public void remove()
        {
            listener.remove();
            listener = null;
        }

        @Override
        public void propertyWrapperDataChanged(PropertyChangeEvent evt)
        {
            curFrameChanged();
        }
    }

    class CurTrackWatcher extends PropertyWrapperAdapter
    {
        PropertyWrapperWeakListener listener;

        public CurTrackWatcher(PropertyWrapper wrapper)
        {
            listener = new PropertyWrapperWeakListener(this, wrapper);
            wrapper.addPropertyWrapperListener(listener);
        }

        public void remove()
        {
            listener.remove();
            listener = null;
        }

        @Override
        public void propertyWrapperDataChanged(PropertyChangeEvent evt)
        {
            curTrackChanged();
        }
    }

    class SelectionWatcher implements SelectionListener
    {
        SelectionWeakListener listener;

        public SelectionWatcher(Selection sel)
        {
            listener = new SelectionWeakListener(this, sel);
            sel.addSelectionListener(listener);
        }

        public void remove()
        {
            listener.remove();
            listener = null;
        }

        @Override
        public void selectionChanged(SelectionEvent evt)
        {
            TrackPanel.this.ravenSelectionChanged();
        }

        @Override
        public void subselectionChanged(SelectionSubEvent evt)
        {
        }
    }

}
