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

package com.kitfox.raven.editor.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 *
 * @author kitfox
 */
public final class ViewProviderIndex
{
    private ArrayList<ViewProvider> viewList = new ArrayList<ViewProvider>();
    
    private static ViewProviderIndex instance = new ViewProviderIndex();

    private ViewProviderIndex()
    {
        reload();
    }

    public static ViewProviderIndex inst()
    {
        return instance;
    }

    public void reload()
    {
        reload(ViewProviderIndex.class.getClassLoader());
    }

    public void reload(ClassLoader clsLoader)
    {
        viewList.clear();

        ServiceLoader<ViewProvider> loader = ServiceLoader.load(ViewProvider.class, clsLoader);

        for (Iterator<ViewProvider> it = loader.iterator();
            it.hasNext();)
        {
            ViewProvider fact = it.next();
            viewList.add(fact);
        }
    }

    public ArrayList<ViewProvider> getProviders()
    {
        return new ArrayList<ViewProvider>(viewList);
    }

    public ViewProvider getProvider(String clazz)
    {
        for (ViewProvider prov: viewList)
        {
            if (prov.getClass().getCanonicalName().equals(clazz))
            {
                return prov;
            }
        }
        return null;
    }
}
