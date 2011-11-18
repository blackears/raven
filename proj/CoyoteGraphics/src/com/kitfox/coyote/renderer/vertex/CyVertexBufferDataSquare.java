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

package com.kitfox.coyote.renderer.vertex;

import com.kitfox.coyote.math.BufferUtil;
import com.kitfox.coyote.math.d3.BoundingBox3d;
import com.kitfox.coyote.renderer.CyMaterial;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.coyote.renderer.CyVertexBufferDataProvider;
import com.kitfox.coyote.renderer.GLWrapper.BufferUsage;
import com.kitfox.coyote.renderer.GLWrapper.DrawMode;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 *
 * @author kitfox
 */
public class CyVertexBufferDataSquare extends CyVertexBufferDataProvider
{
    private float[] vertex = new float[]
    {
        //Vertex
        0, 0,
        1, 0,
        1, 1,
        0, 1,

        //Texcoord
        0, 0,
        1, 0,
        1, 1,
        0, 1,
    };

    private short[] index = new short[]
    {
        0, 1, 2, 3
    };

    BoundingBox3d bounds = new BoundingBox3d(0, 0, 0, 1, 1, 0);

    private static CyVertexBufferDataSquare instance = new CyVertexBufferDataSquare();

    private final CyVertexBuffer buffer;

    private CyVertexBufferDataSquare()
    {
        setVertexArrayInfo(CyMaterial.KEY_POSITION,
                0 * BufferUtil.SIZEOF_FLOAT, 2);
        setVertexArrayInfo(CyMaterial.KEY_TEXCOORD, 
                8 * BufferUtil.SIZEOF_FLOAT, 2);

        buffer = new CyVertexBuffer(this);
    }

    public static CyVertexBufferDataSquare inst()
    {
        return instance;
    }

    @Override
    public FloatBuffer getVertexData()
    {
        FloatBuffer buf = BufferUtil.allocateFloat(vertex.length);
        buf.put(vertex);
        buf.rewind();
        return buf;
    }

    @Override
    public ShortBuffer getIndexData()
    {
        ShortBuffer buf = BufferUtil.allocateShort(index.length);
        buf.put(index);
        buf.rewind();
        return buf;
    }

    @Override
    public BufferUsage getVertexUsage()
    {
        return BufferUsage.GL_STATIC_DRAW;
    }

    @Override
    public BufferUsage getIndexUsage()
    {
        return BufferUsage.GL_STATIC_DRAW;
    }

    @Override
    public DrawMode getDrawMode()
    {
        return DrawMode.GL_TRIANGLE_FAN;
    }

    @Override
    public int getIndexCount()
    {
        return index.length;
    }

    /**
     * @return the buffer
     */
    public CyVertexBuffer getBuffer()
    {
        return buffer;
    }

    @Override
    public BoundingBox3d getBounds()
    {
        return bounds;
    }

}
