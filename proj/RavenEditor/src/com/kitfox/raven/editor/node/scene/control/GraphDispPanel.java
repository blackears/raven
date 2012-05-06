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

import com.kitfox.raven.editor.RavenDocument;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.RavenEditorListener;
import com.kitfox.raven.editor.RavenEditorWeakListener;
import com.kitfox.raven.editor.node.scene.RavenNodeRoot;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperWeakAdapter;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.util.EventObject;

/**
 *
 * @author kitfox
 */
public class GraphDispPanel extends javax.swing.JPanel
        implements RavenEditorListener
{
    final RavenEditor editor;

    RavenEditorWeakListener edListener;
    
    boolean init;

    private RavenColorPreviewPanel previewEdgeColor = new RavenColorPreviewPanel();
    private RavenColorPreviewPanel previewEdgeColorSel = new RavenColorPreviewPanel();
    private RavenColorPreviewPanel previewVertColor = new RavenColorPreviewPanel();
    private RavenColorPreviewPanel previewVertColorSel = new RavenColorPreviewPanel();
    
    private PropertyControlMonitor edgeColorCtrlMonitor;
    private PropertyControlMonitor edgeColorSelCtrlMonitor;
    private PropertyControlMonitor vertColorCtrlMonitor;
    private PropertyControlMonitor vertColorSelCtrlMonitor;
    
    private DocumentPropMonitor vertPickRadPropMonitor;
    private DocumentPropMonitor vertDispRadPropMonitor;
    private DocumentPropMonitor edgeColorPropMonitor;
    private DocumentPropMonitor edgeColorSelPropMonitor;
    private DocumentPropMonitor vertColorPropMonitor;
    private DocumentPropMonitor vertColorSelPropMonitor;
    
    /**
     * Creates new form GraphDispPanel
     */
    public GraphDispPanel(RavenEditor editor)
    {
        this.editor = editor;
        initComponents();
        
        edListener = new RavenEditorWeakListener(this, editor);
        editor.addRavenEditorListener(edListener);

        panel_edgeColor.add(previewEdgeColor, BorderLayout.CENTER);
        edgeColorCtrlMonitor = new PropertyControlMonitor(editor, panel_edgeColor);
        previewEdgeColor.addMouseListener(edgeColorCtrlMonitor);

        panel_edgeColorSel.add(previewEdgeColorSel, BorderLayout.CENTER);
        edgeColorSelCtrlMonitor = new PropertyControlMonitor(editor, panel_edgeColorSel);
        previewEdgeColorSel.addMouseListener(edgeColorSelCtrlMonitor);

        panel_vertColor.add(previewVertColor, BorderLayout.CENTER);
        vertColorCtrlMonitor = new PropertyControlMonitor(editor, panel_vertColor);
        previewVertColor.addMouseListener(vertColorCtrlMonitor);

        panel_vertColorSel.add(previewVertColorSel, BorderLayout.CENTER);
        vertColorSelCtrlMonitor = new PropertyControlMonitor(editor, panel_vertColorSel);
        previewVertColorSel.addMouseListener(vertColorSelCtrlMonitor);

        rebuildDocument();
        
        init = true;
    }

    private void rebuildDocument()
    {
        if (vertPickRadPropMonitor != null)
        {
            edgeColorCtrlMonitor.setWrapper(null);
            edgeColorSelCtrlMonitor.setWrapper(null);
            vertColorCtrlMonitor.setWrapper(null);
            vertColorSelCtrlMonitor.setWrapper(null);
            
            vertPickRadPropMonitor.remove();
            vertPickRadPropMonitor = null;
            vertDispRadPropMonitor.remove();
            vertDispRadPropMonitor = null;
            edgeColorPropMonitor.remove();
            edgeColorPropMonitor = null;
            edgeColorSelPropMonitor.remove();
            edgeColorSelPropMonitor = null;
            vertColorPropMonitor.remove();
            vertColorPropMonitor = null;
            vertColorSelPropMonitor.remove();
            vertColorSelPropMonitor = null;
        }

        RavenNodeRoot root = getRoot();

        if (root != null)
        {
            edgeColorCtrlMonitor.setWrapper(root.graphColorEdge);
            edgeColorSelCtrlMonitor.setWrapper(root.graphColorEdgeSelect);
            vertColorCtrlMonitor.setWrapper(root.graphColorVert);
            vertColorSelCtrlMonitor.setWrapper(root.graphColorVertSelect);

            vertPickRadPropMonitor = new DocumentPropMonitor(root.graphRadiusPick);
            vertDispRadPropMonitor = new DocumentPropMonitor(root.graphRadiusDisplay);
            edgeColorPropMonitor = new DocumentPropMonitor(root.graphColorEdge);
            edgeColorSelPropMonitor = new DocumentPropMonitor(root.graphColorEdgeSelect);
            vertColorPropMonitor = new DocumentPropMonitor(root.graphColorVert);
            vertColorSelPropMonitor = new DocumentPropMonitor(root.graphColorVertSelect);
            
            updateFromDocument();
        }
    }

    private void updateFromDocument()
    {
        RavenNodeRoot root = getRoot();
        if (root == null)
        {
            return;
        }

        previewEdgeColor.setColor(root.getGraphColorEdge());
        previewEdgeColorSel.setColor(root.getGraphColorEdgeSelect());
        previewVertColor.setColor(root.getGraphColorVert());
        previewVertColorSel.setColor(root.getGraphColorVertSelect());

        spinner_vertRadDisp.setValue(root.getGraphRadiusDisplay());
        spinner_vertRadPick.setValue(root.getGraphRadiusPick());
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
    
    private RavenNodeRoot getRoot()
    {
        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return null;
        }

        return (RavenNodeRoot)doc.getCurSymbol();
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

        jLabel1 = new javax.swing.JLabel();
        spinner_vertRadPick = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        spinner_vertRadDisp = new javax.swing.JSpinner();
        jPanel5 = new javax.swing.JPanel();
        panel_edgeColor = new javax.swing.JPanel();
        panel_edgeColorSel = new javax.swing.JPanel();
        panel_vertColor = new javax.swing.JPanel();
        panel_vertColorSel = new javax.swing.JPanel();

        jLabel1.setText("Vertex Pick Radius");

        spinner_vertRadPick.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(1.0f), Float.valueOf(1.0f), null, Float.valueOf(1.0f)));
        spinner_vertRadPick.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                spinner_vertRadPickStateChanged(evt);
            }
        });

        jLabel2.setText("Vertex Display Radius");

        spinner_vertRadDisp.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(1.0f), Float.valueOf(1.0f), null, Float.valueOf(1.0f)));
        spinner_vertRadDisp.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                spinner_vertRadDispStateChanged(evt);
            }
        });

        jPanel5.setLayout(new java.awt.GridLayout(2, 2));

        panel_edgeColor.setBorder(javax.swing.BorderFactory.createTitledBorder("Edge Color"));
        panel_edgeColor.setLayout(new java.awt.BorderLayout());
        jPanel5.add(panel_edgeColor);

        panel_edgeColorSel.setBorder(javax.swing.BorderFactory.createTitledBorder("Edge Select Color"));
        panel_edgeColorSel.setLayout(new java.awt.BorderLayout());
        jPanel5.add(panel_edgeColorSel);

        panel_vertColor.setBorder(javax.swing.BorderFactory.createTitledBorder("Vertex Color"));
        panel_vertColor.setLayout(new java.awt.BorderLayout());
        jPanel5.add(panel_vertColor);

        panel_vertColorSel.setBorder(javax.swing.BorderFactory.createTitledBorder("Vertex Select Color"));
        panel_vertColorSel.setLayout(new java.awt.BorderLayout());
        jPanel5.add(panel_vertColorSel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(spinner_vertRadPick))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(spinner_vertRadDisp, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(spinner_vertRadPick, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(spinner_vertRadDisp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void spinner_vertRadPickStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_spinner_vertRadPickStateChanged
    {//GEN-HEADEREND:event_spinner_vertRadPickStateChanged
        if (!init)
        {
            return;
        }
        
        RavenNodeRoot root = getRoot();
        if (root == null)
        {
            return;
        }

        root.graphRadiusPick.setValue((Float)spinner_vertRadPick.getValue());
    }//GEN-LAST:event_spinner_vertRadPickStateChanged

    private void spinner_vertRadDispStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_spinner_vertRadDispStateChanged
    {//GEN-HEADEREND:event_spinner_vertRadDispStateChanged
        if (!init)
        {
            return;
        }

        RavenNodeRoot root = getRoot();
        if (root == null)
        {
            return;
        }

        root.graphRadiusDisplay.setValue((Float)spinner_vertRadDisp.getValue());
    }//GEN-LAST:event_spinner_vertRadDispStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel panel_edgeColor;
    private javax.swing.JPanel panel_edgeColorSel;
    private javax.swing.JPanel panel_vertColor;
    private javax.swing.JPanel panel_vertColorSel;
    private javax.swing.JSpinner spinner_vertRadDisp;
    private javax.swing.JSpinner spinner_vertRadPick;
    // End of variables declaration//GEN-END:variables

    //----------------------------------

    
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
