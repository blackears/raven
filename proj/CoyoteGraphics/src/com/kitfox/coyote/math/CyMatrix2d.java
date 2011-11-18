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
public class CyMatrix2d
{
    double m00;
    double m10;
    double m01;
    double m11;

    public CyMatrix2d()
    {
    }

    public CyMatrix2d(double m00, double m10, double m01, double m11)
    {
        this.m00 = m00;
        this.m10 = m10;
        this.m01 = m01;
        this.m11 = m11;
    }

    /**
     * Sets this Matrix3d to identity.
     */
    public final void setIdentity()
    {
        this.m00 = 1.0;
        this.m01 = 0.0;

        this.m10 = 0.0;
        this.m11 = 1.0;
    }

    /**
     * Sets the value of this matrix to the sum of itself and matrix m1.
     * @param m1 the other matrix
     */
    public final void add(CyMatrix2d m1)
    {
        this.m00 += m1.m00;
        this.m01 += m1.m01;

        this.m10 += m1.m10;
        this.m11 += m1.m11;
    }

    /**
     * Sets the value of this matrix to the matrix difference of itself and
     * matrix m1 (this = this - m1).
     * @param m1 the other matrix
     */
    public final void sub(CyMatrix2d m1)
    {
        this.m00 -= m1.m00;
        this.m01 -= m1.m01;

        this.m10 -= m1.m10;
        this.m11 -= m1.m11;
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
    }

    /**
     * Sets the value of this matrix to the value of the Matrix3d
     * argument.
     * @param m1 the source matrix3d
     */
    public final void set(CyMatrix2d m1)
    {
        this.m00 = m1.m00;
        this.m01 = m1.m01;

        this.m10 = m1.m10;
        this.m11 = m1.m11;
    }

    /**
     * Inverts this matrix in place.
     */
    public final void invert()
    {
        m10 = -m10;
        m01 = -m01;
        double tmp = m00;
        m00 = m11;
        m11 = tmp;

        mul(1 / determinant());
    }

    public double determinant()
    {
        return m00 * m11 - m10 * m01;
    }

    /**
     * Multiplies each element of this matrix by a scalar.
     * @param scalar  The scalar multiplier.
     */
    public final void mul(double scalar)
    {
        m00 *= scalar;
        m01 *= scalar;

        m10 *= scalar;
        m11 *= scalar;
    }

    /**
     * Sets the value of this matrix to the result of multiplying
     * the two argument matrices together.
     * @param m1 the first matrix
     * @param m2 the second matrix
     */
    public final void mul(CyMatrix2d m1, CyMatrix2d m2)
    {
        if (this != m1 && this != m2)
        {
            this.m00 = m1.m00 * m2.m00 + m1.m01 * m2.m10;
            this.m01 = m1.m00 * m2.m01 + m1.m01 * m2.m11;

            this.m10 = m1.m10 * m2.m00 + m1.m11 * m2.m10;
            this.m11 = m1.m10 * m2.m01 + m1.m11 * m2.m11;
        } else
        {
            double m00, m01,
                    m10, m11;  // vars for temp result matrix

            m00 = m1.m00 * m2.m00 + m1.m01 * m2.m10;
            m01 = m1.m00 * m2.m01 + m1.m01 * m2.m11;

            m10 = m1.m10 * m2.m00 + m1.m11 * m2.m10;
            m11 = m1.m10 * m2.m01 + m1.m11 * m2.m11;

            this.m00 = m00;
            this.m01 = m01;
            this.m10 = m10;
            this.m11 = m11;
        }
    }

    /**
     * Returns true if all of the data members of Matrix3d m1 are
     * equal to the corresponding data members in this Matrix3d.
     * @param m1  the matrix with which the comparison is made
     * @return  true or false
     */
    public boolean equals(CyMatrix2d m1)
    {
        try
        {
            return (this.m00 == m1.m00 && this.m01 == m1.m01
                    && this.m10 == m1.m10 && this.m11 == m1.m11);
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
            CyMatrix2d m2 = (CyMatrix2d) t1;
            return (this.m00 == m2.m00 && this.m01 == m2.m01
                    && this.m10 == m2.m10 && this.m11 == m2.m11);
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
    public boolean epsilonEquals(CyMatrix2d m1, double epsilon)
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
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m10);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(m11);
        return (int) (bits ^ (bits >> 32));
    }

    /**
     *  Sets this matrix to all zeros.
     */
    public final void setZero()
    {
        m00 = 0.0;
        m01 = 0.0;

        m10 = 0.0;
        m11 = 0.0;
    }

    /**
     * Negates the value of this matrix: this = -this.
     */
    public final void negate()
    {
        this.m00 = -this.m00;
        this.m01 = -this.m01;

        this.m10 = -this.m10;
        this.m11 = -this.m11;
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

        this.m10 = -m1.m10;
        this.m11 = -m1.m11;
    }

    /**
     * Multiply this matrix by the tuple t and place the result
     * back into the tuple (t = this*t).
     * @param t  the tuple to be multiplied by this matrix and then replaced
     */
    public final void transform(CyVector2d t)
    {
        double x, y;
        x = m00 * t.x + m01 * t.y;
        y = m10 * t.x + m11 * t.y;
        t.set(x, y);
    }

    /**
     * Multiply this matrix by the tuple t and and place the result
     * into the tuple "result" (result = this*t).
     * @param t  the tuple to be multiplied by this matrix
     * @param result  the tuple into which the product is placed
     */
    public final void transform(CyVector2d t, CyVector2d result)
    {
        double x, y;
        x = m00 * t.x + m01 * t.y;
        y = m10 * t.x + m11 * t.y;
        result.x = x;
        result.y = y;
    }

}
