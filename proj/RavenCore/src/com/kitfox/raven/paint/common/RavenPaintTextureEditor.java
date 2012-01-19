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

package com.kitfox.raven.paint.common;

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
public class RavenPaintTextureEditor implements PropertyEditor
{
    public static final String PROP_VALUE = "value";

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    RavenPaintTexture value;
    String textValue = "";

    RavenPaintTextureCustomEditor customEd = new RavenPaintTextureCustomEditor(this);


    @Override
    public void setValue(Object value)
    {
        RavenPaintTexture oldValue = this.value;
        this.value = (RavenPaintTexture)value;
        textValue = value == null ? ""
                : ((RavenPaintTexture)value).toString();
        
        if (oldValue != null || value != null)
        {
            propertyChangeSupport.firePropertyChange(PROP_VALUE, oldValue, value);
        }
    }

    @Override
    public RavenPaintTexture getValue()
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
        return null;
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
        if (textValue == null || "".equals(textValue))
        {
            setValue(null);
            return;
        }

        RavenPaintTexture tex = RavenPaintTexture.create(textValue);
        setValue(tex);
    }

    @Override
    public String[] getTags()
    {
        return null;
    }
    
    @Override
    public Component getCustomEditor()
    {
        return customEd;
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
}
