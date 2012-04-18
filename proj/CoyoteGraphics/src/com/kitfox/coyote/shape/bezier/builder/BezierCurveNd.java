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

package com.kitfox.coyote.shape.bezier.builder;

import com.kitfox.coyote.shape.bezier.builder.PiecewiseBezierBuilder.FitCurveRecord;

/**
 *
 * @author kitfox
 */
public class BezierCurveNd
{
    BezierPointNd[] points;

    public BezierCurveNd(BezierPointNd... points)
    {
        this.points = points;
    }

    public BezierCurveNd(BezierCurveNd curve)
    {
        this(curve.points.clone());
    }

    public double getSpan(int index)
    {
        double minVal = points[0].get(index);
        double maxVal = minVal;

        for (int i = 1; i < points.length; ++i)
        {
            minVal = Math.min(minVal, points[i].get(index));
            maxVal = Math.max(maxVal, points[i].get(index));
        }
        return maxVal - minVal;
    }

    public BezierCurveNd[] split(double t)
    {
        BezierCurveNd r0 = null;
        BezierCurveNd r1 = null;
        
        switch (points.length)
        {
            case 2:
            {
                BezierPointNd a0 = points[0];
                BezierPointNd a1 = points[1];
                
                BezierPointNd b0 = a0.lerp(a1, t);
                r0 = new BezierCurveNd(a0, b0);
                r1 = new BezierCurveNd(b0, a1);
                break;
            }
            case 3:
            {
                BezierPointNd a0 = points[0];
                BezierPointNd a1 = points[1];
                BezierPointNd a2 = points[2];
                
                BezierPointNd b0 = a0.lerp(a1, t);
                BezierPointNd b1 = a1.lerp(a2, t);
                
                BezierPointNd c0 = b0.lerp(b1, t);
                
                r0 = new BezierCurveNd(a0, b0, c0);
                r1 = new BezierCurveNd(c0, b1, a2);
                break;
            }
            case 4:
            {
                BezierPointNd a0 = points[0];
                BezierPointNd a1 = points[1];
                BezierPointNd a2 = points[2];
                BezierPointNd a3 = points[3];
                
                BezierPointNd b0 = a0.lerp(a1, t);
                BezierPointNd b1 = a1.lerp(a2, t);
                BezierPointNd b2 = a2.lerp(a3, t);
                
                BezierPointNd c0 = b0.lerp(b1, t);
                BezierPointNd c1 = b1.lerp(b2, t);
                
                BezierPointNd d0 = c0.lerp(c1, t);
                
                r0 = new BezierCurveNd(a0, b0, c0, d0);
                r1 = new BezierCurveNd(d0, c1, b2, a3);
                break;
            }
        }
        
        return new BezierCurveNd[]{r0, r1};
    }

    public BezierPointNd eval(double t)
    {
        switch (points.length)
        {
            case 2:
            {
                BezierPointNd a0 = points[0];
                BezierPointNd a1 = points[1];
                
                return a0.lerp(a1, t);
            }
            case 3:
            {
                BezierPointNd a0 = points[0];
                BezierPointNd a1 = points[1];
                BezierPointNd a2 = points[2];
                
                BezierPointNd b0 = a0.lerp(a1, t);
                BezierPointNd b1 = a1.lerp(a2, t);
                
                return b0.lerp(b1, t);
            }
            case 4:
            {
                BezierPointNd a0 = points[0];
                BezierPointNd a1 = points[1];
                BezierPointNd a2 = points[2];
                BezierPointNd a3 = points[3];
                
                BezierPointNd b0 = a0.lerp(a1, t);
                BezierPointNd b1 = a1.lerp(a2, t);
                BezierPointNd b2 = a2.lerp(a3, t);
                
                BezierPointNd c0 = b0.lerp(b1, t);
                BezierPointNd c1 = b1.lerp(b2, t);
                
                return c0.lerp(c1, t);
            }
        }
        
        return null;
    }

    /**
     * Adjust end point of this curve and start point of next curve so that
     * they are C1 continuous.
     * 
     * @param s1 Start of next curve
     */
    public void alignWith(BezierCurveNd s1)
    {
        BezierPointNd p0 = points[points.length - 1];
        BezierPointNd k0 = points[points.length - 2];
        BezierPointNd k1 = s1.points[1];
        BezierPointNd p1 = s1.points[0];
        
        //Start with average of current terminal points
        BezierPointNd mid = new BezierPointNd(p0);
        mid.add(p1);
        mid.scale(.5);
        
        //Find closest point on line from k0 to k1
        BezierPointNd v0 = new BezierPointNd(k1);
        v0.sub(k0);
        BezierPointNd v1 = new BezierPointNd(mid);
        v1.sub(k0);
        
        v0.scale(v0.dot(v1) / v0.dot(v0));
        v0.add(k0);
        
        //Alter vectors
        points[points.length - 1] = v0;
        s1.points[0] = v0;
    }
    
}
