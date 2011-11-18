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

package com.kitfox.raven.util.resource;

import com.kitfox.raven.util.tree.NodeObject;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ServiceLoader;
import javax.swing.JFileChooser;

/**
 *
 * @author kitfox
 */
public final class ResourceIndex
{
    JFileChooser fileChooser;
    private ArrayList<ResourceProvider> nodeList = new ArrayList<ResourceProvider>();
    
    private static ResourceIndex instance = new ResourceIndex();

    private ResourceIndex()
    {
        reload();
    }

    public static ResourceIndex inst()
    {
        return instance;
    }

    public void reload()
    {
        reload(ResourceIndex.class.getClassLoader());
    }

    public void reload(ClassLoader clsLoader)
    {
        nodeList.clear();

        ServiceLoader<ResourceProvider> loader = ServiceLoader.load(ResourceProvider.class, clsLoader);

        for (Iterator<ResourceProvider> it = loader.iterator();
            it.hasNext();)
        {
            ResourceProvider fact = it.next();
            nodeList.add(fact);
        }
    }

    public ArrayList<ResourceProvider> getProviders()
    {
        return new ArrayList<ResourceProvider>(nodeList);
    }

    public <T extends NodeObject> ArrayList<ResourceProvider> getProvidersExtending(Class<T> cls)
    {
        ArrayList<ResourceProvider> list = new ArrayList<ResourceProvider>();

        for (int i = 0; i < nodeList.size(); ++i)
        {
            ResourceProvider prov = nodeList.get(i);
            if (cls.isAssignableFrom(prov.getResourceEditorClass()))
            {
                list.add(prov);
            }
        }
        return list;
    }

    public <T> ResourceProvider<T> getProvider(Class<T> cls)
    {
        for (int i = 0; i < nodeList.size(); ++i)
        {
            ResourceProvider prov = nodeList.get(i);
            if (prov.getResourceEditorClass().equals(cls))
            {
                return prov;
            }
        }
        return null;
    }

    public JFileChooser getFileChooser()
    {
        if (fileChooser == null)
        {
            fileChooser = new JFileChooser();
            for (int i = 0; i < nodeList.size(); ++i)
            {
                ResourceProvider prov = nodeList.get(i);
                fileChooser.addChoosableFileFilter(prov.getFileFilter());
            }

            fileChooser.setAcceptAllFileFilterUsed(true);
            fileChooser.setMultiSelectionEnabled(true);
        }
        return fileChooser;

    }

    public ResourceProvider getProvider(URI uri)
    {
        for (ResourceProvider prov: nodeList)
        {
            if (prov.accepts(uri))
            {
                return prov;
            }
        }
        return null;
    }
}
