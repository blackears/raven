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
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
@Deprecated
public class EarClip
{
    /**
     * Calculates a value equal to twice the area this contour bounds.  Positive
     * values indicate CCW winding, negative for CW.
     *
     * @param ctr
     * @return
     */
    private static double calcTwiceArea(ArrayList<CyVector2d> ctr)
    {
        CyVector2d p0 = ctr.get(0);
        double sum = 0;

        for (int i = 1; i < ctr.size() - 1; ++i)
        {
            CyVector2d p1 = ctr.get(i);
            CyVector2d p2 = ctr.get(i + 1);

            sum += Math2DUtil.cross(p1.x - p0.x, p1.y - p0.y, p2.x - p0.x, p2.y - p0.y);
        }
        return sum;
    }

    /**
     * Tessellate contour using ear clipping algorithm
     * 
     * @param ctr
     * @param triangles
     */
    public static void tessellate(ArrayList<CyVector2d> ctr, ArrayList<CyVector2d> triangles)
    {
        if (ctr.size() <= 2)
        {
            return;
        }

        double area2 = calcTwiceArea(ctr);

        //Create doubly linked loop of contour
        VertexInfo head = new VertexInfo(ctr.get(0));
        VertexInfo last = head;
        if (area2 > 0)
        {
            //Winding is CCW
            for (int i = 1; i < ctr.size(); ++i)
            {
                VertexInfo cur = new VertexInfo(ctr.get(i));
                cur.prev = last;
                last.next = cur;
                last = cur;
            }
        }
        else
        {
            //Winding is CW - must reverse this
            for (int i = ctr.size() - 1; i >= 1; --i)
            {
                VertexInfo cur = new VertexInfo(ctr.get(i));
                cur.prev = last;
                last.next = cur;
                last = cur;
            }
        }
        last.next = head;
        head.prev = last;

        tessellate(head, ctr.size(), triangles);
    }

    private static void tessellate(VertexInfo curVtx, int size, ArrayList<CyVector2d> triangles)
    {
        for (; size >= 3; curVtx = curVtx.next)
        {
            boolean clip = false;
            if (curVtx.point.equals(curVtx.prev.point)
                    || curVtx.prev.point.equals(curVtx.next.point))
            {
                clip = true;
            }
            else if (curVtx.isEar())
            {
                //Only add triangles of non-zero area
                triangles.add(curVtx.point);
                triangles.add(curVtx.next.point);
                triangles.add(curVtx.prev.point);
                
                clip = true;
            }

            if (clip)
            {
                curVtx.next.prev = curVtx.prev;
                curVtx.prev.next = curVtx.next;

                curVtx.prev.makeDirty();
                curVtx.next.makeDirty();
                --size;
            }
        }

    }

    static class VertexInfo
    {
        CyVector2d point;
        VertexInfo prev;
        VertexInfo next;

        boolean dirty = true;
        boolean convex;
        boolean hasInsidePoint;
//        boolean peninsula;

        public VertexInfo(CyVector2d point)
        {
            this.point = point;
//if (Double.isNaN(point.x))
//{
//    int j = 9;
//}
        }

//        private boolean isPeninsula()
//        {
//            return peninsula;
//        }

        private boolean isEar()
        {
            build();
            return convex && !hasInsidePoint;
//            return peninsula || (convex && !hasInsidePoint);
        }

        private void build()
        {
            if (!dirty)
            {
                return;
            }

//            peninsula = next.point.equals(prev.point);
//            if (peninsula)
//            {
//                //No need to compute further is peninsula
//                dirty = false;
//                return;
//            }

            double v0x = next.point.x - point.x;
            double v0y = next.point.y - point.y;
            double v1x = prev.point.x - point.x;
            double v1y = prev.point.y - point.y;
            convex = Math2DUtil.cross(v0x, v0y, v1x, v1y) > 0;

            //Only perform inside test if vertex is convex - otherwise we don't
            // need the info
            if (convex)
            {
                //Check for any distant verts straying into this triangle
                hasInsidePoint = false;
                for (VertexInfo test = next.next; test != prev; test = test.next)
                {
                    CyVector2d pt = test.point;
                    if (pt.equals(prev.point) || pt.equals(point) || pt.equals(next.point))
                    {
                        continue;
                    }

                    if (Math2DUtil.isInsideTriangle(prev.point, point, next.point,
                            pt))
                    {
                        hasInsidePoint = true;
                        break;
                    }
                }
            }

            dirty = false;
        }

        private void makeDirty()
        {
            dirty = true;
        }

        @Override
        public String toString()
        {
            return point.toString();
        }


    }

}
