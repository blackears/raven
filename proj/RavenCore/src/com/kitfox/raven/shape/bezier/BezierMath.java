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

package com.kitfox.raven.shape.bezier;

import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.game.control.color.StrokeStyle.Cap;
import com.kitfox.game.control.color.StrokeStyle.Join;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
@Deprecated
public class BezierMath
{
    public static long square(long v)
    {
        return v * v;
    }

    public static double square(double v)
    {
        return v * v;
    }

    public static long distanceSquared(long p0x, long p0y, long p1x, long p1y)
    {
        return square(p1x - p0x) + square(p1y - p0y);
    }

    public static double distanceSquared(double p0x, double p0y, double p1x, double p1y)
    {
        return square(p1x - p0x) + square(p1y - p0y);
    }

    public static double distance(double p0x, double p0y, double p1x, double p1y)
    {
        return Math.sqrt(distanceSquared(p0x, p0y, p1x, p1y));
    }

    public static double lerp(double v0, double v1, double a)
    {
        return v0 * (1 - a) + v1 * a;
    }

    public static double[] intersectLines(double p0x, double p0y, double r0x, double r0y,
            double p1x, double p1y, double r1x, double r1y,
            double[] time)
    {
        if (time == null)
        {
            time = new double[2];
        }

        //Delta points
        double pdx = p1x - p0x;
        double pdy = p1y - p0y;

        //Calc coeff matrix
        double m00 = r0x;
        double m01 = r0y;
        double m10 = -r1x;
        double m11 = -r1y;

        //Calc inverse
        double det = m00 * m11 - m01 * m10;
        if (det == 0)
        {
            return null;
        }
        double detI = 1f / det;

        double n00 = detI * m11;
        double n01 = detI * -m01;
        double n10 = detI * -m10;
        double n11 = detI * m00;

        //Find time stops
        double t0 = pdx * n00 + pdy * n10;
        double t1 = pdx * n01 + pdy * n11;

        time[0] = t0;
        time[1] = t1;

        return time;
    }

    /**
     * Aproximates arc from p0 to p1 around circle with center at v.
     * 
     * @param p0
     * @param p1
     * @param v
     * @return
     */
    public static BezierCurve arcSegment(
            CyVector2d p0, CyVector2d p1,
            CyVector2d v)
    {
        //Calculate tangents as perpendicular to radius vector
        CyVector2d tan0 = new CyVector2d(p0.y - v.y, -(p0.x - v.x));
        CyVector2d tan1 = new CyVector2d(-(p1.y - v.y), p1.x - v.x);

        CyVector2d midSpan = new CyVector2d((p0.x + p1.x) / 2,
                (p0.y + p1.y) / 2);

        double radius = distance(p0.x, p0.y, v.x, v.y);
        double spanLenHalf = p0.distance(p1) / 2;
        CyVector2d p = new CyVector2d(midSpan.x - v.x, midSpan.y - v.y);
        p.scale((radius / Math.sqrt(radius * radius - spanLenHalf * spanLenHalf))
                - 1);

        //p should now be vector from middle of span to closest point on
        // circumference of circle

        //Calcuate tangents for radial arc on edge of circular join
        CyVector2d vIn = new CyVector2d(tan0);
        vIn.scale((4 * p.dot(p)) / (3 * p.dot(tan0)));
        CyVector2d vOut = new CyVector2d(tan1);
        vOut.scale(-(4 * p.dot(p)) / (3 * p.dot(tan1)));

        int a0x = (int)p0.x;
        int a0y = (int)p0.y;
        int a1x = (int)(p0.x + vIn.x);
        int a1y = (int)(p0.y + vIn.y);
        int a2x = (int)(p1.x + vOut.x);
        int a2y = (int)(p1.y + vOut.y);
        int a3x = (int)p1.x;
        int a3y = (int)p1.y;

        if ((a0x == a1x && a0y == a1y) || (a3x == a2x && a3y == a2y))
        {
            //Curve is smaller than integer resolution
            return new BezierCurveLine(
                    a0x, a0y,
                    a3x, a3y);
        }

        return new BezierCurveCubic(
                a0x, a0y,
                a1x, a1y,
                a2x, a2y,
                a3x, a3y);
    }

    public static void cap(double p0x, double p0y, double p1x, double p1y,
            Cap cap, ArrayList<BezierCurve> result)
    {
        if (p0x == p1x && p0y == p1y)
        {
            //Cap is a point
            return;
        }

        CyVector2d pt0 = new CyVector2d(p0x, p0y);
        CyVector2d pt1 = new CyVector2d(p1x, p1y);

        switch (cap)
        {
            case BUTT:
                result.add(new BezierCurveLine(pt0.x, pt0.y, pt1.x, pt1.y));
                break;
            case ROUND:
            {
                //Diameter rotated 90 degrees CCW
                CyVector2d perp = new CyVector2d(-(pt1.y - pt0.y), pt1.x - pt0.x);
                perp.scale(2 / 3.0);
                result.add(new BezierCurveCubic(
                        pt0.x, pt0.y,
                        pt0.x + perp.x, pt0.y + perp.y,
                        pt1.x + perp.x, pt1.y + perp.y,
                        pt1.x, pt1.y));
                break;
            }
            case SQUARE:
            {
                //Diameter rotated 90 degrees CCW
                CyVector2d perp = new CyVector2d(-(pt1.y - pt0.y), pt1.x - pt0.x);
                perp.scale(.5);

                result.add(new BezierCurveLine(pt0.x, pt0.y, pt0.x + perp.x, pt0.y + perp.y));
                result.add(new BezierCurveLine(pt0.x + perp.x, pt0.y + perp.y,
                        pt1.x + perp.x, pt1.y + perp.y));
                result.add(new BezierCurveLine(pt1.x + perp.x, pt1.y + perp.y, pt1.x, pt1.y));
                break;
            }
        }
    }

    public static double dot(double px, double py, double qx, double qy)
    {
        return px * qx + py * qy;
    }

    /**
     * Takes the 'cross product' in 2D space.  Returns magnitude of
     * z component of cross product taken in the XY plane.
     *
     * @param px
     * @param py
     * @param qx
     * @param qy
     * @return
     */
    public static double cross(double px, double py, double qx, double qy)
    {
        return px * qy - py * qx;
    }

    /**
     * Calculates gap decoration for a join.  Note that there must be a gap
     * between p0 and p1.  Do not call this routine if the contour is concave
     * at this point.
     *
     * @param p0
     * @param tanIn
     * @param p1
     * @param tanOut
     * @param v
     * @param join
     * @param miterLimit
     * @param result
     */
    public static void join(CyVector2d p0, CyVector2d tanIn,
            CyVector2d p1, CyVector2d tanOut, CyVector2d v,
            Join join, double miterLimit, ArrayList<BezierCurve> result)
    {
        double dx = p1.x - p0.x;
        double dy = p1.y - p0.y;
        if (dot(dx, dy, tanIn.y, tanIn.y) <= 0 || dot(dx, dy, tanOut.y, tanOut.y) <= 0)
        {
            //We're doubling back on ourselves.  Just span with a line
            // segment.
            result.add(new BezierCurveLine(p0.x, p0.y, p1.x, p1.y));
            return;
        }

        switch (join)
        {
            case BEVEL:
            {
                result.add(new BezierCurveLine(p0.x, p0.y, p1.x, p1.y));
                break;
            }
            case ROUND:
            {
                double t0x = tanIn.x;
                double t0y = tanIn.y;
                double t0MagI = 1 / Math.sqrt(t0x * t0x + t0y * t0y);
                t0x *= t0MagI;
                t0y *= t0MagI;

                double t1x = -tanOut.x;
                double t1y = -tanOut.y;
                double t1MagI = 1 / Math.sqrt(t1x * t1x + t1y * t1y);
                t1x *= t1MagI;
                t1y *= t1MagI;

                double pdx = p1.x - p0.x;
                double pdy = p1.y - p0.y;

//                double cosT0 = dot(t0x, t0y, pdx, pdy);
//                double cosT1 = dot(t1x, t1y, -pdx, -pdy);
//                double sinT0 = Math.abs(cross(t0x, t0y, pdx, pdy));
//                double sinT1 = Math.abs(cross(t1x, t1y, -pdx, -pdy));
//
//                double t1scalar = (2.0 / 3) / ((cosT0 * sinT1) / sinT0 + cosT1);
//                double t0scalar = t1scalar * sinT1 / sinT0;

                //Dropping diameter term since it cancels out
                double diam = distance(p0.x, p0.y, p1.x, p1.y);
                double diamI = 1 / diam;

                double cosT0 = dot(t0x, t0y, pdx, pdy) * diamI;
                double cosT1 = dot(t1x, t1y, -pdx, -pdy) * diamI;
                double sinT0 = Math.abs(cross(t0x, t0y, pdx, pdy) * diamI);
                double sinT1 = Math.abs(cross(t1x, t1y, -pdx, -pdy) * diamI);

                double t1scalar = (2.0 / 3) * diam / ((cosT0 * sinT1) / sinT0 + cosT1);
                double t0scalar = t1scalar * sinT1 / sinT0;


                int p0x = (int)p0.x;
                int p0y = (int)p0.y;
                int p1x = (int)p1.x;
                int p1y = (int)p1.y;
                int k0x = (int)(p0.x + t0x * t0scalar);
                int k0y = (int)(p0.y + t0y * t0scalar);
                int k1x = (int)(p1.x + t1x * t1scalar);
                int k1y = (int)(p1.y + t1y * t1scalar);

                if ((p0x == k0x && p0y == k0y) || (p1x == k1x && p1y == k1y))
                {
                    result.add(new BezierCurveLine(
                            p0x, p0y,
                            p1x, p1y
                            ));
                }
                else
                {
                    result.add(new BezierCurveCubic(
                            p0x, p0y,
                            k0x, k0y,
                            k1x, k1y,
                            p1x, p1y
                            ));
                }

                
//                double[] time = intersectLines(
//                        p0.x, p0.y, tanIn.x, tanIn.y,
//                        p1.x, p1.y, tanOut.x, tanOut.y, null);
//
//                //Find intersection of lines
//                double qx = p0.x + tanIn.x * time[0];
//                double qy = p0.y + tanIn.y * time[0];
//
//                result.add(new BezierCurveCubic(
//                        p0.x, p0.y,
//                        (qx - p0.x) * 3 / 4, (qy - p0.y) * 3 / 4,
//                        (qx - p1.x) * 3 / 4, (qy - p1.y) * 3 / 4,
//                        p1.x, p1.y
//                        ));

//                result.add(arcSegment(p0, p1, v));
                break;
            }
            case MITER:
            {
                double[] time = intersectLines(p0.x, p0.y, tanIn.x, tanIn.y,
                        p1.x, p1.y, tanOut.x, tanOut.y,
                        null);

                tanIn.scale(time[0]);
                tanOut.scale(time[1]);
                if (miterLimit > 0)
                {
                    if (tanIn.lengthSquared() > miterLimit * miterLimit)
                    {
                        tanIn.normalize();
                        tanIn.scale(miterLimit);
                        tanOut.normalize();
                        tanOut.scale(miterLimit);

                        result.add(new BezierCurveLine(
                                p0.x, p0.y, p0.x + tanIn.x, p0.y + tanIn.y));
                        result.add(new BezierCurveLine(
                                p0.x + tanIn.x, p0.y + tanIn.y,
                                p1.x + tanOut.x, p1.y + tanOut.y));
                        result.add(new BezierCurveLine(
                                p1.x + tanOut.x, p1.y + tanOut.y, p1.x, p1.y));
                        break;
                    }
                }

                result.add(new BezierCurveLine(
                        p0.x, p0.y, p0.x + tanIn.x, p0.y + tanIn.y));
                result.add(new BezierCurveLine(
                        p1.x + tanOut.x, p1.y + tanOut.y, p1.x, p1.y));
                break;
            }
        }
    }

//    public static void join(FlatSegmentList segsIn, FlatSegmentList segsOut, CyVector2d v,
//            Join join, double miterLimit, double flatnessSquared, ArrayList<BezierCurve> result)
//    {
////        FlatSegmentList segsIn, segsOut;
////
////        segsIn = edgeIn.getSegments();
////        segsOut = edgeOut.getSegments();
////
////        FlatSegmentList segsOutlineIn, segsOutlineOut;
////        if (joinLeft)
////        {
////            segsOutlineIn = edgeIn.getOffsetLeft();
////            segsOutlineOut = edgeOut.getOffsetLeft();
////        }
////        else
////        {
////            segsOutlineIn = edgeIn.getOffsetRight();
////            segsOutlineOut = edgeOut.getOffsetRight();
////        }
//
//        CyVector2d tanIn = new CyVector2d();
//        CyVector2d tanOut = new CyVector2d();
//        tanIn.set(segsIn.getTanOutX(), segsIn.getTanOutY());
//        tanOut.set(segsOut.getTanInX(), segsOut.getTanInY());
//
//
//        //Check if CCW angle between in and out tangents > 180 degrees.
//        // If convex, we need to clip line segments
//        if (tanOut.y * tanIn.x - tanOut.x * tanIn.y >= 0)
//        {
//            //In and out segments intersect.  Find point of intersection and
//            // cut there
//
//            double[] time = segsOut.findFirstIntersection(segsIn);
//            FlatSegmentList[] list = segsOut.split(time[0]);
//            segsOut = list[1];
//            segsOut.distributeTimesByDistance();
//
//            list = segsIn.split(time[1]);
//            segsIn = list[0];
//            //Make sure curves are aligned at cut point
//            FlatSegment headOut = segsOut.getHead();
//            FlatSegment tailIn = segsIn.getTail();
//            tailIn.x = headOut.x;
//            tailIn.y = headOut.y;
//            segsIn.distributeTimesByDistance();
//
//            result.add(segsIn);
//            result.add(segsOut);
//            return;
//        }
//
//        //We're going to have to decorate this join
//        result.add(segsIn);
//
//        FlatSegment headOut = segsOut.getHead();
//        FlatSegment tailIn = segsIn.getTail();
//        FlatSegment vertexSeg = segsIn.getTail();
//        switch (join)
//        {
//            case BEVEL:
//            {
//                FlatSegmentList list = new FlatSegmentList(1);
//                list.addSegment(tailIn.x, tailIn.y, 0);
//                list.addSegment(headOut.x, headOut.y, 1);
//                break;
//            }
//            case ROUND:
//            {
//                int vx = vertexSeg.x;
//                int vy = vertexSeg.y;
//
//                BezierCurveCubic curve =
//                        FlatSegmentList.calcJoinRound(segsIn, segsOut, vx, vy);
//
//                result.add(curve.getFlatSegments(flatnessSquared));
//                break;
//            }
//            case MITER:
//            {
//                int p0x = tailIn.x;
//                int p0y = tailIn.y;
//                int p1x = headOut.x;
//                int p1y = headOut.y;
//                double[] time = intersectLines(p0x, p0y, tanIn.x, tanIn.y,
//                        p1x, p1y, tanOut.x, tanOut.y,
//                        null);
//
////                BezierCurveLine line0 = new BezierCurveLine(
////                        p0x, p0y, p0x + tanIn.x, p0y + tanIn.y);
//
//                tanIn.scale(time[0]);
//                tanOut.scale(time[1]);
//                if (miterLimit > 0)
//                {
//                    if (tanIn.lengthSquared() > miterLimit * miterLimit)
//                    {
//                        tanIn.normalize();
//                        tanIn.scale(miterLimit);
//                        tanOut.normalize();
//                        tanOut.scale(miterLimit);
//
//                        result.add(new BezierCurveLine(
//                                p0x, p0y, p0x + tanIn.x, p0y + tanIn.y)
//                                .getFlatSegments(flatnessSquared));
//                        result.add(new BezierCurveLine(
//                                p1x + tanOut.x, p1y + tanOut.y, p1x, p1y)
//                                .getFlatSegments(flatnessSquared));
//                        result.add(new BezierCurveLine(
//                                p0x + tanIn.x, p0y + tanIn.y,
//                                p1x + tanOut.x, p1y + tanOut.y)
//                                .getFlatSegments(flatnessSquared));
//                        break;
//                    }
//                }
//
//                result.add(new BezierCurveLine(
//                        p0x, p0y, p0x + tanIn.x, p0y + tanIn.y)
//                        .getFlatSegments(flatnessSquared));
//                result.add(new BezierCurveLine(
//                        p1x + tanOut.x, p1y + tanOut.y, p1x, p1y)
//                        .getFlatSegments(flatnessSquared));
//                break;
//            }
//        }
//        result.add(segsOut);
//    }

}
