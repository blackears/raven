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

import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class ValueCache<T>
{
    FrameKey key;
    PropertyData<T> data;
    HashMap<Class, Object> userCache;

    public ValueCache(FrameKey key, PropertyData<T> data)
    {
        this.key = key;
        this.data = data;
    }

    public void copyState(ValueCache<T> cache)
    {
        this.data = cache.data;
        this.userCache = cache.userCache == null ? null 
                : new HashMap<Class, Object>(cache.userCache);
    }
    
    public <T> void setUserCache(Class<T> clsKey, T data)
    {
        if (userCache == null)
        {
            userCache = new HashMap<Class, Object>();
        }
        userCache.put(clsKey, data);
    }
    
    public <T> T getUserCache(Class<T> clsKey)
    {
        if (userCache == null)
        {
            return null;
        }
        return (T)userCache.get(clsKey);
    }
}
