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

import com.kitfox.coyote.material.color.CyMaterialColorDrawRecord;
import com.kitfox.coyote.material.color.CyMaterialColorDrawRecordFactory;
import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyGLOffscreenContext;
import com.kitfox.coyote.renderer.CyGLWrapper;
import com.kitfox.coyote.renderer.CyTextureImage;
import com.kitfox.coyote.renderer.CyTransparency;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.PiecewiseBezierBuilder;
import com.kitfox.coyote.shape.ShapeLinesProvider;
import com.kitfox.coyote.shape.bezier.BezierCubic2d;
import com.kitfox.raven.editor.node.scene.RenderContext;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.editor.node.tools.common.ServiceDevice;
import com.kitfox.raven.editor.node.tools.common.ServicePen;
import com.kitfox.raven.editor.node.tools.common.ToolDisplay;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
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
    //boolean readingPen;
    boolean penDown;
    boolean plottedPointsFromMouse;
    
    PiecewiseBezierBuilder curveLeft;
    PiecewiseBezierBuilder curveRight;
    CyPath2d strokePath;
    
//    private float penX;
//    private float penY;
//    private float penPressure;
//    private float penTiltX;
//    private float penTiltY;
//
//    private float penNextX;
//    private float penNextY;
//    private float penNextPressure;
//    private float penNextTiltX;
//    private float penNextTiltY;
    
    StrokeBuilder strokeBuilder;

    ArrayList<PenPoint> penBuf = new ArrayList<PenPoint>();
    ArrayList<PenPoint> penBackBuf = new ArrayList<PenPoint>();

    ArrayList<PenPoint> plotBackBuf = new ArrayList<PenPoint>();
    ArrayList<PenPoint> plotHistory = new ArrayList<PenPoint>();
    PenPoint plotPrev;
//    double distAccum;
    
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

//    private void samplePen(MouseEvent evt)
//    {
//        if (evt != null)
//        {
//            penNextX = evt.getX();
//            penNextY = evt.getY();
//            penNextPressure = 1;
//            penNextTiltX = 0;
//            penNextTiltY = 0;
//            return;
//        }
//
//        Pen pen = penManager.pen;
//        penNextX = pen.getLevelValue(PLevel.Type.X);
//        penNextY = pen.getLevelValue(PLevel.Type.Y);
//        penNextPressure = pen.getLevelValue(PLevel.Type.PRESSURE);
//        penNextTiltX = pen.getLevelValue(PLevel.Type.TILT_X);
//        penNextTiltY = pen.getLevelValue(PLevel.Type.TILT_Y);
//        
////System.err.println("Pen pressure " + penNextPressure);
//    }
    
//    private void buildStroke()
//    {
//        float smoothing = toolProvider.getStrokeSmoothing();
//        
//        if (plotHistory.size() <= 1)
//        {
//            return;
//        }
//        
//        
//        for (int i = 0; i < plotHistory.size(); ++i)
//        {
//            
//        }
//    }
    
    private void plotStroke()
    {
        for (int i = 0; i < plotBackBuf.size(); ++i)
        {
            PenPoint pt = plotBackBuf.get(i);
            strokeTo(pt);
        }
        plotBackBuf.clear();
        plotHistory.clear();
    }
    
    private CyPath2d calcStroke()
    {
        //Examine end points to calc caps
        BezierCubic2d segL0 = curveLeft.getFirstSegment();
        BezierCubic2d segL1 = curveLeft.getLastSegment();
        BezierCubic2d segR0 = curveRight.getFirstSegment();
        BezierCubic2d segR1 = curveRight.getLastSegment();
        
        if (segL0 == null || segR0 == null)
        {
            return null;
        }
        
        CyVector2d pL1 = new CyVector2d(segL1.getEndX(), segL1.getEndY());
        CyVector2d tL1 = new CyVector2d(segL1.getTanOutX(), segL1.getTanOutY());
        CyVector2d pR0 = new CyVector2d(segR1.getEndX(), segR1.getEndY());
        CyVector2d tR0 = new CyVector2d(segR1.getTanOutX(), segR1.getTanOutY());

        CyVector2d pR1 = new CyVector2d(segR0.getStartX(), segR0.getStartY());
        CyVector2d tR1 = new CyVector2d(-segR0.getTanInX(), -segR0.getTanInY());
        CyVector2d pL0 = new CyVector2d(segL0.getStartX(), segL0.getStartY());
        CyVector2d tL0 = new CyVector2d(-segL0.getTanInX(), -segL0.getTanInY());

        double diam0 = pL1.distance(pR0);
        double diam1 = pR1.distance(pL0);

        tL1.normalize();
        tL1.scale(diam0 * 3 / 4);
        tR0.normalize();
        tR0.scale(diam0 * 3 / 4);
        
        tL0.normalize();
        tL0.scale(diam1 * 3 / 4);
        tR1.normalize();
        tR1.scale(diam1 * 3 / 4);
        
        CyPath2d pathStroke = new CyPath2d();

        pathStroke.moveTo(pL0.x, pL0.y);
        
        curveLeft.appendSegs(pathStroke, false);
        
        pathStroke.cubicTo(pL1.x + tL1.x, pL1.y + tL1.y, 
                pR0.x + tR0.x, pR0.y + tR0.y,
                pR0.x, pR0.y);

        curveRight.appendSegs(pathStroke, true);
        
        pathStroke.cubicTo(pR1.x + tR1.x, pR1.y + tR1.y, 
                pL0.x + tL0.x, pL0.y + tL0.y,
                pL0.x, pL0.y);

        pathStroke.close();
        
        return pathStroke;
    }
    
    private void strokeTo(PenPoint pt)
    {
        if (plotPrev == null)
        {
            plotPrev = pt;
            return;
        }
        
        //Only record if minimum distance traveled
//        if (Math2DUtil.square(penX - penNextX) +
//                + Math2DUtil.square(penY - penNextY) < 4)
//        {
//            return;
//        }

        StrokeBuilder curStrokeBuilder = strokeBuilder;
        if (curStrokeBuilder == null)
        {
            //Pen thread may call this after AWT thread has already
            // released strokeBuilder
            return;
        }
        
        final float spacing = toolProvider.getStrokeSpacing();
        final float penWidthMin = toolProvider.getHardness();
        final float penWidthMax = toolProvider.getStrokeWidthMax();

//        double dist = Math2DUtil.dist(penX, penY, penNextX, penNextY);

        
        CyVector2d penOld = new CyVector2d(plotPrev.x, plotPrev.y);
        CyVector2d penNew = new CyVector2d(pt.x, pt.y);
//System.err.println("Old " + penOld);
//System.err.println("new " + penNew);

        CyMatrix4d d2w = getDeviceToWorld(null);
        d2w.transformPoint(penOld, false);
        d2w.transformPoint(penNew, false);

        double dist = penOld.distance(penNew);
        if (dist < spacing)
        {
//            distAccum += dist;
//            if (distAccum < spacing)
//            {
//                //Draw noting if we've only traveled a fraction of required
//                // distance
//                return;
//            }
            //Skip points that are not far enough away from prev point
            return;
        }
//        double gap = Math.max(spacing * penPressure * penWidthMax, 1);
//        int numDots = (int)Math.ceil(dist / gap);
        int numDots = (int)Math.ceil(dist / spacing);
        plotHistory.add(pt);

        //Collect points in stroke builder
        float gap = penWidthMax - penWidthMin;
        CyVector2d norm = new CyVector2d(pt.x - plotPrev.x, 
                pt.y - plotPrev.y);
        norm.normalize();
        norm.rotCCW90();
        norm.scale(plotPrev.pressure * gap + penWidthMin);
        
        CyVector2d ptLeft = new CyVector2d(
                plotPrev.x + norm.x, plotPrev.y + norm.y);
        CyVector2d ptRight = new CyVector2d(
                plotPrev.x - norm.x, plotPrev.y - norm.y);
        curveLeft.addPoint(ptLeft);
        curveRight.addPoint(ptRight);
        
        calcStroke();
        
        //Draw point to raster
        ServiceDevice dev = user.getToolService(ServiceDevice.class);
        CyGLOffscreenContext ctxOff = dev.createOffscreenGLContext();
        
//if (plotPrev.pressure == 1)
//{
//    int j = 9;
//}
//System.err.println("numDots " + numDots);
//if (numDots > 10)
//{
//    int j = 9;
//}
        for (int i = 0; i < numDots; ++i)
        {
            double dt = (double)i / numDots;
            double dPressure = Math2DUtil.lerp(
                    plotPrev.pressure, pt.pressure, dt);
//System.err.println("dPressure " + dPressure + ", " + i + " " + numDots);
            
//System.err.println("  i " + i);
            curStrokeBuilder.daubBrush(ctxOff,
                    Math2DUtil.lerp(penOld.x, penNew.x, dt),
                    Math2DUtil.lerp(penOld.y, penNew.y, dt),
                    Math2DUtil.lerp(penWidthMin, penWidthMax, dPressure) / penWidthMax);
        }

        //Make sure to release context
        ctxOff.dispose();
        
        plotPrev = pt;
//        penX = penNextX;
//        penY = penNextY;
//        penPressure = penNextPressure;
//        penTiltX = penNextTiltX;
//        penTiltY = penNextTiltY;
//        distAccum = 0;
    }

    @Override
    protected void click(MouseEvent evt)
    {
    }

    private void swapPenBuffer()
    {
        penBuf.clear();
        ArrayList<PenPoint> tmp = penBackBuf;
        penBackBuf = penBuf;
        penBuf = tmp;
        
        if (!penBuf.isEmpty())
        {
            PenPoint last = penBuf.get(penBuf.size() - 1);
            penDown = last.pressure > 0;
        }
    }
    
    @Override
    protected void startDrag(MouseEvent evt)
    {
//System.err.println("--------------Start drag");
        
//        if (!penDown)
//        {
//            //samplePen(evt);
//
//            penX = penNextX = evt.getX();
//            penY = penNextY = evt.getY();
//            penPressure = penNextPressure = 1;
//            penTiltX = penNextTiltX = 0;
//            penTiltY = penNextTiltY = 0;
//        }

        
        plotBackBuf.clear();
        plotHistory.clear();
        plotPrev = null;
        penDown = false;
        swapPenBuffer();
        
        if (penDown)
        {
            if (!penBuf.isEmpty())
            {
                int i = penBuf.size() - 1;
                while (i > 0 && penBuf.get(i - 1).pressure > 0)
                {
                    --i;
                }
                for (; i < penBuf.size(); ++i)
                {
                    plotBackBuf.add(penBuf.get(i));
                }
            }
            plottedPointsFromMouse = false;
        }
        else
        {
            plotBackBuf.add(new PenPoint(evt.getX(), evt.getY()));
            plottedPointsFromMouse = true;
        }
        
        float smoothing = toolProvider.getStrokeSmoothing();
        curveLeft = new PiecewiseBezierBuilder(smoothing);
        curveRight = new PiecewiseBezierBuilder(smoothing);
        
        createStrokeBuilder();
        strokePath = null;
        dragging = true;
    }

    @Override
    protected void dragTo(MouseEvent evt)
    {
        swapPenBuffer();
        
//System.err.println("drag to " + evt.getX() + " " + evt.getY());
        if (penDown)
        {
            if (plottedPointsFromMouse)
            {
                //Clear plot history created by mouse
                strokeBuilder.clear();
                plotBackBuf.clear();
                plotHistory.clear();
                plotPrev = null;
                plottedPointsFromMouse = false;
            }
            
            if (!penBuf.isEmpty())
            {
                int i = penBuf.size() - 1;
                while (i > 0 && penBuf.get(i - 1).pressure > 0)
                {
                    --i;
                }
                for (; i < penBuf.size(); ++i)
                {
                    plotBackBuf.add(penBuf.get(i));
                }
            }
        }
        else
        {
            if (plottedPointsFromMouse)
            {
                plotBackBuf.add(new PenPoint(evt.getX(), evt.getY()));
            }
        }
        
        plotStroke();
        strokePath = calcStroke();
        
//        if (!penDown)
//        {
//            samplePen(evt);
//            strokeToNextSample();
//        }
    }

    @Override
    protected void endDrag(MouseEvent evt)
    {
        plotStroke();
//        buildStroke();

        strokeBuilder = null;
        dragging = false;
    }

    @Override
    public void cancel()
    {
        strokeBuilder = null;
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
        //Start reading from pen, if dragging
        //Pen dragging overrides mouse dragging
        PLevel[] levels = ple.levels;
        for (int i = 0; i < levels.length; ++i)
        {
            if (levels[i].getType() == PLevel.Type.PRESSURE)
            {
//                boolean curPenDown = levels[i].value > 0;
//                samplePen(null);
//                
//                if (curPenDown && !penDown)
//                {
////System.err.println("+++Start pen stroke ");
//                    penX = penNextX;
//                    penY = penNextY;
//                    penTiltX = penNextTiltX;
//                    penTiltY = penNextTiltY;
//                    penPressure = penNextPressure;
//                    if (strokeBuilder != null)
//                    {
//                        strokeBuilder.clear();
//                    }
//                }
//                penDown = curPenDown;
//                
//                if (dragging && penDown)
//                {
//                    strokeToNextSample();
//                }
                
                Pen pen = penManager.pen;
                float x = pen.getLevelValue(PLevel.Type.X);
                float y = pen.getLevelValue(PLevel.Type.Y);
                float pressure = pen.getLevelValue(PLevel.Type.PRESSURE);
                float tiltX = pen.getLevelValue(PLevel.Type.TILT_X);
                float tiltY = pen.getLevelValue(PLevel.Type.TILT_Y);
                
                PenPoint pt = new PenPoint(x, y, pressure, tiltX, tiltY);
                penBackBuf.add(pt);
                
                break;
            }
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

    @Override
    public void render(RenderContext ctx)
    {
        if (strokeBuilder != null)
        {
            CyDrawStack stack = ctx.getDrawStack();
            strokeBuilder.render(stack);
        }
        
        if (strokePath != null)
        {
            CyDrawStack stack = ctx.getDrawStack();
            
            CyMaterialColorDrawRecord rec = 
                    CyMaterialColorDrawRecordFactory.inst().allocRecord();

            rec.setColor(CyColor4f.GREEN);
            rec.setOpacity(1);

            ShapeLinesProvider prov = new ShapeLinesProvider(strokePath);
            CyVertexBuffer lineMesh = new CyVertexBuffer(prov);
            rec.setMesh(lineMesh);

            CyMatrix4d mvp = stack.getModelViewProjXform();
            rec.setMvpMatrix(mvp);

            stack.addDrawRecord(rec);
            
        }
  
        
    }
    
    //---------------------------
    class PenPoint
    {
        float x;
        float y;
        float pressure;
        float tiltX;
        float tiltY;

        public PenPoint(float x, float y, float pressure, float tiltX, float tiltY)
        {
            this.x = x;
            this.y = y;
            this.pressure = pressure;
            this.tiltX = tiltX;
            this.tiltY = tiltY;
        }

        public PenPoint(float x, float y)
        {
            this(x, y, 1, 0, 0);
        }

        @Override
        public String toString()
        {
            return String.format("pos (%f %f) pres %f tilt(%f %f)",
                    x, y, pressure, tiltX, tiltY);
        }

        
    }
}
