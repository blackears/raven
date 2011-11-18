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

import com.kitfox.raven.util.resource.ResourceIndex;
import com.kitfox.raven.util.resource.ResourceProvider;
import java.io.File;
import java.net.URI;
import java.util.Date;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 *
 * @author kitfox
 */
public class ResourceBuilderTask extends Task
{
    protected FileSet source;
//    private File destWebResourceDir;
    protected File destResourceDir;
    protected File destSourceDir;
    protected Date date = new Date();
    protected String author = "kitfox";
    private String namespace = "";
//    private File history;  //Keep history of allocated resource ids

    @Override
    public void init() throws BuildException
    {
        ResourceIndex.inst().reload(getClass().getClassLoader());
    }

    @Override
    public void execute() throws BuildException
    {
        ResourceLibraryBuilder builder = new ResourceLibraryBuilder(this);

        //Process all resources
        DirectoryScanner ds = source.getDirectoryScanner();
        String[] files = ds.getIncludedFiles();
        File baseDir = ds.getBasedir();

        for (int i = 0; i < files.length; i++)
        {
//            String path = files[i].replace('\\', '/');
//            File file = new File(baseDir, path);
            String fileName = files[i];

            File file = new File(baseDir, fileName);
            builder.exportResource(file);
//            URI uri = file.toURI();
//            ResourceProvider prov = ResourceIndex.inst().getProvider(uri);
//
//            if (prov == null)
//            {
//                continue;
//            }
//
//            prov.exportToLibrary(builder, file);
        }

        //Write output
        builder.exportIndices();
    }

    public FileSet createSource()
    {
        source = new FileSet();
        return source;
    }

    /**
     * @return the date
     */
    public Date getDate()
    {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date)
    {
        this.date = date;
    }

    /**
     * @return the author
     */
    public String getAuthor()
    {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author)
    {
        this.author = author;
    }

    /**
     * @return the destResourceDir
     */
    public File getDestResourceDir()
    {
        return destResourceDir;
    }

    /**
     * @param destResourceDir the destResourceDir to set
     */
    public void setDestResourceDir(File destResourceDir)
    {
        this.destResourceDir = destResourceDir;
    }

    /**
     * @return the destSourceDir
     */
    public File getDestSourceDir()
    {
        return destSourceDir;
    }

    /**
     * @param destSourceDir the destSourceDir to set
     */
    public void setDestSourceDir(File destSourceDir)
    {
        this.destSourceDir = destSourceDir;
    }

//    /**
//     * @return the destWebResourceDir
//     */
//    public File getDestWebResourceDir()
//    {
//        return destWebResourceDir;
//    }
//
//    /**
//     * @param destWebResourceDir the destWebResourceDir to set
//     */
//    public void setDestWebResourceDir(File destWebResourceDir)
//    {
//        this.destWebResourceDir = destWebResourceDir;
//    }

    public File getSourceDir()
    {
        return source.getDir();
    }

    /**
     * @return the namespace
     */
    public String getNamespace()
    {
        return namespace;
    }

    /**
     * @param namespace the namespace to set
     */
    public void setNamespace(String namespace)
    {
        this.namespace = namespace;
    }

//    /**
//     * @return the history
//     */
//    public File getHistory()
//    {
//        return history;
//    }
//
//    /**
//     * @param history the history to set
//     */
//    public void setHistory(File history)
//    {
//        this.history = history;
//    }


}
