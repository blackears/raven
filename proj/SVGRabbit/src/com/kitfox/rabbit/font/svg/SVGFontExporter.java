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

import com.kitfox.rabbit.codegen.ShapeToSVGUtil;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class SVGFontExporter
{
    class Metrics extends FontMetrics
    {
        public Metrics(Font font)
        {
            super(font);
        }
    }
    
    //ArrayList<Font> fonts = new ArrayList<Font>();
    Font font;
    ArrayList<Character> charList;

    public SVGFontExporter(File source, ArrayList<Character> charList)
    {
        this(loadFont(source), charList);
    }

    public SVGFontExporter(Font font, ArrayList<Character> charList)
    {
        this.font = font.deriveFont(1024f);
        this.charList = charList;
    }

    private static Font loadFont(File source)
    {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, source);
        } catch (FontFormatException ex) {
            Logger.getLogger(SVGFontExporter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SVGFontExporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void exportSVG(File file)
    {
        try {
            FileWriter fw = new FileWriter(file);
            exportSVG(fw);
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(SVGFontExporter.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
    }

    private void exportSVG(Writer writer)
    {
        PrintWriter pw = new PrintWriter(writer);
        exportSVG(pw);
    }

    private void exportSVG(PrintWriter pw)
    {
        Metrics metrics = new Metrics(font);

//        Map<TextAttribute, ?> attrMap = font.getAttributes();
//        AttributedCharacterIterator.Attribute[] attrList = font.getAvailableAttributes();
        pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
        pw.println("<svg xmlns=\"http://www.w3.org/2000/svg\">");
        pw.println(String.format("    <metadata>SVGFontExporter %s</metadata>", new Date().toString()));
        pw.println(String.format("    <font id=\"%s\" horiz-adv-x=\"%d\">", font.getFontName(), metrics.getMaxAdvance()));
        pw.println(String.format("        <font-face font-family=\"%s\" units-per-em=\"1024\" ascent=\"%d\" descent=\"%d\"/>",
                font.getFamily(),
                metrics.getAscent(),
                metrics.getDescent()));

        AffineTransform at = new AffineTransform();
        at.scale(1, -1);
        FontRenderContext frc = new FontRenderContext(at, false, false);
        {
            int missingCode = font.getMissingGlyphCode();
            GlyphVector v = font.createGlyphVector(frc, new int[]{missingCode});

            Shape s = v.getGlyphOutline(0);
            String pathStrn = ShapeToSVGUtil.toPathString(s);
            GlyphMetrics gm = v.getGlyphMetrics(0);
            pw.println(String.format("        <missing-glyph horiz-adv-x=\"%d\" vert-adv-y=\"%d\" d=\"%s\"/>",
                    (int)gm.getAdvanceX(), (int)gm.getAdvanceY(), pathStrn));

        }

        for (char ch: charList)
        {
            if (!font.canDisplay(ch) || !validXMLChar(ch))
            {
                continue;
            }

            GlyphVector v = font.createGlyphVector(frc, "" + ch);

            int code = v.getGlyphCode(0);
            if (code <= 0)
            {
                //Extra test needed for Macintosh
                continue;
            }

            Shape s = v.getGlyphOutline(0);
            String pathStrn = ShapeToSVGUtil.toPathString(s);

            GlyphMetrics gm = v.getGlyphMetrics(0);
            pw.println(String.format("        <glyph unicode=\"%s\" horiz-adv-x=\"%d\" vert-adv-y=\"%d\" d=\"%s\"/>",
                    toCharEntity(ch), (int)gm.getAdvanceX(), (int)gm.getAdvanceY(), pathStrn));
        }

        pw.println("    </font>");
        pw.println("</svg>");
    }

    private boolean validXMLChar(char ch)
    {
        return ch == '\t' || ch == '\n' || ch == '\r'
                || (ch >= ' ' && ch <= '\ud7ff')
                || (ch >= '\ue000' && ch <= '\ufffd');
    }

    private String toCharEntity(char ch)
    {
        switch (ch)
        {
            case '"':
                return "&quot;";
            case '&':
                return "&amp;";
            case '\'':
                return "&apos;";
            case '<':
                return "&lt;";
            case '>':
                return "&gt;";
        }

        if (ch >= ' ' && ch <= '~')
        {
            return "" + ch;
        }
        return String.format("&#x%04x;", (int)ch);
    }



}
