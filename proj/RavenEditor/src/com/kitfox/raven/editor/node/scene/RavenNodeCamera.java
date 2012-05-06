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

import com.kitfox.coyote.drawRecord.CyDrawRecordViewport;
import com.kitfox.coyote.material.color.CyMaterialColorDrawRecord;
import com.kitfox.coyote.material.color.CyMaterialColorDrawRecordFactory;
import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyRendererUtil2D;
import com.kitfox.coyote.renderer.vertex.CyVertexBufferDataSquareLines;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.coyote.shape.CyShape;
import com.kitfox.raven.editor.node.renderer.RavenRenderer;
import com.kitfox.raven.paint.common.RavenPaintColor;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.FrameKey;
import com.kitfox.raven.util.tree.PropertyWrapperFloat;

/**
 *
 * @author kitfox
 */
public class RavenNodeCamera extends RavenNodeXformable
        implements RenderDevice
{
    public static final String PROP_WIDTH = "width";
    public final PropertyWrapperFloat<RavenNodeCamera> width =
            new PropertyWrapperFloat(this, PROP_WIDTH, 320);

    public static final String PROP_HEIGHT = "height";
    public final PropertyWrapperFloat<RavenNodeCamera> height =
            new PropertyWrapperFloat(this, PROP_HEIGHT, 240);
//
//    public static final String PROP_CAMERA_ORDER = "cameraOrder";
//    public final PropertyWrapperInteger<RavenNodeCamera> cameraOrder =
//            new PropertyWrapperInteger(this, PROP_CAMERA_ORDER, 0);

//    ClearCache cacheListener = new ClearCache();
//    CyVertexBuffer boundsMesh;

    protected RavenNodeCamera(int uid)
    {
        super(uid);

//        width.addPropertyWrapperListener(cacheListener);
//        height.addPropertyWrapperListener(cacheListener);
    }

    @Override
    protected void renderContent(RavenRenderer renderer)
    {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

//    @Override
//    public Rectangle2D.Float getPickShapeLocal()
//    {
//        return new Rectangle2D.Float(0, 0, width.getValue(), height.getValue());
//    }


//    @Override
//    protected void clearCache()
//    {
//        super.clearCache();
//        
//        if (boundsMesh != null)
//        {
////            boundsMesh.dispose();
//            boundsMesh = null;
//        }
//    }

//    @Override
//    protected void renderContent(RavenRenderer renderer)
//    {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

    @Override
    protected void renderContent(RenderContext ctx)
    {
        CyDrawStack stack = ctx.getDrawStack();

        if (!ctx.isEditor())
        {
            //Nothing to draw for regular mode
            return;
        }
        
        //CyMatrix4d xform = rend.getModelViewProjTileXform();
        FrameKey frame = ctx.getFrame();
        CyColor4f col = CyColor4f.ORANGE;
        float cOpacity = getOpacity(frame);
        
        float cWidth = width.getValue(frame);
        float cHeight = height.getValue(frame);
        
        CyMatrix4d mvp = stack.getModelViewProjXform();
        mvp.scale(cWidth, cHeight, 1);
        mvp.translate(-.5, -.5, 0);
        
        CyMaterialColorDrawRecord rec = 
                CyMaterialColorDrawRecordFactory.inst().allocRecord();
        
        rec.setColor(col);
        rec.setOpacity(cOpacity);
        rec.setMesh(CyVertexBufferDataSquareLines.inst().getBuffer());
        
        rec.setMvpMatrix(mvp);

        stack.addDrawRecord(rec);
        
        
//        ColorStyle col = ColorStyle.ORANGE;
//
//        if (boundsMesh == null)
//        {
//
//            ShapeLinesProvider meshProv = new ShapeLinesProvider(getLocalCameraRect());
//            boundsMesh = new CyVertexBuffer(meshProv);
//        }
//
//        CyRendererUtil2D.fillShape(stack,
//                new CyColor4f(col.r, col.g, col.b, col.a), boundsMesh);
    }

    @Override
    public void renderComposition(RenderContext context)
    {
        CyDrawStack rend = context.getDrawStack();
        
        RavenNodeRoot root = (RavenNodeRoot)getSymbol();
        
        RavenPaintColor col = root.getBackgroundColor();
        CyRendererUtil2D.clear(rend, col.r, col.g, col.b, col.a);

        int devW = rend.getDeviceWidth();
        int devH = rend.getDeviceHeight();

        rend.addDrawRecord(new CyDrawRecordViewport(
                0, 0, devW, devH));
        
        renderView(context);
    }

    public void renderView(RenderContext context)
    {
        CyDrawStack rend = context.getDrawStack();
        RavenNodeRoot root = (RavenNodeRoot)getSymbol();
        
        //Calc projection matrix
        double cw = getWidth();
        double ch = getHeight();
        CyMatrix4d mat = new CyMatrix4d();
        mat.gluOrtho2D(-cw / 2, cw / 2, ch / 2, -ch / 2);
        rend.setProjXform(mat);

        //Calc view matrix
        getLocalToWorldTransform(mat);
        mat.invert();
        rend.setViewXform(mat);

        //Render scene graph
        RavenNodeSceneGraph sg = root.getSceneGraph();
        sg.render(context);
        
//        ArrayList<RavenNodeXformable> children =
//                root.getSceneGraphChildren();
//        for (int j = 0; j < children.size(); ++j)
//        {
//            children.get(j).render(context);
//        }
        
    }
    

    protected CyRectangle2d getLocalCameraRect()
    {
        double w = width.getValue();
        double h = height.getValue();
        return new CyRectangle2d(-w / 2, -h / 2, w, h);
    }

    @Override
    public CyShape getShapePickLocal()
    {
        double w = width.getValue();
        double h = height.getValue();
        
        double w0 = w * 1.01;
        double h0 = h * 1.01;
        double w1 = w * .99;
        double h1 = h * .99;
        
        //return new CyRectangle2d(-w / 2, -h / 2, w, h);
        CyRectangle2d r0 = new CyRectangle2d(-w0 / 2, -h0 / 2, w0, h0);
        CyRectangle2d r1 = new CyRectangle2d(-w1 / 2, -h1 / 2, w1, h1);
        
        CyPath2d path = new CyPath2d();
        //Exterior
        path.moveTo(r0.getMinX(), r0.getMinY());
        path.lineTo(r0.getMaxX(), r0.getMinY());
        path.lineTo(r0.getMaxX(), r0.getMaxY());
        path.lineTo(r0.getMinX(), r0.getMaxY());
        path.close();

        //Interior
        path.moveTo(r1.getMinX(), r1.getMinY());
        path.lineTo(r1.getMinX(), r1.getMaxY());
        path.lineTo(r1.getMaxX(), r1.getMaxY());
        path.lineTo(r1.getMaxX(), r1.getMinY());
        path.close();
        
        return path;
    }

//    @Override
//    public Shape getPickShapeLocal()
//    {
//        CyRectangle2d rect = getLocalCameraRect();
//        return new Rectangle2D.Double(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
//    }

    public double getWidth()
    {
        return width.getValue();
    }

    public double getHeight()
    {
        return height.getValue();
    }

//    public int getCameraOrder()
//    {
//        return cameraOrder.getValue();
//    }

    
    
    //-----------------------------------------------
    
    @ServiceInst(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<RavenNodeCamera>
    {
        public Provider()
        {
            super(RavenNodeCamera.class, "Camera", "/icons/node/camera.png");
        }

        @Override
        public RavenNodeCamera createNode(int uid)
        {
            return new RavenNodeCamera(uid);
        }
    }


//    class ClearCache implements PropertyWrapperListener
//    {
//
//        @Override
//        public void propertyWrapperDataChanged(PropertyChangeEvent evt)
//        {
//            clearCache();
//        }
//
//        @Override
//        public void propertyWrapperTrackChanged(PropertyTrackChangeEvent evt)
//        {
//        }
//
//        @Override
//        public void propertyWrapperTrackKeyChanged(PropertyTrackKeyChangeEvent evt)
//        {
//        }
//    }
}
