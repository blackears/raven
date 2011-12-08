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

import com.kitfox.coyote.math.CyMatrix2d;
import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.shape.PathConsumer;

/**
 *
 * @author kitfox
 */
public class BezierCubic2i extends BezierCurve2i
{
    private final int ax0;
    private final int ay0;
    private final int ax1;
    private final int ay1;
    private final int ax2;
    private final int ay2;
    private final int ax3;
    private final int ay3;

    public BezierCubic2i(int ax0, int ay0, int ax1, int ay1, int ax2, int ay2, int ax3, int ay3)
    {
        this.ax0 = ax0;
        this.ay0 = ay0;
        this.ax1 = ax1;
        this.ay1 = ay1;
        this.ax2 = ax2;
        this.ay2 = ay2;
        this.ax3 = ax3;
        this.ay3 = ay3;
    }

    @Override
    public int getDegree()
    {
        return 4;
    }
    
    @Override
    public BezierCubic2i reverse()
    {
        return new BezierCubic2i(ax3, ay3, ax2, ay2, ax1, ay1, ax0, ay0);
    }

    @Override
    public int getTanInX()
    {
        if (ax1 != ax0 || ay1 != ay0)
        {
            return ax1 - ax0;
        }
        if (ax2 != ax0 || ay2 != ay0)
        {
            return ax2 - ax0;
        }
        return ax3 - ax0;
    }

    @Override
    public int getTanInY()
    {
        if (ax1 != ax0 || ay1 != ay0)
        {
            return ay1 - ay0;
        }
        if (ax2 != ax0 || ay2 != ay0)
        {
            return ay2 - ay0;
        }
        return ay3 - ay0;
    }

    @Override
    public int getTanOutX()
    {
        if (ax2 != ax3 || ay2 != ay3)
        {
            return ax3 - ax2;
        }
        if (ax1 != ax3 || ay1 != ay3)
        {
            return ax3 - ax1;
        }
        return ax3 - ax0;
    }

    @Override
    public int getTanOutY()
    {
        if (ax2 != ax3 || ay2 != ay3)
        {
            return ay3 - ay2;
        }
        if (ax1 != ax3 || ay1 != ay3)
        {
            return ay3 - ay1;
        }
        return ay3 - ay0;
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
        return ax3;
    }

    @Override
    public int getEndY()
    {
        return ay3;
    }
    
    @Override
    public int getMinX()
    {
        return Math.min(Math.min(Math.min(ax0, ax1), ax2), ax3);
    }
    
    @Override
    public int getMinY()
    {
        return Math.min(Math.min(Math.min(ay0, ay1), ay2), ay3);
    }
    
    @Override
    public int getMaxX()
    {
        return Math.max(Math.max(Math.max(ax0, ax1), ax2), ax3);
    }
    
    @Override
    public int getMaxY()
    {
        return Math.max(Math.max(Math.max(ay0, ay1), ay2), ay3);
    }

    @Override
    public BezierCubic2i[] split(double t)
    {
        double bx0 = ax0 + t * (ax1 - ax0);
        double by0 = ay0 + t * (ay1 - ay0);
        double bx1 = ax1 + t * (ax2 - ax1);
        double by1 = ay1 + t * (ay2 - ay1);
        double bx2 = ax2 + t * (ax3 - ax2);
        double by2 = ay2 + t * (ay3 - ay2);
        double cx0 = bx0 + t * (bx1 - bx0);
        double cy0 = by0 + t * (by1 - by0);
        double cx1 = bx1 + t * (bx2 - bx1);
        double cy1 = by1 + t * (by2 - by1);
        double dx0 = cx0 + t * (cx1 - cx0);
        double dy0 = cy0 + t * (cy1 - cy0);

        return new BezierCubic2i[]{
            new BezierCubic2i(ax0, ay0, (int)bx0, (int)by0, (int)cx0, (int)cy0, (int)dx0, (int)dy0),
            new BezierCubic2i((int)dx0, (int)dy0, (int)cx1, (int)cy1, (int)bx2, (int)by2, ax3, ay3)
        };
    }

    @Override
    public BezierQuad2i getDerivative()
    {
        return new BezierQuad2i(3 * (ax1 - ax0),
                3 * (ay1 - ay0),
                3 * (ax2 - ax1),
                3 * (ay2 - ay1),
                3 * (ax3 - ax2),
                3 * (ay3 - ay2));
    }

    @Override
    public void evaluate(double t, CyVector2d pos, CyVector2d tan)
    {
        double bx0 = ax0 + t * (ax1 - ax0);
        double by0 = ay0 + t * (ay1 - ay0);
        double bx1 = ax1 + t * (ax2 - ax1);
        double by1 = ay1 + t * (ay2 - ay1);
        double bx2 = ax2 + t * (ax3 - ax2);
        double by2 = ay2 + t * (ay3 - ay2);
        double cx0 = bx0 + t * (bx1 - bx0);
        double cy0 = by0 + t * (by1 - by0);
        double cx1 = bx1 + t * (bx2 - bx1);
        double cy1 = by1 + t * (by2 - by1);
        double dx0 = cx0 + t * (cx1 - cx0);
        double dy0 = cy0 + t * (cy1 - cy0);

        if (pos != null)
        {
            pos.set(dx0, dy0);
        }

        if (tan != null)
        {
            tan.set(cx1 - cx0, cy1 - cy0);
        }
    }
    
    @Override
    public boolean isColinear()
    {
        int dx01 = ax1 - ax0;
        int dx02 = ax2 - ax0;
        int dx03 = ax3 - ax0;
        int dy01 = ay1 - ay0;
        int dy02 = ay2 - ay0;
        int dy03 = ay3 - ay0;
        return dx01 * dy03 == dy01 * dx03 
                && dx02 * dy03 == dy02 * dx03 
                && dx01 * dy02 == dy01 * dx02;
    }

    @Override
    public BezierCubic2i asCubic()
    {
        return this;
    }

    @Override
    public double getCurvatureSquared()
    {
        return Math.max(Math2DUtil.distPointLineSquared(
                ax1, ay1,
                ax0, ay0, ax3 - ax0, ay3 - ay0),
            Math2DUtil.distPointLineSquared(
                ax2, ay2,
                ax0, ay0, ax3 - ax0, ay3 - ay0));
    }

    @Override
    public boolean convexHullSelfIsect()
    {
        //Point order 0, 1, 2, 3
        int hullArea = 
                Math2DUtil.cross(ax1 - ax0, ay1 - ay0, ax2 - ax0, ay2 - ay0)
                + Math2DUtil.cross(ax2 - ax0, ay2 - ay0, ax3 - ax0, ay3 - ay0);

        //Point order 0, 2, 1, 3
        int hullAreaSwitch = 
                Math2DUtil.cross(ax2 - ax0, ay2 - ay0, ax1 - ax0, ay1 - ay0)
                + Math2DUtil.cross(ax1 - ax0, ay1 - ay0, ax3 - ax0, ay3 - ay0);
        
        return Math.abs(hullArea) < Math.abs(hullAreaSwitch);
    }

    @Override
    public BezierCubic2i offset(double width)
    {
        //Find points and tangents offset line will need to match
        //Initial points of offset curve displaced perpendicular
        // to curve
        CyVector2d p0 = new CyVector2d();
        CyVector2d t0 = new CyVector2d();
        evaluate(0, p0, t0);
        t0.normalize();
        t0.scale(width);

        CyVector2d p3 = new CyVector2d();
        CyVector2d t3 = new CyVector2d();
        evaluate(1, p3, t3);
        t3.normalize();
        t3.scale(width);

        CyVector2d pm = new CyVector2d();
        CyVector2d tm = new CyVector2d();
        evaluate(.5, pm, tm);
        tm.normalize();
        tm.scale(width);

        CyVector2d q0 = new CyVector2d(p0.x - t0.y, p0.y + t0.x);
        CyVector2d q3 = new CyVector2d(p3.x - t3.y, p3.y + t3.x);
        CyVector2d qm = new CyVector2d(pm.x - tm.y, pm.y + tm.x);

        return create(q0, qm, q3, t0, t3);
    }

    /**
     * Create the bezier that intersects given points and has
     * given input and output tangents
     *
     * @param p0 Curve will have this value for t = 0
     * @param pm Curve will have this value for t = .5
     * @param p1 Curve will have this value for t = 1
     * @param t0 Curve tangent will be parallel to this at t = 0
     * @param t1 Curve tangent will be parallel to this at t = 1
     * @return
     */
    public static BezierCubic2i create(
            CyVector2d p0, CyVector2d pm, CyVector2d p1,
            CyVector2d t0, CyVector2d t1)
    {
        //Calculate bezier with
        // B(0) = p0
        // B(1) = p1
        // B(.5) = pm
        // B'(0) = t0 * s0
        // B'(1) = t1 * s1
        // where s0, s1 are scalars
        //
        // Eqn of bezier:
        //B(t) = (1 - t)^3 p0 + 3 (1 - t)^2 t p1 + 3 (1 - t) t^2 p2 + t^3 p3
        //
        //Define knots p1, p2 from terminal points and tangents:
        // p1 = p0 + s0 * t0
        // p2 = p3 + s3 * t3
        //
        //Then
        // B(.5) = 1/8 (p0 + 3 p1 + 3 p2 + p3)
        // pm = 1/8(p0 + 3 * (p0 + s0 * t0) + 3 * (p3 + s3 * t3) + p3)
        // s0 * t0 + s3 * t3 = (8 * pm - 4 * p0 - 4 * p3) / 3
        //Let s be the column vector [s0 s3]
        //Let T be the matrix [t0 t3]
        // s = T^-1 (8 * pm - 4 * p0 - 4 * p3) / 3

        CyMatrix2d T = new CyMatrix2d(t0.x, t0.y, t1.x, t1.y);
        T.invert();
        CyVector2d s = new CyVector2d(
                8 * pm.x - 4 * (p0.x + p1.x),
                8 * pm.y - 4 * (p0.y + p1.y));
        s.scale(1 / 3.0);
        T.transform(s);

        return new BezierCubic2i(
                (int)p0.x, (int)p0.y,
                (int)(p0.x + t0.x * s.x), (int)(p0.y + t0.y * s.x),
                (int)(p1.x + t1.x * s.y), (int)(p1.y + t1.y * s.y),
                (int)p1.x, (int)p1.y);
    }

    @Override
    public void append(PathConsumer out)
    {
        out.cubicTo(ax1, ay1, ax2, ay2, ax3, ay3);
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
        final BezierCubic2i other = (BezierCubic2i) obj;
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
        if (this.ay3 != other.ay3)
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 79 * hash + this.ax0;
        hash = 79 * hash + this.ay0;
        hash = 79 * hash + this.ax1;
        hash = 79 * hash + this.ay1;
        hash = 79 * hash + this.ax2;
        hash = 79 * hash + this.ay2;
        hash = 79 * hash + this.ax3;
        hash = 79 * hash + this.ay3;
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

    /**
     * @return the ax3
     */
    public int getAx3()
    {
        return ax3;
    }

    /**
     * @return the ay3
     */
    public int getAy3()
    {
        return ay3;
    }

    @Override
    public BezierCubic2i setStart(int x, int y)
    {
        return new BezierCubic2i(x, y, ax1, ay1, ax2, ay2, ax3, ay3);
    }

    @Override
    public BezierCubic2i setEnd(int x, int y)
    {
        return new BezierCubic2i(ax0, ay0, ax1, ay1, ax2, ay2, x, y);
    }

}
