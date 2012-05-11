/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.raven.editor.node.scene.property;

import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.PropertyCustomEditor;
import com.kitfox.raven.util.tree.PropertyDataInline;
import com.kitfox.raven.util.tree.PropertyProvider;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperEditor;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 *
 * @author kitfox
 */
public class NodeSymbolReferenceEditor
        extends PropertyWrapperEditor<NodeSymbolReference>
{
    public NodeSymbolReferenceEditor(PropertyWrapper wrapper)
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
    }

    @Override
    public String getJavaInitializationString()
    {
        return getAsText();
    }

    @Override
    public String getAsText()
    {
        NodeSymbolReference val = getValueFlat();
        return val == null ? "" : val.toString();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        NodeSymbolReference val = NodeSymbolReference.create(text);
        setValue(new PropertyDataInline<NodeSymbolReference>(val));
    }

    @Override
    public String[] getTags()
    {
        return null;
    }

    @Override
    public PropertyCustomEditor createCustomEditor()
    {
        return new NodeSymbolReferenceCustomEditor(this);
    }

    @Override
    public boolean supportsCustomEditor()
    {
        return true;
    }



    //----------------------------

    @ServiceInst(service=PropertyProvider.class)
    public static class Provider extends PropertyProvider<NodeSymbolReference>
    {
        public Provider()
        {
            super(NodeSymbolReference.class);
        }

        @Override
        public PropertyWrapperEditor createEditor(PropertyWrapper wrapper)
        {
            return new NodeSymbolReferenceEditor(wrapper);
        }

        @Override
        public String asText(NodeSymbolReference value)
        {
            return value == null ? "" : value.toString();
        }

        @Override
        public NodeSymbolReference fromText(String text)
        {
            return NodeSymbolReference.create(text);
        }
    }
}
