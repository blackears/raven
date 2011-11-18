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

import com.kitfox.game.control.color.UnderlayPaint;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.PropertyCustomEditor;
import com.kitfox.raven.util.tree.PropertyData;
import com.kitfox.raven.util.tree.PropertyProvider;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperEditor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;

/**
 *
 * @author kitfox
 */
public class RavenPaintGradientEditor extends PropertyWrapperEditor<RavenPaintGradient>
{
    public RavenPaintGradientEditor(PropertyWrapper wrapper)
    {
        super(wrapper);
    }

    @Override
    public boolean isPaintable()
    {
        PropertyData<RavenPaintGradient> data = getValue();
        RavenPaintGradient stops = data.getValue(getDocument());
        return stops != null;
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box)
    {
        Graphics2D g = (Graphics2D)gfx;

        //Graphics2D already has translation applied, so zero out x, y of box
        box.x = 0;
        box.y = 0;

        g.setPaint(UnderlayPaint.inst().getPaint());
        g.fillRect(0, 0, box.width, box.height);

        PropertyData<RavenPaintGradient> data = getValue();
        RavenPaintGradient ravenPaint = data.getValue(getDocument());

        Paint paint = ravenPaint.getPaintSwatch(box);

        g.setPaint(paint);
        g.fillRect(0, 0, box.width, box.height);
    }

    @Override
    public String getJavaInitializationString()
    {
        return "";
    }

    @Override
    public String getAsText()
    {
        PropertyData<RavenPaintGradient> data = getValue();
        RavenPaintGradient stops = data.getValue(null);
        return stops == null ? "" : stops.toString();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        RavenPaintGradient value = RavenPaintGradient.create(text);
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
        return new RavenPaintGradientCustomEditor(this);
    }

    @Override
    public boolean supportsCustomEditor()
    {
        return true;
    }

    //----------------------------


    @ServiceInst(service=PropertyProvider.class)
    public static class Provider extends PropertyProvider<RavenPaintGradient>
    {
        public Provider()
        {
            super(RavenPaintGradient.class);
        }

        @Override
        public PropertyWrapperEditor createEditor(PropertyWrapper wrapper)
        {
            return new RavenPaintGradientEditor(wrapper);
        }

        @Override
        public String asText(RavenPaintGradient value)
        {
            return value == null ? "" : value.toString();
        }

        @Override
        public RavenPaintGradient fromText(String text)
        {
            if ("".equalsIgnoreCase(text))
            {
                return null;
            }

            //Parse
            return RavenPaintGradient.create(text);
        }
    }
}
