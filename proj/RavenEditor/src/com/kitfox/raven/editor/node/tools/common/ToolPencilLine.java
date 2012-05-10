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

import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.CyStroke;
import com.kitfox.coyote.shape.CyStrokeCap;
import com.kitfox.coyote.shape.CyStrokeJoin;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.node.scene.RavenNodeGroup;
import com.kitfox.raven.editor.node.scene.RavenNodePath;
import com.kitfox.raven.editor.node.scene.RavenSymbolRoot;
import com.kitfox.raven.editor.node.scene.RavenNodeSceneGraph;
import com.kitfox.raven.editor.node.tools.ToolProvider;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.paint.RavenStroke;
import com.kitfox.raven.paint.common.RavenPaintColor;
import com.kitfox.raven.shape.bezier.BezierMath;
import com.kitfox.coyote.shape.bezier.builder.PiecewiseBezierSchneider2d;
import com.kitfox.raven.editor.node.scene.RavenSymbol;
import com.kitfox.raven.shape.path.PathCurve;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.tree.NodeObjectProviderIndex;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
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
public class ToolPencilLine extends ToolDisplay
        implements PenListener
{
    PiecewiseBezierSchneider2d pencilBuilder;

    final PenManager penManager;

    private float penX;
    private float penY;
    private float penPressure;

    private float penNextX;
    private float penNextY;
    private float penNextPressure;

    boolean penDown;
    boolean readingPen = false;
    private float smoothing = 10f;

    protected ToolPencilLine(ToolUser user)
    {
        super(user);

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
        g.setStroke(new BasicStroke(5));
        if (pencilBuilder != null)
        {
            CyPath2d path = pencilBuilder.getPath();
            if (path != null)
            {
                g.draw(path.asPathAWT());
            }
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

        pencilBuilder = new PiecewiseBezierSchneider2d(false, smoothing);
        readingPen = true;
    }

    private void samplePen(MouseEvent evt)
    {
        //Stroke building phase is in device space
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

    @Override
    protected void startDrag(MouseEvent evt)
    {
        samplePen(evt);

        penX = penNextX;
        penY = penNextY;
        penPressure = penNextPressure;

        pencilBuilder = new PiecewiseBezierSchneider2d(false, smoothing);
        penDown = true;
        readingPen = false;

    }

    @Override
    protected void dragTo(MouseEvent evt)
    {
        if (penDown)
        {
            samplePen(evt);

            //Only record if minimum distance traveled
            if (BezierMath.square(penX - penNextX) +
                    + BezierMath.square(penY - penNextY) <= 4)
            {
                return;
            }

            penX = penNextX;
            penY = penNextY;
            penPressure = penNextPressure;
            
            pencilBuilder.addPoint(new CyVector2d(penNextX, penNextY));
            fireToolDisplayChanged();
        }

    }

    @Override
    protected void endDrag(MouseEvent evt)
    {
        if (pencilBuilder == null)
        {
            //Will be null if canceled or more than one mouse button pushed
            return;
        }

        samplePen(evt);
        pencilBuilder.addPoint(new CyVector2d(penNextX, penNextY));

//        if (!bubbleOutliner.isEmpty())
        {
            final CyPath2d path = pencilBuilder.getPath();

            if (path != null)
            {
                ServiceDocument provider = user.getToolService(ServiceDocument.class);
                RavenSymbol sym = (RavenSymbol)provider.getSymbol();
                RavenSymbolRoot root = sym.getRoot();
                RavenNodeSceneGraph sceneGraph = root.getSceneGraph();

                ServiceDeviceCamera provDevCam = user.getToolService(ServiceDeviceCamera.class);
                AffineTransform w2d = provDevCam
                        .getWorldToDeviceTransform((AffineTransform)null);

                //Find node to add new stroke to
                NodeObject topSel = root.getSelection().getTopSelected();
                RavenNodeGroup parentGroupNode = null;
                if (topSel != null)
                {
                    if (topSel instanceof RavenNodeGroup)
                    {
                        parentGroupNode = (RavenNodeGroup)topSel;
                    }
                    else
                    {
                        NodeObject topNodeParent = topSel.getParent().getNode();
                        if (topNodeParent instanceof RavenNodeGroup)
                        {
                            parentGroupNode = (RavenNodeGroup)topNodeParent;
                        }
                    }
                }

                AffineTransform devToLocal = null;
                if (parentGroupNode != null)
                {
                    devToLocal = parentGroupNode
                            .getLocalToWorldTransform((AffineTransform)null);
                    devToLocal.preConcatenate(w2d);
                }
                else
                {
                    devToLocal = w2d;
                }
                devToLocal.scale(1 / 100.0, 1 / 100.0);

                try
                {
                    devToLocal.invert();
                } catch (NoninvertibleTransformException ex)
                {
                    Logger.getLogger(ToolPencilLine.class.getName()).log(Level.SEVERE, null, ex);
                }
                Path2D.Double shape = (Path2D.Double)devToLocal.createTransformedShape(
                        path.asPathAWT());



                //Add node
                if (root != null)
                {
                    root.getHistory().beginTransaction("Add pencil stroke");
                }

                NodeObjectProvider<RavenNodePath> prov = 
                        NodeObjectProviderIndex.inst().getProvider(RavenNodePath.class);
                RavenNodePath nodePath = prov.createNode(sym);

                String name = sym.createUniqueName("Pencil");
                nodePath.setName(name);

                PathCurve curve = new PathCurve(shape);
                nodePath.path.setValue(curve);

                nodePath.paint.setValue(null);
                nodePath.strokePaint.setValue(RavenPaintColor.BLACK);
                nodePath.stroke.setValue(new RavenStroke(
                        new CyStroke(5, 
                        CyStrokeCap.ROUND, CyStrokeJoin.ROUND)));

                nodePath.centerPivot(true);
                
                if (parentGroupNode != null)
                {
                    parentGroupNode.children.add(nodePath);
                }
                else
                {
                    sceneGraph.add(nodePath);
                }

                if (root != null)
                {
                    root.getHistory().commitTransaction();
                }
            }
        }

        pencilBuilder = null;
        penDown = false;
        fireToolDisplayChanged();
    }

    @Override
    public void cancel()
    {
        pencilBuilder = null;
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


    //---------------------------------------

    @ServiceInst(service=ToolProvider.class)
    static public class Provider extends ToolProvider<ToolPencilLine>
    {
        public Provider()
        {
            super("Pencil Line", "/icons/tools/pencilLine.png", "/manual/tools/pencilLine.html");
        }

        @Override
        public ToolPencilLine create(ToolUser user)
        {
            return new ToolPencilLine(user);
        }

        @Override
        public Component createToolSettingsEditor(RavenEditor editor)
        {
            return new ToolPencilLineSettings(editor);
        }
    }

}
