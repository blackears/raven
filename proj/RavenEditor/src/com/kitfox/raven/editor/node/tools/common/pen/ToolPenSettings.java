/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.raven.editor.node.tools.common.pen;

import com.kitfox.raven.editor.RavenEditor;

/**
 *
 * @author kitfox
 */
public class ToolPenSettings extends javax.swing.JPanel
{
    RavenEditor editor;
    ToolPenProvider prov;

    /**
     * Creates new form ToolPen2Settings
     */
    public ToolPenSettings(RavenEditor editor, ToolPenProvider prov)
    {
        this.editor = editor;
        this.prov = prov;
        initComponents();
        
        switch (prov.getEditMode())
        {
            case ADD:
                bn_editModeAdd.setSelected(true);
            case DELETE:
                bn_editModeDelete.setSelected(true);
            case EDIT:
                bn_editModeEdit.setSelected(true);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup_editMode = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        bn_editModeAdd = new javax.swing.JToggleButton();
        bn_editModeDelete = new javax.swing.JToggleButton();
        bn_editModeEdit = new javax.swing.JToggleButton();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Point Mode"));

        buttonGroup_editMode.add(bn_editModeAdd);
        bn_editModeAdd.setText("Add");
        bn_editModeAdd.setMargin(new java.awt.Insets(4, 4, 4, 4));
        bn_editModeAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bn_editModeAddActionPerformed(evt);
            }
        });
        jPanel1.add(bn_editModeAdd);

        buttonGroup_editMode.add(bn_editModeDelete);
        bn_editModeDelete.setText("Delete");
        bn_editModeDelete.setMargin(new java.awt.Insets(4, 4, 4, 4));
        bn_editModeDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bn_editModeDeleteActionPerformed(evt);
            }
        });
        jPanel1.add(bn_editModeDelete);

        buttonGroup_editMode.add(bn_editModeEdit);
        bn_editModeEdit.setText("Edit");
        bn_editModeEdit.setMargin(new java.awt.Insets(4, 4, 4, 4));
        bn_editModeEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bn_editModeEditActionPerformed(evt);
            }
        });
        jPanel1.add(bn_editModeEdit);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(244, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(233, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void bn_editModeAddActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bn_editModeAddActionPerformed
    {//GEN-HEADEREND:event_bn_editModeAddActionPerformed
        prov.setEditMode(PenEditMode.ADD);
    }//GEN-LAST:event_bn_editModeAddActionPerformed

    private void bn_editModeDeleteActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bn_editModeDeleteActionPerformed
    {//GEN-HEADEREND:event_bn_editModeDeleteActionPerformed
        prov.setEditMode(PenEditMode.DELETE);
    }//GEN-LAST:event_bn_editModeDeleteActionPerformed

    private void bn_editModeEditActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bn_editModeEditActionPerformed
    {//GEN-HEADEREND:event_bn_editModeEditActionPerformed
        prov.setEditMode(PenEditMode.EDIT);
    }//GEN-LAST:event_bn_editModeEditActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton bn_editModeAdd;
    private javax.swing.JToggleButton bn_editModeDelete;
    private javax.swing.JToggleButton bn_editModeEdit;
    private javax.swing.ButtonGroup buttonGroup_editMode;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
