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

import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.shape.bezier.BezierCubic2i;
import com.kitfox.coyote.shape.bezier.BezierCurve2i;
import com.kitfox.coyote.shape.bezier.PickPoint;
import com.kitfox.coyote.shape.bezier.cutgraph.CurveCutter2i;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author kitfox
 */
abstract public class BezierMesh2i<VertexData, EdgeData>
{
    HashMap<Coord, BezierMeshVertex2i> vertMap = new HashMap<Coord, BezierMeshVertex2i>();
    final double flatnessSquared;
    int nextVertId;
    int nextEdgeId;

    public BezierMesh2i(double flatnessSquared)
    {
        this.flatnessSquared = flatnessSquared;
    }

    public BezierMesh2i(BezierMesh2i<VertexData, EdgeData> mesh)
    {
        this.flatnessSquared = mesh.flatnessSquared;
        this.nextVertId = mesh.nextVertId;
        this.nextEdgeId = mesh.nextEdgeId;
        
        //Duplicate verts
        for (BezierMeshVertex2i v0: mesh.vertMap.values())
        {
            BezierMeshVertex2i v1 = new BezierMeshVertex2i(v0.getId(),
                    v0.getCoord(), copyVertexData((VertexData)v0.getData()));
            vertMap.put(v1.getCoord(), v1);
        }
        
        //Duplicate edges
        for (BezierMeshVertex2i v: mesh.vertMap.values())
        {
            ArrayList<BezierMeshEdge2i> list = v.getEdgesOut();
            for (BezierMeshEdge2i e0: list)
            {
                BezierMeshVertex2i v0 = vertMap.get(e0.getStart().getCoord());
                BezierMeshVertex2i v1 = vertMap.get(e0.getEnd().getCoord());
                
                BezierMeshEdge2i e1 = new BezierMeshEdge2i(e0.getId(),
                        v0, v1, copyEdgeData((EdgeData)e0.getData()),
                        e0.getSmooth0(), e0.getSmooth1(), 
                        e0.getK0(), e0.getK1());
                v0.edgesOut.add(e1);
                v1.edgesIn.add(e1);
            }
        }
    }

//    public BezierMesh2i(BezierMesh2i<VertexData, EdgeData> mesh)
//    {
//        super(mesh.flatnessSquared);
//        
//        for (
//    }
    
    abstract public VertexData copyVertexData(VertexData data);
    abstract public EdgeData copyEdgeData(EdgeData data);
    
    public double getFlatnessSquared()
    {
        return flatnessSquared;
    }
    
    public ArrayList<Coord> getCoords()
    {
        return new ArrayList<Coord>(vertMap.keySet());
    }

    public BezierMeshVertex2i<VertexData> getClosestVertex(double x, double y)
    {
        double bestDist2 = Double.POSITIVE_INFINITY;
        BezierMeshVertex2i bestVert = null;
        for (BezierMeshVertex2i v: vertMap.values())
        {
            Coord c = v.getCoord();
            double dist2 = Math2DUtil.distSquared(c.x, c.y, x, y);
            if (dist2 <= bestDist2)
            {
                bestVert = v;
                bestDist2 = dist2;
            }
        }
        return bestVert;
    }

    /**
     * Finds the edge closest to the given point.
     * 
     * @param x
     * @param y
     * @param maxDistSq
     * @return 
     */
    public BezierMeshEdge2i<EdgeData> getClosestEdge(double x, double y, double maxDistSq)
    {
        double bestDistSq = maxDistSq;
        BezierMeshEdge2i bestEdge = null;
        
        ArrayList<BezierMeshEdge2i> edges = getEdges();
        for (BezierMeshEdge2i e: edges)
        {
            BezierCurve2i c = e.asCurve();
            if (c.distBoundingBoxSq(x, y) > bestDistSq)
            {
                //Bounding box test to filter out points too far away
                continue;
            }
            
            PickPoint pt = c.getClosestPoint(x, y);
            
            if (pt.getDistSquared() <= bestDistSq)
            {
                bestEdge = e;
                bestDistSq = pt.getDistSquared();
            }
        }
        return bestEdge;
    }
    
    public ArrayList<BezierMeshVertex2i> getVertices()
    {
        return new ArrayList<BezierMeshVertex2i>(vertMap.values());
    }
    
    abstract protected VertexData createDefaultVertexData(Coord c);
    
    protected BezierMeshVertex2i getOrCreateVertex(Coord c)
    {
        BezierMeshVertex2i v = vertMap.get(c);
        if (v == null)
        {
            v = new BezierMeshVertex2i(nextVertId++, c, createDefaultVertexData(c));
            vertMap.put(c, v);
        }
        return v;
    }
    
    public ArrayList<BezierMeshEdge2i> getEdges()
    {
        ArrayList<BezierMeshEdge2i> edges = new ArrayList<BezierMeshEdge2i>();
        for (BezierMeshVertex2i v: vertMap.values())
        {
            edges.addAll(v.edgesOut);
        }
        return edges;
    }
    
    public ArrayList<BezierMeshEdge2i> addEdge(BezierCurve2i curve, EdgeData data)
    {
        if (curve.isPoint())
        {
            return new ArrayList<BezierMeshEdge2i>();
        }
        
        ArrayList<BezierCurve2i> insertCurves = new ArrayList<BezierCurve2i>();
        insertCurves.add(curve);
        
        //Cut existing curves and collect list of curves to insert
        ArrayList<BezierMeshEdge2i> initEdges = getEdges();
        for (BezierMeshEdge2i e1: initEdges)
        {
            for (int i = 0; i < insertCurves.size(); ++i)
            {
                BezierCurve2i c0 = insertCurves.get(i);
                
                if (!e1.isBoundingBoxOverlap(c0))
                {
                    continue;
                }

                BezierCurve2i c1 = e1.asCurve();
                BezierCurve2i[][] splitCurves = 
                        CurveCutter2i.cutCurves(c0, c1, flatnessSquared);

                BezierCurve2i[] cuts0 = splitCurves[0];
                BezierCurve2i[] cuts1 = splitCurves[1];

                if (cuts1.length > 1)
                {
                    //Replace current edge with cut sections
                    removeEdge(e1);
                    
                    for (int j = 0; j < cuts1.length; ++j)
                    {
                        BezierCurve2i cm = cuts1[j];
                        if (cm.isPoint())
                        {
                            continue;
                        }
                        addEdgeDirect(cm, e1.getData());
                    }
                }
                
                if (cuts0.length > 1)
                {
                    insertCurves.remove(i);
                    --i;
                    for (int j = 0; j < cuts0.length; ++j)
                    {
                        if (cuts0[j].isPoint())
                        {
                            continue;
                        }
                        ++i;
                        insertCurves.add(i, cuts0[j]);
                    }
                    
//                    insertCurves.addAll(i, Arrays.asList(cuts0));
//                    i += cuts0.length - 1;
                }
            }
        }
        
        //Add new curve pieces
        ArrayList<BezierMeshEdge2i> retEdges = new ArrayList<BezierMeshEdge2i>();
        for (BezierCurve2i c: insertCurves)
        {
            retEdges.add(addEdgeDirect(c, data));
        }
        return retEdges;
    }
    
    protected BezierMeshEdge2i addEdgeDirect(BezierCurve2i curve, Object data)
    {
        Coord c0 = new Coord(curve.getStartX(), curve.getStartY());
        Coord c1 = new Coord(curve.getEndX(), curve.getEndY());
        BezierMeshVertex2i vm0 = getOrCreateVertex(c0);
        BezierMeshVertex2i vm1 = getOrCreateVertex(c1);

        BezierCubic2i cubic = curve.asCubic();
        Coord k0 = new Coord(cubic.getAx1(), cubic.getAy1());
        Coord k1 = new Coord(cubic.getAx2(), cubic.getAy2());
        
        BezierMeshEdge2i edge;
        if (curve.getOrder() == 2)
        {
            edge = new BezierMeshEdge2i(nextEdgeId++, vm0, vm1, data, 
                    BezierVertexSmooth.CORNER, BezierVertexSmooth.CORNER, 
                    k0, k1);
        }
        else
        {
            edge = new BezierMeshEdge2i(nextEdgeId++, vm0, vm1, data, 
                    BezierVertexSmooth.SMOOTH, BezierVertexSmooth.SMOOTH, 
                    k0, k1);
        }
        
        vm0.edgesOut.add(edge);
        vm1.edgesIn.add(edge);
        return edge;
    }
    
    public void removeEdge(BezierMeshEdge2i e)
    {
        BezierMeshVertex2i v0 = e.getStart();
        BezierMeshVertex2i v1 = e.getEnd();
        
        if (vertMap.get(v0.getCoord()) != v0 
                || vertMap.get(v1.getCoord()) != v1)
        {
            throw new UnsupportedOperationException("Graph does not contain edge");
        }
        
        v0.edgesOut.remove(e);
        v1.edgesIn.remove(e);
        
//        if (v0.isEmpty())
//        {
//            vertMap.remove(v0.getCoord());
//        }
//
//        if (v1.isEmpty())
//        {
//            vertMap.remove(v1.getCoord());
//        }
    }

    public void removeEmptyVertices()
    {
        for (Iterator<BezierMeshVertex2i> it = vertMap.values().iterator();
                it.hasNext();)
        {
            BezierMeshVertex2i v = it.next();
            if (v.isEmpty())
            {
                it.remove();
            }
        }
    }

    public void removeEmptyVertex(BezierMeshVertex2i v)
    {
        if (!v.isEmpty())
        {
            throw new UnsupportedOperationException();
        }
        
        if (!vertMap.containsKey(v.getCoord()))
        {
            throw new UnsupportedOperationException();
        }
        
        vertMap.remove(v.getCoord());
    }

    /**
     * Vertices that are empty can be moved.  This is mainly done
     * during vertex dragging operations.  Vertices must be empty to be 
     * moved to prevent overlapping edge segments.
     * 
     * @param moveVertexMap 
     */
    public void moveEmptyVertices(HashMap<Coord, Coord> moveVertexMap)
    {
        //Remove current vertices
        ArrayList<BezierMeshVertex2i> vertList = new ArrayList<BezierMeshVertex2i>();
        for (Coord c0: moveVertexMap.keySet())
        {
            BezierMeshVertex2i v = vertMap.remove(c0);
            if (!v.isEmpty())
            {
                throw new UnsupportedOperationException("Vertex not empty");
            }
            vertList.add(v);
        }
        
        //Reinsert vertices
        for (BezierMeshVertex2i v: vertList)
        {
            Coord c0 = v.getCoord();
            Coord c1 = moveVertexMap.get(c0);
            
            if (vertMap.containsKey(c1))
            {
                continue;
            }
            
            v.setCoord(c1);
            vertMap.put(c1, v);
        }
    }

    public ArrayList<CutLoop> createFaces()
    {
        ArrayList<BezierMeshEdge2i> edges = getEdges();
        
        ArrayList<CutSegment> segments = new ArrayList<CutSegment>();
        for (BezierMeshEdge2i e: edges)
        {
            BezierCurve2i c = e.asCurve();

            CutSegment.createSegments(c, e, flatnessSquared, segments);
        }
        
        return CutGraph.createFaces(segments);
    }
}
