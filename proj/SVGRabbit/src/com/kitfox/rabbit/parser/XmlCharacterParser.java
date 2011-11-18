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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author kitfox
 */
public class XmlCharacterParser extends DefaultHandler
{
    HashSet<Character> charSet = new HashSet<Character>();

    public XmlCharacterParser()
    {
    }

    @Override
    public void characters(char[] chars, int start, int length) throws SAXException
    {
        addCharacters(new String(chars, start, length));
    }

    public void addCharacters(String text)
    {
        for (int i = 0; i < text.length(); ++i)
        {
            char ch = text.charAt(i);
            if (ch != ' ' && Character.isWhitespace(ch))
            {
                continue;
            }
            charSet.add(ch);
        }
    }

    public ArrayList<Character> getCharacters()
    {
        ArrayList<Character> list = new ArrayList<Character>(charSet);
        Collections.sort(list);
        return list;
    }

}
