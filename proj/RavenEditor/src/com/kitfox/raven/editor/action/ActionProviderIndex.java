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

package com.kitfox.raven.editor.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 *
 * @author kitfox
 */
public final class ActionProviderIndex
{
    private ArrayList<ActionProvider> providerList = new ArrayList<ActionProvider>();
    
    private static ActionProviderIndex instance = new ActionProviderIndex();

    private ActionProviderIndex()
    {
        reload();
    }

    public static ActionProviderIndex inst()
    {
        return instance;
    }

    public void reload()
    {
        reload(ActionProviderIndex.class.getClassLoader());
    }

    public void reload(ClassLoader clsLoader)
    {
        providerList.clear();

        ServiceLoader<ActionProvider> loader = ServiceLoader.load(ActionProvider.class, clsLoader);

        for (Iterator<ActionProvider> it = loader.iterator();
            it.hasNext();)
        {
            ActionProvider fact = it.next();
            providerList.add(fact);
        }
    }

    public ArrayList<ActionProvider> getProviders()
    {
        return new ArrayList<ActionProvider>(providerList);
    }

    public <T extends ActionProvider> ActionProvider getProvider(Class<T> cls)
    {
        for (int i = 0; i < providerList.size(); ++i)
        {
            ActionProvider prov = providerList.get(i);
            if (prov.getClass().equals(cls))
            {
                return prov;
            }
        }
        return null;
    }

    public ActionProvider getProvider(String clazz)
    {
        for (int i = 0; i < providerList.size(); ++i)
        {
            ActionProvider prov = providerList.get(i);
            if (prov.getClass().getCanonicalName().equals(clazz))
            {
                return prov;
            }
        }
        return null;

    }

}
