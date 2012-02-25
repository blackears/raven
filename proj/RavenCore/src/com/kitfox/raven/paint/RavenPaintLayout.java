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

package com.kitfox.raven.paint;

import com.kitfox.cache.CacheElement;
import com.kitfox.cache.CacheList;
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.shape.CyRectangle2d;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class RavenPaintLayout
{
    public static final String CACHE_NAME = "layout";

//    public static final String PROP_WIDTH = "matrix";
    
    private CyMatrix4d p2l;

    public RavenPaintLayout()
    {
        this(CyMatrix4d.createIdentity());
    }

    public RavenPaintLayout(CyMatrix4d xform)
    {
        this.p2l = xform;
    }

    public RavenPaintLayout(CyRectangle2d box)
    {
        this(CyMatrix4d.createComponents(box));
    }

    public RavenPaintLayout(
            double transX, double transY,
            double scaleX, double scaleY,
            double angle, double skewAngle)
    {
        this(CyMatrix4d.createComponents(transX, transY,
            scaleX, scaleY,
            angle, skewAngle));
    }

    public static RavenPaintLayout createLinear(CyVector2d startPt, CyVector2d endPt)
    {
        double ix = endPt.x - startPt.x;
        double iy = endPt.y - startPt.y;
        double jx = -iy;
        double jy = ix;

        //Transform basis onto [0 1] unit square
//        return new AffineTransform(
//                ix, iy,
//                jx, jy,
//                startX, startY);
        CyMatrix4d m = new CyMatrix4d(
                ix, iy, 0, 0,
                jx, jy, 0, 0,
                0, 0, 1, 0,
                startPt.x, startPt.y, 0, 1);
        return new RavenPaintLayout(m);
        
    }

    public static RavenPaintLayout createTexture2D(
            CyVector2d ptCenter, CyVector2d ptRadiusX, CyVector2d ptRadiusY)
    {
        CyMatrix4d m = new CyMatrix4d(
                ptRadiusX.x, ptRadiusX.y, 0, 0,
                ptRadiusY.x, ptRadiusY.y, 0, 0,
                0, 0, 1, 0,
                ptCenter.x, ptCenter.y, 0, 1);
        return new RavenPaintLayout(m);
        
    }

    /**
     * @return the xform
     */
    public CyMatrix4d getPaintToLocal()
    {
        return new CyMatrix4d(p2l);
    }
    
    public RavenPaintLayout transform(CyMatrix4d l2w)
    {
        CyMatrix4d p2w = new CyMatrix4d(l2w);
        p2w.mul(p2l);

        return new RavenPaintLayout(p2w);
    }

    /**
     * Find the points used for a linear paint layout.  These
     * correspond to the start and end points of the X basis vector.
     * 
     * @param startPt
     * @param endPt 
     */
    public void getLinearLayout(CyVector2d startPt, CyVector2d endPt)
    {
        startPt.set(p2l.m03, p2l.m13);
        endPt.set(p2l.m03 + p2l.m00, p2l.m13 + p2l.m10);
    }

    /**
     * Get the vital coords for a 2D texture layout.
     * 
     * @param ptCenter
     * @param ptRadiusX
     * @param ptRadiusY 
     */
    public void getTextureLayout(CyVector2d ptCenter, CyVector2d ptRadiusX, CyVector2d ptRadiusY)
    {
        ptCenter.set(p2l.m03, p2l.m13);
        ptRadiusX.set(p2l.m00, p2l.m10);
        ptRadiusY.set(p2l.m01, p2l.m11);
    }
    
    public static RavenPaintLayout create(String text)
    {
        try
        {
            CacheElement ele = CacheParser.parse(text);
            if (!(ele instanceof CacheList))
            {
                return null;
            }
            
            CacheList list = (CacheList)ele;
            return create(list);
        } catch (ParseException ex)
        {
            Logger.getLogger(RavenPaintLayout.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static RavenPaintLayout create(CacheList map)
    {
        if (!CACHE_NAME.equals(map.getName()))
        {
            return null;
        }

        
        float m00 = map.getFloat(0, 1);
        float m01 = map.getFloat(1, 0);
        float m02 = map.getFloat(2, 0);
        float m03 = map.getFloat(3, 0);
        
        float m10 = map.getFloat(4, 0);
        float m11 = map.getFloat(5, 1);
        float m12 = map.getFloat(6, 0);
        float m13 = map.getFloat(7, 0);
        
        float m20 = map.getFloat(8, 0);
        float m21 = map.getFloat(9, 0);
        float m22 = map.getFloat(10, 1);
        float m23 = map.getFloat(11, 0);
        
        float m30 = map.getFloat(12, 0);
        float m31 = map.getFloat(13, 0);
        float m32 = map.getFloat(14, 0);
        float m33 = map.getFloat(15, 1);
        

        CyMatrix4d m = new CyMatrix4d(m00, m10, m20, m30, 
                m01, m11, m21, m31, 
                m02, m12, m22, m32, 
                m03, m13, m23, m33);

        return new RavenPaintLayout(m);
    }

    public CacheList toCache()
    {
        CacheList list = new CacheList(CACHE_NAME);

        list.add((float)p2l.m00);
        list.add((float)p2l.m01);
        list.add((float)p2l.m02);
        list.add((float)p2l.m03);
        
        list.add((float)p2l.m10);
        list.add((float)p2l.m11);
        list.add((float)p2l.m12);
        list.add((float)p2l.m13);
        
        list.add((float)p2l.m20);
        list.add((float)p2l.m21);
        list.add((float)p2l.m22);
        list.add((float)p2l.m23);
        
        list.add((float)p2l.m30);
        list.add((float)p2l.m31);
        list.add((float)p2l.m32);
        list.add((float)p2l.m33);

        return list;
    }

    @Override
    public String toString()
    {
        return toCache().toString();
    }
    
    
}
