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

import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.math.GMatrix;
import com.kitfox.coyote.math.bezier.FitCurve;
import com.kitfox.coyote.shape.bezier.BezierCubic2d;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class PiecewiseBezierBuilder
{
    ArrayList<CyVector2d> fitPoints = new ArrayList<CyVector2d>();
    CyVector2d ptLast;

    //Used to track UI display curve while the stroke is being drawn
    ArrayList<FitCurveRecord> segments = new ArrayList<FitCurveRecord>();
//    OutlinerPath outlinerPath;
    static final int degree = 3;

    final double maxError;

//    private PathCurve displayPath;
    CyPath2d path;

    public PiecewiseBezierBuilder(double maxError)
    {
        this.maxError = maxError;
    }

    public BezierCubic2d getFirstSegment()
    {
        return segments.isEmpty() 
                ? null
                : segments.get(0).seg;
    }

    public BezierCubic2d getLastSegment()
    {
        return segments.isEmpty() 
                ? null
                : segments.get(segments.size() - 1).seg;
    }

    public void addPoint(CyVector2d pt)
    {
        if (ptLast != null)
        {
            if (ptLast.x == pt.x && ptLast.y == pt.y)
            {
                //Something other than position changed - like pressure
                //Replace end record with updated one
//                points.remove(points.size() - 1);
//                fitPoints.remove(fitPoints.size() - 1);
                return;
            }
        }
        fitPoints.add(pt);
        ptLast = pt;
        
        calculateNewSegments();
    }

    private FitCurveRecord fitCurve(ArrayList<CyVector2d> fitPoints, FitCurveRecord prevSeg)
    {
        //Prepare points for least squares fit
        GMatrix P = new GMatrix(fitPoints.size(), 2);
        for (int i = 0; i < fitPoints.size(); ++i)
        {
            CyVector2d pt = fitPoints.get(i);
            P.setElement(i, 0, pt.x);
            P.setElement(i, 1, pt.y);
        }

        //Point times are distributed according to distance from previous point
        double[] Ptimes = new double[fitPoints.size()];
        Ptimes[0] = 0;
        double dist = 0;
        for (int i = 1; i < fitPoints.size(); ++i)
        {
            CyVector2d pt0 = fitPoints.get(i - 1);
            CyVector2d pt1 = fitPoints.get(i);
            double dx = pt1.x - pt0.x;
            double dy = pt1.y - pt0.y;
            dist += Math.sqrt(dx * dx + dy * dy);
            Ptimes[i] = dist;
        }

        //Normalize times
        double distI = 1 / dist;
        for (int i = 1; i < fitPoints.size(); ++i)
        {
            Ptimes[i] *= distI;
        }

        BezierCubic2d seg = null;

        if (fitPoints.size() == 2)
        {
            //Fit line
            CyVector2d pt0 = fitPoints.get(0);
            CyVector2d pt1 = fitPoints.get(1);
            double dx = pt1.x - pt0.x;
            double dy = pt1.y - pt0.y;

            //Cubic form of line
            seg = new BezierCubic2d(
                    pt0.x, pt0.y,
                    pt0.x + dx / 3, pt0.y + dy / 3,
                    pt0.x + dx * 2 / 3, pt0.y + dy * 2 / 3,
                    pt1.x, pt1.y);
        }
        else if (fitPoints.size() == 3)
        {
            GMatrix Q = FitCurve.fitBezierKnots(2, Ptimes, P);

            double p0x = P.getElement(0, 0);
            double p0y = P.getElement(0, 1);
            double p1x = Q.getElement(0, 0);
            double p1y = Q.getElement(0, 1);
            double p2x = P.getElement(2, 0);
            double p2y = P.getElement(2, 1);

            //Convert quadratic curve to cubic
            seg = new BezierCubic2d(
                    p0x, p0y,
                    p0x + (p1x - p0x) * 2 / 3, p0y + (p1y - p0y) * 2 / 3,
                    p2x + (p1x - p2x) * 2 / 3, p2y + (p1y - p2y) * 2 / 3,
                    p2x, p2y);
        }
        else if (fitPoints.size() > 3)
        {
            GMatrix Q = FitCurve.fitBezierKnots(3, Ptimes, P);
            seg = new BezierCubic2d(
                    P.getElement(0, 0), P.getElement(0, 1),
                    Q.getElement(0, 0), Q.getElement(0, 1),
                    Q.getElement(1, 0), Q.getElement(1, 1),
                    P.getElement(P.getNumRow() - 1, 0), P.getElement(P.getNumRow() - 1, 1));
        }

        if (seg == null)
        {
            return null;
        }

//        if (prevSeg != null)
//        {
//            //Tweak this segement to it is C2 continous with prevoius segment
//            double tan0X = prevSeg.seg.getEndKnotX() - prevSeg.seg.getEndX();
//            double tan0Y = prevSeg.seg.getEndKnotY() - prevSeg.seg.getEndY();
//            double tan1X = seg.getStartKnotX() - seg.getStartX();
//            double tan1Y = seg.getStartKnotY() - seg.getStartY();
//
//            //Project tangent of this seg onto tangent of prev seg
//            //Note that to project vec B onto A then
//            // B' = (A / |A|) * |B| * cos AB
//            // B' = A * (A . B) / (A . A)
//            double scalar = (tan0X * tan1X + tan0Y * tan1Y) / (tan0X * tan0X + tan0Y * tan0Y);
////DEBUG
//if ((tan0X == 0 && tan0Y == 0) || (scalar == 0))
//{
//    int j = 9;
//}
//            seg.setStartKnotX(seg.getStartX() + (int)(tan0X * scalar));
//            seg.setStartKnotY(seg.getStartY() + (int)(tan0Y * scalar));
//        }

        double error = 0;
        CyVector2d evalPt = new CyVector2d();
        for (int i = 0; i < fitPoints.size(); ++i)
        {
            CyVector2d pt = fitPoints.get(i);
            seg.evaluate(Ptimes[i], evalPt);
            double dx = pt.x - evalPt.x;
            double dy = pt.y - evalPt.y;
//            double dx = pt.x - seg.calcPointX(Ptimes[i]);
//            double dy = pt.y - seg.calcPointY(Ptimes[i]);
            error += Math.sqrt(dx * dx + dy * dy);
        }

//        double w0 = fitPoints.get(0).pressure;
//        double w1 = fitPoints.get(fitPoints.size() - 1).pressure;
        return new FitCurveRecord(seg, error);
    }

    private void calculateNewSegments()
    {
        //Fit a curve to the last few points on the stack.  If error has
        // gotten too large, build a new path segment using the top (n - 1)
        // points on the stack and push onto our growing history list of
        // curve segments in the drawing path.

        //Note topmost segment is the 'current segment'.  It is popped and
        /// replaced every iteration until error becomes too great - at which
        // point the last good segment is pushed and a new top segment begine
        // to build.

        FitCurveRecord prevSeg = null;
        if (segments.size() >= 2)
        {
            prevSeg = segments.get(segments.size() - 2);
        }
        //Create new top record based on trailing points
        FitCurveRecord rec = fitCurve(fitPoints, prevSeg);
//DEBUG
//if (rec != null && rec.seg.getA0x() == rec.seg.getA1x()
//        && rec.seg.getA0y() == rec.seg.getA1y())
//{
//    rec = fitCurve(fitPoints, prevSeg);
//}

        if (rec != null)
        {
//if (rec.seg.getHullLength() > rec.seg.getBaseLength() * 10)
//{
//    int j = 9;
//}

//            if (outlinerPath == null)
//            {
//                outlinerPath = new OutlinerPath(10000);
//                outlinerPath.moveTo((int)rec.seg.getStartX(), 
//                        (int)rec.seg.getStartY(), 
//                        (float)rec.weight0);
//            }

            //We've accumulated too much error - push last good segment
            // and start fresh
            if (rec.error > maxError)
            {
                //Current segment on top of stack was calculated when
                // error was below threshold.  Keep it and add new
                // top record

                //Top two points on stack are end point of lastSeg and a new
                // currently unused point.  Discard all others.
                while (fitPoints.size() > 2)
                {
                    fitPoints.remove(0);
                }

                rec = fitCurve(fitPoints, prevSeg);
                segments.add(rec);

//                outlinerPath.cubicTo(rec.seg.getA1x(), rec.seg.getA1y(),
//                        rec.seg.getA2x(), rec.seg.getA2y(),
//                        rec.seg.getA3x(), rec.seg.getA3y(),
//                        (float)rec.weight1);
            }
            else
            {
                //Pop top 'working segment'
                if (!segments.isEmpty())
                {
                    segments.remove(segments.size() - 1);
                }
                //Push current segment
                segments.add(rec);

//                outlinerPath.removeLast();
//                outlinerPath.cubicTo(rec.seg.getA1x(), rec.seg.getA1y(),
//                        rec.seg.getA2x(), rec.seg.getA2y(),
//                        rec.seg.getA3x(), rec.seg.getA3y(),
//                        (float)rec.weight1);
            }
        }

        buildDisplayPath();
    }

    private void buildDisplayPath()
    {
        //Math is done - now build something to see
//        displayPath = null;


        if (segments.isEmpty())
        {
            return;
        }

        //Just dump segments directly onto a path of single weight
        FitCurveRecord head = segments.get(0);

        path = new CyPath2d();

        if (true)
        {
            //Segments should already be in good form.  No need to tweak
            path.moveTo(head.seg.getStartX(), head.seg.getStartY());
            for (int i = 0; i < segments.size(); ++i)
            {
                FitCurveRecord recSeg = segments.get(i);
                BezierCubic2d pt = recSeg.seg;

                path.cubicTo(
                        pt.getStartKnotX(), pt.getStartKnotY(),
                        pt.getEndKnotX(), pt.getEndKnotY(),
                        pt.getEndX(), pt.getEndY());

//                    System.err.print("*** " + pt.getStartX() + " " + pt.getStartY());
//                    System.err.print(" " + pt.getStartKnotX() + " " + pt.getStartKnotY());
//                    System.err.print(" " + pt.getEndKnotX() + " " + pt.getEndKnotY());
//                    System.err.println(" " + pt.getEndX() + " " + pt.getEndY());
            }
//                System.err.println();
        }

//        displayPath = new PathCurve(path);
//        return;

    }

    /**
     * @return the displayPath
     */
//    public PathCurve getPathCurve()
//    {
//        return displayPath;
//    }

    /**
     * @return the displayPath
     */
    public CyPath2d getPath()
    {
        return path;
    }

    /**
     * Append existing segments to path.  Does not include moving
     * to the initial point of the first segment.
     * 
     * @param path Path to append cubic segments to
     * @param reverse If true, segments are added in reverse direction
     */
    public void appendSegs(CyPath2d path, boolean reverse)
    {
        if (!reverse)
        {
            for (int i = 0; i < segments.size(); ++i)
            {
                BezierCubic2d seg = segments.get(i).seg;
                path.cubicTo(seg.getAx1(), seg.getAy1(),
                        seg.getAx2(), seg.getAy2(),
                        seg.getAx3(), seg.getAy3());
            }
        }
        else
        {
            for (int i = segments.size() - 1; i >= 0; --i)
            {
                BezierCubic2d seg = segments.get(i).seg;
                path.cubicTo(seg.getAx2(), seg.getAy2(),
                        seg.getAx1(), seg.getAy1(),
                        seg.getAx0(), seg.getAy0());
            }
        }
    }

    //------------------------------------

    class FitCurveRecord
    {
        final BezierCubic2d seg;
//        final double weight0;
//        final double weight1;
        final double error;

//        public FitCurveRecord(BezierCubic2d seg, double weight0, double weight1, double error)
        public FitCurveRecord(BezierCubic2d seg, double error)
        {
            this.seg = seg;
//            this.weight0 = weight0;
//            this.weight1 = weight1;
            this.error = error;
        }
    }

//    public static class PenPoint
//    {
//        final float x;
//        final float y;
//        final float pressure;
//
//        public PenPoint(float x, float y, float pressure)
//        {
//            this.x = x;
//            this.y = y;
//            this.pressure = pressure;
//        }
//
//        @Override
//        public String toString()
//        {
//            return "X: " + x + " Y: " + y + " Pres:" + pressure;
//        }
//    }
}
