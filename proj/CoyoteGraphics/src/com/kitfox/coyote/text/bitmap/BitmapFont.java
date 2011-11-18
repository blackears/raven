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

package com.kitfox.coyote.text.bitmap;

import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyRendererUtil2D;
import com.kitfox.coyote.renderer.CyTextureSource;

/**
 *
 * @author kitfox
 */
public class BitmapFont
{
    private final GlyphSymbolTable symbols;
    private final CyTextureSource glyphPlate;
    private final CyTextureSource glyphShadowPlate;
    private final int lineHeight;
    private final int lineAscent;

    public BitmapFont(GlyphSymbolTable symbols, CyTextureSource glyphPlate,
            int lineHeight, int lineAscent)
    {
        this(symbols, glyphPlate, null, lineHeight, lineAscent);
    }

    public BitmapFont(GlyphSymbolTable symbols,
            CyTextureSource glyphPlate, CyTextureSource glyphShadowPlate,
            int lineHeight, int lineAscent)
    {
        this.symbols = symbols;
        this.glyphPlate = glyphPlate;
        this.glyphShadowPlate = glyphShadowPlate;
        this.lineHeight = lineHeight;
        this.lineAscent = lineAscent;
    }

    public int getSpaceWidth()
    {
        GlyphDef glyph = symbols.getGlyph(' ');
        return glyph == null ? symbols.getLineAdvanceY() / 2 : glyph.getAdvanceX();
    }

    /**
     * @return the symbols
     */
    public GlyphSymbolTable getSymbols()
    {
        return symbols;
    }

    public void renderGlyph(CyDrawStack rend, GlyphDef glyph, int x, int y)
    {
        int sx = glyph.getX();
        int sy = glyph.getY();
        int sw = glyph.getWidth();
        int sh = glyph.getHeight();
        int dx = x - glyph.getOriginX() + glyph.getX();
        int dy = y - glyph.getOriginY() + glyph.getY();
        int dw = glyph.getWidth();
        int dh = glyph.getHeight();

        CyRendererUtil2D.inst().drawImage(rend, glyphPlate,
                sx, sy, sw, sh,
                dx, dy, dw, dh);
    }

    public void renderGlyphShadow(CyDrawStack rend, GlyphDef glyph, int x, int y)
    {
        if (glyphShadowPlate == null)
        {
            return;
        }

        int sx = glyph.getX();
        int sy = glyph.getY();
        int sw = glyph.getWidth();
        int sh = glyph.getHeight();
        int dx = x - glyph.getOriginX() + glyph.getX();
        int dy = y - glyph.getOriginY() + glyph.getY();
        int dw = glyph.getWidth();
        int dh = glyph.getHeight();

        CyRendererUtil2D.inst().drawImage(rend, glyphShadowPlate,
                sx, sy, sw, sh,
                dx, dy, dw, dh);
    }

    /**
     * @return the glyphs
     */
    public CyTextureSource getGlyphs()
    {
        return glyphPlate;
    }

    /**
     * @return the lineHeight
     */
    public int getLineHeight()
    {
        return lineHeight;
    }

    /**
     * @return the lineAscent
     */
    public int getLineAscent()
    {
        return lineAscent;
    }
//    /**
//     * @return the outlines
//     */
//    public ImageSource getOutlines() {
//        return outlines;
//    }
}
