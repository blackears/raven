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

package com.kitfox.coyote.math;

/**
 *
 * @author kitfox
 */
public class CyColor4f
{    
    public static final CyColor4f BLACK = new CyColor4f(0, 0, 0, 1);
    public static final CyColor4f RED = new CyColor4f(1, 0, 0, 1);
    public static final CyColor4f GREEN = new CyColor4f(0, 1, 0, 1);
    public static final CyColor4f YELLOW = new CyColor4f(1, 1, 0, 1);
    public static final CyColor4f BLUE = new CyColor4f(0, 0, 1, 1);
    public static final CyColor4f MAGENTA = new CyColor4f(1, 0, 1, 1);
    public static final CyColor4f CYAN = new CyColor4f(0, 1, 1, 1);
    public static final CyColor4f WHITE = new CyColor4f(1, 1, 1, 1);

    public static final CyColor4f GREY = new CyColor4f(.5f, .5f, .5f, 1);
    public static final CyColor4f LIGHT_GREY = new CyColor4f(.75f, .75f, .75f, 1);
    public static final CyColor4f DARK_GREY = new CyColor4f(.25f, .25f, .25f, 1);
    
    public static final CyColor4f TRANSPARENT = new CyColor4f(0, 0, 0, 0);

    public static final CyColor4f ORANGE = new CyColor4f(1, .5f, 0, 1);
    
    public final float r;
    public final float g;
    public final float b;
    public final float a;

    public CyColor4f(float r, float g, float b, float a)
    {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public CyColor4f(double r, double g, double b, double a)
    {
        this((float)r, (float)g, (float)b, (float)a);
    }

    public static CyColor4f randomRGB()
    {
        return new CyColor4f(Math.random(), Math.random(), Math.random(), 1);
    }

    public void getColor(float[] color)
    {
        if (color == null)
        {
            color = new float[4];
        }

        color[0] = r;
        color[1] = g;
        color[2] = b;
        color[3] = a;
    }

    public static CyColor4f parseWebColor(String text)
    {
        if (text.charAt(0) == '#')
        {
            text = text.substring(1);
        }
        
        int rr = Integer.parseInt(text.substring(0, 2), 16);
        int gg = Integer.parseInt(text.substring(2, 4), 16);
        int bb = Integer.parseInt(text.substring(4, 6), 16);
        
        return new CyColor4f(rr / 255f, gg / 255f, bb / 255f, 1);
    }
    
    public String toWebColor()
    {
        int rr = (int)((r + .5f) * 255);
        int gg = (int)((g + .5f) * 255);
        int bb = (int)((b + .5f) * 255);

        return String.format("#%02x%02x%02x", rr, gg, bb);
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
        final CyColor4f other = (CyColor4f) obj;
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
        int hash = 5;
        hash = 19 * hash + Float.floatToIntBits(this.r);
        hash = 19 * hash + Float.floatToIntBits(this.g);
        hash = 19 * hash + Float.floatToIntBits(this.b);
        hash = 19 * hash + Float.floatToIntBits(this.a);
        return hash;
    }

    @Override
    public String toString()
    {
        return "(" + r + ", " + g + ", " + b + ", " + a + ")";
    }

}
