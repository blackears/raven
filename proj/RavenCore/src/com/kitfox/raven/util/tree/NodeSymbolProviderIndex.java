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

/**
 *
 * @author kitfox
 */
public final class NodeSymbolProviderIndex extends ServiceIndex<NodeSymbolProvider>
{
    private static NodeSymbolProviderIndex instance = new NodeSymbolProviderIndex();

    private NodeSymbolProviderIndex()
    {
        super(NodeSymbolProvider.class);
    }

    public static NodeSymbolProviderIndex inst()
    {
        return instance;
    }

    public NodeSymbolProvider getProvider(String clazz)
    {
        for (int i = 0; i < serviceList.size(); ++i)
        {
            NodeSymbolProvider prov = serviceList.get(i);
            if (prov.getNodeType().getCanonicalName().equals(clazz))
            {
                return prov;
            }
        }
        return null;
    }

    public <T extends NodeSymbol> NodeSymbolProvider<T>
            getProvider(Class<T> clazz)
    {
        for (int i = 0; i < serviceList.size(); ++i)
        {
            NodeSymbolProvider prov = serviceList.get(i);
            if (prov.getNodeType().equals(clazz))
            {
                return prov;
            }
        }
        return null;
    }
}
