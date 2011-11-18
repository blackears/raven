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

package com.kitfox.raven.util.tree.property;

import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.PropertyDataInline;
import com.kitfox.raven.util.tree.PropertyProvider;
import com.kitfox.raven.util.tree.PropertyCustomEditor;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperEditor;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import javax.swing.JPopupMenu;

/**
 *
 * @author kitfox
 */
public class BooleanEditor extends PropertyWrapperEditor<Boolean>
        implements MouseListener
{
    public BooleanEditor(PropertyWrapper wrapper)
    {
        super(wrapper);
    }

    @Override
    protected void buildPopupMenu(JPopupMenu menu)
    {
        appendDefaultMenu(menu);
    }

    @Override
    public boolean isPaintable()
    {
        return false;
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getJavaInitializationString()
    {
        return getAsText();
    }

    @Override
    public String getAsText()
    {
        Boolean val = getValueFlat();
        return val == Boolean.TRUE ? "true" : "false";
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        boolean val = "true".equalsIgnoreCase(text);
        setValue(new PropertyDataInline<Boolean>(val));
    }

    @Override
    public String[] getTags()
    {
        return new String[]{"true", "false"};
    }

    @Override
    public PropertyCustomEditor createCustomEditor()
    {
        return null;
    }

    @Override
    public boolean supportsCustomEditor()
    {
        return false;
    }

    //----------------------------

    @ServiceInst(service=PropertyProvider.class)
    public static class Provider extends PropertyProvider<Boolean>
    {
        public Provider()
        {
            super(Boolean.class);
        }

        @Override
        public PropertyWrapperEditor createEditor(PropertyWrapper wrapper)
        {
            return new BooleanEditor(wrapper);
        }

        @Override
        public boolean isNumeric()
        {
            return true;
        }

        @Override
        public double asDouble(Boolean value)
        {
            return Boolean.TRUE.equals(value) ? 1 : 0;
        }

        @Override
        public Boolean createNumericValue(PropertyWrapper wrapper, double value)
        {
            return value >= 1 ? true : false;
        }

        @Override
        public String asText(Boolean value)
        {
            return "" + value;
        }

        @Override
        public Boolean fromText(String text)
        {
            return Boolean.parseBoolean(text);
        }
    }

//    @ServiceAnno(service=PropertyProvider.class)
//    public static class ProviderPrim extends PropertyProvider<Boolean>
//    {
//        public ProviderPrim()
//        {
//            super(Boolean.TYPE);
//        }
//
//        @Override
//        public PropertyWrapperEditor createEditor(PropertyWrapper wrapper)
//        {
//            return new BooleanEditor(wrapper);
//        }
//
//        @Override
//        public Boolean interpolate(NodeDocument doc, TrackKey<Boolean> k0, TrackKey<Boolean> k1,
//                int frame, int k0Frame, int k1Frame)
//        {
//            return k0.getData().getValue(doc);
//        }
//    }
}
