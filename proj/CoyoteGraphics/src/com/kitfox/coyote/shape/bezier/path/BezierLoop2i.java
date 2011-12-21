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

import com.kitfox.coyote.shape.bezier.path.cut.BezierPathCutter2i;


/**
 *
 * @author kitfox
 */
public class BezierLoop2i<VertexData, EdgeData>
{
    BezierPathVertex2i head;
    BezierPathVertex2i tail;
    private BezierLoopClosure closure;

    public BezierLoop2i(int x, int y)
    {
        this.head = this.tail = new BezierPathVertex2i(x, y);
        closure = BezierLoopClosure.OPEN;
        BezierPathEdge2i e = new BezierPathEdge2i(tail, head, 
                null);
        
        //Start with closing segment
        this.head.edgeOut = e;
        this.head.edgeIn = e;        
    }
    
    public BezierPathEdge2i appendLine(int x, int y)
    {
        return append(0, 0, 0, 0, x, y, 2);
    }
    
    public BezierPathEdge2i appendQuad(int k0x, int k0y, int x, int y)
    {
        return append(k0x, k0y, 0, 0, x, y, 3);
    }
    
    public BezierPathEdge2i appendCubic(int k0x, int k0y, int k1x, int k1y, int x, int y)
    {
        return append(k0x, k0y, k1x, k1y, x, y, 4);
    }
    
    private BezierPathEdge2i append(int k0x, int k0y, int k1x, int k1y, 
            int x, int y, int order)
    {
        BezierPathVertex2i v1 = new BezierPathVertex2i(x, y);
        BezierPathEdge2i e = new BezierPathEdge2i(tail, v1, null, 
                order, k0x, k0y, k1x, k1y);
        
        //BezierVertex2i prev = getPrev(tail);
        BezierPathEdge2i loopEdge = tail.edgeOut;
        loopEdge.setStart(v1);
        v1.edgeOut = loopEdge;
        v1.edgeIn = e;
        tail.edgeOut = e;
        
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
            BezierPathEdge2i e = v.edgeOut;
            
            if (!e.isPoint())
            {
                cut.addEdge(e.asCurve(), e.getData());
            }
            
            v = e.getEnd();
        } while(v != head);
    }
    
}
