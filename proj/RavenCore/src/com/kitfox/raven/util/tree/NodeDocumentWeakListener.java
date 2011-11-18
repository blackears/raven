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
public class NodeDocumentWeakListener implements NodeDocumentListener
{
    final WeakReference<NodeDocumentListener> ref;
    final NodeDocument src;

    public NodeDocumentWeakListener(NodeDocumentListener listener, NodeDocument src)
    {
        this.ref = new WeakReference<NodeDocumentListener>(listener);
        this.src = src;
    }

    public void remove()
    {
        src.removeNodeDocumentListener(this);
    }

    @Override
    public void documentPropertyChanged(PropertyChangeEvent evt)
    {
        NodeDocumentListener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.documentPropertyChanged(evt);
    }

    @Override
    public void documentNodeChildAdded(ChildWrapperEvent evt)
    {
        NodeDocumentListener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.documentNodeChildAdded(evt);
    }

    @Override
    public void documentNodeChildRemoved(ChildWrapperEvent evt)
    {
        NodeDocumentListener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.documentNodeChildRemoved(evt);
    }
}
