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

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.EventObject;

/**
 *
 * @author kitfox
 */
abstract public class Tool
        implements MouseListener
{
    final protected ToolUser user;

    private ArrayList<ToolListener> listeners = new ArrayList<ToolListener>();

    protected Tool(ToolUser user)
    {
        this.user = user;
    }

    public void addToolListener(ToolListener l)
    {
        listeners.add(l);
    }

    public void removeToolListener(ToolListener l)
    {
        listeners.remove(l);
    }

    protected void fireToolDisplayChanged()
    {
        EventObject evt = new EventObject(this);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).toolDisplayChanged(evt);
        }
    }

    abstract public void cancel();

    abstract public void dispose();

    /**
     * Returns an object that this tool can use to receive input from the ToolUser
     * that created them.  This is a lookup-like pattern where the ToolUser can
     * query tool support for various common listeners while also allowing the Tool
     * to implement the minimum number of interfaces necessary.
     * 
     * For example, a ToolUser may be willing to provide events for a MouseListener,
     * CustomListener and a KeyListener.  The tool may be willing to listen to events 
     * for a MouseMotionLister, MouseListener and CustomListener.  During the aquisition
     * process, the ToolUser will call this method with its supported listener classes.
     * The tool will respond with objects that can handle MouseListener and CustomListener.
     * Listeners will then be registered and the ToolUser will feed data to the
     * tool.
     *
     * This method should return the same object for every invocation with a
     * particular class object.
     *
     * @param <T>
     * @param listenerClass
     * @return
     */
    abstract public <T> T getListener(Class<T> listenerClass);

    @Override
    public void mouseClicked(MouseEvent evt)
    {
    }

    @Override
    public void mousePressed(MouseEvent evt)
    {
    }

    @Override
    public void mouseReleased(MouseEvent evt)
    {
    }

    @Override
    public void mouseEntered(MouseEvent evt)
    {
    }

    @Override
    public void mouseExited(MouseEvent evt)
    {
    }

    public void paint(Graphics2D g)
    {
    }
}
