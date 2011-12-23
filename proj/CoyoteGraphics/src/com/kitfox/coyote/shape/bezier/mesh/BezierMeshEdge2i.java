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

/**
 *
 * @author kitfox
 */
public class BezierMeshEdge2i<EdgeData>
{
    final BezierMesh2i mesh;
    
    private BezierMeshVertex2i start;
    private BezierMeshVertex2i end;
    private BezierMeshFace2i left;
    private BezierMeshFace2i right;
    private EdgeData data;

    private CutEdge cutEdge;
    /*
    //Order of curve: 2 -> line, 3 -> quad, 4 -> cubic    
    private int order;
    //Knot values.  Will only be used if degree of curve requires them
    private int k0x;
    private int k0y;
    private int k1x;
    private int k1y;

    //Representation of this curve as line segments
    ArrayList<Segment> segments = new ArrayList<Segment>();
    */
    
    public BezierMeshEdge2i(BezierMesh2i mesh, 
            BezierMeshVertex2i start, BezierMeshVertex2i end,
            BezierMeshFace2i left, BezierMeshFace2i right, EdgeData data, 
            CutEdge cutEdge)
    {
        this.mesh = mesh;
        this.start = start;
        this.end = end;
        this.left = left;
        this.right = right;
        this.data = data;
        this.cutEdge = cutEdge;
    }
    
    /**
     * @return the start
     */
    public BezierMeshVertex2i getStart()
    {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(BezierMeshVertex2i start)
    {
        this.start = start;
    }

    /**
     * @return the end
     */
    public BezierMeshVertex2i getEnd()
    {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(BezierMeshVertex2i end)
    {
        this.end = end;
    }

    /**
     * @return the left
     */
    public BezierMeshFace2i getLeft()
    {
        return left;
    }

    /**
     * @param left the left to set
     */
    public void setLeft(BezierMeshFace2i left)
    {
        this.left = left;
    }

    /**
     * @return the right
     */
    public BezierMeshFace2i getRight()
    {
        return right;
    }

    /**
     * @param right the right to set
     */
    public void setRight(BezierMeshFace2i right)
    {
        this.right = right;
    }

    /**
     * @return the data
     */
    public EdgeData getData()
    {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(EdgeData data)
    {
        this.data = data;
    }

    /**
     * @return the cutEdge
     */
    public CutEdge getCutEdge()
    {
        return cutEdge;
    }

    /**
     * @param cutEdge the cutEdge to set
     */
    public void setCutEdge(CutEdge cutEdge)
    {
        this.cutEdge = cutEdge;
    }
    
}
