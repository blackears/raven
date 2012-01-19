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

import com.kitfox.cache.CacheFloat;
import com.kitfox.cache.CacheIdentifier;
import com.kitfox.cache.CacheList;
import com.kitfox.cache.CacheMap;
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import com.kitfox.coyote.shape.CyStroke;
import com.kitfox.coyote.shape.CyStrokeCap;
import com.kitfox.coyote.shape.CyStrokeJoin;
import com.kitfox.rabbit.util.NumberText;
import com.kitfox.raven.paint.RavenStroke;
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
public class RavenStrokeEditor implements PropertyEditor
{
    public static final String PROP_VALUE = "value";

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    RavenStroke value;
    String textValue = "";
    String codeGen = "null";

    @Override
    public void setValue(Object value)
    {
        RavenStroke oldValue = this.value;
        this.value = (RavenStroke)value;
        textValue = value == null ? ""
                : toCache().toString();
        if (oldValue != null || value != null)
        {
            propertyChangeSupport.firePropertyChange(PROP_VALUE, oldValue, value);
        }
    }

    @Override
    public RavenStroke getValue()
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

        RavenStroke stroke = createFromStyle(textValue);
        setValue(stroke);
        codeGen = toCodeGen();
    }

    @Override
    public String[] getTags()
    {
        return null;
    }

    @Override
    public Component getCustomEditor()
    {
        return new RavenStrokeCustomEditor(this);
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


    public static final String PROP_WIDTH = "width";
    public static final String PROP_CAP = "cap";
    public static final String PROP_JOIN = "join";
    public static final String PROP_MITERLIMIT = "miterLimit";
    public static final String PROP_DASH = "dash";
    public static final String PROP_DASHPHASE = "dashPhase";

    public static RavenStroke createFromStyle(String text)
    {
        StringReader reader = new StringReader(text);
        CacheParser parser = new CacheParser(reader);
        CacheMap map;
        try {
//        parser.Cache();
            map = parser.Map();
        } catch (ParseException ex) {
            Logger.getLogger(RavenStrokeEditor.class.getName()).log(Level.WARNING, null, ex);
            return null;
        }

        double width = map.getFloat(PROP_WIDTH, 1);
        CyStrokeCap cap = CyStrokeCap.valueOf(map.getIdentifierName(PROP_CAP, CyStrokeCap.SQUARE.name()));
        CyStrokeJoin join = CyStrokeJoin.valueOf(map.getIdentifierName(PROP_JOIN, CyStrokeJoin.MITER.name()));
        double miterLimit = map.getFloat(PROP_MITERLIMIT, 10);
        double dashPhase = map.getFloat(PROP_DASHPHASE, 0);

        CacheList dashList = (CacheList)map.get(PROP_DASH);
        double[] dash = dashList == null
                ? null
                : dashList.toDoubleArray(0);

        if (dash != null)
        {
            boolean allZero = true;
            for (int i = 0; i < dash.length; ++i)
            {
                if (dash[i] != 0)
                {
                    allZero = false;
                    break;
                }
            }
            if (allZero)
            {
                dash = null;
            }
        }

        return new RavenStroke(
                new CyStroke(width, cap, join, miterLimit, dash, dashPhase));
    }

    private float[] asFloat(double[] arr)
    {
        float[] ret = new float[arr.length];
        for (int i = 0; i < arr.length; ++i)
        {
            ret[i] = (float)arr[i];
        }
        return ret;
    }

    public CacheMap toCache()
    {
        CyStroke stroke = value.getStroke();
        
        float width = (float)stroke.getWidth();
        CyStrokeCap cap = stroke.getCap();
        CyStrokeJoin join = stroke.getJoin();
        float miterLimit = (float)stroke.getMiterLimit();
        double[] dash = stroke.getDash();
        float dashPhase = (float)stroke.getDashOffset();

        CacheMap map = new CacheMap();
        if (width != 1)
        {
            map.put(PROP_WIDTH, new CacheFloat(width));
        }
        if (cap != CyStrokeCap.SQUARE)
        {
            map.put(PROP_CAP, new CacheIdentifier(cap.toString()));
        }
        if (join != CyStrokeJoin.MITER)
        {
            map.put(PROP_JOIN, new CacheIdentifier(join.toString()));
        }
        if (miterLimit != 10)
        {
            map.put(PROP_MITERLIMIT, new CacheFloat(miterLimit));
        }
        if (dash != null && dash.length != 0)
        {
            CacheList list = new CacheList();
            for (double dashValue: dash)
            {
                list.add(new CacheFloat((float)dashValue));
            }
            map.put(PROP_DASH, list);
        }
        if (dashPhase != 0)
        {
            map.put(PROP_DASHPHASE, new CacheFloat(dashPhase));
        }
        return map;
    }

    public String toCodeGen()
    {
        if (value == null)
        {
            return null;
        }
        
        CyStroke stroke = value.getStroke();
        return String.format("new java.awt.BasicStroke(%f, %s, %s, %f, %s, %f)",
                stroke.getWidth(),
                stroke.getCap().toString(),
                stroke.getJoin().toString(),
                stroke.getMiterLimit(),
                NumberText.asStringCodeGen(asFloat(stroke.getDash())),
                stroke.getDashOffset());
    }
}
