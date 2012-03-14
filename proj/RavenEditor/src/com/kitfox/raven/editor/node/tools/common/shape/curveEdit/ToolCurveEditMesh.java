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

import com.kitfox.coyote.material.color.CyMaterialColorDrawRecord;
import com.kitfox.coyote.material.color.CyMaterialColorDrawRecordFactory;
import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.coyote.renderer.vertex.CyVertexBufferDataSquare;
import com.kitfox.coyote.renderer.vertex.CyVertexBufferDataSquareLines;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.coyote.shape.ShapeLinesProvider;
import com.kitfox.coyote.shape.bezier.BezierCurve2d;
import com.kitfox.coyote.shape.bezier.mesh.BezierVertexSmooth;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import com.kitfox.raven.editor.node.scene.RavenNodeRoot;
import com.kitfox.raven.editor.node.scene.RenderContext;
import com.kitfox.raven.editor.node.tools.common.pen.ServiceBezierMesh;
import com.kitfox.raven.shape.network.NetworkMesh;
import com.kitfox.raven.shape.network.pick.*;
import com.kitfox.raven.util.Intersection;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.tree.NodeObject;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author kitfox
 */
public class ToolCurveEditMesh extends ToolCurveEditDelegate
{
    NodeObject node;
    final ServiceBezierMesh servMesh;
    NetworkMeshHandles cacheHandles;

    private MouseEvent mouseStart;
    private MouseEvent mouseCur;
    
    boolean dragSelRect;
    MeshDragSet dragSet;
    
    protected ToolCurveEditMesh(ToolCurveEditDispatch dispatch, 
            NodeObject node,
            ServiceBezierMesh servMesh)
    {
        super(dispatch);
        this.servMesh = servMesh;
        this.node = node;
    }

    private NetworkMeshHandles getMeshHandles()
    {
        NetworkMesh mesh = getMesh();
        if (cacheHandles == null || cacheHandles.getMesh() != mesh)
        {
            cacheHandles = mesh == null ? null : new NetworkMeshHandles(mesh);
        }
        return cacheHandles;
    }
    
    private NetworkMesh getMesh()
    {
        return servMesh.getNetworkMesh();
    }
    
//    private void setMesh(NetworkMesh mesh, boolean history)
//    {
//        servMesh.setNetworkMesh(mesh, history);
//    }

    protected CyVector2d xformDev2MeshPoint(CyVector2d v, boolean snapGrid)
    {
        v = xformDev2LocalPoint(v, snapGrid);
        v.scale(100);
        return v;
    }

    private ArrayList<NetworkHandleVertex> getSelVertices(NetworkHandleVertex v)
    {
        ArrayList<NetworkHandleVertex> list = new ArrayList<NetworkHandleVertex>();
        
        Selection<NodeObject> sel = getSelection();
        NetworkHandleSelection subSel = 
                sel.getSubselection(node, NetworkHandleSelection.class);

        if (v == null || subSel.containsVertex(v.getIndex()))
        {
            NetworkMeshHandles handles = getMeshHandles();
            
            for (Integer i: subSel.getVertexIds())
            {
                list.add(handles.getVertexHandle(i));
            }
        }
        else
        {
            list.add(v);
        }
        return list;
    }

    private ArrayList<NetworkHandleEdge> getSelEdges(NetworkHandleEdge e)
    {
        ArrayList<NetworkHandleEdge> list = new ArrayList<NetworkHandleEdge>();
        
        Selection<NodeObject> sel = getSelection();
        NetworkHandleSelection subSel = 
                sel.getSubselection(node, NetworkHandleSelection.class);

        if (e == null || subSel.containsEdge(e.getIndex()))
        {
            NetworkMeshHandles handles = getMeshHandles();
            
            for (Integer i: subSel.getEdgeIds())
            {
                list.add(handles.getEdgeHandle(i));
            }
        }
        else
        {
            list.add(e);
        }
        return list;
    }

    private ArrayList<NetworkHandleKnot> getSelKnots(NetworkHandleKnot k)
    {
        ArrayList<NetworkHandleKnot> list = new ArrayList<NetworkHandleKnot>();
        
        Selection<NodeObject> sel = getSelection();
        NetworkHandleSelection subSel = 
                sel.getSubselection(node, NetworkHandleSelection.class);

        if (k == null || subSel.containsKnot(k.getIndex()))
        {
            NetworkMeshHandles handles = getMeshHandles();
            
            for (Integer i: subSel.getKnotIds())
            {
                list.add(handles.getKnotHandle(i));
            }
        }
        else
        {
            list.add(k);
        }
        return list;
    }
    
    private void adjustSelection(CyRectangle2d region, Selection.Operator op,
            Intersection isect)
    {
        CyMatrix4d l2w = servMesh.getLocalToWorldTransform((CyMatrix4d)null);
        CyMatrix4d l2d = getWorldToDevice(null);
        l2d.mul(l2w);
        
        NetworkMeshHandles handles = getMeshHandles();
        
        ArrayList<NetworkHandleVertex> pickVert =
                handles.pickVertices(region, l2d, Intersection.CONTAINS);
        ArrayList<NetworkHandleEdge> pickEdge =
                handles.pickEdges(region, l2d, isect);
        ArrayList<NetworkHandleFace> pickFace =
                handles.pickFaces(region, l2d, isect);
        ArrayList<NetworkHandleKnot> pickKnot =
                handles.pickKnots(region, l2d, Intersection.CONTAINS);
        removeHiddenKnots(pickKnot);

        Selection<NodeObject> sel = getSelection();
        NetworkHandleSelection subSel = 
                sel.getSubselection(node, NetworkHandleSelection.class);

        NetworkHandleSelection subSelNew = null;
                
        if (!pickVert.isEmpty())
        {
            subSelNew = subSel == null 
                    ? new NetworkHandleSelection()
                    : new NetworkHandleSelection(subSel);
            
            ArrayList<Integer> idList = new ArrayList<Integer>();
            for (NetworkHandleVertex v: pickVert)
            {
                idList.add(v.getIndex());
            }
            subSelNew.selectVertices(idList, op);
        }
        else if (!pickKnot.isEmpty())
        {
            subSelNew = subSel == null 
                    ? new NetworkHandleSelection()
                    : new NetworkHandleSelection(subSel);
            
            ArrayList<Integer> idList = new ArrayList<Integer>();
            for (NetworkHandleKnot k: pickKnot)
            {
                idList.add(k.getIndex());
            }
            subSelNew.selectKnots(idList, op);
        }
        else if (!pickEdge.isEmpty())
        {
            subSelNew = subSel == null 
                    ? new NetworkHandleSelection()
                    : new NetworkHandleSelection(subSel);
            
            ArrayList<Integer> idList = new ArrayList<Integer>();
            for (NetworkHandleEdge e: pickEdge)
            {
                idList.add(e.getIndex());
            }
            subSelNew.selectEdges(idList, op);
        }
        else if (!pickFace.isEmpty())
        {
            subSelNew = subSel == null 
                    ? new NetworkHandleSelection()
                    : new NetworkHandleSelection(subSel);
            
            ArrayList<Integer> idList = new ArrayList<Integer>();
            for (NetworkHandleFace e: pickFace)
            {
                idList.add(e.getIndex());
            }
            subSelNew.selectFaces(idList, op);
        }
        
        if (subSelNew != null)
        {
            sel.setSubselection(node, NetworkHandleSelection.class, subSelNew);
        }
    }
    
    @Override
    protected void click(MouseEvent evt)
    {
        RavenNodeRoot root = getDocument();
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
        
        adjustSelection(region, getSelectType(evt), Intersection.INTERSECTS);
    }

    @Override
    protected void startDrag(MouseEvent evt)
    {
        RavenNodeRoot root = getDocument();
        if (root == null)
        {
            return;
        }

        float pickRad = root == null ? 1 : root.getGraphRadiusPick();
        CyRectangle2d region = new CyRectangle2d(evt.getX() - pickRad, evt.getY() - pickRad, 
                pickRad * 2, pickRad * 2);
        
        NetworkMeshHandles handles = getMeshHandles();
        CyMatrix4d l2w = servMesh.getLocalToWorldTransform((CyMatrix4d)null);
        CyMatrix4d l2d = getWorldToDevice(null);
        l2d.mul(l2w);
        
        ArrayList<NetworkHandleVertex> pickVert =
                handles.pickVertices(region, l2d, Intersection.CONTAINS);
        ArrayList<NetworkHandleEdge> pickEdge =
                handles.pickEdges(region, l2d, Intersection.INTERSECTS);
        ArrayList<NetworkHandleKnot> pickKnot =
                handles.pickKnots(region, l2d, Intersection.CONTAINS);
        removeHiddenKnots(pickKnot);

        CyMatrix4d g2w = servMesh.getGraphToWorldXform();
        CyMatrix4d g2d = getWorldToDevice(null);
        g2d.mul(g2w);
        
        if (!pickKnot.isEmpty())
        {
            pickKnot = getSelKnots(pickKnot.get(0));
            dragSet = new MeshDragSetKnot(servMesh, handles, g2d, pickKnot);
        }
        else if (!pickVert.isEmpty())
        {
            pickVert = getSelVertices(pickVert.get(0));
            dragSet = new MeshDragSetVertex(servMesh, handles, g2d, pickVert);
        }
        else if (!pickEdge.isEmpty())
        {
            pickEdge = getSelEdges(pickEdge.get(0));
            dragSet = new MeshDragSetEdge(servMesh, handles, g2d, pickEdge);
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

            adjustSelection(region, getSelectType(evt), Intersection.INTERSECTS);
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

        MeshRenderUtil.drawGraph(stack, getMeshHandles(), 
                getSelection(), node, servMesh.getGraphToWorldXform());
//        drawGraph(stack);
        
        if (dragSelRect)
        {
            int x0 = mouseStart.getX();
            int y0 = mouseStart.getY();
            int x1 = mouseCur.getX();
            int y1 = mouseCur.getY();
            
            drawMarquisRect(stack, x0, y0, x1, y1);
        }
    }

    /**
     * Searches list and removes all entries that are not linked to either 
     * a selected vertex or a selected edge.
     * 
     * @param knotList 
     */
    private void removeHiddenKnots(ArrayList<NetworkHandleKnot> knotList)
    {
        Selection<NodeObject> sel = getSelection();
        
        NetworkHandleSelection subSel = 
                sel.getSubselection(node, NetworkHandleSelection.class);
        
        if (subSel == null)
        {
            knotList.clear();
            return;
        }
        
        for (Iterator<NetworkHandleKnot> it = knotList.iterator();
                it.hasNext();)
        {
            NetworkHandleKnot k = it.next();
            if (subSel.containsVertex(k.getVertex().getIndex())
                    || subSel.containsEdge(k.getEdge().getIndex()))
            {
                continue;
            }

            it.remove();
        }
    }

//    /**
//     * Get list of knots that are part of curved lines and for which either
//     * their parent vertex or edge is selected.
//     * 
//     * @return 
//     */
//    private ArrayList<? extends NetworkHandleKnot> getVisibleKnots()
//    {
//        Selection<NodeObject> sel = getSelection();
//        
//        NetworkHandleSelection subSel = 
//                sel.getSubselection(node, NetworkHandleSelection.class);
//        
//        if (subSel == null)
//        {
//            return new ArrayList<NetworkHandleKnot>();
//        }
//        
//        NetworkMeshHandles handles = getMeshHandles();
//        ArrayList<? extends NetworkHandleKnot> knotList = handles.getKnotList();
//        
//        //Remove knots not attached to something selected
//        for (Iterator<? extends NetworkHandleKnot> it = knotList.iterator();
//                it.hasNext();)
//        {
//            NetworkHandleKnot knot = it.next();
//            if (subSel.containsEdge(knot.getEdge().getIndex())
//                    || subSel.containsVertex(knot.getVertex().getIndex()))
//            {
//                continue;
//            }
//
//            it.remove();
//        }
//
//        return knotList;
//    }
//    
//    private void drawGraph(CyDrawStack stack)
//    {
//        NetworkMeshHandles handles = getMeshHandles();
//        
//        ArrayList<? extends NetworkHandleEdge> edgeList =
//                handles.getEdgeList();
//        ArrayList<? extends NetworkHandleVertex> vertList =
//                handles.getVertList();
//        ArrayList<? extends NetworkHandleKnot> knotList =
//                getVisibleKnots();
//        
////        CyMatrix4d g2w = serv.getGraphToWorldXform();
//        CyMatrix4d g2w = servMesh.getGraphToWorldXform();
//
//        Selection<NodeObject> sel = getSelection();
//        
//        NetworkHandleSelection subSel = 
//                sel.getSubselection(node, NetworkHandleSelection.class);
////                sel.getSubselection(node, NetworkHandleSelection.class);
//        
//        RavenNodeRoot root = getDocument();
//        CyMatrix4d w2d = stack.getViewXform();
//        CyMatrix4d d2p = stack.getProjXform();
//        
//        CyMatrix4d g2p = new CyMatrix4d(d2p);
//        g2p.mul(w2d);
//        g2p.mul(g2w);
//        
//        CyVector2d pt = new CyVector2d();
//        CyMatrix4d v2p = new CyMatrix4d();
//        float radDisp = root.getGraphRadiusDisplay();
//        CyVertexBuffer bufSquare = CyVertexBufferDataSquare.inst().getBuffer();
//        CyVertexBuffer bufSquareLines = CyVertexBufferDataSquareLines.inst().getBuffer();
//        CyMatrix4d g2d = new CyMatrix4d(w2d);
//        g2d.mul(g2w);
//        
//        
//        //Draw visible knots
//        CyPath2d pathKnot = new CyPath2d();
//        for (NetworkHandleKnot k: knotList)
//        {
//            Coord c0 = k.getVertex().getCoord();
//            Coord c1 = k.getCoord();
//            pathKnot.moveTo(c0.x, c0.y);
//            pathKnot.lineTo(c1.x, c1.y);
//        }
//
//        if (!pathKnot.isEmpty())
//        {
//            ShapeLinesProvider lines = new ShapeLinesProvider(pathKnot);
//            CyVertexBuffer buf = new CyVertexBuffer(lines);
//            drawShape(stack, buf, g2p, 
//                    root.getGraphColorEdge().asColor());
//        }
//        
//        for (NetworkHandleKnot k: knotList)
//        {
//            Coord c = k.getCoord();
//            pt.set(c.x, c.y);
//            g2d.transformPoint(pt);
//            
//            v2p.set(d2p);
//            v2p.translate(pt.x, pt.y, 0);
//            v2p.scale(radDisp * 2, radDisp * 2, 1);
//            v2p.translate(-.5, -.5, 0);
//
//            if (subSel != null && subSel.containsKnot(k.getIndex()))
//            {
//                drawShape(stack, bufSquare, v2p, 
//                    root.getGraphColorVertSelect().asColor());
//                drawShape(stack, bufSquareLines, v2p, 
//                    root.getGraphColorEdge().asColor());
//            }
//            else
//            {
//                drawShape(stack, bufSquare, v2p, 
//                    root.getGraphColorVert().asColor());
//                drawShape(stack, bufSquareLines, v2p, 
//                    root.getGraphColorEdge().asColor());
//            }
//        }
//        
//        //Draw paths
//        CyPath2d pathUnsel = new CyPath2d();
//        CyPath2d pathSel = new CyPath2d();
//        for (NetworkHandleEdge e: edgeList)
//        {
//            BezierCurve2d c = e.getCurveLocal();
//            
//            if (subSel != null && subSel.containsEdge(e.getIndex()))
//            {
//                pathSel.moveTo(c.getStartX(), c.getStartY());
//                c.append(pathSel);
//            }
//            else
//            {
//                pathUnsel.moveTo(c.getStartX(), c.getStartY());
//                c.append(pathUnsel);
//            }
//        }
//        
//        if (!pathUnsel.isEmpty())
//        {
//            ShapeLinesProvider lines = new ShapeLinesProvider(pathUnsel);
//            CyVertexBuffer buf = new CyVertexBuffer(lines);
//            drawShape(stack, buf, g2p, 
//                    root.getGraphColorEdge().asColor());
//        }
//        if (!pathSel.isEmpty())
//        {
//            ShapeLinesProvider lines = new ShapeLinesProvider(pathSel);
//            CyVertexBuffer buf = new CyVertexBuffer(lines);
//            drawShape(stack, buf, g2p, 
//                    root.getGraphColorEdgeSelect().asColor());
//        }
//        
//        //Draw verts
//        for (NetworkHandleVertex v: vertList)
//        {
//            Coord c = v.getCoord();
//            pt.set(c.x, c.y);
//            g2d.transformPoint(pt);
//            
//            v2p.set(d2p);
//            v2p.translate(pt.x, pt.y, 0);
//            v2p.scale(radDisp * 2, radDisp * 2, 1);
//            v2p.translate(-.5, -.5, 0);
//            
//            if (subSel != null && subSel.containsVertex(v.getIndex()))
//            {
//                drawShape(stack, bufSquare, v2p, 
//                    root.getGraphColorVertSelect().asColor());
//                drawShape(stack, bufSquareLines, v2p, 
//                    root.getGraphColorEdge().asColor());
//            }
//            else
//            {
//                drawShape(stack, bufSquare, v2p, 
//                    root.getGraphColorVert().asColor());
//                drawShape(stack, bufSquareLines, v2p, 
//                    root.getGraphColorEdge().asColor());
//            }
//        }
//    }
//    
//    private void drawShape(CyDrawStack stack, 
//            CyVertexBuffer buf, CyMatrix4d mvp, CyColor4f color)
//    {
//        CyMaterialColorDrawRecord rec = 
//                CyMaterialColorDrawRecordFactory.inst().allocRecord();
//
//        rec.setColor(color);
//
//        rec.setMesh(buf);
//
//        rec.setOpacity(1);
//
////        CyMatrix4d mvp = stack.getModelViewProjXform();
////        mvp.mul(mvp);
//
//
//        rec.setMvpMatrix(mvp);
//        
//        stack.addDrawRecord(rec);
//    }
    
    private void showPopupMenu(MouseEvent evt)
    {
//        ArrayList<NetworkHandleKnot> knotList = getSelKnots(null);
        ArrayList<NetworkHandleVertex> vertList = getSelVertices(null);
        ArrayList<NetworkHandleVertex> vertListNew = new ArrayList<NetworkHandleVertex>();

        NetworkMesh oldMesh = getMesh();
        NetworkMesh newMesh = new NetworkMesh(oldMesh);
        NetworkMeshHandles newHandles = new NetworkMeshHandles(newMesh);
        
        
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
        NetworkMeshHandles handles;
        ArrayList<NetworkHandleVertex> vertList;
        BezierVertexSmooth smoothing;

        private ActionSmooth(NetworkMeshHandles handles, 
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
            NetworkMesh mesh = handles.getMesh();
            servMesh.setNetworkMesh(mesh, true);
        }
    }
        
}
