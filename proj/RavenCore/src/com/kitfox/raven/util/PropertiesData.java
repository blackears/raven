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

package com.kitfox.raven.util;

import java.util.Properties;

/**
 *
 * @author kitfox
 */
public class PropertiesData
{
    final Properties properties;

    public PropertiesData(Properties properties)
    {
        this.properties = properties;
    }
    
    public boolean getBoolean(String name, boolean defaultValue)
    {
        String value = properties.getProperty(name);
        if (value == null)
        {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
    
    public void setBoolean(String name, boolean value)
    {
        properties.setProperty(name, "" + value);
    }
    
    public int getInt(String name, int defaultValue)
    {
        String value = properties.getProperty(name);
        if (value == null || "".equals(value))
        {
            return defaultValue;
        }
        try
        {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException ex)
        {
            return defaultValue;
        }
    }
    
    public void setInt(String name, int value)
    {
        properties.setProperty(name, "" + value);
    }
    
    public float getFloat(String name, float defaultValue)
    {
        String value = properties.getProperty(name);
        if (value == null || "".equals(value))
        {
            return defaultValue;
        }
        try
        {
            return Float.parseFloat(value);
        }
        catch (NumberFormatException ex)
        {
            return defaultValue;
        }
    }
    
    public void setFloat(String name, float value)
    {
        properties.setProperty(name, "" + value);
    }
    
    public double getDouble(String name, double defaultValue)
    {
        String value = properties.getProperty(name);
        if (value == null || "".equals(value))
        {
            return defaultValue;
        }
        try
        {
            return Double.parseDouble(value);
        }
        catch (NumberFormatException ex)
        {
            return defaultValue;
        }
    }
    
    public void setDouble(String name, double value)
    {
        properties.setProperty(name, "" + value);
    }

    public <T extends Enum> T getEnum(String name, T defaultValue)
    {
        Class cls = defaultValue.getDeclaringClass();
        
        String value = properties.getProperty(name);
        if (value == null || "".equals(value))
        {
            return defaultValue;
        }
        
        for (Object obj: cls.getEnumConstants())
        {
            Enum e = (Enum)obj;
            if (value.equals(e.name()))
            {
                return (T)e;
            }
        }
        return defaultValue;
    }
    
    public <T extends Enum> void setEnum(String name, T value)
    {
        properties.setProperty(name, "" + value);
    }

    public String getString(String name, String defaultValue)
    {
        String value = properties.getProperty(name);
        return value == null ? defaultValue : value;
    }
    
    public void setString(String name, String value)
    {
        properties.setProperty(name, value);
    }
}
