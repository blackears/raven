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

package com.kitfox.raven.editor.node.tools;

import com.kitfox.raven.editor.node.tools.SelectionMask.SelectType;
import com.kitfox.raven.util.Selection;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 *
 * @author kitfox
 */
abstract public class ToolDraggable extends Tool
        implements MouseMotionListener, KeyListener
{
//    public static enum SelectType { REPLACE, ADD, SUBTRACT, INTERSECT };

    transient MouseEvent dragStartEvt;

    private DragState dragState = DragState.NONE;

    RestrictAxis restrict = RestrictAxis.NONE;

    private boolean enableRestrictAxis = true;

    protected ToolDraggable(ToolUser user)
    {
        super(user);
    }

    @Override
    public <T> T getListener(Class<T> listenerClass)
    {
        if (listenerClass.isAssignableFrom(getClass()))
        {
            return (T)this;
        }
        return null;
    }

    abstract protected void click(MouseEvent evt);
    abstract protected void startDrag(MouseEvent evt);
    abstract protected void dragTo(MouseEvent evt);
    abstract protected void endDrag(MouseEvent evt);

    /**
     * @return the dragState
     */
    public DragState getDragState()
    {
        return dragState;
    }

    public boolean restrictMouseToAxis()
    {
        return true;
    }

    @Override
    public void mouseClicked(MouseEvent evt)
    {
        super.mouseClicked(evt);
    }

    @Override
    public void mousePressed(MouseEvent evt)
    {
        super.mousePressed(evt);
        if (evt.isConsumed())
        {
            return;
        }

        dragStartEvt = evt;
        dragState = DragState.INIT;
    }

    @Override
    public void mouseReleased(MouseEvent evt)
    {
        super.mouseReleased(evt);
        if (evt.isConsumed())
        {
            return;
        }

        evt = doRestrict(evt);

        switch (dragState)
        {
            case INIT:
            {
                click(dragStartEvt);
                break;
            }
            case STARTED:
            {
                endDrag(evt);
                break;
            }
        }
        dragState = DragState.NONE;
        restrict = RestrictAxis.NONE;
        dragStartEvt = null;
    }

    @Override
    public void mouseDragged(MouseEvent evt)
    {
        evt = doRestrict(evt);

        switch (dragState)
        {
            case INIT:
            {
                int dx = dragStartEvt.getX() - evt.getX();
                int dy = dragStartEvt.getY() - evt.getY();

                if (dx * dx + dy * dy > 9)
                {
                    //Start dragging
                    dragState = DragState.STARTED;

                    boolean shift = (evt.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0;
                    if (enableRestrictAxis)
                    {
                        restrict = shift ? (
                                dx * dx > dy * dy ? RestrictAxis.X : RestrictAxis.Y
                                ) : RestrictAxis.NONE;
                    }

                    startDrag(dragStartEvt);
                    dragTo(evt);
                }
                break;
            }
            case STARTED:
            {
                dragTo(evt);
                break;
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent evt)
    {
    }

    protected MouseEvent doRestrict(MouseEvent evt)
    {
        switch (restrict)
        {
            case X:
                return new MouseEvent(evt.getComponent(),
                        evt.getID(), evt.getWhen(), evt.getModifiersEx(),
                        evt.getX(), dragStartEvt.getY(),
                        evt.getClickCount(), evt.isPopupTrigger(), evt.getButton());
            case Y:
                return new MouseEvent(evt.getComponent(),
                        evt.getID(), evt.getWhen(), evt.getModifiersEx(),
                        dragStartEvt.getX(), evt.getY(),
                        evt.getClickCount(), evt.isPopupTrigger(), evt.getButton());
        }
        return evt;
    }

    @Override
    public void keyTyped(KeyEvent evt)
    {
    }

    @Override
    public void keyPressed(KeyEvent evt)
    {
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        {
            //Cancel selection
            switch (dragState)
            {
                case STARTED:
                {
                    cancel();
                    break;
                }
            }

            dragState = DragState.NONE;
            dragStartEvt = null;
        }
    }

    @Override
    public void keyReleased(KeyEvent evt)
    {
    }

    protected Selection.Type getSelectType(MouseEvent dragEvt)
    {
        if (dragEvt.isShiftDown() && dragEvt.isControlDown())
        {
            return Selection.Type.INVERSE;
        }
        else if (dragEvt.isShiftDown())
        {
            return Selection.Type.ADD;
        }
        else if (dragEvt.isControlDown())
        {
            return Selection.Type.SUB;
        }
        else
        {
            return Selection.Type.REPLACE;
        }
    }

    /**
     * @return the enableRestrictAxis
     */
    public boolean isEnableRestrictAxis()
    {
        return enableRestrictAxis;
    }

    /**
     * @param enableRestrictAxis the enableRestrictAxis to set
     */
    public void setEnableRestrictAxis(boolean enableRestrictAxis)
    {
        this.enableRestrictAxis = enableRestrictAxis;
    }
    
    //-------------------------
    public static enum DragState { NONE, INIT, STARTED };
    public static enum RestrictAxis { NONE, X, Y };
}
