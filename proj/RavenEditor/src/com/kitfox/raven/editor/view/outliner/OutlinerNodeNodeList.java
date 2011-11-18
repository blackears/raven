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

import com.kitfox.raven.util.tree.ChildWrapperEvent;
import com.kitfox.raven.util.tree.ChildWrapperList;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.NodeObjectListener;
import com.kitfox.raven.util.tree.NodeObjectWeakListener;
import java.beans.PropertyChangeEvent;
import java.util.EventObject;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPopupMenu;

/**
 * Children are fixed.  They cannot be renamed, moved, deleted or extended.
 *
 * @author kitfox
 */
public class OutlinerNodeNodeList extends OutlinerNodeChildList
        implements NodeObjectListener
{
    private final NodeObject node;
    final NodeObjectWeakListener nodeListener;

    OutlinerNodeNodeList(OutlinerTreeModel model, OutlinerNode parent, NodeObject node)
    {
        super(model, parent, (ChildWrapperList)node.getChildWrapper(0));
        this.node = node;

        nodeListener = new NodeObjectWeakListener(this, node);
        node.addNodeObjectListener(nodeListener);
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
        JPopupMenu listMenu = super.getPopupMenu();

        Action[] actions = node.getActions();
        if (actions == null)
        {
            return listMenu;
        }

        JPopupMenu menu = new JPopupMenu();
        menu.add(listMenu);

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

        return super.findNode(node);
    }

}
