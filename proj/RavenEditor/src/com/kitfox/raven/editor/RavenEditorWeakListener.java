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
public class RavenEditorWeakListener implements RavenEditorListener
{
    final WeakReference<RavenEditorListener> ref;
    final RavenEditor src;

    public RavenEditorWeakListener(RavenEditorListener listener, RavenEditor src)
    {
        this.ref = new WeakReference<RavenEditorListener>(listener);
        this.src = src;
    }

    public void remove()
    {
        src.removeRavenEditorListener(this);
    }

    @Override
    public void recentFilesChanged(EventObject evt)
    {
        RavenEditorListener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.recentFilesChanged(evt);
    }

    @Override
    public void documentChanged(EventObject evt)
    {
        RavenEditorListener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.documentChanged(evt);
    }
}
