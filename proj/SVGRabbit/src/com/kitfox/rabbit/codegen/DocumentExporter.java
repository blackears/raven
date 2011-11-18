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

package com.kitfox.rabbit.codegen;

import com.kitfox.rabbit.parser.RabbitDocument;
import com.kitfox.rabbit.types.ElementRef;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class DocumentExporter
{
    final RabbitDocument builder;
    final File baseDir;

    public DocumentExporter(RabbitDocument builder, File baseDir)
    {
        this.builder = builder;
        this.baseDir = baseDir;
    }

    public String getAuthor()
    {
        return "kitfox";
    }

    public Date getDate()
    {
        return new Date();
    }

    /**
     * @return the packageName
     */
    public String getPackageName()
    {
        return builder.getPackageName(baseDir);
    }

    /**
     * @return the className
     */
    public String getClassName()
    {
        return builder.getClassName();
    }

    public int getDocId()
    {
        return builder.getDocumentId();
    }

    public HashMap<String, String> getElementRefs()
    {
        HashMap<String, String> map = new HashMap<String, String>();

        for (String eleName: builder.getReferencedElements())
        {
            ElementRef ref = builder.getLocalElementRef(eleName);
            map.put("" + ref.getElementIndex(), eleName);
        }
        return map;
    }

    public NodeExporter getRoot()
    {
        return new NodeExporter(builder.getRootNode(), this);
    }

    public void export(File dest)
    {
        String pkgName = builder.getPackageName(baseDir);
        String clsName = builder.getClassName();

        File outFile = new File(dest, pkgName.replace('.', '/') + "/" + clsName + ".java");

        File parentFile = outFile.getParentFile();
        parentFile.mkdirs();

        //Prepare freemarker
        final Configuration cfg = new Configuration();
        Template ftlTemplate;
        cfg.setClassForTemplateLoading(getClass(), "");
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        try {
            ftlTemplate = cfg.getTemplate("RabbitDocGen.ftl");
        } catch (IOException ex) {
            Logger.getLogger(DocumentExporter.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        //Write file
        try {
            FileWriter w = new FileWriter(outFile);
            ftlTemplate.process(this, w);
            w.close();
        } catch (TemplateException ex) {
            Logger.getLogger(DocumentExporter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DocumentExporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}
