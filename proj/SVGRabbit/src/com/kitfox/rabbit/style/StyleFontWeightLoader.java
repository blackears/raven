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

import com.kitfox.rabbit.font.FontWeight;
import com.kitfox.rabbit.parser.RabbitDocument;
import com.kitfox.raven.util.service.ServiceInst;

/**
 *
 * @author kitfox
 */
@ServiceInst(service=StyleElementLoader.class)
public class StyleFontWeightLoader extends StyleElementLoader
{

    public StyleFontWeightLoader()
    {
        super("font-weight", StyleKey.FONT_WEIGHT);
    }


    @Override
    public FontWeight parse(String value, RabbitDocument builder)
    {
        if ("normal".equals(value))
        {
            return FontWeight.NORMAL;
        }
        if ("bold".equals(value))
        {
            return FontWeight.BOLD;
        }
        if ("bolder".equals(value))
        {
            return FontWeight.BOLDER;
        }
        if ("lighter".equals(value))
        {
            return FontWeight.LIGHTER;
        }
        if ("100".equals(value))
        {
            return FontWeight._100;
        }
        if ("200".equals(value))
        {
            return FontWeight._200;
        }
        if ("300".equals(value))
        {
            return FontWeight._300;
        }
        if ("400".equals(value))
        {
            return FontWeight._400;
        }
        if ("500".equals(value))
        {
            return FontWeight._500;
        }
        if ("600".equals(value))
        {
            return FontWeight._600;
        }
        if ("700".equals(value))
        {
            return FontWeight._700;
        }
        if ("800".equals(value))
        {
            return FontWeight._800;
        }
        if ("900".equals(value))
        {
            return FontWeight._900;
        }
        return null;
    }

}
