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
 * StopSliderPanel.java
 *
 * Created on Sep 19, 2009, 1:18:27 AM
 */

package com.kitfox.game.control.color;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author kitfox
 */
public class StopSliderPanel<StopType> extends javax.swing.JPanel
        implements StopModelListener
{
    private static final long serialVersionUID = 0;

    private ColorField field;
    int buttonWidth = 10;
    int buttonHeight = 10;
    private int margin = 5;

    private StopSide side = StopSide.EAST;

    private StopModel<StopType> stopModel;
    static final String ACTION_DELETE = "delete";

    boolean dragging = false;
    MouseEvent mouseStart;
    StopType dragStop;

    ArrayList<StopType> selection = new ArrayList<StopType>();
    StopModelWeakListener listener;


    /** Creates new form StopSliderPanel */
    public StopSliderPanel()
    {
        initComponents();

        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), ACTION_DELETE);
        getActionMap().put(ACTION_DELETE,
                new AbstractAction()
                {
                    private static final long serialVersionUID = 0;
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        deleteStops();
                    }
                }
        );
    }

    @Override
    public void paintComponent(Graphics g)
    {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        if (stopModel == null)
        {
            return;
        }
        for (StopType stop: stopModel.getStopObjects())
        {
            paintButton(g, stop);
        }
    }

    public void paintButton(Graphics g, StopType stop)
    {
        Rectangle rect = new Rectangle();
        float value = stopModel.getStopValue(stop);
        ColorStyle color = field.toColor(value, value);

        getStopBounds(rect, value);

        g.setColor(Color.BLACK);

        switch (side)
        {
            case NORTH:
            {
                int cx = rect.x + rect.width / 2;
                g.drawLine(cx, buttonHeight, cx, getHeight());
                break;
            }
            case SOUTH:
            {
                int cx = rect.x + rect.width / 2;
                g.drawLine(cx, 0, cx, getHeight() - buttonHeight);
                break;
            }
            case WEST:
            {
                int cy = rect.y + rect.height / 2;
                g.drawLine(buttonWidth, cy, getWidth(), cy);
                break;
            }
            case EAST:
            {
                int cy = rect.y + rect.height / 2;
                g.drawLine(0, cy, getWidth() - buttonWidth, cy);
                break;
            }
        }

        g.setColor(color.getColor());
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
        g.setColor(Color.GRAY);
        g.draw3DRect(rect.x, rect.y, rect.width, rect.height, !selection.contains(stop));
    }


    private void getStopBounds(Rectangle rect, float value)
    {
        switch (side)
        {
            case NORTH:
            {
                int cx = getMargin() + (int)(value * (getWidth() - 2 * getMargin()));
                rect.setBounds(cx - buttonWidth / 2,
                        0,
                        buttonWidth,
                        buttonHeight);
                break;
            }
            case SOUTH:
            {
                int cx = getMargin() + (int)(value * (getWidth() - 2 * getMargin()));
                rect.setBounds(cx - buttonWidth / 2,
                        getHeight() - buttonHeight - 1,
                        buttonWidth,
                        buttonHeight);
                break;
            }
            case WEST:
            {
                int cy = getMargin() + (int)(value * (getHeight() - 2 * getMargin()));
                rect.setBounds(0,
                        cy - buttonHeight / 2,
                        buttonWidth,
                        buttonHeight);
                break;
            }
            case EAST:
            {
                int cy = getMargin() + (int)(value * (getHeight() - 2 * getMargin()));
                rect.setBounds(getWidth() - buttonWidth - 1,
                        cy - buttonHeight / 2,
                        buttonWidth,
                        buttonHeight);
                break;
            }
        }
    }

    private StopType pickStop(int x, int y)
    {
        Rectangle rect = new Rectangle();

        List<StopType> stops = stopModel.getStopObjects();
        for (int i = stops.size() - 1; i >= 0; --i)
        {
            StopType stop = stops.get(i);
            float value = stopModel.getStopValue(stop);
            getStopBounds(rect, value);

            if (rect.contains(x, y))
            {
                return stop;
            }
        }

        return null;
    }

    public void deleteStops()
    {
        for (StopType stop: selection)
        {
            stopModel.removeStop(stop);
        }
    }

    private float getPositionValue(MouseEvent evt)
    {
        float value;
        
        switch (side)
        {
            case NORTH:
            case SOUTH:
                value = (float)(evt.getX() - getMargin()) / (getWidth() - 2 * getMargin());
                break;
            case EAST:
            case WEST:
                value = (float)(evt.getY() - getMargin()) / (getHeight() - 2 * getMargin());
                break;
            default:
                throw new RuntimeException();
        }

        return Math.max(Math.min(value, 1), 0);
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
        mouseStart = evt;
        stopModel.beginStopEdits();
        requestFocus();
    }//GEN-LAST:event_formMousePressed

    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
        if (dragging)
        {
            //End drag
            mouseStart = null;
            dragging = false;
            dragStop = null;
            stopModel.endStopEdits();
            return;
        }

        //Handle click
        StopType curStop = pickStop(evt.getX(), evt.getY());
        if (curStop == null)
        {
            if (evt.getClickCount() >= 2)
            {
                float value = getPositionValue(evt);
                stopModel.addStop(value);
            }
            else
            {
                selection.clear();
            }
        }
        else
        {
            if (evt.getClickCount() >= 2)
            {
                stopModel.editStop(curStop);
            }
            else
            {
                selection.clear();
                if (curStop != null)
                {
                    selection.add(curStop);
                }
            }
        }

        mouseStart = null;
        repaint();
    }//GEN-LAST:event_formMouseReleased

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
//        if (dragStop == null)
//        {
//            return;
//        }

        if (!dragging)
        {
            int dx = evt.getX() - mouseStart.getX();
            int dy = evt.getY() - mouseStart.getY();

            if (dx * dx + dy * dy < 25)
            {
                return;
            }
            dragStop = pickStop(mouseStart.getX(), mouseStart.getY());
            if (dragStop == null)
            {
                return;
            }
            dragging = true;
        }

        float value = getPositionValue(evt);
        stopModel.setStopValue(dragStop, value);

    }//GEN-LAST:event_formMouseDragged

    /**
     * @return the field
     */
    public ColorField getField()
    {
        return field;
    }

    /**
     * @param field the field to set
     */
    public void setField(ColorField field)
    {
        this.field = field;
    }

    /**
     * @return the side
     */
    public StopSide getSide()
    {
        return side;
    }

    /**
     * @param side the side to set
     */
    public void setSide(StopSide side)
    {
        this.side = side;
    }
    /**
     * @return the model
     */
    public StopModel<StopType> getStopModel()
    {
        return stopModel;
    }

    /**
     * @param model the model to set
     */
    public void setStopModel(StopModel<StopType> model)
    {
        if (listener != null)
        {
            listener.remove();
            listener = null;
        }
        this.stopModel = model;
        if (model != null)
        {
            listener = new StopModelWeakListener(this, model);
            model.addStopModelListener(listener);
        }
    }

    public void stopModelChanged(ChangeEvent evt)
    {
        repaint();
    }

    public void beginStopEdits(ChangeEvent evt)
    {
    }

    public void endStopEdits(ChangeEvent evt)
    {
    }

    /**
     * @return the margin
     */
    public int getMargin()
    {
        return margin;
    }

    /**
     * @param margin the margin to set
     */
    public void setMargin(int margin)
    {
        this.margin = margin;
    }



    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
