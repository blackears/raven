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
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class GlyphMap
{
    HashMap<Character, GlyphMap> map;
    Glyph glyph;  //If non-null, a glyph is defined for this path

    public GlyphMap()
    {
    }

    public GlyphMap(Glyph glyph)
    {
        this.glyph = glyph;
    }

    public void addGlyph(String code, int index, Glyph glyph)
    {
        if (index == code.length())
        {
            this.glyph = glyph;
            return;
        }

        if (map == null)
        {
            map = new HashMap<Character, GlyphMap>();
        }

        char ch = code.charAt(index);
        GlyphMap gm = map.get(ch);
        if (gm == null)
        {
            gm = new GlyphMap();
            map.put(ch, gm);
        }
        gm.addGlyph(code, index + 1, glyph);
    }

    int parseGlyph(String text, int i, ArrayList<Glyph> glyphList)
    {
        if (map == null)
        {
            //This is a terminal.  Return the glyph here (all terminals have glyphs)
            glyphList.add(glyph);
            return 0;
        }

        char ch = text.charAt(i);
        GlyphMap gm = map.get(ch);
        if (gm == null)
        {
            //cannot continue
            if (glyph == null)
            {
                return -1;
            }
            else
            {
                glyphList.add(glyph);
                return 0;
            }
        }
        else
        {
            int skip = gm.parseGlyph(text, i + 1, glyphList);
            if (skip == -1)
            {
                if (glyph == null)
                {
                    return -1;
                }
                else
                {
                    glyphList.add(glyph);
                    return 0;
                }
            }
            else
            {
                return skip + 1;
            }
        }
    }

    
}
