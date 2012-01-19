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
import com.kitfox.cache.CacheMap;
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
@Deprecated
public class MultipleGradientStyleEditor
        implements PropertyEditor
{
    public static final String PROP_VALUE = "value";

//    public static final String PROP_COLORSPACE = "colorSpace";
//    public static final String PROP_CYCLE = "cycle";
//    public static final String PROP_OFFSET = "offset";
//    public static final String PROP_COLOR = "color";
//    public static final String PROP_XFORM = "xform";
    public static final String PROP_WORLDSPACE = "worldSpace";
    public static final String PROP_STOPS = "stops";

    protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    MultipleGradientStyle value;
    String textValue = "";
    String codeGen = "null";


    @Override
    public MultipleGradientStyle getValue()
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
        try
        {
            CacheElement ele = CacheParser.parse(text);
            MultipleGradientStyle style =
                    new MultipleGradientStyle((CacheMap)ele);
            setValue(style);
        } catch (ParseException ex)
        {
            Logger.getLogger(MultipleGradientStyleEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void setValue(Object value)
    {
        MultipleGradientStyle oldValue = this.value;
        this.value = (MultipleGradientStyle)value;
        textValue = value == null ? ""
                : ((MultipleGradientStyle)value).toString();
        if (oldValue != null || value != null)
        {
            propertyChangeSupport.firePropertyChange(PROP_VALUE, oldValue, value);
        }
    }

    @Override
    public Component getCustomEditor()
    {
        return new MultipleGradientStyleCustomEditor(this);
    }

    @Override
    public boolean supportsCustomEditor()
    {
        return true;
    }

    @Override
    public String[] getTags()
    {
        return null;
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

    static public void toCache(MultipleGradientStyle grad, CacheMap map)
    {
        CacheMap localMap = grad.getStops().toCache();
        map.putAll(localMap);
    }
}
