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

package com.kitfox.coyote.renderer;

import com.kitfox.coyote.math.d3.BoundingBox3d;
import com.kitfox.coyote.renderer.GLWrapper.BufferUsage;
import com.kitfox.coyote.renderer.GLWrapper.DrawMode;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
abstract public class CyVertexBufferDataProvider
{
    ArrayList<CyVertexBufferDataProviderListener> listeners
            = new ArrayList<CyVertexBufferDataProviderListener>();

    HashMap<CyVertexArrayKey, CyVertexArrayInfo> infoMap
            = new HashMap<CyVertexArrayKey, CyVertexArrayInfo>();

    abstract public FloatBuffer getVertexData();

    abstract public ShortBuffer getIndexData();

    abstract public BufferUsage getVertexUsage();
    abstract public BufferUsage getIndexUsage();
    abstract public DrawMode getDrawMode();
    abstract public int getIndexCount();

    abstract public BoundingBox3d getBounds();

    /**
     * Return information about the content of the vertex array buffer.
     * Most buffer providers will contain multiples types of data -
     * position, texture, color, etc.  This will return a struct indicating
     * the format of a particular region of the buffer.
     *
     * @param key
     * @return
     */
    public CyVertexArrayInfo getVertexArrayInfo(CyVertexArrayKey key)
    {
        return infoMap.get(key);
    }

    protected void setVertexArrayInfo(CyVertexArrayKey key, CyVertexArrayInfo info)
    {
        infoMap.put(key, info);
    }

    protected void setVertexArrayInfo(CyVertexArrayKey key, long offset, int size)
    {
        CyVertexArrayInfo info = new CyVertexArrayInfo(key, offset, size);
        infoMap.put(key, info);
    }

    public void addCyVertexBufferDataProviderListener(CyVertexBufferDataProviderListener l)
    {
        listeners.add(l);
    }

    public void removeCyVertexBufferDataProviderListener(CyVertexBufferDataProviderListener l)
    {
        listeners.remove(l);
    }

    protected void fireArrayDataChanged()
    {
        CyChangeEvent evt = new CyChangeEvent(this);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).arrayDataChanged(evt);
        }
    }

    protected void fireElementDataChanged()
    {
        CyChangeEvent evt = new CyChangeEvent(this);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).elementDataChanged(evt);
        }
    }

}
