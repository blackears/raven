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

import com.kitfox.raven.util.resource.ResourceCache;
import java.io.File;
import java.net.URI;

/**
 * Uses a token to lookup the data in the resource library.  Resource with
 * given token should return data of this type.
 *
 * @author kitfox
 */
public class PropertyDataResource<T> extends PropertyData<T>
{
//    private final int uid;
    private final URI uri;

    public PropertyDataResource(URI uri)
    {
        this.uri = uri;
    }

    @Override
    public T getValue(NodeSymbol sym)
    {
        if (sym == null)
        {
            return null;
        }

        ResourceCache cache = ResourceCache.inst();

        if (uri.isAbsolute())
        {
            return (T)cache.getResource(uri);
        }

        //Resolve against document root, if known
        File src = sym.getDocument().getEnv().getDocumentSource();
        if (src != null)
        {
            File parent = src.getParentFile();
            URI relUri = parent.toURI().resolve(uri);
            return (T)cache.getResource(relUri);
        }

        //TODO: resolve againt project

        //Could not find resource
        return null;
    }

    /**
     * @return the uid
     */
    public URI getUri()
    {
        return uri;
    }

    @Override
    public String toString()
    {
        return "res: " + uri;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final PropertyDataResource<T> other = (PropertyDataResource<T>) obj;
        if (this.uri != other.uri && (this.uri == null || !this.uri.equals(other.uri)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 89 * hash + (this.uri != null ? this.uri.hashCode() : 0);
        return hash;
    }


}
