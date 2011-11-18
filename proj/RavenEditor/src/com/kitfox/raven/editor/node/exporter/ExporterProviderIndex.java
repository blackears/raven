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

package com.kitfox.raven.editor.node.exporter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 *
 * @author kitfox
 */
public final class ExporterProviderIndex
{
    private ArrayList<ExporterProvider> exporterList = new ArrayList<ExporterProvider>();
    
    private static ExporterProviderIndex instance = new ExporterProviderIndex();

    private ExporterProviderIndex()
    {
        reload();
    }

    public static ExporterProviderIndex inst()
    {
        return instance;
    }

    public void reload()
    {
        reload(ExporterProviderIndex.class.getClassLoader());
    }

    public void reload(ClassLoader clsLoader)
    {
        exporterList.clear();

        ServiceLoader<ExporterProvider> loader = ServiceLoader.load(ExporterProvider.class, clsLoader);

        for (Iterator<ExporterProvider> it = loader.iterator();
            it.hasNext();)
        {
            ExporterProvider fact = it.next();
            exporterList.add(fact);
        }
    }

    public ArrayList<ExporterProvider> getProviders()
    {
        return new ArrayList<ExporterProvider>(exporterList);
    }

    public <T extends ExporterProvider> T getProvider(Class<T> clazz)
    {
        for (ExporterProvider prov: exporterList)
        {
            if (prov.getClass().equals(clazz))
            {
                return (T)prov;
            }
        }
        return null;
    }

    public ExporterProvider getProvider(String clazz)
    {
        for (ExporterProvider prov: exporterList)
        {
            if (prov.getClass().getCanonicalName().equals(clazz))
            {
                return prov;
            }
        }
        return null;
    }
}
