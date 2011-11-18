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

package com.kitfox.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 *
 * @author kitfox
 */
public class LineEncoderIndex
{
    static final LineEncoderIndex instance = new LineEncoderIndex();

    ServiceLoader<LineEncoderFactory> loader = ServiceLoader.load(LineEncoderFactory.class);

    HashMap<Class, LineEncoderFactory> factoryByClass = new HashMap<Class, LineEncoderFactory>();


    private LineEncoderIndex()
    {
        buildIndex();
    }

    public static LineEncoderIndex inst()
    {
        return instance;
    }

    public void setClassLoader(ClassLoader clsLoader)
    {
        loader = ServiceLoader.load(LineEncoderFactory.class, clsLoader);
        buildIndex();
    }

    public ArrayList<LineEncoderFactory> getComponentFactories()
    {
        ArrayList<LineEncoderFactory> facts = new ArrayList<LineEncoderFactory>();

        for (Iterator<LineEncoderFactory> it = loader.iterator(); it.hasNext();)
        {
            LineEncoderFactory fact = it.next();
            facts.add(fact);
        }

        return facts;
    }

    public void reload()
    {
        loader.reload();
        buildIndex();
    }


    private void buildIndex()
    {
        for (Iterator<LineEncoderFactory> it = loader.iterator(); it.hasNext();)
        {
            LineEncoderFactory fact = it.next();
            factoryByClass.put(fact.getTargetClass(), fact);
        }
    }

    public LineEncoderFactory getFactory(Class cls)
    {
        for (Class curCls = cls; curCls != null; curCls = curCls.getSuperclass())
        {
            LineEncoderFactory fact = factoryByClass.get(cls);
            if (fact != null)
            {
                return fact;
            }
        }

        //Check interfaces
        return null;
    }
}
