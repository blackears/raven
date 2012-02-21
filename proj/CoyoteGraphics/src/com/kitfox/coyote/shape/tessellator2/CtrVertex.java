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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 *
 * @author kitfox
 */
public class CtrVertex
{
    Coord coord;
    ArrayList<CtrEdge> edges = new ArrayList<CtrEdge>();

    public CtrVertex(Coord coord)
    {
        this.coord = coord;
    }

    void sortRadial()
    {
        RadialSort sort = new RadialSort();
        Collections.sort(edges, sort);
    }

    boolean isEdgeOut(CtrEdge e)
    {
        return e.v0.coord.equals(coord);
    }

    boolean isEdgeIn(CtrEdge e)
    {
        return e.v1.coord.equals(coord);
    }

    CtrEdge nextEdgeCW(CtrEdge parent)
    {
        int idx = edges.indexOf(parent);
        assert idx != -1;
        if (idx == 0)
        {
            return edges.get(edges.size() - 1);
        }
        return edges.get(idx - 1);
    }

    CtrEdge nextEdgeCCW(CtrEdge parent)
    {
        int idx = edges.indexOf(parent);
        assert idx != -1;
        if (idx == edges.size() - 1)
        {
            return edges.get(0);
        }
        return edges.get(idx + 1);
    }

    @Override
    public String toString()
    {
        return coord.toString();
    }

    void removeEdgesWindNonZero()
    {
        for (Iterator<CtrEdge> it = edges.iterator(); it.hasNext();)
        {
            CtrEdge e = it.next();
            if (e.left.winding != 0 && e.right.winding != 0)
            {
                it.remove();
            }
        }
    }

    boolean isEmpty()
    {
        return edges.isEmpty();
    }
    
    //---------------------
    
    /**
     * Sort line segments CCW around vertex center, with straight
     * downward coming first.
     */
    class RadialSort implements Comparator<CtrEdge>
    {

        @Override
        public int compare(CtrEdge e0, CtrEdge e1)
        {
            Coord c0 = e0.getOppositeCoord(coord);
            Coord c1 = e1.getOppositeCoord(coord);
            
            int dx0 = c0.x - coord.x;
            int dy0 = c0.y - coord.y;
            int dx1 = c1.x - coord.x;
            int dy1 = c1.y - coord.y;
            
            int q0 = getSector(dx0, dy0);
            int q1 = getSector(dx1, dy1);
            
            //Sort by sector
            if (q0 != q1)
            {
                return q0 - q1;
            }
            
            //In same sector.  Sort by slope
            int area = dy0 * dx1 - dy1 * dx0;
            if (area != 0)
            {
                return area;
            }
            
            //Rays overlap.  Sort by incoming/outgoing
            boolean in0 = isEdgeIn(e0);
            boolean in1 = isEdgeIn(e1);
            if (in0 != in1)
            {
                return in0 ? -1 : 1;
            }
            
            //Duplicate rays.  Sort by hash code
            return in0 
                    ? e0.hashCode() - e1.hashCode() 
                    : e1.hashCode() - e0.hashCode();
        }
        
        private int getSector(int dx, int dy)
        {
            if (dx == 0)
            {
                return dy > 0 ? 2 : 0;
            }
            return dx > 0 ? 1 : 3;
        }
    }
}
