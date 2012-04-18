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

package com.kitfox.coyote.math.bezier;

import com.kitfox.coyote.math.GMatrix;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Based on:
 * Least-Squares Fitting of Data with B-Spline Curves
 * David Eberly
 * http://www.geometrictools.com/Documentation/Documentation.html
 *
 * Also:
 * http://www.ibiblio.org/e-notes/Splines/Basis.htm
 *
 * @author kitfox
 */
public class FitCurve
{
    private FitCurve()
    {
    }

    /**
     * Create a vector of evenly spaced knots.  This is the 'default' spline
     * vector and is mostly useful for closed spline curves.  Note that the
     * valid range for evaluating this spline is t on 
     * [(degree/(n + degree)) (n/(n + degree))].  That is, only evaluate this
     * spline for t where knot[degree] <= t <= knot[n]
     *
     * @param n Max point index in spline
     * @param order For spline of degree d, the order is (d + 1)
     * @return
     */
    public static double[] createUniformKnots(int n, int order)
    {
//        int degree = order - 1;
        int numKnots = n + order + 1;
        double[] knots = new double[numKnots];
        for (int i = 0; i < numKnots; ++i)
        {
            knots[i] = (double)i / (numKnots - 1);
        }
        return knots;
    }

    /**
     * Create a vector of evenly spaced knots for an open spline.  This means
     * the first and last knots are repeated 'degree' times and the midpoints
     * uniformly distributed.  This knot vector will produce a spline that
     * starts and ends at the start and end spline points and has a valid
     * range for t on [0 1]
     *
     * @param n Max point index in spline
     * @param order For spline of degree d, the order is (d + 1)
     * @return
     */
    public static double[] createOpenUniformKnots(int n, int order)
    {
        int degree = order - 1;
        int numKnots = n + order + 1;
        double[] knots = new double[numKnots];
        for (int i = 0; i < numKnots; ++i)
        {
            if (i < degree + 1)
            {
                knots[i] = 0;
            }
            else if (i > numKnots - degree - 2)
            {
                knots[i] = 1;
            }
            else
            {
                knots[i] = (double)(i - degree) / (numKnots - degree * 2 - 1);
            }
        }
        return knots;
    }

    /**
     *
     * @param n Index of max control point.  There are n + 1 total control
     * points to evaluate
     * @param d Degree of spline we are evaluating.
     * @param i Index of segment to evaluate
     * @param t Time to evaluate function at
     * @return
     */
    public static double splineCoeff(int n, int i, int d, double[] knots, double t)
    {
        if (d == 0)
        {
            for (int w = 0; w < knots.length - 1; ++w)
            {
                if (knots[w] <= t && t < knots[w + 1])
                {
                    return w == i ? 1 : 0;
                }
            }
//            return 0;
            //We went past the end.  t >= knots[knots.length - 1]
            //Technically this is undefined, but we need to deal with t being
            // equal to the last knot.  Roll back until we find the first
            // interval of non-zero span which contains the last knot value.

            if (t >= knots[knots.length -1])
            {
                t = knots[knots.length - 1];
                for (int w = knots.length - 2; w >= 0; --w)
                {
                    if (knots[w] < t && t <= knots[w + 1])
                    {
                        return w == i ? 1 : 0;
                    }
                }
            }


            return 0;
        }

        double ti = knots[i];
        double tij = knots[i + d];
        double ti1 = knots[i + 1];
        double tij1 = knots[i + d + 1];

        double c0 = splineCoeff(n, i, d - 1, knots, t);
        double c1 = splineCoeff(n, i + 1, d - 1, knots, t);

        if (c0 == 0 && c1 == 0)
        {
            return 0;
        }
        else if (c1 == 0)
        {
            return (t - ti) / (tij - ti) * c0;
        }
        else if (c0 == 0)
        {
            return (tij1 - t) / (tij1 - ti1) * c1;
        }
        else
        {
            return (t - ti) / (tij - ti) * c0
                + (tij1 - t) / (tij1 - ti1) * c1;
        }

//        return (t - ti) / (tij - ti) * splineCoeff(n, i, d - 1, knots, t)
//                + (tij1 - t) / (tij1 - ti1) * splineCoeff(n, i + 1, d - 1, knots, t);
    }

    /**
     *
     * @param degree
     * @param Ptimes
     * @param P
     * @return
     */
    public static GMatrix fitBSpline(int degree, double[] Ptimes, GMatrix P,
            int numFitPoints, double[] Qknots)
    {
        //Map point knots to [0 1]
        double[] PtimesNormal = new double[Ptimes.length];
        double pTimesSpan = Ptimes[Ptimes.length - 1] - Ptimes[0];
        for (int i = 0; i < PtimesNormal.length; ++i)
        {
            PtimesNormal[i] = (Ptimes[i] - Ptimes[0]) / pTimesSpan;
        }

        GMatrix A = new GMatrix(P.getNumRow(), numFitPoints);
        for (int r = 0; r < P.getNumRow(); ++r)
        {
            for (int c = 0; c < numFitPoints; ++c)
            {
                A.setElement(r, c, splineCoeff(numFitPoints - 1,
                        c, degree, Qknots, PtimesNormal[r]));
            }
        }
//System.err.println(A);


        GMatrix AT = new GMatrix(A);
        AT.transpose();

        //Least squares matrix
        //(A^T A)^-1 A^T
        GMatrix ATAI = new GMatrix(numFitPoints, numFitPoints);
        ATAI.mul(AT, A);
        ATAI.invert();

        GMatrix X = new GMatrix(AT.getNumRow(), AT.getNumCol());
        X.mul(ATAI, AT);

        GMatrix Q = new GMatrix(X.getNumRow(), P.getNumCol());
        Q.mul(X, P);
        return Q;
    }


    /**
     *
     * @param degree Degree of Bezier to fit.  Note that P must have at least
     * degree + 1 rows, or the system will be underdetermined.
     * @param Ptimes Non decreasing set of values that spana [0 1].  Indicate time
     * samples of each P data point.
     * @param P Matrix of data points to fit.  Columns indicate point
     * components (x, y, z, etc) and rows indicate individual points.
     * @return A (degree + 1) x (P.numColumns) matrix of controls points of the
     * Bezier curve of best fit.
     */
    public static GMatrix fitBezier(int degree, double[] Ptimes, GMatrix P)
    {
//        double PtimeSpan = Ptimes[Ptimes.length - 1] - Ptimes[0];
//        double PtimeStart = Ptimes[0];
//        for (int i = 0; i < Ptimes.length; ++i)
//        {
//            Ptimes[i] = (Ptimes[i] - PtimeStart) / PtimeSpan;
//        }


        GMatrix A = new GMatrix(P.getNumRow(), degree + 1);

        for (int m = 0; m < P.getNumRow(); ++m)
        {
            for (int n = 0; n <= degree; ++n)
            {
                A.setElement(m, n, bernstein(degree, n, Ptimes[m]));
            }
        }

        GMatrix AT = new GMatrix(A);
        AT.transpose();

        //Least squares matrix
        //(A^T A)^-1 A^T
        GMatrix ATAI = new GMatrix(degree + 1, degree + 1);
        ATAI.mul(AT, A);
        ATAI.invert();

        GMatrix X = new GMatrix(AT.getNumRow(), AT.getNumCol());
        X.mul(ATAI, AT);

        GMatrix Q = new GMatrix(X.getNumRow(), P.getNumCol());
        Q.mul(X, P);
        return Q;
    }

    /**
     * Least squares fitting of the control knots of a bezier.  Using this method
     * will only try to fit the knots, so the start and end points of the final
     * bezier are equal to the start and end points of the input P vector.
     *
     * @param degree Degree of bezier to build
     * @param Ptimes Non decreasing set of values that spans [0 1].  Indicate time
     * samples of each P data point.
     * @param P Data values to fit.
     * @return Vector of the knot values of least squares fit spline.  The start
     * and end points of the spline are omitted from this vector.
     */
    public static GMatrix fitBezierKnots(int degree, double[] Ptimes, GMatrix P)
    {
        if (P.getNumRow() == 2)
        {
            //Line segment
            GMatrix Q = new GMatrix(2, P.getNumCol());
            for (int c = 0; c < P.getNumCol(); ++c)
            {
                Q.setElement(0, c, lerp(P.getElement(0, c), P.getElement(1, c), 1 / 3.0));
                Q.setElement(1, c, lerp(P.getElement(0, c), P.getElement(1, c), 2 / 3.0));
            }
            return Q;
        }

        //Build PC, the P matrix with the contant contribution of the first and
        // last points subtracted out.
        GMatrix PC = new GMatrix(P.getNumRow(), P.getNumCol());
        for (int r = 0; r < P.getNumRow(); ++r)
        {
            for (int c = 0; c < P.getNumCol(); ++c)
            {
                PC.setElement(r, c, P.getElement(r, c)
                        //Influence of first point
                        - bernstein(degree, 0, Ptimes[r]) * P.getElement(0, c)
                        //Influence of last point
                        - bernstein(degree, degree, Ptimes[r]) * P.getElement(P.getNumRow() - 1, c));
            }
        }


        GMatrix A = new GMatrix(PC.getNumRow(), degree - 1);

        for (int m = 0; m < PC.getNumRow(); ++m)
        {
            for (int n = 1; n <= degree - 1; ++n)
            {
                A.setElement(m, n - 1, bernstein(degree, n, Ptimes[m]));
            }
        }

        GMatrix AT = new GMatrix(A);
        AT.transpose();

        //Least squares matrix
        //(A^T A)^-1 A^T
        GMatrix ATAI = new GMatrix(degree - 1, degree - 1);
        ATAI.mul(AT, A);
        try
        {
            ATAI.invert();
        }
        catch (Exception ex)
        {
            Logger.getLogger(FitCurve.class.getName()).log(Level.SEVERE, null, ex);
        }

        GMatrix X = new GMatrix(AT.getNumRow(), AT.getNumCol());
        X.mul(ATAI, AT);

        GMatrix Q = new GMatrix(X.getNumRow(), PC.getNumCol());
        Q.mul(X, PC);
        return Q;
    }

    private static double bernstein(int degree, int knotIdx, double t)
    {
        return choose(degree, knotIdx) 
                * Math.pow(t, knotIdx) * Math.pow(1 - t, degree - knotIdx);
    }

    private static int choose(int a, int b)
    {
        int b0 = b;
        int b1 = a - b;
        if (b1 < b0)
        {
            int tmp = b0;
            b0 = b1;
            b1 = tmp;
        }

        int product = 1;
        for (int k = b1 + 1; k <= a; ++k)
        {
            product *= k;
        }

        for (int k = b0; k >= 2; --k)
        {
            product /= k;
        }

        return product;
    }

    public static double[] evalBezier(GMatrix B, double t)
    {
        return evalBezier(B, t, null);
    }

    public static double[] evalBezier(GMatrix B, double t, double[] result)
    {
        int degree = B.getNumRow() - 1;
        if (result == null)
        {
            result = new double[B.getNumCol()];
        }
        else
        {
            Arrays.fill(result, 0);
        }
        
        for (int r = 0; r <= degree; ++r)
        {
            double b = bernstein(degree, r, t);
            for (int c = 0; c < B.getNumCol(); ++c)
            {
                result[c] += b * B.getElement(r, c);
            }
        }
        return result;
    }

    /**
     * Computes the derivative of a bezier.  Note that the degree of the
     * result will be one less than the degree of the input.
     *
     * http://www.cs.mtu.edu/~shene/COURSES/cs3621/NOTES/spline/Bezier/bezier-der.html
     *
     * @param B Points of a bezier of degree (P.numRows - 1)
     * @return Points of derivative bezier
     */
    public static GMatrix derivativeBezier(GMatrix B)
    {
        int degree = B.getNumRow() - 1;

        GMatrix D = new GMatrix(B.getNumRow() - 1, B.getNumCol());
        for (int i = 0; i < degree; ++i)
        {
            for (int j = 0; j < B.getNumCol(); ++j)
            {
                D.setElement(i, j, degree * (B.getElement(i + 1, j) - B.getElement(i, j)));
            }
        }

        return D;
    }

    public static double[] getPoint(GMatrix B, int row)
    {
        return getPoint(B, row, null);
    }

    public static double[] getPoint(GMatrix B, int row, double[] result)
    {
        if (result == null)
        {
            result = new double[B.getNumRow()];
        }

        for (int c = 0; c < B.getNumCol(); ++c)
        {
            result[c] = B.getElement(row, c);
        }
        return result;
    }

    public static void setPoint(GMatrix B, int row, double[] value)
    {
        for (int c = 0; c < B.getNumCol(); ++c)
        {
            B.setElement(row, c, value[c]);
        }
    }

    public static double[] sub(double[] p0, double[] p1)
    {
        return sub(p0, p1, null);
    }

    public static double[] sub(double[] p0, double[] p1, double[] result)
    {
        if (result == null)
        {
            result = new double[p0.length];
        }

        for (int i = 0; i < p0.length; ++i)
        {
            result[i] = p0[i] - p1[i];
        }
        return result;
    }

    public static double[] add(double[] p0, double[] p1)
    {
        return sub(p0, p1, null);
    }

    public static double[] add(double[] p0, double[] p1, double[] result)
    {
        if (result == null)
        {
            result = new double[p0.length];
        }

        for (int i = 0; i < p0.length; ++i)
        {
            result[i] = p0[i] + p1[i];
        }
        return result;
    }

    public static double lerp(double p0, double p1, double t)
    {
        return (1 - t) * p0 + t * p1;
    }

    public static double[] lerp(double[] p0, double[] p1, double t)
    {
        return lerp(p0, p1, t, null);
    }

    public static double[] lerp(double[] p0, double[] p1, double t, double[] result)
    {
        if (result == null)
        {
            result = new double[p0.length];
        }

        for (int i = 0; i < p0.length; ++i)
        {
            result[i] = lerp(p0[i], p1[i], t);
        }
        return result;
    }

    public static double[] scale(double[] p0, double s)
    {
        return scale(p0, s, null);
    }

    public static double[] scale(double[] p0, double s, double[] result)
    {
        if (result == null)
        {
            result = new double[p0.length];
        }

        for (int i = 0; i < p0.length; ++i)
        {
            result[i] = s * p0[i];
        }
        return result;
    }

    public static double dot(double[] p0, double[] p1)
    {
        double result = 0;
        for (int i = 0; i < p0.length; ++i)
        {
            result += p0[i] * p1[i];
        }
        return result;
    }

    public static double[] cross(double[] p0, double[] p1, double[] result)
    {
        if (p0.length != 3 || p1.length != 3)
        {
            throw new IllegalArgumentException();
        }

        if (result == null)
        {
            result = new double[3];
        }
        result[0] = p0[1] * p1[2] - p0[2] * p1[1];
        result[1] = p0[2] * p1[0] - p0[0] * p1[2];
        result[2] = p0[0] * p1[1] - p0[1] * p1[0];
        return result;
    }

    public static double[] getBezierSpan(GMatrix B)
    {
        return getLinearAprox(B, null);
    }

    public static double[] getLinearAprox(GMatrix B, double[] result)
    {
        return sub(getPoint(B, B.getNumRow() - 1), getPoint(B, 0), result);
    }

    public static double square(double value)
    {
        return value * value;
    }

    public static double lengthSquared(double[] p0)
    {
        return dot(p0, p0);
    }

    public static double distanceSquared(double[] p0, double[] p1)
    {
        double result = 0;
        for (int i = 0; i < p0.length; ++i)
        {
            result += square(p0[i] - p1[i]);
        }
        return result;
    }

    public static double[] normalize(double[] p0)
    {
        return normalize(p0, null);
    }

    public static double[] normalize(double[] p0, double[] result)
    {
        double magI = 1 / Math.sqrt(dot(p0, p0));
        for (int i = 0; i < p0.length; ++i)
        {
            result[i] *= magI;
        }
        return result;
    }

    /**
     * Estimate flatness by calculating max distance from any knot to the line
     * segment from the first to the last bezier point.
     *
     * @param B Bezier curve to determine flatness of
     * @return
     */
    public static double flatnessSquared(GMatrix B)
    {
        double flat = 0;

        double[] vLin = getBezierSpan(B);
        for (int i = 1; i < B.getNumRow() - 1; ++i)
        {
            double[] v = sub(getPoint(B, i), getPoint(B, 0));

            //Project point onto vLin
            double[] v2 = scale(vLin, dot(v, vLin) / dot(vLin, vLin));
            flat = Math.max(flat, distanceSquared(v, v2));
        }
        
        return flat;
    }

    private static void splitDeCasteljau(double[][] points, double t, GMatrix R0, GMatrix R1)
    {
        int order = R0.getNumRow();
        int depth = order - points.length;
        setPoint(R0, depth, points[0]);
        setPoint(R1, order - depth - 1, points[points.length - 1]);

        if (points.length == 0)
        {
            return;
        }
        
        double[][] res = new double[points.length - 1][];
        for (int i = 0; i < res.length; ++i)
        {
            res[i] = lerp(points[i], points[i + 1], t);
        }
        splitDeCasteljau(res, t, R0, R1);
    }

    public static GMatrix[] splitBezier(GMatrix B, double t)
    {
        GMatrix R0 = new GMatrix(B.getNumRow(), B.getNumCol());
        GMatrix R1 = new GMatrix(B.getNumRow(), B.getNumCol());

        int degree = B.getNumRow() - 1;
        double points[][] = new double[degree + 1][];
        for (int i = 0; i <= degree; ++i)
        {
            points[i] = getPoint(B, i);
        }
        splitDeCasteljau(points, t, R0, R1);

        return new GMatrix[]{R0, R1};
    }

    private static ArrayList<FlattenRecord> flatten(GMatrix B,
            double flatToleranceSquared,
            ArrayList<FlattenRecord> result,
            double tOffset, double tSpan)
    {
        if (flatnessSquared(B) <= flatToleranceSquared)
        {
            result.add(new FlattenRecord(getPoint(B, 0), tOffset));
            return result;
        }

        GMatrix[] pair = splitBezier(B, .5);
        flatten(pair[0], flatToleranceSquared, result, tOffset, tSpan / 2);
        flatten(pair[1], flatToleranceSquared, result, tOffset + tSpan, tSpan / 2);
        return result;
    }

    /**
     * Append points to a list that are the vertices of a flattned bezier curve.
     * Points are appended in increasing order.
     *
     * Note that the last point of the Bezier curve is not appended to the
     * output (however, the first one is).
     *
     * @param B
     * @param flatToleranceSquared
     * @param result
     * @return
     */
    public static ArrayList<FlattenRecord> flatten(GMatrix B,
            double flatToleranceSquared,
            ArrayList<FlattenRecord> result)
    {
        return flatten(B, flatToleranceSquared, result, 0, 1);
    }

    /**
     * Calculates set of line segments offset from a flattened bezier curve.
     * B must be in the second dimension.
     *
     * @param B Bezier spline to offset from
     * @param offset0 Amount to offset at start point
     * @param offset1 Amount to offset at end point
     * @param flatToleranceSquared Tolerance value for flattening the path
     * @return
     */
    public static ArrayList<FlattenRecord> offset2D(GMatrix B,
            double offset0, double offset1, double flatToleranceSquared,
            ArrayList<FlattenRecord> result)
    {
        if (B.getNumCol() != 2)
        {
            throw new IllegalArgumentException("This version of offset only works for 2D beziers");
        }

        if (result == null)
        {
            result = new ArrayList<FlattenRecord>();
        }

        GMatrix D1 = derivativeBezier(B);

        ArrayList<FlattenRecord> list = new ArrayList<FlattenRecord>();
        flatten(B, flatToleranceSquared, list);
        //Append last point - this is left out by flatten method
        list.add(new FlattenRecord(getPoint(B, B.getNumRow() - 1), 1));

        double[] d = new double[B.getNumCol()];
        for (int i = 0; i < list.size(); ++i)
        {
            FlattenRecord r0 = list.get(i);

            //Find normal
            evalBezier(D1, r0.t, d);
            normalize(d, d);
            scale(d, lerp(offset0, offset1, r0.t), d);

            //90 degree rotation
            double[] pt = new double[2];
            pt[0] = -r0.point[1];
            pt[1] = r0.point[0];
            FlattenRecord r1 = new FlattenRecord(add(pt, d), r0.t);
            result.add(r1);
        }

        return result;
    }

    /**
     *
     * Calculates set of line segments offset from a flattened bezier curve.
     * Offset is allways to the right of the curve for positive offset values.
     *
     * @param B Bezier spline to offset from
     * @param offset0 Amount to offset at start point
     * @param offset1 Amount to offset at end point
     * @param flatToleranceSquared Tolerance value for flattening the path
     * @return
     */
    public static ArrayList<FlattenRecord> offset3D(GMatrix B,
            double offset0, double offset1, double flatToleranceSquared,
            ArrayList<FlattenRecord> result)
    {
        GMatrix D1 = derivativeBezier(B);
        //Second derivative indicates curvature
        GMatrix D2 = derivativeBezier(D1);

        ArrayList<FlattenRecord> list = new ArrayList<FlattenRecord>();
        flatten(B, flatToleranceSquared, list);

        double[] d1 = new double[B.getNumCol()];
        double[] d2 = new double[B.getNumCol()];
        double[] binorm = new double[B.getNumCol()];
        double[] norm = new double[B.getNumCol()];
        for (int i = 0; i < list.size(); ++i)
        {
            FlattenRecord r0 = list.get(i);

            //Find normal
            evalBezier(D1, r0.t, d1);
            evalBezier(D2, r0.t, d2);

            cross(d1, d2, binorm);
            cross(binorm, d1, norm);
            normalize(norm, norm);
            
            scale(norm, lerp(offset0, offset1, r0.t), norm);
            FlattenRecord r1 = new FlattenRecord(add(r0.point, norm), r0.t);
            result.add(r1);
        }

        return result;
    }

//    private static ArrayList<FlattenRecord> reverse(ArrayList<FlattenRecord> list)
//    {
//        for (int i = 0; i < (list.size() - 1) / 2; ++i)
//        {
//            int j = list.size() - 1 - i;
//            FlattenRecord tmp = list.get(i);
//            list.set(i, list.get(j));
//            list.set(j, tmp);
//        }
//        return list;
//    }
//
//    public static void outlinePath2D(ArrayList<Outline2DCurveRecord> curves,
//            double flatToleranceSquared, Cap cap, Join join,
//            boolean contourOnly)
//    {
//        //Turn input curves into list of segments.  Index segment lists by their
//        // starting and ending vertices.
//        HashMap<Outline2DVertex.Key, Outline2DVertex> vtxMap =
//                new HashMap<Outline2DVertex.Key, Outline2DVertex>();
//
//        for (Outline2DCurveRecord curve: curves)
//        {
//            Outline2DVertex.Key vk0 = new Outline2DVertex.Key(
//                    curve.B.getElement(0, 0), curve.B.getElement(0, 1),
//                    curve.weightStart);
//            Outline2DVertex.Key vk1 = new Outline2DVertex.Key(
//                    curve.B.getElement(curve.B.getNumRow() - 1, 0),
//                    curve.B.getElement(curve.B.getNumRow() - 1, 1),
//                    curve.weightStart);
//
//            Outline2DVertex v0 = vtxMap.get(vk0);
//            if (v0 == null)
//            {
//                v0 = new Outline2DVertex(vk0);
//                vtxMap.put(vk0, v0);
//            }
//
//            Outline2DVertex v1 = vtxMap.get(vk1);
//            if (v1 == null)
//            {
//                v1 = new Outline2DVertex(vk1);
//                vtxMap.put(vk1, v1);
//            }
//
//
//            Outline2DCurveRecord curve1 = curve.getReverse();
//            v0.curveOut.add(curve);
//            v0.curveIn.add(curve1);
//            v1.curveOut.add(curve1);
//            v1.curveIn.add(curve);
//        }
//
//        //Separate mesh into concentric rings
//
//
//        //Go through all vertices and join open ends
//
//    }
//
    //----------------------------------

    public static class FlattenRecord
    {
        double[] point;
        double t;

        public FlattenRecord(double[] point, double t)
        {
            this.point = point;
            this.t = t;
        }
    }
}
