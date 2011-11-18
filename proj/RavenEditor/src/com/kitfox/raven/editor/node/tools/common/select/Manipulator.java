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

package com.kitfox.raven.editor.node.tools.common.select;

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector2d;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.EventObject;

/**
 *
 * @author kitfox
 */
public class Manipulator
{
    protected static final float DRAG_HANDLE_SIZE = 8;
    protected static final float DRAG_HANDLE_HALF = DRAG_HANDLE_SIZE / 2;

    protected static final Shape DRAG_HANDLE_SHAPE =
            new Rectangle2D.Float(-DRAG_HANDLE_HALF, -DRAG_HANDLE_HALF,
            DRAG_HANDLE_SIZE, DRAG_HANDLE_SIZE);
    protected static final float PIVOT_HANDLE_SIZE = 8;
    protected static final Shape PIVOT_HANDLE_SHAPE =
            new Ellipse2D.Float(-DRAG_HANDLE_HALF, -DRAG_HANDLE_HALF,
            PIVOT_HANDLE_SIZE, PIVOT_HANDLE_SIZE);

    private ArrayList<ManipulatorListener> listeners =
            new ArrayList<ManipulatorListener>();

    public void addManipulatorListener(ManipulatorListener l)
    {
        listeners.add(l);
    }

    public void removeManipulatorListener(ManipulatorListener l)
    {
        listeners.remove(l);
    }

    protected void fireManipulatorChanged()
    {
        EventObject evt = new EventObject(this);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).selectionManipulatorChanged(evt);
        }
    }

    protected void paintHandleShape(Graphics2D g, CyVector2d pos,
            Shape shape,
            Color fillColor, Color outlineColor)
    {
        g.translate(pos.getX(), pos.getY());
        g.setColor(fillColor);
        g.fill(shape);
        g.setColor(outlineColor);
        g.draw(shape);
        g.translate(-pos.getX(), -pos.getY());
    }

    protected boolean handleHit(double x, double y,
            double px, double py,
            CyMatrix4d w2d)
    {
        CyVector2d pt = new CyVector2d(px, py);
        w2d.transformPoint(pt, pt);

        Rectangle2D.Double handleBounds =
                new Rectangle2D.Double(
                pt.x - DRAG_HANDLE_HALF,
                pt.y - DRAG_HANDLE_HALF,
                DRAG_HANDLE_SIZE, DRAG_HANDLE_SIZE);

        return handleBounds.contains(x, y);
    }

}
