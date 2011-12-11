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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Finds all points of intersection between two curves down to a given 
 * resolution.
 *
 * @author kitfox
 */
public class BezierCutCurve2i
{
    private BezierCurve2i[] segs0;
    private BezierCurve2i[] segs1;

    public BezierCutCurve2i(BezierCurve2i c0, BezierCurve2i c1, int resolution)
    {
        CutCallback cb = new CutCallback(c0, c1);
        BezierIntersection2i.findIntersections(cb, c0, c1, resolution);
        
        segs0 = cb.segs0;
        segs1 = cb.segs1;
    }

    /**
     * @return the segs0
     */
    public BezierCurve2i[] getSegs0()
    {
        return segs0;
    }

    /**
     * @return the segs1
     */
    public BezierCurve2i[] getSegs1()
    {
        return segs1;
    }
        
    //---------------------------------
    static class CutPoint implements Comparable<CutPoint>
    {
        final double t0;
        final double t1;
        final int x;
        final int y;

        public CutPoint(double t0, double t1, int x, int y)
        {
            this.t0 = t0;
            this.t1 = t1;
            this.x = x;
            this.y = y;
        }

        @Override
        public int compareTo(CutPoint other)
        {
            return Double.compare(t0, other.t0);
        }
    }
    
    static class CutCallback implements BezierIntersection2i.Callback 
    {
        BezierCurve2i c0;
        BezierCurve2i c1;
        BezierCurve2i[] segs0;
        BezierCurve2i[] segs1;
        
        private ArrayList<CutPoint> points = new ArrayList<CutPoint>();

        public CutCallback(BezierCurve2i c0, BezierCurve2i c1)
        {
            this.c0 = c0;
            this.c1 = c1;
        }
        
        @Override
        public void emitCrossover(double t0, double t1, int x, int y)
        {
            points.add(new CutPoint(t0, t1, x, y));
        }

        @Override
        public void done()
        {
            Collections.sort(points);
            
            double[] t0 = new double[points.size()];
            double[] t1 = new double[points.size()];
            
            for (int i = 0; i < points.size(); ++i)
            {
                CutPoint pt = points.get(i);
                t0[i] = pt.t0;
                t1[i] = pt.t1;
            }
            
            Arrays.sort(t1);
            
            segs0 = c0.split(t0);
            segs1 = c1.split(t1);
            
            validateCuts(segs0, points);
            
            Collections.sort(points, new Comparator<CutPoint>()
            {
                @Override
                public int compare(CutPoint o1, CutPoint o2)
                {
                    return Double.compare(o1.t1, o2.t1);
                }
            });
            
            validateCuts(segs1, points);
        }
        
        /**
         * Make sure cut points in two curves match
         */
        private void validateCuts(BezierCurve2i[] segs, ArrayList<CutPoint> points)
        {
            for (int i = 0; i < points.size(); ++i)
            {
                CutPoint pt = points.get(i);
                BezierCurve2i s0 = segs[i];
                BezierCurve2i s1 = segs[i + 1];
                
                if (s0.getEndX() != pt.x || s0.getEndY() != pt.y)
                {
                    segs[0] = s0.setEnd(pt.x, pt.y);
                }
                
                if (s1.getStartX() != pt.x || s1.getStartY() != pt.y)
                {
                    segs[0] = s0.setStart(pt.x, pt.y);
                }
            }
        }

        /**
         * @return the points
         */
        public ArrayList<CutPoint> getPoints()
        {
            return points;
        }
    };
    
}
