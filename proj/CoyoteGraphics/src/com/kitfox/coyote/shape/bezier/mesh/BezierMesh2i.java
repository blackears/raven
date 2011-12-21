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

import com.kitfox.coyote.shape.bezier.BezierCurve2i;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class BezierMesh2i<VertexData, EdgeData, FaceData, FaceVertexData>
{
    //There is always an outside face 
    final BezierMeshFace2i<FaceData, FaceVertexData> faceOutside
            = new BezierMeshFace2i();
    
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
            v = new BezierMeshVertex2i(this, c, BezierVertexSmooth.CUSP, null);
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
    
    /*
    public BezierMeshEdge2i[] addCurve(BezierCurve2i curve)
    {
        CutEdge e0 = new CutEdge(curve, flatnessSquared);
        
        ArrayList<BezierMeshEdge2i> edges = getEdges();
        for (BezierMeshEdge2i be1: edges)
        {
            if (!be1.boundingBoxIntersects(curve))
            {
                continue;
            }
            
            BezierCurve2i c1 = be1.asCurve();
            CutEdge e1 = new CutEdge(c1, flatnessSquared);
            
            CutEdgeRecord rec = e0.cutAgainst(e1);
            if (rec == null)
            {
                continue;
            }

            
        }
        
        
        
        
        
        
        //Cut against existing graph
        Coord c0 = new Coord(curve.getStartX(), curve.getStartY());
        Coord c1 = new Coord(curve.getEndX(), curve.getEndY());
        
        BezierMeshVertex2i v0 = getOrCreateVertex(c0);
        BezierMeshVertex2i v1 = getOrCreateVertex(c1);
        
        BezierMeshEdge2i e = new BezierMeshEdge2i(this, v0, v1, 
                null, null, null, order);
    }
    */
}
