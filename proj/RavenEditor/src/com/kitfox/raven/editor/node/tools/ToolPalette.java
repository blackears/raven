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

import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class ToolPalette
{
    public static final long serialVersionUID = 1;

    ArrayList<ToolProvider> toolList = new ArrayList<ToolProvider>();
    private ToolProvider currentTool;

    ArrayList<ToolPaletteListener> listeners = new ArrayList<ToolPaletteListener>();

    @SuppressWarnings("unchecked")
    public <T extends ToolProvider> T getTool(Class<T> toolTmpltClass)
    {
        for (ToolProvider tmplt: toolList)
        {
            if (toolTmpltClass.isAssignableFrom(tmplt.getClass()))
            {
                return (T)tmplt;
            }
        }
        return null;
    }

    public ArrayList<ToolProvider> getToolList()
    {
        return new ArrayList<ToolProvider>(toolList);
    }

    public void addProvider(ToolProvider provider)
    {
        toolList.add(provider);
    }

    public void addToolPaletteListener(ToolPaletteListener listener)
    {
        listeners.add(listener);
    }

    public void removeToolPaletteListener(ToolPaletteListener listener)
    {
        listeners.add(listener);
    }

    protected void fireCurrentToolChanged()
    {
        ToolPaletteEvent evt = new ToolPaletteEvent(this, currentTool);
        for (ToolPaletteListener l: new ArrayList<ToolPaletteListener>(listeners))
        {
            l.currentToolChanged(evt);
        }
    }

    /**
     * Get the value of currentTool
     *
     * @return the value of currentTool
     */
    public ToolProvider getCurrentTool()
    {
        return currentTool;
    }

    /**
     * Set the value of currentTool
     *
     * @param currentTool new value of currentTool
     */
    public void setCurrentTool(ToolProvider currentTool)
    {
        this.currentTool = currentTool;
        fireCurrentToolChanged();
    }

    public void setCurrentTool(Class<? extends ToolProvider> toolTmpltClass)
    {
        for (ToolProvider tmplt: toolList)
        {
            if (toolTmpltClass.isAssignableFrom(tmplt.getClass()))
            {
                setCurrentTool(tmplt);
                break;
            }
        }
    }
}
