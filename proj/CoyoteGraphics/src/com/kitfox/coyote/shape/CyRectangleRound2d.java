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

/**
 *
 * @author kitfox
 */
public class CyRectangleRound2d extends CyRectangle2d
{
    private double rx;
    private double ry;

    public CyRectangleRound2d()
    {
    }

    public CyRectangleRound2d(CyRectangleRound2d rect)
    {
        this(rect.x, rect.y, rect.width, rect.height, rect.rx, rect.ry);
    }

    public CyRectangleRound2d(double x, double y,
            double width, double height,
            double rx, double ry)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rx = rx;
        this.ry = ry;
    }

    /**
     * @return the rx
     */
    public double getRx()
    {
        return rx;
    }

    /**
     * @param rx the rx to set
     */
    public void setRx(double rx)
    {
        this.rx = rx;
    }

    /**
     * @return the ry
     */
    public double getRy()
    {
        return ry;
    }

    /**
     * @param ry the ry to set
     */
    public void setRy(double ry)
    {
        this.ry = ry;
    }

    @Override
    public CyPathIterator getIterator()
    {
        if (rx <= 0 || ry <= 0)
        {
            return super.getIterator();
        }

        return new CyRectRoundIterator();
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
        final CyRectangleRound2d other = (CyRectangleRound2d) obj;
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
        if (Double.doubleToLongBits(this.rx) != Double.doubleToLongBits(other.rx))
        {
            return false;
        }
        if (Double.doubleToLongBits(this.ry) != Double.doubleToLongBits(other.ry))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.width) ^ (Double.doubleToLongBits(this.width) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.height) ^ (Double.doubleToLongBits(this.height) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.rx) ^ (Double.doubleToLongBits(this.rx) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.ry) ^ (Double.doubleToLongBits(this.ry) >>> 32));
        return hash;
    }

    //------------------------------------
    // ArcIterator.btan(Math.PI/2)
    public static final double tangentLen = 0.5522847498307933 / 2;
    
    public class CyRectRoundIterator implements CyPathIterator
    {
        int step;
        double innerDx;
        double innerDy;
        double x0;
        double x1;
        double x2;
        double x3;
        double y0;
        double y1;
        double y2;
        double y3;

        public CyRectRoundIterator()
        {
            innerDx = width - 2 * rx;
            innerDy = height - 2 * ry;

            x0 = x;
            x1 = innerDx > 0 ? x + rx : x + width / 2;
            x2 = innerDx > 0 ? x + rx + innerDx : x + width / 2;
            x3 = x + width;
            y0 = y;
            y1 = innerDy > 0 ? y + ry : y + height / 2;
            y2 = innerDy > 0 ? y + ry + innerDy : y + height / 2;
            y3 = y + height;
        }

        @Override
        public boolean hasNext()
        {
            return step < 9;
        }

        @Override
        public Type next(double[] coords)
        {
            switch (step++)
            {
                case 0:
                    //Start NW
                    coords[0] = x0;
                    coords[1] = y1;
                    return Type.MOVETO;
                case 1:
                    //NW
                    coords[0] = x0;
                    coords[1] = y1 - ry * tangentLen;
                    coords[2] = x1 - rx * tangentLen;
                    coords[3] = y0;
                    coords[4] = x1;
                    coords[5] = y0;
                    return Type.CUBICTO;
                case 2:
                    //N
                    if (innerDx <= 0)
                    {
                        //Skip if side length too small
                        return next(coords);
                    }
                    coords[0] = x2;
                    coords[1] = y0;
                    return Type.LINETO;
                case 3:
                    //NE
                    coords[0] = x2 + rx * tangentLen;
                    coords[1] = y0;
                    coords[2] = x3;
                    coords[3] = y1 - ry * tangentLen;
                    coords[4] = x3;
                    coords[5] = y1;
                    return Type.CUBICTO;
                case 4:
                    //E
                    if (innerDy <= 0)
                    {
                        //Skip if side length too small
                        return next(coords);
                    }
                    coords[0] = x3;
                    coords[1] = y2;
                    return Type.LINETO;
                case 5:
                    //SE
                    coords[0] = x3;
                    coords[1] = y2 + ry * tangentLen;
                    coords[2] = x2 + rx * tangentLen;
                    coords[3] = y3;
                    coords[4] = x2;
                    coords[5] = y3;
                    return Type.CUBICTO;
                case 6:
                    //S
                    if (innerDx <= 0)
                    {
                        //Skip if side length too small
                        return next(coords);
                    }
                    coords[0] = x1;
                    coords[1] = y3;
                    return Type.LINETO;
                case 7:
                    //SE
                    coords[0] = x1 - rx * tangentLen;
                    coords[1] = y3;
                    coords[2] = x0;
                    coords[3] = y2 + ry * tangentLen;
                    coords[4] = x0;
                    coords[5] = y2;
                    return Type.CUBICTO;
                case 8:
                    //W
                    if (innerDy <= 0)
                    {
                        //Skip if side length too small
                        return next(coords);
                    }
                    coords[0] = x0;
                    coords[1] = y1;
                    return Type.LINETO;
                case 9:
                    return Type.CLOSE;
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }

}
