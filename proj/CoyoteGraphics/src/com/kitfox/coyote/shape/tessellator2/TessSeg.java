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

package com.kitfox.coyote.shape.tessellator2;

import com.kitfox.coyote.material.textureBlit.CyMaterialTextureBlit;
import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class TessSeg
{
    final Coord c0;
    final Coord c1;

    public TessSeg(Coord p0, Coord p1)
    {
        this.c0 = p0;
        this.c1 = p1;
    }

    public TessSeg reverse()
    {
        return new TessSeg(c1, c0);
    }
    
    public double getMinX()
    {
        return Math.min(c0.x, c1.x);
    }
    
    public double getMinY()
    {
        return Math.min(c0.y, c1.y);
    }

    public double getMaxX()
    {
        return Math.max(c0.x, c1.x);
    }
    
    public double getMaxY()
    {
        return Math.max(c0.y, c1.y);
    }
    
    boolean isBoundingBoxOverlap(TessSeg c)
    {
        return c.getMaxX() >= getMinX()
                && c.getMinX() <= getMaxX()
                && c.getMaxY() >= getMinY()
                && c.getMinY() <= getMaxY();
    }
    
    public int getDx()
    {
        return c1.x - c0.x;
    }

    public int getDy()
    {
        return c1.y - c0.y;
    }

    public boolean isParallelTo(TessSeg s)
    {
        int dx0 = c1.x - c0.x;
        int dy0 = c1.y - c0.y;
        int dx1 = s.c1.x - s.c0.x;
        int dy1 = s.c1.y - s.c0.y;
        return dx0 * dy1 == dy0 * dx1;
    }

    public boolean isPointOnLine(Coord c)
    {
        return isPointOnLine(c.x, c.y);
    }
    
    public boolean isPointOnLine(int x, int y)
    {
        int dx0 = c1.x - c0.x;
        int dy0 = c1.y - c0.y;
        int dx1 = x - c0.x;
        int dy1 = y - c0.y;
        return dx0 * dy1 == dy0 * dx1;
    }

    public boolean isColinearWith(TessSeg s)
    {
        return isParallelTo(s) && isPointOnLine(s.c0.x, s.c0.y);
    }

    public double pointOnLineT(Coord c)
    {
        return pointOnLineT(c.x, c.y);
    }
    
    /**
     * For a point (c0.x, c0.y) which lies on this line segment,
     * returns t value.
     * 
     * Note that (c0.x, c0.y) must be a point which lies on this line
     * and which returns true for isPointOnLine().
     * 
     * @param c0.x
     * @param c0.y
     * @return 
     */
    public double pointOnLineT(int x, int y)
    {
        if (x == c0.x && y == c0.y)
        {
            return 0;
        }
        if (x == c1.x && y == c1.y)
        {
            return 1;
        }
        
        int dx = c1.x - c0.x;
        int dy = c1.y - c0.y;
        
        if (Math.abs(dx) > Math.abs(dy))
        {
            return (x - c0.x) / (double)dx;
        }
        else
        {
            return (y - c0.y) / (double)dy;
        }
    }

    /**
     * Compare this segment to passed in segment.  If they cross, 
     * returns list containing new set of segments cut at
     * points of touching or crossing.
     * 
     * @param s1
     * @return List of cut points, or null if lines to not touch.
     */
    public ArrayList<TessSeg> splitAtIsect(TessSeg s1)
    {
        if (!isLineHit(s1))
        {
            return null;
        }
        
        //Check for point-line hits
        double t00 = -1, t01 = -1, t10 = -1, t11 = -1;
        boolean pointMidlineHit = false;
        
        if (isPointOnLine(s1.c0))
        {
            double t = pointOnLineT(s1.c0);
            if (t > 0 && t < 1)
            {
                t00 = t;
                pointMidlineHit = true;
            }
        }
        
        if (isPointOnLine(s1.c1))
        {
            double t = pointOnLineT(s1.c1);
            if (t > 0 && t < 1)
            {
                t01 = t;
                pointMidlineHit = true;
            }
        }
        
        if (s1.isPointOnLine(c0))
        {
            double t = s1.pointOnLineT(c0);
            if (t > 0 && t < 1)
            {
                t10 = t;
                pointMidlineHit = true;
            }
        }
        
        if (s1.isPointOnLine(c1))
        {
            double t = s1.pointOnLineT(c1);
            if (t > 0 && t < 1)
            {
                t11 = t;
                pointMidlineHit = true;
            }
        }

        if (pointMidlineHit)
        {
            ArrayList<TessSeg> list = new ArrayList<TessSeg>();
            if (t00 > 0 && t01 > 0)
            {
                if (t00 < t01)
                {
                    list.add(new TessSeg(c0, s1.c0));
                    list.add(new TessSeg(s1.c0, s1.c1));
                    list.add(new TessSeg(s1.c1, c1));
                }
                else
                {
                    list.add(new TessSeg(c0, s1.c1));
                    list.add(new TessSeg(s1.c1, s1.c0));
                    list.add(new TessSeg(s1.c0, c1));
                }
            }
            else if (t00 > 0)
            {
                list.add(new TessSeg(c0, s1.c0));
                list.add(new TessSeg(s1.c0, c1));
            }
            else if (t01 > 0)
            {
                list.add(new TessSeg(c0, s1.c1));
                list.add(new TessSeg(s1.c1, c1));
            }
            else
            {
                list.add(this);
            }
            
            if (t10 > 0 && t11 > 0)
            {
                if (t10 < t11)
                {
                    list.add(new TessSeg(s1.c0, c0));
                    list.add(new TessSeg(c0, c1));
                    list.add(new TessSeg(c1, s1.c1));
                }
                else
                {
                    list.add(new TessSeg(s1.c0, c1));
                    list.add(new TessSeg(c1, c0));
                    list.add(new TessSeg(c0, s1.c1));
                }
            }
            else if (t10 > 0)
            {
                list.add(new TessSeg(s1.c0, c0));
                list.add(new TessSeg(c0, s1.c1));
            }
            else if (t11 > 0)
            {
                list.add(new TessSeg(s1.c0, c1));
                list.add(new TessSeg(c1, s1.c1));
            }
            else
            {
                list.add(s1);
            }
            
            return list;
        }
        
        if (c0.equals(s1.c0) 
                || c0.equals(s1.c1)
                || c1.equals(s1.c0)
                || c1.equals(s1.c1))
        {
            //No point-midline hits.  If we only meet at verts,
            // do not split.
            return null;
        }
        
//        if (!isParallelTo(s1))
        {
            //Midpoint crossing for both segments.
            // Solve system of linear eqns
            double s0x0 = c0.x;
            double s0y0 = c0.y;
            double s0x1 = c1.x;
            double s0y1 = c1.y;
            double s1x0 = s1.c0.x;
            double s1y0 = s1.c0.y;
            double s1x1 = s1.c1.x;
            double s1y1 = s1.c1.y;

            double[] t = Math2DUtil.lineIsectFractions(
                    s0x0, s0y0, s0x1 - s0x0, s0y1 - s0y0,
                    s1x0, s1y0, s1x1 - s1x0, s1y1 - s1y0,
                    null);

            if (t == null || t[0] < 0 || t[0] > 1 || t[1] < 0 || t[1] > 1)
            {
                Logger.getLogger(TessSeg.class.getName()).log(Level.WARNING, 
                        "Line segments do not overlap");
            }
//            assert (t[0] > 0 && t[0] < 1 && t[1] > 0 && t[1] < 1)
//                    : "Line segments do not overlap";
            
            {
                ArrayList<TessSeg> list = new ArrayList<TessSeg>();
                
                Coord c = new Coord(
                        (int)Math2DUtil.lerp(s1x0, s1x1, t[1]),
                        (int)Math2DUtil.lerp(s1y0, s1y1, t[1]));

                list.add(new TessSeg(c0, c));
                list.add(new TessSeg(c, c1));
                list.add(new TessSeg(s1.c0, c));
                list.add(new TessSeg(c, s1.c1));
                
                return list;
            }
        }
        
//        return null;
    }

    @Override
    public String toString()
    {
        return "[" + c0 + "->" + c1 + "]";
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
        final TessSeg other = (TessSeg)obj;
        if (this.c0 != other.c0 && (this.c0 == null || !this.c0.equals(other.c0)))
        {
            return false;
        }
        if (this.c1 != other.c1 && (this.c1 == null || !this.c1.equals(other.c1)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 67 * hash + (this.c0 != null ? this.c0.hashCode() : 0);
        hash = 67 * hash + (this.c1 != null ? this.c1.hashCode() : 0);
        return hash;
    }

    public boolean isZeroLength()
    {
        return c0.equals(c1);
    }

    /**
     * @return the c0
     */
    public Coord getC0()
    {
        return c0;
    }

    /**
     * @return the c1
     */
    public Coord getC1()
    {
        return c1;
    }

    /**
     * True if s1 would split this line.  Ie, isLineHit() == true and
     * the end points of s1 are not midpoints of this line.
     * 
     * @param s1
     * @return 
     */
    public boolean isLineSplitBy(TessSeg s1)
    {
        if (!isLineHit(s1))
        {
            return false;
        }
        
        if (isPointOnLine(s1.c0)
                && !c0.equals(s1.c0) && !c1.equals(s1.c0))
        {
            return true;
        }
        
        if (isPointOnLine(s1.c1)
                && !c0.equals(s1.c1) && !c1.equals(s1.c1))
        {
            return true;
        }
        return false;
    }
    
    /**
     * True if this line and s1 contain any common points.
     * 
     * @param s1
     * @return 
     */
    public boolean isLineHit(TessSeg s1)
    {
        int side00 = 
                Math2DUtil.getLineSide(s1.c0.x, s1.c0.y, s1.getDx(), s1.getDy(),
                c0.x, c0.y);
        int side01 = 
                Math2DUtil.getLineSide(s1.c0.x, s1.c0.y, s1.getDx(), s1.getDy(), 
                c1.x, c1.y);

        if (side00 == 0 && side01 == 0)
        {
            //Lines lie along same ray
            double t0 = pointOnLineT(s1.c0);
            double t1 = pointOnLineT(s1.c1);
            return ((t0 < 0 && t1 < 0) || (t0 > 1 && t1 > 1)) ? false : true;
        }
        
        if ((side00 < 0 && side01 < 0) || (side00 > 0 && side01 > 0))
        {
            return false;
        }
        
        int side10 = 
                Math2DUtil.getLineSide(c0.x, c0.y, getDx(), getDy(),
                s1.c0.x, s1.c0.y);
        int side11 = 
                Math2DUtil.getLineSide(c0.x, c0.y, getDx(), getDy(), 
                s1.c1.x, s1.c1.y);

        if ((side10 < 0 && side11 < 0) || (side10 > 0 && side11 > 0))
        {
            return false;
        }

        return true;
        
        /*
        if (!isBoundingBoxOverlap(s1))
        {
            return false;
        }
        
        if (isParallelTo(s1))
        {
            if (!isPointOnLine(s1.c0))
            {
                return false;
            }
            
            double t0 = pointOnLineT(s1.c0);
            double t1 = pointOnLineT(s1.c1);
            
            if ((t0 <= 0 && t1 <= 0) || (t0 >= 1 && t1 >= 1))
            {
                return false;
            }
            return true;
        }
        
        if (isPointOnLine(s1.c0))
        {
            double t = pointOnLineT(s1.c0);
            if (t > 0 && t < 1)
            {
                return true;
            }
        }
        
        if (isPointOnLine(s1.c1))
        {
            double t = pointOnLineT(s1.c1);
            if (t > 0 && t < 1)
            {
                return true;
            }
        }
        
        if (s1.isPointOnLine(c0))
        {
            double t = s1.pointOnLineT(c0);
            if (t > 0 && t < 1)
            {
                return true;
            }
        }
        
        if (s1.isPointOnLine(c1))
        {
            double t = s1.pointOnLineT(c1);
            if (t > 0 && t < 1)
            {
                return true;
            }
        }
        
        //Solve system of linear eqns
        double s0x0 = c0.x;
        double s0y0 = c0.y;
        double s0x1 = c1.x;
        double s0y1 = c1.y;
        double s1x0 = s1.c0.x;
        double s1y0 = s1.c0.y;
        double s1x1 = s1.c1.x;
        double s1y1 = s1.c1.y;

        double[] t = Math2DUtil.lineIsectFractions(
                s0x0, s0y0, s0x1 - s0x0, s0y1 - s0y0,
                s1x0, s1y0, s1x1 - s1x0, s1y1 - s1y0,
                null);

        if (t[0] > 0 && t[0] < 1 && t[1] > 0 && t[1] < 1)
        {
            return true;
        }
        
        return false;
        */
    }
    
}
