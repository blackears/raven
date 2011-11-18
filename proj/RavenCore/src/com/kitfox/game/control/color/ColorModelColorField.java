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

package com.kitfox.game.control.color;

import com.kitfox.raven.util.PropertyChangeWeakListener;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author kitfox
 */
abstract public class ColorModelColorField 
        implements ColorField, PropertyChangeListener
{
    protected final ColorChooserModel model;
    ArrayList<ColorFieldListener> listeners = new ArrayList<ColorFieldListener>();

    PropertyChangeWeakListener listener;

    protected ColorModelColorField(ColorChooserModel model)
    {
        this.model = model;
        listener = new PropertyChangeWeakListener(this, model);
        model.addPropertyChangeListener(listener);
    }

    @Override
    public void addColorFieldListener(ColorFieldListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void removeColorFieldListener(ColorFieldListener listener)
    {
        listeners.remove(listener);
    }

    protected void fireModelChanged()
    {
        ChangeEvent evt = new ChangeEvent(this);
        for (ColorFieldListener l: new ArrayList<ColorFieldListener>(listeners))
        {
            l.colorFieldChanged(evt);
        }
    }


}
