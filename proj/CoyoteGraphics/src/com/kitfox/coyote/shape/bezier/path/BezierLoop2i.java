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

import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.CyRectangle2i;
import com.kitfox.coyote.shape.bezier.BezierCubic2i;
import com.kitfox.coyote.shape.bezier.BezierLine2i;
import com.kitfox.coyote.shape.bezier.BezierQuad2i;
import com.kitfox.coyote.shape.bezier.mesh.BezierVertexSmooth;
import com.kitfox.coyote.shape.bezier.path.cut.BezierPathCutter2i;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.util.ArrayList;


/**
 * A loop defines a continuous set of curves within a path.  Loops
 * run from the first vertex (head) to the last (tail).
 * 
 * All loops contain a final segment connecting the tail back to the
 * head.  The closure of the loop indicates if this final section is
 * considered part of the body of the loop.
 *
 * @author kitfox
 */
public class BezierLoop2i<VertexData, EdgeData>
{
    private final int id;
    BezierPath2i path;
    //final GraphDataManager dataMgr;
    private BezierPathVertex2i head;
    private BezierPathVertex2i tail;
    private BezierLoopClosure closure;

    BezierLoop2i(
            int id,
            BezierPath2i path,
            BezierPathVertex2i head, 
            BezierPathVertex2i tail, 
            BezierLoopClosure closure)
    {
        this.id = id;
        this.path = path;
        this.head = head;
        this.tail = tail;
        this.closure = closure;
    }

    BezierLoop2i(
            int id,
            BezierPath2i path,
            BezierPathVertex2i v)
    {
        this.id = id;
        this.path = path;
        
        this.head = this.tail = v;
        closure = BezierLoopClosure.OPEN;
        
        BezierPathEdge2i e = path.createEdge(tail, head);
        
        //Start with closing segment
        this.head.setEdgeOut(e);
        this.head.setEdgeIn(e);        
    }

    BezierLoop2i(
            int id,
            BezierPath2i path,
            BezierPathVertex2i v0,
            BezierPathVertex2i v1)
    {
        this.id = id;
        this.path = path;
        
        this.head = v0;
        this.tail = v1;
        closure = BezierLoopClosure.OPEN;
        
        BezierPathEdge2i e = path.createEdge(tail, head);
        
        //Start with closing segment
//        this.head.setEdgeIn(e);
//        this.tail.setEdgeOut(e); 
        
        v0.setEdgeIn(e);
        v1.setEdgeOut(e);
    }

    public boolean isEmpty()
    {
        return head == null;
    }
    
    public BezierPathEdge2i lineTo(int x, int y)
    {
        BezierLine2i quad = new BezierLine2i(tail.getX(), tail.getY(),
                x, y);
        return append(BezierVertexSmooth.CORNER,
                BezierVertexSmooth.CORNER,
                quad.asCubic());
    }
    
    public BezierPathEdge2i quadTo(int k0x, int k0y, int x, int y)
    {
        BezierQuad2i quad = new BezierQuad2i(tail.getX(), tail.getY(),
                k0x, k0y, x, y);
        return append(BezierVertexSmooth.SMOOTH,
                BezierVertexSmooth.SMOOTH,
                quad.asCubic());
    }
    
    public BezierPathEdge2i cubicTo(int k0x, int k0y, int k1x, int k1y, int x, int y)
    {
        BezierCubic2i cubic = new BezierCubic2i(tail.getX(), tail.getY(),
                k0x, k0y, k1x, k1y, x, y);
        return append(BezierVertexSmooth.SMOOTH,
                BezierVertexSmooth.SMOOTH,
                cubic);
    }
    
    public BezierPathEdge2i append(
            BezierVertexSmooth smooth0, BezierVertexSmooth smooth1,
            BezierCubic2i curve)
    {
        int k0x = curve.getAx1();
        int k0y = curve.getAy1();
        int k1x = curve.getAx2();
        int k1y = curve.getAy2();
        int x = curve.getAx3();
        int y = curve.getAy3();
        
        BezierPathVertex2i v1 = path.createVertex(x, y);
        BezierPathEdge2i e = path.createEdge(
                tail, v1, 
                path.dataMgr.createDefaultEdgeData(curve), 
                smooth0, smooth1,
                k0x, k0y, k1x, k1y);
        
        //BezierVertex2i prev = getPrev(tail);
        BezierPathEdge2i loopEdge = tail.getEdgeOut();
        loopEdge.setStart(v1);
        v1.setEdgeOut(loopEdge);
        v1.setEdgeIn(e);
        tail.setEdgeOut(e);
        
        tail = v1;
        
        return e;
    }

    public boolean endPointsOverlap()
    {
        return head.getCoord().equals(tail.getCoord());
    }

    /**
     * @return the closure
     */
    public BezierLoopClosure getClosure()
    {
        return closure;
    }

    /**
     * @param closure the closure to set
     */
    public void setClosure(BezierLoopClosure closure)
    {
        this.closure = closure;
    }

    public void appendToCutGraph(BezierPathCutter2i cut)
    {
        //In cut graph, loops are always closed
        BezierPathVertex2i v = head;
        
        do
        {
            BezierPathEdge2i e = v.getEdgeOut();
            
            if (!e.isPoint())
            {
                cut.addEdge(e.asCurve(), e.getData());
            }
            
            v = e.getEnd();
        } while(v != head);
    }

    public CyRectangle2i getBounds()
    {
        BezierPathVertex2i v = head;
        Coord c = v.getCoord();
        CyRectangle2i bounds = new CyRectangle2i(c.x, c.y);
        
        do
        {
            v = v.getEdgeOut().getEnd();
            c = v.getCoord();
            
            bounds.union(c.x, c.y);
        }
        while (v != head);
        
        return bounds;
    }

    /**
     * @return the head
     */
    public BezierPathVertex2i getHead()
    {
        return head;
    }

    void setHead(BezierPathVertex2i v)
    {
        this.head = v;
    }

    /**
     * @return the tail
     */
    public BezierPathVertex2i getTail()
    {
        return tail;
    }

    void setTail(BezierPathVertex2i v)
    {
        this.tail = v;
    }

    public void appendToPath(CyPath2d path)
    {
        BezierPathVertex2i v = head;
        Coord c = v.getCoord();
        path.moveTo(c.x, c.y);
        
        do
        {
            BezierPathEdge2i e = v.getEdgeOut();
//if (e == null)
//{
//    int j = 9;
//}
            e.asCurve().append(path);
            v = e.getEnd();
        }
        while (v != tail);
    }

    public ArrayList<BezierPathVertex2i> getVertices()
    {
        ArrayList<BezierPathVertex2i> list
                = new ArrayList<BezierPathVertex2i>();
        appendVertices(list);
        return list;
    }

    public void appendVertices(ArrayList<BezierPathVertex2i> list)
    {
        BezierPathVertex2i v = head;
        list.add(v);
        
        do
        {
            BezierPathEdge2i e = v.getEdgeOut();
            BezierPathVertex2i v1 = e.getEnd();
            list.add(v1);
            v = v1;
        }
        while (v != head);
    }

    public ArrayList<BezierPathEdge2i> getEdges()
    {
        ArrayList<BezierPathEdge2i> list
                = new ArrayList<BezierPathEdge2i>();
        appendEdges(list);
        return list;
    }

    public void appendEdges(ArrayList<BezierPathEdge2i> list)
    {
        BezierPathVertex2i v = head;
        
        do
        {
            BezierPathEdge2i e = v.getEdgeOut();
            list.add(e);
            v = e.getEnd();
        }
        while (v != head);
    }

    public boolean containsEdge(BezierPathEdge2i e)
    {
        BezierPathVertex2i v = head;
        
        do
        {
            BezierPathEdge2i e1 = v.getEdgeOut();
            if (e1 == e)
            {
                return true;
            }
            
            v = e1.getEnd();
        }
        while (v != head);
        
        return false;
    }

    public boolean containsVertex(BezierPathVertex2i v0)
    {
        BezierPathVertex2i v1 = head;
        
        do
        {
            BezierPathEdge2i e1 = v1.getEdgeOut();
            if (v1 == v0)
            {
                return true;
            }
            
            v1 = e1.getEnd();
        }
        while (v1 != head);
        
        return false;
    }

    /**
     * @return the id
     */
    public int getId()
    {
        return id;
    }

    public boolean isOnlyOneVertex()
    {
        return head == tail;
    }
    
}
