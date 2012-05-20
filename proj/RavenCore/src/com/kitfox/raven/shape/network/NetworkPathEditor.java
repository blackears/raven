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

package com.kitfox.raven.shape.network;

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
public class NetworkPathEditor extends PropertyWrapperEditor<NetworkPath>
{
    public NetworkPathEditor(PropertyWrapper wrapper)
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
        NetworkPath val = getValueFlat();
        return val == null ? "" : val.toString();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        NetworkPath val = NetworkPath.create(text);
        setValue(new PropertyDataInline<NetworkPath>(val));
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
    public static class Provider extends PropertyProvider<NetworkPath>
    {
        public Provider()
        {
            super(NetworkPath.class);
        }

        @Override
        public PropertyWrapperEditor createEditor(PropertyWrapper wrapper)
        {
            return new NetworkPathEditor(wrapper);
        }

        @Override
        public String asText(NetworkPath value)
        {
            return value == null ? "" : value.toString();
        }

        @Override
        public NetworkPath fromText(String text)
        {
            return NetworkPath.create(text);
        }
    }
    
}
