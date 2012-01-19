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

import com.kitfox.coyote.math.CyGradientStops.Cycle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author kitfox
 */
public class CacheMap extends CacheIdentifier
{
    LinkedHashMap<String, CacheElement> cache = new LinkedHashMap<String, CacheElement>();

    public CacheMap()
    {
    }

    public CacheMap(String name)
    {
        super(name);
    }

    public Collection<CacheElement> values() {
        return cache.values();
    }

    public int size() {
        return cache.size();
    }

    public CacheElement remove(String key) {
        return cache.remove(key);
    }

    public void putAll(Map<? extends String, ? extends CacheElement> m) {
        cache.putAll(m);
    }

    public void putAll(CacheMap map)
    {
        cache.putAll(map.cache);
    }

    public CacheElement put(String key, CacheElement value) {
        return cache.put(key, value);
    }

    public Set<String> keySet() {
        return cache.keySet();
    }

    public boolean isEmpty() {
        return cache.isEmpty();
    }

    public CacheElement get(String key) {
        return cache.get(key);
    }

    public boolean containsValue(CacheElement value) {
        return cache.containsValue(value);
    }

    public boolean containsKey(String key) {
        return cache.containsKey(key);
    }

    public void clear() {
        cache.clear();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append('{');
        ArrayList<String> keys = new ArrayList<String>(cache.keySet());
        for (int i = 0; i < keys.size(); ++i)
        {
            String key = keys.get(i);
            if (i > 0)
            {
                sb.append(';');
            }
            sb.append(key);
            sb.append(':');
            sb.append(cache.get(key).toString());
        }
        sb.append('}');

        return sb.toString();
    }

    public String getIdentifierName(String name, String defaultValue)
    {
        CacheElement ele = get(name);
        return ele == null || !(ele instanceof CacheIdentifier)
                ? defaultValue
                : ((CacheIdentifier)ele).getName();
    }

    public String getString(String name, String defaultValue)
    {
        CacheElement ele = get(name);
        return ele == null || !(ele instanceof CacheString)
                ? defaultValue
                : ((CacheString)ele).getValue();
    }

    public float getFloat(String name, float defaultValue)
    {
        CacheElement ele = get(name);
        return ele == null || !(ele instanceof CacheFloat)
                ? defaultValue
                : ((CacheFloat)ele).getValue();
    }

    public float[] getFloatArray(String name, float[] value)
    {
        CacheElement ele = get(name);
        if (!(ele instanceof CacheList))
        {
            return value;
        }
        CacheList list = (CacheList)ele;
        float[] arr = new float[list.size()];
        for (int i = 0; i < arr.length; ++i)
        {
            CacheElement listEle = list.get(i);
            if (listEle instanceof CacheFloat)
            {
                arr[i] = ((CacheFloat)listEle).getValue();
                continue;
            }
            if (listEle instanceof CacheInteger)
            {
                arr[i] = ((CacheInteger)listEle).getValue();
                continue;
            }
            return value;
        }

        return arr;
    }

    public <T extends Enum> T getEnum(String name, T defaultValue)
    {
        CacheElement ele = get(name);
        if (ele == null || !(ele instanceof CacheIdentifier))
        {
            return defaultValue;
        }
        
        String value = ((CacheIdentifier)ele).getName();
        Object[] enumConst = defaultValue.getDeclaringClass().getEnumConstants();
        for (int i = 0; i < enumConst.length; ++i)
        {
            Enum e = (Enum)enumConst[i];
            if (e.name().equals(value))
            {
                return (T)e;
            }
        }
        return defaultValue;        
    }

    public int getInteger(String name, int defaultValue)
    {
        CacheElement ele = get(name);
        return ele == null || !(ele instanceof CacheInteger)
                ? defaultValue
                : ((CacheInteger)ele).getValue();
    }

    public int[] getIntegerArray(String name, int[] value)
    {
        CacheElement ele = get(name);
        if (!(ele instanceof CacheList))
        {
            return value;
        }
        CacheList list = (CacheList)ele;
        int[] arr = new int[list.size()];
        for (int i = 0; i < arr.length; ++i)
        {
            CacheElement listEle = list.get(i);
            if (!(listEle instanceof CacheInteger))
            {
                return value;
            }
            arr[i] = ((CacheInteger)listEle).getValue();
        }

        return arr;
    }

    public boolean getBoolean(String name, boolean defaultValue)
    {
        CacheElement ele = get(name);
        return ele == null || !(ele instanceof CacheBoolean)
                ? defaultValue
                : ((CacheBoolean)ele).getValue();
    }

    public void put(String name, boolean value)
    {
        put(name, new CacheBoolean(value));
    }

    public void put(String name, int value)
    {
        put(name, new CacheInteger(value));
    }

    public void put(String name, int[] value)
    {
        CacheList list = new CacheList();
        for (int i = 0; i < value.length; ++i)
        {
            list.add(value[i]);
        }
        put(name, list);
    }

    public void put(String name, float value)
    {
        put(name, new CacheFloat(value));
    }

    public void put(String name, float[] value)
    {
        CacheList list = new CacheList();
        for (int i = 0; i < value.length; ++i)
        {
            list.add(value[i]);
        }
        put(name, list);
    }

    public void put(String name, String value)
    {
        put(name, new CacheString(value));
    }

}
