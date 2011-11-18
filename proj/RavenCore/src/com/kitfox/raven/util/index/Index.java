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

package com.kitfox.raven.util.index;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
abstract public class Index<T>
{
    protected class Entry
    {
        private final int id;
        private final Class<? extends T> cls;
        WeakReference<? extends T> ref;

        public Entry(int id, Class<? extends T> cls)
        {
            this.id = id;
            this.cls = cls;
        }

        public T getValue()
        {
            T value = ref == null ? null : ref.get();
            if (value == null)
            {
                value = createInstance(id);
                ref = new WeakReference<T>(value);
            }
            return value;
        }

        /**
         * @return the id
         */
        public int getId() {
            return id;
        }

        /**
         * @return the cls
         */
        public Class<? extends T> getCls() {
            return cls;
        }


    }

    final HashMap<Class<? extends T>, Entry> clsMap = new HashMap<Class<? extends T>, Entry>();
    final HashMap<Integer, Entry> intMap = new HashMap<Integer, Entry>();

    protected Index(int[] idList, Class<? extends T>[] clsList)
    {
        for (int i = 0; i < idList.length; ++i)
        {
            Entry entry = new Entry(idList[i], clsList[i]);
            clsMap.put(entry.getCls(), entry);
            intMap.put(entry.getId(), entry);
        }
    }

    public T getInstance(Class<? extends T> cls)
    {
        Entry entry = clsMap.get(cls);
        return entry.getValue();
    }

    public T getInstance(int id)
    {
        Entry entry = intMap.get(id);
        return entry.getValue();
    }

    abstract protected T createInstance(int id);
}
