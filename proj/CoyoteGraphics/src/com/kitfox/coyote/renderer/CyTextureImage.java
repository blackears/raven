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
import com.kitfox.coyote.renderer.CyGLWrapper.TexParam;
import com.kitfox.coyote.renderer.CyGLWrapper.TexParamName;
import com.kitfox.coyote.renderer.CyGLWrapper.TexSubTarget;
import java.nio.IntBuffer;

/**
 * Provides all data needed to initialize a texture.  Since CyTextures may
 * be defined in threads other than the GL thread, this interface
 * allows the actual allocation of the GL texture to be deferred until the
 * GL thread is active.
 *
 * @author kitfox
 */
public class CyTextureImage
        implements CyTextureDataProviderListener, CyTextureSource
{
    private final CyGLWrapper.TexTarget target;
    private final CyGLWrapper.InternalFormatTex format;
    private final CyGLWrapper.DataType dataType;
    private final int width;
    private final int height;
    private final CyTransparency transparency;
    private final CyTextureDataProvider dataProvider;

//    int id;
//    int surfInst;
    int dirty;

    public CyTextureImage(CyGLWrapper.TexTarget target,
            CyGLWrapper.InternalFormatTex format,
            CyGLWrapper.DataType dataType,
            int width, int height, CyTransparency transparency,
            CyTextureDataProvider dataProvider)
    {
        this.target = target;
        this.format = format;
        this.dataType = dataType;
        this.width = width;
        this.height = height;
        this.transparency = transparency;
        this.dataProvider = dataProvider;

        dataProvider.addCyTextureDataProviderListener(this);
    }

    @Override
    public CyGLWrapper.InternalFormatTex getFormat()
    {
        return format;
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public CyTransparency getTransparency()
    {
        return transparency;
    }

    @Override
    public CyGLWrapper.DataType getDataType()
    {
        return dataType;
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
     * @return the dataProvider
     */
    public CyTextureDataProvider getDataProvider()
    {
        return dataProvider;
    }

    private void initTex(CyGLWrapper gl, TexSubTarget target)
    {
        gl.glTexImage2D(target,
                0, format, width, height,
                dataType, 
                dataProvider == null
                ? null
                : dataProvider.getData(target));
    }

//    private void uploadData(GLWrapper gl)
//    {
//        if (!dirty)
//        {
//            return;
//        }
//
//        dirty = false;
//        switch (target)
//        {
//            case GL_TEXTURE_2D:
//            {
//                initTex(gl, TexSubTarget.GL_TEXTURE_2D);
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
//                break;
//            }
//        }
//    }

//    private void init(GLWrapper gl)
//    {
////        surfInst = gl.getSurfaceInstanceNumber();
//
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
//        dirty = true;
//    }

    @Override
    public void bindTexture(CyGLContext ctx, CyGLWrapper gl)
    {
        TextureBufferInfo info = ctx.getTextureBufferInfo(this, gl);
//        if (id == 0 || gl.getSurfaceInstanceNumber() != surfInst)
//        {
//            init(gl);
//        }
        int texId = info.getTexId();


        gl.glBindTexture(target, texId);
        if (info.getDirty() < dirty)
        {
        //uploadData(gl);
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

//    public void dispose()
//    {
//        GLActionQueue.inst().postAction(new Dispose(id));
//        id = 0;
//        dataProvider.removeCyTextureDataProviderListener(this);
//    }

    @Override
    public void textureDataChanged(CyChangeEvent evt)
    {
        ++dirty;
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
