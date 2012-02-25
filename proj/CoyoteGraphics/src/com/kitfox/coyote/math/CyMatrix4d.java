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

import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.coyote.shape.CyRectangle2i;
import java.awt.geom.AffineTransform;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

/**
 * This is a cleaned up version of Matrix4d from vecmath, with some extra
 * features added.
 * @author kitfox
 */
public class CyMatrix4d
{
    public double m00;
    public double m10;
    public double m20;
    public double m30;
    public double m01;
    public double m11;
    public double m21;
    public double m31;
    public double m02;
    public double m12;
    public double m22;
    public double m32;
    public double m03;
    public double m13;
    public double m23;
    public double m33;
    private static final double EPS = 1.0E-10;

    public CyMatrix4d()
    {
    }

    public CyMatrix4d(CyMatrix4d m)
    {
        this(
                m.m00, m.m10, m.m20, m.m30,
                m.m01, m.m11, m.m21, m.m31,
                m.m02, m.m12, m.m22, m.m32,
                m.m03, m.m13, m.m23, m.m33
                );
    }

    public CyMatrix4d(double m00, double m10, double m20, double m30,
            double m01, double m11, double m21, double m31,
            double m02, double m12, double m22, double m32,
            double m03, double m13, double m23, double m33)
    {
        this.m00 = m00;
        this.m10 = m10;
        this.m20 = m20;
        this.m30 = m30;
        this.m01 = m01;
        this.m11 = m11;
        this.m21 = m21;
        this.m31 = m31;
        this.m02 = m02;
        this.m12 = m12;
        this.m22 = m22;
        this.m32 = m32;
        this.m03 = m03;
        this.m13 = m13;
        this.m23 = m23;
        this.m33 = m33;
    }

    public CyMatrix4d(double[] colMajor)
    {
        this(
                colMajor[0], colMajor[1], colMajor[2], colMajor[3],
                colMajor[4], colMajor[5], colMajor[6], colMajor[7],
                colMajor[8], colMajor[9], colMajor[10], colMajor[11],
                colMajor[12], colMajor[13], colMajor[14], colMajor[15]);
    }

    public static CyMatrix4d createIdentity()
    {
        return new CyMatrix4d(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1);
    }

    public CyMatrix4d(AffineTransform m)
    {
        this(m.getScaleX(), m.getShearY(), 0, 0,
                m.getShearX(), m.getScaleY(), 0, 0,
                0, 0, 1, 0,
                m.getTranslateX(), m.getTranslateY(), 0, 1);
    }

    public void set(double m00, double m10, double m20, double m30,
            double m01, double m11, double m21, double m31,
            double m02, double m12, double m22, double m32,
            double m03, double m13, double m23, double m33)
    {
        this.m00 = m00;
        this.m10 = m10;
        this.m20 = m20;
        this.m30 = m30;
        this.m01 = m01;
        this.m11 = m11;
        this.m21 = m21;
        this.m31 = m31;
        this.m02 = m02;
        this.m12 = m12;
        this.m22 = m22;
        this.m32 = m32;
        this.m03 = m03;
        this.m13 = m13;
        this.m23 = m23;
        this.m33 = m33;
    }

    public void set(CyMatrix4d m)
    {
        this.m00 = m.m00;
        this.m10 = m.m10;
        this.m20 = m.m20;
        this.m30 = m.m30;
        this.m01 = m.m01;
        this.m11 = m.m11;
        this.m21 = m.m21;
        this.m31 = m.m31;
        this.m02 = m.m02;
        this.m12 = m.m12;
        this.m22 = m.m22;
        this.m32 = m.m32;
        this.m03 = m.m03;
        this.m13 = m.m13;
        this.m23 = m.m23;
        this.m33 = m.m33;
    }

    public void setToQuaternion(CyVector4d q)
    {
        m00 = (1 - 2 * q.y * q.y - 2 * q.z * q.z);
        m10 = (2 * (q.x * q.y + q.w * q.z));
        m20 = (2 * (q.x * q.z - q.w * q.y));

        m01 = (2 * (q.x * q.y - q.w * q.z));
        m11 = (1 - 2 * q.x * q.x - 2 * q.z * q.z);
        m21 = (2 * (q.y * q.z + q.w * q.x));

        m02 = (2 * (q.x * q.z + q.w * q.y));
        m12 = (2 * (q.y * q.z - q.w * q.x));
        m22 = (1 - 2 * q.x * q.x - 2 * q.y * q.y);

        m03 = 0;
        m13 = 0;
        m23 = 0;

        m30 = 0;
        m31 = 0;
        m32 = 0;
        m33 = 1;
    }

    /**
     * Sets matrix based on axis angle
     * @param a Axis angle.  a.xyz represents vector to rotate about and
     * a.w represents angle in radians.
     */
    public void setToAxisAngle(CyVector4d a)
    {
        double mag = Math.sqrt(a.x * a.x + a.y * a.y + a.z * a.z);

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
            double ax = a.x * mag;
            double ay = a.y * mag;
            double az = a.z * mag;

            double sinTheta = Math.sin(a.w);
            double cosTheta = Math.cos(a.w);
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

        m03 = 0;
        m13 = 0;
        m23 = 0;

        m30 = 0;
        m31 = 0;
        m32 = 0;
        m33 = 1;
    }

    /**
     * Write to buffer in column major format.
     * @param buf
     * @return
     */
    public DoubleBuffer toBufferc(DoubleBuffer buf)
    {
        if (buf == null)
        {
            buf = BufferUtil.allocateDouble(16);
        }
        buf.put(m00);
        buf.put(m10);
        buf.put(m20);
        buf.put(m30);
        buf.put(m01);
        buf.put(m11);
        buf.put(m21);
        buf.put(m31);
        buf.put(m02);
        buf.put(m12);
        buf.put(m22);
        buf.put(m32);
        buf.put(m03);
        buf.put(m13);
        buf.put(m23);
        buf.put(m33);
        buf.rewind();
        return buf;
    }

    public DoubleBuffer toBufferr(DoubleBuffer buf)
    {
        if (buf == null)
        {
            buf = BufferUtil.allocateDouble(16);
        }
        buf.put(m00);
        buf.put(m01);
        buf.put(m02);
        buf.put(m03);
        buf.put(m10);
        buf.put(m11);
        buf.put(m12);
        buf.put(m13);
        buf.put(m20);
        buf.put(m21);
        buf.put(m22);
        buf.put(m23);
        buf.put(m30);
        buf.put(m31);
        buf.put(m32);
        buf.put(m33);
        buf.rewind();
        return buf;
    }

    /**
     * Write to buffer in column major format.
     * @param buf
     * @return
     */
    public FloatBuffer toBufferc(FloatBuffer buf)
    {
        if (buf == null)
        {
            buf = BufferUtil.allocateFloat(16);
        }
        buf.put((float)m00);
        buf.put((float)m10);
        buf.put((float)m20);
        buf.put((float)m30);
        buf.put((float)m01);
        buf.put((float)m11);
        buf.put((float)m21);
        buf.put((float)m31);
        buf.put((float)m02);
        buf.put((float)m12);
        buf.put((float)m22);
        buf.put((float)m32);
        buf.put((float)m03);
        buf.put((float)m13);
        buf.put((float)m23);
        buf.put((float)m33);
        buf.rewind();
        return buf;
    }

    public FloatBuffer toBufferr(FloatBuffer buf)
    {
        if (buf == null)
        {
            buf = BufferUtil.allocateFloat(16);
        }
        buf.put((float)m00);
        buf.put((float)m01);
        buf.put((float)m02);
        buf.put((float)m03);
        buf.put((float)m10);
        buf.put((float)m11);
        buf.put((float)m12);
        buf.put((float)m13);
        buf.put((float)m20);
        buf.put((float)m21);
        buf.put((float)m22);
        buf.put((float)m23);
        buf.put((float)m30);
        buf.put((float)m31);
        buf.put((float)m32);
        buf.put((float)m33);
        buf.rewind();
        return buf;
    }

    public void add(CyMatrix4d m)
    {
        m00 += m.m00;
        m10 += m.m10;
        m20 += m.m20;
        m30 += m.m30;
        m01 += m.m01;
        m11 += m.m11;
        m21 += m.m21;
        m31 += m.m31;
        m02 += m.m02;
        m12 += m.m12;
        m22 += m.m22;
        m32 += m.m32;
        m03 += m.m03;
        m13 += m.m13;
        m23 += m.m23;
        m33 += m.m33;
    }

    public void sub(CyMatrix4d m)
    {
        m00 -= m.m00;
        m10 -= m.m10;
        m20 -= m.m20;
        m30 -= m.m30;
        m01 -= m.m01;
        m11 -= m.m11;
        m21 -= m.m21;
        m31 -= m.m31;
        m02 -= m.m02;
        m12 -= m.m12;
        m22 -= m.m22;
        m32 -= m.m32;
        m03 -= m.m03;
        m13 -= m.m13;
        m23 -= m.m23;
        m33 -= m.m33;
    }

    public void transpose()
    {
        double tmp;

        tmp = m10;
        m10 = m01;
        m01 = tmp;

        tmp = m20;
        m20 = m02;
        m02 = tmp;

        tmp = m30;
        m30 = m03;
        m03 = tmp;

        tmp = m21;
        m21 = m12;
        m12 = tmp;

        tmp = m31;
        m31 = m13;
        m13 = tmp;

        tmp = m32;
        m32 = m23;
        m23 = tmp;
    }

    public void invert()
    {
        invertGeneral(this);
    }

    /**
     * Sets the value of this matrix to the matrix inverse
     * of the passed matrix m1.
     * @param m1 the matrix to be inverted
     */
    public final void invert(CyMatrix4d m1)
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
    final void invertGeneral(CyMatrix4d m1)
    {
        double result[] = new double[16];
        int row_perm[] = new int[4];
        int i, r, c;

        // Use LU decomposition and backsubstitution code specifically
        // for floating-point 4x4 matrices.
        double[] tmp = new double[16];  // scratch matrix
        // Copy source matrix to t1tmp
        tmp[0] = m1.m00;
        tmp[1] = m1.m01;
        tmp[2] = m1.m02;
        tmp[3] = m1.m03;

        tmp[4] = m1.m10;
        tmp[5] = m1.m11;
        tmp[6] = m1.m12;
        tmp[7] = m1.m13;

        tmp[8] = m1.m20;
        tmp[9] = m1.m21;
        tmp[10] = m1.m22;
        tmp[11] = m1.m23;

        tmp[12] = m1.m30;
        tmp[13] = m1.m31;
        tmp[14] = m1.m32;
        tmp[15] = m1.m33;

        // Calculate LU decomposition: Is the matrix singular?
        if (!luDecomposition(tmp, row_perm))
        {
            // Matrix has no inverse
            throw new UnsupportedOperationException("Matrix not invertable");
        }

        // Perform back substitution on the identity matrix
        for (i = 0; i < 16; i++)
        {
            result[i] = 0.0;
        }
        result[0] = 1.0;
        result[5] = 1.0;
        result[10] = 1.0;
        result[15] = 1.0;
        luBacksubstitution(tmp, row_perm, result);

        this.m00 = result[0];
        this.m01 = result[1];
        this.m02 = result[2];
        this.m03 = result[3];

        this.m10 = result[4];
        this.m11 = result[5];
        this.m12 = result[6];
        this.m13 = result[7];

        this.m20 = result[8];
        this.m21 = result[9];
        this.m22 = result[10];
        this.m23 = result[11];

        this.m30 = result[12];
        this.m31 = result[13];
        this.m32 = result[14];
        this.m33 = result[15];

    }

    /**
     * Given a 4x4 array "matrix0", this function replaces it with the
     * LU decomposition of a row-wise permutation of itself.  The input
     * parameters are "matrix0" and "dimen".  The array "matrix0" is also
     * an output parameter.  The vector "row_perm[4]" is an output
     * parameter that contains the row permutations resulting from partial
     * pivoting.  The output parameter "even_row_xchg" is 1 when the
     * number of row exchanges is even, or -1 otherwise.  Assumes data
     * type is always double.
     *
     * This function is similar to luDecomposition, except that it
     * is tuned specifically for 4x4 matrices.
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

        double row_scale[] = new double[4];
        // Determine implicit scaling information by looping over rows
        {
            int i, j;
            int ptr, rs;
            double big, temp;

            ptr = 0;
            rs = 0;

            // For each row ...
            i = 4;
            while (i-- != 0)
            {
                big = 0.0;

                // For each column, find the largest element in the row
                j = 4;
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
            for (j = 0; j < 4; j++)
            {
                int i, imax, k;
                int target, p1, p2;
                double sum, big, temp;

                // Determine elements of upper diagonal matrix U
                for (i = 0; i < j; i++)
                {
                    target = mtx + (4 * i) + j;
                    sum = matrix0[target];
                    k = i;
                    p1 = mtx + (4 * i);
                    p2 = mtx + j;
                    while (k-- != 0)
                    {
                        sum -= matrix0[p1] * matrix0[p2];
                        p1++;
                        p2 += 4;
                    }
                    matrix0[target] = sum;
                }

                // Search for largest pivot element and calculate
                // intermediate elements of lower diagonal matrix L.
                big = 0.0;
                imax = -1;
                for (i = j; i < 4; i++)
                {
                    target = mtx + (4 * i) + j;
                    sum = matrix0[target];
                    k = j;
                    p1 = mtx + (4 * i);
                    p2 = mtx + j;
                    while (k-- != 0)
                    {
                        sum -= matrix0[p1] * matrix0[p2];
                        p1++;
                        p2 += 4;
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
                    throw new UnsupportedOperationException("Matrix not invertable");
                }

                // Is a row exchange necessary?
                if (j != imax)
                {
                    // Yes: exchange rows
                    k = 4;
                    p1 = mtx + (4 * imax);
                    p2 = mtx + (4 * j);
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
                if (matrix0[(mtx + (4 * j) + j)] == 0.0)
                {
                    return false;
                }

                // Divide elements of lower diagonal matrix L by pivot
                if (j != (4 - 1))
                {
                    temp = 1.0 / (matrix0[(mtx + (4 * j) + j)]);
                    target = mtx + (4 * (j + 1)) + j;
                    i = 3 - j;
                    while (i-- != 0)
                    {
                        matrix0[target] *= temp;
                        target += 4;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Solves a set of linear equations.  The input parameters "matrix1",
     * and "row_perm" come from luDecompostionD4x4 and do not change
     * here.  The parameter "matrix2" is a set of column vectors assembled
     * into a 4x4 matrix of floating-point values.  The procedure takes each
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
        for (k = 0; k < 4; k++)
        {
            //	    cv = &(matrix2[0][k]);
            cv = k;
            ii = -1;

            // Forward substitution
            for (i = 0; i < 4; i++)
            {
                double sum;

                ip = row_perm[rp + i];
                sum = matrix2[cv + 4 * ip];
                matrix2[cv + 4 * ip] = matrix2[cv + 4 * i];
                if (ii >= 0)
                {
                    //		    rv = &(matrix1[i][0]);
                    rv = i * 4;
                    for (j = ii; j <= i - 1; j++)
                    {
                        sum -= matrix1[rv + j] * matrix2[cv + 4 * j];
                    }
                } else
                {
                    if (sum != 0.0)
                    {
                        ii = i;
                    }
                }
                matrix2[cv + 4 * i] = sum;
            }

            // Backsubstitution
            //	    rv = &(matrix1[3][0]);
            rv = 3 * 4;
            matrix2[cv + 4 * 3] /= matrix1[rv + 3];

            rv -= 4;
            matrix2[cv + 4 * 2] = (matrix2[cv + 4 * 2]
                    - matrix1[rv + 3] * matrix2[cv + 4 * 3]) / matrix1[rv + 2];

            rv -= 4;
            matrix2[cv + 4 * 1] = (matrix2[cv + 4 * 1]
                    - matrix1[rv + 2] * matrix2[cv + 4 * 2]
                    - matrix1[rv + 3] * matrix2[cv + 4 * 3]) / matrix1[rv + 1];

            rv -= 4;
            matrix2[cv + 4 * 0] = (matrix2[cv + 4 * 0]
                    - matrix1[rv + 1] * matrix2[cv + 4 * 1]
                    - matrix1[rv + 2] * matrix2[cv + 4 * 2]
                    - matrix1[rv + 3] * matrix2[cv + 4 * 3]) / matrix1[rv + 0];
        }
    }

    /**
     * Computes the determinant of this matrix.
     * @return the determinant of the matrix
     */
    public final double determinant()
    {
        double det;

        // cofactor exapainsion along first row

        det = m00 * (m11 * m22 * m33 + m12 * m23 * m31 + m13 * m21 * m32
                - m13 * m22 * m31 - m11 * m23 * m32 - m12 * m21 * m33);
        det -= m01 * (m10 * m22 * m33 + m12 * m23 * m30 + m13 * m20 * m32
                - m13 * m22 * m30 - m10 * m23 * m32 - m12 * m20 * m33);
        det += m02 * (m10 * m21 * m33 + m11 * m23 * m30 + m13 * m20 * m31
                - m13 * m21 * m30 - m10 * m23 * m31 - m11 * m20 * m33);
        det -= m03 * (m10 * m21 * m32 + m11 * m22 * m30 + m12 * m20 * m31
                - m12 * m21 * m30 - m10 * m22 * m31 - m11 * m20 * m32);

        return (det);
    }

    /**
     * Multiplies each element of this matrix by a scalar.
     * @param scalar  the scalar multiplier.
     */
    public final void mul(double scalar)
    {
        m00 *= scalar;
        m01 *= scalar;
        m02 *= scalar;
        m03 *= scalar;
        m10 *= scalar;
        m11 *= scalar;
        m12 *= scalar;
        m13 *= scalar;
        m20 *= scalar;
        m21 *= scalar;
        m22 *= scalar;
        m23 *= scalar;
        m30 *= scalar;
        m31 *= scalar;
        m32 *= scalar;
        m33 *= scalar;
    }

    /**
     * Multiplies each component by the coresponding component in passed
     * matrix.  Not the regular matrix multiplication.
     */
    public final void mulComponents(CyMatrix4d m)
    {
        this.m00 = m.m00;
        this.m01 = m.m01;
        this.m02 = m.m02;
        this.m03 = m.m03;
        this.m10 = m.m10;
        this.m11 = m.m11;
        this.m12 = m.m12;
        this.m13 = m.m13;
        this.m20 = m.m20;
        this.m21 = m.m21;
        this.m22 = m.m22;
        this.m23 = m.m23;
        this.m30 = m.m30;
        this.m31 = m.m31;
        this.m32 = m.m32;
        this.m33 = m.m33;
    }

    /**
     * Sets the value of this matrix to the result of multiplying itself
     * with matrix m1.
     * @param m1 the other matrix
     */
    public final void mul(CyMatrix4d m1)
    {
        double m00, m01, m02, m03,
                m10, m11, m12, m13,
                m20, m21, m22, m23,
                m30, m31, m32, m33;  // vars for temp result matrix

        m00 = this.m00 * m1.m00 + this.m01 * m1.m10
                + this.m02 * m1.m20 + this.m03 * m1.m30;
        m01 = this.m00 * m1.m01 + this.m01 * m1.m11
                + this.m02 * m1.m21 + this.m03 * m1.m31;
        m02 = this.m00 * m1.m02 + this.m01 * m1.m12
                + this.m02 * m1.m22 + this.m03 * m1.m32;
        m03 = this.m00 * m1.m03 + this.m01 * m1.m13
                + this.m02 * m1.m23 + this.m03 * m1.m33;

        m10 = this.m10 * m1.m00 + this.m11 * m1.m10
                + this.m12 * m1.m20 + this.m13 * m1.m30;
        m11 = this.m10 * m1.m01 + this.m11 * m1.m11
                + this.m12 * m1.m21 + this.m13 * m1.m31;
        m12 = this.m10 * m1.m02 + this.m11 * m1.m12
                + this.m12 * m1.m22 + this.m13 * m1.m32;
        m13 = this.m10 * m1.m03 + this.m11 * m1.m13
                + this.m12 * m1.m23 + this.m13 * m1.m33;

        m20 = this.m20 * m1.m00 + this.m21 * m1.m10
                + this.m22 * m1.m20 + this.m23 * m1.m30;
        m21 = this.m20 * m1.m01 + this.m21 * m1.m11
                + this.m22 * m1.m21 + this.m23 * m1.m31;
        m22 = this.m20 * m1.m02 + this.m21 * m1.m12
                + this.m22 * m1.m22 + this.m23 * m1.m32;
        m23 = this.m20 * m1.m03 + this.m21 * m1.m13
                + this.m22 * m1.m23 + this.m23 * m1.m33;

        m30 = this.m30 * m1.m00 + this.m31 * m1.m10
                + this.m32 * m1.m20 + this.m33 * m1.m30;
        m31 = this.m30 * m1.m01 + this.m31 * m1.m11
                + this.m32 * m1.m21 + this.m33 * m1.m31;
        m32 = this.m30 * m1.m02 + this.m31 * m1.m12
                + this.m32 * m1.m22 + this.m33 * m1.m32;
        m33 = this.m30 * m1.m03 + this.m31 * m1.m13
                + this.m32 * m1.m23 + this.m33 * m1.m33;

        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = m03;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m30 = m30;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
    }

    /**
     * Sets the value of this matrix to the result of multiplying
     * the two argument matrices together.
     * @param m1 the first matrix
     * @param m2 the second matrix
     */
    public final void mul(CyMatrix4d m1, CyMatrix4d m2)
    {
        if (this != m1 && this != m2)
        {
            // code for mat mul
            this.m00 = m1.m00 * m2.m00 + m1.m01 * m2.m10
                    + m1.m02 * m2.m20 + m1.m03 * m2.m30;
            this.m01 = m1.m00 * m2.m01 + m1.m01 * m2.m11
                    + m1.m02 * m2.m21 + m1.m03 * m2.m31;
            this.m02 = m1.m00 * m2.m02 + m1.m01 * m2.m12
                    + m1.m02 * m2.m22 + m1.m03 * m2.m32;
            this.m03 = m1.m00 * m2.m03 + m1.m01 * m2.m13
                    + m1.m02 * m2.m23 + m1.m03 * m2.m33;

            this.m10 = m1.m10 * m2.m00 + m1.m11 * m2.m10
                    + m1.m12 * m2.m20 + m1.m13 * m2.m30;
            this.m11 = m1.m10 * m2.m01 + m1.m11 * m2.m11
                    + m1.m12 * m2.m21 + m1.m13 * m2.m31;
            this.m12 = m1.m10 * m2.m02 + m1.m11 * m2.m12
                    + m1.m12 * m2.m22 + m1.m13 * m2.m32;
            this.m13 = m1.m10 * m2.m03 + m1.m11 * m2.m13
                    + m1.m12 * m2.m23 + m1.m13 * m2.m33;

            this.m20 = m1.m20 * m2.m00 + m1.m21 * m2.m10
                    + m1.m22 * m2.m20 + m1.m23 * m2.m30;
            this.m21 = m1.m20 * m2.m01 + m1.m21 * m2.m11
                    + m1.m22 * m2.m21 + m1.m23 * m2.m31;
            this.m22 = m1.m20 * m2.m02 + m1.m21 * m2.m12
                    + m1.m22 * m2.m22 + m1.m23 * m2.m32;
            this.m23 = m1.m20 * m2.m03 + m1.m21 * m2.m13
                    + m1.m22 * m2.m23 + m1.m23 * m2.m33;

            this.m30 = m1.m30 * m2.m00 + m1.m31 * m2.m10
                    + m1.m32 * m2.m20 + m1.m33 * m2.m30;
            this.m31 = m1.m30 * m2.m01 + m1.m31 * m2.m11
                    + m1.m32 * m2.m21 + m1.m33 * m2.m31;
            this.m32 = m1.m30 * m2.m02 + m1.m31 * m2.m12
                    + m1.m32 * m2.m22 + m1.m33 * m2.m32;
            this.m33 = m1.m30 * m2.m03 + m1.m31 * m2.m13
                    + m1.m32 * m2.m23 + m1.m33 * m2.m33;
        } else
        {
            double m00, m01, m02, m03,
                    m10, m11, m12, m13,
                    m20, m21, m22, m23,
                    m30, m31, m32, m33;  // vars for temp result matrix

            // code for mat mul
            m00 = m1.m00 * m2.m00 + m1.m01 * m2.m10 + m1.m02 * m2.m20 + m1.m03 * m2.m30;
            m01 = m1.m00 * m2.m01 + m1.m01 * m2.m11 + m1.m02 * m2.m21 + m1.m03 * m2.m31;
            m02 = m1.m00 * m2.m02 + m1.m01 * m2.m12 + m1.m02 * m2.m22 + m1.m03 * m2.m32;
            m03 = m1.m00 * m2.m03 + m1.m01 * m2.m13 + m1.m02 * m2.m23 + m1.m03 * m2.m33;

            m10 = m1.m10 * m2.m00 + m1.m11 * m2.m10 + m1.m12 * m2.m20 + m1.m13 * m2.m30;
            m11 = m1.m10 * m2.m01 + m1.m11 * m2.m11 + m1.m12 * m2.m21 + m1.m13 * m2.m31;
            m12 = m1.m10 * m2.m02 + m1.m11 * m2.m12 + m1.m12 * m2.m22 + m1.m13 * m2.m32;
            m13 = m1.m10 * m2.m03 + m1.m11 * m2.m13 + m1.m12 * m2.m23 + m1.m13 * m2.m33;

            m20 = m1.m20 * m2.m00 + m1.m21 * m2.m10 + m1.m22 * m2.m20 + m1.m23 * m2.m30;
            m21 = m1.m20 * m2.m01 + m1.m21 * m2.m11 + m1.m22 * m2.m21 + m1.m23 * m2.m31;
            m22 = m1.m20 * m2.m02 + m1.m21 * m2.m12 + m1.m22 * m2.m22 + m1.m23 * m2.m32;
            m23 = m1.m20 * m2.m03 + m1.m21 * m2.m13 + m1.m22 * m2.m23 + m1.m23 * m2.m33;

            m30 = m1.m30 * m2.m00 + m1.m31 * m2.m10 + m1.m32 * m2.m20 + m1.m33 * m2.m30;
            m31 = m1.m30 * m2.m01 + m1.m31 * m2.m11 + m1.m32 * m2.m21 + m1.m33 * m2.m31;
            m32 = m1.m30 * m2.m02 + m1.m31 * m2.m12 + m1.m32 * m2.m22 + m1.m33 * m2.m32;
            m33 = m1.m30 * m2.m03 + m1.m31 * m2.m13 + m1.m32 * m2.m23 + m1.m33 * m2.m33;

            this.m00 = m00;
            this.m01 = m01;
            this.m02 = m02;
            this.m03 = m03;
            this.m10 = m10;
            this.m11 = m11;
            this.m12 = m12;
            this.m13 = m13;
            this.m20 = m20;
            this.m21 = m21;
            this.m22 = m22;
            this.m23 = m23;
            this.m30 = m30;
            this.m31 = m31;
            this.m32 = m32;
            this.m33 = m33;

        }
    }

    /**
     *  Multiplies the transpose of matrix m1 times the transpose of matrix
     *  m2, and places the result into this.
     *  @param m1  the matrix on the left hand side of the multiplication
     *  @param m2  the matrix on the right hand side of the multiplication
     */
    public final void mulTransposeBoth(CyMatrix4d m1, CyMatrix4d m2)
    {
        if (this != m1 && this != m2)
        {
            this.m00 = m1.m00 * m2.m00 + m1.m10 * m2.m01 + m1.m20 * m2.m02 + m1.m30 * m2.m03;
            this.m01 = m1.m00 * m2.m10 + m1.m10 * m2.m11 + m1.m20 * m2.m12 + m1.m30 * m2.m13;
            this.m02 = m1.m00 * m2.m20 + m1.m10 * m2.m21 + m1.m20 * m2.m22 + m1.m30 * m2.m23;
            this.m03 = m1.m00 * m2.m30 + m1.m10 * m2.m31 + m1.m20 * m2.m32 + m1.m30 * m2.m33;

            this.m10 = m1.m01 * m2.m00 + m1.m11 * m2.m01 + m1.m21 * m2.m02 + m1.m31 * m2.m03;
            this.m11 = m1.m01 * m2.m10 + m1.m11 * m2.m11 + m1.m21 * m2.m12 + m1.m31 * m2.m13;
            this.m12 = m1.m01 * m2.m20 + m1.m11 * m2.m21 + m1.m21 * m2.m22 + m1.m31 * m2.m23;
            this.m13 = m1.m01 * m2.m30 + m1.m11 * m2.m31 + m1.m21 * m2.m32 + m1.m31 * m2.m33;

            this.m20 = m1.m02 * m2.m00 + m1.m12 * m2.m01 + m1.m22 * m2.m02 + m1.m32 * m2.m03;
            this.m21 = m1.m02 * m2.m10 + m1.m12 * m2.m11 + m1.m22 * m2.m12 + m1.m32 * m2.m13;
            this.m22 = m1.m02 * m2.m20 + m1.m12 * m2.m21 + m1.m22 * m2.m22 + m1.m32 * m2.m23;
            this.m23 = m1.m02 * m2.m30 + m1.m12 * m2.m31 + m1.m22 * m2.m32 + m1.m32 * m2.m33;

            this.m30 = m1.m03 * m2.m00 + m1.m13 * m2.m01 + m1.m23 * m2.m02 + m1.m33 * m2.m03;
            this.m31 = m1.m03 * m2.m10 + m1.m13 * m2.m11 + m1.m23 * m2.m12 + m1.m33 * m2.m13;
            this.m32 = m1.m03 * m2.m20 + m1.m13 * m2.m21 + m1.m23 * m2.m22 + m1.m33 * m2.m23;
            this.m33 = m1.m03 * m2.m30 + m1.m13 * m2.m31 + m1.m23 * m2.m32 + m1.m33 * m2.m33;
        } else
        {
            double m00, m01, m02, m03,
                    m10, m11, m12, m13,
                    m20, m21, m22, m23, // vars for temp result matrix
                    m30, m31, m32, m33;

            m00 = m1.m00 * m2.m00 + m1.m10 * m2.m01 + m1.m20 * m2.m02 + m1.m30 * m2.m03;
            m01 = m1.m00 * m2.m10 + m1.m10 * m2.m11 + m1.m20 * m2.m12 + m1.m30 * m2.m13;
            m02 = m1.m00 * m2.m20 + m1.m10 * m2.m21 + m1.m20 * m2.m22 + m1.m30 * m2.m23;
            m03 = m1.m00 * m2.m30 + m1.m10 * m2.m31 + m1.m20 * m2.m32 + m1.m30 * m2.m33;

            m10 = m1.m01 * m2.m00 + m1.m11 * m2.m01 + m1.m21 * m2.m02 + m1.m31 * m2.m03;
            m11 = m1.m01 * m2.m10 + m1.m11 * m2.m11 + m1.m21 * m2.m12 + m1.m31 * m2.m13;
            m12 = m1.m01 * m2.m20 + m1.m11 * m2.m21 + m1.m21 * m2.m22 + m1.m31 * m2.m23;
            m13 = m1.m01 * m2.m30 + m1.m11 * m2.m31 + m1.m21 * m2.m32 + m1.m31 * m2.m33;

            m20 = m1.m02 * m2.m00 + m1.m12 * m2.m01 + m1.m22 * m2.m02 + m1.m32 * m2.m03;
            m21 = m1.m02 * m2.m10 + m1.m12 * m2.m11 + m1.m22 * m2.m12 + m1.m32 * m2.m13;
            m22 = m1.m02 * m2.m20 + m1.m12 * m2.m21 + m1.m22 * m2.m22 + m1.m32 * m2.m23;
            m23 = m1.m02 * m2.m30 + m1.m12 * m2.m31 + m1.m22 * m2.m32 + m1.m32 * m2.m33;

            m30 = m1.m03 * m2.m00 + m1.m13 * m2.m01 + m1.m23 * m2.m02 + m1.m33 * m2.m03;
            m31 = m1.m03 * m2.m10 + m1.m13 * m2.m11 + m1.m23 * m2.m12 + m1.m33 * m2.m13;
            m32 = m1.m03 * m2.m20 + m1.m13 * m2.m21 + m1.m23 * m2.m22 + m1.m33 * m2.m23;
            m33 = m1.m03 * m2.m30 + m1.m13 * m2.m31 + m1.m23 * m2.m32 + m1.m33 * m2.m33;

            this.m00 = m00;
            this.m01 = m01;
            this.m02 = m02;
            this.m03 = m03;
            this.m10 = m10;
            this.m11 = m11;
            this.m12 = m12;
            this.m13 = m13;
            this.m20 = m20;
            this.m21 = m21;
            this.m22 = m22;
            this.m23 = m23;
            this.m30 = m30;
            this.m31 = m31;
            this.m32 = m32;
            this.m33 = m33;
        }

    }

    /**
     *  Multiplies matrix m1 times the transpose of matrix m2, and
     *  places the result into this.
     *  @param m1  the matrix on the left hand side of the multiplication
     *  @param m2  the matrix on the right hand side of the multiplication
     */
    public final void mulTransposeRight(CyMatrix4d m1, CyMatrix4d m2)
    {
        if (this != m1 && this != m2)
        {
            this.m00 = m1.m00 * m2.m00 + m1.m01 * m2.m01 + m1.m02 * m2.m02 + m1.m03 * m2.m03;
            this.m01 = m1.m00 * m2.m10 + m1.m01 * m2.m11 + m1.m02 * m2.m12 + m1.m03 * m2.m13;
            this.m02 = m1.m00 * m2.m20 + m1.m01 * m2.m21 + m1.m02 * m2.m22 + m1.m03 * m2.m23;
            this.m03 = m1.m00 * m2.m30 + m1.m01 * m2.m31 + m1.m02 * m2.m32 + m1.m03 * m2.m33;

            this.m10 = m1.m10 * m2.m00 + m1.m11 * m2.m01 + m1.m12 * m2.m02 + m1.m13 * m2.m03;
            this.m11 = m1.m10 * m2.m10 + m1.m11 * m2.m11 + m1.m12 * m2.m12 + m1.m13 * m2.m13;
            this.m12 = m1.m10 * m2.m20 + m1.m11 * m2.m21 + m1.m12 * m2.m22 + m1.m13 * m2.m23;
            this.m13 = m1.m10 * m2.m30 + m1.m11 * m2.m31 + m1.m12 * m2.m32 + m1.m13 * m2.m33;

            this.m20 = m1.m20 * m2.m00 + m1.m21 * m2.m01 + m1.m22 * m2.m02 + m1.m23 * m2.m03;
            this.m21 = m1.m20 * m2.m10 + m1.m21 * m2.m11 + m1.m22 * m2.m12 + m1.m23 * m2.m13;
            this.m22 = m1.m20 * m2.m20 + m1.m21 * m2.m21 + m1.m22 * m2.m22 + m1.m23 * m2.m23;
            this.m23 = m1.m20 * m2.m30 + m1.m21 * m2.m31 + m1.m22 * m2.m32 + m1.m23 * m2.m33;

            this.m30 = m1.m30 * m2.m00 + m1.m31 * m2.m01 + m1.m32 * m2.m02 + m1.m33 * m2.m03;
            this.m31 = m1.m30 * m2.m10 + m1.m31 * m2.m11 + m1.m32 * m2.m12 + m1.m33 * m2.m13;
            this.m32 = m1.m30 * m2.m20 + m1.m31 * m2.m21 + m1.m32 * m2.m22 + m1.m33 * m2.m23;
            this.m33 = m1.m30 * m2.m30 + m1.m31 * m2.m31 + m1.m32 * m2.m32 + m1.m33 * m2.m33;
        } else
        {
            double m00, m01, m02, m03,
                    m10, m11, m12, m13,
                    m20, m21, m22, m23, // vars for temp result matrix
                    m30, m31, m32, m33;

            m00 = m1.m00 * m2.m00 + m1.m01 * m2.m01 + m1.m02 * m2.m02 + m1.m03 * m2.m03;
            m01 = m1.m00 * m2.m10 + m1.m01 * m2.m11 + m1.m02 * m2.m12 + m1.m03 * m2.m13;
            m02 = m1.m00 * m2.m20 + m1.m01 * m2.m21 + m1.m02 * m2.m22 + m1.m03 * m2.m23;
            m03 = m1.m00 * m2.m30 + m1.m01 * m2.m31 + m1.m02 * m2.m32 + m1.m03 * m2.m33;

            m10 = m1.m10 * m2.m00 + m1.m11 * m2.m01 + m1.m12 * m2.m02 + m1.m13 * m2.m03;
            m11 = m1.m10 * m2.m10 + m1.m11 * m2.m11 + m1.m12 * m2.m12 + m1.m13 * m2.m13;
            m12 = m1.m10 * m2.m20 + m1.m11 * m2.m21 + m1.m12 * m2.m22 + m1.m13 * m2.m23;
            m13 = m1.m10 * m2.m30 + m1.m11 * m2.m31 + m1.m12 * m2.m32 + m1.m13 * m2.m33;

            m20 = m1.m20 * m2.m00 + m1.m21 * m2.m01 + m1.m22 * m2.m02 + m1.m23 * m2.m03;
            m21 = m1.m20 * m2.m10 + m1.m21 * m2.m11 + m1.m22 * m2.m12 + m1.m23 * m2.m13;
            m22 = m1.m20 * m2.m20 + m1.m21 * m2.m21 + m1.m22 * m2.m22 + m1.m23 * m2.m23;
            m23 = m1.m20 * m2.m30 + m1.m21 * m2.m31 + m1.m22 * m2.m32 + m1.m23 * m2.m33;

            m30 = m1.m30 * m2.m00 + m1.m31 * m2.m01 + m1.m32 * m2.m02 + m1.m33 * m2.m03;
            m31 = m1.m30 * m2.m10 + m1.m31 * m2.m11 + m1.m32 * m2.m12 + m1.m33 * m2.m13;
            m32 = m1.m30 * m2.m20 + m1.m31 * m2.m21 + m1.m32 * m2.m22 + m1.m33 * m2.m23;
            m33 = m1.m30 * m2.m30 + m1.m31 * m2.m31 + m1.m32 * m2.m32 + m1.m33 * m2.m33;

            this.m00 = m00;
            this.m01 = m01;
            this.m02 = m02;
            this.m03 = m03;
            this.m10 = m10;
            this.m11 = m11;
            this.m12 = m12;
            this.m13 = m13;
            this.m20 = m20;
            this.m21 = m21;
            this.m22 = m22;
            this.m23 = m23;
            this.m30 = m30;
            this.m31 = m31;
            this.m32 = m32;
            this.m33 = m33;
        }
    }

    /**
     *  Multiplies the transpose of matrix m1 times matrix m2, and
     *  places the result into this.
     *  @param m1  the matrix on the left hand side of the multiplication
     *  @param m2  the matrix on the right hand side of the multiplication
     */
    public final void mulTransposeLeft(CyMatrix4d m1, CyMatrix4d m2)
    {
        if (this != m1 && this != m2)
        {
            this.m00 = m1.m00 * m2.m00 + m1.m10 * m2.m10 + m1.m20 * m2.m20 + m1.m30 * m2.m30;
            this.m01 = m1.m00 * m2.m01 + m1.m10 * m2.m11 + m1.m20 * m2.m21 + m1.m30 * m2.m31;
            this.m02 = m1.m00 * m2.m02 + m1.m10 * m2.m12 + m1.m20 * m2.m22 + m1.m30 * m2.m32;
            this.m03 = m1.m00 * m2.m03 + m1.m10 * m2.m13 + m1.m20 * m2.m23 + m1.m30 * m2.m33;

            this.m10 = m1.m01 * m2.m00 + m1.m11 * m2.m10 + m1.m21 * m2.m20 + m1.m31 * m2.m30;
            this.m11 = m1.m01 * m2.m01 + m1.m11 * m2.m11 + m1.m21 * m2.m21 + m1.m31 * m2.m31;
            this.m12 = m1.m01 * m2.m02 + m1.m11 * m2.m12 + m1.m21 * m2.m22 + m1.m31 * m2.m32;
            this.m13 = m1.m01 * m2.m03 + m1.m11 * m2.m13 + m1.m21 * m2.m23 + m1.m31 * m2.m33;

            this.m20 = m1.m02 * m2.m00 + m1.m12 * m2.m10 + m1.m22 * m2.m20 + m1.m32 * m2.m30;
            this.m21 = m1.m02 * m2.m01 + m1.m12 * m2.m11 + m1.m22 * m2.m21 + m1.m32 * m2.m31;
            this.m22 = m1.m02 * m2.m02 + m1.m12 * m2.m12 + m1.m22 * m2.m22 + m1.m32 * m2.m32;
            this.m23 = m1.m02 * m2.m03 + m1.m12 * m2.m13 + m1.m22 * m2.m23 + m1.m32 * m2.m33;

            this.m30 = m1.m03 * m2.m00 + m1.m13 * m2.m10 + m1.m23 * m2.m20 + m1.m33 * m2.m30;
            this.m31 = m1.m03 * m2.m01 + m1.m13 * m2.m11 + m1.m23 * m2.m21 + m1.m33 * m2.m31;
            this.m32 = m1.m03 * m2.m02 + m1.m13 * m2.m12 + m1.m23 * m2.m22 + m1.m33 * m2.m32;
            this.m33 = m1.m03 * m2.m03 + m1.m13 * m2.m13 + m1.m23 * m2.m23 + m1.m33 * m2.m33;
        } else
        {
            double m00, m01, m02, m03,
                    m10, m11, m12, m13,
                    m20, m21, m22, m23, // vars for temp result matrix
                    m30, m31, m32, m33;



            m00 = m1.m00 * m2.m00 + m1.m10 * m2.m10 + m1.m20 * m2.m20 + m1.m30 * m2.m30;
            m01 = m1.m00 * m2.m01 + m1.m10 * m2.m11 + m1.m20 * m2.m21 + m1.m30 * m2.m31;
            m02 = m1.m00 * m2.m02 + m1.m10 * m2.m12 + m1.m20 * m2.m22 + m1.m30 * m2.m32;
            m03 = m1.m00 * m2.m03 + m1.m10 * m2.m13 + m1.m20 * m2.m23 + m1.m30 * m2.m33;

            m10 = m1.m01 * m2.m00 + m1.m11 * m2.m10 + m1.m21 * m2.m20 + m1.m31 * m2.m30;
            m11 = m1.m01 * m2.m01 + m1.m11 * m2.m11 + m1.m21 * m2.m21 + m1.m31 * m2.m31;
            m12 = m1.m01 * m2.m02 + m1.m11 * m2.m12 + m1.m21 * m2.m22 + m1.m31 * m2.m32;
            m13 = m1.m01 * m2.m03 + m1.m11 * m2.m13 + m1.m21 * m2.m23 + m1.m31 * m2.m33;

            m20 = m1.m02 * m2.m00 + m1.m12 * m2.m10 + m1.m22 * m2.m20 + m1.m32 * m2.m30;
            m21 = m1.m02 * m2.m01 + m1.m12 * m2.m11 + m1.m22 * m2.m21 + m1.m32 * m2.m31;
            m22 = m1.m02 * m2.m02 + m1.m12 * m2.m12 + m1.m22 * m2.m22 + m1.m32 * m2.m32;
            m23 = m1.m02 * m2.m03 + m1.m12 * m2.m13 + m1.m22 * m2.m23 + m1.m32 * m2.m33;

            m30 = m1.m03 * m2.m00 + m1.m13 * m2.m10 + m1.m23 * m2.m20 + m1.m33 * m2.m30;
            m31 = m1.m03 * m2.m01 + m1.m13 * m2.m11 + m1.m23 * m2.m21 + m1.m33 * m2.m31;
            m32 = m1.m03 * m2.m02 + m1.m13 * m2.m12 + m1.m23 * m2.m22 + m1.m33 * m2.m32;
            m33 = m1.m03 * m2.m03 + m1.m13 * m2.m13 + m1.m23 * m2.m23 + m1.m33 * m2.m33;

            this.m00 = m00;
            this.m01 = m01;
            this.m02 = m02;
            this.m03 = m03;
            this.m10 = m10;
            this.m11 = m11;
            this.m12 = m12;
            this.m13 = m13;
            this.m20 = m20;
            this.m21 = m21;
            this.m22 = m22;
            this.m23 = m23;
            this.m30 = m30;
            this.m31 = m31;
            this.m32 = m32;
            this.m33 = m33;
        }

    }

    /**
     * Returns true if all of the data members of Matrix4d m1 are
     * equal to the corresponding data members in this Matrix4d.
     * @param m1  the matrix with which the comparison is made
     * @return  true or false
     */
    public boolean equals(CyMatrix4d m1)
    {
        try
        {
            return (this.m00 == m1.m00 && this.m01 == m1.m01 && this.m02 == m1.m02
                    && this.m03 == m1.m03 && this.m10 == m1.m10 && this.m11 == m1.m11
                    && this.m12 == m1.m12 && this.m13 == m1.m13 && this.m20 == m1.m20
                    && this.m21 == m1.m21 && this.m22 == m1.m22 && this.m23 == m1.m23
                    && this.m30 == m1.m30 && this.m31 == m1.m31 && this.m32 == m1.m32
                    && this.m33 == m1.m33);
        } catch (NullPointerException e2)
        {
            return false;
        }

    }

    /**
     * Returns true if the Object t1 is of type Matrix4d and all of the
     * data members of t1 are equal to the corresponding data members in
     * this Matrix4d.
     * @param t1  the matrix with which the comparison is made
     * @return  true or false
     */
    public boolean equals(Object t1)
    {
        try
        {
            CyMatrix4d m2 = (CyMatrix4d) t1;
            return (this.m00 == m2.m00 && this.m01 == m2.m01 && this.m02 == m2.m02
                    && this.m03 == m2.m03 && this.m10 == m2.m10 && this.m11 == m2.m11
                    && this.m12 == m2.m12 && this.m13 == m2.m13 && this.m20 == m2.m20
                    && this.m21 == m2.m21 && this.m22 == m2.m22 && this.m23 == m2.m23
                    && this.m30 == m2.m30 && this.m31 == m2.m31 && this.m32 == m2.m32
                    && this.m33 == m2.m33);
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
     * MAX[i=0,1,2,3 ; j=0,1,2,3 ; abs(this.m(i,j) - m1.m(i,j)]
     * @param m1  the matrix to be compared to this matrix
     * @param epsilon  the threshold value
     */
    public boolean epsilonEquals(CyMatrix4d m1, double epsilon)
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

        diff = m03 - m1.m03;
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

        diff = m13 - m1.m13;
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

        diff = m23 - m1.m23;
        if ((diff < 0 ? -diff : diff) > epsilon)
        {
            return false;
        }

        diff = m30 - m1.m30;
        if ((diff < 0 ? -diff : diff) > epsilon)
        {
            return false;
        }

        diff = m31 - m1.m31;
        if ((diff < 0 ? -diff : diff) > epsilon)
        {
            return false;
        }

        diff = m32 - m1.m32;
        if ((diff < 0 ? -diff : diff) > epsilon)
        {
            return false;
        }

        diff = m33 - m1.m33;
        if ((diff < 0 ? -diff : diff) > epsilon)
        {
            return false;
        }

        return true;
    }

    /**
     * Returns a hash code value based on the data values in this
     * object.  Two different Matrix4d objects with identical data values
     * (i.e., Matrix4d.equals returns true) will return the same hash
     * code value.  Two objects with different data members may return the
     * same hash value, although this is not likely.
     * @return the integer hash code value
     */
    @Override
    public int hashCode()
    {
        long bits = 1L;
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m00);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m01);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m02);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m03);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m10);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m11);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m12);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m13);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m20);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m21);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m22);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m23);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m30);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m31);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m32);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m33);
        return (int) (bits ^ (bits >> 32));
    }

    /**
     * Transform the vector vec using this Matrix4d and place the
     * result into vecOut.
     * @param vec  the double precision vector to be transformed
     * @param vecOut  the vector into which the transformed values are placed
     */
    public final void transform(CyVector4d vec, CyVector4d vecOut)
    {
        double x, y, z, w;
        x = (m00 * vec.x + m01 * vec.y
                + m02 * vec.z + m03 * vec.w);
        y = (m10 * vec.x + m11 * vec.y
                + m12 * vec.z + m13 * vec.w);
        z = (m20 * vec.x + m21 * vec.y
                + m22 * vec.z + m23 * vec.w);
        vecOut.w = (m30 * vec.x + m31 * vec.y
                + m32 * vec.z + m33 * vec.w);
        vecOut.x = x;
        vecOut.y = y;
        vecOut.z = z;
    }

    /**
     * Transform the vector vec using this Matrix4d and place the
     * result back into vec.
     * @param vec  the double precision vector to be transformed
     */
    public final void transform(CyVector4d vec)
    {
        double x, y, z;

        x = (m00 * vec.x + m01 * vec.y
                + m02 * vec.z + m03 * vec.w);
        y = (m10 * vec.x + m11 * vec.y
                + m12 * vec.z + m13 * vec.w);
        z = (m20 * vec.x + m21 * vec.y
                + m22 * vec.z + m23 * vec.w);
        vec.w = (m30 * vec.x + m31 * vec.y
                + m32 * vec.z + m33 * vec.w);
        vec.x = x;
        vec.y = y;
        vec.z = z;
    }

  /**
   * Transforms the point parameter with this Matrix4d and
   * places the result back into point.  The fourth element of the
   * point input parameter is assumed to be one.
   * @param point  the input point to be transformed.
   */
    public final void transformPoint(CyVector3d point)
    {
        double x, y;
        x = m00*point.x + m01*point.y + m02*point.z + m03;
        y = m10*point.x + m11*point.y + m12*point.z + m13;
        point.z =  m20*point.x + m21*point.y + m22*point.z + m23;
        point.x = x;
        point.y = y;
    }

    public CyVector3d transformPoint(CyVector3d p, boolean normalize)
    {
        double x, y, z, w;
        x = m00 * p.x + m01 * p.y + m02 * p.z + m03;
        y = m10 * p.x + m11 * p.y + m12 * p.z + m13;
        z = m20 * p.x + m21 * p.y + m22 * p.z + m23;
        if (normalize)
        {
            w = m30 * p.x + m31 * p.y + m32 * p.z + m33;
            double invW = 1 / w;
            x *= invW;
            y *= invW;
            z *= invW;
        }
        p.x = x;
        p.y = y;
        p.z = z;

        return p;
    }

    public CyVector3d transformVector(CyVector3d v, boolean normalize)
    {
        double x, y, z, w;
        x = m00 * v.x + m01 * v.y + m02 * v.z;
        y = m10 * v.x + m11 * v.y + m12 * v.z;
        z = m20 * v.x + m21 * v.y + m22 * v.z;
        if (normalize)
        {
            w = m30 * v.x + m31 * v.y + m32 * v.z;
            double invW = 1 / w;
            x *= invW;
            y *= invW;
            z *= invW;
        }
        v.x = x;
        v.y = y;
        v.z = z;

        return v;
    }

    /**
     * Transforms the point parameter with this Matrix4d and
     * places the result back into point.  The fourth element of the
     * point input parameter is assumed to be one.
     * @param point  the input point to be transformed.
     */
    public final void transformPoint(CyVector2d point)
    {
        double x, y;
        x = m00*point.x + m01*point.y + m03;
        y = m10*point.x + m11*point.y + m13;
        point.y = y;
        point.x = x;
    }

    public final void transformPoint(CyVector2d src, CyVector2d dest)
    {
        double x, y;
        x = m00*src.x + m01*src.y + m03;
        y = m10*src.x + m11*src.y + m13;
        dest.y = y;
        dest.x = x;
    }

    public CyVector2d transformPoint(CyVector2d p, boolean normalize)
    {
        double x, y, w;
        x = m00 * p.x + m01 * p.y + m03;
        y = m10 * p.x + m11 * p.y + m13;
        if (normalize)
        {
            w = m30 * p.x + m31 * p.y + m33;
            double invW = 1 / w;
            x *= invW;
            y *= invW;
        }
        p.x = x;
        p.y = y;

        return p;
    }

    public final void transformVector(CyVector2d vec)
    {
        transformVector(vec, false);
    }

    public CyVector2d transformVector(CyVector2d v, boolean normalize)
    {
        double x, y, w;
        x = m00 * v.x + m01 * v.y;
        y = m10 * v.x + m11 * v.y;
        if (normalize)
        {
            w = m30 * v.x + m31 * v.y;
            double invW = 1 / w;
            x *= invW;
            y *= invW;
        }
        v.x = x;
        v.y = y;

        return v;
    }

    /**
     *  Sets this matrix to all zeros.
     */
    public final void setZero()
    {
        m00 = 0.0;
        m01 = 0.0;
        m02 = 0.0;
        m03 = 0.0;
        m10 = 0.0;
        m11 = 0.0;
        m12 = 0.0;
        m13 = 0.0;
        m20 = 0.0;
        m21 = 0.0;
        m22 = 0.0;
        m23 = 0.0;
        m30 = 0.0;
        m31 = 0.0;
        m32 = 0.0;
        m33 = 0.0;
    }

    public final void setIdentity()
    {
        m00 = 1.0;
        m01 = 0.0;
        m02 = 0.0;
        m03 = 0.0;
        m10 = 0.0;
        m11 = 1.0;
        m12 = 0.0;
        m13 = 0.0;
        m20 = 0.0;
        m21 = 0.0;
        m22 = 1.0;
        m23 = 0.0;
        m30 = 0.0;
        m31 = 0.0;
        m32 = 0.0;
        m33 = 1.0;
    }

    /**
     * Negates the value of this matrix: this = -this.
     */
    public final void negate()
    {
        m00 = -m00;
        m01 = -m01;
        m02 = -m02;
        m03 = -m03;
        m10 = -m10;
        m11 = -m11;
        m12 = -m12;
        m13 = -m13;
        m20 = -m20;
        m21 = -m21;
        m22 = -m22;
        m23 = -m23;
        m30 = -m30;
        m31 = -m31;
        m32 = -m32;
        m33 = -m33;
    }

    /**
     * Concatenate matrix with matrix containing given translation
     * @param retValue
     * @param x
     * @param y
     * @param z
     */
    public void translate(double x, double y, double z)
    {
        m03 = x * m00 + y * m01 + z * m02 + m03;
        m13 = x * m10 + y * m11 + z * m12 + m13;
        m23 = x * m20 + y * m21 + z * m22 + m23;
        m33 = x * m30 + y * m31 + z * m32 + m33;
    }

    public void translate(CyVector3d tuple)
    {
        translate(tuple.x, tuple.y, tuple.z);
    }

    public void scale(double x, double y, double z)
    {
        m00 *= x;
        m10 *= x;
        m20 *= x;
        m30 *= x;

        m01 *= y;
        m11 *= y;
        m21 *= y;
        m31 *= y;

        m02 *= z;
        m12 *= z;
        m22 *= z;
        m32 *= z;
    }

    public void rotX(double angle)
    {
        double cos = (double)Math.cos(angle);
        double sin = (double)Math.sin(angle);

        double tm01 = cos * m01 + sin * m02;
        double tm11 = cos * m11 + sin * m12;
        double tm21 = cos * m21 + sin * m22;
        double tm31 = cos * m31 + sin * m32;

        double tm02 = -sin * m01 + cos * m02;
        double tm12 = -sin * m11 + cos * m12;
        double tm22 = -sin * m21 + cos * m22;
        double tm32 = -sin * m31 + cos * m32;

        m01 = tm01;
        m11 = tm11;
        m21 = tm21;
        m31 = tm31;

        m02 = tm02;
        m12 = tm12;
        m22 = tm22;
        m32 = tm32;
    }

    public void rotY(double angle)
    {
        double cos = (double)Math.cos(angle);
        double sin = (double)Math.sin(angle);

        double tm00 = cos * m00 - sin * m02;
        double tm10 = cos * m10 - sin * m12;
        double tm20 = cos * m20 - sin * m22;
        double tm30 = cos * m30 - sin * m32;

        double tm02 = sin * m00 + cos * m02;
        double tm12 = sin * m10 + cos * m12;
        double tm22 = sin * m20 + cos * m22;
        double tm32 = sin * m30 + cos * m32;

        m00 = tm00;
        m10 = tm10;
        m20 = tm20;
        m30 = tm30;

        m02 = tm02;
        m12 = tm12;
        m22 = tm22;
        m32 = tm32;
    }

    public void rotZ(double angle)
    {
        double cos = (double)Math.cos(angle);
        double sin = (double)Math.sin(angle);

        double tm00 = cos * m00 + sin * m01;
        double tm10 = cos * m10 + sin * m11;
        double tm20 = cos * m20 + sin * m21;
        double tm30 = cos * m30 + sin * m31;

        double tm01 = -sin * m00 + cos * m01;
        double tm11 = -sin * m10 + cos * m11;
        double tm21 = -sin * m20 + cos * m21;
        double tm31 = -sin * m30 + cos * m31;

        m00 = tm00;
        m10 = tm10;
        m20 = tm20;
        m30 = tm30;

        m01 = tm01;
        m11 = tm11;
        m21 = tm21;
        m31 = tm31;
    }

    public void shear(double shx, double shy)
    {
        double sm00 = m00 + m01 * shy;
        double sm10 = m10 + m11 * shy;
        double sm20 = m20 + m21 * shy;
        double sm30 = m30 + m31 * shy;

        double sm01 = m00 * shx + m01;
        double sm11 = m10 * shx + m11;
        double sm21 = m20 * shx + m21;
        double sm31 = m30 * shx + m31;

        m00 = sm00;
        m10 = sm10;
        m20 = sm20;
        m30 = sm30;

        m01 = sm01;
        m11 = sm11;
        m21 = sm21;
        m31 = sm31;
    }

    public void shear(double shx, double shy, double shz)
    {
        double sm00 = m00 + m01 * shy + m02 * shz;
        double sm10 = m10 + m11 * shy + m12 * shz;
        double sm20 = m20 + m21 * shy + m22 * shz;
        double sm30 = m30 + m31 * shy + m32 * shz;

        double sm01 = m00 * shx + m01 + m02 * shz;
        double sm11 = m10 * shx + m11 + m12 * shz;
        double sm21 = m20 * shx + m21 + m22 * shz;
        double sm31 = m30 * shx + m31 + m32 * shz;

        double sm02 = m00 * shx + m01 * shy + m02;
        double sm12 = m10 * shx + m11 * shy + m12;
        double sm22 = m20 * shx + m21 * shy + m22;
        double sm32 = m30 * shx + m31 * shy + m32;

        m00 = sm00;
        m10 = sm10;
        m20 = sm20;
        m30 = sm30;

        m01 = sm01;
        m11 = sm11;
        m21 = sm21;
        m31 = sm31;

        m02 = sm02;
        m12 = sm12;
        m22 = sm22;
        m32 = sm32;
    }

    /**
     * This builds the OpenGL gluLookAt matrix.
     * Taken from Mesa 3.5
     */
    /*
    public void lookAt(double eyeX, double eyeY, double eyeZ,
            double centerX, double centerY, double centerZ,
            double upX, double upY, double upZ)
    {
        CyVector3d z = new CyVector3d(eyeX - centerX, eyeY - centerY, eyeZ - centerZ);
        z.normalize();

        CyVector3d y = new CyVector3d(upX, upY, upZ);

        CyVector3d x = new CyVector3d();
        x.cross(y, z);

        y.cross(z, x);

        x.normalize();
        y.normalize();

        //Since x, y, z is orthogonal, Given B = [x y z] then B^-1 = B^t.
        // That's why the basis vectors are being put in the rows instead of columns.
        m00 = x.x; m01 = x.y; m02 = x.z; m03 = -eyeX * x.x + -eyeY * x.y + -eyeZ * x.z;
        m10 = y.x; m11 = y.y; m12 = y.z; m13 = -eyeX * y.x + -eyeY * y.y + -eyeZ * y.z;
        m20 = z.x; m21 = z.y; m22 = z.z; m23 = -eyeX * z.x + -eyeY * z.y + -eyeZ * z.z;
        m30 = 0; m31 = 0; m32 = 0; m33 = 1;
    }
    */

    public void gluLookAt(double eyeX, double eyeY, double eyeZ, 
            double centerX, double centerY, double centerZ,
            double upX, double upY, double upZ)
    {
        //http://pyopengl.sourceforge.net/documentation/manual/gluLookAt.3G.html
        double fx = eyeX - centerX;
        double fy = eyeY - centerY;
        double fz = eyeZ - centerZ;
        
        double fLenI = 1 / Math.sqrt(fx * fx + fy * fy + fz * fz);
        
        fx *= fLenI;
        fy *= fLenI;
        fz *= fLenI;
        
        //cross(f, up)
        double sx = upY * fz - upZ * fy;
        double sy = upZ * fx - upX * fz;
        double sz = upX * fy - upY * fx;
        
        double sLenI = 1 / Math.sqrt(sx * sx + sy * sy + sz * sz);
        sx *= sLenI;
        sy *= sLenI;
        sz *= sLenI;
        
        //cross(s, f)
        double ux = fy * sz - fz * sy;
        double uy = fz * sx - fx * sz;
        double uz = fx * sy - fy * sx;
        
        m00 = sx;
        m10 = ux;
        m20 = fx;
        m30 = 0;
        
        m01 = sy;
        m11 = uy;
        m21 = fy;
        m31 = 0;
        
        m02 = sz;
        m12 = uz;
        m22 = fz;
        m32 = 0;
        
        m03 = -eyeX * sx + -eyeY * sy + -eyeZ * sz;
        m13 = -eyeX * ux + -eyeY * uy + -eyeZ * uz;
        m23 = -eyeX * fx + -eyeY * fy + -eyeZ * fz;
        m33 = 1;
    }

    public void gluOrtho2D(double left, double right, double bottom, double top)
    {
        glOrtho(left, right, bottom, top, -1f, 1f);
    }

    public void glOrtho(double left, double right, double bottom, double top, double near, double far)
    {
        m00 = 2 / (right - left); m01 = 0; m02 = 0; m03 = -(right + left) / (right - left);
        m10 = 0; m11 = 2 / (top - bottom); m12 = 0; m13 = -(top + bottom) / (top - bottom);
        m20 = 0; m21 = 0; m22 = -2 / (far - near); m23 = -(far + near) / (far - near);
        m30 = 0; m31 = 0; m32 = 0; m33 = 1;
    }

    public void glFrustum(double left, double right, double bottom, double top, double near, double far)
    {
        double x = (2 * near) / (right - left);
        double y = (2 * near) / (top - bottom);
        double a = (right + left) / (right - left);
        double b = (top + bottom) / (top - bottom);
        double c = -(far + near) / ( far - near);
        double d = -(2 * far * near) / (far - near);

        m00 = x; m01 = 0; m02 = a; m03 = 0;
        m10 = 0; m11 = y; m12 = b; m13 = 0;
        m20 = 0; m21 = 0; m22 = c; m23 = d;
        m30 = 0; m31 = 0; m32 = -1; m33 = 0;
    }

    public void gluPerspective(double fovy, double aspect, double zNear, double zFar)
    {
        double ymax = zNear * Math.tan(fovy * Math.PI / 360.0);
        double ymin = -ymax;
        double xmin = ymin * aspect;
        double xmax = ymax * aspect;

        glFrustum(xmin, xmax, ymin, ymax, zNear, zFar);
    }

    public void gluPickMatrix(double x, double y, double width, double height,
            CyRectangle2i viewport)
    {
        double sx = viewport.getWidth() / width;
        double sy = viewport.getHeight() / height;
        double tx = (viewport.getWidth() + 2 * (viewport.getX() - x)) / width;
        double ty = (viewport.getHeight() + 2 * (viewport.getY() - y)) / height;

        m00 = sx; m01 = 0; m02 = 0; m03 = tx;
        m10 = 0; m11 = sy; m12 = 0; m13 = ty;
        m20 = 0; m21 = 0; m22 = 1; m23 = 0;
        m30 = 0; m31 = 0; m32 = 0; m33 = 1;
    }

    @Override
    public String toString()
    {
        return "[" + m00 + " " + m01 + " " + m02 + " " + m03 + "]\n"
                + "[" + m10 + " " + m11 + " " + m12 + " " + m13 + "]\n"
                + "[" + m20 + " " + m21 + " " + m22 + " " + m23 + "]\n"
                + "[" + m30 + " " + m31 + " " + m32 + " " + m33 + "]\n";
    }

    public void setTransform(AffineTransform m)
    {
        set(m.getScaleX(), m.getShearX(), 0, 0,
            m.getShearY(), m.getScaleY(), 0, 0,
            0, 0, 1, 0,
            m.getTranslateX(), m.getTranslateY(), 0, 1);
    }

    public AffineTransform asAffineTransform()
    {
        return new AffineTransform(m00, m10, m01, m11, m30, m31);
    }

    public boolean isIdentity()
    {
        return m00 == 1 && m01 == 0 && m02 == 0 && m03 == 0
                && m10 == 0 && m11 == 1 && m12 == 0 && m13 == 0
                && m20 == 0 && m21 == 0 && m22 == 1 && m23 == 0
                && m30 == 0 && m31 == 0 && m32 == 0 && m33 == 1;
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

    public static CyMatrix4d createComponents(CyRectangle2d box)
    {
        return createComponents(box.getX(), box.getY(),
                box.getWidth(), box.getHeight(), 0, 90);
    }

    public static CyMatrix4d createComponents(
            double transX, double transY,
            double scaleX, double scaleY,
            double angle, double skewAngle)
    {
        //Paint will span [0 1] unit square.

        //Coordinates given either in local or world space.
        // If in local space, xform will have the localToWorld xform.
        double sinx = Math.sin(Math.toRadians(angle));
        double cosx = Math.cos(Math.toRadians(angle));
        double siny = Math.sin(Math.toRadians(angle + skewAngle));
        double cosy = Math.cos(Math.toRadians(angle + skewAngle));

        //Basis for top right quadrant
        double ix = cosx * scaleX;
        double iy = sinx * scaleX;
        double jx = cosy * scaleY;
        double jy = siny * scaleY;

        //Transform basis onto [-1 1] unit square
        return new CyMatrix4d(
                ix, iy, 0, 0,
                jx, jy, 0, 0,
                0, 0, 1, 0,
                transX, transY, 0, 1);
    }
}
