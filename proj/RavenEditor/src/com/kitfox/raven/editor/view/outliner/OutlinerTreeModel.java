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

import com.kitfox.raven.util.tree.NodeSymbol;
import com.kitfox.raven.util.tree.NodeObject;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author kitfox
 */
public class OutlinerTreeModel implements TreeModel
{
    protected final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    ArrayList<TreeModelListener> treeListeners = new ArrayList<TreeModelListener>();
    ArrayList<OutlinerTreeModelListener> outlinerListeners = new ArrayList<OutlinerTreeModelListener>();

    private final NodeSymbol document;
    final OutlinerNode root;

    public OutlinerTreeModel(NodeSymbol rootNode)
    {
        this.document = rootNode;
        this.root = new OutlinerNodeNodeFixed(this, null, rootNode);
    }

    public void addOutlinerTreeModelListener(OutlinerTreeModelListener l)
    {
        outlinerListeners.add(l);
    }

    public void removeOutlinerTreeModelListener(OutlinerTreeModelListener l)
    {
        outlinerListeners.remove(l);
    }

    @Override
    public OutlinerNode getRoot()
    {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index)
    {
        return ((OutlinerNode)parent).getChild(index);
    }

    @Override
    public int getChildCount(Object parent)
    {
        return ((OutlinerNode)parent).getChildCount();
    }

    @Override
    public boolean isLeaf(Object node)
    {
        return ((OutlinerNode)node).isLeaf();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue)
    {
        ((OutlinerNode)path.getLastPathComponent()).setName((String)newValue);
    }

    @Override
    public int getIndexOfChild(Object parent, Object child)
    {
        return ((OutlinerNode)parent).getIndexOfChild((OutlinerNode)child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l)
    {
        treeListeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l)
    {
        treeListeners.remove(l);
    }

    protected void fireTreeNodesChanged(TreePath path, int[] indices, Object[] children)
    {
        TreeModelEvent evt = new TreeModelEvent(this, path, indices, children);
        for (TreeModelListener l: treeListeners)
        {
            l.treeNodesChanged(evt);
        }
    }

    protected void fireTreeNodesInserted(TreePath path, int[] indices, Object[] children)
    {
        TreeModelEvent evt = new TreeModelEvent(this, path, indices, children);
        for (TreeModelListener l: treeListeners)
        {
            l.treeNodesInserted(evt);
        }
    }

    protected void fireTreeNodesRemoved(TreePath path, int[] indices, Object[] children)
    {
        TreeModelEvent evt = new TreeModelEvent(this, path, indices, children);
        for (TreeModelListener l: treeListeners)
        {
            l.treeNodesRemoved(evt);
        }
    }

    public void expandNode(OutlinerNode node)
    {
        OutlinerTreeModelEvent evt = new OutlinerTreeModelEvent(
                this, node);
        for (OutlinerTreeModelListener l: outlinerListeners)
        {
            l.requestExpandNode(evt);
        }
        
    }

    public OutlinerNode findNode(NodeObject node)
    {
        return root.findNode(node);
    }

    /**
     * @return the document
     */
    public NodeSymbol getDocument() {
        return document;
    }

}
