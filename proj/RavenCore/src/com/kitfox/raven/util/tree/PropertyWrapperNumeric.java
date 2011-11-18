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
abstract public class PropertyWrapperNumeric<NodeType extends NodeObject, PropType>
        extends PropertyWrapper<NodeType, PropType>
{

    public PropertyWrapperNumeric(NodeType node,
            String name,
            Class<PropType> cls,
            PropertyData<PropType> initialValue)
    {
        super(node, name, cls, initialValue);
    }

    public PropertyWrapperNumeric(NodeType node,
            String name,
            int flags,
            Class<PropType> cls,
            PropertyData<PropType> initialValue)
    {
        super(node, name, flags, cls, initialValue);
    }

    abstract public double getValueNumeric();
    abstract public void setValueNumeric(double value);
}
