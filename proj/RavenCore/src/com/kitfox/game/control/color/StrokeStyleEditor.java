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

import com.kitfox.cache.CacheFloat;
import com.kitfox.cache.CacheIdentifier;
import com.kitfox.cache.CacheList;
import com.kitfox.cache.CacheMap;
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import com.kitfox.game.control.color.StrokeStyle.Cap;
import com.kitfox.game.control.color.StrokeStyle.Join;
import com.kitfox.rabbit.util.NumberText;
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
public class StrokeStyleEditor implements PropertyEditor
{
    public static final String PROP_VALUE = "value";

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    StrokeStyle value;
    String textValue = "";
    String codeGen = "null";

    @Override
    public void setValue(Object value)
    {
        StrokeStyle oldValue = this.value;
        this.value = (StrokeStyle)value;
        textValue = value == null ? ""
                : toCache().toString();
        if (oldValue != null || value != null)
        {
            propertyChangeSupport.firePropertyChange(PROP_VALUE, oldValue, value);
        }
    }

    @Override
    public StrokeStyle getValue()
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

        StrokeStyle stroke = createFromStyle(textValue);
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
        return new StrokeStyleCustomEditor(this);
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

//    public static enum Cap
//    {
//        BUTT(BasicStroke.CAP_BUTT),
//        ROUND(BasicStroke.CAP_ROUND),
//        SQUARE(BasicStroke.CAP_SQUARE);
//
//        final int value;
//
//        Cap(int value)
//        {
//            this.value = value;
//        }
//
//        int getValue()
//        {
//            return value;
//        }
//
//        static Cap getValueCap(int val)
//        {
//            for (Cap cap: values())
//            {
//                if (cap.value == val)
//                {
//                    return cap;
//                }
//            }
//            return null;
//        }
//    }
//
//    public static enum Join
//    {
//        BEVEL(BasicStroke.JOIN_BEVEL),
//        MITER(BasicStroke.JOIN_MITER),
//        ROUND(BasicStroke.JOIN_ROUND);
//
//        final int value;
//
//        Join(int value)
//        {
//            this.value = value;
//        }
//
//        int getValue()
//        {
//            return value;
//        }
//
//        static Join getValueJoin(int val)
//        {
//            for (Join join: values())
//            {
//                if (join.value == val)
//                {
//                    return join;
//                }
//            }
//            return null;
//        }
//    }


    public static StrokeStyle createFromStyle(String text)
    {
        StringReader reader = new StringReader(text);
        CacheParser parser = new CacheParser(reader);
        CacheMap map;
        try {
//        parser.Cache();
            map = parser.Map();
        } catch (ParseException ex) {
            Logger.getLogger(StrokeStyleEditor.class.getName()).log(Level.WARNING, null, ex);
            return null;
        }

        float width = map.getFloat(PROP_WIDTH, 1);
        Cap cap = Cap.valueOf(map.getIdentifierName(PROP_CAP, Cap.SQUARE.name()));
        Join join = Join.valueOf(map.getIdentifierName(PROP_JOIN, Join.MITER.name()));
        float miterLimit = map.getFloat(PROP_MITERLIMIT, 10);
        float dashPhase = map.getFloat(PROP_DASHPHASE, 0);

        CacheList dashList = (CacheList)map.get(PROP_DASH);
        float[] dash = dashList == null
                ? null
                : dashList.toFloatArray(0);

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

        return new StrokeStyle(width, cap, join, miterLimit, dash, dashPhase);
    }


    public CacheMap toCache()
    {
        float width = value.getWidth();
        Cap cap = value.getCap();
        Join join = value.getJoin();
        float miterLimit = value.getMiterLimit();
        float[] dash = value.getDash();
        float dashPhase = value.getDashPhase();

        CacheMap map = new CacheMap();
        if (width != 1)
        {
            map.put(PROP_WIDTH, new CacheFloat(width));
        }
        if (cap != Cap.SQUARE)
        {
            map.put(PROP_CAP, new CacheIdentifier(cap.toString()));
        }
        if (join != Join.MITER)
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
            for (float dashValue: dash)
            {
                list.add(new CacheFloat(dashValue));
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
        return value == null
                ? null
                : String.format("new java.awt.BasicStroke(%f, %s, %s, %f, %s, %f)",
                value.getWidth(),
                value.getCap().toString(),
                value.getJoin().toString(),
                value.getMiterLimit(),
                NumberText.asStringCodeGen(value.getDash()),
                value.getDashPhase());
    }
}
