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

package com.kitfox.coyote.shape.bezier.mesh;

import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class Segment
{
    final double t0;
    final double t1;
    final Coord c0;
    final Coord c1;

    public Segment(double t0, double t1, Coord c0, Coord c1)
    {
        this.t0 = t0;
        this.t1 = t1;
        this.c0 = c0;
        this.c1 = c1;
    }

    public boolean isParallelTo(Segment s)
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

    public boolean isColinearWith(Segment s)
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

    public CutRecord findCuts(Segment s1)
    {
        if (isParallelTo(s1))
        {
            if (isPointOnLine(s1.c0))
            {
                ArrayList<CutPoint> cuts0 = new ArrayList<CutPoint>();
                ArrayList<CutPoint> cuts1 = new ArrayList<CutPoint>();
                
                //Lines are coincident
                double s0t0 = s1.pointOnLineT(c0);
                double s0t1 = s1.pointOnLineT(c1);
                double s1t0 = pointOnLineT(s1.c0);
                double s1t1 = pointOnLineT(s1.c1);

                //If inside neighboring line, emit point
                if (s1t0 >= 0 && s1t0 <= 1)
                {
                    cuts0.add(new CutPoint(s1t0, s1.c0));
                }

                if (s1t1 >= 0 && s1t1 <= 1)
                {
                    cuts0.add(new CutPoint(s1t1, s1.c1));
                }

                if (s0t0 >= 0 && s0t0 <= 1)
                {
                    cuts1.add(new CutPoint(s0t0, c0));
                }

                if (s0t1 >= 0 && s0t1 <= 1)
                {
                    cuts1.add(new CutPoint(s0t1, c1));
                }
                
                if (cuts0.isEmpty() && cuts1.isEmpty())
                {
                    return null;
                }
                
                return new CutRecord(
                        cuts0.isEmpty() ? null 
                            : cuts0.toArray(new CutPoint[cuts0.size()]),
                        cuts1.isEmpty() ? null 
                            : cuts1.toArray(new CutPoint[cuts1.size()]));
            }
            else
            {
                //Lines are parallel but not coincident
                return null;
            }
        }
        else
        {
            if (isPointOnLine(s1.c0))
            {
                double t = pointOnLineT(s1.c0);

                if (t > 0 && t < 1)
                {
                    return new CutRecord(
                            new CutPoint[]{new CutPoint(t, s1.c0)},
                            new CutPoint[]{new CutPoint(0, s1.c0)});
                }
            }
            else if (isPointOnLine(s1.c1))
            {
                double t = pointOnLineT(s1.c1);

                if (t >= 0 && t <= 1)
                {
                    return new CutRecord(
                            new CutPoint[]{new CutPoint(t, s1.c1)},
                            new CutPoint[]{new CutPoint(1, s1.c1)});
                }
            }
            else if (s1.isPointOnLine(c0))
            {
                double t = s1.pointOnLineT(c0);

                if (t >= 0 && t <= 1)
                {
                    return new CutRecord(new CutPoint[]{new CutPoint(0, c0)},
                            new CutPoint[]{new CutPoint(t, c0)});
                }
            }
            else if (s1.isPointOnLine(c1))
            {
                double t = s1.pointOnLineT(c1);

                if (t >= 0 && t <= 1)
                {
                    return new CutRecord(new CutPoint[]{new CutPoint(1, c1)},
                            new CutPoint[]{new CutPoint(t, c1)});
                }
            }
            else
            {
                //No coincident points.  Solve system of 
                // linear eqns
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

                if (t[0] >= 0 && t[0] <= 1 && t[1] >= 0 && t[1] <= 1)
                {
                    Coord c = new Coord(
                            (int)Math2DUtil.lerp(s1x0, s1x1, t[1]),
                            (int)Math2DUtil.lerp(s1y0, s1y1, t[1]));
                    
                    CutPoint[] cuts0 = c.equals(c0) || c.equals(c1)
                            ? null
                            : new CutPoint[]{new CutPoint(t[0], c)};
                    CutPoint[] cuts1 = c.equals(s1.c0) || c.equals(s1.c1)
                            ? null
                            : new CutPoint[]{new CutPoint(t[1], c)};
                    
                    return new CutRecord(cuts0, cuts1);
                }
            }
        }
      
        return null;
    }
    
    //--------------------------------
    public static class CutRecord
    {
        CutPoint[] cuts0;
        CutPoint[] cuts1;

        public CutRecord(CutPoint[] cuts0, CutPoint[] cuts1)
        {
            this.cuts0 = cuts0;
            this.cuts1 = cuts1;
        }
    }
        
    public static class CutPoint implements Comparable<CutPoint>
    {
        double t; //Relative to segment
        Coord coord;

        public CutPoint(double t, Coord coord)
        {
            this.t = t;
            this.coord = coord;
        }

        @Override
        public int compareTo(CutPoint oth)
        {
            return Double.compare(t, oth.t);
        }
    }
        
}
