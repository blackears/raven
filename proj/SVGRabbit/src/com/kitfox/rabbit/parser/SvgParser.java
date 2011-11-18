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

import com.kitfox.rabbit.nodes.RaElement;
import com.kitfox.rabbit.nodes.RaElementIndex;
import com.kitfox.rabbit.nodes.RaElementLoader;
import com.kitfox.rabbit.nodes.RaString;
import com.kitfox.rabbit.nodes.RaSvg;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Pattern;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author kitfox
 */
public class SvgParser extends DefaultHandler
{
    class Frame
    {
        RaElementLoader loader;
        HashMap<String, String> attrMap = new HashMap<String, String>();
        ArrayList<RaElement> nodes = new ArrayList<RaElement>();

        public Frame()
        {
        }

        public Frame(RaElementLoader loader, Attributes attributes)
        {
            this.loader = loader;

            for (int i = 0; i < attributes.getLength(); ++i)
            {
                String key = attributes.getLocalName(i);
                String value = attributes.getValue(i);
                attrMap.put(key, value);
            }
//            this.attributes = attributes;
        }

        public String getAttr(String name)
        {
            return attrMap.get(name);
        }
    }

    LinkedList<Frame> stack = new LinkedList<Frame>();
    final RabbitDocument builder;
    final Pattern ws = Pattern.compile("\\s+");

    public SvgParser(RabbitDocument builder)
    {
        this.builder = builder;
        stack.add(new Frame());
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        RaElementLoader loader = RaElementIndex.inst().getLoader(new QName(uri, localName));

//System.err.println("Start ele: " + qName);
if (loader == null)
{
    System.err.println("*****No loader for: " + qName);
}
        stack.add(new Frame(loader, attributes));
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        Frame frame = stack.removeLast();
        if (frame.loader == null)
        {
            //Don't know how to handle
            return;
        }

//System.err.println("End ele: " + qName);
//if ("filter".equals(qName))
//{
//    int j = 9;
//}

        RaElement node = frame.loader.create(builder, frame.attrMap, frame.nodes);
        stack.getLast().nodes.add(node);
        
        String id = frame.getAttr("id");
//        builder.registerNode(id.replace('-', '_'), node);
        builder.registerNode(id, node);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        String text = new String(ch, start, length);
        if (ws.matcher(text).matches())
        {
            //Skip whitespace
            return;
        }
        stack.getLast().nodes.add(new RaString(text));

    }

    @Override
    public void endDocument() throws SAXException
    {
        builder.setRootNode((RaSvg)stack.getLast().nodes.get(0));
        builder.fillInMissingNames("node");
    }


}
