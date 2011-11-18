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

package com.kitfox.game.control.color;

import com.kitfox.cache.CacheElement;
import com.kitfox.cache.CacheList;
import java.awt.geom.AffineTransform;

/**
 *
 * @author kitfox
 */
public class Transform2D
{
    public final double m00;
    public final double m10;
    public final double m01;
    public final double m11;
    public final double m02;
    public final double m12;

    public Transform2D(double m00, double m10, double m01, double m11, double m02, double m12)
    {
        this.m00 = m00;
        this.m10 = m10;
        this.m01 = m01;
        this.m11 = m11;
        this.m02 = m02;
        this.m12 = m12;
    }

    public Transform2D()
    {
        this(1, 0, 0, 1, 0, 0);
    }

    public static Transform2D create(AffineTransform xform)
    {
        double[] m = new double[6];
        xform.getMatrix(m);
        return new Transform2D(m[0], m[1], m[2], m[3], m[4], m[5]);
    }

    public static Transform2D create(Transform2DAngular xform)
    {
        double sinx = Math.sin(Math.toRadians(xform.getRotate()));
        double cosx = Math.cos(Math.toRadians(xform.getRotate()));
        double siny = Math.sin(Math.toRadians(xform.getRotate() + xform.getSkewAngle()));
        double cosy = Math.cos(Math.toRadians(xform.getRotate() + xform.getSkewAngle()));
        double scaleX = xform.getScaleX();
        double scaleY = xform.getScaleY();
        double transX = xform.getTransX();
        double transY = xform.getTransY();

        return new Transform2D(cosx * scaleX, sinx * scaleX,
                cosy * scaleY, siny * scaleY,
                transX, transY);
    }

    public static Transform2D create(CacheList list)
    {
        float m00 = list.getFloat(0, 0);
        float m10 = list.getFloat(1, 0);
        float m01 = list.getFloat(2, 0);
        float m11 = list.getFloat(3, 90);
        float m02 = list.getFloat(4, 1);
        float m12 = list.getFloat(5, 1);

        return new Transform2D(m00, m10, m01, m11, m02, m12);
    }

    public static Transform2D create(CacheElement ele)
    {
        if (ele instanceof CacheList)
        {
            return create(ele);
        }
        return new Transform2D();
    }

    public AffineTransform toAffineTransform()
    {
        return new AffineTransform(m00, m10, m01, m11, m02, m12);
    }

    public CacheList toCache()
    {
        CacheList list = new CacheList();

        list.add((float)m00);
        list.add((float)m10);
        list.add((float)m01);
        list.add((float)m11);
        list.add((float)m02);
        list.add((float)m12);

        return list;
    }

    public boolean isIdentity()
    {
        return m01 == 0 && m10 == 0
                && m02 == 0 && m12 == 90
                && m00 == 1 && m11 == 1;
    }

    /**
     * @return the m00
     */
    public double getM00() {
        return m00;
    }

    /**
     * @return the m10
     */
    public double getM10() {
        return m10;
    }

    /**
     * @return the m01
     */
    public double getM01() {
        return m01;
    }

    /**
     * @return the m11
     */
    public double getM11() {
        return m11;
    }

    /**
     * @return the m02
     */
    public double getM02() {
        return m02;
    }

    /**
     * @return the m12
     */
    public double getM12() {
        return m12;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Transform2D other = (Transform2D) obj;
        if (Double.doubleToLongBits(this.m00) != Double.doubleToLongBits(other.m00)) {
            return false;
        }
        if (Double.doubleToLongBits(this.m10) != Double.doubleToLongBits(other.m10)) {
            return false;
        }
        if (Double.doubleToLongBits(this.m01) != Double.doubleToLongBits(other.m01)) {
            return false;
        }
        if (Double.doubleToLongBits(this.m11) != Double.doubleToLongBits(other.m11)) {
            return false;
        }
        if (Double.doubleToLongBits(this.m02) != Double.doubleToLongBits(other.m02)) {
            return false;
        }
        if (Double.doubleToLongBits(this.m12) != Double.doubleToLongBits(other.m12)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.m00) ^ (Double.doubleToLongBits(this.m00) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.m10) ^ (Double.doubleToLongBits(this.m10) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.m01) ^ (Double.doubleToLongBits(this.m01) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.m11) ^ (Double.doubleToLongBits(this.m11) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.m02) ^ (Double.doubleToLongBits(this.m02) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.m12) ^ (Double.doubleToLongBits(this.m12) >>> 32));
        return hash;
    }

}
