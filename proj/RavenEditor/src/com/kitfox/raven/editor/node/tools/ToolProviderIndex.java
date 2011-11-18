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

package com.kitfox.raven.editor.node.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 *
 * @author kitfox
 */
public final class ToolProviderIndex
{
    private ArrayList<ToolProvider> toolList = new ArrayList<ToolProvider>();
    
    private static ToolProviderIndex instance = new ToolProviderIndex();

    private ToolProviderIndex()
    {
        reload();
    }

    public static ToolProviderIndex inst()
    {
        return instance;
    }

    public void reload()
    {
        reload(ToolProviderIndex.class.getClassLoader());
    }

    public void reload(ClassLoader clsLoader)
    {
        toolList.clear();

        ServiceLoader<ToolProvider> loader = ServiceLoader.load(ToolProvider.class, clsLoader);

        for (Iterator<ToolProvider> it = loader.iterator();
            it.hasNext();)
        {
            ToolProvider fact = it.next();
            toolList.add(fact);
        }
    }

    public ArrayList<ToolProvider> getProviders()
    {
        return new ArrayList<ToolProvider>(toolList);
    }

    public ToolProvider getProvider(String clazz)
    {
        for (ToolProvider prov: toolList)
        {
            if (prov.getClass().getCanonicalName().equals(clazz))
            {
                return prov;
            }
        }
        return null;
    }

    public <T extends ToolProvider> T getProvider(Class<T> clazz)
    {
        for (ToolProvider prov: toolList)
        {
            if (prov.getClass().equals(clazz))
            {
                return (T)prov;
            }
        }
        return null;
    }
}
