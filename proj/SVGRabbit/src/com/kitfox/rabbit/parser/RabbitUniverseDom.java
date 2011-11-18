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

package com.kitfox.rabbit.parser;

import com.kitfox.rabbit.font.FontShape;
import com.kitfox.rabbit.nodes.RaElement;
import com.kitfox.rabbit.render.RabbitUniverse;
import com.kitfox.rabbit.types.ElementRef;
import com.kitfox.rabbit.types.ImageRef;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author kitfox
 */
public class RabbitUniverseDom extends RabbitUniverse
{
    class DocInfo
    {
        final URL url;
        final int id;
        final RabbitDocument doc;

        public DocInfo(URL url, int id, RabbitDocument doc)
        {
            this.url = url;
            this.id = id;
            this.doc = doc;
        }
    }

//    HashMap<URL, HareDocBuilder> docMap = new HashMap<URL, HareDocBuilder>();
    HashMap<URL, DocInfo> docMapUrl = new HashMap<URL, DocInfo>();
    HashMap<Integer, DocInfo> docMapId = new HashMap<Integer, DocInfo>();

    HashMap<URL, ImageRef> imageRefServer = new HashMap<URL, ImageRef>();
    HashMap<String, Integer> classIdServer = new HashMap<String, Integer>();


    public RabbitUniverseDom()
    {
    }

    @Override
    public RaElement lookupElement(ElementRef ref)
    {
        DocInfo info = docMapId.get(ref.getDocumentIndex());
        if (info == null)
        {
            return null;
        }
        return info.doc.lookupElement(ref.getElementIndex());
    }

    public int getClassId(String className)
    {
        Integer val = classIdServer.get(className);
        if (val == null)
        {
            val = classIdServer.size();
            classIdServer.put(className, val);
        }
        return val;
    }

    public ArrayList<URL> getDocumentUrls()
    {
        return new ArrayList<URL>(docMapUrl.keySet());
    }

    public ArrayList<URL> getImageUrls()
    {
        return new ArrayList<URL>(imageRefServer.keySet());
    }

    public ImageRef getImageRef(URL url)
    {
        ImageRef val = imageRefServer.get(url);
        if (val == null)
        {
            val = new ImageRef(imageRefServer.size());
            imageRefServer.put(url, val);
        }
        return val;
    }

    public RabbitDocument getDocument(URL url)
    {
        DocInfo info = docMapUrl.get(url);
        if (info == null)
        {
//System.err.println("Building " + url);
            RabbitDocument doc = new RabbitDocument(url, docMapUrl.size(), this);
            info = new DocInfo(url, doc.getDocumentId(), doc);
            docMapUrl.put(url, info);
            docMapId.put(doc.getDocumentId(), info);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            try {
                SAXParser saxParser = factory.newSAXParser();
                SvgParser parser = new SvgParser(doc);
                saxParser.parse(url.openStream(), parser);

                //parser.getRootNode();
            } catch (IOException ex) {
                Logger.getLogger(RabbitUniverseDom.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(RabbitUniverseDom.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(RabbitUniverseDom.class.getName()).log(Level.SEVERE, null, ex);
            }

//            Document doc = loadDoc(url);
//            builder.build();
        }

        return info.doc;
    }


    protected Document loadDoc(URL url)
    {
        DocumentBuilderFactory builderFactory =
                DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            return builder.parse(url.openStream());
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(RabbitUniverseDom.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(RabbitUniverseDom.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RabbitUniverseDom.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

}
