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

import com.kitfox.coyote.shape.bezier.BezierCurve2i;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class Segment
{

    int x0;
    int y0;
    int x1;
    int y1;
    double t0;
    double t1;

    public Segment(int x0, int y0, int x1, int y1, double t0, double t1)
    {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.t0 = t0;
        this.t1 = t1;
    }

    public int getDx()
    {
        return x1 - x0;
    }

    public int getDy()
    {
        return y1 - y0;
    }

    public boolean isParallelTo(Segment s)
    {
        int dx0 = x1 - x0;
        int dy0 = y1 - y0;
        int dx1 = s.x1 - s.x0;
        int dy1 = s.y1 - s.y0;
        return dx0 * dy1 == dy0 * dx1;
    }

    public boolean isPointOnLine(int x, int y)
    {
        int dx0 = x1 - x0;
        int dy0 = y1 - y0;
        int dx1 = x - x0;
        int dy1 = y - y0;
        return dx0 * dy1 == dy0 * dx1;
    }

    public boolean isColinearWith(Segment s)
    {
        return isParallelTo(s) && isPointOnLine(s.x0, s.y0);
    }
    
    public static Segment[] flatten(BezierCurve2i curve, double flatnessSquared)
    {
        if (curve.getCurvatureSquared() <= flatnessSquared)
        {
            return new Segment[]{new Segment(
                    curve.getStartX(), curve.getStartY(),
                    curve.getEndX(), curve.getEndY(),
                    0, 1
                    )};
        }
        
        ArrayList<Segment> segs = new ArrayList<Segment>();
        flatten(curve, flatnessSquared, 0, 1, segs);
        
        return segs.toArray(new Segment[segs.size()]);
    }

    private static void flatten(BezierCurve2i curve, 
            double flatnessSquared, 
            double t0, double t1,
            ArrayList<Segment> segs)
    {
        if (curve.getCurvatureSquared() <= flatnessSquared)
        {
            Segment seg = new Segment(curve.getStartX(), curve.getStartY(),
                    curve.getEndX(), curve.getEndY(),
                    t0, t1);
            segs.add(seg);
            return;
        }
        
        BezierCurve2i[] curves = curve.split(.5);
        double tm = (t0 + t1) / 2;
        flatten(curves[0], flatnessSquared, t0, tm, segs);
        flatten(curves[1], flatnessSquared, tm, t1, segs);
    }

    /**
     * For a point (x0, y0) which lies on this line segment,
     * returns t value.
     * 
     * Note that (x0, y0) must be a point which lies on this line
     * and which returns true for isPointOnLine().
     * 
     * @param x0
     * @param y0
     * @return 
     */
    public double pointOnLineT(int x, int y)
    {
        if (x == x0 && y == y0)
        {
            return 0;
        }
        if (x == x1 && y == y1)
        {
            return 1;
        }
        
        int dx = x1 - x0;
        int dy = y1 - y0;
        
        if (Math.abs(dx) > Math.abs(dy))
        {
            return (x - x0) / (double)dx;
        }
        else
        {
            return (y - y0) / (double)dy;
        }
    }
}
