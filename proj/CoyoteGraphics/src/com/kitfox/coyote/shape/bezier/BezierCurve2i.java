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

package com.kitfox.coyote.shape.bezier;

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.shape.PathConsumer;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
abstract public class BezierCurve2i
{
    abstract public int getOrder();
    abstract public BezierCurve2i reverse();

    abstract public int getTanInX();
    abstract public int getTanInY();
    abstract public int getTanOutX();
    abstract public int getTanOutY();

    abstract public int getStartX();
    abstract public int getStartY();
    abstract public int getEndX();
    abstract public int getEndY();
    
    //Bounding box of hull
    abstract public int getMinX();
    abstract public int getMinY();
    abstract public int getMaxX();
    abstract public int getMaxY();
    
    public boolean boundingBoxIntersects(BezierCurve2i curve)
    {
        return curve.getMaxX() >= getMinX()
                && curve.getMinX() <= getMaxX()
                && curve.getMaxY() >= getMinY()
                && curve.getMinY() <= getMaxY();
    }

    public boolean boundingBoxContains(BezierCurve2i curve)
    {
        return curve.getMinX() >= getMinX()
                && curve.getMaxX() <= getMaxX()
                && curve.getMinY() >= getMinY()
                && curve.getMaxY() <= getMaxY();
    }

    public int getBoundingBoxWidth()
    {
        return getMaxX() - getMinX();
    }

    public int getBoundingBoxHeight()
    {
        return getMaxY() - getMinY();
    }
    
    public boolean isUnitBoundingBox()
    {
        return getMinX() == getMaxX() && getMinY() == getMaxY();
    }
    
    abstract public BezierCurve2i[] split(double t);
    abstract public void evaluate(double t, CyVector2d pos, CyVector2d tan);
    abstract public BezierCurve2i getDerivative();

    abstract public boolean convexHullSelfIsect();

    /**
     * True if all points lie in a straight line
     * @return 
     */
    abstract public boolean isColinear();
    public BezierLine2i getBaseline()
    {
        return new BezierLine2i(getStartX(), getStartY(), getEndX(), getEndY());
    }
    abstract public BezierCubic2i asCubic();
    
    abstract public double getCurvatureSquared();

    abstract public BezierCurve2i offset(double width);
    
    abstract public void append(PathConsumer out);

    /**
     * Split curve at a set of t values.  Values must be arranged in ascending
     * order.
     * 
     * @param t
     * @return 
     */
    public BezierCurve2i[] split(double[] t)
    {
        BezierCurve2i[] ret = new BezierCurve2i[t.length + 1];
        BezierCurve2i curve = this;
        double offset = 0;
        for (int i = 0; i < t.length; ++i)
        {
            BezierCurve2i[] curSplit = curve.split((t[i] - offset) / (1 - offset));
            ret[i] = curSplit[0];
            curve = curSplit[1];
        }
        
        ret[t.length] = curve;
        return ret;
    }

    /**
     * Rebuild this curve with a new starting point.  Since BezierCurve*
     * is immutable, will create a new object rather than changing this one.
     * 
     * @param x
     * @param y
     * @return New curve with altered starting point.
     */
    abstract public BezierCurve2i setStart(int x, int y);
    abstract public BezierCurve2i setEnd(int x, int y);
    abstract public BezierCurve2i setEndPoints(int x0, int y0, int x1, int y1);

    public BezierLine2i[] flatten(double curvatureSquared)
    {
        if (getCurvatureSquared() <= curvatureSquared)
        {
            return new BezierLine2i[]{getBaseline()};
        }
        
        ArrayList<BezierLine2i> segs = new ArrayList<BezierLine2i>();
        flatten(curvatureSquared, segs);
        
        return segs.toArray(new BezierLine2i[segs.size()]);
    }

    private void flatten(double curvatureSquared, ArrayList<BezierLine2i> segs)
    {
        if (getCurvatureSquared() <= curvatureSquared)
        {
            segs.add(getBaseline());
            return;
        }
        
        BezierCurve2i[] curves = split(.5);
        curves[0].flatten(curvatureSquared, segs);
        curves[1].flatten(curvatureSquared, segs);
    }

    public PickPoint getClosestPoint(double x, double y)
    {
        return getClosestPoint(x, y, 0, 1);
    }

    private PickPoint getClosestPoint(double x, double y, double t0, double t1)
    {
        if (getMaxX() - getMinX() <= 2 && getMaxY() - getMinY() <= 2)
        {
            return new PickPoint(getMinX(), getMinY(), t1 == 1 ? 1 : t0, 
                    Math2DUtil.distSquared(getMinX(), getMinY(), x, y));
        }

        BezierCurve2i curves[] = split(.5);
        BezierCurve2i c0 = curves[0];
        BezierCurve2i c1 = curves[1];
        
//System.err.println("edgeSplit " + c0 + "**" + c1);
        
        if (c0.equals(this))
        {
            //Degenerate curve
            return new PickPoint(c0.getStartX(), c0.getStartY(), t0, 
                    Math2DUtil.distSquared(c0.getStartX(), c0.getStartY(), x, y));
        }
        
        if (c1.equals(this))
        {
            //Degenerate curve
            return new PickPoint(c1.getEndX(), c1.getEndY(), t1, 
                    Math2DUtil.distSquared(c1.getEndX(), c1.getEndY(), x, y));
        }
        
        double tMid = (t0 + t1) / 2;
        
        double bestDistSq0, worstDistSq0;
        {
            double d00 = Math2DUtil.distSquared(x, y, c0.getMinX(), c0.getMinY());
            double d10 = Math2DUtil.distSquared(x, y, c0.getMaxX(), c0.getMinY());
            double d01 = Math2DUtil.distSquared(x, y, c0.getMinX(), c0.getMaxY());
            double d11 = Math2DUtil.distSquared(x, y, c0.getMaxX(), c0.getMaxY());
            
            bestDistSq0 = c0.boundingBoxContains(x, y) ? 0 
                    : Math.min(Math.min(Math.min(d00, d01), d10), d11);
            worstDistSq0 = Math.max(Math.max(Math.max(d00, d01), d10), d11);
        }
        
        double bestDistSq1, worstDistSq1;
        {
            double d00 = Math2DUtil.distSquared(x, y, c1.getMinX(), c1.getMinY());
            double d10 = Math2DUtil.distSquared(x, y, c1.getMaxX(), c1.getMinY());
            double d01 = Math2DUtil.distSquared(x, y, c1.getMinX(), c1.getMaxY());
            double d11 = Math2DUtil.distSquared(x, y, c1.getMaxX(), c1.getMaxY());
            
            bestDistSq1 = c1.boundingBoxContains(x, y) ? 0 
                    : Math.min(Math.min(Math.min(d00, d01), d10), d11);
            worstDistSq1 = Math.max(Math.max(Math.max(d00, d01), d10), d11);
        }
        
        if (worstDistSq0 <= bestDistSq1)
        {
            return c0.getClosestPoint(x, y, t0, tMid);
        }
        
        if (worstDistSq1 <= bestDistSq0)
        {
            return c1.getClosestPoint(x, y, tMid, t1);
        }
        
        PickPoint p0 = c0.getClosestPoint(x, y, t0, tMid);
        PickPoint p1 = c1.getClosestPoint(x, y, tMid, t1);
        
        return p0.getDistSquared() < p1.getDistSquared() ? p0 : p1;
    }

    public boolean boundingBoxContains(double x, double y)
    {
        return x >= getMinX() && x <= getMaxX() && y >= getMinY() && y <= getMaxY();
    }

    public boolean boundingBoxContains(int x, int y)
    {
        return x >= getMinX() && x <= getMaxX() && y >= getMinY() && y <= getMaxY();
    }

    public double distBoundingBoxSq(double x, double y)
    {
        int minX = getMinX();
        int maxX = getMaxX();
        int minY = getMinY();
        int maxY = getMaxY();
        
        double dx = x < minX ? minX - x :
                (x > maxX ? maxX - x : 0);
        double dy = y < minY ? minY - y :
                (y > maxY ? maxY - y : 0);
        return dx * dx + dy * dy;
    }

    abstract public boolean isPoint();

    abstract public BezierCurve2d transfrom(CyMatrix4d xform);

}
