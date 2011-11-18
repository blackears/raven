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

package com.kitfox.raven.editor.menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 *
 * @author kitfox
 */
public final class MenuListProviderIndex
{
    private ArrayList<MenuListProvider> providerList = new ArrayList<MenuListProvider>();
    
    private static MenuListProviderIndex instance = new MenuListProviderIndex();

    private MenuListProviderIndex()
    {
        reload();
    }

    public static MenuListProviderIndex inst()
    {
        return instance;
    }

    public void reload()
    {
        reload(MenuListProviderIndex.class.getClassLoader());
    }

    public void reload(ClassLoader clsLoader)
    {
        providerList.clear();

        ServiceLoader<MenuListProvider> loader = ServiceLoader.load(MenuListProvider.class, clsLoader);

        for (Iterator<MenuListProvider> it = loader.iterator();
            it.hasNext();)
        {
            MenuListProvider fact = it.next();
            providerList.add(fact);
        }
    }

    public ArrayList<MenuListProvider> getProviders()
    {
        return new ArrayList<MenuListProvider>(providerList);
    }

    public <T extends MenuListProvider> MenuListProvider getProvider(Class<T> cls)
    {
        for (int i = 0; i < providerList.size(); ++i)
        {
            MenuListProvider prov = providerList.get(i);
            if (prov.getClass().equals(cls))
            {
                return prov;
            }
        }
        return null;
    }

    public MenuListProvider getProvider(String clazz)
    {
        for (int i = 0; i < providerList.size(); ++i)
        {
            MenuListProvider prov = providerList.get(i);
            if (prov.getClass().getCanonicalName().equals(clazz))
            {
                return prov;
            }
        }
        return null;

    }

}
