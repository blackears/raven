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

package com.kitfox.raven.shape.builders;

import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.math.GMatrix;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.bezier.builder.PiecewiseBezierBuilder2d;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class Contour
{
    ArrayList<ContourPoint> points = new ArrayList<ContourPoint>();

    public void addStart(int x, int y)
    {
        ContourPoint pt = new ContourPoint(x, y);
        points.add(0, pt);
    }

    void addEnd(int x, int y)
    {
        ContourPoint pt = new ContourPoint(x, y);
        points.add(pt);
    }

    void append(Contour contourStart)
    {
        points.addAll(contourStart.points);
    }

    ContourPoint getLastPoint()
    {
        return points.get(points.size() - 1);
    }

    public CyPath2d getPath(double maxError)
    {
        PiecewiseBezierBuilder2d builder = new PiecewiseBezierBuilder2d(maxError);
        
        for (ContourPoint pt: points)
        {
            CyVector2d v = new CyVector2d(pt.x, pt.y);
            builder.addPoint(v);
        }
        
        return builder.getPath(true);
    }
    
//    public void appendTo(Path2D path)
//    {
//        ContourPoint pt = points.get(0);
//        path.moveTo(pt.x, pt.y);
//
//        for (int i = 1; i < points.size(); ++i)
//        {
//            pt = points.get(i);
//            path.lineTo(pt.x, pt.y);
//        }
//        path.closePath();
//    }
//
//    public void removeColinearPoints()
//    {
//        for (int i = 1; i < points.size() - 1; ++i)
//        {
//            ContourPoint pt0 = points.get(i - 1);
//            ContourPoint pt1 = points.get(i);
//            ContourPoint pt2 = points.get(i + 1);
//
//            int dx0 = pt1.x - pt0.x;
//            int dy0 = pt1.y - pt0.y;
//            int dx1 = pt2.x - pt1.x;
//            int dy1 = pt2.y - pt1.y;
//            if (dx0 * dy1 == dx1 * dy0)
//            {
//                //pt1 lies on line segment from pt0 to pt2
//                points.remove(i);
//                --i;
//            }
//        }
//    }
//
//    private FitCurveRecord fitSection(int startX, int endX)
//    {
//        int numPoints = endX - startX + 1;
//
//        //Prepare points for least squares fit
//        GMatrix P = new GMatrix(numPoints, 2);
//        for (int i = startX; i <= endX; ++i)
//        {
//            int index = i == points.size() ? 0 : i;
//            ContourPoint pt = points.get(index);
//            P.setElement(i - startX, 0, pt.x);
//            P.setElement(i - startX, 1, pt.y);
//        }
//
//        if (numPoints <= 2)
//        {
//            //For degree <= 2, points are unaltered
//            return new FitCurveRecord(P, 0, startX, endX);
//        }
//
//        //Point times are distributed acording to distance from previous point
//        double[] Ptimes = new double[numPoints];
//        Ptimes[0] = 0;
//        double dist = 0;
//        for (int i = 1; i < numPoints; ++i)
//        {
//            double p0x = P.getElement(i - 1, 0);
//            double p0y = P.getElement(i - 1, 1);
//            double p1x = P.getElement(i, 0);
//            double p1y = P.getElement(i, 1);
//            dist += Math2DUtil.dist(p0x, p0y, p1x, p1y);
//            Ptimes[i] = dist;
//        }
//
//        //Normalize times
//        double distI = 1 / dist;
//        for (int i = 1; i < numPoints; ++i)
//        {
//            Ptimes[i] *= distI;
//        }
//
//        GMatrix Q;
//        if (numPoints == 3)
//        {
//            Q = FitCurve.fitBezierKnots(2, Ptimes, P);
//        }
//        else //if (numPoints > 3)
//        {
//            Q = FitCurve.fitBezierKnots(3, Ptimes, P);
//        }
//
//        //Result matrix is Q matrix with first and last points from P matrix
//        // added
//        GMatrix R = new GMatrix(Q.getNumRow() + 2, 2);
//        R.setElement(0, 0, P.getElement(0, 0));
//        R.setElement(0, 1, P.getElement(0, 1));
//        for (int i = 0; i < Q.getNumRow(); ++i)
//        {
//            R.setElement(i + 1, 0, Q.getElement(i, 0));
//            R.setElement(i + 1, 1, Q.getElement(i, 1));
//        }
//        R.setElement(R.getNumRow() - 1, 0, P.getElement(P.getNumRow() - 1, 0));
//        R.setElement(R.getNumRow() - 1, 1, P.getElement(P.getNumRow() - 1, 1));
//
//        double error = 0;
//        double[] evalPt = new double[2];
//        for (int i = 0; i < numPoints; ++i)
//        {
//            FitCurve.evalBezier(R, Ptimes[i], evalPt);
//
//            double curveDist = Math2DUtil.dist(evalPt[0], evalPt[1],
//                    P.getElement(i, 0), P.getElement(i, 1));
//
//            error += curveDist;
//        }
//
//        return new FitCurveRecord(R, error, startX, endX);
//    }
//
//    private ArrayList<FitCurveRecord> getSmoothedSegments(double maxError)
//    {
//        int segStart = 0;
//        FitCurveRecord recLast = null;
//
//        ArrayList<FitCurveRecord> records = new ArrayList<FitCurveRecord>();
//        //Go through all points, plus the first point again
//        for (int i = 1; i <= points.size(); ++i)
//        {
////            int index = (i == points.size()) ? 0 : i;
//            FitCurveRecord rec = fitSection(segStart, i);
//
//            if (rec.error >= maxError)
//            {
//                segStart = i - 1;
//                records.add(recLast);
//                recLast = fitSection(i - 1, i);
//                continue;
//            }
//
//            recLast = rec;
//        }
//
//        //Add in last record
//        records.add(recLast);
////        if (records.isEmpty())
////        {
////            records.add(recLast);
////        }
////        else
////        {
////            //Merge last segment with remaining points
////            FitCurveRecord last = records.get(records.size() - 1);
////            FitCurveRecord rec = fitSection(last.startIdx, points.size());
////            records.set(records.size() - 1, rec);
////        }
//
//        return records;
//    }
//
//    public void appendSmoothedPath(double maxError, Path2D.Double path)
//    {
//        ArrayList<FitCurveRecord> records = getSmoothedSegments(maxError);
//
//        if (records.isEmpty())
//        {
//            return;
//        }
////        Path2D.Double path = new Path2D.Double();
////        Path2D.Double path = null;
//
//        FitCurveRecord recStart = records.get(0);
//        path.moveTo(recStart.points.getElement(0, 0),
//                recStart.points.getElement(0, 1));
//
//        for (FitCurveRecord rec: records)
//        {
//            GMatrix P = rec.points;
//            
//            if (path == null)
//            {
//                path = new Path2D.Double();
//                path.moveTo(P.getElement(0, 0), P.getElement(0, 1));
//            }
//
//            int order = P.getNumRow();
//            if (order == 2)
//            {
//                path.lineTo(P.getElement(1, 0), P.getElement(1, 1));
//            }
//            else if (order == 3)
//            {
//                path.quadTo(
//                        P.getElement(1, 0), P.getElement(1, 1),
//                        P.getElement(2, 0), P.getElement(2, 1)
//                        );
//            }
//            else if (order == 4)
//            {
//                path.curveTo(
//                        P.getElement(1, 0), P.getElement(1, 1),
//                        P.getElement(2, 0), P.getElement(2, 1),
//                        P.getElement(3, 0), P.getElement(3, 1)
//                        );
//            }
//        }
//
////        return path;
//    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < points.size(); ++i)
        {
            sb.append(points.get(i));
            if (i != 0)
            {
                sb.append(' ');
            }
        }
        return sb.toString();
    }

    /**
     * Scan through contour.  If any two adjacent points have a length of longer
     * than maxSegmentLength, then split them.
     *
     * @param maxSegmentLength
     */
//    public void cutSegments(int maxSegmentLength)
//    {
//        for (int i = 0; i < points.size(); ++i)
//        {
//            int nextIdx = i == points.size() - 1 ? 0 : i + 1;
//
//            ContourPoint pt0 = points.get(i);
//            ContourPoint pt1 = points.get(nextIdx);
//
//            if (Math.abs(pt0.x - pt1.x) > maxSegmentLength
//                    || Math.abs(pt0.y - pt1.y) > maxSegmentLength)
//            {
//                ContourPoint pm = new ContourPoint((pt0.x + pt1.x) / 2, (pt0.y + pt1.y) / 2);
//                points.add(i + 1, pm);
//
//                //Let's try again after new point has been inserted
//                --i;
//            }
//        }
//    }

    //---------------------------------------

    class FitCurveRecord
    {
        final GMatrix points;
        final double error;
        final int startIdx;
        final int endIdx;

        public FitCurveRecord(GMatrix points, double error, int startIdx, int endIdx)
        {
            this.points = points;
            this.error = error;
            this.startIdx = startIdx;
            this.endIdx = endIdx;
        }
    }

}
