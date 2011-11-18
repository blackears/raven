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
 * PaintLayoutCustomEditor.java
 *
 * Created on Jan 1, 2011, 4:00:23 AM
 */

package com.kitfox.raven.editor.paint;

import com.kitfox.game.control.color.PaintLayout;
import com.kitfox.game.control.color.PaintLayoutLinear;
import com.kitfox.game.control.color.PaintLayoutRadial;
import com.kitfox.game.control.color.PaintLayoutTexture;
import com.kitfox.raven.util.tree.PropertyCustomEditor;
import com.kitfox.raven.util.tree.PropertyData;
import com.kitfox.raven.util.tree.PropertyDataInline;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.SwingUtilities;

/**
 *
 * @author kitfox
 */
public class PaintLayoutCustomEditor extends javax.swing.JPanel
        implements PropertyCustomEditor
{
    final PaintLayoutEditor ed;

    public static final String TYPE_LINEAR = "LINEAR";
    public static final String TYPE_RADIAL = "RADIAL";
    public static final String TYPE_TEXTURE = "TEXTURE";

    PaintLayoutLinearEditor linearEditor = new PaintLayoutLinearEditor();
    PaintLayoutRadialEditor radialEditor = new PaintLayoutRadialEditor();
    PaintLayoutTextureEditor textureEditor = new PaintLayoutTextureEditor();

    PropertyData<PaintLayout> initValue;
    PropertyData<PaintLayout> curValue;

    boolean updating = true;

    /** Creates new form PaintLayoutCustomEditor */
    public PaintLayoutCustomEditor(PaintLayoutEditor ed)
    {
        initComponents();

        this.ed = ed;
        curValue = initValue = ed.getValue();

        combo_layoutType.addItem(TYPE_LINEAR);
        combo_layoutType.addItem(TYPE_RADIAL);
        combo_layoutType.addItem(TYPE_TEXTURE);

        linearEditor.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                setValue(linearEditor.getPaintLayout());
            }
        });

        radialEditor.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                setValue(radialEditor.getPaintLayout());
            }
        });

        textureEditor.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                setValue(textureEditor.getPaintLayout());
            }
        });

        updateTo(initValue.getValue(ed.getDocument()));
    }

    private void updateTo(PaintLayout layout)
    {
        updating = true;

        panel_workArea.removeAll();

        if (layout instanceof PaintLayoutLinear)
        {
            combo_layoutType.setSelectedItem(TYPE_LINEAR);
            panel_workArea.add(linearEditor, BorderLayout.CENTER);
            linearEditor.setPaintLayout((PaintLayoutLinear)layout);
        }
        else if (layout instanceof PaintLayoutRadial)
        {
            combo_layoutType.setSelectedItem(TYPE_RADIAL);
            panel_workArea.add(radialEditor, BorderLayout.CENTER);
            radialEditor.setPaintLayout((PaintLayoutRadial)layout);
        }
        else if (layout instanceof PaintLayoutTexture)
        {
            combo_layoutType.setSelectedItem(TYPE_TEXTURE);
            panel_workArea.add(textureEditor, BorderLayout.CENTER);
            textureEditor.setPaintLayout((PaintLayoutTexture)layout);
        }

        updating = false;

        revalidate();
        Window ancestor = SwingUtilities.getWindowAncestor(this);
        if (ancestor != null)
        {
            ancestor.pack();
        }
        repaint();
    }

    public void setValue(PaintLayout value)
    {
        setValue(new PropertyDataInline(value));
    }

    public void setValue(PropertyData<PaintLayout> data)
    {
        curValue = data;
        ed.setValue(data, false);
        
//        updateTo(curValue.getValue(ed.getDocument()));
    }

    @Override
    public void customEditorCommit()
    {
        ed.setValue(curValue);
    }

    @Override
    public void customEditorCancel()
    {
        ed.setValue(initValue, false);
    }

    @Override
    public Component getCustomEditor()
    {
        return this;
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        combo_layoutType = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        panel_workArea = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        combo_layoutType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_layoutTypeActionPerformed(evt);
            }
        });

        jLabel1.setText("Layout Type");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(8, 8, 8)
                .addComponent(combo_layoutType, 0, 312, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(combo_layoutType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel1, java.awt.BorderLayout.NORTH);

        panel_workArea.setLayout(new java.awt.BorderLayout());
        add(panel_workArea, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void combo_layoutTypeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_combo_layoutTypeActionPerformed
    {//GEN-HEADEREND:event_combo_layoutTypeActionPerformed
        if (updating)
        {
            return;
        }

        String type = (String)combo_layoutType.getSelectedItem();

        if (TYPE_LINEAR.equals(type))
        {
            PaintLayoutLinear layout = PaintLayoutLinear.create(
                    curValue.getValue(ed.getDocument()));
            setValue(layout);
            updateTo(layout);
        }
        else if (TYPE_RADIAL.equals(type))
        {
            PaintLayoutRadial layout = PaintLayoutRadial.create(
                    curValue.getValue(ed.getDocument()));
            setValue(layout);
            updateTo(layout);
        }
        else if (TYPE_TEXTURE.equals(type))
        {
            PaintLayoutTexture layout = PaintLayoutTexture.create(
                    curValue.getValue(ed.getDocument()));
            setValue(layout);
            updateTo(layout);
        }
    }//GEN-LAST:event_combo_layoutTypeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox combo_layoutType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel panel_workArea;
    // End of variables declaration//GEN-END:variables

}
