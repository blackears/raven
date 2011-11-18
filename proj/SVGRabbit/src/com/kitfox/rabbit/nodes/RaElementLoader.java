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

package com.kitfox.rabbit.nodes;

import com.kitfox.rabbit.style.Style;
import com.kitfox.rabbit.style.StyleElementLoader;
import com.kitfox.rabbit.style.StyleIndex;
import com.kitfox.rabbit.types.ElementRef;
import com.kitfox.rabbit.parser.RabbitDocument;
import com.kitfox.rabbit.parser.attribute.AttributeParser;
import com.kitfox.rabbit.parser.attribute.ParseException;
import com.kitfox.rabbit.parser.path.PathParser;
import com.kitfox.rabbit.types.ImageRef;
import com.kitfox.rabbit.types.RaLength;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

/**
 *
 * @author kitfox
 */
abstract public class RaElementLoader<T extends RaElement>
{
    private final QName qname;

    public RaElementLoader(String tag)
    {
        this(tag, "http://www.w3.org/2000/svg");
    }

    public RaElementLoader(String tag, String namespace)
    {
        this.qname = new QName(namespace, tag);
    }

    /**
     * @return the qname
     */
    public QName getQname() {
        return qname;
    }

//    abstract public T create(Node node, HareDocBuilder builder);

    abstract public T create(RabbitDocument builder, HashMap<String, String> attributes, ArrayList<RaElement> nodes);

    protected int[] parseClasses(String text, RabbitDocument builder)
    {
        if (text == null)
        {
            return null;
        }
        String[] names = text.split("\\s+");
        int[] arr = new int[names.length];
        for (int i = 0; i < arr.length; ++i)
        {
            arr[i] = builder.getUniverse().getClassId(names[i]);
        }
        return arr;
    }

    protected String[] parseStrings(String text, float defaultValue)
    {        
        if (text == null)
        {
            return new String[0];
        }
        return text.split("\\w+");
    }

    protected int parseInt(String text, int defaultValue)
    {
        if (text == null)
        {
            return defaultValue;
        }
        return Integer.parseInt(text);
    }

    protected float parseFloat(String text, float defaultValue)
    {
        if (text == null)
        {
            return defaultValue;
        }
        return Float.parseFloat(text);
    }

    protected float[] parseFloatArr(String text, float[] def)
    {
        if (text == null)
        {
            return def;
        }
        AttributeParser parser = new AttributeParser(new StringReader(text));
        try {
            float[] arr = parser.FloatArray();

            return arr;
        } catch (ParseException ex) {
            Logger.getLogger(RaElementLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return def;
    }

    protected RaLength parseLength(String text, RaLength defaultValue)
    {
        if (text == null)
        {
            return defaultValue;
        }
        AttributeParser parser = new AttributeParser(new StringReader(text));
        try {
            return parser.Length();
        } catch (ParseException ex) {
            Logger.getLogger(RaElementLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    protected int parseFilterInput(String text, RabbitDocument builder)
    {
        if (text == null)
        {
            return builder.getLastResultFilter();
        }
        return builder.getFilterId(text);
    }

    protected int parseFilterResult(String text, RabbitDocument builder)
    {
        if (text == null)
        {
            return RaFe.IMAGE_SCRATCH;
        }
        return builder.getFilterId(text);
    }

    protected ElementRef parseElementRef(String text, RabbitDocument builder)
    {
        if (text == null)
        {
            return null;
        }

        return builder.getElementRef(text);
    }
    
    protected ImageRef parseImageRef(String text, RabbitDocument builder)
    {
        if (text == null)
        {
            return null;
        }

        return builder.getImageRef(text);
    }

    protected URL parseUrl(String text, URL def)
    {
        if (text == null)
        {
            return def;
        }
        try {
            return new URL(text);
        } catch (MalformedURLException ex) {
            Logger.getLogger(RaElementLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return def;
    }

    protected Path2D.Double parsePath(String text, Path2D.Double def)
    {
        if (text == null)
        {
            return def;
        }
        PathParser parser = new PathParser(new StringReader(text));
        try {
            return parser.Path();
        } catch (com.kitfox.rabbit.parser.path.ParseException ex) {
            Logger.getLogger(RaElementLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return def;
    }

    protected AffineTransform parseTransform(String text, AffineTransform def)
    {
        if (text == null)
        {
            return def;
        }
        AttributeParser parser = new AttributeParser(new StringReader(text));
        try {
            return parser.Transform();
        } catch (ParseException ex) {
            Logger.getLogger(RaElementLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return def;
    }

    protected Rectangle2D.Float parseRectangle(String text, Rectangle2D.Float def)
    {
        if (text == null)
        {
            return def;
        }
        AttributeParser parser = new AttributeParser(new StringReader(text));
        try {
            float[] arr = parser.FloatArray();
            if (arr.length < 4)
            {
                return def;
            }

            return new Rectangle2D.Float(arr[0], arr[1], arr[2], arr[3]);
        } catch (ParseException ex) {
            Logger.getLogger(RaElementLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return def;
    }

    protected Style parseStyle(String text, RabbitDocument builder)
    {
        Style style = new Style();

        if (text == null)
        {
            return style;
        }

        String[] styles = text.split(";");
        for (String s: styles)
        {
            s = s.trim();
            if ("".equals(s))
            {
                continue;
            }

            int idx = s.indexOf(':');
            String key = s.substring(0, idx).trim();
            String val = s.substring(idx + 1).trim();

            StyleElementLoader l = StyleIndex.inst().getLoader(key);
            if (l == null)
            {
                continue;
            }

            Object value = l.parse(val, builder);
            style.put(l.getKey(), value);
        }
        
        return style;
    }


    protected GradientUnits parseGradientUnits(String text)
    {
        if ("userSpaceOnUse".equals(text))
        {
            return GradientUnits.USER_SPACE_ON_USE;
        }
        return GradientUnits.OBJECT_BOUNDING_BOX;
    }

    protected SpreadMethod parseSpreadMethod(String text)
    {
        if ("reflect".equals(text))
        {
            return SpreadMethod.REFLECT;
        }
        if ("repeat".equals(text))
        {
            return SpreadMethod.REPEAT;
        }
        return SpreadMethod.PAD;
    }

    protected ChanelSelector parseChannelSelector(String text, ChanelSelector def)
    {
        if (text == null)
        {
            return def;
        }

        if ("R".equals(text))
        {
            return ChanelSelector.R;
        }
        if ("G".equals(text))
        {
            return ChanelSelector.G;
        }
        if ("B".equals(text))
        {
            return ChanelSelector.B;
        }
        if ("A".equals(text))
        {
            return ChanelSelector.A;
        }
        return def;
    }

}
