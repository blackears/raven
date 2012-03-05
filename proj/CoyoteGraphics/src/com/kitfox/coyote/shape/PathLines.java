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

package com.kitfox.coyote.shape;

import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.math.Math2DUtil;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class PathLines extends PathConsumer
{
    double mx, my;
    double bx, by;
    boolean drawingPath;

    ArrayList<CyVector2d> verts = new ArrayList<CyVector2d>();

    @Override
    public void beginPath()
    {
    }

    @Override
    public void beginSubpath(double x0, double y0)
    {
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
        drawingPath = false;
    }

    /**
     * Called after endPath().  Retrieves tessellated triangles.
     */
    public ArrayList<CyVector2d> getLines()
    {
        ArrayList<CyVector2d> res = new ArrayList<CyVector2d>(verts);
        return res;
    }

    private void addLine(double x0, double y0, double x1, double y1)
    {
        verts.add(new CyVector2d(x0, y0));
        verts.add(new CyVector2d(x1, y1));
    }

//    public boolean contains(double x, double y)
//    {
//        int size = verts.size();
//        int crossings = 0;
//        for (int i = 0; i < size; ++i)
//        {
//            CyVector2d v0 = verts.get(i);
//            CyVector2d v1 = verts.get(i == size - 1 ? 0 : i + 1);
//
//            if (v0.x <= x && v1.x > x)
//            {
//                //Line y = mx + b
//                double m = (v1.y - v0.y) / (v1.x - v0.x);
//                double b = v0.y - m * v0.x;
//
//                double yHit = m * x + b;
//                if (yHit > y)
//                {
//                    ++crossings;
//                }
//            }
//            else if (v0.x >= x && v1.x < x)
//            {
//                //Line y = mx + b
//                double m = (v1.y - v0.y) / (v1.x - v0.x);
//                double b = v0.y - m * v0.x;
//
//                double yHit = m * x + b;
//                if (yHit > y)
//                {
//                    --crossings;
//                }
//            }
//        }
//
//        return crossings != 0;
//    }
//
//    /**
//     *
//     * @param rect
//     * @return true if rectangle completely bounded by shape
//     */
//    public boolean contains(CyRectangle2d rect)
//    {
//        if (crosses(rect))
//        {
//            return false;
//        }
//
//        return contains(rect.getX(), rect.getY());
//    }

    /**
     *
     * @param rect
     * @return true if the intersection of this shape and rectangle
     * contain any common points.
     */
    public boolean intersectsEdge(CyRectangle2d rect)
    {
        for (int i = 0; i < verts.size(); i += 2)
        {
            CyVector2d v0 = verts.get(i);
            CyVector2d v1 = verts.get(i + 1);
            
            if (rect.intersectsSegment(v0.x, v0.y, v1.x, v1.y))
            {
                return true;
            }
        }
        
        return false;
    }

//    public boolean intersectsEdge(CyRectangle2d rect)
//    {
//        if (crosses(rect))
//        {
//            return true;
//        }
//
//        if (verts.isEmpty())
//        {
//            return false;
//        }
//        CyVector2d v = verts.get(0);
//        return rect.contains(v.x, v.y) || contains(rect.getX(), rect.getY());
//    }

//    private boolean crossing(double[] res)
//    {
//        return res != null && res[0] >= 0 && res[0] <= 1 && res[1] >= 0 && res[1] <= 1;
//    }
//
//    /**
//     *
//     * @param rect
//     * @return true if any line segment of this path crosses any line
//     * segment of the passed rect.
//     */
//    public boolean crosses(CyRectangle2d rect)
//    {
//        double x = rect.getX();
//        double y = rect.getY();
//        double width = rect.getWidth();
//        double height = rect.getHeight();
//        double[] frac = new double[2], res;
//
//        int size = verts.size();
//        for (int i = 0; i < size; ++i)
//        {
//            CyVector2d v0 = verts.get(i);
//            CyVector2d v1 = verts.get(i == size - 1 ? 0 : i + 1);
//
//            res = Math2DUtil.lineIsectFractions(
//                    v0.x, v0.y, v1.x - v0.x, v1.y - v0.y,
//                    x, y, width, 0,
//                    frac);
//
//            if (crossing(res))
//            {
//                return true;
//            }
//
//            res = Math2DUtil.lineIsectFractions(
//                    v0.x, v0.y, v1.x - v0.x, v1.y - v0.y,
//                    x + width, y, 0, height,
//                    frac);
//
//            if (crossing(res))
//            {
//                return true;
//            }
//
//            res = Math2DUtil.lineIsectFractions(
//                    v0.x, v0.y, v1.x - v0.x, v1.y - v0.y,
//                    x + width, y + height, -width, 0,
//                    frac);
//
//            if (crossing(res))
//            {
//                return true;
//            }
//
//            res = Math2DUtil.lineIsectFractions(
//                    v0.x, v0.y, v1.x - v0.x, v1.y - v0.y,
//                    x, y + height, 0, -height,
//                    frac);
//
//            if (crossing(res))
//            {
//                return true;
//            }
//
//        }
//
//        return false;
//    }

}
