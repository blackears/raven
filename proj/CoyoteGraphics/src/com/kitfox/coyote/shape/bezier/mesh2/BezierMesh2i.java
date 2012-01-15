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

import com.kitfox.coyote.shape.bezier.BezierCubic2i;
import com.kitfox.coyote.shape.bezier.BezierCurve2i;
import com.kitfox.coyote.shape.bezier.cutgraph.CurveCutter2i;
import com.kitfox.coyote.shape.bezier.mesh.BezierVertexSmooth;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class BezierMesh2i<EdgeData>
{
    HashMap<Coord, BezierMeshVertex2i> vertMap = new HashMap<Coord, BezierMeshVertex2i>();
    final double flatnessSquared;

    public BezierMesh2i(double flatnessSquared)
    {
        this.flatnessSquared = flatnessSquared;
    }
    
    private BezierMeshVertex2i getOrCreateVertex(Coord c)
    {
        BezierMeshVertex2i v = vertMap.get(c);
        if (v == null)
        {
            v = new BezierMeshVertex2i(c, null);
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
        ArrayList<BezierCurve2i> insertCurves = new ArrayList<BezierCurve2i>();
        insertCurves.add(curve);
        
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
                        addEdgeDirect(cm, e1.getData());
                    }
                }
                
                if (cuts0.length > 1)
                {
                    insertCurves.remove(i);
                    insertCurves.addAll(i, Arrays.asList(cuts0));
                    i += cuts0.length - 1;
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
    
    private BezierMeshEdge2i addEdgeDirect(BezierCurve2i curve, Object data)
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
            edge = new BezierMeshEdge2i(vm0, vm1, data, 
                    BezierVertexSmooth.CORNER, BezierVertexSmooth.CORNER, 
                    k0, k1);
        }
        else
        {
            edge = new BezierMeshEdge2i(vm0, vm1, data, 
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
        v0.edgesOut.remove(e);
        v1.edgesIn.remove(e);
        
        if (v0.isEmpty())
        {
            vertMap.remove(v0.getCoord());
        }
        
        if (v1.isEmpty())
        {
            vertMap.remove(v1.getCoord());
        }
    }

    public CutLoop createFaces()
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
