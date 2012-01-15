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

package com.kitfox.coyote.shape.bezier.mesh2;

import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class CutLoop implements Comparable<CutLoop>
{
    ArrayList<CutSegHalf> segList;
    final int minX, maxX, minY, maxY;
    int area2;

    ArrayList<CutLoop> children = new ArrayList<CutLoop>();

    public CutLoop(ArrayList<CutSegHalf> segList)
    {
        this.segList = segList;

        CutSegHalf initSeg = segList.get(0);

        Coord c0 = initSeg.c0;
        //Sum of area.  Will be twice actual area.
        int cArea2 = 0;
        int cMinX, cMaxX, cMinY, cMaxY;
        cMinX = cMaxX = c0.x;
        cMinY = cMaxY = c0.y;

//System.err.println("Loop");
        for (int i = 1; i < segList.size(); ++i)
        {
            CutSegHalf seg = segList.get(i);
            Coord c1 = seg.c0;
            Coord c2 = seg.c1;
            int areaTri = Math2DUtil.cross(c1.x - c0.x, c1.y - c0.y, 
                    c2.x - c0.x, c2.y - c0.y);
            cArea2 += areaTri;
//System.err.println("Tri:" + c0 + " " + c1 + " " + c2 
//        + "  areaTri: " + areaTri + "  areaTotal: " + area2);
            
            cMinX = Math.min(cMinX, c1.x);
            cMinY = Math.min(cMinY, c1.y);
            cMaxX = Math.max(cMaxX, c1.x);
            cMaxY = Math.max(cMaxY, c1.y);
        }

        this.area2 = cArea2;
        this.minX = cMinX;
        this.maxX = cMaxX;
        this.minY = cMinY;
        this.maxY = cMaxY;
    }

    public boolean isCcw()
    {
        return area2 >= 0;
    }

    public boolean contains(int px, int py)
    {
        //Consider the half space x >= px.  Count the number of
        // segments entering this partition where (px, py) is to the right
        // of the segment and also the segments leaving where (px, py)
        // is to the left.  If these two counts are equal, (px, py) is
        // outside of polygon.

        int crossing = 0;

        for (int i = 0; i < segList.size(); ++i)
        {
            CutSegHalf seg = segList.get(i);

            Coord c0 = seg.c0;
            Coord c1 = seg.c1;

            int side = Math2DUtil.getLineSide(c0.x, c0.y, 
                    c1.x - c0.x, c1.y - c0.y, 
                    px, py);

            if (c0.x < px && c1.x >= px)
            {
                if (side < 0)
                {
                    ++crossing;
                }
            }

            if (c0.x >= px && c1.x < px)
            {
                if (side > 0)
                {
                    --crossing;
                }
            }
        }

        return crossing != 0;
    }

    @Override
    public int compareTo(CutLoop oth)
    {
        //Move smaller shapes to right.
        //If same size, move CCW to right
        int absArea0 = Math.abs(area2);
        int absArea1 = Math.abs(oth.area2);

        if (absArea0 == absArea1)
        {
            return isCcw() ? 1 : -1;
        }

        return absArea1 - absArea0;
    }

    public boolean boundingBoxContains(CutLoop subLoop)
    {
        return subLoop.minX >= minX
                && subLoop.maxX <= maxX
                && subLoop.minY >= minY
                && subLoop.maxY <= maxY;
    }

    public boolean containsVertex(Coord c)
    {
        for (int i = 0; i < segList.size(); ++i)
        {
            CutSegHalf seg = segList.get(i);
            if (seg.c0.equals(c))
            {
                return true;
            }
        }
        return false;
    }
}
