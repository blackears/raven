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

package com.kitfox.raven.shape.network;

import com.kitfox.raven.util.ServiceIndex;

/**
 *
 * @author kitfox
 */
public final class NetworkDataTypeIndex extends ServiceIndex<NetworkDataType>
{
    private static NetworkDataTypeIndex instance = new NetworkDataTypeIndex();

    private NetworkDataTypeIndex()
    {
        super(NetworkDataType.class);
    }

    public static NetworkDataTypeIndex inst()
    {
        return instance;
    }

    public <T> NetworkDataType<T> getByData(Class<T> cls)
    {
        for (int i = 0; i < serviceList.size(); ++i)
        {
            NetworkDataType prov = serviceList.get(i);
            if (prov.getDataType().equals(cls))
            {
                return prov;
            }
        }
        return null;
    }

    public <T> NetworkDataType<T> getByData(String clsName)
    {
        for (int i = 0; i < serviceList.size(); ++i)
        {
            NetworkDataType prov = serviceList.get(i);
            if (prov.getDataType().getName().equals(clsName))
            {
                return prov;
            }
        }
        return null;
    }
}
