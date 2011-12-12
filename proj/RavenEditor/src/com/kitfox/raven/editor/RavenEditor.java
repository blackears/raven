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
import com.kitfox.xml.ns.raveneditorpreferences.FileType;
import com.kitfox.xml.ns.raveneditorpreferences.ObjectFactory;
import com.kitfox.xml.ns.raveneditorpreferences.RavenEditorPreferencesType;
import com.kitfox.xml.ns.raveneditorpreferences.RecentFilesType;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
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
public class RavenEditor
{
    private static RavenEditor instance;

    private final DocumentIOHelper documentIOHelper;

    private final PlayerThread player = new PlayerThread(this);
    private final RavenViewManager viewManager;
    private final RavenToolManager toolManager;
    private final RavenImportManager importManager;
    private final RavenExportManager exportManager;
    private final ResourceCache resourceCache;

    final File homeDir;
    final File prefFile;

    private final int MAX_RECENT_FILES = 20;
    private final ArrayList<File> recentFiles = new ArrayList<File>();

    private RavenDocument document;

    ArrayList<RavenEditorListener> listeners = new ArrayList<RavenEditorListener>();

    private RavenEditor()
    {
        viewManager = new RavenViewManager(this);
        toolManager = new RavenToolManager(this);
        importManager = new RavenImportManager(this);
        exportManager = new RavenExportManager(this);
        resourceCache = new ResourceCache();
        documentIOHelper = new DocumentIOHelper(viewManager);
        
        //Find and create our Raven home directory
        String home = System.getProperty("user.home");
        homeDir = new File(home, ".raven");
        homeDir.mkdirs();
        
        prefFile = new File(homeDir, "preferences.xml");
        
        loadPreferences();
    }

    synchronized public static RavenEditor inst()
    {
//        if (!instance.player.isAlive())
//        {
//            instance.player.start();
//        }
        if (instance == null)
        {
            instance = new RavenEditor();
            instance.player.start();
        }
        return instance;
    }

    public void addRavenEditorListener(RavenEditorListener l)
    {
        listeners.add(l);
    }

    public void removeRavenEditorListener(RavenEditorListener l)
    {
        listeners.remove(l);
    }

    public void showEditor()
    {
        viewManager.show();
    }

    private void loadPreferences()
    {
        if (!prefFile.exists())
        {
            loadPreferences(null);
            return;
        }

        try {
            JAXBContext context = JAXBContext.newInstance(RavenEditorPreferencesType.class);
            StreamSource source = new StreamSource(prefFile);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            JAXBElement<RavenEditorPreferencesType> ele = unmarshaller.unmarshal(source, RavenEditorPreferencesType.class);
            loadPreferences(ele.getValue());
        } catch (JAXBException ex) {
            Logger.getLogger(RavenEditor.class.getName()).log(Level.WARNING, null, ex);
        }
    }

    protected void loadPreferences(RavenEditorPreferencesType pref)
    {
        if (pref == null)
        {
            viewManager.loadPreferences(null);
            toolManager.loadPreferences(null);
            importManager.loadPreferences(null);
            exportManager.loadPreferences(null);
            return;
        }

        viewManager.loadPreferences(pref);
        toolManager.loadPreferences(pref);
        importManager.loadPreferences(pref);
        exportManager.loadPreferences(pref);

        //Set up recent file list
        {
            recentFiles.clear();
            RecentFilesType rft = pref.getRecentFiles();
            if (rft != null)
            {
                for (FileType ft: rft.getFile())
                {
                    File file = new File(ft.getValue());
                    recentFiles.add(file);
                }
            }
            fireRecentFilesChanged();
        }
    }

    public void savePreferences()
    {
        RavenEditorPreferencesType pref = prefAsJAXB();

        ObjectFactory fact
                = new ObjectFactory();
        JAXBElement<RavenEditorPreferencesType> value = fact.createRavenEditorPreferences(pref);


        try {
            FileWriter fw = new FileWriter(prefFile);

            JAXBContext context = JAXBContext.newInstance(RavenEditorPreferencesType.class);
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

    protected RavenEditorPreferencesType prefAsJAXB()
    {
        RavenEditorPreferencesType pref = new RavenEditorPreferencesType();

        viewManager.exportViewLayout(pref);
        toolManager.export(pref);
        importManager.export(pref);
        exportManager.export(pref);

        //Save References
        {
            RecentFilesType type = new RecentFilesType();
            pref.setRecentFiles(type);

            for (File file: recentFiles)
            {
                try {
                    FileType ft = new FileType();
                    ft.setValue(file.getCanonicalPath());
                    type.getFile().add(ft);
                } catch (IOException ex) {
                    Logger.getLogger(RavenEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

        return pref;
    }

    public ArrayList<File> getRecentFileList()
    {
        return new ArrayList<File>(recentFiles);
    }

    /**
     * @return the project
     */
    public RavenDocument getDocument()
    {
        return document;
    }

    public void setDocument(RavenDocument document)
    {
        this.document = document;

        fireProjectChanged();
    }

    public void open(File file)
    {
        RavenDocument openProject = new RavenDocument(this, file);
        setDocument(openProject);

        setMostRecentFile(file);
    }

    void setMostRecentFile(File file)
    {
        recentFiles.remove(file);
        recentFiles.add(0, file);
        while (recentFiles.size() >= MAX_RECENT_FILES)
        {
            recentFiles.remove(recentFiles.size() - 1);
        }
        
        fireRecentFilesChanged();
    }

    private void fireRecentFilesChanged()
    {
        EventObject evt = new EventObject(this);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).recentFilesChanged(evt);
        }
    }

    private void fireProjectChanged()
    {
        EventObject evt = new EventObject(this);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).documentChanged(evt);
        }
    }

//    /**
//     * @return the history
//     */
//    public History getHistory() {
//        return history;
//    }

    /**
     * @return the viewManager
     */
    public RavenViewManager getViewManager() {
        return viewManager;
    }

    /**
     * @return the documentIOHelper
     */
    public DocumentIOHelper getDocumentIOHelper() {
        return documentIOHelper;
    }

    /**
     * @return the player
     */
    public PlayerThread getPlayer() {
        return player;
    }

    /**
     * @return the toolManager
     */
    public RavenToolManager getToolManager() {
        return toolManager;
    }

    public void exit()
    {
        viewManager.exit();
    }

    /**
     * @return the importManager
     */
    public RavenImportManager getImportManager()
    {
        return importManager;
    }

    /**
     * @return the exportManager
     */
    public RavenExportManager getExportManager()
    {
        return exportManager;
    }

    /**
     * @return the resourceManager
     */
    public ResourceCache getResourceCache()
    {
        return resourceCache;
    }

}
