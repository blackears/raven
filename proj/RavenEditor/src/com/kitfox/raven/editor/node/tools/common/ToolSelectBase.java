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

package com.kitfox.raven.editor.node.tools.common;

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.raven.util.Intersection;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.editor.node.tools.common.select.ManipulatorListener;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.SelectionListener;
import com.kitfox.raven.util.SelectionWeakListener;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.SelectionRecord;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.TimerTask;

/**
 *
 * @author kitfox
 */
abstract public class ToolSelectBase extends ToolDisplay
        implements SelectionListener, ManipulatorListener
{
    MouseEvent startEvt;
    MouseEvent endEvt;

    TimerTask repaintTask;

    SelectionWeakListener listener;

    protected ToolSelectBase(ToolUser user)
    {
        super(user);

        setEnableRestrictAxis(false);

        ServiceDocument provider = user.getToolService(ServiceDocument.class);
        if (provider == null)
        {
            return;
        }

        Selection<NodeObject> sel = provider.getSelection();
        listener = new SelectionWeakListener(this, sel);
        sel.addSelectionListener(listener);
    }

    @Override
    protected void click(MouseEvent evt)
    {
        ServiceDocument provider = user.getToolService(ServiceDocument.class);
        if (provider == null)
        {
            return;
        }

        ServiceDeviceCamera provCam = user.getToolService(ServiceDeviceCamera.class);
        if (provCam == null)
        {
            return;
        }
        CyMatrix4d w2d = provCam.getWorldToDeviceTransform((CyMatrix4d)null);

        Selection.Operator selType = getSelectType(evt);
        NodeObject pickObj = provider.pickObject(
                new CyRectangle2d(evt.getX(), evt.getY(), 1, 1),
                w2d, Intersection.INTERSECTS);

        Selection<NodeObject> sel = provider.getSelection();

        if (pickObj == null)
        {
            if (selType == Selection.Operator.REPLACE)
            {
                sel.clear();
            }
            fireToolDisplayChanged();
            return;
        }

        sel.select(pickObj, selType);
    }


    @Override
    protected void startDrag(MouseEvent evt)
    {
        startEvt = evt;
        repaintTask = new TimerTask()
        {
            @Override
            public void run() {
                fireToolDisplayChanged();
            }
        };
        timer.schedule(repaintTask, 0, 100);
    }

    @Override
    protected void dragTo(MouseEvent evt)
    {
        endEvt = evt;
    }

    abstract protected boolean isDraggingSelectionArea();

    @Override
    protected void endDrag(MouseEvent evt)
    {
        Rectangle rect = getDragShape();

        ServiceDocument provider = user.getToolService(ServiceDocument.class);
        if (provider == null)
        {
            return;
        }

        ServiceDeviceCamera provCam = user.getToolService(ServiceDeviceCamera.class);
        if (provCam == null)
        {
            return;
        }
        CyMatrix4d w2d = provCam.getWorldToDeviceTransform((CyMatrix4d)null);

        if (isDraggingSelectionArea())
        {
            //Do selection
            ArrayList<NodeObject> selList = new ArrayList<NodeObject>();
            provider.pickObjects(
                    new CyRectangle2d(rect.x, rect.y, rect.width, rect.height),
                    w2d, Intersection.CONTAINS, selList);

//            ArrayList<NodeObject> pickList = new ArrayList<NodeObject>(selList.size());
//            for (NodeObject node: selList)
//            {
//                pickList.add(node);
//            }

            Selection<NodeObject> sel = provider.getSelection();
            sel.select(selList, getSelectType(evt));
        }

        startEvt = endEvt = null;

        repaintTask.cancel();
        repaintTask = null;
    }

    @Override
    public void cancel()
    {
        startEvt = endEvt = null;
    }

    @Override
    public void paint(Graphics2D g)
    {
        if (startEvt != null && isDraggingSelectionArea())
        {
            //Draw drag marquis
            Shape dragShape = getDragShape();
            g.setPaint(MaskPaint.inst().getPaint());
            g.draw(dragShape);
        }
    }

    public Rectangle getDragShape()
    {
        if (startEvt == null)
        {
            return null;
        }

        int minX = Math.min(endEvt.getX(), startEvt.getX());
        int maxX = Math.max(endEvt.getX(), startEvt.getX());
        int minY = Math.min(endEvt.getY(), startEvt.getY());
        int maxY = Math.max(endEvt.getY(), startEvt.getY());

        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

}
