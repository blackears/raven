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

/**
 *
 * @author kitfox
 */
public class GlyphSignature implements Comparable<GlyphSignature>
{

    private final char character;
    private final GlyphDef glyph;

    public GlyphSignature(char character, GlyphDef glyph)
    {
        this.character = character;
        this.glyph = glyph;
    }

    public String getCharacterString()
    {
        if (character == '\'')
        {
            return "'\\''";
        }
        if (character == '\\')
        {
            return "'\\\\'";
        }
        if (character >= ' ' && character <= '~')
        {
            return "'" + character + "'";
        }
//        String ch = Integer.toHexString(character);
        return String.format("'\\u%04x'", (int) character);
    }

    /**
     * @return the character
     */
    public char getCharacter()
    {
        return character;
    }

    /**
     * @return the glyph
     */
    public GlyphDef getGlyph()
    {
        return glyph;
    }

    @Override
    public int compareTo(GlyphSignature o)
    {
        return character - o.character;
    }
}
