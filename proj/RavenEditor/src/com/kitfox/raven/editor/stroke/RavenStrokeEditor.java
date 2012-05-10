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

package com.kitfox.raven.editor.stroke;

import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeSymbol;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.PropertyCustomEditor;
import com.kitfox.raven.util.tree.PropertyData;
import com.kitfox.raven.util.tree.PropertyDataInline;
import com.kitfox.raven.util.tree.PropertyDataReference;
import com.kitfox.raven.util.tree.PropertyProvider;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperEditor;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

/**
 *
 * @author kitfox
 */
@Deprecated
public class RavenStrokeEditor extends PropertyWrapperEditor<RavenStroke>
{
    public RavenStrokeEditor(PropertyWrapper wrapper)
    {
        super(wrapper);
    }

    @Override
    public boolean isPaintable()
    {
        PropertyData<RavenStroke> data = getValue();
        RavenStroke val = data.getValue(getDocument());
        return val != null;
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box)
    {
        Graphics2D g = (Graphics2D)gfx;

        g.setPaint(Color.WHITE);
        g.fillRect(0, 0, box.width, box.height);
        
        PropertyData<RavenStroke> data = getValue();
        RavenStroke ravenStroke = data.getValue(getDocument());

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        ravenStroke.drawPreview(g, box);
    }

    @Override
    public String getJavaInitializationString()
    {
        PropertyData<RavenStroke> data = getValue();
        RavenStroke stroke = data.getValue(null);
        if (stroke instanceof RavenStrokeBasic)
        {
            return ((RavenStrokeBasic)stroke).toCodeGen();
        }
        return RavenStrokeNone.STROKE.toCodeGen();
    }

    @Override
    public String getAsText()
    {
        PropertyData<RavenStroke> data = getValue();
        if (data instanceof PropertyDataInline)
        {
            RavenStroke stroke = data.getValue(null);
            return stroke == null ? "inherit" : stroke.toString();
        }

        if (data instanceof PropertyDataReference)
        {
            int uid = ((PropertyDataReference)data).getUid();
            NodeSymbol doc = getWrapper().getNode().getSymbol();
            NodeObject node = doc.getNode(uid);
            return node.getName();
        }

        return "inherit";
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        RavenStrokeInline value = RavenStrokeInline.create(text);
        if (value != null)
        {
            setValue(value);
            return;
        }

        //Check for node with this name
        FindNode find = new FindNode(text);
        NodeSymbol sym = getWrapper().getNode().getSymbol();
        sym.getRoot().visit(find);

        NodeObject node = find.getBestNode();
        if (node != null)
        {
            setValue(new PropertyDataReference<RavenStroke>(node.getUid()));
            return;
        }

        setValue(RavenStrokeNone.STROKE);
    }

    @Override
    public String[] getTags()
    {
        return null;
    }

    @Override
    public PropertyCustomEditor createCustomEditor()
    {
        return new RavenStrokeCustomEditor(this);
    }

    @Override
    public boolean supportsCustomEditor()
    {
        return true;
    }

    //----------------------------


    @ServiceInst(service=PropertyProvider.class)
    public static class Provider extends PropertyProvider<RavenStroke>
    {
        public Provider()
        {
            super(RavenStroke.class);
        }

        @Override
        public PropertyWrapperEditor createEditor(PropertyWrapper wrapper)
        {
            return new RavenStrokeEditor(wrapper);
        }

        @Override
        public String asText(RavenStroke value)
        {
            return value == null ? "inherit" : value.toString();
        }

        @Override
        public RavenStroke fromText(String text)
        {
            RavenStrokeInline value = RavenStrokeInline.create(text);
            return value == null ? RavenStrokeNone.STROKE : value;

//            if (text == null || "".equals(text) || "null".equals(text)
//                    || "inherit".equalsIgnoreCase(text))
//            {
//                return RavenStrokeInherit.STROKE;
//            }
//
//            if ("none".equalsIgnoreCase(text))
//            {
//                return RavenStrokeNone.STROKE;
//            }
//
//            //Parse
//            StringReader reader = new StringReader(text);
//            CacheParser parser = new CacheParser(reader);
//            CacheIdentifier ident;
//            try
//            {
//                ident = (CacheIdentifier)parser.Cache();
//            } catch (ParseException ex) {
//                Logger.getLogger(ColorStyleEditor.class.getName()).log(Level.WARNING, null, ex);
//                return null;
//            }
//
//            return RavenStrokeBasic.create((CacheMap)ident);
        }
    }
}
