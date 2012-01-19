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

package com.kitfox.raven.paint.control;

import java.lang.ref.WeakReference;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author kitfox
 */
public class StopModelWeakListener implements StopModelListener
{
    final WeakReference<StopModelListener> ref;
    final StopModel src;

    public StopModelWeakListener(StopModelListener listener, StopModel src)
    {
        this.ref = new WeakReference<StopModelListener>(listener);
        this.src = src;
    }

    public void remove()
    {
        src.removeStopModelListener(this);
    }

    @Override
    public void stopModelChanged(ChangeEvent evt)
    {
        StopModelListener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.stopModelChanged(evt);
    }

    @Override
    public void beginStopEdits(ChangeEvent evt)
    {
        StopModelListener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.beginStopEdits(evt);
    }

    @Override
    public void endStopEdits(ChangeEvent evt)
    {
        StopModelListener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.endStopEdits(evt);
    }
}
