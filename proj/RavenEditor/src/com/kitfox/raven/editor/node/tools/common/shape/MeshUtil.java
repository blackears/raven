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

package com.kitfox.raven.editor.node.tools.common.shape;

import com.kitfox.coyote.material.color.CyMaterialColorDrawRecord;
import com.kitfox.coyote.material.color.CyMaterialColorDrawRecordFactory;
import com.kitfox.coyote.material.screen.CyMaterialScreenDrawRecord;
import com.kitfox.coyote.material.screen.CyMaterialScreenDrawRecordFactory;
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
import com.kitfox.raven.shape.network.pick.*;
import com.kitfox.raven.shape.network.pick.NetworkMeshHandles.HandleFace;
import com.kitfox.raven.util.Intersection;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.tree.NodeObject;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author kitfox
 */
public class MeshUtil
{
    /**
     * Get list of knots that are part of curved lines and for which either
     * their parent vertex or edge is selected.
     * 
     * @return 
     */
    public static ArrayList<? extends NetworkHandleKnot> getVisibleKnots(
            Selection<NodeObject> sel, NodeObject node, NetworkMeshHandles handles)
    {
        NetworkHandleSelection subSel = 
                sel.getSubselection(node, NetworkHandleSelection.class);
        
        if (subSel == null)
        {
            return new ArrayList<NetworkHandleKnot>();
        }
        
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
    
    public static void drawGraph(CyDrawStack stack, NetworkMeshHandles handles,
            Selection<NodeObject> sel, NodeObject node, CyMatrix4d g2w)
    {
        ArrayList<? extends NetworkHandleEdge> edgeList =
                handles.getEdgeList();
        ArrayList<? extends NetworkHandleVertex> vertList =
                handles.getVertList();
        ArrayList<HandleFace> faceList =
                handles.getFaceList();
        ArrayList<? extends NetworkHandleKnot> knotList =
                getVisibleKnots(sel, node, handles);
        
//        CyMatrix4d g2w = servMesh.getGraphToWorldXform();

        NetworkHandleSelection subSel = 
                sel.getSubselection(node, NetworkHandleSelection.class);
     
        RavenNodeRoot root = (RavenNodeRoot)node.getSymbol();
//        RavenNodeRoot root = getDocument();
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
        
        long millis = System.currentTimeMillis();
        boolean blinkOn = (((int)(millis / 250)) & 0x3) != 0;
        
        //Draw selected faces
        if (blinkOn && subSel != null)
        {
            for (HandleFace face: faceList)
            {
                if (!subSel.containsFace(face.getIndex()))
                {
                    continue;
                }

//                CyPath2d path = face.getPathGraph();
//
//                ShapeMeshProvider mesh = new ShapeMeshProvider(path);
//                CyVertexBuffer buf = new CyVertexBuffer(mesh);
                CyVertexBuffer buf = face.getMeshBuffer();
                
                drawScreen(stack, buf, g2d, g2p, 
                        root.getGraphColorVertSelect().asColor());
            }
        }
        
        
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
            BezierCurve2d c = e.getCurveLocal();
            
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
    
    private static void drawShape(CyDrawStack stack, 
            CyVertexBuffer buf, CyMatrix4d mvp, CyColor4f color)
    {
        CyMaterialColorDrawRecord rec = 
                CyMaterialColorDrawRecordFactory.inst().allocRecord();

        rec.setColor(color);

        rec.setMesh(buf);

        rec.setOpacity(1);

        rec.setMvpMatrix(mvp);
        
        stack.addDrawRecord(rec);
    }
    
    private static void drawScreen(CyDrawStack stack, 
            CyVertexBuffer buf, CyMatrix4d mv, CyMatrix4d mvp, CyColor4f color)
    {
        CyMaterialScreenDrawRecord rec = 
                CyMaterialScreenDrawRecordFactory.inst().allocRecord();

        rec.setColorFg(color);
        rec.setLineWidth(4);

        rec.setMesh(buf);

        rec.setOpacity(1);

        rec.setMvMatrix(mv);
        rec.setMvpMatrix(mvp);
        
        stack.addDrawRecord(rec);
    }

    public static ArrayList<NetworkHandleVertex> getSelVertices(
            NetworkHandleVertex v, 
            Selection<NodeObject> sel,
            NodeObject node, NetworkMeshHandles handles)
    {
        ArrayList<NetworkHandleVertex> list = new ArrayList<NetworkHandleVertex>();
        
        NetworkHandleSelection subSel = 
                sel.getSubselection(node, NetworkHandleSelection.class);

        if (subSel == null)
        {
            if (v != null)
            {
                list.add(v);
            }
            return list;
        }
        
        if (v == null || subSel.containsVertex(v.getIndex()))
        {
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

    public static ArrayList<NetworkHandleEdge> getSelEdges(
            NetworkHandleEdge e, 
            Selection<NodeObject> sel,
            NodeObject node, NetworkMeshHandles handles)
    {
        ArrayList<NetworkHandleEdge> list = new ArrayList<NetworkHandleEdge>();
        
        NetworkHandleSelection subSel = 
                sel.getSubselection(node, NetworkHandleSelection.class);

        if (subSel == null)
        {
            if (e != null)
            {
                list.add(e);
            }
            return list;
        }

        if (e == null || subSel.containsEdge(e.getIndex()))
        {
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

    public static ArrayList<NetworkHandleFace> getSelFaces(
            NetworkHandleFace f, 
            Selection<NodeObject> sel,
            NodeObject node, NetworkMeshHandles handles)
    {
        ArrayList<NetworkHandleFace> list = new ArrayList<NetworkHandleFace>();
        
        NetworkHandleSelection subSel = 
                sel.getSubselection(node, NetworkHandleSelection.class);

        if (subSel == null)
        {
            if (f != null)
            {
                list.add(f);
            }
            return list;
        }

        if (f == null || subSel.containsFace(f.getIndex()))
        {
            for (Integer i: subSel.getFaceIds())
            {
                list.add(handles.getFaceHandle(i));
            }
        }
        else
        {
            list.add(f);
        }
        return list;
    }

    public static ArrayList<NetworkHandleKnot> getSelKnots(
            NetworkHandleKnot k, 
            Selection<NodeObject> sel,
            NodeObject node, NetworkMeshHandles handles)
    {
        ArrayList<NetworkHandleKnot> list = new ArrayList<NetworkHandleKnot>();
        
        NetworkHandleSelection subSel = 
                sel.getSubselection(node, NetworkHandleSelection.class);

        if (subSel == null)
        {
            if (k != null)
            {
                list.add(k);
            }
            return list;
        }

        if (k == null || subSel.containsKnot(k.getIndex()))
        {
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
    
    /**
     * Searches list and removes all entries that are not linked to either 
     * a selected vertex or a selected edge.
     * 
     * @param knotList 
     */
    public static void removeHiddenKnots(
            ArrayList<NetworkHandleKnot> knotList, 
            Selection<NodeObject> sel, NodeObject node)
    {
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
    
    public static void adjustSelection(CyRectangle2d region, Selection.Operator op,
            Intersection isect,
            Selection<NodeObject> sel,
            NodeObject node, NetworkMeshHandles handles,
            CyMatrix4d l2w, CyMatrix4d l2d)
    {
//        CyMatrix4d l2w = servMesh.getLocalToWorldTransform((CyMatrix4d)null);
//        CyMatrix4d l2d = getWorldToDevice(null);
//        l2d.mul(l2w);
        
        ArrayList<NetworkHandleVertex> pickVert =
                handles.pickVertices(region, l2d, Intersection.CONTAINS);
        ArrayList<NetworkHandleEdge> pickEdge =
                handles.pickEdges(region, l2d, isect);
        ArrayList<NetworkHandleFace> pickFace =
                handles.pickFaces(region, l2d, isect);
        ArrayList<NetworkHandleKnot> pickKnot =
                handles.pickKnots(region, l2d, Intersection.CONTAINS);

        NetworkHandleSelection subSel = 
                sel.getSubselection(node, NetworkHandleSelection.class);

        MeshUtil.removeHiddenKnots(pickKnot, sel, node);

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
        
}
