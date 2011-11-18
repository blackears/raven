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

import com.kitfox.coyote.math.BufferUtil;
import com.kitfox.coyote.math.d3.BoundingBox3d;
import com.kitfox.coyote.renderer.GLContext.VertexBufferInfo;
import com.kitfox.coyote.renderer.GLWrapper.BufferTarget;
import com.kitfox.coyote.renderer.GLWrapper.DrawMode;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 *
 * @author kitfox
 */
public class CyVertexBuffer
        implements CyVertexBufferDataProviderListener
{
//    int surfInst;

    CyVertexBufferDataProvider dataProv;
    int arrayDirty = 0;
    int eleDirty = 0;

    public CyVertexBuffer(CyVertexBufferDataProvider dataProv)
    {
        this.dataProv = dataProv;
        dataProv.addCyVertexBufferDataProviderListener(this);
    }

    public DrawMode getDrawMode()
    {
        return dataProv.getDrawMode();
    }

    public int getIndexCount()
    {
        return dataProv.getIndexCount();
    }

    public void bind(GLContext ctx, GLWrapper gl)
    {
        VertexBufferInfo info = ctx.getVertexBufferInfo(this, gl);

        int arrayId = info.getArrayId();
        int eleId = info.getEleId();
        
        //Upload new data if it has changed
        if (info.getArrayDirty() < arrayDirty)
        {
            gl.glBindBuffer(BufferTarget.GL_ARRAY_BUFFER, arrayId);
            FloatBuffer buf = dataProv.getVertexData();
            gl.glBufferData(BufferTarget.GL_ARRAY_BUFFER,
                    buf.limit() * BufferUtil.SIZEOF_FLOAT,
                    buf,
                    dataProv.getVertexUsage());

            info.setArrayDirty(arrayDirty);
        }

        if (info.getEleDirty() < eleDirty)
        {
            gl.glBindBuffer(BufferTarget.GL_ELEMENT_ARRAY_BUFFER, eleId);
            ShortBuffer buf = dataProv.getIndexData();
            gl.glBufferData(BufferTarget.GL_ELEMENT_ARRAY_BUFFER,
                    buf.limit() * BufferUtil.SIZEOF_SHORT,
                    buf,
                    dataProv.getIndexUsage());

            info.setEleDirty(eleDirty);
        }

        gl.glBindBuffer(BufferTarget.GL_ARRAY_BUFFER, arrayId);
        gl.glBindBuffer(BufferTarget.GL_ELEMENT_ARRAY_BUFFER, eleId);
    }

    @Override
    public void arrayDataChanged(CyChangeEvent evt)
    {
        ++arrayDirty;
    }

    @Override
    public void elementDataChanged(CyChangeEvent evt)
    {
        ++eleDirty;
    }

    public CyVertexArrayInfo getVertexArrayInfo(CyVertexArrayKey key)
    {
        return dataProv.getVertexArrayInfo(key);
    }

    public BoundingBox3d getBounds()
    {
        return dataProv.getBounds();
    }
}
