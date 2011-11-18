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

package com.kitfox.rabbit.render;

import com.kitfox.rabbit.font.FontManager;
import com.kitfox.rabbit.font.FontShape;
import com.kitfox.rabbit.nodes.RaElement;
import com.kitfox.rabbit.font.FontStyle;
import com.kitfox.rabbit.font.FontVariant;
import com.kitfox.rabbit.font.FontWeight;
import com.kitfox.rabbit.types.ElementRef;

/**
 *
 * @author kitfox
 */
abstract public class RabbitUniverse
{
    FontManager fontManager = new FontManager();
    private String defaultFont;

    public void addFont(FontShape font)
    {
        fontManager.addFont(font);
    }

    public FontShape getFont(String fontFamily)
    {
        return getFont(fontFamily, FontStyle.NORMAL, FontVariant.NORMAL, FontWeight.NORMAL);
    }

    public FontShape getFont(String fontFamily, FontStyle style, FontVariant variant, FontWeight weight)
    {
        return fontManager.getFont(fontFamily, style, variant, weight);
    }

    abstract public RaElement lookupElement(ElementRef ref);

    public FontShape getFont(String[] fontFamily, FontStyle style, FontVariant variant, FontWeight weight)
    {
        for (String family: fontFamily)
        {
            FontShape font = fontManager.getFont(family, style, variant, weight);
            if (font != null)
            {
                return font;
            }
        }
        return fontManager.getFont(defaultFont, style, variant, weight);
    }

    /**
     * @return the defaultFont
     */
    public String getDefaultFont() {
        return defaultFont;
    }

    /**
     * @param defaultFont the defaultFont to set
     */
    public void setDefaultFont(String defaultFont) {
        this.defaultFont = defaultFont;
    }

}
