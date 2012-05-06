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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

/**
 *
 * @author kitfox
 */
abstract public class NodeDocument2
{
    protected ArrayList<NodeSymbol> symbols = new ArrayList<NodeSymbol>();
    public static final String PROP_CURDOCUMENT = "curDocument";
    protected NodeSymbol curSymbol;

    ArrayList<NodeDocument2Listener> listeners =
            new ArrayList<NodeDocument2Listener>();

    //Meta properties provide a way for views and tools to store document
    // specific persistent information
    protected HashMap<String, Properties> metaProperties = new HashMap<String, Properties>();

    protected final History history = new History();
    
//    @Override
    public History getHistory()
    {
        return history;
    }

    abstract public NodeSymbol.Environment getEnv();
    
    /**
     * @return the sceneGraph
     */
    public NodeSymbol getCurSymbol()
    {
        return curSymbol;
    }

    public NodeSymbol getSymbol(String name)
    {
        for (NodeSymbol doc: symbols)
        {
            if (name.equals(doc.getSymbolName()))
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
        if (!symbols.contains(symbol))
        {
            return;
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
        if (!symbols.contains(sym))
        {
            return;
        }
        RemoveSymbolAction action = new RemoveSymbolAction(sym);
        history.doAction(action);
    }
    
    public String getUnusedSymbolName(String rootName)
    {
        HashSet<String> names = new HashSet<String>();
        for (NodeSymbol doc: symbols)
        {
            names.add(doc.getSymbolName());
        }
        
        String name = rootName;
        int idx = 0;
        while (names.contains(name))
        {
            name = rootName + idx++;
        }
        return name;
    }
    
    public ArrayList<NodeSymbol> getSymbols()
    {
        return new ArrayList<NodeSymbol>(symbols);
    }
    
    public void addNodeDocumentListener(NodeDocument2Listener l)
    {
        listeners.add(l);
    }

    public void removeNodeDocumentListener(NodeDocument2Listener l)
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
        NodeDocument2Event evt = new NodeDocument2Event(this, doc);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).symbolAdded(evt);
        }
    }

    private void fireSymbolRemoved(NodeSymbol doc)
    {
        NodeDocument2Event evt = new NodeDocument2Event(this, doc);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).symbolRemoved(evt);
        }
    }

    private void fireCurrentSymbolChanged(NodeSymbol oldDoc, NodeSymbol doc)
    {
        NodeDocument2Event evt = 
                new NodeDocument2Event(this, doc, oldDoc);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).currentSymbolChanged(evt);
        }
    }

//    @Override
    public Properties getMetaProperties(String key)
    {
        return metaProperties.get(key);
    }

//    @Override
    public void setMetaProperties(String key, Properties props)
    {
        metaProperties.put(key, props);
    }
    
    //------------------------------------
    public class AddSymbolAction implements HistoryAction
    {
        final NodeSymbol sym;
        final String name;

        public AddSymbolAction(NodeSymbol sym)
        {
            this.sym = sym;
            this.name = sym.getSymbolName();
        }

        @Override
        public void redo(History history)
        {
            symbols.add(sym);
            sym.setDocument(NodeDocument2.this);
            fireSymbolAdded(sym);
        }

        @Override
        public void undo(History history)
        {
            symbols.remove(sym);
            sym.setDocument(null);
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
        boolean updateCurDoc;
        NodeSymbol replaceCurDoc;

        public RemoveSymbolAction(NodeSymbol sym)
        {
            this.sym = sym;
            this.name = sym.getSymbolName();

            replaceCurDoc = null;
            if (sym == curSymbol)
            {
                updateCurDoc = true;
                int idx = symbols.indexOf(sym);
                if (idx > 0)
                {
                    replaceCurDoc = symbols.get(idx - 1);
                }
                else if (!symbols.isEmpty())
                {
                    replaceCurDoc = symbols.get(0);
                }
            }
        }

        @Override
        public void redo(History history)
        {
            if (curSymbol == sym)
            {
                curSymbol = null;
            }
            
            if (updateCurDoc)
            {
                curSymbol = replaceCurDoc;
                fireCurrentSymbolChanged(sym, replaceCurDoc);
            }
            
            symbols.remove(sym);
            sym.setDocument(null);
            fireSymbolRemoved(sym);
        }

        @Override
        public void undo(History history)
        {
            symbols.add(sym);
            sym.setDocument(NodeDocument2.this);
            fireSymbolAdded(sym);
            
            if (updateCurDoc)
            {
                curSymbol = sym;
                fireCurrentSymbolChanged(replaceCurDoc, sym);
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
            return "Set current symbol: " + symNew.getSymbolName();
        }
    }
    
}
