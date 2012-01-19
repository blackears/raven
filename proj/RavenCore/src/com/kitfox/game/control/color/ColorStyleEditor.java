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

package com.kitfox.game.control.color;

import com.kitfox.cache.CacheElement;
import com.kitfox.cache.CacheIdentifier;
import com.kitfox.cache.CacheInteger;
import com.kitfox.cache.CacheList;
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
@Deprecated
public class ColorStyleEditor implements PropertyEditor
{
    public static final String PROP_VALUE = "value";

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    ColorStyle value;
    String textValue = "";
    String codeGen = "null";


    @Override
    public void setValue(Object value)
    {
        ColorStyle oldValue = this.value;
        this.value = (ColorStyle)value;
        textValue = value == null ? ""
                : toCache((ColorStyle)value).toString();
        if (oldValue != null || value != null)
        {
            propertyChangeSupport.firePropertyChange(PROP_VALUE, oldValue, value);
        }
    }

    @Override
    public ColorStyle getValue()
    {
        return value;
    }

    @Override
    public boolean isPaintable()
    {
        return false;
    }

    @Override
    public void paintValue(Graphics g, Rectangle box)
    {
    }

    @Override
    public String getJavaInitializationString()
    {
        return codeGen;
    }

    @Override
    public String getAsText()
    {
        return textValue;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        this.textValue = text;
        updateValueFromText();
    }

    private void updateValueFromText()
    {
        codeGen = "null";
        if (textValue == null || "".equals(textValue))
        {
            setValue(null);
            return;
        }

        StringReader reader = new StringReader(textValue);
        CacheParser parser = new CacheParser(reader);
        CacheIdentifier ident;
        try
        {
            ident = (CacheIdentifier)parser.Cache();
        } catch (ParseException ex) {
            Logger.getLogger(ColorStyleEditor.class.getName()).log(Level.WARNING, null, ex);
            setValue(null);
            return;
        }

        ColorStyle paint;
        String name = ident.getName();
        if ("rgb".equals(name) || "rgba".equals(name))
        {
            paint = ColorStyleEditor.create(ident);
            if (paint != null)
            {
                codeGen = ColorStyleEditor.toCodeGen(paint);
            }
        }
        else
        {
            paint = null;
        }

        setValue(paint);
    }

    @Override
    public String[] getTags()
    {
        return null;
    }

    @Override
    public Component getCustomEditor()
    {
        return new ColorCustomEditor(this);
    }

    @Override
    public boolean supportsCustomEditor()
    {
        return true;
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Deprecated
    public static ColorStyle create(CacheList fn)
    {
//        String name = fn.getName();
//        if (!"rgb".equals(name) && !"rgba".equals(name))
//        {
//            return null;
//        }
        int r = fn.getInteger(0, 0);
        int g = fn.getInteger(1, 0);
        int b = fn.getInteger(2, 0);
        int a = fn.getInteger(3, 255);

        return new ColorStyle(r, g, b, a);
    }

    public static ColorStyle create(CacheElement ele)
    {
        return ele instanceof CacheList
                ? create((CacheList)ele)
                : null;
    }

    static public CacheList toCache(ColorStyle color)
    {
        if (color == null)
        {
            color = ColorStyle.BLACK;
        }

        CacheList fn = new CacheList();
        fn.add(new CacheInteger(toInt(color.r)));
        fn.add(new CacheInteger(toInt(color.g)));
        fn.add(new CacheInteger(toInt(color.b)));
        if (color.a == 1)
        {
//            fn.setName("rgb");
        }
        else
        {
//            fn.setName("rgba");
            fn.add(new CacheInteger(toInt(color.a)));
        }

        return fn;
    }

    static private int toInt(float value)
    {
        return (int)(value * 255 + .5f);
    }

    public static String toCodeGen(ColorStyle color)
    {
        return color.a == 1
            ? String.format("new %s(%ff, %ff, %ff)",
                ColorStyle.class.getName(), color.r, color.g, color.b)
            : String.format("new %s(%ff, %ff, %ff, %ff)",
                ColorStyle.class.getName(), color.r, color.g, color.b, color.a);
    }
}
