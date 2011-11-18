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

package com.kitfox.raven.util.tree.property;

import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.util.tree.PropertyCustomEditor;
import com.kitfox.raven.util.tree.PropertyDataInline;
import com.kitfox.raven.util.tree.PropertyProvider;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperEditor;
import com.kitfox.raven.util.tree.TrackKey;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

/**
 *
 * @author kitfox
 */
public class DoubleEditor extends PropertyWrapperEditor<Double>
        implements MouseListener, MouseMotionListener, MouseWheelListener
{
    float dragIncrement = 1;
    ButtonGroup dragButtonGroup = new ButtonGroup();

    static final int DRAG_UNIT = 10;

    double dragCache;
    MouseEvent startDragEvt;

    public DoubleEditor(PropertyWrapper wrapper)
    {
        super(wrapper);
    }

    @Override
    protected void buildPopupMenu(JPopupMenu menu)
    {
        appendDefaultMenu(menu);

        menu.add(new JSeparator());

        addDragIncrement(menu, .001f, false);
        addDragIncrement(menu, .01f, false);
        addDragIncrement(menu, .1f, false);
        addDragIncrement(menu, 1, true);
        addDragIncrement(menu, 10, false);
        addDragIncrement(menu, 100, false);
        addDragIncrement(menu, 1000, false);
    }

    private void addDragIncrement(JPopupMenu menu, final float dragIncrement, boolean selected)
    {
        final JRadioButtonMenuItem item = new JRadioButtonMenuItem();
        item.setAction(new AbstractAction("" + dragIncrement)
        {
            private static final long serialVersionUID = 1;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                DoubleEditor.this.dragIncrement = dragIncrement;
                item.setSelected(true);
            }
        });

        item.setSelected(selected);
        dragButtonGroup.add(item);
        menu.add(item);
    }

    @Override
    protected void setKey()
    {
        setKey(TrackKey.Interp.SMOOTH_STEP);
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
        return getAsText() + "f";
    }

    @Override
    public String getAsText()
    {
        return "" + getValueFlat();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        try
        {
            Double val = Double.parseDouble(text);
            setValue(new PropertyDataInline<Double>(val));
        }
        catch (NumberFormatException ex)
        {
            //Logger.getLogger(DoubleEditor.class.getName()).log(Level.INFO, null, ex);
        }
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

    @Override
    public void mousePressed(MouseEvent evt)
    {
        super.mousePressed(evt);
        if (evt.isConsumed())
        {
            return;
        }

        //Drag value on 2nd mouse bn only
        if ((evt.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) == MouseEvent.BUTTON2_DOWN_MASK)
        {
            startDragEvt = evt;
            dragCache = getValueFlat();
        }
    }

    @Override
    public void mouseReleased(MouseEvent evt)
    {
        super.mouseReleased(evt);
        if (evt.isConsumed())
        {
            return;
        }

        //Reset to make undo record
        if (startDragEvt != null)
        {
            setValue(new PropertyDataInline<Double>(dragCache), false);

            int dx = evt.getX() - startDragEvt.getX();
            double newVal = dragCache + (dx / DRAG_UNIT) * dragIncrement;
            setValue(new PropertyDataInline<Double>(newVal));
        
            startDragEvt = null;
        }
    }

    @Override
    public void mouseDragged(MouseEvent evt)
    {
        if (startDragEvt == null)
        {
            //Action has been canceled
            return;
        }

        int dx = evt.getX() - startDragEvt.getX();
        double newVal = dragCache + (dx / DRAG_UNIT) * dragIncrement;
        setValue(new PropertyDataInline<Double>(newVal), false);
    }

    @Override
    public void mouseMoved(MouseEvent evt)
    {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent evt)
    {
        setValue(getValueFlat() - evt.getWheelRotation() * dragIncrement);
    }

    //----------------------------

    @ServiceInst(service=PropertyProvider.class)
    public static class Provider extends PropertyProvider<Double>
    {
        public Provider()
        {
            super(Double.class);
        }

        @Override
        public PropertyWrapperEditor createEditor(PropertyWrapper wrapper)
        {
            return new DoubleEditor(wrapper);
        }

        @Override
        public boolean isNumeric()
        {
            return true;
        }

        @Override
        public double asDouble(Double value)
        {
            return value;
        }

        @Override
        public Double createNumericValue(PropertyWrapper wrapper, double value)
        {
            return value;
        }

        @Override
        public String asText(Double value)
        {
            return "" + value;
        }

        @Override
        public Double fromText(String text)
        {
            return Double.parseDouble(text);
        }

        @Override
        public Double interpolate(NodeDocument doc,
                TrackKey<Double> k0, TrackKey<Double> k1,
                int frame, int k0Frame, int k1Frame)
        {
            int span = k1Frame - k0Frame;
            double frac = (frame - k0Frame) / (double)span;

            return interpolate(k0.getInterp(),
                    k0.getData().getValue(doc), k0.getTanOutX(), k0.getTanOutY(),
                    k1.getData().getValue(doc), k1.getTanInX(), k1.getTanInY(),
                    span, frac);
        }

        @Override
        public Double interpolateWithOffset(NodeDocument doc,
                TrackKey<Double> k0, TrackKey<Double> k1,
                int frame, int k0Frame, int k1Frame,
                TrackKey<Double> firstKey, TrackKey<Double> lastKey, int offsetSize)
        {
            double last = lastKey.getData().getValue(doc);
            double first = firstKey.getData().getValue(doc);

            return (last - first) * offsetSize
                    + interpolate(doc, k0, k1, frame, k0Frame, k1Frame);
        }
    }

//    @ServiceAnno(service=PropertyProvider.class)
//    public static class ProviderPrim extends PropertyProvider<Float>
//    {
//        public ProviderPrim()
//        {
//            super(Float.TYPE);
//        }
//
//        @Override
//        public PropertyWrapperEditor createEditor(PropertyWrapper wrapper)
//        {
//            return new FloatEditor(wrapper);
//        }
//    }
}
