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
import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.util.tree.NodeSymbol;
import com.kitfox.raven.util.tree.NodeSymbol.Environment;
import com.kitfox.xml.schema.ravendocumentschema.NodeDocumentType;
import com.kitfox.xml.schema.ravendocumentschema.ObjectFactory;
import java.awt.Window;
import java.io.File;
import java.util.ArrayList;
import javax.xml.bind.JAXBElement;

/**
 *
 * @author kitfox
 */
public class RavenDocument extends NodeDocument
        implements NodeSymbol.Environment
{
    public static final String RAVEN_FILE_SUFFIX = "raven";

    private final RavenEditor editor;
    private File source;

//    private ArrayList<NodeSymbol> symbols = new ArrayList<NodeSymbol>();
//    public static final String PROP_CURDOCUMENT = "curDocument";
//    NodeSymbol curSymbol;
//
    ArrayList<RavenDocumentListener> listeners = new ArrayList<RavenDocumentListener>();
//
//    //Meta properties provide a way for views and tools to store document
//    // specific persistent information
//    HashMap<String, Properties> metaProperties = new HashMap<String, Properties>();
//
//    private final History history = new History();

    public RavenDocument(RavenEditor editor)
    {
        this.editor = editor;
    }

//    public RavenDocument(RavenEditor editor, NodeSymbol root)
//    {
//        this.editor = editor;
////        addDocument(root);
//        symbols.add(root);
//        root.setDocument(this);
//        curSymbol = root;
////        history.clear();
//    }
//
//    public RavenDocument(RavenEditor editor, File file)
//    {
//        this(editor);
//
//        //Load document
//        RavenDocumentType docTree = load(file);
//        String curDocName = docTree.getCurSymbol();
//        for (NodeSymbolType docType: docTree.getSymbols())
//        {
//            String docClass = docType.getClazz();
//            NodeSymbolProvider prov =
//                    NodeSymbolProviderIndex.inst().getProvider(docClass);
//            NodeSymbol doc = prov.loadDocument(docType);
//            doc.setDocument(this);
//            
//            symbols.add(doc);
//            
//            if (curDocName != null && curDocName.equals(doc.getSymbolName()))
//            {
//                curSymbol = doc;
//            }
//        }
//
//        if (curSymbol == null && !symbols.isEmpty())
//        {
//            curSymbol = symbols.get(0);
//        }
//
//        //Load meta properties
//        MetaPropertySetGroupType group = docTree.getPropertySetGroups();
//        if (group != null)
//        {
//            for (MetaPropertySetType set: group.getPropertySet())
//            {
//                Properties prop = new Properties();
//                for (MetaPropertyEntryType entry: set.getEntry())
//                {
//                    prop.setProperty(entry.getName(), entry.getValue());
//                }
//                metaProperties.put(set.getKey(), prop);
//            }
//        }
//
//        //Remove any history created during load
//        history.clear();
//    }
    
    public static RavenDocument create(RavenEditor editor, NodeDocumentType type)
    {
        RavenDocument doc = new RavenDocument(editor);
        doc.load(type);
        return doc;
    }
    
    public static RavenDocument create(RavenEditor editor, File file)
    {
        NodeDocumentType type = 
                JAXBUtil.loadJAXB(NodeDocumentType.class, file);
        return create(editor, type);
    }
    
    public void addRavenDocumentListener(RavenDocumentListener l)
    {
        listeners.add(l);
    }

    public void removeRavenDocumentListener(RavenDocumentListener l)
    {
        listeners.remove(l);
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
//        fireSourceChanged();
    }

//    protected RavenDocumentType asJAXB()
//    {
//        RavenDocumentType pref = new RavenDocumentType();
//
//        Properties props = new Properties();
//        try {
//            props.load(RavenDocument.class.getResourceAsStream("/info/editor.properties"));
//        } catch (IOException ex) {
//            Logger.getLogger(RavenDocument.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        pref.setVersion(props.getProperty("version"));
//
//        //Save Documents
//        for (NodeSymbol doc: symbols)
//        {
//            NodeSymbolType type = doc.export();
//            pref.getSymbols().add(type);
//        }
//        pref.setCurSymbol(curSymbol == null ? null : curSymbol.getSymbolName());
//
//        //Save meta properties
//        MetaPropertySetGroupType groups = new MetaPropertySetGroupType();
//        pref.setPropertySetGroups(groups);
//        for (String key: metaProperties.keySet())
//        {
//            Properties prop = metaProperties.get(key);
//            MetaPropertySetType set = new MetaPropertySetType();
//            groups.getPropertySet().add(set);
//            
//            for (String name: prop.stringPropertyNames())
//            {
//                String value = prop.getProperty(name);
//                MetaPropertyEntryType entry = new MetaPropertyEntryType();
//                set.getEntry().add(entry);
//                entry.setName(name);
//                entry.setValue(value);
//            }
//        }
//
//        return pref;
//    }
//
//    private RavenDocumentType load(File file)
//    {
//        setSource(file);
//
//        if (file == null)
//        {
//            return null;
//        }
//
//        return JAXBUtil.loadJAXB(RavenDocumentType.class, file);
//    }
//

    public void save(File file)
    {
        NodeDocumentType pref = export();

        ObjectFactory fact = new ObjectFactory();
        JAXBElement<NodeDocumentType> value = 
                fact.createNodeDocument(pref);

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

//    @Override
//    public ResourceCache getResourceCache()
//    {
//        return editor.getResourceCache();
//    }

    @Override
    public File getDocumentSource()
    {
        return source;
    }

    @Override
    public Environment getEnv()
    {
        return this;
    }
}
