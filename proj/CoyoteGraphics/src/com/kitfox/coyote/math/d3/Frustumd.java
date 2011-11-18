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
import static com.kitfox.coyote.math.d3.PartitionPlacement.*;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Extracts and manages the frutsum planes from a projection * modelview matrix
 *
 * http://www.cs.otago.ac.nz/postgrads/alexis/planeExtraction.pdf
 *
 * @author kitfox
 */
public class Frustumd
{
    public enum Plane
    {
        LEFT, RIGHT, BOTTOM, TOP, NEAR, FAR
    };
    final CyPlane4d[] planes = new CyPlane4d[Plane.values().length];

    /** Creates a new instance of FrutsumPlanes */
    public Frustumd()
    {
        for (int i = 0; i < planes.length; i++)
        {
            planes[i] = new CyPlane4d();
        }
    }

    public Frustumd(Frustumd f)
    {
        for (int i = 0; i < planes.length; i++)
        {
            planes[i] = new CyPlane4d(f.planes[i]);
        }
    }

    public static Frustumd create(CyMatrix4d viewProjection)
    {
        Frustumd frustum = new Frustumd();
        frustum.extractPlanes(viewProjection);
        return frustum;
    }

    public CyPlane4d getPlane(Plane plane)
    {
        return planes[plane.ordinal()];
    }

    /**
     * Extracts the planes from a perspective projection matrix.  The persp proj matrix can be 
     * premultiplied by a modelview matrix to extract planes in world space or
     * object space.
     *
     * @param viewProjection - Matrix which maps from world space to clip
     * space.  Normally for projection matrix P and modelview matrix V this
     * would be V * P.
     *
     * Eg,
     *
    Matrix4d projection = new Matrix4d();
    Matrix4d modelViewProj = new Matrix4d();
    MatrixOps.frustumPersp(projection, 90, 1, 1, 1000);
    modelViewProj.set(projection);  //Maps view to clip
    modelViewProj.mul(modelToView);  //maps world or model to view
    frustum.extractPerspectivePlanes(modelViewProj);
     */
    public void extractPlanes(CyMatrix4d viewProjection)
    {
        //VP * (1 0 0 1)
        CyPlane4d left = planes[Plane.LEFT.ordinal()];
        left.x = viewProjection.m30 + viewProjection.m00;
        left.y = viewProjection.m31 + viewProjection.m01;
        left.z = viewProjection.m32 + viewProjection.m02;
        left.w = viewProjection.m33 + viewProjection.m03;

        //VP * (-1 0 0 1)
        CyPlane4d right = planes[Plane.RIGHT.ordinal()];
        right.x = viewProjection.m30 - viewProjection.m00;
        right.y = viewProjection.m31 - viewProjection.m01;
        right.z = viewProjection.m32 - viewProjection.m02;
        right.w = viewProjection.m33 - viewProjection.m03;

        //VP * (0 1 0 1)
        CyPlane4d bottom = planes[Plane.BOTTOM.ordinal()];
        bottom.x = viewProjection.m30 + viewProjection.m10;
        bottom.y = viewProjection.m31 + viewProjection.m11;
        bottom.z = viewProjection.m32 + viewProjection.m12;
        bottom.w = viewProjection.m33 + viewProjection.m13;

        //VP * (0 -1 0 1)
        CyPlane4d top = planes[Plane.TOP.ordinal()];
        top.x = viewProjection.m30 - viewProjection.m10;
        top.y = viewProjection.m31 - viewProjection.m11;
        top.z = viewProjection.m32 - viewProjection.m12;
        top.w = viewProjection.m33 - viewProjection.m13;

        //VP * (0 0 1 1)
        CyPlane4d near = planes[Plane.NEAR.ordinal()];
        near.x = viewProjection.m30 + viewProjection.m20;
        near.y = viewProjection.m31 + viewProjection.m21;
        near.z = viewProjection.m32 + viewProjection.m22;
        near.w = viewProjection.m33 + viewProjection.m23;

        //VP * (0 0 -1 1)
        CyPlane4d far = planes[Plane.FAR.ordinal()];
        far.x = viewProjection.m30 - viewProjection.m20;
        far.y = viewProjection.m31 - viewProjection.m21;
        far.z = viewProjection.m32 - viewProjection.m22;
        far.w = viewProjection.m33 - viewProjection.m23;

        //Normalize plane normals
        left.normalize();
        right.normalize();
        bottom.normalize();
        top.normalize();
        near.normalize();
        far.normalize();
    }

    /**
     * Tests a point against a plane.
     * @return 1 if point on positive side of the plane, -1 if on the negative side,
     * 0 if on the plane itself.
     */
    public PartitionPlacement testPoint(Plane plane, CyVector3d point)
    {
        double dist = distToPlane(plane, point);

        if (dist < 0)
        {
            return OUTSIDE;
        }

        if (dist > 0)
        {
            return INSIDE;
        }

        return SPANNING;
    }

    public PartitionPlacement testSphere(Plane plane, CyVector3d point, double radius)
    {
        double dist = distToPlane(plane, point);

        if (dist < radius)
        {
            return OUTSIDE;
        }

        if (dist > radius)
        {
            return INSIDE;
        }

        return SPANNING;
    }

    public double distToPlane(Plane plane, CyVector3d point)
    {
        //Note for normalized plane ax + by + cz + d = 0, d is the distance from the origin to the plane
        CyPlane4d planeVec = planes[plane.ordinal()];
        return planeVec.distanceToPlane(point);
//        return planeVec.x * point.x + planeVec.y * point.y + planeVec.z * point.z + planeVec.w;
    }

    public PartitionPlacement testBoundingSphere(double radiusSquared, CyVector3d center)
    {
        return testBoundingSphere(radiusSquared, center.x, center.y, center.z);
    }

    /**
     * Tests bounding sphere against planes of frustum.  
     *
     * @param radiusSquared - Radius of sphere.  Must be positive
     * @param center - center of sphere
     */
    public PartitionPlacement testBoundingSphere(double radiusSquared, double cx, double cy, double cz)
    {
        boolean touches = false;

        //Check bounding sphere
        for (Plane plane : Plane.values())
        {
            double dist = planes[plane.ordinal()].distanceToPlane(cx, cy, cz);
            if (dist < 0)
            {
                if (radiusSquared < dist * dist)
                {
                    return OUTSIDE;
                }
                touches = true;
            } else
            {
                if (radiusSquared > dist * dist)
                {
                    touches = true;
                }
            }
        }

        return touches ? SPANNING : INSIDE;
    }

    /**
     * Tests a bounding box against one of the planes of this frustum.  Does not do
     * bounding sphere test.
     * @return BACK if box is outside of frustum, TOUCHES if the box stradles at
     * least one frustum plane, or FRONT if it is entirely within the frustum.
     */
    public PartitionPlacement testBoundingBox(BoundingBox3d bounds)
    {
        boolean touches = false;
        //Check bounding box
        for (Plane plane : Plane.values())
        {
            PartitionPlacement res = bounds.testPlane(planes[plane.ordinal()]);
            if (res == OUTSIDE)
            {
                return OUTSIDE;
            } else
            {
                if (res == SPANNING)
                {
                    touches = true;
                }
            }
        }

        return touches ? SPANNING : INSIDE;
    }

    /**
     * Tests a bounding box against one of the planes of this frustum.  Does not do
     * bounding sphere test.
     * @param margin - Adds an extra margin around the bounding box 
     * @return BACK if box is outside of frustum, TOUCHES if the box stradles at
     * least one frustum plane, or FRONT if it is entirely within the frustum.
     */
    public PartitionPlacement testBoundingBox(BoundingBox3d bounds, double margin)
    {
        boolean touches = false;
        for (Plane plane : Plane.values())
        {
            PartitionPlacement res = bounds.testPlane(planes[plane.ordinal()], margin);
            if (res == OUTSIDE)
            {
                return OUTSIDE;
            } else
            {
                if (res == SPANNING)
                {
                    touches = true;
                }
            }
        }

        return touches ? SPANNING : INSIDE;
    }

    @Override
    public String toString()
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("left: " + planes[Plane.LEFT.ordinal()]);
        pw.println("right: " + planes[Plane.RIGHT.ordinal()]);
        pw.println("bottom: " + planes[Plane.BOTTOM.ordinal()]);
        pw.println("top: " + planes[Plane.TOP.ordinal()]);
        pw.println("near: " + planes[Plane.NEAR.ordinal()]);
        pw.println("far: " + planes[Plane.FAR.ordinal()]);

        pw.close();
        return sw.toString();
    }

//    /**
//     * This builds the OpenGL gluLookAt matrix.
//     * Taken from Mesa 3.5
//     */
//    public static CyMatrix4d gluLookAt(CyVector3d eye, CyVector3d center, CyVector3d up, CyMatrix4d m)
//    {
//        CyVector3d z = new CyVector3d(eye.x - center.x, eye.y - center.y, eye.z - center.z);
//        z.normalize();
//
//        CyVector3d y = new CyVector3d(up);
//
//        CyVector3d x = new CyVector3d();
//        x.cross(y, z);
//
//        y.cross(z, x);
//
//        x.normalize();
//        y.normalize();
//
//        //Since x, y, z is orthogonal, Given B = [x y z] then B^-1 = B^t.
//        // That's why the basis vectors are being put in the rows instead of columns.
//        m.m00 = x.x;
//        m.m01 = x.y;
//        m.m02 = x.z;
//        m.m03 = -eye.x * x.x + -eye.y * x.y + -eye.z * x.z;
//        m.m10 = y.x;
//        m.m11 = y.y;
//        m.m12 = y.z;
//        m.m13 = -eye.x * y.x + -eye.y * y.y + -eye.z * y.z;
//        m.m20 = z.x;
//        m.m21 = z.y;
//        m.m22 = z.z;
//        m.m23 = -eye.x * z.x + -eye.y * z.y + -eye.z * z.z;
//        m.m30 = 0;
//        m.m31 = 0;
//        m.m32 = 0;
//        m.m33 = 1;
//
//        return m;
//    }
//
//    public static CyMatrix4d gluOrtho2D(double left, double right, double bottom, double top, CyMatrix4d m)
//    {
//        return glOrtho(left, right, bottom, top, -1f, 1f, m);
//    }
//
//    public static CyMatrix4d glOrtho(double left, double right, double bottom, double top, double near, double far, CyMatrix4d m)
//    {
//        m.m00 = 2 / (right - left);
//        m.m01 = 0;
//        m.m02 = 0;
//        m.m03 = -(right + left) / (right - left);
//        m.m10 = 0;
//        m.m11 = 2 / (top - bottom);
//        m.m12 = 0;
//        m.m13 = -(top + bottom) / (top - bottom);
//        m.m20 = 0;
//        m.m21 = 0;
//        m.m22 = -2 / (far - near);
//        m.m23 = -(far + near) / (far - near);
//        m.m30 = 0;
//        m.m31 = 0;
//        m.m32 = 0;
//        m.m33 = 1;
//
//        return m;
//    }
//
//    public static CyMatrix4d glFrustum(double left, double right, double bottom, double top, double near, double far, CyMatrix4d m)
//    {
//        double x = (2 * near) / (right - left);
//        double y = (2 * near) / (top - bottom);
//        double a = (right + left) / (right - left);
//        double b = (top + bottom) / (top - bottom);
//        double c = -(far + near) / (far - near);
//        double d = -(2 * far * near) / (far - near);
//
//        m.m00 = x;
//        m.m01 = 0;
//        m.m02 = a;
//        m.m03 = 0;
//        m.m10 = 0;
//        m.m11 = y;
//        m.m12 = b;
//        m.m13 = 0;
//        m.m20 = 0;
//        m.m21 = 0;
//        m.m22 = c;
//        m.m23 = d;
//        m.m30 = 0;
//        m.m31 = 0;
//        m.m32 = -1;
//        m.m33 = 0;
//
//        return m;
//    }
//
//    public static CyMatrix4d gluPerspective(double fovy, double aspect, double zNear, double zFar, CyMatrix4d m)
//    {
//        double ymax = zNear * Math.tan(fovy * Math.PI / 360.0);
//        double ymin = -ymax;
//        double xmin = ymin * aspect;
//        double xmax = ymax * aspect;
//
//        return glFrustum(xmin, xmax, ymin, ymax, zNear, zFar, m);
//    }
//
//    public static CyMatrix4d gluPickMatrix(double x, double y, double width, double height, Rectangle viewport, CyMatrix4d m)
//    {
//        double sx = viewport.width / width;
//        double sy = viewport.height / height;
//        double tx = (viewport.width + 2 * (viewport.x - x)) / width;
//        double ty = (viewport.height + 2 * (viewport.y - y)) / height;
//
//        m.m00 = sx;
//        m.m01 = 0;
//        m.m02 = 0;
//        m.m03 = tx;
//        m.m10 = 0;
//        m.m11 = sy;
//        m.m12 = 0;
//        m.m13 = ty;
//        m.m20 = 0;
//        m.m21 = 0;
//        m.m22 = 1;
//        m.m23 = 0;
//        m.m30 = 0;
//        m.m31 = 0;
//        m.m32 = 0;
//        m.m33 = 1;
//
//        return m;
//    }
}
