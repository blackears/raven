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
 *
 * @author kitfox
 */
@Deprecated
public class BezierCurveQuadratic extends BezierCurve
{
    int a0x;
    int a0y;
    int a1x;
    int a1y;
    int a2x;
    int a2y;

    public BezierCurveQuadratic(double a0x, double a0y, double a1x, double a1y, double a2x, double a2y)
    {
        this(
            (int)a0x, (int)a0y,
            (int)a1x, (int)a1y,
            (int)a2x, (int)a2y
            );
    }

    public BezierCurveQuadratic(float a0x, float a0y, float a1x, float a1y, float a2x, float a2y)
    {
        this(
            (int)a0x, (int)a0y,
            (int)a1x, (int)a1y,
            (int)a2x, (int)a2y
            );
    }

    public BezierCurveQuadratic(int a0x, int a0y, int a1x, int a1y, int a2x, int a2y)
    {
        this.a0x = a0x;
        this.a0y = a0y;
        this.a1x = a1x;
        this.a1y = a1y;
        this.a2x = a2x;
        this.a2y = a2y;
    }

    public BezierCurveQuadratic(BezierCurveQuadratic other)
    {
        this(
                other.a0x, other.a0y,
                other.a1x, other.a1y,
                other.a2x, other.a2y
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

        int area = Math.abs(dx1 * dy0 - dx0 * dy1) / 2;
        return area;
    }

    @Override
    public BezierCurveQuadratic copy()
    {
        return new BezierCurveQuadratic(this);
    }

    @Override
    public BezierCurveLine getDerivative()
    {
        return new BezierCurveLine(2 * (a1x - a0x),
                2 * (a1y - a0y),
                2 * (a2x - a1x),
                2 * (a2y - a1y));
    }

    @Override
    public double getHullLength()
    {
        return distance(a0x, a0y, a1x, a1y)
                + distance(a1x, a1y, a2x, a2y);
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

        double c0x = lerp(b0x, b1x, t);
        double c0y = lerp(b0y, b1y, t);

        point[0] = c0x;
        point[1] = c0y;

        return point;
    }

    @Override
    public double calcPointX(double t)
    {
        double b0x = lerp(a0x, a1x, t);
        double b1x = lerp(a1x, a2x, t);

        return lerp(b0x, b1x, t);
    }

    @Override
    public double calcPointY(double t)
    {
        double b0y = lerp(a0y, a1y, t);
        double b1y = lerp(a1y, a2y, t);

        return lerp(b0y, b1y, t);
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

        tan[0] = b1x - b0x;
        tan[1] = b1y - b0y;
        
        return tan;
    }

    @Override
    public BezierCurveQuadratic reverse()
    {
        return new BezierCurveQuadratic(a2x, a2y, a1x, a1y, a0x, a0y);
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

        double c0x = lerp(b0x, b1x, t);
        double c0y = lerp(b0y, b1y, t);

        segs[0] = new BezierCurveQuadratic(a0x, a0y, b0x, b0y, c0x, c0y);
        segs[1] = new BezierCurveQuadratic(c0x, c0y, b1x, b1y, a2x, a2y);

        return segs;
    }

    @Override
    public int getNumKnots()
    {
        return 1;
    }

    @Override
    public int getKnotX(int index)
    {
        switch (index)
        {
            case 0:
                return a1x;
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
        }
        return 0;
    }
//
//    @Override
//    public double getFlatnessSquared()
//    {
//        int vx = a2x - a0x;
//        int vy = a2y - a0y;
//        int kx = a1x - a0x;
//        int ky = a1y - a0y;
//
//        //Project knot vector onto base
//        int dotVK = vx * kx + vy * ky;
//        int dotKK = kx * kx + ky * ky;
//        double scalar = (double)dotVK / dotKK;
//
//        double wx = scalar * vx;
//        double wy = scalar * vy;
//
//        return square(wx - kx) + square(wy - ky);
//    }

//    @Override
//    protected FlatSegment flatten(FlatSegment head, double tOff, double tSpan)
//    {
//        if (getHullLength() <= MAX_SEG_FLATTEN_LENGTH)
//        {
//            return head.next = new FlatSegment(a2x, a2y, tOff + tSpan);
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
        return a2x;
    }

    @Override
    public int getEndY() {
        return a2y;
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
    public void setEndX(int px)
    {
        this.a2x = px;
    }

    @Override
    public void setEndY(int py)
    {
        this.a2y = py;
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
        return a1x;
    }

    @Override
    public int getEndKnotY()
    {
        return a1y;
    }

    @Override
    public void setStartKnotX(int x)
    {
        a1x = x;
    }

    @Override
    public void setStartKnotY(int y)
    {
        a1y = y;
    }

    @Override
    public void setEndKnotX(int x)
    {
        a1x = x;
    }

    @Override
    public void setEndKnotY(int y)
    {
        a1y = y;
    }

    @Override
    public int getHullMinX()
    {
        return Math.min(Math.min(a0x, a1x), a2x);
    }

    @Override
    public int getHullMinY()
    {
        return Math.min(Math.min(a0y, a1y), a2y);
    }

    @Override
    public int getHullMaxX()
    {
        return Math.max(Math.max(a0x, a1x), a2x);
    }

    @Override
    public int getHullMaxY()
    {
        return Math.max(Math.max(a0y, a1y), a2y);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BezierCurveQuadratic other = (BezierCurveQuadratic) obj;
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
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.a0x;
        hash = 89 * hash + this.a0y;
        hash = 89 * hash + this.a1x;
        hash = 89 * hash + this.a1y;
        hash = 89 * hash + this.a2x;
        hash = 89 * hash + this.a2y;
        return hash;
    }

    @Override
    public void appendToPath(Path2D.Double path)
    {
        path.quadTo(a1x, a1y, a2x, a2y);
    }

    @Override
    public String toString()
    {
        return String.format("(%d %d, %d %d, %d %d)",
                a0x, a0y,
                a1x, a1y,
                a2x, a2y
                );
    }

    @Override
    public String toMatlab()
    {
        return String.format("plot2d([%d %d %d], [%d %d %d], -1)",
                a0x, a1x, a2x,
                a0y, a1y, a2y
                );
    }

    @Override
    public String toSVGPath()
    {
        return "M " + a0x + " " + a0y
                + "Q " + a1x + " " + a1y
                + " " + a2x + " " + a2y;
    }

}
