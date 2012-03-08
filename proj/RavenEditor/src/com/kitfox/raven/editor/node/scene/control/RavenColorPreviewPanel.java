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
 * RavenStrokePreviewPanel.java
 *
 * Created on Jan 19, 2011, 10:39:32 AM
 */

package com.kitfox.raven.editor.node.scene.control;

import com.kitfox.raven.paint.common.RavenPaintColor;
import com.kitfox.raven.paint.control.UnderlayPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

/**
 *
 * @author kitfox
 */
public class RavenColorPreviewPanel extends javax.swing.JPanel
{
    private RavenPaintColor paint;

    /** Creates new form RavenStrokePreviewPanel */
    public RavenColorPreviewPanel()
    {
        initComponents();
    }

    @Override
    protected void paintComponent(Graphics gg)
    {
        Graphics2D g = (Graphics2D)gg;

        g.setPaint(UnderlayPaint.inst().getPaint());
        g.fillRect(0, 0, getWidth(), getHeight());

        if (paint == null)
        {
            return;
        }

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Rectangle bounds = getBounds();
        bounds.x = bounds.y = 0;
        g.setPaint(paint.getPaintSwatch(bounds));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        setMinimumSize(new java.awt.Dimension(24, 24));
        setPreferredSize(new java.awt.Dimension(24, 24));
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @return the paint
     */
    public RavenPaintColor getPaint()
    {
        return paint;
    }

    /**
     * @param paint the paint to set
     */
    public void setColor(RavenPaintColor paint)
    {
        this.paint = paint;
        repaint();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
