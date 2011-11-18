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

package com.kitfox.raven.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author kitfox
 */
public class ChildList<T>
{
    private final Class<T> childrenClass;
    ArrayList<T> list = new ArrayList<T>();

    public ChildList(Class<T> childrenClass)
    {
        this.childrenClass = childrenClass;
    }

    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    public Object[] toArray() {
        return list.toArray();
    }

    

    public int size() {
        return list.size();
    }

    public T set(int index, T element) {
        return list.set(index, element);
    }

    public boolean remove(T o)
    {
        return list.remove(o);
    }

    public T remove(int index) {
        return list.remove(index);
    }

    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    public T get(int index) {
        return list.get(index);
    }

    public void ensureCapacity(int minCapacity) {
        list.ensureCapacity(minCapacity);
    }

    public boolean contains(T o)
    {
        return list.contains(o);
    }

    public void clear() {
        list.clear();
    }

    public boolean addAll(int index, Collection<? extends T> c) {
        return list.addAll(index, c);
    }

    public boolean addAll(Collection<? extends T> c) {
        return list.addAll(c);
    }

    public void add(int index, T element) {
        list.add(index, element);
    }

    public boolean add(T e) {
        return list.add(e);
    }

    /**
     * @return the childrenClass
     */
    public Class<T> getChildrenClass() {
        return childrenClass;
    }
}
