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

import com.kitfox.xml.schema.ravendocumentschema.NodeObjectType;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author kitfox
 */
abstract public class NodeObjectProvider<T extends NodeObject>
        implements Comparable<NodeObjectProvider>
{
    private final Class<T> nodeType;
    private final String name;
    private final ImageIcon icon;

    private static ImageIcon DEFAULT_ICON
            = new ImageIcon(NodeObjectProvider.class.getResource("/icons/node/stack.png"));

    public NodeObjectProvider(Class<T> nodeType, String name)
    {
        this(nodeType, name, DEFAULT_ICON);
    }

    public NodeObjectProvider(Class<T> nodeType, String name, String iconPath)
    {
        this(nodeType, name, loadIcon(iconPath));
    }

    public NodeObjectProvider(Class<T> nodeType, String name, ImageIcon icon)
    {
        this.nodeType = nodeType;
        this.name = name;
        this.icon = icon;
    }

    private static ImageIcon loadIcon(String path)
    {
        return new ImageIcon(NodeObjectProvider.class.getResource(path));
    }

    public Class<T> getNodeType()
    {
        return nodeType;
    }

    public String getName()
    {
        return name;
    }

    public Icon getIcon()
    {
        return icon;
    }

    abstract public T createNode(int uid);

    final public T createNode(NodeSymbol doc)
    {
        return createNode(doc, null);
    }

    public T createNode(NodeSymbol symbol, NodeObjectType nodeType)
    {
        int uid;
        if (nodeType == null)
        {
            uid = symbol.allocUid();
        }
        else
        {
            uid = nodeType.getUid();
            //Check if node with this uid already exists in doc
            if (symbol.getNode(uid) == null)
            {
                //Make sure this UID will not be allocated to another node
                symbol.advanceNextUid(uid);
            }
            else
            {
                //Collision
                uid = symbol.allocUid();
            }
        }

        T node = createNode(uid);
        node.load(symbol, nodeType);
        return node;
    }

    @Override
    public int compareTo(NodeObjectProvider obj)
    {
        return name.compareTo(obj.getName());
    }


}
