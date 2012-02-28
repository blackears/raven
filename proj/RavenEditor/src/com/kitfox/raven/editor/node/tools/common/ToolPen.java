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

import com.kitfox.coyote.shape.CyStroke;
import com.kitfox.coyote.shape.CyStrokeCap;
import com.kitfox.coyote.shape.CyStrokeJoin;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.node.scene.RavenNodeGroup;
import com.kitfox.raven.editor.node.scene.RavenNodePath;
import com.kitfox.raven.editor.node.scene.RavenNodeRoot;
import com.kitfox.raven.editor.node.tools.ToolProvider;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.paint.RavenStroke;
import com.kitfox.raven.paint.common.RavenPaintColor;
import com.kitfox.raven.shape.bezier.BezierCurve;
import com.kitfox.raven.shape.bezier.BezierEdge;
import com.kitfox.raven.shape.bezier.BezierNetwork;
import com.kitfox.raven.shape.bezier.BezierPath;
import com.kitfox.raven.shape.bezier.BezierPoint;
import com.kitfox.raven.shape.bezier.BezierVertex;
import com.kitfox.raven.shape.bezier.VertexSmooth;
import com.kitfox.raven.shape.path.PathCurve;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.tree.NodeObjectProviderIndex;
import com.kitfox.raven.util.tree.SelectionRecord;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
@Deprecated
public class ToolPen extends ToolDisplay
{
    static final int flatnessSquared = 10000;

    BezierPath bezierPath;
    
    int lastPx;
    int lastPy;
    int lastKx;
    int lastKy;
    boolean lastPointWasSmooth;
    boolean addedCubicSegment;
    boolean firstPoint;

    int curPx;
    int curPy;
    int curKx;
    int curKy;

    MouseEvent mouseMovePos;

    protected ToolPen(ToolUser user)
    {
        super(user);
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void click(MouseEvent evt)
    {
        addPoint(evt.getX() * 100, evt.getY() * 100);
        fireToolDisplayChanged();
    }

    @Override
    public void paint(Graphics2D g)
    {
        super.paint(g);

        if (bezierPath != null)
        {
            if (getDragState() == DragState.NONE && mouseMovePos != null)
            {
                int mx = mouseMovePos.getX();
                int my = mouseMovePos.getY();

                Path2D.Double movePath = new Path2D.Double();
                movePath.moveTo(lastPx / 100, lastPy / 100);
                if (lastPointWasSmooth)
                {
                    movePath.curveTo(curKx / 100, curKy / 100,
                            mx, my,
                            mx, my);
                }
                else
                {
                    movePath.lineTo(mx, my);
                }

                g.setColor(Color.blue);
                g.draw(movePath);
            }

            if (firstPoint && curKx != curPx && curKy != curPy)
            {
                bezierPath.renderKnotManipulator(g,
                        curPx / 100, curPy / 100,
                        curKx / 100, curKy / 100,
                        Color.blue);
                bezierPath.renderKnotManipulator(g,
                        curPx / 100, curPy / 100,
                        (curPx * 2 - curKx) / 100, (curPy * 2 - curKy) / 100,
                        Color.blue);
            }

            BezierVertex endVtx = bezierPath.getVtxLast();
//            HashSet<BezierVertex> selectedVerts = new HashSet<BezierVertex>();
            BezierNetwork.Subselection subsel = new BezierNetwork.Subselection(
                    endVtx);
//            selectedVerts.add(endVtx);

            bezierPath.renderManipulator(g, subsel, null, Color.blue);
        }

    }

    @Override
    protected void startDrag(MouseEvent evt)
    {
        startCubic(evt.getX() * 100, evt.getY() * 100);
        fireToolDisplayChanged();
    }

    @Override
    protected void dragTo(MouseEvent evt)
    {
        setCubicKnot(evt.getX() * 100, evt.getY() * 100);

        fireToolDisplayChanged();
    }

    @Override
    protected void endDrag(MouseEvent evt)
    {
        setCubicKnot(evt.getX() * 100, evt.getY() * 100);
        finishCubic();

        fireToolDisplayChanged();
    }

    @Override
    public void mouseMoved(MouseEvent evt)
    {
        super.mouseMoved(evt);

        if (bezierPath != null)
        {
            mouseMovePos = evt;
            fireToolDisplayChanged();
        }
        else
        {
            mouseMovePos = null;
        }
    }



    @Override
    public void cancel()
    {
        bezierPath = null;
    }

    @Override
    public void keyPressed(KeyEvent evt)
    {
        switch (evt.getKeyCode())
        {
            default:
                super.keyPressed(evt);
                break;
            case KeyEvent.VK_ENTER:
            {
                export();
                bezierPath = null;
                fireToolDisplayChanged();
                break;
            }
            case KeyEvent.VK_DELETE:
            {
                if (getDragState() == DragState.NONE)
                {
                    if (bezierPath.getNumVertices() == 1)
                    {
                        bezierPath = null;
                    }
                    else
                    {
                        BezierVertex vtxLast = bezierPath.getVtxLast();
                        BezierEdge edge = bezierPath.prevEdge(vtxLast);
                        BezierCurve curve = edge.getCurve();
                        
                        lastPx = curve.getStartX();
                        lastPy = curve.getStartY();
                        lastKx = curve.getStartKnotX();
                        lastKy = curve.getStartKnotY();
                        
                        bezierPath.removeLast();
                    }
                    fireToolDisplayChanged();
                }
                break;
            }
        }
    }

    private void addPoint(int x, int y)
    {
        if (bezierPath == null)
        {
            bezierPath = new BezierPath(flatnessSquared);
            bezierPath.moveTo(x, y);

            lastPointWasSmooth = false;
            curPx = curKx = lastPx = x;
            curPy = curKy = lastPy = y;
            firstPoint = true;
            return;
        }

        //If close to start point, snap to it and finish curve
        boolean finishPath = false;
        BezierPoint startPt = bezierPath.getVtxFirst().getPoint();
        if (Math.abs(startPt.getX() - x) / 100 < BezierNetwork.POINT_HANDLE_RADIUS
                && Math.abs(startPt.getY() - y) / 100 < BezierNetwork.POINT_HANDLE_RADIUS)
        {
            x = startPt.getX();
            y = startPt.getY();
            finishPath = true;
        }

        //No segments of zero size
        if (lastPx == x && lastPy == y)
        {
            return;
        }

        firstPoint = false;
        if (lastPointWasSmooth)
        {
            bezierPath.cubicTo(lastKx, lastKy,
                    (x * 2 + lastPx) / 3, (y * 2 + lastPy) / 3,
                    x, y);
        }
        else
        {
            bezierPath.lineTo(x, y);
        }

        lastPointWasSmooth = false;
        lastPx = x;
        lastPy = y;

        if (finishPath)
        {
            export();
            bezierPath = null;
            fireToolDisplayChanged();
            return;
        }
    }

    private void startCubic(int x, int y)
    {
        if (bezierPath == null)
        {
            bezierPath = new BezierPath(flatnessSquared);
            bezierPath.moveTo(x, y);

//            lastPointWasSmooth = true;
            curPx = x;
            curPy = y;
            curKx = x;
            curKy = y;
            firstPoint = true;
            addedCubicSegment = false;
            return;
        }

        firstPoint = false;
        curPx = x;
        curPy = y;
        addedCubicSegment = false;

        if (!lastPointWasSmooth)
        {
            lastKx = (curPx * 2 + lastPx) / 3;
            lastKy = (curPy * 2 + lastPy) / 3;
        }
    }

    private void setCubicKnot(int x, int y)
    {
        if (addedCubicSegment)
        {
            bezierPath.removeLast();
        }

        curKx = x;
        curKy = y;

        if (curPx == curKx && curPy == curKy)
        {
            addedCubicSegment = false;
            return;
        }

        if (!firstPoint)
        {
            bezierPath.cubicTo(lastKx, lastKy,
                    curPx * 2 - curKx, curPy * 2 - curKy,
                    curPx, curPy);

            addedCubicSegment = true;
        }
        else
        {
            addedCubicSegment = false;
        }

        BezierVertex vtxLast = bezierPath.getVtxLast();
        vtxLast.setData(VertexSmooth.PlaneData.class, VertexSmooth.SMOOTH);
    }

    private void finishCubic()
    {
        lastPx = curPx;
        lastPy = curPy;
        lastKx = curKx;
        lastKy = curKy;
        lastPointWasSmooth = true;
    }

    private void export()
    {
        if (bezierPath == null)
        {
            return;
        }

        if (bezierPath.getVtxFirst().getPoint().equals(bezierPath.getVtxLast().getPoint()))
        {
            bezierPath.closePath();
        }

        ServiceDocument provider = user.getToolService(ServiceDocument.class);
        RavenNodeRoot doc = (RavenNodeRoot)provider.getDocument();

        //Find node to add new stroke to
        NodeObject topSel = doc.getSelection().getTopSelected();
        RavenNodeGroup parentGroupNode = null;
        if (topSel != null)
        {
            if (topSel instanceof RavenNodeGroup)
            {
                parentGroupNode = (RavenNodeGroup)topSel;
            }
            else
            {
                NodeObject topNodeParent = topSel.getParent().getNode();
                if (topNodeParent instanceof RavenNodeGroup)
                {
                    parentGroupNode = (RavenNodeGroup)topNodeParent;
                }
            }
        }

        AffineTransform devToLocal = null;
        if (parentGroupNode != null)
        {
            devToLocal = parentGroupNode.getLocalToDeviceTransform((AffineTransform)null);
        }
        else
        {
            devToLocal = getWorldToDeviceTransform((AffineTransform)null);
        }
        devToLocal.scale(1 / 100.0, 1 / 100.0);

        try
        {
            devToLocal.invert();
        } catch (NoninvertibleTransformException ex)
        {
            Logger.getLogger(ToolPaintStroke.class.getName()).log(Level.SEVERE, null, ex);
        }

        Path2D.Double path = bezierPath.asPathInPixels();
        Path2D.Double shape = (Path2D.Double)devToLocal.createTransformedShape(path);



        //Add node
        if (doc != null)
        {
            doc.getHistory().beginTransaction("Add pen stroke");
        }

        NodeObjectProvider<RavenNodePath> prov =
                NodeObjectProviderIndex.inst().getProvider(RavenNodePath.class);
        RavenNodePath nodePath = prov.createNode(doc);

        String name = doc.createUniqueName("Pen");
        nodePath.setName(name);

        PathCurve curve = new PathCurve(shape);
        nodePath.path.setValue(curve);

        nodePath.paint.setValue(null);
        nodePath.strokePaint.setValue(RavenPaintColor.BLACK);
        nodePath.stroke.setValue(new RavenStroke(
                new CyStroke(5, 
                CyStrokeCap.ROUND, CyStrokeJoin.ROUND)));

        nodePath.centerPivot(true);

        if (parentGroupNode != null)
        {
            parentGroupNode.children.add(nodePath);

        }
        else
        {
            doc.sceneGraph.add(nodePath);
        }

        if (doc != null)
        {
            doc.getHistory().commitTransaction();
        }
    }

    //---------------------------------------

//    @ServiceInst(service=ToolProvider.class)
    static public class Provider extends ToolProvider<ToolPen>
    {
        public Provider()
        {
            super("Pen", "/icons/tools/pen.png", "/manual/tools/pen.html");
        }

        @Override
        public ToolPen create(ToolUser user)
        {
            return new ToolPen(user);
        }

        @Override
        public Component createToolSettingsEditor(RavenEditor editor)
        {
            return new ToolPenSettings(editor);
        }
    }

}
