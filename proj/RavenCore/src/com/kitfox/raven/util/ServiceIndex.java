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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * A base class for the many types of indices that are built from Jar services.
 * Typically each service type will create a singleton which extends this.
 *
 * @author kitfox
 */
public class ServiceIndex<T>
{
    protected final Class<T> serviceType;
    
    protected ArrayList<T> serviceList = new ArrayList<T>();

    public ServiceIndex(Class<T> serviceType)
    {
        this.serviceType = serviceType;
        reload();
    }

    public final void reload()
    {
        reload(serviceType.getClassLoader());
    }

    public final void reload(ClassLoader clsLoader)
    {
        serviceList.clear();

        ServiceLoader<T> loader = ServiceLoader.load(serviceType, clsLoader);

        for (Iterator<T> it = loader.iterator();
            it.hasNext();)
        {
            T fact = it.next();
            serviceList.add(fact);
        }
    }
    
    public ArrayList<T> getServices()
    {
        return new ArrayList<T>(serviceList);
    }
    
    public T getServiceByClass(Class<? extends T> cls)
    {
        for (T service: serviceList)
        {
            if (service.getClass().equals(cls))
            {
                return service;
            }
        }
        return null;
    }
    
    public T getServiceByClass(String clsName)
    {
        for (T service: serviceList)
        {
            if (service.getClass().getName().equals(clsName))
            {
                return service;
            }
        }
        return null;
    }
    
    /**
     * @return the serviceType
     */
    public Class<T> getServiceType()
    {
        return serviceType;
    }
}
