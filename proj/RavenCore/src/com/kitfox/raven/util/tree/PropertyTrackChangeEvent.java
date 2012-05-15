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

import java.util.EventObject;

/**
 *
 * @author kitfox
 */
public class PropertyTrackChangeEvent
        extends EventObject
{
    public PropertyTrackChangeEvent(PropertyWrapper source)
    {
        super(source);
    }

    @Override
    public PropertyWrapper getSource()
    {
        return (PropertyWrapper)super.getSource();
    }

}
