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
 * ColorStylePanel.java
 *
 * Created on Sep 17, 2009, 4:40:13 PM
 */

package com.kitfox.raven.paint.common;

import com.kitfox.raven.paint.control.ColorEditorPanel;
import com.kitfox.raven.util.PropertyChangeWeakListener;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author kitfox
 */
public class RavenPaintColorCustomEditor extends javax.swing.JPanel implements PropertyChangeListener
{
    private static final long serialVersionUID = 1;

    final RavenPaintColorEditor ed;
    PropertyChangeWeakListener listener;

    ColorEditorPanel colorPanel = new ColorEditorPanel();

    /** Creates new form ColorStylePanel */
    public RavenPaintColorCustomEditor(RavenPaintColorEditor ed)
    {
        initComponents();

        this.ed = ed;
        listener = new PropertyChangeWeakListener(this, ed);
        ed.addPropertyChangeListener(listener);

        add(colorPanel, BorderLayout.CENTER);

        colorPanel.addPropertyChangeListener(this);

        update();
    }

    private void update()
    {
        RavenPaintColor value = ed.getValue();
        if  (value == null)
        {
            value = RavenPaintColor.BLACK;
        }
        colorPanel.setColor(value);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (evt.getSource() == colorPanel 
                && ColorEditorPanel.PROP_COLOR.equals(evt.getPropertyName()))
        {
            RavenPaintColor col = colorPanel.getColor();
            ed.setValue(col);
        }
        else if (evt.getSource() == ed)
        {
            update();
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}