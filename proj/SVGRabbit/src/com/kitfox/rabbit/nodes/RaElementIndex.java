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

package com.kitfox.rabbit.nodes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ServiceLoader;
import javax.xml.namespace.QName;

/**
 *
 * @author kitfox
 */
public class RaElementIndex
{
    HashMap<QName, RaElementLoader> loaderMap = new HashMap<QName, RaElementLoader>();

    private static RaElementIndex instance = new RaElementIndex();

    private RaElementIndex()
    {
        reload();
    }

    public static RaElementIndex inst()
    {
        return instance;
    }

    public RaElementLoader getLoader(QName name)
    {
        return loaderMap.get(name);
    }

    public void reload()
    {
        reload(RaElementIndex.class.getClassLoader());
    }

    public void reload(ClassLoader classLoader)
    {
        ServiceLoader<RaElementLoader> loader =
                ServiceLoader.load(RaElementLoader.class, classLoader);

        loaderMap.clear();
        for (Iterator<RaElementLoader> it = loader.iterator(); it.hasNext();)
        {
            RaElementLoader l = it.next();
            loaderMap.put(l.getQname(), l);
        }
    }
}
