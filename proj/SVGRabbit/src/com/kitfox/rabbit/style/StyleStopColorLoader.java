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
import com.kitfox.rabbit.parser.attribute.AttributeParser;
import com.kitfox.rabbit.parser.attribute.ParseException;
import com.kitfox.raven.util.service.ServiceInst;
import java.awt.Color;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
@ServiceInst(service=StyleElementLoader.class)
public class StyleStopColorLoader extends StyleElementLoader
{

    public StyleStopColorLoader()
    {
        super("stop-color", StyleKey.STOP_COLOR);
    }


    @Override
    public Color parse(String value, RabbitDocument builder)
    {
        AttributeParser parser = new AttributeParser(new StringReader(value));
        try {
            return parser.Color();
        } catch (ParseException ex) {
            Logger.getLogger(StyleStopColorLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
