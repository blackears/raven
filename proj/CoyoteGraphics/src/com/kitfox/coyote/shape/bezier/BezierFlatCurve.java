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

package com.kitfox.coyote.shape.bezier;

import com.kitfox.coyote.math.Math2DUtil;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class BezierFlatCurve
{
    BezierCurve2d curve;
    Segment[] segments;

    private BezierFlatCurve(BezierCurve2d curve, Segment[] segments)
    {
        this.curve = curve;
        this.segments = segments;
    }
    
    /**
     * Find t values for all crossovers of these two flattened curves.
     * 
     * @param other Curve to check against
     * @param marginSquared To avoid round off errors, an extra margin is
     * added to line segments.  If endpoints of a segment are within the
     * margin of the other segment, the endpoint will be clamped to it.
     * @return List of tuples.  Each tuple provides the t value of the
     * crossover of this and the other curve.
     */
    public ArrayList<double[]> findCrossovers(BezierFlatCurve other, double marginSquared)
    {
        ArrayList<double[]> tList = new ArrayList<double[]>();
        
        for (int i = 0; i < segments.length; ++i)
        {
            Segment s0 = segments[i];
            
            for (int j = 0; j < other.segments.length; ++j)
            {
                Segment s1 = other.segments[j];
            
                double[] t = findCrossover(s0, s1, marginSquared);
                if (t == null)
                {
                    continue;
                }
                
                if ((t[0] == 1 && i != segments.length)
                    || (t[1] == 1 && j != other.segments.length))
                {
                    //Ignore terminating points unless final segment
                    continue;
                }
                
                //Save crossover
                //Using this particular formula to avoid
                // round off error when t == 0 or t == 1.
                t[0] = s0.t0 * (1 - t[0]) + s0.t1 * t[0];
                t[1] = s1.t0 * (1 - t[1]) + s1.t1 * t[1];
                tList.add(t);
            }
        }
        
        return tList;
    }
    
    private double[] findCrossover(Segment s0, Segment s1, 
            double marginSquared)
    {
        double[] t = Math2DUtil.lineIsectFractions(
                s0.x0, s0.y0, s0.getDx(), s0.getDy(), 
                s1.x0, s1.y0, s1.getDy(), s1.getDy(), 
                null);

        if (t == null)
        {
            //Lines parallel
            return null;
        }

        //Clamp to end points if close enough
        double s00Dist2 = Math2DUtil.distPointSegmentSquared(s0.x0, s0.y0, 
                s1.x0, s1.y0, s1.getDx(), s1.getDy());
        
        double s01Dist2 = Math2DUtil.distPointSegmentSquared(s0.x1, s0.y1, 
                s1.x0, s1.y0, s1.getDx(), s1.getDy());
        
        double s10Dist2 = Math2DUtil.distPointSegmentSquared(s1.x0, s1.y0, 
                s0.x0, s0.y0, s0.getDx(), s0.getDy());
        
        double s11Dist2 = Math2DUtil.distPointSegmentSquared(s1.x1, s1.y1, 
                s0.x0, s0.y0, s0.getDx(), s0.getDy());
        
        if (s00Dist2 <= marginSquared)
        {
            t[0] = 0;
        }
        
        if (s01Dist2 <= marginSquared)
        {
            t[0] = 1;
        }
        
        if (s10Dist2 <= marginSquared)
        {
            t[1] = 0;
        }
        
        if (s11Dist2 <= marginSquared)
        {
            t[1] = 1;
        }
        
        if (t[0] < 0 || t[0] > 1 || t[1] < 0 || t[1] > 1)
        {
            return null;
        }
        return t;
    }
    
    public static BezierFlatCurve create(BezierCurve2d curve, double resolution)
    {
        double res2 = resolution * resolution;
        ArrayList<Segment> segList = new ArrayList<Segment>();

        flattenCurve(curve, res2, 0, 1, segList);
        
        return new BezierFlatCurve(curve, 
                segList.toArray(new Segment[segList.size()]));
    }
    
    private static void flattenCurve(
            BezierCurve2d curve, double res2, 
            double t0, double t1,
            ArrayList<Segment> segList)
    {
        if (curve.getCurvatureSquared() <= res2)
        {
            Segment seg = new Segment(curve.getStartX(), curve.getEndX(),
                    curve.getEndX(), curve.getEndY(), t0, t1);
            segList.add(seg);
            return;
        }
        
        BezierCurve2d[] curves = curve.split(.5);
        flattenCurve(curves[0], res2, t0, (t1 + t0) / 2, segList);
        flattenCurve(curves[1], res2, (t1 + t0) / 2, t1, segList);
    }
    
    //----------------------
    private static class Segment
    {
        final double x0;
        final double y0;
        final double x1;
        final double y1;
        final double t0;
        final double t1;

        public Segment(double x0, double y0, double x1, double y1, double t0, double t1)
        {
            this.x0 = x0;
            this.y0 = y0;
            this.x1 = x1;
            this.y1 = y1;
            this.t0 = t0;
            this.t1 = t1;
        }
        
        public double getDx()
        {
            return x1 - x0;
        }
        
        public double getDy()
        {
            return y1 - y0;
        }
    }
}
