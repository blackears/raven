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
import com.kitfox.raven.util.resource.ResourceCache;
import com.kitfox.raven.util.undo.History;
import com.kitfox.xml.schema.ravendocumentschema.NodeDocumentType;
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
abstract public class NodeDocument extends NodeObject
{
    private final History history = new History();

    int nextUid;

    public static final String CHILD_TRACKLIBRARY = "trackLibrary";
    public final ChildWrapperSingle<NodeDocument, TrackLibrary>
            childTrackLibrary =
            new ChildWrapperSingle(this,
            CHILD_TRACKLIBRARY, TrackLibrary.class);

//    public static final String CHILD_RESOURCELIBRARY = "resourceLibrary";
//    public final ChildWrapperSingle<NodeDocument, ResourceLibrary>
//            childResourceLibrary =
//            new ChildWrapperSingle(this,
//            CHILD_RESOURCELIBRARY, ResourceLibrary.class);

//    private Window swingRoot;
    private final Selection<SelectionRecord> selection = new Selection<SelectionRecord>();

    ArrayList<NodeDocumentListener> docListeners = new ArrayList<NodeDocumentListener>();

    //Index for rapidly looking up nodes by uid
    HashMap<Integer, NodeObject> nodeIndex =
            new HashMap<Integer, NodeObject>();

    //Source code that will be added to objects generated from this document
    private final DocumentCode documentCode = new DocumentCode();

    private final PluginsManager pluginsManager = new PluginsManager();
    private Environment env;

    protected NodeDocument()
    {
        super(0);
    }

    /**
     * Optimization to hash node values, since tree search lookup of nodes
     * by UID is taking a lot of CPU time.
     * 
     * @param uid
     * @return
     */
    @Override
    public NodeObject getNode(int uid)
    {
        return nodeIndex.get(uid);
    }

    public void addNodeDocumentListener(NodeDocumentListener l)
    {
        docListeners.add(l);
    }

    public void removeNodeDocumentListener(NodeDocumentListener l)
    {
        docListeners.remove(l);
    }

    protected void fireDocumentPropertyChanged(PropertyChangeEvent evt)
    {
        for (int i = 0; i < docListeners.size(); ++i)
        {
            docListeners.get(i).documentPropertyChanged(evt);
        }
    }

    protected void fireDocumentNodeChildAdded(ChildWrapperEvent evt)
    {
        for (int i = 0; i < docListeners.size(); ++i)
        {
            docListeners.get(i).documentNodeChildAdded(evt);
        }
    }

    protected void fireDocumentNodeChildRemoved(ChildWrapperEvent evt)
    {
        for (int i = 0; i < docListeners.size(); ++i)
        {
            docListeners.get(i).documentNodeChildRemoved(evt);
        }
    }

    protected void notifyDocumentPropertyChanged(PropertyChangeEvent evt)
    {
        fireDocumentPropertyChanged(evt);
    }

    void notifyDocumentNodeChildAdded(ChildWrapperEvent evt)
    {
        nodeIndex.clear();
        buildUidIndex(nodeIndex);
        fireDocumentNodeChildAdded(evt);
    }

    void notifyDocumentNodeChildRemoved(ChildWrapperEvent evt)
    {
        nodeIndex.clear();
        buildUidIndex(nodeIndex);
        fireDocumentNodeChildRemoved(evt);
    }

    /**
     * Should only be called during initialization
     *
     * @param type Tree with saved state.  If null, a default document
     * will be produced.  (In the default document, all ChildWrapperSingle
     * will be initialzied).
     */
    protected void load(NodeDocumentType type)
    {
        super.load(this, type);

        if (type != null)
        {
            documentCode.load(type.getCode());
            pluginsManager.load(type.getPlugins());
        }

        history.clear();
    }


    public int allocUid()
    {
        return ++nextUid;
    }

    /**
     * Make sure next UID is at least minValue
     * 
     * @param minValue
     */
    public void advanceNextUid(int minValue)
    {
        nextUid = Math.max(minValue, nextUid);
    }

    @Override
    public NodeDocument getDocument()
    {
        return this;
    }

    @Override
    protected void setParent(ChildWrapper parent)
    {
        throw new UnsupportedOperationException("Document is a root");
    }

    @Override
    public NodeDocumentType export()
    {
        NodeDocumentType type = new NodeDocumentType();
        export(type);

        type.setNextUid(nextUid);
        type.setCode(documentCode.export());
        type.setPlugins(pluginsManager.export());

        return type;
    }

    /**
     * @return the history
     */
    public History getHistory()
    {
        return history;
    }

    public String createUniqueName(String name)
    {
        HashSet<String> set = new HashSet<String>();
        getNames(set);

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

//    /**
//     * @return the swingRoot
//     */
//    public Window getSwingRoot() {
//        return swingRoot;
//    }
//
//    /**
//     * @param swingRoot the swingRoot to set
//     */
//    public void setSwingRoot(Window swingRoot) {
//        this.swingRoot = swingRoot;
//    }

    /**
     * @return the selection
     */
    public Selection<SelectionRecord> getSelection()
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

        visit(new NodeVisitor()
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
        visit(new NodeVisitor()
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
        history.beginTransaction("Delete");

        ArrayList<SelectionRecord> list = selection.getSelection();
        for (int i = 0; i < list.size(); ++i)
        {
            SelectionRecord rec = list.get(i);
            NodeObject node = rec.getNode();
            ChildWrapper wrap = node.getParent();
            if (wrap instanceof ChildWrapperList)
            {
                ((ChildWrapperList)wrap).remove(node);
            }
        }
        
        history.commitTransaction();
    }

    public TrackLibrary getTrackLibrary()
    {
        return childTrackLibrary.getChild();
    }

//    public ResourceLibrary getResourceLibrary()
//    {
//        return childResourceLibrary.getChild();
//    }

    /**
     * @return the sourceCode
     */
    public DocumentCode getDocumentCode()
    {
        return documentCode;
    }

    /**
     * @return the pluginsManager
     */
    public PluginsManager getPluginsManager()
    {
        return pluginsManager;
    }

    /**
     * @return the env
     */
    public Environment getEnv()
    {
        return env;
    }

    /**
     * @param env the env to set
     */
    public void setEnv(Environment env)
    {
        this.env = env;
    }

    //---------------------------------
    public static interface Environment
    {
        public Window getSwingRoot();
        public ResourceCache getResourceCache();
        public File getDocumentSource();
        public Properties getMetaProperties(String key);
        public void setMetaProperties(String key, Properties props);
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
