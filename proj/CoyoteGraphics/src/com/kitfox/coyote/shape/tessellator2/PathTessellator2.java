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

package com.kitfox.coyote.shape.tessellator2;

import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.shape.PathConsumer;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author kitfox
 */
public class PathTessellator2 extends PathConsumer
{
    double mx, my;
    double bx, by;
    boolean drawingPath;
    final boolean removeInternalSegments;
    final double resolution;
    final double resolutionI;
    
    ArrayList<TessSeg> segments = new ArrayList<TessSeg>();
    ArrayList<CtrLoop> contours;

    public PathTessellator2()
    {
        this(1, false);
    }
    
    /**
     * Turns path into a set of nested loops with known winding level.
     * 
     * To avoid round off error, all internal calculations round
     * vertex coords off to the nearest integer.  To allow for
     * tessellation of shapes that require finer resolution, a
     * resolution scalar can be specified.  All vertices will 
     * have their coords divided by this value when added.
     * The loops will then be computed and the final result will
     * be multiplied by this value to bring it back into your 
     * coordinate system.
     * 
     * @param resolution Unit distance all vertices will be rounded
     * to during calculation.
     * @param removeInternalSegments If true, any segments that do
     * not have one other their sides touching the outside (ie, have a 
     * winding level of 0) are removed before loops are calculated.
     */
    public PathTessellator2(double resolution, boolean removeInternalSegments)
    {
        this.resolution = resolution;
        this.resolutionI = 1 / resolution;
        this.removeInternalSegments = removeInternalSegments;
    }

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
        splitOverlappingSegments();
        connectGraph();
        buildContours();
    }

    private void addLine(double x0, double y0, double x1, double y1)
    {
        Coord c0 = new Coord((int)(x0 * resolutionI), (int)(y0 * resolutionI));
        Coord c1 = new Coord((int)(x1 * resolutionI), (int)(y1 * resolutionI));
        if (c0.equals(c1))
        {
            return;
        }
        segments.add(new TessSeg(c0, c1));
    }
    
    private void removeZeroLengthSegs(ArrayList<TessSeg> segs)
    {
        for (Iterator<TessSeg> it = segs.iterator(); it.hasNext();)
        {
            TessSeg seg = it.next();
            if (seg.isZeroLength())
            {
                it.remove();
            }
        }
    }
    
    private void splitOverlappingSegments()
    {
        boolean didSplit;
        do
        {
            didSplit = false;
            ArrayList<TessSeg> newSegs = new ArrayList<TessSeg>();

            while (!segments.isEmpty())
            {
                TessSeg curSeg = segments.remove(segments.size() - 1);
                
                for (int j = 0; j < segments.size(); ++j)
                {
                    TessSeg peerSeg = segments.get(j);
                    ArrayList<TessSeg> cutSegs = 
                            curSeg.splitAtIsect(peerSeg);
                    
                    if (cutSegs != null)
                    {
                        didSplit = true;
                        segments.remove(j);
                        for (TessSeg s: cutSegs)
                        {
                            if (!s.isZeroLength())
                            {
                                newSegs.add(s);
                            }
                        }
                        curSeg = null;
                        break;
                    }
                }
                
                if (curSeg != null)
                {
                    //Went through entire list without splitting
                    // anything.  Add unaltered segments to list.
                    newSegs.add(curSeg);
                }
            }
            
            segments = newSegs;
        } while (didSplit);
    }

    /**
     * Search segments to see if there are more than one
     * contigious groups.  If so, connect them by inserting an
     * additional cut.
     */
    private void connectGraph()
    {
       HashMap<Coord, SegGroup> segMap = new HashMap<Coord, SegGroup>();
       ArrayList<SegGroup> grpList = new ArrayList<SegGroup>();
       
       for (TessSeg seg: segments)
       {
           SegGroup grp0 = segMap.get(seg.c0);
           SegGroup grp1 = segMap.get(seg.c1);
           
           if (grp0 == null && grp1 == null)
           {
               grp0 = new SegGroup();
               grpList.add(grp0);
               
               grp0.add(seg);
               segMap.put(seg.c0, grp0);
               segMap.put(seg.c1, grp0);
           }
           else if (grp0 == null)
           {
               grp1.add(seg);
               segMap.put(seg.c0, grp1);
           }
           else if (grp1 == null)
           {
               grp0.add(seg);
               segMap.put(seg.c1, grp0);
           }
           else
           {
                //Merge grp1 into grp0
                if (grp0 != grp1)
                {
                    grpList.remove(grp1);
                    grp0.segments.addAll(grp1.segments);
                    for (Coord c: grp1.coords)
                    {
                        segMap.put(c, grp0);
                    }
                }
                grp0.add(seg);
           }
       }
       
       //Merge disjoint mesh groups
       NEXT_GROUP:
       while (grpList.size() > 1)
       {
           SegGroup curGrp = grpList.remove(grpList.size() - 1);
           
           for (int i = 0; i < grpList.size(); ++i)
           {
               SegGroup testGrp = grpList.get(i);
               for (Coord c0: curGrp.coords)
               {
                   for (Coord c1: testGrp.coords)
                   {
                       TessSeg seg = new TessSeg(c0, c1);
                       if (!isSplitBySegments(seg))
                       {
                           segments.add(seg);
                           segments.add(seg.reverse());
                           continue NEXT_GROUP;
                       }
                   }
               }
           }
           
           assert false : "Could not connect groups";
       }
    }

    private boolean isSplitBySegments(TessSeg s0)
    {
        for (TessSeg s1: segments)
        {
            if (s0.isLineSplitBy(s1))
            {
                return true;
            }
        }
        return false;
    }
    
    private void buildContours()
    {
        CtrGraph graph = new CtrGraph();
        for (TessSeg s: segments)
        {
            graph.addSegment(s.c0, s.c1);
        }
        
        contours = graph.buildContours();
        
        if (removeInternalSegments)
        {
//            for (Iterator<CtrLoop> it = contours.iterator(); it.hasNext();)
//            {
//                CtrLoop loop = it.next();
//                loop.removeInternalSegments();
//                if (loop.isEmpty())
//                {
//                    it.remove();
//                }
//            }
        }
    }
    
    public ArrayList<CtrLoop> getContours()
    {
        return new ArrayList<CtrLoop>(contours);
    }

    public void dump(PrintStream ps)
    {
        for (int i = 0; i < contours.size(); ++i)
        {
            CtrLoop loop = contours.get(i);
            ps.println("Contour " + i);
            loop.dump(ps);
        }
    }

    public void dumpScilab(PrintStream ps)
    {
        for (int i = 0; i < contours.size(); ++i)
        {
            CtrLoop loop = contours.get(i);
            ps.println("Contour " + i);
            loop.dumpScilab(ps);
        }
    }

    public ArrayList<Coord> getTrianglesNonZero()
    {
        ArrayList<Coord> triList = new ArrayList<Coord>();
        for (CtrLoop loop: contours)
        {
            if (loop.getWinding() == 0)
            {
                continue;
            }
            loop.buildTriangles(triList);
        }
        return triList;
    }
}
