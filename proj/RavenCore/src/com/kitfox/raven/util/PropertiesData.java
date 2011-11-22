/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
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
    
    public boolean getBoolean(String name)
    {
        String value = properties.getProperty(name);
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
