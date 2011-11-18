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

import java.lang.ref.WeakReference;

/**
 *
 * @author kitfox
 */
public class ToolPaletteWeakListener implements ToolPaletteListener
{
    final WeakReference<ToolPaletteListener> ref;
    final ToolPalette src;

    public ToolPaletteWeakListener(ToolPaletteListener listener, ToolPalette src)
    {
        this.ref = new WeakReference<ToolPaletteListener>(listener);
        this.src = src;
    }

    public void currentToolChanged(ToolPaletteEvent evt)
    {
        ToolPaletteListener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.currentToolChanged(evt);
    }

    public void remove()
    {
        src.removeToolPaletteListener(this);
    }
}
