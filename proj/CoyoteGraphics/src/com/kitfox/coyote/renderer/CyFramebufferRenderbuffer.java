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
import com.kitfox.coyote.renderer.CyGLContext.RenderbufferInfo;
import com.kitfox.coyote.renderer.CyGLWrapper.Attachment;
import com.kitfox.coyote.renderer.CyGLWrapper.InternalFormatBuf;
import java.nio.IntBuffer;

/**
 *
 * @author kitfox
 */
public class CyFramebufferRenderbuffer extends CyFramebufferAttachment
{
    final int width;
    final int height;
    final InternalFormatBuf internalFormat;

    int dirty;
//    int id;
//    int surfInst;

    public CyFramebufferRenderbuffer(Attachment attachment, int width, int height,
            CyGLWrapper.InternalFormatBuf internalFormat)
    {
        super(attachment);
        this.width = width;
        this.height = height;
        this.internalFormat = internalFormat;
    }

    /**
     * Specifies the width of the renderbuffer in pixels.
     *
     * @return
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Specifies the height of the renderbuffer in pixels.
     *
     * @return
     */
    public int getHeight()
    {
        return height;
    }

    public CyGLWrapper.InternalFormatBuf getInternalFormat()
    {
        return internalFormat;
    }

//    private void init(GLWrapper gl)
//    {
//        surfInst = gl.getSurfaceInstanceNumber();
//
//        IntBuffer ibuf = BufferUtil.allocateInt(1);
//        gl.glGenRenderbuffers(1, ibuf);
//        id = ibuf.get(0);
//
//        gl.glBindRenderbuffer(id);
//        gl.glRenderbufferStorage(internalFormat, width, height);
//
//        gl.glFramebufferRenderbuffer(attachment, id);
//    }

    @Override
    public void bind(CyGLContext ctx, CyGLWrapper gl)
    {
        RenderbufferInfo info = ctx.getRenderbufferInfo(this, gl);
        int rboId = info.getRboId();

        gl.glBindRenderbuffer(rboId);
        if (info.getDirty() < dirty)
        {
            gl.glRenderbufferStorage(internalFormat, width, height);
            gl.glFramebufferRenderbuffer(attachment, rboId);

            info.setDirty(dirty);
        }




//        if (id == 0 || gl.getSurfaceInstanceNumber() != surfInst)
//        {
//            init(gl);
//        }
//        else
//        {
//            gl.glBindRenderbuffer(id);
//        }
    }

//    public void dispose()
//    {
//        GLActionQueue.inst().postAction(new Dispose(id));
//        id = 0;
//    }

    //------------------------------------

    static class Dispose implements CyGLAction
    {
        int id;

        public Dispose(int id)
        {
            this.id = id;
        }

        public void doAction(CyGLWrapper gl)
        {
            IntBuffer ibuf = BufferUtil.allocateInt(1);
            ibuf.put(0, id);
            gl.glDeleteRenderbuffers(1, ibuf);
        }
    }
}
