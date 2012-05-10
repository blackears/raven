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

import com.kitfox.raven.util.resource.game.ResourceGameLoaderAWT;
import com.kitfox.raven.util.undo.History;
import com.kitfox.raven.util.undo.HistoryAction;
import com.kitfox.xml.schema.ravendocumentschema.ChildrenType;
import com.kitfox.xml.schema.ravendocumentschema.NodeObjectType;
import com.kitfox.xml.schema.ravendocumentschema.PropertyType;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.Icon;

/**
 *
 * @author kitfox
 */
public class NodeObject
{
    protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private ArrayList<PropertyWrapper> propertyWrappers =
            new ArrayList<PropertyWrapper>();
    private ArrayList<ChildWrapper> childWrappers =
            new ArrayList<ChildWrapper>();
    private ArrayList<EventWrapper> eventWrappers =
            new ArrayList<EventWrapper>();

    private final int uid;

//    private NodeSymbol symbol;

    protected String name;
    public static final String PROP_NAME = "name";
    protected ChildWrapper parent;
    public static final String PROP_PARENT = "parent";

    ArrayList<NodeObjectListener> listeners = new ArrayList<NodeObjectListener>();

    public NodeObject(int uid)
    {
        this.uid = uid;
        this.name = lowerFirst(getClass().getSimpleName());
    }

    protected static String lowerFirst(String value)
    {
        return value.substring(0, 1).toLowerCase() + value.substring(1);
    }

    public void addNodeObjectListener(NodeObjectListener l)
    {
        listeners.add(l);
    }

    public void removeNodeObjectListener(NodeObjectListener l)
    {
        listeners.remove(l);
    }

    protected void fireNodeNameChanged()
    {
        EventObject evt = new EventObject(this);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).nodeNameChanged(evt);
        }
    }

    protected void fireNodePropertyChanged(PropertyChangeEvent evt)
    {
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).nodePropertyChanged(evt);
        }
    }

    protected void fireNodeChildAdded(ChildWrapperEvent evt)
    {
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).nodeChildAdded(evt);
        }
    }

    protected void fireNodeChildRemoved(ChildWrapperEvent evt)
    {
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).nodeChildRemoved(evt);
        }
    }

    /**
     * Called by PropertyWrapper when there is a change
     * @param evt
     */
    protected void notifyNodePropertyChanged(PropertyChangeEvent evt)
    {
        fireNodePropertyChanged(evt);
        NodeSymbol doc = getSymbol();
        if (doc != null)
        {
            doc.notifySymbolPropertyChanged(evt);
        }
    }

    protected void notifyNodeAdded(ChildWrapperEvent evt)
    {
        fireNodeChildAdded(evt);
        NodeSymbol doc = getSymbol();
        if (doc != null)
        {
            doc.notifySymbolNodeChildAdded(evt);
        }
    }

    protected void notifyNodeRemoved(ChildWrapperEvent evt)
    {
        fireNodeChildRemoved(evt);
        NodeSymbol doc = getSymbol();
        if (doc != null)
        {
            doc.notifySymbolNodeChildRemoved(evt);
        }
    }

    public Icon getIcon()
    {
        NodeObjectProvider provider =
                NodeObjectProviderIndex.inst().getProvider(getClass());
        return provider == null ? null : provider.getIcon();
    }

    public String getTooltipText()
    {
        return getClass().getSimpleName();
    }

    void registerPropertyWrapper(PropertyWrapper property)
    {
        propertyWrappers.add(property);
    }

    void registerChildWrapper(ChildWrapper child)
    {
        childWrappers.add(child);
    }

    void registerEventWrapper(EventWrapper event)
    {
        eventWrappers.add(event);
    }

    public int getNumPropertyWrappers()
    {
        return propertyWrappers.size();
    }

    public PropertyWrapper getPropertyWrapper(int index)
    {
        return propertyWrappers.get(index);
    }
    public PropertyWrapper getPropertyWrapper(String name)
    {
        for (int i = 0; i < propertyWrappers.size(); ++i)
        {
            PropertyWrapper wrap = propertyWrappers.get(i);
            if (wrap.getName().equals(name))
            {
                return wrap;
            }
        }
        return null;
    }


    public ArrayList<PropertyWrapper> getPropertyWrappers()
    {
        return new ArrayList<PropertyWrapper>(propertyWrappers);
    }

    public int getNumChildWrappers()
    {
        return childWrappers.size();
    }

    public ChildWrapper getChildWrapper(int index)
    {
        return childWrappers.get(index);
    }

    public ChildWrapper getChildWrapper(String name)
    {
        for (int i = 0; i < childWrappers.size(); ++i)
        {
            ChildWrapper wrap = childWrappers.get(i);
            if (wrap.getName().equals(name))
            {
                return wrap;
            }
        }
        return null;
    }

    public ArrayList<ChildWrapper> getChildWrappers()
    {
        return new ArrayList<ChildWrapper>(childWrappers);
    }

    public int getNumEventWrappers()
    {
        return eventWrappers.size();
    }

    public EventWrapper getEventWrapper(int index)
    {
        return eventWrappers.get(index);
    }

    public EventWrapper getEventWrapper(String name)
    {
        for (int i = 0; i < eventWrappers.size(); ++i)
        {
            EventWrapper wrap = eventWrappers.get(i);
            if (wrap.getName().equals(name))
            {
                return wrap;
            }
        }
        return null;
    }

    public ArrayList<EventWrapper> getEventWrappers()
    {
        return new ArrayList<EventWrapper>(eventWrappers);
    }

    public NodeObjectType export()
    {
        NodeObjectType type = new NodeObjectType();
        export(type);
        return type;
    }

    protected NodeObjectType export(NodeObjectType type)
    {
        type.setUid(uid);
        type.setName(name);
        type.setClazz(getClass().getCanonicalName());

        for (int i = 0; i < getNumPropertyWrappers(); ++i)
        {
            PropertyWrapper wrap = getPropertyWrapper(i);
            type.getProperty().add(wrap.export());
        }

        for (int i = 0; i < getNumChildWrappers(); ++i)
        {
            ChildWrapper wrap = getChildWrapper(i);

            ChildrenType childType = new ChildrenType();
            type.getChild().add(childType);

            childType.setName(wrap.getName());

            for (int j = 0; j < wrap.size(); ++j)
            {
                NodeObject obj = wrap.get(j);
                childType.getNode().add(obj.export());
            }
        }

//        for (EventWrapper wrap: eventWrappers)
//        {
//            type.getEvent().add(wrap.export());
//        }

        return type;
    }

    public void visit(NodeVisitor visitor)
    {
        visitor.visit(this);
        for (int i = 0; i < childWrappers.size(); ++i)
        {
            ChildWrapper wrapper = childWrappers.get(i);
            wrapper.visit(visitor);
        }
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Get the value of parent
     *
     * @return the value of parent
     */
    public ChildWrapper getParent()
    {
        return parent;
    }

    /**
     * Set the value of parent
     *
     * @param parent new value of parent
     */
    protected void setParent(ChildWrapper parent)
    {
        ChildWrapper oldParent = this.parent;
        this.parent = parent;
        broadcastSymbolChanged(parent == null ? null : parent.node.getSymbol());
        propertyChangeSupport.firePropertyChange(PROP_PARENT, oldParent, parent);
    }

    public NodeSymbol getSymbol()
    {
        return parent == null ? null : parent.getNode().getSymbol();
//        return symbol;
    }

    public NodeRoot getRoot()
    {
        return parent == null ? null : parent.getNode().getRoot();
//        return symbol;
    }

    protected void broadcastSymbolChanged(NodeSymbol doc)
    {
//        this.symbol = doc;
        for (int i = 0; i < getNumChildWrappers(); ++i)
        {
            getChildWrapper(i).broadcastSymbolChanged(doc);
        }
    }

    public String getName()
    {
        //return name.getValue();
        return name;
    }

    public void setName(String name)
    {
        if ((this.name == null && name == null)
            || (this.name != null && this.name.equals(name)))
        {
            return;
        }
        
        SetNameAction action = new SetNameAction(this.name, name);
        doAction(action);
    }

    public String[] getPath()
    {
        ArrayList<String> path = getPath(null);
        return path.toArray(new String[path.size()]);
    }

    public ArrayList<String> getPath(ArrayList<String> list)
    {
        if (list == null)
        {
            list = new ArrayList<String>();
        }
        if (parent != null)
        {
            parent.node.getPath(list);
        }
        list.add(name);
        return list;
    }

    public String getPathAsString()
    {
        return "/" + (parent == null ? getName() : parent.getNode().getPathAsString() + "/" + getName());
    }

    public void buildUidIndex(HashMap<Integer, NodeObject> map)
    {
        map.put(uid, this);

        for (int i = 0; i < getNumChildWrappers(); ++i)
        {
            ChildWrapper wrap = getChildWrapper(i);
            wrap.buildUidIndex(map);
        }
    }

    public NodeObject getNode(int uid)
    {
        if (this.uid == uid)
        {
            return this;
        }

        for (int i = 0; i < getNumChildWrappers(); ++i)
        {
            ChildWrapper wrap = getChildWrapper(i);
            for (int j = 0; j < wrap.size(); ++j)
            {
                NodeObject child = wrap.get(j);
                if (child == null)
                {
                    continue;
                }

                NodeObject value = child.getNode(uid);
                if (value != null)
                {
                    return value;
                }
            }
        }
        return null;
    }

    public NodeObject getNode(String[] path)
    {
        return getNode(path, 0);
    }

    private NodeObject getNode(String[] path, int index)
    {
        if (path.length == index)
        {
            return this;
        }

        for (int i = 0; i < getNumChildWrappers(); ++i)
        {
            ChildWrapper wrapper = getChildWrapper(i);
            NodeObject node = wrapper.get(path[index]);
            if (node != null)
            {
                return node.getNode(path, index + 1);
            }
        }
        return null;
    }

    public void getNames(Collection<String> list)
    {
        list.add(getName());

        for (ChildWrapper wrapper: getChildWrappers())
        {
            wrapper.getNames(list);
        }
    }

    public int getPrevKeyFrame(int curFrame, int trackUid)
    {
        int prevFrame = Integer.MIN_VALUE;

        for (int i = 0; i < propertyWrappers.size(); ++i)
        {
            PropertyWrapper wrap = propertyWrappers.get(i);
            prevFrame = Math.max(prevFrame,
                    wrap.getPrevKeyFrame(curFrame, trackUid));
        }

        for (int i = 0; i < childWrappers.size(); ++i)
        {
            ChildWrapper wrap = childWrappers.get(i);
            prevFrame = Math.max(prevFrame,
                    wrap.getPrevKeyFrame(curFrame, trackUid));
        }

        return prevFrame;
    }

    public int getNextKeyFrame(int curFrame, int trackUid)
    {
        int nextFrame = Integer.MAX_VALUE;

        for (int i = 0; i < propertyWrappers.size(); ++i)
        {
            PropertyWrapper wrap = propertyWrappers.get(i);
            nextFrame = Math.min(nextFrame,
                    wrap.getNextKeyFrame(curFrame, trackUid));
        }

        for (int i = 0; i < childWrappers.size(); ++i)
        {
            ChildWrapper wrap = childWrappers.get(i);
            nextFrame = Math.min(nextFrame,
                    wrap.getNextKeyFrame(curFrame, trackUid));
        }

        return nextFrame;
    }

    /**
     * Get list of actions that can be performed on this object from UI.
     * 
     * @return
     */
    public Action[] getActions()
    {
        return null;
    }

    @Override
    public String toString()
    {
        return getName() + "(" + uid + ")";
    }

    /**
     * @return the uid
     */
    public int getUid()
    {
        return uid;
    }

    public <T> T getNodeService(Class<T> serviceClass, boolean recursive)
    {
        //Provide objects tool can use to communicate with this panel
        if (serviceClass.isAssignableFrom(getClass()))
        {
            return (T)this;
        }

        if (recursive)
        {
            for (int i = 0; i < getNumChildWrappers(); ++i)
            {
                ChildWrapper child = getChildWrapper(i);
                T childService = (T)child.getNodeService(serviceClass, recursive);
                if (childService != null)
                {
                    return childService;
                }
            }
        }
        return null;
    }

    public <T> void getNodeServices(Class<T> serviceClass, ArrayList<T> appendList, boolean recursive)
    {
        T service = getNodeService(serviceClass, false);
        if (service != null)
        {
            appendList.add(service);
        }

        if (recursive)
        {
            for (int i = 0; i < getNumChildWrappers(); ++i)
            {
                ChildWrapper child = getChildWrapper(i);
                child.getNodeServices(serviceClass, appendList, recursive);
            }
        }
    }

    protected void load(NodeSymbol doc, NodeObjectType type)
    {
        if (type == null)
        {
            //We are creating an empty document - init fixed size
            // child wrappers with default values
            for (int i = 0; i < getNumChildWrappers(); ++i)
            {
                ChildWrapper wrapper = getChildWrapper(i);
                if (wrapper instanceof ChildWrapperSingle)
                {
                    NodeObjectProvider prov = NodeObjectProviderIndex.inst().getProvider(wrapper.getChildType());
                    ((ChildWrapperSingle)wrapper).set(prov.createNode(doc, null), false);
                }
            }
            return;
        }

        name = type.getName();

        for (PropertyType propType: type.getProperty())
        {
            PropertyWrapper wrap = getPropertyWrapper(propType.getName());
            if (wrap == null)
            {
                //Skip unknown properties - may happen if NodeObject
                // structure has been changed since this file was last
                // saved
                continue;
            }
            wrap.load(propType);
        }

        for (ChildrenType childType: type.getChild())
        {
            String childName = childType.getName();
            ChildWrapper wrapper = getChildWrapper(childName);

            if (wrapper instanceof ChildWrapperSingle)
            {
                NodeObjectType nodeType = childType.getNode().get(0);
                NodeObjectProvider prov = NodeObjectProviderIndex.inst().getProvider(nodeType.getClazz());
//                prov.pasteNode(wrapper, 0, nodeType);
                ((ChildWrapperSingle)wrapper).set(prov.createNode(doc, nodeType), false);
            }
            else if (wrapper instanceof ChildWrapperList)
            {
                for (NodeObjectType nodeType: childType.getNode())
                {
                    NodeObjectProvider prov = NodeObjectProviderIndex.inst().getProvider(nodeType.getClazz());
                    if (prov == null)
                    {
                        Logger.getLogger(ResourceGameLoaderAWT.class.getName()).log(Level.INFO,
                                "Could not find loader for object type {0}", nodeType.getClazz());
                        continue;
                    }
                    ((ChildWrapperList)wrapper).add(prov.createNode(doc, nodeType));
//                    prov.pasteNode(wrapper, wrapper.size(), nodeType);
                }
            }
        }

//        for (EventType eventType: type.getEvent())
//        {
//            EventWrapper wrap = getEventWrapper(eventType.getName());
//            wrap.load(eventType);
//        }
    }

    protected void doAction(HistoryAction action)
    {
        NodeSymbol doc = getSymbol();
        History hist = doc == null ? null : doc.getHistory();
        if (hist == null)
        {
            action.redo(null);
            return;
        }
        hist.doAction(action);
    }

    public boolean isAncestorOf(NodeObject node)
    {
        if (node.getParent() == null)
        {
            return false;
        }
        NodeObject parentNode = node.getParent().getNode();
        return this == parentNode ? true : isAncestorOf(parentNode);
    }

//    @Override
//    public int hashCode()
//    {
//        return uid;
//    }
//
//    @Override
//    public boolean equals(Object obj)
//    {
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        final NodeObject other = (NodeObject) obj;
//        if (this.uid != other.uid) {
//            return false;
//        }
//        return true;
//    }

    //--------------------------------------
    class SetNameAction implements HistoryAction
    {
        final String oldName;
        final String newName;

        public SetNameAction(String oldName, String newName)
        {
            this.oldName = oldName;
            this.newName = newName;
        }

        @Override
        public void undo(History history)
        {
            name = oldName;
            propertyChangeSupport.firePropertyChange(PROP_NAME, newName, oldName);
            fireNodeNameChanged();
        }

        @Override
        public void redo(History history)
        {
//        String oldName = name;
            name = newName;
            propertyChangeSupport.firePropertyChange(PROP_NAME, oldName, newName);
            fireNodeNameChanged();
//        }
        }

        @Override
        public String getTitle()
        {
            return "Rename " + oldName + " -> " + newName;
        }
    }
}
