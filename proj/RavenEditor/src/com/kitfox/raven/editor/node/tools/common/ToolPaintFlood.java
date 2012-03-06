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
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.node.scene.RavenNodeRoot;
import com.kitfox.raven.editor.node.tools.ToolProvider;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.util.Intersection;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeObject;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Properties;

/**
 *
 * @author kitfox
 */
@Deprecated
public class ToolPaintFlood extends ToolDisplay
{
    Provider toolProvider;

    protected ToolPaintFlood(ToolUser user, Provider toolProvider)
    {
        super(user);
        this.toolProvider = toolProvider;
    }

    @Override
    protected void click(MouseEvent evt)
    {
        ServiceDocument provider = user.getToolService(ServiceDocument.class);
        if (provider == null)
        {
            return;
        }

        ServiceDeviceCamera provCam = user.getToolService(ServiceDeviceCamera.class);
        if (provCam == null)
        {
            return;
        }
        CyMatrix4d w2d = provCam.getWorldToDeviceTransform((CyMatrix4d)null);

        NodeObject pickObj = provider.pickObject(
                new CyRectangle2d(evt.getX(), evt.getY(), 1, 1),
                w2d, Intersection.INTERSECTS);

        if (pickObj == null)
        {
            return;
        }

        RavenNodeRoot root = (RavenNodeRoot)provider.getDocument();

        //Check for subcomponent that needs filling
        ServiceMaterial floodProv = pickObj.getNodeService(ServiceMaterial.class, false);

        if (toolProvider.isStrokeMode())
        {
            floodProv.floodStroke(root.strokePaint.getValue(),
                    root.strokeShape.getValue(),
                    new CyRectangle2d(evt.getX(), evt.getY(), 1, 1),
                    w2d, Intersection.INTERSECTS);
        }
        else
        {
            floodProv.floodFill(root.fillPaint.getValue(),
                    new CyRectangle2d(evt.getX(), evt.getY(), 1, 1),
                    w2d, Intersection.INTERSECTS);
        }
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

//    @ServiceInst(service=ToolProvider.class)
    static public class Provider extends ToolProvider<ToolPaintFlood>
    {
        public static final String PROP_STROKEMODE = "strokeMode";
        private boolean strokeMode;

        public Provider()
        {
            super("Paint Flood", "/icons/tools/paintFlood.png", "/manual/tools/paintFlood.html");
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
