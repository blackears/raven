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

import com.kitfox.raven.util.tree.FrameKey;
import com.kitfox.coyote.drawRecord.CyDrawRecordViewport;
import com.kitfox.coyote.material.color.CyMaterialColorDrawRecord;
import com.kitfox.coyote.material.color.CyMaterialColorDrawRecordFactory;
import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyRendererUtil2D;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.coyote.shape.ShapeLinesProvider;
import com.kitfox.raven.editor.node.scene.wizard.RavenNodeRootWizard;
import com.kitfox.raven.editor.node.tools.common.ServiceBackground;
import com.kitfox.raven.editor.node.tools.common.ServiceColors2D;
import com.kitfox.raven.util.Intersection;
import com.kitfox.raven.editor.node.tools.common.ServiceDeviceCamera;
import com.kitfox.raven.editor.node.tools.common.ServiceDocument;
import com.kitfox.raven.editor.node.tools.common.ServiceText;
import com.kitfox.raven.editor.view.displayCy.CyRenderService;
import com.kitfox.raven.paint.RavenPaint;
import com.kitfox.raven.paint.RavenStroke;
import com.kitfox.raven.paint.common.RavenPaintColor;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.text.Justify;
import com.kitfox.raven.util.tree.*;
import com.kitfox.raven.wizard.RavenWizardPageIterator;
import com.kitfox.xml.schema.ravendocumentschema.NodeSymbolType;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author kitfox
 */
public class RavenNodeRoot extends NodeDocument
        implements ServiceDeviceCamera, ServiceDocument,
        ServiceText, ServiceColors2D, ServiceBackground,
        CyRenderService
{
    public static final String PROP_BACKGROUND = "background";
    public final PropertyWrapper<RavenNodeRoot, RavenPaintColor> background =
            new PropertyWrapper(
            this, PROP_BACKGROUND, RavenPaintColor.class,
            new RavenPaintColor(CyColor4f.WHITE));

    public static final String PROP_ANTIALIASED = "antialiased";
    public final PropertyWrapperBoolean<RavenNodeRoot> antialiased =
            new PropertyWrapperBoolean(this, PROP_ANTIALIASED, false);

    public static final String PROP_FILLPAINT = "fillPaint";
    public final PropertyWrapper<RavenNodeRoot, RavenPaint> fillPaint =
            new PropertyWrapper(
            this, PROP_FILLPAINT, PropertyWrapper.FLAGS_NOANIM, 
            RavenPaint.class, new RavenPaintColor(CyColor4f.BLACK));

    public static final String PROP_STROKEPAINT = "strokePaint";
    public final PropertyWrapper<RavenNodeRoot, RavenPaint> strokePaint =
            new PropertyWrapper(
            this, PROP_STROKEPAINT, PropertyWrapper.FLAGS_NOANIM, 
            RavenPaint.class, new RavenPaintColor(CyColor4f.BLACK));

    public static final String PROP_STROKESTYLE = "strokeStyle";
    public final PropertyWrapper<RavenNodeRoot, RavenStroke> strokeShape =
            new PropertyWrapper(
            this, PROP_STROKESTYLE, PropertyWrapper.FLAGS_NOANIM, 
            RavenStroke.class, new RavenStroke());

    public static final String PROP_FONT = "font";
    public final PropertyWrapper<RavenNodeRoot, Font> font =
            new PropertyWrapper(
            this, PROP_FONT, PropertyWrapper.FLAGS_NOANIM, 
            Font.class, new Font(Font.SERIF, Font.PLAIN, 12));

    public static final String PROP_JUSTIFY = "justify";
    public final PropertyWrapper<RavenNodeRoot, Justify> justify =
            new PropertyWrapper(
            this, PROP_JUSTIFY, PropertyWrapper.FLAGS_NOANIM, 
            Justify.class, Justify.LEFT);

    //Navigation
    public static final String PROP_PANX = "panX";
    public final PropertyWrapperFloat<RavenNodeRoot> panX =
            new PropertyWrapperFloat(this, PROP_PANX,
            PropertyWrapper.FLAGS_NOANIM, 0);

    public static final String PROP_PANY = "panY";
    public final PropertyWrapperFloat<RavenNodeRoot> panY =
            new PropertyWrapperFloat(this, PROP_PANY,
            PropertyWrapper.FLAGS_NOANIM, 0);

    public static final String PROP_ROTATE = "rotate";
    public final PropertyWrapperFloat<RavenNodeRoot> rotate =
            new PropertyWrapperFloat(this, PROP_ROTATE,
            PropertyWrapper.FLAGS_NOANIM, 0);

    public static final String PROP_ZOOM = "zoom";
    public final PropertyWrapperFloat<RavenNodeRoot> zoom =
            new PropertyWrapperFloat(this, PROP_ZOOM,
            PropertyWrapper.FLAGS_NOANIM, 1);

    //Snapping
    public static final String PROP_SNAP_VERTEX = "snapVertex";
    public final PropertyWrapperBoolean<RavenNodeRoot> snapVertex =
            new PropertyWrapperBoolean(this, PROP_SNAP_VERTEX, 
            PropertyWrapper.FLAGS_NOANIM, true);

    public static final String PROP_SNAP_GRID = "snapGrid";
    public final PropertyWrapperBoolean<RavenNodeRoot> snapGrid =
            new PropertyWrapperBoolean(this, PROP_SNAP_GRID, 
            PropertyWrapper.FLAGS_NOANIM, false);

    //Grid
    public static final String PROP_GRID_SHOW = "gridShow";
    public final PropertyWrapperBoolean<RavenNodeRoot> gridShow =
            new PropertyWrapperBoolean(this, PROP_GRID_SHOW, 
            PropertyWrapper.FLAGS_NOANIM, false);

    public static final String PROP_GRID_SPACING_MAJ = "gridSpacingMajor";
    public final PropertyWrapperFloat<RavenNodeRoot> gridSpacingMaj =
            new PropertyWrapperFloat(this, PROP_GRID_SPACING_MAJ, 
            PropertyWrapper.FLAGS_NOANIM, 100);

    public static final String PROP_GRID_SPACING_MIN = "gridSpacingMinor";
    public final PropertyWrapperFloat<RavenNodeRoot> gridSpacingMin =
            new PropertyWrapperFloat(this, PROP_GRID_SPACING_MIN, 
            PropertyWrapper.FLAGS_NOANIM, 10);

    public static final String PROP_GRID_SPACING_OFFX = "gridSpacingOffX";
    public final PropertyWrapperFloat<RavenNodeRoot> gridSpacingOffX =
            new PropertyWrapperFloat(this, PROP_GRID_SPACING_OFFX, 
            PropertyWrapper.FLAGS_NOANIM, 0);

    public static final String PROP_GRID_SPACING_OFFY = "gridSpacingOffY";
    public final PropertyWrapperFloat<RavenNodeRoot> gridSpacingOffY =
            new PropertyWrapperFloat(this, PROP_GRID_SPACING_OFFY, 
            PropertyWrapper.FLAGS_NOANIM, 0);

    public static final String PROP_GRID_COLOR = "gridColor";
    public final PropertyWrapper<RavenNodeRoot, RavenPaintColor> gridColor =
            new PropertyWrapper(this, PROP_GRID_COLOR, 
            PropertyWrapper.FLAGS_NOANIM, 
            RavenPaintColor.class, RavenPaintColor.LIGHT_GREY);
    
    //Graph
    public static final String PROP_GRAPH_RADIUS_PICK = "graphRadiusPick";
    public final PropertyWrapperFloat<RavenNodeRoot> graphRadiusPick =
            new PropertyWrapperFloat(this, PROP_GRAPH_RADIUS_PICK, 
            PropertyWrapper.FLAGS_NOANIM, 3);

    public static final String PROP_GRAPH_RADIUS_DISPLAY = "graphRadiusDisplay";
    public final PropertyWrapperFloat<RavenNodeRoot> graphRadiusDisplay =
            new PropertyWrapperFloat(this, PROP_GRAPH_RADIUS_DISPLAY, 
            PropertyWrapper.FLAGS_NOANIM, 2);

    public static final String PROP_GRAPH_COLOR_EDGE = "graphColorEdge";
    public final PropertyWrapper<RavenNodeRoot, RavenPaintColor> graphColorEdge =
            new PropertyWrapper(this, PROP_GRAPH_COLOR_EDGE, 
            PropertyWrapper.FLAGS_NOANIM, 
            RavenPaintColor.class, RavenPaintColor.BLUE);

    public static final String PROP_GRAPH_COLOR_EDGE_SELECT = "graphColorEdgeSelect";
    public final PropertyWrapper<RavenNodeRoot, RavenPaintColor> graphColorEdgeSelect =
            new PropertyWrapper(this, PROP_GRAPH_COLOR_EDGE_SELECT, 
            PropertyWrapper.FLAGS_NOANIM, 
            RavenPaintColor.class, RavenPaintColor.RED);

    public static final String PROP_GRAPH_COLOR_VERT = "graphColorVert";
    public final PropertyWrapper<RavenNodeRoot, RavenPaintColor> graphColorVert =
            new PropertyWrapper(this, PROP_GRAPH_COLOR_VERT, 
            PropertyWrapper.FLAGS_NOANIM, 
            RavenPaintColor.class, RavenPaintColor.WHITE);

    public static final String PROP_GRAPH_COLOR_VERT_SELECT = "graphColorVertSelect";
    public final PropertyWrapper<RavenNodeRoot, RavenPaintColor> graphColorVertSelect =
            new PropertyWrapper(this, PROP_GRAPH_COLOR_VERT_SELECT, 
            PropertyWrapper.FLAGS_NOANIM, 
            RavenPaintColor.class, RavenPaintColor.GREEN);
    
    
    
//    public static final String PROP_SNAPPING = "snapping";
//    public final PropertyWrapper<RavenNodeRoot, Snapping> snapping =
//            new PropertyWrapper(this, PROP_SNAPPING, Snapping.class, new Snapping());
//
//    public static final String PROP_GRAPHDISPLAY = "graphDisplay";
//    public final PropertyWrapper<RavenNodeRoot, GraphLayout> graphDisplay =
//            new PropertyWrapper(this, PROP_GRAPHDISPLAY, GraphLayout.class, new GraphLayout());

//    public static final String PROP_CAMERA = "camera";
//    public final PropertyWrapper<RavenNodeRoot, RavenNodeCamera> camera =
//            new PropertyWrapper(
//            this, PROP_CAMERA, RavenNodeCamera.class);


    public static final String CHILD_PAINTLIBRARY = "paintLibrary";
    public final ChildWrapperSingle<RavenNodeRoot, RavenNodePaintLibrary> paintLibrary =
            new ChildWrapperSingle(
            this, CHILD_PAINTLIBRARY, RavenNodePaintLibrary.class);

    public static final String CHILD_STROKELIBRARY = "strokeLibrary";
    public final ChildWrapperSingle<RavenNodeRoot, RavenNodeStrokeLibrary> strokeLibrary =
            new ChildWrapperSingle(
            this, CHILD_STROKELIBRARY, RavenNodeStrokeLibrary.class);

    public static final String CHILD_SCENEGRAPH = "sceneGraph";
    public final ChildWrapperList<RavenNodeRoot, RavenNodeXformable> sceneGraph =
            new ChildWrapperList(
            this, CHILD_SCENEGRAPH, RavenNodeXformable.class);

    AffineTransform viewXform = new AffineTransform();

    protected RavenNodeRoot()
    {
        ViewUpdater update = new ViewUpdater();
        panX.addPropertyWrapperListener(update);
        panY.addPropertyWrapperListener(update);
        rotate.addPropertyWrapperListener(update);
        zoom.addPropertyWrapperListener(update);
    }

    public static RavenNodeRoot create(NodeSymbolType nodeDocumentType)
    {
        RavenNodeRoot root = new RavenNodeRoot();
        root.load(nodeDocumentType);
        return root;
    }

    @Override
    public String getTooltipText()
    {
        return "root";
    }

    @Override
    public void renderEditor(RenderContext ctx)
    {
        CyDrawStack rend = ctx.getDrawStack();

        CyMatrix4d proj = new CyMatrix4d();
        proj.gluOrtho2D(0, rend.getDeviceWidth(), rend.getDeviceHeight(), 0);
        rend.setProjXform(proj);

        FrameKey frame = ctx.getFrame();
        
//        ColorStyle col = background.getValue().getColor();
        RavenPaintColor rcol = background.getValue(frame);
        //ColorStyle col = rcol.getColor();
        CyRendererUtil2D.clear(rend, rcol.r, rcol.g, rcol.b, rcol.a);

        CyMatrix4d view = new CyMatrix4d(viewXform);
        rend.setViewXform(view);

        for (int i = 0; i < sceneGraph.size(); ++i)
        {
            sceneGraph.get(i).render(ctx);
        }

        //Draw grid
        if (gridShow.getValue())
        {
            float spaceMin = gridSpacingMin.getValue();
            float spaceMaj = gridSpacingMaj.getValue();
            float offX = gridSpacingOffX.getValue();
            float offY = gridSpacingOffY.getValue();
            drawGrid(ctx, spaceMin, offX, offY);
            
        }
    }

    private void drawGrid(RenderContext ctx, float spacing, float offX, float offY)
    {
        if (spacing * zoom.getValueNumeric() < 4)
        {
            //Don't draw if grid resolution under 4 pixels per cell
            return;
        }
        
        RavenPaintColor col = gridColor.getValue();

        CyMatrix4d g2w = CyMatrix4d.createIdentity();
        g2w.scale(spacing, spacing, 1);
        g2w.translate(offX, offY, 0);
        
        CyMatrix4d w2d = new CyMatrix4d();
        getWorldToDeviceTransform(w2d);

        CyDrawStack stack = ctx.getDrawStack();
        CyMatrix4d d2p = stack.getProjXform();
        
        CyMatrix4d g2p = new CyMatrix4d(d2p);
        g2p.mul(w2d);
        g2p.mul(g2w);

        CyMatrix4d p2g = new CyMatrix4d(g2p);
        p2g.invert();

//        CyRectangle2d boundsProj = new CyRectangle2d(0, 0, stack.getDeviceWidth(), stack.getDeviceHeight());
        CyRectangle2d boundsProj = new CyRectangle2d(-1, -1, 2, 2);
        CyRectangle2d boundsGrid = boundsProj.createTransformedBounds(p2g);

        int minX = (int)Math.floor(boundsGrid.getMinX());
        int maxX = (int)Math.ceil(boundsGrid.getMaxX());
        int minY = (int)Math.floor(boundsGrid.getMinY());
        int maxY = (int)Math.ceil(boundsGrid.getMaxY());
        
        CyPath2d gridLines = new CyPath2d();
        CyVector2d p0 = new CyVector2d();
        for (int i = minX; i <= maxX; ++i)
        {
            p0.set(i, minY);
            g2p.transformPoint(p0);
            gridLines.moveTo(p0.x, p0.y);
            
            p0.set(i, maxY);
            g2p.transformPoint(p0);
            gridLines.lineTo(p0.x, p0.y);
        }
        
        for (int i = minY; i <= maxY; ++i)
        {
            p0.set(minX, i);
            g2p.transformPoint(p0);
            gridLines.moveTo(p0.x, p0.y);
            
            p0.set(maxX, i);
            g2p.transformPoint(p0);
            gridLines.lineTo(p0.x, p0.y);
        }
        
        CyMaterialColorDrawRecord rec = 
                CyMaterialColorDrawRecordFactory.inst().allocRecord();
        rec.setColor(col.asColor());
        
        ShapeLinesProvider prov = new ShapeLinesProvider(gridLines);
        CyVertexBuffer lineMesh = new CyVertexBuffer(prov);
        rec.setMesh(lineMesh);
        
        rec.setMvpMatrix(CyMatrix4d.createIdentity());
        rec.setOpacity(1);
        
        ctx.getDrawStack().addDrawRecord(rec);
    }
    
    @Override
    public void renderCamerasAll(RenderContext ctx)
    {
        CyDrawStack rend = ctx.getDrawStack();
        FrameKey frame = ctx.getFrame();

        //Find cameras to draw
        ArrayList<RavenNodeCamera> cams = getNodes(RavenNodeCamera.class);

        class Comp implements Comparator<RavenNodeCamera>
        {
            @Override
            public int compare(RavenNodeCamera o1, RavenNodeCamera o2)
            {
                return o1.getCameraOrder() - o2.getCameraOrder();
            }
        }

        Collections.sort(cams, new Comp());

        RavenPaintColor col = background.getValue();
        CyRendererUtil2D.clear(rend, col.r, col.g, col.b, col.a);

        int devW = rend.getDeviceWidth();
        int devH = rend.getDeviceHeight();

        //Draw cameras
        CyMatrix4d mat = new CyMatrix4d();
//        AffineTransform l2w = new AffineTransform();
        for (int i = 0; i < cams.size(); ++i)
        {
            RavenNodeCamera camera = cams.get(i);
            if (!camera.isVisible(frame))
            {
                continue;
            }

            float opacity = camera.getOpacity(frame);
            rend.setOpacity(opacity);
            if (opacity == 0)
            {
                continue;
            }

            double vx = camera.getViewportX();
            double vy = camera.getViewportY();
            double vw = camera.getViewportWidth();
            double vh = camera.getViewportHeight();

            rend.addDrawRecord(new CyDrawRecordViewport(
                    (int)(vx * devW), (int)(vy * devH),
                    (int)(vw * devW), (int)(vh * devH)
                    ));

            double cw = camera.getWidth();
            double ch = camera.getHeight();
            mat.gluOrtho2D(-cw / 2, cw / 2, ch / 2, -ch / 2);
            rend.setProjXform(mat);

            camera.getLocalToWorldTransform(mat);
            mat.invert();
            rend.setViewXform(mat);

            //Render scene graph
            for (int j = 0; j < sceneGraph.size(); ++j)
            {
                sceneGraph.get(j).render(ctx);
            }
        }
    }

    @Override
    public int getNumCameras()
    {
        ArrayList<RavenNodeCamera> cams = getNodes(RavenNodeCamera.class);
        return cams.size();
    }

//    @Deprecated
//    @Override
//    public void render(RavenRenderer renderer)
//    {
//        RavenPaintColor color = background.getValue();
//        Color bgCol = color == null ? Color.GRAY : color.getColor().getColor();
//
//        renderer.clear(bgCol);
//        renderer.setAntialiased(antialiased.getValue());
//
////        renderer.mulTransform(viewXform);
//        renderer.setWorldToViewTransform(viewXform);
//
//        for (int i = 0; i < sceneGraph.size(); ++i)
//        {
//            RavenNodeXformable child = sceneGraph.get(i);
//            child.render(renderer);
//        }
//    }

    @Deprecated
    @Override
    public AffineTransform getWorldToDeviceTransform(AffineTransform xform)
    {
        if (xform == null)
        {
            xform = new AffineTransform();
        }

        float cPanX = panX.getValue();
        float cPanY = panY.getValue();
        float cRot = rotate.getValue();
        float cZoom = zoom.getValue();

        double sin = Math.sin(Math.toRadians(cRot));
        double cos = Math.cos(Math.toRadians(cRot));

        xform.setTransform(cos * cZoom, sin * cZoom,
                -sin * cZoom, cos * cZoom, cPanX, cPanY);
        return xform;
    }

    @Deprecated
    @Override
    public void setWorldToDeviceTransform(AffineTransform xform)
    {
        double m00 = xform.getScaleX();
        double m10 = xform.getShearY();
        double m01 = xform.getShearX();
        double m11 = xform.getScaleY();
        double m02 = xform.getTranslateX();
        double m12 = xform.getTranslateY();

        double xscale = Math.sqrt(m00 * m00 + m10 * m10);
        double xrot = Math.toDegrees(Math.atan2(m10, m00));

        panX.setValue((float)m02, false);
        panY.setValue((float)m12, false);
        rotate.setValue((float)xrot, false);
        zoom.setValue((float)xscale, false);
    }

    @Override
    public CyMatrix4d getWorldToDeviceTransform(CyMatrix4d xform)
    {
        if (xform == null)
        {
            xform = CyMatrix4d.createIdentity();
        }

        float cPanX = panX.getValue();
        float cPanY = panY.getValue();
        float cRot = rotate.getValue();
        float cZoom = zoom.getValue();

        double sin = Math.sin(Math.toRadians(cRot));
        double cos = Math.cos(Math.toRadians(cRot));

        xform.set(cos * cZoom, sin * cZoom, 0, 0,
                -sin * cZoom, cos * cZoom, 0, 0,
                0, 0, 1, 0,
                cPanX, cPanY, 0, 1);
        return xform;
    }

    @Override
    public void setWorldToDeviceTransform(CyMatrix4d xform)
    {
        double m00 = xform.m00;
        double m10 = xform.m10;
        double m01 = xform.m01;
        double m11 = xform.m11;
        double m02 = xform.m02;
        double m12 = xform.m12;

        double xscale = Math.sqrt(m00 * m00 + m10 * m10);
        double xrot = Math.toDegrees(Math.atan2(m10, m00));

        panX.setValue((float)m02, false);
        panY.setValue((float)m12, false);
        rotate.setValue((float)xrot, false);
        zoom.setValue((float)xscale, false);
    }

    public void resetView()
    {
        panX.setValue(0f, false);
        panY.setValue(0f, false);
        rotate.setValue(0f, false);
        zoom.setValue(1f, false);
    }

    @Override
    public NodeObject pickObject(CyRectangle2d rectangle, CyMatrix4d worldToPick, Intersection intersection)
    {
        for (int i = 0; i < sceneGraph.size(); ++i)
        {
            RavenNodeXformable child = sceneGraph.get(i);
            NodeObject node = child.pickObject(rectangle, worldToPick, intersection);
            if (node != null)
            {
                return node;
            }
        }
        return null;
    }

    @Override
    public void pickObjects(CyRectangle2d rectangle, CyMatrix4d worldToPick, Intersection intersection, ArrayList<NodeObject> pickList)
    {
        for (int i = 0; i < sceneGraph.size(); ++i)
        {
            RavenNodeXformable child = sceneGraph.get(i);
            child.pickObjects(rectangle, worldToPick, intersection, pickList);
        }
    }

    @Override
    public Justify getTextJustify()
    {
        return justify.getValue();
    }

    @Override
    public void setTextJustify(Justify justify)
    {
        this.justify.setValue(justify);
    }

    @Override
    public Font getTextFont()
    {
        return font.getValue();
    }

    @Override
    public void setTextFont(Font font)
    {
        this.font.setValue(font);
    }

    @Override
    public PropertyWrapper<RavenNodeRoot, RavenStroke> getStrokeStyleProp()
    {
        return strokeShape;
    }

    @Override
    public PropertyWrapper<RavenNodeRoot, RavenPaint> getStrokePaintProp()
    {
        return strokePaint;
    }

    @Override
    public PropertyWrapper<RavenNodeRoot, RavenPaint> getFillPaintProp()
    {
        return fillPaint;
    }

    @Deprecated
    @Override
    public RavenNodePaintLibrary getPaintLibrary()
    {
        return paintLibrary.getChild();
    }

    @Deprecated
    @Override
    public RavenNodeStrokeLibrary getStrokeLibrary()
    {
        return strokeLibrary.getChild();
    }

    @Override
    public RavenPaintColor getBackgroundColor()
    {
        return background.getValue();
    }

//    @Override
//    public Snapping getSnapping()
//    {
//        return snapping.getValue();
//    }
//    
//    @Override
//    public GraphLayout getGraphLayout()
//    {
//        return graphDisplay.getValue();
//    }

    public boolean isSnapGrid()
    {
        return snapGrid.getValue();
    }

    public boolean isSnapVertex()
    {
        return snapVertex.getValue();
    }

    public boolean isGridShow()
    {
        return gridShow.getValue();
    }
    
    public float getGridSpacingMajor()
    {
        return gridSpacingMaj.getValue();
    }
    
    public float getGridSpacingMinor()
    {
        return gridSpacingMin.getValue();
    }
    
    public float getGridSpacingOffsetX()
    {
        return gridSpacingOffX.getValue();
    }
    
    public float getGridSpacingOffsetY()
    {
        return gridSpacingOffY.getValue();
    }
    
    public RavenPaintColor getGridColor()
    {
        return gridColor.getValue();
    }
    
    
    public float getGraphRadiusDisplay()
    {
        return graphRadiusDisplay.getValue();
    }
    
    public float getGraphRadiusPick()
    {
        return graphRadiusPick.getValue();
    }
    
    public RavenPaintColor getGraphColorEdge()
    {
        return graphColorEdge.getValue();
    }
    
    public RavenPaintColor getGraphColorEdgeSelect()
    {
        return graphColorEdgeSelect.getValue();
    }
    
    public RavenPaintColor getGraphColorVert()
    {
        return graphColorVert.getValue();
    }
    
    public RavenPaintColor getGraphColorVertSelect()
    {
        return graphColorVertSelect.getValue();
    }
    
    public RavenPaint getFillPaint()
    {
        return fillPaint.getValue();
    }

    public RavenPaint getStrokePaint()
    {
        return strokePaint.getValue();
    }

    public RavenStroke getStrokeShape()
    {
        return strokeShape.getValue();
    }
    
    //-----------------------------------------------

    class ViewUpdater extends PropertyWrapperAdapter
    {
//        private AffineTransform xform = new AffineTransform();

        @Override
        public void propertyWrapperDataChanged(PropertyChangeEvent evt)
        {
            getWorldToDeviceTransform(viewXform);
            
//            getWorldToDeviceTransform(xform);
//
//            viewXform.setTransform(xform.m00, xform.m10,
//                    xform.m01, xform.m11,
//                    xform.m02, xform.m12);
        }
    }

    @ServiceInst(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<RavenNodeRoot>
    {
        public Provider()
        {
            super(RavenNodeRoot.class, "Root", "/icons/node/root.png");
        }

        @Override
        public RavenNodeRoot createNode(int uid)
        {
            return new RavenNodeRoot();
        }
    }

    @ServiceInst(service=NodeDocumentProvider.class)
    public static class DocProvider extends NodeDocumentProvider<RavenNodeRoot>
    {
        public DocProvider()
        {
            super(RavenNodeRoot.class, "Raven Animation 2D", "/icons/node/root.png");
        }

        @Override
        public NodeDocument loadDocument(NodeSymbolType docTree)
        {
            return create(docTree);
        }

        @Override
        public RavenWizardPageIterator<RavenNodeRoot> createDocumentWizard()
        {
            return new RavenNodeRootWizard();
        }
    }


}
