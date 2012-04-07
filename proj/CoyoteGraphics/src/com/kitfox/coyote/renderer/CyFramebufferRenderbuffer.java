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

import com.kitfox.coyote.renderer.CyGLContext.RenderbufferInfo;
import com.kitfox.coyote.renderer.CyGLWrapper.Attachment;
import com.kitfox.coyote.renderer.CyGLWrapper.InternalFormatBuf;

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

    @Override
    public void bindFramebuffer(CyGLContext ctx, CyGLWrapper gl)
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
    }

    @Override
    public void unbindFramebuffer(CyGLContext ctx, CyGLWrapper gl)
    {
        gl.glFramebufferRenderbuffer(attachment, 0);
    }
}
