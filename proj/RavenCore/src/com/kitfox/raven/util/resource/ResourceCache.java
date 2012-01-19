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

package com.kitfox.raven.util.resource;

import java.lang.ref.SoftReference;
import java.net.URI;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class ResourceCache
{
    HashMap<URI, ResInfo> resMap = new HashMap<URI, ResInfo>();

    private static ResourceCache instance = new ResourceCache();

    private ResourceCache()
    {
    }
    
    public static ResourceCache inst()
    {
        return instance;
    }
    
    public Object getResource(URI uri)
    {
        ResInfo info = resMap.get(uri);
        Object val = info == null ? null : info.data.get();
        if (val == null)
        {
            ResourceProvider prov = ResourceIndex.inst().getProvider(uri);
            if (prov == null)
            {
                return null;
            }

            val = prov.load(uri);
            info = new ResInfo(uri, val);
            resMap.put(uri, info);
        }
        return val;
    }

    //---------------------------
    class ResInfo
    {
        URI uri;
        SoftReference data;

        public ResInfo(URI uri, Object data)
        {
            this.uri = uri;
            this.data = new SoftReference(data);
        }

    }

}
