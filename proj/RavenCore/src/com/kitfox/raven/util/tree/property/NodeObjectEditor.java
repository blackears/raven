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

package com.kitfox.raven.util.tree.property;

import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.PropertyCustomEditor;
import com.kitfox.raven.util.tree.PropertyData;
import com.kitfox.raven.util.tree.PropertyDataReference;
import com.kitfox.raven.util.tree.PropertyProvider;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperEditor;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 *
 * @author kitfox
 */
public class NodeObjectEditor extends PropertyWrapperEditor<NodeObject>
{
    public NodeObjectEditor(PropertyWrapper wrapper)
    {
        super(wrapper);
    }

    @Override
    public boolean isPaintable()
    {
        return false;
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box)
    {
    }

    @Override
    public String getJavaInitializationString()
    {
        return getAsText();
    }

    @Override
    public String getAsText()
    {
        PropertyData<NodeObject> data = getWrapper().getData();
        if (data == null || !(data instanceof PropertyDataReference))
        {
            return "";
        }

        int uid = ((PropertyDataReference)data).getUid();
        NodeDocument doc = getWrapper().getNode().getDocument();
        NodeObject refNode = doc.getNode(uid);

        return refNode == null ? "" : refNode.getName();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        FindNode findNode = new FindNode(text);
        NodeDocument doc = getWrapper().getNode().getDocument();
        doc.visit(findNode);

        NodeObject refNode = findNode.getBestNode();

        setValue(refNode == null ? null
                : new PropertyDataReference(refNode.getUid()));
    }

    @Override
    public String[] getTags()
    {
        return null;
    }

    @Override
    public PropertyCustomEditor createCustomEditor()
    {
        return new NodeObjectCustomEditor(this);
    }

    @Override
    public boolean supportsCustomEditor()
    {
        return true;
    }


    //----------------------------

    @ServiceInst(service=PropertyProvider.class)
    public static class Provider extends PropertyProvider<NodeObject>
    {
        public Provider()
        {
            super(NodeObject.class);
        }

        @Override
        public PropertyWrapperEditor createEditor(PropertyWrapper wrapper)
        {
            return new NodeObjectEditor(wrapper);
        }

        @Override
        public String asText(NodeObject value)
        {
            throw new UnsupportedOperationException("Node objects should have no inline value");
        }

        @Override
        public NodeObject fromText(String text)
        {
            if (text == null || "".equals(text))
            {
                return null;
            }

            throw new UnsupportedOperationException("Node objects should have no inline value");
        }
    }
}
