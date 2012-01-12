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
import com.kitfox.coyote.shape.bezier.BezierLine2i;
import com.kitfox.coyote.shape.bezier.mesh.BezierVertexSmooth;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import static java.lang.Math.*;

/**
 *
 * @author kitfox
 */
public class BezierMeshEdge2i<EdgeData>
{
    private BezierMeshVertex2i start;
    private BezierMeshVertex2i end;
    private EdgeData data;
    private BezierVertexSmooth smooth0;
    private BezierVertexSmooth smooth1;
    
    private Coord k0;
    private Coord k1;

    public BezierMeshEdge2i(BezierMeshVertex2i start, BezierMeshVertex2i end)
    {
        this(start, end, null);
    }

    public BezierMeshEdge2i(BezierMeshVertex2i start, BezierMeshVertex2i end, EdgeData data)
    {
        this(start, end, data, 
                BezierVertexSmooth.CORNER, BezierVertexSmooth.CORNER,
                start.getCoord(), end.getCoord());
    }

    public BezierMeshEdge2i(BezierMeshVertex2i start, BezierMeshVertex2i end,
            EdgeData data, 
            BezierVertexSmooth smooth0, BezierVertexSmooth smooth1, 
            Coord k0, Coord k1)
    {
        this.start = start;
        this.end = end;
        this.data = data;
        this.smooth0 = smooth0;
        this.smooth1 = smooth1;
        this.k0 = k0;
        this.k1 = k1;
    }
    
    public boolean isLine()
    {
        return smooth0 == BezierVertexSmooth.CORNER
                && smooth1 == BezierVertexSmooth.CORNER;
    }
    
    public BezierCurve2i asCurve()
    {
        Coord c0 = start.getCoord();
        Coord c1 = start.getCoord();
        
        if (isLine())
        {
            return new BezierLine2i(c0.x, c0.y, c1.x, c1.y);
        }

        return new BezierCubic2i(c0.x, c0.y, 
                k0.x, k0.y,
                k1.x, k1.y,
                c1.x, c1.y);
    }
    
    public boolean isBoundingBoxOverlap(BezierMeshEdge2i e1)
    {
        return e1.getMaxX() >= getMinX()
                && e1.getMinX() <= getMaxX()
                && e1.getMaxY() >= getMinY()
                && e1.getMinY() >= getMaxY();
    }

    boolean isBoundingBoxOverlap(BezierCurve2i c)
    {
        return c.getMaxX() >= getMinX()
                && c.getMinX() <= getMaxX()
                && c.getMaxY() >= getMinY()
                && c.getMinY() >= getMaxY();
    }
    
    public int getMinX()
    {
        Coord c0 = start.getCoord();
        Coord c1 = end.getCoord();
        
        if (isLine())
        {
            return min(c0.x, c1.x);
        }
        return min(min(c0.x, c1.x), min(k0.x, k1.x));
    }
    
    public int getMaxX()
    {
        Coord c0 = start.getCoord();
        Coord c1 = end.getCoord();
        
        if (isLine())
        {
            return max(c0.x, c1.x);
        }
        return max(max(c0.x, c1.x), max(k0.x, k1.x));
    }
    
    public int getMinY()
    {
        Coord c0 = start.getCoord();
        Coord c1 = end.getCoord();
        
        if (isLine())
        {
            return min(c0.y, c1.y);
        }
        return min(min(c0.y, c1.y), min(k0.y, k1.y));
    }
    
    public int getMaxY()
    {
        Coord c0 = start.getCoord();
        Coord c1 = end.getCoord();
        
        if (isLine())
        {
            return max(c0.y, c1.y);
        }
        return max(max(c0.y, c1.y), max(k0.y, k1.y));
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
     * @return the k0
     */
    public Coord getK0()
    {
        return k0;
    }

    /**
     * @param k0 the k0 to set
     */
    public void setK0(Coord k0)
    {
        this.k0 = k0;
    }

    /**
     * @return the k1
     */
    public Coord getK1()
    {
        return k1;
    }

    /**
     * @param k1 the k1 to set
     */
    public void setK1(Coord k1)
    {
        this.k1 = k1;
    }

    /**
     * @return the smooth0
     */
    public BezierVertexSmooth getSmooth0()
    {
        return smooth0;
    }

    /**
     * @param smooth0 the smooth0 to set
     */
    public void setSmooth0(BezierVertexSmooth smooth0)
    {
        this.smooth0 = smooth0;
    }

    /**
     * @return the smooth1
     */
    public BezierVertexSmooth getSmooth1()
    {
        return smooth1;
    }

    /**
     * @param smooth1 the smooth1 to set
     */
    public void setSmooth1(BezierVertexSmooth smooth1)
    {
        this.smooth1 = smooth1;
    }
}
