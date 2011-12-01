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
    
    abstract public double getCurvatureSquared();

    abstract public BezierCurve2i offset(double width);
    
    abstract public void append(PathConsumer out);

}
