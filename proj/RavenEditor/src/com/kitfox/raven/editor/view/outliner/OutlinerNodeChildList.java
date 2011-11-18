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
import com.kitfox.raven.util.tree.ChildWrapperListener;
import com.kitfox.raven.util.tree.ChildWrapperWeakListener;
import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.NodeObjectProviderIndex;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.undo.History;
import com.kitfox.xml.schema.ravendocumentschema.NodeObjectType;
import com.kitfox.xml.schema.ravendocumentschema.RavenTransferableType;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;

/**
 * Children are fixed.  They cannot be renamed, moved, deleted or extended.
 *
 * @author kitfox
 */
public class OutlinerNodeChildList extends OutlinerNode
        implements ChildWrapperListener
{
    public static final ImageIcon ICON = new ImageIcon(OutlinerNodeChildList.class.getResource("/icons/node/childGroup.png"));

    final ChildWrapperList wrapper;
    ArrayList<OutlinerNode> childNodes = new ArrayList<OutlinerNode>();

    final ChildWrapperWeakListener childrenListener;

    OutlinerNodeChildList(OutlinerTreeModel model, OutlinerNode parent, ChildWrapperList wrapper)
    {
        super(model, parent);
        this.wrapper = wrapper;

        childrenListener = new ChildWrapperWeakListener(this, wrapper);
        wrapper.addChildWrapperListener(childrenListener);

        for (int i = 0; i < wrapper.size(); ++i)
        {
            childNodes.add(wrapNode(model, this, wrapper.get(i)));
        }
    }


    @Override
    public Object getChild(int index)
    {
        return childNodes.get(index);
    }

    @Override
    public int getChildCount()
    {
        return childNodes.size();
    }

    @Override
    public boolean isLeaf()
    {
        return false;
    }

    @Override
    public int getIndexOfChild(OutlinerNode outlinerNode)
    {
        return childNodes.indexOf(outlinerNode);
    }

    @Override
    public String getName()
    {
        return wrapper.getName();
    }

    @Override
    public void setName(String name)
    {
        //Cannot change the name of child lists
        //throw new UnsupportedOperationException("Cannot change child list name.");
    }

    @Override
    public boolean isNameModifiable()
    {
        return false;
    }

    @Override
    public Icon getIcon()
    {
        return ICON;
    }

    @Override
    public String getTooltip()
    {
        return wrapper.getChildType().getSimpleName();
    }

    @Override
    public void childWrapperNodeAdded(ChildWrapperEvent evt)
    {
        int index = evt.getIndex();
        OutlinerNode child = wrapNode(getModel(),this, evt.getNode());
        childNodes.add(index, child);

        getModel().fireTreeNodesInserted(getPath(),
                new int[]{index},
                new Object[]{child});
    }

    @Override
    public void childWrapperNodeRemoved(ChildWrapperEvent evt)
    {
        int index = evt.getIndex();
        OutlinerNode child = childNodes.remove(index);

        getModel().fireTreeNodesRemoved(getPath(),
                new int[]{index},
                new Object[]{child});
    }

    @Override
    public JPopupMenu getPopupMenu()
    {
        ArrayList<NodeObjectProvider> list = 
                NodeObjectProviderIndex.inst().getProvidersExtending(wrapper.getChildType());

        Collections.sort(list);

        JPopupMenu menu = new JPopupMenu("Add Node");
        for (NodeObjectProvider prov: list)
        {
            menu.add(new CreateComponentAction(wrapper, prov));
        }

        return menu;
    }

    //------------------------------------------
    class CreateComponentAction extends AbstractAction
    {
        final ChildWrapperList wrapper;
        final NodeObjectProvider prov;

        private CreateComponentAction(
                ChildWrapperList wrapper, NodeObjectProvider prov)
        {
            super(prov.getName(), prov.getIcon());
            this.wrapper = wrapper;
            this.prov = prov;
        }

        @Override
        public void actionPerformed(ActionEvent evt)
        {
            NodeObject node = prov.createNode(wrapper.getNode().getDocument());

            String name = prov.getNodeType().getSimpleName();
            name = name.substring(0, 1).toLowerCase() + name.substring(1);
            name = wrapper.getNode().getDocument().createUniqueName(name);

//            node.name.setValue(name, false);
            node.setName(name);
            wrapper.add(node);

            getModel().expandNode(OutlinerNodeChildList.this);
        }
    }

    @Override
    public OutlinerNode findNode(NodeObject node)
    {
        for (OutlinerNode child: childNodes)
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
    public boolean paste(int index, RavenTransferableType xferLayers)
    {
        NodeDocument doc = wrapper.getNode().getDocument();

        History hist = doc.getHistory();
        hist.beginTransaction("Paste");

        for (NodeObjectType type: xferLayers.getNodes())
        {
            String cls = type.getClazz();
            NodeObjectProvider prov = NodeObjectProviderIndex.inst().getProvider(cls);

            if (wrapper.getChildType().isAssignableFrom(prov.getNodeType()))
            {
                NodeObject node = prov.createNode(doc, type);
                wrapper.add(index, node);
                ++index;
            }
        }

        hist.commitTransaction();

        return true;
    }


}
