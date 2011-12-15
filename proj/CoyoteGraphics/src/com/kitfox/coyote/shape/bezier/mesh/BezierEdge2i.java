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

import com.kitfox.coyote.shape.bezier.BezierCubic2i;
import com.kitfox.coyote.shape.bezier.BezierCurve2i;
import com.kitfox.coyote.shape.bezier.BezierLine2i;
import com.kitfox.coyote.shape.bezier.BezierPoint2i;
import com.kitfox.coyote.shape.bezier.BezierQuad2i;

/**
 *
 * @author kitfox
 */
public class BezierEdge2i<EdgeData>
{
    private BezierVertex2i start;
    private BezierVertex2i end;
    private BezierFace2i left;
    private BezierFace2i right;
    private EdgeData data;

    //Order of curve: 2 -> line, 3 -> quad, 4 -> cubic    
    private int order;
    //Knot values.  Will only be used if degree of curve requires them
    private int k0x;
    private int k0y;
    private int k1x;
    private int k1y;

    public BezierEdge2i(BezierVertex2i start, BezierVertex2i end,
            BezierFace2i left, BezierFace2i right, EdgeData data, 
            int order)
    {
        this(start, end, left, right, data, order, 0, 0);
    }

    public BezierEdge2i(BezierVertex2i start, BezierVertex2i end,
            BezierFace2i left, BezierFace2i right, EdgeData data, 
            int order, int k0x, int k0y)
    {
        this(start, end, left, right, data, order, k0x, k0y, 0, 0);
    }
    
    public BezierEdge2i(BezierVertex2i start, BezierVertex2i end,
            BezierFace2i left, BezierFace2i right, EdgeData data, 
            int order, int k0x, int k0y, int k1x, int k1y)
    {
        this.start = start;
        this.end = end;
        this.left = left;
        this.right = right;
        this.data = data;
        this.order = order;
        this.k0x = k0x;
        this.k0y = k0y;
        this.k1x = k1x;
        this.k1y = k1y;
    }
    
    /**
     * @return the start
     */
    public BezierVertex2i getStart()
    {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(BezierVertex2i start)
    {
        this.start = start;
    }

    /**
     * @return the end
     */
    public BezierVertex2i getEnd()
    {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(BezierVertex2i end)
    {
        this.end = end;
    }

    /**
     * @return the left
     */
    public BezierFace2i getLeft()
    {
        return left;
    }

    /**
     * @param left the left to set
     */
    public void setLeft(BezierFace2i left)
    {
        this.left = left;
    }

    /**
     * @return the right
     */
    public BezierFace2i getRight()
    {
        return right;
    }

    /**
     * @param right the right to set
     */
    public void setRight(BezierFace2i right)
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
     * @return the degree
     */
    public int getDegree()
    {
        return order;
    }

    /**
     * @param degree the degree to set
     */
    public void setDegree(int degree)
    {
        this.order = degree;
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

    public BezierCurve2i asCurve()
    {
        int p0x = start.getX();
        int p0y = start.getY();
        int p1x = end.getX();
        int p1y = end.getY();
        switch (order)
        {
            case 1:
                return new BezierPoint2i(p0x, p0y);
            case 2:
                return new BezierLine2i(p0x, p0y, p1x, p1y);
            case 3:
                return new BezierQuad2i(p0x, p0y, k0x, k0y, p1x, p1y);
            case 4:
                return new BezierCubic2i(p0x, p0y, k0x, k0y, k1x, k1y, p1x, p1y);
        }
        return null;
    }

    public BezierVertex2i splitAt(double d)
    {
        int p0x = start.getX();
        int p0y = start.getY();
        int p1x = end.getX();
        int p1y = end.getY();
        switch (order)
        {
            case 1:
            {
                return null;
            }
            case 2:
            {
                BezierLine2i c = new BezierLine2i(p0x, p0y, p1x, p1y);
                BezierLine2i[] segs = c.split(d);
                BezierVertex2i v = new BezierVertex2i(segs[0].getEndX(), segs[0].getEndY());
                BezierEdge2i e1 = new BezierEdge2i(v, end, left, right, data, 2);
                end.edgesIn.remove(this);
                end.edgesIn.add(e1);
                v.edgesIn.add(this);
                v.edgesOut.add(e1);
                return v;
            }
            case 3:
            {
                BezierQuad2i c = new BezierQuad2i(p0x, p0y, k0x, k0y, p1x, p1y);
                BezierQuad2i[] segs = c.split(d);
                BezierVertex2i v = new BezierVertex2i(segs[0].getEndX(), segs[0].getEndY());
                BezierEdge2i e1 = new BezierEdge2i(v, end, left, right, data, 3);
                k0x = segs[0].getAx1();
                k0y = segs[0].getAy1();
                e1.k0x = segs[1].getAx1();
                e1.k0y = segs[1].getAy1();
                
                end.edgesIn.remove(this);
                end.edgesIn.add(e1);
                v.edgesIn.add(this);
                v.edgesOut.add(e1);
                return v;
            }
            case 4:
            {
                BezierCubic2i c = new BezierCubic2i(p0x, p0y, k0x, k0y, k1x, k1y, p1x, p1y);
                BezierCubic2i[] segs = c.split(d);
                BezierVertex2i v = new BezierVertex2i(segs[0].getEndX(), segs[0].getEndY());
                BezierEdge2i e1 = new BezierEdge2i(v, end, left, right, data, 4);
                k0x = segs[0].getAx1();
                k0y = segs[0].getAy1();
                k1x = segs[0].getAx2();
                k1y = segs[0].getAy2();
                e1.k0x = segs[1].getAx1();
                e1.k0y = segs[1].getAy1();
                e1.k1x = segs[1].getAx2();
                e1.k1y = segs[1].getAy2();
                
                end.edgesIn.remove(this);
                end.edgesIn.add(e1);
                v.edgesIn.add(this);
                v.edgesOut.add(e1);
                return v;
            }
        }
        
        return null;
    }
    
}
