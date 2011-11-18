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

package com.kitfox.raven.util.cursor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 *
 * @author kitfox
 */
public final class CursorProviderIndex
{
    private ArrayList<CursorProvider> cursorList = new ArrayList<CursorProvider>();
    
    private static CursorProviderIndex instance = new CursorProviderIndex();

    private CursorProviderIndex()
    {
        reload();
    }

    public static CursorProviderIndex inst()
    {
        return instance;
    }

    public void reload()
    {
        reload(CursorProviderIndex.class.getClassLoader());
    }

    public void reload(ClassLoader clsLoader)
    {
        cursorList.clear();

        ServiceLoader<CursorProvider> loader = ServiceLoader.load(CursorProvider.class, clsLoader);

        for (Iterator<CursorProvider> it = loader.iterator();
            it.hasNext();)
        {
            CursorProvider fact = it.next();
            cursorList.add(fact);
        }
    }

    public ArrayList<CursorProvider> getProviders()
    {
        return new ArrayList<CursorProvider>(cursorList);
    }

    public CursorProvider getProvider(Class<? extends CursorProvider> cls)
    {
        for (int i = 0; i < cursorList.size(); ++i)
        {
            CursorProvider prov = cursorList.get(i);
            if (prov.getClass().equals(cls))
            {
                return prov;
            }
        }
        return null;
    }

    public CursorProvider getProvider(String clazz)
    {
        for (int i = 0; i < cursorList.size(); ++i)
        {
            CursorProvider prov = cursorList.get(i);
            if (prov.getClass().getCanonicalName().equals(clazz))
            {
                return prov;
            }
        }
        return null;

    }

}
