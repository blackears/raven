/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.kitfox.coyote.shape.bezier.path;

import com.kitfox.coyote.shape.bezier.BezierCurve2i;
import com.kitfox.coyote.shape.bezier.path.Segment.CutRecord;
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
            boolean inside, boolean boundary, boolean outside,
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
}
