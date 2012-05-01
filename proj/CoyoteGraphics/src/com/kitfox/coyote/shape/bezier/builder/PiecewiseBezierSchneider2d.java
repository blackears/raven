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
import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.bezier.BezierCubic2d;
import com.kitfox.coyote.shape.bezier.BezierLine2d;
import com.kitfox.coyote.shape.bezier.BezierQuad2d;
import java.util.ArrayList;

/**
 * <p>Based on 
 * <code>An Algorithm for Automatically Fitting Digitized Curves
 * by Philip J. Schneider
 * from "Graphics Gems", Academic Press, 1990</code>
 * </p>
 *
 * @author kitfox
 */
public class PiecewiseBezierSchneider2d
{
    private ArrayList<CyVector2d> points = new ArrayList<CyVector2d>();
    private final double error;
    private final double iterationError;

    private final double cornerAngle;
    private final double cornerAngleCos;
    
    private final boolean closedLoop;
    private final int MAX_ITERATIONS = 4;
    
    private ArrayList<FitCubicRecord> pieces = new ArrayList<FitCubicRecord>();
    private boolean dirty;
    
    private final int tangentNeighborhood;
    
    private final boolean verbose = false;
    
    private double[][] leastSqCusp;
    private double[][] leastSqSmooth;
    
    /**
     * 
     * @param closedLoop
     * @param maxError
     * @param cornerAngle If angle between incoming and outgoing 
     * tangents at this point is smaller than specified angle, then
     * a cusp is created instead of a smooth join.
     * @param tangentNeighborhood When calculating tangents to the outline,
     * consider a neighborhood of this many points extending away from the
     * point under consideration.
     */
    public PiecewiseBezierSchneider2d(boolean closedLoop, double maxError, 
            double cornerAngle, int tangentNeighborhood)
    {
        this.error = maxError;
        this.iterationError = maxError * maxError;
        this.cornerAngle = cornerAngle;
        this.cornerAngleCos = Math.cos(cornerAngle);
        this.closedLoop = closedLoop;
        this.tangentNeighborhood = tangentNeighborhood;
        
        leastSqCusp = Math2DUtil.createLeastSquaresMatrix(tangentNeighborhood + 1);
        leastSqSmooth = Math2DUtil.createLeastSquaresMatrix(tangentNeighborhood * 2 + 1);
    }

    public PiecewiseBezierSchneider2d(boolean closedLoop, double maxError)
    {
        this(closedLoop, maxError, Math.toRadians(60), 5);
    }

    public PiecewiseBezierSchneider2d(boolean closedLoop)
    {
        this(closedLoop, 4);
    }

    public CyPath2d getPath()
    {
        buildCurve();
        if (pieces.isEmpty())
        {
            return null;
        }
        
        CyPath2d path = new CyPath2d();
        
        BezierCubic2d first = pieces.get(0).curve;
        path.moveTo(first.getStartX(), first.getStartY());
        for (FitCubicRecord rec: pieces)
        {
            rec.curve.append(path);
        }
        
        if (closedLoop)
        {
            path.close();
        }
        return path;
    }
    
    public CyPath2d getPathPoints()
    {
        CyPath2d path = new CyPath2d();
        
        CyVector2d first = points.get(0);
        path.moveTo(first.x, first.y);
        
        for (int i = 1; i < points.size(); ++i)
        {
            CyVector2d pt = points.get(i);
            path.lineTo(pt.x, pt.y);
        }
        return path;
    }
    
    public ArrayList<BezierCubic2d> getCurves()
    {
        buildCurve();
        
        ArrayList<BezierCubic2d> list = new ArrayList<BezierCubic2d>();
        for (FitCubicRecord rec: pieces)
        {
            list.add(rec.curve);
        }
        return list;
    }
    
    public void addPoint(double x, double y)
    {
        addPoint(new CyVector2d(x, y));
    }
    
    /**
     * Append a point to the point queue.  Must not add two consecutive
     * points that are equal.
     * 
     * If adding points for a closed loop, all points should be added 
     * before calculating final curve.  Also, the last point should
     * not equal the first point.
     * 
     * If loop is not closed, 
     * calculation can be performed repeatedly as new points are 
     * appended to queue;
     * 
     * @param v 
     */
    public void addPoint(CyVector2d v)
    {
        points.add(v);
        dirty = true;
    }
    
    private void buildCurve()
    {
        if (points.size() <= 1)
        {
            return;
        }
        
        if (!dirty)
        {
            return;
        }

        if (closedLoop)
        {
            pieces.clear();
            CyVector2d tHat1 = new CyVector2d();
            CyVector2d tHat2 = new CyVector2d();
            computeJoinTangents(0, tHat2, tHat1);

            if (verbose)
            {
                System.err.println("Path points");
                System.err.println(getPathPoints().toString());
            }
            
            CyVector2d p0 = points.get(0);
            points.add(p0);
            fitCubic(0, points.size() - 1, tHat1, tHat2);
            points.remove(points.size() - 1);
        }
        else
        {
            if (pieces.isEmpty())
            {
                int last = points.size() - 1;
                CyVector2d tHat1 = computeForwardTangent(0);
                CyVector2d tHat2 = computeBackwardTangent(last);
                
                fitCubic(0, last, tHat1, tHat2);
            }
            else
            {
                int last = points.size() - 1;
                FitCubicRecord lastRec = pieces.remove(pieces.size() - 1);
                CyVector2d tHat2 = computeBackwardTangent(last);
    
                fitCubic(lastRec.first, last, lastRec.tHat1, tHat2);
            }
        }
        
        dirty = false;
    }
    
    private void emitCurve(BezierCubic2d curve, int first, int last, CyVector2d tHat1, CyVector2d tHat2)
    {
        pieces.add(new FitCubicRecord(curve, first, last, tHat1, tHat2));
    }
    
    /**
     * Fit curve to points startIdx to endIdx inclusive.
     * 
     * @param startIdx
     * @param endIdx 
     * @param tHat1 Unit tangent at first point, pointing in direction
     * of trajectory.
     * @param tHat2 Unit tangent at last point, pointing back along
     * trajectory traversed.
     */
    private void fitCubic(int first, int last, CyVector2d tHat1, CyVector2d tHat2)
    {
        int nPts = last - first + 1;
        
        if (nPts == 2)
        {
            //Use heuristic if region only has two points in it
            CyVector2d p0 = points.get(first);
            CyVector2d p3 = points.get(last);
            double dist = p0.distance(p3);
            
            CyVector2d p1 = new CyVector2d(p0);
            p1.addScaled(tHat1, dist / 3);
            CyVector2d p2 = new CyVector2d(p3);
            p2.addScaled(tHat2, dist / 3);
            
            emitCurve(new BezierCubic2d(p0, p1, p2, p3), 
                    first, last, tHat1, tHat2);
            return;
        }
        
        //Parameterize points, and attempt to fit curve
        double[] u = chordLengthParameterize(first, last);
        BezierCubic2d bezCurve = generateBezier(first, last, u, tHat1, tHat2);
        
        //Find max deviation of points to fitted curve
        ErrorRecord err = computeMaxError(first, last, bezCurve, u);

        if (verbose)
        {
            System.err.println();
            System.err.println("Fitting  (first: " + first + 
                    " last: " + last + " error: " + err.maxError + ")");
            System.err.println("tHat1: " + tHat1 + " tHat2: " + tHat2);
            System.err.println(bezCurve.asSvg());
        }

        if (err.maxError < error)
        {
            emitCurve(bezCurve, first, last, tHat1, tHat2);
            return;
        }
        
        if (err.maxError < iterationError)
        {
            for (int i = 0; i < MAX_ITERATIONS; ++i)
            {
                double[] uPrime = reparameterize(
                        first, last, u, bezCurve);
                if (uPrime == null)
                {
                    //Bad reparameterization.  Just use existing one
                    break;
                }
                bezCurve = generateBezier(
                        first, last, uPrime, tHat1, tHat2);
                err = computeMaxError(first, last, bezCurve, uPrime);
                
//                if (errNew.maxError > err.maxError)
//                {
//                    break;
//                }
//                err = errNew;
//                bezCurve = bezCurveNew;
                
//if (err.maxError > 5100)
//{

                //uPrime = reparameterize(first, last, u, bezCurve);
////generateBezier(first, last, uPrime, tHat1, tHat2);
//err = computeMaxError(first, last, bezCurve, uPrime);    
//}
//bezCurve = bezCurveNew;

                if (verbose)
                {
                    System.err.println("Refining  (#" + i + " error: " + err.maxError + ")");
                    System.err.println(bezCurve.asSvg());
                }
                
                if (err.maxError < error)
                {
                    emitCurve(bezCurve, first, last, tHat1, tHat2);
                    return;
                }
                u = uPrime;
            }
        }
        
        //Fitting failed -- split at max error point and fit recursively
        int splitPoint = err.splitPoint;
        CyVector2d tC1 = new CyVector2d();
        CyVector2d tC2 = new CyVector2d();
        computeJoinTangents(splitPoint, tC1, tC2);

        if (verbose)
        {
            System.err.println("Splitting at " + splitPoint);
            System.err.println("tHat1: " + tHat1 + " tC1: " + tC1 + " tC2: " + tC2 + " tHat2: " + tHat2);
        }
        
        fitCubic(first, splitPoint, tHat1, tC1);
        fitCubic(splitPoint, last, tC2, tHat2);
    }
    
    /**
     * Find the maximum squared distance of digitized points
     * to fitted curve.
     * 
     * @param first
     * @param last
     * @param bezCurve
     * @param u
     * @return 
     */
    private ErrorRecord computeMaxError(int first, int last, 
            BezierCubic2d bezCurve, double[] u)
    {
        int splitPoint = (last - first + 1) / 2;
        double maxDist = 0;
        
        CyVector2d P = new CyVector2d();
        for (int i = first + 1; i < last; ++i)
        {
            bezCurve.evaluate(u[i - first], P);
            double dist = P.distanceSquared(points.get(i));
            
            if (dist >= maxDist)
            {
                maxDist = dist;
                splitPoint = i;
            }
        }
        
        return new ErrorRecord(maxDist, splitPoint);
    }
    
    /**
     * Use least-squares method to find Bezier control points for region.
     * 
     * @param first
     * @param last
     * @param uPrime
     * @param tHat1
     * @param tHat2
     * @return 
     */
    private BezierCubic2d generateBezier(int first, int last, 
            double[] uPrime, CyVector2d tHat1, CyVector2d tHat2)
    {
        int nPts = last - first + 1;
        
        //Compute the A's
        CyVector2d[][] A = new CyVector2d[nPts][2];
        for (int i = 0; i < nPts; ++i)
        {
            CyVector2d v1 = new CyVector2d(tHat1);
            v1.scale(bernstein1(uPrime[i]));
            
            CyVector2d v2 = new CyVector2d(tHat2);
            v2.scale(bernstein2(uPrime[i]));
            
            A[i][0] = v1;
            A[i][1] = v2;
        }
        
        //Create the C and X matrices
        double[][] C = new double[2][2];
        double[] X = new double[2];
        
        CyVector2d tmp = new CyVector2d();
        CyVector2d p0 = points.get(first);
        CyVector2d p3 = points.get(last);
        for (int i = 0; i < nPts; ++i)
        {
            C[0][0] += A[i][0].dot(A[i][0]);
            C[0][1] += A[i][0].dot(A[i][1]);
            C[1][0] = C[0][1];
            C[1][1] += A[i][1].dot(A[i][1]);

            tmp.set(points.get(i));
            tmp.subScaled(p0, bernstein0(uPrime[i]));
            tmp.subScaled(p0, bernstein1(uPrime[i]));
            tmp.subScaled(p3, bernstein2(uPrime[i]));
            tmp.subScaled(p3, bernstein3(uPrime[i]));
            
            X[0] += A[i][0].dot(tmp);
            X[1] += A[i][1].dot(tmp);
        }
        
        //Compute the determinants of C and X
        double det_C0_C1 = C[0][0] * C[1][1] - C[1][0] * C[0][1];
        double det_C0_X = C[0][0] * X[1] - C[1][0] * X[0];
        double det_X_C1 = X[0] * C[1][1] - X[1] * C[0][1];
        
        //Finally, derive alpha values
        double alphaL = det_C0_C1 == 0 ? 0 : det_X_C1 / det_C0_C1;
        double alphaR = det_C0_C1 == 0 ? 0 : det_C0_X / det_C0_C1;
        
        //If alpha negative, use the Wu/Barsky heuristic (see text)
        //(if alpha is 0, you get coincident control points that lead to
        //divide by zero in any subsequent NewtonRaphsonRootFind() call.
        double segLength = p0.distance(p3);
        double epsilon = 1.0e-6 * segLength;
        if (alphaL < epsilon || alphaR < epsilon)
        {
            //fall back on standard (probably inaccurate) formula, 
            //and subdivide further if needed.
            double dist = segLength / 3;
            
            CyVector2d p1 = new CyVector2d(p0);
            p1.addScaled(tHat1, dist);
            CyVector2d p2 = new CyVector2d(p3);
            p2.addScaled(tHat2, dist);
            
            return new BezierCubic2d(p0, p1, p2, p3);
        }
        
        //First and last control points of the Bezier curve are
        //positioned exactly at the first and last data points
        //Control points 1 and 2 are positioned an alpha distance out
        //on the tangent vectors, left and right, respectively
        CyVector2d p1 = new CyVector2d(p0);
        p1.addScaled(tHat1, alphaL);
        CyVector2d p2 = new CyVector2d(p3);
        p2.addScaled(tHat2, alphaR);
            
        return new BezierCubic2d(p0, p1, p2, p3);        
    }
    
    private double bernstein0(double u)
    {
        double a = 1 - u;
        return a * a * a;
    }
    
    private double bernstein1(double u)
    {
        double a = 1 - u;
        return 3 * a * a * u;
    }
    
    private double bernstein2(double u)
    {
        double a = 1 - u;
        return 3 * a * u * u;
    }
    
    private double bernstein3(double u)
    {
        return u * u * u;
    }
    
    /**
     * Given set of points and their parameterization, try to find
     * a better parameterization.
     * @param first
     * @param last
     * @param u
     * @param bezCurve
     * @return 
     */
    private double[] reparameterize(int first, int last, 
            double[] u, BezierCubic2d bezCurve)
    {
        double[] uPrime = new double[u.length];
        for (int i = 0; i < u.length; ++i)
        {
            double val = newtonRaphsonRootFind(bezCurve, 
                    points.get(i + first), u[i]);
            
            if (i != 0 && val < u[i - 1])
            {
                //Reparameterization not ascending.  Likely incorrect
                return null;
            }
//            if (val < 0 || val > 1)
//            {
//                //Function undefined for these values.  Not sure
//                // why they're occurring
//                val = Math2DUtil.clamp(val, 0, 1);
//            }
            uPrime[i] = val;
        }
        return uPrime;
    }
    
    /**
     * Use Newton-Raphson iteration to find better root.
     * 
     * @param Q
     * @param P
     * @param u
     * @return 
     */
    private double newtonRaphsonRootFind(BezierCubic2d Q, CyVector2d P, double u)
    {
        BezierQuad2d Q1 = Q.getDerivative();
        BezierLine2d Q2 = Q1.getDerivative();
        
        CyVector2d Q_u = new CyVector2d();
        CyVector2d Q1_u = new CyVector2d();
        CyVector2d Q2_u = new CyVector2d();
        
        //Compute Q(u)
        Q.evaluate(u, Q_u);
        Q1.evaluate(u, Q1_u);
        Q2.evaluate(u, Q2_u);

        //Compute f(u) / f'(u)
        double numerator = (Q_u.x - P.x) * Q1_u.x 
                + (Q_u.y - P.y) * Q1_u.y;
        double denominator = Q1_u.x * Q1_u.x
                + Q1_u.y * Q1_u.y
                + (Q_u.x - P.x) * Q2_u.x
                + (Q_u.y - P.y) * Q2_u.y;
        
        if (denominator == 0)
        {
            return u;
        }
        
        //u = u - f(u) / f'(u)
        return u - (numerator / denominator);
    }
    
    /**
     * Assign parameter values to digitized points 
     * using relative distances between points.
     * 
     * @param first
     * @param last
     * @return 
     */
    private double[] chordLengthParameterize(int first, int last)
    {
        double[] u = new double[last - first + 1];
        
        u[0] = 0;
        for (int i = 1; i < u.length; ++i)
        {
            CyVector2d p0 = points.get(i + first - 1);
            CyVector2d p1 = points.get(i + first);
            u[i] = u[i - 1] + p1.distance(p0);
        }
        
        for (int i = 1; i < u.length; ++i)
        {
            u[i] /= u[u.length - 1];
        }
        return u;
    }

    private CyVector2d computeForwardTangent(int index)
    {
        //Calc tangent of line of least squares fit of aproaching points
        CyVector2d tan = new CyVector2d();

        int size = points.size();
        for (int i = 0; i <= tangentNeighborhood; ++i)
        {
            int idx = Math2DUtil.modPos(index + i,
                    size);
            CyVector2d p = points.get(idx);
            tan.addScaled(p, leastSqCusp[0][i]);
        }
        tan.normalize();
        return tan;        
    }
    
    private CyVector2d computeBackwardTangent(int index)
    {
        //Calc tangent of line of least squares fit of aproaching points
        CyVector2d tan = new CyVector2d();

        int size = points.size();
        for (int i = 0; i <= tangentNeighborhood; ++i)
        {
            int idx = Math2DUtil.modPos(index - i,
                    size);
            CyVector2d p = points.get(idx);
            tan.addScaled(p, leastSqCusp[0][i]);
        }
        tan.normalize();
        return tan;
    }

    private void computeJoinTangents(int index, CyVector2d tBackward, CyVector2d tForward)
    {
        tBackward.set(computeBackwardTangent(index));
        tForward.set(computeForwardTangent(index));
        
        if (tForward.dot(tBackward) >= cornerAngleCos)
        {
            //Use cusp tangents
            return;
        }

        //Calc least squares fit
        CyVector2d tan = new CyVector2d();

        int size = points.size();
        for (int i = 0; i <= tangentNeighborhood * 2; ++i)
        {
            int idx = Math2DUtil.modPos(index - tangentNeighborhood + i,
                    size);
            CyVector2d p = points.get(idx);
            tan.addScaled(p, leastSqSmooth[0][i]);
        }
        tan.normalize();

        //Use smoothed tangent
        tForward.set(tan);
        tBackward.set(tan);
        tBackward.negate();
    }
 

    /*
    private CyVector2d computeForwardTangent(int index)
    {
        CyVector2d tan = new CyVector2d();
        CyVector2d dir = new CyVector2d();
        
        int size = points.size();
        CyVector2d p0 = points.get(index);
        for (int i = 1; i <= tangentNeighborhood; ++i)
        {
//            int idx = index + i;
//            while (idx >= size)
//            {
//                idx -= size;
//            }

            int idx = Math2DUtil.modPos(index + i, size);
            dir.set(points.get(idx));
            dir.sub(p0);
            dir.normalize();

            tan.add(dir);
        }
        
        tan.normalize();
        return tan;
    }

    private CyVector2d computeBackwardTangent(int index)
    {
        CyVector2d tan = new CyVector2d();
        CyVector2d dir = new CyVector2d();
        
        int size = points.size();
        CyVector2d p0 = points.get(index);
        for (int i = 1; i <= tangentNeighborhood; ++i)
        {
//            int idx = index - i;
//            while (idx < 0)
//            {
//                idx += size;
//            }
            
            int idx = Math2DUtil.modPos(index - i, size);
            dir.set(points.get(idx));
            dir.sub(p0);
            dir.normalize();

            tan.add(dir);
        }
        
        tan.normalize();
        return tan;
    }

    private void computeJoinTangents(int index, CyVector2d tBackward, CyVector2d tForward)
    {
        tBackward.set(computeBackwardTangent(index));
        tForward.set(computeForwardTangent(index));
        
        if (tForward.dot(tBackward) >= cornerAngleCos)
        {
            //Use cusp tangents
            return;
        }

        //Use smoothed tangent
        tForward.sub(tBackward);
        tForward.normalize();
        tBackward.set(tForward);
        tBackward.negate();
    }
    */
    
    /*
    private CyVector2d computeForwardTangent(int index)
    {
        CyVector2d p0 = points.get(index);
        CyVector2d p1 = index == points.size() - 1
                ? points.get(0)
                : points.get(index + 1);
        
        CyVector2d tan = new CyVector2d(p1);
        tan.sub(p0);
        tan.normalize();
        return tan;
    }

    private CyVector2d computeBackwardTangent(int index)
    {
        CyVector2d p0 = index == 0 
                ? points.get(points.size() - 1)
                : points.get(index - 1);
        CyVector2d p1 = points.get(index);
        
        CyVector2d tan = new CyVector2d(p0);
        tan.sub(p1);
        tan.normalize();
        return tan;
    }

    private void computeJoinTangents(int index, CyVector2d tBackward, CyVector2d tForward)
    {
        CyVector2d p0 = index == 0 
                ? points.get(points.size() - 1)
                : points.get(index - 1);
        CyVector2d p1 =  points.get(index);
        CyVector2d p2 = index == points.size() - 1
                ? points.get(0)
                : points.get(index + 1);
        
        tForward.sub(p2, p1);
        tForward.normalize();
        
        tBackward.sub(p0, p1);
        tBackward.normalize();
        
        if (tForward.dot(tBackward) >= cornerAngleCos)
        {
            //Use cusp tangents
            return;
        }

        //Use smoothed tangent
        tForward.sub(p2, p0);
        tForward.normalize();
        tBackward.set(tForward);
        tBackward.negate();
    }
    */
    
    //--------------------
    private static final class ErrorRecord
    {
        final double maxError;
        final int splitPoint;

        public ErrorRecord(double error, int splitPoint)
        {
            this.maxError = error;
            this.splitPoint = splitPoint;
        }

        @Override
        public String toString()
        {
            return "err " + maxError + " splitPt " + splitPoint;
        }
    }
    
    public static class FitCubicRecord
    {
        final BezierCubic2d curve;
        final int first;
        final int last;
        final CyVector2d tHat1;
        final CyVector2d tHat2;

        public FitCubicRecord(BezierCubic2d curve, int first, int last, CyVector2d tHat1, CyVector2d tHat2)
        {
            this.curve = curve;
            this.first = first;
            this.last = last;
            this.tHat1 = tHat1;
            this.tHat2 = tHat2;
        }

    }
}
