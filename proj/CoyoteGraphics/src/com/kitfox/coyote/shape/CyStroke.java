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

import java.awt.BasicStroke;
import java.util.Arrays;

/**
 *
 * @author kitfox
 */
public class CyStroke
{
    private final double width;
    private final CyStrokeCap cap;
    private final CyStrokeJoin join;
    private final double miterLimit;

    private final double[] dash;
    private final double dashOffset;

    public CyStroke(double width)
    {
        this(width, CyStrokeCap.BUTT, CyStrokeJoin.BEVEL, 4, null, 0);
    }

    public CyStroke(double width, CyStrokeCap cap, CyStrokeJoin join)
    {
        this(width, cap, join, 4);
    }

    public CyStroke(double width, CyStrokeCap cap, CyStrokeJoin join, double miterLimit)
    {
        this(width, cap, join, miterLimit, null, 0);
    }

    public CyStroke(float width, CyStrokeCap cap, CyStrokeJoin join, float miterLimit, float[] dash, float dashOffset)
    {
        this(width, cap, join, miterLimit, toFloat(dash), dashOffset);
    }

    public CyStroke(double width, CyStrokeCap cap, CyStrokeJoin join, double miterLimit, double[] dash, double dashOffset)
    {
        this.width = width;
        this.cap = cap;
        this.join = join;
        this.miterLimit = miterLimit;
        this.dash = dash;
        this.dashOffset = dashOffset;
    }

    private static double[] toFloat(float[] arr)
    {
        if (arr == null)
        {
            return null;
        }
        double[] ret = new double[arr.length];
        for (int i = 0; i < arr.length; ++i)
        {
            ret[i] = arr[i];
        }
        return ret;
    }
    
    public CyPath2d outlineShape(CyShape shape)
    {
//        return outlineShape(shape, 10000);
        return outlineShape(shape, 2500);
    }
    
    public CyPath2d outlineShape(CyShape shape, double flatnessSquared)
    {
        PathCollector col = new PathCollector();
        PathConsumer builder = new PathOutliner(col,
                width / 2,
                cap,
                join,
                miterLimit,
                flatnessSquared);

        if (dash != null && dash.length >= 2)
        {
            builder = new PathDasher(builder, dash, dashOffset);
        }

        builder.feedShape(shape);
        return col.getPath();
    }
    
    /**
     * @return the width
     */
    public double getWidth()
    {
        return width;
    }

    /**
     * @return the cap
     */
    public CyStrokeCap getCap()
    {
        return cap;
    }

    /**
     * @return the join
     */
    public CyStrokeJoin getJoin()
    {
        return join;
    }

    /**
     * @return the miterLimit
     */
    public double getMiterLimit()
    {
        return miterLimit;
    }

    /**
     * @return the dash
     */
    public double[] getDash()
    {
        return dash.clone();
    }

    /**
     * @return the dash
     */
    public float[] getDashFloat()
    {
        if (dash == null)
        {
            return new float[0];
        }
        
        float[] arr = new float[dash.length];
        for (int i = 0; i < arr.length; ++i)
        {
            arr[i] = (float)dash[i];
        }
        return arr;
    }

    /**
     * @return the dashOffset
     */
    public double getDashOffset()
    {
        return dashOffset;
    }

    public boolean isDashUsed()
    {
        if (dash == null || dash.length <= 1)
        {
            return false;
        }
        
        for (int i = 0; i < dash.length; ++i)
        {
            if (dash[i] > 0)
            {
                return true;
            }
        }
        
        return false;
    }
    
    public BasicStroke asBasicStroke()
    {
        int sCap, sJoin;
        switch (cap)
        {
            default:
            case BUTT:
                sCap = BasicStroke.CAP_BUTT;
                break;
            case ROUND:
                sCap = BasicStroke.CAP_ROUND;
                break;
            case SQUARE:
                sCap = BasicStroke.CAP_SQUARE;
                break;
        }
        
        switch (join)
        {
            default:
            case BEVEL:
                sJoin = BasicStroke.JOIN_BEVEL;
                break;
            case MITER:
                sJoin = BasicStroke.JOIN_MITER;
                break;
            case ROUND:
                sJoin = BasicStroke.JOIN_ROUND;
                break;
        }
        
        if (isDashUsed())
        {
            return new BasicStroke((float)width, sCap, sJoin, 
                    (float)miterLimit, getDashFloat(), (float)dashOffset);
        }
        
        return new BasicStroke((float)width, sCap, sJoin, 
                (float)miterLimit);
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
        final CyStroke other = (CyStroke) obj;
        if (Double.doubleToLongBits(this.width) != Double.doubleToLongBits(other.width))
        {
            return false;
        }
        if (this.cap != other.cap)
        {
            return false;
        }
        if (this.join != other.join)
        {
            return false;
        }
        if (Double.doubleToLongBits(this.miterLimit) != Double.doubleToLongBits(other.miterLimit))
        {
            return false;
        }
        if (!Arrays.equals(this.dash, other.dash))
        {
            return false;
        }
        if (Double.doubleToLongBits(this.dashOffset) != Double.doubleToLongBits(other.dashOffset))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.width) ^ (Double.doubleToLongBits(this.width) >>> 32));
        hash = 53 * hash + (this.cap != null ? this.cap.hashCode() : 0);
        hash = 53 * hash + (this.join != null ? this.join.hashCode() : 0);
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.miterLimit) ^ (Double.doubleToLongBits(this.miterLimit) >>> 32));
        hash = 53 * hash + Arrays.hashCode(this.dash);
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.dashOffset) ^ (Double.doubleToLongBits(this.dashOffset) >>> 32));
        return hash;
    }

    public CyStroke scale(double scalar)
    {
        double[] newDash = null;
        if (dash != null)
        {
            newDash = new double[dash.length];
            for (int i = 0; i < dash.length; ++i)
            {
                newDash[i] = dash[i] * scalar;
            }
        }
        return new CyStroke(width * scalar, 
                cap, join, 
                miterLimit * scalar, newDash, dashOffset * scalar);
    }
}
