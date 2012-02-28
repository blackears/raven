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

package com.kitfox.coyote.shape.tessellator2;

import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class CtrGraph
{
    HashMap<Coord, CtrVertex> vertMap = new HashMap<Coord, CtrVertex>();
//    boolean windingDone;
    
    private CtrVertex getOrCreateVertex(Coord c)
    {
        CtrVertex v = vertMap.get(c);
        if (v == null)
        {
            v = new CtrVertex(c);
            vertMap.put(c, v);
        }
        return v;
    }
    
    public void addSegment(Coord c0, Coord c1)
    {
        CtrVertex v0 = getOrCreateVertex(c0);
        CtrVertex v1 = getOrCreateVertex(c1);
        
        //Check to see if this segment already exists
        for (CtrEdge e: v0.edges)
        {
            if (e.v0.coord.equals(c0) && e.v1.coord.equals(c1))
            {
                ++e.weight;
                return;
            }
            if (e.v0.coord.equals(c1) && e.v1.coord.equals(c0))
            {
                --e.weight;
                return;
            }
        }
        
        CtrEdge e = new CtrEdge(v0, v1);
        
        v0.edges.add(e);
        v1.edges.add(e);
        
//        if (windingDone)
//        {
//            throw new IllegalStateException("Cannot add new segments once winding's been calculated");
//        }
//        
//        CtrEdge e = new CtrEdge(v0, v1);
//        
//        v0.edges.add(e);
//        v1.edges.add(e);
    }
    
//    public void buildWinding()
//    {
//        //Sort radially
//        for (CtrVertex v: vertMap.values())
//        {
//            v.sortRadial();
//        }
//
//        //Find bottommost point
//        CtrVertex bottomVtx = null;
//        for (CtrVertex v: vertMap.values())
//        {
//            if (bottomVtx == null ||
//                    v.coord.y < bottomVtx.coord.y)
//            {
//                bottomVtx = v;
//            }
//        }
//
//        //Get first CCW edge from bottom
//        CtrEdge firstEdge = bottomVtx.edges.get(0);
//        if (bottomVtx.isEdgeIn(firstEdge))
//        {
//            firstEdge.left.floodWinding(0);
//        }
//        else
//        {
//            firstEdge.right.floodWinding(0);
//        }
//        
//        windingDone = true;
//    }

    public ArrayList<CtrLoop> buildContours()
    {
        ArrayList<CtrHalfEdge> halfEdges = new ArrayList<CtrHalfEdge>();
//        ArrayList<CtrEdge> edges = new ArrayList<CtrEdge>();
        
        //Sort radially and collect edges
        for (CtrVertex v: vertMap.values())
        {
            v.sortRadial();
            
            for (CtrEdge e: v.edges)
            {
                if (v.isEdgeOut(e))
                {
//                    edges.add(e);
                    halfEdges.add(e.left);
                    halfEdges.add(e.right);
                }
            }
        }
        
        //Extract loops from graph
        ArrayList<CtrLoop> loops = new ArrayList<CtrLoop>();
        while (!halfEdges.isEmpty())
        {
            CtrHalfEdge firstEdge = halfEdges.remove(halfEdges.size() - 1);
            //CtrLoop loop = new CtrLoop();
            ArrayList<CtrHalfEdge> edgeList = new ArrayList<CtrHalfEdge>();
            edgeList.add(firstEdge);
            
//            Coord cMin = firstEdge.getTailVert().coord;
            
            for (CtrHalfEdge e = firstEdge.nextLoopEdge();
                    e != firstEdge;
                    e = e.nextLoopEdge())
            {
                edgeList.add(e);
                
//                Coord cTail= e.getTailVert().coord;
  //              cMin = cMin.y < cTail.y ? cMin : cTail;
if (edgeList.size() == 1000)
{
Logger.getLogger(CtrGraph.class.getName()).log(Level.WARNING, 
        "Edge list getting suspiciously long");
}
                halfEdges.remove(e);
                
            }

//            int wind = calcWinding(cMin, edges);
            loops.add(new CtrLoop(edgeList));
        }

        //Calc winding
        calcWinding(loops);
        
        return loops;
    }

    private void calcWinding(ArrayList<CtrLoop> loops)
    {
        //Build map of edges to loops
        HashMap<CtrHalfEdge, CtrLoop> halfMap
                = new HashMap<CtrHalfEdge, CtrLoop>();
        ArrayList<CtrHalfEdge> halfList = new ArrayList<CtrHalfEdge>();
        for (CtrLoop loop: loops)
        {
            for (CtrHalfEdge half: loop.edgeList)
            {
                halfMap.put(half, loop);
                halfList.add(half);
            }
        }
        
        //Find start vert/edge
        CtrVertex bottomVert = null;
        for (CtrVertex v: vertMap.values())
        {
            if (bottomVert == null 
                    || v.coord.y < bottomVert.coord.y)
            {
                bottomVert = v;
            }
        }
        
        CtrEdge bottomEdge = bottomVert.edges.get(0);
        CtrHalfEdge bmHalf = bottomVert.isEdgeOut(bottomEdge)
                ? bottomEdge.right
                : bottomEdge.left;

        CtrLoop outsideLoop = halfMap.get(bmHalf);
        outsideLoop.setWinding(0);

        halfList.removeAll(outsideLoop.edgeList);
        
        while (!halfList.isEmpty())
        {
            for (int i = 0; i < halfList.size(); ++i)
            {
                CtrHalfEdge half = halfList.get(i);
                CtrHalfEdge peer = half.getPeer();
                
                CtrLoop loopHalf = halfMap.get(half);
                CtrLoop loopPeer = halfMap.get(peer);
                
                int peerWind = loopPeer.getWinding();
                
                if (peerWind != Integer.MIN_VALUE)
                {
                    int weight = half.right 
                            ? half.parent.weight
                            : -half.parent.weight;
                    loopHalf.setWinding(peerWind 
                            + weight);
                    halfList.removeAll(loopHalf.edgeList);
                    break;
                }
            }
        }
    }
    
//    public ArrayList<CtrLoop> buildContours_()
//    {
//        if (!windingDone)
//        {
//            buildWinding();
//        }
//        
//        ArrayList<CtrHalfEdge> halfEdges = new ArrayList<CtrHalfEdge>();
//        
//        //Sort radially and collect edges
//        for (CtrVertex v: vertMap.values())
//        {
//            v.sortRadial();
//            
//            for (CtrEdge e: v.edges)
//            {
//                if (v.isEdgeOut(e))
//                {
//                    halfEdges.add(e.left);
//                    halfEdges.add(e.right);
//                }
//            }
//        }
//        //Extract loops from graph
//        ArrayList<CtrLoop> loops = new ArrayList<CtrLoop>();
//        while (!halfEdges.isEmpty())
//        {
//            CtrHalfEdge firstEdge = halfEdges.remove(halfEdges.size() - 1);
//            //CtrLoop loop = new CtrLoop();
//            ArrayList<CtrHalfEdge> edgeList = new ArrayList<CtrHalfEdge>();
//            edgeList.add(firstEdge);
//            
//            for (CtrHalfEdge e = firstEdge.nextLoopEdge();
//                    e != firstEdge;
//                    e = e.nextLoopEdge())
//            {
//                edgeList.add(e);
//if (edgeList.size() > 1000)
//{
//Logger.getLogger(CyMaterialTextureBlit.class.getName()).log(Level.WARNING, 
//        "Edge list getting suspiciously long");
//}
//                halfEdges.remove(e);
//                
//            }
//
//            loops.add(new CtrLoop(edgeList));
//        }
//        
//        return loops;
//    }


}
