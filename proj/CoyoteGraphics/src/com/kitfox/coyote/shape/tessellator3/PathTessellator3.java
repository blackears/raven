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

package com.kitfox.coyote.shape.tessellator3;

import com.kitfox.coyote.shape.PathConsumer;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author kitfox
 */
public class PathTessellator3 extends PathConsumer
{
    double mx, my;
    double bx, by;
    boolean drawingPath;
    final double resolution;
    final double resolutionI;

    ArrayList<TessSeg> segments = new ArrayList<TessSeg>();
    ArrayList<TessSeg> curCtr = new ArrayList<TessSeg>();

    TessGraph graph = new TessGraph();
    ArrayList<TessLoop> contours = new ArrayList<TessLoop>();
    
    public PathTessellator3()
    {
        this(1);
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
     */
    public PathTessellator3(double resolution)
    {
        this.resolution = resolution;
        this.resolutionI = 1 / resolution;
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
            beginSubpath(x0, y0);
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
        
        if (mx != bx || my != by)
        {
            addLine(mx, my, bx, by);
            mx = bx;
            my = by;
        }
        
        segments.addAll(curCtr);
        curCtr.clear();
    }

    @Override
    public void endPath()
    {
        if (drawingPath)
        {
            //Finish up if not done
            closeSubpath();
        }
        
        splitOverlappingSegments();
        createGraph();
    }
    
    public ArrayList<TessLoop> getContours()
    {
        return new ArrayList<TessLoop>(contours);
    }

    private void addLine(double x0, double y0, double x1, double y1)
    {
        Coord c0 = new Coord((int)(x0 * resolutionI), (int)(y0 * resolutionI));
        Coord c1 = new Coord((int)(x1 * resolutionI), (int)(y1 * resolutionI));
        if (c0.equals(c1))
        {
            return;
        }
        curCtr.add(new TessSeg(c0, c1));
    }
    
    private void splitOverlappingSegments()
    {
        HashSet<TessSeg> consumed = new HashSet<TessSeg>();
        ArrayList<TessSeg> newSegs = new ArrayList<TessSeg>();
        ArrayList<TessSeg> splitSegs = new ArrayList<TessSeg>();
        
        boolean hadSplit;
        do
        {
            hadSplit = false;
            
            //Compare all pairs of segs, and split crossing pairs
            NEXT_SEG:
            for (int i = 0; i < segments.size(); ++i)
            {
                TessSeg s0 = segments.get(i);
                if (consumed.contains(s0))
                {
                    continue;
                }

                for (int j = i + 1; j < segments.size(); ++j)
                {
                    TessSeg s1 = segments.get(j);
                    if (consumed.contains(s1))
                    {
                        continue;
                    }

                    splitSegs.clear();
                    if (s0.split(s1, splitSegs))
                    {
                        hadSplit = true;
                        
//splitSegs.clear();
//s0.split(s1, splitSegs);

                        consumed.add(s1);
                        newSegs.addAll(splitSegs);
                        continue NEXT_SEG;
                    }
                }
                
                //No splits, append to keep list
                newSegs.add(s0);
            }
            
            consumed.clear();
            ArrayList<TessSeg> tmp = segments;
            segments = newSegs;
            newSegs = tmp;
            newSegs.clear();

        } while (hadSplit);
    }

    private void createGraph()
    {
        for (TessSeg s: segments)
        {
            graph.addEdge(s);
        }
        
        graph.prepareGraph();
        
        while (!graph.isEmpty())
        {
            TessLoop newLoop = graph.extractContour();
            contours.add(newLoop);
            
            for (int i = contours.size() - 2; i >= 0; --i)
            {
                TessLoop loop = contours.get(i);
                if (newLoop.isInside(loop))
                {
                    loop.children.add(newLoop);
                    break;
                }
            }
        }
    }

    public ArrayList<Coord> getTrianglesNonZero()
    {
        ArrayList<Coord> triList = new ArrayList<Coord>();
        for (TessLoop loop: contours)
        {
            if (loop.getWinding() == 0)
            {
                continue;
            }
            loop.buildTriangles(triList);
        }
        return triList;
    }

    public void dump(PrintStream ps)
    {
        for (int i = 0; i < contours.size(); ++i)
        {
            TessLoop loop = contours.get(i);
            ps.println("Contour " + i);
            loop.dump(ps);
        }
    }

    public void dumpScilab(PrintStream ps)
    {
        for (int i = 0; i < contours.size(); ++i)
        {
            TessLoop loop = contours.get(i);
            ps.println("Contour " + i);
            loop.dumpScilab(ps);
        }
    }

    public void dumpSvg(PrintStream ps)
    {
        for (int i = 0; i < contours.size(); ++i)
        {
            TessLoop loop = contours.get(i);
            ps.println("Contour " + i);
            ps.println(loop.asSvg());
        }
    }
    
}
