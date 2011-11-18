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

import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.PropertyCustomEditor;
import com.kitfox.raven.util.tree.PropertyDataInline;
import com.kitfox.raven.util.tree.PropertyProvider;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperEditor;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class EnumEditor extends PropertyWrapperEditor<Enum>
        implements MouseListener
{
    final Class enumClass;

    public EnumEditor(PropertyWrapper wrapper)
    {
        super(wrapper);
        this.enumClass = wrapper.getPropertyType();
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
        if (getValue() == null)
        {
            return null;
        }

        Enum enumVal = getValueFlat();
        return enumVal == null
                ? "null"
                : enumVal.getDeclaringClass().getName().replace('$', '.') + "." + enumVal.name();
    }

    @Override
    public String getAsText()
    {
        Enum e = getValueFlat();
        return e == null ? "" : e.name();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        Object[] enumConsts = enumClass.getEnumConstants();
        for (Object enumValue: enumConsts)
        {
            if (((Enum)enumValue).name().equals(text))
            {
                setValue(new PropertyDataInline(enumValue));
                return;
            }
        }
        setValue(null);
    }

    @Override
    public String[] getTags()
    {
        Object[] enumConsts = enumClass.getEnumConstants();
        String[] tags = new String[enumConsts.length];
//        tags[0] = "";
        for (int i = 0; i < enumConsts.length; ++i)
        {
            tags[i] = ((Enum)enumConsts[i]).name();
        }
        return tags;
    }

    @Override
    public PropertyCustomEditor createCustomEditor()
    {
        return null;
    }

    @Override
    public boolean supportsCustomEditor()
    {
        return false;
    }

    //----------------------------

    @ServiceInst(service=PropertyProvider.class)
    public static class Provider extends PropertyProvider<Enum>
    {
        public Provider()
        {
            super(Enum.class);
        }

        @Override
        public PropertyWrapperEditor createEditor(PropertyWrapper wrapper)
        {
            return new EnumEditor(wrapper);
        }

        @Override
        public boolean isNumeric()
        {
            return true;
        }

        @Override
        public double asDouble(Enum value)
        {
            return value.ordinal();
        }

        @Override
        public Enum createNumericValue(PropertyWrapper wrapper, double value)
        {
            Class propType = wrapper.getPropertyType();
            Object[] enumConst = propType.getEnumConstants();

            int idx = Math.max(Math.min((int)value, enumConst.length - 1), 0);
            return (Enum)enumConst[idx];
        }

        @Override
        public String asText(Enum value)
        {
            return value.getDeclaringClass().getName().replace('$', '.') + "." + value.name();
        }

        @Override
        public Enum fromText(String text)
        {
            try {
                int idx = text.lastIndexOf('.');
                String clsName = text.substring(0, idx);
                String name = text.substring(idx + 1);
                Class enumCls = Class.forName(clsName);
                Object[] enumConsts = enumCls.getEnumConstants();
                for (Object enumValue : enumConsts)
                {
                    Enum value = (Enum) enumValue;
                    if (value.name().equals(name))
                    {
                        return value;
                    }
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(EnumEditor.class.getName()).log(Level.SEVERE, null, ex);
            }

            return null;
        }
    }

}
