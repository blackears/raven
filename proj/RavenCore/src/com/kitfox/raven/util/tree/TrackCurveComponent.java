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

package com.kitfox.raven.util.tree;

/**
 *
 * @author kitfox
 */
abstract public class TrackCurveComponent
{
//    private final PropertyWrapper wrapper;
//    private final int frame;
//    private final Type type;
//
//    public TrackCurveComponent(PropertyWrapper wrapper, int frame, Type type)
//    {
//        this.wrapper = wrapper;
//        this.frame = frame;
//        this.type = type;
//    }

    protected final PropertyWrapper wrapper;
    
    public TrackCurveComponent(PropertyWrapper wrapper)
    {
        this.wrapper = wrapper;
    }

    /**
     * @return the wrapper
     */
    public PropertyWrapper getWrapper()
    {
        return wrapper;
    }

//    public double getNumericValue()
//    {
//        if (wrapper instanceof PropertyWrapperNumeric)
//        {
//            return ((PropertyWrapperNumeric)wrapper).getValueNumeric();
//        }
//        return 0;
//    }
}
