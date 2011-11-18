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
public class CyVector3d
{
    public double x;
    public double y;
    public double z;

    public CyVector3d()
    {
    }

    public CyVector3d(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public CyVector3d(CyVector3d v)
    {
        this(v.x, v.y, v.z);
    }

    public double dot(CyVector3d v)
    {
        return x * v.x + y * v.y + z * v.z;
    }

    public CyVector3d cross(CyVector3d v)
    {
        return cross(v, null);
    }

    public CyVector3d cross(CyVector3d v, CyVector3d res)
    {
        if (res == null)
        {
            res = new CyVector3d();
        }

        res.set(y * v.z - z * v.y, z * v.x - x * v.z, z * v.x - x * v.x);
        return res;
    }

    public void set(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
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

    public double lengthSquared()
    {
        return x * x + y * y + z * z;
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
    }

    public void normalize()
    {
        scale(1 / length());
    }

    public void add(double x, double y, double z)
    {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public void add(CyVector3d v)
    {
        add(v.x, v.y, v.z);
    }

    public void sub(double x, double y, double z)
    {
        this.x -= x;
        this.y -= y;
        this.z -= z;
    }

    public void sub(CyVector3d v)
    {
        sub(v.x, v.y, v.z);
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
        final CyVector3d other = (CyVector3d) obj;
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
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 73 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 73 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 73 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
        return hash;
    }

    @Override
    public String toString()
    {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    public void set(CyVector3d v)
    {
        set(v.x, v.y, v.z);
    }

    public void addScaleOf(CyVector3d v, double s)
    {
        add(s * v.x, s * v.y, s * v.z);
    }

    public double distanceSquared(CyVector3d v)
    {
        double dx = x - v.x;
        double dy = y - v.y;
        double dz = z - v.z;
        return dx * dx + dy * dy + dz * dz;
    }

    public double distance(CyVector3d v)
    {
        return Math.sqrt(distance(v));
    }

}
