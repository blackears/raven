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

import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class BezierMeshVertex2i<VertexData>
{
    final BezierMesh2i mesh;
    
    private Coord coord;
    private BezierVertexSmooth smooth;
    //User defined data stored per vertex
    private VertexData data;
    
    final ArrayList<BezierMeshEdge2i> edgesIn = new ArrayList<BezierMeshEdge2i>();
    final ArrayList<BezierMeshEdge2i> edgesOut = new ArrayList<BezierMeshEdge2i>();

    public BezierMeshVertex2i(BezierMesh2i mesh, Coord coord, BezierVertexSmooth smooth, VertexData data)
    {
        this.mesh = mesh;
        this.coord = coord;
        this.smooth = smooth;
        this.data = data;
    }

    public BezierMeshVertex2i(BezierMesh2i mesh, int x, int y)
    {
        this(mesh, new Coord(x, y), BezierVertexSmooth.FREE, null);
    }

    public BezierMeshVertex2i(BezierMesh2i mesh)
    {
        this(mesh, 0, 0);
    }

    public BezierMeshEdge2i getEdgeIn(int index)
    {
        return edgesIn.get(index);
    }
    
    public BezierMeshEdge2i getEdgeOut(int index)
    {
        return edgesOut.get(index);
    }

    /**
     * @return the smooth
     */
    public BezierVertexSmooth getSmooth()
    {
        return smooth;
    }

    /**
     * @param smooth the smooth to set
     */
    public void setSmooth(BezierVertexSmooth smooth)
    {
        this.smooth = smooth;
    }

    /**
     * @return the data
     */
    public VertexData getData()
    {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(VertexData data)
    {
        this.data = data;
    }

    /**
     * @return the coord
     */
    public Coord getCoord()
    {
        return coord;
    }

    /**
     * @param coord the coord to set
     */
    public void setCoord(Coord coord)
    {
        this.coord = coord;
    }

}
