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

import static java.lang.Math.*;

/**
 *
 * @author kitfox
 */
public class Math2DUtil
{
    public static boolean pointEquals(double px, double py, double qx, double qy)
    {
        return px == qx && py == qy;
    }

    public static double distSquared(double ax, double ay, double bx, double by)
    {
        double dx = bx - ax;
        double dy = by - ay;
        return dx * dx + dy * dy;
    }

    public static double dist(double ax, double ay, double bx, double by)
    {
        return sqrt(distSquared(ax, ay, bx, by));
    }

    public static double lerp(double v0, double v1, double t)
    {
        return v0 * (1 - t) + v1 * t;
    }

    public static double dot(double ax, double ay, double bx, double by)
    {
        return ax * bx + ay * by;
    }

    public static double distAlongRaySquared(double px, double py,
            double qx, double qy, double rx, double ry)
    {
        double sx = px - qx;
        double sy = py - qy;

        double num = dot(sx, sy, rx, ry);
        return num * num / dot(rx, ry, rx, ry);
    }

    public static double distAlongRay(double px, double py,
            double qx, double qy, double rx, double ry)
    {
        double sx = px - qx;
        double sy = py - qy;

        return dot(sx, sy, rx, ry) / sqrt(dot(rx, ry, rx, ry));
    }

    /**
     * @param px
     * @param py
     * @param qx
     * @param qy
     * @param rx
     * @param ry
     * @return Distance of projection of P onto the ray, expressed as a
     * scalar to the ray.
     */
    public static double fractionAlongRay(double px, double py,
            double qx, double qy, double rx, double ry)
    {
        double sx = px - qx;
        double sy = py - qy;

        return dot(sx, sy, rx, ry) / dot(rx, ry, rx, ry);
    }

    /**
     * Calculates square of min distance from point to an infinite line segment.
     * P - Point
     * Q - Point on line of reference
     * R - Vector indicating direction of line
     *
     * @param px
     * @param py
     * @param qx
     * @param qy
     * @param rx
     * @param ry
     * @return
     */
    public static double distPointLineSquared(double px, double py,
            double qx, double qy, double rx, double ry)
    {
        double sx = px - qx;
        double sy = py - qy;

        return dot(sx, sy, sx, sy) - distAlongRaySquared(px, py, qx, qy, rx, ry);
    }

    public static double distPointLine(double px, double py,
            double qx, double qy, double rx, double ry)
    {
        return sqrt(distPointLineSquared(px, py, qx, qy, rx, ry));
    }

    public static double distPointSegmentSquared(double px, double py,
            double qx, double qy, double rx, double ry)
    {
        double sx = px - qx;
        double sy = py - qy;

        //Multiplying r by this value will create r', 
        // the projection of s onto r
        double rScalar = dot(rx, ry, sx, sy) / dot(sx, sy, sx, sy);
        if (rScalar <= 0)
        {
            //before first point
            return distSquared(px, py, qx, qy);
        }
        else if (rScalar >= 1)
        {
            //After last point
            return distSquared(px, py, qx + rx, qy + ry);
        }
        
        return dot(sx, sy, sx, sy) - rScalar * rScalar * dot(rx, ry, rx, ry);
    }


    /**
     * Points to right of line segment have negative distance.
     */
    public static double distPointLineSigned(double px, double py,
            double qx, double qy, double rx, double ry)
    {
        //Calc eqn of line in Cartesian form
        
        //Get normal of line
        double magI = 1 / Math.sqrt(rx * rx + ry * ry);
        double nx = -ry * magI;
        double ny = rx * magI;
        
        double c = -(qx * nx + qy * ny);
        
        //Eqn of line is a * x + b * y + c = 0, where a == nx and b == ny
        return px * nx + py * ny + c;
    }

    /**
     * Find intersection of two line segments.  Lines are input in
     * point-ray form.  They are compared and the result array is filled with
     * scalars for the rays of the respective lines that will cause them to
     * meet at the same point.  If no such point exists (because the lines are
     * parallel), null is returned.
     *
     * Let a be scalar for line PR and b be scalar for line QS.  Then:
     * [a] = [qx - px] [rx  -sx]^-1
     * [b]   [qy - py] [ry  -sy]
     *
     * @param px
     * @param py
     * @param rx
     * @param ry
     * @param qx
     * @param qy
     * @param sx
     * @param sy
     * @return
     */
    public static double[] lineIsectFractions(
            double px, double py, double rx, double ry,
            double qx, double qy, double sx, double sy,
            double[] result)
    {
        double a = rx;
        double b = -sx;
        double c = ry;
        double d = -sy;

        double det = (a * d) - (b * c);
        if (det == 0)
        {
            return null;
        }

        if (result == null)
        {
            result = new double[2];
        }

        double detI = 1 / det;
        double dqx = qx - px;
        double dqy = qy - py;
        result[0] = (dqx * d - dqy * b) * detI;
        result[1] = (dqy * a - dqx * c) * detI;
        return result;
    }

    public static boolean isInsideTriangle(CyVector2d p0, CyVector2d p1, CyVector2d p2, CyVector2d pt)
    {
        CyVector2d v = getBarycentricCoordsPoints(p0, p1, p2, pt);

        //Should be in unit triangle in local space
        return v.x >= 0 && v.y >= 0 && v.x + v.y <= 1;
    }

    public static CyVector2d getBarycentricCoordsPoints(
            CyVector2d p0, CyVector2d p1, CyVector2d p2, CyVector2d pt)
    {
        return getBarycentricCoordsPointVec(p0,
                new CyVector2d(p1.x - p0.x, p1.y - p0.y),
                new CyVector2d(p2.x - p0.x, p2.y - p0.y),
                pt);
    }

    /**
     * Calc the coords of point pt in the space determined with an
     * origin at 'origin' and the basis [v0 v1]
     *
     * http://www.blackpawn.com/texts/pointinpoly/default.html
     *
     * @param origin
     * @param v0
     * @param v1
     * @param pt
     * @return
     */
    public static CyVector2d getBarycentricCoordsPointVec(
            CyVector2d origin, CyVector2d v0, CyVector2d v1, CyVector2d pt)
    {
        //We have a triangle with origin 'origin' and arms v0 and v1.
        // Find [u v] such that
        //     pt = origin + u * v0 + v * v1
        // Let v2 = pt - origin
        //     v2 = u * v0 + v * v1
        // Create two equations to solve for two unknowns
        //     v2 . v0 = (u * v0 + v * v1) . v0
        //     v2 . v1 = (u * v0 + v * v1) . v1
        // Distributing dot product:
        //     v2 . v0 = u * v0 . v0 + v * v1 . v0
        //     v2 . v1 = u * v0 . v1 + v * v1 . v1
        // Then solve as system of linear equations

        CyVector2d v2 = new CyVector2d(pt.x - origin.x, pt.y - origin.y);

        double dot00 = v0.dot(v0);
        double dot01 = v0.dot(v1);
        double dot02 = v0.dot(v2);
        double dot11 = v1.dot(v1);
        double dot12 = v1.dot(v2);

        double invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
        double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

        return new CyVector2d(u, v);
    }

    /**
     * Calculates the 'cross product' of two vectors.  This is the same as the
     * 3D version with the assumption that the vectors lie in the XY plane.  The
     * Z component of the cross product is returned.
     *
     * @param v0x
     * @param v0y
     * @param v1x
     * @param v1y
     * @return
     */
    public static double cross(double v0x, double v0y, double v1x, double v1y)
    {
        return v0x * v1y - v0y * v1x;
    }

    public static int cross(int v0x, int v0y, int v1x, int v1y)
    {
        return v0x * v1y - v0y * v1x;
    }

    /**
     * Determine which side of the given line (px, py) is on.
     * 
     * @param px X coord of point on line
     * @param py Y coord of point on line
     * @param rx X coord of ray of line direction
     * @param ry Y coord of ray of line direction
     * @param tx X coord of point to test against line
     * @param ty Y coord of point to test against line
     * @return > 0 if point is to left of line, < 0 if point is to right,
     * 0 if on line.
     */
    public static int getLineSide(int px, int py, int rx, int ry, int tx, int ty)
    {
        //General form of line is ax + by + c = 0
        // a = -ry;
        // b = rx;
        // c = ry * px - rx * py;
        //
        // side = a * tx + b * ty + c
        //      = (-ry * tx) + (rx * ty) + (ry * px - rx * py)
        
        return ry * (px - tx) + rx * (ty - py);
    }
}
