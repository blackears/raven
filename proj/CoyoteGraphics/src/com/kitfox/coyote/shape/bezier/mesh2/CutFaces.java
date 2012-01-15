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
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
@Deprecated
public class CutFaces
{
    HashMap<Coord, FaceVertex> vertMap = new HashMap<Coord, FaceVertex>();

    private CutFaces()
    {
    }

    /**
     * Calculates nested set of faces given an input graph.
     * 
     * Segments in input graph should only meet at vertices (ie,
     * no segments should cross over each other).  Also, given any two
     * vertices in the graph, there should be at most one segment 
     * connecting them.
     * 
     * Output data structure will be a nested tree of faces.  The root will
     * contain the CW face of the over all shape.  It will have CCW
     * children that represent the faces within this shape.  If any of the 
     * child shapes have holes, this will continue with child CW shapes
     * representing the hole and CCW shapes representing the boundaries
     * of shapes within the hole.
     * 
     * @param segList
     * @return 
     */
    public static FaceLoop buildFaces(ArrayList<CutSegment> segList)
    {
        CutFaces cf = new CutFaces();
        for (CutSegment seg: segList)
        {
            cf.addSegment(seg);
        }
        
        return cf.buildFaces();
    }
    
    private void addSegment(CutSegment seg)
    {
        FaceSeg s0 = new FaceSeg(seg, true);
        FaceSeg s1 = new FaceSeg(seg, false);
        
        FaceVertex v0 = getOrCreateVertex(s0.getStart());
        FaceVertex v1 = getOrCreateVertex(s0.getEnd());
        
        v0.segsOut.add(s0);
        v1.segsIn.add(s0);

        v0.segsIn.add(s1);
        v1.segsOut.add(s1);
    }
    
    private FaceVertex getOrCreateVertex(Coord c)
    {
        FaceVertex v = vertMap.get(c);
        if (v == null)
        {
            v = new FaceVertex(c);
            vertMap.put(c, v);
        }
        return v;
    }

    public FaceLoop buildFaces()
    {
        ArrayList<FaceSeg> segList = new ArrayList<FaceSeg>();
        for (FaceVertex v: vertMap.values())
        {
            v.sortRadial();
            segList.addAll(v.segsOut);
        }
        
        ArrayList<FaceLoop> loopList = new ArrayList<FaceLoop>();
        while (!segList.isEmpty())
        {
            loopList.add(extractFaceLoop(segList));
        }
        
        //Bind loops into faces
        Collections.sort(loopList);
        
        for (int i = loopList.size() - 1; i >= 1; --i)
        {
            FaceLoop subLoop = loopList.get(i);
            
            for (int j = i - 1; j >= 0; --j)
            {
                FaceLoop parentLoop = loopList.get(j);
                if (contains(parentLoop, subLoop))
                {
                    parentLoop.children.add(subLoop);
                    break;
                }
            }
        }
        
        return loopList.get(0);
    }

    private boolean contains(FaceLoop parLoop, FaceLoop subLoop)
    {
        if (parLoop.isCcw() == subLoop.isCcw())
        {
            //Nesting should alternate cw and ccw
            return false;
        }
        
        if (!parLoop.boundingBoxContains(subLoop))
        {
            //Optimization
            return false;
        }
        
        for (FaceSeg seg: subLoop.segList)
        {
            Coord c = seg.getStart();
            if (!parLoop.containsVertex(c))
            {
                return parLoop.contains(c.x, c.y);
            }
        }

        //All of subLoops verts are part of parLoop
        return true;        
    }

    private FaceLoop extractFaceLoop(ArrayList<FaceSeg> segList)
    {
        FaceSeg initSeg = segList.get(0);
        
        ArrayList<FaceSeg> loop = new ArrayList<FaceSeg>();
        
        FaceSeg curSeg = initSeg;
        do
        {
            loop.add(curSeg);
            curSeg = nextSeg(curSeg);
        } 
        while (curSeg != initSeg);
        
        segList.removeAll(loop);
        return new FaceLoop(loop);
    }
    
    /**
     * Next segment in CCW direction
     * @param seg
     * @return 
     */
    private FaceSeg nextSeg(FaceSeg seg)
    {
        Coord c = seg.getEnd();
        FaceVertex v = vertMap.get(c);
        
        int i = 0;
        for (; i < v.segsOut.size(); ++i)
        {
            //Find matching output segment
            FaceSeg matchSeg = v.segsOut.get(i);
            if (matchSeg.src == seg.src)
            {
                break;
            }
        }
        
        i = i == 0 ? v.segsOut.size() : i - 1;
        return v.segsOut.get(i);
    }
    
    //-----------------------------
    public class FaceLoop implements Comparable<FaceLoop>
    {
        ArrayList<FaceSeg> segList;
        final int minX, maxX, minY, maxY;
        int area2;

        ArrayList<FaceLoop> children = new ArrayList<FaceLoop>();
        
        public FaceLoop(ArrayList<FaceSeg> segList)
        {
            this.segList = segList;
            
            FaceSeg initSeg = segList.get(0);
            
            Coord c0 = initSeg.getStart();
            //Sum of area.  Will be twice actual area.
            int area2 = 0;
            int minX, maxX, minY, maxY;
            minX = maxX = c0.x;
            minY = maxY = c0.y;
            
            for (int i = 0; i < segList.size(); ++i)
            {
                FaceSeg seg = segList.get(i);
                Coord c1 = seg.getStart();
                Coord c2 = seg.getEnd();
                area2 += Math2DUtil.cross(c1.x - c0.x, c1.y - c0.y, 
                        c2.x - c0.x, c2.y - c0.y);
                minX = Math.min(minX, c1.x);
                minY = Math.min(minY, c1.y);
                maxX = Math.max(maxX, c1.x);
                maxY = Math.max(maxY, c1.y);
            }
            
            this.area2 = area2;
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
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
                FaceSeg seg = segList.get(i);
                
                Coord c0 = seg.getStart();
                Coord c1 = seg.getEnd();
                
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
        public int compareTo(FaceLoop oth)
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

        public boolean boundingBoxContains(FaceLoop subLoop)
        {
            return subLoop.minX >= minX
                    && subLoop.maxX <= maxX
                    && subLoop.minY >= minY
                    && subLoop.maxY <= maxY;
        }

        private boolean containsVertex(Coord c)
        {
            for (int i = 0; i < segList.size(); ++i)
            {
                FaceSeg seg = segList.get(i);
                if (seg.getStart().equals(c))
                {
                    return true;
                }
            }
            return false;
        }
    }
    
    class FaceVertex
    {
        Coord coord;
        ArrayList<FaceSeg> segsIn = new ArrayList<FaceSeg>();
        ArrayList<FaceSeg> segsOut = new ArrayList<FaceSeg>();

        public FaceVertex(Coord coord)
        {
            this.coord = coord;
        }

        private void sortRadial()
        {
            Collections.sort(segsIn);
            Collections.sort(segsOut);
        }
    }
    
    public class FaceSeg implements Comparable<FaceSeg>
    {
        CutSegment src;
        boolean forward;

        public FaceSeg(CutSegment src, boolean forward)
        {
            this.src = src;
            this.forward = forward;
        }

        private Coord getStart()
        {
            return forward ? src.c0 : src.c1;
        }

        private Coord getEnd()
        {
            return forward ? src.c1 : src.c0;
        }

        @Override
        public int compareTo(FaceSeg oth)
        {
            int dx0 = forward ? src.getDx() : -src.getDx();
            int dy0 = forward ? src.getDy() : -src.getDy();
            int dx1 = forward ? oth.src.getDx() : -oth.src.getDx();
            int dy1 = forward ? oth.src.getDy() : -oth.src.getDy();

            //Sort ccw
            if ((dx0 > 0 && dx1 > 0) || (dx0 < 0 && dx1 < 0))
            {
                //If on same side of y == 0 then compare slopes
                return dx1 * dy0 - dx0 * dy1;
            }
            
            if ((dx0 == 0 && dy0 == 0) || (dx1 == 0 && dy1 == 0))
            {
                throw new RuntimeException("Cannot sort zero length segments");
            }
            
            //Sort based on quadrant
            return rank(dx1, dy1) - rank(dx0, dy0);
        }
        
        private int rank(int dx, int dy)
        {
            if (dx > 0)
            {
                return 1;
            }
            else if (dx < 0)
            {
                return 3;
            }
            else if (dy < 0)
            {
                return 0;
            }
            else 
            {
                return 2;
            }
        }
    }
}
