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
import com.kitfox.coyote.shape.CyRectangle2d;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author kitfox
 */
@Deprecated
public class PaintLayoutTexture extends PaintLayoutAbstract
{
    public static final String CACHE_NAME = "matrix";
    public static final String PROP_TRANSX = "tx";
    public static final String PROP_TRANSY = "ty";
    public static final String PROP_SCALEX = "sx";
    public static final String PROP_SCALEY = "sy";
    public static final String PROP_ANGLE = "rot";
    public static final String PROP_SKEW = "skew";
    private final double transX;
    private final double transY;
    private final double scaleX;
    private final double scaleY;
    private final double angle;
    private final double skewAngle;

    public PaintLayoutTexture()
    {
        this(0, 0, 1, 1, 0, 90);
    }

    public PaintLayoutTexture(
            double transX, double transY,
            double scaleX, double scaleY,
            double angle, double skewAngle)
    {
        this.transX = transX;
        this.transY = transY;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.angle = angle;
        this.skewAngle = skewAngle;
    }

    public PaintLayoutTexture(CyRectangle2d box)
    {
        this(box.getX(), box.getY(),
                box.getWidth(), box.getHeight(), 0, 90);
    }

    public PaintLayoutTexture(Rectangle2D box)
    {
        this(box.getX(), box.getY(),
                box.getWidth(), box.getHeight(), 0, 90);
    }

    public PaintLayoutTexture(CacheMap map)
    {
        this(
            map.getFloat(PROP_TRANSX, 0),
            map.getFloat(PROP_TRANSY, 0),
            map.getFloat(PROP_SCALEX, 1),
            map.getFloat(PROP_SCALEY, 1),
            map.getFloat(PROP_ANGLE, 0),
            map.getFloat(PROP_SKEW, 90)
                );
    }

    public static PaintLayoutTexture create(PaintLayout layout)
    {
        CyMatrix4d xform = layout.getPaintToLocalTransform();
        return create(xform);
    }

    public static PaintLayoutTexture create(CyMatrix4d paintToLocal)
    {
        double m00 = paintToLocal.m00;
        double m10 = paintToLocal.m10;
        double m01 = paintToLocal.m01;
        double m11 = paintToLocal.m11;
        double m03 = paintToLocal.m03;
        double m13 = paintToLocal.m13;

        double transX = m03;
        double transY = m13;
        double scaleX = (float)Math.sqrt(m00 * m00 + m10 * m10);
        double scaleY = (float)Math.sqrt(m01 * m01 + m11 * m11);
        double angle = (float)Math.toDegrees(Math.atan2(m10, m00));
        double skewAngle = (float)Math.toDegrees(Math.atan2(m11, m01)) - angle;

//        while (skewAngle < angle)
//        {
//            skewAngle += 360;
//        }

        return new PaintLayoutTexture(
                transX, transY,
                scaleX, scaleY,
                angle, skewAngle);
    }

    @Override
    protected CyMatrix4d createPaintToLocalTransform()
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

    @Override
    public CyVector2d getFocusLocal()
    {
        CyMatrix4d p2l = getPaintToLocalTransform();
        CyVector2d focus = new CyVector2d(.5, .5);
        p2l.transformPoint(focus, focus);
        return focus;
    }

    @Override
    public CacheMap toCache()
    {
        CacheMap map = new CacheMap(CACHE_NAME);

        map.put(PROP_TRANSX, (float)transX);
        map.put(PROP_TRANSY, (float)transY);
        map.put(PROP_SCALEX, (float)scaleX);
        map.put(PROP_SCALEY, (float)scaleY);
        map.put(PROP_ANGLE, (float)angle);
        map.put(PROP_SKEW, (float)skewAngle);

        return map;
    }

    @Override
    public String toString()
    {
        return toCache().toString();
    }

    /**
     * @return the transX
     */
    public double getTransX()
    {
        return transX;
    }

    /**
     * @return the transY
     */
    public double getTransY()
    {
        return transY;
    }

    /**
     * @return the scaleX
     */
    public double getScaleX()
    {
        return scaleX;
    }

    /**
     * @return the scaleY
     */
    public double getScaleY()
    {
        return scaleY;
    }

    /**
     * @return the angle
     */
    public double getAngle()
    {
        return angle;
    }

    /**
     * @return the skewAngle
     */
    public double getSkewAngle()
    {
        return skewAngle;
    }

    @Override
    public PaintLayoutTexture transform(CyMatrix4d l2w)
    {
        CyMatrix4d p2l = createPaintToLocalTransform();

        CyMatrix4d p2w = new CyMatrix4d(l2w);
        p2w.mul(p2l);

        return create(p2w);
    }
    
}
