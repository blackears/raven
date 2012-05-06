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

/**
 *
 * @author kitfox
 */
public class NodeSymbolWeakListener implements NodeSymbolListener
{
    final WeakReference<NodeSymbolListener> ref;
    final NodeSymbol src;

    public NodeSymbolWeakListener(NodeSymbolListener listener, NodeSymbol src)
    {
        this.ref = new WeakReference<NodeSymbolListener>(listener);
        this.src = src;
    }

    public void remove()
    {
        src.removeNodeSymbolListener(this);
    }

    @Override
    public void symbolPropertyChanged(PropertyChangeEvent evt)
    {
        NodeSymbolListener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.symbolPropertyChanged(evt);
    }

    @Override
    public void symbolNameChanged(PropertyChangeEvent evt)
    {
        NodeSymbolListener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.symbolNameChanged(evt);
    }

    @Override
    public void symbolNodeChildAdded(ChildWrapperEvent evt)
    {
        NodeSymbolListener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.symbolNodeChildAdded(evt);
    }

    @Override
    public void symbolNodeChildRemoved(ChildWrapperEvent evt)
    {
        NodeSymbolListener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.symbolNodeChildRemoved(evt);
    }
}
