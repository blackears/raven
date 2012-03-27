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
 * Extracts flattened loops from a shape and allows containment
 * tests to be performed on them.
 *
 * @author kitfox
 */
public class PathClosedLoops extends PathConsumer
{
    double mx, my;
    double bx, by;
//    boolean drawingPath;

    Loop curLoop;
    ArrayList<Loop> loops = new ArrayList<Loop>();

    @Override
    public void beginPath()
    {
    }

    @Override
    public void beginSubpath(double x0, double y0)
    {
        bx = mx = x0;
        by = my = y0;
//        drawingPath = true;
        curLoop = new Loop();
        curLoop.verts.add(new CyVector2d(x0, y0));
    }

    @Override
    public void lineTo(double x0, double y0)
    {
        if (curLoop == null)
        {
            beginSubpath(mx, my);
        }

        if (mx == x0 && my == y0)
        {
            return;
        }

        //addLine(mx, my, x0, y0);
        curLoop.verts.add(new CyVector2d(x0, y0));
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
//        if (mx != bx || my != by)
//        {
//            addLine(mx, my, bx, by);
//        }

        loops.add(curLoop);
        curLoop = null;
        
        mx = bx;
        my = by;
    }

    @Override
    public void endPath()
    {
        curLoop = null;
    }

//    private void addLine(double x0, double y0, double x1, double y1)
//    {
//        curLoop.segs.add(new Seg(x0, y0, x1, y1));
//    }
    

    
    /**
     *
     * @param rect
     * @return true if rectangle completely bounded by shape
     */
    public boolean contains(CyRectangle2d rect)
    {
        int crossings = 0;
        
        for (Loop loop: loops)
        {
            if (loop.edgeIntersects(rect))
            {
                return false;
            }
            crossings += loop.countCrossings(rect.x, rect.y);
        }

        return crossings != 0;
    }

    public boolean contains(double x, double y)
    {
        int crossings = 0;
        
        for (Loop loop: loops)
        {
            crossings += loop.countCrossings(x, y);
        }

        return crossings != 0;
    }

    
    //---------------------------
    class Loop
    {
        ArrayList<CyVector2d> verts = new ArrayList<CyVector2d>();

        private boolean edgeIntersects(CyRectangle2d rect)
        {
            for (int i = 0; i < verts.size(); ++i)
            {
                int i1 = i + 1 == verts.size() ? 0 : i + 1;
                CyVector2d c0 = verts.get(i);
                CyVector2d c1 = verts.get(i1);
                
                if (rect.intersectsSegment(c0.x, c0.y, c1.x, c1.y))
                {
                    return true;
                }
            }
            return false;
        }

        private int countCrossings(double x, double y)
        {
            int crossings = 0;
            
            for (int i = 0; i < verts.size(); ++i)
            {
                int i1 = i + 1 == verts.size() ? 0 : i + 1;
                CyVector2d c0 = verts.get(i);
                CyVector2d c1 = verts.get(i1);

                if (c0.x < x && x <= c1.x)
                {
                    //Moving left to right across point
                    double side = Math2DUtil.getLineSide(c0.x, c0.y, 
                            c1.x - c0.x, c1.y - c0.y, x, y);
                    if (side < 0)
                    {
                        //On right side of line
                        --crossings;
                    }
                }
                else if (c1.x < x && x <= c0.x)
                {
                    //Moving right to left across point
                    double side = Math2DUtil.getLineSide(c0.x, c0.y, 
                            c1.x - c0.x, c1.y - c0.y, x, y);
                    if (side > 0)
                    {
                        //On left side of line
                        ++crossings;
                    }
                }
            }
            
            //return crossings != 0;
            return crossings;
        }
    }
}
