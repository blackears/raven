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

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector3d;
import com.kitfox.coyote.math.CyVector4d;
import static com.kitfox.coyote.math.d3.Octants.*;
import java.util.Arrays;

/**
 * Axis aligned bounding box
 *
 * @author kitfox
 */
public class BoundingBox3d
{
    private final double x0;
    private final double y0;
    private final double z0;
    private final double x1;
    private final double y1;
    private final double z1;
    final int hashCode;
    
    /** Creates a new instance of Bounds3d */
    public BoundingBox3d()
    {
        this(0, 0, 0, 0, 0, 0);
    }
    
    public BoundingBox3d(CyVector3d origin)
    {
        this(origin.x, origin.y, origin.z, 0, 0, 0);
    }
    
    public BoundingBox3d(double x0, double y0, double z0, double x1, double y1, double z1)
    {
        if (x0 > x1 || y0 > y1 || z0 > z1) throw new IllegalArgumentException();
        
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        
        hashCode = Arrays.hashCode(new double[]{x0, y0, z0, x1, y1, z1});
    }
    
    /**
     * Finds bounding box of this bounding box in transform's coordinate system.
     * A faster bounding box transformed that presunes T(x) = B(x) + c, where
     * B is a 3x3 matrix and c is a translation
     */
    public BoundingBox3d getBoundsTransformedAffine(CyMatrix4d T)
    {
        CyVector3d pt = new CyVector3d(x0, y0, z0);
        T.transformPoint(pt);

        double nx0 = pt.x;
        double ny0 = pt.y;
        double nz0 = pt.z;
        double nx1 = pt.x;
        double ny1 = pt.y;
        double nz1 = pt.z;
        
        double dx = x1 - x0;
        double dy = y1 - y0;
        double dz = z1 - z0;
        
        for (int i = 1; i < 8; i++)
        {
            double ox = pt.x;
            double oy = pt.y;
            double oz = pt.z;
            if ((i & 1) == 1)
            {
                ox += dx * T.m00;
                oy += dx * T.m10;
                oz += dx * T.m20;
            }
            if ((i & 2) == 2)
            {
                ox += dy * T.m01;
                oy += dy * T.m11;
                oz += dy * T.m21;
            }
            if ((i & 4) == 4)
            {
                ox += dz * T.m02;
                oy += dz * T.m12;
                oz += dz * T.m22;
            }
            
            nx0 = Math.min(ox, nx0);
            ny0 = Math.min(oy, ny0);
            nz0 = Math.min(oz, nz0);
            nx1 = Math.max(ox, nx1);
            ny1 = Math.max(oy, ny1);
            nz1 = Math.max(oz, nz1);
        }
        
        return new BoundingBox3d(nx0, ny0, nz0, nx1 - nx0, ny1 - ny0, nz1 - nz0);
    }

    /**
     * A more complete bounds transformation that allows T to include perspective 
     * effects.
     */
    public BoundingBox3d createBoundsTransformed(CyMatrix4d T)
    {
        CyVector4d pt = new CyVector4d(x0, y0, z0, 1);
        T.transform(pt);
        normalizeHomogenous(pt);
        BoundsBuilder3d bb = new BoundsBuilder3d(pt.x, pt.y, pt.z);
        
        pt.set(x1, y0, z0, 1);
        T.transform(pt);
        normalizeHomogenous(pt);
        bb.union(pt.x, pt.y, pt.z);
        
        pt.set(x0, y1, z0, 1);
        T.transform(pt);
        normalizeHomogenous(pt);
        bb.union(pt.x, pt.y, pt.z);
        
        pt.set(x1, y1, z0, 1);
        T.transform(pt);
        normalizeHomogenous(pt);
        bb.union(pt.x, pt.y, pt.z);
        
        pt.set(x0, y0, z1, 1);
        T.transform(pt);
        normalizeHomogenous(pt);
        bb.union(pt.x, pt.y, pt.z);
        
        pt.set(x1, y0, z1, 1);
        T.transform(pt);
        normalizeHomogenous(pt);
        bb.union(pt.x, pt.y, pt.z);
        
        pt.set(x0, y1, z1, 1);
        T.transform(pt);
        normalizeHomogenous(pt);
        bb.union(pt.x, pt.y, pt.z);
        
        pt.set(x1, y1, z1, 1);
        T.transform(pt);
        normalizeHomogenous(pt);
        bb.union(pt.x, pt.y, pt.z);
        
        return bb.toBounds();
    }
    
    private void normalizeHomogenous(CyVector4d pt)
    {
        double inv = 1 / pt.w;
        pt.x *= inv;
        pt.y *= inv;
        pt.z *= inv;
    }
    
    public BoundingBox3d union(CyVector3d... pts)
    {
        double x0 = this.x0;
        double y0 = this.y0;
        double z0 = this.z0;
        double x1 = this.x1;
        double y1 = this.y1;
        double z1 = this.z1;
        
        for (CyVector3d pt: pts)
        {
            x0 = Math.min(x0, pt.x);
            x1 = Math.max(x1, pt.x);
            y0 = Math.min(y0, pt.y);
            y1 = Math.max(y1, pt.y);
            z0 = Math.min(z0, pt.z);
            z1 = Math.max(z1, pt.z);
        }
        
        return new BoundingBox3d(x0, y0, z0, x1, y1, z1);
    }

    public boolean contains(CyVector3d pt)
    {
        return contains(pt.x, pt.y, pt.z);
    }
    
    public boolean contains(double x, double y, double z)
    {
        return x >= x0 && x <= x1 &&
                 y >= y0 && y <= y1 &&
                 z >= z0 && z <= z1;
    }

    /**
     * Deterimes if this bounding object, plus a margin of 'margin' around it
     * is entirely within 'bounds'.
     */
    public boolean isWithin(double margin, BoundingBox3d bounds)
    {
        return x0 - margin >= bounds.x0 &&
                y0 - margin >= bounds.y0 &&
                z0 - margin >= bounds.z0 &&
                x1 + margin <= bounds.x1 &&
                y1 + margin <= bounds.y1 &&
                z1 + margin <= bounds.z1;
    }
    
    /**
     * Checks if this box plus a margin of margin on all sides contains the point (x, y, z).
     */
    public boolean contains(double margin, double x, double y, double z)
    {
        return x >= x0 - margin && x <= x1 + margin &&
                 y >= y0 - margin && y <= y1 + margin &&
                 z >= z0 - margin && z <= z1 + margin;
    }
    
    /**
     * Deterimes if this bounding object, plus a margin of 'margin' around it
     * contains 'bounds'.
     */
    public boolean contains(double margin, BoundingBox3d bounds)
    {
        return x0 - margin <= bounds.x0 &&
                y0 - margin <= bounds.y0 &&
                z0 - margin <= bounds.z0 &&
                x1 + margin >= bounds.x1 &&
                y1 + margin >= bounds.y1 &&
                z1 + margin >= bounds.z1;
    }
    
    public boolean intersects(BoundingBox3d bounds)
    {
        //Check if partition plane exists for any of the six faces
        return !(x1 <= bounds.x0 ||
                y1 <= bounds.y0 ||
                z1 <= bounds.z0 ||
                x0 >= bounds.x1 ||
                y0 >= bounds.y1 ||
                z0 >= bounds.z1);
    }

    /**
     * Deterimes if this bounding object, plus a margin of 'margin' around it
     * intersects the bounding box 'bounds'.
     */
    public boolean intersects(double margin, BoundingBox3d bounds)
    {
        //Check if partition plane exists for any of the six faces
        return !(x1 + margin <= bounds.x0 ||
                y1 + margin <= bounds.y0 ||
                z1 + margin <= bounds.z0 ||
                x0 - margin >= bounds.x1 ||
                y0 - margin >= bounds.y1 ||
                z0 - margin >= bounds.z1);
    }

    public CyVector3d getPoint(int octant, CyVector3d retPoint)
    {
        if (retPoint == null) retPoint = new CyVector3d();
        
        retPoint.x = (octant & OCTANT_X) != 0 ? x1 : x0;
        retPoint.y = (octant & OCTANT_Y) != 0 ? y1 : y0;
        retPoint.z = (octant & OCTANT_Z) != 0 ? z1 : z0;
        
        return retPoint;
    }
    
    public CyVector3d getPoint(int octant, CyVector3d retPoint, double margin)
    {
        if (retPoint == null) retPoint = new CyVector3d();
        
        retPoint.x = (octant & OCTANT_X) != 0 ? x1 + margin : x0 - margin;
        retPoint.y = (octant & OCTANT_Y) != 0 ? y1 + margin : y0 - margin;
        retPoint.z = (octant & OCTANT_Z) != 0 ? z1 + margin : z0 - margin;
        
        return retPoint;
    }
    
    /**
     * Performs an inside/oputside/spanning test for this bounding box against 
     * an arbitrary plane
     * @param plane Plane to test against
     */
    public PartitionPlacement testPlane(CyPlane4d plane)
    {
        int normOct = getOctant(plane.x, plane.y, plane.z);
        CyVector3d testPoint = new CyVector3d();
        
        getPoint(normOct, testPoint);
        double farVal = plane.x * testPoint.x + plane.y * testPoint.y + plane.z * testPoint.z + plane.w;
        
        if (farVal < 0) 
        {
            return PartitionPlacement.OUTSIDE;
        }
        
        getPoint(getOppositeOctant(normOct), testPoint);
        double nearVal = plane.x * testPoint.x + plane.y * testPoint.y + plane.z * testPoint.z + plane.w;
        
        if (nearVal > 0) 
        {
            return PartitionPlacement.INSIDE;
        }
        
        return PartitionPlacement.SPANNING;
    }
    
    /**
     * Performs an inside/oputside/spanning test for this bounding box against 
     * an arbitrary plane
     * @param plane Plane to test against
     * @param margin A margin to increase the dimensions of the cube by for the
     * calculation
     */
    public PartitionPlacement testPlane(CyPlane4d plane, double margin)
    {
        int normOct = getOctant(plane.x, plane.y, plane.z);
        CyVector3d testPoint = new CyVector3d();
        
        getPoint(normOct, testPoint, margin);
        double farVal = plane.x * testPoint.x + plane.y * testPoint.y + plane.z * testPoint.z + plane.w;
        
        if (farVal < 0) 
        {
            return PartitionPlacement.OUTSIDE;
        }
        
        getPoint(getOppositeOctant(normOct), testPoint, margin);
        double nearVal = plane.x * testPoint.x + plane.y * testPoint.y + plane.z * testPoint.z + plane.w;
        
        if (nearVal > 0) 
        {
            return PartitionPlacement.INSIDE;
        }
        
        return PartitionPlacement.SPANNING;
    }
    
    public double getDiagonal()
    {
        return Math.sqrt(getDiagonalSquared());
    }
    
    public double getDiagonalSquared()
    {
        return square(x1 - x0) + square(y1 - y0) + square(z1 - z0);
    }
    
    private double square(double v)
    {
        return v * v;
    }
    
    public double getCenterX()
    {
        return (x0 + x1) / 2;
    }
    
    public double getCenterY()
    {
        return (y0 + y1) / 2;
    }
    
    public double getCenterZ()
    {
        return (z0 + z1) / 2;
    }

    public CyVector3d getCenter(CyVector3d center)
    {
        center.set(getCenterX(), getCenterY(), getCenterZ());
        return center;
    }
    
    public double getX0()
    {
        return x0;
    }
    
    public double getY0()
    {
        return y0;
    }
    
    public double getZ0()
    {
        return z0;
    }
    
    public double getX1()
    {
        return x1;
    }
    
    public double getY1()
    {
        return y1;
    }
    
    public double getZ1()
    {
        return z1;
    }
    
    public double getXSpan()
    {
        return x1 - x0;
    }
    
    public double getYSpan()
    {
        return y1 - y0;
    }
    
    public double getZSpan()
    {
        return z1 - z0;
    }

    public int hashCode()
    {
        return hashCode;
    }
    
    public boolean equals(Object obj)
    {
        if (!(obj instanceof BoundingBox3d)) return false;
        BoundingBox3d b = (BoundingBox3d)obj;
        return b.x0 == x0 && b.y0 == y0 && b.z0 == z0 &&
                b.x1 == x1 && b.y1 == y1 && b.z1 == z1;
    }
    
    public String toString()
    {
        return String.format("(%f, %f, %f) - (%f, %f, %f)", x0, y0, z0, x1, y1, z1);
    }
}
