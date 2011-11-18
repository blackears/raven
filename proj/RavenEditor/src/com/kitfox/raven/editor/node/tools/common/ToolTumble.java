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

import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.node.tools.ToolProvider;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.util.service.ServiceInst;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

/**
 * Wolframalpha:
 * Query:
 * {{cos(a), -sin(a), 0}, {sin(a), cos(a), 0}, {0, 0, 1}}.{{s, 0, 0}, {0, t, 0}, {0, 0, 1}}.{{1, 0, x}, {0, 1, y}, {0, 0, 1}}
 *
 * Query:
 * {{s, 0, 0}, {0, t, 0}, {0, 0, 1}}.{{1, 0, x}, {0, 1, y}, {0, 0, 1}}
 * Result:
 * {{s, 0, sx}, {0, t, ty}, {0, 0, 1}}
 *
 * @author kitfox
 */
public class ToolTumble extends ToolDisplay
{
    AffineTransform startXform = new AffineTransform();
    AffineTransform targetXform = new AffineTransform();
    MouseEvent startEvt;

    protected ToolTumble(ToolUser user)
    {
        super(user);
    }

    @Override
    public void click(MouseEvent evt)
    {
    }

    @Override
    protected void startDrag(MouseEvent evt)
    {
        ServiceDeviceCamera provider = user.getToolService(ServiceDeviceCamera.class);
        if (provider == null)
        {
            return;
        }

        startEvt = evt;
        provider.getWorldToDeviceTransform(startXform);
    }

    @Override
    protected void dragTo(MouseEvent evt)
    {
        ServiceDeviceCamera provider = user.getToolService(ServiceDeviceCamera.class);
        if (provider == null)
        {
            return;
        }

        int dx = evt.getX() - startEvt.getX();
        int dy = evt.getY() - startEvt.getY();

//        double angle = Math.sqrt(dx * dx + dy * dy) * 2 * Math.PI / 200;
        double angle = dx * 2 * Math.PI / 300;

        targetXform.setToTranslation(startEvt.getX(), startEvt.getY());
        targetXform.rotate(angle);
        targetXform.translate(-startEvt.getX(), -startEvt.getY());
        targetXform.concatenate(startXform);

        provider.setWorldToDeviceTransform(targetXform);
    }

    @Override
    protected void endDrag(MouseEvent evt)
    {
        ServiceDeviceCamera provider = user.getToolService(ServiceDeviceCamera.class);
        if (provider == null)
        {
            return;
        }

        int dx = evt.getX() - startEvt.getX();
        int dy = evt.getY() - startEvt.getY();

//        double angle = Math.sqrt(dx * dx + dy * dy) * 2 * Math.PI / 200;
        double angle = dx * 2 * Math.PI / 300;

        targetXform.setToTranslation(startEvt.getX(), startEvt.getY());
        targetXform.rotate(angle);
        targetXform.translate(-startEvt.getX(), -startEvt.getY());
        targetXform.concatenate(startXform);

        provider.setWorldToDeviceTransform(targetXform);
    }

    @Override
    public void cancel()
    {
        ServiceDeviceCamera provider = user.getToolService(ServiceDeviceCamera.class);
        if (provider == null)
        {
            return;
        }

        provider.setWorldToDeviceTransform(startXform);
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void paint(Graphics2D g)
    {
        paintSelectionBounds(g);
    }


    //---------------------------------------

    @ServiceInst(service=ToolProvider.class)
    static public class Provider extends ToolProvider<ToolTumble>
    {
        public Provider()
        {
            super("Tumble", "/icons/tools/tumble.png", "/manual/tools/tumble.html");
        }

        @Override
        public ToolTumble create(ToolUser user)
        {
            return new ToolTumble(user);
        }

        @Override
        public Component createToolSettingsEditor(RavenEditor editor)
        {
            return new ToolTumbleSettings(editor);
        }
    }

}
