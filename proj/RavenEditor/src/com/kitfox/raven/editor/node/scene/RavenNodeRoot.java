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
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyRendererUtil2D;
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.game.control.color.ColorStyle;
import com.kitfox.raven.editor.node.renderer.RavenRenderer;
import com.kitfox.raven.editor.node.scene.wizard.RavenNodeRootWizard;
import com.kitfox.raven.editor.node.tools.common.ServiceBackground;
import com.kitfox.raven.editor.node.tools.common.ServiceColors2D;
import com.kitfox.raven.util.Intersection;
import com.kitfox.raven.editor.node.tools.common.ServiceDeviceCamera;
import com.kitfox.raven.editor.node.tools.common.ServiceDocument;
import com.kitfox.raven.editor.node.tools.common.ServiceRenderer2D;
import com.kitfox.raven.editor.node.tools.common.ServiceText;
import com.kitfox.raven.editor.paint.RavenPaint;
import com.kitfox.raven.editor.paint.RavenPaintColor;
import com.kitfox.raven.editor.stroke.RavenStroke;
import com.kitfox.raven.editor.stroke.RavenStrokeBasic;
import com.kitfox.raven.editor.view.displayCy.CyRenderService;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.text.Justify;
import com.kitfox.raven.util.tree.ChildWrapperList;
import com.kitfox.raven.util.tree.ChildWrapperSingle;
import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.util.tree.NodeDocumentProvider;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperAdapter;
import com.kitfox.raven.util.tree.PropertyWrapperBoolean;
import com.kitfox.raven.util.tree.PropertyWrapperFloat;
import com.kitfox.raven.wizard.RavenWizardPageIterator;
import com.kitfox.xml.schema.ravendocumentschema.NodeDocumentType;
import com.kitfox.xml.schema.ravendocumentschema.RavenDocumentType;
import java.awt.Color;
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
        ServiceRenderer2D, CyRenderService
{
    public static final String PROP_BACKGROUND = "background";
    public final PropertyWrapper<RavenNodeRoot, RavenPaintColor> background =
            new PropertyWrapper(
            this, PROP_BACKGROUND, RavenPaintColor.class,
            new RavenPaintColor(ColorStyle.WHITE));

    public static final String PROP_ANTIALIASED = "antialiased";
    public final PropertyWrapperBoolean<RavenNodeRoot> antialiased =
            new PropertyWrapperBoolean(this, PROP_ANTIALIASED, false);

    public static final String PROP_FILLPAINT = "fillPaint";
    public final PropertyWrapper<RavenNodeRoot, RavenPaint> fillPaint =
            new PropertyWrapper(
            this, PROP_FILLPAINT, RavenPaint.class,
            new RavenPaintColor(ColorStyle.BLACK));

    public static final String PROP_STROKEPAINT = "strokePaint";
    public final PropertyWrapper<RavenNodeRoot, RavenPaint> strokePaint =
            new PropertyWrapper(
            this, PROP_STROKEPAINT, RavenPaint.class,
            new RavenPaintColor(ColorStyle.BLACK));

    public static final String PROP_STROKESTYLE = "strokeStyle";
    public final PropertyWrapper<RavenNodeRoot, RavenStroke> strokeStyle =
            new PropertyWrapper(
            this, PROP_STROKESTYLE, RavenStroke.class,
            new RavenStrokeBasic());

    public static final String PROP_FONT = "font";
    public final PropertyWrapper<RavenNodeRoot, Font> font =
            new PropertyWrapper(
            this, PROP_FONT, Font.class,
            new Font(Font.SERIF, Font.PLAIN, 12));

    public static final String PROP_JUSTIFY = "justify";
    public final PropertyWrapper<RavenNodeRoot, Justify> justify =
            new PropertyWrapper(
            this, PROP_JUSTIFY, Justify.class,
            Justify.LEFT);

    public static final String PROP_PANX = "panX";
    public final PropertyWrapperFloat<RavenNodeRoot> panX =
            new PropertyWrapperFloat(this, PROP_PANX, 0);

    public static final String PROP_PANY = "panY";
    public final PropertyWrapperFloat<RavenNodeRoot> panY =
            new PropertyWrapperFloat(this, PROP_PANY, 0);

    public static final String PROP_ROTATE = "rotate";
    public final PropertyWrapperFloat<RavenNodeRoot> rotate =
            new PropertyWrapperFloat(this, PROP_ROTATE, 0);

    public static final String PROP_ZOOM = "zoom";
    public final PropertyWrapperFloat<RavenNodeRoot> zoom =
            new PropertyWrapperFloat(this, PROP_ZOOM, 1);

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

    public static RavenNodeRoot create(NodeDocumentType nodeDocumentType)
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

        ColorStyle col = background.getValue().getColor();
        CyRendererUtil2D.clear(rend, col.r, col.g, col.b, col.a);

        CyMatrix4d view = new CyMatrix4d(viewXform);
        rend.setViewXform(view);

        for (int i = 0; i < sceneGraph.size(); ++i)
        {
            sceneGraph.get(i).render(ctx);
        }

    }

    @Override
    public void renderCamerasAll(RenderContext ctx)
    {
        CyDrawStack rend = ctx.getDrawStack();

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

        ColorStyle col = background.getValue().getColor();
        CyRendererUtil2D.clear(rend, col.r, col.g, col.b, col.a);

        int devW = rend.getDeviceWidth();
        int devH = rend.getDeviceHeight();

        //Draw cameras
        CyMatrix4d mat = new CyMatrix4d();
//        AffineTransform l2w = new AffineTransform();
        for (int i = 0; i < cams.size(); ++i)
        {
            RavenNodeCamera camera = cams.get(i);
            if (!camera.isVisible())
            {
                continue;
            }

            float opacity = camera.getOpacity();
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

    @Deprecated
    @Override
    public void render(RavenRenderer renderer)
    {
        RavenPaintColor color = background.getValue();
        Color bgCol = color == null ? Color.GRAY : color.getColor().getColor();

        renderer.clear(bgCol);
        renderer.setAntialiased(antialiased.getValue());

//        renderer.mulTransform(viewXform);
        renderer.setWorldToViewTransform(viewXform);

        for (int i = 0; i < sceneGraph.size(); ++i)
        {
            RavenNodeXformable child = sceneGraph.get(i);
            child.render(renderer);
        }
    }

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
        return strokeStyle;
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

    @Override
    public RavenNodePaintLibrary getPaintLibrary()
    {
        return paintLibrary.getChild();
    }

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
        public NodeDocument loadDocument(RavenDocumentType docTree)
        {
            return create(docTree.getRoot());
        }

        @Override
        public RavenWizardPageIterator<RavenNodeRoot> createDocumentWizard()
        {
            return new RavenNodeRootWizard();
        }
    }


}
