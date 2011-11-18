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

package com.kitfox.raven.util.tree;

import java.beans.PropertyChangeEvent;
import java.lang.ref.WeakReference;
import java.util.EventObject;

/**
 *
 * @author kitfox
 */
public class NodeObjectWeakListener implements NodeObjectListener
{
    final WeakReference<NodeObjectListener> ref;
    final NodeObject src;

    public NodeObjectWeakListener(NodeObjectListener listener, NodeObject src)
    {
        this.ref = new WeakReference<NodeObjectListener>(listener);
        this.src = src;
    }

    public void remove()
    {
        src.removeNodeObjectListener(this);
    }

    @Override
    public void nodeNameChanged(EventObject evt)
    {
        NodeObjectListener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.nodeNameChanged(evt);
    }

    @Override
    public void nodePropertyChanged(PropertyChangeEvent evt)
    {
        NodeObjectListener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.nodePropertyChanged(evt);
    }

    @Override
    public void nodeChildAdded(ChildWrapperEvent evt)
    {
        NodeObjectListener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.nodeChildAdded(evt);
    }

    @Override
    public void nodeChildRemoved(ChildWrapperEvent evt)
    {
        NodeObjectListener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.nodeChildRemoved(evt);
    }
}
