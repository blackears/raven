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

import com.kitfox.cache.CacheIdentifier;
import com.kitfox.cache.CacheMap;
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
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
public class PaintStyleEditor implements PropertyEditor, PropertyChangeListener
{
    public static final String PROP_VALUE = "value";

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    PaintStyle value;
    String textValue = "";
    String codeGen = "null";

    ColorStyleEditor colorEditor = new ColorStyleEditor();
    MultipleGradientStyleEditor gradientEditor = new MultipleGradientStyleEditor();

    public PaintStyleEditor()
    {
        colorEditor.addPropertyChangeListener(this);
        gradientEditor.addPropertyChangeListener(this);
    }

    @Override
    public void setValue(Object value)
    {
        PaintStyle oldValue = this.value;
        this.value = (PaintStyle)value;
        if (value instanceof ColorStyle)
        {
            textValue = ((ColorStyle)value).toString();
        }
        else if (value instanceof MultipleGradientStyle)
        {
            textValue = ((MultipleGradientStyle)value).toString();
        }
        else
        {
            textValue = "";
        }
//        textValue = value == null ? ""
//                : value.toString();
        if (oldValue != null || value != null)
        {
            propertyChangeSupport.firePropertyChange(PROP_VALUE, oldValue, value);
        }
    }

    @Override
    public PaintStyle getValue()
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
        if (this.textValue.equals(text))
        {
            return;
        }
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
            Logger.getLogger(PaintStyleEditor.class.getName()).log(Level.WARNING, null, ex);
            setValue(null);
            return;
        }

        String name = ident.getName();
        if (ColorStyle.CACHE_NAME.equals(name))
        {
            colorEditor.setAsText(textValue);
            codeGen = colorEditor.getJavaInitializationString();
            setValue(colorEditor.getValue());
        }
        else if (MultipleGradientStops.CACHE_NAME.equals(name))
        {
            gradientEditor.setAsText(textValue);
            codeGen = colorEditor.getJavaInitializationString();
            setValue(gradientEditor.getValue());
        }
    }

    @Override
    public String[] getTags()
    {
        return null;
    }

    @Override
    public Component getCustomEditor()
    {
        return new PaintStyleCustomEditor(this);
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

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (evt.getSource() == colorEditor)
        {
            setAsText(colorEditor.getAsText());
        }
        else if (evt.getSource() == gradientEditor)
        {
            setAsText(gradientEditor.getAsText());
        }
    }

}
