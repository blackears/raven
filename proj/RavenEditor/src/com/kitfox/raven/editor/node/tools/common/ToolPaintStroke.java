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

package com.kitfox.raven.editor.node.tools.common;

import com.kitfox.cache.CacheIdentifier;
import com.kitfox.cache.CacheList;
import com.kitfox.game.control.color.PaintLayoutTexture;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.node.scene.RavenNodeDataPlane;
import com.kitfox.raven.editor.node.scene.RavenNodeGroup;
import com.kitfox.raven.editor.node.scene.RavenNodePath;
import com.kitfox.raven.editor.node.scene.RavenNodeRoot;
import com.kitfox.raven.editor.node.tools.ToolProvider;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.shape.bezier.BezierMath;
import com.kitfox.raven.shape.bezier.BezierPath;
import com.kitfox.raven.shape.bezier.BezierVertex;
import com.kitfox.raven.shape.bezier.VertexSmooth;
import com.kitfox.raven.shape.builders.BitmapOutliner;
import com.kitfox.raven.shape.builders.StrokeBuffer;
import com.kitfox.raven.shape.path.PathCurve;
import com.kitfox.rabbit.util.NumberText;
import com.kitfox.raven.editor.node.scene.RavenNodeSceneGraph;
import com.kitfox.raven.paint.RavenPaintLayout;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.tree.NodeObjectProviderIndex;
import com.kitfox.raven.util.tree.SelectionRecord;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
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
@Deprecated
public class ToolPaintStroke extends ToolDisplay
        implements PenListener
{
    final ToolPaintStroke.Provider toolProvider;

    StrokeBuffer bubbleOutliner;
    final int strokeBufferSize = 128;

    final PenManager penManager;

    private float penX;
    private float penY;
    private float penPressure;

    private float penNextX;
    private float penNextY;
    private float penNextPressure;

    boolean penDown;
    boolean readingPen = false;
//    private float spacing = .2f;
//    private float penWeight = 10f;
//    private float smoothing = 10f;

//    private final static AffineTransform toCentipixels = new AffineTransform(100, 0, 0, 100, 0, 0);

    //Angle used when building paths from stroke to determine if a given vertex
    // should have a CUSP or SMOOTH join
//    private double smoothAngle = 45;

    protected ToolPaintStroke(ToolUser user, Provider toolProvider)
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

    @Override
    public void dispose()
    {
        if (penManager != null)
        {
            penManager.pen.removeListener(this);
        }
    }

    @Override
    public void click(MouseEvent evt)
    {
    }

    @Override
    public void paint(Graphics2D g)
    {
        super.paint(g);

        g.setColor(Color.blue);
        if (bubbleOutliner != null)
        {
            bubbleOutliner.render(g);
        }
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
        bubbleOutliner = new StrokeBuffer(strokeBufferSize, strokeBufferSize,
                dev.getComponent().getGraphicsConfiguration());
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
        if (BezierMath.square(penX - penNextX) +
                + BezierMath.square(penY - penNextY) < 4)
        {
            return;
        }

        final float spacing = toolProvider.getStrokeSpacing();
        final float penWeightMin = toolProvider.getStrokeWidthMin();
        final float penWeightMax = toolProvider.getStrokeWidthMax();

        float gap = Math.max(spacing * penPressure * penWeightMax, 1);
        double dist = BezierMath.distance(penX, penY, penNextX, penNextY);
        int numDots = (int)Math.ceil(dist / gap);

        for (int i = 0; i < numDots; ++i)
        {
            double dt = (double)i / numDots;
            double dPressure = BezierMath.lerp(penPressure, penNextPressure, dt);
            bubbleOutliner.addCircle(
                    BezierMath.lerp(penX, penNextX, dt),
                    BezierMath.lerp(penY, penNextY, dt),
                    BezierMath.lerp(penWeightMin, penWeightMax, dPressure));

        }

        penX = penNextX;
        penY = penNextY;
        penPressure = penNextPressure;
    }

    @Override
    protected void startDrag(MouseEvent evt)
    {
        samplePen(evt);

        penX = penNextX;
        penY = penNextY;
        penPressure = penNextPressure;

        ServiceDevice dev = user.getToolService(ServiceDevice.class);
        bubbleOutliner = new StrokeBuffer(strokeBufferSize, strokeBufferSize,
                dev.getComponent().getGraphicsConfiguration());
        penDown = true;
        readingPen = false;

    }

    @Override
    protected void endDrag(MouseEvent evt)
    {
        if (bubbleOutliner == null)
        {
            //Will be null if canceled or more than one mouse button pushed
            return;
        }

        strokeSegment(evt);

//        if (!bubbleOutliner.isEmpty())
//        {
//            BitmapOutliner outliner = bubbleOutliner.buildOutliner();
//            final float smoothing = toolProvider.getStrokeSmoothing();
//            final Path2D.Double path = outliner.createSmoothedPath(
//                    Math.max(smoothing, .1));
//
//            if (path != null)
//            {
//                ServiceDocument provider = user.getToolService(ServiceDocument.class);
//                RavenNodeRoot doc = (RavenNodeRoot)provider.getDocument();
//                RavenNodeSceneGraph sceneGraph = doc.getSceneGraph();
//
//                
//                ServiceDeviceCamera provDevCam = user.getToolService(ServiceDeviceCamera.class);
//                AffineTransform w2d = provDevCam
//                        .getWorldToDeviceTransform((AffineTransform)null);
//
//                //Find node to add new stroke to
//                NodeObject topSel = doc.getSelection().getTopSelected();
//                RavenNodeGroup parentGroupNode = null;
//                if (topSel != null)
//                {
//                    if (topSel instanceof RavenNodeGroup)
//                    {
//                        parentGroupNode = (RavenNodeGroup)topSel;
//                    }
//                    else if (topSel.getParent() != null)
//                    {
//                        NodeObject topNodeParent = topSel.getParent().getNode();
//                        if (topNodeParent instanceof RavenNodeGroup)
//                        {
//                            parentGroupNode = (RavenNodeGroup)topNodeParent;
//                        }
//                    }
//                }
//
//                AffineTransform devToLocal = null;
//                if (parentGroupNode != null)
//                {
//                    devToLocal = parentGroupNode
//                            .getLocalToWorldTransform((AffineTransform)null);
//                    devToLocal.preConcatenate(w2d);
//                }
//                else
//                {
//                    devToLocal = w2d;
//                }
//                devToLocal.scale(1 / 100.0, 1 / 100.0);
//
//                try
//                {
//                    devToLocal.invert();
//                } catch (NoninvertibleTransformException ex)
//                {
//                    Logger.getLogger(ToolPaintStroke.class.getName()).log(Level.SEVERE, null, ex);
//                }
////                devToLocal.preConcatenate(toCentipixels);
//                //devToLocal.scale(100, 100);
//                Path2D.Double shape = (Path2D.Double)devToLocal
//                        .createTransformedShape(path);
//
//
//
//
//                //Add node
//                if (doc != null)
//                {
//                    doc.getHistory().beginTransaction("Add paint stroke");
//                }
//
//                NodeObjectProvider<RavenNodePath> prov = 
//                        NodeObjectProviderIndex.inst().getProvider(RavenNodePath.class);
//                RavenNodePath nodePath = prov.createNode(doc);
//
//                String name = doc.createUniqueName("Stroke");
//                nodePath.setName(name);
//
////                PathCurve curve = new PathCurve(shape);
////                nodePath.path.setValue(curve);
//
//                RavenNodeDataPlane plane = NodeObjectProviderIndex.inst()
//                        .createNode(RavenNodeDataPlane.class, doc);
//                {
//                    BezierPath bezPath = new BezierPath(10000);
//                    bezPath.append(shape, null);
//
//                    CacheList cacheSmoothList = new CacheList();
//                    ArrayList<VertexSmooth> smoothList;
//                    switch (toolProvider.vertexSmooth)
//                    {
//                        default:
//                        case SMOOTH:
//                            smoothList = bezPath.buildVertexSmoothing(
//                                    toolProvider.getVertexSmoothAngle());
//                            break;
//                        case TENSE:
//                            smoothList = new ArrayList<VertexSmooth>();
//                            for (int i = 0; i < bezPath.getNumVertices(); ++i)
//                            {
//                                smoothList.add(VertexSmooth.TENSE);
//                            }
//                            break;
//                    }
//                    ArrayList<BezierVertex> vertList = bezPath.getVertices();
//                    
//                    for (int i = 0; i < smoothList.size(); ++i)
//                    {
//                        VertexSmooth smooth = smoothList.get(i);
//                        BezierVertex vtx = vertList.get(i);
//                        bezPath.applyVertexSmoothing(smooth, vtx);
//                        cacheSmoothList.add(new CacheIdentifier(smooth.name()));
//                    }
//
//                    PathCurve curve = new PathCurve(bezPath.asPath());
//                    nodePath.path.setValue(curve);
//
//                    plane.dataType.setValue(VertexSmooth.PlaneData.class.getCanonicalName(), false);
//                    plane.dataValues.setValue(cacheSmoothList.toString());
//                    nodePath.vertexPlanes.add(plane);
//                }
//
//                nodePath.paint.setData(doc.fillPaint.getData());
//                nodePath.paintLayout.setValue(new RavenPaintLayout(nodePath.getBoundsLocal()));
//                nodePath.centerPivot(true);
//
//                if (parentGroupNode != null)
//                {
//                    parentGroupNode.children.add(nodePath);
//
//                }
//                else
//                {
//                    sceneGraph.add(nodePath);
//                }
//
//                if (doc != null)
//                {
//                    doc.getHistory().commitTransaction();
//                }
//            }
//        }

        bubbleOutliner = null;
        penDown = false;
        fireToolDisplayChanged();
    }

    @Override
    protected void dragTo(MouseEvent evt)
    {
//System.err.println("Drag to");
        strokeSegment(evt);
        fireToolDisplayChanged();
    }

    @Override
    public void cancel()
    {
        bubbleOutliner = null;
        penDown = false;
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

//    /**
//     * @return the smoothAngle
//     */
//    public double getSmoothAngle()
//    {
//        return smoothAngle;
//    }
//
//    /**
//     * @param smoothAngle the smoothAngle to set
//     */
//    public void setSmoothAngle(double smoothAngle)
//    {
//        this.smoothAngle = smoothAngle;
//    }


    //---------------------------------------

//    @ServiceInst(service=ToolProvider.class)
    static public class Provider extends ToolProvider<ToolPaintStroke>
    {
        private float strokeWidthMax = 4;
        public static final String PROP_STROKE_WIDTH_MAX = "strokeWidthMax";

        private float strokeWidthMin = 2;
        public static final String PROP_STROKE_WIDTH_MIN = "strokeWidthMin";

        private float strokeSpacing = .2f;
        public static final String PROP_STROKE_SPACING = "strokeSpacing";

        private float strokeSmoothing = 10;
        public static final String PROP_STROKE_SMOOTHING = "strokeSmoothing";

        private float vertexSmoothAngle = 10;
        public static final String PROP_VERTEX_SMOOTH_ANGLE = "vertexSmoothAngle";

        private VertexSmooth vertexSmooth = VertexSmooth.SMOOTH;
        public static final String PROP_VERTEX_SMOOTH = "vertexSmooth";

        public Provider()
        {
            super("Paint Stroke", "/icons/tools/paintStroke.png", "/manual/tools/paintStroke.html");
        }

        @Override
        public void loadPreferences(Properties properties)
        {
            super.loadPreferences(properties);

            strokeWidthMax = NumberText.findFloat(
                    properties.getProperty(PROP_STROKE_WIDTH_MAX), 4);
            strokeWidthMin = NumberText.findFloat(
                    properties.getProperty(PROP_STROKE_WIDTH_MIN), 2);
            strokeSpacing = NumberText.findFloat(
                    properties.getProperty(PROP_STROKE_SPACING), .2f);
            strokeSmoothing = NumberText.findFloat(
                    properties.getProperty(PROP_STROKE_SMOOTHING), 10);
            vertexSmoothAngle = NumberText.findFloat(
                    properties.getProperty(PROP_VERTEX_SMOOTH_ANGLE), 10);
            try
            {
                String strn = properties.getProperty(PROP_VERTEX_SMOOTH);
                vertexSmooth = strn == null ? VertexSmooth.SMOOTH
                        : VertexSmooth.valueOf(strn);
            }
            catch (IllegalArgumentException ex)
            {
                vertexSmooth = VertexSmooth.SMOOTH;
            }
        }

        @Override
        public Properties savePreferences()
        {
            Properties prop = new Properties();
            prop.setProperty(PROP_STROKE_WIDTH_MAX, "" + strokeWidthMax);
            prop.setProperty(PROP_STROKE_WIDTH_MIN, "" + strokeWidthMin);
            prop.setProperty(PROP_STROKE_SPACING, "" + strokeSpacing);
            prop.setProperty(PROP_STROKE_SMOOTHING, "" + strokeSmoothing);
            if (vertexSmooth != null)
            {
                prop.setProperty(PROP_VERTEX_SMOOTH, "" + vertexSmooth.name());
            }
            prop.setProperty(PROP_VERTEX_SMOOTH_ANGLE, "" + vertexSmoothAngle);
            return prop;
        }

        @Override
        public ToolPaintStroke create(ToolUser user)
        {
            return new ToolPaintStroke(user, this);
        }

        @Override
        public Component createToolSettingsEditor(RavenEditor editor)
        {
            return new ToolPaintStrokeSettings(editor, this);
        }

        /**
         * @return the strokeWidthMax
         */
        public float getStrokeWidthMax()
        {
            return strokeWidthMax;
        }

        /**
         * @param strokeWidthMax the strokeWidthMax to set
         */
        public void setStrokeWidthMax(float strokeWidthMax)
        {
            this.strokeWidthMax = strokeWidthMax;
        }

        /**
         * @return the strokeWidthMin
         */
        public float getStrokeWidthMin()
        {
            return strokeWidthMin;
        }

        /**
         * @param strokeWidthMin the strokeWidthMin to set
         */
        public void setStrokeWidthMin(float strokeWidthMin)
        {
            this.strokeWidthMin = strokeWidthMin;
        }

        /**
         * @return the strokeSpacing
         */
        public float getStrokeSpacing()
        {
            return strokeSpacing;
        }

        /**
         * @param strokeSpacing the strokeSpacing to set
         */
        public void setStrokeSpacing(float strokeSpacing)
        {
            this.strokeSpacing = strokeSpacing;
        }

        /**
         * @return the strokeSmoothing
         */
        public float getStrokeSmoothing()
        {
            return strokeSmoothing;
        }

        /**
         * @param strokeSmoothing the strokeSmoothing to set
         */
        public void setStrokeSmoothing(float strokeSmoothing)
        {
            this.strokeSmoothing = strokeSmoothing;
        }

        /**
         * @return the vertexSmoothAngle
         */
        public float getVertexSmoothAngle()
        {
            return vertexSmoothAngle;
        }

        /**
         * @param vertexSmoothAngle the vertexSmoothAngle to set
         */
        public void setVertexSmoothAngle(float vertexSmoothAngle)
        {
            this.vertexSmoothAngle = vertexSmoothAngle;
        }

        /**
         * @return the vertexSmooth
         */
        public VertexSmooth getVertexSmooth()
        {
            return vertexSmooth;
        }

        /**
         * @param vertexSmooth the vertexSmooth to set
         */
        public void setVertexSmooth(VertexSmooth vertexSmooth)
        {
            this.vertexSmooth = vertexSmooth;
        }
    }

}
