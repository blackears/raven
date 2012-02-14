/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.raven.editor.node.scene.snap;

import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.*;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 *
 * @author kitfox
 */
public class GraphLayoutEditor extends PropertyWrapperEditor<GraphLayout>
{
    public GraphLayoutEditor(PropertyWrapper wrapper)
    {
        super(wrapper);
    }

    @Override
    public boolean isPaintable()
    {
        return false;
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getJavaInitializationString()
    {
        return getAsText();
    }

    @Override
    public String getAsText()
    {
        GraphLayout val = getValueFlat();
        return val == null ? "" : val.toString();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        GraphLayout val = GraphLayout.create(text);
        setValue(new PropertyDataInline<GraphLayout>(val));
    }

    @Override
    public String[] getTags()
    {
        return null;
    }

    @Override
    public PropertyCustomEditor createCustomEditor()
    {
        return null;
    }

    @Override
    public boolean supportsCustomEditor()
    {
        return false;
    }

    //----------------------------

    @ServiceInst(service=PropertyProvider.class)
    public static class Provider extends PropertyProvider<GraphLayout>
    {
        public Provider()
        {
            super(GraphLayout.class);
        }

        @Override
        public PropertyWrapperEditor createEditor(PropertyWrapper wrapper)
        {
            return new GraphLayoutEditor(wrapper);
        }

        @Override
        public String asText(GraphLayout value)
        {
            return value == null ? "" : value.toString();
        }

        @Override
        public GraphLayout fromText(String text)
        {
            return GraphLayout.create(text);
        }
    }
    
}
