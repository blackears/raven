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
import java.util.Collections;
import java.util.Comparator;
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
        initGraph(segs);
    }
    
    public static CutLoop createFaces(ArrayList<CutSegment> segsToInsert)
    {
        CutGraph graph = new CutGraph(segsToInsert);
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
    
    private void initGraph(ArrayList<CutSegment> segs)
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
        
        //Load graph
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
                CutSegHalf h0 = new CutSegHalf(s.t0, s.t1, s.c0, s.c1, s.data);
                CutSegHalf h1 = new CutSegHalf(s.t1, s.t0, s.c1, s.c0, s.data);
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

    public CutLoop buildFaces()
    {
        ArrayList<CutSegHalf> segList = getSegmentsRadial();
        
        //Build loops
        ArrayList<CutLoop> loopList = new ArrayList<CutLoop>();
        while (!segList.isEmpty())
        {
            loopList.add(extractLoop(segList));
        }
        
        //Sort loops by size.  Since loops do not cross, parent loops 
        // will always have greater area than child loops
        Collections.sort(loopList);
        
        //Nest loops
        for (int i = loopList.size() - 1; i >= 1; --i)
        {
            CutLoop subLoop = loopList.get(i);
            
            for (int j = i - 1; j >= 0; --j)
            {
                CutLoop parentLoop = loopList.get(j);
                if (contains(parentLoop, subLoop))
                {
                    parentLoop.children.add(subLoop);
                    break;
                }
            }
        }
        
        return loopList.get(0);
    }

    private boolean contains(CutLoop parLoop, CutLoop subLoop)
    {
        if (parLoop.isCcw() == subLoop.isCcw())
        {
            //Exterior CW loops should encompass interior CCW loops
            //Hence two loops with the same winding cannot have a
            // parent/child relationship
            return false;
        }

        if (!parLoop.boundingBoxContains(subLoop))
        {
            //Optimization
            return false;
        }
        
        for (CutSegHalf seg: subLoop.segList)
        {
            Coord c = seg.c0;
            if (!parLoop.containsVertex(c))
            {
                //Since point is not common to both loops, can be used
                // for inside/outside test
                return parLoop.contains(c.x, c.y);
            }
        }

        //All verts of subloop are also verts of parent loop.
        // Since loops cannot overlap, must be contained in loop.
        return true;        
    }

    private CutLoop extractLoop(ArrayList<CutSegHalf> segList)
    {
        CutSegHalf initSeg = segList.get(0);
        
        ArrayList<CutSegHalf> loop = new ArrayList<CutSegHalf>();
        
        CutSegHalf curSeg = initSeg;
        do
        {
            loop.add(curSeg);
            
            CutVertex v = vertMap.get(curSeg.c1);
            curSeg = v.nextSegCW(curSeg.getPeer());
        } 
        while (curSeg != initSeg);
        
        segList.removeAll(loop);
        return new CutLoop(loop);
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
