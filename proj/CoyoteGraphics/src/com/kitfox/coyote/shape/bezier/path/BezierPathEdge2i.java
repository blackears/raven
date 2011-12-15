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

import com.kitfox.coyote.shape.bezier.BezierCubic2i;
import com.kitfox.coyote.shape.bezier.BezierCurve2i;
import com.kitfox.coyote.shape.bezier.BezierLine2i;
import com.kitfox.coyote.shape.bezier.BezierQuad2i;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;

/**
 *
 * @author kitfox
 */
public class BezierPathEdge2i<EdgeData>
{
    private BezierPathVertex2i start;
    private BezierPathVertex2i end;
    private EdgeData data;
    
    //Order of curve: 2 -> line, 3 -> quad, 4 -> cubic    
    private int order;
    //Knot values.  Will only be used if degree of curve requires them
    private int k0x;
    private int k0y;
    private int k1x;
    private int k1y;

    public BezierPathEdge2i(BezierPathVertex2i start, BezierPathVertex2i end, EdgeData data, int order, int k0x, int k0y, int k1x, int k1y)
    {
        this.start = start;
        this.end = end;
        this.data = data;
        this.order = order;
        this.k0x = k0x;
        this.k0y = k0y;
        this.k1x = k1x;
        this.k1y = k1y;
    }

    public BezierPathEdge2i(BezierPathVertex2i start, BezierPathVertex2i end, EdgeData data, int k0x, int k0y, int k1x, int k1y)
    {
        this(start, end, data, 4, k0x, k0y, k1x, k1y);
    }

    public BezierPathEdge2i(BezierPathVertex2i start, BezierPathVertex2i end, EdgeData data, int k0x, int k0y)
    {
        this(start, end, data, 3, k0x, k0y, 0, 0);
    }

    public BezierPathEdge2i(BezierPathVertex2i start, BezierPathVertex2i end, EdgeData data)
    {
        this(start, end, data, 2, 0, 0, 0, 0);
    }

    /**
     * @return the start
     */
    public BezierPathVertex2i getStart()
    {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(BezierPathVertex2i start)
    {
        this.start = start;
    }

    /**
     * @return the end
     */
    public BezierPathVertex2i getEnd()
    {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(BezierPathVertex2i end)
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
     * @return the order
     */
    public int getOrder()
    {
        return order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(int order)
    {
        this.order = order;
    }

    /**
     * @return the k0x
     */
    public int getK0x()
    {
        return k0x;
    }

    /**
     * @param k0x the k0x to set
     */
    public void setK0x(int k0x)
    {
        this.k0x = k0x;
    }

    /**
     * @return the k0y
     */
    public int getK0y()
    {
        return k0y;
    }

    /**
     * @param k0y the k0y to set
     */
    public void setK0y(int k0y)
    {
        this.k0y = k0y;
    }

    /**
     * @return the k1x
     */
    public int getK1x()
    {
        return k1x;
    }

    /**
     * @param k1x the k1x to set
     */
    public void setK1x(int k1x)
    {
        this.k1x = k1x;
    }

    /**
     * @return the k1y
     */
    public int getK1y()
    {
        return k1y;
    }

    /**
     * @param k1y the k1y to set
     */
    public void setK1y(int k1y)
    {
        this.k1y = k1y;
    }

    public boolean isPoint()
    {
        Coord c0 = start.getCoord();
        Coord c1 = end.getCoord();
        if (!c0.equals(c1))
        {
            return false;
        }
        
        if (order >= 3 && (c0.x != k0x || c0.y != k0y))
        {
            return false;
        }
        
        if (order >= 4 && (c0.x != k1x || c0.y != k1y))
        {
            return false;
        }
        return true;
    }

    public BezierCurve2i asCurve()
    {
        Coord c0 = start.getCoord();
        Coord c1 = end.getCoord();
        
        switch (order)
        {
            case 2:
                return new BezierLine2i(c0.x, c0.y, c1.x, c1.y);
            case 3:
                return new BezierQuad2i(c0.x, c0.y, k0x, k0y, c1.x, c1.y);
            case 4:
                return new BezierCubic2i(c0.x, c0.y, k0x, k0y, k1x, k1y, c1.x, c1.y);
        }
        return null;
    }

    
}
