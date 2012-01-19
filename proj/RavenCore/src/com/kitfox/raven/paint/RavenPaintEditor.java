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

package com.kitfox.raven.paint;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class RavenPaintEditor 
        implements PropertyEditor, PropertyChangeListener
{
    public static final String PROP_VALUE = "value";

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    RavenPaint value;
    String textValue = "";

    private PropertyEditor delegateEditor;
    
    HashMap<RavenPaintProvider, PropertyEditor> paintEditorMap = 
            new HashMap<RavenPaintProvider, PropertyEditor>();

    public RavenPaintEditor()
    {
        for (RavenPaintProvider prov: RavenPaintIndex.inst().getServices())
        {
            paintEditorMap.put(prov, prov.createEditor());
        }
    }

    @Override
    public RavenPaint getValue()
    {
        return value;
    }

    @Override
    public void setValue(Object value)
    {
        RavenPaint oldValue = this.value;
        this.value = (RavenPaint)value;
        
        RavenPaintProvider prov = 
                RavenPaintIndex.inst().getByPaint(this.value.getClass());
        textValue = prov == null ? null : prov.asText(this.value);

        updateDelegate();
        
        if (oldValue != null || value != null)
        {
            propertyChangeSupport.firePropertyChange(PROP_VALUE, oldValue, value);
        }
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
        if (this.textValue.equals(text))
        {
            return;
        }
        this.textValue = text;
        
        RavenPaint oldValue = value;
        if (textValue == null || "".equals(textValue))
        {
            value = null;
        }
        else
        {
            RavenPaintProvider prov =
                    RavenPaintIndex.inst().getProviderSupporting(textValue);
            value = prov.fromText(textValue);
        }
        
        updateDelegate();
        
        if (oldValue != null || value != null)
        {
            propertyChangeSupport.firePropertyChange(PROP_VALUE, oldValue, value);
        }
    }

    private void updateDelegate()
    {
        RavenPaintProvider prov =
                RavenPaintIndex.inst().getByPaint(value.getClass());
        if (prov == null)
        {
            return;
        }

        PropertyEditor paintEd = paintEditorMap.get(prov);
        if (paintEd == delegateEditor)
        {
            //Don't update editor with itself
            return;
        }
        
        if (delegateEditor != null)
        {
            delegateEditor.removePropertyChangeListener(this);
        }
        
        delegateEditor = paintEd;
        
        if (delegateEditor != null)
        {
            delegateEditor.setValue(value);
            delegateEditor.addPropertyChangeListener(this);
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
        return new RavenPaintCustomEditor(this);
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
        if (evt.getSource() == delegateEditor)
        {
            setValue(delegateEditor.getValue());
        }
    }

    /**
     * @return the delegateEditor
     */
    public PropertyEditor getDelegateEditor()
    {
        return delegateEditor;
    }

}
