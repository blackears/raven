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

package com.kitfox.raven.paint.control;

import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.raven.paint.RavenPaint;
import com.kitfox.raven.paint.RavenPaintIndex;
import com.kitfox.raven.paint.RavenPaintProvider;
import com.kitfox.raven.paint.common.RavenPaintColor;
import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import javax.swing.*;

/**
 *
 * @author kitfox
 */
public class PaintEditorPanel extends javax.swing.JPanel
{
    HashMap<RavenPaintProvider, ProviderInfo> provMap
            = new HashMap<RavenPaintProvider, ProviderInfo>();

    private RavenPaint paint = new RavenPaintColor(CyColor4f.BLACK);
    public static final String PROP_PAINT = "paint";
    
    boolean updating = true;
    
    /**
     * Creates new form PaintEditorPanel
     */
    public PaintEditorPanel()
    {
        initComponents();
        
        combo_paintType.setRenderer(new CellRenderer());
        
        for (RavenPaintProvider prov: RavenPaintIndex.inst().getServices())
        {
            combo_paintType.addItem(prov);
            provMap.put(prov, new ProviderInfo(prov));
        }
        
        updatePaint();
    }

    private void updatePaint()
    {
        updating = true;
        
        if (paint == null)
        {
            updating = false;
            return;
        }
        
        RavenPaintProvider prov =
                RavenPaintIndex.inst().getByPaint(paint.getClass());
        combo_paintType.setSelectedItem(prov);
        
        panel_control.removeAll();
        ProviderInfo info = provMap.get(prov);
        panel_control.add(info.component, BorderLayout.CENTER);
        info.control.setPaint(paint);
        revalidate();
        repaint();
        
        updating = false;
    }
    
    /**
     * @return the paint
     */
    public RavenPaint getPaint()
    {
        return paint;
    }

    /**
     * @param paint the paint to set
     */
    public void setPaint(RavenPaint paint)
    {
        RavenPaint oldPaint = this.paint;
        this.paint = paint;
        updatePaint();
        firePropertyChange(PROP_PAINT, oldPaint, paint);
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

        combo_paintType = new javax.swing.JComboBox();
        panel_control = new javax.swing.JPanel();

        combo_paintType.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                combo_paintTypeActionPerformed(evt);
            }
        });

        panel_control.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel_control, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(combo_paintType, 0, 342, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(combo_paintType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_control, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void combo_paintTypeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_combo_paintTypeActionPerformed
    {//GEN-HEADEREND:event_combo_paintTypeActionPerformed
        if (updating)
        {
            return;
        }
        
        RavenPaintProvider prov = 
                (RavenPaintProvider)combo_paintType.getSelectedItem();
        ProviderInfo info = provMap.get(prov);
        
        panel_control.removeAll();
        panel_control.add(info.component, BorderLayout.CENTER);

        RavenPaint oldPaint = paint;
        paint = info.control.getPaint();
        firePropertyChange(PROP_PAINT, oldPaint, paint);
        
        revalidate();
        repaint();

    }//GEN-LAST:event_combo_paintTypeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox combo_paintType;
    private javax.swing.JPanel panel_control;
    // End of variables declaration//GEN-END:variables

    //---------------------------------------
    
    class ProviderInfo
        implements PropertyChangeListener
    {
        final RavenPaintProvider prov;
        RavenPaintControl control;
        Component component;

        public ProviderInfo(RavenPaintProvider prov)
        {
            this.prov = prov;
            control = prov.createEditor();
            component = control.getComponent();
            control.addPropertyChangeListener(this);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt)
        {
            String name = evt.getPropertyName();
            if (control.getPaintPropertyName().equals(name))
            {
                RavenPaint oldPaint = paint;
                paint = control.getPaint();
                
                firePropertyChange(PROP_PAINT, oldPaint, paint);
            }
        }

    }
    
    class CellRenderer extends JLabel
            implements ListCellRenderer
    {
        public CellRenderer()
        {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
        {
            //UIDefaults uid = UIManager.getLookAndFeel().getDefaults();

            if (isSelected)
            {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else
            {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            RavenPaintProvider font = (RavenPaintProvider)value;
            setText(font.getName());

            return this;
        }
    }
}