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
import static java.lang.Math.*;
import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.shape.PathConsumer;

/**
 *
 * @author kitfox
 */
public class BezierLine2i extends BezierCurve2i
{
    private final int ax0;
    private final int ay0;
    private final int ax1;
    private final int ay1;

    public BezierLine2i(int ax0, int ay0, int ax1, int ay1)
    {
        this.ax0 = ax0;
        this.ay0 = ay0;
        this.ax1 = ax1;
        this.ay1 = ay1;
    }

    @Override
    public int getOrder()
    {
        return 2;
    }

    @Override
    public BezierLine2i reverse()
    {
        return new BezierLine2i(ax1, ay1, ax0, ay0);
    }

    @Override
    public int getTanInX()
    {
        return ax1 - ax0;
    }

    @Override
    public int getTanInY()
    {
        return ay1 - ay0;
    }

    @Override
    public int getTanOutX()
    {
        return ax1 - ax0;
    }

    @Override
    public int getTanOutY()
    {
        return ay1 - ay0;
    }

    @Override
    public int getStartX()
    {
        return ax0;
    }

    @Override
    public int getStartY()
    {
        return ay0;
    }

    @Override
    public int getEndX()
    {
        return ax1;
    }

    @Override
    public int getEndY()
    {
        return ay1;
    }
    
    @Override
    public int getMinX()
    {
        return Math.min(ax0, ax1);
    }
    
    @Override
    public int getMinY()
    {
        return Math.min(ay0, ay1);
    }
    
    @Override
    public int getMaxX()
    {
        return Math.max(ax0, ax1);
    }
    
    @Override
    public int getMaxY()
    {
        return Math.max(ay0, ay1);
    }

    @Override
    public BezierLine2i[] split(double t)
    {
        double bx0 = ax0 + t * (ax1 - ax0);
        double by0 = ay0 + t * (ay1 - ay0);

        return new BezierLine2i[]{
            new BezierLine2i(ax0, ay0, 
                (int)round(bx0), (int)round(by0)),
            
            new BezierLine2i((int)round(bx0), (int)round(by0),
                ax1, ay1)
        };
    }

    @Override
    public void evaluate(double t, CyVector2d pos, CyVector2d tan)
    {
        double bx0 = ax0 + t * (ax1 - ax0);
        double by0 = ay0 + t * (ay1 - ay0);

        if (pos != null)
        {
            pos.set(bx0, by0);
        }

        if (tan != null)
        {
            tan.set(ax1 - ax0, ay1 - ay0);
        }
    }

    @Override
    public BezierPoint2i getDerivative()
    {
        return new BezierPoint2i(ax1 - ax0, ay1 - ay0);
    }
    
    @Override
    public boolean isColinear()
    {
        return true;
    }

    @Override
    public BezierCubic2i asCubic()
    {
        return new BezierCubic2i(ax0, ay0, 
                (2 * ax0 + ax1) / 3, (2 * ay0 + ay1) / 3, 
                (ax0 + 2 * ax1) / 3, (ay0 + 2 * ay1) / 3, 
                ax1, ay1);
    }

    @Override
    public double getCurvatureSquared()
    {
        return 0;
    }

    @Override
    public boolean convexHullSelfIsect()
    {
        return false;
    }

    @Override
    public BezierLine2i getBaseline()
    {
        return this;
    }

    @Override
    public BezierLine2i offset(double width)
    {
        CyVector2d v = new CyVector2d(ax1 - ax0, ay1 - ay0);
        v.normalize();
        v.rotCCW90();
        v.scale(width);

        return new BezierLine2i((int)(ax0 + v.x), (int)(ay0 + v.y),
                (int)(ax1 + v.x), (int)(ay1 + v.y));
    }

    @Override
    public void append(PathConsumer out)
    {
        out.lineTo(ax1, ay1);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final BezierLine2i other = (BezierLine2i) obj;
        if (this.ax0 != other.ax0)
        {
            return false;
        }
        if (this.ay0 != other.ay0)
        {
            return false;
        }
        if (this.ax1 != other.ax1)
        {
            return false;
        }
        if (this.ay1 != other.ay1)
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 89 * hash + this.ax0;
        hash = 89 * hash + this.ay0;
        hash = 89 * hash + this.ax1;
        hash = 89 * hash + this.ay1;
        return hash;
    }

    /**
     * @return the ax0
     */
    public int getAx0()
    {
        return ax0;
    }

    /**
     * @return the ay0
     */
    public int getAy0()
    {
        return ay0;
    }

    /**
     * @return the ax1
     */
    public int getAx1()
    {
        return ax1;
    }

    /**
     * @return the ay1
     */
    public int getAy1()
    {
        return ay1;
    }

    @Override
    public BezierLine2i setStart(int x, int y)
    {
        return new BezierLine2i(x, y, ax1, ay1);
    }

    @Override
    public BezierLine2i setEnd(int x, int y)
    {
        return new BezierLine2i(ax0, ay0, x, y);
    }

    @Override
    public BezierLine2i setEndPoints(int x0, int y0, int x1, int y1)
    {
        return new BezierLine2i(x0, y0, x1, y1);
    }

    @Override
    public PickPoint getClosestPoint(double x, double y)
    {
        //Calculate analytically
        double t = Math2DUtil.fractionAlongRay(x, y, ax0, ay0, ax1 - ax0, ay1 - ay0);
        t = Math2DUtil.clamp(t, 0, 1);

        double rx = Math2DUtil.lerp(ax0, ax1, t);
        double ry = Math2DUtil.lerp(ay0, ay1, t);
        return new PickPoint(rx, ry, t, Math2DUtil.distSquared(rx, ry, x, y));
    }

    @Override
    public boolean isPoint()
    {
        return ax0 == ax1 && ay0 == ay1;
    }

    @Override
    public BezierLine2d transfrom(CyMatrix4d xform)
    {
        CyVector2d a0 = new CyVector2d(ax0, ay0);
        CyVector2d a1 = new CyVector2d(ax1, ay1);
        
        xform.transformPoint(a0);
        xform.transformPoint(a1);
        
        return new BezierLine2d(a0.x, a0.y, a1.x, a1.y);
    }

    @Override
    public String toString()
    {
        return String.format("line {(%d, %d)(%d, %d)}",
                ax0, ay0, ax1, ay1);
    }
}
