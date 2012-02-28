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
import com.kitfox.raven.util.cursor.CursorProviderIndex;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.node.tools.common.select.SelectionManipulator;
import com.kitfox.raven.editor.node.tools.common.select.SelectionManipulatorGroup;
import com.kitfox.raven.editor.node.tools.common.select.SelectionManipulatorSingle;
import com.kitfox.raven.editor.node.tools.ToolProvider;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.editor.node.tools.common.select.CursorRotate;
import com.kitfox.raven.editor.node.tools.common.select.CursorScale;
import com.kitfox.raven.editor.node.tools.common.select.CursorScaleX;
import com.kitfox.raven.editor.node.tools.common.select.CursorScaleY;
import com.kitfox.raven.editor.node.tools.common.select.CursorSkewX;
import com.kitfox.raven.editor.node.tools.common.select.CursorSkewY;
import com.kitfox.raven.editor.node.tools.common.select.CursorTranslate;
import com.kitfox.raven.editor.node.tools.common.select.SelectionManipulatorHandle;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.SelectionEvent;
import com.kitfox.raven.util.SelectionSubEvent;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.SelectionRecord;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class ToolSelect extends ToolSelectBase
{
    SelectionManipulatorHandle manipHandle;

    SelectionManipulator manip;

    protected ToolSelect(ToolUser user)
    {
        super(user);
        buildManipulator();
    }

    @Override
    protected void click(MouseEvent evt)
    {
        super.click(evt);

        updateCursor(evt);
        fireToolDisplayChanged();
    }

    private void updateCursor(MouseEvent evt)
    {
        ServiceDevice provider = user.getToolService(ServiceDevice.class);
        if (provider == null)
        {
            return;
        }

        SelectionManipulator curManip = manip;
        if (curManip == null)
        {
            return;
        }

        CyMatrix4d w2d =
                getWorldToDeviceTransform((CyMatrix4d)null);

        SelectionManipulatorHandle handle = curManip.getManipulatorHandle(
                evt.getX(), evt.getY(), w2d);

        CursorProviderIndex index = CursorProviderIndex.inst();
        Cursor cursor = Cursor.getDefaultCursor();
        if (handle != null)
        {
            switch (handle.getType())
            {
                case ROTATE:
                    cursor = index.getServiceByClass(CursorRotate.class)
                            .getCursor();
                    break;
                case SCALE:
                    cursor = index.getServiceByClass(CursorScale.class)
                            .getCursor();
                    break;
                case SCALEX:
                    cursor = index.getServiceByClass(CursorScaleX.class)
                            .getCursor();
                    break;
                case SCALEY:
                    cursor = index.getServiceByClass(CursorScaleY.class)
                            .getCursor();
                    break;
                case SKEWX:
                    cursor = index.getServiceByClass(CursorSkewX.class)
                            .getCursor();
                    break;
                case SKEWY:
                    cursor = index.getServiceByClass(CursorSkewY.class)
                            .getCursor();
                    break;
                case TRANSLATE:
                    cursor = index.getServiceByClass(CursorTranslate.class)
                            .getCursor();
                    break;
            }
        }

        Component comp = provider.getComponent();
        comp.setCursor(cursor);
    }


    @Override
    public void mouseMoved(MouseEvent evt)
    {
        updateCursor(evt);
    }

    @Override
    protected void startDrag(MouseEvent evt)
    {
        super.startDrag(evt);

        if (manip != null)
        {
            manip.reset();

            CyMatrix4d w2d =
                    getWorldToDeviceTransform((CyMatrix4d)null);
            manipHandle = manip.getManipulatorHandle(startEvt.getX(), startEvt.getY(), w2d);
        }
    }

    @Override
    protected void dragTo(MouseEvent evt)
    {
        super.dragTo(evt);

        if (manipHandle != null)
        {
            int mod = evt.getModifiersEx();
            boolean symmetric = (mod & InputEvent.SHIFT_DOWN_MASK) != 0;
            boolean centered = (mod & InputEvent.CTRL_DOWN_MASK) != 0;
            try
            {
                //Update manipulation
                manipHandle.apply(evt.getX(), evt.getY(), manip,
                        centered, symmetric, false);
            } catch (NoninvertibleTransformException ex)
            {
                Logger.getLogger(ToolSelect.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        fireToolDisplayChanged();
    }

    @Override
    protected boolean isDraggingSelectionArea()
    {
        return manipHandle == null;
    }

    @Override
    protected void endDrag(MouseEvent evt)
    {
        super.endDrag(evt);

        if (manipHandle != null)
        {
            //Finish applying manipulator
            int mod = evt.getModifiersEx();
            boolean symmetric = (mod & InputEvent.SHIFT_DOWN_MASK) != 0;
            boolean centered = (mod & InputEvent.CTRL_DOWN_MASK) != 0;
            try
            {
                //Finish manipulation
                manipHandle.apply(evt.getX(), evt.getY(), manip, centered, symmetric, true);
            } catch (NoninvertibleTransformException ex)
            {
                Logger.getLogger(ToolSelect.class.getName()).log(Level.SEVERE, null, ex);
            }

            manipHandle = null;
            manip.reset();
        }

        fireToolDisplayChanged();
    }

    @Override
    public void cancel()
    {
        super.cancel();
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
            manip.paint(g, w2d);
        }
    }

    @Override
    public void selectionChanged(SelectionEvent evt)
    {
        buildManipulator();
    }

    @Override
    public void subselectionChanged(SelectionSubEvent evt)
    {
    }

    private void buildManipulator()
    {
        if (manipHandle != null)
        {
            //Do not rebuild maniuplator if currently being manipulated
            return;
        }

        if (manip != null)
        {
            manip.removeManipulatorListener(this);
            manip = null;
            fireToolDisplayChanged();
        }

        ServiceDocument provider = user.getToolService(ServiceDocument.class);
        if (provider == null)
        {
            return;
        }

        ArrayList<ServiceTransformable> rootList = new ArrayList<ServiceTransformable>();
        Selection<NodeObject> sel = provider.getSelection();
        NEXT_NODE:
        for (int j = 0; j < sel.size(); ++j)
        {
            NodeObject rec = sel.get(j);
            if (!(rec instanceof ServiceTransformable))
            {
                continue;
            }

            ServiceTransformable rootNode = (ServiceTransformable)rec;

            for (int i = 0; i < sel.size(); ++i)
            {
                if (i == j)
                {
                    continue;
                }

                NodeObject other = sel.get(i);
                if (other.isAncestorOf(rootNode.getNodeObject()))
                {
                    continue NEXT_NODE;
                }
            }

            rootList.add(rootNode);
        }

        if (rootList.isEmpty())
        {
            return;
        }

        if (rootList.size() == 1)
        {
            manip = SelectionManipulatorSingle.create(rootList.get(0));
        }
        else
        {
            manip = SelectionManipulatorGroup.create(rootList);
        }
        manip.addManipulatorListener(this);

        fireToolDisplayChanged();
    }

    @Override
    public void selectionManipulatorChanged(EventObject evt)
    {
        fireToolDisplayChanged();
    }

    //---------------------------------------

    @ServiceInst(service=ToolProvider.class)
    static public class Provider extends ToolProvider<ToolSelect>
    {
        public Provider()
        {
            super("Select", "/icons/tools/select.png", "/manual/tools/select.html");
        }

        @Override
        public ToolSelect create(ToolUser user)
        {
            return new ToolSelect(user);
        }

        @Override
        public Component createToolSettingsEditor(RavenEditor editor)
        {
            return new ToolSelectSettings(editor);
        }
    }
}
