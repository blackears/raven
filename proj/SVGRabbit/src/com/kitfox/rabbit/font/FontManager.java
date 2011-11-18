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
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class FontManager
{
    HashMap<String, FontShape> fontFamilyMap = new HashMap<String, FontShape>();

    public FontShape getFont(String fontName, FontStyle style, FontVariant variant, FontWeight weight)
    {
        return fontFamilyMap.get(fontName);
    }

    public void addFont(FontShape font)
    {
        fontFamilyMap.put(font.getFontFace().getFontFamily(), font);
    }

    public void loadSystemFont(String fontName)
    {
        Font font = new Font(fontName, Font.PLAIN, 1024);

        SystemFontBuilder builder = new SystemFontBuilder(font);
        addFont(builder.build());
    }

//    public FontShape getFont(String[] fontFamily, FontStyle fontStyle, FontVariant fontVariant, FontWeight fontWeight)
//    {
//
//    }
}
