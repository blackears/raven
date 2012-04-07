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

package com.kitfox.raven.editor.node.tools.common.shape.brush;

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyGLOffscreenContext;
import com.kitfox.coyote.renderer.CyGLWrapper;
import com.kitfox.coyote.renderer.CyTextureImage;
import com.kitfox.coyote.renderer.CyTransparency;
import com.kitfox.raven.editor.node.scene.RenderContext;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.editor.node.tools.common.ServiceDevice;
import com.kitfox.raven.editor.node.tools.common.ServicePen;
import com.kitfox.raven.editor.node.tools.common.ToolDisplay;
import java.awt.event.MouseEvent;
import jpen.PButtonEvent;
import jpen.PKindEvent;
import jpen.PLevel;
import jpen.PLevelEvent;
import jpen.PScrollEvent;
import jpen.Pen;
import jpen.PenManager;
import jpen.event.PenListener;

/**
 *
 * @author kitfox
 */
public class ToolBrush extends ToolDisplay
        implements PenListener
{
    final ToolBrushProvider toolProvider;
    
    final PenManager penManager;

//    DragSource dragState = DragSource.NONE;
    boolean dragging;
    boolean penDown;
    
    private float penX;
    private float penY;
    private float penPressure;
    private float penTiltX;
    private float penTiltY;

    private float penNextX;
    private float penNextY;
    private float penNextPressure;
    private float penNextTiltX;
    private float penNextTiltY;
    
    StrokeBuilder strokeBuilder;
    
    protected ToolBrush(ToolUser user, ToolBrushProvider toolProvider)
    {
        super(user);
        this.toolProvider = toolProvider;

        ServicePen provider = user.getToolService(ServicePen.class);
        PenManager mgr = null;
        if (provider != null)
        {
            mgr = provider.getPenManager();
            mgr.pen.addListener(this);
        }
        this.penManager = mgr;
    }

    private void createStrokeBuilder()
    {
        RoundBrushSource brush = 
                new RoundBrushSource(
                toolProvider.getStrokeWidthMax(), 
                toolProvider.getHardness(), 
                toolProvider.isAntialias());
        
        int size = brush.getSize();
        CyTextureImage source = new CyTextureImage(
                CyGLWrapper.TexTarget.GL_TEXTURE_2D, 
                CyGLWrapper.InternalFormatTex.GL_RGBA,
                CyGLWrapper.DataType.GL_UNSIGNED_BYTE,
                size, size, CyTransparency.TRANSLUCENT, brush);
        
        strokeBuilder = new StrokeBuilder(source);
    }

    private void samplePen(MouseEvent evt)
    {
        if (evt != null)
        {
            penNextX = evt.getX();
            penNextY = evt.getY();
            penNextPressure = 1;
            penNextTiltX = 0;
            penNextTiltY = 0;
            return;
        }

        Pen pen = penManager.pen;
        penNextX = pen.getLevelValue(PLevel.Type.X);
        penNextY = pen.getLevelValue(PLevel.Type.Y);
        penNextPressure = pen.getLevelValue(PLevel.Type.PRESSURE);
        penNextTiltX = pen.getLevelValue(PLevel.Type.TILT_X);
        penNextTiltY = pen.getLevelValue(PLevel.Type.TILT_Y);
        
//System.err.println("Pen pressure " + penNextPressure);
    }

    private void strokeToNextSample()
    {
        //Only record if minimum distance traveled
        if (Math2DUtil.square(penX - penNextX) +
                + Math2DUtil.square(penY - penNextY) < 4)
        {
            return;
        }

        StrokeBuilder curStrokeBuilder = strokeBuilder;
        if (curStrokeBuilder == null)
        {
            //Pen thread may call this after AWT thread has already
            // released strokeBuilder
            return;
        }
        
        ServiceDevice dev = user.getToolService(ServiceDevice.class);
        CyGLOffscreenContext ctxOff = dev.createOffscreenGLContext();
        
        final float spacing = toolProvider.getStrokeSpacing();
        final float penWidthMin = toolProvider.getHardness();
        final float penWidthMax = toolProvider.getStrokeWidthMax();

        float gap = Math.max(spacing * penPressure * penWidthMax, 1);
        double dist = Math2DUtil.dist(penX, penY, penNextX, penNextY);
        int numDots = (int)Math.ceil(dist / gap);

        CyVector2d penOld = new CyVector2d(penX, penY);
        CyVector2d penNew = new CyVector2d(penNextX, penNextY);
        CyMatrix4d d2w = getDeviceToWorld(null);
        d2w.transformPoint(penOld, false);
        d2w.transformPoint(penNew, false);
        
        
//if (penPressure == 1)
//{
//    int j = 9;
//}
//System.err.println("-penPres " + penPressure + ", " + penNextPressure);
        for (int i = 0; i < numDots; ++i)
        {
            double dt = (double)i / numDots;
            double dPressure = Math2DUtil.lerp(penPressure, penNextPressure, dt);
//System.err.println("dPressure " + dPressure + ", " + i + " " + numDots);
            
            curStrokeBuilder.daubBrush(ctxOff,
                    Math2DUtil.lerp(penOld.x, penNew.x, dt),
                    Math2DUtil.lerp(penOld.y, penNew.y, dt),
                    Math2DUtil.lerp(penWidthMin, penWidthMax, dPressure) / penWidthMax);
        }

        //Make sure to release context
        ctxOff.dispose();
        
        penX = penNextX;
        penY = penNextY;
        penPressure = penNextPressure;
        penTiltX = penNextTiltX;
        penTiltY = penNextTiltY;
    }

    @Override
    protected void click(MouseEvent evt)
    {
        //This entire method is just temporary code to test out 
        // a few ideas
        /*
        ServiceDevice dev = user.getToolService(ServiceDevice.class);

        CyGLOffscreenContext ctx = dev.createOffscreenGLContext();
        
        CyGLWrapper gl = ctx.getGL();
        String vendor = gl.glGetString(CyGLWrapper.StringName.GL_VENDOR);
        System.err.println("GL Vendor " + vendor);

        int width = 100;
        int height = 100;
        
        CyFramebufferTexture texBuf = new CyFramebufferTexture(
                CyGLWrapper.Attachment.GL_COLOR_ATTACHMENT0, 
                CyGLWrapper.TexTarget.GL_TEXTURE_2D, 
                CyGLWrapper.InternalFormatTex.GL_RGBA, 
                CyGLWrapper.DataType.GL_UNSIGNED_BYTE, 
                width, height);

        CyFramebuffer framebuffer = new CyFramebuffer(width, height,
                texBuf);
        
        
        CyGLContext glCtx = ctx.getGLContext();
        framebuffer.bind(glCtx, gl);
        
        gl.glClearColor(0, .5f, 1, 1);
        gl.glClear(true, false, false);
        
        ByteBuffer pixBuf = BufferUtil.allocateByte(
                width * height * 4);
        gl.glReadPixels(0, 0, width, height, 
                CyGLWrapper.ReadPixelsFormat.GL_RGBA, 
                CyGLWrapper.DataType.GL_UNSIGNED_BYTE, 
                pixBuf);
        
        pixBuf.rewind();
        byte[] bytes = new byte[width * height * 4];
        pixBuf.get(bytes);
        
        
        
        ctx.dispose();
        */
    }

    @Override
    protected void startDrag(MouseEvent evt)
    {
//System.err.println("Start drag: " + dragState);
        
//        if (dragState == DragSource.NONE)
        if (!penDown)
        {
            //samplePen(evt);

            penX = penNextX = evt.getX();
            penY = penNextY = evt.getY();
            penPressure = penNextPressure = 1;
            penTiltX = penNextTiltX = 0;
            penTiltY = penNextTiltY = 0;
            
//            strokeBuider = new StrokeBuilder();
//    System.err.println("Created builder - start drag");

//            penDown = true;
//            readingPen = false;
        }
        
        createStrokeBuilder();
//        dragState = DragSource.MOUSE;
        dragging = true;

//        ServiceDevice dev = user.getToolService(ServiceDevice.class);
//        bubbleOutliner = new StrokeBuffer(strokeBufferSize, strokeBufferSize,
//                dev.getComponent().getGraphicsConfiguration());
    }

    @Override
    protected void dragTo(MouseEvent evt)
    {
//System.err.println("Drag to: " + dragState);
//        strokeSegment(evt);
//        fireToolDisplayChanged();
//        if (dragState == DragSource.MOUSE)
        if (!penDown)
        {
            samplePen(evt);
            strokeToNextSample();
        }
    }

    @Override
    protected void endDrag(MouseEvent evt)
    {
//System.err.println("End drag: " + dragState);
        strokeBuilder = null;
//        dragState = DragSource.NONE;
        dragging = false;
        
//        if (bubbleOutliner == null)
//        {
//            //Will be null if canceled or more than one mouse button pushed
//            return;
//        }
//
//        strokeSegment(evt);
//
//        if (!bubbleOutliner.isEmpty())
//        {
//            //Commit
//        }
    }

    @Override
    public void cancel()
    {
//        bubbleOutliner = null;
//        penDown = false;
        strokeBuilder = null;
//        dragState = DragSource.NONE;
    }

    @Override
    public void dispose()
    {
        if (penManager != null)
        {
            penManager.pen.removeListener(this);
        }
    }

    @Override
    public void penKindEvent(PKindEvent pke)
    {
    }
    
//    private void startReadingFromPen()
//    {
////        if (readingPen)
////        {
////            return;
////        }
//System.err.println("Start reading from pen: " + dragState);
//
//        //Restart drawing, using pen now
//        Pen pen = penManager.pen;
//        penNextX = penX = pen.getLevelValue(PLevel.Type.X);
//        penNextY = penY = pen.getLevelValue(PLevel.Type.Y);
//        penNextTiltX = penTiltX = pen.getLevelValue(PLevel.Type.TILT_X);
//        penNextTiltY = penTiltY = pen.getLevelValue(PLevel.Type.TILT_Y);
//        penNextPressure = penPressure = pen.getLevelValue(PLevel.Type.PRESSURE);
//
//        createStrokeBuilder();
////        strokeBuider = new StrokeBuilder();
////System.err.println("Created builder - pen event");
////        ServiceDevice dev = user.getToolService(ServiceDevice.class);
////        bubbleOutliner = new StrokeBuffer(strokeBufferSize, strokeBufferSize,
////                dev.getComponent().getGraphicsConfiguration());
////        readingPen = true;
//        dragState = DragSource.PEN;
//    }

    @Override
    public void penLevelEvent(PLevelEvent ple)
    {
        //Start reading from pen, if dragging
        //Pen dragging overrides mouse dragging
        PLevel[] levels = ple.levels;
        for (int i = 0; i < levels.length; ++i)
        {
            if (levels[i].getType() == PLevel.Type.PRESSURE)
            {
                boolean curPenDown = levels[i].value > 0;
//                if (dragState != DragSource.PEN)
//                {
//                    if (levels[i].value > 0)
//                    {
//                        startReadingFromPen();
//                    }
//                }
//                else
//                {
//                    samplePen(null);
//                    strokeToNextSample();
//                }
                samplePen(null);
                
                if (curPenDown && !penDown)
                {
//System.err.println("+++Start pen stroke ");
                    penX = penNextX;
                    penY = penNextY;
                    penTiltX = penNextTiltX;
                    penTiltY = penNextTiltY;
                    penPressure = penNextPressure;
                    if (strokeBuilder != null)
                    {
                        strokeBuilder.clear();
                    }
                }
                penDown = curPenDown;
                
                if (dragging && penDown)
                {
                    strokeToNextSample();
                }
                
                break;
            }
        }

//        if (dragState != DragState.NONE)
//        {
//            //If pen is down, do extra sampling to overcome sluggish API calls
//            samplePen(null);
//        }
    }

    @Override
    public void penButtonEvent(PButtonEvent pbe)
    {
    }

    @Override
    public void penScrollEvent(PScrollEvent pse)
    {
    }

    @Override
    public void penTock(long l)
    {
    }

    @Override
    public void render(RenderContext ctx)
    {
        if (strokeBuilder != null)
        {
            CyDrawStack stack = ctx.getDrawStack();
            strokeBuilder.render(stack);
        }
        
    }
    
    //----------------------------
//    enum DragSource
//    {
//        NONE, MOUSE, PEN
//    }
    
}
