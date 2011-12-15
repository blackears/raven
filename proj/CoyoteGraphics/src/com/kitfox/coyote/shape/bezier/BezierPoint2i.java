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
public class BezierPoint2i extends BezierCurve2i
{
    final int ax0;
    final int ay0;

    public BezierPoint2i(int ax0, int ay0)
    {
        this.ax0 = ax0;
        this.ay0 = ay0;
    }

    @Override
    public int getOrder()
    {
        return 1;
    }

    @Override
    public BezierPoint2i reverse()
    {
        return new BezierPoint2i(ax0, ay0);
    }

    @Override
    public int getTanInX()
    {
        return 0;
    }

    @Override
    public int getTanInY()
    {
        return 0;
    }

    @Override
    public int getTanOutX()
    {
        return 0;
    }

    @Override
    public int getTanOutY()
    {
        return 0;
    }

    @Override
    public int getStartX()
    {
        return ax0;
    }

    @Override
    public int getStartY()
    {
        return ay0;
    }

    @Override
    public int getEndX()
    {
        return ax0;
    }

    @Override
    public int getEndY()
    {
        return ay0;
    }
    
    @Override
    public int getMinX()
    {
        return ax0;
    }
    
    @Override
    public int getMinY()
    {
        return ay0;
    }
    
    @Override
    public int getMaxX()
    {
        return ax0;
    }
    
    @Override
    public int getMaxY()
    {
        return ay0;
    }

    @Override
    public BezierPoint2i[] split(double t)
    {
        return new BezierPoint2i[]{
            new BezierPoint2i(ax0, ay0),
            new BezierPoint2i(ax0, ay0)
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
    public BezierPoint2i getDerivative()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public BezierCubic2i asCubic()
    {
        return new BezierCubic2i(ax0, ay0, 
                ax0, ay0,
                ax0, ay0,
                ax0, ay0);
    }
    
    @Override
    public boolean isColinear()
    {
        return true;
    }

    @Override
    public double getCurvatureSquared()
    {
        return 0;
    }

    @Override
    public boolean convexHullSelfIsect()
    {
        return false;
    }

    @Override
    public BezierPoint2i offset(double width)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void append(PathConsumer out)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final BezierPoint2i other = (BezierPoint2i) obj;
        if (this.ax0 != other.ax0)
        {
            return false;
        }
        if (this.ay0 != other.ay0)
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 31 * hash + this.ax0;
        hash = 31 * hash + this.ay0;
        return hash;
    }

    @Override
    public BezierPoint2i setStart(int x, int y)
    {
        return new BezierPoint2i(x, y);
    }

    @Override
    public BezierPoint2i setEnd(int x, int y)
    {
        return new BezierPoint2i(x, y);
    }

    @Override
    public BezierPoint2i setEndPoints(int x0, int y0, int x1, int y1)
    {
        return new BezierPoint2i(x0, y0);
    }


}
