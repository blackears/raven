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

package com.kitfox.coyote.shape.bezier;

import com.kitfox.coyote.math.CyVector2d;

/**
 *
 * @author kitfox
 */
public class BezierFit2d
{
    /**
     * Assign parameter values to digitized points 
     * using relative distances between points.
     * 
     * @param first
     * @param last
     * @return 
     */
    public static double[] chordLengthParameterize(int first, int last,
            CyVector2d[] points)
    {
        double[] u = new double[last - first + 1];
        
        u[0] = 0;
        for (int i = 1; i < u.length; ++i)
        {
            CyVector2d p0 = points[i + first - 1];
            CyVector2d p1 = points[i + first];
            u[i] = u[i - 1] + p1.distance(p0);
        }
        
        for (int i = 1; i < u.length; ++i)
        {
            u[i] /= u[u.length - 1];
        }
        return u;
    }
    
    /**
     * Use least-squares method to find Bezier control points for region.
     * 
     * @param first Index of first point to fit curve to
     * @param last Index of last point to fit curve to
     * @param uPrime t values for initial parameterization
     * @param points Array of points to fit
     * @param tHat1
     * @param tHat2
     * @return 
     */
    public static BezierCubic2d generateBezier(int first, int last, 
            double[] uPrime, CyVector2d[] points,
            CyVector2d tHat1, CyVector2d tHat2)
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
        CyVector2d p0 = points[first];
        CyVector2d p3 = points[last];
        for (int i = 0; i < nPts; ++i)
        {
            C[0][0] += A[i][0].dot(A[i][0]);
            C[0][1] += A[i][0].dot(A[i][1]);
            C[1][0] = C[0][1];
            C[1][1] += A[i][1].dot(A[i][1]);

            tmp.set(points[i]);
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
    
    private static double bernstein0(double u)
    {
        double a = 1 - u;
        return a * a * a;
    }
    
    private static double bernstein1(double u)
    {
        double a = 1 - u;
        return 3 * a * a * u;
    }
    
    private static double bernstein2(double u)
    {
        double a = 1 - u;
        return 3 * a * u * u;
    }
    
    private static double bernstein3(double u)
    {
        return u * u * u;
    }
    
}
