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

import com.kitfox.coyote.math.CyMatrix3d;
import com.kitfox.coyote.math.CyVector3d;
import com.kitfox.coyote.math.CyVector4d;

/**
 * Represents a plane in 3-space.  This is a plane in Hermatian form.
 *
 * Represents the plane c0 * x + c1 * y + c2 * z + c3 = 0, where the
 * plane coefficients are stored as (x, y, z, w).
 *
 * @author kitfox
 */
public class CyPlane4d extends CyVector4d
{
    private static final long serialVersionUID = 0;

    /** Creates a new instance of Plane4f */
    public CyPlane4d()
    {
    }
    
    public CyPlane4d(CyVector4d tuple)
    {
        super(tuple);
    }
    
    /**
     * Returns Hermatian plane that passes through the three plane points.
     * Points must not be colinear.  Plane will not necessarily be normal.
     */
    public CyPlane4d(CyVector3d pt0, CyVector3d pt1, CyVector3d pt2)
    {
        set(pt0, pt1, pt2);
    }
    
    public CyPlane4d(double x0, double y0, double z0, double x1, double y1, double z1, double x2, double y2, double z2)
    {
        set(x0, y0, z0, x1, y1, z1, x2, y2, z2);
    }
    
    /**
     * Creates a plane with the given normal direction and point on plane.
     * Note that if planeDirection is unit length, this plane will be in 
     * normal form.
     */
    public CyPlane4d(CyVector3d pointOnPlane, CyVector3d planeDirection)
    {
        set(pointOnPlane, planeDirection);
    }
    
    public void normalize()
    {
        //Set normal components (not the fourth) to unit vector
        double scalar = (double)Math.sqrt(x * x + y * y + z * z);
        scale(1 / scalar);
    }
    
    public void set(double x0, double y0, double z0, double x1, double y1, double z1, double x2, double y2, double z2)
    {
        CyVector3d v0 = new CyVector3d(x1 - x0, y1 - y0, z1 - z0);
        CyVector3d v1 = new CyVector3d(x2 - x0, y2 - y0, z2 - z0);
        CyVector3d cross = new CyVector3d();
        cross.cross(v0, v1);

        CyVector3d planeDirection = cross;
        double dot = x0 * planeDirection.x + y0 * planeDirection.y + z0 * planeDirection.z;
        
        x = planeDirection.x;
        y = planeDirection.y;
        z = planeDirection.z;
        w = -dot;
    }
    
    public void set(CyVector3d pt0, CyVector3d pt1, CyVector3d pt2)
    {
        CyVector3d v0 = new CyVector3d(pt1.x - pt0.x, pt1.y - pt0.y, pt1.z - pt0.z);
        CyVector3d v1 = new CyVector3d(pt2.x - pt0.x, pt2.y - pt0.y, pt2.z - pt0.z);
        CyVector3d cross = new CyVector3d();
        cross.cross(v0, v1);

        CyVector3d pointOnPlane = pt0;
        CyVector3d planeDirection = cross;
        double dot = pointOnPlane.x * planeDirection.x + pointOnPlane.y * planeDirection.y + pointOnPlane.z * planeDirection.z;
        
        x = planeDirection.x;
        y = planeDirection.y;
        z = planeDirection.z;
        w = -dot;
    }
    
    public void set(CyVector3d pointOnPlane, CyVector3d planeDirection)
    {
        double dot = pointOnPlane.x * planeDirection.x + pointOnPlane.y * planeDirection.y + pointOnPlane.z * planeDirection.z;
        
        x = planeDirection.x;
        y = planeDirection.y;
        z = planeDirection.z;
        w = -dot;
    }
    
    /**
     * Finds intersection between a line and a plane in Hessian normal form.
     *
     * http://cgafaq.info/wiki/Ray_Plane_Intersection
     *
     * @return the point of intersection, or null if point and plane are parallel.
     */
    public CyVector3d intersectionLinePlane(CyVector3d linePoint, CyVector3d lineNorm, CyVector3d retPoint)
    {
        CyVector3d planeNorm = new CyVector3d(x, y, z);
        double denom = planeNorm.dot(lineNorm);
        
        if (denom == 0) return null;
        
        //Vector from point on plane to point on line is E = linePoint - planePoint.  A point on the plane is -d(a, b, c)
        CyVector3d planeLineSep = new CyVector3d(x, y, z);
        planeLineSep.scale(w);
        planeLineSep.add(linePoint);
        
        double t = -(planeNorm.dot(planeLineSep)) / denom;

        retPoint.set(lineNorm);
        retPoint.scale(t);
        retPoint.add(linePoint);
        
        return retPoint;
    }
    
    /**
     * http://cgafaq.info/wiki/Intersection_Of_Three_Planes
     * http://mathworld.wolfram.com/HessianNormalForm.html
     *
     * @return the point where all three planes intersect, or null if any planes
     * are parallel.
     */
    static public CyVector3d intersectionOf3Planes(CyPlane4d plane1, CyPlane4d plane2, CyPlane4d plane3, CyVector3d retPoint)
    {
        CyVector3d N1 = new CyVector3d(plane1.x, plane1.y, plane1.z);
        CyVector3d N2 = new CyVector3d(plane2.x, plane2.y, plane2.z);
        CyVector3d N3 = new CyVector3d(plane3.x, plane3.y, plane3.z);
        CyVector3d N12 = new CyVector3d();
        CyVector3d N23 = new CyVector3d();
        CyVector3d N31 = new CyVector3d();
        N12.cross(N1, N2);
        N23.cross(N2, N3);
        N31.cross(N3, N1);
        
        double denom = N1.dot(N23);
        if (denom == 0) return null;
        
        if (retPoint == null) retPoint = new CyVector3d();

        retPoint.addScaleOf(N12, plane3.w);
        retPoint.addScaleOf(N23, plane1.w);
        retPoint.addScaleOf(N31, plane2.w);

//        retPoint.scaleAdd(plane3.w, N12, retPoint);
//        retPoint.scaleAdd(plane1.w, N23, retPoint);
//        retPoint.scaleAdd(plane2.w, N31, retPoint);
        
        retPoint.scale(1 / denom);
        
        return retPoint;
    }
    
    /**
     * Distance from point to plane where if plane = (a, b, c, d) and a plane
     * is defined by ax + by + cz + d = 0.  
     * 
     * Plane must be in normal form.
     *
     * Result is signed, with positive values lying on the positive half space
     * defined by the plane.
     *
     * http://cgafaq.info/wiki/Point_Plane_Distance
     */
    public double distanceToPlane(double cx, double cy, double cz)
    {
        return x * cx + y * cy + z * cz + w;
    }

    public double distanceToPlane(CyVector3d point)
    {
        return distanceToPlane(point.x, point.y, point.z);
    }

    /**
     * Returns projection of point onto plane along plane's normal.
     * Plane must be in normal form
     */
    public CyVector3d projectToPlane(CyVector3d point, CyVector3d retPoint)
    {
        if (retPoint == null)
        {
            retPoint = new CyVector3d(point);
        }
        else if (retPoint != point)
        {
            retPoint.set(point);
        }
        double dist = -distanceToPlane(point);
        retPoint.x += dist * x;
        retPoint.y += dist * y;
        retPoint.z += dist * z;
        
        return retPoint;
    }
    
    /**
     * @return minimal vector from origin to a point on the plane.  This vector
     * will be parallel to the plane normal.
     */
    public CyVector3d displacementFromOrigin(CyVector3d retPoint)
    {
        if (retPoint == null) retPoint = new CyVector3d();
        
        retPoint.set(x, y, z);
        retPoint.scale(-w);
        
        return retPoint;
    }
    
    /**
     * Calculates the plane that partitions the space into points closer to p
     * and points closer to q.  Resultant plane will be precicely between p and q,
     * with a normal pointing towards q.  Plane is in Hessian normal form.
     */
    static public CyPlane4d separationPlane(CyVector3d p, CyVector3d q, CyPlane4d retPlane)
    {
        if (p.equals(q)) return null;
        
        if (retPlane == null) retPlane = new CyPlane4d();
        
        CyVector3d pq = new CyVector3d(q);
        pq.sub(p);
        
        //Extract plane components
        double a = pq.x;
        double b = pq.y;
        double c = pq.z;

        double norm = 1 / (double)Math.sqrt(a * a + b * b + c * c);
        a *= norm;
        b *= norm;
        c *= norm;
        
        //Set ab to the point between A and B
        pq.scale(.5);
        pq.add(p);
//        pq.scaleAdd(.5f, p);
  
        double d = -(a * pq.x + b * pq.y + c * pq.z);
        
        retPlane.set(a, b, c, d);
        
        return retPlane;
    }
    
//    /**
//     * Use least squares method to find plane that best fits point set
//     */
//    static public CyVector4d fitPlane(Collection<? extends CyVector3d> points)
//    {
//        MatrixMNf mat = new MatrixMNf(points.size(), 3);
//        MatrixMNf slnMat = new MatrixMNf(points.size(), 1);
//        int index = 0;
//        for (CyVector3d t: points)
//        {
//            mat.set(index, 0, t.x);
//            mat.set(index, 1, t.y);
//            mat.set(index, 2, t.z);
//            slnMat.set(index, 0, 1);
//            index++;
//        }
//        MatrixMNf lsqMat = mat.leastSquaresMatrix();
//
//        MatrixMNf coeffMat = MatrixMNf.mul(lsqMat, slnMat);
//        return new CyVector4d(coeffMat.get(0, 0), coeffMat.get(1, 0), coeffMat.get(2, 0), -1);
//    }
    
    /**
     * Transforms this plane by given transform.  If this plane is described as
     * {n d}, where n is the normal vector of the plane and d is a delta, then
     * this plane contains all x s.t. n dot x + d = 0.
     *
     * We wish to transform by the affine transformation x' = Bx + c.
     *
     * @param B Rotational/scaling/shearing part of transform
     * @param c Translation part of transform
     * @param isOrthoB Hint to indicate if B is orthogonal (ie, a pure 
     * rotation or reflection matrix).  If so, then computing an inverse can 
     * be skipped.  Otherwise, the full algorithm will be used.  Set to false if
     * you're not sure.
     */
    public void transform(CyMatrix3d B, CyVector3d c, boolean isOrthoB)
    {
        CyVector3d nNorm = new CyVector3d(x, y, z);
        if (!isOrthoB)
        {
            CyMatrix3d mat = new CyMatrix3d();
            mat.invert(B);
            mat.transpose();
            B = mat;
        }
        B.transform(nNorm);
        
        double nDelta = w - nNorm.dot(c);
        
        x = nNorm.x;
        y = nNorm.y;
        z = nNorm.z;
        w = nDelta;
    }
    
    public CyPlane4d transform(CyMatrix3d B, CyVector3d c, boolean isOrthoB, CyPlane4d dest)
    {
        CyVector3d nNorm = new CyVector3d(x, y, z);
        if (!isOrthoB)
        {
            CyMatrix3d mat = new CyMatrix3d();
            mat.invert(B);
            mat.transpose();
            B = mat;
        }
        B.transform(nNorm);
        
        double nDelta = w - nNorm.dot(c);
        
        dest.x = nNorm.x;
        dest.y = nNorm.y;
        dest.z = nNorm.z;
        dest.w = nDelta;
        return dest;
    }

    /**
     * @return true if point is within plane's halfspace
     */
    public boolean isInside(CyVector3d point)
    {
        double value = point.x * x + point.y * y + point.z * z + w;
        return value > 0;
    }

    public boolean isOutside(CyVector3d point)
    {
        double value = point.x * x + point.y * y + point.z * z + w;
        return value < 0;
    }
}
