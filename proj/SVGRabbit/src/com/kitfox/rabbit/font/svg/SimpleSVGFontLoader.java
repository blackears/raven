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

package com.kitfox.rabbit.font.svg;

import com.kitfox.rabbit.ant.SVGFontTask;
import com.kitfox.rabbit.font.FontFace;
import com.kitfox.rabbit.font.FontShape;
import com.kitfox.rabbit.font.Glyph;
import com.kitfox.rabbit.parser.path.ParseException;
import com.kitfox.rabbit.parser.path.PathParser;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A simplified parser that loads a single SVG font.  Meant to be lighter weight
 * that the full SVG parser.
 *
 * @author kitfox
 */
public class SimpleSVGFontLoader extends DefaultHandler
{
    Glyph missingGlyph;
    HashMap<String, Glyph> glyphs = new HashMap<String, Glyph>();
    FontFace fontFace;
    private FontShape font;
    float horizAdvX;
    String fontName;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        if ("font".equals(localName))
        {
            fontName = attributes.getValue("id");
            horizAdvX = Float.parseFloat(attributes.getValue("horiz-adv-x"));
        }
        else if("font-face".equals(localName))
        {
            fontFace = new FontFace();
            fontFace.setFontFamily(attributes.getValue("font-family"));
            fontFace.setAscent(Integer.parseInt(attributes.getValue("ascent")));
            fontFace.setDescent(Integer.parseInt(attributes.getValue("descent")));
            fontFace.setUnitsPerEm(Integer.parseInt(attributes.getValue("units-per-em")));
        }
        else if("missing-glyph".equals(localName))
        {
            missingGlyph = parseGlyph(attributes);
        }
        else if("glyph".equals(localName))
        {
            String unicode = attributes.getValue("unicode");
            glyphs.put(unescapeXML(unicode), parseGlyph(attributes));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        if ("font".equals(localName))
        {
            font = new FontShape(qName, fontFace, horizAdvX, missingGlyph);
            for (String code: glyphs.keySet())
            {
                font.addGlyph(code, glyphs.get(code));
            }
        }

    }

    private Glyph parseGlyph(Attributes attributes)
    {
        float horizAdv = Float.parseFloat(attributes.getValue("horiz-adv-x"));
        float vertAdv = Float.parseFloat(attributes.getValue("vert-adv-y"));

        StringReader reader = new StringReader(attributes.getValue("d"));
        PathParser parser = new PathParser(reader);
        Path2D.Double path = null;
        try {
            path = parser.Path();
        } catch (ParseException ex) {
            Logger.getLogger(SimpleSVGFontLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

//        AffineTransform at = new AffineTransform();
//        at.scale(1, -1);
//        path = new Path2D.Double(at.createTransformedShape(path));

        return new Glyph(path, horizAdv, vertAdv);
    }

    private String unescapeXML(String unicode)
    {
        StringBuilder sb = new StringBuilder();
        StringBuilder entity = null;
        for (int i = 0; i < unicode.length(); ++i)
        {
            char ch = unicode.charAt(i);

            if (entity == null)
            {
                if (ch == '&')
                {
                    entity = new StringBuilder();
                }
                else
                {
                    sb.append(ch);
                }
            }
            else
            {
                if (ch == ';')
                {
                    sb.append(parseEntity(entity.toString()));
                    entity = null;
                }
                else
                {
                    entity.append(ch);
                }
            }
        }
        return sb.toString();
    }

    private char parseEntity(String text)
    {
        if (text.startsWith("#x"))
        {
            return (char)Integer.parseInt(text.substring(2), 16);
        }

        if ("quot".equals(text))
        {
            return '"';
        }
        if ("amp".equals(text))
        {
            return '&';
        }
        if ("apos".equals(text))
        {
            return '\'';
        }
        if ("lt".equals(text))
        {
            return '<';
        }
        if ("gt".equals(text))
        {
            return '>';
        }

        return ' ';
    }

    /**
     * @return the font
     */
    public FontShape getFont()
    {
        return font;
    }

    public static FontShape loadFont(File file)
    {
        FontShape font = null;
        try {
            FileInputStream fin = new FileInputStream(file);
            font = loadFont(fin);
            fin.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SimpleSVGFontLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SimpleSVGFontLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return font;
    }

    public static FontShape loadFont(InputStream is)
    {
        SimpleSVGFontLoader parser = new SimpleSVGFontLoader();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(is, parser);

            return parser.getFont();
        } catch (IOException ex) {
            Logger.getLogger(SVGFontTask.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(SVGFontTask.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(SVGFontTask.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
