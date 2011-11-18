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

import com.kitfox.coyote.text.TextFormatter;
import com.kitfox.coyote.text.TextFormatter.GlyphInfo;

/**
 *
 * @author kitfox
 */
public class TextFormatterBitmapFont extends TextFormatter<GlyphDef>
{
    BitmapFont font;

    public TextFormatterBitmapFont(BitmapFont font)
    {
        this.font = font;
    }

    @Override
    protected GlyphInfo<GlyphDef> createGlyph(char ch)
    {
        GlyphDef gd = font.getSymbols().getGlyph(ch);
        return new GlyphInfo<GlyphDef>(ch, gd, gd.getAdvanceX());
    }

    @Override
    public int getLineHeight()
    {
        return font.getLineHeight();
    }

    @Override
    public int getLineAscent()
    {
        return font.getLineAscent();
    }

}
