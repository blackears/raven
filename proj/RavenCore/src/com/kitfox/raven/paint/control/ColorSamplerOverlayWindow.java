/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.raven.paint.control;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class ColorSamplerOverlayWindow extends javax.swing.JWindow
{
    ArrayList<ColorSamplerOverlayListener> listeners = 
            new ArrayList<ColorSamplerOverlayListener>();

    /**
     * Creates new form ColorSamplerOverlayPanel
     */
    public ColorSamplerOverlayWindow(Window win)
    {
        super(win);
        
        initComponents();

        fillScreen();
    }

    public void addColorSamplerOverlayListener(ColorSamplerOverlayListener l)
    {
        listeners.add(l);
    }

    public void removeColorSamplerOverlayListener(ColorSamplerOverlayListener l)
    {
        listeners.remove(l);
    }
    
    protected void fireColorPicked(Color col)
    {
        ColorSamplerOverlayEvent evt = new ColorSamplerOverlayEvent(this, col);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).colorPicked(evt);
        }
    }
    
    public void fillScreen()
    {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0, size.width, size.height);
    }

    @Override
    public void paint(Graphics g)
    {
        //Do nothing
//        g.setColor(Color.RED);
//        g.fillOval(0, 0, getWidth(), getHeight());
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMouseClicked
    {//GEN-HEADEREND:event_formMouseClicked
        try
        {
            Robot robot = new Robot();
            Color col = robot.getPixelColor(evt.getXOnScreen(), evt.getYOnScreen());
            fireColorPicked(col);
        } catch (AWTException ex)
        {
            Logger.getLogger(ColorSamplerOverlayWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }//GEN-LAST:event_formMouseClicked

    private void formKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_formKeyPressed
    {//GEN-HEADEREND:event_formKeyPressed
        switch (evt.getKeyCode())
        {
            case KeyEvent.VK_ESCAPE:
                fireColorPicked(null);
                break;
        }
    }//GEN-LAST:event_formKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
