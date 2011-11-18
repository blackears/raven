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

import com.kitfox.cache.CacheElement;
import com.kitfox.cache.CacheList;
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.PropertyCustomEditor;
import com.kitfox.raven.util.tree.PropertyDataInline;
import com.kitfox.raven.util.tree.PropertyProvider;
import com.kitfox.raven.util.tree.PropertyProviderIndex;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperEditor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPopupMenu;

/**
 *
 * @author kitfox
 */
public class FontEditor extends PropertyWrapperEditor<Font>
{
    public FontEditor(PropertyWrapper wrapper)
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
    }

    @Override
    public String getJavaInitializationString()
    {
        return null;
    }

    @Override
    public String getAsText()
    {
        PropertyProvider<Font> prov = PropertyProviderIndex.inst().getProviderBest(Font.class);
        return prov.asText(getValueFlat());
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        PropertyProvider<Font> prov = PropertyProviderIndex.inst().getProviderBest(Font.class);
        Font font = prov.fromText(text);
        setValue(new PropertyDataInline<Font>(font));
    }

    @Override
    public String[] getTags()
    {
        return null;
    }

    @Override
    public PropertyCustomEditor createCustomEditor()
    {
        return new FontCustomEditor(this);
    }

    @Override
    public boolean supportsCustomEditor()
    {
        return true;
    }

    //----------------------------

    @ServiceInst(service=PropertyProvider.class)
    public static class Provider extends PropertyProvider<Font>
    {
        public Provider()
        {
            super(Font.class);
        }

        @Override
        public PropertyWrapperEditor createEditor(PropertyWrapper wrapper)
        {
            return new FontEditor(wrapper);
        }

        @Override
        public String asText(Font value)
        {
            CacheList list = new CacheList("font");
            list.add(value.getFamily());
            list.add(value.getSize());
            list.add(value.isBold());
            list.add(value.isItalic());
            return list.toString();
        }

        @Override
        public Font fromText(String text)
        {
            try {
                CacheElement ele = CacheParser.parse(text);
                if (ele instanceof CacheList)
                {
                    CacheList list = (CacheList)ele;
                    String name = list.getString(0, "Serif");
                    int size = list.getInteger(1, 12);
                    boolean bold = list.getBoolean(2, false);
                    boolean italic = list.getBoolean(3, false);

                    return new Font(name,
                            (bold ? Font.BOLD : 0) | (italic ? Font.ITALIC : 0),
                            size);
                }

            } catch (ParseException ex) {
                Logger.getLogger(FontEditor.class.getName()).log(Level.SEVERE, null, ex);
            }

            return null;
        }
    }
}
