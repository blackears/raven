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
 * GradientStyleCustomEditor.java
 *
 * Created on Dec 31, 2010, 11:23:56 PM
 */

package com.kitfox.raven.paint.control;

import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.coyote.math.CyGradientStops;
import com.kitfox.coyote.math.CyGradientStops.Cycle;
import com.kitfox.coyote.math.CyGradientStops.Style;
import com.kitfox.raven.paint.common.RavenPaintColor;
import com.kitfox.raven.paint.control.Gradient.StopEditor;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author kitfox
 */
public class GradientStyleEditorPanel extends javax.swing.JPanel
        implements StopModelListener, StopEditor
{
    GradientSliderPanel panelSlider = new GradientSliderPanel();
    
    Gradient gradient;
    public static final String PROP_GRADIENT = "gradient";

    boolean updating = true;

    CyGradientStops gradientCompiled;

    /** Creates new form GradientStyleCustomEditor */
    public GradientStyleEditorPanel()
    {
        initComponents();

        panelSlider.setSide(StopSide.SOUTH);
        panelSlider.setSliderMargin(16);
        panel_gradient.add(panelSlider, BorderLayout.CENTER);

        combo_cycle.addItem(Cycle.NO_CYCLE);
        combo_cycle.addItem(Cycle.REFLECT);
        combo_cycle.addItem(Cycle.REPEAT);

        combo_style.addItem(Style.LINEAR);
        combo_style.addItem(Style.RADIAL);

        loadGradient(null);
    }

    public CyGradientStops getGradient()
    {
        if (gradientCompiled == null)
        {
            gradientCompiled = new CyGradientStops(
                    getOffsets(),
                    getColors(),
                    getCycle(),
                    getStyle());
        }
        return gradientCompiled;
    }

    public void setGradient(CyGradientStops value)
    {
        if ((gradientCompiled == null && value == null)
                || (gradientCompiled != null && gradientCompiled.equals(value)))
        {
            return;
        }

        gradientCompiled = value;
        loadGradient(value);
    }

    private void loadGradient(CyGradientStops grad)
    {
        updating = true;

        if (grad == null)
        {
            grad = new CyGradientStops();
        }

        //MultipleGradientStyle grad = (MultipleGradientStyle)value;

        CyColor4f[] colors = grad.getColors();
        float[] offsets = grad.getFractions();
        GradientStop[] stops = new GradientStop[colors.length];
        for (int i = 0; i < colors.length; ++i)
        {
            stops[i] = new GradientStop(colors[i], offsets[i]);
        }

        if (gradient != null)
        {
            gradient.removeStopModelListener(this);
        }
        gradient = new Gradient(stops);
        gradient.setHorizontal(true);
        gradient.setStopEditor(this);
        gradient.addStopModelListener(this);
        panelSlider.setGradient(gradient);

        combo_cycle.setSelectedItem(grad.getCycleMethod());
        combo_style.setSelectedItem(grad.getStyle());

        updating = false;

        firePropertyChange(PROP_GRADIENT, null, null);
    }

    @Override
    public void stopModelChanged(ChangeEvent evt)
    {
        gradientCompiled = null;
        firePropertyChange(PROP_GRADIENT, null, null);
    }

    @Override
    public void beginStopEdits(ChangeEvent evt)
    {
    }

    @Override
    public void endStopEdits(ChangeEvent evt)
    {
        gradientCompiled = null;
        firePropertyChange(PROP_GRADIENT, null, null);
    }


    public float[] getOffsets()
    {
        if (gradient == null)
        {
            return new float[]{0, 1};
        }
        ArrayList<GradientStop> stops = gradient.getStopObjects();
        Collections.sort(stops);
        float[] arr = new float[stops.size()];
        for (int i = 0; i < arr.length; ++i)
        {
            arr[i] = stops.get(i).getOffset();
        }
        return arr;
    }

    public CyColor4f[] getColors()
    {
        if (gradient == null)
        {
            return new CyColor4f[]{CyColor4f.BLACK, CyColor4f.WHITE};
        }
        ArrayList<GradientStop> stops = gradient.getStopObjects();
        Collections.sort(stops);
        CyColor4f[] arr = new CyColor4f[stops.size()];
        for (int i = 0; i < arr.length; ++i)
        {
            arr[i] = stops.get(i).getColor();
        }
        return arr;
    }

    public CyGradientStops.Cycle getCycle()
    {
        return (CyGradientStops.Cycle)combo_cycle.getSelectedItem();
    }

    public CyGradientStops.Style getStyle()
    {
        return (CyGradientStops.Style)combo_style.getSelectedItem();
    }

    @Override
    public void editStop(final GradientStop stop)
    {
        final ColorEditorPanel colorEditor = new ColorEditorPanel();

        PropertyChangeListener colorCopier = new PropertyChangeListener()
        {
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                stop.setColor(colorEditor.getColor().asColor());
            }
        };

        gradient.beginStopEdits();
        colorEditor.setColor(new RavenPaintColor(stop.getColor()));
        colorEditor.addPropertyChangeListener(colorCopier);
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit Color", ModalityType.APPLICATION_MODAL);
        dlg.getContentPane().add(colorEditor, BorderLayout.CENTER);
        dlg.pack();
        dlg.setVisible(true);

        colorEditor.removePropertyChangeListener(colorCopier);
        stop.setColor(colorEditor.getColor().asColor());
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
        jPanel4 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        combo_style = new javax.swing.JComboBox();

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
                .addComponent(combo_cycle, 0, 150, Short.MAX_VALUE)
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

        jLabel12.setText("Style");

        combo_style.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_styleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(combo_style, 0, 151, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(combo_style, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel4);

        add(jPanel1, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

    private void panel_gradientMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_panel_gradientMouseReleased
    {//GEN-HEADEREND:event_panel_gradientMouseReleased
//        firePropertyChange(PROP_GRADIENT, null, null);
}//GEN-LAST:event_panel_gradientMouseReleased

    private void combo_cycleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_combo_cycleActionPerformed
    {//GEN-HEADEREND:event_combo_cycleActionPerformed
        if (updating)
        {
            return;
        }

        gradientCompiled = null;
        firePropertyChange(PROP_GRADIENT, null, null);
}//GEN-LAST:event_combo_cycleActionPerformed

    private void combo_styleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_combo_styleActionPerformed
    {//GEN-HEADEREND:event_combo_styleActionPerformed
        if (updating)
        {
            return;
        }

        gradientCompiled = null;
        firePropertyChange(PROP_GRADIENT, null, null);
}//GEN-LAST:event_combo_styleActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox combo_cycle;
    private javax.swing.JComboBox combo_style;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel panel_gradient;
    // End of variables declaration//GEN-END:variables

}
