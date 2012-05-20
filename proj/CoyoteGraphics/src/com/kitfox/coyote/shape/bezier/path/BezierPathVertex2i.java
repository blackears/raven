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

package com.kitfox.coyote.shape.bezier.path;

import com.kitfox.coyote.shape.bezier.path.cut.Coord;

/**
 *
 * @author kitfox
 */
public class BezierPathVertex2i<VertexData>
{
    private final int id;
    private Coord coord;
//    private BezierVertexSmooth smooth;
    private VertexData data;
    
    private BezierPathEdge2i edgeIn;
    private BezierPathEdge2i edgeOut;

    BezierPathVertex2i(int id, Coord coord, VertexData data)
    {
        this.id = id;
        this.coord = coord;
  //      this.smooth = smooth;
        this.data = data;
    }
    
//    BezierPathVertex2i(int id, int x, int y)
//    {
//        this(id, new Coord(x, y), null);
//    }

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

    /**
     * @return the edgeIn
     */
    public BezierPathEdge2i getEdgeIn()
    {
        return edgeIn;
    }

    /**
     * @param edgeIn the edgeIn to set
     */
    public void setEdgeIn(BezierPathEdge2i edgeIn)
    {
        this.edgeIn = edgeIn;
    }

    /**
     * @return the edgeOut
     */
    public BezierPathEdge2i getEdgeOut()
    {
        return edgeOut;
    }

    /**
     * @param edgeOut the edgeOut to set
     */
    public void setEdgeOut(BezierPathEdge2i edgeOut)
    {
        this.edgeOut = edgeOut;
    }

    /**
     * @return the id
     */
    public int getId()
    {
        return id;
    }

    public int getNumEdges()
    {
        return 2;
    }

    public int getX()
    {
        return coord.x;
    }
    
    public int getY()
    {
        return coord.y;
    }

    public BezierPathEdge2i getOtherEdge(BezierPathEdge2i edge)
    {
        if (edgeIn == edge)
        {
            return edgeOut;
        }
        if (edgeOut == edge)
        {
            return edgeIn;
        }
        throw new IllegalArgumentException();
    }
}
