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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 *
 * @author kitfox
 */
public final class PropertyProviderIndex
{
    private ArrayList<PropertyProvider> propList = new ArrayList<PropertyProvider>();
    
    private static PropertyProviderIndex instance = new PropertyProviderIndex();

    private PropertyProviderIndex()
    {
        reload();
    }

    public static PropertyProviderIndex inst()
    {
        return instance;
    }

    public void reload()
    {
        reload(PropertyProviderIndex.class.getClassLoader());
    }

    public void reload(ClassLoader clsLoader)
    {
        propList.clear();

        ServiceLoader<PropertyProvider> loader = ServiceLoader.load(PropertyProvider.class, clsLoader);

        for (Iterator<PropertyProvider> it = loader.iterator();
            it.hasNext();)
        {
            PropertyProvider fact = it.next();
            propList.add(fact);
        }
    }

    public ArrayList<PropertyProvider> getProviders()
    {
        return new ArrayList<PropertyProvider>(propList);
    }

    public <T> ArrayList<PropertyProvider> getProvidersExtending(Class<T> cls)
    {
        ArrayList<PropertyProvider> list = new ArrayList<PropertyProvider>();

        for (int i = 0; i < propList.size(); ++i)
        {
            PropertyProvider prov = propList.get(i);
            if (cls.isAssignableFrom(prov.getPropertyType()))
            {
                list.add(prov);
            }
        }
        return list;
    }

    public <T> PropertyProvider<T> getProviderBest(Class<T> cls)
    {
        PropertyProvider<T> bestProv = null;

        for (int i = 0; i < propList.size(); ++i)
        {
            PropertyProvider prov = propList.get(i);
            if (prov.getPropertyType().isAssignableFrom(cls))
            {
                //return prov;
                if (bestProv == null)
                {
                    bestProv = prov;
                }
                else if (bestProv.getPropertyType().isInterface())
                {
                    if (!prov.getPropertyType().isInterface())
                    {
                        bestProv = prov;
                    }
                    else if (bestProv.getPropertyType().isAssignableFrom(
                            prov.getPropertyType()))
                    {
                        bestProv = prov;
                    }
                }
                else if (bestProv.getPropertyType().isAssignableFrom(
                        prov.getPropertyType()))
                {
                    bestProv = prov;
                }
            }
        }
        return bestProv;
    }
//
//    public PropertyProvider getProvider(String clazz)
//    {
//        for (int i = 0; i < nodeList.size(); ++i)
//        {
//            PropertyProvider prov = nodeList.get(i);
//            if (prov.getPropertyType().getCanonicalName().equals(clazz))
//            {
//                return prov;
//            }
//        }
//        return null;
//    }
}
