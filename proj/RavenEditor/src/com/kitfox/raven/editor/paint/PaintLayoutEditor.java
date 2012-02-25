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

import com.kitfox.game.control.color.PaintLayout;
import com.kitfox.game.control.color.PaintLayoutSerializer;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.PropertyCustomEditor;
import com.kitfox.raven.util.tree.PropertyData;
import com.kitfox.raven.util.tree.PropertyProvider;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperEditor;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 *
 * @author kitfox
 */
@Deprecated
public class PaintLayoutEditor extends PropertyWrapperEditor<PaintLayout>
{
    public PaintLayoutEditor(PropertyWrapper wrapper)
    {
        super(wrapper);
    }

    @Override
    public boolean isPaintable()
    {
        return false;
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box)
    {
    }

    @Override
    public String getJavaInitializationString()
    {
        return "";
    }

    @Override
    public String getAsText()
    {
        PropertyData<PaintLayout> data = getValue();
        PaintLayout stops = data.getValue(null);
        return stops == null ? "" : stops.toString();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        PaintLayout value = PaintLayoutSerializer.create(text);
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
        return new PaintLayoutCustomEditor(this);
    }

    @Override
    public boolean supportsCustomEditor()
    {
        return true;
    }


    //----------------------------


    @ServiceInst(service=PropertyProvider.class)
    public static class Provider extends PropertyProvider<PaintLayout>
    {
        public Provider()
        {
            super(PaintLayout.class);
        }

        @Override
        public PropertyWrapperEditor createEditor(PropertyWrapper wrapper)
        {
            return new PaintLayoutEditor(wrapper);
        }

        @Override
        public String asText(PaintLayout value)
        {
            return value == null ? "" : value.toString();
        }

        @Override
        public PaintLayout fromText(String text)
        {
            if ("".equalsIgnoreCase(text))
            {
                return null;
            }

            //Parse
            return PaintLayoutSerializer.create(text);
        }
    }

}
