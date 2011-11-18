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
import com.kitfox.coyote.renderer.GLContext.TextureBufferInfo;
import com.kitfox.coyote.renderer.GLWrapper.Attachment;
import com.kitfox.coyote.renderer.GLWrapper.DataType;
import com.kitfox.coyote.renderer.GLWrapper.InternalFormatTex;
import com.kitfox.coyote.renderer.GLWrapper.TexParam;
import com.kitfox.coyote.renderer.GLWrapper.TexParamName;
import com.kitfox.coyote.renderer.GLWrapper.TexSubTarget;
import com.kitfox.coyote.renderer.GLWrapper.TexTarget;
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
    private final GLWrapper.TexTarget target;
    private final GLWrapper.InternalFormatTex format;
    private final GLWrapper.DataType dataType;
    private final int width;
    private final int height;

    int dirty;
//    int id;
//    int surfInst;

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
    public GLWrapper.TexTarget getTarget()
    {
        return target;
    }

    /**
     * @return the format
     */
    @Override
    public GLWrapper.InternalFormatTex getFormat()
    {
        return format;
    }

    /**
     * @return the dataType
     */
    @Override
    public GLWrapper.DataType getDataType()
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

    public void setFramebufferTexture(GLWrapper gl, TexSubTarget target, int texId)
    {
        gl.glFramebufferTexture2D(attachment, target, texId, 0);
    }

    private void initTex(GLWrapper gl, TexSubTarget target)
    {
        gl.glTexImage2D(target,
                0, format, width, height,
                dataType, null);

//        gl.glFramebufferTexture2D(attachment, target, id, 0);
    }

//    private void init(GLWrapper gl)
//    {
//        surfInst = gl.getSurfaceInstanceNumber();
//
////System.err.println("Init CyFramebufferTexture");
//        IntBuffer ibuf = BufferUtil.allocateInt(1);
//        gl.glGenTextures(1, ibuf);
//        id = ibuf.get(0);
//
//        gl.glBindTexture(target, id);
//        gl.glTexParameter(target,
//                TexParamName.GL_TEXTURE_MIN_FILTER, TexParam.GL_LINEAR);
//        gl.glTexParameter(target,
//                TexParamName.GL_TEXTURE_MAG_FILTER, TexParam.GL_LINEAR);
//        gl.glTexParameter(target,
//                TexParamName.GL_TEXTURE_WRAP_S, TexParam.GL_CLAMP_TO_EDGE);
//        gl.glTexParameter(target,
//                TexParamName.GL_TEXTURE_WRAP_T, TexParam.GL_CLAMP_TO_EDGE);
//        gl.glGenerateMipmap(target);
//
//        switch (target)
//        {
//            case GL_TEXTURE_2D:
//            {
//                initTex(gl, TexSubTarget.GL_TEXTURE_2D);
//                setFramebufferTexture(gl, TexSubTarget.GL_TEXTURE_2D);
//                break;
//            }
//            case GL_TEXTURE_CUBE_MAP:
//            {
//                initTex(gl, TexSubTarget.GL_TEXTURE_CUBE_MAP_NEGATIVE_X);
//                initTex(gl, TexSubTarget.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y);
//                initTex(gl, TexSubTarget.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z);
//                initTex(gl, TexSubTarget.GL_TEXTURE_CUBE_MAP_POSITIVE_X);
//                initTex(gl, TexSubTarget.GL_TEXTURE_CUBE_MAP_POSITIVE_Y);
//                initTex(gl, TexSubTarget.GL_TEXTURE_CUBE_MAP_POSITIVE_Z);
//
//                //Just pick one to start with.  User will need to
//                // manually switch this to cover entire cube
//                setFramebufferTexture(gl, TexSubTarget.GL_TEXTURE_CUBE_MAP_NEGATIVE_X);
//                break;
//            }
//        }
//
//    }

    @Override
    public void bind(GLContext ctx, GLWrapper gl)
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
                    setFramebufferTexture(gl, TexSubTarget.GL_TEXTURE_2D, texId);
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

                    //Just pick one to start with.  User will need to
                    // manually switch this to cover entire cube
                    setFramebufferTexture(gl, TexSubTarget.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, texId);
                    break;
                }
            }


            info.setDirty(dirty);
        }





//        if (id == 0 || gl.getSurfaceInstanceNumber() != surfInst)
//        {
//            init(gl);
//        }
//        else
//        {
//            gl.glBindTexture(target, id);
//        }
    }

//    public void dispose()
//    {
//        GLActionQueue.inst().postAction(new Dispose(id));
//        id = 0;
//    }

    public void dumpTexture(GLWrapper gl, File file, String fileFormat)
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

    static class Dispose implements GLAction
    {
        int id;

        public Dispose(int id)
        {
            this.id = id;
        }

        @Override
        public void doAction(GLWrapper gl)
        {
            IntBuffer ibuf = BufferUtil.allocateInt(1);
            ibuf.put(0, id);
            gl.glDeleteTextures(1, ibuf);
        }
    }


}
