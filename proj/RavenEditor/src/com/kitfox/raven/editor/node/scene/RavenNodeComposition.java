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
import com.kitfox.raven.editor.node.RavenNode;
import com.kitfox.raven.paint.common.RavenPaintColor;
import com.kitfox.raven.util.Units;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.ChildWrapperList;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperInteger;

/**
 *
 * @author kitfox
 */
public class RavenNodeComposition extends RavenNode
        implements RenderDevice
{
    public static final String PROP_BACKGROUND = "background";
    public final PropertyWrapper<RavenNodeComposition, RavenPaintColor> background =
            new PropertyWrapper(
            this, PROP_BACKGROUND, RavenPaintColor.class,
            new RavenPaintColor(CyColor4f.WHITE));

    public static final String PROP_WIDTH = "width";
    public final PropertyWrapperInteger<RavenNodeComposition> width =
            new PropertyWrapperInteger(this, PROP_WIDTH, 
            PropertyWrapper.FLAGS_NOANIM,
            640);

    public static final String PROP_HEIGHT = "height";
    public final PropertyWrapperInteger<RavenNodeComposition> height =
            new PropertyWrapperInteger(this, PROP_HEIGHT, 
            PropertyWrapper.FLAGS_NOANIM,
            480);

    public static final String PROP_UNITS = "units";
    public final PropertyWrapper<RavenNodeComposition, Units> units =
            new PropertyWrapper(this, PROP_UNITS,
            PropertyWrapper.FLAGS_NOANIM,
             Units.class, Units.PIXELS);

    public static final String CHILD_LAYERS = "layers";
    public final ChildWrapperList<RavenNodeComposition, RavenNodeCameraProxy> 
            layers =
            new ChildWrapperList(
            this, CHILD_LAYERS, RavenNodeCameraProxy.class);

    protected RavenNodeComposition(int uid)
    {
        super(uid);
    }

    @Override
    public void renderComposition(RenderContext context)
    {
        CyDrawStack rend = context.getDrawStack();
        
        RavenSymbolRoot root = ((RavenSymbol)getSymbol()).getRoot();
        
        RavenPaintColor col = root.getBackgroundColor();
        CyRendererUtil2D.clear(rend, col.r, col.g, col.b, col.a);

        for (int i = 0; i < layers.size(); ++i)
        {
            RavenNodeCameraProxy cameraProxy = layers.get(i);
            
            cameraProxy.renderCamera(context);
        }
    }

    public int getWidth()
    {
        return width.getValue();
    }
    
    public int getHeight()
    {
        return height.getValue();
    }
    
//    @Override
//    public void renderCamerasAll(RenderContext ctx)
//    {
//        CyDrawStack rend = ctx.getDrawStack();
//        FrameKey frame = ctx.getFrame();
//
//        //Find cameras to draw
//        ArrayList<RavenNodeCamera> cams = getNodes(RavenNodeCamera.class);
//
//        class Comp implements Comparator<RavenNodeCamera>
//        {
//            @Override
//            public int compare(RavenNodeCamera o1, RavenNodeCamera o2)
//            {
//                return o1.getCameraOrder() - o2.getCameraOrder();
//            }
//        }
//
//        Collections.sort(cams, new Comp());
//
//        RavenPaintColor col = background.getValue();
//        CyRendererUtil2D.clear(rend, col.r, col.g, col.b, col.a);
//
//        int devW = rend.getDeviceWidth();
//        int devH = rend.getDeviceHeight();
//
//        //Draw cameras
//        CyMatrix4d mat = new CyMatrix4d();
////        AffineTransform l2w = new AffineTransform();
//        for (int i = 0; i < cams.size(); ++i)
//        {
//            RavenNodeCamera camera = cams.get(i);
//            if (!camera.isVisible(frame))
//            {
//                continue;
//            }
//
//            float opacity = camera.getOpacity(frame);
//            rend.setOpacity(opacity);
//            if (opacity == 0)
//            {
//                continue;
//            }
//
//            double vx = camera.getViewportX();
//            double vy = camera.getViewportY();
//            double vw = camera.getViewportWidth();
//            double vh = camera.getViewportHeight();
//
//            rend.addDrawRecord(new CyDrawRecordViewport(
//                    (int)(vx * devW), (int)(vy * devH),
//                    (int)(vw * devW), (int)(vh * devH)
//                    ));
//
//            double cw = camera.getWidth();
//            double ch = camera.getHeight();
//            mat.gluOrtho2D(-cw / 2, cw / 2, ch / 2, -ch / 2);
//            rend.setProjXform(mat);
//
//            camera.getLocalToWorldTransform(mat);
//            mat.invert();
//            rend.setViewXform(mat);
//
//            //Render scene graph
//            for (int j = 0; j < sceneGraph.size(); ++j)
//            {
//                sceneGraph.get(j).render(ctx);
//            }
//        }
//    }
//
//    @Override
//    public int getNumCameras()
//    {
//        ArrayList<RavenNodeCamera> cams = getNodes(RavenNodeCamera.class);
//        return cams.size();
//    }
    
    //-----------------------------------------------
    
    @ServiceInst(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<RavenNodeComposition>
    {
        public Provider()
        {
            super(RavenNodeComposition.class, "Composition", "/icons/node/composition.png");
        }

        @Override
        public RavenNodeComposition createNode(int uid)
        {
            return new RavenNodeComposition(uid);
        }
    }
    
}
