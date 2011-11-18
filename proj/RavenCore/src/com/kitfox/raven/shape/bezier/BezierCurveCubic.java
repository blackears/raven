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

package com.kitfox.raven.shape.bezier;

import java.awt.geom.Path2D;
import static com.kitfox.raven.shape.bezier.BezierMath.*;

/**
 * Beziers exist on a unit grid.  This is to prevent round off errors and
 * provide equal resolution for the entire coord space.
 *
 * @author kitfox
 */
public class BezierCurveCubic extends BezierCurve
{
    private int a0x;
    private int a0y;
    private int a1x;
    private int a1y;
    private int a2x;
    private int a2y;
    private int a3x;
    private int a3y;

    public BezierCurveCubic(double a0x, double a0y, double a1x, double a1y, double a2x, double a2y, double a3x, double a3y)
    {
        this(
            (int)a0x, (int)a0y,
            (int)a1x, (int)a1y,
            (int)a2x, (int)a2y,
            (int)a3x, (int)a3y
            );
    }

    public BezierCurveCubic(float a0x, float a0y, float a1x, float a1y, float a2x, float a2y, float a3x, float a3y)
    {
        this(
            (int)a0x, (int)a0y,
            (int)a1x, (int)a1y,
            (int)a2x, (int)a2y,
            (int)a3x, (int)a3y
            );
    }

    public BezierCurveCubic(int a0x, int a0y, int a1x, int a1y, int a2x, int a2y, int a3x, int a3y)
    {
        this.a0x = a0x;
        this.a0y = a0y;
        this.a1x = a1x;
        this.a1y = a1y;
        this.a2x = a2x;
        this.a2y = a2y;
        this.a3x = a3x;
        this.a3y = a3y;

////DEBUG
//if (a0x == a1x && a0y == a1y)
//{
//    int j = 9;
//}
//if (a2x == a3x && a2y == a3y)
//{
//    int j = 9;
//}
    }

    public BezierCurveCubic(BezierCurveCubic other)
    {
        this(
                other.a0x, other.a0y,
                other.a1x, other.a1y,
                other.a2x, other.a2y,
                other.a3x, other.a3y
                );
    }

    @Override
    public double getFlatnessSquared()
    {
        //return area of hull
        int dx0 = a1x - a0x;
        int dy0 = a1y - a0y;
        int dx1 = a2x - a0x;
        int dy1 = a2y - a0y;
        int dx2 = a3x - a0x;
        int dy2 = a3y - a0y;

        int area0 = Math.abs(dx1 * dy0 - dx0 * dy1) / 2;
        int area1 = Math.abs(dx2 * dy1 - dx1 * dy2) / 2;
        return area0 + area1;
    }

    @Override
    public BezierCurveCubic copy()
    {
        return new BezierCurveCubic(this);
    }

    @Override
    public BezierCurveQuadratic getDerivative()
    {
        return new BezierCurveQuadratic(3 * (a1x - a0x),
                3 * (a1y - a0y),
                3 * (a2x - a1x),
                3 * (a2y - a1y),
                3 * (a3x - a2x),
                3 * (a3y - a2y));
    }

    @Override
    public double getHullLength()
    {
        return distance(a0x, a0y, a1x, a1y)
                + distance(a1x, a1y, a2x, a2y)
                + distance(a2x, a2y, a3x, a3y);
    }

    @Override
    public double[] calcPoint(double t, double[] point)
    {
        if (point == null)
        {
            point = new double[2];
        }

        double b0x = lerp(a0x, a1x, t);
        double b0y = lerp(a0y, a1y, t);
        double b1x = lerp(a1x, a2x, t);
        double b1y = lerp(a1y, a2y, t);
        double b2x = lerp(a2x, a3x, t);
        double b2y = lerp(a2y, a3y, t);

        double c0x = lerp(b0x, b1x, t);
        double c0y = lerp(b0y, b1y, t);
        double c1x = lerp(b1x, b2x, t);
        double c1y = lerp(b1y, b2y, t);

        double d0x = lerp(c0x, c1x, t);
        double d0y = lerp(c0y, c1y, t);

        point[0] = d0x;
        point[1] = d0y;

        return point;
    }

    @Override
    public double calcPointX(double t)
    {
        double b0x = lerp(a0x, a1x, t);
        double b1x = lerp(a1x, a2x, t);
        double b2x = lerp(a2x, a3x, t);

        double c0x = lerp(b0x, b1x, t);
        double c1x = lerp(b1x, b2x, t);

        return lerp(c0x, c1x, t);
    }

    @Override
    public double calcPointY(double t)
    {
        double b0y = lerp(a0y, a1y, t);
        double b1y = lerp(a1y, a2y, t);
        double b2y = lerp(a2y, a3y, t);

        double c0y = lerp(b0y, b1y, t);
        double c1y = lerp(b1y, b2y, t);

        return lerp(c0y, c1y, t);
    }

    @Override
    public double[] getTangent(double t, double[] tan)
    {
        if (tan == null)
        {
            tan = new double[2];
        }

        double b0x = lerp(a0x, a1x, t);
        double b0y = lerp(a0y, a1y, t);
        double b1x = lerp(a1x, a2x, t);
        double b1y = lerp(a1y, a2y, t);
        double b2x = lerp(a2x, a3x, t);
        double b2y = lerp(a2y, a3y, t);

        double c0x = lerp(b0x, b1x, t);
        double c0y = lerp(b0y, b1y, t);
        double c1x = lerp(b1x, b2x, t);
        double c1y = lerp(b1y, b2y, t);

        tan[0] = c1x - c0x;
        tan[1] = c1y - c0y;

        return tan;
    }

    @Override
    public BezierCurveCubic reverse()
    {
        return new BezierCurveCubic(a3x, a3y, a2x, a2y, a1x, a1y, a0x, a0y);
    }

    @Override
    public BezierCurve[] split(double t, BezierCurve[] segs)
    {
        if (segs == null)
        {
            segs = new BezierCurve[2];
        }

        double b0x = lerp(a0x, a1x, t);
        double b0y = lerp(a0y, a1y, t);
        double b1x = lerp(a1x, a2x, t);
        double b1y = lerp(a1y, a2y, t);
        double b2x = lerp(a2x, a3x, t);
        double b2y = lerp(a2y, a3y, t);

        double c0x = lerp(b0x, b1x, t);
        double c0y = lerp(b0y, b1y, t);
        double c1x = lerp(b1x, b2x, t);
        double c1y = lerp(b1y, b2y, t);

        double d0x = lerp(c0x, c1x, t);
        double d0y = lerp(c0y, c1y, t);

        segs[0] = new BezierCurveCubic(a0x, a0y, b0x, b0y, c0x, c0y, d0x, d0y);
        segs[1] = new BezierCurveCubic(d0x, d0y, c1x, c1y, b2x, b2y, a3x, a3y);

        return segs;
    }

    @Override
    public int getNumKnots()
    {
        return 2;
    }

    @Override
    public int getKnotX(int index)
    {
        switch (index)
        {
            case 0:
                return a1x;
            case 1:
                return a2x;
        }
        return 0;
    }

    @Override
    public int getKnotY(int index)
    {
        switch (index)
        {
            case 0:
                return a1y;
            case 1:
                return a2y;
        }
        return 0;
    }
//
//    @Override
//    protected FlatSegment flatten(FlatSegment head, float tOff, float tSpan)
//    {
//        if (getHullLength() <= MAX_SEG_FLATTEN_LENGTH)
//        {
//            return head.next = new FlatSegment(a3x, a3y, tOff + tSpan);
//        }
//        else
//        {
//            BezierCurve[] segs = split(.5f, null);
//            head = segs[0].flatten(head, tOff, tSpan * .5f);
//            return segs[1].flatten(head, tOff + tSpan * .5f, tSpan * .5f);
//        }
//    }

    @Override
    public int getStartX()
    {
        return a0x;
    }

    @Override
    public int getStartY() {
        return a0y;
    }

    @Override
    public int getEndX() {
        return a3x;
    }

    @Override
    public int getEndY() {
        return a3y;
    }

    @Override
    public void setStartX(int px)
    {
        this.a0x = px;
    }

    @Override
    public void setStartY(int py)
    {
        this.a0y = py;
    }

    @Override
    public void setStartKnotX(int px)
    {
        this.a1x = px;
    }

    @Override
    public void setStartKnotY(int py)
    {
        this.a1y = py;
    }

    @Override
    public void setEndKnotX(int px)
    {
        this.a2x = px;
    }

    @Override
    public void setEndKnotY(int py)
    {
        this.a2y = py;
    }

    @Override
    public void setEndX(int px)
    {
        this.a3x = px;
    }

    @Override
    public void setEndY(int py)
    {
        this.a3y = py;
    }

    @Override
    public int getStartKnotX()
    {
        return a1x;
    }

    @Override
    public int getStartKnotY()
    {
        return a1y;
    }

    @Override
    public int getEndKnotX()
    {
        return a2x;
    }

    @Override
    public int getEndKnotY()
    {
        return a2y;
    }

    @Override
    public int getHullMinX()
    {
        return Math.min(Math.min(Math.min(a0x, a1x), a2x), a3x);
    }

    @Override
    public int getHullMinY()
    {
        return Math.min(Math.min(Math.min(a0y, a1y), a2y), a3y);
    }

    @Override
    public int getHullMaxX()
    {
        return Math.max(Math.max(Math.max(a0x, a1x), a2x), a3x);
    }

    @Override
    public int getHullMaxY()
    {
        return Math.max(Math.max(Math.max(a0y, a1y), a2y), a3y);
    }

    /**
     * Aproximate this cubic curve with a single quadratic.
     *
     * @return
     */
    public BezierCurveQuadratic midpointAprox()
    {
        //A quadratic aproximation of this cubic.  Control point is
        // chosen as average(a0 + 3/2(a1 - a0), a3 + 3/2(a3 - a2))
        //http://www.caffeineowl.com/graphics/2d/vectorial/cubic2quad01.html
        return new BezierCurveQuadratic(a0x, a0y,
                (-a0x + 3 * a1x + 3 * a2x - a3x) / 4,
                (-a0y + 3 * a1y + 3 * a2y - a3y) / 4,
                a3x, a3y);
    }

//    /**
//     * Calculates an upper bound on the flatness of the curve.  Given
//     * a bezier B(t) and a line segment L(t) where B(t) and L(t) have the
//     * same starting and ending points, the 'flatness' is defined as
//     * max(B(t) - L(t)), t = [0 1]
//     *
//     * Piecewise Approximation of Linear Curves
//     * Kaspar Fischer
//     * Oct 16, 2000
//     *
//     * @return
//     */
//    public double getFlatnessSquared()
//    {
//        int ux = 3 * a1x - 2 * a0x - a3x;
//        int uy = 3 * a1y - 2 * a0y - a3y;
//        int vx = 3 * a2x - 2 * a3x - a0x;
//        int vy = 3 * a2y - 2 * a3y - a0y;
//        ux *= ux;
//        uy *= uy;
//        vx *= vx;
//        vy *= vy;
//        return (Math.max(ux, vx) + Math.max(uy, vy)) / 16.0;
//    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BezierCurveCubic other = (BezierCurveCubic) obj;
        if (this.a0x != other.a0x) {
            return false;
        }
        if (this.a0y != other.a0y) {
            return false;
        }
        if (this.a1x != other.a1x) {
            return false;
        }
        if (this.a1y != other.a1y) {
            return false;
        }
        if (this.a2x != other.a2x) {
            return false;
        }
        if (this.a2y != other.a2y) {
            return false;
        }
        if (this.a3x != other.a3x) {
            return false;
        }
        if (this.a3y != other.a3y) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.a0x;
        hash = 47 * hash + this.a0y;
        hash = 47 * hash + this.a1x;
        hash = 47 * hash + this.a1y;
        hash = 47 * hash + this.a2x;
        hash = 47 * hash + this.a2y;
        hash = 47 * hash + this.a3x;
        hash = 47 * hash + this.a3y;
        return hash;
    }

    /**
     * @return the a0x
     */
    public int getA0x()
    {
        return a0x;
    }

    /**
     * @param a0x the a0x to set
     */
    public void setA0x(int a0x)
    {
        this.a0x = a0x;
    }

    /**
     * @return the a0y
     */
    public int getA0y()
    {
        return a0y;
    }

    /**
     * @param a0y the a0y to set
     */
    public void setA0y(int a0y)
    {
        this.a0y = a0y;
    }

    /**
     * @return the a1x
     */
    public int getA1x()
    {
        return a1x;
    }

    /**
     * @param a1x the a1x to set
     */
    public void setA1x(int a1x)
    {
        this.a1x = a1x;
    }

    /**
     * @return the a1y
     */
    public int getA1y()
    {
        return a1y;
    }

    /**
     * @param a1y the a1y to set
     */
    public void setA1y(int a1y)
    {
        this.a1y = a1y;
    }

    /**
     * @return the a2x
     */
    public int getA2x()
    {
        return a2x;
    }

    /**
     * @param a2x the a2x to set
     */
    public void setA2x(int a2x)
    {
        this.a2x = a2x;
    }

    /**
     * @return the a2y
     */
    public int getA2y()
    {
        return a2y;
    }

    /**
     * @param a2y the a2y to set
     */
    public void setA2y(int a2y)
    {
        this.a2y = a2y;
    }

    /**
     * @return the a3x
     */
    public int getA3x()
    {
        return a3x;
    }

    /**
     * @param a3x the a3x to set
     */
    public void setA3x(int a3x)
    {
        this.a3x = a3x;
    }

    /**
     * @return the a3y
     */
    public int getA3y()
    {
        return a3y;
    }

    /**
     * @param a3y the a3y to set
     */
    public void setA3y(int a3y)
    {
        this.a3y = a3y;
    }

    @Override
    public void appendToPath(Path2D.Double path)
    {
        path.curveTo(a1x, a1y, a2x, a2y, a3x, a3y);
    }

    @Override
    public String toString()
    {
        return String.format("(%d %d, %d %d, %d %d, %d %d)",
                a0x, a0y,
                a1x, a1y,
                a2x, a2y,
                a3x, a3y
                );
    }

    @Override
    public String toMatlab()
    {
        return String.format("plot2d([%d %d %d %d], [%d %d %d %d], -1)",
                a0x, a1x, a2x, a3x,
                a0y, a1y, a2y, a3y
                );
    }

    @Override
    public String toSVGPath()
    {
        return "M " + a0x + " " + a0y
                + "C " + a1x + " " + a1y
                + " " + a2x + " " + a2y
                + " " + a3x + " " + a3y;
    }

}
