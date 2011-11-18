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

package com.kitfox.raven.util.resource.common;

import com.kitfox.raven.util.FileFilterSuffix;
import com.kitfox.raven.util.resource.ResourceProvider;
import com.kitfox.raven.util.resource.ResourceLibraryBuilder;
import com.kitfox.raven.util.resource.ResourceDataStore;
import com.kitfox.raven.util.resource.ResourceDataStore.Record;
import com.kitfox.raven.util.service.ServiceInst;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author kitfox
 */
@ServiceInst(service=ResourceProvider.class)
public class ResourceProviderBufferedImage extends ResourceProvider<BufferedImage>
{

    public ResourceProviderBufferedImage()
    {
        super(BufferedImage.class);
    }

    @Override
    public FileFilter getFileFilter()
    {
        String[] suffix = ImageIO.getReaderFileSuffixes();
        FileFilterSuffix filter = new FileFilterSuffix("Image File", suffix);

        return filter;
    }

    @Override
    public boolean accepts(URI uri)
    {
        String path = uri.getPath().toLowerCase();
        String[] suffix = ImageIO.getReaderFileSuffixes();
        for (int i = 0; i < suffix.length; ++i)
        {
            if (path.endsWith("." + suffix[i].toLowerCase()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public BufferedImage load(URI uri)
    {
        try {
            return ImageIO.read(uri.toURL());
        } catch (IOException ex) {
            Logger.getLogger(ResourceProviderBufferedImage.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    @Override
    public int exportToLibrary(ResourceLibraryBuilder builder, File source)
    {
        //Put file directly in library
        ResourceDataStore store = builder.getResStore();
        Record rec = store.exportFile(source.toURI());

        String qname = builder.calcSourceQname(source);
        try
        {
            OutputStream out = builder.exportSource(qname);
            PrintWriter pw = new PrintWriter(out);
            exportSource(pw, builder, qname, rec);
            pw.close();
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(ResourceProviderBufferedImage.class.getName()).log(Level.SEVERE, null, ex);
        }

        return rec.getId();
    }

    private void exportSource(PrintWriter pw, ResourceLibraryBuilder builder, String qname, Record rec)
    {
//        ResourceBuilderTask t = builder.getTask();
//        ResourceBuilderTask task = builder.getTask();

        int dot = qname.lastIndexOf('.');
        String pkgName = qname.substring(0, dot);
        String clsName = qname.substring(dot + 1);

        pw.println("/*");
        pw.println(" * Generated index");
        pw.println(" * @author " + builder.getTask().getAuthor());
        pw.println(" * @date " + builder.getTask().getDate());
        pw.println(" */");
        pw.println();
        pw.println("package " + pkgName + ";");
        pw.println();
        pw.println("public class " + clsName);
        pw.println("{");

        pw.println("    public static final int RES_ID = " + rec.getId() + ";");
//        pw.println();
//        for (String prop: propList)
//        {
//            pw.println("    " + prop);
//        }
//        pw.println();

        pw.println("}");
        
    }

}
