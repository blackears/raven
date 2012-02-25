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
public class RavenPaintListEditor extends PropertyWrapperEditor<RavenPaintList>
{
    public RavenPaintListEditor(PropertyWrapper wrapper)
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
        PropertyData<RavenPaintList> data = getValue();
        RavenPaintList paint = data.getValue(null);
        return ((RavenPaintList)paint).toString();
    }

    @Override
    public String getAsText()
    {
        PropertyData<RavenPaintList> data = getValue();
        RavenPaintList paint = data.getValue(null);
        return paint == null ? "" : paint.toString();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        RavenPaintList list = RavenPaintList.create(text);
        setValue(list);

        //Parse
//        StringReader reader = new StringReader(text);
//        CacheParser parser = new CacheParser(reader);
//        CacheIdentifier ident;
//        try
//        {
//            ident = (CacheIdentifier)parser.Cache();
//        } catch (ParseException ex) {
//            Logger.getLogger(ColorStyleEditor.class.getName()).log(Level.WARNING, null, ex);
//            setValue(null);
//            return;
//        }
//
//        String name = ident.getName();
//        if ("rgb".equals(name) || "rgba".equals(name))
//        {
//            ColorStyle paint = ColorStyleEditor.create(ident);
//            setValue(new RavenPaintColor(paint));
//            return;
//        }
//
//        setValue(null);
    }

    @Override
    public String[] getTags()
    {
        return null;
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
    public static class Provider extends PropertyProvider<RavenPaintList>
    {
        public Provider()
        {
            super(RavenPaintList.class);
        }

        @Override
        public PropertyWrapperEditor createEditor(PropertyWrapper wrapper)
        {
            return new RavenPaintListEditor(wrapper);
        }

        @Override
        public String asText(RavenPaintList value)
        {
            return value == null ? "" : value.toString();
        }

        @Override
        public RavenPaintList fromText(String text)
        {
            return RavenPaintList.create(text);
        }
    }
}
