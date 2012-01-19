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
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector2d;
import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 *
 * @author kitfox
 */
@Deprecated
public class MultipleGradientStyle extends PaintStyle
{
//    protected final static AffineTransform identity = new AffineTransform();

    private final MultipleGradientStops stops;

    public MultipleGradientStyle(MultipleGradientStops stops)
    {
        this.stops = stops == null
                ? new MultipleGradientStops()
                : stops;
    }

    public MultipleGradientStyle(CacheMap cache)
    {
        this(new MultipleGradientStops(cache));
    }

    public MultipleGradientStyle(String text) throws ParseException
    {
        this((CacheMap)CacheParser.parse(text));
    }

    public CacheMap toCache()
    {
        return stops.toCache();
    }

    @Override
    public MultipleGradientPaint getPaint(PaintLayout layout, AffineTransform localToWorld)
    {
        ColorStyle[] colors = stops.getColors();
        Color[] colorList;
        if (colors == null)
        {
            colorList = new Color[0];
        }
        else
        {
            colorList = new Color[colors.length];
            for (int i = 0; i < colorList.length; ++i)
            {
                colorList[i] = colors[i].getColor();
            }
        }

        CycleMethod method;
        switch (stops.getCycleMethod())
        {
            case NO_CYCLE:
                method = CycleMethod.NO_CYCLE;
                break;
            case REFLECT:
                method = CycleMethod.REFLECT;
                break;
            case REPEAT:
                method = CycleMethod.REPEAT;
                break;
            default:
                throw new RuntimeException();
        }

        CyMatrix4d paintToWorldXform = new CyMatrix4d(localToWorld);
        paintToWorldXform.mul(layout.getPaintToLocalTransform());

        switch (stops.getStyle())
        {
            default:
            case LINEAR:
                return new LinearGradientPaint(new Point2D.Double(0, 0),
                        new Point2D.Double(1, 0),
                        stops.getFractions(), colorList, method,
                        stops.getColorSpace(), paintToWorldXform.asAffineTransform());
            case RADIAL:
            {
                CyVector2d focus = layout.getFocusPaint();

                return new RadialGradientPaint(new Point2D.Double(.5, .5),
                        .5f,
                        focus.asPoint2D(),
                        stops.getFractions(), colorList, method,
                        stops.getColorSpace(), paintToWorldXform.asAffineTransform());
            }
        }
    }


    /**
     * @return the stops
     */
    public MultipleGradientStops getStops()
    {
        return stops;
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
        final MultipleGradientStyle other = (MultipleGradientStyle) obj;
        if (this.stops != other.stops && (this.stops == null || !this.stops.equals(other.stops)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 37 * hash + (this.stops != null ? this.stops.hashCode() : 0);
        return hash;
    }

    
}
