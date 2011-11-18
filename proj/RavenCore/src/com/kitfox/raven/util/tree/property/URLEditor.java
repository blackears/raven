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
import com.kitfox.raven.util.tree.PropertyCustomEditor;
import com.kitfox.raven.util.tree.PropertyDataInline;
import com.kitfox.raven.util.tree.PropertyProvider;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperEditor;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class URLEditor extends PropertyWrapperEditor<URL>
{

    public URLEditor(PropertyWrapper wrapper)
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
        return "new java.net.URL(\"" + getValueFlat().toExternalForm() + "\")";
    }

    @Override
    public String getAsText()
    {
        URL val = getValueFlat();
        return val == null ? "" : val.toExternalForm();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        try {
            URL val = new URL(text);
            setValue(new PropertyDataInline<URL>(val));
        } catch (MalformedURLException ex) {
//            Logger.getLogger(URLEditor.class.getName()).log(Level.SEVERE, null, ex);
            setValue(null);
        }
    }

    @Override
    public String[] getTags()
    {
        return null;
    }

    @Override
    public PropertyCustomEditor createCustomEditor()
    {
        return new URLCustomEditor(this);
    }

    @Override
    public boolean supportsCustomEditor()
    {
        return true;
    }

    //----------------------------

    @ServiceInst(service=PropertyProvider.class)
    public static class Provider extends PropertyProvider<URL>
    {
        public Provider()
        {
            super(URL.class);
        }

        @Override
        public PropertyWrapperEditor createEditor(PropertyWrapper wrapper)
        {
            return new URLEditor(wrapper);
        }

        @Override
        public String asText(URL value)
        {
            return value.toExternalForm();
        }

        @Override
        public URL fromText(String text)
        {
            try {
                return new URL(text);
            } catch (MalformedURLException ex) {
                Logger.getLogger(URLEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return null;
        }
    }
}
