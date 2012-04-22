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

package com.kitfox.coyote.shape.bezier.builder;

import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.math.GMatrix;
import com.kitfox.coyote.math.bezier.FitCurve;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.bezier.BezierCubic2d;
import com.kitfox.coyote.shape.bezier.BezierCurve2d;
import com.kitfox.coyote.shape.bezier.BezierLine2d;
import com.kitfox.coyote.shape.bezier.BezierQuad2d;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class PiecewiseBezierBuilder2d
{
    ArrayList<CyVector2d> fitPoints = new ArrayList<CyVector2d>();
    CyVector2d ptLast;

    //Used to track UI display curve while the stroke is being drawn
    ArrayList<FitCurveRecord> segments = new ArrayList<FitCurveRecord>();

    final double maxError;

//    CyPath2d path;

    public PiecewiseBezierBuilder2d(double maxError)
    {
        this.maxError = maxError;
    }

    public BezierCurve2d getFirstSegment()
    {
        return segments.isEmpty() 
                ? null
                : segments.get(0).curve;
    }

    public BezierCurve2d getLastSegment()
    {
        return segments.isEmpty() 
                ? null
                : segments.get(segments.size() - 1).curve;
    }

    private CyVector2d alignCurves(BezierCurve2d c0, BezierCurve2d c1)
    {
        double k0x = c0.getEndX() - c0.getTanOutX();
        double k0y = c0.getEndY() - c0.getTanOutY();
        double k1x = c1.getStartX() + c1.getTanInX();
        double k1y = c1.getStartY() + c1.getTanInY();
        
        double px = (c0.getEndX() + c1.getStartX()) / 2;
        double py = (c0.getEndY() + c1.getStartY()) / 2;
        
        //Find closest point to p on line from k0 to k1
        CyVector2d v0 = new CyVector2d(k1x - k0x, k1y - k0y);
        CyVector2d v1 = new CyVector2d(px - k0x, py - k0y);
        
        v0.scale(v0.dot(v1) / v0.dot(v0));
        v0.add(k0x, k0y);
        
        return v0;
    }
    
    /**
     * Takes piecewise smoothed segments and adjusts the endpoints of their
     * curves so that entire piecewise Bezier is continuous
     * 
     * @param alignEndPoints If true, the last point of the final segment will
     * be aligned with the first point of the first segment.  Should only be
     * set if it is known that this piecewise bezier is a loop.
     * 
     * @return 
     */
    public ArrayList<BezierCurve2d> getAlignedCurves(boolean alignEndPoints)
    {
        ArrayList<BezierCurve2d> arr = new ArrayList<BezierCurve2d>();
        if (segments.isEmpty())
        {
            return arr;
        }
        
        FitCurveRecord s0 = segments.get(0);
        BezierCurve2d c0 = s0.curve;
        arr.add(c0);
System.err.println("-----SVG");
System.err.println(c0.asSvg());
        for (int i = 1; i < segments.size(); ++i)
        {
            FitCurveRecord s1 = segments.get(i);
            BezierCurve2d c1 = s1.curve;
System.err.println(c1.asSvg());

            CyVector2d p = alignCurves(c0, c1);
            c0 = c0.setEnd(p.x, p.y);
            c1 = c1.setStart(p.x, p.y);
            
            arr.set(arr.size() - 1, c0);
            arr.add(c1);
            
            c0 = c1;
        }
        
        if (alignEndPoints)
        {
            BezierCurve2d c1 = arr.get(0);

            CyVector2d p = alignCurves(c0, c1);
            c0 = c0.setEnd(p.x, p.y);
            c1 = c1.setStart(p.x, p.y);
            
            arr.set(arr.size() - 1, c0);
            arr.set(0, c1);
        }
        
        return arr;
    }

    public void addPoint(CyVector2d pt)
    {
        if (ptLast != null)
        {
            if (ptLast.x == pt.x && ptLast.y == pt.y)
            {
                return;
            }
        }
        fitPoints.add(pt);
        ptLast = pt;
        
        calculateNewSegments();
    }

    private FitCurveRecord fitCurve(ArrayList<CyVector2d> fitPoints)
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

        BezierCurve2d seg = null;

        if (fitPoints.size() == 2)
        {
            //Fit line
            CyVector2d pt0 = fitPoints.get(0);
            CyVector2d pt1 = fitPoints.get(1);
            
            seg = new BezierLine2d(pt0.x, pt0.y, pt1.x, pt1.y);
            
//            double dx = pt1.x - pt0.x;
//            double dy = pt1.y - pt0.y;
//
//            //Cubic form of line
//            seg = new BezierCubic2d(
//                    pt0.x, pt0.y,
//                    pt0.x + dx / 3, pt0.y + dy / 3,
//                    pt0.x + dx * 2 / 3, pt0.y + dy * 2 / 3,
//                    pt1.x, pt1.y);
        }
        else if (fitPoints.size() == 3)
        {
            GMatrix Q = FitCurve.fitBezier(2, Ptimes, P);

            double p0x = Q.getElement(0, 0);
            double p0y = Q.getElement(0, 1);
            double p1x = Q.getElement(1, 0);
            double p1y = Q.getElement(1, 1);
            double p2x = Q.getElement(2, 0);
            double p2y = Q.getElement(2, 1);

            //Convert quadratic curve to cubic
            seg = new BezierQuad2d(
                    p0x, p0y,
                    p1x, p1y,
                    p2x, p2y);
        }
        else if (fitPoints.size() > 3)
        {
            GMatrix Q = FitCurve.fitBezier(3, Ptimes, P);

            double p0x = Q.getElement(0, 0);
            double p0y = Q.getElement(0, 1);
            double p1x = Q.getElement(1, 0);
            double p1y = Q.getElement(1, 1);
            double p2x = Q.getElement(2, 0);
            double p2y = Q.getElement(2, 1);
            double p3x = Q.getElement(3, 0);
            double p3y = Q.getElement(3, 1);
            
            seg = new BezierCubic2d(
                    p0x, p0y,
                    p1x, p1y,
                    p2x, p2y,
                    p3x, p3y);
        }

        if (seg == null)
        {
            return null;
        }

        double error = 0;
        CyVector2d evalPt = new CyVector2d();
        for (int i = 0; i < fitPoints.size(); ++i)
        {
            CyVector2d pt = fitPoints.get(i);
            seg.evaluate(Ptimes[i], evalPt);
            double dx = pt.x - evalPt.x;
            double dy = pt.y - evalPt.y;
//            error += Math.sqrt(dx * dx + dy * dy);
            error = Math.max(error, Math.sqrt(dx * dx + dy * dy));
        }

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

//        FitCurveRecord prevSeg = null;
//        if (segments.size() >= 2)
//        {
//            prevSeg = segments.get(segments.size() - 2);
//        }
        //Create new top record based on trailing points
        FitCurveRecord rec = fitCurve(fitPoints);

        if (rec != null)
        {
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

                rec = fitCurve(fitPoints);
                segments.add(rec);
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
            }
        }

//        buildDisplayPath();
    }

//    private void buildDisplayPath()
//    {
//        //Math is done - now build something to see
//        if (segments.isEmpty())
//        {
//            return;
//        }
//
//        //Just dump segments directly onto a path of single weight
//        FitCurveRecord head = segments.get(0);
//
//        path = new CyPath2d();
//
//        if (true)
//        {
//            //Segments should already be in good form.  No need to tweak
//            path.moveTo(head.seg.getStartX(), head.seg.getStartY());
//            for (int i = 0; i < segments.size(); ++i)
//            {
//                FitCurveRecord recSeg = segments.get(i);
//                BezierCurve2d pt = recSeg.seg;
//
//                path.cubicTo(
//                        pt.getStartKnotX(), pt.getStartKnotY(),
//                        pt.getEndKnotX(), pt.getEndKnotY(),
//                        pt.getEndX(), pt.getEndY());
//            }
//        }
//    }

    /**
     * @return the displayPath
     */
    public CyPath2d getPath(boolean alignEndPoints)
    {
        ArrayList<BezierCurve2d> curves = getAlignedCurves(alignEndPoints);

        if (curves.isEmpty())
        {
            return null;
        }
        
        CyPath2d path = new CyPath2d();
        BezierCurve2d c0 = curves.get(0);
        path.moveTo(c0.getStartX(), c0.getStartY());
        
        for (BezierCurve2d c: curves)
        {
            c.append(path);
        }
        
        return path;
    }

//    /**
//     * Append existing segments to path.  Does not include moving
//     * to the initial point of the first segment.
//     * 
//     * @param path Path to append cubic segments to
//     * @param reverse If true, segments are added in reverse direction
//     */
//    public void appendSegs(CyPath2d path, boolean reverse)
//    {
//        if (!reverse)
//        {
//            for (int i = 0; i < segments.size(); ++i)
//            {
//                BezierCurve2d seg = segments.get(i).seg;
//                path.cubicTo(seg.getAx1(), seg.getAy1(),
//                        seg.getAx2(), seg.getAy2(),
//                        seg.getAx3(), seg.getAy3());
//            }
//        }
//        else
//        {
//            for (int i = segments.size() - 1; i >= 0; --i)
//            {
//                BezierCurve2d seg = segments.get(i).seg;
//                path.cubicTo(seg.getAx2(), seg.getAy2(),
//                        seg.getAx1(), seg.getAy1(),
//                        seg.getAx0(), seg.getAy0());
//            }
//        }
//    }

    //------------------------------------

    class FitCurveRecord
    {
        final BezierCurve2d curve;
        final double error;

        public FitCurveRecord(BezierCurve2d seg, double error)
        {
            this.curve = seg;
            this.error = error;
        }
    }
}
