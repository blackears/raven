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

import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.shape.PathConsumer;

/**
 *
 * @author kitfox
 */
abstract public class BezierCurve2i
{
    abstract public int getDegree();
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

}
