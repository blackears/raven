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

import com.kitfox.raven.util.resource.ResourceCache;
import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.util.tree.NodeDocumentProvider;
import com.kitfox.raven.util.tree.NodeDocumentProviderIndex;
import com.kitfox.xml.schema.ravendocumentschema.MetaPropertyEntryType;
import com.kitfox.xml.schema.ravendocumentschema.MetaPropertySetGroupType;
import com.kitfox.xml.schema.ravendocumentschema.MetaPropertySetType;
import com.kitfox.xml.schema.ravendocumentschema.ObjectFactory;
import com.kitfox.xml.schema.ravendocumentschema.RavenDocumentType;
import java.awt.Window;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

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

//    private RavenNodeRoot root;
    private NodeDocument root;

    ArrayList<RavenDocumentListener> listeners = new ArrayList<RavenDocumentListener>();

    //Meta properties provide a way for views and tools to store document
    // specific persistent information
    HashMap<String, Properties> metaProperties = new HashMap<String, Properties>();

    public RavenDocument(RavenEditor editor, NodeDocument root)
    {
        this.editor = editor;
        this.root = root;

        root.setEnv(this);
    }

    public RavenDocument(RavenEditor editor, File file)
    {
        this.editor = editor;

        //Load document
        RavenDocumentType docTree = load(file);
        String docClass = docTree.getRoot().getClazz();
        NodeDocumentProvider prov =
                NodeDocumentProviderIndex.inst().getProvider(docClass);
        this.root = prov.loadDocument(docTree);
        root.setEnv(this);

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

        //Save Document
        pref.setRoot(root.export());

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

        try {
            JAXBContext context = JAXBContext.newInstance(RavenDocumentType.class);
            StreamSource streamSource = new StreamSource(file);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            JAXBElement<RavenDocumentType> ele = unmarshaller.unmarshal(streamSource, RavenDocumentType.class);
            return ele.getValue();
        } catch (JAXBException ex) {
            Logger.getLogger(RavenDocument.class.getName()).log(Level.WARNING, null, ex);
        }
        return null;
    }


    public void save(File file)
    {
        RavenDocumentType pref = asJAXB();

        ObjectFactory fact
                = new ObjectFactory();
        JAXBElement<RavenDocumentType> value = fact.createRavenDocument(pref);

        try {
            FileWriter fw = new FileWriter(file);

            JAXBContext context = JAXBContext.newInstance(RavenDocumentType.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            marshaller.marshal(value, fw);
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(RavenEditor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(RavenEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the sceneGraph
     */
    public NodeDocument getRoot()
    {
        return root;
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
}
