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

import com.kitfox.cache.CacheMap;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector2d;

/**
 *
 * @author kitfox
 */
@Deprecated
public class PaintLayoutRadial extends PaintLayoutAbstract
{
    public static final String CACHE_NAME = "radial";
    public static final String PROP_CENTERX = "cx";
    public static final String PROP_CENTERY = "cy";
    public static final String PROP_RADIUSX = "rx";
    public static final String PROP_RADIUSY = "ry";
    public static final String PROP_ANGLE = "rot";
    public static final String PROP_SKEW = "skew";
    public static final String PROP_FOCUSX = "fx";
    public static final String PROP_FOCUSY = "fy";
    private final float centerX;
    private final float centerY;
    private final float radiusX;
    private final float radiusY;
    private final float angle;
    private final float skewAngle;
    private final float focusX;
    private final float focusY;

    public PaintLayoutRadial()
    {
        this(.5f, .5f, .5f, 0, 0, 90, .5f, .5f);
    }

    public PaintLayoutRadial(float centerX, float centerY,
            float radiusX, float radiusY,
            float angle, float skewAngle,
            float focusX, float focusY)
    {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radiusX = radiusX;
        this.radiusY = radiusY;
        this.angle = angle;
        this.skewAngle = skewAngle;
        this.focusX = focusX;
        this.focusY = focusY;
    }

    public PaintLayoutRadial(float centerX, float centerY,
            float radiusX, float radiusY,
            float angle, float skewAngle)
    {
        this(centerX, centerY, radiusX, radiusY, angle, skewAngle, centerX, centerY);
    }

    public PaintLayoutRadial(CacheMap map)
    {
        this(
            map.getFloat(PROP_CENTERX, 0),
            map.getFloat(PROP_CENTERY, 0),
            map.getFloat(PROP_RADIUSX, 1),
            map.getFloat(PROP_RADIUSY, 1),
            map.getFloat(PROP_ANGLE, 0),
            map.getFloat(PROP_SKEW, 90),
            map.getFloat(PROP_FOCUSX, 0),
            map.getFloat(PROP_FOCUSY, 0)
                );
    }

    public static PaintLayoutRadial create(PaintLayout layout)
    {
        CyMatrix4d xform = layout.getPaintToLocalTransform();
        CyVector2d focus = layout.getFocusLocal();

        return create(xform, focus);
    }

    public static PaintLayoutRadial create(CyMatrix4d paintToLocal, CyVector2d focusPaint)
    {
        double m00 = paintToLocal.m00;
        double m10 = paintToLocal.m10;
        double m01 = paintToLocal.m01;
        double m11 = paintToLocal.m11;
        double m02 = paintToLocal.m02;
        double m12 = paintToLocal.m12;

        double transX = m02;
        double transY = m12;
        double scaleX = (double)Math.sqrt(m00 * m00 + m10 * m10);
        double scaleY = (double)Math.sqrt(m01 * m01 + m11 * m11);
        double angle = (double)Math.toDegrees(Math.atan2(m10, m00));
        double skewAngle = (double)Math.toDegrees(Math.atan2(m11, m01)) - angle;

        return new PaintLayoutRadial(
                (float)(transX + (m00 + m01) / 2), (float)(transY + (m10 + m11) / 2),
                (float)(scaleX / 2), (float)(scaleY / 2),
                (float)angle, (float)skewAngle,
                (float)focusPaint.getX(), (float)focusPaint.getY());
    }

    @Override
    protected CyMatrix4d createPaintToLocalTransform()
    {
        //Paint circle will be centered on [0 1] unit square.  Find transform
        // that maps paint square to world space.
        //Similar to calculation in RavenNodeSpatial

        //Coordinates given either in local or world space.
        // If in local space, xform will have the localToWorld xform.
        float sinx = (float)Math.sin(Math.toRadians(angle));
        float cosx = (float)Math.cos(Math.toRadians(angle));
        float siny = (float)Math.sin(Math.toRadians(angle + skewAngle));
        float cosy = (float)Math.cos(Math.toRadians(angle + skewAngle));

        //Basis for top right quadrant
        float ix = cosx * radiusX;
        float iy = sinx * radiusX;
        float jx = cosy * radiusY;
        float jy = siny * radiusY;

        //Transform basis onto [-1 1] unit square
        return new CyMatrix4d(
                ix * 2, iy * 2, 0, 0,
                jx * 2, jy * 2, 0, 0,
                0, 0, 1, 0,
                centerX - ix - jx, centerY - iy - jy, 0, 1);
    }

    @Override
    public CyVector2d getFocusLocal()
    {
        CyVector2d focus = new CyVector2d(focusX, focusY);
        return focus;
    }

    @Override
    public CacheMap toCache()
    {
        CacheMap map = new CacheMap(CACHE_NAME);

        map.put(PROP_CENTERX, centerX);
        map.put(PROP_CENTERY, centerY);
        map.put(PROP_RADIUSX, radiusX);
        map.put(PROP_RADIUSY, radiusY);
        map.put(PROP_FOCUSX, focusX);
        map.put(PROP_FOCUSY, focusY);
        map.put(PROP_ANGLE, angle);
        map.put(PROP_SKEW, skewAngle);

        return map;
    }

    @Override
    public String toString()
    {
        return toCache().toString();
    }

    /**
     * @return the centerX
     */
    public float getCenterX()
    {
        return centerX;
    }

    /**
     * @return the centerY
     */
    public float getCenterY()
    {
        return centerY;
    }

    /**
     * @return the radiusX
     */
    public float getRadiusX()
    {
        return radiusX;
    }

    /**
     * @return the radiusY
     */
    public float getRadiusY()
    {
        return radiusY;
    }

    /**
     * @return the angle
     */
    public float getAngle()
    {
        return angle;
    }

    /**
     * @return the skewAngle
     */
    public float getSkewAngle()
    {
        return skewAngle;
    }

    /**
     * @return the focusX
     */
    public float getFocusX()
    {
        return focusX;
    }

    /**
     * @return the focusY
     */
    public float getFocusY()
    {
        return focusY;
    }

    @Override
    public PaintLayoutRadial transform(CyMatrix4d l2w)
    {
        CyMatrix4d p2l = getPaintToLocalTransform();

        CyMatrix4d p2w = new CyMatrix4d(l2w);
        p2w.mul(p2l);

        CyVector2d focus = getFocusLocal();
        l2w.transformPoint(focus, focus);

        return create(p2w, focus);
    }
}
