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

import static java.lang.Math.*;

/**
 *
 * @author kitfox
 */
public class CyRectangle2i extends CyShape
{
    private int x;
    private int y;
    private int width;
    private int height;

    public CyRectangle2i()
    {
    }

    public CyRectangle2i(CyRectangle2i bounds)
    {
        this(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public CyRectangle2i(int x, int y)
    {
        this(x, y, 0, 0);
    }

    public CyRectangle2i(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void union(int x, int y)
    {
        int minX = min(getMinX(), x);
        int maxX = max(getMaxX(), x);
        int minY = min(getMinY(), y);
        int maxY = max(getMaxY(), y);

        x = minX;
        y = minY;
        width = maxX - minX;
        height = maxY - minY;
    }

    public void union(CyRectangle2i rect)
    {
        int minX = min(getMinX(), rect.getMinX());
        int maxX = max(getMaxX(), rect.getMaxX());
        int minY = min(getMinY(), rect.getMinY());
        int maxY = max(getMaxY(), rect.getMaxY());

        x = minX;
        y = minY;
        width = maxX - minX;
        height = maxY - minY;
    }

    public int getMinX()
    {
        return x;
    }

    public int getMaxX()
    {
        return x + width;
    }

    public int getMinY()
    {
        return y;
    }

    public int getMaxY()
    {
        return y + height;
    }

    /**
     * @return the x
     */
    public int getX()
    {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x)
    {
        this.x = x;
    }

    /**
     * @return the y
     */
    public int getY()
    {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y)
    {
        this.y = y;
    }

    /**
     * @return the width
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width)
    {
        this.width = width;
    }

    /**
     * @return the height
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height)
    {
        this.height = height;
    }

    public void set(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public CyPathIterator getIterator()
    {
        return new CyRectIterator();
    }

    @Override
    public CyRectangle2d getBounds()
    {
        return new CyRectangle2d(x, y, width, height);
    }

    public boolean contains(int px, int py)
    {
        return px >= x 
                && px < x + width
                && py >= y
                && py < y + height;
    }
    
    @Override
    public String toString()
    {
        return "(" + x + ", " + y + ", " + width + ", " + height + ")";
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
        final CyRectangle2i other = (CyRectangle2i) obj;
        if (this.x != other.x)
        {
            return false;
        }
        if (this.y != other.y)
        {
            return false;
        }
        if (this.width != other.width)
        {
            return false;
        }
        if (this.height != other.height)
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 79 * hash + this.x;
        hash = 79 * hash + this.y;
        hash = 79 * hash + this.width;
        hash = 79 * hash + this.height;
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
