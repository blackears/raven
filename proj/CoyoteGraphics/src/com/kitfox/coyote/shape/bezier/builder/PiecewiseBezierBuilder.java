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

import com.kitfox.coyote.math.GMatrix;
import com.kitfox.coyote.math.bezier.FitCurve;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
abstract public class PiecewiseBezierBuilder
{
    ArrayList<BezierPointNd> fitPoints = new ArrayList<BezierPointNd>();
    BezierPointNd ptLast;

    //Used to track UI display curve while the stroke is being drawn
    ArrayList<FitCurveRecord> segments = new ArrayList<FitCurveRecord>();
    final int order;

    final double maxError;

    /**
     * 
     * @param order Number of components in point tuple.  Eg, (x, y, weight)
     * would have order == 3
     * @param maxError 
     */
    public PiecewiseBezierBuilder(int order, double maxError)
    {
        this.order = order;
        this.maxError = maxError;
    }

    public ArrayList<FitCurveRecord> getSegments()
    {
        return new ArrayList<FitCurveRecord>(segments);
    }
    
    public FitCurveRecord getFirstSegment()
    {
        return segments.isEmpty() 
                ? null
                : segments.get(0);
    }

    public FitCurveRecord getLastSegment()
    {
        return segments.isEmpty() 
                ? null
                : segments.get(segments.size() - 1);
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
    public ArrayList<BezierCurveNd> getAlignedCurves(boolean alignEndPoints)
    {
        ArrayList<BezierCurveNd> arr = new ArrayList<BezierCurveNd>();
        
        FitCurveRecord s0 = segments.get(0);
        BezierCurveNd c0 = new BezierCurveNd(s0.curve);
        arr.add(c0);
        
        for (int i = 1; i < segments.size(); ++i)
        {
            FitCurveRecord s1 = segments.get(i);
            BezierCurveNd c1 = new BezierCurveNd(s1.curve);

            c0.alignWith(c1);
            arr.add(c1);
            
            c0 = c1;
//            s0 = s1;
        }
        
        if (alignEndPoints)
        {
            BezierCurveNd c1 = new BezierCurveNd(arr.get(0));
            c0.alignWith(c1);
        }
        
        return arr;
    }
    
    /**
     * Calculate spatial distance between two points.
     * 
     * @param p0
     * @param p1
     * @return 
     */
    abstract protected double distanceSpatial(BezierPointNd p0, BezierPointNd p1);
    abstract protected double distanceError(BezierPointNd p0, BezierPointNd p1);
    
    public void addPoint(BezierPointNd pt)
    {
        if (ptLast != null)
        {
            if (ptLast.equals(pt))
            {
                return;
            }
        }
        fitPoints.add(pt);
        ptLast = pt;
        
        calculateNewSegments();
    }
    
    private FitCurveRecord fitCurve(ArrayList<BezierPointNd> fitPoints)
    {
        //Prepare points for least squares fit
        GMatrix P = new GMatrix(fitPoints.size(), order);
        for (int i = 0; i < fitPoints.size(); ++i)
        {
            BezierPointNd pt = fitPoints.get(i);
            for (int j = 0; j < order; ++j)
            {
                P.setElement(i, j, pt.get(j));
            }
        }

        //Point times are distributed according to distance from previous point
        double[] Ptimes = new double[fitPoints.size()];
        Ptimes[0] = 0;
        double dist = 0;
        for (int i = 1; i < fitPoints.size(); ++i)
        {
            BezierPointNd pt0 = fitPoints.get(i - 1);
            BezierPointNd pt1 = fitPoints.get(i);
            dist += distanceSpatial(pt0, pt1);
            Ptimes[i] = dist;
        }

        //Normalize times
        double distI = 1 / dist;
        for (int i = 1; i < fitPoints.size(); ++i)
        {
            Ptimes[i] *= distI;
        }

        BezierCurveNd curve = null;

        if (fitPoints.size() == 2)
        {
            //Fit line
            BezierPointNd pt0 = fitPoints.get(0);
            BezierPointNd pt1 = fitPoints.get(1);
            curve = new BezierCurveNd(pt0, pt1);
        }
        else if (fitPoints.size() == 3)
        {
            GMatrix Q = FitCurve.fitBezier(2, Ptimes, P);

            double[] vec; 
            
            vec = new double[order];
            Q.getRow(0, vec);
            BezierPointNd pt0 = new BezierPointNd(vec);
            
            vec = new double[order];
            Q.getRow(1, vec);
            BezierPointNd k0 = new BezierPointNd(vec);
            
            vec = new double[order];
            Q.getRow(2, vec);
            BezierPointNd pt1 = new BezierPointNd(vec);
            
            curve = new BezierCurveNd(pt0, k0, pt1);
        }
        else if (fitPoints.size() > 3)
        {
            GMatrix Q = FitCurve.fitBezier(3, Ptimes, P);

            double[] vec; 

            vec = new double[order];
            Q.getRow(0, vec);
            BezierPointNd pt0 = new BezierPointNd(vec);
            
            vec = new double[order];
            Q.getRow(1, vec);
            BezierPointNd k0 = new BezierPointNd(vec);

            vec = new double[order];
            Q.getRow(2, vec);
            BezierPointNd k1 = new BezierPointNd(vec);

            vec = new double[order];
            Q.getRow(3, vec);
            BezierPointNd pt1 = new BezierPointNd(vec);
            
//            GMatrix Q = FitCurve.fitBezierKnots(3, Ptimes, P);
//
//            BezierPointNd pt0 = fitPoints.get(0);
//            BezierPointNd pt1 = fitPoints.get(3);
//            
//            double[] vec = new double[order];
//            Q.getRow(0, vec);
//            BezierPointNd k0 = new BezierPointNd(vec);
//
//            vec = new double[order];
//            Q.getRow(1, vec);
//            BezierPointNd k1 = new BezierPointNd(vec);
            
            curve = new BezierCurveNd(pt0, k0, k1, pt1);
        }

        if (curve == null)
        {
            return null;
        }

        //Calc error
        double error = 0;
        //CyVector2d evalPt = new CyVector2d();
        for (int i = 0; i < fitPoints.size(); ++i)
        {
            BezierPointNd ptSrc = fitPoints.get(i);
            BezierPointNd ptEval = 
                    curve.eval(Ptimes[i]);
            error += distanceError(ptSrc, ptEval);
        }

        return new FitCurveRecord(curve, error);
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

        //Create new top record based on trailing points
        FitCurveRecord rec = fitCurve(fitPoints);

        if (rec != null)
        {
            //We've accumulated too much error - push last good segment
            // and start fresh
            if (rec.getError() > maxError)
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

//                if (segments.size() >= 2)
//                {
//                    //Make pushed segments continuous
//                    FitCurveRecord s0 = segments.get(segments.size() - 2);
//                    FitCurveRecord s1 = segments.get(segments.size() - 1);
//                    
//                    s0.curve.alignWith(s1.curve);
//                }
                
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
    }

    //------------------------------------

    public class FitCurveRecord
    {
        private BezierCurveNd curve;
        private final double error;

        public FitCurveRecord(BezierCurveNd curve, double error)
        {
            this.curve = curve;
            this.error = error;
        }
        
//        public T eval(double t)
//        {
//            return evalBezier(points, t);
//        }
//
//        public FitCurveRecord[] split(double t)
//        {
//            switch (points.length)
//            {
//                case 2:
//                    T a0 = points[0];
//                    T a1 = points[1];
//                    T b0 = lerp(a0, a1, t);
//                    return new FitCurveRecord[]{
//                        new FitCurveRecord(new T[]{a0, b0}, error)
//                    };
//                    
//            }
//            
//            T[] pointsNew = points.clone();
//            
//            
//        }
//        
//        /**
//         * @return the points
//         */
//        public T[] getPoints()
//        {
//            return points.clone();
//        }

        /**
         * @return the error
         */
        public double getError()
        {
            return error;
        }

        /**
         * @return the curve
         */
        public BezierCurveNd getCurve()
        {
            return curve;
        }
    }
}
