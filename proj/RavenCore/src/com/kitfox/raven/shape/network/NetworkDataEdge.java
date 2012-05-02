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

import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class NetworkDataEdge extends NetworkData
{
    //Edge data
    HashMap<Class<? extends NetworkDataType>, Object> dataMapEdge;
    
    //Left side of edge
    HashMap<Class<? extends NetworkDataType>, Object> dataMapLeft;

    //Right side of edge
    HashMap<Class<? extends NetworkDataType>, Object> dataMapRight;

    public NetworkDataEdge(HashMap<Class<? extends NetworkDataType>, Object> dataMapEdge, HashMap<Class<? extends NetworkDataType>, Object> dataMapLeft, HashMap<Class<? extends NetworkDataType>, Object> dataMapRight)
    {
        this.dataMapEdge = dataMapEdge;
        this.dataMapLeft = dataMapLeft;
        this.dataMapRight = dataMapRight;
    }
    
    public NetworkDataEdge()
    {
        this(new HashMap<Class<? extends NetworkDataType>, Object>(),
                new HashMap<Class<? extends NetworkDataType>, Object>(),
                new HashMap<Class<? extends NetworkDataType>, Object>());
    }
    
    public NetworkDataEdge(NetworkDataEdge data)
    {
        this(
                new HashMap<Class<? extends NetworkDataType>, Object>(data.dataMapEdge),
                new HashMap<Class<? extends NetworkDataType>, Object>(data.dataMapLeft),
                new HashMap<Class<? extends NetworkDataType>, Object>(data.dataMapRight));
    }

    public boolean isLeftSideEqualToRightSide()
    {
        return dataMapLeft.equals(dataMapRight);
    }
    
    public <R, T extends NetworkDataType<R>> R getLeft(Class<T> key)
    {
        return (R)dataMapLeft.get(key);
    }

    public <R, T extends NetworkDataType<R>> void putLeft(Class<T> key, R data)
    {
        dataMapLeft.put(key, data);
    }

    public <R, T extends NetworkDataType<R>> R getRight(Class<T> key)
    {
        return (R)dataMapRight.get(key);
    }

    public <R, T extends NetworkDataType<R>> void putRight(Class<T> key, R data)
    {
        dataMapRight.put(key, data);
    }

    public <R, T extends NetworkDataType<R>> R getEdge(Class<T> key)
    {
        return (R)dataMapEdge.get(key);
    }

    public <R, T extends NetworkDataType<R>> void putEdge(Class<T> key, R data)
    {
        dataMapEdge.put(key, data);
    }
}
