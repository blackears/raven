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

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.game.control.color.MultipleGradientStops.Style;
import com.kitfox.game.control.color.PaintLayout;
import com.kitfox.game.control.color.PaintLayoutLinear;
import com.kitfox.game.control.color.PaintLayoutRadial;
import com.kitfox.game.control.color.PaintLayoutTexture;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.node.scene.RavenNodePaint;
import com.kitfox.raven.editor.node.tools.ToolProvider;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.editor.node.tools.common.select.PaintLayoutManipulator;
import com.kitfox.raven.editor.node.tools.common.select.PaintLayoutManipulator.MaterialElement;
import com.kitfox.raven.editor.node.tools.common.select.PaintLayoutManipulatorHandle;
import com.kitfox.raven.editor.node.tools.common.select.PaintLayoutManipulatorLinear;
import com.kitfox.raven.editor.node.tools.common.select.PaintLayoutManipulatorRadial;
import com.kitfox.raven.editor.node.tools.common.select.PaintLayoutManipulatorTexture;
import com.kitfox.raven.editor.paint.RavenPaint;
import com.kitfox.raven.editor.paint.RavenPaintGradient;
import com.kitfox.raven.shape.bezier.BezierNetwork;
import com.kitfox.raven.util.Intersection;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.SelectionRecord;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Properties;

/**
 * NOTE: works on faces and edges, not entire paths or meshes
 *
 * @author kitfox
 */
public class ToolPaintLayout extends ToolDisplay
{
    ToolPaintLayout.Provider toolProvider;

    PaintLayoutManipulator manip;
    PaintLayoutManipulatorHandle manipHandle;

    protected ToolPaintLayout(ToolUser user, ToolPaintLayout.Provider toolProvider)
    {
        super(user);
        this.toolProvider = toolProvider;

        setEnableRestrictAxis(false);

        buildManipulator();
    }

    @Override
    protected void click(MouseEvent evt)
    {
        //Single click selects shape underneath it
        ServiceDocument provDoc = user.getToolService(ServiceDocument.class);
        if (provDoc == null)
        {
            return;
        }

        ServiceDeviceCamera provDevice = user.getToolService(ServiceDeviceCamera.class);
        if (provDevice == null)
        {
            return;
        }
        CyMatrix4d xform = provDevice.getWorldToDeviceTransform((CyMatrix4d)null);

        Selection.Type selType = getSelectType(evt);
        NodeObject pickObj = provDoc.pickObject(
                new CyRectangle2d(evt.getX(), evt.getY(), 1, 1),
                xform, Intersection.INTERSECTS);

        Selection<SelectionRecord> sel = provDoc.getSelection();

        if (pickObj == null)
        {
            if (selType == Selection.Type.REPLACE)
            {
                sel.clear();
            }
            fireToolDisplayChanged();
            return;
        }

        SelectionRecord rec = new SelectionRecord(pickObj);
        sel.select(selType, rec);

        buildManipulator();
        fireToolDisplayChanged();
    }

    @Override
    protected void startDrag(MouseEvent evt)
    {
        if (manip != null)
        {
            CyMatrix4d w2d =
                    getWorldToDeviceTransform((CyMatrix4d)null);
//            CyMatrix4d l2w = manip.getLocalToWorldTransform(null);
//            CyMatrix4d l2d = new CyMatrix4d(w2d);
//            l2d.mul(l2w);

            manipHandle = manip.getManipulatorHandle(evt, w2d);
        }
    }

    @Override
    protected void dragTo(MouseEvent evt)
    {
        if (manipHandle != null)
        {
            //Update manipulation
            manipHandle.dragTo(evt.getX(), evt.getY(), manip, false);
        }
        fireToolDisplayChanged();
    }

    @Override
    protected void endDrag(MouseEvent evt)
    {
        if (manipHandle != null)
        {
            //Finish manipulation
            manipHandle.dragTo(evt.getX(), evt.getY(), manip, true);

            manipHandle = null;
            manip.rebuild();
        }

        fireToolDisplayChanged();
    }

    @Override
    public void cancel()
    {
        fireToolDisplayChanged();
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void paint(Graphics2D g)
    {
        super.paint(g);

        if (manip != null)
        {
            CyMatrix4d w2d =
                    getWorldToDeviceTransform((CyMatrix4d)null);
//            CyMatrix4d l2w = manip.getLocalToWorldTransform(null);
//            CyMatrix4d l2d = new CyMatrix4d(w2d);
//            l2d.mul(l2w);

            manip.paint(g, w2d);
        }
    }

    private ArrayList<MaterialElement> buildMaterialList(ServiceDocument provider)
    {
        ArrayList<MaterialElement> list = new ArrayList<MaterialElement>();
        Selection<SelectionRecord> sel = provider.getSelection();
        for (SelectionRecord rec: sel.getSelection())
        {
            NodeObject node = rec.getNode();
            ServiceMaterial mat = node.getNodeService(ServiceMaterial.class, false);
            if (mat == null)
            {
                continue;
            }

            BezierNetwork.Subselection subsel =
                    sel.getSubselection(rec, BezierNetwork.Subselection.class);

            if (toolProvider.isStrokeMode())
            {
                CyRectangle2d bounds = mat.getMaterialEdgeBounds(null);
                if (bounds != null)
                {
                    list.add(new MaterialElement(mat, null, bounds));
                }

                if (subsel != null)
                {
                    for (Integer val: subsel.getEdgeUids())
                    {
                        bounds = mat.getMaterialEdgeBounds(val);
                        if (bounds != null)
                        {
                            list.add(new MaterialElement(mat, val, bounds));
                        }
                    }
                }
            }
            else
            {
                CyRectangle2d bounds = mat.getMaterialFaceBounds(null);
                if (bounds != null)
                {
                    list.add(new MaterialElement(mat, null, bounds));
                }

                if (subsel != null)
                {
                    for (Integer val: subsel.getFaceUids())
                    {
                        bounds = mat.getMaterialFaceBounds(val);
                        if (bounds != null)
                        {
                            list.add(new MaterialElement(mat, val, bounds));
                        }
                    }
                }
            }
        }

        return list;
    }

    private CyRectangle2d getCombinedBoundsWorld(ArrayList<MaterialElement> list)
    {
        //Find bounds of components
        CyRectangle2d bounds =
                new CyRectangle2d(list.get(0).getBoundsWorld());
        
        for (int i = 1; i < list.size(); ++i)
        {
            bounds.union(list.get(i).getBoundsWorld());
        }

        return bounds;
    }

    private void buildManipulator()
    {
        manip = null;
        
        ServiceDocument provider = user.getToolService(ServiceDocument.class);
        if (provider == null)
        {
            return;
        }

        ArrayList<MaterialElement> eleList = buildMaterialList(provider);
        if (eleList.isEmpty())
        {
            return;
        }

        //Use first component to init manipulator
        MaterialElement ele = eleList.get(0);
//        PlaneDataProvider prov;

        RavenPaint paint;
        PaintLayout layout;
        if (toolProvider.isStrokeMode())
        {
            paint = ele.getEdgePaint();
            layout = ele.getEdgeLayoutWorld();

//            CyMatrix4d l2w = ele.getNode().getLocalToWorldTransform(null);
//            layout = layout.transform(l2w);
        }
        else
        {
            paint = ele.getFacePaint();
            layout = ele.getFaceLayoutWorld();

//            CyMatrix4d l2w = ele.getNode().getLocalToWorldTransform(null);
//            layout = layout.transform(l2w);
        }

        if (layout == null)
        {
            CyRectangle2d bounds = getCombinedBoundsWorld(eleList);
            layout = new PaintLayoutTexture(bounds);
        }


        //Force the layout to be the appropriate type for the current
        // paint
        if (paint instanceof RavenNodePaint)
        {
            paint = ((RavenNodePaint)paint).createPaint();
        }

//        if (paint instanceof RavenPaintGradient)
//        {
//            Style style = ((RavenPaintGradient)paint).getGradient().getStops().getStyle();
//            if (style == Style.LINEAR)
//            {
//                manip = new PaintLayoutManipulatorLinear(eleList,
//                        toolProvider.isStrokeMode(),
//                        PaintLayoutLinear.create(layout));
//            }
//            else
//            {
//                manip = new PaintLayoutManipulatorRadial(eleList,
//                        toolProvider.isStrokeMode(),
//                        PaintLayoutRadial.create(layout));
//            }
//        }
//        else
//        {
            manip = new PaintLayoutManipulatorTexture(eleList,
                    toolProvider.isStrokeMode(),
                    PaintLayoutTexture.create(layout));
//        }
    }

    //---------------------------------------


    
//    static enum ManipType
//    {
//        LINEAR, RADIAL, TEXTURE
//    }


    @ServiceInst(service=ToolProvider.class)
    static public class Provider extends ToolProvider<ToolPaintLayout>
    {
        public static final String PROP_STROKEMODE = "strokeMode";
        private boolean strokeMode;

        public Provider()
        {
            super("Paint Layout", "/icons/tools/paintLayout.png", "/manual/tools/paintLayout.html");
        }

        @Override
        public void loadPreferences(Properties properties)
        {
            super.loadPreferences(properties);

            strokeMode = Boolean.parseBoolean(properties.getProperty(PROP_STROKEMODE));
        }

        @Override
        public Properties savePreferences()
        {
            Properties prop = new Properties();
            prop.setProperty(PROP_STROKEMODE, "" + strokeMode);
            return prop;
        }

        @Override
        public ToolPaintLayout create(ToolUser user)
        {
            return new ToolPaintLayout(user, this);
        }

        @Override
        public Component createToolSettingsEditor(RavenEditor editor)
        {
            return new ToolPaintLayoutSettings(editor, this);
        }

        /**
         * @return the strokeMode
         */
        public boolean isStrokeMode()
        {
            return strokeMode;
        }

        /**
         * @param strokeMode the strokeMode to set
         */
        public void setStrokeMode(boolean strokeMode)
        {
            this.strokeMode = strokeMode;
        }
    }

}
