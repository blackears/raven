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

package com.kitfox.raven.editor.paint;

import com.kitfox.game.control.color.ColorStyle;
import com.kitfox.game.control.color.UnderlayPaint;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeSymbol;
import com.kitfox.raven.util.tree.PropertyCustomEditor;
import com.kitfox.raven.util.tree.PropertyData;
import com.kitfox.raven.util.tree.PropertyProvider;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperEditor;
import com.kitfox.raven.util.tree.TrackKey;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;

/**
 *
 * @author kitfox
 */
@Deprecated
public class RavenPaintColorEditor extends PropertyWrapperEditor<RavenPaintColor>
{
    public RavenPaintColorEditor(PropertyWrapper wrapper)
    {
        super(wrapper);
    }

    @Override
    public boolean isPaintable()
    {
        PropertyData<RavenPaintColor> data = getValue();
        RavenPaintColor val = data.getValue(getDocument());
        return val != null;
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box)
    {
        Graphics2D g = (Graphics2D)gfx;

        g.setPaint(UnderlayPaint.inst().getPaint());
        g.fillRect(0, 0, box.width, box.height);

        PropertyData<RavenPaintColor> data = getValue();
        RavenPaintColor ravenPaint = data.getValue(getDocument());

        Paint paint = ravenPaint.getPaintSwatch(box);

        g.setPaint(paint);
        g.fillRect(0, 0, box.width, box.height);
    }

    @Override
    public String getJavaInitializationString()
    {
        PropertyData<RavenPaintColor> data = getValue();
        RavenPaint paint = data.getValue(null);
        return ((RavenPaintColor)paint).toCode();
    }

    @Override
    public String getAsText()
    {
        PropertyData<RavenPaintColor> data = getValue();
        RavenPaint paint = data.getValue(null);
        return paint == null ? "inherit" : paint.toString();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        RavenPaintColor value = RavenPaintColor.create(text);
        setValue(value);
    }

    @Override
    public String[] getTags()
    {
        return null;
    }

    @Override
    public PropertyCustomEditor createCustomEditor()
    {
        return new RavenPaintColorCustomEditor(this);
    }

    @Override
    public boolean supportsCustomEditor()
    {
        return true;
    }

    //----------------------------


    @ServiceInst(service=PropertyProvider.class)
    public static class Provider extends PropertyProvider<RavenPaintColor>
    {
        public Provider()
        {
            super(RavenPaintColor.class);
        }

        @Override
        public PropertyWrapperEditor createEditor(PropertyWrapper wrapper)
        {
            return new RavenPaintColorEditor(wrapper);
        }

        @Override
        public String asText(RavenPaintColor value)
        {
            return value == null ? "inherit" : value.toString();
        }

        @Override
        public RavenPaintColor fromText(String text)
        {
            if ("inherit".equalsIgnoreCase(text))
            {
                return null;
            }

            //Parse
            return RavenPaintColor.create(text);
        }

        @Override
        public RavenPaintColor interpolate(NodeSymbol doc, TrackKey<RavenPaintColor> k0, TrackKey<RavenPaintColor> k1, int frame, int k0Frame, int k1Frame)
        {
            RavenPaintColor rc0 = k0.getData().getValue(doc);
            RavenPaintColor rc1 = k1.getData().getValue(doc);

            if (rc0 == null || rc1 == null)
            {
                return rc0;
            }

            int span = k1Frame - k0Frame;
            double frac = (frame - k0Frame) / (double)span;

            ColorStyle c0 = rc0.getColor();
            ColorStyle c1 = rc1.getColor();

            float r = (float)interpolate(k0.getInterp(),
                    c0.r, k0.getTanOutX(), k0.getTanOutY(),
                    c1.r, k1.getTanInX(), k1.getTanInY(),
                    span, frac);
            float g = (float)interpolate(k0.getInterp(),
                    c0.g, k0.getTanOutX(), k0.getTanOutY(),
                    c1.g, k1.getTanInX(), k1.getTanInY(),
                    span, frac);
            float b = (float)interpolate(k0.getInterp(),
                    c0.b, k0.getTanOutX(), k0.getTanOutY(),
                    c1.b, k1.getTanInX(), k1.getTanInY(),
                    span, frac);
            float a = (float)interpolate(k0.getInterp(),
                    c0.a, k0.getTanOutX(), k0.getTanOutY(),
                    c1.a, k1.getTanInX(), k1.getTanInY(),
                    span, frac);

            return new RavenPaintColor(r, g, b, a);
        }
    }
}
