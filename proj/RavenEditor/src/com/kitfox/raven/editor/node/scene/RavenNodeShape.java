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

package com.kitfox.raven.editor.node.scene;

import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.raven.util.tree.FrameKey;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.coyote.shape.*;
import com.kitfox.raven.editor.node.renderer.RavenRenderer;
import com.kitfox.raven.editor.node.tools.common.ServiceMaterial;
import com.kitfox.raven.paint.RavenPaint;
import com.kitfox.raven.paint.RavenPaintLayout;
import com.kitfox.raven.paint.RavenStroke;
import com.kitfox.raven.paint.common.RavenPaintColor;
import com.kitfox.raven.util.Intersection;
import com.kitfox.raven.util.tree.PropertyTrackChangeEvent;
import com.kitfox.raven.util.tree.PropertyTrackKeyChangeEvent;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperListener;
import java.awt.Color;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author kitfox
 */
abstract public class RavenNodeShape extends RavenNodeXformable
        implements ServiceMaterial
{
    public static final String PROP_PAINT = "paint";
    public final PropertyWrapper<RavenNodeShape, RavenPaint> paint =
            new PropertyWrapper(
            this, PROP_PAINT, RavenPaint.class,
            new RavenPaintColor(Color.BLACK));

    public static final String PROP_PAINT_LAYOUT = "paintLayout";
    public final PropertyWrapper<RavenNodeShape, RavenPaintLayout> paintLayout =
            new PropertyWrapper(
            this, PROP_PAINT_LAYOUT, RavenPaintLayout.class,
            new RavenPaintLayout());

    public static final String PROP_STROKE = "stroke";
    public final PropertyWrapper<RavenNodeShape, RavenStroke> stroke =
            new PropertyWrapper(
            this, PROP_STROKE, RavenStroke.class, new RavenStroke());

    public static final String PROP_STROKEPAINT = "strokePaint";
    public final PropertyWrapper<RavenNodeShape, RavenPaint> strokePaint =
            new PropertyWrapper(
            this, PROP_STROKEPAINT, RavenPaint.class, 
            new RavenPaintColor(CyColor4f.TRANSPARENT));

    public static final String PROP_STROKE_PAINT_LAYOUT = "strokePaintLayout";
    public final PropertyWrapper<RavenNodeShape, RavenPaintLayout> strokePaintLayout =
            new PropertyWrapper(
            this, PROP_STROKE_PAINT_LAYOUT, RavenPaintLayout.class,
            new RavenPaintLayout());

    SpaceCache<CyShape, CyVertexBuffer> meshCache
            = new SpaceCache<CyShape, CyVertexBuffer>();
    
    static final CyMatrix4d meshToLocal;
    static {
        meshToLocal = CyMatrix4d.createIdentity();
        meshToLocal.scale(.01, .01, 1);
    }


    public RavenNodeShape(int uid)
    {
        super(uid);

        stroke.addPropertyWrapperListener(new PropertyWrapperListener()
        {
            @Override
            public void propertyWrapperDataChanged(PropertyChangeEvent evt)
            {
                clearCache();
            }

            @Override
            public void propertyWrapperTrackChanged(PropertyTrackChangeEvent evt)
            {
            }

            @Override
            public void propertyWrapperTrackKeyChanged(PropertyTrackKeyChangeEvent evt)
            {
            }
        });
    }

    @Override
    protected void clearCache()
    {
        super.clearCache();
    }

    abstract public CyShape createShapeLocal(FrameKey time);

    public CyShape createShapeStrokeLocal(FrameKey time)
    {
        RavenStroke cStroke = stroke.getValue();
        if (cStroke == null)
        {
            return null;
        }
        
        CyShape local = createShapeLocal(time);
        CyStroke st = cStroke.getStroke();
        return st.outlineShape(local);
    }

    public CyShape getShapeLocal(FrameKey time)
    {
        return createShapeLocal(time);
    }

    public CyShape getShapeStrokeLocal(FrameKey time)
    {
        return createShapeStrokeLocal(time);
    }

    public CyVertexBuffer getMeshLocal(FrameKey time)
    {
        CyShape shape = getShapeLocal(time);
        if (shape == null)
        {
            return null;
        }

        CyVertexBuffer mesh = meshCache.get(shape);
        if (mesh == null)
        {
            CyMatrix4d m = CyMatrix4d.createIdentity();
            m.scale(100, 100, 1);
            shape = shape.createTransformedPath(m);
            
            ShapeMeshProvider meshProv = new ShapeMeshProvider(shape);
            mesh = new CyVertexBuffer(meshProv);
            meshCache.put(shape, mesh);
        }
        meshCache.flush();
        return mesh;
    }

    public CyVertexBuffer getMeshStrokeLocal(FrameKey time)
    {
        CyShape shape = getShapeStrokeLocal(time);
        if (shape == null)
        {
            return null;
        }

        CyVertexBuffer mesh = meshCache.get(shape);
        if (mesh == null)
        {
            CyMatrix4d m = CyMatrix4d.createIdentity();
            m.scale(100, 100, 1);
            shape = shape.createTransformedPath(m);
            
            ShapeMeshProvider meshProv = new ShapeMeshProvider(shape);
            mesh = new CyVertexBuffer(meshProv);
            meshCache.put(shape, mesh);
        }
        meshCache.flush();
        return mesh;
    }

    @Override
    public CyRectangle2d getBoundsWorld()
    {
        return getBoundsWorld(FrameKey.DIRECT);
    }

    @Override
    public CyShape getShapeWorld()
    {
        return getShapeWorld(FrameKey.DIRECT);
    }

    @Override
    public CyShape getShapePickLocal(FrameKey key)
    {
        return createShapeLocal(key);
    }

    @Override
    protected void renderContent(RenderContext ctx)
    {
        CyDrawStack stack = ctx.getDrawStack();

        FrameKey frame = ctx.getFrame();

        CyRectangle2d bounds = getBoundsLocal(frame);
        if (!stack.intersectsFrustum(bounds))
        {
            return;
        }

        //Gets local shape

        RavenPaint curFillPaint = paint.getValue(frame);
        RavenPaintLayout curFillLayout = paintLayout.getValue(frame);
        RavenPaint curStrokePaint = strokePaint.getValue(frame);
        RavenPaintLayout curStrokeLayout = strokePaintLayout.getValue(frame);

//        stack.pushFrame(null);
//        stack.scale(.01, .01, 1);
        if (curFillPaint != null)
        {
            CyVertexBuffer mesh = getMeshLocal(frame);
            if (mesh != null)
            {
                curFillPaint.fillShape(stack, curFillLayout, mesh, meshToLocal);
            }

        }

        if (curStrokePaint != null)
        {
            CyVertexBuffer mesh = getMeshStrokeLocal(frame);
            if (mesh != null)
            {
                curStrokePaint.fillShape(stack, curStrokeLayout, mesh, meshToLocal);
            }
        }
//        stack.popFrame();
    }


    @Override
    protected void renderContent(RavenRenderer renderer)
    {
//        CyRectangle2d bounds = getBoundsLocal();
//        if (renderer.isBoundsClipped(bounds))
//        {
//            return;
//        }
//
////        if (editorSupressRendering)
////        {
////            return;
////        }
//
//        //Gets local shape
//        CyShape shape = getShapeLocal();
//
//        RavenPaint curFillPaint = paint.getValue();
//        PaintLayout curFillLayout = paintLayout.getValue();
//        RavenPaint curStrokePaint = strokePaint.getValue();
//        PaintLayout curStrokeLayout = strokePaintLayout.getValue();
//        RavenStroke curStroke = stroke.getValue();
//
//        if (curFillPaint != null
//                && curFillPaint != RavenPaintNone.PAINT
//                && curFillLayout != null)
//        {
//            renderer.setPaint(curFillPaint);
//            renderer.setPaintLayout(curFillLayout);
//            renderer.fill(shape);
//        }
//
//        if (curStrokePaint != null
//                && curStrokePaint != RavenPaintNone.PAINT
//                && curStrokeLayout != null
//                && curStroke != null)
//        {
//            renderer.setStroke(curStroke.getStroke());
//            renderer.setPaint(curStrokePaint);
//            renderer.setPaintLayout(curStrokeLayout);
//            renderer.draw(shape);
//        }
    }

    @Override
    public void floodFill(RavenPaint paint,
            CyRectangle2d pickArea,
            CyMatrix4d worldToPick, Intersection isect)
    {
        CyShape shape = getShapePickLocal(FrameKey.DIRECT);
        if (shape == null)
        {
            return;
        }
        CyMatrix4d localToPick = new CyMatrix4d();
        getLocalToWorldTransform(localToPick);
        localToPick.mul(worldToPick, localToPick);

        CyPath2d pickShape = shape.createTransformedPath(localToPick);

        switch (isect)
        {
            case INSIDE:
                if (!pickShape.contains(pickArea))
                {
                    return;
                }
            case INTERSECTS:
                if (!pickShape.intersects(pickArea))
                {
                    return;
                }
            case CONTAINS:
            default:
                if (!pickArea.contains(pickShape.getBounds()))
                {
                    return;
                }
        }

        if (paint != null)
        {
            this.paint.setValue(paint);
        }
    }

    @Override
    public void floodStroke(RavenPaint paint, RavenStroke stroke, 
            CyRectangle2d pickArea,
            CyMatrix4d worldToPick, Intersection isect)
    {
        CyShape shape = getShapeStrokeLocal(FrameKey.DIRECT);
        if (shape == null)
        {
            return;
        }
        CyMatrix4d localToPick = new CyMatrix4d();
        getLocalToWorldTransform(localToPick);
        localToPick.mul(worldToPick, localToPick);

        CyPath2d pickShape = shape.createTransformedPath(localToPick);

        switch (isect)
        {
            case INSIDE:
                if (!pickShape.contains(pickArea))
                {
                    return;
                }
            case INTERSECTS:
                if (!pickShape.intersects(pickArea))
                {
                    return;
                }
            case CONTAINS:
            default:
                if (!pickArea.contains(pickShape.getBounds()))
                {
                    return;
                }
        }

        if (paint != null)
        {
            this.strokePaint.setValue(paint);
        }
        if (stroke != null)
        {
            this.stroke.setValue(stroke);
        }
    }

    @Override
    public CyRectangle2d getMaterialFaceBounds(Integer subselection)
    {
        if (subselection != null)
        {
            //Shapes should not have subselections
            return null;
        }

        return getBoundsLocal(FrameKey.DIRECT);
    }

    @Override
    public RavenPaint getMaterialFacePaint(Integer subselection)
    {
        return paint.getValue();
    }

    @Override
    public RavenPaintLayout getMaterialFaceLayout(Integer subselection)
    {
        return paintLayout.getValue();
    }

    @Override
    public CyRectangle2d getMaterialEdgeBounds(Integer subselection)
    {
        if (subselection != null)
        {
            //Shapes should not have subselections
            return null;
        }

        return getBoundsLocal(FrameKey.DIRECT);
    }

    @Override
    public RavenStroke getMaterialEdgeStroke(Integer subselection)
    {
        return stroke.getValue();
    }

    @Override
    public RavenPaint getMaterialEdgePaint(Integer subselection)
    {
        return strokePaint.getValue();
    }

    @Override
    public RavenPaintLayout getMaterialEdgeLayout(Integer subselection)
    {
        return strokePaintLayout.getValue();
    }


    @Override
    public void setMaterialFacePaint(Integer subselection, RavenPaint value, boolean history)
    {
        paint.setValue(value, history);
    }

    @Override
    public void setMaterialFaceLayout(Integer subselection, RavenPaintLayout value, boolean history)
    {
        paintLayout.setValue(value, history);
    }

    @Override
    public void setMaterialEdgeStroke(Integer subselection, RavenStroke value, boolean history)
    {
        stroke.setValue(value, history);
    }

    @Override
    public void setMaterialEdgePaint(Integer subselection, RavenPaint value, boolean history)
    {
        strokePaint.setValue(value, history);
    }

    @Override
    public void setMaterialEdgeLayout(Integer subselection, RavenPaintLayout value, boolean history)
    {
        strokePaintLayout.setValue(value, history);
    }

}
