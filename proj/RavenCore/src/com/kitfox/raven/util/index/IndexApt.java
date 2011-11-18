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

package com.kitfox.raven.util.index;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 *
 * @author kitfox
 */
public class IndexApt extends AbstractProcessor
{
//    static class State implements Serializable
//    {
//        String service;
//        String provider;
//
//        State(String service, String provider)
//        {
//            this.service = service;
//            this.provider = provider;
//        }
//    }

    final Configuration cfg = new Configuration();
    Template ftlTemplate;
    final String TMPLT_NAME = "IndexStubTmplt.ftl";

    public IndexApt()
    {
        cfg.setClassForTemplateLoading(getClass(), "");
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        try {
            ftlTemplate = cfg.getTemplate(TMPLT_NAME);
        } catch (IOException ex) {
            Logger.getLogger(IndexApt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.RELEASE_6;
    }


    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        return Collections.singleton(IndexAnno.class.getName());
    }

    @Override
    public Set<String> getSupportedOptions()
    {
        HashSet<String> set = new HashSet<String>();
        return set;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
//        if (roundEnv.processingOver() && ! roundEnv.errorRaised())
//        {
//            return generate();
//        }
//
//        StateSaver<State> stateSaver = StateSaver.getInstance(this, State.class, processingEnv);
//        stateSaver.startRound(roundEnv);

        for (Element ele : roundEnv.getElementsAnnotatedWith(IndexAnno.class))
        {
            if (ele.getKind() != ElementKind.CLASS)
            {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                        "Only classes should have @IndexAnno: ", ele);
                continue;
            }
            TypeElement classEle = (TypeElement)ele;

            Element pkgEle = classEle.getEnclosingElement();
            while (pkgEle.getKind() != ElementKind.PACKAGE)
            {
                pkgEle = pkgEle.getEnclosingElement();
            }
            String packageName = pkgEle.toString();

            String classQname = classEle.getQualifiedName().toString();
//            String shortName = classQname.substring(packageName.length() + 1);
            String simpleName = classEle.getSimpleName().toString();

            IndexAnno anno = ele.getAnnotation(IndexAnno.class);

            String cat = null;
            try
            {
                Class catCls = anno.category();
                cat = catCls.getCanonicalName();
            }
            catch (MirroredTypeException ex)
            {
                cat = ex.getTypeMirror().toString();
            }

            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("author", "kitfox");
            data.put("date", new Date());
            data.put("package", packageName);
            data.put("className", simpleName);
            data.put("classQname", classQname);
            data.put("indexQname", cat);
//            data.put("leaf", anno.leaf());

            try {
//                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Wrapping " + itemAiCls);

                JavaFileObject f = processingEnv.getFiler().createSourceFile(packageName + "." + simpleName + "Stub");
                Writer w = f.openWriter();
                ftlTemplate.process(data, w);
                w.close();
            } catch (IOException x) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, x.toString());
            } catch (TemplateException x) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, x.toString());
            } catch (Exception x) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, x.toString());
            }

//            stateSaver.addData(classEle,
//                new State(classQname + "Stub", classQname));
        }

        return true;
    }
//
//    private boolean generate()
//    {
//        StateSaver<State> stateSaver = StateSaver.getInstance(this, State.class, processingEnv);
////        Comparator<State> byService = new Comparator<State>() {
////            public int compare(ServiceProviderProcessor.State o1, ServiceProviderProcessor.State o2) {
////                return o1.service.compareTo(o2.service);
////            }
////        };
//        Filer filer = processingEnv.getFiler();
//        try
//        {
//            FileObject f = filer.createResource(StandardLocation.CLASS_OUTPUT,
//                    "", "META-INF/services/" + IndexStub.class.getCanonicalName());
//
//            Writer out = f.openWriter();
//            PrintWriter pw = new PrintWriter(out);
//
//            for (State state : stateSaver.getData())
//            {
//                pw.println(state.service);
//            }
//            pw.close();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            throw new RuntimeException(ex);
//        }
//
//        return true;
//    }

}
