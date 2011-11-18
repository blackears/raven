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
public final class NodeDocumentProviderIndex
{
    private ArrayList<NodeDocumentProvider> provList = new ArrayList<NodeDocumentProvider>();
    
    private static NodeDocumentProviderIndex instance = new NodeDocumentProviderIndex();

    private NodeDocumentProviderIndex()
    {
        reload();
    }

    public static NodeDocumentProviderIndex inst()
    {
        return instance;
    }

    public void reload()
    {
        reload(NodeDocumentProviderIndex.class.getClassLoader());
    }

    public void reload(ClassLoader clsLoader)
    {
        provList.clear();

        ServiceLoader<NodeDocumentProvider> loader = ServiceLoader.load(NodeDocumentProvider.class, clsLoader);

        for (Iterator<NodeDocumentProvider> it = loader.iterator();
            it.hasNext();)
        {
            NodeDocumentProvider prov = it.next();
            provList.add(prov);
        }
    }

    public ArrayList<NodeDocumentProvider> getProviders()
    {
        return new ArrayList<NodeDocumentProvider>(provList);
    }

    public <T extends NodeDocument> NodeDocumentProvider<T> getProvider(Class<T> cls)
    {
        for (int i = 0; i < provList.size(); ++i)
        {
            NodeDocumentProvider prov = provList.get(i);
            if (prov.getNodeType().equals(cls))
            {
                return prov;
            }
        }
        return null;
    }

    public NodeDocumentProvider getProvider(String clazz)
    {
        for (int i = 0; i < provList.size(); ++i)
        {
            NodeDocumentProvider prov = provList.get(i);
            if (prov.getNodeType().getCanonicalName().equals(clazz))
            {
                return prov;
            }
        }
        return null;

    }

//    public <T extends NodeDocument> T createDocument(Class<T> cls)
//    {
//        return getProvider(cls).createDocument();
//    }
}
