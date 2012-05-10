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

package com.kitfox.raven.editor.paint;

import com.kitfox.game.control.color.UnderlayPaint;
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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;

/**
 *
 * @author kitfox
 */
@Deprecated
public class RavenPaintEditor extends PropertyWrapperEditor<RavenPaint>
{
    PropertyData<RavenPaint> customPaintCache;

    public RavenPaintEditor(PropertyWrapper wrapper)
    {
        super(wrapper);
    }

    @Override
    public boolean isPaintable()
    {
        PropertyData<RavenPaint> data = getValue();
        RavenPaint val = data.getValue(getDocument());
        return val != null;
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box)
    {
        Graphics2D g = (Graphics2D)gfx;

        //Graphics2D already has translation applied, so zero out x, y of box
        box.x = 0;
        box.y = 0;

        g.setPaint(UnderlayPaint.inst().getPaint());
        g.fillRect(0, 0, box.width, box.height);

        PropertyData<RavenPaint> data = getValue();
        RavenPaint ravenPaint = data.getValue(getDocument());

        Paint paint = ravenPaint.getPaintSwatch(box);

        g.setPaint(paint);
        g.fillRect(0, 0, box.width, box.height);
    }

    @Override
    public String getJavaInitializationString()
    {
        PropertyData<RavenPaint> data = getValue();
        if (data instanceof PropertyDataInline)
        {
            RavenPaint paint = data.getValue(null);

            if (paint instanceof RavenPaintColor)
            {
                return ((RavenPaintColor)paint).toCode();
            }
            if (paint instanceof RavenPaintGradient)
            {
                return ((RavenPaintGradient)paint).toCode();
            }
            return "null";
        }

        if (data instanceof PropertyDataReference)
        {
            int uid = ((PropertyDataReference)data).getUid();
            NodeSymbol doc = getWrapper().getNode().getSymbol();
            NodeObject node = doc.getNode(uid);
            return node.getName();
        }

        return "null";
    }

    @Override
    public String getAsText()
    {
        PropertyData<RavenPaint> data = getValue();
        if (data instanceof PropertyDataInline)
        {
            RavenPaint paint = data.getValue(null);
            return paint == null ? "none" : paint.toString();
        }

        if (data instanceof PropertyDataReference)
        {
            int uid = ((PropertyDataReference)data).getUid();
            NodeSymbol doc = getWrapper().getNode().getSymbol();
            NodeObject node = doc.getNode(uid);
            return node.getName();
        }

        return "none";
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        RavenPaintInline value = RavenPaintInline.create(text);
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
            setValue(new PropertyDataReference<RavenPaint>(node.getUid()));
            return;
        }

        //If nothing fits, set to inherit
        setValue(RavenPaintNone.PAINT);
    }

    @Override
    public String[] getTags()
    {
        return null;
    }

    @Override
    public boolean supportsCustomEditor()
    {
        return true;
    }

    @Override
    public PropertyCustomEditor createCustomEditor()
    {
        customPaintCache = getValue();
        return new RavenPaintCustomEditor(this);
    }

    //----------------------------


    @ServiceInst(service=PropertyProvider.class)
    public static class Provider extends PropertyProvider<RavenPaint>
    {
        public Provider()
        {
            super(RavenPaint.class);
        }

        @Override
        public PropertyWrapperEditor createEditor(PropertyWrapper wrapper)
        {
            return new RavenPaintEditor(wrapper);
        }

        @Override
        public String asText(RavenPaint value)
        {
            return value == null ? "inherit" : value.toString();
        }

        @Override
        public RavenPaint fromText(String text)
        {
            RavenPaintInline value = RavenPaintInline.create(text);
            return value == null ? RavenPaintNone.PAINT : value;
        }
    }
}
