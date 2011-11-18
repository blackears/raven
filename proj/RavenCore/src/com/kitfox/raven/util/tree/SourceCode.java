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

import com.kitfox.xml.schema.ravendocumentschema.SourceCodeType;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author kitfox
 */
public class SourceCode
{
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

//    protected String language;
//    public static final String PROP_LANGUAGE = "language";
//
//    protected String header;
//    public static final String PROP_HEADER = "header";
    
    protected String source;
    public static final String PROP_SOURCE = "source";

    public SourceCode()
    {
        this("");
    }

    public SourceCode(String source)
    {
//        this.language = language;
//        this.header = header;
        this.source = source;
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
     * Get the value of source
     *
     * @return the value of source
     */
    public String getSource()
    {
        return source;
    }

    /**
     * Set the value of source
     *
     * @param source new value of source
     */
    public void setSource(String source)
    {
        String oldSource = this.source;
        this.source = source;
        propertyChangeSupport.firePropertyChange(PROP_SOURCE, oldSource, source);
    }

//    /**
//     * Get the value of header
//     *
//     * @return the value of header
//     */
//    public String getHeader()
//    {
//        return header;
//    }
//
//    /**
//     * Set the value of header
//     *
//     * @param header new value of header
//     */
//    public void setHeader(String header)
//    {
//        String oldHeader = this.header;
//        this.header = header;
//        propertyChangeSupport.firePropertyChange(PROP_HEADER, oldHeader, header);
//    }
//
//    /**
//     * Get the value of language
//     *
//     * @return the value of language
//     */
//    public String getLanguage()
//    {
//        return language;
//    }
//
//    /**
//     * Set the value of language
//     *
//     * @param language new value of language
//     */
//    public void setLanguage(String language)
//    {
//        String oldLanguage = this.language;
//        this.language = language;
//        propertyChangeSupport.firePropertyChange(PROP_LANGUAGE, oldLanguage, language);
//    }

    public SourceCodeType export()
    {
        SourceCodeType type = new SourceCodeType();

        type.setSource(source);

        return type;
    }

    public void load(SourceCodeType sourceCode)
    {
        if (sourceCode != null)
        {
            setSource(sourceCode.getSource());
        }
    }

    public boolean isEmpty()
    {
        return "".equals(source);
    }
}
