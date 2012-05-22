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

package com.kitfox.raven.editor.node.tools.common.shape.curveEdit;

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.coyote.shape.bezier.mesh.BezierVertexSmooth;
import com.kitfox.raven.editor.node.scene.RavenSymbolRoot;
import com.kitfox.raven.editor.node.scene.RenderContext;
import com.kitfox.raven.editor.node.tools.common.shape.PathUtil;
import com.kitfox.raven.editor.node.tools.common.shape.pen.ServiceBezierPath;
import com.kitfox.raven.shape.network.NetworkPath;
import com.kitfox.raven.shape.network.pick.NetworkHandleEdge;
import com.kitfox.raven.shape.network.pick.NetworkHandleKnot;
import com.kitfox.raven.shape.network.pick.NetworkHandleSelection;
import com.kitfox.raven.shape.network.pick.NetworkHandleVertex;
import com.kitfox.raven.shape.network.pick.NetworkPathHandles;
import com.kitfox.raven.shape.network.pick.NetworkPathHandles.HandleEdge;
import com.kitfox.raven.shape.network.pick.NetworkPathHandles.HandleFace;
import com.kitfox.raven.shape.network.pick.NetworkPathHandles.HandleVertex;
import com.kitfox.raven.util.Intersection;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.tree.NodeObject;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author kitfox
 */
public class ToolCurveEditPath extends ToolCurveEditDelegate
{
    NodeObject node;
    final ServiceBezierPath servPath;
    NetworkPathHandles cacheHandles;

    private MouseEvent mouseStart;
    private MouseEvent mouseCur;
    
    boolean dragSelRect;
    PathDragSet dragSet;
    
    protected ToolCurveEditPath(ToolCurveEditDispatch dispatch, 
            NodeObject node,
            ServiceBezierPath servPath)
    {
        super(dispatch);
        this.servPath = servPath;
        this.node = node;
    }

    private NetworkPathHandles getPathHandles()
    {
        NetworkPath path = getPath();
        if (cacheHandles == null || cacheHandles.getPath() != path)
        {
            cacheHandles = path == null ? null : new NetworkPathHandles(path);
        }
        return cacheHandles;
    }
    
    private NetworkPath getPath()
    {
        return servPath.getNetworkPath();
    }

    protected CyVector2d xformDev2PathPoint(CyVector2d v, boolean snapGrid)
    {
        v = xformDev2LocalPoint(v, snapGrid);
        v.scale(100);
        return v;
    }

    @Override
    protected void click(MouseEvent evt)
    {
        RavenSymbolRoot root = getDocument();
        if (root == null)
        {
            return;
        }

        int mod = evt.getModifiersEx();
        if ((mod & InputEvent.BUTTON3_DOWN_MASK) != 0)
        {
            showPopupMenu(evt);
            return;
        }
        
        //Find components to select
        float pickRad = root == null ? 1 : root.getGraphRadiusPick();
        CyRectangle2d region = new CyRectangle2d(evt.getX() - pickRad, evt.getY() - pickRad, 
                pickRad * 2, pickRad * 2);
        
        CyMatrix4d l2w = servPath.getLocalToWorldTransform((CyMatrix4d)null);
        CyMatrix4d l2d = getWorldToDevice(null);
        l2d.mul(l2w);
        
        PathUtil.adjustSelection(region, getSelectType(evt), Intersection.INTERSECTS,
                getSelection(), node, getPathHandles(), l2w, l2d);
    }

    @Override
    protected void startDrag(MouseEvent evt)
    {
        RavenSymbolRoot root = getDocument();
        if (root == null)
        {
            return;
        }

        float pickRad = root == null ? 1 : root.getGraphRadiusPick();
        CyRectangle2d region = new CyRectangle2d(evt.getX() - pickRad, evt.getY() - pickRad, 
                pickRad * 2, pickRad * 2);
        
        NetworkPathHandles handles = getPathHandles();
        CyMatrix4d l2w = servPath.getLocalToWorldTransform((CyMatrix4d)null);
        CyMatrix4d l2d = getWorldToDevice(null);
        l2d.mul(l2w);
        
        ArrayList<NetworkHandleVertex> pickVert =
                handles.pickVertices(region, l2d, Intersection.CONTAINS);
        ArrayList<NetworkHandleEdge> pickEdge =
                handles.pickEdges(region, l2d, Intersection.INTERSECTS);
        ArrayList<NetworkHandleKnot> pickKnot =
                handles.pickKnots(region, l2d, Intersection.CONTAINS);
        
        
        Selection<NodeObject> sel = getSelection();
        PathUtil.removeHiddenKnots(pickKnot, sel, node);

        CyMatrix4d g2w = servPath.getGraphToWorldXform();
        CyMatrix4d g2d = getWorldToDevice(null);
        g2d.mul(g2w);
        
        if (!pickKnot.isEmpty())
        {
            pickKnot = PathUtil.getSelKnots(pickKnot.get(0),
                    sel, node, handles);
            dragSet = new PathDragSetKnot(servPath, handles, g2d, pickKnot);
        }
        else if (!pickVert.isEmpty())
        {
            pickVert = PathUtil.getSelVertices(pickVert.get(0),
                    sel, node, handles);
            dragSet = new PathDragSetVertex(servPath, handles, g2d, pickVert);
        }
        else if (!pickEdge.isEmpty())
        {
            NetworkHandleEdge refEdge = pickEdge.get(0);
            
            pickEdge = PathUtil.getSelEdges(pickEdge.get(0),
                    sel, node, handles);
            dragSet = new PathDragSetEdge(servPath, handles, g2d, 
                    evt.getX(), evt.getY(),
                    refEdge, pickEdge);
        }
        else
        {
            dragSelRect = true;
        }
            
        
        mouseCur = mouseStart = evt;
    }

    @Override
    protected void dragTo(MouseEvent evt)
    {
        mouseCur = evt;
        
        if (dragSet != null)
        {
            dragSet.dragBy(evt.getX() - mouseStart.getX(),
                    evt.getY() - mouseStart.getY(), false);
        }
    }

    @Override
    protected void endDrag(MouseEvent evt)
    {
        if (dragSet != null)
        {
            dragSet.dragBy(evt.getX() - mouseStart.getX(),
                    evt.getY() - mouseStart.getY(), true);
            dragSet = null;
        }
        
        if (dragSelRect)
        {
            //Find components to select
            int x0 = Math.min(mouseStart.getX(), evt.getX());
            int x1 = Math.max(mouseStart.getX(), evt.getX());
            int y0 = Math.min(mouseStart.getY(), evt.getY());
            int y1 = Math.max(mouseStart.getY(), evt.getY());
            
            CyRectangle2d region = new CyRectangle2d(x0, y0, x1 - x0, y1 - y0);
        
            CyMatrix4d l2w = servPath.getLocalToWorldTransform((CyMatrix4d)null);
            CyMatrix4d l2d = getWorldToDevice(null);
            l2d.mul(l2w);
            
            PathUtil.adjustSelection(region, 
                    getSelectType(evt), Intersection.INTERSECTS,
                    getSelection(), node, getPathHandles(), l2w, l2d);
            dragSelRect = false;
        }
        
        mouseCur = null;
        mouseStart = null;
    }

    @Override
    public void keyPressed(KeyEvent evt)
    {
        switch (evt.getKeyCode())
        {
            case KeyEvent.VK_ESCAPE:
                cancel();
                return;
            case KeyEvent.VK_DELETE:
                deleteSelection();
                return;
        }
        
        super.keyPressed(evt);
    }

    @Override
    public void cancel()
    {
        if (dragSet != null)
        {
            dragSet.dragBy(0, 0, false);
            dragSet = null;
        }

        if (dragSelRect)
        {
            dragSelRect = false;
        }
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void render(RenderContext ctx)
    {
        super.render(ctx);

        CyDrawStack stack = ctx.getDrawStack();

        PathUtil.drawGraph(stack, getPathHandles(), 
                getSelection(), node, servPath.getGraphToWorldXform());
        
        if (dragSelRect)
        {
            int x0 = mouseStart.getX();
            int y0 = mouseStart.getY();
            int x1 = mouseCur.getX();
            int y1 = mouseCur.getY();
            
            drawMarquisRect(stack, x0, y0, x1, y1);
        }
    }

    private void deleteSelection()
    {
        Selection<NodeObject> sel = getSelection();
        
        NetworkHandleSelection subSel = 
                sel.getSubselection(node, NetworkHandleSelection.class);
        
        if (subSel == null)
        {
            return;
        }
        
        NetworkPathHandles oldHandles = getPathHandles();
        NetworkPath newPath = new NetworkPath(oldHandles.getPath());
        NetworkPathHandles newHandles = new NetworkPathHandles(newPath);
        
        if (subSel.getNumVertices() == 0
                && subSel.getNumEdges() == 0
                && subSel.getNumFaces() == 0)
        {
            return;
        }
        
        for (Integer i: subSel.getVertexIds())
        {
            HandleVertex h = newHandles.getVertexHandle(i);
            h.delete();
        }
        
        for (Integer i: subSel.getEdgeIds())
        {
            HandleEdge h = newHandles.getEdgeHandle(i);
            if (h != null)
            {
                h.remove();
            }
        }
        
        for (Integer i: subSel.getFaceIds())
        {
            HandleFace h = newHandles.getFaceHandle(i);
            if (h != null)
            {
                h.delete();
            }
        }
        
        servPath.setNetworkPath(newPath, true);
    }

    private void showPopupMenu(MouseEvent evt)
    {
        NetworkPathHandles oldHandles = getPathHandles();
        Selection<NodeObject> sel = getSelection();
        
        ArrayList<NetworkHandleVertex> vertList = 
                PathUtil.getSelVertices(null, sel, node, oldHandles);
        ArrayList<NetworkHandleVertex> vertListNew = new ArrayList<NetworkHandleVertex>();

        NetworkPath oldPath = getPath();
        NetworkPath newPath = new NetworkPath(oldPath);
        NetworkPathHandles newHandles = new NetworkPathHandles(newPath);
        
        
        boolean hasAutoSmooth = false;
        boolean hasSmooth = false;
        boolean hasFree = false;
        boolean hasCorner = false;
        for (NetworkHandleVertex v: vertList)
        {
            vertListNew.add(newHandles.getVertexHandle(v.getIndex()));
            
            for (NetworkHandleEdge e0: v.getInputEdges())
            {
                switch (e0.getSmooth1())
                {
                    case AUTO_SMOOTH:
                        hasAutoSmooth = true;
                        break;
                    case SMOOTH:
                        hasSmooth = true;
                        break;
                    case CORNER:
                        hasCorner = true;
                        break;
                    case FREE:
                        hasFree = true;
                        break;
                }
            }
            for (NetworkHandleEdge e0: v.getOutputEdges())
            {
                switch (e0.getSmooth0())
                {
                    case AUTO_SMOOTH:
                        hasAutoSmooth = true;
                        break;
                    case SMOOTH:
                        hasSmooth = true;
                        break;
                    case CORNER:
                        hasCorner = true;
                        break;
                    case FREE:
                        hasFree = true;
                        break;
                }
            }
        }

        JPopupMenu menu = new JPopupMenu();
        menu.add(new JCheckBoxMenuItem(
                new ActionSmooth(newHandles, vertListNew,
                BezierVertexSmooth.CORNER, hasCorner)));
        menu.add(new JCheckBoxMenuItem(
                new ActionSmooth(newHandles, vertListNew,
                BezierVertexSmooth.SMOOTH, hasSmooth)));
        menu.add(new JCheckBoxMenuItem(
                new ActionSmooth(newHandles, vertListNew,
                BezierVertexSmooth.AUTO_SMOOTH, hasAutoSmooth)));
        menu.add(new JCheckBoxMenuItem(
                new ActionSmooth(newHandles, vertListNew,
                BezierVertexSmooth.FREE, hasFree)));
        
        menu.show(evt.getComponent(), evt.getX(), evt.getY());
    }
    
    //-------------------------------------------
    
    class ActionSmooth extends AbstractAction
    {
        NetworkPathHandles handles;
        ArrayList<NetworkHandleVertex> vertList;
        BezierVertexSmooth smoothing;

        private ActionSmooth(NetworkPathHandles handles, 
                ArrayList<NetworkHandleVertex> vertList,
                BezierVertexSmooth smoothing,
                boolean selected)
        {
            super(smoothing.name());
            putValue(Action.SELECTED_KEY, selected);
            this.handles = handles;
            this.vertList = vertList;
            this.smoothing = smoothing;
        }

        @Override
        public void actionPerformed(ActionEvent evt)
        {
            for (NetworkHandleVertex v: vertList)
            {
                for (NetworkHandleEdge e: v.getInputEdges())
                {
                    e.setSmooth1(smoothing);
                }
                for (NetworkHandleEdge e: v.getOutputEdges())
                {
                    e.setSmooth0(smoothing);
                }
            }
            NetworkPath path = handles.getPath();
            servPath.setNetworkPath(path, true);
        }
    }

    
}
