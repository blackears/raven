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

import com.kitfox.coyote.renderer.GLWrapper.TexSubTarget;
import java.nio.Buffer;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
abstract public class CyTextureDataProvider
{
    ArrayList<CyTextureDataProviderListener> listeners
            = new ArrayList<CyTextureDataProviderListener>();

    abstract public Buffer getData(TexSubTarget target);

    public void addCyTextureDataProviderListener(CyTextureDataProviderListener l)
    {
        listeners.add(l);
    }

    public void removeCyTextureDataProviderListener(CyTextureDataProviderListener l)
    {
        listeners.remove(l);
    }

    protected void fireTextureDataChanged()
    {
        CyChangeEvent evt = new CyChangeEvent(this);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).textureDataChanged(evt);
        }
    }
}
