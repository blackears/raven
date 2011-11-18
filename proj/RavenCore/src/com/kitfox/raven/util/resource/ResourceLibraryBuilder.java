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

package com.kitfox.raven.util.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Path names in the form
 * path/to/resource/resourceName
 *
 * @author kitfox
 */
public class ResourceLibraryBuilder
{
    private final ResourceBuilderTask task;
    
    private final ResourceDataStore resDataStore;
//    private final ResourceStore webStore;

    public static final String NAME_RES_INDEX = "res.index";
//    public static final String NAME_WEB_INDEX = "web.index";

    HashMap<URI, Integer> resourceIdMap = new HashMap<URI, Integer>();

    public ResourceLibraryBuilder(ResourceBuilderTask task)
    {
        this.task = task;

        resDataStore = new ResourceDataStore(task.getDestResourceDir());
//        webStore = new ResourceStore(task.getDestWebResourceDir());

        resDataStore.reserveName(NAME_RES_INDEX);
//        resStore.reserveName(NAME_WEB_INDEX);
    }

    /**
     * Loads resource and exports it to library.  Returns id that can be used
     * to lookup resource in library.  Cache of filenames is used to
     * prevent duplicate exports.
     *
     * @param file
     * @return
     */
    public int exportResource(File file)
    {
        URI uri = file.toURI();
        Integer index = resourceIdMap.get(uri);
        if (index != null)
        {
            //If already exported, just return id
            return index;
        }

        ResourceProvider prov = ResourceIndex.inst().getProvider(uri);

        if (prov == null)
        {
            return -1;
        }

        index = resourceIdMap.size();
        resourceIdMap.put(uri, index);
        prov.exportToLibrary(this, file);
        return index;
    }

    public String calcSourceQname(File source)
    {
        File srcDir = task.getSourceDir();

        URI srcDirUri = srcDir.toURI();
        URI srcUri = source.toURI();
        URI relUri = srcDirUri.relativize(srcUri);
        String path = relUri.toString();

        int slash = path.lastIndexOf('/');
        String pkgName = path.substring(0, slash).replace('/', '.');
        String clsName = path.substring(slash + 1);
        clsName = "" + clsName.substring(0, 1).toUpperCase() + clsName.substring(1);

        int dot = clsName.indexOf('.');
        if (dot != -1)
        {
            clsName = clsName.substring(0, dot);
        }

        return pkgName + "." + clsName;
    }

    public OutputStream exportSource(String qname) throws FileNotFoundException
    {
        int dot = qname.lastIndexOf('.');
        String pkgName = qname.substring(0, dot);
        String clsName = qname.substring(dot + 1);

        File file = new File(task.getDestSourceDir(),
                pkgName.replace('.', '/') + "/" + clsName + ".java");
        file.getParentFile().mkdirs();

        FileOutputStream fout = new FileOutputStream(file);
        return fout;
    }

    public void exportIndices()
    {
        try
        {
            File file = new File(task.destResourceDir, NAME_RES_INDEX);
            PrintWriter pw = new PrintWriter(file);

            //Print namespace
//            pw.println(task.getNamespace());

            //Print file indices
            resDataStore.exportIndex(pw);
            pw.close();
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(ResourceLibraryBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        
//        try
//        {
//            File file = new File(task.destResourceDir, NAME_WEB_INDEX);
//            PrintWriter pw = new PrintWriter(file);
//            webStore.exportIndex(pw);
//            pw.close();
//        } catch (FileNotFoundException ex)
//        {
//            Logger.getLogger(ResourceLibraryBuilder.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
    }

    /**
     * @return the task
     */
    public ResourceBuilderTask getTask()
    {
        return task;
    }

    /**
     * @return the resStore
     */
    public ResourceDataStore getResStore()
    {
        return resDataStore;
    }

//    /**
//     * @return the webStore
//     */
//    public ResourceStore getWebStore()
//    {
//        return webStore;
//    }

//    //---------------------------------
//    class ResourceInfo
//    {
//        int id;
//        URI uri;
//
//    }

}
