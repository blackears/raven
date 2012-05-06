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

import java.lang.ref.WeakReference;

/**
 *
 * @author kitfox
 */
public class NodeDocument2WeakListener implements NodeDocument2Listener
{
    final WeakReference<NodeDocument2Listener> ref;
    final NodeDocument2 src;

    public NodeDocument2WeakListener(NodeDocument2Listener listener, 
            NodeDocument2 src)
    {
        this.ref = new WeakReference<NodeDocument2Listener>(listener);
        this.src = src;
    }

    public void remove()
    {
        src.removeNodeDocumentListener(this);
    }

    @Override
    public void symbolAdded(NodeDocument2Event evt)
    {
        NodeDocument2Listener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.symbolAdded(evt);
    }

    @Override
    public void symbolRemoved(NodeDocument2Event evt)
    {
        NodeDocument2Listener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.symbolRemoved(evt);
    }

    @Override
    public void currentSymbolChanged(NodeDocument2Event evt)
    {
        NodeDocument2Listener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.currentSymbolChanged(evt);
    }
    
}
