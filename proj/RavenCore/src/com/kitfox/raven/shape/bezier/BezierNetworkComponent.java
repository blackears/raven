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

import com.kitfox.raven.util.planeData.PlaneDataProvider;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
@Deprecated
abstract public class BezierNetworkComponent
{
    private HashMap<Class<? extends PlaneDataProvider>, Object> data =
            new HashMap<Class<? extends PlaneDataProvider>, Object>();

    //Index used for identifying this component in subselections
    private int uid;

    public ArrayList<Class<? extends PlaneDataProvider>> getDataKeys()
    {
        return new ArrayList<Class<? extends PlaneDataProvider>>(data.keySet());
    }

    public HashMap<Class<? extends PlaneDataProvider>, Object> getData()
    {
        return new HashMap<Class<? extends PlaneDataProvider>, Object>(data);
    }

    /**
     * @param data the data to set
     */
    public void setData(HashMap<Class<? extends PlaneDataProvider>, Object> map)
    {
        this.data.putAll(map);
    }

    /**
     * @return the data
     */
    public <T extends PlaneDataProvider<R>, R> R getData(Class<T> key)
    {
        return (R)data.get(key);
    }

    /**
     * @param data the data to set
     */
    public <T extends PlaneDataProvider<R>, R> void setData(Class<T> key, R value)
    {
        this.data.put(key, value);
    }

    /**
     * @return the index
     */
    public int getUid()
    {
        return uid;
    }

    /**
     * @param uid the index to set
     */
    protected void setUid(int uid)
    {
        this.uid = uid;
    }

    abstract public Rectangle getBounds();

}
