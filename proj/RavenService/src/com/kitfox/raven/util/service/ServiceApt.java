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

package com.kitfox.raven.util.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 *
 * @author kitfox
 */
public class ServiceApt extends AbstractProcessor
{
    public static final String CACHE_FILE = "service.log";

    HashMap<String, HashSet<String>> serviceCache
            = new HashMap<String, HashSet<String>>();

    public ServiceApt()
    {
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.RELEASE_6;
    }


    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        return Collections.singleton(ServiceInst.class.getName());
    }

    @Override
    public Set<String> getSupportedOptions()
    {
        HashSet<String> set = new HashSet<String>();
        return set;
    }

    private void addToCache(String service, String instance)
    {
        HashSet<String> list = serviceCache.get(service);
        if (list == null)
        {
            list = new HashSet<String>();
            serviceCache.put(service, list);
        }
        list.add(instance);
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);

        //Load cached services
        try
        {
            Filer filer = processingEnv.getFiler();
            FileObject fo = filer.getResource(StandardLocation.SOURCE_OUTPUT,
                    ServiceApt.class.getName(), CACHE_FILE);
            
            if (fo.getLastModified() != 0)
            {
                BufferedReader reader = new BufferedReader(fo.openReader(true));
                for (String line = reader.readLine();
                    line != null;
                    line = reader.readLine())
                {
                    int idx = line.indexOf('/');
                    String service = line.substring(0, idx);
                    String instance = line.substring(idx + 1);

                    addToCache(service, instance);
                }
                reader.close();
            }
        }
        catch (FileNotFoundException ex)
        {
            //OK.  Just means cache has not been created yet.
        }
        catch (IOException ex)
        {
            Logger.getLogger(ServiceApt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        if (roundEnv.processingOver() && ! roundEnv.errorRaised())
        {
            return generate();
        }

        for (Element ele : roundEnv.getElementsAnnotatedWith(ServiceInst.class))
        {
            if (ele.getKind() != ElementKind.CLASS)
            {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                        "Only classes should have @Service: ", ele);
                continue;
            }
            TypeElement annoClassEle = (TypeElement)ele;

            Element pkgEle = annoClassEle.getEnclosingElement();
            while (pkgEle.getKind() != ElementKind.PACKAGE)
            {
                pkgEle = pkgEle.getEnclosingElement();
            }

            String annoClassQname = buildClassName(annoClassEle);

            ServiceInst anno = ele.getAnnotation(ServiceInst.class);

            String service = null;
            try
            {
                Class servCls = anno.service();
                service = servCls.getCanonicalName();
            }
            catch (MirroredTypeException ex)
            {
                service = buildClassName((TypeElement)((DeclaredType)ex.getTypeMirror()).asElement());
            }

            //Add to cache
            addToCache(service, annoClassQname);
        }

        return true;
    }

    private boolean generate()
    {
        Filer filer = processingEnv.getFiler();
        try
        {
            for (String service: serviceCache.keySet())
            {
                FileObject f = filer.createResource(StandardLocation.CLASS_OUTPUT,
                        "", "META-INF/services/" + service);

                Writer out = f.openWriter();
                PrintWriter pw = new PrintWriter(out);

                ArrayList<String> providers =
                        new ArrayList(serviceCache.get(service));
                Collections.sort(providers);
                for (String provider: providers)
                {
                    pw.println(provider);
                }
                pw.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(ServiceApt.class.getName()).log(Level.SEVERE, null, ex);
        }
        try
        {
            FileObject fo = filer.createResource(StandardLocation.SOURCE_OUTPUT,
                    ServiceApt.class.getName(), CACHE_FILE);

            PrintWriter writer = new PrintWriter(fo.openWriter());

            ArrayList<String> services = new ArrayList<String>(serviceCache.keySet());
            Collections.sort(services);
            for (String service: services)
            {
                ArrayList<String> instances =
                        new ArrayList(serviceCache.get(service));
                Collections.sort(instances);

                for (String inst: instances)
                {
                    writer.println(service + "/" + inst);
                }
            }

            writer.close();
        } catch (IOException ex)
        {
            Logger.getLogger(ServiceApt.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        return true;
    }

    private String buildClassName(Element ele)
    {
        String sb = null;
        while (true)
        {
            if (sb == null)
            {
                sb = ele.getSimpleName().toString().replace('.', '$');
                ele = ele.getEnclosingElement();
            }
            else if (ele instanceof TypeElement)
            {
                sb = ele.getSimpleName().toString() + "$" + sb;
                ele = ele.getEnclosingElement();
            }
            else if (ele instanceof PackageElement)
            {
                return ele.toString() + "." + sb;
            }
            else
            {
                throw new RuntimeException("Illegal element " + ele);
            }
        }
    }

}
