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

import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class CtrLoop
{
    ArrayList<CtrHalfEdge> edgeList;
    private int winding = Integer.MIN_VALUE;

    public CtrLoop(ArrayList<CtrHalfEdge> edgeList)
    {
        this.edgeList = edgeList;
    }
    
//    public int getWindingLevel()
//    {
////        return edgeList.get(0).winding;
//        return winding;
//    }

    public boolean isEmpty()
    {
        return edgeList.isEmpty();
    }
    
//    /**
//     * Remove all segments where parent edge does not border an
//     * area with a winding of 0.
//     */
//    public void removeInternalSegments()
//    {
//        NEXT_EDGE:
//        for (int i = 0; i < edgeList.size(); ++i)
//        {
//            CtrHalfEdge e0 = edgeList.get(i);
//            if (e0.isNextToZero())
//            {
//                continue;
//            }
//            
//            CtrVertex v0 = e0.getTailVert();
//            for (int j = 1; j < edgeList.size(); ++j)
//            {
//                int nextIdx = i + j >= edgeList.size()
//                        ? i + j - edgeList.size() : i + j;
//                CtrHalfEdge e1 = edgeList.get(nextIdx);
//                if (e1.isNextToZero())
//                {
//                    continue NEXT_EDGE;
//                }
//                
//                if (e1.getHeadVert() == v0)
//                {
//                    //We've found an entire sub-loop with all interal
//                    // edges
//                    ArrayList<CtrHalfEdge> newList = new ArrayList<CtrHalfEdge>(j + 1);
//                    if (i < nextIdx)
//                    {
//                        for (int k = 0; k < i; ++k)
//                        {
//                            newList.add(edgeList.get(k));
//                        }
//                        for (int k = nextIdx + 1; k < edgeList.size(); ++k)
//                        {
//                            newList.add(edgeList.get(k));
//                        }
//                        i = i - 1;
//                    }
//                    else
//                    {
//                        for (int k = nextIdx + 1; k < i; ++k)
//                        {
//                            newList.add(edgeList.get(k));
//                        }
//                        i = -1;
//                    }
//                    edgeList = newList;
//                }
//            }
//        }
//        
//        
////        for (Iterator<CtrHalfEdge> it = edgeList.iterator(); it.hasNext();)
////        {
////            CtrHalfEdge e = it.next();
////            if (e.parent.left.winding != 0
////                    && e.parent.right.winding != 0)
////            {
////                it.remove();
////            }
////        }
//    }
    
    public void buildTriangles(ArrayList<Coord> triList)
    {
        //Calc winding
        long area = 0;
        ArrayList<Coord> verts = new ArrayList<Coord>();
        
        {
            CtrHalfEdge edgeFirst = edgeList.get(0);
            Coord c0 = edgeFirst.getTailVert().coord;
            verts.add(c0);
            verts.add(edgeFirst.getHeadVert().coord);

            for (int i = 1; i < edgeList.size() - 1; ++i)
            {
                CtrHalfEdge e = edgeList.get(i);
                Coord c1 = e.getTailVert().coord;
                Coord c2 = e.getHeadVert().coord;
                verts.add(c2);
                area += Math2DUtil.cross(c1.x - c0.x, c1.y - c0.y, 
                        c2.x - c0.x, c2.y - c0.y);
            }
        }
        
        if (area == 0)
        {
            return;
        }
        boolean ccw = area > 0;
        
        //Remove verts which are in the middle of a straight line
        for (int i = 0; i < verts.size(); ++i)
        {
            int prevIdx = i == 0 ? verts.size() - 1 : i - 1;
            int nextIdx = i == verts.size() - 1 ? 0 : i + 1;
            
            Coord c0 = verts.get(prevIdx);
            Coord c1 = verts.get(i);
            Coord c2 = verts.get(nextIdx);
            
            int dx10 = c1.x - c0.x;
            int dy10 = c1.y - c0.y;
            int dx21 = c2.x - c1.x;
            int dy21 = c2.y - c1.y;

            if (dx10 * dx21 < 0 || dy10 * dy21 < 0)
            {
                //c1 is not within bounding box of [c0 c2]
                continue;
            }
            
            if (dx10 * dy21 != dx21 * dy10)
            {
                //Not colinear
                continue;
            }
            
            //Remove vert
            verts.remove(i);
            --i;
        }
        
        //Build tris
        while (verts.size() > 2)
        {
            int cacheSize = verts.size();
            
            NEXT_POINT:
            for (int i = 0; i < verts.size(); ++i)
            {
                int i0 = i == 0 ? verts.size() - 1 : i - 1;
                int i1 = i;
                int i2 = i == verts.size() - 1 ? 0 : i + 1;
                Coord c0 = verts.get(i0);
                Coord c1 = verts.get(i1);
                Coord c2 = verts.get(i2);
                
                int triArea = Math2DUtil.cross(c1.x - c0.x, c1.y - c0.y, 
                        c2.x - c0.x, c2.y - c0.y);
                boolean triCcw = triArea > 0;
                
                if (triCcw != ccw)
                {
                    //Concave
                    continue;
                }
                
                for (int j = 0; j < verts.size(); ++j)
                {
                    if (isInsideTrangle(c0, c1, c2, verts.get(j)))
                    {
                        continue NEXT_POINT;
                    }
                }
                
                verts.remove(i1);
                triList.add(c0);
                triList.add(c1);
                triList.add(c2);
                break;
            }
            
            if (cacheSize == verts.size())
            {
                //TODO: Went through entire list without finding any trianges
                // to export.  Perhaps face is self intersecting?
                return;
            }
        }
    }

    private boolean isInsideTrangle(Coord c0, Coord c1, Coord c2, Coord ct)
    {
        if (c0 == ct || c1 == ct || c2 == ct)
        {
            return false;
        }
        
        int s0 = Math2DUtil.getLineSide(
                c0.x, c0.y, c1.x - c0.x, c1.y - c0.y, 
                ct.x, ct.y);
        int s1 = Math2DUtil.getLineSide(
                c1.x, c1.y, c2.x - c1.x, c2.y - c1.y, 
                ct.x, ct.y);
        int s2 = Math2DUtil.getLineSide(
                c2.x, c2.y, c0.x - c2.x, c0.y - c2.y, 
                ct.x, ct.y);
        
        return (s0 > 0 && s1 > 0 && s2 > 0) 
                || (s0 < 0 && s1 < 0 && s2 < 0);
    }

    void dump(PrintStream ps)
    {
        for (CtrHalfEdge e: edgeList)
        {
            ps.println(e.toString());
        }
    }

    void dumpScilab(PrintStream ps)
    {
        ps.println("scf()");
        ps.print("data = [");
        {
            CtrHalfEdge e = edgeList.get(0);
            Coord c = e.getTailVert().coord;
            ps.print(c.x + " " + c.y);
        }
        
        for (CtrHalfEdge e: edgeList)
        {
            Coord c = e.getHeadVert().coord;
            ps.print(" ;" + c.x + " " + c.y);
        }
        ps.println("]");
        ps.println("plot(data(:, 1), data(:, 2))");
    }

    /**
     * @return the winding
     */
    public int getWinding()
    {
        return winding;
    }

    /**
     * @param winding the winding to set
     */
    public void setWinding(int winding)
    {
        this.winding = winding;
    }
}
