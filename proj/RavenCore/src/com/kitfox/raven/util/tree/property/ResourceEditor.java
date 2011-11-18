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
abstract public class ResourceEditor<ResType>
        implements PropertyEditor
{

    public static final String PROP_VALUE = "value";

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    final Class<ResType> type;
    ResType value;
    String path;

    public ResourceEditor(Class<ResType> type)
    {
        this.type = type;
    }

    @Override
    public void setValue(Object value)
    {
        if (value != null && !type.isAssignableFrom(value.getClass()))
        {
            return;
        }

        ResType oldValue = this.value;
        this.value = (ResType)value;
        propertyChangeSupport.firePropertyChange(PROP_VALUE, oldValue, value);
    }

    @Override
    public ResType getValue()
    {
        return value;
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
        return "loadResource(\"" + path + "\")";
    }

    @Override
    public String getAsText()
    {
        return path;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        this.path = text;
        /*
        URL url;
        try {
            url = new URL(text);

            String frag = url.getRef();
            url = new URL(url.getProtocol(), url.getHost(), url.getPath());

            ResourceFactory fact = ResourceLoader.inst().getResourceFactory(url);
            Resource form = fact.create(url);
            setValue(form.createResource(frag));
        } catch (MalformedURLException ex) {
//            Logger.getLogger(ResourceEditor.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
         */
    }

    @Override
    public String[] getTags()
    {
        return null;
    }

    @Override
    public Component getCustomEditor()
    {
        return new ResourceCustomEditor(this);
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

//    abstract protected void setResource(ResFormType form, String fragment);

//    public void setBean(Object bean)
//    {
//        this.bean = bean;
//    }
}
