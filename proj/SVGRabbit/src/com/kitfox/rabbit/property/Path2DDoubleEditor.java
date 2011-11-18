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

import com.kitfox.rabbit.parser.path.ParseException;
import com.kitfox.rabbit.parser.path.PathParser;
import com.kitfox.rabbit.util.PathBuilder;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
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
public class Path2DDoubleEditor implements PropertyEditor
{
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    Path2D.Double value;
    public static final String PROP_VALUE = "value";

    @Override
    public void setValue(Object value)
    {
        Path2D.Double oldValue = this.value;
        this.value = (Path2D.Double)value;
        propertyChangeSupport.firePropertyChange(PROP_VALUE, oldValue, value);
    }

    @Override
    public Path2D.Double getValue()
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
        StringBuilder sbVal = new StringBuilder();
        StringBuilder sbMode = new StringBuilder();
        float[] arr = new float[6];
        for (PathIterator it = value.getPathIterator(null); !it.isDone(); it.next())
        {
            switch (it.currentSegment(arr))
            {
                case PathIterator.SEG_CLOSE:
                    sbMode.append(PathIterator.SEG_CLOSE).append(", ");
                    break;
                case PathIterator.SEG_CUBICTO:
                    sbMode.append(PathIterator.SEG_CUBICTO).append(", ");
                    sbVal.append(arr[0]).append("f, ");
                    sbVal.append(arr[1]).append("f, ");
                    sbVal.append(arr[2]).append("f, ");
                    sbVal.append(arr[3]).append("f, ");
                    sbVal.append(arr[4]).append("f, ");
                    sbVal.append(arr[5]).append("f, ");
                    break;
                case PathIterator.SEG_LINETO:
                    sbMode.append(PathIterator.SEG_LINETO).append(", ");
                    sbVal.append(arr[0]).append("f, ");
                    sbVal.append(arr[1]).append("f, ");
                    break;
                case PathIterator.SEG_MOVETO:
                    sbMode.append(PathIterator.SEG_MOVETO).append(", ");
                    sbVal.append(arr[0]).append("f, ");
                    sbVal.append(arr[1]).append("f, ");
                    break;
                case PathIterator.SEG_QUADTO:
                    sbMode.append(PathIterator.SEG_QUADTO).append(", ");
                    sbVal.append(arr[0]).append("f, ");
                    sbVal.append(arr[1]).append("f, ");
                    sbVal.append(arr[2]).append("f, ");
                    sbVal.append(arr[3]).append("f, ");
                    break;
            }
        }

        return PathBuilder.class.getCanonicalName() 
                + ".create(new int[]{" + sbMode.toString() + "}, new float[]{" + sbVal.toString() + "})";
    }

    @Override
    public String getAsText()
    {
        StringBuilder sb = new StringBuilder();
        float[] arr = new float[6];
        for (PathIterator it = value.getPathIterator(null); !it.isDone();)
        {
            switch (it.currentSegment(arr))
            {
                case PathIterator.SEG_CLOSE:
                    sb.append("Z ");
                    break;
                case PathIterator.SEG_CUBICTO:
                    sb.append("C ");
                    sb.append(arr[0]).append(" ");
                    sb.append(arr[1]).append(" ");
                    sb.append(arr[2]).append(" ");
                    sb.append(arr[3]).append(" ");
                    sb.append(arr[4]).append(" ");
                    sb.append(arr[5]).append(" ");
                    break;
                case PathIterator.SEG_LINETO:
                    sb.append("L ");
                    sb.append(arr[0]).append(" ");
                    sb.append(arr[1]).append(" ");
                    break;
                case PathIterator.SEG_MOVETO:
                    sb.append("M ");
                    sb.append(arr[0]).append(" ");
                    sb.append(arr[1]).append(" ");
                    break;
                case PathIterator.SEG_QUADTO:
                    sb.append("Q ");
                    sb.append(arr[0]).append(" ");
                    sb.append(arr[1]).append(" ");
                    sb.append(arr[2]).append(" ");
                    sb.append(arr[3]).append(" ");
                    break;
            }
        }

        return sb.toString();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        PathParser parser = new PathParser(new StringReader(text));
        try {
            setValue(parser.Path());
        } catch (ParseException ex) {
            Logger.getLogger(Path2DDoubleEditor.class.getName()).log(Level.SEVERE, null, ex);
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean supportsCustomEditor()
    {
        return false;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

}
