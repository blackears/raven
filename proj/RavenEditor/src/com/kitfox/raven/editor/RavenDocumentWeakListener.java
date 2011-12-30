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

package com.kitfox.raven.editor;

import java.lang.ref.WeakReference;
import java.util.EventObject;

/**
 *
 * @author kitfox
 */
public class RavenDocumentWeakListener implements RavenDocumentListener
{
    final WeakReference<RavenDocumentListener> ref;
    final RavenDocument src;

    public RavenDocumentWeakListener(RavenDocumentListener listener, RavenDocument src)
    {
        this.ref = new WeakReference<RavenDocumentListener>(listener);
        this.src = src;
    }

    public void remove()
    {
        src.removeRavenDocumentListener(this);
    }

    @Override
    public void documentSourceChanged(EventObject evt)
    {
        RavenDocumentListener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.documentSourceChanged(evt);
    }

    @Override
    public void documentAdded(RavenDocumentEvent evt)
    {
        RavenDocumentListener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.documentAdded(evt);
    }

    @Override
    public void documentRemoved(RavenDocumentEvent evt)
    {
        RavenDocumentListener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.documentRemoved(evt);
    }

    @Override
    public void currentDocumentChanged(RavenDocumentEvent evt)
    {
        RavenDocumentListener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.currentDocumentChanged(evt);
    }
    
}
