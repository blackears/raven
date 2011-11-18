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
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyRendererUtil2D;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.coyote.shape.CyShape;
import com.kitfox.coyote.shape.ShapeLinesProvider;
import com.kitfox.game.control.color.ColorStyle;
import com.kitfox.raven.editor.node.renderer.RavenRenderer;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.PropertyTrackChangeEvent;
import com.kitfox.raven.util.tree.PropertyTrackKeyChangeEvent;
import com.kitfox.raven.util.tree.PropertyWrapperDouble;
import com.kitfox.raven.util.tree.PropertyWrapperFloat;
import com.kitfox.raven.util.tree.PropertyWrapperInteger;
import com.kitfox.raven.util.tree.PropertyWrapperListener;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author kitfox
 */
public class RavenNodeCamera extends RavenNodeXformable
{
    public static final String PROP_WIDTH = "width";
    public final PropertyWrapperFloat<RavenNodeCamera> width =
            new PropertyWrapperFloat(this, PROP_WIDTH);

    public static final String PROP_HEIGHT = "height";
    public final PropertyWrapperFloat<RavenNodeCamera> height =
            new PropertyWrapperFloat(this, PROP_HEIGHT);

    public static final String PROP_VIEWPORT_X = "viewportX";
    public final PropertyWrapperDouble<RavenNodeCamera> viewportX =
            new PropertyWrapperDouble(this, PROP_VIEWPORT_X, 0.0);

    public static final String PROP_VIEWPORT_Y = "viewportY";
    public final PropertyWrapperDouble<RavenNodeCamera> viewportY =
            new PropertyWrapperDouble(this, PROP_VIEWPORT_Y, 0.0);

    public static final String PROP_VIEWPORT_WIDTH = "viewportWidth";
    public final PropertyWrapperDouble<RavenNodeCamera> viewportWidth =
            new PropertyWrapperDouble(this, PROP_VIEWPORT_WIDTH, 1.0);

    public static final String PROP_VIEWPORT_HEIGHT = "viewportHeight";
    public final PropertyWrapperDouble<RavenNodeCamera> viewportHeight =
            new PropertyWrapperDouble(this, PROP_VIEWPORT_HEIGHT, 1.0);

    public static final String PROP_CAMERA_ORDER = "cameraOrder";
    public final PropertyWrapperInteger<RavenNodeCamera> cameraOrder =
            new PropertyWrapperInteger(this, PROP_CAMERA_ORDER, 0);

    ClearCache cacheListener = new ClearCache();
    CyVertexBuffer boundsMesh;

    protected RavenNodeCamera(int uid)
    {
        super(uid);

        width.addPropertyWrapperListener(cacheListener);
        height.addPropertyWrapperListener(cacheListener);
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


    @Override
    protected void clearCache()
    {
        super.clearCache();
        
        if (boundsMesh != null)
        {
//            boundsMesh.dispose();
            boundsMesh = null;
        }
    }

//    @Override
//    protected void renderContent(RavenRenderer renderer)
//    {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

    @Override
    protected void renderContent(RenderContext ctx)
    {
        CyDrawStack renderer = ctx.getDrawStack();

        //CyMatrix4d xform = rend.getModelViewProjTileXform();
        ColorStyle col = ColorStyle.ORANGE;

        if (boundsMesh == null)
        {

            ShapeLinesProvider meshProv = new ShapeLinesProvider(getLocalCameraRect());
            boundsMesh = new CyVertexBuffer(meshProv);
        }

        CyRendererUtil2D.fillShape(renderer,
                new CyColor4f(col.r, col.g, col.b, col.a), boundsMesh);
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
        return getLocalCameraRect();
    }

//    @Override
//    public Shape getPickShapeLocal()
//    {
//        CyRectangle2d rect = getLocalCameraRect();
//        return new Rectangle2D.Double(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
//    }

    public double getViewportX()
    {
        return viewportX.getValue();
    }

    public double getViewportY()
    {
        return viewportY.getValue();
    }

    public double getViewportWidth()
    {
        return viewportWidth.getValue();
    }

    public double getViewportHeight()
    {
        return viewportHeight.getValue();
    }

    public double getWidth()
    {
        return width.getValue();
    }

    public double getHeight()
    {
        return height.getValue();
    }

    public int getCameraOrder()
    {
        return cameraOrder.getValue();
    }

    
    
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


    class ClearCache implements PropertyWrapperListener
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
    }}
