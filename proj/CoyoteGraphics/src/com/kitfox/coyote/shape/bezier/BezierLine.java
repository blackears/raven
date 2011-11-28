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

import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.shape.PathConsumer;

/**
 *
 * @author kitfox
 */
public class BezierLine extends BezierCurve
{
    final double ax0;
    final double ay0;
    final double ax1;
    final double ay1;

    public BezierLine(double ax0, double ay0, double ax1, double ay1)
    {
        this.ax0 = ax0;
        this.ay0 = ay0;
        this.ax1 = ax1;
        this.ay1 = ay1;
    }

    @Override
    public BezierLine reverse()
    {
        return new BezierLine(ax1, ay1, ax0, ay0);
    }

    @Override
    public double getTanInX()
    {
        return ax1 - ax0;
    }

    @Override
    public double getTanInY()
    {
        return ay1 - ay0;
    }

    @Override
    public double getTanOutX()
    {
        return ax1 - ax0;
    }

    @Override
    public double getTanOutY()
    {
        return ay1 - ay0;
    }

    @Override
    public double getStartX()
    {
        return ax0;
    }

    @Override
    public double getStartY()
    {
        return ay0;
    }

    @Override
    public double getEndX()
    {
        return ax1;
    }

    @Override
    public double getEndY()
    {
        return ay1;
    }

    @Override
    public BezierLine[] split(double t)
    {
        double bx0 = ax0 + t * (ax1 - ax0);
        double by0 = ay0 + t * (ay1 - ay0);

        return new BezierLine[]{
            new BezierLine(ax0, ay0, bx0, by0),
            new BezierLine(bx0, by0, ax1, ay1)
        };
    }

    @Override
    public void evaluate(double t, CyVector2d pos, CyVector2d tan)
    {
        double bx0 = ax0 + t * (ax1 - ax0);
        double by0 = ay0 + t * (ay1 - ay0);

        if (pos != null)
        {
            pos.set(bx0, by0);
        }

        if (tan != null)
        {
            tan.set(ax1 - ax0, ay1 - ay0);
        }
    }

    @Override
    public BezierPoint getDerivative()
    {
        return new BezierPoint(ax1 - ax0, ay1 - ay0);
    }

    @Override
    public double getCurvatureSquared()
    {
        return 0;
    }

    @Override
    public BezierLine offset(double width)
    {
        CyVector2d v = new CyVector2d(ax1 - ax0, ay1 - ay0);
        v.normalize();
        v.rotCCW90();
        v.scale(width);

        return new BezierLine(ax0 + v.x, ay0 + v.y,
                ax1 + v.x, ay1 + v.y);
    }

    @Override
    public void append(PathConsumer out)
    {
        out.lineTo(ax1, ay1);
    }


}
