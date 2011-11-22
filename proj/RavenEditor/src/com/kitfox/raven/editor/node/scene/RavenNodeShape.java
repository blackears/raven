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

import com.kitfox.raven.util.tree.FrameKey;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.coyote.shape.CyShape;
import com.kitfox.coyote.shape.CyStrokeCap;
import com.kitfox.coyote.shape.CyStrokeJoin;
import com.kitfox.coyote.shape.PathCollector;
import com.kitfox.coyote.shape.PathConsumer;
import com.kitfox.coyote.shape.PathDasher;
import com.kitfox.coyote.shape.PathOutliner;
import com.kitfox.coyote.shape.ShapeMeshProvider;
import com.kitfox.game.control.color.PaintLayout;
import com.kitfox.game.control.color.PaintLayoutLinear;
import com.kitfox.raven.editor.node.renderer.RavenRenderer;
import com.kitfox.raven.editor.node.tools.common.ServiceMaterial;
import com.kitfox.raven.editor.paint.RavenPaint;
import com.kitfox.raven.editor.paint.RavenPaintColor;
import com.kitfox.raven.editor.stroke.RavenStroke;
import com.kitfox.raven.util.Intersection;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.PropertyDataReference;
import com.kitfox.raven.util.tree.PropertyTrackChangeEvent;
import com.kitfox.raven.util.tree.PropertyTrackKeyChangeEvent;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperListener;
import java.awt.BasicStroke;
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
    public final PropertyWrapper<RavenNodeShape, PaintLayout> paintLayout =
            new PropertyWrapper(
            this, PROP_PAINT_LAYOUT, PaintLayout.class,
            new PaintLayoutLinear());

    public static final String PROP_STROKE = "stroke";
    public final PropertyWrapper<RavenNodeShape, RavenStroke> stroke =
            new PropertyWrapper(
            this, PROP_STROKE, RavenStroke.class);

    public static final String PROP_STROKEPAINT = "strokePaint";
    public final PropertyWrapper<RavenNodeShape, RavenPaint> strokePaint =
            new PropertyWrapper(
            this, PROP_STROKEPAINT, RavenPaint.class);

    public static final String PROP_STROKE_PAINT_LAYOUT = "strokePaintLayout";
    public final PropertyWrapper<RavenNodeShape, PaintLayout> strokePaintLayout =
            new PropertyWrapper(
            this, PROP_STROKE_PAINT_LAYOUT, PaintLayout.class,
            new PaintLayoutLinear());

//    private CyVertexBuffer mesh;
//    private CyVertexBuffer strokeMesh;
//    private CyShape strokeShape;
//
    SpaceCache<CyShape, CyVertexBuffer> meshCache
            = new SpaceCache<CyShape, CyVertexBuffer>();


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
//        strokeShape = null;
    }

    abstract public CyShape createShapeLocal(FrameKey time);

    public CyShape createShapeStrokeLocal(FrameKey time)
    {
        RavenStroke cStroke = stroke.getValue();
        if (cStroke == null)
        {
            return null;
        }
        BasicStroke st = (BasicStroke)cStroke.getStroke();
        if (st == null)
        {
            return null;
        }

        CyStrokeCap cap;
        switch (st.getEndCap())
        {
            default:
            case BasicStroke.CAP_BUTT:
                cap = CyStrokeCap.BUTT;
                break;
            case BasicStroke.CAP_ROUND:
                cap = CyStrokeCap.ROUND;
                break;
            case BasicStroke.CAP_SQUARE:
                cap = CyStrokeCap.SQUARE;
                break;
        }

        CyStrokeJoin join;
        switch (st.getLineJoin())
        {
            default:
            case BasicStroke.JOIN_BEVEL:
                join = CyStrokeJoin.BEVEL;
                break;
            case BasicStroke.JOIN_MITER:
                join = CyStrokeJoin.MITER;
                break;
            case BasicStroke.JOIN_ROUND:
                join = CyStrokeJoin.ROUND;
                break;
        }

        PathCollector col = new PathCollector();
        PathConsumer builder = new PathOutliner(col,
                st.getLineWidth() / 2,
                cap,
                join,
                st.getMiterLimit());

        float[] dash = st.getDashArray();
        if (dash != null && dash.length >= 2)
        {
            double[] dashD = new double[dash.length];
            for (int i = 0; i < dash.length; ++i)
            {
                dashD[i] = dash[i];
            }
            builder = new PathDasher(builder, dashD, st.getDashPhase());
        }

        CyShape local = createShapeLocal(time);
        builder.feedShape(local);

        return col.getPath();
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

        CyVertexBuffer mesh = meshCache.get(shape);
        if (mesh == null)
        {
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

        CyVertexBuffer mesh = meshCache.get(shape);
        if (mesh == null)
        {
            ShapeMeshProvider meshProv = new ShapeMeshProvider(shape);
            mesh = new CyVertexBuffer(meshProv);
            meshCache.put(shape, mesh);
        }
        meshCache.flush();
        return mesh;
    }

//    protected void clearCacheTrack(PropertyWrapper wrap, int trackUid, int frame)
//    {
//    }
//
//    protected void clearCacheProp(PropertyWrapper wrap)
//    {
//        //Clear all cache spot not writing to 'wrap'
//    }

//    public CyVertexBuffer getMesh()
//    {
//        if (mesh == null)
//        {
//            ShapeMeshProvider meshProv = new ShapeMeshProvider(getShapePickLocal());
//            mesh = new CyVertexBuffer(meshProv);
//        }
//
//        return mesh;
//    }

    @Override
    public CyShape getShapePickLocal()
    {
        return createShapeLocal(FrameKey.DIRECT);
    }

//    private HashKey calcHash(CyShape shape)
//    {
//        try
//        {
//            HashKeyOutputStream hout = new HashKeyOutputStream();
//            DataOutputStream dout = new DataOutputStream(hout);
//            shape.export(dout);
//            dout.close();
//
//            return hout.getKey();
//        } catch (IOException ex)
//        {
//            Logger.getLogger(RavenNodeShape.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return null;
//    }
//
//    private FrameCache createCache(FrameKey key)
//    {
//        CyShape shape = getShapeLocal(key);
//
//        FrameCache cache = new FrameCache();
//
//
//    }

    @Override
    protected void renderContent(RenderContext ctx)
    {
        CyDrawStack stack = ctx.getDrawStack();

        FrameKey frame = ctx.getFrame();
//        FrameCache cacheInfo = frameCache.get(key);
//        if (cacheInfo == null)
//        {
//            cacheInfo = createCache(key);
//            frameCache.put(key, cacheInfo);
//        }




        CyRectangle2d bounds = getBoundsLocal(frame);
        if (!stack.intersectsFrustum(bounds))
        {
            return;
        }

        //Gets local shape

        RavenPaint curFillPaint = paint.getValue(frame);
        PaintLayout curFillLayout = paintLayout.getValue(frame);
        RavenPaint curStrokePaint = strokePaint.getValue(frame);
        PaintLayout curStrokeLayout = strokePaintLayout.getValue(frame);
//        RavenStroke curStroke = stroke.getValue();

        if (curFillPaint != null)
        {
//            CyShape shape = getShapePickLocal();
            CyVertexBuffer mesh = getMeshLocal(frame);
            if (mesh != null)
            {
                curFillPaint.fillShape(stack, curFillLayout, mesh);
            }

        }

        if (curStrokePaint != null)
        {
//            CyShape shape = calcShapeStrokeLocal();
            CyVertexBuffer mesh = getMeshStrokeLocal(frame);
            if (mesh != null)
            {
                curStrokePaint.fillShape(stack, curStrokeLayout, mesh);
            }
        }
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

//    @Override
//    abstract public CyShape getShapeLocal();
//
//    public Shape getShapeStrokedLocal()
//    {
//        if (strokeShape == null)
//        {
//            CyShape shape = getShapeLocal();
//
//            RavenStroke cStroke = stroke.getValue();
//            if (shape != null && cStroke != null && cStroke != RavenStrokeNone.STROKE)
//            {
//                Stroke str = cStroke.getStroke();
//                strokeShape = str.createStrokedShape(shape);
//            }
//        }
//
//        return strokeShape;
//    }
//
//    @Override
//    public Shape getPickShapeLocal()
//    {
//        Shape fillShape = getShapeLocal();
//        Shape strokeShape = getShapeStrokedLocal();
//
//        RavenPaint cFillPaint = paint.getValue();
//        if (cFillPaint == null && cFillPaint != RavenPaintNone.PAINT)
//        {
//            fillShape = null;
//        }
//
//        RavenPaint cStrokePaint = strokePaint.getValue();
//        if (cStrokePaint == null && cStrokePaint != RavenPaintNone.PAINT)
//        {
//            strokeShape = null;
//        }
//
//        if (fillShape != null && strokeShape != null)
//        {
//            Path2D.Double path = new Path2D.Double(fillShape);
//            path.append(strokeShape, false);
//            return path;
//        }
//        if (fillShape != null)
//        {
//            return fillShape;
//        }
//
//        return strokeShape;
//    }

    @Override
    public void floodFill(RavenPaint paint,
            CyRectangle2d pickArea,
            CyMatrix4d worldToPick, Intersection isect)
    {
        CyShape shape = getShapePickLocal();
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

        return getBoundsLocal();
    }

    @Override
    public RavenPaint getMaterialFacePaint(Integer subselection)
    {
        return paint.getValue();
    }

    @Override
    public PaintLayout getMaterialFaceLayout(Integer subselection)
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

        return getBoundsLocal();
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
    public PaintLayout getMaterialEdgeLayout(Integer subselection)
    {
        return strokePaintLayout.getValue();
    }


    @Override
    public void setMaterialFacePaint(Integer subselection, RavenPaint value, boolean history)
    {
        if (value instanceof NodeObject)
        {
            paint.setData(new PropertyDataReference<RavenPaint>(
                    ((NodeObject)value).getUid()), history);
        }
        else
        {
            paint.setValue(value, history);
        }
    }

    @Override
    public void setMaterialFaceLayout(Integer subselection, PaintLayout value, boolean history)
    {
        paintLayout.setValue(value, history);
    }

    @Override
    public void setMaterialEdgeStroke(Integer subselection, RavenStroke value, boolean history)
    {
        if (value instanceof NodeObject)
        {
            stroke.setData(new PropertyDataReference<RavenStroke>(
                    ((NodeObject)value).getUid()), history);
        }
        else
        {
            stroke.setValue(value, history);
        }
    }

    @Override
    public void setMaterialEdgePaint(Integer subselection, RavenPaint value, boolean history)
    {
        if (value instanceof NodeObject)
        {
            strokePaint.setData(new PropertyDataReference<RavenPaint>(
                    ((NodeObject)value).getUid()), history);
        }
        else
        {
            strokePaint.setValue(value, history);
        }
    }

    @Override
    public void setMaterialEdgeLayout(Integer subselection, PaintLayout value, boolean history)
    {
        strokePaintLayout.setValue(value, history);
    }

    //---------------------------------
//    class FrameCache
//    {
//        //Precompiled meshes
//        CyVertexBuffer shape;
//        CyVertexBuffer strokeShape;
//    }
}
