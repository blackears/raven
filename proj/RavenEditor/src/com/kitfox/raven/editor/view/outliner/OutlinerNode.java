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

import com.kitfox.raven.util.tree.ChildWrapperList;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.xml.schema.ravendocumentschema.NodeTransferableType;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

/**
 *
 * @author kitfox
 */
abstract public class OutlinerNode
{
    private final OutlinerNode parent;
    private final OutlinerTreeModel model;

    public OutlinerNode(OutlinerTreeModel model, OutlinerNode parent)
    {
        this.model = model;
        this.parent = parent;
    }

    public TreePath getPath()
    {
        return parent == null ? new TreePath(this)
                : parent.getPath().pathByAddingChild(this);
    }

    abstract public Object getChild(int index);
    abstract public int getChildCount();
    abstract public boolean isLeaf();
    abstract public int getIndexOfChild(OutlinerNode outlinerNode);

    abstract public String getName();

    abstract public void setName(String name);

    abstract public boolean isNameModifiable();

    abstract public Icon getIcon();
    abstract public String getTooltip();
    abstract public JPopupMenu getPopupMenu();

    protected OutlinerNode wrapNode(OutlinerTreeModel model, OutlinerNode parent, NodeObject node)
    {
        int numChildren = node.getNumChildWrappers();

        if (numChildren == 1 && node.getChildWrapper(0) instanceof ChildWrapperList)
        {
            //Special visual shortcut for nodes with only one list child
            return new OutlinerNodeNodeList(model, parent, node);
        }

        //Fixed children
        return new OutlinerNodeNodeFixed(model, parent, node);
    }

    abstract public OutlinerNode findNode(NodeObject node);

    abstract public boolean paste(int index, NodeTransferableType xferLayers);

    /**
     * @return the parent
     */
    public OutlinerNode getParent()
    {
        return parent;
    }

    /**
     * @return the model
     */
    public OutlinerTreeModel getModel()
    {
        return model;
    }
}
