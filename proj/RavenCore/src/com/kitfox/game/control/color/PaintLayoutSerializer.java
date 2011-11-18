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

package com.kitfox.game.control.color;

import com.kitfox.cache.CacheElement;
import com.kitfox.cache.CacheMap;
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class PaintLayoutSerializer
{
    public static PaintLayout create(String text)
    {
        try
        {
            CacheElement ele = CacheParser.parse(text);
            if (ele instanceof CacheMap)
            {
                return create((CacheMap)ele);
            }
        } catch (ParseException ex)
        {
            Logger.getLogger(PaintLayoutSerializer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public static PaintLayout create(CacheMap map)
    {
        String name = map.getName();
        if (PaintLayoutNone.CACHE_NAME.equals(name))
        {
            return PaintLayoutNone.LAYOUT;
        }
        else if(PaintLayoutLinear.CACHE_NAME.equals(name))
        {
            return new PaintLayoutLinear(map);
        }
        else if (PaintLayoutRadial.CACHE_NAME.equals(name))
        {
            return new PaintLayoutRadial(map);
        }
        else if (PaintLayoutTexture.CACHE_NAME.equals(name))
        {
            return new PaintLayoutTexture(map);
        }

        return null;
    }
}
