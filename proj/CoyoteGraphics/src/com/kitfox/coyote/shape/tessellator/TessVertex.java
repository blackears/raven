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

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author kitfox
 */
public class TessVertex
{
    final TessPoint point;
    ArrayList<TessEdge> edgeOut = new ArrayList<TessEdge>();
    ArrayList<TessEdge> edgeIn = new ArrayList<TessEdge>();

    ArrayList<LineInfo> linesRadial;
    boolean windingCalcDone;

    public TessVertex(TessPoint point)
    {
        this.point = point;
    }

    ArrayList<LineInfo> getLinesRadial()
    {
        if (linesRadial == null)
        {
            linesRadial = new ArrayList<LineInfo>();

            for (TessEdge line: edgeOut)
            {
                linesRadial.add(new LineInfo(line));
            }
            for (TessEdge line: edgeIn)
            {
                linesRadial.add(new LineInfo(line));
            }
            Collections.sort(linesRadial);
        }
        return linesRadial;
    }

    /**
     * Should be called after graph has been fully connected and
     * this vertex has been identified as the topmost vertex.
     * Kicks off the vertex winding calculation.
     */
    void startWindingCalc()
    {
        ArrayList<LineInfo> lines = getLinesRadial();

        //Since this is the bottomost vertex, all lines have
        // positive angles.
        LineInfo line = lines.get(0);
        if (isLineOut(line.line))
        {
            //Right side has winding 0, since it is on the outside edge.
            line.line.windingLevel = 0;
        }
        else
        {
            //Left side has winding 0, since it is on the outside edge.
            line.line.windingLevel = -1;
        }
        markLines(line.line);
    }

    boolean isLineIn(TessEdge line)
    {
        return line.p1 == this;
    }

    boolean isLineOut(TessEdge line)
    {
        return line.p0 == this;
    }

    /**
     * Radially set line winding about this vertex starting
     * with passed line (which should have winding level set)
     *
     * @param line
     */
    private void markLines(TessEdge line)
    {
        ArrayList<LineInfo> lines = getLinesRadial();

        //Find initial offset
        int lineIdx = getRadialIndex(line);

        //Set windings
        int prevWinding = line.windingLevel;
        LineInfo prevLine = lines.get(lineIdx);
        boolean prevOut = isLineOut(prevLine.line);
        
        for (int i = 1; i < lines.size(); ++i)
        {
            int idx = lineIdx + i;
            if (idx >= lines.size())
            {
                idx -= lines.size();
            }
            LineInfo curLine = lines.get(idx);

            boolean curOut = isLineOut(curLine.line);
            int curWinding;
            if (prevOut)
            {
                curWinding = prevWinding + (curOut ? 1 : 0);
            }
            else
            {
                curWinding = prevWinding + (curOut ? 0 : -1);
            }

            if (curLine.line.windingLevel == Integer.MIN_VALUE)
            {
                curLine.line.windingLevel = curWinding;
            }
            else
            {
                assert (curLine.line.windingLevel == curWinding)
                        : "Calculated different winding level for this line";
            }

            prevLine = curLine;
            prevWinding = curWinding;
            prevOut = curOut;
        }

        windingCalcDone = true;

        //Spread to other vertices
        for (int i = 0; i < lines.size(); ++i)
        {
            LineInfo info = lines.get(i);
            if (isLineOut(info.line))
            {
                if (!info.line.p1.windingCalcDone)
                {
                    info.line.p1.markLines(info.line);
                }
            }
            else
            {
                if (!info.line.p0.windingCalcDone)
                {
                    info.line.p0.markLines(info.line);
                }
            }
        }
    }

    /**
     * Finds the next half edge that touches this vertex in the
     * CCW direction with the same winding level.
     *
     * @param curEdge
     * @return
     */
    TessHalfEdge followCCW(TessHalfEdge curEdge)
    {
        int winding = curEdge.getWinding();

        ArrayList<LineInfo> lines = getLinesRadial();
        int firstIdx = getRadialIndex(curEdge.parent);
        for (int i = 1; i < lines.size(); ++i)
        {
            int idx = firstIdx + i;
            if (idx >= lines.size())
            {
                idx -= lines.size();
            }

            LineInfo info = lines.get(idx);
            if (isLineOut(info.line) 
                    && info.line.halfRight.getWinding() == winding)
            {
                return info.line.halfRight;
            }
            if (isLineIn(info.line)
                    && info.line.halfLeft.getWinding() == winding)
            {
                return info.line.halfLeft;
            }
        }

        throw new RuntimeException();
    }

    private int getRadialIndex(TessEdge parent)
    {
        ArrayList<LineInfo> lines = getLinesRadial();
        for (int i = 0; i < lines.size(); ++i)
        {
            if (lines.get(i).line == parent)
            {
                return i;
            }
        }

        throw new RuntimeException();
    }

    @Override
    public String toString()
    {
        return point.toString();
    }

    //----------------------------------
    class LineInfo implements Comparable<LineInfo>
    {
        TessEdge line;
//        boolean windingSet;
        //Angle of incidence at vertex
        double angle;

        public LineInfo(TessEdge line)
        {
            this.line = line;
            double px0 = line.p0.point.getX();
            double py0 = line.p0.point.getY();
            double px1 = line.p1.point.getX();
            double py1 = line.p1.point.getY();

            if (isLineOut(line))
            {
                angle = Math.atan2(py1 - py0, px1 - px0);
            }
            else
            {
                angle = Math.atan2(py0 - py1, px0 - px1);
            }
        }

        @Override
        public int compareTo(LineInfo obj)
        {
            int val = Double.compare(angle, obj.angle);
            if (val == 0)
            {
                return isLineOut(line) ? 1 : 0;
            }
            return val;
        }

        @Override
        public String toString()
        {
            return "" + line + ", ang:" + angle;
        }


    }

}
