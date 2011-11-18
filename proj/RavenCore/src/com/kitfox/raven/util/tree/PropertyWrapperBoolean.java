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
public class PropertyWrapperBoolean<NodeType extends NodeObject>
        extends PropertyWrapperNumeric<NodeType, Boolean>
{
    public PropertyWrapperBoolean(
            NodeType node,
            String name)
    {
        super(node, name, Boolean.class, new PropertyDataInline<Boolean>(false));
    }

    public PropertyWrapperBoolean(
            NodeType node,
            String name,
            Boolean initialValue)
    {
        super(node, name, Boolean.class, new PropertyDataInline<Boolean>(initialValue));
    }

    public PropertyWrapperBoolean(
            NodeType node,
            String name,
            PropertyData<Boolean> initialValue)
    {
        super(node, name, Boolean.class, initialValue);
    }

    public PropertyWrapperBoolean(
            NodeType node,
            String name,
            int flags)
    {
        super(node, name, flags, Boolean.class, new PropertyDataInline<Boolean>(false));
    }

    public PropertyWrapperBoolean(
            NodeType node,
            String name,
            int flags,
            Boolean initialValue)
    {
        super(node, name, flags, Boolean.class, new PropertyDataInline<Boolean>(initialValue));
    }

    public PropertyWrapperBoolean(
            NodeType node,
            String name,
            int flags,
            PropertyData<Boolean> initialValue)
    {
        super(node, name, flags, Boolean.class, initialValue);
    }

    @Override
    public double getValueNumeric()
    {
        return Boolean.TRUE.equals(getValue()) ? 1 : 0;
    }

    @Override
    public void setValueNumeric(double value)
    {
        setValue(value > .5 ? true : false);
    }


}
