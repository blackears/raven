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

package com.kitfox.raven.editor.view.outliner;

import com.kitfox.raven.util.tree.ChildWrapper;
import com.kitfox.raven.util.tree.ChildWrapperEvent;
import com.kitfox.raven.util.tree.ChildWrapperList;
import com.kitfox.raven.util.tree.ChildWrapperSingle;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.NodeObjectListener;
import com.kitfox.raven.util.tree.NodeObjectWeakListener;
import com.kitfox.xml.schema.ravendocumentschema.NodeTransferableType;
import java.beans.PropertyChangeEvent;
import java.util.EventObject;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPopupMenu;

/**
 * Children list is unchanging.  Can be of mixed data type.
 *
 * @author kitfox
 */
public class OutlinerNodeNodeFixed extends OutlinerNode
        implements NodeObjectListener
{
    final OutlinerNode[] children;
    private final NodeObject node;

    final NodeObjectWeakListener nodeListener;

    OutlinerNodeNodeFixed(OutlinerTreeModel model, 
            OutlinerNode parent, NodeObject node)
    {
        super(model, parent);
        this.node = node;

        nodeListener = new NodeObjectWeakListener(this, node);
        node.addNodeObjectListener(nodeListener);

        int numChildren = node.getNumChildWrappers();
        children = new OutlinerNode[numChildren];
        for (int i = 0; i < numChildren; ++i)
        {
            ChildWrapper wrapper = node.getChildWrapper(i);

            if (wrapper instanceof ChildWrapperSingle)
            {
                children[i] = wrapNode(model, this, ((ChildWrapperSingle)wrapper).getChild());
            }
            else if (wrapper instanceof ChildWrapperList)
            {
                children[i] = new OutlinerNodeChildList(model, this, (ChildWrapperList)wrapper);
            }
            else
            {
                assert false;
            }
        }
    }

    @Override
    public Object getChild(int index)
    {
        return children[index];
    }

    @Override
    public int getChildCount()
    {
        return children.length;
    }

    @Override
    public boolean isLeaf()
    {
        return children.length == 0;
    }

    @Override
    public int getIndexOfChild(OutlinerNode outlinerNode)
    {
        for (int i = 0; i < children.length; ++i)
        {
            if (outlinerNode == children[i])
            {
                return i;
            }
        }
        return -1;
    }


    @Override
    public String getName()
    {
        return node.getName();
    }

    @Override
    public void setName(String name)
    {
        node.setName(name);
    }

    @Override
    public boolean isNameModifiable()
    {
        return true;
    }

    @Override
    public Icon getIcon()
    {
        return node.getIcon();
    }

    @Override
    public String getTooltip()
    {
        return node.getTooltipText();
    }

    @Override
    public void nodeNameChanged(EventObject evt)
    {
//        getModel().fireTreeNodesChanged(getPath(), null, null);
    }

    @Override
    public void nodePropertyChanged(PropertyChangeEvent evt)
    {
        if (NodeObject.PROP_NAME.equals(evt.getPropertyName()))
        {
            getModel().fireTreeNodesChanged(getPath(), null, null);
        }
    }

    @Override
    public void nodeChildAdded(ChildWrapperEvent evt)
    {
    }

    @Override
    public void nodeChildRemoved(ChildWrapperEvent evt)
    {
    }

    @Override
    public JPopupMenu getPopupMenu()
    {
        Action[] actions = node.getActions();
        if (actions == null)
        {
            return null;
        }

        JPopupMenu menu = new JPopupMenu();
        for (Action action: actions)
        {
            menu.add(action);
        }
        return menu;
    }

    /**
     * @return the node
     */
    public NodeObject getNode() {
        return node;
    }

    @Override
    public OutlinerNode findNode(NodeObject node)
    {
        if (this.node.equals(node))
        {
            return this;
        }

        for (OutlinerNode child: children)
        {
            OutlinerNode val = child.findNode(node);
            if (val != null)
            {
                return val;
            }
        }

        return null;
    }

    @Override
    public boolean paste(int index, NodeTransferableType xferLayers)
    {
        //Cannot paste onto a fixed node
        return false;
    }

}
