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

import com.kitfox.xml.schema.ravendocumentschema.PropertyDataType;
import com.kitfox.xml.schema.ravendocumentschema.PropertyStyleType;

/**
 * Define a value that is completely owned by the property itself.
 *
 * @author kitfox
 */
public class PropertyDataInline<T> extends PropertyData<T>
{
    final T value;

    public PropertyDataInline(T value)
    {
        this.value = value;
    }

    @Override
    public T getValue(NodeDocument document)
    {
        return value;
    }

    @Override
    public String toString()
    {
        return "" + value;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof PropertyDataInline))
        {
            return false;
        }

        PropertyDataInline iobj = (PropertyDataInline)obj;
        if (value == null && iobj.value == null)
        {
            return true;
        }
        return value != null && value.equals(iobj.value);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

 }
