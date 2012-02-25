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

import com.kitfox.raven.paint.control.UnderlayPaint;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.*;
import java.awt.*;

/**
 *
 * @author kitfox
 */
public class RavenPaintEditor 
    extends PropertyWrapperEditor<RavenPaint>
{
    public RavenPaintEditor(PropertyWrapper<? extends NodeObject, RavenPaint> wrapper)
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
        RavenPaint col = getValueFlat();
        
        g.setPaint(UnderlayPaint.inst().getPaint());
        g.fillRect(0, 0, box.width, box.height);
        
        if (col == null)
        {
            return;
        }
        
        Paint paint = col.getPaintSwatch(box);
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
        RavenPaint paint = getValueFlat();
        if (paint == null)
        {
            return "";
        }
        RavenPaintProvider prov =
                RavenPaintIndex.inst().getByPaint(paint.getClass());
        if (prov == null)
        {
            return "";
        }
        return prov.asText(paint);
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        RavenPaintProvider prov =
                RavenPaintIndex.inst().getProviderSupporting(text);

        RavenPaint paint = prov.fromText(text);
        setValue(paint);
    }

    @Override
    public String[] getTags()
    {
        return null;
    }

    @Override
    public PropertyCustomEditor createCustomEditor()
    {
        return new RavenPaintCustomEditor(this);
    }

    @Override
    public boolean supportsCustomEditor()
    {
        return true;
    }


    //----------------------------

    @ServiceInst(service=PropertyProvider.class)
    public static class Provider extends PropertyProvider<RavenPaint>
    {

        public Provider()
        {
            super(RavenPaint.class);
        }

        @Override
        public PropertyWrapperEditor createEditor(PropertyWrapper wrapper)
        {
            return new RavenPaintEditor(wrapper);
        }

        @Override
        public String asText(RavenPaint paint)
        {
            RavenPaintProvider prov =
                    RavenPaintIndex.inst().getByPaint(paint.getClass());
            if (prov == null)
            {
                return "";
            }
            return prov.asText(paint);
        }

        @Override
        public RavenPaint fromText(String text)
        {
            RavenPaintProvider prov =
                    RavenPaintIndex.inst().getProviderSupporting(text);

            return prov.fromText(text);
        }
    }
}
