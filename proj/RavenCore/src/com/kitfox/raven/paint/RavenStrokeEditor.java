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

import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.*;
import java.awt.*;

/**
 *
 * @author kitfox
 */
public class RavenStrokeEditor
    extends PropertyWrapperEditor<RavenStroke>
{

    public RavenStrokeEditor(PropertyWrapper wrapper)
    {
        super(wrapper);
    }

    @Override
    public boolean isPaintable()
    {
        return true;
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box)
    {
        Graphics2D g = (Graphics2D)gfx;

        g.setPaint(Color.WHITE);
        g.fillRect(0, 0, box.width, box.height);
        
        PropertyData<RavenStroke> data = getValue();
        RavenStroke ravenStroke = data.getValue(getDocument());
        if (ravenStroke == null)
        {
            return;
        }

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        ravenStroke.drawPreview(g, box);
    }

    @Override
    public String getJavaInitializationString()
    {
        return null;
    }

    @Override
    public String getAsText()
    {
        RavenStroke val = getValueFlat();
        return val == null ? "" : val.toString();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        RavenStroke val = RavenStroke.create(text);
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
        return new RavenStrokeCustomEditor(this);
    }

    @Override
    public boolean supportsCustomEditor()
    {
        return true;
    }

    //----------------------------

    @ServiceInst(service=PropertyProvider.class)
    public static class Provider extends PropertyProvider<RavenStroke>
    {

        public Provider()
        {
            super(RavenStroke.class);
        }

        @Override
        public PropertyWrapperEditor createEditor(PropertyWrapper wrapper)
        {
            return new RavenStrokeEditor(wrapper);
        }

        @Override
        public String asText(RavenStroke value)
        {
            return value.toString();
        }

        @Override
        public RavenStroke fromText(String text)
        {
            return RavenStroke.create(text);
        }
    }
}
