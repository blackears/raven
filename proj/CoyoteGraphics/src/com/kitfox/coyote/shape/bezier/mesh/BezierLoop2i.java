/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.kitfox.coyote.shape.bezier.mesh;

import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.shape.bezier.BezierCurve2i;
import com.kitfox.coyote.shape.bezier.BezierLine2i;

/**
 * A loop is a single chain of connected vertices.  Each vertex has 
 * exactly one edge segment leading from the previous vertex into it, 
 * and exactly one leading from it to the next vertex.
 * 
 * The edge from the last to the first vertex bears special consideration.
 * While this special edge is always present in the loop, the current
 * closure will affect how it is interpreted.  If closure is OPEN, 
 * this segment will be ignored and the loop will be treated as if
 * the ends do not connect at all.  If closure is CLOSED_FREE, this
 * edge segment is used to connect the last to the first vertex.
 * If closure is CLOSED_CLAMPED, the the last vertex
 * is treated as if it was coincident with the first vertex.  This
 * special merged vertex inherits position, smoothing and the edge out
 * from the head vertex, and the edge in from the tail vertex.  The
 * special tail-to-head edge is ignored.
 *
 * @author kitfox
 */
public class BezierLoop2i<VertexData, EdgeData>
{
    private BezierVertex2i head;
    private BezierVertex2i tail;
    private BezierLoopClosure closure;

    public BezierLoop2i(int x, int y)
    {
        this.head = this.tail = new BezierVertex2i(x, y);
        closure = BezierLoopClosure.OPEN;
        BezierEdge2i e = new BezierEdge2i(tail, head, null, null, null, 2);
        
        //Start with closing segment
        this.head.edgesOut.add(e);
        this.head.edgesIn.add(e);        
    }
    
    private static BezierVertex2i getPrev(BezierVertex2i v)
    {
        BezierEdge2i e = (BezierEdge2i)v.edgesIn.get(0);
        return e.getStart();
    }
    
    private static BezierVertex2i getNext(BezierVertex2i v)
    {
        BezierEdge2i e = (BezierEdge2i)v.edgesOut.get(0);
        return e.getEnd();
    }
    
    public void appendLine(int x, int y)
    {
        append(0, 0, 0, 0, x, y, 2);
    }
    
    public void appendQuad(int k0x, int k0y, int x, int y)
    {
        append(k0x, k0y, 0, 0, x, y, 3);
    }
    
    public void appendCubic(int k0x, int k0y, int k1x, int k1y, int x, int y)
    {
        append(k0x, k0y, k1x, k1y, x, y, 4);
    }
    
    private void append(int k0x, int k0y, int k1x, int k1y, 
            int x, int y, int degree)
    {
        BezierVertex2i v1 = new BezierVertex2i(x, y);
        BezierEdge2i e = new BezierEdge2i(tail, v1, null, null, null, 
                degree, k0x, k0y, k1x, k1y);
        
        //BezierVertex2i prev = getPrev(tail);
        BezierEdge2i loopEdge = (BezierEdge2i)tail.edgesOut.remove(0);
        tail.edgesOut.add(e);
        v1.edgesIn.add(e);
        v1.edgesOut.add(loopEdge);
        
        loopEdge.setStart(v1);
        tail = v1;
    }

    public boolean endPointsOverlap()
    {
        return head.getX() == tail.getX() && head.getY() == tail.getY();
    }

    public void cutAgainst(BezierLoop2i loop)
    {
        BezierVertex2i v0 = head;
        do
        {
            BezierEdge2i e0 = v0.getEdgeOut(0);
            BezierCurve2i c0 = e0.asCurve();
            if (c0.isUnitBoundingBox())
            {
                continue;
            }
            
            BezierVertex2i v1 = loop.head;
            do
            {
                BezierEdge2i e1 = v1.getEdgeOut(0);
                BezierCurve2i c1 = e1.asCurve();
                
                if (c1.isUnitBoundingBox())
                {
                    continue;
                }
                
                if (!c0.boundingBoxIntersects(c1))
                {
                    continue;
                }
                
                if (c0.equals(c1))
                {
                    continue;
                }
                
                //Check for crossover
                if (c0.isColinear() && c1.isColinear())
                {
//                    cutLines(e0, c0.getBaseline(), e1, c1.getBaseline());
                }
                else
                {
//                    cutCurves(e0, c0, e1, c1);
                }
                
                v1 = getNext(v1);
            } while (v1 != head);
            v0 = getNext(v0);
        } while (v0 != head);
        //for (BezierVertex2i v0 = head; 
    }

    /*
    private void cutLines(BezierEdge2i e0, BezierLine2i b0, BezierEdge2i e1, BezierLine2i b1)
    {
        if (b0.equals(b1))
        {
            return;
        }
        
        //Renaming the points of the lines:
        // Line 0 runs from a to b
        // Line 1 runs from c to d
        
        CyVector2d ab = new CyVector2d(b0.getEndX() - b0.getStartX(), b0.getEndY() - b0.getStartY());
        CyVector2d ac = new CyVector2d(b1.getStartX() - b0.getStartX(), b1.getStartY() - b0.getStartY());
        CyVector2d ad = new CyVector2d(b1.getEndX() - b0.getStartX(), b1.getEndY() - b0.getStartY());
        
        //Project ac, ad onto ab
        double acPabScalar = ac.dot(ab) / ab.dot(ab);
        CyVector2d acPab = new CyVector2d(ab.x * acPabScalar, ab.y * acPabScalar);
        double adPabScalar = ad.dot(ab) / ab.dot(ab);
        CyVector2d adPab = new CyVector2d(ab.x * adPabScalar, ab.y * adPabScalar);
        
        double dist2Ac = ac.distanceSquared(acPab);
        double dist2Ad = ad.distanceSquared(adPab);
        
        if (b0.getStartX() == b1.getStartX() 
                && b0.getStartY() == b1.getStartY())
        {
            if (dist2Ad <= 1)
            {
                //Close enough to be parallel
                if (adPabScalar > 0 && adPabScalar < 1)
                {
                    //ad shorter than ab
                    e0.setDegree(2);
                    //Arbitrarily add vertex
                    BezierVertex2i v = e0.splitAt(.5);
                    v.setX(b1.getEndX());
                    v.setY(b1.getEndY());
                    return;
                }
                else if (adPabScalar > 1)
                {
                    //ab shorter than ad
                    e1.setDegree(2);
                    //Arbitrarily add vertex
                    BezierVertex2i v = e1.splitAt(.5);
                    v.setX(b0.getEndX());
                    v.setY(b0.getEndY());
                    return;                    
                }
            }
            
            //Lines are not coincident
            return;
        }
        
        
        
        CyVector2d r0 = new CyVector2d(b0.getTanInX(), b0.getTanInY());
        CyVector2d r1 = new CyVector2d(b1.getTanInX(), b1.getTanInY());
        
        CyVector2d rb00b10 = new CyVector2d(b1.getStartX() - b0.getStartX(), b1.getStartY() - b0.getStartY());
        CyVector2d rb00b11 = new CyVector2d(b1.getEndX() - b0.getStartX(), b1.getEndY() - b0.getStartY());
        
        if (b0.getStartX() == b1.getStartX() && b0.getStartY() == b1.getStartY())
        {
            
        }
        
        CyVector2d b0p0 = new CyVector2d(b0.get);
        
        int dx0 = b0.getTanInX();
        int dy0 = b0.getTanInY();
        int dx1 = b0.getTanInX();
        int dy1 = b0.getTanInY();
        
        double projScalar0 = Math2DUtil.dot(dx0, dy0, dx1, dy1)
                / Math2DUtil.dot(dx0, dy0, dx0, dy0);
        double projScalar1 = Math2DUtil.dot(dx0, dy0, dx1, dy1)
                / Math2DUtil.dot(dx1, dy1, dx1, dy1);
        
        //Find projection of line 1 onto line 0
        double dx0p = dx0 * projScalar0;
        double dy0p = dy0 * projScalar0;
        
        
        
        
        double d0 = Math2DUtil.distPointLineSquared(b0.getStartX(), b0.getStartY(), 
                b1.getStartX(), b1.getStartY(), b1.getTanInX(), b1.getTanInY());
        double d1 = Math2DUtil.distPointLineSquared(b1.getStartX(), b1.getStartY(), 
                b1.getStartX(), b1.getStartY(), b1.getTanInX(), b1.getTanInY());

        if (d0 <= 1 && d1 <= 1)
        {
            //Treat lines as parallel and coincident
            
        }
    }
    */
    
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
    
}
