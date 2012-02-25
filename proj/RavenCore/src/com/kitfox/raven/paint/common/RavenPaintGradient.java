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
import com.kitfox.cache.CacheIdentifier;
import com.kitfox.cache.CacheList;
import com.kitfox.cache.CacheMap;
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import com.kitfox.coyote.material.gradient.CyMaterialGradientDrawRecord;
import com.kitfox.coyote.material.gradient.CyMaterialGradientDrawRecordFactory;
import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.coyote.math.CyGradientStops;
import com.kitfox.coyote.math.CyGradientStops.Cycle;
import com.kitfox.coyote.math.CyGradientStops.Style;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.raven.paint.RavenPaint;
import com.kitfox.raven.paint.control.RavenPaintControl;
import com.kitfox.raven.paint.RavenPaintLayout;
import com.kitfox.raven.paint.RavenPaintProvider;
import com.kitfox.raven.paint.control.GradientEditorPanel;
import com.kitfox.raven.util.service.ServiceInst;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class RavenPaintGradient implements RavenPaint
{
    public static final String CACHE_NAME = "grad";
    public static final String PROP_COLOR = "color";
    public static final String PROP_CYCLE = "cycle";
    public static final String PROP_STYLE = "style";
    public static final String PROP_OFFSET = "offset";
    
    private final CyGradientStops stops;

    public RavenPaintGradient(CyGradientStops stops)
    {
        this.stops = stops;
    }

    public MultipleGradientPaint getPaintAWT(AffineTransform xform)
    {
        CyColor4f[] colors = stops.getColors();
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
                CyColor4f c = colors[i];
                colorList[i] = new Color(c.r, c.g, c.b, c.a);
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

        switch (stops.getStyle())
        {
            default:
            case LINEAR:
                return new LinearGradientPaint(new Point2D.Double(0, 0),
                        new Point2D.Double(1, 0),
                        stops.getFractions(), colorList, method,
                        MultipleGradientPaint.ColorSpaceType.SRGB,
                        xform);
            case RADIAL:
            {
                Point2D center = new Point2D.Double(.5, .5);
                
                return new RadialGradientPaint(center,
                        .5f,
                        center,
                        stops.getFractions(), colorList, method,
                        MultipleGradientPaint.ColorSpaceType.SRGB,
                        xform);
            }
        }
    }

    @Override
    public Paint getPaintSwatch(Rectangle box)
    {
        return getPaintAWT(new AffineTransform(box.width, 0, 
                0, box.height, 
                -box.x, -box.y));
    }

    /**
     * @return the stops
     */
    public CyGradientStops getStops()
    {
        return stops;
    }

    @Override
    public void fillShape(CyDrawStack stack, 
        RavenPaintLayout layout, CyVertexBuffer mesh)
    {
        CyMaterialGradientDrawRecord rec =
                CyMaterialGradientDrawRecordFactory.inst().allocRecord();
        
        rec.setStops(stops);
        rec.setTexToLocalMatrix(layout.getPaintToLocal());
        rec.setMesh(mesh);
        rec.setMvpMatrix(stack.getModelViewProjXform());
        rec.setOpacity(1);
        
        stack.addDrawRecord(rec);
    }

    public static RavenPaintGradient create(String text)
    {
        try
        {
            CacheElement ele = CacheParser.parse(text);
            if (!(ele instanceof CacheMap))
            {
                return null;
            }
            CacheMap map = (CacheMap)ele;
            return create(map);
        } catch (ParseException ex)
        {
            Logger.getLogger(RavenPaintGradient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static RavenPaintGradient create(CacheMap map)
    {
        if (!CACHE_NAME.equals(map.getName()))
        {
            return null;
        }

        float[] offsets;
        {
            CacheList stopsList = (CacheList)map.get(PROP_OFFSET);
            offsets = stopsList.toFloatArray(0);
        }
        
        CyColor4f[] colorArr;
        {
            CacheList colorsList = (CacheList)map.get(PROP_COLOR);
            colorArr = new CyColor4f[colorsList.size()];
            for (int i = 0; i < colorArr.length; ++i)
            {
                CacheList colList = (CacheList)colorsList.get(i);
                float r = colList.getFloat(0, 0);
                float g = colList.getFloat(1, 0);
                float b = colList.getFloat(2, 0);
                float a = colList.getFloat(3, 1);
                colorArr[i] = new CyColor4f(r, g, b, a);
            }
        }
        
        Cycle cycle = map.getEnum(PROP_CYCLE, Cycle.NO_CYCLE);
        Style style = map.getEnum(PROP_STYLE, Style.LINEAR);
        
        CyGradientStops stops = new CyGradientStops(
                offsets, colorArr, cycle, style);
        return new RavenPaintGradient(stops);
    }

    public CacheMap toCache()
    {
        CacheMap map = new CacheMap(CACHE_NAME);

        {
            Cycle cycle = stops.getCycleMethod();
            if (cycle != Cycle.NO_CYCLE)
            {
                map.put(PROP_CYCLE, new CacheIdentifier(cycle.name()));
            }
        }
        {
            Style value = stops.getStyle();
            if (value != Style.LINEAR)
            {
                map.put(PROP_STYLE, new CacheIdentifier(value.name()));
            }
        }

        CacheList colorList = new CacheList();
        CyColor4f[] colStyleList = stops.getColors();
        if (colStyleList != null)
        {
            for (CyColor4f color: colStyleList)
            {
                CacheList col = new CacheList();
                col.add(color.r);
                col.add(color.g);
                col.add(color.b);
                col.add(color.a);
                colorList.add(col);
            }
        }
        map.put(PROP_COLOR, colorList);

        CacheList offsetList = new CacheList();
        float[] fractList = stops.getFractions();
        if (fractList != null)
        {
            for (float offset: fractList)
            {
                offsetList.add(offset);
            }
        }
        map.put(PROP_OFFSET, offsetList);

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
        final RavenPaintGradient other = (RavenPaintGradient) obj;
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
        hash = 59 * hash + (this.stops != null ? this.stops.hashCode() : 0);
        return hash;
    }
    
    //--------------------------------------
    @ServiceInst(service=RavenPaintProvider.class)
    public static class Provider extends RavenPaintProvider<RavenPaintGradient>
    {
//        RavenPaintGradientEditor ed = new RavenPaintGradientEditor();
        
        public Provider()
        {
            super("Gradient", RavenPaintGradient.class);
        }

        @Override
        public boolean canParse(String text)
        {
            return text.startsWith(CACHE_NAME);
        }

        @Override
        public RavenPaintGradient fromText(String text)
        {
            return create(text);
        }

        @Override
        public String asText(RavenPaintGradient value)
        {
            return value.toString();
        }

        @Override
        public RavenPaintControl createEditor()
        {
            return new GradientEditorPanel();
        }

        @Override
        public RavenPaintGradient getDefaultValue()
        {
            return new RavenPaintGradient(new CyGradientStops());
        }
    }
}
