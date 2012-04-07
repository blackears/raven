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
import com.kitfox.coyote.renderer.CyGLContext.TextureBufferInfo;
import com.kitfox.coyote.renderer.CyGLWrapper.Attachment;
import com.kitfox.coyote.renderer.CyGLWrapper.DataType;
import com.kitfox.coyote.renderer.CyGLWrapper.InternalFormatTex;
import com.kitfox.coyote.renderer.CyGLWrapper.TexParam;
import com.kitfox.coyote.renderer.CyGLWrapper.TexParamName;
import com.kitfox.coyote.renderer.CyGLWrapper.TexSubTarget;
import com.kitfox.coyote.renderer.CyGLWrapper.TexTarget;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author kitfox
 */
public class CyFramebufferTexture extends CyFramebufferAttachment
        implements CyTextureSource
{
    private final CyGLWrapper.TexTarget target;
    private final CyGLWrapper.InternalFormatTex format;
    private final CyGLWrapper.DataType dataType;
    private final int width;
    private final int height;

    int dirty;

    public CyFramebufferTexture(Attachment attcahment,
            TexTarget target, InternalFormatTex format, DataType dataType,
            int width, int height)
    {
        super(attcahment);
        this.target = target;
        this.format = format;
        this.dataType = dataType;
        this.width = width;
        this.height = height;
    }

    /**
     * @return the target
     */
    @Override
    public CyGLWrapper.TexTarget getTarget()
    {
        return target;
    }

    /**
     * @return the format
     */
    @Override
    public CyGLWrapper.InternalFormatTex getFormat()
    {
        return format;
    }

    /**
     * @return the dataType
     */
    @Override
    public CyGLWrapper.DataType getDataType()
    {
        return dataType;
    }

    /**
     * @return the width
     */
    @Override
    public int getWidth()
    {
        return width;
    }

    /**
     * @return the height
     */
    @Override
    public int getHeight()
    {
        return height;
    }

    public void setFramebufferTexture(CyGLWrapper gl, TexSubTarget target, int texId)
    {
        gl.glFramebufferTexture2D(attachment, target, texId, 0);
    }

    private void initTex(CyGLWrapper gl, TexSubTarget target)
    {
        gl.glTexImage2D(target,
                0, format, width, height,
                dataType, null);
    }

    @Override
    public void bindTexture(CyGLContext ctx, CyGLWrapper gl)
    {
        TextureBufferInfo info = ctx.getTextureBufferInfo(this, gl);
        int texId = info.getTexId();
        
        gl.glBindTexture(target, texId);
        if (info.getDirty() < dirty)
        {
            gl.glTexParameter(target,
                    TexParamName.GL_TEXTURE_MIN_FILTER, TexParam.GL_LINEAR);
            gl.glTexParameter(target,
                    TexParamName.GL_TEXTURE_MAG_FILTER, TexParam.GL_LINEAR);
            gl.glTexParameter(target,
                    TexParamName.GL_TEXTURE_WRAP_S, TexParam.GL_CLAMP_TO_EDGE);
            gl.glTexParameter(target,
                    TexParamName.GL_TEXTURE_WRAP_T, TexParam.GL_CLAMP_TO_EDGE);
            gl.glGenerateMipmap(target);

            switch (target)
            {
                case GL_TEXTURE_2D:
                {
                    initTex(gl, TexSubTarget.GL_TEXTURE_2D);
                    break;
                }
                case GL_TEXTURE_CUBE_MAP:
                {
                    initTex(gl, TexSubTarget.GL_TEXTURE_CUBE_MAP_NEGATIVE_X);
                    initTex(gl, TexSubTarget.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y);
                    initTex(gl, TexSubTarget.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z);
                    initTex(gl, TexSubTarget.GL_TEXTURE_CUBE_MAP_POSITIVE_X);
                    initTex(gl, TexSubTarget.GL_TEXTURE_CUBE_MAP_POSITIVE_Y);
                    initTex(gl, TexSubTarget.GL_TEXTURE_CUBE_MAP_POSITIVE_Z);
                    break;
                }
            }

            info.setDirty(dirty);
        }
    }

    @Override
    public void bindFramebuffer(CyGLContext ctx, CyGLWrapper gl)
    {
        bindTexture(ctx, gl);
        
        TextureBufferInfo info = ctx.getTextureBufferInfo(this, gl);
        int texId = info.getTexId();
        
        switch (target)
        {
            case GL_TEXTURE_2D:
            {
                setFramebufferTexture(gl, 
                        TexSubTarget.GL_TEXTURE_2D, texId);
                break;
            }
            case GL_TEXTURE_CUBE_MAP:
            {
                //Just pick one to start with.  User will need to
                // manually switch this to cover entire cube
                setFramebufferTexture(gl, 
                        TexSubTarget.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, texId);
                break;
            }
        }
        
    }
    
    @Override
    public void unbindFramebuffer(CyGLContext ctx, CyGLWrapper gl)
    {
        switch (target)
        {
            case GL_TEXTURE_2D:
            {
                gl.glFramebufferTexture2D(attachment, 
                        TexSubTarget.GL_TEXTURE_2D, 0, 0);
                break;
            }
            case GL_TEXTURE_CUBE_MAP:
            {
                gl.glFramebufferTexture2D(attachment, 
                        TexSubTarget.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, 0);
                break;
            }
        }
            
    }

    public void dumpTexture(CyGLWrapper gl, File file, String fileFormat)
    {
        ByteBuffer buf = BufferUtil.allocateByte(width * height * 4);
        gl.glGetTexImage(target, 0, format, dataType, buf);

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int j = height - 1; j >= 0; --j)
        {
            for (int i = 0; i < width; ++i)
            {
                int r = (int)(buf.get() & 0xff);
                int g = (int)(buf.get() & 0xff);
                int b = (int)(buf.get() & 0xff);
                int a = (int)(buf.get() & 0xff);

                img.setRGB(i, j, (a << 24) | (r << 16) | (g << 8) | b);
            }
        }
        
        try
        {
            ImageIO.write(img, fileFormat, file);
        } catch (IOException ex)
        {
            Logger.getLogger(CyFramebufferTexture.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public CyTransparency getTransparency()
    {
        switch (format)
        {
            case GL_RGBA:
            case GL_ALPHA:
            case GL_LUMINANCE_ALPHA:
                return CyTransparency.TRANSLUCENT;
            default:
                return CyTransparency.OPAQUE;
        }
    }

    //------------------------------------

    static class Dispose implements CyGLAction
    {
        int id;

        public Dispose(int id)
        {
            this.id = id;
        }

        @Override
        public void doAction(CyGLWrapper gl)
        {
            IntBuffer ibuf = BufferUtil.allocateInt(1);
            ibuf.put(0, id);
            gl.glDeleteTextures(1, ibuf);
        }
    }


}
