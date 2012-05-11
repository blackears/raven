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
import com.kitfox.xml.schema.ravendocumentschema.MetaPropertyEntryType;
import com.kitfox.xml.schema.ravendocumentschema.MetaPropertySetGroupType;
import com.kitfox.xml.schema.ravendocumentschema.MetaPropertySetType;
import com.kitfox.xml.schema.ravendocumentschema.NodeDocumentType;
import com.kitfox.xml.schema.ravendocumentschema.NodeSymbolType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

/**
 *
 * @author kitfox
 */
abstract public class NodeDocument
{
    public static final String VERSION = "0.0.0";
    int nextSymbolUid;
    
//    protected ArrayList<NodeSymbol> symbols = new ArrayList<NodeSymbol>();
    protected HashMap<Integer, NodeSymbol> symbolMap
            = new HashMap<Integer, NodeSymbol>();
    
    public static final String PROP_CURDOCUMENT = "curDocument";
    protected NodeSymbol curSymbol;

    ArrayList<NodeDocumentListener> listeners =
            new ArrayList<NodeDocumentListener>();

    //Meta properties provide a way for views and tools to store document
    // specific persistent information
    protected HashMap<String, Properties> metaProperties = new HashMap<String, Properties>();

    protected final History history = new History();
    private final PluginsManager pluginsManager = new PluginsManager();

    private final DocumentCode documentCode = new DocumentCode();
    
    protected void load(NodeDocumentType type)
    {
        if (type != null)
        {
            nextSymbolUid = type.getNextSymbolUid();
            
            pluginsManager.load(type.getPlugins());
            
            for (NodeSymbolType symTyp: type.getSymbols())
            {
                NodeSymbolProvider prov =
                        NodeSymbolProviderIndex.inst().getProvider(symTyp.getClazz());
                NodeSymbol sym = prov.loadDocument(this, symTyp);
                if (sym != null)
                {
                    symbolMap.put(sym.getSymbolUid(), sym);
                }
            }
            
            curSymbol = symbolMap.get(type.getCurSymbol());

            //Load meta properties
            MetaPropertySetGroupType group = type.getPropertySetGroups();
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
        }
    }

    public NodeDocumentType export()
    {
        NodeDocumentType type = new NodeDocumentType();

        type.setVersion(VERSION);
        type.setNextSymbolUid(nextSymbolUid);
        
        type.setPlugins(pluginsManager.export());
        
        for (NodeSymbol sym: symbolMap.values())
        {
            NodeSymbolType symTyp = sym.export();
            type.getSymbols().add(symTyp);
        }
        
        type.setCurSymbol(curSymbol == null ? 0 : curSymbol.getSymbolUid());
        

        //Save meta properties
        MetaPropertySetGroupType groups = new MetaPropertySetGroupType();
        type.setPropertySetGroups(groups);
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

        return type;
    }
    
    public History getHistory()
    {
        return history;
    }

    abstract public NodeSymbol.Environment getEnv();

    public int allocSymbolUid()
    {
        return ++nextSymbolUid;
    }
    /**
     * Forces the next symbol uid to be allocated to be greater than 
     * or equal to minValue
     * 
     * @param minValue
     */
    public void advanceNextUid(int minValue)
    {
        nextSymbolUid = Math.max(minValue, nextSymbolUid);
    }

    
    /**
     * @return the sceneGraph
     */
    public NodeSymbol getCurSymbol()
    {
        return curSymbol;
    }

    public NodeSymbol getSymbol(int index)
    {
        return symbolMap.get(index);
    }
    
    public NodeSymbol getSymbol(String name)
    {
        for (NodeSymbol doc: symbolMap.values())
        {
            if (name.equals(doc.getName()))
            {
                return doc;
            }
        }
        return null;
    }
    
    public void setCurrentSymbol(NodeSymbol symbol)
    {
        if (curSymbol == symbol)
        {
            return;
        }
        if (!symbolMap.containsKey(symbol.getSymbolUid()))
        {
            throw new IllegalArgumentException(
                    "Current symbol must be a member of this document");
        }
        
        SetCurrentSymbolAction action = 
                new SetCurrentSymbolAction(curSymbol, symbol);
        history.doAction(action);
    }
    
    public void addSymbol(NodeSymbol sym)
    {
        AddSymbolAction action = new AddSymbolAction(sym);
        history.doAction(action);
    }
    
    public void removeSymbol(NodeSymbol sym)
    {
        if (curSymbol == sym ||
                !symbolMap.containsKey(sym.getSymbolUid()))
        {
            return;
        }
        RemoveSymbolAction action = new RemoveSymbolAction(sym);
        history.doAction(action);
    }
    
    public String getUnusedSymbolName(String rootName)
    {
        HashSet<String> names = new HashSet<String>();
        for (NodeSymbol doc: symbolMap.values())
        {
            names.add(doc.getName());
        }
        
        String name = rootName;
        int idx = 0;
        while (names.contains(name))
        {
            name = rootName + idx++;
        }
        return name;
    }
    
    public HashMap<Integer, NodeSymbol> getSymbolMap()
    {
        return new HashMap<Integer, NodeSymbol>(symbolMap);
    }
    
    public ArrayList<NodeSymbol> getSymbols()
    {
        return new ArrayList<NodeSymbol>(symbolMap.values());
    }
    
    public void addNodeDocumentListener(NodeDocumentListener l)
    {
        listeners.add(l);
    }

    public void removeNodeDocumentListener(NodeDocumentListener l)
    {
        listeners.remove(l);
    }

//    private void fireSourceChanged()
//    {
//        EventObject evt = new EventObject(this);
//        for (int i = 0; i < listeners.size(); ++i)
//        {
//            listeners.get(i).documentSourceChanged(evt);
//        }
//    }

    private void fireSymbolAdded(NodeSymbol doc)
    {
        NodeDocumentEvent evt = new NodeDocumentEvent(this, doc);
        ArrayList<NodeDocumentListener> list =
                new ArrayList<NodeDocumentListener>(listeners);
        for (int i = 0; i < list.size(); ++i)
        {
            list.get(i).symbolAdded(evt);
        }
    }

    private void fireSymbolRemoved(NodeSymbol doc)
    {
        NodeDocumentEvent evt = new NodeDocumentEvent(this, doc);
        ArrayList<NodeDocumentListener> list =
                new ArrayList<NodeDocumentListener>(listeners);
        for (int i = 0; i < list.size(); ++i)
        {
            list.get(i).symbolRemoved(evt);
        }
    }

    private void fireCurrentSymbolChanged(NodeSymbol oldDoc, NodeSymbol doc)
    {
        NodeDocumentEvent evt = 
                new NodeDocumentEvent(this, doc, oldDoc);
        ArrayList<NodeDocumentListener> list =
                new ArrayList<NodeDocumentListener>(listeners);
        for (int i = 0; i < list.size(); ++i)
        {
            list.get(i).currentSymbolChanged(evt);
        }
    }

    /**
     * @return the pluginsManager
     */
    public PluginsManager getPluginsManager()
    {
        return pluginsManager;
    }

    public Properties getMetaProperties(String key)
    {
        return metaProperties.get(key);
    }

    public void setMetaProperties(String key, Properties props)
    {
        metaProperties.put(key, props);
    }

    /**
     * @return the documentCode
     */
    public DocumentCode getDocumentCode()
    {
        return documentCode;
    }
    
    //------------------------------------
    public class AddSymbolAction implements HistoryAction
    {
        final NodeSymbol sym;
        final String name;

        public AddSymbolAction(NodeSymbol sym)
        {
            this.sym = sym;
            this.name = sym.getName();
        }

        @Override
        public void redo(History history)
        {
            symbolMap.put(sym.getSymbolUid(), sym);
//            sym.setDocument(NodeDocument.this);
            fireSymbolAdded(sym);
        }

        @Override
        public void undo(History history)
        {
            symbolMap.remove(sym.getSymbolUid());
//            sym.setDocument(null);
            fireSymbolRemoved(sym);
        }

        @Override
        public String getTitle()
        {
            return "Add Symbol " + name;
        }
    }
    
    public class RemoveSymbolAction implements HistoryAction
    {
        final NodeSymbol sym;
        final String name;
        boolean isCurSym;
//        boolean updateCurDoc;
//        NodeSymbol replaceCurDoc;

        public RemoveSymbolAction(NodeSymbol sym)
        {
            this.sym = sym;
            this.name = sym.getName();

            this.isCurSym = sym == curSymbol;
//            replaceCurDoc = null;
//            if (sym == curSymbol)
//            {
//                updateCurDoc = true;
//                int idx = symbols.indexOf(sym);
//                if (idx > 0)
//                {
//                    replaceCurDoc = symbols.get(idx - 1);
//                }
//                else if (!symbols.isEmpty())
//                {
//                    replaceCurDoc = symbols.get(0);
//                }
//            }
        }

        @Override
        public void redo(History history)
        {
            if (isCurSym)
            {
                return;
            }
            
//            if (curSymbol == sym)
//            {
//                curSymbol = null;
//            }
//            
//            if (updateCurDoc)
//            {
//                curSymbol = replaceCurDoc;
//                fireCurrentSymbolChanged(sym, replaceCurDoc);
//            }
            
            symbolMap.remove(sym.getSymbolUid());
//            sym.setDocument(null);
            fireSymbolRemoved(sym);
        }

        @Override
        public void undo(History history)
        {
            if (isCurSym)
            {
                return;
            }
            
            symbolMap.put(sym.getSymbolUid(), sym);
//            sym.setDocument(NodeDocument.this);
            fireSymbolAdded(sym);
            
//            if (updateCurDoc)
//            {
//                curSymbol = sym;
//                fireCurrentSymbolChanged(replaceCurDoc, sym);
//            }
        }

        @Override
        public String getTitle()
        {
            return "Remove Symbol " + name;
        }
    }
    
    public class SetCurrentSymbolAction implements HistoryAction
    {
        final NodeSymbol symOld;
        final NodeSymbol symNew;

        public SetCurrentSymbolAction(NodeSymbol symOld, NodeSymbol symNew)
        {
            this.symOld = symOld;
            this.symNew = symNew;
        }

        @Override
        public void redo(History history)
        {
            curSymbol = symNew;
            fireCurrentSymbolChanged(symOld, symNew);
        }

        @Override
        public void undo(History history)
        {
            curSymbol = symOld;
            fireCurrentSymbolChanged(symNew, symOld);
        }

        @Override
        public String getTitle()
        {
            return "Set current symbol: " + symNew.getName();
        }
    }
    
}
