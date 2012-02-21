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
abstract public class BezierCurve2d
{
    abstract public int getOrder();
    abstract public BezierCurve2d reverse();

    abstract public double getTanInX();
    abstract public double getTanInY();
    abstract public double getTanOutX();
    abstract public double getTanOutY();

    abstract public double getStartX();
    abstract public double getStartY();
    abstract public double getEndX();
    abstract public double getEndY();
    
    //Bounding box of hull
    abstract public double getMinX();
    abstract public double getMinY();
    abstract public double getMaxX();
    abstract public double getMaxY();
    
    public boolean boundingBoxIntersects(BezierCurve2d curve)
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

    public double getBoundingBoxWidth()
    {
        return getMaxX() - getMinX();
    }

    public double getBoundingBoxHeight()
    {
        return getMaxY() - getMinY();
    }

    abstract public BezierCurve2d[] split(double t);
//    abstract public void evaluate(double t, CyVector2d pos, CyVector2d tan);
    abstract public CyVector2d evaluate(double t, CyVector2d val);
    abstract public BezierCurve2d getDerivative();
    public BezierLine2d getBaseline()
    {
        return new BezierLine2d(getStartX(), getStartY(), getEndX(), getEndY());
    }
    abstract public BezierCubic2d asCubic();

    abstract public double getCurvatureSquared();

    abstract public BezierCurve2d offset(double width);

    abstract public void append(PathConsumer out);
}
