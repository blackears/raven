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

package com.kitfox.coyote.shape.bezier.mesh2;

import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class CutGraph<EdgeData>
{
    HashMap<Coord, CutVertex> vertMap = new HashMap<Coord, CutVertex>();

    private CutGraph()
    {
    }
    
    public static CutFaces.FaceLoop createFaces(ArrayList<CutSegment> segsToInsert)
    {
        CutGraph graph = new CutGraph();
        graph.addSegments(segsToInsert);
        return graph.buildFaces();
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
            for (CutSegment seg: vtx.segOut)
            {
                segList.add(seg);
            }
        }
        
        return segList;
    }
    
    public void addSegments(ArrayList<CutSegment> segsToInsert)
    {
        ArrayList<CutSegment> graphSegs = getSegments();

        while (!segsToInsert.isEmpty())
        {
            CutSegment cur = segsToInsert.remove(segsToInsert.size() - 1);
            
            NEWSEGS:
            for (int i = 0; i < graphSegs.size(); ++i)
            {
                CutSegment oth = graphSegs.get(i);
                CutRecord rec = cur.cutAgainst(oth);
                if (rec != null)
                {
                    if (rec.splitOth != null)
                    {
                        removeSegment(oth);
                        
                        graphSegs.remove(i);
                        segsToInsert.addAll(Arrays.asList(rec.splitOth));
                        --i;
                    }
                    if (rec.splitCur != null)
                    {
                        segsToInsert.addAll(Arrays.asList(rec.splitCur));
                        continue NEWSEGS;
                    }
                }
            }
            
            //We only had vertex-vertex intersections.  Add segment to graph
            CutVertex v0 = getOrCreateVertex(cur.c0);
            v0.segOut.add(cur);
            CutVertex v1 = getOrCreateVertex(cur.c1);
            v1.segIn.add(cur);
            graphSegs.add(cur);
        }
    }

    private void removeSegment(CutSegment seg)
    {
        CutVertex v0 = vertMap.get(seg.c0);
        v0.segOut.remove(seg);
        
        CutVertex v1 = vertMap.get(seg.c1);
        v1.segIn.remove(seg);
    }

    public CutFaces.FaceLoop buildFaces()
    {
        ArrayList<CutSegment> segList = new ArrayList<CutSegment>();
        
        for (CutVertex vtx: vertMap.values())
        {
            for (CutSegment seg: vtx.segOut)
            {
                segList.add(seg);
            }
        }
        
        //Remove duplicate segments that span two points that are already
        // connected
        Collections.sort(segList, new SortSegs());
        
        for (int i = 0; i < segList.size() - 1; ++i)
        {
            CutSegment s0 = segList.get(i);
            CutSegment s1 = segList.get(i + 1);
            
            if (getMinCoord(s0).equals(getMinCoord(s1)) &&
                    getMaxCoord(s0).equals(getMaxCoord(s1)))
            {
                segList.remove(i + 1);
            }
        }
        
        //Build faces
        CutFaces.FaceLoop tree = CutFaces.buildFaces(segList);
        return tree;
    }
    
    private Coord getMinCoord(CutSegment seg)
    {
        return seg.c0.compareTo(seg.c1) < 0 ? seg.c0 : seg.c1;
    }

    private Coord getMaxCoord(CutSegment seg)
    {
        return seg.c0.compareTo(seg.c1) < 0 ? seg.c1 : seg.c0;
    }
        
    
    //-------------------------------
    class SortSegs implements Comparator<CutSegment>
    {
        @Override
        public int compare(CutSegment o1, CutSegment o2)
        {
            int comp = getMinCoord(o1).compareTo(getMinCoord(o2));
            if (comp != 0)
            {
                return comp;
            }
            return getMaxCoord(o1).compareTo(getMaxCoord(o2));
        }
    }
}
