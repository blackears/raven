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

import com.kitfox.coyote.math.CyVector2d;
import static java.lang.Math.*;

/**
 *
 * @author kitfox
 */
public class CyRectangle2d extends CyShape
{
    protected double x;
    protected double y;
    protected double width;
    protected double height;

    public CyRectangle2d()
    {
    }

    public CyRectangle2d(CyRectangle2d rect)
    {
        this(rect.x, rect.y, rect.width, rect.height);
    }

    public CyRectangle2d(double x, double y, double width, double height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void union(double x, double y)
    {
        double minX = min(getMinX(), x);
        double maxX = max(getMaxX(), x);
        double minY = min(getMinY(), y);
        double maxY = max(getMaxY(), y);

        x = minX;
        y = minY;
        width = maxX - minX;
        height = maxY - minY;
    }

    public void union(CyRectangle2d rect)
    {
        double minX = min(getMinX(), rect.getMinX());
        double maxX = max(getMaxX(), rect.getMaxX());
        double minY = min(getMinY(), rect.getMinY());
        double maxY = max(getMaxY(), rect.getMaxY());

        x = minX;
        y = minY;
        width = maxX - minX;
        height = maxY - minY;
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

    public double getCenterX()
    {
        return x + width / 2;
    }

    public double getCenterY()
    {
        return y + height / 2;
    }

    @Override
    public String toString()
    {
        return "(" + x + ", " + y + ", " + width + ", " + height + ")";
    }

    @Override
    public CyPathIterator getIterator()
    {
        return new CyRectIterator();
    }

    @Override
    public CyRectangle2d getBounds()
    {
        return new CyRectangle2d(this);
    }

    @Override
    public boolean contains(double x, double y)
    {
        return x >= getMinX() && x <= getMaxX()
                && y >= getMinY() && y < getMaxY();
    }

    @Override
    public boolean contains(CyRectangle2d rect)
    {
        return rect.getMinX() >= getMinX()
                && rect.getMaxX() <= getMaxX()
                && rect.getMinY() >= getMinY()
                && rect.getMaxY() <= getMaxY();
    }

    @Override
    public boolean contains(CyVector2d p)
    {
        return contains(p.x, p.y);
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
        final CyRectangle2d other = (CyRectangle2d) obj;
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
        int hash = 5;
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.width) ^ (Double.doubleToLongBits(this.width) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.height) ^ (Double.doubleToLongBits(this.height) >>> 32));
        return hash;
    }


    //------------------------------------
    public class CyRectIterator implements CyPathIterator
    {
        int step;

        @Override
        public boolean hasNext()
        {
            return step < 5;
        }

        @Override
        public Type next(double[] coords)
        {
            switch (step++)
            {
                case 0:
                    coords[0] = x;
                    coords[1] = y;
                    return Type.MOVETO;
                case 1:
                    coords[0] = x + width;
                    coords[1] = y;
                    return Type.LINETO;
                case 2:
                    coords[0] = x + width;
                    coords[1] = y + height;
                    return Type.LINETO;
                case 3:
                    coords[0] = x;
                    coords[1] = y + height;
                    return Type.LINETO;
                case 4:
                    return Type.CLOSE;
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }
}
