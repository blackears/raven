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

/**
 * http://www.cs.mtu.edu/~shene/COURSES/cs3621/NOTES/spline/B-spline/single-insertion.html
 *
 * u - knot value
 * m - max knot idx
 * n - max point idx
 * p - degree
 *
 * @author kitfox
 */
public class BSpline
{
    private double[] knots;

    //Each row is a separate point.  Each column is a point component in the
    // dimension of the spline (eg, a spline in 3D space would have three columns).
    double[][] points;

    public BSpline(double[] knots, double[][] points)
    {
        this.knots = knots;
        this.points = points;
    }

    public BSpline(BSpline spline)
    {
        this.knots = spline.knots.clone();
        this.points = new double[spline.points.length][];
        for (int i = 0; i < points.length; ++i)
        {
            this.points[i] = spline.points[i].clone();
        }
    }

    public double getPoint(int index, int component)
    {
        return points[index][component];
    }

    public double[] getPoint(int index)
    {
        return points[index].clone();
    }

    public int getDegree()
    {
        return knots.length - points.length - 1;
    }

    public int getOrder()
    {
        return knots.length - points.length;
    }

    public int getMaxPointIndex()
    {
        return points.length - 1;
    }

    public int getNumPoints()
    {
        return points.length;
    }

    public int getSpanIndex(double u)
    {
        for (int i = 0; i < knots.length - 1; ++i)
        {
            if (knots[i] <= u && knots[i + 1] > u)
            {
                return i;
            }
        }
        
        return -1;
    }

    /**
     *
     * @param u
     * @return Number of times u appears in the knot vector
     */
    public int getMultiplicity(double u)
    {
        int sum = 0;
        for (int i = 0; i < knots.length; ++i)
        {
            if (knots[i] == u)
            {
                ++sum;
            }
            else if (knots[i] > u)
            {
                break;
            }
        }
        return sum;
    }

    private double[] lerp(double[] t0, double[] t1, double a)
    {
        double[] val = new double[t0.length];
        for (int i = 0; i < val.length; ++i)
        {
            val[i] = (1 - a) * t0[i] + a * t1[i];
        }
        return val;
    }

    public void insertKnot(double t)
    {
        int k = getSpanIndex(t);
        int p = getDegree();
        //Used in optimization for when inserting at multiple knot.
        int s = getMultiplicity(t); 

        //Build new points
        double[][] newPoints = new double[points.length + 1][];
        for (int i = 0; i <= k - p; ++i)
        {
            //Copy first points
            newPoints[i] = points[i];
        }

        for (int i = k - p + 1; i <= k - s; ++i)
        {
            double a = (t - knots[i]) / (knots[i + p] - knots[i]);
            newPoints[i] = lerp(points[i - 1], points[i], a);
        }

        for (int i = k - s; i < points.length; ++i)
        {
            //Copy last points
            newPoints[i + 1] = points[i];
        }

        //Insert into knot vector
        double[] newKnots = new double[knots.length + 1];
        for (int i = 0; i <= k; ++i)
        {
            newKnots[i] = knots[i];
        }
        newKnots[k + 1] = t;
        for (int i = k + 1; i < knots.length; ++i)
        {
            newKnots[i + 1] = knots[i];
        }

        points = newPoints;
        knots = newKnots;
    }

//    /**
//     *
//     * @param t Value of knot to insert
//     * @param h Times to insert knot
//     */
//    public void insertKnotMultiple(double t, int h)
//    {
//        int k = getSpanIndex(t);
//        int p = getDegree();
//        int s = getMultiplicity(t);
//
//        if (h + s > p)
//        {
//            throw new IllegalArgumentException("<times to insert> + <current knot multiplicity> cannot be greater than degree of spline");
//        }
//
//        for (int r = 1; r <= h; ++r)
//        {
//            for (int i = k - p + r; i <= k - s; ++i)
//            {
//                double a = (t - knots[i]) / (knots[i + p - r + 1] - knots[i]);
//                lerp();
//            }
//        }
//    }

    /**
     * @return the knots
     */
    public double[] getKnots()
    {
        return knots.clone();
    }

    public double[] getDistinctKnots()
    {
        int count = 0;
        double curKnot = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < knots.length; ++i)
        {
            if (knots[i] != curKnot)
            {
                ++count;
                curKnot = knots[i];
            }
        }

        double[] distinct = new double[count];
        count = 0;
        curKnot = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < knots.length; ++i)
        {
            if (knots[i] != curKnot)
            {
                distinct[count++] = knots[i];
                curKnot = knots[i];
            }
        }

        return distinct;
    }

    /**
     * Go through all interior knots and insert new knots until their multiplicity
     * is equal to the degree of the curve.
     *
     * After this is called, the control points of this spline will form the
     * hull of a piecewise bezier of degree equal to the curent degree of the
     * spline.
     */
    public void splitIntoBeziers()
    {
        int p = getDegree();
        double[] distinct = getDistinctKnots();

        for (int i = 1; i < distinct.length - 1; ++i)
        {
            double t = distinct[i];
            int s = getMultiplicity(t);
            for (int j = s; j < p; ++j)
            {
                insertKnot(t);
            }
        }
    }
}
