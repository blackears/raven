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

package com.kitfox.coyote.shape.tessellator3;

import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class TessGraph
{
    HashMap<Coord, Vertex> vertMap = new HashMap<Coord, Vertex>();
    HashMap<TessSeg, Edge> edgeMap = new HashMap<TessSeg, Edge>();

    private Vertex getOrCreateVertex(Coord c)
    {
        Vertex v = vertMap.get(c);
        if (v == null)
        {
            v = new Vertex(c);
            vertMap.put(c, v);
        }
        return v;
    }

    void addEdge(TessSeg s)
    {
        Edge e = edgeMap.get(s);
        if (e != null)
        {
            e.weight++;
            return;
        }
        
        e = edgeMap.get(s.reverse());
        if (e != null)
        {
            e.weight--;
            
            if  (e.weight == 0)
            {
                removeEdge(e.seg);
            }
            
            return;
        }
        
        //New edge
        Vertex v0 = getOrCreateVertex(s.v0);
        Vertex v1 = getOrCreateVertex(s.v1);
        
        e = new Edge(s, 1, v0, v1);
        edgeMap.put(s, e);
        
        vertMap.put(v0.p, v0);
        vertMap.put(v1.p, v1);
        v0.edges.add(e);
        v1.edges.add(e);
    }

    private void removeEdge(TessSeg s)
    {
        Edge e = edgeMap.remove(s);
        
        e.v0.edges.remove(e);
        e.v1.edges.remove(e);
        
        if (e.v0.isEmpty())
        {
            vertMap.remove(e.v0.p);
        }
        if (e.v1.isEmpty())
        {
            vertMap.remove(e.v1.p);
        }
    }
    
    boolean isEmpty()
    {
        return edgeMap.isEmpty();
    }
    
    void prepareGraph()
    {
        for (Vertex v: vertMap.values())
        {
            v.sortRadial();
        }
        
        for (Edge e: edgeMap.values())
        {
            calcWinding(e);
        }
    }

    private void calcWinding(Edge e0)
    {
        //Refrence point
        Coord cr0 = e0.v0.p;
        Coord cr1 = e0.v1.p;
        boolean reversedRef = false;

        if (cr0.x > cr1.x)
        {
            Coord c = cr0;
            cr0 = cr1;
            cr1 = c;
            reversedRef = true;
        }
        
        int refWeight = 0;
        
        for (Edge e: edgeMap.values())
        {
            if (e == e0)
            {
                continue;
            }

            TessSeg s = e.seg;
            Coord c0 = s.v0;
            Coord c1 = s.v1;

            if (c0.x == c1.x)
            {
                //Ignore verticals
                continue;
            }

            boolean reversed = false;

            if (c0.x > c1.x)
            {
                Coord c = c0;
                c0 = c1;
                c1 = c;
                reversed = true;
            }
            
            if (!(c0.x < cr1.x && cr1.x <= c1.x))
            {
                //X span must cross reference point
                continue;
            }
            
            if (cr1.equals(c1))
            {
                int drx = cr0.x - cr1.x;
                int dry = cr0.y - cr1.y;
                int dex = c0.x - c1.x;
                int dey = c0.y - c1.y;
                
                int cross = drx * dey - dry * dex;
                if (cross > 0)
                {
                    refWeight += !reversed ? e.weight : -e.weight;
                }
                continue;
            }
            
            int side = Math2DUtil.getLineSide(
                    c0.x, c0.y, c1.x - c0.x, c1.y - c0.y, 
                    cr1.x, cr1.y);
            
            if (side > 0)
            {
                refWeight += !reversed ? e.weight : -e.weight;
            }
        }

        //Calc winding for right side of edge
        int windingRight = refWeight;
        
        if (reversedRef
                || (cr0.x == cr1.x && cr0.y < cr1.y))
        {
            //Cross left to right
            windingRight += -e0.weight;
        }
        
        e0.windingRight = windingRight;
        e0.windingLeft = windingRight + e0.weight;
    }
    
    TessLoop extractContour()
    {
        Vertex vInit = null;
        
        //Find lowest vertex
        for (Vertex v: vertMap.values())
        {
            if (vInit == null || vInit.p.y > v.p.y)
            {
                vInit = v;
            }
        }
        
        //Find exterior edge
        Edge eInit = vInit.edges.get(0);
        
        //Overall winding of shape we're extracting
        boolean windCCW = vInit == eInit.v0;
        if (windCCW)
        {
            //Move cursor to head of segment
            vInit = eInit.v1;
        }
        
        int weightAdj = eInit.weight;

        ArrayList<Coord> points = new ArrayList<Coord>();
        ArrayList<Edge> visited = new ArrayList<Edge>();
        
        Edge e0 = eInit;
        Vertex v0 = vInit;

        do
        {
            points.add(v0.p);
            visited.add(e0);
            
            boolean atHead = v0 == e0.v1;
            e0.weight += atHead ? -weightAdj : weightAdj;
            
            Edge e1 = windCCW
                    ? v0.nextEdgeCW(e0)
                    : v0.nextEdgeCCW(e0);
            Vertex v1 = e1.getOtherVertex(v0);

            e0 = e1;
            v0 = v1;
        } while (e0 != eInit);
        
        //Remove empty edges
        for (Edge e: visited)
        {
            if (e.weight == 0)
            {
                removeEdge(e.seg);
            }
        }
        
        return new TessLoop(points, 
                windCCW ? e0.windingLeft : e0.windingRight,
                windCCW);
    }

    //----------------------------------
    class RadialSort implements Comparator<Edge>
    {
        final Coord c;

        public RadialSort(Coord c)
        {
            this.c = c;
        }

        @Override
        public int compare(Edge o1, Edge o2)
        {
            Coord c0 = o1.seg.getOtherVertex(c);
            Coord c1 = o2.seg.getOtherVertex(c);
            
            return compareRadial(c0.x - c.x, c0.y - c.y, c1.x - c.x, c1.y - c.y);
        }

        private int compareRadial(int dx0, int dy0, int dx1, int dy1)
        {
            int q0 = quad(dx0, dy0);
            int q1 = quad(dx1, dy1);
            if (q0 != q1)
            {
                return q0 - q1;
            }
            
            int cross = dx0 * dy1 - dx1 * dy0;
            return -cross;
        }
        
        private int quad(int dx, int dy)
        {
            if (dx == 0)
            {
                return dy > 0 ? 2 : 0;
            }
            return dx > 0 ? 1 : 3;
        }
    }
    
    class Edge
    {
        TessSeg seg;
        int weight;
        Vertex v0;
        Vertex v1;

        //Winding level
        int windingLeft = Integer.MIN_VALUE;
        int windingRight = Integer.MIN_VALUE;

        public Edge(TessSeg seg, int weight, Vertex v0, Vertex v1)
        {
            this.seg = seg;
            this.weight = weight;
            this.v0 = v0;
            this.v1 = v1;
        }

        private Vertex getOtherVertex(Vertex v)
        {
            return v == v0 ? v1 : v0;
        }

        @Override
        public String toString()
        {
            return "[" + seg + " we:" + weight 
                    + " wiL:" + windingLeft 
                    + " wiR:" + windingRight
                    + "]";
        }
        
        
    }
    
    class Vertex
    {
        final Coord p;
        
        ArrayList<Edge> edges = new ArrayList<Edge>();

        public Vertex(Coord p)
        {
            this.p = p;
        }

        private void sortRadial()
        {
            RadialSort sort = new RadialSort(p);
            Collections.sort(edges, sort);
        }
        
        private boolean isEmpty()
        {
            return edges.isEmpty();
        }

        private Edge nextEdgeCCW(Edge e)
        {
            int idx = edges.indexOf(e);
            if (idx == edges.size() - 1)
            {
                return edges.get(0);
            }
            return edges.get(idx + 1);
        }

        private Edge nextEdgeCW(Edge e)
        {
            int idx = edges.indexOf(e);
            if (idx == 0)
            {
                return edges.get(edges.size() - 1);
            }
            return edges.get(idx - 1);
        }
    }
}
