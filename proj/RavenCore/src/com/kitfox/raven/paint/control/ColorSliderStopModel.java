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

package com.kitfox.raven.paint.control;

import com.kitfox.raven.paint.common.RavenPaintColor;
import com.kitfox.raven.util.PropertyChangeWeakListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.event.ChangeEvent;

/**
 * Represents a single stop in a color gradient slider panel.
 *
 * @author kitfox
 */
public class ColorSliderStopModel 
        implements StopModel<ColorChooserModel>, PropertyChangeListener
{
    final private ColorField field;
    final private ColorChooserModel model;
    final StopSide side;
    
    ArrayList<StopModelListener> listeners = new ArrayList<StopModelListener>();
    PropertyChangeWeakListener propListener;

    public ColorSliderStopModel(ColorField field, ColorChooserModel model, StopSide side)
    {
        this.field = field;
        this.model = model;
        this.side = side;

        propListener = new PropertyChangeWeakListener(this, model);
        model.addPropertyChangeListener(propListener);
    }

    @Override
    public void addStopModelListener(StopModelListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void removeStopModelListener(StopModelListener listener)
    {
        listeners.remove(listener);
    }

    private void fireModelChanged()
    {
        ChangeEvent evt = new ChangeEvent(this);
        for (StopModelListener l: new ArrayList<StopModelListener>(listeners))
        {
            l.stopModelChanged(evt);
        }
    }

    @Override
    public float getStopValue(ColorChooserModel stop)
    {
        RavenPaintColor color = model.getColor();
        Point2D.Float pt = field.toCoords(color);

        switch (side)
        {
            case NORTH:
            case SOUTH:
                return pt.x;
            default:
                return pt.y;
        }
    }

    @Override
    public void setStopValue(ColorChooserModel stop, float value)
    {
        RavenPaintColor color = field.toColor(value, value);
        model.setColor(color);
    }

    @Override
    public List<ColorChooserModel> getStopObjects()
    {
        return Collections.singletonList(model);
    }

    @Override
    public void removeStop(ColorChooserModel stop)
    {
        //Ignore
    }

    @Override
    public void editStop(ColorChooserModel stop)
    {
        //Ignore
    }

    @Override
    public void addStop(float value)
    {
        //Ignore
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        fireModelChanged();
    }

    @Override
    public void beginStopEdits()
    {
        //Ignore
    }

    @Override
    public void endStopEdits()
    {
        //Ignore
    }

}
