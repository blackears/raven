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

import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.undo.History;
import com.kitfox.raven.util.undo.HistoryAction;
import com.kitfox.xml.schema.ravendocumentschema.NodeObjectType;
import com.kitfox.xml.schema.ravendocumentschema.NodeSymbolType;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Serves as the root of
 *
 * @author kitfox
 */
abstract public class NodeSymbol<RootType extends NodeRoot>
{
    final NodeDocument document;
    final int symbolUid;
    
    public static final String PROP_NAME = "name";
    String name = "symbol";
    
    int nextUid;
    
    private RootType root;

    private final Selection<NodeObject> selection = new Selection<NodeObject>();

    ArrayList<NodeSymbolListener> symbolListeners = new ArrayList<NodeSymbolListener>();

    //Index for rapidly looking up nodes by uid
    HashMap<Integer, NodeObject> nodeIndex =
            new HashMap<Integer, NodeObject>();

    protected NodeSymbol(int symbolUid, NodeDocument document)
    {
        this.symbolUid = symbolUid;
        this.document = document;
    }

//    protected NodeSymbol(int symbolUid, NodeDocument document, NodeSymbolType type)
//    {
//        this.symbolUid = symbolUid;
//        this.document = document;
//        load(type);
//    }

    /**
     * Should only be called by NodeSymbolProvider during initialization
     *
     * @param type Tree with saved state.  If null, a default document
     * will be produced.  (In the default document, all ChildWrapperSingle
     * will be initialzied).
     */
    protected void load(NodeSymbolType type)
    {
        //symbolUID and document should already be set

        if (type != null)
        {
            name = type.getName();
            nextUid = type.getNextUid();
            
            NodeObjectType rootType = type.getRoot();
            NodeObjectProvider prov = 
                    NodeObjectProviderIndex.inst().getProvider(rootType.getClazz());
            RootType newRoot = (RootType)prov.createNode(this, rootType);
            setRoot(newRoot);
        }
        else
        {
//            symbolName = "symbol";
        }
    }

    public NodeSymbolType export()
    {
        NodeSymbolType type = new NodeSymbolType();
        
        type.setClazz(getClass().getName());
        type.setSymbolUid(symbolUid);
        type.setName(name);
        type.setNextUid(nextUid);
        
        type.setRoot(root.export());

        return type;
    }

    /**
     * @return the root
     */
    public RootType getRoot()
    {
        return root;
    }

    /**
     * The root can only be set once, preferably shortly after this
     * NodeSymbol is constructed.
     * 
     * @param root 
     */
    protected void setRoot(RootType root)
    {
        if (this.root != null)
        {
            throw new IllegalStateException("Symbol root can only be set once");
        }
        
        this.root = root;
        root.setSymbol(this);
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        if (name != null && name.equals(this.name))
        {
            return;
        }

        RenameSymbolAction action = new RenameSymbolAction(this.name, name);
        
        doAction(action);
    }

    protected void doAction(HistoryAction action)
    {
        History hist = document.getHistory();
        if (hist == null)
        {
            action.redo(null);
            return;
        }
        hist.doAction(action);
    }
    
    public History getHistory()
    {
        return document.getHistory();
    }
    
    /**
     * Optimization to hash node values, since tree search lookup of nodes
     * by UID is taking a lot of CPU time.
     * 
     * @param uid
     * @return
     */
    public NodeObject getNode(int uid)
    {
        return nodeIndex.get(uid);
    }

    public void addNodeSymbolListener(NodeSymbolListener l)
    {
        symbolListeners.add(l);
    }

    public void removeNodeSymbolListener(NodeSymbolListener l)
    {
        symbolListeners.remove(l);
    }

    protected void fireSymbolNameChanged(PropertyChangeEvent evt)
    {
        for (int i = 0; i < symbolListeners.size(); ++i)
        {
            symbolListeners.get(i).symbolNameChanged(evt);
        }
    }

    protected void fireSymbolPropertyChanged(PropertyChangeEvent evt)
    {
        for (int i = 0; i < symbolListeners.size(); ++i)
        {
            symbolListeners.get(i).symbolPropertyChanged(evt);
        }
    }

    protected void fireSymbolNodeChildAdded(ChildWrapperEvent evt)
    {
        for (int i = 0; i < symbolListeners.size(); ++i)
        {
            symbolListeners.get(i).symbolNodeChildAdded(evt);
        }
    }

    protected void fireSymbolNodeChildRemoved(ChildWrapperEvent evt)
    {
        for (int i = 0; i < symbolListeners.size(); ++i)
        {
            symbolListeners.get(i).symbolNodeChildRemoved(evt);
        }
    }

    protected void notifySymbolPropertyChanged(PropertyChangeEvent evt)
    {
        fireSymbolPropertyChanged(evt);
    }

    void notifySymbolNodeChildAdded(ChildWrapperEvent evt)
    {
        nodeIndex.clear();
        root.buildUidIndex(nodeIndex);
        fireSymbolNodeChildAdded(evt);
    }

    void notifySymbolNodeChildRemoved(ChildWrapperEvent evt)
    {
        nodeIndex.clear();
        root.buildUidIndex(nodeIndex);
        fireSymbolNodeChildRemoved(evt);
    }

    public int allocUid()
    {
        return ++nextUid;
    }

    /**
     * Forces the next uid to be allocated to be greater than or equal to
     * minValue
     * 
     * @param minValue
     */
    public void advanceNextUid(int minValue)
    {
        nextUid = Math.max(minValue, nextUid);
    }

    public String createUniqueName(String name)
    {
        HashSet<String> set = new HashSet<String>();
        root.getNames(set);

        if (!set.contains(name))
        {
            return name;
        }

        Matcher match = Pattern.compile("[0-9]+$").matcher(name);
        int index = 0;
        if (match.find())
        {
            String numPart = match.group();
            name = name.substring(0, numPart.length());
            index = Integer.parseInt(numPart);
        }

        String rootName;
        do
        {
            ++index;
            rootName = name + index;
        } while (set.contains(rootName));

        return rootName;
    }

    /**
     * @return the selection
     */
    public Selection<NodeObject> getSelection()
    {
        return selection;
    }

    public ArrayList<Integer> getNodeUIDs()
    {
        ArrayList<Integer> list = new ArrayList<Integer>();
        getNodeUIDs(list);
        return list;
    }

    public <T extends NodeObject> ArrayList<T> getNodes(final Class<T> type)
    {
        final ArrayList<T> list = new ArrayList<T>();

        root.visit(new NodeVisitor()
        {
            @Override
            public void visit(NodeObject node)
            {
                if (type.isAssignableFrom(node.getClass()))
                {
                    list.add((T)node);
                }
            }
        });
        return list;
    }

    public Collection<Integer> getNodeUIDs(final Collection<Integer> list)
    {
        root.visit(new NodeVisitor()
        {
            @Override
            public void visit(NodeObject node) {
                list.add(node.getUid());
            }
        });
        return list;
    }


    public void deleteSelected()
    {
        History history = getHistory();
        history.beginTransaction("Delete");

        ArrayList<NodeObject> list = selection.getSelection();
        for (int i = 0; i < list.size(); ++i)
        {
            NodeObject node = list.get(i);
            ChildWrapper wrap = node.getParent();
            if (wrap instanceof ChildWrapperList)
            {
                ((ChildWrapperList)wrap).remove(node);
            }
        }
        
        history.commitTransaction();
    }

    /**
     * @return the env
     */
    public NodeDocument getDocument()
    {
        return document;
    }

    public int getSymbolUid()
    {
        return symbolUid;
    }

    public void visit(NodeFilter nodes)
    {
        root.visit(nodes);
    }

    public <T> T getNodeService(Class<T> serviceClass, boolean recursive)
    {
        return (T)root.getNodeService(serviceClass, recursive);
    }

    //---------------------------------
    
    public class RenameSymbolAction implements HistoryAction
    {
        final String oldName;
        final String newName;

        public RenameSymbolAction(String oldName, String newName)
        {
            this.oldName = oldName;
            this.newName = newName;
        }
        
        @Override
        public void redo(History history)
        {
            PropertyChangeEvent evt = 
                    new PropertyChangeEvent(this, PROP_NAME, oldName, newName);
            name = newName;
            fireSymbolNameChanged(evt);
        }

        @Override
        public void undo(History history)
        {
            PropertyChangeEvent evt = 
                    new PropertyChangeEvent(this, PROP_NAME, newName, oldName);
            name = oldName;
            fireSymbolNameChanged(evt);
        }

        @Override
        public String getTitle()
        {
            return "Renaming Symbol " + oldName + " -> " + newName;
        }
    }
    
    public static interface Environment
    {
        public Window getSwingRoot();
        //public ResourceCache getResourceCache();
        public File getDocumentSource();
        public Properties getMetaProperties(String key);
        public void setMetaProperties(String key, Properties props);
        public History getHistory();
        //public RavenDocument getDocument();
    }

    public static class NodeFilter implements NodeVisitor
    {
        private ArrayList<NodeObject> list = new ArrayList<NodeObject>();
        final Class filterClass;

        public NodeFilter(Class filterClass)
        {
            this.filterClass = filterClass;
        }

        @Override
        public void visit(NodeObject node)
        {
            if (filterClass.isAssignableFrom(node.getClass()))
            {
                list.add(node);
            }
        }

        /**
         * @return the list
         */
        public ArrayList<NodeObject> getList()
        {
            return list;
        }
    }

}
