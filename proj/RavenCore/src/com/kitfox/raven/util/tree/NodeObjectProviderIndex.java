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
public final class NodeObjectProviderIndex
{
    private ArrayList<NodeObjectProvider> nodeList = new ArrayList<NodeObjectProvider>();
    
    private static NodeObjectProviderIndex instance = new NodeObjectProviderIndex();

    private NodeObjectProviderIndex()
    {
        reload();
    }

    public static NodeObjectProviderIndex inst()
    {
        return instance;
    }

    public void reload()
    {
        reload(NodeObjectProviderIndex.class.getClassLoader());
    }

    public void reload(ClassLoader clsLoader)
    {
        nodeList.clear();

        ServiceLoader<NodeObjectProvider> loader = ServiceLoader.load(NodeObjectProvider.class, clsLoader);

        for (Iterator<NodeObjectProvider> it = loader.iterator();
            it.hasNext();)
        {
            NodeObjectProvider fact = it.next();
            nodeList.add(fact);
        }
    }

    public ArrayList<NodeObjectProvider> getProviders()
    {
        return new ArrayList<NodeObjectProvider>(nodeList);
    }

    public <T extends NodeObject> ArrayList<NodeObjectProvider> getProvidersExtending(Class<T> cls)
    {
        ArrayList<NodeObjectProvider> list = new ArrayList<NodeObjectProvider>();

        for (int i = 0; i < nodeList.size(); ++i)
        {
            NodeObjectProvider prov = nodeList.get(i);
            if (cls.isAssignableFrom(prov.getNodeType()))
            {
                list.add(prov);
            }
        }
        return list;
    }

    public <T extends NodeObject> NodeObjectProvider<T> getProvider(Class<T> cls)
    {
        for (int i = 0; i < nodeList.size(); ++i)
        {
            NodeObjectProvider prov = nodeList.get(i);
            if (prov.getNodeType().equals(cls))
            {
                return prov;
            }
        }
        return null;
    }

    public NodeObjectProvider getProvider(String clazz)
    {
        for (int i = 0; i < nodeList.size(); ++i)
        {
            NodeObjectProvider prov = nodeList.get(i);
            if (prov.getNodeType().getCanonicalName().equals(clazz))
            {
                return prov;
            }
        }
        return null;

    }

    public <T extends NodeObject> T createNode(Class<T> cls, NodeDocument doc)
    {
        return getProvider(cls).createNode(doc);
    }
}
