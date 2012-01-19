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

import com.kitfox.raven.util.ServiceIndex;
import java.net.URI;
import javax.swing.JFileChooser;

/**
 *
 * @author kitfox
 */
public final class ResourceIndex extends ServiceIndex<ResourceProvider>
{
    JFileChooser fileChooser;
    private static ResourceIndex instance = new ResourceIndex();

    private ResourceIndex()
    {
        super(ResourceProvider.class);
    }

    public static ResourceIndex inst()
    {
        return instance;
    }

    public <T> ResourceProvider<T> getProvider(Class<T> cls)
    {
        for (int i = 0; i < serviceList.size(); ++i)
        {
            ResourceProvider prov = serviceList.get(i);
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
            for (int i = 0; i < serviceList.size(); ++i)
            {
                ResourceProvider prov = serviceList.get(i);
                fileChooser.addChoosableFileFilter(prov.getFileFilter());
            }

            fileChooser.setAcceptAllFileFilterUsed(true);
            fileChooser.setMultiSelectionEnabled(true);
        }
        return fileChooser;

    }

    public ResourceProvider getProvider(URI uri)
    {
        for (ResourceProvider prov: serviceList)
        {
            if (prov.accepts(uri))
            {
                return prov;
            }
        }
        return null;
    }
}
