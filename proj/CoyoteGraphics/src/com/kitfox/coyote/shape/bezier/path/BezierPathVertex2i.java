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

import com.kitfox.coyote.shape.bezier.mesh.BezierVertexSmooth;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;

/**
 *
 * @author kitfox
 */
public class BezierPathVertex2i<VertexData>
{
    private Coord coord;
    private BezierVertexSmooth smooth;
    private VertexData data;
    
    BezierPathEdge2i edgeIn;
    BezierPathEdge2i edgeOut;

    public BezierPathVertex2i(Coord coord, BezierVertexSmooth smooth, VertexData data)
    {
        this.coord = coord;
        this.smooth = smooth;
        this.data = data;
    }
    
    public BezierPathVertex2i(int x, int y)
    {
        this(new Coord(x, y), BezierVertexSmooth.FREE, null);
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
