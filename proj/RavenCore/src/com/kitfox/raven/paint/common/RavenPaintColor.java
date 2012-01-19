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

package com.kitfox.raven.paint.common;

import com.kitfox.cache.CacheElement;
import com.kitfox.cache.CacheList;
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.raven.paint.RavenPaint;
import com.kitfox.raven.paint.RavenPaintProvider;
import com.kitfox.raven.util.service.ServiceInst;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;
import java.beans.PropertyEditor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class RavenPaintColor implements RavenPaint
{
    public static final String CACHE_NAME = "color";

    public static final String PROP_R = "r";
    public static final String PROP_G = "g";
    public static final String PROP_B = "b";
    public static final String PROP_A = "a";

    //CyColor4f color;
    public final float r;
    public final float g;
    public final float b;
    public final float a;

    public static final RavenPaintColor CLEAR = new RavenPaintColor(1, 1, 1, 0);
    
    public static final RavenPaintColor BLACK = new RavenPaintColor(0, 0, 0, 1);
    public static final RavenPaintColor WHITE = new RavenPaintColor(1, 1, 1, 1);
    
    
    public RavenPaintColor(float r, float g, float b)
    {
        this(r, g, b, 1);
    }

    public RavenPaintColor(float r, float g, float b, float a)
    {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public RavenPaintColor(CyColor4f color)
    {
        this(color.r, color.g, color.b, color.a);
    }
    
    public static RavenPaintColor create(String text)
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
            Logger.getLogger(RavenPaintColor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static RavenPaintColor create(CacheList list)
    {
        if (!CACHE_NAME.equals(list.getName()))
        {
            return null;
        }

        float r = list.getFloat(0, 0);
        float g = list.getFloat(1, 0);
        float b = list.getFloat(2, 0);
        float a = list.getFloat(3, 1);

        return new RavenPaintColor(r, g, b, a);
    }
    
    public int getARGB()
    {
        int aa = (int)(a * 255) & 0xff;
        int rr = (int)(r * 255) & 0xff;
        int gg = (int)(g * 255) & 0xff;
        int bb = (int)(b * 255) & 0xff;
        return (aa << 24) | (rr << 16) | (gg << 8) | bb;
    }
    
    public CyColor4f asColor()
    {
        return new CyColor4f(r, g, b, a);
    }

    public Color asColorAWT()
    {
        return new Color(r, g, b, a);
    }
    
    @Override
    public Paint getPaintSwatch(Rectangle box)
    {
        return asColorAWT();
    }

    /**
     * @return the r
     */
    public float getR()
    {
        return r;
    }

    /**
     * @return the g
     */
    public float getG()
    {
        return g;
    }

    /**
     * @return the b
     */
    public float getB()
    {
        return b;
    }

    /**
     * @return the a
     */
    public float getA()
    {
        return a;
    }

    public CacheList toCache()
    {
        CacheList map = new CacheList(CACHE_NAME);

        map.add(r);
        map.add(g);
        map.add(b);
        if (a != 1)
        {
            map.add(a);
        }

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
        final RavenPaintColor other = (RavenPaintColor) obj;
        if (Float.floatToIntBits(this.r) != Float.floatToIntBits(other.r))
        {
            return false;
        }
        if (Float.floatToIntBits(this.g) != Float.floatToIntBits(other.g))
        {
            return false;
        }
        if (Float.floatToIntBits(this.b) != Float.floatToIntBits(other.b))
        {
            return false;
        }
        if (Float.floatToIntBits(this.a) != Float.floatToIntBits(other.a))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 83 * hash + Float.floatToIntBits(this.r);
        hash = 83 * hash + Float.floatToIntBits(this.g);
        hash = 83 * hash + Float.floatToIntBits(this.b);
        hash = 83 * hash + Float.floatToIntBits(this.a);
        return hash;
    }
    
    //--------------------------------------
    @ServiceInst(service=RavenPaintProvider.class)
    public static class Provider extends RavenPaintProvider<RavenPaintColor>
    {
//        RavenPaintColorEditor ed = new RavenPaintColorEditor();
        
        public Provider()
        {
            super("Color", RavenPaintColor.class);
        }

        @Override
        public boolean canParse(String text)
        {
            return text.startsWith(CACHE_NAME);
        }

        @Override
        public RavenPaintColor fromText(String text)
        {
            return create(text);
        }

        @Override
        public String asText(RavenPaintColor value)
        {
            return value.toString();
        }

        @Override
        public PropertyEditor createEditor()
        {
            return new RavenPaintColorEditor();
//          return ed;
        }

        @Override
        public RavenPaintColor getDefaultValue()
        {
            return RavenPaintColor.BLACK;
        }
    }
}
