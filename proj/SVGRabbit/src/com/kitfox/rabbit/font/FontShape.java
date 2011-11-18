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

import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class FontShape
{
//    float horizOriginX;
//    float horizOriginY;
    private final FontFace fontFace;
    private final String name;
    private final float horizAdvX;
//    float vertOriginX;
//    float vertOriginY;
//    float vertAdvY;

    final GlyphMap glyphs;

    public FontShape(String name, FontFace fontFace, float horizAdvX, Glyph missingGlyph)
    {
        this.fontFace = fontFace;
        this.name = name;
        this.horizAdvX = horizAdvX;
        glyphs = new GlyphMap(missingGlyph);
    }

    public void addGlyph(String code, Glyph glyph)
    {
        glyphs.addGlyph(code, 0, glyph);
    }

    public ArrayList<Glyph> toGlyphs(String text)
    {
        ArrayList<Glyph> glyphList = new ArrayList<Glyph>();
        for (int i = 0; i < text.length();)
        {
            int skip = glyphs.parseGlyph(text, i, glyphList);
            i += Math.max(skip, 1);
        }
        return glyphList;
    }

    /**
     * @return the fontFace
     */
    public FontFace getFontFace() {
        return fontFace;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the horixAdvX
     */
    public float getHorixAdvX() {
        return horizAdvX;
    }
}
