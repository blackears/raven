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

package com.kitfox.raven.util.tree;

/**
 *
 * @author kitfox
 */
public class TrackKey<T>
{
    private final PropertyData<T> data;
    private final Interp interp;
    private final double tanInX;
    private final double tanInY;
    private final double tanOutX;
    private final double tanOutY;

    public TrackKey(PropertyData<T> data)
    {
        this(data, Interp.CONST, 1, 0, 1, 0);
    }

    public TrackKey(PropertyData<T> data, Interp interp)
    {
        this(data, interp, 1, 0, 1, 0);
    }

    public TrackKey(PropertyData<T> data, Interp interp,
            double tanInX, double tanInY, double tanOutX, double tanOutY)
    {
        this.data = data;
        this.interp = interp;
        this.tanInX = tanInX;
        this.tanInY = tanInY;
        this.tanOutX = tanOutX;
        this.tanOutY = tanOutY;
    }

    /**
     * @return the data
     */
    public PropertyData<T> getData()
    {
        return data;
    }

    /**
     * @return the interp
     */
    public Interp getInterp()
    {
        return interp;
    }

    /**
     * @return the dx
     */
    public double getTanInX()
    {
        return tanInX;
    }

    /**
     * @return the dy
     */
    public double getTanInY()
    {
        return tanInY;
    }

    /**
     * @return the dx
     */
    public double getTanOutX()
    {
        return tanOutX;
    }

    /**
     * @return the dy
     */
    public double getTanOutY()
    {
        return tanOutY;
    }

    @Override
    public String toString()
    {
        return data.toString()
                + " " + interp
                + " tanInX:" + tanInX
                + " tanInY:" + tanInY
                + " tanOutX:" + tanOutX
                + " tanOutY:" + tanOutY;
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
        final TrackKey<T> other = (TrackKey<T>) obj;
        if (this.data != other.data && (this.data == null || !this.data.equals(other.data)))
        {
            return false;
        }
        if (this.interp != other.interp)
        {
            return false;
        }
        if (Double.doubleToLongBits(this.tanInX) != Double.doubleToLongBits(other.tanInX))
        {
            return false;
        }
        if (Double.doubleToLongBits(this.tanInY) != Double.doubleToLongBits(other.tanInY))
        {
            return false;
        }
        if (Double.doubleToLongBits(this.tanOutX) != Double.doubleToLongBits(other.tanOutX))
        {
            return false;
        }
        if (Double.doubleToLongBits(this.tanOutY) != Double.doubleToLongBits(other.tanOutY))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 37 * hash + (this.data != null ? this.data.hashCode() : 0);
        hash = 37 * hash + (this.interp != null ? this.interp.hashCode() : 0);
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.tanInX) ^ (Double.doubleToLongBits(this.tanInX) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.tanInY) ^ (Double.doubleToLongBits(this.tanInY) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.tanOutX) ^ (Double.doubleToLongBits(this.tanOutX) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.tanOutY) ^ (Double.doubleToLongBits(this.tanOutY) >>> 32));
        return hash;
    }


    //------------------------------------

    public static enum Interp {
        //Used by all data types
        NONE,  //Do not set any value for interpolation span
        CONST, //Each frame in span set to this value

        //Only meaningful to numberic data types.
        // Treated like CONST for all others
        LINEAR, 
        SMOOTH_STEP,  //Smooth step curve: -2x^3 + 3x^2
        SMOOTH,  //tanIn will be evaluated as if it is equal to tanOut
        BEZIER,  //dx, dy used to calc curve
    };


}
