/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.kitfox.coyote.shape.bezier.mesh;

import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.shape.bezier.BezierCubic2i;
import com.kitfox.coyote.shape.bezier.BezierCurve2i;
import com.kitfox.coyote.shape.bezier.BezierCutCurve2i;
import com.kitfox.coyote.shape.bezier.BezierLine2i;
import com.kitfox.coyote.shape.bezier.BezierQuad2i;
import java.util.ArrayList;

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

    private ArrayList<BezierEdge2i> getEdges(ArrayList<BezierEdge2i> edges)
    {
        if (edges == null)
        {
            edges = new ArrayList<BezierEdge2i>();
        }
        
        BezierVertex2i v0 = head;
        do
        {
            BezierEdge2i e0 = v0.getEdgeOut(0);
            edges.add(e0);
            v0 = getNext(v0);
        } while (v0 != head);
        return edges;
    }
    
    public void cutAgainst(BezierLoop2i loop, int resolution)
    {
        ArrayList<BezierEdge2i> edges0 = getEdges(null);
        
        for (int i = 0; i < edges0.size(); ++i)
        {
            BezierEdge2i e0 = edges0.get(i);
            BezierCurve2i c0 = e0.asCurve();
            if (c0.isUnitBoundingBox())
            {
                continue;
            }
            
            ArrayList<BezierEdge2i> edges1 = loop.getEdges(null);
            for (BezierEdge2i e1: edges1)
            {
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
                CutRecord rec = null;
                if (c0.isColinear() && c1.isColinear())
                {
                    rec = cutLines(
                            e0, c0.getBaseline(), e1, c1.getBaseline());
                }
                else
                {
                    rec = cutCurves(e0, c0, e1, c1, resolution);
                }
                
                if (rec != null && rec.newSrc != null && rec.newSrc.length > 0)
                {
                    //We've cut the curve, replacing it with new curves.
                    // Update current source stack of curves to reflect
                    // new path.  Also update iteration pointers.
                    edges0.remove(i);
                    
                    for (int j = 0; j < rec.newSrc.length; ++j)
                    {
                        edges0.add(i + j, rec.newSrc[j]);
                    }
                    e0 = rec.newSrc[0];
                    c0 = e0.asCurve();
                }
            }
        }
    }
    
    private CutRecord cutCurves(
            BezierEdge2i e0, BezierCurve2i c0, 
            BezierEdge2i e1, BezierCurve2i c1,
            int resolution)
    {
        //Finds crossovers by subdividing down to a given resolution.
        // Does not go all the way to the smallest unit, since this would
        // allow for a lot of false positives due to round off error in
        // sections of the curve that are nearly tangent.
        //Curves that are near tangent or very small may not be cut in
        // all overlapping places.  Routines that rely on this should take
        // this into account.

        BezierCutCurve2i cut = new BezierCutCurve2i(c0, c1, resolution);
        
        BezierEdge2i[] newSrc = replaceEdge(e0, 
                    cut.getSegs0());
        BezierEdge2i[] newDst = replaceEdge(e1, 
                    cut.getSegs1());
        
        return new CutRecord(newSrc, newDst);

        //New plan for cutting loops:
        //First, cut all curve-curve and curve-line intersetcions.  
        // Ignore line-line intersections.  Don't
        // worry if there are points missed due to tangency or bumping into
        // minimum resolution.
        //Next, treat all segments as straight lines according to their base
        // lines.  Find all line-line intersections.
        //Determine interior/exterior sets for both shapes.  Use for boolean
        // ops.
        
        
    }
        
    private CutRecord cutLines(
            BezierEdge2i e0, BezierLine2i c0, BezierEdge2i e1, BezierLine2i c1)
    {
        if (c0.equals(c1))
        {
            return null;
        }
        
        //Renaming the points of the lines:
        // Line 0 runs from a to b
        // Line 1 runs from c to d
        
        int ax = c0.getStartX();
        int ay = c0.getStartY();
        int bx = c0.getEndX();
        int by = c0.getEndY();
        int cx = c1.getStartX();
        int cy = c1.getStartY();
        int dx = c1.getEndX();
        int dy = c1.getEndY();
        CyVector2d ab = new CyVector2d(bx - ax, by - ay);
        CyVector2d ac = new CyVector2d(cx - ax, cy - ay);
        CyVector2d ad = new CyVector2d(dx - ax, dy - ay);
        
        CyVector2d cd = new CyVector2d(dx - cx, dy - cy);
        CyVector2d ca = new CyVector2d(ax - cx, ay - cy);
        CyVector2d cb = new CyVector2d(bx - cx, by - cy);
        
        //Project ac, ad onto ab
        double acPabScalar = ac.dot(ab) / ab.dot(ab);
        CyVector2d acPab = new CyVector2d(ab.x * acPabScalar, ab.y * acPabScalar);
        double adPabScalar = ad.dot(ab) / ab.dot(ab);
        CyVector2d adPab = new CyVector2d(ab.x * adPabScalar, ab.y * adPabScalar);
        
        //Distances from c, d to line of ab
        double dist2c = ac.distanceSquared(acPab);
        double dist2d = ad.distanceSquared(adPab);

        double caPcdScalar = ca.dot(cd) / cd.dot(cd);
        CyVector2d caPcd = new CyVector2d(cd.x * caPcdScalar, cd.y * caPcdScalar);
        double cbPcdScalar = cb.dot(cd) / cd.dot(cd);
        CyVector2d cbPcd = new CyVector2d(cd.x * cbPcdScalar, cd.y * cbPcdScalar);
        
        //Dinstances from a, b to line cd
        double dist2a = ca.distanceSquared(caPcd);
        double dist2b = cb.distanceSquared(cbPcd);

        BezierEdge2i[] newSrc = null;
        BezierEdge2i[] newDst = null;

        boolean sameDir = ab.dot(cd) >= 0;
        
        //Divide ab
        if (dist2a <= 1 || dist2b <= 1 || dist2c <= 1 || dist2d <= 1)
        {
            //Split at C if a, b bound c
            boolean splitC = dist2c <= 1
                    && acPabScalar > 0 && acPabScalar < 1
                    && !(cx == ax && cy == ay) 
                    && !(cx == bx && cy == by);
            
            //Split at C if a, b bound d
            boolean splitD = dist2d <= 1
                    && adPabScalar > 0 && adPabScalar < 1
                    && !(dx == ax && dy == ay) 
                    && !(dx == bx && dy == by);

            //Update curve in this loop
            if (splitC && splitD)
            {
                if (sameDir)
                {
                    newSrc = replaceEdge(e0, 
                            new BezierLine2i(ax, ay, cx, cy),
                            new BezierLine2i(cx, cy, dx, dy),
                            new BezierLine2i(dx, dy, bx, by));
                }
                else
                {
                    newSrc = replaceEdge(e0, 
                            new BezierLine2i(ax, ay, dx, dy),
                            new BezierLine2i(dx, dy, cx, cy),
                            new BezierLine2i(cx, cy, bx, by));
                }
            }
            else if (splitC)
            {
                newSrc = replaceEdge(e0, 
                        new BezierLine2i(ax, ay, cx, cy),
                        new BezierLine2i(cx, cy, bx, by));
            }
            else if (splitD)
            {
                newSrc = replaceEdge(e0, 
                        new BezierLine2i(ax, ay, dx, dy),
                        new BezierLine2i(dx, dy, bx, by));
            }

        
            //Divide cd
            boolean splitA = dist2a <= 1
                    && caPcdScalar > 0 && caPcdScalar < 1
                    && !(cx == ax && cy == ay) 
                    && !(cx == bx && cy == by);
            
            boolean splitB = dist2b <= 1
                    && cbPcdScalar > 0 && cbPcdScalar < 1
                    && !(dx == ax && dy == ay) 
                    && !(dx == bx && dy == by);
            
            if (splitA && splitB)
            {
                if (sameDir)
                {
                    newDst = replaceEdge(e1, 
                            new BezierLine2i(cx, cy, ax, ay),
                            new BezierLine2i(ax, ay, bx, by),
                            new BezierLine2i(bx, by, dx, dy));
                }
                else
                {
                    newDst = replaceEdge(e1, 
                            new BezierLine2i(cx, cy, bx, by),
                            new BezierLine2i(bx, by, ax, ay),
                            new BezierLine2i(ax, ay, dx, dy));
                }
            }
            else if (splitA)
            {
                newDst = replaceEdge(e1, 
                        new BezierLine2i(cx, cy, ax, ay),
                        new BezierLine2i(ax, ay, dx, dy));
            }
            else if (splitB)
            {
                newDst = replaceEdge(e1, 
                        new BezierLine2i(cx, cy, bx, by),
                        new BezierLine2i(bx, by, dx, dy));
            }

            return new CutRecord(newSrc, newDst);
        }
        
        //Lines do not touch end-to-end or end-to-middle.
        // Solve a plain old linear system of equations
        double[] t = Math2DUtil.lineIsectFractions(
                ax, ay, bx - ax, by - ay, 
                cx, cy, dx - cx, dy - cy, null);
        
        if (t != null && t[0] > 0 && t[0] < 1 && t[1] > 0 && t[1] < 1)
        {
            CyVector2d pos = new CyVector2d();
            c0.evaluate(t[0], pos, null);
            int mx = (int)pos.x;
            int my = (int)pos.y;
            
            newSrc = replaceEdge(e0, 
                    new BezierLine2i(c0.getStartX(), c0.getStartY(), mx, my),
                    new BezierLine2i(mx, my, c0.getEndX(), c0.getEndY()));
            newDst = replaceEdge(e1, 
                    new BezierLine2i(c1.getStartX(), c1.getStartY(), mx, my),
                    new BezierLine2i(mx, my, c1.getEndX(), c1.getEndY()));
            return new CutRecord(newSrc, newDst);
        }
        
        return null;
    }
    
    /**
     * Segment an edge into the given pieces.  Pieces must be continuous
     * and start and end at the same points as the existing edge.
     * 
     * @param edge
     * @param curves 
     */
    private BezierEdge2i[] replaceEdge(BezierEdge2i edge, BezierCurve2i... curves)
    {
        BezierVertex2i vStart = edge.getStart();
        BezierVertex2i vEnd = edge.getEnd();
        vStart.edgesOut.remove(edge);
        vEnd.edgesIn.remove(edge);
        
        BezierEdge2i[] newEdges = new BezierEdge2i[curves.length];
        BezierEdge2i lastEdge = null;
        for (int i = 0; i < curves.length; ++i)
        {
            BezierCurve2i curve = curves[i];
            
            BezierVertex2i v0 = i == 0 ? vStart : lastEdge.getEnd();
            BezierVertex2i v1 = i == curves.length - 1 ? vEnd 
                    : new BezierVertex2i(curve.getEndX(), curve.getEndY());
            
            int degree = curve.getDegree();
            BezierEdge2i curEdge = new BezierEdge2i(
                    v0, v1, null, null, edge.getData(), 
                    degree);
            newEdges[i] = curEdge;
            
            switch (degree)
            {
                case 3:
                {
                    BezierQuad2i quad = (BezierQuad2i)curve;
                    curEdge.setK0x(quad.getAx1());
                    curEdge.setK0y(quad.getAy1());
                    break;
                }
                case 4:
                {
                    BezierCubic2i quad = (BezierCubic2i)curve;
                    curEdge.setK0x(quad.getAx1());
                    curEdge.setK0y(quad.getAy1());
                    curEdge.setK1x(quad.getAx2());
                    curEdge.setK1y(quad.getAy2());
                    break;
                }
            }
            
            //Connect
            v0.edgesOut.add(curEdge);
            v1.edgesIn.add(curEdge);
            lastEdge = curEdge;
        }
        
        return newEdges;
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

    //----------------------------
    private class CutRecord
    {
        BezierEdge2i[] newSrc;
        BezierEdge2i[] newDst;

        public CutRecord()
        {
        }

        public CutRecord(BezierEdge2i[] newSrc, BezierEdge2i[] newDst)
        {
            this.newSrc = newSrc;
            this.newDst = newDst;
        }
    }
    
}
