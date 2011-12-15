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

package com.kitfox.coyote.shape.bezier.path.cut;

import com.kitfox.coyote.shape.bezier.BezierCubic2i;
import com.kitfox.coyote.shape.bezier.BezierCurve2i;
import com.kitfox.coyote.shape.bezier.BezierLine2i;
import com.kitfox.coyote.shape.bezier.BezierQuad2i;
import com.kitfox.coyote.shape.bezier.path.BezierLoop2i;
import com.kitfox.coyote.shape.bezier.path.BezierPath2i;
import com.kitfox.coyote.shape.bezier.path.BezierPathEdge2i;
import com.kitfox.coyote.shape.bezier.path.cut.Segment.CutRecord;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class BezierPathCutter2i<DataType>
{
    HashMap<Coord, Vertex> vertMap = new HashMap<Coord, Vertex>();
    final double flatnessSquared;

    public BezierPathCutter2i(double flatnessSquared)
    {
        this.flatnessSquared = flatnessSquared;
    }

    private Vertex getOrCreateVertex(int x, int y)
    {
        Coord c = new Coord(x, y);
        return getOrCreateVertex(c);
    }
    
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
    
    public ArrayList<Segment> getSegments()
    {
        ArrayList<Segment> segs = new ArrayList<Segment>();
        for (Vertex v: vertMap.values())
        {
            segs.addAll(v.segOut);
        }
        return segs;
    }
    
    public void addEdge(BezierCurve2i curve, DataType data)
    {
        Segment[] segs = Segment.flatten(curve, data, flatnessSquared);
        
        ArrayList<Segment> segsToAdd = new ArrayList<Segment>(
                Arrays.asList(segs));

        cutAgainstSegments(segsToAdd);
        
        for (Segment s: segsToAdd)
        {
            addSegment(s);
        }
    }

    /**
     * Cuts the current graph against list of segments.  New
     * vertices will be added into the graph at all points where
     * added segments cross it.  Also, the list of segments passed
     * in will also be cut at the points where vertices were added.
     * 
     * @param segsToCut List of segments to cut against.  This list
     * will be modified if any segment in it intersects a segment
     * in the graph anywhere other than an end point.
     */
    private void cutAgainstSegments(ArrayList<Segment> segsToCut)
    {
        ArrayList<Segment> segsInGraph = getSegments();
        for (int i = 0; i < segsToCut.size(); ++i)
        {
            Segment s0 = segsToCut.get(i);
            
            for (int j = 0; j < segsInGraph.size(); ++j)
            {
                Segment s1 = segsInGraph.get(j);

                CutRecord rec = s0.findCuts(s1);

                if (rec == null)
                {
                    continue;
                }

                //If graph was cut, update it and list of graph segments
                if (rec.cuts1 != null)
                {
                    Segment[] newSegs = s1.split(rec.cuts1);

                    removeSegment(s1);
                    segsInGraph.remove(j);

                    for (int k = 0; k < newSegs.length; ++k)
                    {
                        addSegment(newSegs[k]);
                        segsInGraph.add(j + k, newSegs[k]);
                    }

                    //Skip over segs just added
                    j += newSegs.length - 1;
                }
                
                //If toAdd set was cut, update
                if (rec.cuts0 != null)
                {
                    Segment[] newSegs = s0.split(rec.cuts0);

                    segsToCut.remove(i);
                    segsToCut.addAll(i, Arrays.asList(newSegs));
                    s0 = newSegs[0];
                }
            }
        }
    }
    
    /**
     * Compares this graph with passed graph and inserts vertices
     * into both so that graphs only have crossings at vertices.
     * 
     * @param graph 
     */
    public void cutAgainstGraph(BezierPathCutter2i graph)
    {
        ArrayList<Segment> segsToCut = graph.getSegments();
        
        ArrayList<Segment> segsInGraph = getSegments();
        for (int i = 0; i < segsToCut.size(); ++i)
        {
            Segment s0 = segsToCut.get(i);
            
            for (int j = 0; j < segsInGraph.size(); ++j)
            {
                Segment s1 = segsInGraph.get(j);

                CutRecord rec = s0.findCuts(s1);

                if (rec == null)
                {
                    continue;
                }

                //If graph was cut, update it and list of graph segments
                if (rec.cuts1 != null)
                {
                    Segment[] newSegs = s1.split(rec.cuts1);

                    removeSegment(s1);
                    segsInGraph.remove(j);

                    for (int k = 0; k < newSegs.length; ++k)
                    {
                        addSegment(newSegs[k]);
                        segsInGraph.add(j + k, newSegs[k]);
                    }

                    //Skip over segs just added
                    j += newSegs.length - 1;
                }
                
                //If toAdd set was cut, update
                if (rec.cuts0 != null)
                {
                    Segment[] newSegs = s0.split(rec.cuts0);

                    graph.removeSegment(s0);
                    segsToCut.remove(i);
                    
                    for (int k = 0; k < newSegs.length; ++k)
                    {
                        graph.addSegment(newSegs[k]);
                        segsToCut.add(i + k, newSegs[k]);
                    }
                    s0 = newSegs[0];
                }
            }
        }
    }
    
    private void addSegment(Segment seg)
    {
        Vertex v0 = getOrCreateVertex(seg.c0);
        Vertex v1 = getOrCreateVertex(seg.c1);
        
        v0.segOut.add(seg);
        v1.segIn.add(seg);
    }
    
    private void removeSegment(Segment seg)
    {
        Vertex v0 = vertMap.get(seg.c0);
        Vertex v1 = vertMap.get(seg.c1);
        
        v0.segOut.remove(seg);
        v1.segIn.remove(seg);
        
        if (v0.isEmpty())
        {
            vertMap.remove(v0.coord);
        }
        
        if (v1.isEmpty())
        {
            vertMap.remove(v1.coord);
        }
    }

    public BoundryTest pointInsideTest(int x, int y)
    {
        //Search through all lines directly above point of interest
        
        //crossCount is sum of crossings from left to right minus
        // crossings from right to left
        int crossCount = 0;
        boolean onEdge = false;
        for (Vertex v: vertMap.values())
        {
            for (Segment s: v.segOut)
            {
                int x0 = s.c0.x;
                int y0 = s.c0.y;
                int x1 = s.c1.x;
                int y1 = s.c1.y;

                if ((x0 < x && x1 < x) || (x0 > x && x1 > x))
                {
                    continue;
                }

                if (x0 == x && x1 == x)
                {
                    //Vertical line
                    if ((y0 <= y && y <= y1) || (y1 <= y && y <= y0))
                    {
                        onEdge = true;
                    }
                    continue;
                }

                int side = s.getSide(x, y);

                if (side == 0)
                {
                    onEdge = true;
                }
                else if (side > 0)
                {
                    //On left of line
                    if (x0 > x1)
                    {
                        --crossCount;
                    }
                }
                else if (side > 0)
                {
                    //On right of line
                    if (x0 < x1)
                    {
                        ++crossCount;
                    }
                }
            }
        }
        
        if (crossCount == 0)
        {
            return onEdge ? BoundryTest.BOUNDARY : BoundryTest.OUTSIDE;
        }
        return BoundryTest.INSIDE;
    }

    /**
     * Finds all segments in this graph that have the given
     * relationship to the passed in graph.  Results appended
     * to retList.
     * 
     * For useful results, this graph must have been cut against
     * the passed graph.  Ie, they can only have crossovers at
     * vertices.
     * 
     * @param graph Graph to perform inside/outside test against.
     * @param inside If true, return segments that are inside graph.
     * @param boundary If true, return segments that are on the 
     * outside boundary.
     * @param outside If true, return segments that are outside graph.
     * @param retList List results will be appended to.  If null, a 
     * list will be allocated.
     * @return List of results.
     */
    public ArrayList<Segment> getSegments(BezierPathCutter2i graph, 
            boolean outside, boolean boundary, boolean inside,
            ArrayList<Segment> retList)
    {
        if (retList == null)
        {
            retList = new ArrayList<Segment>();
        }
        
        for (Vertex v: vertMap.values())
        {
            for (Segment s: v.segOut)
            {
                int x = (s.c0.x + s.c1.x) / 2;
                int y = (s.c0.y + s.c1.y) / 2;
                switch (graph.pointInsideTest(x, y))
                {
                    case INSIDE:
                        if (inside)
                        {
                            retList.add(s);
                        }
                        break;
                    case BOUNDARY:
                        if (boundary)
                        {
                            retList.add(s);
                        }
                        break;
                    case OUTSIDE:
                        if (outside)
                        {
                            retList.add(s);
                        }
                        break;
                }
            }
        }

        return retList;
    }
    
    public static BezierPath2i segmentsToEdges(ArrayList<Segment> segs)
    {
        ArrayList<EdgeBuilder> edges = new ArrayList<EdgeBuilder>();
        
        for (int i = 0; i < segs.size(); ++i)
        {
            Segment s = segs.get(i);
            
            EdgeBuilder e0 = null;
            EdgeBuilder e1 = null;
            for (int j = 0; j < edges.size(); ++j)
            {
                EdgeBuilder e = edges.get(j);
                if (e.canAppend(s))
                {
                    e.t1 = s.t1;
                    e.c1 = s.c1;
                    e0 = e;
                    edges.remove(j);
                    --j;
                }
                else if (e.canPrepend(s))
                {
                    e.t0 = s.t0;
                    e.c0 = s.c0;
                    e1 = e;
                    edges.remove(j);
                    --j;
                }
            }
            
            if (e0 == null && e1 == null)
            {
                EdgeBuilder e = new EdgeBuilder(s);
                edges.add(e);
            }
            else if (e0 != null && e1 != null)
            {
                //Merge
                e0.t1 = e1.t1;
                edges.add(e0);
            }
            else if (e0 != null)
            {
                edges.add(e0);
            }
            else
            {
                edges.add(e1);
            }
        }

        //Turn into edges
        ArrayList<EdgeItem> edgeItems = new ArrayList<EdgeItem>();
        for (EdgeBuilder e: edges)
        {
            EdgeItem ei = e.asItem();
            edgeItems.add(ei);
        }
        
        //Create loops
        ArrayList<EdgeLoop> loops = new ArrayList<EdgeLoop>();
        while (!edgeItems.isEmpty())
        {
            EdgeItem ei = edgeItems.remove(edgeItems.size() - 1);

            EdgeLoop curLoop = new EdgeLoop(ei);
            loops.add(curLoop);
            
            boolean checkAgain = true;
            while (checkAgain)
            {
                checkAgain = false;
                
                for (int j = 0; j < edgeItems.size(); ++j)
                {
                    EdgeItem item = edgeItems.get(j);
                    if (curLoop.attach(item))
                    {
                        edgeItems.remove(j);
                        --j;
                        checkAgain = true;
                    }
                }
            }
        }
        
        //Build graph from loops
        BezierPath2i path = new BezierPath2i();
        for (EdgeLoop loop: loops)
        {
            path.addLoop(loop.asBezierLoop());
        }
        
        return path;
    }
    
    //----------------
    static class EdgeLoop
    {
        ArrayList<EdgeItem> items = new ArrayList<EdgeItem>();

        private EdgeLoop(EdgeItem ei)
        {
            items.add(ei);
        }
        
        public BezierLoop2i asBezierLoop()
        {
            EdgeItem head = getHead();
            BezierLoop2i loop = new BezierLoop2i(head.c0.x, head.c0.y);
            
            for (EdgeItem item: items)
            {
                switch (item.curve.getOrder())
                {
                    case 2:
                    {
                        BezierLine2i c = (BezierLine2i)item.curve;
                        BezierPathEdge2i e = 
                                loop.appendLine(
                                c.getAx1(), c.getAy1());
                        e.setData(item.data);
                        break;
                    }
                    case 3:
                    {
                        BezierQuad2i c = (BezierQuad2i)item.curve;
                        BezierPathEdge2i e = 
                                loop.appendQuad(
                                c.getAx1(), c.getAy1(), 
                                c.getAx2(), c.getAy2());
                        e.setData(item.data);
                        break;
                    }
                    case 4:
                    {
                        BezierCubic2i c = (BezierCubic2i)item.curve;
                        BezierPathEdge2i e = 
                                loop.appendCubic(
                                c.getAx1(), c.getAy1(), 
                                c.getAx2(), c.getAy2(),
                                c.getAx3(), c.getAy3());
                        e.setData(item.data);
                        break;
                    }
                }
            }
            
            return loop;
        }
        
        public EdgeItem getHead()
        {
            return items.get(0);
        }
        
        public EdgeItem getTail()
        {
            return items.get(items.size() - 1);
        }
        
        boolean isComplete()
        {
            return getHead().c0.equals(getTail().c1);
        }

        private boolean attach(EdgeItem item)
        {
            Coord c0 = getHead().c0;
            Coord c1 = getTail().c1;
            
            if (item.c0.equals(c1))
            {
                items.add(item);
                return true;
            }
            else if (item.c1.equals(c1))
            {
                item.reverse();
                items.add(item);
                return true;
            }
            else if (item.c1.equals(c0))
            {
                items.add(0, item);
                return true;
            }
            else if (item.c0.equals(c0))
            {
                item.reverse();
                items.add(0, item);
                return true;
            }
            return false;
        }
    }
    
    static class EdgeItem<DataType>
    {
        Coord c0;
        Coord c1;
        BezierCurve2i curve;
        DataType data;

        public EdgeItem(Coord c0, Coord c1, BezierCurve2i curve, DataType data)
        {
            this.c0 = c0;
            this.c1 = c1;
            this.curve = curve;
            this.data = data;
        }
        
        public void reverse()
        {
            this.curve = curve.reverse();
            Coord tmp = c0;
            c0 = c1;
            c1 = tmp;
        }
    }
    
    static class EdgeBuilder<DataType>
    {
        double t0;
        double t1;
        Coord c0;
        Coord c1;
        BezierCurve2i curve;
        DataType data;

        private EdgeBuilder(Segment s)
        {
            this.t0 = s.t0;
            this.t1 = s.t1;
            this.c0 = s.c0;
            this.c1 = s.c1;
            this.curve = s.curve;
            this.data = (DataType)s.data;
        }
        
        private EdgeItem asItem()
        {
            BezierCurve2i curvePart = curve;
            if (t0 != 0 && t1 != 1)
            {
                BezierCurve2i[] split = curve.split(new double[]{t0, t1});
                curvePart = split[1];
            }
            else if (t0 != 0)
            {
                BezierCurve2i[] split = curve.split(t0);
                curvePart = split[1];
            }
            else if (t1 != 0)
            {
                BezierCurve2i[] split = curve.split(t1);
                curvePart = split[0];
            }
            
            return new EdgeItem(c0, c1,
                    curvePart.setEndPoints(c0.x, c0.y, c1.x, c1.y), 
                    data);
        }

        private boolean canAppend(Segment s)
        {
            return t1 == s.t0 
                    && c1.equals(s.c0)
                    && curve.equals(s.curve);
        }

        private boolean canPrepend(Segment s)
        {
            return t0 == s.t1 
                    && c0.equals(s.c1)
                    && curve.equals(s.curve);
        }
    }
}
