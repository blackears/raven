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
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

/**
 *
 * @author kitfox
 */
public class ToolZoom extends ToolDisplay
{
    Rectangle bounds = new Rectangle();
    AffineTransform startXform = new AffineTransform();
    AffineTransform targetXform = new AffineTransform();
    MouseEvent startEvt;
    float startZoom;

    final double ZOOM_FACTOR = Math.sqrt(2);
    final double LOG_FACTOR_INV = 1 / Math.log(ZOOM_FACTOR);
    final double EPSILON = .001;

    protected ToolZoom(ToolUser user)
    {
        super(user);
    }

    @Override
    public void click(MouseEvent evt)
    {
        ServiceDeviceCamera provider = user.getToolService(ServiceDeviceCamera.class);
        if (provider == null)
        {
            return;
        }
        ServiceDevice serviceDevice = user.getToolService(ServiceDevice.class);
        if (serviceDevice == null)
        {
            return;
        }

        provider.getWorldToDeviceTransform(startXform);
        serviceDevice.getDeviceBounds(bounds);

        int mod = evt.getModifiersEx();
        boolean ctrlDown = (mod & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK;
        boolean shiftDown = (mod & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK;

        double m00 = startXform.getScaleX();
        double m10 = startXform.getShearY();
        double curZoom = Math.sqrt(m00 * m00 + m10 * m10);
//        double curZoom = startXform.m00;
        double targetZoom;

        boolean zoomIn = evt.getButton() == 1;
        boolean zoomOut = evt.getButton() == 2;

        if (zoomIn || zoomOut)
        {
            if (shiftDown)
            {
                //provider.setZoom(1);
                targetZoom = 1;
            }
            else
            {
                //Move zoom to next power of ZOOM_FACTOR
                double exp = Math.log(curZoom) * LOG_FACTOR_INV;
                double expFloor = Math.floor(exp);
                double frac = exp - expFloor;
                double newExp = expFloor;
                if (zoomOut)
                {
                    //Zoom out
                    newExp += frac < EPSILON ? -1 : 0;
                    if (newExp < -8)
                    {
                        newExp = -8;
                    }
                }
                else
                {
                    //Zoom in
                    newExp += 1 - frac < EPSILON ? 2 : 1;
                    if (newExp > 8)
                    {
                        newExp = 8;
                    }
                }
                targetZoom = Math.pow(ZOOM_FACTOR, newExp);
            }

            double relZoom = targetZoom / curZoom;
            if (true)
            {
                //Center zoom on screen
                targetXform.setToTranslation(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
//                MatrixOps.setToTranslation(targetXform, bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
            }
            else
            {
                //Zoom centered on pick point
                targetXform.setToTranslation(evt.getX(), evt.getY());
//                MatrixOps.setToTranslation(targetXform, evt.getX(), evt.getY());
            }
            targetXform.scale(relZoom, relZoom);
            targetXform.translate(-evt.getX(), -evt.getY());
            targetXform.concatenate(startXform);
            
//            MatrixOps.scale(targetXform, (float)relZoom, (float)relZoom);
//            MatrixOps.translate(targetXform, -evt.getX(), -evt.getY());
//            targetXform.mul(startXform);


            provider.setWorldToDeviceTransform(targetXform);
        }


    }

    @Override
    protected void startDrag(MouseEvent evt)
    {
        ServiceDeviceCamera provider = user.getToolService(ServiceDeviceCamera.class);
        if (provider == null)
        {
            return;
        }

        provider.getWorldToDeviceTransform(startXform);
        startEvt = evt;
    }

    @Override
    protected void dragTo(MouseEvent evt)
    {
        ServiceDeviceCamera provider = user.getToolService(ServiceDeviceCamera.class);
        if (provider == null)
        {
            return;
        }

        int dist = evt.getX() - startEvt.getX() + evt.getY() - startEvt.getY();
        float zoom = (float)Math.exp(dist / 100.0);

        targetXform.setToTranslation(startEvt.getX(), startEvt.getY());
        targetXform.scale(zoom, zoom);
        targetXform.translate(-startEvt.getX(), -startEvt.getY());
        targetXform.concatenate(startXform);
        
//        MatrixOps.setToTranslation(targetXform, startEvt.getX(), startEvt.getY());
//        MatrixOps.scale(targetXform, zoom, zoom);
//        MatrixOps.translate(targetXform, -startEvt.getX(), -startEvt.getY());
//        targetXform.mul(startXform);


        provider.setWorldToDeviceTransform(targetXform);
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

    @Override
    public void paint(Graphics2D g)
    {
        paintSelectionBounds(g);
    }


    //---------------------------------------

    @ServiceInst(service=ToolProvider.class)
    static public class Provider extends ToolProvider<ToolZoom>
    {
        public Provider()
        {
            super("Zoom", "/icons/tools/zoom.png", "/manual/tools/zoom.html");
        }

        @Override
        public ToolZoom create(ToolUser user)
        {
            return new ToolZoom(user);
        }

        @Override
        public Component createToolSettingsEditor(RavenEditor editor)
        {
            return new ToolZoomSettings(editor);
        }
    }
}
