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
 * ColorChooserPanel.java
 *
 * Created on Sep 19, 2009, 12:08:09 AM
 */

package com.kitfox.game.control.color;

import com.kitfox.raven.util.PropertyChangeWeakListener;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 *
 * @author kitfox
 */
public class ColorChooserPanel extends ColorFieldPanel
        implements PropertyChangeListener
{
    private static final long serialVersionUID = 0;

    /**
     * @return the colorCursor
     */
    public Cursor getColorCursor() {
        return colorCursor;
    }

    /**
     * @param colorCursor the colorCursor to set
     */
    public void setColorCursor(Cursor colorCursor) {
        this.colorCursor = colorCursor;
    }

    public interface Cursor
    {
        public void paint(Graphics2D g, Color color);
    }

    public static class CircleCursor implements Cursor
    {
        static final Ellipse2D.Float cursor = new Ellipse2D.Float(-5, -5, 10, 10);

        float[] rgba = new float[4];

        @Override
        public void paint(Graphics2D g, Color color)
        {
            (color == null ? Color.BLACK : color).getColorComponents(rgba);
            float lum = rgba[0] * 0.2126f + rgba[1] * 0.7152f + rgba[2] * 0.0722f;

            g.setColor(lum > .5f ? Color.BLACK : Color.WHITE);
            g.draw(cursor);
        }
    }

    private ColorChooserModel model;

    PropertyChangeWeakListener propListener;
    float cursorX;
    float cursorY;
    boolean dragging = false;

    private Cursor colorCursor = new CircleCursor();

    static final String ACTION_LEFT = "left";
    static final String ACTION_RIGHT = "right";
    static final String ACTION_UP = "up";
    static final String ACTION_DOWN = "down";

    /** Creates new form ColorChooserPanel */
    public ColorChooserPanel()
    {
        initComponents();

        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), ACTION_LEFT);
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), ACTION_RIGHT);
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), ACTION_UP);
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), ACTION_DOWN);
        getActionMap().put(ACTION_LEFT,
                new AbstractAction()
                {
                    private static final long serialVersionUID = 0;
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        moveLeft();
                    }
                }
        );
        getActionMap().put(ACTION_RIGHT,
                new AbstractAction()
                {
                    private static final long serialVersionUID = 0;
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        moveRight();
                    }
                }
        );
        getActionMap().put(ACTION_UP,
                new AbstractAction()
                {
                    private static final long serialVersionUID = 0;
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        moveUp();
                    }
                }
        );
        getActionMap().put(ACTION_DOWN,
                new AbstractAction()
                {
                    private static final long serialVersionUID = 0;
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        moveDown();
                    }
                }
        );
    }

    @Override
    public void paintComponent(Graphics gg)
    {
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D)gg;

        int x = (int)(cursorX * getWidth());
        int y = (int)(cursorY * getHeight());
        g.translate(x, y);
        if (getColorCursor() != null)
        {
            getColorCursor().paint(g, model.getColor().getColor());
        }
        g.translate(-x, -y);
    }

    public void updateColor()
    {
        ColorStyle color = model == null ? new ColorStyle() : model.getColor();
        if (field != null)
        {
            Point2D.Float coords = field.toCoords(color);
            cursorX = coords.x;
            cursorY = coords.y;
        }

        repaint();
    }

    private float sat(float value)
    {
        return value < 0 ? 0 : (value > 1 ? 1 : value);
    }

    private void choosePoint(MouseEvent evt)
    {
        if (field == null)
        {
            return;
        }

        float x = sat((float)evt.getX() / getWidth());
        float y = sat((float)evt.getY() / getHeight());
        ColorStyle color = field.toColor(x, y);
        if (model != null)
        {
            model.setColor(color);
        }
    }

    private void moveUp()
    {
        cursorY -= 1f / getHeight();
        if (cursorY < 0)
        {
            cursorY += 1;
        }
        ColorStyle color = field.toColor(cursorX, cursorY);
        if (model != null)
        {
            model.setColor(color);
        }
    }

    private void moveDown()
    {
        cursorY += 1f / getHeight();
        if (cursorY >= 1)
        {
            cursorY -= 1;
        }
        ColorStyle color = field.toColor(cursorX, cursorY);
        if (model != null)
        {
            model.setColor(color);
        }
    }

    private void moveRight()
    {
        cursorX += 1f / getWidth();
        if (cursorX >= 1)
        {
            cursorX -= 1;
        }
        ColorStyle color = field.toColor(cursorX, cursorY);
        if (model != null)
        {
            model.setColor(color);
        }
    }

    private void moveLeft()
    {
        cursorX -= 1f / getWidth();
        if (cursorX < 0)
        {
            cursorX += 1;
        }
        ColorStyle color = field.toColor(cursorX, cursorY);
        if (model != null)
        {
            model.setColor(color);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
    }// </editor-fold>//GEN-END:initComponents

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        cursorX = (float)evt.getX() / getWidth();
        cursorY = (float)evt.getY() / getHeight();
        dragging  = true;
        repaint();

    }//GEN-LAST:event_formMousePressed

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        cursorX = (float)evt.getX() / getWidth();
        cursorY = (float)evt.getY() / getHeight();
        choosePoint(evt);
        repaint();

    }//GEN-LAST:event_formMouseDragged

    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
        choosePoint(evt);
        dragging  = false;

    }//GEN-LAST:event_formMouseReleased

    /**
     * @return the model
     */
    public ColorChooserModel getModel()
    {
        return model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(ColorChooserModel model)
    {
        if (propListener != null)
        {
            propListener.remove();
            propListener = null;
        }
        this.model = model;
        if (model != null)
        {
            propListener = new PropertyChangeWeakListener(this, model);
            model.addPropertyChangeListener(propListener);
        }
        updateColor();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (!dragging)
        {
            updateColor();
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
