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
import java.util.Collection;

/**
 *
 * @author kitfox
 */
public class CacheList extends CacheIdentifier
{
    ArrayList<CacheElement> cache = new ArrayList<CacheElement>();

    public CacheList()
    {
    }

    public CacheList(int... data)
    {
        for (int i = 0; i < data.length; ++i)
        {
            cache.add(new CacheInteger(data[i]));
        }
    }

    public CacheList(String name, CacheElement... data)
    {
        super(name);
        for (int i = 0; i < data.length; ++i)
        {
            cache.add(data[i]);
        }
    }

    public void trimToSize() {
        cache.trimToSize();
    }

    public int size() {
        return cache.size();
    }

    public CacheElement set(int index, CacheElement element) {
        return cache.set(index, element);
    }

    public boolean remove(CacheElement o) {
        return cache.remove(o);
    }

    public CacheElement remove(int index) {
        return cache.remove(index);
    }

    public int lastIndexOf(Object o) {
        return cache.lastIndexOf(o);
    }

    public boolean isEmpty() {
        return cache.isEmpty();
    }

    public int indexOf(Object o) {
        return cache.indexOf(o);
    }

    public CacheElement get(int index) {
        return cache.get(index);
    }

    public void ensureCapacity(int minCapacity) {
        cache.ensureCapacity(minCapacity);
    }

    public boolean contains(CacheElement o) {
        return cache.contains(o);
    }

    public void clear() {
        cache.clear();
    }

    public boolean addAll(int index, Collection<? extends CacheElement> c) {
        return cache.addAll(index, c);
    }

    public boolean addAll(Collection<? extends CacheElement> c) {
        return cache.addAll(c);
    }

    public void add(int index, CacheElement element) {
        cache.add(index, element);
    }

    public boolean add(CacheElement e) {
        return cache.add(e);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append('(');
        for (int i = 0; i < cache.size(); ++i)
        {
            if (i > 0)
            {
                sb.append(',');
            }
            sb.append(cache.get(i));
        }
        sb.append(')');

        return sb.toString();
    }

    public float[] toFloatArray(float defaultValue)
    {
        float[] list = new float[cache.size()];
        for (int i = 0; i < list.length; ++i)
        {
            CacheElement ele = cache.get(i);
            list[i] = ele != null && ele instanceof CacheNumber
                    ? ((CacheNumber)ele).getValue().floatValue()
                    : defaultValue;
        }
        return list;
    }

    public double[] toDoubleArray(float defaultValue)
    {
        double[] list = new double[cache.size()];
        for (int i = 0; i < list.length; ++i)
        {
            CacheElement ele = cache.get(i);
            list[i] = ele != null && ele instanceof CacheNumber
                    ? ((CacheNumber)ele).getValue().floatValue()
                    : defaultValue;
        }
        return list;
    }

    public int[] toIntegerArray(int defaultValue)
    {
        int[] list = new int[cache.size()];
        for (int i = 0; i < list.length; ++i)
        {
            CacheElement ele = cache.get(i);
            list[i] = ele != null && ele instanceof CacheNumber
                    ? ((CacheNumber)ele).getValue().intValue()
                    : defaultValue;
        }
        return list;
    }

    public String getIdentifierName(int idx, String defaultValue)
    {
        if (idx < 0 || idx >= size())
        {
            return defaultValue;
        }
        CacheElement ele = get(idx);
        return ele == null || !(ele instanceof CacheIdentifier)
                ? defaultValue
                : ((CacheIdentifier)ele).getName();
    }

    public String getString(int idx, String defaultValue)
    {
        if (idx < 0 || idx >= size())
        {
            return defaultValue;
        }
        CacheElement ele = get(idx);
        return ele == null || !(ele instanceof CacheString)
                ? defaultValue
                : ((CacheString)ele).getValue();
    }

    public float getFloat(int idx, float defaultValue)
    {
        if (idx < 0 || idx >= size())
        {
            return defaultValue;
        }
        CacheElement ele = get(idx);
        return ele == null || !(ele instanceof CacheFloat)
                ? defaultValue
                : ((CacheFloat)ele).getValue();
    }

    public int getInteger(int idx, int defaultValue)
    {
        if (idx < 0 || idx >= size())
        {
            return defaultValue;
        }
        CacheElement ele = get(idx);
        return ele == null || !(ele instanceof CacheInteger)
                ? defaultValue
                : ((CacheInteger)ele).getValue();
    }

    public boolean getBoolean(int idx, boolean defaultValue)
    {
        if (idx < 0 || idx >= size())
        {
            return defaultValue;
        }
        CacheElement ele = get(idx);
        return ele == null || !(ele instanceof CacheBoolean)
                ? defaultValue
                : ((CacheBoolean)ele).getValue();
    }

    public void add(boolean value)
    {
        add(new CacheBoolean(value));
    }

    public void add(int value)
    {
        add(new CacheInteger(value));
    }

    public void add(float value)
    {
        add(new CacheFloat(value));
    }

    public void add(String value)
    {
        add(new CacheString(value));
    }

}
