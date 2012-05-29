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

import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.shape.bezier.path.cut.BoundryTest;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class TessLoop
{
    ArrayList<Coord> points;
    private int winding;
    private boolean ccw;
    ArrayList<TessLoop> children = new ArrayList<TessLoop>();

    public TessLoop(ArrayList<Coord> points, int winding, boolean ccw)
    {
        this.points = points;
        this.winding = winding;
        this.ccw = ccw;
    }
    
    public boolean isCCW()
    {
        return winding > 0;
    }
        
    public boolean isInside(TessLoop l)
    {
        for (int i = 0; i < points.size(); ++i)
        {
            Coord c0 = points.get(i);
            Coord c1 = points.get(i == points.size() - 1 
                    ? 0 : i + 1);
            
            switch (l.testInside((c0.x + c1.x) / 2, (c0.y + c1.y) / 2))
            {
                case INSIDE:
                    return true;
                case OUTSIDE:
                    return false;
            }
        }
        
        //Coincident
        return false;
    }
    
    public BoundryTest testInside(int x, int y)
    {
        int count = 0;
        
        for (int i = 0; i < points.size(); ++i)
        {
            Coord c0 = points.get(i);
            Coord c1 = points.get(i == points.size() - 1 ? 0 : i + 1);
            
            if (c0.x == x && c1.x == x)
            {
                if (c0.y <= y && y <= c1.y)
                {
                    return BoundryTest.BOUNDARY;
                }
            }
            else if (c0.x <= x && x <= c1.x)
            {
                int side = Math2DUtil.getLineSide(c0.x, c0.y,
                        c1.x - c0.x, c1.y - c0.y,
                        x, y);
                
                if (side == 0)
                {
                    return BoundryTest.BOUNDARY;
                }
                else if (side < 0)
                {
                    count++;
                }
            }
            else if (c1.x <= x && x <= c0.x)
            {
                int side = Math2DUtil.getLineSide(c0.x, c0.y,
                        c1.x - c0.x, c1.y - c0.y,
                        x, y);
                
                if (side == 0)
                {
                    return BoundryTest.BOUNDARY;
                }
                else if (side > 0)
                {
                    count--;
                }
            }
        }
        
        return count == 0 ? BoundryTest.OUTSIDE : BoundryTest.INSIDE;
    }

    public void buildTriangles(ArrayList<Coord> triList)
    {
        ArrayList<Coord> verts = new ArrayList<Coord>(points);
        
        //Add holes
        if (!children.isEmpty())
        {
            //Splice children into boundary
            ArrayList<TessLoop> addList = new ArrayList<TessLoop>(children);
            while (!addList.isEmpty())
            {
                TessLoop child = addList.remove(addList.size() - 1);
                
                if (child.winding != 0)
                {
                    //Only add holes of 0 winding
                    continue;
                }
                
                if (!insertChild(verts, child))
                {
                    addList.add(0, child);
                }
            }
        }
        
        earClip(verts, triList);
    }
    
    private boolean insertChild(ArrayList<Coord> verts, TessLoop child)
    {
        for (int i = 0; i < verts.size(); ++i)
        {
            Coord c0 = verts.get(i);
            
            ArrayList<Coord> childVerts = child.getPoints();
            for (int j = 0; j < childVerts.size(); ++j)
            {
                Coord c1 = childVerts.get(j);
                
                if (canCut(c0, c1))
                {
                    //Insert child loop
                    boolean reverse = isCCW() != child.isCCW();

                    int size = childVerts.size();
                    for (int k = 0; k < size; ++k)
                    {
                        int idx = j + (reverse ? -k : k);
                        if (idx < 0)
                        {
                            idx += size;
                        }
                        if (idx >= size)
                        {
                            idx -= size;
                        }
                        
                        verts.add(++i, childVerts.get(idx));
                    }
                    
                    verts.add(++i, c1);
                    verts.add(++i, c0);
                    
                    return true;
                }
            }
        }
        
        return false;
    }
  
    private boolean canCut(Coord c0, Coord c1)
    {
        if (crossesLoop(c0, c1))
        {
            return false;
        }

        for (TessLoop child: children)
        {
            if (child.crossesLoop(c0, c1))
            {
                return false;
            }
        }
        return true;
    }
    
    private boolean crossesLoop(Coord c0, Coord c1)
    {
        for (int i = 0; i < points.size(); ++i)
        {
            Coord v0 = points.get(i);
            Coord v1 = points.get(i == points.size() - 1 ? 0 : i + 1);
            
            if (c0.equals(v0) || c0.equals(v1)
                    || c1.equals(v0) || c1.equals(v1))
            {
                continue;
            }
            
            double[] cross = Math2DUtil.lineIsectFractions(
                    c0.x, c0.y,
                    c1.x - c0.x, c1.y - c0.y,
                    v0.x, v0.y,
                    v1.x - v0.x, v1.y - v0.y,
                    null);
            
            if (cross != null 
                    && cross[0] > 0 && cross[0] < 1
                    && cross[1] > 0 && cross[1] < 1)
            {
                return true;
            }
        }
        
        return false;
    }
    
    private void earClip(ArrayList<Coord> verts, ArrayList<Coord> triList)
    {
//        boolean ccw = winding > 0;
        
        //Remove verts which are in the middle of a straight line
        for (int i = 0; i < verts.size(); ++i)
        {
            int prevIdx = i == 0 ? verts.size() - 1 : i - 1;
            int nextIdx = i == verts.size() - 1 ? 0 : i + 1;
            
            Coord c0 = verts.get(prevIdx);
            Coord c1 = verts.get(i);
            Coord c2 = verts.get(nextIdx);
            
            int dx10 = c1.x - c0.x;
            int dy10 = c1.y - c0.y;
            int dx21 = c2.x - c1.x;
            int dy21 = c2.y - c1.y;

            if (dx10 * dx21 < 0 || dy10 * dy21 < 0)
            {
                //c1 is not within bounding box of [c0 c2]
                continue;
            }
            
            if (dx10 * dy21 != dx21 * dy10)
            {
                //Not colinear
                continue;
            }
            
            //Remove vert
            verts.remove(i);
            --i;
        }
        
        //Build tris
        while (verts.size() > 2)
        {
            int cacheSize = verts.size();
            
            NEXT_POINT:
            for (int i = 0; i < verts.size(); ++i)
            {
                int i0 = i == 0 ? verts.size() - 1 : i - 1;
                int i1 = i;
                int i2 = i == verts.size() - 1 ? 0 : i + 1;
                Coord c0 = verts.get(i0);
                Coord c1 = verts.get(i1);
                Coord c2 = verts.get(i2);
                
                int triArea = Math2DUtil.cross(c1.x - c0.x, c1.y - c0.y, 
                        c2.x - c0.x, c2.y - c0.y);
                boolean triCcw = triArea > 0;
                
                if (triCcw != ccw)
                {
                    //Concave
                    continue;
                }
                
                for (int j = 0; j < verts.size(); ++j)
                {
                    if (isInsideTrangle(c0, c1, c2, verts.get(j)))
                    {
                        continue NEXT_POINT;
                    }
                }
                
                verts.remove(i1);
                triList.add(c0);
                triList.add(c1);
                triList.add(c2);
                break;
            }
            
            if (cacheSize == verts.size())
            {
                //TODO: Went through entire list without finding any trianges
                // to export.  Perhaps face is self intersecting?
                return;
            }
        }
    }

    private boolean isInsideTrangle(Coord c0, Coord c1, Coord c2, Coord ct)
    {
        if (c0 == ct || c1 == ct || c2 == ct)
        {
            return false;
        }
        
        int s0 = Math2DUtil.getLineSide(
                c0.x, c0.y, c1.x - c0.x, c1.y - c0.y, 
                ct.x, ct.y);
        int s1 = Math2DUtil.getLineSide(
                c1.x, c1.y, c2.x - c1.x, c2.y - c1.y, 
                ct.x, ct.y);
        int s2 = Math2DUtil.getLineSide(
                c2.x, c2.y, c0.x - c2.x, c0.y - c2.y, 
                ct.x, ct.y);
        
        return (s0 > 0 && s1 > 0 && s2 > 0) 
                || (s0 < 0 && s1 < 0 && s2 < 0);
    }

    /**
     * @return the winding
     */
    public int getWinding()
    {
        return winding;
    }

    void dump(PrintStream ps)
    {
        ps.println("wind: " + winding);
        for (Coord e: points)
        {
            ps.println(e.toString());
        }
    }

    void dumpScilab(PrintStream ps)
    {
        ps.println("scf()");
        ps.print("data = [");

        for (int i = 0; i < points.size(); ++i)
        {
            Coord c = points.get(i);
            if (i != 0)
            {
                ps.print("; ");
            }
            
            ps.print("" + c.x + " " + c.y);
        }
        ps.println("]");
        ps.println("plot(data(:, 1), data(:, 2))");
    }

    public String asSvg()
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        
        Coord c = points.get(0);
        pw.print("M " + c.x + "," + c.y);
        for (int i = 1; i < points.size(); ++i)
        {
            c = points.get(i);
            pw.print(" L " + c.x + "," + c.y);
        }
        pw.append('z');
        pw.close();
        
        return sw.toString();
    }

    private ArrayList<Coord> getPoints()
    {
        return new ArrayList<Coord>(points);
    }

    @Override
    public String toString()
    {
        return "wi: " + winding + " ccw: " + ccw + " " + asSvg();
    }
    
    
}
