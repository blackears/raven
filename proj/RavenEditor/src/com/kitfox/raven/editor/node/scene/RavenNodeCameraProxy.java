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
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.raven.editor.node.RavenNode;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.FrameKey;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperBoolean;
import com.kitfox.raven.util.tree.PropertyWrapperDouble;
import com.kitfox.raven.util.tree.PropertyWrapperFloat;

/**
 *
 * @author kitfox
 */
public class RavenNodeCameraProxy extends RavenNode
{
    public static final String PROP_CAMERA = "camera";
    public final PropertyWrapper<RavenNodeCameraProxy, RavenNodeCamera> camera =
            new PropertyWrapper(this, PROP_CAMERA, RavenNodeCamera.class);

    public static final String PROP_VISIBLE = "visible";
    public final PropertyWrapperBoolean<RavenNodeCameraProxy> visible =
            new PropertyWrapperBoolean(this, PROP_VISIBLE, true);

    public static final String PROP_OPACITY = "opacity";
    public final PropertyWrapperFloat<RavenNodeCameraProxy> opacity =
            new PropertyWrapperFloat(this, PROP_OPACITY, 1f);

    public static final String PROP_VIEWPORT_X = "viewportX";
    public final PropertyWrapperDouble<RavenNodeCameraProxy> viewportX =
            new PropertyWrapperDouble(this, PROP_VIEWPORT_X, 0.0);

    public static final String PROP_VIEWPORT_Y = "viewportY";
    public final PropertyWrapperDouble<RavenNodeCameraProxy> viewportY =
            new PropertyWrapperDouble(this, PROP_VIEWPORT_Y, 0.0);

    public static final String PROP_VIEWPORT_WIDTH = "viewportWidth";
    public final PropertyWrapperDouble<RavenNodeCameraProxy> viewportWidth =
            new PropertyWrapperDouble(this, PROP_VIEWPORT_WIDTH, 1.0);

    public static final String PROP_VIEWPORT_HEIGHT = "viewportHeight";
    public final PropertyWrapperDouble<RavenNodeCameraProxy> viewportHeight =
            new PropertyWrapperDouble(this, PROP_VIEWPORT_HEIGHT, 1.0);

    protected RavenNodeCameraProxy(int uid)
    {
        super(uid);
    }

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

    public void renderCamera(RenderContext context)
    {
        CyDrawStack rend = context.getDrawStack();
        
        FrameKey frame = context.getFrame();

        if (!isVisible(frame))
        {
            return;
        }

        RavenNodeCamera curCam = camera.getValue(frame);
        if (curCam == null)
        {
            return;
        }
        
        float curOpacity = getOpacity(frame);
        rend.setOpacity(curOpacity);
        if (curOpacity == 0)
        {
            return;
        }

        int devW = rend.getDeviceWidth();
        int devH = rend.getDeviceHeight();

        double vx = viewportX.getValue(frame);
        double vy = viewportY.getValue(frame);
        double vw = viewportWidth.getValue(frame);
        double vh = viewportHeight.getValue(frame);

        rend.addDrawRecord(new CyDrawRecordViewport(
                (int)(vx * devW), (int)(vy * devH),
                (int)(vw * devW), (int)(vh * devH)
                ));
        
//        RavenNodeRoot root = (RavenNodeRoot)getDocument();

        curCam.renderView(context);
    }
    
    public boolean isVisible(FrameKey frame)
    {
        return visible.getValue(frame);
    }
    
    public float getOpacity(FrameKey frame)
    {
        return opacity.getValue(frame);
    }
    
    //-----------------------------------------------
    
    @ServiceInst(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<RavenNodeCameraProxy>
    {
        public Provider()
        {
            super(RavenNodeCameraProxy.class, "Camera Proxy", "/icons/node/cameraProxy.png");
        }

        @Override
        public RavenNodeCameraProxy createNode(int uid)
        {
            return new RavenNodeCameraProxy(uid);
        }
    }
    
}
