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

package com.kitfox.rabbit.font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class SystemFontBuilder
{
    class Metrics extends FontMetrics
    {
        public Metrics(Font font)
        {
            super(font);
        }
    }

    final Font font;

    public SystemFontBuilder(File trueTypeFont)
    {
        Font loadFont = null;
        try {
            loadFont = Font.createFont(Font.TRUETYPE_FONT, trueTypeFont);
        } catch (FontFormatException ex) {
            Logger.getLogger(SystemFontBuilder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SystemFontBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }

        loadFont = loadFont.deriveFont(1000f);
        font = loadFont;
    }

    public SystemFontBuilder(Font font)
    {
        this.font = font;
    }


    public FontShape build()
    {
        Metrics metrics = new Metrics(font);

        FontFace fontFace = new FontFace();

//        fontFace.setFontFamily(font.getFontName());
        fontFace.setFontFamily(font.getFamily());
        fontFace.setAscent(metrics.getAscent());
        fontFace.setDescent(metrics.getDescent());
        fontFace.setUnitsPerEm(font.getSize());

        AffineTransform at = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(at, false, false);

        //Calc missing glyph
        Glyph missingGlyph = null;
        {
            int missingCode = font.getMissingGlyphCode();
            GlyphVector v = font.createGlyphVector(frc, new int[]{missingCode});

            Shape s = v.getGlyphOutline(0);
            GlyphMetrics gm = v.getGlyphMetrics(0);

            missingGlyph = new Glyph(s, gm.getAdvanceX(), gm.getAdvanceY());
        }

        FontShape fontShape = new FontShape(font.getFontName(), 
                fontFace,
                metrics.getMaxAdvance(),
                missingGlyph);

        processChar('\t', fontShape, frc);
        processChar('\n', fontShape, frc);
        processChar('\r', fontShape, frc);

        for (char ch = ' '; ch <= '\ud7ff'; ++ch)
        {
            processChar(ch, fontShape, frc);
        }

        for (char ch = '\ue000'; ch <= '\ufffd'; ++ch)
        {
            processChar(ch, fontShape, frc);
        }

        return fontShape;
    }


    private void processChar(char ch, FontShape fontShape, FontRenderContext frc)
    {
        if (!font.canDisplay(ch))
        {
            return;
        }

        GlyphVector v = font.createGlyphVector(frc, "" + ch);

        int code = v.getGlyphCode(0);
        if (code <= 0)
        {
            //Extra test needed for Macintosh
            return;
        }

        Shape s = v.getGlyphOutline(0);

        GlyphMetrics gm = v.getGlyphMetrics(0);

        Glyph g = new Glyph(s, gm.getAdvanceX(), gm.getAdvanceY());
        fontShape.addGlyph("" + ch, g);
    }

}
