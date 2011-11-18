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

import java.util.Arrays;

/**
 *
 * @author kitfox
 */
public class CyGradientStops
{
//    public static final String CACHE_NAME = "gradient";
//    public static final String PROP_COLORSPACE = "colorSpace";
//    public static final String PROP_CYCLE = "cycle";
//    public static final String PROP_STYLE = "style";
//    public static final String PROP_OFFSET = "offset";
//    public static final String PROP_COLOR = "color";

    private float[] fractions;
    private CyColor4f[] colors;
    private final Cycle cycleMethod;
    private final Style style;
    //private final ColorSpaceType colorSpace;

    public CyGradientStops()
    {
        this(new float[]{0, 1},
                new CyColor4f[]{CyColor4f.BLACK, CyColor4f.WHITE},
                Cycle.NO_CYCLE, Style.LINEAR);
    }

    public CyGradientStops(float[] fractions, CyColor4f[] colors,
            Cycle cycleMethod, Style style)
    {
        this.fractions = fractions;
        this.colors = colors;
        this.cycleMethod = cycleMethod;
        this.style = style;
//        this.colorSpace = colorSpace;
    }

//    public CyGradientStops(CacheMap map)
//    {
//        this(loadOffsets(map), loadColors(map), loadCycle(map),
//                loadStyle(map), loadColorSpace(map));
//
//    }
//
//    public static CyGradientStops create(String text)
//    {
//        try {
//            CacheElement ele = CacheParser.parse(text);
//            if (!(ele instanceof CacheMap))
//            {
//                return null;
//            }
//            return new CyGradientStops((CacheMap)ele);
//        } catch (ParseException ex) {
//            return null;
//        }
//    }
//
//    protected static Cycle loadCycle(CacheMap map)
//    {
//        try
//        {
//            return Cycle.valueOf(map.getIdentifierName(PROP_CYCLE,
//                    Cycle.NO_CYCLE.name()));
//        }
//        catch (IllegalArgumentException ex)
//        {
//            return Cycle.NO_CYCLE;
//        }
//    }
//
//    protected static Style loadStyle(CacheMap map)
//    {
//        try
//        {
//            return Style.valueOf(map.getIdentifierName(PROP_STYLE,
//                    Style.LINEAR.name()));
//        }
//        catch (IllegalArgumentException ex)
//        {
//            return Style.LINEAR;
//        }
//    }
//
//    protected static ColorSpaceType loadColorSpace(CacheMap map)
//    {
//        try
//        {
//            return ColorSpaceType.valueOf(map.getIdentifierName(PROP_COLORSPACE,
//                    ColorSpaceType.SRGB.name()));
//        }
//        catch (IllegalArgumentException ex)
//        {
//            return ColorSpaceType.SRGB;
//        }
//    }
//
//    protected static ColorStyle[] loadColors(CacheMap map)
//    {
//        CacheList list = (CacheList)map.get(PROP_COLOR);
//        ColorStyle[] arr = new ColorStyle[list.size()];
//        for (int i = 0; i < arr.length; ++i)
//        {
//            arr[i] = ColorStyleEditor.create(list.get(i));
//        }
//        return arr;
//    }
//
//    protected static float[] loadOffsets(CacheMap map)
//    {
//        CacheList list = (CacheList)map.get(PROP_OFFSET);
//        return list.toFloatArray(0);
//    }
//
//    public CacheMap toCache()
//    {
//        CacheMap map = new CacheMap(CACHE_NAME);
//
//        {
//            ColorSpaceType value = getColorSpace();
//            if (value != ColorSpaceType.SRGB)
//            {
//                map.put(PROP_COLORSPACE, new CacheIdentifier(value.name()));
//            }
//        }
//        {
//            Cycle cycle = getCycleMethod();
//            if (cycle != Cycle.NO_CYCLE)
//            {
//                map.put(PROP_CYCLE, new CacheIdentifier(cycle.name()));
//            }
//        }
//        {
//            Style value = getStyle();
//            if (value != Style.LINEAR)
//            {
//                map.put(PROP_STYLE, new CacheIdentifier(value.name()));
//            }
//        }
//
//        CacheList colorList = new CacheList();
//        ColorStyle[] colStyleList = getColors();
//        if (colStyleList != null)
//        {
//            for (ColorStyle color: colStyleList)
//            {
//                colorList.add(ColorStyleEditor.toCache(color));
//            }
//        }
//        map.put(PROP_COLOR, colorList);
//
//        CacheList offsetList = new CacheList();
//        float[] fractList = getFractions();
//        if (fractList != null)
//        {
//            for (float offset: fractList)
//            {
//                offsetList.add(offset);
//            }
//        }
//        map.put(PROP_OFFSET, offsetList);
//
//        return map;
//    }

//    private static ColorStyle[] toStyle(Color[] colors)
//    {
//        ColorStyle[] style = new ColorStyle[colors.length];
//        for (int i = 0; i < colors.length; ++i)
//        {
//            style[i] = new ColorStyle(colors[i]);
//        }
//        return style;
//    }

    /**
     * @return the fractions
     */
    public float[] getFractions()
    {
        return fractions == null ? null : fractions.clone();
    }

    /**
     * @return the colors
     */
    public CyColor4f[] getColors()
    {
        return colors == null ? null : colors.clone();
    }

    /**
     * @return the cycleMethod
     */
    public Cycle getCycleMethod()
    {
        return cycleMethod;
    }

//    /**
//     * @return the colorSpace
//     */
//    public ColorSpaceType getColorSpace()
//    {
//        return colorSpace;
//    }

//    @Override
//    public String toString()
//    {
//        return toCache().toString();
//    }

    /**
     * @return the style
     */
    public Style getStyle()
    {
        return style;
    }

    /**
     * Sample this gradient at given stop fraction.  Ignores cycle
     * information.
     *
     * @param fraction
     * @param color
     * @return
     */
    public float[] sampleRaw(float fraction, float[] color)
    {
        if (color == null)
        {
            color = new float[4];
        }

        int maxIdx = 0;
        for (; maxIdx < fractions.length; ++maxIdx)
        {
            if (fractions[maxIdx] > fraction)
            {
                break;
            }
        }

        if (maxIdx == 0)
        {
            //Before first stop
            colors[0].getColor(color);
        }
        else if (maxIdx == fractions.length)
        {
            //After last stop
            colors[colors.length - 1].getColor(color);
        }
        else
        {
            float t = (fraction - fractions[maxIdx - 1])
                    / (fractions[maxIdx] - fractions[maxIdx - 1]);
            CyColor4f c0 = colors[maxIdx - 1];
            CyColor4f c1 = colors[maxIdx];

            color[0] = (1 - t) * c0.r + t * c1.r;
            color[1] = (1 - t) * c0.g + t * c1.g;
            color[2] = (1 - t) * c0.b + t * c1.b;
            color[3] = (1 - t) * c0.a + t * c1.a;
        }

        return color;
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
        final CyGradientStops other = (CyGradientStops) obj;
        if (!Arrays.equals(this.fractions, other.fractions))
        {
            return false;
        }
        if (!Arrays.deepEquals(this.colors, other.colors))
        {
            return false;
        }
        if (this.cycleMethod != other.cycleMethod)
        {
            return false;
        }
        if (this.style != other.style)
        {
            return false;
        }
//        if (this.colorSpace != other.colorSpace)
//        {
//            return false;
//        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 23 * hash + Arrays.hashCode(this.fractions);
        hash = 23 * hash + Arrays.deepHashCode(this.colors);
        hash = 23 * hash + (this.cycleMethod != null ? this.cycleMethod.hashCode() : 0);
        hash = 23 * hash + (this.style != null ? this.style.hashCode() : 0);
//        hash = 23 * hash + (this.colorSpace != null ? this.colorSpace.hashCode() : 0);
        return hash;
    }


    //---------------------------------------

    public static enum Style { LINEAR, RADIAL }
    public static enum Cycle { NO_CYCLE, REFLECT, REPEAT }
}
