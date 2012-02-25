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

package com.kitfox.raven.editor.node.tools.common.select;

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.game.control.color.PaintLayoutLinear;
import com.kitfox.raven.paint.RavenPaintLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class PaintLayoutManipulatorLinear extends PaintLayoutManipulator
{
    RavenPaintLayout initLayout;
    RavenPaintLayout lastLayout;

    public PaintLayoutManipulatorLinear(
            ArrayList<MaterialElement> compList,
            boolean strokeMode,
            RavenPaintLayout layout)
    {
        super(compList, strokeMode);
        this.initLayout = this.lastLayout = layout;
    }

    @Override
    public PaintLayoutManipulatorHandle getManipulatorHandle(
            MouseEvent evt, CyMatrix4d w2d)
    {
        int x = evt.getX();
        int y = evt.getY();

        //CyMatrix4d p2l = initLayout.getPaintToLocal();
        CyVector2d startPt = new CyVector2d();
        CyVector2d endPt = new CyVector2d();
        initLayout.getLinearLayout(startPt, endPt);
        
        if (handleHit(x, y, startPt.x, startPt.y, w2d))
        {
            return new Handle(new CyVector2d(x, y),
                    w2d, Type.START);
        }

        if (handleHit(x, y, endPt.x, endPt.y, w2d))
        {
            return new Handle(new CyVector2d(x, y),
                    w2d, Type.END);
        }

        return null;
    }

    @Override
    public void paint(Graphics2D g, CyMatrix4d w2d)
    {
        CyVector2d ptStart = new CyVector2d();
        CyVector2d ptEnd = new CyVector2d();
        lastLayout.getLinearLayout(ptStart, ptEnd);

        w2d.transformPoint(ptStart);
        w2d.transformPoint(ptEnd);
        
//        CyVector2d ptStart = new CyVector2d(
//                lastLayout.getStartX(), lastLayout.getStartY());
//        w2d.transformPoint(ptStart, ptStart);
//
//        CyVector2d ptEnd = new CyVector2d(
//                lastLayout.getEndX(), lastLayout.getEndY());
//        w2d.transformPoint(ptEnd, ptEnd);

        g.setColor(Color.blue);
        Line2D.Double line = new Line2D.Double(ptStart.x, ptStart.y,
                ptEnd.x, ptEnd.y);
        g.draw(line);

        paintHandleShape(g, ptStart, DRAG_HANDLE_SHAPE, Color.white, Color.black);
        paintHandleShape(g, ptEnd, PIVOT_HANDLE_SHAPE, Color.white, Color.black);
    }

    @Override
    public void rebuild()
    {
        initLayout = lastLayout;
    }

    //----------------------------------------
    static enum Type
    {
        START, END
    }

    class Handle extends PaintLayoutManipulatorHandle
    {
        final Type type;

        public Handle(CyVector2d anchor, CyMatrix4d w2d, Type type)
        {
            super(anchor, w2d);
            this.type = type;
        }

        @Override
        public void dragTo(int x, int y, PaintLayoutManipulator manip, boolean history)
        {
            CyVector2d delta = new CyVector2d(x - anchor.getX(), y - anchor.getY());

            d2w.transformVector(delta);
//            mulVector(d2w, delta, delta);

            CyVector2d startPt = new CyVector2d();
            CyVector2d endPt = new CyVector2d();
            initLayout.getLinearLayout(startPt, endPt);
//            double sx = initLayout.getStartX();
//            double sy = initLayout.getStartY();
//            double ex = initLayout.getEndX();
//            double ey = initLayout.getEndY();

            switch (type)
            {
                case START:
                {
                    startPt.add(delta);
//                    sx += (float)delta.x;
//                    sy += (float)delta.y;
                    break;
                }
                case END:
                {
                    endPt.add(delta);
//                    ex += (float)delta.x;
//                    ey += (float)delta.y;
                    break;
                }
            }

//            lastLayout = new PaintLayoutLinear(sx, sy, ex, ey);
            lastLayout = RavenPaintLayout.createLinear(startPt, endPt);
            for (MaterialElement ele: compList)
            {
                if (strokeMode)
                {
                    ele.setEdgeLayoutWorld(lastLayout, history);
                }
                else
                {
                    ele.setFaceLayoutWorld(lastLayout, history);
                }
            }
        }

    }

}
