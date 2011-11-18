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
public class Transform2DAngular
{
    private final double transX;
    private final double transY;
    private final double rotate;
    private final double skewAngle;
    private final double scaleX;
    private final double scaleY;

    public Transform2DAngular(double transX, double transY, 
            double rotate, double skewAngle,
            double scaleX, double scaleY)
    {
        this.transX = transX;
        this.transY = transY;
        this.rotate = rotate;
        this.skewAngle = skewAngle;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public Transform2DAngular(double transX, double transY)
    {
        this(transX, transY, 0, 90, 1, 1);
    }

    public Transform2DAngular()
    {
        this(0, 0);
    }

    public static Transform2DAngular create(Transform2D m)
    {
        double scaleX = Math.sqrt(m.m00 * m.m00 + m.m10 * m.m10);
        double scaleY = Math.sqrt(m.m01 * m.m01 + m.m11 * m.m11);
        double rotate = Math.atan2(m.m10, m.m00);
        double skewAngle = Math.atan2(m.m11, m.m01) - rotate;

        return new Transform2DAngular(m.m02, m.m12,
                Math.toDegrees(rotate), Math.toDegrees(skewAngle),
                scaleX, scaleY);
    }

    public static Transform2DAngular create(AffineTransform xform)
    {
        double[] m = new double[6];
        xform.getMatrix(m);
        double scaleX = Math.sqrt(m[0] * m[0] + m[1] * m[1]);
        double scaleY = Math.sqrt(m[2] * m[2] + m[3] * m[3]);
        double rotate = Math.atan2(m[1], m[0]);
        double skewAngle = Math.atan2(m[3], m[2]) - rotate;

        return new Transform2DAngular(m[4], m[5],
                Math.toDegrees(rotate), Math.toDegrees(skewAngle),
                scaleX, scaleY);
    }

//    public AffineTransform toAffineTransform()
//    {
//        double sinx = Math.sin(Math.toRadians(rotate));
//        double cosx = Math.cos(Math.toRadians(rotate));
//        double siny = Math.sin(Math.toRadians(rotate + skewAngle));
//        double cosy = Math.cos(Math.toRadians(rotate + skewAngle));
//
//        return new AffineTransform(cosx * scaleX, sinx * scaleX,
//                cosy * scaleY, siny * scaleY,
//                transX, transY);
//    }

    public static Transform2DAngular create(CacheList list)
    {
        float transX = list.getFloat(0, 0);
        float transY = list.getFloat(1, 0);
        float rotate = list.getFloat(2, 0);
        float skewAngle = list.getFloat(3, 90);
        float scaleX = list.getFloat(4, 1);
        float scaleY = list.getFloat(5, 1);

        return new Transform2DAngular(transX, transY, rotate, skewAngle, scaleX, scaleY);
    }

    public static Transform2DAngular create(CacheElement ele)
    {
        if (ele instanceof CacheList)
        {
            return create(ele);
        }
        return new Transform2DAngular();
    }

    public CacheList toCache()
    {
        CacheList list = new CacheList();

        list.add((float)transX);
        list.add((float)transY);
        list.add((float)rotate);
        list.add((float)skewAngle);
        list.add((float)scaleX);
        list.add((float)scaleY);

        return list;
    }

    public boolean isIdentity()
    {
        return transX == 0 && transY == 0
                && rotate == 0 && skewAngle == 90
                && scaleX == 1 && scaleY == 1;
    }

    /**
     * @return the transX
     */
    public double getTransX() {
        return transX;
    }

    /**
     * @return the transY
     */
    public double getTransY() {
        return transY;
    }

    /**
     * @return the rotate
     */
    public double getRotate() {
        return rotate;
    }

    /**
     * @return the skewAngle
     */
    public double getSkewAngle() {
        return skewAngle;
    }

    /**
     * @return the scaleX
     */
    public double getScaleX() {
        return scaleX;
    }

    /**
     * @return the scaleY
     */
    public double getScaleY() {
        return scaleY;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Transform2DAngular other = (Transform2DAngular) obj;
        if (Double.doubleToLongBits(this.transX) != Double.doubleToLongBits(other.transX)) {
            return false;
        }
        if (Double.doubleToLongBits(this.transY) != Double.doubleToLongBits(other.transY)) {
            return false;
        }
        if (Double.doubleToLongBits(this.rotate) != Double.doubleToLongBits(other.rotate)) {
            return false;
        }
        if (Double.doubleToLongBits(this.skewAngle) != Double.doubleToLongBits(other.skewAngle)) {
            return false;
        }
        if (Double.doubleToLongBits(this.scaleX) != Double.doubleToLongBits(other.scaleX)) {
            return false;
        }
        if (Double.doubleToLongBits(this.scaleY) != Double.doubleToLongBits(other.scaleY)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.transX) ^ (Double.doubleToLongBits(this.transX) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.transY) ^ (Double.doubleToLongBits(this.transY) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.rotate) ^ (Double.doubleToLongBits(this.rotate) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.skewAngle) ^ (Double.doubleToLongBits(this.skewAngle) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.scaleX) ^ (Double.doubleToLongBits(this.scaleX) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.scaleY) ^ (Double.doubleToLongBits(this.scaleY) >>> 32));
        return hash;
    }



}
