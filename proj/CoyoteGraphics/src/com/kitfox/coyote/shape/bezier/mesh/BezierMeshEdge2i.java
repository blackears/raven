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
import com.kitfox.coyote.shape.bezier.path.cut.Segment;
import java.util.ArrayList;

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

    //Order of curve: 2 -> line, 3 -> quad, 4 -> cubic    
    private int order;
    //Knot values.  Will only be used if degree of curve requires them
    private int k0x;
    private int k0y;
    private int k1x;
    private int k1y;

    //Representation of this curve as line segments
    ArrayList<Segment> segments = new ArrayList<Segment>();
    
    public BezierMeshEdge2i(BezierMesh2i mesh, 
            BezierMeshVertex2i start, BezierMeshVertex2i end,
            BezierMeshFace2i left, BezierMeshFace2i right, EdgeData data, 
            int order)
    {
        this(mesh, start, end, left, right, data, order, 0, 0);
    }

    public BezierMeshEdge2i(BezierMesh2i mesh, 
            BezierMeshVertex2i start, BezierMeshVertex2i end,
            BezierMeshFace2i left, BezierMeshFace2i right, EdgeData data, 
            int order, int k0x, int k0y)
    {
        this(mesh, start, end, left, right, data, order, k0x, k0y, 0, 0);
    }
    
    public BezierMeshEdge2i(BezierMesh2i mesh, 
            BezierMeshVertex2i start, BezierMeshVertex2i end,
            BezierMeshFace2i left, BezierMeshFace2i right, EdgeData data, 
            int order, int k0x, int k0y, int k1x, int k1y)
    {
        this.mesh = mesh;
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
        int p0x = start.getCoord().x;
        int p0y = start.getCoord().y;
        int p1x = end.getCoord().x;
        int p1y = end.getCoord().y;
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

    public BezierMeshVertex2i splitAt(double t)
    {
        int p0x = start.getCoord().x;
        int p0y = start.getCoord().y;
        int p1x = end.getCoord().x;
        int p1y = end.getCoord().y;
        switch (order)
        {
            case 1:
            {
                return null;
            }
            case 2:
            {
                BezierLine2i c = new BezierLine2i(p0x, p0y, p1x, p1y);
                BezierLine2i[] segs = c.split(t);
                BezierMeshVertex2i v = new BezierMeshVertex2i(mesh, segs[0].getEndX(), segs[0].getEndY());
                BezierMeshEdge2i e1 = new BezierMeshEdge2i(mesh, v, end, left, right, data, 2);
                end.edgesIn.remove(this);
                end.edgesIn.add(e1);
                v.edgesIn.add(this);
                v.edgesOut.add(e1);
                return v;
            }
            case 3:
            {
                BezierQuad2i c = new BezierQuad2i(p0x, p0y, k0x, k0y, p1x, p1y);
                BezierQuad2i[] segs = c.split(t);
                BezierMeshVertex2i v = new BezierMeshVertex2i(mesh, segs[0].getEndX(), segs[0].getEndY());
                BezierMeshEdge2i e1 = new BezierMeshEdge2i(mesh, v, end, left, right, data, 3);
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
                BezierCubic2i[] segs = c.split(t);
                BezierMeshVertex2i v = new BezierMeshVertex2i(mesh, segs[0].getEndX(), segs[0].getEndY());
                BezierMeshEdge2i e1 = new BezierMeshEdge2i(mesh, v, end, left, right, data, 4);
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

    public int getMinX()
    {
        int value = Math.min(start.getCoord().x, end.getCoord().x);
        if (order >= 3)
        {
            value = Math.min(value, k0x);
        }
        if (order >= 4)
        {
            value = Math.min(value, k1x);
        }
        return value;
    }
    
    public int getMinY()
    {
        int value = Math.min(start.getCoord().y, end.getCoord().y);
        if (order >= 3)
        {
            value = Math.min(value, k0y);
        }
        if (order >= 4)
        {
            value = Math.min(value, k1y);
        }
        return value;
    }
    
    public int getMaxX()
    {
        int value = Math.max(start.getCoord().x, end.getCoord().x);
        if (order >= 3)
        {
            value = Math.max(value, k0x);
        }
        if (order >= 4)
        {
            value = Math.max(value, k1x);
        }
        return value;
    }
    
    public int getMaxY()
    {
        int value = Math.max(start.getCoord().y, end.getCoord().y);
        if (order >= 3)
        {
            value = Math.max(value, k0y);
        }
        if (order >= 4)
        {
            value = Math.max(value, k1y);
        }
        return value;
    }
    
    public boolean boundingBoxIntersects(BezierCurve2i curve)
    {
        return curve.getMaxX() >= getMinX()
                && curve.getMinX() <= getMaxX()
                && curve.getMaxY() >= getMinY()
                && curve.getMinY() <= getMaxY();
    }
    
}
