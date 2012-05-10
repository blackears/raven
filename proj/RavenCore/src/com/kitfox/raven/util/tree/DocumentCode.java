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

package com.kitfox.raven.util.tree;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author kitfox
 */
public class DocumentCode
{
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    protected final SourceCode source = new SourceCode();
    public static final String PROP_SOURCE = "source";
    protected final SourceCode extendsClass = new SourceCode();
    public static final String PROP_EXTENDSCLASS = "extendsClass";
    protected final SourceCode imports = new SourceCode();
    public static final String PROP_IMPORTS = "imports";
    protected final SourceCode annotations = new SourceCode();
    public static final String PROP_ANNOTATIONS = "annotations";
    protected final SourceCode implementsClasses = new SourceCode();
    public static final String PROP_IMPLEMENTSCLASSES = "implementsClasses";

    public DocumentCode()
    {
        this("", "", "", "", "");
    }

    public DocumentCode(String extendsClass, String implementsClasses,
            String imports, String annotations, String source)
    {
        this.source.setSource(source);
        this.extendsClass.setSource(extendsClass);
        this.imports.setSource(imports);
        this.annotations.setSource(annotations);
        this.implementsClasses.setSource(implementsClasses);
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Get the value of implementsClasses
     *
     * @return the value of implementsClasses
     */
    public SourceCode getImplementsClasses()
    {
        return implementsClasses;
    }

    /**
     * Get the value of annotations
     *
     * @return the value of annotations
     */
    public SourceCode getAnnotations()
    {
        return annotations;
    }

    /**
     * Get the value of imports
     *
     * @return the value of imports
     */
    public SourceCode getImports()
    {
        return imports;
    }

    /**
     * Get the value of extendsClass
     *
     * @return the value of extendsClass
     */
    public SourceCode getExtendsClass()
    {
        return extendsClass;
    }

    /**
     * Get the value of source
     *
     * @return the value of source
     */
    public SourceCode getSource()
    {
        return source;
    }

//    public DocumentCodeType export()
//    {
//        DocumentCodeType type = new DocumentCodeType();
//
//        type.setAnnotations(annotations.export());
//        type.setExtends(extendsClass.export());
//        type.setImplements(implementsClasses.export());
//        type.setImports(imports.export());
//        type.setSource(source.export());
//
//        return type;
//    }
//
//    public void load(DocumentCodeType sourceCode)
//    {
//        if (sourceCode != null)
//        {
//            annotations.load(sourceCode.getAnnotations());
//            extendsClass.load(sourceCode.getExtends());
//            implementsClasses.load(sourceCode.getImplements());
//            imports.load(sourceCode.getImports());
//            source.load(sourceCode.getSource());
//        }
//    }

    public boolean isEmpty()
    {
        return source.isEmpty()
                && extendsClass.isEmpty()
                && implementsClasses.isEmpty()
                && imports.isEmpty()
                && annotations.isEmpty();
    }
}
