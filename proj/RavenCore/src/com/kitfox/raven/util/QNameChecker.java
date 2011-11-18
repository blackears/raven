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

package com.kitfox.raven.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author kitfox
 */
public class QNameChecker extends DefaultHandler
{
    public static class ScannedRootSAXException extends SAXException
    {
        public static final long serialVersionUID = 0;
    }
    
    //String namespace;
    QName qname;
    
    private QNameChecker()
    {
    }
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        qname = new QName(uri, localName);
        
        //We are only instrested in the first element.  Abort the read
        throw new ScannedRootSAXException();
    }

    public static QName getQName(File file)
    {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(QNameChecker.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        QName qn = getQName(fin);
        try {
            fin.close();
        } catch (IOException ex) {
//            Logger.getLogger(QNameChecker.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return qn;
    }

    public static QName getQName(URL url) throws IOException
    {
        InputStream is = url.openStream();
        QName qn = getQName(is);
        is.close();
        return qn;
    }

    public static QName getQName(InputStream is)
    {
		//get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);

        QNameChecker checker = new QNameChecker();
        try {
            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse(is, checker);
        }
        catch (ScannedRootSAXException ex) 
        {
            //This is a valid state.  Return the URI we found.
            return checker.qname;
        } 
        catch (SAXException ex) 
        {
//            Logger.getLogger(QNameChecker.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) 
        {
//            Logger.getLogger(QNameChecker.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (ParserConfigurationException ex) 
        {
//            Logger.getLogger(QNameChecker.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
}
