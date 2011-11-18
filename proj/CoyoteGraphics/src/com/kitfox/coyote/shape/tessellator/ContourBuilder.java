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

package com.kitfox.coyote.shape.tessellator;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class ContourBuilder
{
    HashMap<TessPoint, Vertex> edgeMap = new HashMap<TessPoint, Vertex>();
    ArrayList<HalfEdge> edges = new ArrayList<HalfEdge>();

    void addHalfEdge(TessPoint p0, TessPoint p1)
    {
        HalfEdge edge = new HalfEdge(p0, p1);
        edges.add(edge);

        Vertex v0 = edgeMap.get(p0);
        v0.edgeOut.add(edge);
    }

    public void buildContour()
    {

        while (!edges.isEmpty())
        {
            Contour ctr = new Contour();
            HalfEdge firstEdge = edges.remove(edges.size() - 1);
            ctr.path.add(firstEdge);

            HalfEdge curEdge = firstEdge;
            Vertex curVert = edgeMap.get(curEdge.p0);
            
        }
    }

    //----------------------------

    class Contour
    {
        ArrayList<HalfEdge> path = new ArrayList<HalfEdge>();
    }

    class Vertex
    {
        TessPoint point;
        ArrayList<HalfEdge> edgeOut = new ArrayList<HalfEdge>();
    }

    class HalfEdge
    {
        final TessPoint p0;
        final TessPoint p1;

        public HalfEdge(TessPoint p0, TessPoint p1)
        {
            this.p0 = p0;
            this.p1 = p1;
        }
    }
}
