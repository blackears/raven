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
import com.kitfox.coyote.shape.bezier.BezierCurve2i;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class CutSegment<EdgeData>
{
    final double t0;
    final double t1;
    final Coord c0;
    final Coord c1;
    EdgeData data;

    public CutSegment(double t0, double t1, Coord c0, Coord c1, EdgeData data)
    {
        this.t0 = t0;
        this.t1 = t1;
        this.c0 = c0;
        this.c1 = c1;
        this.data = data;
    }

    public static ArrayList<CutSegment> createSegments(
            BezierCurve2i curve, Object data, double flatnessSquared, 
            ArrayList<CutSegment> list)
    {
        if (list == null)
        {
            list = new ArrayList<CutSegment>();
        }
        if (curve.isPoint())
        {
            return list;
        }
        createSegments(curve, data, flatnessSquared, 0, 1, list);
        return list;
    }

    private static void createSegments(
            BezierCurve2i curve, Object data, double flatnessSquared,
            double t0, double t1, ArrayList<CutSegment> list)
    {
        if (curve.getCurvatureSquared() <= flatnessSquared)
        {
            Coord c0 = new Coord(curve.getStartX(), curve.getStartY());
            Coord c1 = new Coord(curve.getEndX(), curve.getEndY());
            if (!c0.equals(c1))
            {
                CutSegment seg = new CutSegment(t0, t1, c0, c1, data);
                list.add(seg);
            }
            return;
        }
        
        double tm = (t0 + t1) / 2;
        BezierCurve2i[] curves = curve.split(.5);
        createSegments(curves[0], data, flatnessSquared,
                t0, tm, list);
        createSegments(curves[1], data, flatnessSquared,
                tm, t1, list);
    }

    public int getDx()
    {
        return c1.x - c0.x;
    }

    public int getDy()
    {
        return c1.y - c0.y;
    }

    public boolean isParallelTo(CutSegment s)
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

    public boolean isColinearWith(CutSegment s)
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

    @Override
    public String toString()
    {
        return "{" + c0 + " " + c1 + " t[" + t0 + " " + t1 + "]}";
    }

    /**
     * True if lines cross at non-vertices
     * @param s1
     * @return 
     */
    public boolean collidesWith(CutSegment s1)
    {
        if (isParallelTo(s1))
        {
            if (isPointOnLine(s1.c0))
            {
                if ((c0.equals(s1.c0) || c0.equals(s1.c1))
                        && (c1.equals(s1.c0) || c1.equals(s1.c1)))
                {
                    //Spans same region
                    return true;
                }
                
                {
                    double t0 = pointOnLineT(s1.c0);
                    double t1 = pointOnLineT(s1.c1);
                    if ((t0 > 0 && t0 < 1)
                            || (t1 > 0 && t1 < 1))
                    {
                        return true;
                    }
                }
                
                {
                    double t0 = s1.pointOnLineT(c0);
                    double t1 = s1.pointOnLineT(c1);
                    if ((t0 > 0 && t0 < 1)
                            || (t1 > 0 && t1 < 1))
                    {
                        return true;
                    }
                }
            }
            return false;
        }
        
        if (isPointOnLine(s1.c0))
        {
            double t = pointOnLineT(s1.c0);
            return t > 0 && t < 1;
        }
        
        if (isPointOnLine(s1.c1))
        {
            double t = pointOnLineT(s1.c1);
            return t > 0 && t < 1;
        }
        
        if (s1.isPointOnLine(c0))
        {
            double t = s1.pointOnLineT(c0);
            return t > 0 && t < 1;
        }
        
        if (s1.isPointOnLine(c1))
        {
            double t = s1.pointOnLineT(c1);
            return t > 0 && t < 1;
        }
        
        //Check for midpoint collision
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
    }

    public CutSegment reverse()
    {
        return new CutSegment(t1, t0, c1, c0, data);
    }
    

}
