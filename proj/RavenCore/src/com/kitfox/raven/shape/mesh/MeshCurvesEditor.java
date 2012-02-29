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

package com.kitfox.raven.shape.mesh;

import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.PropertyCustomEditor;
import com.kitfox.raven.util.tree.PropertyData;
import com.kitfox.raven.util.tree.PropertyProvider;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperEditor;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 *
 * @author kitfox
 */
@Deprecated
public class MeshCurvesEditor extends PropertyWrapperEditor<MeshCurves>
{
    public MeshCurvesEditor(PropertyWrapper wrapper)
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
        PropertyData<MeshCurves> data = getValue();
        MeshCurves paint = data.getValue(null);
        return ((MeshCurves)paint).toString();
    }

    @Override
    public String getAsText()
    {
        PropertyData<MeshCurves> data = getValue();
        MeshCurves curves = data.getValue(null);
        return curves == null ? "" : curves.toString();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        MeshCurves value = MeshCurves.create(text);
        setValue(value);
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
    public static class Provider extends PropertyProvider<MeshCurves>
    {
        public Provider()
        {
            super(MeshCurves.class);
        }

        @Override
        public PropertyWrapperEditor createEditor(PropertyWrapper wrapper)
        {
            return new MeshCurvesEditor(wrapper);
        }

        @Override
        public String asText(MeshCurves value)
        {
            return value == null ? "" : value.toString();
        }

        @Override
        public MeshCurves fromText(String text)
        {
            if ("".equals(text))
            {
                return null;
            }

            //Parse
            return MeshCurves.create(text);
        }
    }
}
