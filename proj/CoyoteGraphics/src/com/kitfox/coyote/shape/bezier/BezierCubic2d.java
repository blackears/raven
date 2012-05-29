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
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.PathConsumer;

/**
 *
 * @author kitfox
 */
public class BezierCubic2d extends BezierCurve2d
{
    private final double ax0;
    private final double ay0;
    private final double ax1;
    private final double ay1;
    private final double ax2;
    private final double ay2;
    private final double ax3;
    private final double ay3;

    public BezierCubic2d(double ax0, double ay0, double ax1, double ay1, double ax2, double ay2, double ax3, double ay3)
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

    public BezierCubic2d(CyVector2d p0, CyVector2d p1, CyVector2d p2, CyVector2d p3)
    {
        this(p0.x, p0.y, p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
    }

    @Override
    public int getOrder()
    {
        return 4;
    }

    @Override
    public BezierCubic2d reverse()
    {
        return new BezierCubic2d(ax3, ay3, ax2, ay2, ax1, ay1, ax0, ay0);
    }

    @Override
    public double getTanInX()
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
    public double getTanInY()
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
    public double getTanOutX()
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
    public double getTanOutY()
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
    public double getStartX()
    {
        return ax0;
    }

    @Override
    public double getStartY()
    {
        return ay0;
    }

    @Override
    public double getEndX()
    {
        return ax3;
    }

    @Override
    public double getEndY()
    {
        return ay3;
    }
    
    @Override
    public double getMinX()
    {
        return Math.min(Math.min(Math.min(ax0, ax1), ax2), ax3);
    }
    
    @Override
    public double getMinY()
    {
        return Math.min(Math.min(Math.min(ay0, ay1), ay2), ay3);
    }
    
    @Override
    public double getMaxX()
    {
        return Math.max(Math.max(Math.max(ax0, ax1), ax2), ax3);
    }
    
    @Override
    public double getMaxY()
    {
        return Math.max(Math.max(Math.max(ay0, ay1), ay2), ay3);
    }

    @Override
    public BezierCubic2d[] split(double t)
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

        return new BezierCubic2d[]{
            new BezierCubic2d(ax0, ay0, bx0, by0, cx0, cy0, dx0, dy0),
            new BezierCubic2d(dx0, dy0, cx1, cy1, bx2, by2, ax3, ay3)
        };
    }

    @Override
    public BezierQuad2d getDerivative()
    {
        return new BezierQuad2d(3 * (ax1 - ax0),
                3 * (ay1 - ay0),
                3 * (ax2 - ax1),
                3 * (ay2 - ay1),
                3 * (ax3 - ax2),
                3 * (ay3 - ay2));
    }

    @Override
    public BezierCubic2d asCubic()
    {
        return this;
    }
/*
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
    */
    @Override
    public CyVector2d evaluate(double t, CyVector2d pos)
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

        if (pos == null)
        {
            return new CyVector2d(dx0, dy0);
        }
        
        pos.set(dx0, dy0);
        return pos;

//        if (tan != null)
//        {
//            tan.set(cx1 - cx0, cy1 - cy0);
//        }
    }

    @Override
    public double getCurvatureSquared()
    {
        if (ax3 == ax0 && ay3 == ay0)
        {
            return Math.max(Math2DUtil.distSquared(ax1, ay1, ax0, ay0),
            Math2DUtil.distSquared(ax2, ay2, ax0, ay0));
        }
        
        return Math.max(Math2DUtil.distPointLineSquared(
                ax1, ay1,
                ax0, ay0, ax3 - ax0, ay3 - ay0),
            Math2DUtil.distPointLineSquared(
                ax2, ay2,
                ax0, ay0, ax3 - ax0, ay3 - ay0));
    }
//
//    @Override
//    public BezierCubic2d offset(double width)
//    {
//        //Find points and tangents offset line will need to match
//        //Initial points of offset curve displaced perpendicular
//        // to curve
//        BezierQuad2d dc = getDerivative();
//        
//        CyVector2d p0 = new CyVector2d();
//        CyVector2d t0 = new CyVector2d();
//        evaluate(0, p0);
//        t0.set(getTanInX(), getTanInY());
////        dc.evaluate(0, t0);
//        if (t0.lengthSquared() == 0)
//        {
//            t0.x = 1;
//        }
//        t0.normalize();
//        t0.scale(width);
//
//        CyVector2d p3 = new CyVector2d();
//        CyVector2d t3 = new CyVector2d();
//        evaluate(1, p3);
//        t3.set(getTanOutX(), getTanOutY());
////        dc.evaluate(1, t3);
//        if (t3.lengthSquared() == 0)
//        {
//            t3.x = 1;
//        }
//        t3.normalize();
//        t3.scale(width);
//
//        CyVector2d pm = new CyVector2d();
//        CyVector2d tm = new CyVector2d();
//        evaluate(.5, pm);
//        dc.evaluate(.5, tm);
//        if (tm.lengthSquared() == 0)
//        {
//            tm.set(ax3 - ax0, ay3 - ay0);
//        }
//        if (tm.lengthSquared() == 0)
//        {
//            tm.x = 1;
//        }
//        tm.normalize();
//        tm.scale(width);
//
//        CyVector2d q0 = new CyVector2d(p0.x - t0.y, p0.y + t0.x);
//        CyVector2d q3 = new CyVector2d(p3.x - t3.y, p3.y + t3.x);
//        CyVector2d qm = new CyVector2d(pm.x - tm.y, pm.y + tm.x);
//
//        return create(q0, qm, q3, t0, t3);
//    }

    @Override
    public BezierCubic2d offset(double width)
    {
        //Find points and tangents offset line will need to match
        //Initial points of offset curve displaced perpendicular
        // to curve
        BezierQuad2d dc = getDerivative();
        
        CyVector2d p0 = new CyVector2d(ax0, ay0);
        CyVector2d t0 = new CyVector2d(getTanInX(), getTanInY());

        if (t0.lengthSquared() == 0)
        {
            t0.x = 1;
        }
        t0.normalize();
        t0.scale(width);

        CyVector2d p3 = new CyVector2d(ax3, ay3);
        CyVector2d t3 = new CyVector2d(getTanOutX(), getTanOutY());

        if (t3.lengthSquared() == 0)
        {
            t3.x = 1;
        }
        t3.normalize();
        t3.scale(width);

        final int numFitPoints = 3;
        final double dt = 1.0 / (numFitPoints + 1);
        double[] tArr = new double[numFitPoints + 2];
        CyVector2d[] points = new CyVector2d[numFitPoints + 2];

        CyVector2d q0 = new CyVector2d(p0.x - t0.y, p0.y + t0.x);
        CyVector2d q3 = new CyVector2d(p3.x - t3.y, p3.y + t3.x);
        
        tArr[0] = 0;
        tArr[numFitPoints + 1] = 1;
        points[0] = q0;
        points[numFitPoints + 1] = q3;
        
        for (int i = 0; i < numFitPoints; ++i)
        {
            double t = (i + 1) * dt;
            CyVector2d pm = new CyVector2d();
            CyVector2d tm = new CyVector2d();
            evaluate(t, pm);
            dc.evaluate(t, tm);
            if (tm.lengthSquared() == 0)
            {
                tm.set(ax3 - ax0, ay3 - ay0);
                if (tm.lengthSquared() == 0)
                {
                    tm.x = 1;
                }
            }
            tm.normalize();
            tm.scale(width);

            CyVector2d qm = new CyVector2d(pm.x - tm.y, pm.y + tm.x);
            
            tArr[i + 1] = t;
            points[i + 1] = qm;
        }

        t3.negate();
        
        BezierCubic2d curve = BezierFit2d.generateBezier(
                0, numFitPoints + 1, 
                tArr, points, 
                t0, t3);
        
        return curve;
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
    public static BezierCubic2d create(
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
        
        if (T.determinant() == 0)
        {
            //Tangents are parallel.  Cannot solve eqn, so fit to quad
            BezierQuad2d curve = new BezierQuad2d(
                    p0.x, p0.y,
                    (4 * pm.x - p0.x - p1.x) / 2, (4 * pm.y - p0.y - p1.y) / 2, 
                    p1.x, p1.y);
            return curve.asCubic();
        }
        
        T.invert();
        CyVector2d s = new CyVector2d(
                8 * pm.x - 4 * (p0.x + p1.x),
                8 * pm.y - 4 * (p0.y + p1.y));
        s.scale(1 / 3.0);
        T.transform(s);

        return new BezierCubic2d(
                p0.x, p0.y,
                p0.x + t0.x * s.x, p0.y + t0.y * s.x,
                p1.x + t1.x * s.y, p1.y + t1.y * s.y,
                p1.x, p1.y);
    }
    
    @Override
    public BezierCubic2d transform(CyMatrix4d xform)
    {
        CyVector2d a0 = new CyVector2d(ax0, ay0);
        CyVector2d a1 = new CyVector2d(ax1, ay1);
        CyVector2d a2 = new CyVector2d(ax2, ay2);
        CyVector2d a3 = new CyVector2d(ax3, ay3);
        
        xform.transformPoint(a0);
        xform.transformPoint(a1);
        xform.transformPoint(a2);
        xform.transformPoint(a3);
        
        return new BezierCubic2d(a0.x, a0.y, a1.x, a1.y, a2.x, a2.y, a3.x, a3.y);
    }

    @Override
    public CyPath2d asPath()
    {
        CyPath2d path = new CyPath2d();
        path.moveTo(ax0, ay0);
        path.cubicTo(ax1, ay1, ax2, ay2, ax3, ay3);
        return path;
    }

    @Override
    public void append(PathConsumer out)
    {
        out.cubicTo(ax1, ay1, ax2, ay2, ax3, ay3);
    }

    @Override
    public void append(CyPath2d path)
    {
        path.cubicTo(ax1, ay1, ax2, ay2, ax3, ay3);
    }

    public double getStartKnotX()
    {
        return ax1;
    }

    public double getStartKnotY()
    {
        return ay1;
    }

    public double getEndKnotX()
    {
        return ax2;
    }

    public double getEndKnotY()
    {
        return ay2;
    }
    
    /*
    public void clip(BezierCurve curve)
    {
        //Calculate fat line
        double d1 = Math2DUtil.distPointLineSigned(ax1, ay1, ax0, ay0, ax3 - ax0, ay3 - ay0);
        double d2 = Math2DUtil.distPointLineSigned(ax2, ay2, ax0, ay0, ax3 - ax0, ay3 - ay0);

        double dmin, dmax;
        if (d1 * d2 > 0)
        {
            dmin = Math.min(0, Math.min(d1, d2)) * 3 / 4;
            dmax = Math.max(0, Math.max(d1, d2)) * 3 / 4;
        }
        else
        {
            dmin = Math.min(0, Math.min(d1, d2)) * 4 / 9;
            dmax = Math.max(0, Math.max(d1, d2)) * 4 / 9;
        }

        
    }
*/

    /**
     * @return the ax0
     */
    public double getAx0()
    {
        return ax0;
    }

    /**
     * @return the ay0
     */
    public double getAy0()
    {
        return ay0;
    }

    /**
     * @return the ax1
     */
    public double getAx1()
    {
        return ax1;
    }

    /**
     * @return the ay1
     */
    public double getAy1()
    {
        return ay1;
    }

    /**
     * @return the ax2
     */
    public double getAx2()
    {
        return ax2;
    }

    /**
     * @return the ay2
     */
    public double getAy2()
    {
        return ay2;
    }

    /**
     * @return the ax3
     */
    public double getAx3()
    {
        return ax3;
    }

    /**
     * @return the ay3
     */
    public double getAy3()
    {
        return ay3;
    }

    @Override
    public BezierCubic2d setStart(double x, double y)
    {
        return new BezierCubic2d(
                x, y, ax1, ay1, ax2, ay2, ax3, ay3);
    }

    @Override
    public BezierCubic2d setEnd(double x, double y)
    {
        return new BezierCubic2d(
                ax0, ay0, ax1, ay1, ax2, ay2, x, y);
    }

    @Override
    public String asSvg()
    {
        return String.format("M %f %f C %f %f  %f %f  %f %f",
                ax0, ay0, ax1, ay1, ax2, ay2, ax3, ay3);
    }

    @Override
    public String toString()
    {
        return String.format("cubic {(%f, %f)(%f, %f)(%f, %f)(%f, %f)}",
                ax0, ay0, ax1, ay1, ax2, ay2, ax3, ay3);
    }
}
