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
 * GradientStylePanel.java
 *
 * Created on Sep 20, 2009, 2:34:20 AM
 */

package com.kitfox.raven.editor.paint;

import com.kitfox.game.control.color.ColorStyle;
import com.kitfox.game.control.color.ColorStyleEditorPanel;
import com.kitfox.game.control.color.Gradient;
import com.kitfox.game.control.color.Gradient.StopEditor;
import com.kitfox.game.control.color.GradientSliderPanel;
import com.kitfox.game.control.color.GradientStop;
import com.kitfox.game.control.color.MultipleGradientStops;
import com.kitfox.game.control.color.MultipleGradientStops.Cycle;
import com.kitfox.game.control.color.MultipleGradientStops.Style;
import com.kitfox.game.control.color.StopModelListener;
import com.kitfox.game.control.color.StopSide;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.MultipleGradientPaint.ColorSpaceType;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author kitfox
 */
public class MultipleGradientStopsPanel extends javax.swing.JPanel
//        implements StopModelListener, PropertyChangeListener, StopEditor
        implements StopModelListener, StopEditor
{
    private static final long serialVersionUID = 0;

    GradientSliderPanel panelSlider = new GradientSliderPanel();

    private Gradient gradient;

    boolean updating = true;

    private MultipleGradientStops stops = new MultipleGradientStops();
    public static final String PROP_STOPS = "stops";
    
    ColorStyleEditorPanel colorEditor = new ColorStyleEditorPanel();


    /** Creates new form GradientStylePanel */
    public MultipleGradientStopsPanel()
    {
        initComponents();

        panelSlider.setSide(StopSide.SOUTH);
        panel_gradient.add(panelSlider, BorderLayout.CENTER);

        combo_cycle.addItem(Cycle.NO_CYCLE);
        combo_cycle.addItem(Cycle.REFLECT);
        combo_cycle.addItem(Cycle.REPEAT);

        combo_style.addItem(Style.LINEAR);
        combo_style.addItem(Style.RADIAL);

        combo_colorSpace.addItem(ColorSpaceType.SRGB);
        combo_colorSpace.addItem(ColorSpaceType.LINEAR_RGB);

        updateFromStops();
    }

    private void updateFromStops()
    {
        updating = true;

        if (stops == null)
        {
            combo_cycle.setSelectedItem(Cycle.NO_CYCLE);
            combo_style.setSelectedItem(Style.LINEAR);
            combo_colorSpace.setSelectedItem(ColorSpaceType.SRGB);

            gradient = new Gradient(ColorStyle.BLACK, ColorStyle.WHITE);
        }
        else
        {
            combo_cycle.setSelectedItem(stops.getCycleMethod());
            combo_style.setSelectedItem(stops.getStyle());
            combo_colorSpace.setSelectedItem(stops.getColorSpace());

            ColorStyle[] colors = stops.getColors();
            float[] offsets = stops.getFractions();
            GradientStop[] gradStops = new GradientStop[colors.length];
            for (int i = 0; i < colors.length; ++i)
            {
                gradStops[i] = new GradientStop(colors[i], offsets[i]);
            }

            gradient = new Gradient(gradStops);
        }
        
        gradient.setHorizontal(true);
        gradient.setStopEditor(this);
        gradient.addStopModelListener(this);
        panelSlider.setGradient(gradient);

        updating = false;
    }

    private void buildStops()
    {
        ArrayList<GradientStop> list = gradient.getStopObjects();

        float[] fractions = new float[list.size()];
        ColorStyle[] colors = new ColorStyle[list.size()];
        for (int i = 0; i < list.size(); ++i)
        {
            GradientStop stop = list.get(i);
            fractions[i] = stop.getOffset();
            colors[i] = stop.getColor();
        }

        Cycle cycle = (Cycle)combo_cycle.getSelectedItem();
        Style style = (Style)combo_style.getSelectedItem();
        ColorSpaceType colorSpace = (ColorSpaceType)combo_colorSpace.getSelectedItem();

        MultipleGradientStops oldStops = stops;
        stops = new MultipleGradientStops(
                fractions, colors, cycle, style, colorSpace);

        firePropertyChange(PROP_STOPS, oldStops, stops);
    }

    @Override
    public void stopModelChanged(ChangeEvent evt)
    {
        buildStops();
    }
    
    @Override
    public void beginStopEdits(ChangeEvent evt)
    {
    }

    @Override
    public void endStopEdits(ChangeEvent evt)
    {
        buildStops();
    }

    @Override
    public void editStop(final GradientStop stop)
    {
        ColorStyle oldColor = stop.getColor();
        PropertyChangeListener colorCopier = new PropertyChangeListener()
        {
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                stop.setColor(colorEditor.getColor());
            }
        };

        gradient.beginStopEdits();
        colorEditor.setColor(stop.getColor());
        colorEditor.addPropertyChangeListener(colorCopier);
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit Color", ModalityType.APPLICATION_MODAL);
        dlg.getContentPane().add(colorEditor, BorderLayout.CENTER);
        dlg.pack();
        dlg.setVisible(true);

        colorEditor.removePropertyChangeListener(colorCopier);
        stop.setColor(colorEditor.getColor());
        gradient.endStopEdits();
        dlg.dispose();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel_gradient = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        combo_cycle = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        combo_style = new javax.swing.JComboBox();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        combo_colorSpace = new javax.swing.JComboBox();

        setLayout(new java.awt.BorderLayout());

        panel_gradient.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                panel_gradientMouseReleased(evt);
            }
        });
        panel_gradient.setLayout(new java.awt.BorderLayout());
        add(panel_gradient, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.X_AXIS));

        combo_cycle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_cycleActionPerformed(evt);
            }
        });

        jLabel11.setText("Cycle");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(combo_cycle, 0, 61, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(combo_cycle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2);

        jLabel2.setText("Style");

        combo_style.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_styleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(combo_style, 0, 44, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(combo_style, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel3);

        jLabel1.setText("Color Space");

        combo_colorSpace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_colorSpaceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(combo_colorSpace, 0, 41, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(combo_colorSpace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel4);

        add(jPanel1, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

    private void combo_cycleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_cycleActionPerformed
        if (updating)
        {
            return;
        }

        buildStops();
    }//GEN-LAST:event_combo_cycleActionPerformed

    private void panel_gradientMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_gradientMouseReleased
//        buildStops();
    }//GEN-LAST:event_panel_gradientMouseReleased

    private void combo_colorSpaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_colorSpaceActionPerformed
        if (updating)
        {
            return;
        }

        buildStops();
    }//GEN-LAST:event_combo_colorSpaceActionPerformed

    private void combo_styleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_combo_styleActionPerformed
    {//GEN-HEADEREND:event_combo_styleActionPerformed
        if (updating)
        {
            return;
        }

        buildStops();
    }//GEN-LAST:event_combo_styleActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox combo_colorSpace;
    private javax.swing.JComboBox combo_cycle;
    private javax.swing.JComboBox combo_style;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel panel_gradient;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the stops
     */
    public MultipleGradientStops getStops() {
        return stops;
    }

    /**
     * @param stops the stops to set
     */
    public void setStops(MultipleGradientStops stops) {
        MultipleGradientStops oldStops = stops;
        this.stops = stops;
        firePropertyChange(PROP_STOPS, oldStops, stops);
        
        updateFromStops();
    }


}
