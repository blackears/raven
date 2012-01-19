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

package com.kitfox.raven.paint;

import com.kitfox.raven.util.ServiceIndex;

/**
 *
 * @author kitfox
 */
public final class RavenPaintIndex extends ServiceIndex<RavenPaintProvider>
{
    private static RavenPaintIndex instance = new RavenPaintIndex();

    private RavenPaintIndex()
    {
        super(RavenPaintProvider.class);
    }

    public static RavenPaintIndex inst()
    {
        return instance;
    }

    public <T extends RavenPaint> RavenPaintProvider<T> getByPaint(Class<T> cls)
    {
        for (int i = 0; i < serviceList.size(); ++i)
        {
            RavenPaintProvider prov = serviceList.get(i);
            if (prov.getDataType().equals(cls))
            {
                return prov;
            }
        }
        return null;
    }

    public RavenPaintProvider getProviderSupporting(String textValue)
    {
        for (int i = 0; i < serviceList.size(); ++i)
        {
            RavenPaintProvider prov = serviceList.get(i);
            if (prov.canParse(textValue))
            {
                return prov;
            }
        }
        return null;
    }
}
