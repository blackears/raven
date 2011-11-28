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
public class BezierPoint extends BezierCurve
{
    final double ax0;
    final double ay0;

    public BezierPoint(double ax0, double ay0)
    {
        this.ax0 = ax0;
        this.ay0 = ay0;
    }

    @Override
    public BezierPoint reverse()
    {
        return new BezierPoint(ax0, ay0);
    }

    @Override
    public double getTanInX()
    {
        return 0;
    }

    @Override
    public double getTanInY()
    {
        return 0;
    }

    @Override
    public double getTanOutX()
    {
        return 0;
    }

    @Override
    public double getTanOutY()
    {
        return 0;
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
        return ax0;
    }

    @Override
    public double getEndY()
    {
        return ay0;
    }

    @Override
    public BezierPoint[] split(double t)
    {
        return new BezierPoint[]{
            new BezierPoint(ax0, ay0),
            new BezierPoint(ax0, ay0)
        };
    }

    @Override
    public void evaluate(double t, CyVector2d pos, CyVector2d tan)
    {
        if (pos != null)
        {
            pos.set(ax0, ay0);
        }

        if (tan != null)
        {
            tan.set(0, 0);
        }
    }

    @Override
    public BezierPoint getDerivative()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getCurvatureSquared()
    {
        return 0;
    }

    @Override
    public BezierPoint offset(double width)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void append(PathConsumer out)
    {
        throw new UnsupportedOperationException();
    }


}
