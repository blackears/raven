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
import com.kitfox.coyote.shape.ShapeLinesProvider;
import com.kitfox.coyote.shape.bezier.BezierCurve2d;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import com.kitfox.raven.editor.node.scene.RavenNodeRoot;
import com.kitfox.raven.shape.network.pick.*;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.tree.NodeObject;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author kitfox
 */
public class MeshRenderUtil
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
        ArrayList<? extends NetworkHandleKnot> knotList =
                getVisibleKnots(sel, node, handles);
        
//        CyMatrix4d g2w = servMesh.getGraphToWorldXform();

        NetworkHandleSelection subSel = 
                sel.getSubselection(node, NetworkHandleSelection.class);
     
        RavenNodeRoot root = (RavenNodeRoot)node.getDocument();
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
    
}
