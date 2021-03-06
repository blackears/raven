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
 * RavenPaintInlineDialog.java
 *
 * Created on Jan 20, 2011, 10:40:39 AM
 */

package com.kitfox.raven.editor.stroke;

import com.kitfox.raven.util.RavenSwingUtil;
import java.awt.BorderLayout;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author kitfox
 */
@Deprecated
public class RavenStrokeInlineDialog extends javax.swing.JDialog
        implements PropertyChangeListener
{

    RavenStrokeBasicPanel panel = new RavenStrokeBasicPanel();

    private boolean selected = false;

    /** Creates new form RavenPaintInlineDialog */
    public RavenStrokeInlineDialog(Window parent)
    {
        super(parent, "Choose Stroke", DEFAULT_MODALITY_TYPE);
        initComponents();

        panel_workArea.add(panel, BorderLayout.CENTER);
        panel.addPropertyChangeListener(RavenStrokeBasicPanel.PROP_STROKE, this);
        pack();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        //rethrow
        firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
    }

    /**
     * @return the paint
     */
    public RavenStrokeBasic getStroke()
    {
        return panel.getStroke();
    }

    /**
     * @param stroke the paint to set
     */
    public void setStroke(RavenStrokeBasic stroke)
    {
        panel.setStroke(stroke);
    }

    /**
     * @return the colorSelected
     */
    public boolean isSelected()
    {
        return selected;
    }

    public static RavenStrokeBasic showPicker(Window parent)
    {
        RavenStrokeInlineDialog dlg = new RavenStrokeInlineDialog(parent);
        RavenSwingUtil.centerWindow(parent, parent.getBounds());
        dlg.setVisible(true);

        return dlg.selected ? dlg.getStroke() : null;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel_workArea = new javax.swing.JPanel();
        panel_buttonArea = new javax.swing.JPanel();
        bn_okay = new javax.swing.JButton();
        bn_cancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        panel_workArea.setLayout(new java.awt.BorderLayout());
        getContentPane().add(panel_workArea, java.awt.BorderLayout.CENTER);

        bn_okay.setText("Ok");
        bn_okay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bn_okayActionPerformed(evt);
            }
        });
        panel_buttonArea.add(bn_okay);

        bn_cancel.setText("Cancel");
        bn_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bn_cancelActionPerformed(evt);
            }
        });
        panel_buttonArea.add(bn_cancel);

        getContentPane().add(panel_buttonArea, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bn_okayActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bn_okayActionPerformed
    {//GEN-HEADEREND:event_bn_okayActionPerformed
        setVisible(false);
        dispose();
        selected = true;
    }//GEN-LAST:event_bn_okayActionPerformed

    private void bn_cancelActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bn_cancelActionPerformed
    {//GEN-HEADEREND:event_bn_cancelActionPerformed
        setVisible(false);
        dispose();
    }//GEN-LAST:event_bn_cancelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bn_cancel;
    private javax.swing.JButton bn_okay;
    private javax.swing.JPanel panel_buttonArea;
    private javax.swing.JPanel panel_workArea;
    // End of variables declaration//GEN-END:variables

}
