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

import com.kitfox.raven.util.undo.History;
import com.kitfox.raven.util.undo.HistoryAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
abstract public class ChildWrapper<NodeType extends NodeObject,
        ChildType extends NodeObject>
{
    protected final NodeType node;
    protected final String name;
    protected final Class<ChildType> childType;

    ArrayList<ChildWrapperListener> listeners = new ArrayList<ChildWrapperListener>();

    public ChildWrapper(NodeType node, String name, Class<ChildType> childType)
    {
        this.node = node;
        this.name = name;
        this.childType = childType;

        node.registerChildWrapper(this);
    }

    public void addChildWrapperListener(ChildWrapperListener l)
    {
        listeners.add(l);
    }

    public void removeChildWrapperListener(ChildWrapperListener l)
    {
        listeners.remove(l);
    }

    protected void fireNodeAdded(int index, NodeObject childNode)
    {
        ChildWrapperEvent evt = new ChildWrapperEvent(this, index, childNode);
        ArrayList<ChildWrapperListener> list =
                new ArrayList<ChildWrapperListener>(listeners);
        for (int i = 0; i < list.size(); ++i)
        {
            list.get(i).childWrapperNodeAdded(evt);
        }
        node.notifyNodeAdded(evt);
    }

    protected void fireNodeRemoved(int index, NodeObject childNode)
    {
        ChildWrapperEvent evt = new ChildWrapperEvent(this, index, childNode);
        ArrayList<ChildWrapperListener> list =
                new ArrayList<ChildWrapperListener>(listeners);
        for (int i = 0; i < list.size(); ++i)
        {
            list.get(i).childWrapperNodeRemoved(evt);
        }
        node.notifyNodeRemoved(evt);
    }

    abstract public int size();
    abstract public ChildType get(int index);
    abstract public void set(int index, ChildType child);

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the childType
     */
    public Class<ChildType> getChildType() {
        return childType;
    }

    /**
     * @return the node
     */
    public NodeType getNode() {
        return node;
    }

    void visit(NodeVisitor visitor)
    {
        for (int i = 0; i < size(); ++i)
        {
            ChildType child = get(i);
            child.visit(visitor);
        }
    }

    public NodeObject get(String name)
    {
        for (int i = 0; i < size(); ++i)
        {
            NodeObject child = get(i);
            if (name.equals(child.getName()))
            {
                return child;
            }
        }
        return null;
    }

    public void getNames(Collection<String> list)
    {
        for (int i = 0; i < size(); ++i)
        {
            get(i).getNames(list);
        }
    }

    protected void doAction(HistoryAction action)
    {
        NodeSymbol doc = node.getSymbol();
        History hist = doc == null ? null : doc.getHistory();
        if (hist == null)
        {
            action.redo(null);
            return;
        }
        hist.doAction(action);
    }

    protected void beginTransaction(String name)
    {
        NodeSymbol doc = node.getSymbol();
        History hist = doc == null ? null : doc.getHistory();
        if (hist == null)
        {
            return;
        }
        hist.beginTransaction(name);
    }

    protected void commitTransaction()
    {
        NodeSymbol doc = node.getSymbol();
        History hist = doc == null ? null : doc.getHistory();
        if (hist == null)
        {
            return;
        }
        hist.commitTransaction();
    }

    public int getPrevKeyFrame(int curFrame)
    {
        int prevFrame = Integer.MIN_VALUE;

        for (int i = 0; i < size(); ++i)
        {
            ChildType child = get(i);
            prevFrame = Math.max(prevFrame,
                    child.getPrevKeyFrame(curFrame));
        }
        return prevFrame;
    }

    public int getNextKeyFrame(int curFrame)
    {
        int nextFrame = Integer.MAX_VALUE;

        for (int i = 0; i < size(); ++i)
        {
            ChildType child = get(i);
            nextFrame = Math.min(nextFrame,
                    child.getNextKeyFrame(curFrame));
        }
        return nextFrame;
    }

    public void buildUidIndex(HashMap<Integer, NodeObject> map)
    {
        for (int i = 0; i < size(); ++i)
        {
            ChildType child = get(i);
            if (child != null)
            {
                child.buildUidIndex(map);
            }
        }
    }

    protected void broadcastSymbolChanged(NodeSymbol doc)
    {
        for (int i = 0; i < size(); ++i)
        {
            ChildType child = get(i);
            child.broadcastSymbolChanged(doc);
        }
    }

    protected <T> T getNodeService(Class<T> service, boolean recursive)
    {
        for (int i = 0; i < size(); ++i)
        {
            ChildType child = get(i);
            T childService = child.getNodeService(service, recursive);
            if (childService != null)
            {
                return childService;
            }
        }
        return null;
    }

    protected <T> void getNodeServices(Class<T> service, ArrayList<T> appendList, boolean recursive)
    {
        for (int i = 0; i < size(); ++i)
        {
            ChildType child = get(i);
            child.getNodeServices(service, appendList, recursive);
        }
    }
}
