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
public class PaintLayoutLinear extends PaintLayoutAbstract
{
    public static final String CACHE_NAME = "linear";
    public static final String PROP_STARTX = "sx";
    public static final String PROP_STARTY = "sy";
    public static final String PROP_ENDX = "ex";
    public static final String PROP_ENDY = "ey";
    private final double startX;
    private final double startY;
    private final double endX;
    private final double endY;

    public PaintLayoutLinear()
    {
        this(0, 0, 1, 0);
    }

    public PaintLayoutLinear(double startX, double startY, double endX, double endY)
    {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public static PaintLayoutLinear create(PaintLayout layout)
    {
        CyMatrix4d xform = layout.getPaintToLocalTransform();

//        float m00 = (float)xform.getScaleX();
//        float m10 = (float)xform.getShearY();
//        float m01 = (float)xform.getShearX();
//        float m11 = (float)xform.getScaleY();
//        float m02 = (float)xform.getTranslateX();
//        float m12 = (float)xform.getTranslateY();

        double transX = xform.m03;
        double transY = xform.m13;
//        float scaleX = (float)Math.sqrt(m00 * m00 + m10 * m10);
//        float scaleY = (float)Math.sqrt(m01 * m01 + m11 * m11);
//        float angle = (float)Math.toDegrees(Math.atan2(m10, m00));
//        float skewAngle = (float)Math.toDegrees(Math.atan2(m11, m01)) - angle;

        return new PaintLayoutLinear(
                transX, transY,
                transX + xform.m00, transY + xform.m10
                );
    }

    public PaintLayoutLinear(CacheMap map)
    {
        this(
            map.getFloat(PROP_STARTX, 0),
            map.getFloat(PROP_STARTY, 0),
            map.getFloat(PROP_ENDX, 1),
            map.getFloat(PROP_ENDY, 0)
                );
    }

    @Override
    protected CyMatrix4d createPaintToLocalTransform()
    {
        double ix = endX - startX;
        double iy = endY - startY;
        double jx = -iy;
        double jy = ix;

        //Transform basis onto [0 1] unit square
//        return new AffineTransform(
//                ix, iy,
//                jx, jy,
//                startX, startY);
        return new CyMatrix4d(
                ix, iy, 0, 0,
                jx, jy, 0, 0,
                0, 0, 1, 0,
                startX, startY, 0, 1);
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

        map.put(PROP_STARTX, (float)startX);
        map.put(PROP_STARTY, (float)startY);
        map.put(PROP_ENDX, (float)endX);
        map.put(PROP_ENDY, (float)endY);

        return map;
    }

    @Override
    public String toString()
    {
        return toCache().toString();
    }

    /**
     * @return the startX
     */
    public double getStartX()
    {
        return startX;
    }

    /**
     * @return the startY
     */
    public double getStartY()
    {
        return startY;
    }

    /**
     * @return the endX
     */
    public double getEndX()
    {
        return endX;
    }

    /**
     * @return the endY
     */
    public double getEndY()
    {
        return endY;
    }

    @Override
    public PaintLayoutLinear transform(CyMatrix4d l2w)
    {
        CyVector2d start = new CyVector2d(startX, startY);
        CyVector2d end = new CyVector2d(endX, endY);

        l2w.transformPoint(start, start);
        l2w.transformPoint(end, end);

        return new PaintLayoutLinear(start.x, start.y, end.x, end.y);
    }

    
}
