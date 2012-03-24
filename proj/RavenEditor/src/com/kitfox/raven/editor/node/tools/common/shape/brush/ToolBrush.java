/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.raven.editor.node.tools.common.shape.brush;

import com.kitfox.coyote.math.BufferUtil;
import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.renderer.*;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.editor.node.tools.common.ServiceDevice;
import com.kitfox.raven.editor.node.tools.common.ServicePen;
import com.kitfox.raven.editor.node.tools.common.ToolDisplay;
import java.awt.event.MouseEvent;
import java.nio.ByteBuffer;
import jpen.*;
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

    private float penX;
    private float penY;
    private float penPressure;

    private float penNextX;
    private float penNextY;
    private float penNextPressure;

    boolean penDown;
    boolean readingPen = false;
    
//    final int strokeBufferSize = 128;
    
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

    private void startReadingFromPen()
    {
        if (readingPen)
        {
            return;
        }

        //Restart drawing, using pen now
        Pen pen = penManager.pen;
        penNextX = penX = pen.getLevelValue(PLevel.Type.X);
        penNextY = penY = pen.getLevelValue(PLevel.Type.Y);
        penNextPressure = penPressure = pen.getLevelValue(PLevel.Type.PRESSURE);

        ServiceDevice dev = user.getToolService(ServiceDevice.class);
//        bubbleOutliner = new StrokeBuffer(strokeBufferSize, strokeBufferSize,
//                dev.getComponent().getGraphicsConfiguration());
        readingPen = true;
    }

    private void samplePen(MouseEvent evt)
    {
        if (!readingPen && evt != null)
        {
            penNextX = evt.getX();
            penNextY = evt.getY();
            penNextPressure = 1;
            return;
        }

        Pen pen = penManager.pen;
        penNextX = pen.getLevelValue(PLevel.Type.X);
        penNextY = pen.getLevelValue(PLevel.Type.Y);
        penNextPressure = pen.getLevelValue(PLevel.Type.PRESSURE);
    }

    private void strokeSegment(MouseEvent evt)
    {
        if (!penDown)
        {
            return;
        }

        samplePen(evt);

        //Only record if minimum distance traveled
        if (Math2DUtil.square(penX - penNextX) +
                + Math2DUtil.square(penY - penNextY) < 4)
        {
            return;
        }

        final float spacing = toolProvider.getStrokeSpacing();
        final float penWeightMin = toolProvider.getStrokeWidthMin();
        final float penWeightMax = toolProvider.getStrokeWidthMax();

        float gap = Math.max(spacing * penPressure * penWeightMax, 1);
        double dist = Math2DUtil.dist(penX, penY, penNextX, penNextY);
        int numDots = (int)Math.ceil(dist / gap);

        for (int i = 0; i < numDots; ++i)
        {
            double dt = (double)i / numDots;
            double dPressure = Math2DUtil.lerp(penPressure, penNextPressure, dt);
//            bubbleOutliner.addCircle(
//                    Math2DUtil.lerp(penX, penNextX, dt),
//                    Math2DUtil.lerp(penY, penNextY, dt),
//                    Math2DUtil.lerp(penWeightMin, penWeightMax, dPressure));

        }

        penX = penNextX;
        penY = penNextY;
        penPressure = penNextPressure;
    }

    @Override
    protected void click(MouseEvent evt)
    {
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
    }

    @Override
    protected void startDrag(MouseEvent evt)
    {
        samplePen(evt);

        penX = penNextX;
        penY = penNextY;
        penPressure = penNextPressure;

        ServiceDevice dev = user.getToolService(ServiceDevice.class);
//        bubbleOutliner = new StrokeBuffer(strokeBufferSize, strokeBufferSize,
//                dev.getComponent().getGraphicsConfiguration());
        penDown = true;
        readingPen = false;
    }

    @Override
    protected void dragTo(MouseEvent evt)
    {
        strokeSegment(evt);
//        fireToolDisplayChanged();
    }

    @Override
    protected void endDrag(MouseEvent evt)
    {
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
        penDown = false;
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

    @Override
    public void penLevelEvent(PLevelEvent ple)
    {
        PLevel[] levels = ple.levels;
        for (int i = 0; i < levels.length; ++i)
        {
            if (penDown && levels[i].getType() == PLevel.Type.PRESSURE)
            {
                startReadingFromPen();
            }
        }

        if (penDown)
        {
            //If pen is down, do extra sampling to overcome sluggish API calls
            samplePen(null);
        }
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
    
}
