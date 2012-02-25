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
 * ColorPanel.java
 *
 * Created on Jan 17, 2011, 8:47:22 PM
 */

package com.kitfox.raven.editor.view.color;

import com.kitfox.raven.editor.RavenDocument;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.RavenEditorListener;
import com.kitfox.raven.editor.RavenEditorWeakListener;
import com.kitfox.raven.util.RavenSwingUtil;
import com.kitfox.raven.editor.node.scene.RavenNodePaint;
import com.kitfox.raven.editor.node.scene.RavenNodePaintLibrary;
import com.kitfox.raven.editor.node.scene.RavenNodeRoot;
import com.kitfox.raven.editor.node.scene.RavenNodeStroke;
import com.kitfox.raven.editor.node.scene.RavenNodeStrokeLibrary;
import com.kitfox.raven.editor.node.tools.common.ServiceColors2D;
import com.kitfox.raven.editor.paint.RavenPaintInlineDialog;
import com.kitfox.raven.editor.paint.RavenPaintInlinePanel;
import com.kitfox.raven.editor.paint.RavenPaintPreviewPanel;
import com.kitfox.raven.editor.stroke.RavenStrokeBasic;
import com.kitfox.raven.editor.stroke.RavenStrokeBasicPanel;
import com.kitfox.raven.editor.stroke.RavenStrokeInlineDialog;
import com.kitfox.raven.editor.stroke.RavenStrokePreviewPanel;
import com.kitfox.raven.paint.RavenPaint;
import com.kitfox.raven.paint.RavenStroke;
import com.kitfox.raven.util.tree.ChildWrapperEvent;
import com.kitfox.raven.util.tree.ChildWrapperListener;
import com.kitfox.raven.util.tree.ChildWrapperWeakListener;
import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.PropertyData;
import com.kitfox.raven.util.tree.PropertyDataReference;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperAdapter;
import com.kitfox.raven.util.tree.PropertyWrapperWeakListener;
import com.kitfox.raven.util.tree.property.NodeObjectCellRenderer;
import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventObject;
import javax.swing.SwingUtilities;

/**
 *
 * @author kitfox
 */
@Deprecated
public class ColorPanel extends javax.swing.JPanel
        implements RavenEditorListener
{
    RavenEditor editor;

    RavenEditorWeakListener edListener;
    StrokeStyleMonitor strokeStyleMonitor;
    StrokePaintMonitor strokePaintMonitor;
    FillPaintMonitor fillPaintMonitor;

    FillPaintInlineMonitor fillPaintInlineMonitor = new FillPaintInlineMonitor();
    StrokePaintInlineMonitor strokePaintInlineMonitor = new StrokePaintInlineMonitor();
    StrokeStyleInlineMonitor strokeStyleInlineMonitor = new StrokeStyleInlineMonitor();

    RavenPaint curFillPaintInline;
    RavenPaint curStrokePaintInline;
    RavenStroke curStrokeStyleInline;

    RavenStrokePreviewPanel previewStrokeStyle = new RavenStrokePreviewPanel();
    RavenPaintPreviewPanel previewStrokePaint = new RavenPaintPreviewPanel();
    RavenPaintPreviewPanel previewFillPaint = new RavenPaintPreviewPanel();

    PaintLibraryMonitor paintMon;
    StrokeLibraryMonitor strokeMon;

    int updateCount;

    /** Creates new form ColorPanel */
    public ColorPanel(RavenEditor editor)
    {
        this.editor = editor;
        initComponents();

        edListener = new RavenEditorWeakListener(this, editor);
        editor.addRavenEditorListener(edListener);

        combo_fillPaintRef.setRenderer(new NodeObjectCellRenderer());
        combo_strokePaintRef.setRenderer(new NodeObjectCellRenderer());
        combo_strokeStyleRef.setRenderer(new NodeObjectCellRenderer());

        panel_strokeStyleSwatch.add(previewStrokeStyle, BorderLayout.CENTER);
        panel_strokePaintSwatch.add(previewStrokePaint, BorderLayout.CENTER);
        panel_fillPaintSwatch.add(previewFillPaint, BorderLayout.CENTER);

        previewFillPaint.addMouseListener(fillPaintInlineMonitor);
        previewStrokePaint.addMouseListener(strokePaintInlineMonitor);
        previewStrokeStyle.addMouseListener(strokeStyleInlineMonitor);

        rebuildDocument();
    }

    private void rebuildDocument()
    {
        if (strokeStyleMonitor != null)
        {
            strokeStyleMonitor.remove();
            strokeStyleMonitor = null;

            strokePaintMonitor.remove();
            strokePaintMonitor = null;

            fillPaintMonitor.remove();
            fillPaintMonitor = null;

            paintMon.remove();
            paintMon = null;

            strokeMon.remove();
            strokeMon = null;
        }

        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return;
        }

        NodeDocument root = doc.getCurDocument();
        ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);

        if (service != null)
        {
            strokeStyleMonitor = new StrokeStyleMonitor(service.getStrokeStyleProp());
            strokePaintMonitor = new StrokePaintMonitor(service.getStrokePaintProp());
            fillPaintMonitor = new FillPaintMonitor(service.getFillPaintProp());

            paintMon = new PaintLibraryMonitor(service.getPaintLibrary());
            strokeMon = new StrokeLibraryMonitor(service.getStrokeLibrary());

            updateFromDocument();
        }
    }

    private void updateFromDocument()
    {
        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return;
        }

        ++updateCount;

        rebuildPaintRefCombo();
        rebuildStrokeRefCombo();

        updateFillPaint();
        updateStrokePaint();
        updateStrokeStyle();
//        curFillPaint = root.fillPaint.getValue();
//        curStrokePaint = root.strokePaint.getValue();
//        curStrokeStyle = root.strokeStyle.getValue();
//
//        previewStrokeStyle.setStroke(curStrokeStyle);
//        previewStrokePaint.setPaint(curStrokePaint);
//        previewFillPaint.setPaint(curFillPaint);

        --updateCount;
    }

    private void rebuildPaintRefCombo()
    {
        ++updateCount;

        Object fillSel = combo_fillPaintRef.getSelectedItem();
        Object strokeSel = combo_strokePaintRef.getSelectedItem();

        combo_fillPaintRef.removeAllItems();
        combo_strokePaintRef.removeAllItems();

        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            --updateCount;
            return;
        }

        NodeDocument root = doc.getCurDocument();
        NodeDocument.NodeFilter nodes =
                new NodeDocument.NodeFilter(RavenNodePaint.class);
        root.visit(nodes);

        for (NodeObject obj: nodes.getList())
        {
            combo_fillPaintRef.addItem(obj);
            combo_strokePaintRef.addItem(obj);
        }

        combo_fillPaintRef.setSelectedItem(fillSel);
        combo_strokePaintRef.setSelectedItem(strokeSel);

        --updateCount;
    }

    private void rebuildStrokeRefCombo()
    {
        ++updateCount;

        Object strokeSel = combo_strokePaintRef.getSelectedItem();
        combo_strokeStyleRef.removeAllItems();

        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            --updateCount;
            return;
        }

        NodeDocument root = doc.getCurDocument();
        NodeDocument.NodeFilter nodes =
                new NodeDocument.NodeFilter(RavenNodeStroke.class);
        root.visit(nodes);

        for (NodeObject obj: nodes.getList())
        {
            combo_strokeStyleRef.addItem(obj);
        }
        combo_strokeStyleRef.setSelectedItem(strokeSel);

        --updateCount;
    }

    private void updateStrokeStyle()
    {
        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return;
        }

        ++updateCount;

        NodeDocument root = doc.getCurDocument();
        ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);
//        RavenNodeRoot root = doc.getRoot();

        PropertyData<RavenStroke> data = service.getStrokeStyleProp().getData();
        /*
        if (data instanceof PropertyDataReference)
        {
            RavenNodeStroke node = (RavenNodeStroke)
                    ((PropertyDataReference)data).getValue(root);

            curStrokeStyleInline = node.getRavenStroke();
            combo_strokeStyleRef.setSelectedItem(node);
            radio_strokeStyleRef.setSelected(true);
        }
        else
        {
            curStrokeStyleInline = data.getValue(root);
            radio_strokeStyleInline.setSelected(true);
        }
        previewStrokeStyle.setStroke(curStrokeStyleInline);
        */

        --updateCount;
    }

    private void updateStrokePaint()
    {
        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return;
        }

        ++updateCount;

        NodeDocument root = doc.getCurDocument();
        ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);
//        RavenNodeRoot root = doc.getRoot();

        PropertyData<RavenPaint> data = service.getStrokePaintProp().getData();
        /*
        if (data instanceof PropertyDataReference)
        {
            RavenNodePaint node = (RavenNodePaint)
                    ((PropertyDataReference)data).getValue(root);

            curStrokePaintInline = node.createPaint();
            combo_strokePaintRef.setSelectedItem(node);
            radio_strokePaintRef.setSelected(true);
        }
        else
        {
            curStrokePaintInline = data.getValue(root);
            radio_strokePaintInline.setSelected(true);
        }
        previewStrokePaint.setPaint(curStrokePaintInline);
        */

        --updateCount;
    }

    private void updateFillPaint()
    {
        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return;
        }

        ++updateCount;

        NodeDocument root = doc.getCurDocument();
        ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);
//        RavenNodeRoot root = doc.getRoot();

        PropertyData<RavenPaint> data = service.getFillPaintProp().getData();
        /*
        if (data instanceof PropertyDataReference)
        {
            RavenNodePaint node = (RavenNodePaint)
                    ((PropertyDataReference)data).getValue(root);

            curFillPaintInline = node.createPaint();
            combo_fillPaintRef.setSelectedItem(node);
            radio_fillPaintRef.setSelected(true);
        }
        else
        {
            curFillPaintInline = data.getValue(root);
            radio_fillPaintInline.setSelected(true);
        }
        previewFillPaint.setPaint(curFillPaintInline);
        */

        --updateCount;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup_fillPaint = new javax.swing.ButtonGroup();
        buttonGroup_strokePaint = new javax.swing.ButtonGroup();
        buttonGroup_strokeStyle = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        radio_fillPaintInline = new javax.swing.JRadioButton();
        panel_fillPaintSwatch = new javax.swing.JPanel();
        radio_fillPaintRef = new javax.swing.JRadioButton();
        combo_fillPaintRef = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        radio_strokePaintInline = new javax.swing.JRadioButton();
        panel_strokePaintSwatch = new javax.swing.JPanel();
        radio_strokePaintRef = new javax.swing.JRadioButton();
        combo_strokePaintRef = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        radio_strokeStyleInline = new javax.swing.JRadioButton();
        panel_strokeStyleSwatch = new javax.swing.JPanel();
        radio_strokeStyleRef = new javax.swing.JRadioButton();
        combo_strokeStyleRef = new javax.swing.JComboBox();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Fill"));

        buttonGroup_fillPaint.add(radio_fillPaintInline);
        radio_fillPaintInline.setSelected(true);
        radio_fillPaintInline.setText("Inline");
        radio_fillPaintInline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio_fillPaintInlineActionPerformed(evt);
            }
        });

        panel_fillPaintSwatch.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panel_fillPaintSwatch.setLayout(new java.awt.BorderLayout());

        buttonGroup_fillPaint.add(radio_fillPaintRef);
        radio_fillPaintRef.setText("Reference");
        radio_fillPaintRef.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio_fillPaintRefActionPerformed(evt);
            }
        });

        combo_fillPaintRef.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_fillPaintRefActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(radio_fillPaintRef)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(combo_fillPaintRef, 0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(radio_fillPaintInline)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panel_fillPaintSwatch, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radio_fillPaintInline)
                    .addComponent(panel_fillPaintSwatch, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radio_fillPaintRef)
                    .addComponent(combo_fillPaintRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Stroke"));

        buttonGroup_strokePaint.add(radio_strokePaintInline);
        radio_strokePaintInline.setSelected(true);
        radio_strokePaintInline.setText("Inline");
        radio_strokePaintInline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio_strokePaintInlineActionPerformed(evt);
            }
        });

        panel_strokePaintSwatch.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panel_strokePaintSwatch.setLayout(new java.awt.BorderLayout());

        buttonGroup_strokePaint.add(radio_strokePaintRef);
        radio_strokePaintRef.setText("Reference");
        radio_strokePaintRef.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio_strokePaintRefActionPerformed(evt);
            }
        });

        combo_strokePaintRef.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_strokePaintRefActionPerformed(evt);
            }
        });

        jLabel1.setText("Style");

        jLabel2.setText("Paint");

        buttonGroup_strokeStyle.add(radio_strokeStyleInline);
        radio_strokeStyleInline.setSelected(true);
        radio_strokeStyleInline.setText("Inline");
        radio_strokeStyleInline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio_strokeStyleInlineActionPerformed(evt);
            }
        });

        panel_strokeStyleSwatch.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panel_strokeStyleSwatch.setLayout(new java.awt.BorderLayout());

        buttonGroup_strokeStyle.add(radio_strokeStyleRef);
        radio_strokeStyleRef.setText("Reference");
        radio_strokeStyleRef.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio_strokeStyleRefActionPerformed(evt);
            }
        });

        combo_strokeStyleRef.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_strokeStyleRefActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(radio_strokeStyleInline)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel_strokeStyleSwatch, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(radio_strokeStyleRef)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(combo_strokeStyleRef, 0, 69, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(radio_strokePaintInline)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel_strokePaintSwatch, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(radio_strokePaintRef)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(combo_strokePaintRef, 0, 65, Short.MAX_VALUE)))
                        .addGap(14, 14, 14))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addContainerGap(132, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addContainerGap(132, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(panel_strokePaintSwatch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(radio_strokePaintInline, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radio_strokePaintRef)
                    .addComponent(combo_strokePaintRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panel_strokeStyleSwatch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(radio_strokeStyleInline))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radio_strokeStyleRef)
                    .addComponent(combo_strokeStyleRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void radio_fillPaintInlineActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_radio_fillPaintInlineActionPerformed
    {//GEN-HEADEREND:event_radio_fillPaintInlineActionPerformed
        if (updateCount > 0)
        {
            return;
        }

        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return;
        }

//        RavenNodeRoot root = doc.getRoot();
        NodeDocument root = doc.getCurDocument();
        ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);
        service.getFillPaintProp().setValue(curFillPaintInline);
    }//GEN-LAST:event_radio_fillPaintInlineActionPerformed

    private void radio_fillPaintRefActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_radio_fillPaintRefActionPerformed
    {//GEN-HEADEREND:event_radio_fillPaintRefActionPerformed
        if (updateCount > 0)
        {
            return;
        }

        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return;
        }

        RavenNodePaint node = (RavenNodePaint)combo_fillPaintRef.getSelectedItem();
        if (node == null)
        {
            return;
        }

        NodeDocument root = doc.getCurDocument();
        ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);
//        RavenNodeRoot root = doc.getRoot();
        service.getFillPaintProp().setData(
                new PropertyDataReference<RavenPaint>(node.getUid()));
    }//GEN-LAST:event_radio_fillPaintRefActionPerformed

    private void combo_fillPaintRefActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_combo_fillPaintRefActionPerformed
    {//GEN-HEADEREND:event_combo_fillPaintRefActionPerformed
        if (updateCount > 0)
        {
            return;
        }

        Object item = combo_fillPaintRef.getSelectedItem();

        if (!(item instanceof RavenNodePaint))
        {
            return;
        }

        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return;
        }

        RavenNodePaint node = (RavenNodePaint)item;
        NodeDocument root = doc.getCurDocument();
        ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);
//        RavenNodeRoot root = doc.getRoot();
        service.getFillPaintProp().setData(new PropertyDataReference<RavenPaint>(
                node.getUid()));
    }//GEN-LAST:event_combo_fillPaintRefActionPerformed

    private void radio_strokePaintInlineActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_radio_strokePaintInlineActionPerformed
    {//GEN-HEADEREND:event_radio_strokePaintInlineActionPerformed
        if (updateCount > 0)
        {
            return;
        }

        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return;
        }

        NodeDocument root = doc.getCurDocument();
        ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);
//        RavenNodeRoot root = doc.getRoot();
        service.getStrokePaintProp().setValue(curStrokePaintInline);
    }//GEN-LAST:event_radio_strokePaintInlineActionPerformed

    private void radio_strokePaintRefActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_radio_strokePaintRefActionPerformed
    {//GEN-HEADEREND:event_radio_strokePaintRefActionPerformed
        if (updateCount > 0)
        {
            return;
        }

        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return;
        }

        RavenNodePaint node = (RavenNodePaint)combo_strokePaintRef.getSelectedItem();
        if (node == null)
        {
            return;
        }

        NodeDocument root = doc.getCurDocument();
        ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);
//        RavenNodeRoot root = doc.getRoot();
        service.getStrokePaintProp().setData(
                new PropertyDataReference<RavenPaint>(node.getUid()));
    }//GEN-LAST:event_radio_strokePaintRefActionPerformed

    private void combo_strokePaintRefActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_combo_strokePaintRefActionPerformed
    {//GEN-HEADEREND:event_combo_strokePaintRefActionPerformed
        if (updateCount > 0)
        {
            return;
        }

        Object item = combo_strokePaintRef.getSelectedItem();

        if (!(item instanceof RavenNodePaint))
        {
            return;
        }

        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return;
        }

        RavenNodePaint node = (RavenNodePaint)item;
        NodeDocument root = doc.getCurDocument();
        ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);
//        RavenNodeRoot root = doc.getRoot();
        service.getStrokePaintProp().setData(new PropertyDataReference<RavenPaint>(
                node.getUid()));
    }//GEN-LAST:event_combo_strokePaintRefActionPerformed

    private void radio_strokeStyleInlineActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_radio_strokeStyleInlineActionPerformed
    {//GEN-HEADEREND:event_radio_strokeStyleInlineActionPerformed
        if (updateCount > 0)
        {
            return;
        }

        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return;
        }

        NodeDocument root = doc.getCurDocument();
        ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);
//        RavenNodeRoot root = doc.getRoot();
        service.getStrokeStyleProp().setValue(curStrokeStyleInline);
    }//GEN-LAST:event_radio_strokeStyleInlineActionPerformed

    private void radio_strokeStyleRefActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_radio_strokeStyleRefActionPerformed
    {//GEN-HEADEREND:event_radio_strokeStyleRefActionPerformed
        if (updateCount > 0)
        {
            return;
        }

        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return;
        }

        RavenNodeStroke node = (RavenNodeStroke)combo_strokeStyleRef.getSelectedItem();
        if (node == null)
        {
            return;
        }

        NodeDocument root = doc.getCurDocument();
        ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);
//        RavenNodeRoot root = doc.getRoot();
        service.getStrokeStyleProp().setData(
                new PropertyDataReference<RavenStroke>(node.getUid()));
    }//GEN-LAST:event_radio_strokeStyleRefActionPerformed

    private void combo_strokeStyleRefActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_combo_strokeStyleRefActionPerformed
    {//GEN-HEADEREND:event_combo_strokeStyleRefActionPerformed
        if (updateCount > 0)
        {
            return;
        }

        Object item = combo_strokeStyleRef.getSelectedItem();

        if (!(item instanceof RavenNodeStroke))
        {
            return;
        }

        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return;
        }

        RavenNodeStroke node = (RavenNodeStroke)item;
        NodeDocument root = doc.getCurDocument();
        ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);
//        RavenNodeRoot root = doc.getRoot();
        service.getStrokeStyleProp().setData(new PropertyDataReference<RavenStroke>(
                node.getUid()));
    }//GEN-LAST:event_combo_strokeStyleRefActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup_fillPaint;
    private javax.swing.ButtonGroup buttonGroup_strokePaint;
    private javax.swing.ButtonGroup buttonGroup_strokeStyle;
    private javax.swing.JComboBox combo_fillPaintRef;
    private javax.swing.JComboBox combo_strokePaintRef;
    private javax.swing.JComboBox combo_strokeStyleRef;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel panel_fillPaintSwatch;
    private javax.swing.JPanel panel_strokePaintSwatch;
    private javax.swing.JPanel panel_strokeStyleSwatch;
    private javax.swing.JRadioButton radio_fillPaintInline;
    private javax.swing.JRadioButton radio_fillPaintRef;
    private javax.swing.JRadioButton radio_strokePaintInline;
    private javax.swing.JRadioButton radio_strokePaintRef;
    private javax.swing.JRadioButton radio_strokeStyleInline;
    private javax.swing.JRadioButton radio_strokeStyleRef;
    // End of variables declaration//GEN-END:variables

    @Override
    public void recentFilesChanged(EventObject evt)
    {
    }

    @Override
    public void documentChanged(EventObject evt)
    {
        rebuildDocument();
    }

    //--------------------------------------

    class StrokeStyleInlineMonitor extends MouseAdapter
            implements PropertyChangeListener
    {
        RavenStrokeInlineDialog dlg;
        NodeDocument root;

        private void setPaint(RavenStroke stroke, boolean history)
        {
            ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);
            service.getStrokeStyleProp().setValue(stroke, history);
        }

        @Override
        public void mouseClicked(MouseEvent e)
        {
            RavenDocument doc = editor.getDocument();
            if (doc == null)
            {
                return;
            }
            root = doc.getCurDocument();

            ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);
            RavenStroke initStroke = service.getStrokeStyleProp().getValue();
/*
            Window parent = SwingUtilities.getWindowAncestor(ColorPanel.this);
            dlg = new RavenStrokeInlineDialog(parent);
            dlg.setStroke((RavenStrokeBasic)curStrokeStyleInline);
            dlg.addPropertyChangeListener(RavenStrokeBasicPanel.PROP_STROKE, this);

            RavenSwingUtil.centerWindow(dlg, parent.getBounds());
            dlg.setVisible(true);

            if (dlg.isSelected())
            {
                setPaint(dlg.getStroke(), true);
            }
            else
            {
                setPaint(initStroke, false);
            }
*/
            dlg = null;
            root = null;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt)
        {
            /*
            setPaint(dlg.getStroke(), false);
            */
        }
    }

    class StrokePaintInlineMonitor extends MouseAdapter
            implements PropertyChangeListener
    {
        RavenPaintInlineDialog dlg;
        NodeDocument root;

        private void setPaint(RavenPaint paint, boolean history)
        {
            ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);
            service.getStrokePaintProp().setValue(paint, history);
        }

        @Override
        public void mouseClicked(MouseEvent e)
        {
            RavenDocument doc = editor.getDocument();
            if (doc == null)
            {
                return;
            }
            root = doc.getCurDocument();

            ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);
            RavenPaint initPaint = service.getStrokePaintProp().getValue();
/*
            Window parent = SwingUtilities.getWindowAncestor(ColorPanel.this);
            dlg = new RavenPaintInlineDialog(parent);
            dlg.setPaint(curStrokePaintInline);
            dlg.addPropertyChangeListener(RavenPaintInlinePanel.PROP_PAINT, this);

            RavenSwingUtil.centerWindow(dlg, parent.getBounds());
            dlg.setVisible(true);

            if (dlg.isColorSelected())
            {
                setPaint(dlg.getPaint(), true);
            }
            else
            {
                setPaint(initPaint, false);
            }
*/
            dlg = null;
            root = null;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt)
        {
            /*
            setPaint(dlg.getPaint(), false);
            */
        }
    }

    class FillPaintInlineMonitor extends MouseAdapter
            implements PropertyChangeListener
    {
        RavenPaintInlineDialog dlg;
        NodeDocument root;

        private void setPaint(RavenPaint paint, boolean history)
        {
            ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);
            service.getFillPaintProp().setValue(paint, history);
        }

        @Override
        public void mouseClicked(MouseEvent e)
        {
            RavenDocument doc = editor.getDocument();
            if (doc == null)
            {
                return;
            }
            root = doc.getCurDocument();

            ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);
            RavenPaint initPaint = service.getFillPaintProp().getValue();
/*
            Window parent = SwingUtilities.getWindowAncestor(ColorPanel.this);
            dlg = new RavenPaintInlineDialog(parent);
            dlg.setPaint(curFillPaintInline);
            dlg.addPropertyChangeListener(RavenPaintInlinePanel.PROP_PAINT, this);

            RavenSwingUtil.centerWindow(dlg, parent.getBounds());
            dlg.setVisible(true);

            if (dlg.isColorSelected())
            {
                setPaint(dlg.getPaint(), true);
            }
            else
            {
                setPaint(initPaint, false);
            }
*/
            dlg = null;
            root = null;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt)
        {
            /*
            setPaint(dlg.getPaint(), false);
            */
        }
    }

    class StrokeStyleMonitor extends PropertyWrapperAdapter
    {
        final PropertyWrapper<RavenNodeRoot, RavenStroke> source;
        PropertyWrapperWeakListener listener;

        public StrokeStyleMonitor(PropertyWrapper<RavenNodeRoot, RavenStroke> source)
        {
            this.source = source;
            listener = new PropertyWrapperWeakListener(this, source);
            source.addPropertyWrapperListener(listener);
        }

        @Override
        public void propertyWrapperDataChanged(PropertyChangeEvent evt)
        {
            updateStrokeStyle();
        }

        private void remove()
        {
            listener.remove();
            listener = null;
        }
    }

    class StrokePaintMonitor extends PropertyWrapperAdapter
    {
        final PropertyWrapper<RavenNodeRoot, RavenPaint> source;
        PropertyWrapperWeakListener listener;

        public StrokePaintMonitor(PropertyWrapper<RavenNodeRoot, RavenPaint> source)
        {
            this.source = source;
            listener = new PropertyWrapperWeakListener(this, source);
            source.addPropertyWrapperListener(listener);
        }

        @Override
        public void propertyWrapperDataChanged(PropertyChangeEvent evt)
        {
            updateStrokePaint();
        }

        private void remove()
        {
            listener.remove();
            listener = null;
        }
    }

    class FillPaintMonitor extends PropertyWrapperAdapter
    {
        final PropertyWrapper<RavenNodeRoot, RavenPaint> source;
        PropertyWrapperWeakListener listener;

        public FillPaintMonitor(PropertyWrapper<RavenNodeRoot, RavenPaint> source)
        {
            this.source = source;
            listener = new PropertyWrapperWeakListener(this, source);
            source.addPropertyWrapperListener(listener);
        }

        @Override
        public void propertyWrapperDataChanged(PropertyChangeEvent evt)
        {
            updateFillPaint();
        }

        private void remove()
        {
            listener.remove();
            listener = null;
        }
    }

    class PaintLibraryMonitor implements ChildWrapperListener
    {
        final RavenNodePaintLibrary paintLib;
        ChildWrapperWeakListener listener;

        public PaintLibraryMonitor(RavenNodePaintLibrary paintLib)
        {
            this.paintLib = paintLib;
            listener = new ChildWrapperWeakListener(this, paintLib.paints);
            paintLib.paints.addChildWrapperListener(listener);
        }

        @Override
        public void childWrapperNodeAdded(ChildWrapperEvent evt)
        {
            rebuildPaintRefCombo();
        }

        @Override
        public void childWrapperNodeRemoved(ChildWrapperEvent evt)
        {
            rebuildPaintRefCombo();
        }

        private void remove()
        {
            listener.remove();
            listener = null;
        }
    }

    class StrokeLibraryMonitor implements ChildWrapperListener
    {
        final RavenNodeStrokeLibrary strokeLib;
        ChildWrapperWeakListener listener;

        public StrokeLibraryMonitor(RavenNodeStrokeLibrary strokeLib)
        {
            this.strokeLib = strokeLib;
            listener = new ChildWrapperWeakListener(this, strokeLib.strokes);
            strokeLib.strokes.addChildWrapperListener(listener);
        }

        @Override
        public void childWrapperNodeAdded(ChildWrapperEvent evt)
        {
            rebuildStrokeRefCombo();
        }

        @Override
        public void childWrapperNodeRemoved(ChildWrapperEvent evt)
        {
            rebuildStrokeRefCombo();
        }

        private void remove()
        {
            listener.remove();
            listener = null;
        }
    }

}
