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

package com.kitfox.raven.editor.node.scene;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author kitfox
 */
public class SpaceCache<KeyType, ValueType>
{
    HashMap<KeyType, SoftReference<ValueType>> cache =
            new HashMap<KeyType, SoftReference<ValueType>>();

    public ValueType get(KeyType key)
    {
        SoftReference<ValueType> ref = cache.get(key);
        return ref == null ? null : ref.get();
    }

    public void put(KeyType key, ValueType value)
    {
        SoftReference<ValueType> ref = new SoftReference<ValueType>(value);
        cache.put(key, ref);
    }

    public void flush()
    {
        for (Iterator<SoftReference<ValueType>> it
                = cache.values().iterator(); it.hasNext();)
        {
            SoftReference<ValueType> ref = it.next();
            if (ref.get() == null)
            {
                it.remove();
            }
        }
    }
}
