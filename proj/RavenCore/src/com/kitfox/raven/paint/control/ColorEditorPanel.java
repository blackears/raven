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
 * ColorStyleEditorPanel.java
 *
 * Created on Sep 19, 2009, 6:23:16 PM
 */

package com.kitfox.raven.paint.control;

import com.kitfox.coyote.math.MathColorUtil;
import com.kitfox.raven.paint.common.RavenPaintColor;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author kitfox
 */
public class ColorEditorPanel extends javax.swing.JPanel
        implements PropertyChangeListener
{
    private static final long serialVersionUID = 0;

    public static final String PROP_COLOR = "color";

    SimpleColorModel model = new SimpleColorModel();
    ColorFieldH fieldH = new ColorFieldH(model);
    ColorFieldS fieldS = new ColorFieldS(model);
    ColorFieldV fieldV = new ColorFieldV(model);
    ColorFieldA fieldA = new ColorFieldA(model);
    ColorFieldHS fieldHS = new ColorFieldHS(model);
    ColorFieldHV fieldHV = new ColorFieldHV(model);
    ColorFieldSV fieldSV = new ColorFieldSV(model);

    ColorSliderPanel sliderA = new ColorSliderPanel();

    ColorStylePickSlidePanel pickHS;
    ColorStylePickSlidePanel pickHV;
    ColorStylePickSlidePanel pickSV;

    float[] hsv = new float[3];
    float[] rgb = new float[3];
    boolean updating = true;

    /** Creates new form ColorStyleEditorPanel */
    public ColorEditorPanel()
    {
        initComponents();

        model.addPropertyChangeListener(this);

        model.addPropertyChangeListener(fieldHS);
        model.addPropertyChangeListener(fieldHV);
        model.addPropertyChangeListener(fieldSV);
        model.addPropertyChangeListener(fieldH);
        model.addPropertyChangeListener(fieldS);
        model.addPropertyChangeListener(fieldV);
        model.addPropertyChangeListener(fieldA);

        pickHS = new ColorStylePickSlidePanel(model, fieldHS, fieldV);
        pickHV = new ColorStylePickSlidePanel(model, fieldHV, fieldS);
        pickSV = new ColorStylePickSlidePanel(model, fieldSV, fieldH);

        panel_pickers.add(pickSV);
        panel_pickers.add(pickHV);
        panel_pickers.add(pickHS);

        //Alpha slider
        fieldA.setHorizontal(true);
        sliderA.setColorChooserModel(model);
        sliderA.setColorField(fieldA);
        sliderA.setSliderMargin(16);
        panel_alphaSlider.add(sliderA, BorderLayout.CENTER);

        updateText();
    }

    private void updateText()
    {
        updating = true;

        RavenPaintColor col = model.getColor();
        MathColorUtil.RGBtoHSV(col.r, col.g, col.b, hsv);

        spinner_r.setValue((int)(col.getR() * 255 + .5f));
        spinner_g.setValue((int)(col.getG() * 255 + .5f));
        spinner_b.setValue((int)(col.getB() * 255 + .5f));

        spinner_h.setValue((int)(hsv[0] * 359 + .5f));
        spinner_s.setValue((int)(hsv[1] * 100 + .5f));
        spinner_v.setValue((int)(hsv[2] * 100 + .5f));

        spinner_a.setValue((int)(col.getA() * 255 + .5f));

        updating = false;
    }

    public RavenPaintColor getColor()
    {
        return model.getColor();
    }

    public void setColor(RavenPaintColor color)
    {
        model.setColor(color);
    }

    private void setTextHSV()
    {
        if (updating)
        {
            return;
        }

        int h = (Integer)spinner_h.getValue();
        int s = (Integer)spinner_s.getValue();
        int v = (Integer)spinner_v.getValue();
        int a = (Integer)spinner_a.getValue();

        MathColorUtil.HSVtoRGB(h / 360f, s / 100f, v / 100f, rgb);
        model.setColor(new RavenPaintColor(rgb[0], rgb[1], rgb[2], a / 255f));
    }

    private void setTextRGB()
    {
        if (updating)
        {
            return;
        }

        int r = (Integer)spinner_r.getValue();
        int g = (Integer)spinner_g.getValue();
        int b = (Integer)spinner_b.getValue();
        int a = (Integer)spinner_a.getValue();

        model.setColor(new RavenPaintColor(r, g, b, a));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        //Echo color prpperty changes
        updateText();
        firePropertyChange(PROP_COLOR, null, null);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel_pickers = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        panel_text = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        spinner_r = new javax.swing.JSpinner();
        spinner_g = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        spinner_b = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        spinner_h = new javax.swing.JSpinner();
        spinner_s = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        spinner_v = new javax.swing.JSpinner();
        panel_alpha = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        spinner_a = new javax.swing.JSpinner();
        panel_alphaSlider = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        panel_pickers.setLayout(new javax.swing.BoxLayout(panel_pickers, javax.swing.BoxLayout.LINE_AXIS));
        add(panel_pickers, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        jLabel1.setText("R");

        spinner_r.setModel(new javax.swing.SpinnerNumberModel(0, 0, 255, 1));
        spinner_r.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinner_rStateChanged(evt);
            }
        });

        spinner_g.setModel(new javax.swing.SpinnerNumberModel(0, 0, 255, 1));
        spinner_g.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinner_gStateChanged(evt);
            }
        });

        jLabel2.setText("G");

        spinner_b.setModel(new javax.swing.SpinnerNumberModel(0, 0, 255, 1));
        spinner_b.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinner_bStateChanged(evt);
            }
        });

        jLabel3.setText("B");

        spinner_h.setModel(new javax.swing.SpinnerNumberModel(0, 0, 359, 1));
        spinner_h.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinner_hStateChanged(evt);
            }
        });

        spinner_s.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        spinner_s.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinner_sStateChanged(evt);
            }
        });

        jLabel4.setText("H");

        jLabel5.setText("S");

        jLabel6.setText("V");

        spinner_v.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        spinner_v.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinner_vStateChanged(evt);
            }
        });

        javax.swing.GroupLayout panel_textLayout = new javax.swing.GroupLayout(panel_text);
        panel_text.setLayout(panel_textLayout);
        panel_textLayout.setHorizontalGroup(
            panel_textLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_textLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_textLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_textLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinner_r, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinner_g, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinner_b, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel_textLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinner_h, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinner_s, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinner_v, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(203, Short.MAX_VALUE))
        );
        panel_textLayout.setVerticalGroup(
            panel_textLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_textLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_textLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(spinner_h, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(spinner_s, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(spinner_v, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel_textLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(spinner_r, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(spinner_g, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(spinner_b, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(panel_text);

        jLabel7.setText("A");

        spinner_a.setModel(new javax.swing.SpinnerNumberModel(0, 0, 255, 1));
        spinner_a.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinner_aStateChanged(evt);
            }
        });

        panel_alphaSlider.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout panel_alphaLayout = new javax.swing.GroupLayout(panel_alpha);
        panel_alpha.setLayout(panel_alphaLayout);
        panel_alphaLayout.setHorizontalGroup(
            panel_alphaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_alphaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spinner_a, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_alphaSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE))
        );
        panel_alphaLayout.setVerticalGroup(
            panel_alphaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_alphaLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(panel_alphaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(spinner_a, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addComponent(panel_alphaSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel1.add(panel_alpha);

        add(jPanel1, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void spinner_hStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_spinner_hStateChanged
    {//GEN-HEADEREND:event_spinner_hStateChanged
        setTextHSV();
    }//GEN-LAST:event_spinner_hStateChanged

    private void spinner_sStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_spinner_sStateChanged
    {//GEN-HEADEREND:event_spinner_sStateChanged
        setTextHSV();
    }//GEN-LAST:event_spinner_sStateChanged

    private void spinner_vStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_spinner_vStateChanged
    {//GEN-HEADEREND:event_spinner_vStateChanged
        setTextHSV();
    }//GEN-LAST:event_spinner_vStateChanged

    private void spinner_rStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_spinner_rStateChanged
    {//GEN-HEADEREND:event_spinner_rStateChanged
        setTextRGB();
    }//GEN-LAST:event_spinner_rStateChanged

    private void spinner_gStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_spinner_gStateChanged
    {//GEN-HEADEREND:event_spinner_gStateChanged
        setTextRGB();
    }//GEN-LAST:event_spinner_gStateChanged

    private void spinner_bStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_spinner_bStateChanged
    {//GEN-HEADEREND:event_spinner_bStateChanged
        setTextRGB();
    }//GEN-LAST:event_spinner_bStateChanged

    private void spinner_aStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_spinner_aStateChanged
    {//GEN-HEADEREND:event_spinner_aStateChanged
        setTextRGB();
    }//GEN-LAST:event_spinner_aStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel panel_alpha;
    private javax.swing.JPanel panel_alphaSlider;
    private javax.swing.JPanel panel_pickers;
    private javax.swing.JPanel panel_text;
    private javax.swing.JSpinner spinner_a;
    private javax.swing.JSpinner spinner_b;
    private javax.swing.JSpinner spinner_g;
    private javax.swing.JSpinner spinner_h;
    private javax.swing.JSpinner spinner_r;
    private javax.swing.JSpinner spinner_s;
    private javax.swing.JSpinner spinner_v;
    // End of variables declaration//GEN-END:variables

}
