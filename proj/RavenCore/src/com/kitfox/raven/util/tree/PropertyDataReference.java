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

/**
 * Uses a token to lookup an object in our document to use as the value.
 *
 * @author kitfox
 */
public class PropertyDataReference<T> extends PropertyData<T>
{
    private final int uid;

    public PropertyDataReference(int uid)
    {
        this.uid = uid;
    }

    @Override
    public T getValue(NodeSymbol document)
    {
        return (T)document.getNode(uid);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof PropertyDataReference))
        {
            return false;
        }
        return uid == ((PropertyDataReference)obj).getUid();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + this.uid;
        return hash;
    }

    /**
     * @return the uid
     */
    public int getUid() {
        return uid;
    }

    @Override
    public String toString()
    {
        return "ref: " + uid;
    }

    public NodeObject getNode(NodeSymbol doc)
    {
        return doc.getNode(uid);
    }


}
