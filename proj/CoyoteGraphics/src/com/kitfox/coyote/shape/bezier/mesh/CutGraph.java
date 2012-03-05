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

package com.kitfox.coyote.shape.bezier.mesh;

import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author kitfox
 */
public class CutGraph<EdgeData>
{
    HashMap<Coord, CutVertex> vertMap = new HashMap<Coord, CutVertex>();

    private CutGraph(ArrayList<CutSegment> segs)
    {
        connectGraph(segs);
        insertSegments(segs);
    }
    
    /**
     * Builds faces from the passed in list of segments.  
     * 
     * A list will be returned with loops indicating each face.  
     * All faces except one will be non-self intersecting, 
     * have CCW winding and a positive area.  The other will be
     * CW and have negative area - this loop is the bounding perimeter
     * of the graph and is the union of all the other faces.
     * 
     * @param segsToInsert
     * @return 
     */
    public static ArrayList<CutLoop> createFaces(ArrayList<CutSegment> segsToInsert)
    {
        CutGraph graph = new CutGraph(segsToInsert);
        return graph.buildFaces();
    }

    /**
     * Connect any disjoint groups of segments
     * 
     * @param segs 
     */
    private void connectGraph(ArrayList<CutSegment> segs)
    {
        HashMap<Coord, CutSegGroup> segMap = new HashMap<Coord, CutSegGroup>();
        ArrayList<CutSegGroup> groups = new ArrayList<CutSegGroup>(segMap.values());
        for (CutSegment seg: segs)
        {
            CutSegGroup g0 = segMap.get(seg.c0);
            CutSegGroup g1 = segMap.get(seg.c1);
            
            if (g0 == null && g1 == null)
            {
                g0 = new CutSegGroup();
                groups.add(g0);
                g0.addSeg(seg);
                segMap.put(seg.c0, g0);
                segMap.put(seg.c1, g0);
            }
            else if (g1 == null)
            {
                segMap.put(seg.c1, g0);
                g0.addSeg(seg);
            }
            else if (g0 == null)
            {
                segMap.put(seg.c0, g1);
                g1.addSeg(seg);
            }
            else if (g0 == g1)
            {
                g0.addSeg(seg);
            }
            else
            {
                //Connect groups by merging g1 into g0
                g0.addAll(g1);
                groups.remove(g1);
                for (Coord c: g1.coords)
                {
                    segMap.put(c, g0);
                }
            }
        }
        
        //While there are disjoint groups, connect them by inserting
        // segments with no data
        NEXT_GROUP:
        while (groups.size() > 1)
        {
            CutSegGroup g1 = groups.remove(groups.size() - 1);
            
            for (int i = 0; i < groups.size(); ++i)
            {
                CutSegGroup g0 = groups.get(i);
                
                for (Coord c1: g1.coords)
                {
                    for (Coord c0: g0.coords)
                    {
                        CutSegment sc = new CutSegment(0, 1, c1, c0, null);
                        if (!isCollision(segs, sc))
                        {
                            g0.addAll(g1);
                            g0.addSeg(sc);
                            segs.add(sc);
                            continue NEXT_GROUP;
                        }
                    }
                }
            }
        }
    }

    private boolean isCollision(ArrayList<CutSegment> segs, CutSegment s0)
    {
        for (int j = 0; j < segs.size(); ++j)
        {
            CutSegment s1 = segs.get(j);

            if (s0.collidesWith(s1))
            {
                return true;
            }
        }
        return false;
    }
    
    private CutVertex getOrCreateVertex(Coord c)
    {
        CutVertex v = vertMap.get(c);
        if (v == null)
        {
            v = new CutVertex(c);
            vertMap.put(c, v);
        }
        return v;
    }
    
    public ArrayList<CutSegment> getSegments()
    {
        ArrayList<CutSegment> segList = new ArrayList<CutSegment>();
        
        for (CutVertex vtx: vertMap.values())
        {
            segList.addAll(vtx.segOut);
        }
        
        return segList;
    }
    
    public ArrayList<CutSegHalf> getSegmentsRadial()
    {
        ArrayList<CutSegHalf> segList = new ArrayList<CutSegHalf>();
        
        for (CutVertex vtx: vertMap.values())
        {
            segList.addAll(vtx.segRadial);
        }
        
        return segList;
    }
    
    private void insertSegments(ArrayList<CutSegment> segs)
    {
        //Remove any segments that match or overlap at non-vertices
        for (int i = 0; i < segs.size(); ++i)
        {
            CutSegment s0 = segs.get(i);
            
            for (int j = i + 1; j < segs.size(); ++j)
            {
                CutSegment s1 = segs.get(j);
                
                if (s0.collidesWith(s1))
                {
                    segs.remove(j);
                    --j;
                }
            }
        }
        
        //Insert segs into graph
        for (int i = 0; i < segs.size(); ++i)
        {
            CutSegment s0 = segs.get(i);
            CutVertex v0 = getOrCreateVertex(s0.c0);
            CutVertex v1 = getOrCreateVertex(s0.c1);
            
            v0.segOut.add(s0);
            v1.segIn.add(s0);
        }
        
        //Remove peninsulas
        for (CutVertex v: vertMap.values())
        {
            CutVertex vCur = v;
            while (vCur.size() == 1)
            {
                if (vCur.segIn.isEmpty())
                {
                    CutSegment s = vCur.segOut.remove(0);
                    vCur = vertMap.get(s.c1);
                    vCur.segIn.remove(s);
                }
                else
                {
                    CutSegment s = vCur.segIn.remove(0);
                    vCur = vertMap.get(s.c0);
                    vCur.segOut.remove(s);
                }
            }
        }
        
        for (Iterator<CutVertex> it = vertMap.values().iterator(); it.hasNext();)
        {
            CutVertex v = it.next();
            if (v.isEmpty())
            {
                it.remove();
            }
        }
        
        //Create radial half segments
        for (Iterator<CutVertex> it = vertMap.values().iterator(); it.hasNext();)
        {
            CutVertex v0 = it.next();
            
            for (CutSegment s: v0.segOut)
            {
                CutSegHalf h0 = 
                        new CutSegHalf(s.t0, s.t1, s.c0, s.c1, s.edge, false);
                CutSegHalf h1 = 
                        new CutSegHalf(s.t1, s.t0, s.c1, s.c0, s.edge, true);
                h0.setPeer(h1);
                h1.setPeer(h0);

                CutVertex v1 = vertMap.get(s.c1);
                
                v0.segRadial.add(h0);
                v1.segRadial.add(h1);
            }
        }
        
        for (Iterator<CutVertex> it = vertMap.values().iterator(); it.hasNext();)
        {
            CutVertex v0 = it.next();
//if (v0.size() >= 3)
//{
//    int j = 9;
//}
            v0.sortRadial();
        }
    }

    public ArrayList<CutLoop> buildFaces()
    {
        ArrayList<CutLoop> faces = new ArrayList<CutLoop>();
        
        ArrayList<CutSegHalf> segList = getSegmentsRadial();
        
        while (!segList.isEmpty())
        {
            ArrayList<CutSegHalf> edges = new ArrayList<CutSegHalf>();
            
            CutSegHalf firstSeg = segList.remove(0);
            edges.add(firstSeg);
            
            for (CutSegHalf half = nextSeg(firstSeg);
                    half != firstSeg;
                    half = nextSeg(half))
            {
                edges.add(half);
                segList.remove(half);
            }
            
            faces.add(new CutLoop(edges));
        }
        
        return faces;
    }
    
    private CutSegHalf nextSeg(CutSegHalf s0)
    {
        CutVertex v = vertMap.get(s0.c1);
        return v.nextSegCW(s0.getPeer());
    }
}
