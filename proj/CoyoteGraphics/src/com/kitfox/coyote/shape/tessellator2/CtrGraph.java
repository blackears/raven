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

/**
 *
 * @author kitfox
 */
public class CtrGraph
{
    HashMap<Coord, CtrVertex> vertMap = new HashMap<Coord, CtrVertex>();
    boolean windingDone;
    
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
        if (windingDone)
        {
            throw new IllegalStateException("Cannot add new segments once winding's been calculated");
        }
        
        CtrVertex v0 = getOrCreateVertex(c0);
        CtrVertex v1 = getOrCreateVertex(c1);
        
        CtrEdge e = new CtrEdge(v0, v1);
        
        v0.edges.add(e);
        v1.edges.add(e);
    }
    
    public void buildWinding()
    {
        //Sort radially
        for (CtrVertex v: vertMap.values())
        {
            v.sortRadial();
        }

        //Find bottommost point
        CtrVertex bottomVtx = null;
        for (CtrVertex v: vertMap.values())
        {
            if (bottomVtx == null ||
                    v.coord.y < bottomVtx.coord.y)
            {
                bottomVtx = v;
            }
        }

        //Get first CCW edge from bottom
        CtrEdge firstEdge = bottomVtx.edges.get(0);
        if (bottomVtx.isEdgeIn(firstEdge))
        {
            firstEdge.left.floodWinding(0);
        }
        else
        {
            firstEdge.right.floodWinding(0);
        }
        
        windingDone = true;
    }

    public ArrayList<CtrLoop> buildContours()
    {
        if (!windingDone)
        {
            buildWinding();
        }
        
        ArrayList<CtrHalfEdge> halfEdges = new ArrayList<CtrHalfEdge>();
        
        //Sort radially and collect edges
        for (CtrVertex v: vertMap.values())
        {
            for (CtrEdge e: v.edges)
            {
                if (v.isEdgeOut(e))
                {
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
            
            for (CtrHalfEdge e = firstEdge.nextLoopEdge();
                    e != firstEdge;
                    e = e.nextLoopEdge())
            {
                edgeList.add(e);
                halfEdges.remove(e);
            }

            loops.add(new CtrLoop(edgeList));
        }
        
        return loops;
    }


}
