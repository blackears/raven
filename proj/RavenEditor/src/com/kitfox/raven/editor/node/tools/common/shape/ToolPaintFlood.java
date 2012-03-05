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

package com.kitfox.raven.editor.node.tools.common.shape;

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.node.scene.RavenNodeRoot;
import com.kitfox.raven.editor.node.scene.RavenNodeXformable;
import com.kitfox.raven.editor.node.scene.snap.GraphLayout;
import com.kitfox.raven.editor.node.tools.ToolProvider;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.editor.node.tools.common.ToolDisplay;
import com.kitfox.raven.paint.RavenPaint;
import com.kitfox.raven.paint.RavenStroke;
import com.kitfox.raven.shape.network.pick.NetworkHandleEdge;
import com.kitfox.raven.shape.network.pick.NetworkHandleFace;
import com.kitfox.raven.shape.network.pick.NetworkHandleSelection;
import com.kitfox.raven.util.Intersection;
import com.kitfox.raven.util.PropertiesData;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.undo.History;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;

/**
 *
 * @author kitfox
 */
public class ToolPaintFlood extends ToolDisplay
{
    ToolPaintFlood.Provider toolProvider;

    protected ToolPaintFlood(ToolUser user, ToolPaintFlood.Provider toolProvider)
    {
        super(user);
        this.toolProvider = toolProvider;
    }

    @Override
    protected void click(MouseEvent evt)
    {
        ArrayList<RavenNodeXformable> nodes = getNodes(true);
        for (RavenNodeXformable node: nodes)
        {
            ServiceShapeManip servShape = 
                    node.getNodeService(ServiceShapeManip.class, false);
            if (servShape != null)
            {
                if (doFill(node, servShape, evt))
                {
                    return;
                }
            }
        }
    }
     
    private boolean doFill(RavenNodeXformable node, ServiceShapeManip servShape, MouseEvent evt)
    {           
//        ServiceShapeManip servShape = user.getToolService(ServiceShapeManip.class);
//        if (servShape == null)
//        {
//            return;
//        }
        CyMatrix4d l2w = node.getLocalToWorldTransform((CyMatrix4d)null);
        CyMatrix4d l2d = getWorldToDevice(null);
        l2d.mul(l2w);
        
        RavenNodeRoot doc = getDocument();
        GraphLayout graphLayout = doc.getGraphLayout();
        int pickRad = graphLayout == null ? 1 : graphLayout.getPointRadiusPick();
        
        CyRectangle2d region = new CyRectangle2d(evt.getX() - pickRad, evt.getY() - pickRad, 
                pickRad * 2, pickRad * 2);

        ArrayList<NetworkHandleEdge> edges = 
                servShape.pickEdges(region, l2d, Intersection.INTERSECTS);
        
        if (!edges.isEmpty())
        {
            boolean connectedEdges = evt.getClickCount() > 1;
            floodEdges(edges, connectedEdges, servShape);
            return true;
        }

        ArrayList<NetworkHandleFace> faces = 
                servShape.pickFaces(region, l2d, Intersection.INTERSECTS);
        
        if (!faces.isEmpty())
        {
            floodFaces(faces, servShape);
            return true;
        }
        
        return false;
    }

    @Override
    protected void startDrag(MouseEvent evt)
    {
    }

    @Override
    protected void dragTo(MouseEvent evt)
    {
    }

    @Override
    protected void endDrag(MouseEvent evt)
    {
    }

    @Override
    public void cancel()
    {
    }

    @Override
    public void dispose()
    {
    }

    private void floodEdges(ArrayList<NetworkHandleEdge> edges, 
            boolean connectedEdges, ServiceShapeManip servShape)
    {
        HashSet<NetworkHandleEdge> floodSet = new HashSet<NetworkHandleEdge>();
        Selection<NodeObject> sel = getSelection();

        if (connectedEdges)
        {
            for (NetworkHandleEdge curEdge: edges)
            {
                ArrayList<NetworkHandleEdge> conn 
                        = servShape.getConnectedEdges(curEdge);
                floodSet.addAll(conn);
            }
        }
        else
        {
            floodSet.addAll(edges);
        }
        
//        for (NetworkHandleEdge curEdge: edges)
//        {
//            if (connectedEdges)
//            {
//                ArrayList<NetworkHandleEdge> conn 
//                        = servShape.getConnectedEdges(curEdge);
//                floodList.addAll(conn);
//            }
//            else
//            {
//                floodList.add(curEdge);
//            }
//        }
        
        //Include selection
        for (int i = 0; i < sel.size(); ++i)
        {
            NodeObject node = sel.get(i);
            NetworkHandleSelection subSel = 
                    sel.getSubselection(node, NetworkHandleSelection.class);
            if (subSel == null)
            {
                continue;
            }

            for (NetworkHandleEdge curEdge: edges)
            {
                if (subSel.containsEdge(curEdge))
                {
                    floodSet.addAll(subSel.getEdges());
                }
            }
        }
        
        //Flood
        RavenNodeRoot doc = getDocument();
//        History hist = doc.getHistory();
        
        if (toolProvider.isStrokePaint() && toolProvider.isStrokeShape())
        {
            RavenPaint paint = doc.getStrokePaint();
            RavenStroke stroke = doc.getStrokeShape();
            servShape.setEdgePaintAndStroke(paint, stroke, floodSet, true);
//            hist.beginTransaction("Flood edge");
        }
        else if (toolProvider.isStrokePaint())
        {
            RavenPaint paint = doc.getStrokePaint();
            servShape.setEdgePaint(paint, floodSet, true);
        }
        else if (toolProvider.isStrokeShape())
        {
            RavenStroke stroke = doc.getStrokeShape();
            servShape.setEdgeStroke(stroke, floodSet, true);
        }

//        if (updateMultiple)
//        {
//            hist.commitTransaction();
//        }
    }

    private void floodFaces(ArrayList<NetworkHandleFace> faces, 
            ServiceShapeManip servShape)
    {
        HashSet<NetworkHandleFace> floodList
                = new HashSet<NetworkHandleFace>(faces);
        Selection<NodeObject> sel = getSelection();
        
        //Include selection
        for (int i = 0; i < sel.size(); ++i)
        {
            NodeObject node = sel.get(i);
            NetworkHandleSelection subSel = 
                    sel.getSubselection(node, NetworkHandleSelection.class);
            if (subSel == null)
            {
                continue;
            }

            for (NetworkHandleFace curFace: faces)
            {
                if (subSel.containsFace(curFace))
                {
                    floodList.addAll(subSel.getFaces());
                }
            }
        }
        
        //Flood
        RavenNodeRoot doc = getDocument();
        if (toolProvider.isFacePaint())
        {
            RavenPaint paint = doc.getFillPaint();
            servShape.setFacePaint(paint, faces, true);
        }
    }
    
    
    //---------------------------------------

    @ServiceInst(service=ToolProvider.class)
    static public class Provider extends ToolProvider<ToolPaintFlood>
    {
        public static final String PROP_FACEPAINT = "facePaint";
        private boolean facePaint;
        public static final String PROP_STROKEPAINT = "strokePaint";
        private boolean strokePaint;
        public static final String PROP_STROKESHAPE = "strokeShape";
        private boolean strokeShape;

        public Provider()
        {
            super("Paint Flood", "/icons/tools/paintFlood.png", "/manual/tools/paintFlood.html");
        }

        @Override
        public void loadPreferences(Properties properties)
        {
            super.loadPreferences(properties);

            PropertiesData prop = new PropertiesData(properties);
            
            facePaint = prop.getBoolean(PROP_FACEPAINT, true);
            strokePaint = prop.getBoolean(PROP_STROKEPAINT, true);
            strokeShape = prop.getBoolean(PROP_STROKESHAPE, true);
        }

        @Override
        public Properties savePreferences()
        {
            Properties properties = new Properties();
            PropertiesData prop = new PropertiesData(properties);
            
            prop.setBoolean(PROP_FACEPAINT, facePaint);
            prop.setBoolean(PROP_STROKEPAINT, strokePaint);
            prop.setBoolean(PROP_STROKESHAPE, strokeShape);
            
            return properties;
        }

        @Override
        public ToolPaintFlood create(ToolUser user)
        {
            return new ToolPaintFlood(user, this);
        }

        @Override
        public Component createToolSettingsEditor(RavenEditor editor)
        {
            return new ToolPaintFloodSettings(editor, this);
        }

        /**
         * @return the facePaint
         */
        public boolean isFacePaint()
        {
            return facePaint;
        }

        /**
         * @param facePaint the facePaint to set
         */
        public void setFacePaint(boolean facePaint)
        {
            this.facePaint = facePaint;
        }

        /**
         * @return the strokePaint
         */
        public boolean isStrokePaint()
        {
            return strokePaint;
        }

        /**
         * @param strokePaint the strokePaint to set
         */
        public void setStrokePaint(boolean strokePaint)
        {
            this.strokePaint = strokePaint;
        }

        /**
         * @return the strokeShape
         */
        public boolean isStrokeShape()
        {
            return strokeShape;
        }

        /**
         * @param strokeShape the strokeShape to set
         */
        public void setStrokeShape(boolean strokeShape)
        {
            this.strokeShape = strokeShape;
        }
    }
    
}
