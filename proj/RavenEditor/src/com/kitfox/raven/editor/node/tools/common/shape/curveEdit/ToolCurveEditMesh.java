/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import com.kitfox.raven.editor.node.scene.RavenNodeRoot;
import com.kitfox.raven.editor.node.scene.RenderContext;
import com.kitfox.raven.editor.node.tools.common.pen.ServiceBezierMesh;
import com.kitfox.raven.editor.node.tools.common.shape.curveEdit.ToolCurveEdit.TanRecord;
import com.kitfox.raven.shape.network.NetworkMesh;
import com.kitfox.raven.shape.network.pick.*;
import com.kitfox.raven.util.Intersection;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.tree.NodeObject;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

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
    
    private void setMesh(NetworkMesh mesh, boolean history)
    {
        servMesh.setNetworkMesh(mesh, history);
    }

    protected CyVector2d xformDev2MeshPoint(CyVector2d v, boolean snapGrid)
    {
        v = xformDev2LocalPoint(v, snapGrid);
        v.scale(100);
        return v;
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
        
        if (pickVert.isEmpty() && pickEdge.isEmpty() && pickKnot.isEmpty())
        {
            dragSelRect = true;
        }
        
        
        mouseCur = mouseStart = evt;
    }

    @Override
    protected void dragTo(MouseEvent evt)
    {
        mouseCur = evt;
    }

    @Override
    protected void endDrag(MouseEvent evt)
    {
        if (dragSelRect)
        {
            //Find components to select
            int x0 = Math.min(mouseStart.getX(), mouseCur.getX());
            int x1 = Math.max(mouseStart.getX(), mouseCur.getX());
            int y0 = Math.min(mouseStart.getY(), mouseCur.getY());
            int y1 = Math.max(mouseStart.getY(), mouseCur.getY());
            
            CyRectangle2d region = new CyRectangle2d(x0, y0, x1 - x0, y1 - y0);

            adjustSelection(region, getSelectType(evt), Intersection.INTERSECTS);
        }
        
        mouseCur = null;
        mouseStart = null;
        dragSelRect = false;
    }

    @Override
    public void cancel()
    {
//        dispatch.delegateDone();
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
        
        drawGraph(stack);
        
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

    /**
     * Get list of knots that are part of curved lines and for which either
     * their parent vertex or edge is selected.
     * 
     * @return 
     */
    private ArrayList<? extends NetworkHandleKnot> getVisibleKnots()
    {
        Selection<NodeObject> sel = getSelection();
        
        NetworkHandleSelection subSel = 
                sel.getSubselection(node, NetworkHandleSelection.class);
        
        if (subSel == null)
        {
            return new ArrayList<NetworkHandleKnot>();
        }
        
        NetworkMeshHandles handles = getMeshHandles();
        ArrayList<? extends NetworkHandleKnot> knotList = handles.getKnotList();
        
        //Remove knots not attached to something selected
        for (Iterator<? extends NetworkHandleKnot> it = knotList.iterator();
                it.hasNext();)
        {
            NetworkHandleKnot knot = it.next();
            if (subSel.containsEdge(knot.getEdge().getIndex())
                    || subSel.containsVertex(knot.getVertex().getIndex()))
            {
                continue;
            }

            it.remove();
        }

        return knotList;
    }
    
    private void drawGraph(CyDrawStack stack)
    {
        NetworkMeshHandles handles = getMeshHandles();
        
        ArrayList<? extends NetworkHandleEdge> edgeList =
                handles.getEdgeList();
        ArrayList<? extends NetworkHandleVertex> vertList =
                handles.getVertList();
        ArrayList<? extends NetworkHandleKnot> knotList =
                getVisibleKnots();
        
//        CyMatrix4d g2w = serv.getGraphToWorldXform();
        CyMatrix4d g2w = servMesh.getGraphToWorldXform();

        Selection<NodeObject> sel = getSelection();
        
        NetworkHandleSelection subSel = 
                sel.getSubselection(node, NetworkHandleSelection.class);
//                sel.getSubselection(node, NetworkHandleSelection.class);
        
        RavenNodeRoot root = getDocument();
        CyMatrix4d w2d = stack.getViewXform();
        CyMatrix4d d2p = stack.getProjXform();
        
        CyMatrix4d g2p = new CyMatrix4d(d2p);
        g2p.mul(w2d);
        g2p.mul(g2w);
        
        CyVector2d pt = new CyVector2d();
        CyMatrix4d v2p = new CyMatrix4d();
        float radDisp = root.getGraphRadiusDisplay();
        CyVertexBuffer bufSquare = CyVertexBufferDataSquare.inst().getBuffer();
        CyVertexBuffer bufSquareLines = CyVertexBufferDataSquareLines.inst().getBuffer();
        CyMatrix4d g2d = new CyMatrix4d(w2d);
        g2d.mul(g2w);
        
        
        //Draw visible knots
        CyPath2d pathKnot = new CyPath2d();
        for (NetworkHandleKnot k: knotList)
        {
            Coord c0 = k.getVertex().getCoord();
            Coord c1 = k.getCoord();
            pathKnot.moveTo(c0.x, c0.y);
            pathKnot.lineTo(c1.x, c1.y);
        }

        if (!pathKnot.isEmpty())
        {
            ShapeLinesProvider lines = new ShapeLinesProvider(pathKnot);
            CyVertexBuffer buf = new CyVertexBuffer(lines);
            drawShape(stack, buf, g2p, 
                    root.getGraphColorEdge().asColor());
        }
        
        for (NetworkHandleKnot k: knotList)
        {
            Coord c = k.getCoord();
            pt.set(c.x, c.y);
            g2d.transformPoint(pt);
            
            v2p.set(d2p);
            v2p.translate(pt.x, pt.y, 0);
            v2p.scale(radDisp * 2, radDisp * 2, 1);
            v2p.translate(-.5, -.5, 0);

            if (subSel != null && subSel.containsKnot(k.getIndex()))
            {
                drawShape(stack, bufSquare, v2p, 
                    root.getGraphColorVertSelect().asColor());
                drawShape(stack, bufSquareLines, v2p, 
                    root.getGraphColorEdge().asColor());
            }
            else
            {
                drawShape(stack, bufSquare, v2p, 
                    root.getGraphColorVert().asColor());
                drawShape(stack, bufSquareLines, v2p, 
                    root.getGraphColorEdge().asColor());
            }
        }
        
        //Draw paths
        CyPath2d pathUnsel = new CyPath2d();
        CyPath2d pathSel = new CyPath2d();
        for (NetworkHandleEdge e: edgeList)
        {
            BezierCurve2d c = e.getCurve();
            
            if (subSel != null && subSel.containsEdge(e.getIndex()))
            {
                pathSel.moveTo(c.getStartX(), c.getStartY());
                c.append(pathSel);
            }
            else
            {
                pathUnsel.moveTo(c.getStartX(), c.getStartY());
                c.append(pathUnsel);
            }
        }
        
        if (!pathUnsel.isEmpty())
        {
            ShapeLinesProvider lines = new ShapeLinesProvider(pathUnsel);
            CyVertexBuffer buf = new CyVertexBuffer(lines);
            drawShape(stack, buf, g2p, 
                    root.getGraphColorEdge().asColor());
        }
        if (!pathSel.isEmpty())
        {
            ShapeLinesProvider lines = new ShapeLinesProvider(pathSel);
            CyVertexBuffer buf = new CyVertexBuffer(lines);
            drawShape(stack, buf, g2p, 
                    root.getGraphColorEdgeSelect().asColor());
        }
        
        //Draw verts
        for (NetworkHandleVertex v: vertList)
        {
            Coord c = v.getCoord();
            pt.set(c.x, c.y);
            g2d.transformPoint(pt);
            
            v2p.set(d2p);
            v2p.translate(pt.x, pt.y, 0);
            v2p.scale(radDisp * 2, radDisp * 2, 1);
            v2p.translate(-.5, -.5, 0);
            
            if (subSel != null && subSel.containsVertex(v.getIndex()))
            {
                drawShape(stack, bufSquare, v2p, 
                    root.getGraphColorVertSelect().asColor());
                drawShape(stack, bufSquareLines, v2p, 
                    root.getGraphColorEdge().asColor());
            }
            else
            {
                drawShape(stack, bufSquare, v2p, 
                    root.getGraphColorVert().asColor());
                drawShape(stack, bufSquareLines, v2p, 
                    root.getGraphColorEdge().asColor());
            }
        }
    }
    
    private void drawShape(CyDrawStack stack, 
            CyVertexBuffer buf, CyMatrix4d mvp, CyColor4f color)
    {
        CyMaterialColorDrawRecord rec = 
                CyMaterialColorDrawRecordFactory.inst().allocRecord();

        rec.setColor(color);

        rec.setMesh(buf);

        rec.setOpacity(1);

//        CyMatrix4d mvp = stack.getModelViewProjXform();
//        mvp.mul(mvp);


        rec.setMvpMatrix(mvp);
        
        stack.addDrawRecord(rec);
    }
        
}
