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

package com.kitfox.raven.util.tree;

import com.kitfox.xml.schema.ravendocumentschema.PluginPropertiesType;
import com.kitfox.xml.schema.ravendocumentschema.PluginPropertyEntry;
import com.kitfox.xml.schema.ravendocumentschema.PluginsType;
import java.util.HashMap;
import java.util.Properties;

/**
 *
 * @author kitfox
 */
public class PluginsManager
{
    HashMap<Class, Properties> pluginPropertyMap =
            new HashMap<Class, Properties>();

    public Properties getProperties(Class pluginClass)
    {
        Properties prop = pluginPropertyMap.get(pluginClass);
        if (prop == null)
        {
            prop = new Properties();
            pluginPropertyMap.put(pluginClass, prop);
        }
        return prop;
    }

    protected void load(PluginsType plugins)
    {
        if (plugins == null)
        {
            return;
        }

        for (PluginPropertiesType props: plugins.getProperties())
        {
            Properties properties = new Properties();
            pluginPropertyMap.put(props.getClass(), properties);

            for (PluginPropertyEntry entry: props.getProperty())
            {
                properties.setProperty(entry.getName(), entry.getValue());
            }
        }
    }

    public PluginsType export()
    {
        PluginsType pluginType = new PluginsType();
        for (Class cls: pluginPropertyMap.keySet())
        {
            PluginPropertiesType propType = new PluginPropertiesType();
            pluginType.getProperties().add(propType);

            propType.setClazz(cls.getCanonicalName());

            Properties props = pluginPropertyMap.get(cls);
            for (String propName: props.stringPropertyNames())
            {
                PluginPropertyEntry entry = new PluginPropertyEntry();
                propType.getProperty().add(entry);

                entry.setName(propName);
                entry.setValue(props.getProperty(propName));
            }
        }
        return pluginType;
    }
}
