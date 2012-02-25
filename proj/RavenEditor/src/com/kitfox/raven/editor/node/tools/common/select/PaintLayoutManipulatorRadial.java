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
public class PaintLayoutManipulatorRadial extends PaintLayoutManipulator
{
    RavenPaintLayout initLayout;
    RavenPaintLayout lastLayout;

    public PaintLayoutManipulatorRadial(
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
        int mod = evt.getModifiersEx();
        boolean ctrlDown = (mod & MouseEvent.CTRL_DOWN_MASK) != 0;

        LayoutPoints lp = new LayoutPoints(w2d, initLayout);

//        if (ctrlDown)
//        {
//            //Force selection of focus
//            return new Handle(new CyVector2d(lp.ptFocus.x, lp.ptFocus.y), w2d, Type.FOCUS);
//        }

        if (handleHit(x, y,
                lp.ptRadiusX.x,
                lp.ptRadiusX.y,
                w2d))
        {
            return new Handle(new CyVector2d(x, y), w2d, Type.RADIUSX);
        }

        if (handleHit(x, y,
                lp.ptRadiusY.x,
                lp.ptRadiusY.y,
                w2d))
        {
            return new Handle(new CyVector2d(x, y), w2d, Type.RADIUSY);
        }

        if (handleHit(x, y, lp.ptCenter.x, lp.ptCenter.y, w2d))
        {
            return new Handle(new CyVector2d(x, y), w2d, Type.CENTER);
        }

        //Point2D.Double focusLocal = initLayout.getFocusLocal();
//        if (handleHit(x, y,
//                lp.ptFocus.x,
//                lp.ptFocus.y,
//                w2d))
//        {
//            return new Handle(new CyVector2d(x, y), w2d, Type.FOCUS);
//        }

        return null;
    }

    @Override
    public void paint(Graphics2D g, CyMatrix4d w2d)
    {
        LayoutPoints lp = new LayoutPoints(w2d, lastLayout);

        g.setColor(Color.blue);
        {
            Line2D.Double line = new Line2D.Double(
                    lp.ptCenter.x, lp.ptCenter.y,
                    lp.ptRadiusX.x, lp.ptRadiusX.y);
            g.draw(line);
        }

        {
            Line2D.Double line = new Line2D.Double(
                    lp.ptCenter.x, lp.ptCenter.y,
                    lp.ptRadiusY.x, lp.ptRadiusY.y);
            g.draw(line);
        }

        paintHandleShape(g, lp.ptCenter, DRAG_HANDLE_SHAPE, Color.white, Color.black);
        paintHandleShape(g, lp.ptRadiusX, DRAG_HANDLE_SHAPE, Color.white, Color.black);
        paintHandleShape(g, lp.ptRadiusY, DRAG_HANDLE_SHAPE, Color.white, Color.black);
//        paintHandleShape(g, lp.ptFocus, PIVOT_HANDLE_SHAPE, Color.white, Color.black);
    }

    @Override
    public void rebuild()
    {
        initLayout = lastLayout;
    }

    //----------------------------------
    static enum Type
    {
        CENTER, RADIUSX, RADIUSY, FOCUS
    }

    class LayoutPoints
    {
        CyVector2d ptCenter = new CyVector2d();
//        CyVector2d ptFocus;
        CyVector2d ptRadiusX = new CyVector2d();
        CyVector2d ptRadiusY = new CyVector2d();

        LayoutPoints(CyMatrix4d w2d, RavenPaintLayout layout)
        {
            layout.getTextureLayout(ptCenter, ptRadiusX, ptRadiusY);
            w2d.transformPoint(ptCenter, ptCenter);
            w2d.transformPoint(ptRadiusX, ptRadiusX);
            w2d.transformPoint(ptRadiusY, ptRadiusY);
            
            
//            ptCenter = new CyVector2d(
//                    layout.getCenterX(), layout.getCenterY());
//            w2d.transformPoint(ptCenter, ptCenter);
//
//            ptFocus = layout.getFocusLocal();
//            w2d.transformPoint(ptFocus, ptFocus);
//
//            double cosX = Math.cos(Math.toRadians(layout.getAngle()));
//            double sinX = Math.sin(Math.toRadians(layout.getAngle()));
//            double cosY = Math.cos(Math.toRadians(layout.getAngle() + layout.getSkewAngle()));
//            double sinY = Math.sin(Math.toRadians(layout.getAngle() + layout.getSkewAngle()));
//
//            ptRadiusX = new CyVector2d(
//                    layout.getRadiusX() * cosX + layout.getCenterX(),
//                    layout.getRadiusX() * sinX + layout.getCenterY());
//            w2d.transformPoint(ptRadiusX, ptRadiusX);
//
//            ptRadiusY = new CyVector2d(
//                    layout.getRadiusY() * cosY + layout.getCenterX(),
//                    layout.getRadiusY() * sinY + layout.getCenterY());
//            w2d.transformPoint(ptRadiusY, ptRadiusY);
        }
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

            LayoutPoints lp = new LayoutPoints(w2d, initLayout);

            switch (type)
            {
                case CENTER:
                {
                    lp.ptCenter.x += delta.x;
                    lp.ptCenter.y += delta.y;

                    //Move everything along with center
//                    lp.ptFocus.x += delta.x;
//                    lp.ptFocus.y += delta.y;
                    lp.ptRadiusX.x += delta.x;
                    lp.ptRadiusX.y += delta.y;
                    lp.ptRadiusY.x += delta.x;
                    lp.ptRadiusY.y += delta.y;
                    break;
                }
                case FOCUS:
                {
//                    lp.ptFocus.x += delta.x;
//                    lp.ptFocus.y += delta.y;
                    break;
                }
                case RADIUSX:
                {
                    lp.ptRadiusX.x += delta.x;
                    lp.ptRadiusX.y += delta.y;
                    break;
                }
                case RADIUSY:
                {
                    lp.ptRadiusY.x += delta.x;
                    lp.ptRadiusY.y += delta.y;
                    break;
                }
            }

//            double radiusXdx = lp.ptRadiusX.x - lp.ptCenter.x;
//            double radiusXdy = lp.ptRadiusX.y - lp.ptCenter.y;
//            double radiusYdx = lp.ptRadiusY.x - lp.ptCenter.x;
//            double radiusYdy = lp.ptRadiusY.y - lp.ptCenter.y;
//
//            double radiusXlen = Math.sqrt(radiusXdx * radiusXdx + radiusXdy * radiusXdy);
//            double radiusYlen = Math.sqrt(radiusYdx * radiusYdx + radiusYdy * radiusYdy);
//            double angle = Math.toDegrees(Math.atan2(radiusXdy, radiusXdx));
//            double skewAngle = Math.toDegrees(Math.atan2(radiusYdy, radiusYdx)) - angle;
//            while (skewAngle < 0)
//            {
//                skewAngle += 360;
//            }
//            while (skewAngle >= 360)
//            {
//                skewAngle -= 360;
//            }
//
//            lastLayout = new PaintLayoutRadial(
//                    (float)lp.ptCenter.x, (float)lp.ptCenter.y,
//                    (float)radiusXlen,
//                    (float)radiusYlen,
//                    (float)angle,
//                    (float)skewAngle,
//                    (float)lp.ptFocus.x,
//                    (float)lp.ptFocus.y
//                    );

            lastLayout = RavenPaintLayout.createTexture2D(
                    lp.ptCenter, lp.ptRadiusX, lp.ptRadiusY);
            
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
