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

/**
 *
 * @author kitfox
 */
public class CyMatrix3d
{
    double m00;
    double m10;
    double m20;
    double m01;
    double m11;
    double m21;
    double m02;
    double m12;
    double m22;
    
    private static final double EPS = 1.110223024E-16;

    public CyMatrix3d()
    {
    }

    public CyMatrix3d(double m00, double m10, double m20, double m01, double m11, double m21, double m02, double m12, double m22)
    {
        this.m00 = m00;
        this.m10 = m10;
        this.m20 = m20;
        this.m01 = m01;
        this.m11 = m11;
        this.m21 = m21;
        this.m02 = m02;
        this.m12 = m12;
        this.m22 = m22;
    }

    /**
     * Sets this Matrix3d to identity.
     */
    public final void setIdentity()
    {
        this.m00 = 1.0;
        this.m01 = 0.0;
        this.m02 = 0.0;

        this.m10 = 0.0;
        this.m11 = 1.0;
        this.m12 = 0.0;

        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 1.0;
    }

    /**
     * Sets the value of this matrix to the sum of itself and matrix m1.
     * @param m1 the other matrix
     */
    public final void add(CyMatrix3d m1)
    {
        this.m00 += m1.m00;
        this.m01 += m1.m01;
        this.m02 += m1.m02;

        this.m10 += m1.m10;
        this.m11 += m1.m11;
        this.m12 += m1.m12;

        this.m20 += m1.m20;
        this.m21 += m1.m21;
        this.m22 += m1.m22;
    }

    /**
     * Sets the value of this matrix to the matrix difference of itself and
     * matrix m1 (this = this - m1).
     * @param m1 the other matrix
     */
    public final void sub(CyMatrix3d m1)
    {
        this.m00 -= m1.m00;
        this.m01 -= m1.m01;
        this.m02 -= m1.m02;

        this.m10 -= m1.m10;
        this.m11 -= m1.m11;
        this.m12 -= m1.m12;

        this.m20 -= m1.m20;
        this.m21 -= m1.m21;
        this.m22 -= m1.m22;
    }

    /**
     * Sets the value of this matrix to its transpose.
     */
    public final void transpose()
    {
        double temp;

        temp = this.m10;
        this.m10 = this.m01;
        this.m01 = temp;

        temp = this.m20;
        this.m20 = this.m02;
        this.m02 = temp;

        temp = this.m21;
        this.m21 = this.m12;
        this.m12 = temp;
    }

    /**
     * Sets the value of this matrix to the matrix conversion of the
     * double precision quaternion argument.
     * @param q1 the quaternion to be converted
     */
    public final void setQuaternion(CyVector4d q1)
    {
        this.m00 = (1.0 - 2.0 * q1.y * q1.y - 2.0 * q1.z * q1.z);
        this.m10 = (2.0 * (q1.x * q1.y + q1.w * q1.z));
        this.m20 = (2.0 * (q1.x * q1.z - q1.w * q1.y));

        this.m01 = (2.0 * (q1.x * q1.y - q1.w * q1.z));
        this.m11 = (1.0 - 2.0 * q1.x * q1.x - 2.0 * q1.z * q1.z);
        this.m21 = (2.0 * (q1.y * q1.z + q1.w * q1.x));

        this.m02 = (2.0 * (q1.x * q1.z + q1.w * q1.y));
        this.m12 = (2.0 * (q1.y * q1.z - q1.w * q1.x));
        this.m22 = (1.0 - 2.0 * q1.x * q1.x - 2.0 * q1.y * q1.y);
    }

    /**
     * Sets the value of this matrix to the matrix conversion of the
     * double precision axis and angle argument.
     * @param a1 the axis and angle to be converted
     */
    public final void setAxisAngle(CyVector4d a1)
    {
        double mag = Math.sqrt(a1.x * a1.x + a1.y * a1.y + a1.z * a1.z);

        if (mag < EPS)
        {
            m00 = 1.0;
            m01 = 0.0;
            m02 = 0.0;

            m10 = 0.0;
            m11 = 1.0;
            m12 = 0.0;

            m20 = 0.0;
            m21 = 0.0;
            m22 = 1.0;
        } else
        {
            mag = 1.0 / mag;
            double ax = a1.x * mag;
            double ay = a1.y * mag;
            double az = a1.z * mag;

            double sinTheta = Math.sin(a1.w);
            double cosTheta = Math.cos(a1.w);
            double t = 1.0 - cosTheta;

            double xz = ax * az;
            double xy = ax * ay;
            double yz = ay * az;

            m00 = t * ax * ax + cosTheta;
            m01 = t * xy - sinTheta * az;
            m02 = t * xz + sinTheta * ay;

            m10 = t * xy + sinTheta * az;
            m11 = t * ay * ay + cosTheta;
            m12 = t * yz - sinTheta * ax;

            m20 = t * xz - sinTheta * ay;
            m21 = t * yz + sinTheta * ax;
            m22 = t * az * az + cosTheta;
        }
    }

    /**
     * Sets the value of this matrix to the value of the Matrix3d
     * argument.
     * @param m1 the source matrix3d
     */
    public final void set(CyMatrix3d m1)
    {
        this.m00 = m1.m00;
        this.m01 = m1.m01;
        this.m02 = m1.m02;

        this.m10 = m1.m10;
        this.m11 = m1.m11;
        this.m12 = m1.m12;

        this.m20 = m1.m20;
        this.m21 = m1.m21;
        this.m22 = m1.m22;
    }

    /**
     * Inverts this matrix in place.
     */
    public final void invert()
    {
        invertGeneral(this);
    }

    /**
     * Sets the value of this matrix to the matrix inverse
     * of the passed matrix m1.
     * @param m1 the matrix to be inverted
     */
    public final void invert(CyMatrix3d m1)
    {
	    invertGeneral(m1);
    }

    /**
     * General invert routine.  Inverts m1 and places the result in "this".
     * Note that this routine handles both the "this" version and the
     * non-"this" version.
     *
     * Also note that since this routine is slow anyway, we won't worry
     * about allocating a little bit of garbage.
     */
    private void invertGeneral(CyMatrix3d m1)
    {
        double result[] = new double[9];
        int row_perm[] = new int[3];
        int i, r, c;
        double[] tmp = new double[9];  // scratch matrix

        // Use LU decomposition and backsubstitution code specifically
        // for floating-point 3x3 matrices.

        // Copy source matrix to t1tmp
        tmp[0] = m1.m00;
        tmp[1] = m1.m01;
        tmp[2] = m1.m02;

        tmp[3] = m1.m10;
        tmp[4] = m1.m11;
        tmp[5] = m1.m12;

        tmp[6] = m1.m20;
        tmp[7] = m1.m21;
        tmp[8] = m1.m22;


        // Calculate LU decomposition: Is the matrix singular?
        if (!luDecomposition(tmp, row_perm))
        {
            // Matrix has no inverse
            throw new UnsupportedOperationException("Cannot invert matrix");
        }

        // Perform back substitution on the identity matrix
        for (i = 0; i < 9; i++)
        {
            result[i] = 0.0;
        }
        result[0] = 1.0;
        result[4] = 1.0;
        result[8] = 1.0;
        luBacksubstitution(tmp, row_perm, result);

        this.m00 = result[0];
        this.m01 = result[1];
        this.m02 = result[2];

        this.m10 = result[3];
        this.m11 = result[4];
        this.m12 = result[5];

        this.m20 = result[6];
        this.m21 = result[7];
        this.m22 = result[8];

    }

    /**
     * Given a 3x3 array "matrix0", this function replaces it with the
     * LU decomposition of a row-wise permutation of itself.  The input
     * parameters are "matrix0" and "dimen".  The array "matrix0" is also
     * an output parameter.  The vector "row_perm[3]" is an output
     * parameter that contains the row permutations resulting from partial
     * pivoting.  The output parameter "even_row_xchg" is 1 when the
     * number of row exchanges is even, or -1 otherwise.  Assumes data
     * type is always double.
     *
     * This function is similar to luDecomposition, except that it
     * is tuned specifically for 3x3 matrices.
     *
     * @return true if the matrix is nonsingular, or false otherwise.
     */
    //
    // Reference: Press, Flannery, Teukolsky, Vetterling,
    //	      _Numerical_Recipes_in_C_, Cambridge University Press,
    //	      1988, pp 40-45.
    //
    static boolean luDecomposition(double[] matrix0,
            int[] row_perm)
    {

        double row_scale[] = new double[3];
        // Determine implicit scaling information by looping over rows
        {
            int i, j;
            int ptr, rs;
            double big, temp;

            ptr = 0;
            rs = 0;

            // For each row ...
            i = 3;
            while (i-- != 0)
            {
                big = 0.0;

                // For each column, find the largest element in the row
                j = 3;
                while (j-- != 0)
                {
                    temp = matrix0[ptr++];
                    temp = Math.abs(temp);
                    if (temp > big)
                    {
                        big = temp;
                    }
                }

                // Is the matrix singular?
                if (big == 0.0)
                {
                    return false;
                }
                row_scale[rs++] = 1.0 / big;
            }
        }
        {
            int j;
            int mtx;

            mtx = 0;

            // For all columns, execute Crout's method
            for (j = 0; j < 3; j++)
            {
                int i, imax, k;
                int target, p1, p2;
                double sum, big, temp;

                // Determine elements of upper diagonal matrix U
                for (i = 0; i < j; i++)
                {
                    target = mtx + (3 * i) + j;
                    sum = matrix0[target];
                    k = i;
                    p1 = mtx + (3 * i);
                    p2 = mtx + j;
                    while (k-- != 0)
                    {
                        sum -= matrix0[p1] * matrix0[p2];
                        p1++;
                        p2 += 3;
                    }
                    matrix0[target] = sum;
                }

                // Search for largest pivot element and calculate
                // intermediate elements of lower diagonal matrix L.
                big = 0.0;
                imax = -1;
                for (i = j; i < 3; i++)
                {
                    target = mtx + (3 * i) + j;
                    sum = matrix0[target];
                    k = j;
                    p1 = mtx + (3 * i);
                    p2 = mtx + j;
                    while (k-- != 0)
                    {
                        sum -= matrix0[p1] * matrix0[p2];
                        p1++;
                        p2 += 3;
                    }
                    matrix0[target] = sum;

                    // Is this the best pivot so far?
                    if ((temp = row_scale[i] * Math.abs(sum)) >= big)
                    {
                        big = temp;
                        imax = i;
                    }
                }

                if (imax < 0)
                {
                    throw new UnsupportedOperationException("Cannot invert matrix");
                }

                // Is a row exchange necessary?
                if (j != imax)
                {
                    // Yes: exchange rows
                    k = 3;
                    p1 = mtx + (3 * imax);
                    p2 = mtx + (3 * j);
                    while (k-- != 0)
                    {
                        temp = matrix0[p1];
                        matrix0[p1++] = matrix0[p2];
                        matrix0[p2++] = temp;
                    }

                    // Record change in scale factor
                    row_scale[imax] = row_scale[j];
                }

                // Record row permutation
                row_perm[j] = imax;

                // Is the matrix singular
                if (matrix0[(mtx + (3 * j) + j)] == 0.0)
                {
                    return false;
                }

                // Divide elements of lower diagonal matrix L by pivot
                if (j != (3 - 1))
                {
                    temp = 1.0 / (matrix0[(mtx + (3 * j) + j)]);
                    target = mtx + (3 * (j + 1)) + j;
                    i = 2 - j;
                    while (i-- != 0)
                    {
                        matrix0[target] *= temp;
                        target += 3;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Solves a set of linear equations.  The input parameters "matrix1",
     * and "row_perm" come from luDecompostionD3x3 and do not change
     * here.  The parameter "matrix2" is a set of column vectors assembled
     * into a 3x3 matrix of floating-point values.  The procedure takes each
     * column of "matrix2" in turn and treats it as the right-hand side of the
     * matrix equation Ax = LUx = b.  The solution vector replaces the
     * original column of the matrix.
     *
     * If "matrix2" is the identity matrix, the procedure replaces its contents
     * with the inverse of the matrix from which "matrix1" was originally
     * derived.
     */
    //
    // Reference: Press, Flannery, Teukolsky, Vetterling,
    //	      _Numerical_Recipes_in_C_, Cambridge University Press,
    //	      1988, pp 44-45.
    //
    static void luBacksubstitution(double[] matrix1,
            int[] row_perm,
            double[] matrix2)
    {

        int i, ii, ip, j, k;
        int rp;
        int cv, rv;

        //	rp = row_perm;
        rp = 0;

        // For each column vector of matrix2 ...
        for (k = 0; k < 3; k++)
        {
            //	    cv = &(matrix2[0][k]);
            cv = k;
            ii = -1;

            // Forward substitution
            for (i = 0; i < 3; i++)
            {
                double sum;

                ip = row_perm[rp + i];
                sum = matrix2[cv + 3 * ip];
                matrix2[cv + 3 * ip] = matrix2[cv + 3 * i];
                if (ii >= 0)
                {
                    //		    rv = &(matrix1[i][0]);
                    rv = i * 3;
                    for (j = ii; j <= i - 1; j++)
                    {
                        sum -= matrix1[rv + j] * matrix2[cv + 3 * j];
                    }
                } else
                {
                    if (sum != 0.0)
                    {
                        ii = i;
                    }
                }
                matrix2[cv + 3 * i] = sum;
            }

            // Backsubstitution
            //	    rv = &(matrix1[3][0]);
            rv = 2 * 3;
            matrix2[cv + 3 * 2] /= matrix1[rv + 2];

            rv -= 3;
            matrix2[cv + 3 * 1] = (matrix2[cv + 3 * 1]
                    - matrix1[rv + 2] * matrix2[cv + 3 * 2]) / matrix1[rv + 1];

            rv -= 3;
            matrix2[cv + 4 * 0] = (matrix2[cv + 3 * 0]
                    - matrix1[rv + 1] * matrix2[cv + 3 * 1]
                    - matrix1[rv + 2] * matrix2[cv + 3 * 2]) / matrix1[rv + 0];

        }
    }

    /**
     * Computes the determinant of this matrix.
     * @return the determinant of the matrix
     */
    public final double determinant()
    {
        double total;

        total = this.m00 * (this.m11 * this.m22 - this.m12 * this.m21)
                + this.m01 * (this.m12 * this.m20 - this.m10 * this.m22)
                + this.m02 * (this.m10 * this.m21 - this.m11 * this.m20);
        return total;
    }

    /**
     * Multiplies each element of this matrix by a scalar.
     * @param scalar  The scalar multiplier.
     */
    public final void mul(double scalar)
    {
        m00 *= scalar;
        m01 *= scalar;
        m02 *= scalar;

        m10 *= scalar;
        m11 *= scalar;
        m12 *= scalar;

        m20 *= scalar;
        m21 *= scalar;
        m22 *= scalar;

    }

    /**
     * Sets the value of this matrix to the result of multiplying
     * the two argument matrices together.
     * @param m1 the first matrix
     * @param m2 the second matrix
     */
    public final void mul(CyMatrix3d m1, CyMatrix3d m2)
    {
        if (this != m1 && this != m2)
        {
            this.m00 = m1.m00 * m2.m00 + m1.m01 * m2.m10 + m1.m02 * m2.m20;
            this.m01 = m1.m00 * m2.m01 + m1.m01 * m2.m11 + m1.m02 * m2.m21;
            this.m02 = m1.m00 * m2.m02 + m1.m01 * m2.m12 + m1.m02 * m2.m22;

            this.m10 = m1.m10 * m2.m00 + m1.m11 * m2.m10 + m1.m12 * m2.m20;
            this.m11 = m1.m10 * m2.m01 + m1.m11 * m2.m11 + m1.m12 * m2.m21;
            this.m12 = m1.m10 * m2.m02 + m1.m11 * m2.m12 + m1.m12 * m2.m22;

            this.m20 = m1.m20 * m2.m00 + m1.m21 * m2.m10 + m1.m22 * m2.m20;
            this.m21 = m1.m20 * m2.m01 + m1.m21 * m2.m11 + m1.m22 * m2.m21;
            this.m22 = m1.m20 * m2.m02 + m1.m21 * m2.m12 + m1.m22 * m2.m22;
        } else
        {
            double m00, m01, m02,
                    m10, m11, m12,
                    m20, m21, m22;  // vars for temp result matrix

            m00 = m1.m00 * m2.m00 + m1.m01 * m2.m10 + m1.m02 * m2.m20;
            m01 = m1.m00 * m2.m01 + m1.m01 * m2.m11 + m1.m02 * m2.m21;
            m02 = m1.m00 * m2.m02 + m1.m01 * m2.m12 + m1.m02 * m2.m22;

            m10 = m1.m10 * m2.m00 + m1.m11 * m2.m10 + m1.m12 * m2.m20;
            m11 = m1.m10 * m2.m01 + m1.m11 * m2.m11 + m1.m12 * m2.m21;
            m12 = m1.m10 * m2.m02 + m1.m11 * m2.m12 + m1.m12 * m2.m22;

            m20 = m1.m20 * m2.m00 + m1.m21 * m2.m10 + m1.m22 * m2.m20;
            m21 = m1.m20 * m2.m01 + m1.m21 * m2.m11 + m1.m22 * m2.m21;
            m22 = m1.m20 * m2.m02 + m1.m21 * m2.m12 + m1.m22 * m2.m22;

            this.m00 = m00;
            this.m01 = m01;
            this.m02 = m02;
            this.m10 = m10;
            this.m11 = m11;
            this.m12 = m12;
            this.m20 = m20;
            this.m21 = m21;
            this.m22 = m22;
        }
    }

    /**
     *  Multiplies the transpose of matrix m1 times the transpose of matrix
     *  m2, and places the result into this.
     *  @param m1  the matrix on the left hand side of the multiplication
     *  @param m2  the matrix on the right hand side of the multiplication
     */
    public final void mulTransposeBoth(CyMatrix3d m1, CyMatrix3d m2)
    {
        if (this != m1 && this != m2)
        {
            this.m00 = m1.m00 * m2.m00 + m1.m10 * m2.m01 + m1.m20 * m2.m02;
            this.m01 = m1.m00 * m2.m10 + m1.m10 * m2.m11 + m1.m20 * m2.m12;
            this.m02 = m1.m00 * m2.m20 + m1.m10 * m2.m21 + m1.m20 * m2.m22;

            this.m10 = m1.m01 * m2.m00 + m1.m11 * m2.m01 + m1.m21 * m2.m02;
            this.m11 = m1.m01 * m2.m10 + m1.m11 * m2.m11 + m1.m21 * m2.m12;
            this.m12 = m1.m01 * m2.m20 + m1.m11 * m2.m21 + m1.m21 * m2.m22;

            this.m20 = m1.m02 * m2.m00 + m1.m12 * m2.m01 + m1.m22 * m2.m02;
            this.m21 = m1.m02 * m2.m10 + m1.m12 * m2.m11 + m1.m22 * m2.m12;
            this.m22 = m1.m02 * m2.m20 + m1.m12 * m2.m21 + m1.m22 * m2.m22;
        } else
        {
            double m00, m01, m02,
                    m10, m11, m12,
                    m20, m21, m22;  // vars for temp result matrix

            m00 = m1.m00 * m2.m00 + m1.m10 * m2.m01 + m1.m20 * m2.m02;
            m01 = m1.m00 * m2.m10 + m1.m10 * m2.m11 + m1.m20 * m2.m12;
            m02 = m1.m00 * m2.m20 + m1.m10 * m2.m21 + m1.m20 * m2.m22;

            m10 = m1.m01 * m2.m00 + m1.m11 * m2.m01 + m1.m21 * m2.m02;
            m11 = m1.m01 * m2.m10 + m1.m11 * m2.m11 + m1.m21 * m2.m12;
            m12 = m1.m01 * m2.m20 + m1.m11 * m2.m21 + m1.m21 * m2.m22;

            m20 = m1.m02 * m2.m00 + m1.m12 * m2.m01 + m1.m22 * m2.m02;
            m21 = m1.m02 * m2.m10 + m1.m12 * m2.m11 + m1.m22 * m2.m12;
            m22 = m1.m02 * m2.m20 + m1.m12 * m2.m21 + m1.m22 * m2.m22;

            this.m00 = m00;
            this.m01 = m01;
            this.m02 = m02;
            this.m10 = m10;
            this.m11 = m11;
            this.m12 = m12;
            this.m20 = m20;
            this.m21 = m21;
            this.m22 = m22;
        }

    }

    /**
     *  Multiplies matrix m1 times the transpose of matrix m2, and
     *  places the result into this.
     *  @param m1  the matrix on the left hand side of the multiplication
     *  @param m2  the matrix on the right hand side of the multiplication
     */
    public final void mulTransposeRight(CyMatrix3d m1, CyMatrix3d m2)
    {
        if (this != m1 && this != m2)
        {
            this.m00 = m1.m00 * m2.m00 + m1.m01 * m2.m01 + m1.m02 * m2.m02;
            this.m01 = m1.m00 * m2.m10 + m1.m01 * m2.m11 + m1.m02 * m2.m12;
            this.m02 = m1.m00 * m2.m20 + m1.m01 * m2.m21 + m1.m02 * m2.m22;

            this.m10 = m1.m10 * m2.m00 + m1.m11 * m2.m01 + m1.m12 * m2.m02;
            this.m11 = m1.m10 * m2.m10 + m1.m11 * m2.m11 + m1.m12 * m2.m12;
            this.m12 = m1.m10 * m2.m20 + m1.m11 * m2.m21 + m1.m12 * m2.m22;

            this.m20 = m1.m20 * m2.m00 + m1.m21 * m2.m01 + m1.m22 * m2.m02;
            this.m21 = m1.m20 * m2.m10 + m1.m21 * m2.m11 + m1.m22 * m2.m12;
            this.m22 = m1.m20 * m2.m20 + m1.m21 * m2.m21 + m1.m22 * m2.m22;
        } else
        {
            double m00, m01, m02,
                    m10, m11, m12,
                    m20, m21, m22;  // vars for temp result matrix

            m00 = m1.m00 * m2.m00 + m1.m01 * m2.m01 + m1.m02 * m2.m02;
            m01 = m1.m00 * m2.m10 + m1.m01 * m2.m11 + m1.m02 * m2.m12;
            m02 = m1.m00 * m2.m20 + m1.m01 * m2.m21 + m1.m02 * m2.m22;

            m10 = m1.m10 * m2.m00 + m1.m11 * m2.m01 + m1.m12 * m2.m02;
            m11 = m1.m10 * m2.m10 + m1.m11 * m2.m11 + m1.m12 * m2.m12;
            m12 = m1.m10 * m2.m20 + m1.m11 * m2.m21 + m1.m12 * m2.m22;

            m20 = m1.m20 * m2.m00 + m1.m21 * m2.m01 + m1.m22 * m2.m02;
            m21 = m1.m20 * m2.m10 + m1.m21 * m2.m11 + m1.m22 * m2.m12;
            m22 = m1.m20 * m2.m20 + m1.m21 * m2.m21 + m1.m22 * m2.m22;

            this.m00 = m00;
            this.m01 = m01;
            this.m02 = m02;
            this.m10 = m10;
            this.m11 = m11;
            this.m12 = m12;
            this.m20 = m20;
            this.m21 = m21;
            this.m22 = m22;
        }
    }

    /**
     *  Multiplies the transpose of matrix m1 times matrix m2, and
     *  places the result into this.
     *  @param m1  the matrix on the left hand side of the multiplication
     *  @param m2  the matrix on the right hand side of the multiplication
     */
    public final void mulTransposeLeft(CyMatrix3d m1, CyMatrix3d m2)
    {
        if (this != m1 && this != m2)
        {
            this.m00 = m1.m00 * m2.m00 + m1.m10 * m2.m10 + m1.m20 * m2.m20;
            this.m01 = m1.m00 * m2.m01 + m1.m10 * m2.m11 + m1.m20 * m2.m21;
            this.m02 = m1.m00 * m2.m02 + m1.m10 * m2.m12 + m1.m20 * m2.m22;

            this.m10 = m1.m01 * m2.m00 + m1.m11 * m2.m10 + m1.m21 * m2.m20;
            this.m11 = m1.m01 * m2.m01 + m1.m11 * m2.m11 + m1.m21 * m2.m21;
            this.m12 = m1.m01 * m2.m02 + m1.m11 * m2.m12 + m1.m21 * m2.m22;

            this.m20 = m1.m02 * m2.m00 + m1.m12 * m2.m10 + m1.m22 * m2.m20;
            this.m21 = m1.m02 * m2.m01 + m1.m12 * m2.m11 + m1.m22 * m2.m21;
            this.m22 = m1.m02 * m2.m02 + m1.m12 * m2.m12 + m1.m22 * m2.m22;
        } else
        {
            double m00, m01, m02,
                    m10, m11, m12,
                    m20, m21, m22;  // vars for temp result matrix

            m00 = m1.m00 * m2.m00 + m1.m10 * m2.m10 + m1.m20 * m2.m20;
            m01 = m1.m00 * m2.m01 + m1.m10 * m2.m11 + m1.m20 * m2.m21;
            m02 = m1.m00 * m2.m02 + m1.m10 * m2.m12 + m1.m20 * m2.m22;

            m10 = m1.m01 * m2.m00 + m1.m11 * m2.m10 + m1.m21 * m2.m20;
            m11 = m1.m01 * m2.m01 + m1.m11 * m2.m11 + m1.m21 * m2.m21;
            m12 = m1.m01 * m2.m02 + m1.m11 * m2.m12 + m1.m21 * m2.m22;

            m20 = m1.m02 * m2.m00 + m1.m12 * m2.m10 + m1.m22 * m2.m20;
            m21 = m1.m02 * m2.m01 + m1.m12 * m2.m11 + m1.m22 * m2.m21;
            m22 = m1.m02 * m2.m02 + m1.m12 * m2.m12 + m1.m22 * m2.m22;

            this.m00 = m00;
            this.m01 = m01;
            this.m02 = m02;
            this.m10 = m10;
            this.m11 = m11;
            this.m12 = m12;
            this.m20 = m20;
            this.m21 = m21;
            this.m22 = m22;
        }
    }

    /**
     * Perform cross product normalization of this matrix.
     */
    public final void normalizeCP()
    {
        double mag = 1.0 / Math.sqrt(m00 * m00 + m10 * m10 + m20 * m20);
        m00 = m00 * mag;
        m10 = m10 * mag;
        m20 = m20 * mag;

        mag = 1.0 / Math.sqrt(m01 * m01 + m11 * m11 + m21 * m21);
        m01 = m01 * mag;
        m11 = m11 * mag;
        m21 = m21 * mag;

        m02 = m10 * m21 - m11 * m20;
        m12 = m01 * m20 - m00 * m21;
        m22 = m00 * m11 - m01 * m10;
    }

    /**
     * Perform cross product normalization of matrix m1 and place the
     * normalized values into this.
     * @param m1  Provides the matrix values to be normalized
     */
    public final void normalizeCP(CyMatrix3d m1)
    {
        double mag = 1.0 / Math.sqrt(m1.m00 * m1.m00 + m1.m10 * m1.m10 + m1.m20 * m1.m20);
        m00 = m1.m00 * mag;
        m10 = m1.m10 * mag;
        m20 = m1.m20 * mag;

        mag = 1.0 / Math.sqrt(m1.m01 * m1.m01 + m1.m11 * m1.m11 + m1.m21 * m1.m21);
        m01 = m1.m01 * mag;
        m11 = m1.m11 * mag;
        m21 = m1.m21 * mag;

        m02 = m10 * m21 - m11 * m20;
        m12 = m01 * m20 - m00 * m21;
        m22 = m00 * m11 - m01 * m10;
    }

    /**
     * Returns true if all of the data members of Matrix3d m1 are
     * equal to the corresponding data members in this Matrix3d.
     * @param m1  the matrix with which the comparison is made
     * @return  true or false
     */
    public boolean equals(CyMatrix3d m1)
    {
        try
        {
            return (this.m00 == m1.m00 && this.m01 == m1.m01 && this.m02 == m1.m02
                    && this.m10 == m1.m10 && this.m11 == m1.m11 && this.m12 == m1.m12
                    && this.m20 == m1.m20 && this.m21 == m1.m21 && this.m22 == m1.m22);
        } catch (NullPointerException e2)
        {
            return false;
        }

    }

    /**
     * Returns true if the Object t1 is of type Matrix3d and all of the
     * data members of t1 are equal to the corresponding data members in
     * this Matrix3d.
     * @param t1  the matrix with which the comparison is made
     * @return  true or false
     */
    public boolean equals(Object t1)
    {
        try
        {
            CyMatrix3d m2 = (CyMatrix3d) t1;
            return (this.m00 == m2.m00 && this.m01 == m2.m01 && this.m02 == m2.m02
                    && this.m10 == m2.m10 && this.m11 == m2.m11 && this.m12 == m2.m12
                    && this.m20 == m2.m20 && this.m21 == m2.m21 && this.m22 == m2.m22);
        } catch (ClassCastException e1)
        {
            return false;
        } catch (NullPointerException e2)
        {
            return false;
        }

    }

    /**
     * Returns true if the L-infinite distance between this matrix
     * and matrix m1 is less than or equal to the epsilon parameter,
     * otherwise returns false.  The L-infinite
     * distance is equal to
     * MAX[i=0,1,2 ; j=0,1,2 ; abs(this.m(i,j) - m1.m(i,j)]
     * @param m1  the matrix to be compared to this matrix
     * @param epsilon  the threshold value
     */
    public boolean epsilonEquals(CyMatrix3d m1, double epsilon)
    {
        double diff;

        diff = m00 - m1.m00;
        if ((diff < 0 ? -diff : diff) > epsilon)
        {
            return false;
        }

        diff = m01 - m1.m01;
        if ((diff < 0 ? -diff : diff) > epsilon)
        {
            return false;
        }

        diff = m02 - m1.m02;
        if ((diff < 0 ? -diff : diff) > epsilon)
        {
            return false;
        }

        diff = m10 - m1.m10;
        if ((diff < 0 ? -diff : diff) > epsilon)
        {
            return false;
        }

        diff = m11 - m1.m11;
        if ((diff < 0 ? -diff : diff) > epsilon)
        {
            return false;
        }

        diff = m12 - m1.m12;
        if ((diff < 0 ? -diff : diff) > epsilon)
        {
            return false;
        }

        diff = m20 - m1.m20;
        if ((diff < 0 ? -diff : diff) > epsilon)
        {
            return false;
        }

        diff = m21 - m1.m21;
        if ((diff < 0 ? -diff : diff) > epsilon)
        {
            return false;
        }

        diff = m22 - m1.m22;
        if ((diff < 0 ? -diff : diff) > epsilon)
        {
            return false;
        }

        return true;
    }

    /**
     * Returns a hash code value based on the data values in this
     * object.  Two different Matrix3d objects with identical data values
     * (i.e., Matrix3d.equals returns true) will return the same hash
     * code value.  Two objects with different data members may return the
     * same hash value, although this is not likely.
     * @return the integer hash code value
     */
    public int hashCode()
    {
        long bits = 1L;
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m00);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m01);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m02);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m10);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m11);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m12);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m20);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m21);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m22);
        return (int) (bits ^ (bits >> 32));
    }

    /**
     *  Sets this matrix to all zeros.
     */
    public final void setZero()
    {
        m00 = 0.0;
        m01 = 0.0;
        m02 = 0.0;

        m10 = 0.0;
        m11 = 0.0;
        m12 = 0.0;

        m20 = 0.0;
        m21 = 0.0;
        m22 = 0.0;

    }

    /**
     * Negates the value of this matrix: this = -this.
     */
    public final void negate()
    {
        this.m00 = -this.m00;
        this.m01 = -this.m01;
        this.m02 = -this.m02;

        this.m10 = -this.m10;
        this.m11 = -this.m11;
        this.m12 = -this.m12;

        this.m20 = -this.m20;
        this.m21 = -this.m21;
        this.m22 = -this.m22;

    }

    /**
     *  Sets the value of this matrix equal to the negation of
     *  of the Matrix3d parameter.
     *  @param m1  the source matrix
     */
    public final void negate(CyMatrix3d m1)
    {
        this.m00 = -m1.m00;
        this.m01 = -m1.m01;
        this.m02 = -m1.m02;

        this.m10 = -m1.m10;
        this.m11 = -m1.m11;
        this.m12 = -m1.m12;

        this.m20 = -m1.m20;
        this.m21 = -m1.m21;
        this.m22 = -m1.m22;

    }

    /**
     * Multiply this matrix by the tuple t and place the result
     * back into the tuple (t = this*t).
     * @param t  the tuple to be multiplied by this matrix and then replaced
     */
    public final void transform(CyVector3d t)
    {
        double x, y, z;
        x = m00 * t.x + m01 * t.y + m02 * t.z;
        y = m10 * t.x + m11 * t.y + m12 * t.z;
        z = m20 * t.x + m21 * t.y + m22 * t.z;
        t.set(x, y, z);
    }

    /**
     * Multiply this matrix by the tuple t and and place the result
     * into the tuple "result" (result = this*t).
     * @param t  the tuple to be multiplied by this matrix
     * @param result  the tuple into which the product is placed
     */
    public final void transform(CyVector3d t, CyVector3d result)
    {
        double x, y, z;
        x = m00 * t.x + m01 * t.y + m02 * t.z;
        y = m10 * t.x + m11 * t.y + m12 * t.z;
        result.z = m20 * t.x + m21 * t.y + m22 * t.z;
        result.x = x;
        result.y = y;
    }

    /**
     * Do a transformation, treating input vector as a point in 2-space.
     *
     * @param v
     * @param normalize If vector should be normalized after calculation.
     * @return
     */
    public CyVector3d transform2d(CyVector3d v, boolean normalize)
    {
        transform(v);
        if (normalize)
        {
            double invZ = 1 / v.z;
            v.x *= invZ;
            v.y *= invZ;
            v.z = 1;
        }
        return v;
    }

    public CyVector2d transformPoint(CyVector2d p, boolean normalize)
    {
        double x, y, z;
        x = m00 * p.x + m01 * p.y + m02;
        y = m10 * p.x + m11 * p.y + m12;
        if (normalize)
        {
            z = m20 * p.x + m21 * p.y + m22;
            double invZ = 1 / z;
            x *= invZ;
            y *= invZ;
        }
        p.x = x;
        p.y = y;

        return p;
    }

    public CyVector2d transformVector(CyVector2d v, boolean normalize)
    {
        double x, y, z;
        x = m00 * v.x + m01 * v.y;
        y = m10 * v.x + m11 * v.y;
        if (normalize)
        {
            z = m20 * v.x + m21 * v.y;
            double invZ = 1 / z;
            x *= invZ;
            y *= invZ;
        }
        v.x = x;
        v.y = y;

        return v;
    }
    
    /**
     * Extracts the rotation of this matrix as a quaternion.
     * 
     * http://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToQuaternion/index.htm
     * 
     * @param q Quaternion to set to rotation.  If null, one
     * will be allocated.
     * @return Quaternion representing rotation of this matrix
     */
    public CyVector4d getQuaternion(CyVector4d q)
    {
        if (q == null)
        {
            q = new CyVector4d();
        }
        
        double tr = m00 + m11 + m22;

        if (tr > 0)
        {
            double s = Math.sqrt(tr + 1) * 2; // S=4*qw 
            q.w = 0.25f * s;
            q.x = (m21 - m12) / s;
            q.y = (m02 - m20) / s;
            q.z = (m10 - m01) / s;
        }
        else if ((m00 > m11) & (m00 > m22))
        {
            double s = (double)(Math.sqrt(1.0 + m00 - m11 - m22) * 2); // S=4*qx 
            q.w = (m21 - m12) / s;
            q.x = 0.25f * s;
            q.y = (m01 + m10) / s;
            q.z = (m02 + m20) / s;
        }
        else if (m11 > m22)
        {
            double s = (double)(Math.sqrt(1.0 + m11 - m00 - m22) * 2); // S=4*qy
            q.w = (m02 - m20) / s;
            q.x = (m01 + m10) / s;
            q.y = 0.25f * s;
            q.z = (m12 + m21) / s;
        } else
        {
            double s = (double)(Math.sqrt(1.0 + m22 - m00 - m11) * 2); // S=4*qz
            q.w = (m10 - m01) / s;
            q.x = (m02 + m20) / s;
            q.y = (m12 + m21) / s;
            q.z = 0.25f * s;
        }
        
        return q;
    }

}
