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

import com.kitfox.raven.util.ServiceIndex;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public final class NodeObjectProviderIndex extends ServiceIndex<NodeObjectProvider>
{
    private static NodeObjectProviderIndex instance = new NodeObjectProviderIndex();

    private NodeObjectProviderIndex()
    {
        super(NodeObjectProvider.class);
    }

    public static NodeObjectProviderIndex inst()
    {
        return instance;
    }

    public <T extends NodeObject> ArrayList<NodeObjectProvider> getProvidersExtending(Class<T> cls)
    {
        ArrayList<NodeObjectProvider> list = new ArrayList<NodeObjectProvider>();

        for (int i = 0; i < serviceList.size(); ++i)
        {
            NodeObjectProvider prov = serviceList.get(i);
            if (cls.isAssignableFrom(prov.getNodeType()))
            {
                list.add(prov);
            }
        }
        return list;
    }

    public <T extends NodeObject> NodeObjectProvider<T> getProvider(Class<T> cls)
    {
        for (int i = 0; i < serviceList.size(); ++i)
        {
            NodeObjectProvider prov = serviceList.get(i);
            if (prov.getNodeType().equals(cls))
            {
                return prov;
            }
        }
        return null;
    }

    public NodeObjectProvider getProvider(String clazz)
    {
        for (int i = 0; i < serviceList.size(); ++i)
        {
            NodeObjectProvider prov = serviceList.get(i);
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
