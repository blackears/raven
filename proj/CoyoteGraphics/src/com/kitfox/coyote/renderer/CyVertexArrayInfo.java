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

/**
 *
 * @author kitfox
 */
public class CyVertexArrayInfo
{
    private final CyVertexArrayKey key;  //Number of floats per sample
    private final long offset;
    private final int size;

    public CyVertexArrayInfo(CyVertexArrayKey key, long offset, int size)
    {
        this.key = key;
        this.offset = offset;
        this.size = size;
    }

    /**
     * @return the key
     */
    public CyVertexArrayKey getKey()
    {
        return key;
    }

    /**
     * @return the offset
     */
    public long getOffset()
    {
        return offset;
    }

    /**
     * @return the size
     */
    public int getSize()
    {
        return size;
    }


}
