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
import com.kitfox.raven.editor.node.scene.RavenNodeXformable;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.editor.node.tools.common.select.ManipulatorListener;
import com.kitfox.raven.shape.bezier.BezierNetwork;
import com.kitfox.raven.shape.bezier.BezierNetwork.ManipComponent;
import com.kitfox.raven.shape.bezier.BezierNetwork.ManipVertex;
import com.kitfox.raven.shape.bezier.BezierVertex;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.SelectionEvent;
import com.kitfox.raven.util.SelectionListener;
import com.kitfox.raven.util.SelectionSubEvent;
import com.kitfox.raven.util.SelectionWeakListener;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.SelectionRecord;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.TimerTask;

/**
 *
 * @author kitfox
 */
abstract public class ToolSubselectBase extends ToolDisplay
        implements SelectionListener, ManipulatorListener
{
    MouseEvent startEvt;
    MouseEvent endEvt;

    TimerTask repaintTask;

    SelectionWeakListener listener;


    public ToolSubselectBase(ToolUser user)
    {
        super(user);

        setEnableRestrictAxis(false);

        ServiceDocument provider = user.getToolService(ServiceDocument.class);
        if (provider == null)
        {
            return;
        }

        Selection<SelectionRecord> sel = provider.getSelection();
        listener = new SelectionWeakListener(this, sel);
        sel.addSelectionListener(listener);
    }


    @Override
    protected void click(MouseEvent evt)
    {
        ServiceDocument provDoc = user.getToolService(ServiceDocument.class);
        if (provDoc == null)
        {
            return;
        }

        ServiceDeviceCamera provDevCam = user.getToolService(ServiceDeviceCamera.class);
        if (provDevCam == null)
        {
            return;
        }


        Selection.Type selType = getSelectType(evt);
        
        BezierVertex pickVtx = null;
        Selection<SelectionRecord> sel = provDoc.getSelection();
        for (int i = 0; i < sel.size(); ++i)
        {
            SelectionRecord rec = sel.get(i);
            ServiceBezierNetwork provBez =
                    rec.getNode().getNodeService(ServiceBezierNetwork.class, false);

            if (provBez == null)
            {
                continue;
            }

            RavenNodeXformable nodeSpatial = (RavenNodeXformable)rec.getNode();
            AffineTransform l2d = nodeSpatial.getLocalToDeviceTransform((AffineTransform)null);

            BezierNetwork network = provBez.getBezierNetwork();
            ArrayList<ManipComponent> comps = network.selectPointManipulators(
                    new Rectangle(evt.getX(), evt.getY(), 1, 1),
                    null,
                    l2d,
                    Intersection.INTERSECTS);

            for (ManipComponent comp: comps)
            {
                if (comp instanceof ManipVertex)
                {
                    pickVtx = ((ManipVertex)comp).getVertex();
                    break;
                }
            }

            if (pickVtx != null)
            {
//                ArrayList<BezierVertex> vtxList = network.getLinkedVertices(pickVtx);
//                vtxList.add(pickVtx);

                BezierNetwork.Subselection sub =
                        sel.getSubselection(rec, BezierNetwork.Subselection.class);
                if (sub == null)
                {
                    sub = new BezierNetwork.Subselection();
                }
                else
                {
                    sub = new BezierNetwork.Subselection(sub);
                }

                sub.addVertices(selType, Collections.singletonList(pickVtx));
//                sub.addVertices(selType, vtxList);
                sel.setSubselection(rec, BezierNetwork.Subselection.class, sub);
                fireToolDisplayChanged();
                return;
            }
//            else
//            {
//                sel.setSubselection(rec, BezierNetwork.Subselection.class, null);
//            }
            break;
        }

        if (pickVtx == null)
        {
            //We didn't hit a vertex.  Pick a shape instead.
            CyMatrix4d w2d = provDevCam.getWorldToDeviceTransform((CyMatrix4d)null);
            NodeObject pickObj = provDoc.pickObject(
                    new CyRectangle2d(evt.getX(), evt.getY(), 1, 1),
                    w2d, Intersection.INTERSECTS);

            if (pickObj != null)
            {
                SelectionRecord newRec = new SelectionRecord(pickObj);
                sel.select(selType, newRec);
                fireToolDisplayChanged();
                return;
            }
        }

        //We still hit nothing.  Clear subselections.
        if (selType == Selection.Type.REPLACE)
        {
            for (int i = 0; i < sel.size(); ++i)
            {
                SelectionRecord rec = sel.get(i);
                sel.setSubselection(rec, BezierNetwork.Subselection.class, null);
            }
            fireToolDisplayChanged();
        }
        
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

    @Override
    protected void endDrag(MouseEvent evt)
    {
        Rectangle rect = getDragShape();

        ServiceDocument provider = user.getToolService(ServiceDocument.class);
        if (provider == null)
        {
            return;
        }

        if (isDraggingSelectionArea())
        {
            Selection.Type selType = getSelectType(evt);

            Selection<SelectionRecord> sel = provider.getSelection();
            for (int i = 0; i < sel.size(); ++i)
            {
                SelectionRecord rec = sel.get(i);
                ServiceBezierNetwork provBez =
                        rec.getNode().getNodeService(ServiceBezierNetwork.class, false);

                if (provBez == null)
                {
                    continue;
                }

                RavenNodeXformable nodeSpatial = (RavenNodeXformable)rec.getNode();
                AffineTransform l2d = nodeSpatial.getLocalToDeviceTransform((AffineTransform)null);

                BezierNetwork network = provBez.getBezierNetwork();
                ArrayList<ManipComponent> comps = network.selectPointManipulators(
                        rect,
                        null,
                        l2d,
                        Intersection.INTERSECTS);

//                ArrayList<BezierVertex> pickList = network.selectVertices(rect,
//                        l2d,
//                        Intersection.INTERSECTS);

                if (!comps.isEmpty())
                {
                    BezierNetwork.Subselection sub =
                            sel.getSubselection(rec, BezierNetwork.Subselection.class);

                    if (sub == null)
                    {
                        sub = new BezierNetwork.Subselection();
                    }
                    else
                    {
                        sub = new BezierNetwork.Subselection(sub);
                    }

                    ArrayList<BezierVertex> vertList = new ArrayList<BezierVertex>();
                    for (ManipComponent comp: comps)
                    {
                        if (comp instanceof ManipVertex)
                        {
                            vertList.add(((ManipVertex)comp).getVertex());
                        }
                    }
                    sub.addVertices(selType, vertList);
                    sel.setSubselection(rec, BezierNetwork.Subselection.class, sub);
                }
            }
//            fireToolDisplayChanged();
//            return;

//            //Do selection
//            ArrayList<RavenNodeSpatial> selList = new ArrayList<RavenNodeSpatial>();
//            provider.pickObjects(
//                    new Rectangle(rect.x, rect.y, rect.width, rect.height),
//                    Intersection.CONTAINS, selList);
//
//            ArrayList<SelectionRecord> pickList = new ArrayList<SelectionRecord>(selList.size());
//            for (RavenNodeSpatial node: selList)
//            {
//                pickList.add(new SelectionRecord(node));
//            }
//
//            Selection<SelectionRecord> sel = provider.getSelection();
//
//            switch (getSelectType(evt))
//            {
//                case REPLACE:
//                    sel.select(Selection.Type.REPLACE, pickList);
//                    break;
//                case ADD:
//                {
//                    sel.select(Selection.Type.ADD, pickList);
//                    break;
//                }
//                case SUBTRACT:
//                {
//                    sel.select(Selection.Type.SUB, pickList);
//                    break;
//                }
//                case INTERSECT:
//                {
//                    sel.select(Selection.Type.INVERSE, pickList);
//                    break;
//                }
//            }
        }

        startEvt = endEvt = null;

        repaintTask.cancel();
        repaintTask = null;
        fireToolDisplayChanged();
    }

    @Override
    public void cancel()
    {
        startEvt = endEvt = null;
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void selectionChanged(SelectionEvent evt)
    {
        fireToolDisplayChanged();
    }

    @Override
    public void subselectionChanged(SelectionSubEvent evt)
    {
        fireToolDisplayChanged();
    }

    @Override
    public void selectionManipulatorChanged(EventObject evt)
    {
    }

    abstract protected boolean isDraggingSelectionArea();

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
