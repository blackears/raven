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
public class BezierQuad2i extends BezierCurve2i
{
    private final int ax0;
    private final int ay0;
    private final int ax1;
    private final int ay1;
    private final int ax2;
    private final int ay2;

    public BezierQuad2i(int ax0, int ay0, int ax1, int ay1, int ax2, int ay2)
    {
        this.ax0 = ax0;
        this.ay0 = ay0;
        this.ax1 = ax1;
        this.ay1 = ay1;
        this.ax2 = ax2;
        this.ay2 = ay2;
    }

    @Override
    public int getOrder()
    {
        return 3;
    }

    @Override
    public BezierQuad2i reverse()
    {
        return new BezierQuad2i(ax2, ay2, ax1, ay1, ax0, ay0);
    }

    @Override
    public int getTanInX()
    {
        return (ax1 != ax0 || ay1 != ay0) ? ax1 - ax0 : ax2 - ax0;
    }

    @Override
    public int getTanInY()
    {
        return (ax1 != ax0 || ay1 != ay0) ? ay1 - ay0 : ay2 - ay0;
    }

    @Override
    public int getTanOutX()
    {
        return (ax2 != ax1 || ay2 != ay1) ? ax2 - ax1 : ax2 - ax0;
    }

    @Override
    public int getTanOutY()
    {
        return (ax2 != ax1 || ay2 != ay1) ? ay2 - ay1 : ay2 - ay0;
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
        return ax2;
    }

    @Override
    public int getEndY()
    {
        return ay2;
    }
    
    @Override
    public int getMinX()
    {
        return Math.min(Math.min(ax0, ax1), ax2);
    }
    
    @Override
    public int getMinY()
    {
        return Math.min(Math.min(ay0, ay1), ay2);
    }
    
    @Override
    public int getMaxX()
    {
        return Math.max(Math.max(ax0, ax1), ax2);
    }
    
    @Override
    public int getMaxY()
    {
        return Math.max(Math.max(ay0, ay1), ay2);
    }

    @Override
    public BezierQuad2i[] split(double t)
    {
        double bx0 = ax0 + t * (ax1 - ax0);
        double by0 = ay0 + t * (ay1 - ay0);
        double bx1 = ax1 + t * (ax2 - ax1);
        double by1 = ay1 + t * (ay2 - ay1);
        double cx0 = bx0 + t * (bx1 - bx0);
        double cy0 = by0 + t * (by1 - by0);

        return new BezierQuad2i[]{
            new BezierQuad2i(ax0, ay0, 
                (int)round(bx0), (int)round(by0),
                (int)round(cx0), (int)round(cy0)),
            
            new BezierQuad2i((int)round(cx0), (int)round(cy0),
                (int)round(bx1), (int)round(by1),
                ax2, ay2)
        };
    }

    @Override
    public void evaluate(double t, CyVector2d pos, CyVector2d tan)
    {
        double bx0 = ax0 + t * (ax1 - ax0);
        double by0 = ay0 + t * (ay1 - ay0);
        double bx1 = ax1 + t * (ax2 - ax1);
        double by1 = ay1 + t * (ay2 - ay1);
        double cx0 = bx0 + t * (bx1 - bx0);
        double cy0 = by0 + t * (by1 - by0);

        if (pos != null)
        {
            pos.set(cx0, cy0);
        }

        if (tan != null)
        {
            tan.set(bx1 - bx0, by1 - by0);
        }
    }

    @Override
    public BezierLine2i getDerivative()
    {
        return new BezierLine2i(2 * (ax1 - ax0),
                2 * (ay1 - ay0),
                2 * (ax2 - ax1),
                2 * (ay2 - ay1));
    }
    
    @Override
    public boolean isColinear()
    {
        int dx01 = ax1 - ax0;
        int dx02 = ax2 - ax0;
        int dy01 = ay1 - ay0;
        int dy02 = ay2 - ay0;
        return dx01 * dy02 == dy01 * dx02;
    }

    @Override
    public BezierCubic2i asCubic()
    {
        return new BezierCubic2i(ax0, ay0, 
                (ax0 + 2 * ax1) / 3, (ay0 + 2 * ay1) / 3, 
                (ax2 + 2 * ax1) / 3, (ay2 + 2 * ay1) / 3, 
                ax2, ay2);
    }

    @Override
    public double getCurvatureSquared()
    {
        if (ax2 == ax0 && ay2 == ay0)
        {
            return Math2DUtil.distSquared(ax1, ay1, ax0, ay0);
        }
        
        return Math2DUtil.distPointLineSquared(ax1, ay1,
                ax0, ay0, ax2 - ax0, ay2 - ay0);
    }

    @Override
    public boolean convexHullSelfIsect()
    {
        return false;
    }

    @Override
    public BezierQuad2i offset(double width)
    {
        //Find points and tangents offset line will need to match
        //Initial points of offset curve displaced perpendicular
        // to curve
        CyVector2d p0 = new CyVector2d();
        CyVector2d t0 = new CyVector2d();
        evaluate(0, p0, t0);
        if (t0.lengthSquared() == 0)
        {
            t0.x = 1;
        }
        t0.normalize();
        t0.scale(width);

        CyVector2d p2 = new CyVector2d();
        CyVector2d t2 = new CyVector2d();
        evaluate(1, p2, t2);
        if (t2.lengthSquared() == 0)
        {
            t2.x = 1;
        }
        t2.normalize();
        t2.scale(width);

        CyVector2d pm = new CyVector2d();
        CyVector2d tm = new CyVector2d();
        evaluate(.5, pm, tm);
        if (tm.lengthSquared() == 0)
        {
            tm.x = 1;
        }
        tm.normalize();
        tm.scale(width);

        CyVector2d q0 = new CyVector2d(p0.x - t0.y, p0.y + t0.x);
        CyVector2d q2 = new CyVector2d(p2.x - t2.y, p2.y + t2.x);
        CyVector2d qm = new CyVector2d(pm.x - tm.y, pm.y + tm.x);

        //Calculate bezier B(t) where
        //B(0) = q0
        //B(1) = q2
        //B(.5) = qm
        //
        // Eqn of bezier:
        //B(t) = (1 - t)^2 p0 + 2 (1 - t) t p1 + t^2 p2
        //
        //So
        // B(.5) = 1/4 (q0 + 2 * q1 + q2)
        // 4qm = q0 + 2 q1 + q2
        // q1 = 1/2 (4qm - q0 - q2)

        return new BezierQuad2i(
                (int)q0.x, (int)q0.y,
                (int)((4 * qm.x - q0.x - q2.x) / 2), (int)((4 * qm.y - q0.y - q2.y) / 2),
                (int)q2.x, (int)q2.y);
    }

    @Override
    public void append(PathConsumer out)
    {
        out.quadTo(ax1, ay1, ax2, ay2);
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
        final BezierQuad2i other = (BezierQuad2i) obj;
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
        if (this.ax2 != other.ax2)
        {
            return false;
        }
        if (this.ay2 != other.ay2)
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 67 * hash + this.ax0;
        hash = 67 * hash + this.ay0;
        hash = 67 * hash + this.ax1;
        hash = 67 * hash + this.ay1;
        hash = 67 * hash + this.ax2;
        hash = 67 * hash + this.ay2;
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

    /**
     * @return the ax2
     */
    public int getAx2()
    {
        return ax2;
    }

    /**
     * @return the ay2
     */
    public int getAy2()
    {
        return ay2;
    }

    @Override
    public BezierQuad2i setStart(int x, int y)
    {
        return new BezierQuad2i(x, y, ax1, ay1, ax2, ay2);
    }

    @Override
    public BezierQuad2i setEnd(int x, int y)
    {
        return new BezierQuad2i(ax0, ay0, ax1, ay1, x, y);
    }

    @Override
    public BezierQuad2i setEndPoints(int x0, int y0, int x1, int y1)
    {
        return new BezierQuad2i(x0, y0, ax1, ay1, x1, y1);
    }

    @Override
    public boolean isPoint()
    {
        return ax0 == ax1 && ax0 == ax2
                && ay0 == ay1 && ay0 == ay2;
    }

    @Override
    public BezierQuad2d transfrom(CyMatrix4d xform)
    {
        CyVector2d a0 = new CyVector2d(ax0, ay0);
        CyVector2d a1 = new CyVector2d(ax1, ay1);
        CyVector2d a2 = new CyVector2d(ax2, ay2);
        
        xform.transformPoint(a0);
        xform.transformPoint(a1);
        xform.transformPoint(a2);
        
        return new BezierQuad2d(a0.x, a0.y, a1.x, a1.y, a2.x, a2.y);
    }

    @Override
    public String toString()
    {
        return String.format("quad {(%d, %d)(%d, %d)(%d, %d)}",
                ax0, ay0, ax1, ay1, ax2, ay2);
    }

}
