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

package com.kitfox.rabbit.ant;

import com.kitfox.rabbit.codegen.DocumentExporter;
import com.kitfox.rabbit.parser.RabbitDocument;
import com.kitfox.rabbit.parser.RabbitUniverseDom;
import com.kitfox.rabbit.property.FloatArrayEditor;
import com.kitfox.rabbit.property.IntegerArrayEditor;
import com.kitfox.rabbit.property.Path2DDoubleEditor;
import java.awt.geom.Path2D;
import java.beans.PropertyEditorManager;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 *
 * @author kitfox
 */
public class SVGToResourceTask extends Task
{
    private FileSet inputResources;
    private File dest;
//    private Path classpath;
//    private Path sourcepath;

    @Override
    public void init() throws BuildException
    {
        PropertyEditorManager.registerEditor(int[].class, IntegerArrayEditor.class);
        PropertyEditorManager.registerEditor(float[].class, FloatArrayEditor.class);
        PropertyEditorManager.registerEditor(Path2D.Float.class, Path2DDoubleEditor.class);
    }

    @Override
    public void execute() throws BuildException
    {
        if (inputResources == null)
        {
            throw new BuildException("Sources not specified");
        }

        //Process all resources
        DirectoryScanner ds = inputResources.getDirectoryScanner();
        String[] files = ds.getIncludedFiles();
        File baseDir = ds.getBasedir();

        //Load all svg resources into a single universe
        RabbitUniverseDom universe = new RabbitUniverseDom();

        for (int i = 0; i < files.length; i++)
        {
            String path = files[i].replace('\\', '/');
//            File file = new File(baseDir, path);
//System.err.println("Res input file: " + file + "(" + path + ")");

//            processSVG(baseDir, path);
            File file = new File(baseDir, path);
            try {
                //Cause builder to load file
                universe.getDocument(file.toURI().toURL());
            } catch (MalformedURLException ex) {
                Logger.getLogger(SVGToResourceTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //Write out all loaded documents
        for (URL url: universe.getDocumentUrls())
        {
            RabbitDocument docBuild = universe.getDocument(url);
            DocumentExporter exp = new DocumentExporter(docBuild, baseDir);
            exp.export(dest);
        }
    }

//    private void processSVG(File baseDir, String path)
//    {
//        File file = new File(baseDir, path);
//        int nameStart = path.lastIndexOf('/');
//        String className = path.substring(nameStart + 1);
//        if (className.endsWith(".svg"))
//        {
//            className = className.substring(0, className.length() - 4);
//        }
//        String packageName = nameStart == -1 ? ""
//                : path.substring(0, nameStart).replace('/', '.');
//
//        SVGProducer prod = new SVGProducer(file, baseDir);
//        prod.produceResources();
//    }


    public FileSet createInputResources()
    {
        inputResources = new FileSet();
        return inputResources;
    }

    /**
     * @return the dest
     */
    public File getDest() {
        return dest;
    }

    /**
     * @param dest the dest to set
     */
    public void setDest(File dest) {
        this.dest = dest;
    }

//    public Path createClasspath()
//    {
//        classpath = new Path(getProject());
//        return classpath;
//    }
//
//    public Path createSourcepath()
//    {
//        sourcepath = new Path(getProject());
//        return sourcepath;
//    }

}
