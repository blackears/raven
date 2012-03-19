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
public class PaintLayoutManipulatorTexture extends PaintLayoutManipulator
{
    RavenPaintLayout initLayout;
    RavenPaintLayout lastLayout;

    public PaintLayoutManipulatorTexture(
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

        LayoutPoints lp = new LayoutPoints(initLayout);

        if (handleHit(x, y,
                lp.ptBasisX.x,
                lp.ptBasisX.y,
                w2d))
        {
            return new Handle(new CyVector2d(x, y), w2d, Type.RADIUSX);
        }

        if (handleHit(x, y,
                lp.ptBasisY.x,
                lp.ptBasisY.y,
                w2d))
        {
            return new Handle(new CyVector2d(x, y), w2d, Type.RADIUSY);
        }

        if (handleHit(x, y, lp.ptOrigin.x, lp.ptOrigin.y, w2d))
        {
            return new Handle(new CyVector2d(x, y), w2d, Type.CENTER);
        }

        return null;
    }

    @Override
    public void paint(Graphics2D g, CyMatrix4d w2d)
    {
        LayoutPoints lp = new LayoutPoints(lastLayout);

        CyVector2d ptOrigin = new CyVector2d();
        CyVector2d ptBasisX = new CyVector2d();
        CyVector2d ptBasisY = new CyVector2d();
        w2d.transformPoint(lp.ptOrigin, ptOrigin);
        w2d.transformPoint(lp.ptBasisX, ptBasisX);
        w2d.transformPoint(lp.ptBasisY, ptBasisY);

        g.setColor(Color.blue);
        {
            Line2D.Double line = new Line2D.Double(
                    ptOrigin.x, ptOrigin.y,
                    ptBasisX.x, ptBasisX.y);
            g.draw(line);
        }

        {
            Line2D.Double line = new Line2D.Double(
                    ptOrigin.x, ptOrigin.y,
                    ptBasisY.x, ptBasisY.y);
            g.draw(line);
        }

        paintHandleShape(g, ptOrigin, DRAG_HANDLE_SHAPE, Color.white, Color.black);
        paintHandleShape(g, ptBasisX, DRAG_HANDLE_SHAPE, Color.white, Color.black);
        paintHandleShape(g, ptBasisY, DRAG_HANDLE_SHAPE, Color.white, Color.black);
    }


    @Override
    public void rebuild()
    {
        initLayout = lastLayout;
    }

    //----------------------------------
    static enum Type
    {
        CENTER, RADIUSX, RADIUSY
    }

    class LayoutPoints
    {
        CyVector2d ptOrigin = new CyVector2d();
        CyVector2d ptBasisX = new CyVector2d();
        CyVector2d ptBasisY = new CyVector2d();

        LayoutPoints(RavenPaintLayout layout)
        {
            layout.getCenter(ptOrigin);
            layout.getRadiusX(ptBasisX);
            layout.getRadiusY(ptBasisY);
  
            ptBasisX.add(ptOrigin);
            ptBasisY.add(ptOrigin);
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
//            mulVector(d2w, delta, delta);

            LayoutPoints lp = new LayoutPoints(initLayout);

            switch (type)
            {
                case CENTER:
                {
                    lp.ptOrigin.x += delta.x;
                    lp.ptOrigin.y += delta.y;

                    //Move everything along with center
                    lp.ptBasisX.x += delta.x;
                    lp.ptBasisX.y += delta.y;
                    lp.ptBasisY.x += delta.x;
                    lp.ptBasisY.y += delta.y;
                    break;
                }
                case RADIUSX:
                {
                    lp.ptBasisX.x += delta.x;
                    lp.ptBasisX.y += delta.y;
                    break;
                }
                case RADIUSY:
                {
                    lp.ptBasisY.x += delta.x;
                    lp.ptBasisY.y += delta.y;
                    break;
                }
            }

//            double radiusXdx = lp.ptBasisX.x - lp.ptOrigin.x;
//            double radiusXdy = lp.ptBasisX.y - lp.ptOrigin.y;
//            double radiusYdx = lp.ptBasisY.x - lp.ptOrigin.x;
//            double radiusYdy = lp.ptBasisY.y - lp.ptOrigin.y;
//
//            double scaleXlen = Math.sqrt(radiusXdx * radiusXdx + radiusXdy * radiusXdy);
//            double scaleYlen = Math.sqrt(radiusYdx * radiusYdx + radiusYdy * radiusYdy);
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
//            lastLayout = new PaintLayoutTexture(
//                    (float)lp.ptOrigin.x, (float)lp.ptOrigin.y,
//                    (float)scaleXlen,
//                    (float)scaleYlen,
//                    (float)angle,
//                    (float)skewAngle
//                    );
            
//            lastLayout = RavenPaintLayout.createTexture2D(
//                    lp.ptOrigin, lp.ptBasisX, lp.ptBasisY);
            lastLayout = RavenPaintLayout.createTexture2D(
                    lp.ptOrigin.x, lp.ptOrigin.y, 
                    lp.ptBasisX.x - lp.ptOrigin.x, lp.ptBasisX.y - lp.ptOrigin.y, 
                    lp.ptBasisY.x - lp.ptOrigin.x, lp.ptBasisY.y - lp.ptOrigin.y);

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
