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
 * ToolPanSettings.java
 *
 * Created on Dec 11, 2010, 1:31:09 AM
 */

package com.kitfox.raven.editor.node.tools.common;

import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.view.color.ColorPanel;
import com.kitfox.raven.shape.bezier.VertexSmooth;
import java.awt.BorderLayout;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 *
 * @author kitfox
 */
public class ToolPaintStrokeSettings extends javax.swing.JPanel
{
    final RavenEditor editor;
    final ToolPaintStroke.Provider toolProvider;

    ColorPanel colorPanel;
    StrokeRenderer renderer = new StrokeRenderer();

    boolean updating = true;

    /** Creates new form ToolPanSettings */
    public ToolPaintStrokeSettings(RavenEditor editor, ToolPaintStroke.Provider toolProvider)
    {
        this.editor = editor;
        this.toolProvider = toolProvider;
        initComponents();

        colorPanel = new ColorPanel(editor);
        panel_colorArea.add(colorPanel, BorderLayout.CENTER);

        panel_strokePreview.add(renderer, BorderLayout.CENTER);
        update();
    }

    private void update()
    {
        if (toolProvider == null)
        {
            return;
        }

        updating = true;
        
        spinner_strokeMaxWidth.setValue(toolProvider.getStrokeWidthMax());
        spinner_strokeMinWidth.setValue(toolProvider.getStrokeWidthMin());
        spinner_strokeSmoothing.setValue(toolProvider.getStrokeSmoothing());
        spinner_strokeSpacing.setValue(toolProvider.getStrokeSpacing());
        spinner_vertexSmoothAngle.setValue(toolProvider.getVertexSmoothAngle());
        switch (toolProvider.getVertexSmooth())
        {
            case TENSE:
                radio_vertexTense.setSelected(true);
                spinner_vertexSmoothAngle.setEnabled(false);
                break;
            case SMOOTH:
                radio_vertexSmooth.setSelected(true);
                spinner_vertexSmoothAngle.setEnabled(true);
                break;
        }

        updating = false;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup_vertexType = new javax.swing.ButtonGroup();
        panel_colorArea = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        spinner_strokeSpacing = new javax.swing.JSpinner();
        panel_strokePreview = new javax.swing.JPanel();
        spinner_strokeMinWidth = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        radio_vertexTense = new javax.swing.JRadioButton();
        spinner_strokeSmoothing = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        spinner_vertexSmoothAngle = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        radio_vertexSmooth = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        spinner_strokeMaxWidth = new javax.swing.JSpinner();

        setLayout(new java.awt.BorderLayout());

        panel_colorArea.setLayout(new java.awt.BorderLayout());
        add(panel_colorArea, java.awt.BorderLayout.SOUTH);

        spinner_strokeSpacing.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), null, Float.valueOf(0.1f)));
        spinner_strokeSpacing.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinner_strokeSpacingStateChanged(evt);
            }
        });

        panel_strokePreview.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout panel_strokePreviewLayout = new javax.swing.GroupLayout(panel_strokePreview);
        panel_strokePreview.setLayout(panel_strokePreviewLayout);
        panel_strokePreviewLayout.setHorizontalGroup(
            panel_strokePreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 229, Short.MAX_VALUE)
        );
        panel_strokePreviewLayout.setVerticalGroup(
            panel_strokePreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 34, Short.MAX_VALUE)
        );

        spinner_strokeMinWidth.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), null, Float.valueOf(1.0f)));
        spinner_strokeMinWidth.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinner_strokeMinWidthStateChanged(evt);
            }
        });

        jLabel4.setText("Spacing");

        buttonGroup_vertexType.add(radio_vertexTense);
        radio_vertexTense.setText("Tense");
        radio_vertexTense.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio_vertexTenseActionPerformed(evt);
            }
        });

        spinner_strokeSmoothing.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(1.0f), Float.valueOf(0.1f), null, Float.valueOf(0.1f)));
        spinner_strokeSmoothing.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinner_strokeSmoothingStateChanged(evt);
            }
        });

        jLabel1.setText("Smoothness");

        spinner_vertexSmoothAngle.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(10.0f), Float.valueOf(0.0f), Float.valueOf(180.0f), Float.valueOf(1.0f)));
        spinner_vertexSmoothAngle.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinner_vertexSmoothAngleStateChanged(evt);
            }
        });

        jLabel2.setText("Max Width");

        jLabel5.setText("Angle");

        buttonGroup_vertexType.add(radio_vertexSmooth);
        radio_vertexSmooth.setText("Smooth");
        radio_vertexSmooth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio_vertexSmoothActionPerformed(evt);
            }
        });

        jLabel3.setText("Min Width");

        spinner_strokeMaxWidth.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(10.0f), Float.valueOf(0.0f), null, Float.valueOf(1.0f)));
        spinner_strokeMaxWidth.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinner_strokeMaxWidthStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 253, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(panel_strokePreview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addContainerGap())
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(radio_vertexTense)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(radio_vertexSmooth)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel5)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(spinner_vertexSmoothAngle, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(spinner_strokeMaxWidth))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(spinner_strokeMinWidth))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(spinner_strokeSmoothing))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(spinner_strokeSpacing, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGap(34, 34, 34)))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 190, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(spinner_strokeMaxWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(spinner_strokeMinWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(spinner_strokeSpacing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(spinner_strokeSmoothing, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(radio_vertexTense)
                        .addComponent(radio_vertexSmooth)
                        .addComponent(jLabel5)
                        .addComponent(spinner_vertexSmoothAngle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(panel_strokePreview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void spinner_strokeMaxWidthStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_spinner_strokeMaxWidthStateChanged
    {//GEN-HEADEREND:event_spinner_strokeMaxWidthStateChanged
        if (updating == true)
        {
            return;
        }

        float max = (Float)spinner_strokeMaxWidth.getValue();
        float min = (Float)spinner_strokeMinWidth.getValue();
        toolProvider.setStrokeWidthMax(max);
        if (min > max)
        {
            spinner_strokeMinWidth.setValue(max);
        }

        panel_strokePreview.repaint();
    }//GEN-LAST:event_spinner_strokeMaxWidthStateChanged

    private void spinner_strokeMinWidthStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_spinner_strokeMinWidthStateChanged
    {//GEN-HEADEREND:event_spinner_strokeMinWidthStateChanged
        if (updating == true)
        {
            return;
        }

        float max = (Float)spinner_strokeMaxWidth.getValue();
        float min = (Float)spinner_strokeMinWidth.getValue();
        toolProvider.setStrokeWidthMin(min);
        if (min > max)
        {
            spinner_strokeMaxWidth.setValue(min);
        }

        panel_strokePreview.repaint();
    }//GEN-LAST:event_spinner_strokeMinWidthStateChanged

    private void spinner_strokeSpacingStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_spinner_strokeSpacingStateChanged
    {//GEN-HEADEREND:event_spinner_strokeSpacingStateChanged
        if (updating == true)
        {
            return;
        }

        float val = (Float)spinner_strokeSpacing.getValue();
        toolProvider.setStrokeSpacing(val);

        panel_strokePreview.repaint();
    }//GEN-LAST:event_spinner_strokeSpacingStateChanged

    private void spinner_strokeSmoothingStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_spinner_strokeSmoothingStateChanged
    {//GEN-HEADEREND:event_spinner_strokeSmoothingStateChanged
        if (updating == true)
        {
            return;
        }

        float val = (Float)spinner_strokeSmoothing.getValue();
        toolProvider.setStrokeSmoothing(val);

        panel_strokePreview.repaint();
    }//GEN-LAST:event_spinner_strokeSmoothingStateChanged

    private void spinner_vertexSmoothAngleStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_spinner_vertexSmoothAngleStateChanged
    {//GEN-HEADEREND:event_spinner_vertexSmoothAngleStateChanged
        if (updating == true)
        {
            return;
        }

        float val = (Float)spinner_vertexSmoothAngle.getValue();
        toolProvider.setVertexSmoothAngle(val);
    }//GEN-LAST:event_spinner_vertexSmoothAngleStateChanged

    private void radio_vertexTenseActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_radio_vertexTenseActionPerformed
    {//GEN-HEADEREND:event_radio_vertexTenseActionPerformed
        if (updating == true)
        {
            return;
        }

        toolProvider.setVertexSmooth(VertexSmooth.TENSE);
        spinner_vertexSmoothAngle.setEnabled(false);
    }//GEN-LAST:event_radio_vertexTenseActionPerformed

    private void radio_vertexSmoothActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_radio_vertexSmoothActionPerformed
    {//GEN-HEADEREND:event_radio_vertexSmoothActionPerformed
        if (updating == true)
        {
            return;
        }

        toolProvider.setVertexSmooth(VertexSmooth.SMOOTH);
        spinner_vertexSmoothAngle.setEnabled(true);
    }//GEN-LAST:event_radio_vertexSmoothActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup_vertexType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel panel_colorArea;
    private javax.swing.JPanel panel_strokePreview;
    private javax.swing.JRadioButton radio_vertexSmooth;
    private javax.swing.JRadioButton radio_vertexTense;
    private javax.swing.JSpinner spinner_strokeMaxWidth;
    private javax.swing.JSpinner spinner_strokeMinWidth;
    private javax.swing.JSpinner spinner_strokeSmoothing;
    private javax.swing.JSpinner spinner_strokeSpacing;
    private javax.swing.JSpinner spinner_vertexSmoothAngle;
    // End of variables declaration//GEN-END:variables


    //------------------------------------
    class StrokeRenderer extends JPanel
    {

        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
        }
        
    }
}
