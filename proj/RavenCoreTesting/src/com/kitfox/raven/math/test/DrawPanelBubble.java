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
 * DrawPanelBubble.java
 *
 * Created on Jan 4, 2011, 4:51:10 AM
 */

package com.kitfox.raven.math.test;

import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.raven.shape.builders.BitmapOutliner;
import com.kitfox.raven.shape.builders.StrokeBuffer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import jpen.PButtonEvent;
import jpen.PKindEvent;
import jpen.PLevel;
import jpen.PLevelEvent;
import jpen.PScrollEvent;
import jpen.Pen;
import jpen.PenManager;
import jpen.event.PenListener;
import jpen.owner.multiAwt.AwtPenToolkit;

/**
 *
 * @author kitfox
 */
public class DrawPanelBubble extends javax.swing.JPanel
        implements PenListener
{
//    BubbleOutliner bubbleOutliner;
    StrokeBuffer bubbleOutliner;
//    final PenManager penManager;
    static final int strokeBufferSize = 64;

    ArrayList<Path2D.Double> history = new ArrayList<Path2D.Double>();

    private float penX;
    private float penY;
    private float penPressure;

    private float penNextX;
    private float penNextY;
    private float penNextPressure;

    boolean penDown;
    boolean readingPen = false;
    private static float spacing = .2f;
    private static float penWeight = 10f;


    /** Creates new form DrawPanelBubble */
    public DrawPanelBubble()
    {
        initComponents();
        PenManager penManager = AwtPenToolkit.getPenManager();
        penManager.pen.setFirePenTockOnSwing(true);
        penManager.pen.setFrequencyLater(40);
        penManager.pen.levelEmulator.setPressureTriggerForLeftCursorButton(.5f);
        AwtPenToolkit.addPenListener(this, this);
        
//        penManager = new PenManager(this);
//        penManager.pen.addListener(this);

//        penManager.pen.setFirePenTockOnSwing(true);
//        penManager.pen.levelEmulator.setPressureTriggerForLeftCursorButton(.1f);
        
//        penNextX = penManager.pen.getLevelValue(PLevel.Type.X);
//        penNextY = penManager.pen.getLevelValue(PLevel.Type.Y);
//        penNextPressure = penManager.pen.getLevelValue(PLevel.Type.PRESSURE);
    }

    @Override
    protected void paintComponent(Graphics gg)
    {
        Graphics2D g = (Graphics2D)gg;

        //Clear
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());

        //Draw curves
        g.setColor(Color.black);
        for (Path2D.Double path: history)
        {
            g.draw(path);
//            g.fill(path);
        }

        g.setColor(Color.blue);
        if (bubbleOutliner != null)
        {
            bubbleOutliner.render(g);
        }
    }

    public void clear()
    {
        history.clear();
        repaint();
    }

    private void startReadingFromPen()
    {
        if (readingPen)
        {
            return;
        }

        //Restart drawing, using pen now
        PenManager penManager = AwtPenToolkit.getPenManager();
        Pen pen = penManager.pen;
        penNextX = penX = pen.getLevelValue(PLevel.Type.X);
        penNextY = penY = pen.getLevelValue(PLevel.Type.Y);
        penNextPressure = penPressure = pen.getLevelValue(PLevel.Type.PRESSURE);
//System.err.println("Start read " + penNextX + " " + penNextY + " " + penNextPressure);

//        bubbleOutliner = new BubbleOutliner(16, 16);
        bubbleOutliner = new StrokeBuffer(strokeBufferSize, strokeBufferSize,
                getGraphicsConfiguration());
        readingPen = true;
//System.err.println("start readPen " + readingPen);
    }

    private void samplePen(MouseEvent evt)
    {
//        boolean mouseBnDown =
//                (evt.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0;

//        System.err.println(evt);
        
        if (!readingPen)
        {
            penNextX = evt.getX();
            penNextY = evt.getY();
            penNextPressure = 1;
            return;
        }

        PenManager penManager = AwtPenToolkit.getPenManager();
        Pen pen = penManager.pen;
//        pen.

        penNextX = pen.getLevelValue(PLevel.Type.X);
        penNextY = pen.getLevelValue(PLevel.Type.Y);
        penNextPressure = pen.getLevelValue(PLevel.Type.PRESSURE);
//System.err.println("Sample " + penNextX + " " + penNextY + " " + penNextPressure);

    }

    private void penDown(MouseEvent evt)
    {
        penNextX = penX = evt.getX();
        penNextY = penY = evt.getY();
        penNextPressure = penPressure = 0;

        samplePen(evt);

        bubbleOutliner = new StrokeBuffer(strokeBufferSize, strokeBufferSize,
                getGraphicsConfiguration());
        penDown = true;
        readingPen = false;
//System.err.println("pd readPen " + readingPen);
    }

    private void penUp(MouseEvent evt)
    {
        if (bubbleOutliner == null)
        {
            return;
        }

        strokeSegment(evt);

        if (!bubbleOutliner.isEmpty())
        {
            BitmapOutliner outliner = bubbleOutliner.buildOutliner();
//            final Path2D.Double path = outliner.createPath();
            
            CyPath2d cyPath = outliner.getPath(5);
//            CyRectangle2d region = outliner.getRegion();
//            CyMatrix4d m = CyMatrix4d.createIdentity();
//            m.translate(region.getX(), region.getY(), 0);
//            cyPath = cyPath.createTransformedPath(m);
System.err.println("---SVG path");
System.err.println(cyPath.toString());
            
            final Path2D.Double path = cyPath.asPathAWT();

            if (path != null)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        history.add(path);
                        repaint();
                    }
                });
            }
        }

        bubbleOutliner = null;
        penDown = false;
    }

    private void penMoved(MouseEvent evt)
    {
        strokeSegment(evt);
    }

    private void strokeSegment(MouseEvent evt)
    {
        if (!penDown)
        {
            return;
        }

        samplePen(evt);

        //Only record if minimum distance traveled
        if (Math2DUtil.square(penX - penNextX) +
                + Math2DUtil.square(penY - penNextY) < 4)
        {
            return;
        }

        float gap = Math.max(spacing * penPressure * penWeight, 1);
        double dist = Math2DUtil.dist(penX, penY, penNextX, penNextY);
        int numDots = (int)Math.ceil(dist / gap);

//if (penPressure == 1)
//{
//    int j = 9;
//}

        for (int i = 0; i < numDots; ++i)
        {
            double dt = (double)i / numDots;
            double x = Math2DUtil.lerp(penX, penNextX, dt);
            double y = Math2DUtil.lerp(penY, penNextY, dt);
            double p = Math2DUtil.lerp(penPressure, penNextPressure, dt) * penWeight;
//System.err.println("Pressure " + p);
            bubbleOutliner.addCircle(x, y, p);

        }

        penX = penNextX;
        penY = penNextY;
        penPressure = penNextPressure;


        repaint();
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
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    private void formMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMousePressed
    {//GEN-HEADEREND:event_formMousePressed
//        System.err.println("***Mouse pressed");
        penDown(evt);
    }//GEN-LAST:event_formMousePressed

    private void formMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMouseReleased
    {//GEN-HEADEREND:event_formMouseReleased
//        System.err.println("***Mouse released");
        penUp(evt);
    }//GEN-LAST:event_formMouseReleased

    private void formMouseDragged(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMouseDragged
    {//GEN-HEADEREND:event_formMouseDragged
        penMoved(evt);
    }

    @Override
    public void penKindEvent(PKindEvent pke)
    {
//        System.err.println(pke);
    }

    @Override
    public void penLevelEvent(PLevelEvent ple)
    {
        PenManager penManager = AwtPenToolkit.getPenManager();
        float pressure =
                penManager.pen.getLevelValue(PLevel.Type.PRESSURE);
//System.err.println("Pres " + pressure);
        if (pressure > 0)
        {
            startReadingFromPen();
        }
        
//        System.err.println(ple);
        PLevel[] levels = ple.levels;
        for (int i = 0; i < levels.length; ++i)
        {
//System.err.println("Pen event " + i + " " + levels[i].getType() + " " + levels[i].value);
//if (levels[i].getType() == PLevel.Type.PRESSURE)
//{
//    int j = 9;
//}
            
//            if (penDown)
//            {
//System.err.println("Pen event " + i + " " + levels[i].getType() + " " + levels[i].value);
//                if (levels[i].getType() == PLevel.Type.PRESSURE)
//                {
//                    startReadingFromPen();
//                }
//            }
        }
    }

    @Override
    public void penButtonEvent(PButtonEvent pbe)
    {
//        System.err.println(pbe);
    }

    @Override
    public void penScrollEvent(PScrollEvent pse)
    {
//        System.err.println(pse);
    }

    @Override
    public void penTock(long l)
    {

    }//GEN-LAST:event_formMouseDragged



    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
