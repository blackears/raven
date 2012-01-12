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

package com.kitfox.coyote.shape.bezier.cutgraph;

import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.shape.bezier.BezierCurve2i;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class CutSegment
{
    final double t0;
    final double t1;
    final Coord c0;
    final Coord c1;

    public CutSegment(double t0, double t1, Coord c0, Coord c1)
    {
        this.t0 = t0;
        this.t1 = t1;
        this.c0 = c0;
        this.c1 = c1;
    }

    public static ArrayList<CutSegment> createSegments(
            BezierCurve2i curve, double flatnessSquared)
    {
        ArrayList<CutSegment> list = new ArrayList<CutSegment>();
        createSegments(curve, flatnessSquared, 0, 1, list);
        return list;
    }

    private static void createSegments(
            BezierCurve2i curve, double flatnessSquared,
            double t0, double t1, ArrayList<CutSegment> list)
    {
        if (curve.getCurvatureSquared() <= flatnessSquared)
        {
            CutSegment seg = new CutSegment(t0, t1, 
                    new Coord(curve.getStartX(), curve.getStartY()), 
                    new Coord(curve.getEndX(), curve.getEndY()));
            list.add(seg);
            return;
        }
        
        double tm = (t0 + t1) / 2;
        BezierCurve2i[] curves = curve.split(.5);
        createSegments(curves[0], flatnessSquared,
                t0, tm, list);
        createSegments(curves[1], flatnessSquared,
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

    public void cutAgainst(CutSegment s1, 
            ArrayList<CutPoint> cuts0, ArrayList<CutPoint> cuts1)
    {
        //Check end points
        if (c0.equals(s1.c0) || c0.equals(s1.c1))
        {
            cuts0.add(new CutPoint(t0, c0));
        }
        
        if (c1.equals(s1.c0) || c1.equals(s1.c1))
        {
            cuts0.add(new CutPoint(t1, c1));
        }
        
        if (s1.c0.equals(c0) || s1.c0.equals(c1))
        {
            cuts0.add(new CutPoint(s1.t0, s1.c0));
        }
        
        if (s1.c1.equals(c0) || s1.c1.equals(c1))
        {
            cuts0.add(new CutPoint(s1.t1, s1.c1));
        }
        
        if (isParallelTo(s1))
        {
            //Find relative t points where S0 and s1 should be cut
            double s0t0 = s1.pointOnLineT(c0);
            double s0t1 = s1.pointOnLineT(c1);
            double s1t0 = pointOnLineT(s1.c0);
            double s1t1 = pointOnLineT(s1.c1);

            if (s0t0 > 0 && s0t0 < 1)
            {
                cuts1.add(new CutPoint(Math2DUtil.lerp(s1.t0, s1.t1, s0t0),
                        c0));
            }

            if (s0t1 > 0 && s0t1 < 1)
            {
                cuts1.add(new CutPoint(Math2DUtil.lerp(s1.t0, s1.t1, s0t1),
                        c1));
            }

            if (s1t0 > 0 && s1t0 < 1)
            {
                cuts0.add(new CutPoint(Math2DUtil.lerp(t0, t1, s1t0),
                        s1.c0));
            }

            if (s1t1 > 0 && s1t1 < 1)
            {
                cuts0.add(new CutPoint(Math2DUtil.lerp(t0, t1, s1t1),
                        s1.c1));
            }
        }
        else
        {
            //Not parallel.  Solve system of linear eqns
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

                double tp0 = Math2DUtil.lerp(t0, t1, t[0]);
                double tp1 = Math2DUtil.lerp(s1.t0, s1.t1, t[1]);
                cuts0.add(new CutPoint(tp0, c));
                cuts1.add(new CutPoint(tp1, c));
            }
        }
    }

}
