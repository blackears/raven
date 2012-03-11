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
import com.kitfox.raven.editor.node.tools.ToolProvider;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.editor.node.tools.common.ToolDisplay;
import com.kitfox.raven.paint.RavenPaint;
import com.kitfox.raven.paint.RavenStroke;
import com.kitfox.raven.shape.network.pick.NetworkHandleEdge;
import com.kitfox.raven.shape.network.pick.NetworkHandleFace;
import com.kitfox.raven.util.Intersection;
import com.kitfox.raven.util.PropertiesData;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.undo.History;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author kitfox
 */
public class ToolPaintSampler extends ToolDisplay
{
    ToolPaintSampler.Provider toolProvider;

    protected ToolPaintSampler(ToolUser user, ToolPaintSampler.Provider toolProvider)
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
                if (doSample(node, servShape, evt))
                {
                    return;
                }
            }
        }
    }
     
    private boolean doSample(RavenNodeXformable node, ServiceShapeManip servShape, MouseEvent evt)
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
//        GraphLayout graphLayout = doc.getGraphLayout();
        float pickRad = doc == null ? 1 : doc.getGraphRadiusPick();
        
        CyRectangle2d region = new CyRectangle2d(evt.getX() - pickRad, evt.getY() - pickRad, 
                pickRad * 2, pickRad * 2);

        if (toolProvider.isStrokePaint() || toolProvider.isStrokeShape())
        {
            ArrayList<? extends NetworkHandleEdge> edges = 
                    servShape.pickEdges(region, l2d, Intersection.INTERSECTS);

            if (!edges.isEmpty())
            {
                NetworkHandleEdge e = edges.get(0);
                RavenPaint paint = e.getPaint();
                RavenStroke stroke = e.getStroke();

                History hist = doc.getHistory();
                hist.beginTransaction("Sample edge paint");
                
                if (toolProvider.isStrokePaint())
                {
                    doc.strokePaint.setValue(paint);
                }
                if (toolProvider.isStrokeShape())
                {
                    doc.strokeShape.setValue(stroke);
                }
                
                hist.commitTransaction();
                return true;
            }
        }

        if (toolProvider.isFacePaint())
        {
            ArrayList<? extends NetworkHandleFace> faces = 
                    servShape.pickFaces(region, l2d, Intersection.INTERSECTS);

            if (!faces.isEmpty())
            {
                NetworkHandleFace face = faces.get(0);
                RavenPaint paint = face.getPaint();
                
                doc.fillPaint.setValue(paint);
                
                return true;
            }
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
    
    //---------------------------------------

    @ServiceInst(service=ToolProvider.class)
    static public class Provider extends ToolProvider<ToolPaintSampler>
    {
        public static final String PROP_FACEPAINT = "facePaint";
        private boolean facePaint = true;
        public static final String PROP_STROKEPAINT = "strokePaint";
        private boolean strokePaint = true;
        public static final String PROP_STROKESHAPE = "strokeShape";
        private boolean strokeShape = true;

        public Provider()
        {
            super("Paint Sampler", "/icons/tools/colorSampler.png", "/manual/tools/colorSampler.html");
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
        public ToolPaintSampler create(ToolUser user)
        {
            return new ToolPaintSampler(user, this);
        }

        @Override
        public Component createToolSettingsEditor(RavenEditor editor)
        {
            return new ToolPaintSamplerSettings(editor, this);
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
