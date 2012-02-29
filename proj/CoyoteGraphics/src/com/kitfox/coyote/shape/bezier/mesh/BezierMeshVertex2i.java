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
    private Coord coord;
    //User defined data stored per vertex
    private VertexData data;
    
    final ArrayList<BezierMeshEdge2i> edgesIn = new ArrayList<BezierMeshEdge2i>();
    final ArrayList<BezierMeshEdge2i> edgesOut = new ArrayList<BezierMeshEdge2i>();

    public BezierMeshVertex2i(Coord coord, VertexData data)
    {
        this.coord = coord;
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

    public boolean isEmpty()
    {
        return edgesIn.isEmpty() && edgesOut.isEmpty();
    }

    public int getNumEdges()
    {
        return edgesIn.size() + edgesOut.size();
    }

    public BezierMeshEdge2i getEdge(int index)
    {
        return index < edgesIn.size() ? edgesIn.get(index)
                : edgesOut.get(index - edgesIn.size());
    }

    /**
     * @return the edgesIn
     */
    public ArrayList<BezierMeshEdge2i> getEdgesIn()
    {
        return new ArrayList<BezierMeshEdge2i>(edgesIn);
    }

    /**
     * @return the edgesOut
     */
    public ArrayList<BezierMeshEdge2i> getEdgesOut()
    {
        return new ArrayList<BezierMeshEdge2i>(edgesOut);
    }

}
