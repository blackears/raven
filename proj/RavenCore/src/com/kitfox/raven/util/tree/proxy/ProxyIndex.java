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

package com.kitfox.raven.util.tree.proxy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Static index for looking up proxy objects
 *
 * @author kitfox
 */
public final class ProxyIndex
{
    private ArrayList<ProxyProvider> nodeList = new ArrayList<ProxyProvider>();

    private static ProxyIndex instance = new ProxyIndex();

    private ProxyIndex()
    {
        reload();
    }

    public static ProxyIndex inst()
    {
        return instance;
    }

    public void reload()
    {
        reload(ProxyIndex.class.getClassLoader());
    }

    public void reload(ClassLoader clsLoader)
    {
        nodeList.clear();

        ServiceLoader<ProxyProvider> loader = ServiceLoader.load(ProxyProvider.class, clsLoader);

        for (Iterator<ProxyProvider> it = loader.iterator();
            it.hasNext();)
        {
            ProxyProvider fact = it.next();
            nodeList.add(fact);
        }
    }

    public ProxyProvider[] getProviders()
    {
        return nodeList.toArray(new ProxyProvider[nodeList.size()]);
    }

}
