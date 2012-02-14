/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.raven.shape.network;

import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.*;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 *
 * @author kitfox
 */
public class NetworkMeshEditor extends PropertyWrapperEditor<NetworkMesh>
{
    public NetworkMeshEditor(PropertyWrapper wrapper)
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
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public String getJavaInitializationString()
    {
        return getAsText();
    }

    @Override
    public String getAsText()
    {
        NetworkMesh val = getValueFlat();
        return val == null ? "" : val.toString();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        NetworkMesh val = NetworkMesh.create(text);
        setValue(new PropertyDataInline<NetworkMesh>(val));
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
    public static class Provider extends PropertyProvider<NetworkMesh>
    {
        public Provider()
        {
            super(NetworkMesh.class);
        }

        @Override
        public PropertyWrapperEditor createEditor(PropertyWrapper wrapper)
        {
            return new NetworkMeshEditor(wrapper);
        }

        @Override
        public String asText(NetworkMesh value)
        {
            return value == null ? "" : value.toString();
        }

        @Override
        public NetworkMesh fromText(String text)
        {
            return NetworkMesh.create(text);
        }
    }
    
}
