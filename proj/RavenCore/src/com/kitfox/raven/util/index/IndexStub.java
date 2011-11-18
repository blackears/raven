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

/**
 *
 * @author kitfox
 */
public class IndexStub<T, R extends T>
{
    private final Class<T> indexClass;
    private final Class<R> indexItemClass;

    public IndexStub(Class<T> indexClass, Class<R> indexItemClass)
    {
        this.indexClass = indexClass;
        this.indexItemClass = indexItemClass;
    }

    /**
     * @return the indexClass
     */
    public Class<T> getIndexClass() {
        return indexClass;
    }

    /**
     * @return the indexItemClass
     */
    public Class<R> getIndexItemClass() {
        return indexItemClass;
    }

    @Override
    public String toString()
    {
        return "" + indexClass.getCanonicalName() + " " + indexItemClass.getCanonicalName();
    }


}
