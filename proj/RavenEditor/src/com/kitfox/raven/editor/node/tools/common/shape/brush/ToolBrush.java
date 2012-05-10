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

import com.kitfox.raven.raster.RoundBrushSource;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyGLOffscreenContext;
import com.kitfox.coyote.renderer.CyGLWrapper;
import com.kitfox.coyote.renderer.CyTextureImage;
import com.kitfox.coyote.renderer.CyTransparency;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.coyote.shape.CyRectangle2i;
import com.kitfox.coyote.shape.bezier.builder.BezierCurveNd;
import com.kitfox.coyote.shape.bezier.builder.BezierPointNd;
import com.kitfox.coyote.shape.bezier.builder.PiecewiseBezierSchneiderNd;
import com.kitfox.coyote.shape.bezier.mesh.BezierMeshEdge2i;
import com.kitfox.coyote.shape.outliner.bitmap.BitmapOutliner;
import com.kitfox.raven.editor.node.scene.RavenNodeMesh2;
import com.kitfox.raven.editor.node.scene.RavenSymbolRoot;
import com.kitfox.raven.editor.node.scene.RavenNodeSceneGraph;
import com.kitfox.raven.editor.node.scene.RenderContext;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.editor.node.tools.common.ServiceDevice;
import com.kitfox.raven.editor.node.tools.common.ServicePen;
import com.kitfox.raven.editor.node.tools.common.ToolDisplay;
import com.kitfox.raven.paint.RavenPaint;
import com.kitfox.raven.paint.RavenPaintLayout;
import com.kitfox.raven.paint.RavenStroke;
import com.kitfox.raven.raster.TiledRasterData;
import com.kitfox.raven.shape.network.NetworkDataEdge;
import com.kitfox.raven.shape.network.NetworkMesh;
import com.kitfox.raven.shape.network.keys.NetworkDataTypePaint;
import com.kitfox.raven.shape.network.keys.NetworkDataTypePaintLayout;
import com.kitfox.raven.shape.network.keys.NetworkDataTypeStroke;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.NodeObjectProviderIndex;
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

/*
Brush design:

Brush tip is simple bitmap.  No gradients.
Fit pen points to a smoothed piecewise bezier
	- 3 dimensions in bezier: (x, y, weight)
	- May want to include tilt.
Brush tip mask plotted along smoothed curve
	- Spacing is 1 pixel
	- Curve weight, brush width (min, max) used to scale tip mask
Bubble outliner used to calc stroke contours
Bubble contours smoothed
 */

/**
 *
 * @author kitfox
 */
public class ToolBrush extends ToolDisplay
        implements PenListener
{
    final ToolBrushProvider toolProvider;
    
    final PenManager penManager;

    boolean dragging;
    boolean penDown;
    boolean plottedPointsFromMouse;
    
//    PiecewiseBezierBuilder2d curveLeft;
//    PiecewiseBezierBuilder2d curveRight;
//    CyPath2d strokePath;
    
    //Track and smooth base bezier curve
    PiecewiseBezierSchneiderNd pathBuilder;
    
    //Raster that will have pen bitmaps blitted to it for stroke preview
    StrokeBuilder strokeBuilder;

    ArrayList<PenPoint> penBuf = new ArrayList<PenPoint>();
    ArrayList<PenPoint> penBackBuf = new ArrayList<PenPoint>();

    ArrayList<PenPoint> plotBackBuf = new ArrayList<PenPoint>();
//    ArrayList<PenPoint> plotHistory = new ArrayList<PenPoint>();
    PenPoint plotPrev;
    
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
                1, 
                false);
        
        int size = brush.getSize();
        CyTextureImage source = new CyTextureImage(
                CyGLWrapper.TexTarget.GL_TEXTURE_2D, 
                CyGLWrapper.InternalFormatTex.GL_RGBA,
                CyGLWrapper.DataType.GL_UNSIGNED_BYTE,
                size, size, CyTransparency.TRANSLUCENT, brush);
        
        strokeBuilder = new StrokeBuilder(source);
        
        pathBuilder = new PiecewiseBezierSchneiderNd(3, 
                false, toolProvider.getPathSmoothing());
    }
    
    private void plotStroke()
    {
        for (int i = 0; i < plotBackBuf.size(); ++i)
        {
            PenPoint pt = plotBackBuf.get(i);
            strokeTo(pt);
        }
        plotBackBuf.clear();
//        plotHistory.clear();
    }
    
//    private CyPath2d calcStroke()
//    {
//        //Examine end points to calc caps
//        BezierCubic2d segL0 = curveLeft.getFirstSegment();
//        BezierCubic2d segL1 = curveLeft.getLastSegment();
//        BezierCubic2d segR0 = curveRight.getFirstSegment();
//        BezierCubic2d segR1 = curveRight.getLastSegment();
//        
//        if (segL0 == null || segR0 == null)
//        {
//            return null;
//        }
//        
//        CyVector2d pL1 = new CyVector2d(segL1.getEndX(), segL1.getEndY());
//        CyVector2d tL1 = new CyVector2d(segL1.getTanOutX(), segL1.getTanOutY());
//        CyVector2d pR0 = new CyVector2d(segR1.getEndX(), segR1.getEndY());
//        CyVector2d tR0 = new CyVector2d(segR1.getTanOutX(), segR1.getTanOutY());
//
//        CyVector2d pR1 = new CyVector2d(segR0.getStartX(), segR0.getStartY());
//        CyVector2d tR1 = new CyVector2d(-segR0.getTanInX(), -segR0.getTanInY());
//        CyVector2d pL0 = new CyVector2d(segL0.getStartX(), segL0.getStartY());
//        CyVector2d tL0 = new CyVector2d(-segL0.getTanInX(), -segL0.getTanInY());
//
//        double diam0 = pL1.distance(pR0);
//        double diam1 = pR1.distance(pL0);
//
//        tL1.normalize();
//        tL1.scale(diam0 * 3 / 4);
//        tR0.normalize();
//        tR0.scale(diam0 * 3 / 4);
//        
//        tL0.normalize();
//        tL0.scale(diam1 * 3 / 4);
//        tR1.normalize();
//        tR1.scale(diam1 * 3 / 4);
//        
//        CyPath2d pathStroke = new CyPath2d();
//
//        pathStroke.moveTo(pL0.x, pL0.y);
//        
//        curveLeft.appendSegs(pathStroke, false);
//        
//        pathStroke.cubicTo(pL1.x + tL1.x, pL1.y + tL1.y, 
//                pR0.x + tR0.x, pR0.y + tR0.y,
//                pR0.x, pR0.y);
//
//        curveRight.appendSegs(pathStroke, true);
//        
//        pathStroke.cubicTo(pR1.x + tR1.x, pR1.y + tR1.y, 
//                pL0.x + tL0.x, pL0.y + tL0.y,
//                pL0.x, pL0.y);
//
//        pathStroke.close();
//        
//        return pathStroke;
//    }
    
    private void strokeTo(PenPoint pt)
    {
        if (plotPrev == null)
        {
            plotPrev = pt;
            return;
        }
        
        //Only record if minimum distance traveled
        if (Math2DUtil.distSquared(plotPrev.x, plotPrev.y, pt.x, pt.y)
                < 16)
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
        
        
        //Plot cursor in preview raster
        final float spacing = toolProvider.getStrokeSpacing();
        final float penWidthMin = toolProvider.getHardness();
        final float penWidthMax = toolProvider.getStrokeWidthMax();

//        double dist = Math2DUtil.dist(penX, penY, penNextX, penNextY);

//System.err.println("Old " + penOld);
//System.err.println("new " + penNew);
        CyVector2d penOld = new CyVector2d(plotPrev.x, plotPrev.y);
        CyVector2d penNew = new CyVector2d(pt.x, pt.y);

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
        
        //Build smoothed path
        pathBuilder.addPoint(new BezierPointNd(penNew.x, penNew.y, pt.pressure));
        
//        double gap = Math.max(spacing * penPressure * penWidthMax, 1);
//        int numDots = (int)Math.ceil(dist / gap);
        int numDots = (int)Math.ceil(dist / spacing);
//        plotHistory.add(pt);

//        //Collect points in stroke builder
//        float gap = penWidthMax - penWidthMin;
//        CyVector2d norm = new CyVector2d(pt.x - plotPrev.x, 
//                pt.y - plotPrev.y);
//        norm.normalize();
//        norm.rotCCW90();
//        norm.scale(plotPrev.pressure * gap + penWidthMin);
//        
//        CyVector2d ptLeft = new CyVector2d(
//                plotPrev.x + norm.x, plotPrev.y + norm.y);
//        CyVector2d ptRight = new CyVector2d(
//                plotPrev.x - norm.x, plotPrev.y - norm.y);
//        curveLeft.addPoint(ptLeft);
//        curveRight.addPoint(ptRight);
//        
//        calcStroke();
        
        //Draw point to raster
        ServiceDevice dev = user.getToolService(ServiceDevice.class);
        CyGLOffscreenContext ctxOff = dev.createOffscreenGLContext();
        
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
    }

    private void plotLine(BezierPointNd p0, BezierPointNd p1,
            CyGLOffscreenContext ctxOff)
    {
        final float penWidthMin = toolProvider.getHardness();
        final float penWidthMax = toolProvider.getStrokeWidthMax();

        double dx = p1.get(0) - p0.get(0);
        double dy = p1.get(1) - p0.get(1);

        int steps = Math.abs(dx) > Math.abs(dy)
                ? (int)Math.ceil(Math.abs(dx))
                : (int)Math.ceil(Math.abs(dy));
        
        double dt = 1.0 / steps;
        for (int i = 0; i < steps; ++i)
        {
            BezierPointNd pt = p0.lerp(p1, i * dt);

            double x = pt.get(0);
            double y = pt.get(1);
            double pres = pt.get(2);
            
            double pressure = Math2DUtil.lerp(
                    penWidthMin, penWidthMax, pres) / penWidthMax;
            
            strokeBuilder.daubBrush(ctxOff, 
                    x, y, pressure);
        }
    }
    
    private void plotCurve(BezierCurveNd curve, CyGLOffscreenContext ctxOff)
    {
        if (curve.flatnessSq(3) < 2)
        {
            BezierPointNd p0 = curve.getStart();
            BezierPointNd p1 = curve.getEnd();
            plotLine(p0, p1, ctxOff);
            return;
        }
        
        BezierCurveNd[] part = curve.split(.5);
        plotCurve(part[0], ctxOff);
        plotCurve(part[1], ctxOff);
    }
    
//    private void plotCurve(BezierCurveNd curve, CyGLOffscreenContext ctxOff, 
//            double tMin, double tMax)
//    {
//        double spanX = curve.getSpan(0);
//        double spanY = curve.getSpan(1);
//        
//        if (spanX <= 1 && spanY <= 1)
//        {
//            final float penWidthMin = toolProvider.getHardness();
//            final float penWidthMax = toolProvider.getStrokeWidthMax();
//        
//            BezierPointNd pt = curve.eval(tMin);
//            double x = pt.get(0);
//            double y = pt.get(1);
//            double pres = pt.get(2);
//            
//            double pressure = Math2DUtil.lerp(
//                    penWidthMin, penWidthMax, pres) / penWidthMax;
//            
//            strokeBuilder.daubBrush(ctxOff, 
//                    x, y, pressure);
//            return;
//        }
//        
//        double tMid = (tMin + tMax) / 2;
//        BezierCurveNd[] part = curve.split(.5);
//        plotCurve(part[0], ctxOff, tMin, tMid);
//        plotCurve(part[1], ctxOff, tMid, tMax);
//    }
    
    private void createStrokeShape()
    {
        strokeBuilder.clear();

        ServiceDevice dev = user.getToolService(ServiceDevice.class);
        CyGLOffscreenContext ctxOff = dev.createOffscreenGLContext();

        //Redraw path using smoothed curve and with spacing == 1
        for (BezierCurveNd curve: pathBuilder.getCurves())
        {
            plotCurve(curve, ctxOff);
        }

        TiledRasterData rasterData = strokeBuilder.getData(ctxOff);
        
        ctxOff.dispose();

//System.err.println("Dumping raster");
//rasterData.dump();
        
        //Create outline
        CyRectangle2i bounds = rasterData.getBounds();
        RasterDataSampler sampler = new RasterDataSampler(rasterData, 128);
        float strokeSmoothing = toolProvider.getStrokeSmoothing();
        BitmapOutliner outliner = new BitmapOutliner(
                sampler, bounds, sampler.getEmptyLevel(), strokeSmoothing);

//        BitmapOutliner outliner = 
//                new BitmapOutliner(new RasterDataSampler(rasterData),
//                new CyRectangle2d(bounds), 
//                bounds.getWidth(), bounds.getHeight());
        
//outliner.dumpBitmap();
        
//        strokePath = outliner.getPath(strokeSmoothing);
        applyStroke(outliner.getPath());
    }
    
    private void applyStroke(CyPath2d path)
    {
        Selection<NodeObject> sel = getSelection();
        NodeObject topObj = sel.getTopSelected();

        if (topObj instanceof RavenNodeMesh2)
        {
            applyStrokeToMesh(path, (RavenNodeMesh2)topObj);
            return;
        }

        RavenNodeMesh2 meshNode = createStrokeMesh(path);
                
        RavenSymbolRoot doc = getDocument();
        RavenNodeSceneGraph sg = doc.getSceneGraph();
        sg.children.add(meshNode);
    }
    
    private void applyStrokeToMesh(CyPath2d path, RavenNodeMesh2 mesh)
    {
        NetworkMesh curNet = mesh.getNetworkMesh();
        NetworkMesh newNet = new NetworkMesh(curNet);
        
        CyMatrix4d scale = RavenNodeMesh2.getMeshToLocal();
        scale.invert();
        CyPath2d meshPath = path.createTransformedPath(scale);
        
        //Add new edges
        NetworkDataEdge data = createMeshData(path);
        ArrayList<BezierMeshEdge2i> edges = newNet.addEdge(meshPath, data);
        
        //Delete new curve pieces that have same paint on both sides
        for (BezierMeshEdge2i e: edges)
        {
            NetworkDataEdge edgeData = (NetworkDataEdge)e.getData();
            if (edgeData.isLeftSideEqualToRightSide())
            {
                newNet.removeEdge(e);
            }
        }
        
        mesh.setNetworkMesh(newNet,true);
    }
    
    private RavenNodeMesh2 createStrokeMesh(CyPath2d path)
    {
        CyMatrix4d scale = RavenNodeMesh2.getMeshToLocal();
        scale.invert();
        CyPath2d meshPath = path.createTransformedPath(scale);

        RavenSymbolRoot root = getDocument();
        
        //Create node
        NetworkDataEdge data = createMeshData(path);
        
        NetworkMesh network = NetworkMesh.create(meshPath, data);
        
        RavenNodeMesh2 mesh = NodeObjectProviderIndex.inst().createNode(
                RavenNodeMesh2.class, root.getSymbol());
        mesh.setNetworkMesh(network, false);
        
        return mesh;
    }
    
    private NetworkDataEdge createMeshData(CyPath2d path)
    {
        CyRectangle2d boundsLocal = path.getBounds();

        RavenSymbolRoot root = getDocument();
        
        RavenPaintLayout layout = new RavenPaintLayout(boundsLocal);
        RavenStroke stroke = root.getStrokeShape();
        RavenPaint edgeColor = root.getStrokePaint();
        RavenPaint leftColor = root.getFillPaint();
        
        NetworkDataEdge data = new NetworkDataEdge();
        data.putRight(NetworkDataTypePaint.class, leftColor);
        data.putRight(NetworkDataTypePaintLayout.class, layout);
        
        data.putEdge(NetworkDataTypePaint.class, edgeColor);
        data.putEdge(NetworkDataTypePaintLayout.class, layout);
        data.putEdge(NetworkDataTypeStroke.class, stroke);
        
        return data;
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
        
        plotBackBuf.clear();
//        plotHistory.clear();
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
        
//        float smoothing = toolProvider.getStrokeSmoothing();
//        curveLeft = new PiecewiseBezierBuilder2d(smoothing);
//        curveRight = new PiecewiseBezierBuilder2d(smoothing);
        
        createStrokeBuilder();
//        strokePath = null;
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
//                plotHistory.clear();
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
//        strokePath = calcStroke();
    }

    @Override
    protected void endDrag(MouseEvent evt)
    {
        plotStroke();
        createStrokeShape();

        strokeBuilder = null;
        pathBuilder = null;
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
//System.err.println("Pen event " + i + " " + levels[i].getType() + " " + levels[i].value);
            if (levels[i].getType() == PLevel.Type.PRESSURE)
            {
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
        
//        if (strokePath != null)
//        {
//            CyDrawStack stack = ctx.getDrawStack();
//            
//            CyMaterialColorDrawRecord rec = 
//                    CyMaterialColorDrawRecordFactory.inst().allocRecord();
//
//            rec.setColor(CyColor4f.GREEN);
//            rec.setOpacity(1);
//
//            ShapeLinesProvider prov = new ShapeLinesProvider(strokePath);
//            CyVertexBuffer lineMesh = new CyVertexBuffer(prov);
//            rec.setMesh(lineMesh);
//
//            CyMatrix4d mvp = stack.getModelViewProjXform();
//            rec.setMvpMatrix(mvp);
//
//            stack.addDrawRecord(rec);
//            
//        }
    }
}
