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

package com.kitfox.coyote.shape.tessellator;

import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.math.Math2DUtil;
import static com.kitfox.coyote.math.Math2DUtil.*;
import com.kitfox.coyote.shape.PathConsumer;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * The math here is a little tricky, so here are some pointers:
 *
 * The coordinate space used is OpenGL style, so that X and Y values increase
 * as you travel right and up across the page.
 *
 * The key to this system is calculating the winding levels of each edge.  All
 * points in an empty plane have a winding level of 0.  When you add a shape to
 * the plane, you partition it into the points inside and outside of the shape.
 * Each edge of the shape has a direction, and the overall shape has a winding
 * traveling clockwise or counterclockwise within the plane.  Points bounded
 * by regions moving counter clockwise are considered to have a winding 1
 * greater than that of their exterior points.  Clockwise bounded regions have
 * a winding 1 less.
 *
 * Since each line divides points into two different winding levels, it is
 * sometimes useful to think of the right and left sides of the line as
 * separate objects.  The edge is assigned a winding weight that depends on
 * what the winding weight of the points on its right side.  Since CCW bounded
 * regions have a +1 winding, the left side will always have a winding weight
 * 1 greater than the right side.  Further, while the right half-edge travels
 * the same way as the edge - from tail to head, the left half-edge is considered
 * to travel in the reverse direction.  This is so that if you make a complete
 * graph of all half-edges, every region of the same weight will be bounded
 * by half edges traveling in a CW direction.  (Except for the outermost boundary
 * which will be CCW).
 *
 * @author kitfox
 */
@Deprecated
public class PathTessellator extends PathConsumer
{
    double mx, my;
    double bx, by;
    boolean drawingPath;

    HashMap<TessPoint, TessVertex> vertMap = new HashMap<TessPoint, TessVertex>();
    
    //Ordered list of vertices
    ArrayList<TessPoint> pointList = new ArrayList<TessPoint>();
    ArrayList<TessSeg> segList = new ArrayList<TessSeg>();

    final static double EPSILON = .0000001;

    HashMap<Integer, ContourSet> ctrSets;

    @Override
    public void beginPath()
    {
    }

    @Override
    public void beginSubpath(double x0, double y0)
    {
        if (drawingPath)
        {
            closeSubpath();
        }

        bx = mx = x0;
        by = my = y0;
        drawingPath = true;
    }

    @Override
    public void lineTo(double x0, double y0)
    {
        if (!drawingPath)
        {
            beginSubpath(mx, my);
        }

        if (mx == x0 && my == y0)
        {
            return;
        }

        addLine(mx, my, x0, y0);
        mx = x0;
        my = y0;
    }

    @Override
    public void quadTo(double x0, double y0, double x1, double y1)
    {
        throw new UnsupportedOperationException("Cannot handle curves");
    }

    @Override
    public void cubicTo(double x0, double y0, double x1, double y1, double x2, double y2)
    {
        throw new UnsupportedOperationException("Cannot handle curves");
    }

    @Override
    public void closeSubpath()
    {
        drawingPath = false;
        
        if (mx == bx && my == by)
        {
            return;
        }

        addLine(mx, my, bx, by);
        mx = bx;
        my = by;
    }

    @Override
    public void endPath()
    {
        if (drawingPath)
        {
            closeSubpath();
        }

        splitCrossingLines();
        connectInteriorPaths();
        calcWindingLevels();
        //Contour ctr = buildContour();
        ctrSets = buildContourSets();
    }

    /**
     * Called after endPath().  Retrieves tessellated triangles.
     */
    public ArrayList<CyVector2d> getTrianglesNonZero()
    {
        ArrayList<CyVector2d> res = new ArrayList<CyVector2d>();

        for (Integer level: ctrSets.keySet())
        {
            if (level != 0)
            {
                ContourSet set = ctrSets.get(level);
                for (Contour ctr: set.contours)
                {
ctr.dump(System.err);
                    ArrayList<CyVector2d> loop = ctr.getContour();
System.err.println("Tess start");
                    EarClip.tessellate(loop, res);
System.err.println("Tess done");
                }
            }
        }

        return res;


//        ContourSet set = ctrSets.get(0);
//        Contour ctr = set.getSingleContour();
//
//        for (TessHalfEdge edge: ctr.edges)
//        {
//            TessPoint pt = edge.getTail().point;
//            res.add(new CyVector2d(pt.x, pt.y));
//        }
//        return res;
    }

    /**
     * Called after endPath().  Retrieves tessellated triangles.
     */
    public ArrayList<CyVector2d> getTrianglesOdd()
    {
        ArrayList<CyVector2d> res = new ArrayList<CyVector2d>();

        for (Integer level: ctrSets.keySet())
        {
            if ((level & 0x1) == 0x1)
            {
                ContourSet set = ctrSets.get(level);
                for (Contour ctr: set.contours)
                {
                    ArrayList<CyVector2d> loop = ctr.getContour();
                    EarClip.tessellate(loop, res);
                }
            }
        }

        return res;
    }

    private TessVertex getOrCreateVertex(TessPoint pt)
    {
        TessVertex v = vertMap.get(pt);
        if (v == null)
        {
            v = new TessVertex(pt);
            vertMap.put(pt, v);
        }
        return v;
    }

    private TessPoint addOrClampPoint(double x, double y)
    {
        for (TessPoint p1: pointList)
        {
            double px = p1.getX();
            double py = p1.getY();
            if (distSquared(x, y, px, py) < EPSILON)
            {
                return p1;
            }
        }
        
        TessPoint p = new TessPoint(x, y);
        pointList.add(p);
        return p;
    }

    private void addLine(double x0, double y0, double x1, double y1)
    {
        TessPoint p0 = addOrClampPoint(x0, y0);
        TessPoint p1 = addOrClampPoint(x1, y1);
        
        segList.add(new TessSeg(p0, p1));
    }
    
    private void splitSeg(TessSeg s, TessPoint p0, TessPoint p1, ArrayList<TessSeg> splits)
    {
        double s0x = s.pt0.x;
        double s0y = s.pt0.y;
        double s1x = s.pt1.x;
        double s1y = s.pt1.y;
        double dsx = s1x - s0x;
        double dsy = s1y - s0y;
        
        double p0x = p0.x;
        double p0y = p0.y;
        double p1x = p1.x;
        double p1y = p1.y;

        double frac0 = Math2DUtil.fractionAlongRay(p0x, p0y, s0x, s0y, dsx, dsy);
        double dist0 = Math2DUtil.distPointLineSigned(p0x, p0y, s0x, s0y, dsx, dsy);
        double frac1 = Math2DUtil.fractionAlongRay(p1x, p1y, s0x, s0y, dsx, dsy);
        double dist1 = Math2DUtil.distPointLineSigned(p1x, p1y, s0x, s0y, dsx, dsy);
        
        boolean cut0 = !s.pt0.equals(p0) && s.pt1.equals(p0) 
                && dist0 < EPSILON && frac0 > 0 && frac0 < 1;
        boolean cut1 = !s.pt0.equals(p1) && s.pt1.equals(p1) 
                && dist1 < EPSILON && frac1 > 0 && frac1 < 1;
        
        if (cut0 && cut1)
        {
            TessPoint c0 = addOrClampPoint(Math2DUtil.lerp(p0x, p1x, frac0),
                    Math2DUtil.lerp(p0y, p1y, frac0));
            TessPoint c1 = addOrClampPoint(Math2DUtil.lerp(p0x, p1x, frac1),
                    Math2DUtil.lerp(p0y, p1y, frac1));
            
            if (frac0 < frac1)
            {
                splits.add(new TessSeg(s.pt0, c0));
                splits.add(new TessSeg(c0, c1));
                splits.add(new TessSeg(c1, s.pt1));
            }
            else
            {
                splits.add(new TessSeg(s.pt0, c1));
                splits.add(new TessSeg(c1, c0));
                splits.add(new TessSeg(c0, s.pt1));
            }
        }
        else if (cut0)
        {
            TessPoint c0 = addOrClampPoint(Math2DUtil.lerp(p0x, p1x, frac0),
                    Math2DUtil.lerp(p0y, p1y, frac0));
            
            splits.add(new TessSeg(s.pt0, c0));
            splits.add(new TessSeg(c0, s.pt1));
        }
        else if (cut1)
        {
            TessPoint c1 = addOrClampPoint(Math2DUtil.lerp(p0x, p1x, frac1),
                    Math2DUtil.lerp(p0y, p1y, frac1));
            
            splits.add(new TessSeg(s.pt0, c1));
            splits.add(new TessSeg(c1, s.pt1));
        }
    }
    
    private ArrayList<TessSeg> splitSegs(TessSeg s0, TessSeg s1)
    {
        if (!s0.boundsOverlap(s1))
        {
            return null;
        }
        
        ArrayList<TessSeg> splits = new ArrayList<TessSeg>();
        
        //Check for line/point cross
        splitSeg(s0, s1.pt0, s1.pt1, splits);
        splitSeg(s1, s0.pt0, s0.pt1, splits);
        
        if (!splits.isEmpty())
        {
            return splits;
        }
        
        if (s0.pt0.equals(s1.pt0)
                || s0.pt0.equals(s1.pt1)
                || s0.pt1.equals(s1.pt0)
                || s0.pt1.equals(s1.pt1))
        {
            //Lines meet at end points
            return null;
        }
        
        //Check for midpoint cross
        double p0x = s0.pt0.x;
        double p0y = s0.pt0.y;
        double p1x = s0.pt1.x;
        double p1y = s0.pt1.y;
        double q0x = s1.pt0.x;
        double q0y = s1.pt0.y;
        double q1x = s1.pt1.x;
        double q1y = s1.pt1.y;
        
        double[] frac = 
                Math2DUtil.lineIsectFractions(
                p0x, p0y, p1x - p0x, p1y - p0y, 
                q0x, q0y, q1x - q0x, q1y - q0y, 
                null);
        
        if (frac != null 
                && frac[0] > 0 && frac[0] < 1
                && frac[1] > 0 && frac[1] < 1)
        {
            double mx = Math2DUtil.lerp(p0x, p1x, frac[0]);
            double my = Math2DUtil.lerp(p0y, p1y, frac[0]);
            
            TessPoint mp = addOrClampPoint(mx, my);
            splits.add(new TessSeg(s0.pt0, mp));
            splits.add(new TessSeg(mp, s0.pt1));
            splits.add(new TessSeg(s1.pt0, mp));
            splits.add(new TessSeg(mp, s1.pt1));
        }
        
        return splits.isEmpty() ? null : splits;
    }
    
    private void splitCrossingLines()
    {
        ArrayList<TessSeg> segsClear = new ArrayList<TessSeg>();
        
        NEXT_SEG:
        while (!segList.isEmpty())
        {
            TessSeg curEdge = segList.remove(segList.size() - 1);
            
            for (int j = 0; j < segList.size(); ++j)
            {
                TessSeg testEdge = segList.get(j);
                
                ArrayList<TessSeg> split = splitSegs(curEdge, testEdge);
                if (split != null && !split.isEmpty())
                {
                    segList.remove(j);
                    segList.addAll(split);
                    continue NEXT_SEG;
                }
            }
            
            //No conflicts
            segsClear.add(curEdge);
        }

        //Build graph from cut segments
        for (TessSeg seg: segsClear)
        {
            //Ignore zero length lines
            if (seg.pt0.equals(seg.pt1))
            {
                continue;
            }
            
            TessVertex vv0 = getOrCreateVertex(seg.pt0);
            TessVertex vv1 = getOrCreateVertex(seg.pt1);
            TessEdge line = new TessEdge(vv0, vv1);
            vv0.edgeOut.add(line);
            vv1.edgeIn.add(line);
        }
    }
    
    /*
    private void addLine(double x0, double y0, double x1, double y1)
    {
        double[] lineFrac = new double[2];

//        boolean snapped = false;

        //Snap to existing vertices
        for (TessVertex v: vertList)
        {
            double px = v.point.getX();
            double py = v.point.getY();

//            if (pointEquals(x0, y0, px, py) || pointEquals(x1, y1, px, py))
//            {
//                continue;
//            }

            //If close to existing vertex, snap to it
            if (distSquared(x0, y0, px, py) < EPSILON)
            {
                x0 = px;
                y0 = py;
            }

            if (distSquared(x1, y1, px, py) < EPSILON)
            {
                x1 = px;
                y1 = py;
            }
        }

        for (TessPoint pt: vertMap.keySet())
        {
            double px = pt.getX();
            double py = pt.getY();

            if (pointEquals(px, py, x0, y0) || pointEquals(px, py, x1, y1))
            {
                continue;
            }

            //If we pass close to an existing vertex, subdivide
            if (distPointLineSquared(px, py, x0, y0, x1 - x0, y1 - y0)
                    < EPSILON)
            {
                double frac = fractionAlongRay(px, py, x0, y0, x1 - x0, y1 - y0);
                if (isOn01(frac))
                {
                    //Add subdivided line instead
                    double xx = lerp(x0, x1, frac);
                    double yy = lerp(y0, y1, frac);
                    addLine(x0, y0, xx, yy);
                    addLine(xx, yy, x1, y1);
                    return;
                }
            }
        }

        for (TessPoint pt: vertMap.keySet())
        {
            //Check if we intersect an existing line
            TessVertex v0 = vertMap.get(pt);
            for (int i = 0; i < v0.edgeOut.size(); ++i)
            {
                TessEdge line = v0.edgeOut.get(i);
                double px0 = line.p0.point.getX();
                double py0 = line.p0.point.getY();
                double px1 = line.p1.point.getX();
                double py1 = line.p1.point.getY();

                if (pointEquals(x0, y0, px0, py0)
                        || pointEquals(x0, y0, px1, py1)
                        || pointEquals(x1, y1, px0, py0)
                        || pointEquals(x1, y1, px1, py1))
                {
                    continue;
                }

                double[] fracRes = lineIsectFractions(x0, y0, x1 - x0, y1 - y0,
                        px0, py0, px1 - px0, py1 - py0, lineFrac);
                if (fracRes != null && isOn01(fracRes[0]) && isOn01(fracRes[1]))
                {
                    //Break existing line
                    v0.edgeOut.remove(line);
                    TessVertex v1 = vertMap.get(line.p1.point);
                    v1.edgeIn.remove(line);

                    double pxc = lerp(px0, px1, lineFrac[1]);
                    double pyc = lerp(py0, py1, lineFrac[1]);
                    TessVertex vc = getOrCreateVertex(pxc, pyc);
                    TessEdge line0 = new TessEdge(v0, vc);
                    TessEdge line1 = new TessEdge(vc, v1);

                    v0.edgeOut.add(line0);
                    vc.edgeIn.add(line0);
                    vc.edgeOut.add(line1);
                    v1.edgeIn.add(line1);

                    //Add broken new line
                    addLine(x0, y0, pxc, pyc);
                    addLine(pxc, pyc, x1, y1);
                    return;
                }
            }
        }

        //If we got here, we have no conflicts with existing lines
        TessVertex vv0 = getOrCreateVertex(x0, y0);
        TessVertex vv1 = getOrCreateVertex(x1, y1);
        TessEdge line = new TessEdge(vv0, vv1);
        vv0.edgeOut.add(line);
        vv1.edgeIn.add(line);
    }
*/
    private boolean isOn01(double value)
    {
        return 0 <= value && value <= 1;
    }

    private void connectInteriorPaths()
    {
        //At this point, all lines have been inserted.  None of them
        // overlap.  Every vertex has equal numbers of input and
        // output lines.

        //Need to add extra lines to ensure that a path exists from any
        // vertex to any other vertex.

        //Partition into connected sets
        ArrayList<VertexSet> setList = new ArrayList<VertexSet>();
        HashMap<TessVertex, VertexSet> setMap = new HashMap<TessVertex, VertexSet>();
        for (TessVertex vtx: vertMap.values())
        {
            for (TessEdge edge: vtx.edgeOut)
            {
                TessVertex v0 = edge.p0;
                TessVertex v1 = edge.p1;
                VertexSet vs0 = setMap.get(v0);
                VertexSet vs1 = setMap.get(v1);

                if (vs0 == null && vs1 == null)
                {
                    //Create new set with line's vertices
                    VertexSet set = new VertexSet(v0, v1);
                    setMap.put(v0, set);
                    setMap.put(v1, set);
                    setList.add(set);
                }
                else if (vs0 == null)
                {
                    //Merge free vertex into other set
                    vs1.set.add(v0);
                    setMap.put(v0, vs1);
                }
                else if (vs1 == null)
                {
                    //Merge free vertex into other set
                    vs0.set.add(v1);
                    setMap.put(v1, vs0);
                }
                else if (vs1 != vs0)
                {
                    //Merge vs1 into vs0
                    vs0.set.addAll(vs1.set);
                    for (TessVertex xferVert: vs1.set)
                    {
                        setMap.put(xferVert, vs0);
                    }
                    setList.remove(vs1);
                }
            }
        }

        //Connect sets
        for (int i = 0; i < setList.size(); ++i)
        {
            VertexSet vs0 = setList.get(i);
            for (int j = i + 1; j < setList.size(); ++j)
            {
                VertexSet vs1 = setList.get(j);

                double bestDist = Double.POSITIVE_INFINITY;
                TessVertex bestV0 = null;
                TessVertex bestV1 = null;
                for (TessVertex v0: vs0.set)
                {
                    for (TessVertex v1: vs1.set)
                    {
                        double px0 = v0.point.getX();
                        double py0 = v0.point.getY();
                        double px1 = v1.point.getX();
                        double py1 = v1.point.getY();
                        if (!lineIntersects(px0, py0, px1, py1))
                        {
                            double dist = distSquared(px0, py0, px1, py1);
                            if (dist < bestDist)
                            {
                                bestDist = dist;
                                bestV0 = v0;
                                bestV1 = v1;
                            }
                        }
                    }
                }

                //Add connection
                if (bestDist != Double.POSITIVE_INFINITY)
                {
                    TessEdge line0 = new TessEdge(bestV0, bestV1);
                    TessEdge line1 = new TessEdge(bestV1, bestV0);
                    bestV0.edgeOut.add(line0);
                    bestV1.edgeIn.add(line0);
                    bestV0.edgeIn.add(line1);
                    bestV1.edgeOut.add(line1);
                }
            }
        }
    }

    private boolean lineIntersects(double px0, double py0, double px1, double py1)
    {
        double[] frac = new double[2];
        for (TessVertex vtx: vertMap.values())
        {
            for (TessEdge line: vtx.edgeOut)
            {
                double qx0 = line.p0.point.getX();
                double qy0 = line.p0.point.getY();
                double qx1 = line.p1.point.getX();
                double qy1 = line.p1.point.getY();

                if (pointEquals(px0, py0, qx0, qy0)
                        || pointEquals(px0, py0, qx1, qy1)
                        || pointEquals(px1, py1, qx0, qy0)
                        || pointEquals(px1, py1, qx1, qy1))
                {
                    continue;
                }

                double[] res = lineIsectFractions(
                        px0, py0, px1 - px0, py1 - py0,
                        qx0, qy0, qx1 - qx0, qy1 - qy0, frac);

                if (res != null && isOn01(res[0]) && isOn01(res[1]))
                {
                    return true;
                }
            }
        }
        
        return false;
    }

    private void calcWindingLevels()
    {
        //At this point, the graph should be fully connected with
        // no self intersecting lines

        //Find bottommost point
        TessVertex bottomVtx = null;
        for (TessVertex vtx: vertMap.values())
        {
            if (bottomVtx == null ||
                    vtx.point.getY() < bottomVtx.point.getY())
            {
                bottomVtx = vtx;
            }
        }

        if (bottomVtx != null)
        {
            bottomVtx.startWindingCalc();
        }
    }

    private HashMap<Integer, ContourSet> buildContourSets()
    {
        ArrayList<TessHalfEdge> halfEdges = new ArrayList<TessHalfEdge>();

        for (TessVertex vtx: vertMap.values())
        {
            for (TessEdge edge: vtx.edgeOut)
            {
                halfEdges.add(edge.halfLeft);
                halfEdges.add(edge.halfRight);
            }
        }

        HashMap<Integer, ContourSet> contourSets = new HashMap<Integer, ContourSet>();
//        ArrayList<Contour> contours = new ArrayList<Contour>();
        while (!halfEdges.isEmpty())
        {
            //Start new contour
            TessHalfEdge first = halfEdges.remove(halfEdges.size() - 1);
            ContourSet ctrSet = contourSets.get(first.getWinding());
            if (ctrSet == null)
            {
                ctrSet = new ContourSet(first.getWinding());
                contourSets.put(ctrSet.winding, ctrSet);
            }

            Contour ctr = new Contour(first.getWinding());
            ctrSet.contours.add(ctr);

            //Add edges
            ctr.addEdge(first);

            for (TessHalfEdge edge = first.getHead().followCCW(first);
                edge != first; edge = edge.getHead().followCCW(edge))
            {
                ctr.addEdge(edge);
                assert halfEdges.remove(edge) : "Edge removed twice";
            }
        }

        return contourSets;
    }
    
    //---------------------------
    class ContourSet
    {
        int winding;
        ArrayList<Contour> contours = new ArrayList<Contour>();

        public ContourSet(int winding)
        {
            this.winding = winding;
        }

        public Contour getSingleContour()
        {
            ArrayList<Contour> contours = new ArrayList<Contour>(this.contours);

            //Merge contours
            while (contours.size() > 1)
            {
                Contour ctr = contours.remove(contours.size() - 1);

                Trace bestTrace = null;
                Contour bestOther = null;

                for (int i = 0; i < contours.size(); ++i)
                {
                    Contour other = contours.get(i);
                    Trace trace = other.findPathTo(ctr);
                    if (bestTrace == null || bestTrace.getSize() > trace.getSize())
                    {
                        bestTrace = trace;
                        bestOther = other;
                    }
                }

                contours.remove(bestOther);
                contours.add(splice(bestOther, ctr, bestTrace));
            }

            return contours.get(0);
        }

        /**
         * Combine two contours into a new contour along the trace.
         * 
         * @param ctr0
         * @param ctr1
         * @param trace Path from ctr0 to ctr1
         * @return
         */
        private Contour splice(Contour ctr0, Contour ctr1, Trace trace)
        {
            Contour res = new Contour(ctr0.winding);

            //Add first contour
            int offset = 0;
            TessVertex vert0 = trace.edgeList.get(0).getTail();
            while (ctr0.edges.get(offset).getTail() != vert0)
            {
                ++offset;
            }

            for (int i = 0; i < ctr0.edges.size(); ++i)
            {
                int idx = offset + i;
                if (idx >= ctr0.edges.size())
                {
                    idx -= ctr0.edges.size();
                }
                res.addEdge(ctr0.edges.get(idx));
            }

            //Add connecting path
            for (int i = 0; i < trace.getSize(); ++i)
            {
                res.addEdge(trace.edgeList.get(i));
            }

            //Add second contour
            offset = 0;
            TessVertex vert1 = trace.edgeList.get(trace.edgeList.size() - 1).getHead();
            while (ctr1.edges.get(offset).getTail() != vert1)
            {
                ++offset;
            }

            for (int i = 0; i < ctr1.edges.size(); ++i)
            {
                int idx = offset + i;
                if (idx >= ctr1.edges.size())
                {
                    idx -= ctr1.edges.size();
                }
                res.addEdge(ctr1.edges.get(idx));
            }

            //Add connecting path
            for (int i = trace.getSize() - 1; i >= 0; --i)
            {
                res.addEdge(trace.edgeList.get(i).getOtherSide());
            }

            return res;
        }
        
    }

    class Contour
    {
        int winding;
        private ArrayList<TessHalfEdge> edges = new ArrayList<TessHalfEdge>();
        HashSet<TessVertex> vertSet = new HashSet<TessVertex>();

        public Contour(int winding)
        {
            this.winding = winding;
        }

        public void dump(PrintStream ps)
        {
            ps.println("Contour winding: " + winding);
            for (TessHalfEdge e: edges)
            {
                ps.println(e);
            }
            ps.println();
        }
        
        public void addEdge(TessHalfEdge edge)
        {
            edges.add(edge);
            vertSet.add(edge.parent.p0);
            vertSet.add(edge.parent.p1);
        }

        /**
         * Find shortest path from this contour to passed one
         * @param ctr
         */
        public Trace findPathTo(Contour ctr)
        {
            HashSet<TessVertex> boundaryVerts = new HashSet<TessVertex>();
            boundaryVerts.addAll(vertMap.values());

            HashMap<TessVertex, Trace> traceMap = new HashMap<TessVertex, Trace>();
            for (TessVertex vtx: boundaryVerts)
            {
                traceMap.put(vtx, new Trace());
            }

            //Build set of minimal paths from any vertex to a point in this contour
            while (true)
            {
                HashSet<TessVertex> nextBoundaryVerts = new HashSet<TessVertex>();
                for (TessVertex vtx: boundaryVerts)
                {
                    Trace trace = traceMap.get(vtx);
                    if (ctr.vertSet.contains(vtx))
                    {
                        return trace;
                    }

                    for (TessEdge edge: vtx.edgeIn)
                    {
                        TessVertex v = edge.p0;
                        if (!traceMap.containsKey(v))
                        {
                            nextBoundaryVerts.add(v);
                            traceMap.put(v, new Trace(trace, edge.halfLeft));
                        }
                    }
                    for (TessEdge edge: vtx.edgeOut)
                    {
                        TessVertex v = edge.p1;
                        if (!traceMap.containsKey(v))
                        {
                            nextBoundaryVerts.add(v);
                            traceMap.put(v, new Trace(trace, edge.halfRight));
                        }
                    }
                }
                boundaryVerts = nextBoundaryVerts;
            }
        }

        private ArrayList<CyVector2d> getContour()
        {
            ArrayList<CyVector2d> ctr = new ArrayList<CyVector2d>();
            for (TessHalfEdge edge: edges)
            {
                TessPoint pt = edge.getTail().point;
                ctr.add(new CyVector2d(pt.x, pt.y));
            }
            return ctr;
        }
    }

    class Trace
    {
        ArrayList<TessHalfEdge> edgeList = new ArrayList<TessHalfEdge>();

        public Trace()
        {
        }

        public Trace(Trace oldTrace, TessHalfEdge newEdge)
        {
            edgeList.addAll(oldTrace.edgeList);
            edgeList.add(newEdge);
        }

        public int getSize()
        {
            return edgeList.size();
        }
    }

    class VertexSet
    {
        HashSet<TessVertex> set = new HashSet<TessVertex>();

        public VertexSet(TessVertex v0, TessVertex v1)
        {
            this.set.add(v0);
            this.set.add(v1);
        }


    }
}
