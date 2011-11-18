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

package com.kitfox.coyote.shape;

import java.awt.geom.Ellipse2D;

/**
 *
 * @author kitfox
 */
public class CyEllipse2d extends CyShape
{
    private double x;
    private double y;
    private double width;
    private double height;

    public CyEllipse2d()
    {
    }

    public CyEllipse2d(CyEllipse2d ellipse)
    {
        this(ellipse.x, ellipse.y, ellipse.width, ellipse.height);
    }

    public CyEllipse2d(double x, double y, double width, double height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getMinX()
    {
        return x;
    }

    public double getMaxX()
    {
        return x + width;
    }

    public double getMinY()
    {
        return y;
    }

    public double getMaxY()
    {
        return y + height;
    }

    /**
     * @return the x
     */
    public double getX()
    {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(double x)
    {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY()
    {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(double y)
    {
        this.y = y;
    }

    /**
     * @return the width
     */
    public double getWidth()
    {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(double width)
    {
        this.width = width;
    }

    /**
     * @return the height
     */
    public double getHeight()
    {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(double height)
    {
        this.height = height;
    }

    @Override
    public String toString()
    {
        return "(" + x + ", " + y + ", " + width + ", " + height + ")";
    }

    @Override
    public CyPathIterator getIterator()
    {
        return new CyEllipseIterator();
    }

    @Override
    public CyRectangle2d getBounds()
    {
        return new CyRectangle2d(x, y, width, height);
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
        final CyEllipse2d other = (CyEllipse2d) obj;
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x))
        {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y))
        {
            return false;
        }
        if (Double.doubleToLongBits(this.width) != Double.doubleToLongBits(other.width))
        {
            return false;
        }
        if (Double.doubleToLongBits(this.height) != Double.doubleToLongBits(other.height))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.width) ^ (Double.doubleToLongBits(this.width) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.height) ^ (Double.doubleToLongBits(this.height) >>> 32));
        return hash;
    }

    // ArcIterator.btan(Math.PI/2)
    public static final double tangentLen = 0.5522847498307933 / 2;

    //------------------------------------
    public class CyEllipseIterator implements CyPathIterator
    {
        int step;

        @Override
        public boolean hasNext()
        {
            return step < 6;
        }

        @Override
        public Type next(double[] coords)
        {
            double px0 = x + width;
            double py0 = y + height / 2;
            double px1 = x + width / 2;
            double py1 = y + height;
            double px2 = x;
            double py2 = y + height / 2;
            double px3 = x + width / 2;
            double py3 = y;

            Ellipse2D p;

            switch (step++)
            {
                case 0:
                    coords[0] = px0;
                    coords[1] = py0;
                    return Type.MOVETO;
                case 1:
                    coords[0] = px0;
                    coords[1] = py0 + height * tangentLen;
                    coords[2] = px1 + width * tangentLen;
                    coords[3] = py1;
                    coords[4] = px1;
                    coords[5] = py1;
                    return Type.CUBICTO;
                case 2:
                    coords[0] = px1 - width * tangentLen;
                    coords[1] = py1;
                    coords[2] = px2;
                    coords[3] = py2 + height * tangentLen;
                    coords[4] = px2;
                    coords[5] = py2;
                    return Type.CUBICTO;
                case 3:
                    coords[0] = px2;
                    coords[1] = py2 - height * tangentLen;
                    coords[2] = px3 - width * tangentLen;
                    coords[3] = py3;
                    coords[4] = px3;
                    coords[5] = py3;
                    return Type.CUBICTO;
                case 4:
                    coords[0] = px3 + width * tangentLen;
                    coords[1] = py3;
                    coords[2] = px0;
                    coords[3] = py0 - height * tangentLen;
                    coords[4] = px0;
                    coords[5] = py0;
                    return Type.CUBICTO;
                case 5:
                    return Type.CLOSE;
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }
}
