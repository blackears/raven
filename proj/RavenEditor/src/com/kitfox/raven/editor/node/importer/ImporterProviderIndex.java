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

package com.kitfox.raven.editor.node.importer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 *
 * @author kitfox
 */
public final class ImporterProviderIndex
{
    private ArrayList<ImporterProvider> importerList = new ArrayList<ImporterProvider>();
    
    private static ImporterProviderIndex instance = new ImporterProviderIndex();

    private ImporterProviderIndex()
    {
        reload();
    }

    public static ImporterProviderIndex inst()
    {
        return instance;
    }

    public void reload()
    {
        reload(ImporterProviderIndex.class.getClassLoader());
    }

    public void reload(ClassLoader clsLoader)
    {
        importerList.clear();

        ServiceLoader<ImporterProvider> loader = ServiceLoader.load(ImporterProvider.class, clsLoader);

        for (Iterator<ImporterProvider> it = loader.iterator();
            it.hasNext();)
        {
            ImporterProvider fact = it.next();
            importerList.add(fact);
        }
    }

    public ArrayList<ImporterProvider> getProviders()
    {
        return new ArrayList<ImporterProvider>(importerList);
    }

    public ImporterProvider getProvider(String clazz)
    {
        for (ImporterProvider prov: importerList)
        {
            if (prov.getClass().getCanonicalName().equals(clazz))
            {
                return prov;
            }
        }
        return null;
    }
}
