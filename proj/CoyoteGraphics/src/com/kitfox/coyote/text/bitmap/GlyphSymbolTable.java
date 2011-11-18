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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author kitfox
 */
public class GlyphSymbolTable
{
    private final int lineAdvanceY;  //How far down to start writing glyphs
    private final int baselineY;
    HashMap<Character, GlyphDef> glyphs;

    public GlyphSymbolTable(HashMap<Character, GlyphDef> glyphs, int lineAdvanceY, int baselineY)
    {
        this.glyphs = glyphs;
        this.lineAdvanceY = lineAdvanceY;
        this.baselineY = baselineY;
    }

    public ArrayList<Character> getCodes()
    {
        ArrayList<Character> list = new ArrayList<Character>();

        getCodes(list);

        return list;
    }

    public void getCodes(List<Character> list)
    {
        list.addAll(glyphs.keySet());
    }

    public ArrayList<GlyphDef> getGlyphs()
    {
        ArrayList<GlyphDef> list = new ArrayList<GlyphDef>();
        list.addAll(glyphs.values());
        return list;
    }

    public void getGlyphs(List<GlyphDef> list)
    {
        list.addAll(glyphs.values());
    }

    public GlyphDef getGlyph(char code)
    {
        return glyphs.get(code);
    }

    public GlyphDef getGlyph(Character code)
    {
        return glyphs.get(code);
    }

    /**
     * @return the lineAdvanceY
     */
    public int getLineAdvanceY()
    {
        return lineAdvanceY;
    }

    /**
     * @return the baselineY
     */
    public int getBaselineY()
    {
        return baselineY;
    }

    public ArrayList<GlyphSignature> getSignatures()
    {
        ArrayList<GlyphSignature> list = new ArrayList<GlyphSignature>();

        for (Character ch : glyphs.keySet())
        {
            GlyphDef glyph = glyphs.get(ch);
            list.add(new GlyphSignature(ch, glyph));
        }

        Collections.sort(list);
        return list;
    }
}
