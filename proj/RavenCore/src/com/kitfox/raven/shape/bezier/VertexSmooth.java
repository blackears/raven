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

package com.kitfox.raven.shape.bezier;

import com.kitfox.cache.CacheElement;
import com.kitfox.cache.CacheIdentifier;
import com.kitfox.raven.util.planeData.PlaneDataProvider;
import com.kitfox.raven.util.service.ServiceInst;

/**
 *
 * @author kitfox
 */
public enum VertexSmooth
{
    TENSE,
    SMOOTH,
    CUSP;

    @ServiceInst(service=PlaneDataProvider.class)
    public static class PlaneData extends PlaneDataProvider<VertexSmooth>
    {
        public PlaneData()
        {
            super(VertexSmooth.class, "Smoothing");
        }

        @Override
        public CacheElement asCache(VertexSmooth data)
        {
            return new CacheIdentifier(data.name());
        }

        @Override
        public VertexSmooth parse(CacheElement cacheElement)
        {
            if (cacheElement instanceof CacheIdentifier)
            {
                String name = ((CacheIdentifier)cacheElement).getName();
                return VertexSmooth.valueOf(name);
            }
            return VertexSmooth.CUSP;
        }
    }
}
