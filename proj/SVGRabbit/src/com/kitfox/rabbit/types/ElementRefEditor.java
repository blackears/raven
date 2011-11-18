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

package com.kitfox.rabbit.types;

import com.kitfox.rabbit.parser.attribute.AttributeParser;
import com.kitfox.rabbit.parser.attribute.ParseException;
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
public class ElementRefEditor implements PropertyEditor
{
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    ElementRef value;
    public static final String PROP_VALUE = "value";

    public void setValue(Object value)
    {
        ElementRef oldValue = this.value;
        this.value = (ElementRef)value;
        propertyChangeSupport.firePropertyChange(PROP_VALUE, oldValue, value);
    }

    public ElementRef getValue()
    {
        return value;
    }

    public boolean isPaintable()
    {
        return false;
    }

    public void paintValue(Graphics gfx, Rectangle box)
    {
        
    }

    public String getJavaInitializationString()
    {
        return "new " + ElementRef.class.getCanonicalName()
                + "(" + value.getDocumentIndex()
                + ", " + value.getElementIndex() + ")";
    }

    public String getAsText()
    {
        return value.getDocumentIndex() + " " + value.getElementIndex();
    }

    public void setAsText(String text) throws IllegalArgumentException
    {
        AttributeParser parser = new AttributeParser(new StringReader(text));
        try {
            setValue(parser.FloatArray());
        } catch (ParseException ex) {
            Logger.getLogger(ElementRefEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String[] getTags()
    {
        return null;
    }

    public Component getCustomEditor()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean supportsCustomEditor()
    {
        return false;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

}
