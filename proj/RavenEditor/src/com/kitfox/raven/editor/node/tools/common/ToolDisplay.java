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
import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.action.ActionManager;
import com.kitfox.raven.editor.node.scene.RavenNodeXformable;
import com.kitfox.raven.editor.node.scene.snap.Snapping;
import com.kitfox.raven.editor.node.tools.ToolDraggable;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.cursor.CursorProvider;
import com.kitfox.raven.util.cursor.CursorProviderIndex;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.SelectionRecord;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.Timer;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

/**
 *
 * @author kitfox
 */
abstract public class ToolDisplay extends ToolDraggable
{
    static final Timer timer = new Timer(true);

    protected ToolDisplay(ToolUser user)
    {
        super(user);
    }

    @Deprecated
    protected AffineTransform getWorldToDeviceTransform(AffineTransform xform)
    {
        if (xform == null)
        {
            xform = new AffineTransform();
        }

        ServiceDeviceCamera provider = user.getToolService(ServiceDeviceCamera.class);
        if (provider == null)
        {
            xform.setToIdentity();
            return xform;
        }

        provider.getWorldToDeviceTransform(xform);
        return xform;
    }

    @Deprecated
    protected CyMatrix4d getWorldToDeviceTransform(CyMatrix4d xform)
    {
        if (xform == null)
        {
            xform = CyMatrix4d.createIdentity();
        }

        ServiceDeviceCamera provider = user.getToolService(ServiceDeviceCamera.class);
        if (provider == null)
        {
            xform.setIdentity();
            return xform;
        }

        provider.getWorldToDeviceTransform(xform);
        return xform;
    }

    /**
     * Transform a point from device space to local space.
     * 
     * @param point Point to be transformed
     * @param snapGrid If true, grid snapping will be used if it is enabled
     * @return Transformed point
     */
    protected CyVector2d xformDev2LocalPoint(CyVector2d point, boolean snapGrid)
    {
        CyMatrix4d d2w = getDeviceToWorld(null);
        point = d2w.transformPoint(point, false);
        
        if (snapGrid)
        {
            point = snapToGridWorld(point);
        }
        
        CyMatrix4d w2l = getWorldToLocal(null);
        point = w2l.transformPoint(point, false);
        
        return point;
    }
    
    protected CyVector2d snapToGridWorld(CyVector2d v)
    {
        ServiceDocument provDoc = user.getToolService(ServiceDocument.class);
        if (provDoc == null)
        {
            return v;
        }
        
        Snapping snap = provDoc.getSnapping();
        if (snap.isSnapGrid())
        {
            //int major = snap.getGridSpacingMajor();
            int minor = snap.getGridSpacingMinor();
            int offX = snap.getGridSpacingOffsetX();
            int offY = snap.getGridSpacingOffsetY();
            
            v.sub(offX, offY);
            v.scale(1.0 / minor);
            v.x = Math.round(v.x);
            v.y = Math.round(v.y);
            v.scale(minor);
            v.add(offX, offY);
        }
        
        return v;
    }
    
    protected CyMatrix4d getLocalToWorld(CyMatrix4d xform)
    {
        if (xform == null)
        {
            xform = CyMatrix4d.createIdentity();
        }

        ServiceTransformable provider = user.getToolService(ServiceTransformable.class);
        if (provider != null)
        {
            provider.getLocalToWorldTransform(xform);
        }
        
        return xform;
    }
    
    protected CyMatrix4d getWorldToLocal(CyMatrix4d xform)
    {
        xform = getLocalToWorld(xform);
        xform.invert();
        return xform;
    }
    
    protected CyMatrix4d getLocalToDevice(CyMatrix4d xform)
    {
        xform = getWorldToDevice(xform);
        CyMatrix4d l2w = getLocalToWorld(xform);
        xform.mul(l2w);
        return xform;
    }

    protected CyMatrix4d getDeviceToLocal(CyMatrix4d xform)
    {
        xform = getLocalToDevice(xform);
        xform.invert();
        return xform;
    }
    
    protected CyMatrix4d getWorldToDevice(CyMatrix4d xform)
    {
        if (xform == null)
        {
            xform = CyMatrix4d.createIdentity();
        }

        ServiceDeviceCamera provider = user.getToolService(ServiceDeviceCamera.class);
        if (provider == null)
        {
            xform.setIdentity();
            return xform;
        }

        provider.getWorldToDeviceTransform(xform);
        return xform;
    }
    
    protected CyMatrix4d getDeviceToWorld(CyMatrix4d xform)
    {
        xform = getWorldToDevice(xform);
        xform.invert();
        return xform;
    }
    
    protected void setCursor(Cursor csr)
    {
        ServiceDevice provider = user.getToolService(ServiceDevice.class);
        if (provider == null)
        {
            return;
        }

        Component comp = provider.getComponent();
        comp.setCursor(csr);
    }
    
    protected void setCursor(Class<? extends CursorProvider> cls)
    {
        CursorProviderIndex index = CursorProviderIndex.inst();
        CursorProvider cp = index.getServiceByClass(cls);

        setCursor(cp.getCursor());
    }

    @Override
    public void mousePressed(MouseEvent evt)
    {
//        if (evt.isPopupTrigger())
//        {
//            showPopup(evt);
//            evt.consume();
//        }
        super.mousePressed(evt);
    }

    @Override
    public void mouseReleased(MouseEvent evt)
    {
//        if (evt.isPopupTrigger())
//        {
//            showPopup(evt);
//            evt.consume();
//        }
        super.mouseReleased(evt);
    }

    @Override
    public void keyPressed(KeyEvent evt)
    {
        super.keyPressed(evt);

        ServiceEditor provider = user.getToolService(ServiceEditor.class);
        RavenEditor editor = provider.getEditor();

        ActionManager mgr = editor.getViewManager().getActionManager();
        KeyStroke stroke = KeyStroke.getKeyStrokeForEvent(evt);
        mgr.executeKeyStroke(stroke, evt.getSource());
    }


    private void showPopup(MouseEvent evt)
    {
        AffineTransform m = new AffineTransform();
        getWorldToDeviceTransform(m);

        ServicePopupMenuProvider provider = user.getToolService(ServicePopupMenuProvider.class);
        if (provider == null)
        {
            return;
        }

        JPopupMenu menu = provider.getPopupMenu(evt, m);
        menu.show(evt.getComponent(), evt.getX(), evt.getY());
        evt.consume();
    }

    @Deprecated
    public void paintSelectionBounds(Graphics2D g)
    {
        //Draw selection tool
        CyRectangle2d bounds = getSelectedItemBoundsWorld();
        if (bounds == null)
        {
            return;
        }
        Path2D.Double boundsPath = bounds.asPathAWT();

        if (bounds != null)
        {
            AffineTransform xform =
                    getWorldToDeviceTransform((AffineTransform)null);

            Shape shape = xform.createTransformedShape(boundsPath);
            g.setColor(Color.cyan);
            g.draw(shape);
        }
    }

    public CyRectangle2d getSelectedItemBoundsWorld()
    {
        ServiceDocument provider = user.getToolService(ServiceDocument.class);
        if (provider == null)
        {
            return null;
        }

        CyRectangle2d bounds = null;
        Selection<NodeObject> sel = provider.getSelection();
        for (NodeObject obj: sel.getSelection())
        {
            if (obj instanceof RavenNodeXformable)
            {
                RavenNodeXformable node = (RavenNodeXformable)obj;
                CyRectangle2d nodeBounds = node.getBoundsWorld();
                if (bounds == null)
                {
                    bounds = nodeBounds;
                }
                else
                {
                    bounds.union(nodeBounds);
                }
            }
        }

        return bounds;
    }

}
