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

package com.kitfox.raven.editor.node.tools.common.shape.layout;

import com.kitfox.coyote.material.color.CyMaterialColorDrawRecord;
import com.kitfox.coyote.material.color.CyMaterialColorDrawRecordFactory;
import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.coyote.renderer.vertex.CyVertexBufferDataSquare;
import com.kitfox.coyote.renderer.vertex.CyVertexBufferDataSquareLines;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.coyote.shape.ShapeLinesProvider;
import com.kitfox.raven.editor.node.scene.RenderContext;
import com.kitfox.raven.paint.RavenPaintLayout;
import java.awt.event.MouseEvent;

/**
 *
 * @author kitfox
 */
public class PaintLayoutManipulator
{
    RavenPaintLayout layout;
//    RavenPaintLayout lastLayout;

    static final double HANDLE_RADIUS = 6;
    static final double MARGIN = 1.25;
    
    public PaintLayoutManipulator(RavenPaintLayout layout)
    {
        this.layout = layout;
    }

    public Handle getManipulatorHandle(MouseEvent evt, CyMatrix4d l2d)
    {
        int x = evt.getX();
        int y = evt.getY();

        LayoutPoints lp = new LayoutPoints(l2d, layout);

        CyRectangle2d pick = new CyRectangle2d(
                x - HANDLE_RADIUS, y - HANDLE_RADIUS,
                HANDLE_RADIUS * 2, HANDLE_RADIUS * 2);
        
        CyMatrix4d d2l = new CyMatrix4d(l2d);
        d2l.invert();
        
        if (pick.contains(lp.ptScaleX))
        {
            return new Handle(layout, 
                    lp.ptScaleX, d2l, Type.SCALEX);
        }
        
        if (pick.contains(lp.ptScaleY))
        {
            return new Handle(layout, 
                    lp.ptScaleY, d2l, Type.SCALEY);
        }
        
        if (pick.contains(lp.ptCenter))
        {
            return new Handle(layout, 
                    lp.ptCenter, d2l, Type.CENTER);
        }
        
        if (pick.contains(lp.ptRotate))
        {
            return new Handle(layout, 
                    lp.ptRotate, d2l, Type.ROTATE);
        }
        
        if (pick.contains(lp.ptSkew))
        {
            return new Handle(layout, 
                    lp.ptSkew, d2l, Type.SKEW);
        }

        return null;
    }

//    public void rebuild()
//    {
//        layout = lastLayout;
//    }
    
    public void render(RenderContext ctx)
    {
        CyDrawStack stack = ctx.getDrawStack();

        CyMatrix4d l2d = stack.getModelViewXform();
        CyMatrix4d proj = stack.getProjXform();
//        CyMatrix4d mvp = stack.getProjXform();
//        mvp.mul(mv);
        
        LayoutPoints lp = new LayoutPoints(l2d, layout);
        
        CyVector2d c = lp.ptCenter;
        CyVector2d bx = lp.basisX;
        CyVector2d by = lp.basisY;
        
        //Wireframe
        CyPath2d pathKnot = new CyPath2d();
        pathKnot.moveTo(c.x, c.y);
        pathKnot.lineTo(c.x + bx.x * MARGIN, c.y + bx.y * MARGIN);

        pathKnot.moveTo(c.x + bx.x, c.y + bx.y);
        pathKnot.lineTo(c.x + bx.x + by.x * MARGIN, c.y + bx.y + by.y * MARGIN);

        pathKnot.moveTo(c.x, c.y);
        pathKnot.lineTo(c.x + by.x, c.y + by.y);
        pathKnot.lineTo(c.x + by.x + bx.x * MARGIN, c.y + by.y + bx.y * MARGIN);
        
        ShapeLinesProvider lines = new ShapeLinesProvider(pathKnot);
        CyVertexBuffer buf = new CyVertexBuffer(lines);
        drawShape(stack, buf, proj, CyColor4f.BLUE);
        
        drawKnot(stack, proj, lp.ptScaleX);
        drawKnot(stack, proj, lp.ptScaleY);
        drawKnot(stack, proj, lp.ptCenter);
        drawKnot(stack, proj, lp.ptRotate);
        drawKnot(stack, proj, lp.ptSkew);
    }    
    
    private void drawKnot(CyDrawStack stack, CyMatrix4d proj, CyVector2d pt)
    {
        CyVertexBuffer bufSquare = CyVertexBufferDataSquare.inst().getBuffer();
        CyVertexBuffer bufSquareLines = CyVertexBufferDataSquareLines.inst().getBuffer();
        
        CyMatrix4d mvp = new CyMatrix4d(proj);
        mvp.translate(pt.x, pt.y, 0);
        mvp.scale(HANDLE_RADIUS, HANDLE_RADIUS, 1);
        mvp.translate(-.5, -.5, 0);
        
        drawShape(stack, bufSquare, mvp, CyColor4f.WHITE);
        drawShape(stack, bufSquareLines, mvp, CyColor4f.BLACK);
        
    }
    
    private static void drawShape(CyDrawStack stack, 
            CyVertexBuffer buf, CyMatrix4d mvp, CyColor4f color)
    {
        CyMaterialColorDrawRecord rec = 
                CyMaterialColorDrawRecordFactory.inst().allocRecord();

        rec.setColor(color);

        rec.setMesh(buf);

        rec.setOpacity(1);

        rec.setMvpMatrix(mvp);
        
        stack.addDrawRecord(rec);
    }

    private void updateLayout(RavenPaintLayout newLayout)
    {
        this.layout = newLayout;
    }
    
    //----------------------------------
    static enum Type
    {
        CENTER, SCALEX, SCALEY, ROTATE, SKEW
    }

    public class LayoutPoints
    {
        //Handle locations in device space
        CyVector2d ptCenter = new CyVector2d();
        CyVector2d ptScaleX = new CyVector2d();
        CyVector2d ptScaleY = new CyVector2d();
        CyVector2d ptRotate = new CyVector2d();
        CyVector2d ptSkew = new CyVector2d();

        CyVector2d basisX = new CyVector2d();
        CyVector2d basisY = new CyVector2d();

//        public LayoutPoints(LayoutPoints pts)
//        {
//            ptCenter.set(pts.ptCenter);
//            ptScaleX.set(pts.ptScaleX);
//            ptScaleY.set(pts.ptScaleY);
//            ptRotate.set(pts.ptRotate);
//            ptSkew.set(pts.ptSkew);
//            
//            basisX.set(pts.basisX);
//            basisY.set(pts.basisY);
//        }
        
        public LayoutPoints(CyMatrix4d l2d, RavenPaintLayout layout)
        {
            layout.getCenter(ptCenter);
            layout.getBasisX(basisX);
            layout.getBasisY(basisY);

            ptScaleX.set(ptCenter);
            ptScaleX.add(basisX);

            ptScaleY.set(ptCenter);
            ptScaleY.add(basisY);

            ptSkew.set(basisY);
            ptSkew.scale(.5);
            ptSkew.add(ptCenter);

            ptRotate.set(basisX);
            ptRotate.scale(MARGIN);
            ptRotate.add(ptCenter);

            l2d.transformPoint(ptCenter, ptCenter);
            l2d.transformPoint(ptScaleX, ptScaleX);
            l2d.transformPoint(ptScaleY, ptScaleY);
            l2d.transformPoint(ptRotate, ptRotate);
            l2d.transformPoint(ptSkew, ptSkew);
            
            l2d.transformVector(basisX);
            l2d.transformVector(basisY);
        }
    }

    public class Handle
    {
        RavenPaintLayout initLayout;
        CyVector2d anchor;
        final Type type;
        CyMatrix4d d2l;

        public Handle(RavenPaintLayout initLayout, CyVector2d anchor, 
                CyMatrix4d d2l, Type type)
        {
            this.initLayout = initLayout;
            this.anchor = anchor;
            this.d2l = d2l;
            this.type = type;
        }

        public void dragTo(int x, int y)
        {
            //Operating in local space
            CyVector2d delta = new CyVector2d(x - anchor.getX(), y - anchor.getY());
            d2l.transformVector(delta);

            CyVector2d center = new CyVector2d();
            CyVector2d basisX = new CyVector2d();
            CyVector2d basisY = new CyVector2d();
            
            initLayout.getBasisX(basisX);
            initLayout.getBasisY(basisY);
            initLayout.getCenter(center);
            
            switch (type)
            {
                case CENTER:
                    center.add(delta);
                    break;
                case SCALEX:
                {
                    CyVector2d b = new CyVector2d(basisX);
                    b.add(delta);

                    CyVector2d newBasis = b.projectOnto(basisX);
                    basisX.set(newBasis);
                    break;
                }
                case SCALEY:
                {
                    CyVector2d b = new CyVector2d(basisY);
                    b.add(delta);

                    CyVector2d newBasis = b.projectOnto(basisY);
                    basisY.set(newBasis);
                    break;
                }
                case ROTATE:
                {
                    CyVector2d b0 = new CyVector2d(basisX);
                    b0.scale(MARGIN);
                    
                    CyVector2d b1 = new CyVector2d(b0);
                    b1.add(delta);
                    
                    double a0 = Math.atan2(b0.y, b0.x);
                    double a1 = Math.atan2(b1.y, b1.x);
                    double da = a1 - a0;
                    
                    basisX.rotate(da);
                    basisY.rotate(da);
                    break;
                }
                case SKEW:
                {
                    CyVector2d b0 = new CyVector2d(basisY);
                    b0.scale(.5);
                    
                    CyVector2d b1 = new CyVector2d(b0);
                    b1.add(delta);
                    
                    double a0 = Math.atan2(b0.y, b0.x);
                    double a1 = Math.atan2(b1.y, b1.x);
                    double da = a1 - a0;
                    
                    basisY.rotate(da);
                    break;
                }
            }
                    
            RavenPaintLayout newLayout = 
                    RavenPaintLayout.createTexture2D(center, basisX, basisY);

            updateLayout(newLayout);
        }

        public RavenPaintLayout getLayout()
        {
            return layout;
        }
    }

    
}
