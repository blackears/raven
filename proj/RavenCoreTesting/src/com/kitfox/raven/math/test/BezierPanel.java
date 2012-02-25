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
 * BezierPanel.java
 *
 * Created on Dec 17, 2010, 2:25:55 AM
 */

package com.kitfox.raven.math.test;

import com.kitfox.coyote.math.GMatrix;
import com.kitfox.coyote.math.bezier.FitCurve;
import com.kitfox.raven.shape.bezier.BSpline;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class BezierPanel extends javax.swing.JPanel
{
    State state = State.NONE;

    ArrayList<Point> points = new ArrayList<Point>();
    Point dragPoint;

    static final int POINT_RADIUS = 3;
    MouseEvent curMouse;

    Path2D.Double interpCurve;
    Path2D.Double splineCurve;

    /** Creates new form BezierPanel */
    public BezierPanel()
    {
        initComponents();
    }

    @Override
    protected void paintComponent(Graphics gg)
    {
        Graphics2D g = (Graphics2D)gg;

        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());

        for (int i = 0; i < points.size(); ++i)
        {
            Point pt = points.get(i);
            g.setColor(Color.red);
            g.drawOval(pt.x - POINT_RADIUS, pt.y - POINT_RADIUS, POINT_RADIUS * 2, POINT_RADIUS * 2);

            g.setColor(Color.magenta);
            if (i != points.size() - 1)
            {
                Point pt1 = points.get(i + 1);
                g.drawLine(pt.x, pt.y, pt1.x, pt1.y);
            }
            else if (state == State.DRAW_DATA && curMouse != null)
            {
                g.drawLine(pt.x, pt.y, curMouse.getX(), curMouse.getY());
            }
        }

        if (interpCurve != null)
        {
            g.setColor(Color.blue);
            g.draw(interpCurve);
        }

        g.setColor(Color.RED);
        plotSpline(g);

        g.setColor(Color.GREEN);
        plotFitSpline(g);

        if (splineCurve != null)
        {
            g.setColor(Color.BLACK);
            g.draw(splineCurve);
        }
    }

    public void drawDataPoints()
    {
        state = state.DRAW_DATA;
        curMouse = null;
        points.clear();
        repaint();
    }

    public void plotSpline(Graphics2D g)
    {
        final int order = 4;
        final int numSamples = 100;
        final int PLOT_POINT_HALF = 2;
        int maxPointIdx = points.size() - 1;
        if (order - 1 > maxPointIdx)
        {
            return;
        }

        double[] knots = fitOpen
                ? FitCurve.createOpenUniformKnots(maxPointIdx, order)
                : FitCurve.createUniformKnots(maxPointIdx, order);

        Point2D.Double sum = new Point2D.Double();
        Rectangle2D.Double plotPt = new Rectangle2D.Double(0, 0, PLOT_POINT_HALF * 2, PLOT_POINT_HALF * 2);
        double spanStart = knots[fitDegree];
        double spanEnd = knots[knots.length - fitDegree - 1];
        for (int samp = 0; samp <= numSamples; ++samp)
        {
//            double t = (double)samp / numSamples;
            double t = spanStart + (spanEnd - spanStart) * samp / numSamples;

            sum.setLocation(0, 0);
            for (int i = 0; i <= maxPointIdx; ++i)
            {
                double c = FitCurve.splineCoeff(maxPointIdx, i, order - 1, knots, t);
                Point p = points.get(i);
                sum.x += c * p.x;
                sum.y += c * p.y;
            }

            plotPt.x = sum.x - PLOT_POINT_HALF;
            plotPt.y = sum.y - PLOT_POINT_HALF;
            g.draw(plotPt);
        }
        
    }

    public void fitBezier()
    {
        interpCurve = null;
        
        if (points.size() < 4)
        {
            return;
        }

        GMatrix P = new GMatrix(points.size(), 2);
        for (int i = 0; i < points.size(); ++i)
        {
            Point p = points.get(i);
            P.setElement(i, 0, p.x);
            P.setElement(i, 1, p.y);
        }

        double[] Ptimes = new double[points.size()];
        Ptimes[0] = 0;
        double dist = 0;
        for (int i = 1; i < points.size(); ++i)
        {
            Point pt0 = points.get(i - 1);
            Point pt1 = points.get(i);
            double dx = pt1.x - pt0.x;
            double dy = pt1.y - pt0.y;
            dist += Math.sqrt(dx * dx + dy * dy);
            Ptimes[i] = dist;
        }
        for (int i = 0; i < points.size(); ++i)
        {
            Ptimes[i] /= dist;
        }

        if (false)
        {
            //Least squares bezier fit
            GMatrix Q = FitCurve.fitBezier(3, Ptimes, P);
            interpCurve = new Path2D.Double();
            interpCurve.moveTo(Q.getElement(0, 0), Q.getElement(0, 1));
            interpCurve.curveTo(
                    Q.getElement(1, 0), Q.getElement(1, 1),
                    Q.getElement(2, 0), Q.getElement(2, 1),
                    Q.getElement(3, 0), Q.getElement(3, 1)
                    );
        }

        if (true)
        {
            //Least squares knots-only bezier fit
            GMatrix Q = FitCurve.fitBezierKnots(3, Ptimes, P);
            interpCurve = new Path2D.Double();
            interpCurve.moveTo(P.getElement(0, 0), P.getElement(0, 1));
            interpCurve.curveTo(
                    Q.getElement(0, 0), Q.getElement(0, 1),
                    Q.getElement(1, 0), Q.getElement(1, 1),
                    P.getElement(points.size() - 1, 0), P.getElement(points.size() - 1, 1)
                    );
        }

//        System.err.println(Q);
    }

    GMatrix splineFitPoints;
    double[] splineFitKnots;
    private int fitDegree = 3;
    private boolean fitOpen = true;
    private int fitNumPoints = 6;

    public void fitSpline()
    {
        splineFitPoints = null;
        splineFitKnots = null;

        if (fitNumPoints < fitDegree + 1
                || points.size() < fitNumPoints)
        {
            return;
        }

        GMatrix P = new GMatrix(points.size(), 2);
        double[] Ptimes = new double[points.size()];
        for (int i = 0; i < points.size(); ++i)
        {
            Point p = points.get(i);
            P.setElement(i, 0, p.x);
            P.setElement(i, 1, p.y);

            Ptimes[i] = (double)i / (Ptimes.length - 1);
        }

//if (!fitOpen)
//{
//    int j = 9;
//}

        splineFitKnots = fitOpen
                ? FitCurve.createOpenUniformKnots(fitNumPoints - 1, fitDegree + 1)
                : FitCurve.createUniformKnots(fitNumPoints - 1, fitDegree + 1);

        splineFitPoints = FitCurve.fitBSpline(fitDegree, Ptimes, P, fitNumPoints, splineFitKnots);

        //Find bezier form
        double[][] pointArr = new double[splineFitPoints.getNumRow()][];
        for (int i = 0; i < pointArr.length; ++i)
        {
            double[] pt = new double[splineFitPoints.getNumCol()];
            pointArr[i] = pt;
            for (int j = 0; j < pt.length; ++j)
            {
                pt[j] = splineFitPoints.getElement(i, j);
            }
        }

        BSpline spline = new BSpline(splineFitKnots, pointArr);
        spline.splitIntoBeziers();
        splineCurve = new Path2D.Double();
        splineCurve.moveTo(spline.getPoint(0, 0), spline.getPoint(0, 1));
        for (int i = 1; i < spline.getNumPoints(); i += 3)
        {
            splineCurve.curveTo(spline.getPoint(i, 0), spline.getPoint(i, 1),
                    spline.getPoint(i + 1, 0), spline.getPoint(i + 1, 1),
                    spline.getPoint(i + 2, 0), spline.getPoint(i + 2, 1));
        }
    }

    public void plotFitSpline(Graphics2D g)
    {
        if (splineFitPoints == null || splineFitKnots == null)
        {
            return;
        }
        
//        final int order = 4;
        final int numSamples = 100;
        final int PLOT_POINT_HALF = 3;
        int maxPointIdx = splineFitPoints.getNumRow() - 1;
//        if (order - 1 > maxPointIdx)
//        {
//            return;
//        }

//        double[] knots = FitCurve.createOpenUniformKnots(maxPointIdx, order);

        Point2D.Double sum = new Point2D.Double();
        Rectangle2D.Double plotPt = new Rectangle2D.Double(0, 0, PLOT_POINT_HALF * 2, PLOT_POINT_HALF * 2);
        //Only interpolate over span of knots that are supported by spline
        double spanStart = splineFitKnots[fitDegree];
        double spanEnd = splineFitKnots[splineFitKnots.length - fitDegree - 1];
        for (int samp = 0; samp <= numSamples; ++samp)
        {
            //double t = (double)samp / numSamples;
            double t = spanStart + (spanEnd - spanStart) * samp / numSamples;

            sum.setLocation(0, 0);
            for (int i = 0; i <= maxPointIdx; ++i)
            {
                double c = FitCurve.splineCoeff(maxPointIdx, i, fitDegree, splineFitKnots, t);
                Point p = points.get(i);
                sum.x += c * splineFitPoints.getElement(i, 0);
                sum.y += c * splineFitPoints.getElement(i, 1);
            }

            plotPt.x = sum.x - PLOT_POINT_HALF;
            plotPt.y = sum.y - PLOT_POINT_HALF;
            g.draw(plotPt);
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
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMousePressed
    {//GEN-HEADEREND:event_formMousePressed
        Rectangle rect = new Rectangle(0, 0, POINT_RADIUS * 2, POINT_RADIUS * 2);

        for (Point pt: points)
        {
            rect.x = pt.x - POINT_RADIUS;
            rect.y = pt.y - POINT_RADIUS;

            if (rect.contains(evt.getX(), evt.getY()))
            {
                state = State.DRAG_POINT;
                dragPoint = pt;
                return;
            }
        }
    }//GEN-LAST:event_formMousePressed

    private void formMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMouseReleased
    {//GEN-HEADEREND:event_formMouseReleased
        requestFocus();

        curMouse = evt;
        if (state == State.DRAW_DATA)
        {
            points.add(new Point(evt.getX(), evt.getY()));
            fitBezier();
            fitSpline();
        }
        else if (state == State.DRAG_POINT)
        {
            state = State.NONE;
        }
        repaint();
    }//GEN-LAST:event_formMouseReleased

    private void formMouseDragged(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMouseDragged
    {//GEN-HEADEREND:event_formMouseDragged
        curMouse = evt;

        if (state == State.DRAG_POINT)
        {
            dragPoint.setLocation(evt.getX(), evt.getY());
            fitBezier();
            fitSpline();
        }
        repaint();
    }//GEN-LAST:event_formMouseDragged

    private void formKeyReleased(java.awt.event.KeyEvent evt)//GEN-FIRST:event_formKeyReleased
    {//GEN-HEADEREND:event_formKeyReleased
        switch (evt.getKeyCode())
        {
            case KeyEvent.VK_ENTER:
                state = State.NONE;
                break;
        }
        repaint();
    }//GEN-LAST:event_formKeyReleased

    private void formMouseMoved(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMouseMoved
    {//GEN-HEADEREND:event_formMouseMoved
        curMouse = evt;
        repaint();
    }//GEN-LAST:event_formMouseMoved

    /**
     * @return the fitDegree
     */
    public int getFitDegree()
    {
        return fitDegree;
    }

    /**
     * @param fitDegree the fitDegree to set
     */
    public void setFitDegree(int fitDegree)
    {
        this.fitDegree = fitDegree;
        fitSpline();
        repaint();
    }

    /**
     * @return the fitOpen
     */
    public boolean isFitOpen()
    {
        return fitOpen;
    }

    /**
     * @param fitOpen the fitOpen to set
     */
    public void setFitOpen(boolean fitOpen)
    {
        this.fitOpen = fitOpen;
        fitSpline();
        repaint();
    }

    /**
     * @return the fitNumPoints
     */
    public int getFitNumPoints()
    {
        return fitNumPoints;
    }

    /**
     * @param fitNumPoints the fitNumPoints to set
     */
    public void setFitNumPoints(int fitNumPoints)
    {
        this.fitNumPoints = fitNumPoints;
        fitSpline();
        repaint();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    static enum State { NONE, DRAW_DATA, DRAG_POINT }

}
