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

package com.kitfox.rabbit.property;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;

/**
 *
 * @author kitfox
 */
public class EnumEditor 
        implements PropertyEditor
{

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    protected Enum value;
    public static final String PROP_VALUE = "value";
    final Class enumClass;

    public EnumEditor(Class enumClass)
    {
        this.enumClass = enumClass;
    }

    /**
     * Get the value of value
     *
     * @return the value of value
     */
    @Override
    public Object getValue()
    {
        return value;
    }

    /**
     * Set the value of value
     *
     * @param value new value of value
     */
    @Override
    public void setValue(Object value)
    {
        if (this.value == value)
        {
            return;
        }
        Enum oldValue = this.value;
        this.value = (Enum)value;
        propertyChangeSupport.firePropertyChange(PROP_VALUE, oldValue, value);
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
        if (value == null)
        {
            return null;
        }
        
        return value == null
                ? "null"
                : value.getDeclaringClass().getName().replace('$', '.') + "." + value.name();
    }

    @Override
    public String getAsText()
    {
        return value == null ? null : value.name();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        Object[] enumConsts = enumClass.getEnumConstants();
        for (Object enumValue: enumConsts)
        {
            if (((Enum)enumValue).name().equals(text))
            {
                setValue(enumValue);
                return;
            }
        }
        setValue(null);
    }

    @Override
    public String[] getTags()
    {
        Object[] enumConsts = enumClass.getEnumConstants();
        String[] tags = new String[enumConsts.length + 1];
        tags[0] = "";
        for (int i = 0; i < enumConsts.length; ++i)
        {
            tags[i + 1] = ((Enum)enumConsts[i]).name();
        }
        return tags;
    }

    @Override
    public Component getCustomEditor()
    {
        return null;
    }

    @Override
    public boolean supportsCustomEditor()
    {
        return false;
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
}
