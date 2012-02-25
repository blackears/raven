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
import com.kitfox.cache.CacheMap;
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.raven.paint.common.RavenPaintColor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
@Deprecated
public class RavenTexTransform
{
    public static final String CACHE_NAME = "matrix";
    
    private final CyMatrix4d xform;

    public RavenTexTransform(CyMatrix4d xform)
    {
        this.xform = new CyMatrix4d(xform);
    }

    /**
     * @return the xform
     */
    public CyMatrix4d getXform()
    {
        return new CyMatrix4d(xform);
    }

    /**
     * @return the xform
     */
    public CyMatrix4d getXform(CyMatrix4d m)
    {
        m.set(xform);
        return m;
    }
    
    public static RavenTexTransform create(String text)
    {
        try
        {
            CacheElement ele = CacheParser.parse(text);
            if (!(ele instanceof CacheMap))
            {
                return null;
            }
            
            CacheList list = (CacheList)ele;
            return create(list);
        } catch (ParseException ex)
        {
            Logger.getLogger(RavenPaintColor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static RavenTexTransform create(CacheList list)
    {
        if (!CACHE_NAME.equals(list.getName()))
        {
            return null;
        }

        CyMatrix4d m = new CyMatrix4d();
        m.m00 = list.getFloat(0, 1);
        m.m10 = list.getFloat(1, 0);
        m.m20 = list.getFloat(2, 0);
        m.m30 = list.getFloat(3, 0);
        
        m.m01 = list.getFloat(4, 0);
        m.m11 = list.getFloat(5, 1);
        m.m21 = list.getFloat(6, 0);
        m.m31 = list.getFloat(7, 0);
        
        m.m02 = list.getFloat(8, 0);
        m.m12 = list.getFloat(9, 0);
        m.m22 = list.getFloat(10, 1);
        m.m32 = list.getFloat(11, 0);
        
        m.m03 = list.getFloat(12, 0);
        m.m13 = list.getFloat(13, 0);
        m.m23 = list.getFloat(14, 0);
        m.m33 = list.getFloat(15, 1);
        
        return new RavenTexTransform(m);
    }

    public CacheList toCache()
    {
        CacheList list = new CacheList(CACHE_NAME);

        //Column major
        list.add((float)xform.m00);
        list.add((float)xform.m10);
        list.add((float)xform.m20);
        list.add((float)xform.m30);

        list.add((float)xform.m01);
        list.add((float)xform.m11);
        list.add((float)xform.m21);
        list.add((float)xform.m31);

        list.add((float)xform.m02);
        list.add((float)xform.m12);
        list.add((float)xform.m22);
        list.add((float)xform.m32);

        list.add((float)xform.m03);
        list.add((float)xform.m13);
        list.add((float)xform.m23);
        list.add((float)xform.m33);

        return list;
    }

    @Override
    public String toString()
    {
        return toCache().toString();
    }
    
}
