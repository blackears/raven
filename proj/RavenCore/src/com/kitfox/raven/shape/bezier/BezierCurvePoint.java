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

import java.awt.geom.Path2D.Double;

/**
 *
 * @author kitfox
 */
@Deprecated
public class BezierCurvePoint extends BezierCurve
{
    int a0x;
    int a0y;

    public BezierCurvePoint(int x, int y)
    {
        this.a0x = x;
        this.a0y = y;
    }

    public BezierCurvePoint(double x, double y)
    {
        this((int)x, (int)y);
    }

    public BezierCurvePoint(BezierCurvePoint other)
    {
        this(
                other.a0x, other.a0y
                );
    }

    @Override
    public double getFlatnessSquared()
    {
        return 0;
    }

    @Override
    public BezierCurvePoint copy()
    {
        return new BezierCurvePoint(this);
    }


    @Override
    public double getHullLength()
    {
        return 0;
    }

    @Override
    public double[] calcPoint(double t, double[] point)
    {
        return new double[]{a0x, a0y};
    }

    @Override
    public BezierCurve[] split(double t, BezierCurve[] segs)
    {
        return new BezierCurve[]{new BezierCurvePoint(a0x, a0y)};
    }

    @Override
    public int getNumKnots()
    {
        return -1;
    }

    @Override
    public int getKnotX(int index)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getKnotY(int index)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getStartX()
    {
        return a0x;
    }

    @Override
    public int getStartY()
    {
        return a0y;
    }

    @Override
    public int getEndX()
    {
        return a0x;
    }

    @Override
    public int getEndY()
    {
        return a0y;
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
        this.a0x = px;
    }

    @Override
    public void setEndY(int py)
    {
        this.a0y = py;
    }

    @Override
    public int getStartKnotX()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getStartKnotY()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getEndKnotX()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getEndKnotY()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setStartKnotX(int x)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setStartKnotY(int y)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setEndKnotX(int x)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setEndKnotY(int y)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getHullMinX()
    {
        return a0x;
    }

    @Override
    public int getHullMinY()
    {
        return a0y;
    }

    @Override
    public int getHullMaxX()
    {
        return a0x;
    }

    @Override
    public int getHullMaxY()
    {
        return a0y;
    }

    @Override
    public double calcPointX(double t)
    {
        return a0x;
    }

    @Override
    public double calcPointY(double t)
    {
        return a0y;
    }

    @Override
    public BezierCurvePoint reverse()
    {
        return new BezierCurvePoint(a0x, a0y);
    }

    @Override
    public BezierCurve getDerivative()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double[] getTangent(double t, double[] tan)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void appendToPath(Double path)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString()
    {
        return String.format("(%d %d)",
                a0x, a0y
                );
    }


    @Override
    public String toMatlab()
    {
        return String.format("plot2d([%d], [%d], -1)",
                a0x,
                a0y
                );
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
        final BezierCurvePoint other = (BezierCurvePoint) obj;
        if (this.a0x != other.a0x)
        {
            return false;
        }
        if (this.a0y != other.a0y)
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 59 * hash + this.a0x;
        hash = 59 * hash + this.a0y;
        return hash;
    }

    @Override
    public String toSVGPath()
    {
        return "M " + a0x + " " + a0y;
    }

}
