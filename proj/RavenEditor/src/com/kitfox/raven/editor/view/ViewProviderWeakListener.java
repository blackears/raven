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

package com.kitfox.raven.editor.view;

import java.lang.ref.WeakReference;
import java.util.EventObject;

/**
 *
 * @author kitfox
 */
public class ViewProviderWeakListener implements ViewProviderListener
{
    final WeakReference<ViewProviderListener> ref;
    final ViewProvider src;

    public ViewProviderWeakListener(ViewProviderListener listener, ViewProvider src)
    {
        this.ref = new WeakReference<ViewProviderListener>(listener);
        this.src = src;
    }

    public void remove()
    {
        src.removeViewProviderListener(this);
    }

    @Override
    public void viewProviderPreferencesChanged(EventObject evt)
    {
        ViewProviderListener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.viewProviderPreferencesChanged(evt);
    }
}
