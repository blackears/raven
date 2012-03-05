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

package com.kitfox.raven.editor.stroke;

import com.kitfox.cache.CacheElement;
import com.kitfox.cache.CacheIdentifier;
import com.kitfox.cache.CacheMap;
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;

/**
 *
 * @author kitfox
 */
@Deprecated
abstract public class RavenStrokeInline implements RavenStroke
{
    abstract public CacheIdentifier toCache();

    public static RavenStrokeInline create(String text)
    {
        CacheElement ele;
        try {
            ele = CacheParser.parse(text);
        } catch (ParseException ex) {
//            Logger.getLogger(RavenPaintInline.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        if (!(ele instanceof CacheIdentifier))
        {
            return null;
        }
        return create((CacheIdentifier)ele);
    }

    public static RavenStrokeInline create(CacheIdentifier ele)
    {
        String name = ele.getName();

        if (RavenStrokeNone.CACHE_NAME.equalsIgnoreCase(name))
        {
            return RavenStrokeNone.STROKE;
        }
        if (RavenStrokeBasic.CACHE_NAME.equalsIgnoreCase(name)
                && ele instanceof CacheMap)
        {
            return new RavenStrokeBasic((CacheMap)ele);
        }

        return null;
    }

}
