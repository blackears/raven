/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.kitfox.coyote.shape.bezier.mesh2;

import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;

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

    public CutRecord cutAgainst(CutSegment s1)
    {
        if (isParallelTo(s1))
        {
            if (!isPointOnLine(s1.c0))
            {
                //Lines not colinear
                return null;
            }
                
            if ((c0.equals(s1.c0) || c0.equals(s1.c1))
                    && (c1.equals(s1.c0) || c1.equals(s1.c1)))
            {
                //Lines have matching end points
                return null;
            }
            
            //Find relative t points where S0 and s1 should be cut
            double s0t0 = s1.pointOnLineT(c0);
            double s0t1 = s1.pointOnLineT(c1);
            double s1t0 = pointOnLineT(s1.c0);
            double s1t1 = pointOnLineT(s1.c1);

            if ((s0t0 <= 0 && s0t1 <= 0) ||
                    (s0t0 >= 1 && s0t1 >= 1))
            {
                //Segments do not overlap
                return null;
            }

            CutSegment[] splitCur = null;
            CutSegment[] splitOth = null;
            
            if (s1t0 > 0 && s1t0 < 1 && s1t1 > 0 && s1t1 < 1)
            {
                //s1 inside of s0
                double tp0 = Math2DUtil.lerp(t0, t1, s1t0);
                double tp1 = Math2DUtil.lerp(t0, t1, s1t1);

                if (s1t0 < s1t1)
                {
                    splitCur = new CutSegment[]{
                                new CutSegment(t0, tp0, c0, s1.c0, data),
                                new CutSegment(tp0, tp1, s1.c0, s1.c1, data),
                                new CutSegment(tp1, t1, s1.c1, c1, data)
                            };
                }
                else
                {
                    splitCur = new CutSegment[]{
                                new CutSegment(t0, tp1, c0, s1.c1, data),
                                new CutSegment(tp1, tp0, s1.c1, s1.c0, data),
                                new CutSegment(tp0, t1, s1.c0, c1, data)
                            };
                }
            }
            else if (s1t0 > 0 && s1t0 < 1)
            {
                double tp = Math2DUtil.lerp(t0, t1, s1t0);
                splitCur = new CutSegment[]{
                                new CutSegment(t0, tp, c0, s1.c0, data),
                                new CutSegment(tp, t1, s1.c0, c1, data)
                            };
            }
            else if (s1t1 > 0 && s1t1 < 1)
            {
                double tp = Math2DUtil.lerp(t0, t1, s1t1);
                splitCur = new CutSegment[]{
                                new CutSegment(t0, tp, c0, s1.c1, data),
                                new CutSegment(tp, t1, s1.c1, c1, data)
                            };
            }

            if (s0t0 > 0 && s0t0 < 1 && s0t1 > 0 && s0t1 < 1)
            {
                //s0 inside of s1
                double tp0 = Math2DUtil.lerp(s1.t0, s1.t1, s0t0);
                double tp1 = Math2DUtil.lerp(s1.t0, s1.t1, s0t1);

                if (s0t0 < s0t1)
                {
                    splitOth = new CutSegment[]{
                                new CutSegment(s1.t0, tp0, s1.c0, c0, s1.data),
                                new CutSegment(tp0, tp1, c0, c1, s1.data),
                                new CutSegment(tp1, s1.t1, c1, s1.c1, s1.data)
                            };
                }
                else
                {
                    splitOth = new CutSegment[]{
                                new CutSegment(s1.t0, tp1, s1.c0, c1, s1.data),
                                new CutSegment(tp1, tp0, c1, c0, s1.data),
                                new CutSegment(tp0, s1.t1, c0, s1.c1, s1.data)
                            };
                }
            }
            else if (s0t0 > 0 && s0t0 < 1)
            {
                double tp = Math2DUtil.lerp(s1.t0, s1.t1, s0t0);
                splitOth = new CutSegment[]{
                                new CutSegment(s1.t0, tp, s1.c0, c0, s1.data),
                                new CutSegment(tp, s1.t1, c0, s1.c1, s1.data)
                            };
            }
            else if (s0t1 > 0 && s0t1 < 1)
            {
                double tp = Math2DUtil.lerp(s1.t0, s1.t1, s0t1);
                splitOth = new CutSegment[]{
                                new CutSegment(s1.t0, tp, s1.c0, c1, s1.data),
                                new CutSegment(tp, s1.t1, c1, s1.c1, s1.data)
                            };
            }


            return new CutRecord(splitCur, splitOth);
        }
        else
        {
            if (isPointOnLine(s1.c0))
            {
                double t = pointOnLineT(s1.c0);

                if (t > 0 && t < 1)
                {
                    return new CutRecord(
                            new CutSegment[]{
                                new CutSegment(t0, t, c0, s1.c0, data),
                                new CutSegment(t, t1, s1.c0, c1, data)
                            },
                            null);
                }
                else
                {
                    return null;
                }
            }
            else if (isPointOnLine(s1.c1))
            {
                double t = pointOnLineT(s1.c1);

                if (t > 0 && t < 1)
                {
                    return new CutRecord(
                            new CutSegment[]{
                                new CutSegment(t0, t, c0, s1.c1, data),
                                new CutSegment(t, t1, s1.c1, c1, data)
                            },
                            null);
                }
                else
                {
                    return null;
                }
            }
            else if (s1.isPointOnLine(c0))
            {
                double t = pointOnLineT(c0);

                if (t > 0 && t < 1)
                {
                    return new CutRecord(null, 
                            new CutSegment[]{
                                new CutSegment(s1.t0, t, s1.c0, c0, s1.data),
                                new CutSegment(t, s1.t1, c0, s1.c1, s1.data)
                            });
                }
                else
                {
                    return null;
                }
            }
            else if (s1.isPointOnLine(c1))
            {
                double t = pointOnLineT(c1);

                if (t > 0 && t < 1)
                {
                    return new CutRecord(null, 
                            new CutSegment[]{
                                new CutSegment(s1.t0, t, s1.c0, c1, s1.data),
                                new CutSegment(t, s1.t1, c1, s1.c1, s1.data)
                            });
                }
                else
                {
                    return null;
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
                    
                    double tp0 = Math2DUtil.lerp(t0, t1, t[0]);
                    double tp1 = Math2DUtil.lerp(s1.t0, s1.t1, t[1]);
                    return new CutRecord(
                            new CutSegment[]{
                                new CutSegment(t0, tp0, c0, c, data),
                                new CutSegment(tp0, t1, c, c1, data),
                            }, 
                            new CutSegment[]{
                                new CutSegment(s1.t0, tp1, s1.c0, c, s1.data),
                                new CutSegment(tp1, s1.t1, c, s1.c1, s1.data),
                            });
                }
                
                return null;
            }
        }
        
    }
}
