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

import com.kitfox.coyote.renderer.CyGLWrapper.Attachment;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class CyRenderTileCache
{
    HashMap<Key, Allocator> allocMap = new HashMap<Key, Allocator>();
    

    public FilteredTile allocTile(int width, int height)
    {
        Key key = new Key(width, height);
        Allocator alloc = allocMap.get(key);
        if (alloc == null)
        {
            alloc = new Allocator(key);
            allocMap.put(key, alloc);
        }

        return alloc.allocate();
    }

    //---------------------------------
    class Allocator
    {
        final Key key;

        ArrayList<FilteredTile> tilePool = new ArrayList<FilteredTile>();

        public Allocator(Key key)
        {
            this.key = key;
        }

        private FilteredTile allocate()
        {
            FilteredTile tile;
            if (!tilePool.isEmpty())
            {
                tile = tilePool.remove(tilePool.size() - 1);
            }
            else
            {
                tile = new FilteredTile(key);
            }

            return tile;
        }

        
    }

    public static class Key
    {
        final int width;
        final int height;

        public Key(int width, int height)
        {
            this.width = width;
            this.height = height;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj == null)
            {
                return false;
            }
            if (getClass() != obj.getClass())
            {
                return false;
            }
            final Key other = (Key) obj;
            if (this.width != other.width)
            {
                return false;
            }
            if (this.height != other.height)
            {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode()
        {
            int hash = 5;
            hash = 97 * hash + this.width;
            hash = 97 * hash + this.height;
            return hash;
        }

    }

    public class FilteredTile
    {
        private Key key;
        private final CyFramebuffer buffer;
        private final CyFramebufferTexture color;
        private final CyFramebufferRenderbuffer depth;

        public FilteredTile(Key key)
        {
            int width = key.width;
            int height = key.height;

            color = new CyFramebufferTexture(Attachment.GL_COLOR_ATTACHMENT0,
                    CyGLWrapper.TexTarget.GL_TEXTURE_2D,
                    CyGLWrapper.InternalFormatTex.GL_RGBA,
                    CyGLWrapper.DataType.GL_UNSIGNED_BYTE,
                    width, height);
            depth = new CyFramebufferRenderbuffer(Attachment.GL_DEPTH_ATTACHMENT,
                    width, height,
                    CyGLWrapper.InternalFormatBuf.GL_DEPTH_COMPONENT16);
            buffer = new CyFramebuffer(width, height, color, depth);
        }

        public void returnTile()
        {
            Allocator alloc = allocMap.get(key);
            alloc.tilePool.add(this);
        }

        /**
         * @return the key
         */
        public Key getKey()
        {
            return key;
        }

        /**
         * @return the buffer
         */
        public CyFramebuffer getBuffer()
        {
            return buffer;
        }

        /**
         * @return the color
         */
        public CyFramebufferTexture getColor()
        {
            return color;
        }

        /**
         * @return the depth
         */
        public CyFramebufferRenderbuffer getDepth()
        {
            return depth;
        }
    }
}
