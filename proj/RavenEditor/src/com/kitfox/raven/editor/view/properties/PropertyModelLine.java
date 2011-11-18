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

package com.kitfox.raven.editor.view.properties;

import com.kitfox.raven.util.tree.PropertyProvider;
import com.kitfox.raven.util.tree.PropertyProviderIndex;
import com.kitfox.raven.util.tree.PropertyTrackChangeEvent;
import com.kitfox.raven.util.tree.PropertyTrackKeyChangeEvent;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperEditor;
import com.kitfox.raven.util.tree.PropertyWrapperListener;
import com.kitfox.raven.util.tree.PropertyWrapperWeakListener;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author kitfox
 */
public class PropertyModelLine
        implements PropertyWrapperListener
{
    private final PropertyModel model;
    private final PropertyWrapper wrapper;
    private final int row;
    private final PropertyProvider prov;
    private final PropertyWrapperEditor editor;

    PropertyWrapperWeakListener listener;

    public PropertyModelLine(PropertyModel model, PropertyWrapper wrapper, int row)
    {
        this.model = model;
        this.wrapper = wrapper;
        this.row = row;
        
        this.prov = PropertyProviderIndex.inst().getProviderBest(wrapper.getPropertyType());
        this.editor = prov == null ? null : prov.createEditor(wrapper);

        listener = new PropertyWrapperWeakListener(this, wrapper);
        wrapper.addPropertyWrapperListener(listener);
    }

    /**
     * @return the model
     */
    public PropertyModel getModel() {
        return model;
    }

    /**
     * @return the prop
     */
    public PropertyWrapper getProp() {
        return wrapper;
    }

    /**
     * @return the row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return the prov
     */
    public PropertyProvider getProv() {
        return prov;
    }

    /**
     * @return the ed
     */
    public PropertyWrapperEditor getEditor() {
        return editor;
    }

    @Override
    public void propertyWrapperDataChanged(PropertyChangeEvent evt)
    {
        model.fireTableRowModified(row);
    }

    @Override
    public void propertyWrapperTrackChanged(PropertyTrackChangeEvent evt)
    {
        model.fireTableRowModified(row);
    }

    @Override
    public void propertyWrapperTrackKeyChanged(PropertyTrackKeyChangeEvent evt)
    {
        model.fireTableRowModified(row);
    }


}
