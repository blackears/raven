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
    HashMap<Class<? extends NetworkDataType>, Object> dataMap 
            = new HashMap<Class<? extends NetworkDataType>, Object>();
    
    //Left side of edge
    HashMap<Class<? extends NetworkDataType>, Object> dataMapLeft 
            = new HashMap<Class<? extends NetworkDataType>, Object>();

    //Right side of edge
    HashMap<Class<? extends NetworkDataType>, Object> dataMapRight 
            = new HashMap<Class<? extends NetworkDataType>, Object>();
}
