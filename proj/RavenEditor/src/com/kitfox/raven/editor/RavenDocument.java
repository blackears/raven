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

package com.kitfox.raven.editor;

import com.kitfox.raven.util.JAXBUtil;
import com.kitfox.raven.util.resource.ResourceCache;
import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.util.tree.NodeDocumentProvider;
import com.kitfox.raven.util.tree.NodeDocumentProviderIndex;
import com.kitfox.raven.util.undo.History;
import com.kitfox.raven.util.undo.HistoryAction;
import com.kitfox.xml.schema.ravendocumentschema.MetaPropertyEntryType;
import com.kitfox.xml.schema.ravendocumentschema.MetaPropertySetGroupType;
import com.kitfox.xml.schema.ravendocumentschema.MetaPropertySetType;
import com.kitfox.xml.schema.ravendocumentschema.NodeDocumentType;
import com.kitfox.xml.schema.ravendocumentschema.ObjectFactory;
import com.kitfox.xml.schema.ravendocumentschema.RavenDocumentType;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;

/**
 *
 * @author kitfox
 */
public class RavenDocument
        implements NodeDocument.Environment
{
    public static final String RAVEN_FILE_SUFFIX = "raven";

    private final RavenEditor editor;
    private File source;

    private ArrayList<NodeDocument> documents = new ArrayList<NodeDocument>();
    public static final String PROP_CURDOCUMENT = "curDocument";
    NodeDocument curDoc;

    ArrayList<RavenDocumentListener> listeners = new ArrayList<RavenDocumentListener>();

    //Meta properties provide a way for views and tools to store document
    // specific persistent information
    HashMap<String, Properties> metaProperties = new HashMap<String, Properties>();

    private final History history = new History();

    public RavenDocument(RavenEditor editor)
    {
        this.editor = editor;
    }

    public RavenDocument(RavenEditor editor, NodeDocument root)
    {
        this.editor = editor;
//        addDocument(root);
        documents.add(root);
        root.setEnv(this);
        curDoc = root;
//        history.clear();
    }

    public RavenDocument(RavenEditor editor, File file)
    {
        this(editor);

        //Load document
        RavenDocumentType docTree = load(file);
        String curDocName = docTree.getCurDocument();
        for (NodeDocumentType docType: docTree.getRoot())
        {
            String docClass = docType.getClazz();
            NodeDocumentProvider prov =
                    NodeDocumentProviderIndex.inst().getProvider(docClass);
            NodeDocument doc = prov.loadDocument(docType);
            doc.setEnv(this);
            
            documents.add(doc);
            
            if (curDocName != null && curDocName.equals(doc.getDocumentName()))
            {
                curDoc = doc;
            }
        }

        if (curDoc == null && !documents.isEmpty())
        {
            curDoc = documents.get(0);
        }

        //Load meta properties
        MetaPropertySetGroupType group = docTree.getPropertySetGroups();
        if (group != null)
        {
            for (MetaPropertySetType set: group.getPropertySet())
            {
                Properties prop = new Properties();
                for (MetaPropertyEntryType entry: set.getEntry())
                {
                    prop.setProperty(entry.getName(), entry.getValue());
                }
                metaProperties.put(set.getKey(), prop);
            }
        }

        //Remove any history created during load
        history.clear();
    }
    
    @Override
    public History getHistory()
    {
        return history;
    }

    /**
     * @return the sceneGraph
     */
    public NodeDocument getCurDocument()
    {
        return curDoc;
    }

    public NodeDocument getDocument(String name)
    {
        for (NodeDocument doc: documents)
        {
            if (name.equals(doc.getDocumentName()))
            {
                return doc;
            }
        }
        return null;
    }
    
//    public int getNumDocuments()
//    {
//        return documents.size();
//    }
//    
//    public NodeDocument getDocument(int index)
//    {
//        return documents.get(index);
//    }
    
    public void setCurrentDocument(NodeDocument symbol)
    {
        if (curDoc == symbol)
        {
            return;
        }
        if (!documents.contains(symbol))
        {
            return;
        }
        
        SetCurrentSymbolAction action = 
                new SetCurrentSymbolAction(curDoc, symbol);
        history.doAction(action);
    }
    
//    public int indexOfDocument(NodeDocument doc)
//    {
//        for (int i = 0; i < documents.size(); ++i)
//        {
//            if (documents.get(i) == doc)
//            {
//                return i;
//            }
//        }
//        return -1;
//    }
    
    public void addDocument(NodeDocument sym)
    {
//        if (sym.getEnv() != null)
//        {
//            return;
//        }
        AddSymbolAction action = new AddSymbolAction(sym);
        history.doAction(action);
    }
    
    public void removeDocument(NodeDocument sym)
    {
        if (!documents.contains(sym))
        {
            return;
        }
        RemoveSymbolAction action = new RemoveSymbolAction(sym);
        history.doAction(action);
    }
    
    public String getUnusedDocumentName(String rootName)
    {
        HashSet<String> names = new HashSet<String>();
        for (NodeDocument doc: documents)
        {
            names.add(doc.getDocumentName());
        }
        
        String name = rootName;
        int idx = 0;
        while (names.contains(name))
        {
            name = rootName + idx++;
        }
        return name;
    }
    
    public ArrayList<NodeDocument> getDocuments()
    {
        return new ArrayList<NodeDocument>(documents);
    }
    
    public void addRavenDocumentListener(RavenDocumentListener l)
    {
        listeners.add(l);
    }

    public void removeRavenDocumentListener(RavenDocumentListener l)
    {
        listeners.remove(l);
    }

    private void fireSourceChanged()
    {
        EventObject evt = new EventObject(this);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).documentSourceChanged(evt);
        }
    }

    private void fireDocumentAdded(NodeDocument doc)
    {
        RavenDocumentEvent evt = new RavenDocumentEvent(this, doc);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).documentAdded(evt);
        }
    }

    private void fireDocumentRemoved(NodeDocument doc)
    {
        RavenDocumentEvent evt = new RavenDocumentEvent(this, doc);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).documentRemoved(evt);
        }
    }

    private void fireCurrentDocumentChanged(NodeDocument oldDoc, NodeDocument doc)
    {
        RavenDocumentEvent evt = 
                new RavenDocumentEvent(this, doc, oldDoc);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).currentDocumentChanged(evt);
        }
    }

    @Override
    public Properties getMetaProperties(String key)
    {
        return metaProperties.get(key);
    }

    @Override
    public void setMetaProperties(String key, Properties props)
    {
        metaProperties.put(key, props);
    }

    /**
     * @return the source
     */
    public File getSource()
    {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(File source)
    {
        this.source = source;
        fireSourceChanged();
    }

    protected RavenDocumentType asJAXB()
    {
        RavenDocumentType pref = new RavenDocumentType();

        Properties props = new Properties();
        try {
            props.load(RavenDocument.class.getResourceAsStream("/info/editor.properties"));
        } catch (IOException ex) {
            Logger.getLogger(RavenDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        pref.setVersion(props.getProperty("version"));

        //Save Documents
        for (NodeDocument doc: documents)
        {
            NodeDocumentType type = doc.export();
            pref.getRoot().add(type);
        }
        pref.setCurDocument(curDoc == null ? null : curDoc.getDocumentName());

        //Save meta properties
        MetaPropertySetGroupType groups = new MetaPropertySetGroupType();
        pref.setPropertySetGroups(groups);
        for (String key: metaProperties.keySet())
        {
            Properties prop = metaProperties.get(key);
            MetaPropertySetType set = new MetaPropertySetType();
            groups.getPropertySet().add(set);
            
            for (String name: prop.stringPropertyNames())
            {
                String value = prop.getProperty(name);
                MetaPropertyEntryType entry = new MetaPropertyEntryType();
                set.getEntry().add(entry);
                entry.setName(name);
                entry.setValue(value);
            }
        }

        return pref;
    }

    private RavenDocumentType load(File file)
    {
        setSource(file);

        if (file == null)
        {
            return null;
        }

        return JAXBUtil.loadJAXB(RavenDocumentType.class, file);
    }


    public void save(File file)
    {
        RavenDocumentType pref = asJAXB();

        ObjectFactory fact
                = new ObjectFactory();
        JAXBElement<RavenDocumentType> value = fact.createRavenDocument(pref);

        JAXBUtil.saveJAXB(value, file);
    }

    /**
     * @return the editor
     */
    public RavenEditor getEditor()
    {
        return editor;
    }

    @Override
    public Window getSwingRoot()
    {
        return editor.getViewManager().getSwingRoot();
    }

    @Override
    public ResourceCache getResourceCache()
    {
        return editor.getResourceCache();
    }

    @Override
    public File getDocumentSource()
    {
        return source;
    }
    
    //------------------------------------
    public class AddSymbolAction implements HistoryAction
    {
        final NodeDocument sym;
        final String name;

        public AddSymbolAction(NodeDocument sym)
        {
            this.sym = sym;
            this.name = sym.getDocumentName();
        }

        @Override
        public void redo(History history)
        {
            documents.add(sym);
            sym.setEnv(RavenDocument.this);
            fireDocumentAdded(sym);
        }

        @Override
        public void undo(History history)
        {
            documents.remove(sym);
            sym.setEnv(null);
            fireDocumentRemoved(sym);
        }

        @Override
        public String getTitle()
        {
            return "Add Symbol " + name;
        }
    }
    
    public class RemoveSymbolAction implements HistoryAction
    {
        final NodeDocument sym;
        final String name;
        boolean updateCurDoc;
        NodeDocument replaceCurDoc;

        public RemoveSymbolAction(NodeDocument sym)
        {
            this.sym = sym;
            this.name = sym.getDocumentName();

            replaceCurDoc = null;
            if (sym == curDoc)
            {
                updateCurDoc = true;
                int idx = documents.indexOf(sym);
                if (idx > 0)
                {
                    replaceCurDoc = documents.get(idx - 1);
                }
                else if (!documents.isEmpty())
                {
                    replaceCurDoc = documents.get(0);
                }
            }
        }

        @Override
        public void redo(History history)
        {
            if (curDoc == sym)
            {
                curDoc = null;
            }
            
            if (updateCurDoc)
            {
                curDoc = replaceCurDoc;
                fireCurrentDocumentChanged(sym, replaceCurDoc);
            }
            
            documents.remove(sym);
            sym.setEnv(null);
            fireDocumentRemoved(sym);
        }

        @Override
        public void undo(History history)
        {
            documents.add(sym);
            sym.setEnv(RavenDocument.this);
            fireDocumentAdded(sym);
            
            if (updateCurDoc)
            {
                curDoc = sym;
                fireCurrentDocumentChanged(replaceCurDoc, sym);
            }
        }

        @Override
        public String getTitle()
        {
            return "Remove Symbol " + name;
        }
    }
    
    public class SetCurrentSymbolAction implements HistoryAction
    {
        final NodeDocument symOld;
        final NodeDocument symNew;

        public SetCurrentSymbolAction(NodeDocument symOld, NodeDocument symNew)
        {
            this.symOld = symOld;
            this.symNew = symNew;
        }

        @Override
        public void redo(History history)
        {
            curDoc = symNew;
            fireCurrentDocumentChanged(symOld, symNew);
        }

        @Override
        public void undo(History history)
        {
            curDoc = symOld;
            fireCurrentDocumentChanged(symNew, symOld);
        }

        @Override
        public String getTitle()
        {
            return "Set current symbol: " + symNew.getDocumentName();
        }
    }
}
