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

package com.kitfox.coyote.math;

/**
 *
 * @author kitfox
 */
public class CyVector4d
{
    public double x;
    public double y;
    public double z;
    public double w;

    public CyVector4d()
    {
    }

    public CyVector4d(CyVector4d v)
    {
        this(v.x, v.y, v.z, v.w);
    }

    public CyVector4d(double x, double y, double z, double w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public double dot(CyVector4d v)
    {
        return x * v.x + y * v.y + z * v.z + w * v.w;
    }

    public double lengthSquared()
    {
        return dot(this);
    }

    public double length()
    {
        return Math.sqrt(lengthSquared());
    }

    public void scale(double scalar)
    {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        w *= scalar;
    }

    public void add(double x, double y, double z, double w)
    {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
    }

    public void add(CyVector4d v)
    {
        add(v.x, v.y, v.z, v.w);
    }

    public void addScaleOf(CyVector4d v, double s)
    {
        add(s * v.x, s * v.y, s * v.z, s * v.w);
    }

    public void sub(double x, double y, double z, double w)
    {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.w -= w;
    }

    public void sub(CyVector4d v)
    {
        sub(v.x, v.y, v.z, v.w);
    }

    public void set(double x, double y, double z, double w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * @return the x
     */
    public double getX()
    {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(double x)
    {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY()
    {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(double y)
    {
        this.y = y;
    }

    /**
     * @return the z
     */
    public double getZ()
    {
        return z;
    }

    /**
     * @param z the z to set
     */
    public void setZ(double z)
    {
        this.z = z;
    }

    /**
     * @return the w
     */
    public double getW()
    {
        return w;
    }

    /**
     * @param w the w to set
     */
    public void setW(double w)
    {
        this.w = w;
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
        final CyVector4d other = (CyVector4d) obj;
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x))
        {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y))
        {
            return false;
        }
        if (Double.doubleToLongBits(this.z) != Double.doubleToLongBits(other.z))
        {
            return false;
        }
        if (Double.doubleToLongBits(this.w) != Double.doubleToLongBits(other.w))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.w) ^ (Double.doubleToLongBits(this.w) >>> 32));
        return hash;
    }

    @Override
    public String toString()
    {
        return "(" + x + ", " + y + ", " + z + ", " + w + ")";
    }

}
