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

import com.kitfox.cache.CacheList;
import java.awt.Color;
import java.awt.geom.AffineTransform;

/**
 *
 * @author kitfox
 */
public class ColorStyle extends PaintStyle
{
    public static final String CACHE_NAME = "color";

    public static final String PROP_R = "r";
    public static final String PROP_G = "g";
    public static final String PROP_B = "b";
    public static final String PROP_A = "a";
    public final float r;
    public final float g;
    public final float b;
    public final float a;

    public static final ColorStyle BLACK = new ColorStyle(0, 0, 0);
    public static final ColorStyle WHITE = new ColorStyle(1, 1, 1);
    public static final ColorStyle ORANGE = new ColorStyle(1, .5f, 0);

    public ColorStyle()
    {
        this(0, 0, 0, 1);
    }

    public ColorStyle(Color color)
    {
        this(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public ColorStyle(int r, int g, int b, int a)
    {
        this(r / 255f, g / 255f, b / 255f, a / 255f);
    }

    public ColorStyle(float r, float g, float b)
    {
        this(r, g, b, 1);
    }

    public ColorStyle(float r, float g, float b, float a)
    {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public ColorStyle(CacheList map)
    {
        this(
                map.getFloat(0, 0),
                map.getFloat(1, 0),
                map.getFloat(2, 0),
                map.getFloat(3, 1)
                );
    }

    public static float mod2(float value)
    {
        float div = value / 2;
        return div - (float)Math.floor(div);
    }

    public static float min(float a, float b, float c)
    {
        return a < b && a < c ? a
                : (b < c ? b : c);
    }

    public static float max(float a, float b, float c)
    {
        return a > b && a > c ? a
                : (b > c ? b : c);
    }

    /**
     * http://www.cs.rit.edu/~ncs/color/t_convert.html
     * 
     * @param r
     * @param g
     * @param b
     * @return
     */
    public static float[] RGBtoHSV(float r, float g, float b, float[] result)
    {
        if (result == null)
        {
            result = new float[3];
        }

        float h, s, v;
        float min, max, delta;

        min = min( r, g, b );
        max = max( r, g, b );
        v = max;				// v

        delta = max - min;

        if (max != 0)
        {
            s = delta / max;		// s
        }
        else
        {
            // r = g = b = 0		// s = 0, v is undefined
            s = 0;
            h = -1;

//            result[0] = 0;
//            result[1] = 0;
            result[2] = 0;
            return result;
        }

        if( r == max )
            h = ( g - b ) / delta;		// between yellow & magenta
        else if( g == max )
            h = 2 + ( b - r ) / delta;	// between cyan & yellow
        else
            h = 4 + ( r - g ) / delta;	// between magenta & cyan

        h *= 60;				// degrees
        if (h < 0)
        {
            h += 360;
        }

        if (delta != 0)
        {
            //Only set hue if saturation is non-zero
            result[0] = h / 360;
        }
        result[1] = s;
        result[2] = v;
        return result;
    }

    /**
     * http://www.cs.rit.edu/~ncs/color/t_convert.html
     * 
     * @param h
     * @param s
     * @param v
     * @return
     */
    public static float[] HSVtoRGB(float h, float s, float v, float[] result)
    {
        if (result == null)
        {
            result = new float[3];
        }

        float r, g, b;
        int i;
        float f, p, q, t;

        if( s == 0 ) {
            // achromatic (grey)
            r = g = b = v;

            result[0] = r;
            result[1] = g;
            result[2] = b;
            return result;
        }

//        h /= 60;			// sector 0 to 5
        h *= 6;			// sector 0 to 5
        i = (int)Math.floor(h);
        f = h - i;			// factorial part of h
        p = v * ( 1 - s );
        q = v * ( 1 - s * f );
        t = v * ( 1 - s * ( 1 - f ) );

        switch( i ) {
            case 0:
                r = v;
                g = t;
                b = p;
                break;
            case 1:
                r = q;
                g = v;
                b = p;
                break;
            case 2:
                r = p;
                g = v;
                b = t;
                break;
            case 3:
                r = p;
                g = q;
                b = v;
                break;
            case 4:
                r = t;
                g = p;
                b = v;
                break;
            default:		// case 5:
                r = v;
                g = p;
                b = q;
                break;
        }

        result[0] = r;
        result[1] = g;
        result[2] = b;
        return result;
    }

    public static float[] RGBtoXYZ(float r, float g, float b)
    {
        float x = 0.412453f * r + 0.357580f * g + 0.180423f * b;
        float y = 0.212671f * r + 0.715160f * g + 0.072169f * b;
        float z = 0.019334f * r + 0.119193f * g + 0.950227f * b;

        return new float[]{x, y, z};
    }

    public static float[] XYZtoRGB(float x, float y, float z)
    {
        float r = 3.240479f * x + -1.537150f * y + -0.498535f * z;
        float g = -0.969256f * x + 1.875992f * y + 0.041556f * z;
        float b = 0.055648f * x + -0.204043f * y + 1.057311f * z;

        return new float[]{r, g, b};
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

    public Color getColor()
    {
        return new Color(r, g, b, a);
    }

    private int satColor(float value)
    {
        return Math.max(Math.min((int)(value * 255 + 0.5), 255), 0);
    }

    public int getRGB()
    {
        int rr = satColor(r);
        int gg = satColor(g);
        int bb = satColor(b);
        int aa = satColor(a);

        return ((aa & 0xff) << 24) |
                ((rr & 0xff) << 16) |
                ((gg & 0xff) << 8)  |
                ((bb & 0xff) << 0);
    }

    public boolean isTransparent()
    {
        return a < 1;
    }

    @Override
    public Color getPaint(PaintLayout layout, AffineTransform localToWorld)
    {
        return getColor();
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
//        return "rgba(" + r + " " + g + " " + b + " " + a + ")";
        return toCache().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ColorStyle other = (ColorStyle) obj;
        if (Float.floatToIntBits(this.r) != Float.floatToIntBits(other.r)) {
            return false;
        }
        if (Float.floatToIntBits(this.g) != Float.floatToIntBits(other.g)) {
            return false;
        }
        if (Float.floatToIntBits(this.b) != Float.floatToIntBits(other.b)) {
            return false;
        }
        if (Float.floatToIntBits(this.a) != Float.floatToIntBits(other.a)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Float.floatToIntBits(this.r);
        hash = 47 * hash + Float.floatToIntBits(this.g);
        hash = 47 * hash + Float.floatToIntBits(this.b);
        hash = 47 * hash + Float.floatToIntBits(this.a);
        return hash;
    }

    public void getColor(float[] color)
    {
        color[0] = r;
        color[1] = g;
        color[2] = b;
        color[3] = a;
    }


}
