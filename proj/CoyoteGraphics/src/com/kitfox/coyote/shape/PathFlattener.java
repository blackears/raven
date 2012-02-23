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

import static com.kitfox.coyote.math.Math2DUtil.*;

/**
 *
 * @author kitfox
 */
public class PathFlattener extends PathConsumer
{
    private final PathConsumer out;
    private double maxCurvatureSquared;

    private double mx, my;
    private double bx, by;

    public PathFlattener(PathConsumer out)
    {
        this(out, .01);
    }

    /**
     * Flattens all quadratic and cubic segments of curve into 
     * straight line segments, as determined by the degree of
     * desired curvature.
     * 
     * For each quadratic or cubic segment in the input path, the distance
     * from each knot to the line between the start and end points is
     * calculated.  The square of the max knot distance is then compared
     * to the maxCurvatureSquared.  If it is larger, the curve is subdivided
     * and the process repeated.  This is continued until all segments are
     * flat enough.
     *
     * @param out
     * @param maxCurvatureSquared
     */
    public PathFlattener(PathConsumer out, double maxCurvatureSquared)
    {
        this.out = out;
        this.maxCurvatureSquared = maxCurvatureSquared;
    }

    @Override
    public void beginPath()
    {
        out.beginPath();
    }

    @Override
    public void beginSubpath(double x0, double y0)
    {
        out.beginSubpath(x0, y0);
        bx = mx = x0;
        by = my = y0;
    }

    @Override
    public void lineTo(double x0, double y0)
    {
        if (!(x0 == mx && y0 == my))
        {
            out.lineTo(x0, y0);
            mx = x0;
            my = y0;
        }
    }

    @Override
    public void quadTo(double x0, double y0, double x1, double y1)
    {
        //Distance from knots to spanning line
        double dist0 = distPointLineSquared(x0, y0, mx, my, x1 - mx, y1 - my);
//        double len = distSquared(mx, my, x1, y1);

//        if (dist0 > maxCurvatureSquared * len)
        if (dist0 > maxCurvatureSquared)
        {
            //Subdivide curve
            double ax0 = lerp(mx, x0, .5);
            double ay0 = lerp(my, y0, .5);
            double ax1 = lerp(x0, x1, .5);
            double ay1 = lerp(y0, y1, .5);

            double bx0 = lerp(ax0, ax1, .5);
            double by0 = lerp(ay0, ay1, .5);

            quadTo(ax0, ay0, bx0, by0);
            quadTo(ax1, ay1, x1, y1);
        }
        else
        {
            //Curve is flat enough to treat as line
            out.lineTo(x1, y1);
        }
        
        mx = x1;
        my = y1;
    }

    @Override
    public void cubicTo(double x0, double y0, double x1, double y1, double x2, double y2)
    {
        //Distance from knots to spanning line
        double dist0 = distPointLineSquared(x0, y0, mx, my, x2 - mx, y2 - my);
        double dist1 = distPointLineSquared(x1, y1, mx, my, x2 - mx, y2 - my);
//        double len = distSquared(mx, my, x2, y2);

//        if (dist0 > maxCurvatureSquared * len || dist1 > maxCurvatureSquared * len)
        if (dist0 > maxCurvatureSquared || dist1 > maxCurvatureSquared)
        {
            //Subdivide curve
            double ax0 = lerp(mx, x0, .5);
            double ay0 = lerp(my, y0, .5);
            double ax1 = lerp(x0, x1, .5);
            double ay1 = lerp(y0, y1, .5);
            double ax2 = lerp(x1, x2, .5);
            double ay2 = lerp(y1, y2, .5);

            double bx0 = lerp(ax0, ax1, .5);
            double by0 = lerp(ay0, ay1, .5);
            double bx1 = lerp(ax1, ax2, .5);
            double by1 = lerp(ay1, ay2, .5);
            
            double cx0 = lerp(bx0, bx1, .5);
            double cy0 = lerp(by0, by1, .5);

            cubicTo(ax0, ay0, bx0, by0, cx0, cy0);
            cubicTo(bx1, by1, ax2, ay2, x2, y2);
        }
        else
        {
            //Curve is flat enough to treat as line
            out.lineTo(x2, y2);
        }

        mx = x2;
        my = y2;
    }

    @Override
    public void closeSubpath()
    {
        out.closeSubpath();
        mx = bx;
        my = by;
    }

    @Override
    public void endPath()
    {
        out.endPath();
    }


}
