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

import com.kitfox.rabbit.types.ElementRef;
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
public class StyleStrokeLoader extends StyleElementLoader
{

    public StyleStrokeLoader()
    {
        super("stroke", StyleKey.STROKE);
    }


    @Override
    public StylePaint parse(String value, RabbitDocument builder)
    {
        if ("none".equals(value))
        {
            return null;
        }

        AttributeParser parser = new AttributeParser(new StringReader(value));
        try {
            Object paint = parser.Paint();

            if (paint instanceof String)
            {
                ElementRef ref = builder.getElementRef((String)paint);
                return new StyleGradient(ref);
            }
            else if (paint instanceof Color)
            {
                return new StyleColor((Color)paint);
            }

        } catch (ParseException ex) {
            Logger.getLogger(StyleStrokeLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
