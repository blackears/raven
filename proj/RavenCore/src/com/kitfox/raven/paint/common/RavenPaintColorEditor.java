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

import com.kitfox.raven.paint.control.UnderlayPaint;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;

/**
 *
 * @author kitfox
 */
public class RavenPaintColorEditor
    extends PropertyWrapperEditor<RavenPaintColor>
{
    public RavenPaintColorEditor(PropertyWrapper<? extends NodeObject, RavenPaintColor> wrapper)
    {
        super(wrapper);
    }
    
    @Override
    public boolean isPaintable()
    {
        return true;
    }

    @Override
    public void paintValue(Graphics gg, Rectangle box)
    {
        Graphics2D g = (Graphics2D)gg;
        RavenPaintColor col = getValueFlat();
        
        if (col == null || col.getA() < 1)
        {
            g.setPaint(UnderlayPaint.inst().getPaint());
            g.fillRect(0, 0, box.width, box.height);
        }
        
        if (col == null)
        {
            return;
        }
        
        Rectangle drawBox = new Rectangle(box.width, box.height);
        Paint paint = col.getPaintSwatch(drawBox);
        g.setPaint(paint);
        g.fillRect(0, 0, box.width, box.height);
    }

    @Override
    public String getJavaInitializationString()
    {
        return null;
    }

    @Override
    public String getAsText()
    {
        RavenPaintColor val = getValueFlat();
        return val == null ? "" : val.toString();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        RavenPaintColor val = RavenPaintColor.create(text);
        setValue(val);
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
            return value.toString();
        }

        @Override
        public RavenPaintColor fromText(String text)
        {
            return RavenPaintColor.create(text);
        }
    }
}
