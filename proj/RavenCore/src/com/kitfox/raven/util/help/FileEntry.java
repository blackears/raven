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

package com.kitfox.raven.util.help;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author kitfox
 */
public class FileEntry extends HelpEntry
{
    Template ftlTemplate;

    public FileEntry(DirEntry parent, File src, File dest, String title)
    {
        super(parent, src, dest, title);

        //Prepare freemarker
        final Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(getClass(), "");
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        try {
            ftlTemplate = cfg.getTemplate("HelpTemplatePage.ftl");
        } catch (IOException ex) {
            Logger.getLogger(HelpBuilderAnt.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
    }

    private Element getChild(Element node, String tag)
    {
        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
        {
            if (!(child instanceof Element))
            {
                continue;
            }

            Element ele = (Element)child;
            if (tag.equalsIgnoreCase(ele.getTagName()))
            {
                return ele;
            }
        }
        return null;
    }

    public String getBody()
    {
        try
        {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document docSrc = dBuilder.parse(getSrc());

            Document docDiv = dBuilder.newDocument();

            Element node = docSrc.getDocumentElement();
//            Element htmlTag = getChild(node, "html");
            Element bodyTag = getChild(node, "body");
//            Element pTag = getChild(bodyTag, "p");

            Element eleDiv = docDiv.createElement("div");
            eleDiv.setAttribute("class", "main");
            docDiv.appendChild(eleDiv);
            for (Node child = bodyTag.getFirstChild(); child != null; child = child.getNextSibling())
            {
                Node copy = docDiv.importNode(child, true);
                eleDiv.appendChild(copy);
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer xform = tf.newTransformer();
            xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            StringWriter sw = new StringWriter();
            StreamResult sr = new StreamResult(sw);
            DOMSource source = new DOMSource(docDiv);
            xform.transform(source, sr);

            return sw.toString();
        }
        catch (SAXException ex)
        {
            Logger.getLogger(FileEntry.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(FileEntry.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex)
        {
            Logger.getLogger(FileEntry.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex)
        {
            Logger.getLogger(FileEntry.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    @Override
    protected void generate()
    {
        try {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("date", new Date());
            map.put("author", "kitfox");
            map.put("page", this);
//            map.put("navBar", buildNavBar(null));

            File parentDir = getDest().getParentFile();
            if (!parentDir.exists())
            {
                parentDir.mkdirs();
            }
            FileWriter w = new FileWriter(getDest());
            ftlTemplate.process(map, w);
            w.close();
        } catch (TemplateException ex)
        {
            Logger.getLogger(HelpBuilderAnt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(HelpBuilderAnt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
