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

package com.kitfox.coyote.shape.bezier.cutgraph;

import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.shape.bezier.BezierCurve2i;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class BezierFlatGraph2i<DataType>
{
    final double flatnessSquared;
    HashMap<Coord, Vertex> vertices = new HashMap<Coord, Vertex>();

    public BezierFlatGraph2i(double flatnessSquared)
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
        Vertex v = vertices.get(c);
        if (v == null)
        {
            v = new Vertex(c);
            vertices.put(c, v);
        }
        return v;
    }
    
    /**
     * Add curve to graph for analysis.
     * 
     * @param curve Curve that will be used in graph
     * @param source User defined data used to track a particular 
     * edge.  Used after analysis is finished to map cut curves
     * back onto source graph.
     */
    public void addCurve(BezierCurve2i curve, DataType source)
    {
        Vertex v0 = getOrCreateVertex(curve.getStartX(), curve.getStartY());
        Vertex v1 = getOrCreateVertex(curve.getEndX(), curve.getEndY());

        Edge<DataType> e = new Edge<DataType>(
                v0, v1, curve, flatnessSquared, source);
        v0.edgesOut.add(e);
        v1.edgesIn.add(e);
    }
    
    private ArrayList<Edge> getEdges()
    {
        ArrayList<Edge> edges = new ArrayList<Edge>();
        for (Vertex v: vertices.values())
        {
            edges.addAll(v.edgesOut);
        }
        return edges;
    }
    
    public void cutAgainst(BezierFlatGraph2i other)
    {
        ArrayList<Edge> edges0 = getEdges();
        ArrayList<Edge> edges1 = other.getEdges();
        
        for (int i = 0; i < edges0.size(); ++i)
        {
            Edge e0 = edges0.get(i);
            BezierCurve2i c0 = e0.curve;
            if (c0.isUnitBoundingBox())
            {
                continue;
            }
            
            for (int j = 0; j < edges1.size(); ++j)
            {
                Edge e1 = edges1.get(j);
                BezierCurve2i c1 = e1.curve;
                
                if (c1.isUnitBoundingBox())
                {
                    continue;
                }
                
                if (!c0.boundingBoxIntersects(c1))
                {
                    continue;
                }
                
                if (c0.equals(c1))
                {
                    continue;
                }
                
                CutRecord rec = cutEdges(e0, e1, other);
                
                if (rec != null && rec.newSrc != null && rec.newSrc.length > 0)
                {
                    //We've cut the curve, replacing it with new curves.
                    // Update current source stack of curves to reflect
                    // new path.  Also update iteration pointers.
                    edges0.remove(i);
                    
                    for (int k = 0; k < rec.newSrc.length; ++k)
                    {
                        edges0.add(i + k, rec.newSrc[k]);
                    }
                    e0 = rec.newSrc[0];
                    c0 = e0.curve;
                    
                    //Also update list we're cutting against.
                    // Skip past edges added, since they're alreay
                    // been cut against current source
                    edges1.remove(j);
                    for (int k = 0; k < rec.newDst.length; ++k)
                    {
                        edges1.add(j + k, rec.newDst[k]);
                    }
                    j = j + rec.newDst.length - 1;
                }
            }
        }
    }

    /**
     * Given two edges from two different graphs, inserts vertices 
     * into graphs so that no edges cross each other.
     * 
     * @param e0 Edge in this graph to cut
     * @param e1 Edge in other graph to cut
     * @param other Other graph
     * @return 
     */
    private CutRecord cutEdges(Edge e0, Edge e1, BezierFlatGraph2i other)
    {
        SegTracker tracker = new SegTracker();
        
        for (int i = 0; i < e0.flat.length; ++i)
        {
            Segment s0 = e0.flat[i];
            
            for (int j = 0; j < e1.flat.length; ++j)
            {
                Segment s1 = e1.flat[j];
                
                if (s0.isParallelTo(s1))
                {
                    if (s0.isPointOnLine(s1.x0, s1.y0))
                    {
                        //Lines are coincident
                        double s0t0 = s1.pointOnLineT(s0.x0, s0.y0);
                        double s0t1 = s1.pointOnLineT(s0.x1, s0.y1);
                        double s1t0 = s0.pointOnLineT(s1.x0, s1.y0);
                        double s1t1 = s0.pointOnLineT(s1.x1, s1.y1);
                        
                        if ((s0t0 < 0 && s0t1 < 0) 
                                || (s0t0 > 1 && s0t1 > 1))
                        {
                            //Lines do not overlap
                            continue;
                        }
                        
                        tracker.addRegion(s0, saturate(s1t0), saturate(s1t1),
                                s1, saturate(s0t0), saturate(s0t1),
                                new Coord((int)Math2DUtil.lerp(s0.x0, s0.x1, s0t0), 
                                (int)Math2DUtil.lerp(s0.y0, s0.y1, s0t0)));
                    }
                    else
                    {
                        //Lines are parallel but not coincident
                        continue;
                    }
                }
                else
                {
                    if (s0.isPointOnLine(s1.x0, s1.y0))
                    {
                        double t = s0.pointOnLineT(s1.x0, s1.y0);

                        if (t >= 0 && t <= 1)
                        {
                            tracker.addPoint(s0, t, s1, 0, 
                                    new Coord(s1.x0, s1.y0));
                        }
                    }
                    else if (s0.isPointOnLine(s1.x1, s1.y1))
                    {
                        double t = s0.pointOnLineT(s1.x1, s1.y1);

                        if (t >= 0 && t <= 1)
                        {
                            tracker.addPoint(s0, t, s1, 1,
                                    new Coord(s1.x1, s1.y1));
                        }
                    }
                    else if (s1.isPointOnLine(s0.x0, s0.y0))
                    {
                        double t = s1.pointOnLineT(s0.x0, s0.y0);

                        if (t >= 0 && t <= 1)
                        {
                            tracker.addPoint(s0, 0, s1, t, 
                                    new Coord(s0.x0, s0.y0));
                        }
                    }
                    else if (s1.isPointOnLine(s0.x1, s0.y1))
                    {
                        double t = s1.pointOnLineT(s0.x1, s0.y1);

                        if (t >= 0 && t <= 1)
                        {
                            tracker.addPoint(s0, 1, s1, t, 
                                    new Coord(s0.x1, s0.y1));
                        }
                    }
                    else
                    {
                        //No coincident points.  Solve system of 
                        // linear eqns
                        double[] t = Math2DUtil.lineIsectFractions(
                                s0.x0, s0.y0, s0.x1 - s0.x0, s0.y1 - s0.y0,
                                s1.x0, s1.y0, s1.x1 - s1.x0, s1.y1 - s1.y0,
                                null);
                        
                        if (t[0] > 0 && t[0] < 1 && t[1] > 0 && t[1] < 1)
                        {
                            tracker.addPoint(s0, t[0], s1, t[1],
                                    new Coord(
                                    (int)Math2DUtil.lerp(s0.x0, s0.x1, t[0]), 
                                    (int)Math2DUtil.lerp(s0.y0, s0.y1, t[0])));
                        }
                    }
                }
            }
        }
        
        ArrayList<SegCrossover> cross0 = tracker.getCrossovers0();
        ArrayList<SegCrossover> cross1 = tracker.getCrossovers1();
        
        Edge[] newSrc = splitEdge(e0, cross0);
        Edge[] newDst = other.splitEdge(e1, cross1);
        
        return new CutRecord(newSrc, newDst);
    }

    /**
     * Actually splits an edge along indicated cut points.  Edge
     * must be a member of this graph.
     * 
     * @param edge Edge to split
     * @param cross T values and coordinates to cut at.
     * @return 
     */
    private Edge[] splitEdge(Edge edge, ArrayList<SegCrossover> cross)
    {
        if (!cross.isEmpty() && cross.get(0).t0 == 0)
        {
            cross.remove(0);
        }
        
        if (!cross.isEmpty() && cross.get(cross.size() - 1).t1 == 1)
        {
            cross.remove(cross.size() - 1);
        }
        
        if (cross.isEmpty())
        {
            return new Edge[]{};
        }
        
        double[] t = new double[cross.size()];
        for (int i = 0; i < cross.size(); ++i)
        {
            SegCrossover cr = cross.get(i);
            t[i] = cr.t0;
        }
        
        BezierCurve2i[] newCurves = edge.curve.split(t);
        Edge[] newEdges = new Edge[newCurves.length];
        
        edge.v0.edgesOut.remove(edge);
        edge.v1.edgesIn.remove(edge);
        
        for (int i = 0; i < newCurves.length; ++i)
        {
            Coord c0 = i == 0 
                    ? edge.v0.coord : cross.get(i - 1).coord;
            Coord c1 = i == newCurves.length - 1 
                    ? edge.v1.coord : cross.get(i).coord;
            
            Vertex v0 = getOrCreateVertex(c0);
            Vertex v1 = getOrCreateVertex(c1);
            Edge e = new Edge(v0, v1, newCurves[i], 
                    flatnessSquared, edge.source);
            v0.edgesOut.add(e);
            v1.edgesIn.add(e);
            
            newEdges[i] = e;
        }
        
        return newEdges;
    }
    
    private double saturate(double value)
    {
        return value <= 0 ? 0 : (value >= 1 ? 1 : value);
    }
    
    
    
}
