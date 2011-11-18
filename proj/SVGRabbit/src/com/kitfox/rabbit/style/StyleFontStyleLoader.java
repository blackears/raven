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

package com.kitfox.rabbit.style;

import com.kitfox.rabbit.font.FontStyle;
import com.kitfox.rabbit.parser.RabbitDocument;
import com.kitfox.raven.util.service.ServiceInst;

/**
 *
 * @author kitfox
 */
@ServiceInst(service=StyleElementLoader.class)
public class StyleFontStyleLoader extends StyleElementLoader
{

    public StyleFontStyleLoader()
    {
        super("font-style", StyleKey.FONT_STYLE);
    }


    @Override
    public FontStyle parse(String value, RabbitDocument builder)
    {
        if ("normal".equals(value))
        {
            return FontStyle.NORMAL;
        }
        if ("italic".equals(value))
        {
            return FontStyle.ITALIC;
        }
        if ("oblique".equals(value))
        {
            return FontStyle.OBLIQUE;
        }
        return null;
    }

}
