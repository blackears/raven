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

package com.kitfox.raven.editor.node.scene.control;

import com.kitfox.raven.util.tree.PropertyWrapperWeakAdapter;
import com.kitfox.raven.editor.RavenDocument;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.RavenEditorListener;
import com.kitfox.raven.editor.RavenEditorWeakListener;
import com.kitfox.raven.editor.node.scene.RavenSymbolRoot;
import com.kitfox.raven.paint.common.RavenPaintColor;
import com.kitfox.raven.util.tree.*;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.util.EventObject;

/**
 *
 * @author kitfox
 */
public class SnappingPanel extends javax.swing.JPanel
        implements RavenEditorListener
{
    RavenEditor editor;

    boolean init;

    RavenEditorWeakListener edListener;
    
    RavenColorPreviewPanel previewGridColor = new RavenColorPreviewPanel();
    
    private DocumentPropMonitor gridColorPropMonitor;
    DocumentPropMonitor gridShowPropMon;
    DocumentPropMonitor gridSpacingMinPropMon;
    DocumentPropMonitor gridSpacingMajPropMon;
    DocumentPropMonitor gridSpacingOffXPropMon;
    DocumentPropMonitor gridSpacingOffYPropMon;
    DocumentPropMonitor snapGridPropMon;
    DocumentPropMonitor snapVertexPropMon;
    
    private PropertyControlMonitor gridColorCtrlMonitor;
    
    /**
     * Creates new form SnappingPanel
     */
    public SnappingPanel(RavenEditor editor)
    {
        this.editor = editor;
        initComponents();
        
        edListener = new RavenEditorWeakListener(this, editor);
        editor.addRavenEditorListener(edListener);

        panel_gridColor.add(previewGridColor, BorderLayout.CENTER);
        gridColorCtrlMonitor = new PropertyControlMonitor(editor, panel_gridColor);
        previewGridColor.addMouseListener(gridColorCtrlMonitor);

        rebuildDocument();
        
        init = true;
    }

    private void rebuildDocument()
    {
        if (gridColorPropMonitor != null)
        {
            gridColorCtrlMonitor.setWrapper(null);
            
            gridColorPropMonitor.remove();
            gridColorPropMonitor = null;
            
            gridShowPropMon.remove();
            gridShowPropMon = null;
            
            gridSpacingMinPropMon.remove();
            gridSpacingMinPropMon = null;
            gridSpacingMajPropMon.remove();
            gridSpacingMajPropMon = null;
            gridSpacingOffXPropMon.remove();
            gridSpacingOffXPropMon = null;
            gridSpacingOffYPropMon.remove();
            gridSpacingOffYPropMon = null;
            snapGridPropMon.remove();
            snapGridPropMon = null;
            snapVertexPropMon.remove();
            snapVertexPropMon = null;
        }

        RavenSymbolRoot root = getRoot();

        if (root != null)
        {
            gridColorCtrlMonitor.setWrapper(root.gridColor);
            
            gridColorPropMonitor = new DocumentPropMonitor(root.gridColor);

            gridShowPropMon = new DocumentPropMonitor(root.gridShow);
            gridSpacingMinPropMon = new DocumentPropMonitor(root.gridSpacingMin);
            gridSpacingMajPropMon = new DocumentPropMonitor(root.gridSpacingMaj);
            gridSpacingOffXPropMon = new DocumentPropMonitor(root.gridSpacingOffX);
            gridSpacingOffYPropMon = new DocumentPropMonitor(root.gridSpacingOffY);
            snapGridPropMon = new DocumentPropMonitor(root.snapGrid);
            snapVertexPropMon = new DocumentPropMonitor(root.snapVertex);
            
            updateFromDocument();
        }
    }

    private void updateFromDocument()
    {
        RavenSymbolRoot root = getRoot();
        if (root == null)
        {
            return;
        }

        RavenPaintColor col = root.getGridColor();
        previewGridColor.setColor(col);
        
        check_snapToGrid.setSelected(root.isSnapGrid());
        check_snapToVertex.setSelected(root.isSnapVertex());
        check_showGrid.setSelected(root.isGridShow());
        
        spinner_majSpacing.setValue(root.getGridSpacingMajor());
        spinner_minSpacing.setValue(root.getGridSpacingMinor());
        spinner_offsetX.setValue(root.getGridSpacingOffsetX());
        spinner_offsetY.setValue(root.getGridSpacingOffsetY());
    }

    @Override
    public void recentFilesChanged(EventObject evt)
    {
    }

    @Override
    public void documentChanged(EventObject evt)
    {
        rebuildDocument();
    }

    
    private RavenSymbolRoot getRoot()
    {
        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return null;
        }

        return (RavenSymbolRoot)doc.getCurSymbol().getRoot();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        check_showGrid = new javax.swing.JCheckBox();
        check_snapToVertex = new javax.swing.JCheckBox();
        check_snapToGrid = new javax.swing.JCheckBox();
        panel_gridColor = new javax.swing.JPanel();
        spinner_minSpacing = new javax.swing.JSpinner();
        spinner_majSpacing = new javax.swing.JSpinner();
        spinner_offsetX = new javax.swing.JSpinner();
        spinner_offsetY = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        check_showGrid.setText("Show Grid");
        check_showGrid.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                check_showGridActionPerformed(evt);
            }
        });

        check_snapToVertex.setText("Snap To Vertex");
        check_snapToVertex.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                check_snapToVertexActionPerformed(evt);
            }
        });

        check_snapToGrid.setText("Snap To Grid");
        check_snapToGrid.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                check_snapToGridActionPerformed(evt);
            }
        });

        panel_gridColor.setBorder(javax.swing.BorderFactory.createTitledBorder("Grid Color"));
        panel_gridColor.setLayout(new java.awt.BorderLayout());

        spinner_minSpacing.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));
        spinner_minSpacing.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                spinner_minSpacingStateChanged(evt);
            }
        });

        spinner_majSpacing.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));
        spinner_majSpacing.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                spinner_majSpacingStateChanged(evt);
            }
        });

        spinner_offsetX.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));
        spinner_offsetX.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                spinner_offsetXStateChanged(evt);
            }
        });

        spinner_offsetY.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));
        spinner_offsetY.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                spinner_offsetYStateChanged(evt);
            }
        });

        jLabel1.setText("Min Spacing");

        jLabel2.setText("Max Spacing");

        jLabel3.setText("Offset X");

        jLabel4.setText("Offset Y");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 6, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(spinner_minSpacing)
                    .addComponent(spinner_majSpacing, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinner_offsetX, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinner_offsetY, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(check_snapToGrid)
                    .addComponent(check_snapToVertex)
                    .addComponent(check_showGrid))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_gridColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(check_snapToVertex)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(check_snapToGrid)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(check_showGrid))
                    .addComponent(panel_gridColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(spinner_minSpacing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(spinner_offsetX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinner_majSpacing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(spinner_offsetY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void check_snapToVertexActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_check_snapToVertexActionPerformed
    {//GEN-HEADEREND:event_check_snapToVertexActionPerformed
        if (!init)
        {
            return;
        }
        
        RavenSymbolRoot root = getRoot();
        if (root == null)
        {
            return;
        }

        root.snapVertex.setValue(check_snapToVertex.isSelected());
    }//GEN-LAST:event_check_snapToVertexActionPerformed

    private void check_snapToGridActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_check_snapToGridActionPerformed
    {//GEN-HEADEREND:event_check_snapToGridActionPerformed
        if (!init)
        {
            return;
        }
        
        RavenSymbolRoot root = getRoot();
        if (root == null)
        {
            return;
        }

        root.snapGrid.setValue(check_snapToGrid.isSelected());
    }//GEN-LAST:event_check_snapToGridActionPerformed

    private void check_showGridActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_check_showGridActionPerformed
    {//GEN-HEADEREND:event_check_showGridActionPerformed
        if (!init)
        {
            return;
        }
        
        RavenSymbolRoot root = getRoot();
        if (root == null)
        {
            return;
        }

        root.gridShow.setValue(check_showGrid.isSelected());
    }//GEN-LAST:event_check_showGridActionPerformed

    private void spinner_minSpacingStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_spinner_minSpacingStateChanged
    {//GEN-HEADEREND:event_spinner_minSpacingStateChanged
        if (!init)
        {
            return;
        }
        
        RavenSymbolRoot root = getRoot();
        if (root == null)
        {
            return;
        }

        root.gridSpacingMin.setValue((Float)spinner_minSpacing.getValue());
    }//GEN-LAST:event_spinner_minSpacingStateChanged

    private void spinner_majSpacingStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_spinner_majSpacingStateChanged
    {//GEN-HEADEREND:event_spinner_majSpacingStateChanged
        if (!init)
        {
            return;
        }
        
        RavenSymbolRoot root = getRoot();
        if (root == null)
        {
            return;
        }

        root.gridSpacingMaj.setValue((Float)spinner_majSpacing.getValue());
    }//GEN-LAST:event_spinner_majSpacingStateChanged

    private void spinner_offsetXStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_spinner_offsetXStateChanged
    {//GEN-HEADEREND:event_spinner_offsetXStateChanged
        if (!init)
        {
            return;
        }
        
        RavenSymbolRoot root = getRoot();
        if (root == null)
        {
            return;
        }

        root.gridSpacingOffX.setValue((Float)spinner_offsetX.getValue());
    }//GEN-LAST:event_spinner_offsetXStateChanged

    private void spinner_offsetYStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_spinner_offsetYStateChanged
    {//GEN-HEADEREND:event_spinner_offsetYStateChanged
        if (!init)
        {
            return;
        }
        
        RavenSymbolRoot root = getRoot();
        if (root == null)
        {
            return;
        }

        root.gridSpacingOffY.setValue((Float)spinner_offsetY.getValue());
    }//GEN-LAST:event_spinner_offsetYStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox check_showGrid;
    private javax.swing.JCheckBox check_snapToGrid;
    private javax.swing.JCheckBox check_snapToVertex;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel panel_gridColor;
    private javax.swing.JSpinner spinner_majSpacing;
    private javax.swing.JSpinner spinner_minSpacing;
    private javax.swing.JSpinner spinner_offsetX;
    private javax.swing.JSpinner spinner_offsetY;
    // End of variables declaration//GEN-END:variables

    //--------------------------
    
    private class DocumentPropMonitor extends PropertyWrapperWeakAdapter
    {
        public DocumentPropMonitor(PropertyWrapper source)
        {
            super(source);
        }

        @Override
        public void propertyWrapperDataChanged(PropertyChangeEvent evt)
        {
            updateFromDocument();
        }
    }
    
}
