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
public class PropertyWrapperDouble<NodeType extends NodeObject>
        extends PropertyWrapperNumeric<NodeType, Double>
{
    public PropertyWrapperDouble(
            NodeType node,
            String name)
    {
        super(node, name, Double.class, new PropertyDataInline<Double>(0.0));
    }

    public PropertyWrapperDouble(
            NodeType node,
            String name,
            Double initialValue)
    {
        super(node, name, Double.class, new PropertyDataInline<Double>(initialValue));
    }

    public PropertyWrapperDouble(
            NodeType node,
            String name,
            PropertyData<Double> initialValue)
    {
        super(node, name, Double.class, initialValue);
    }

    public PropertyWrapperDouble(
            NodeType node,
            String name,
            int flags)
    {
        super(node, name, flags, Double.class, new PropertyDataInline<Double>(0.0));
    }

    public PropertyWrapperDouble(
            NodeType node,
            String name,
            int flags,
            Double initialValue)
    {
        super(node, name, flags, Double.class, new PropertyDataInline<Double>(initialValue));
    }

    public PropertyWrapperDouble(
            NodeType node,
            String name,
            int flags,
            PropertyData<Double> initialValue)
    {
        super(node, name, flags, Double.class, initialValue);
    }

    @Override
    public double getValueNumeric()
    {
        return getValue();
    }

    @Override
    public void setValueNumeric(double value)
    {
        setValue(value);
    }

}
