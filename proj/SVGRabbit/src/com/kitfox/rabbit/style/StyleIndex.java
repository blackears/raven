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

package com.kitfox.rabbit.style;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 *
 * @author kitfox
 */
public class StyleIndex
{
    HashMap<String, StyleElementLoader> loaderMap = new HashMap<String, StyleElementLoader>();

    private static StyleIndex instance = new StyleIndex();

    private StyleIndex()
    {
        reload();
    }

    public static StyleIndex inst()
    {
        return instance;
    }

    public StyleElementLoader getLoader(String name)
    {
        return loaderMap.get(name);
    }

    public void reload()
    {
        reload(StyleIndex.class.getClassLoader());
    }

    public void reload(ClassLoader classLoader)
    {
        ServiceLoader<StyleElementLoader> loader =
                ServiceLoader.load(StyleElementLoader.class, classLoader);

        loaderMap.clear();
        for (Iterator<StyleElementLoader> it = loader.iterator(); it.hasNext();)
        {
            StyleElementLoader l = it.next();
            loaderMap.put(l.getName(), l);
        }
    }
}
