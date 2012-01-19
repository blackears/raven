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
import com.kitfox.cache.CacheMap;
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import com.kitfox.coyote.shape.CyStroke;
import com.kitfox.coyote.shape.CyStrokeCap;
import com.kitfox.coyote.shape.CyStrokeJoin;
import com.kitfox.raven.paint.common.RavenPaintColor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class RavenStroke
{
    public static final String CACHE_NAME = "stroke";

    public static final String PROP_WIDTH = "width";
    public static final String PROP_CAP = "cap";
    public static final String PROP_JOIN = "join";
    public static final String PROP_MITER = "miter";
    public static final String PROP_DASH = "dash";
    public static final String PROP_OFFSET = "offset";
    
    private final CyStroke stroke;

    public RavenStroke()
    {
        this(new CyStroke(1));
    }

    public RavenStroke(CyStroke stroke)
    {
        this.stroke = stroke;
    }

    /**
     * @return the stroke
     */
    public CyStroke getStroke()
    {
        return stroke;
    }
    
    public static RavenStroke create(String text)
    {
        try
        {
            CacheElement ele = CacheParser.parse(text);
            if (!(ele instanceof CacheMap))
            {
                return null;
            }
            
            CacheMap list = (CacheMap)ele;
            return create(list);
        } catch (ParseException ex)
        {
            Logger.getLogger(RavenPaintColor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static RavenStroke create(CacheMap map)
    {
        if (!CACHE_NAME.equals(map.getName()))
        {
            return null;
        }

        float width = map.getFloat(PROP_WIDTH, 1);
        CyStrokeCap cap = map.getEnum(PROP_CAP, CyStrokeCap.BUTT);
        CyStrokeJoin join = map.getEnum(PROP_JOIN, CyStrokeJoin.BEVEL);
        float miter = map.getFloat(PROP_MITER, 4);
        float[] dash = map.getFloatArray(PROP_DASH, null);
        float offset = map.getFloat(PROP_OFFSET, 0);

        CyStroke stroke = new CyStroke(width, cap, join, miter, dash, offset);

        return new RavenStroke(stroke);
    }

    public CacheMap toCache()
    {
        CacheMap map = new CacheMap(CACHE_NAME);

        map.put(PROP_WIDTH, (float)stroke.getWidth());
        map.put(PROP_CAP, stroke.getCap().name());
        map.put(PROP_JOIN, stroke.getJoin().name());
        map.put(PROP_MITER, (float)stroke.getMiterLimit());
        map.put(PROP_DASH, stroke.getDashFloat());
        map.put(PROP_OFFSET, (float)stroke.getDashOffset());

        return map;
    }

    @Override
    public String toString()
    {
        return toCache().toString();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final RavenStroke other = (RavenStroke) obj;
        if (this.stroke != other.stroke && (this.stroke == null || !this.stroke.equals(other.stroke)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 73 * hash + (this.stroke != null ? this.stroke.hashCode() : 0);
        return hash;
    }
    
}
