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

import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class BezierVertex2i<VertexData>
{
    private int x;
    private int y;
    private BezierVertexSmooth smooth;
    //User defined data stored per vertex
    private VertexData data;
    
    final ArrayList<BezierEdge2i> edgesIn = new ArrayList<BezierEdge2i>();
    final ArrayList<BezierEdge2i> edgesOut = new ArrayList<BezierEdge2i>();

    public BezierVertex2i(int x, int y, BezierVertexSmooth smooth, VertexData data)
    {
        this.x = x;
        this.y = y;
        this.smooth = smooth;
        this.data = data;
    }

    public BezierVertex2i(int x, int y)
    {
        this(x, y, BezierVertexSmooth.CUSP, null);
    }

    public BezierVertex2i()
    {
        this(0, 0);
    }

    public BezierEdge2i getEdgeIn(int index)
    {
        return edgesIn.get(index);
    }
    
    public BezierEdge2i getEdgeOut(int index)
    {
        return edgesOut.get(index);
    }
    
    /**
     * @return the x
     */
    public int getX()
    {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x)
    {
        this.x = x;
    }

    /**
     * @return the y
     */
    public int getY()
    {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y)
    {
        this.y = y;
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

}
