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
public class RavenViewManagerWeakListener implements RavenViewManagerListener
{
    final WeakReference<RavenViewManagerListener> ref;
    final RavenViewManager src;

    public RavenViewManagerWeakListener(RavenViewManagerListener listener, RavenViewManager src)
    {
        this.ref = new WeakReference<RavenViewManagerListener>(listener);
        this.src = src;
    }

    @Override
    public void layoutListChanged(EventObject evt)
    {
        RavenViewManagerListener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.layoutListChanged(evt);
    }

    @Override
    public void viewLayoutChanged(EventObject evt)
    {
        RavenViewManagerListener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.viewLayoutChanged(evt);
    }

    public void remove()
    {
        src.removeRavenViewManagerListener(this);
    }
}
