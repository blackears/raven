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
public class BezierCurveLine extends BezierCurve
{
    int a0x;
    int a0y;
    int a1x;
    int a1y;

    public BezierCurveLine(double a0x, double a0y, double a1x, double a1y)
    {
        this(
            (int)a0x, (int)a0y,
            (int)a1x, (int)a1y
            );
    }

    public BezierCurveLine(float a0x, float a0y, float a1x, float a1y)
    {
        this(
            (int)a0x, (int)a0y,
            (int)a1x, (int)a1y
            );
    }

    public BezierCurveLine(int a0x, int a0y, int a1x, int a1y)
    {
        this.a0x = a0x;
        this.a0y = a0y;
        this.a1x = a1x;
        this.a1y = a1y;
    }

    public BezierCurveLine(BezierCurveLine other)
    {
        this(
                other.a0x, other.a0y,
                other.a1x, other.a1y
                );
    }

    @Override
    public double getFlatnessSquared()
    {
        return 0;
    }

    @Override
    public BezierCurveLine copy()
    {
        return new BezierCurveLine(this);
    }

    @Override
    public double getHullLength()
    {
        return distance(a0x, a0y, a1x, a1y);
    }

    @Override
    public BezierCurvePoint getDerivative()
    {
        return new BezierCurvePoint(a1x - a0x, a1y - a0y);
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

        point[0] = b0x;
        point[1] = b0y;

        return point;
    }

    @Override
    public double calcPointX(double t)
    {
        return lerp(a0x, a1x, t);
    }

    @Override
    public double calcPointY(double t)
    {
        return lerp(a0y, a1y, t);
    }

    @Override
    public BezierCurveLine reverse()
    {
        return new BezierCurveLine(a1x, a1y, a0x, a0y);
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

        segs[0] = new BezierCurveLine(a0x, a0y, b0x, b0y);
        segs[1] = new BezierCurveLine(b0x, b0y, a1x, a1y);

        return segs;
    }

    @Override
    public int getNumKnots()
    {
        return 0;
    }

    @Override
    public int getKnotX(int index)
    {
        return 0;
    }

    @Override
    public int getKnotY(int index)
    {
        return 0;
    }
//    @Override
//    public double getFlatnessSquared()
//    {
//        return 0;
//    }
//
//    @Override
//    protected FlatSegment flatten(FlatSegment head, float tOff, float tSpan)
//    {
//        //No need to subdivide a straight line
//        return head.next = new FlatSegment(a1x, a1y, tOff + tSpan);
//
////        if (getHullLength() <= MAX_SEG_FLATTEN_LENGTH)
////        {
////            return head.next = new FlatSegment(a1x, a1y, tOff + tSpan);
////        }
////        else
////        {
////            BezierCurve[] segs = split(.5f, null);
////            head = segs[0].flatten(head, tOff, tSpan * .5f);
////            return segs[1].flatten(head, tOff + tSpan * .5f, tSpan * .5f);
////        }
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
        return a1x;
    }

    @Override
    public int getEndY() {
        return a1y;
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
        this.a1x = px;
    }

    @Override
    public void setEndY(int py)
    {
        this.a1y = py;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BezierCurveLine other = (BezierCurveLine) obj;
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
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.a0x;
        hash = 67 * hash + this.a0y;
        hash = 67 * hash + this.a1x;
        hash = 67 * hash + this.a1y;
        return hash;
    }

    @Override
    public int getStartKnotX()
    {
        return a0x + (a1x - a0x) / 3;
    }

    @Override
    public int getStartKnotY()
    {
        return a0y + (a1y - a0y) / 3;
    }

    @Override
    public int getEndKnotX()
    {
        return a0x + (a1x - a0x) * (2 / 3);
    }

    @Override
    public int getEndKnotY()
    {
        return a0y + (a1y - a0y) * (2 / 3);
    }

    @Override
    public void setStartKnotX(int x)
    {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setStartKnotY(int y)
    {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setEndKnotX(int x)
    {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setEndKnotY(int y)
    {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getHullMinX()
    {
        return Math.min(a0x, a1x);
    }

    @Override
    public int getHullMinY()
    {
        return Math.min(a0y, a1y);
    }

    @Override
    public int getHullMaxX()
    {
        return Math.max(a0x, a1x);
    }

    @Override
    public int getHullMaxY()
    {
        return Math.max(a0y, a1y);
    }

    @Override
    public double[] getTangent(double t, double[] tan)
    {
        if (tan == null)
        {
            tan = new double[2];
        }
        tan[0] = a1x - a0x;
        tan[1] = a1y - a0y;
        return tan;
    }

    @Override
    public void appendToPath(Path2D.Double path)
    {
        path.lineTo(a1x, a1y);
    }

    @Override
    public String toString()
    {
        return String.format("(%d %d, %d %d)",
                a0x, a0y,
                a1x, a1y
                );
    }

    @Override
    public String toMatlab()
    {
        return String.format("plot2d([%d %d], [%d %d], -1)",
                a0x, a1x,
                a0y, a1y
                );
    }

    @Override
    public String toSVGPath()
    {
        return "M " + a0x + " " + a0y
                + "L " + a1x + " " + a1y;
    }
}
