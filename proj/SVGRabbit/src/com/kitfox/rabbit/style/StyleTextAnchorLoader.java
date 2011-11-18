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

import com.kitfox.rabbit.parser.RabbitDocument;
import com.kitfox.rabbit.text.TextAnchor;
import com.kitfox.raven.util.service.ServiceInst;

/**
 *
 * @author kitfox
 */
@ServiceInst(service=StyleElementLoader.class)
public class StyleTextAnchorLoader extends StyleElementLoader
{

    public StyleTextAnchorLoader()
    {
        super("text-anchor", StyleKey.TEXT_ANCHOR);
    }


    @Override
    public TextAnchor parse(String value, RabbitDocument builder)
    {
        if ("start".equals(value))
        {
            return TextAnchor.START;
        }
        if ("middle".equals(value))
        {
            return TextAnchor.MIDDLE;
        }
        if ("end".equals(value))
        {
            return TextAnchor.END;
        }
        return null;
    }

}
