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

package com.kitfox.raven.editor.paintLayout;

import com.kitfox.cache.CacheElement;
import com.kitfox.cache.CacheMap;
import com.kitfox.game.control.color.PaintLayout;
import com.kitfox.game.control.color.PaintLayoutSerializer;
import com.kitfox.raven.util.planeData.PlaneDataProvider;
import com.kitfox.raven.util.service.ServiceInst;

/**
 *
 * @author kitfox
 */
public class PaintLayoutProxy
{
    private final PaintLayout layout;

    public PaintLayoutProxy(PaintLayout layout)
    {
        this.layout = layout;
    }

    /**
     * @return the layout
     */
    public PaintLayout getLayout()
    {
        return layout;
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
        final PaintLayoutProxy other = (PaintLayoutProxy) obj;
        if (this.layout != other.layout && (this.layout == null || !this.layout.equals(other.layout)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 17 * hash + (this.layout != null ? this.layout.hashCode() : 0);
        return hash;
    }

    //----------------------------------------
    @ServiceInst(service=PlaneDataProvider.class)
    public static class PlaneData extends PlaneDataProvider<PaintLayoutProxy>
    {
        public PlaneData()
        {
            super(PaintLayoutProxy.class, "PaintLayoutProxy");
        }

        @Override
        public CacheElement asCache(PaintLayoutProxy data)
        {
//if (data.getLayout() == null)
//{
//    int j = 9;
//    assert false;
//}
            return data.getLayout().toCache();
        }

        @Override
        public PaintLayoutProxy parse(CacheElement cacheElement)
        {
            PaintLayout layout = PaintLayoutSerializer.create((CacheMap)cacheElement);
            return new PaintLayoutProxy(layout);
        }
    }
    
}
