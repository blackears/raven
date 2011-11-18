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

package com.kitfox.coyote.math.d3;

import com.kitfox.coyote.math.CyVector3d;

/**
 *
 * @author kitfox
 */
public class BoundsBuilder3d
{
    double x0;
    double y0;
    double z0;
    double x1;
    double y1;
    double z1;
    
    /** Creates a new instance of BoundsBuilder3f */
    public BoundsBuilder3d()
    {
        this(0, 0, 0, 0, 0, 0);
    }
    
    public BoundsBuilder3d(double x, double y, double z)
    {
        this.x0 = this.x1 = x;
        this.y0 = this.y1 = y;
        this.z0 = this.z1 = z;
    }
    
    public BoundsBuilder3d(double x0, double y0, double z0, double x1, double y1, double z1)
    {
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
    }
    
    public BoundsBuilder3d(CyVector3d point)
    {
        this(point.x, point.y, point.z);
    }
    
    public void union(CyVector3d pt)
    {
        union(pt.x, pt.y, pt.z);
    }
    
    public void union(double x, double y, double z)
    {
        this.x0 = Math.min(x, this.x0);
        this.y0 = Math.min(y, this.y0);
        this.z0 = Math.min(z, this.z0);
        this.x1 = Math.max(x, this.x1);
        this.y1 = Math.max(y, this.y1);
        this.z1 = Math.max(z, this.z1);
    }
    
    public BoundingBox3d toBounds()
    {
        return new BoundingBox3d(x0, y0, z0, x1, y1, z1);
    }

    public void union(BoundingBox3d childBounds)
    {
        union(childBounds.getX0(), childBounds.getY0(), childBounds.getZ0());
        union(childBounds.getX1(), childBounds.getY1(), childBounds.getZ1());
    }
}
