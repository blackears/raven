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

package com.kitfox.swf;

import com.kitfox.swf.tags.SWFTagLoader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 *
 * @author kitfox
 */
public class SWFTagIndex
{
    HashMap<Integer, SWFTagLoader> loaderMap = new HashMap<Integer, SWFTagLoader>();

    final static SWFTagIndex instance = new SWFTagIndex();
    
    private SWFTagIndex()
    {
        reload();
    }

    public static SWFTagIndex inst()
    {
        return instance;
    }
    
    private void reload()
    {
        ServiceLoader<SWFTagLoader> tagLoaders = ServiceLoader.load(SWFTagLoader.class);
        for (Iterator<SWFTagLoader> it = tagLoaders.iterator(); it.hasNext();)
        {
            SWFTagLoader loader = it.next();
            int id = loader.getTagId();
            loaderMap.put(id, loader);
        }
    }
    
    public SWFTagLoader getLoader(int tagId)
    {
        return loaderMap.get(tagId);
    }
}
