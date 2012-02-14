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

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
@Deprecated
public class TrackCache<T>
{
    HashMap<Integer, SoftReference<PropertyData<T>>> cache
            = new HashMap<Integer, SoftReference<PropertyData<T>>>();

    public PropertyData<T> get(int frame)
    {
        SoftReference<PropertyData<T>> ref = cache.get(frame);
        return ref == null ? null : ref.get();
    }

    public void set(int frame, PropertyData<T> value)
    {
        cache.put(frame, new SoftReference(value));
    }
}
