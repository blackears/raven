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

package com.kitfox.coyote.renderer.jogl;

import com.kitfox.coyote.renderer.GLWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

/**
 *
 * @author kitfox
 */
public class GLWrapperJOGL implements GLWrapper
{
    private final GLAutoDrawable drawable;
    private final GL2 gl;

    private final int surfaceInstanceNumber;

    public GLWrapperJOGL(GLAutoDrawable drawable, int surfaceInstanceNumber)
    {
        this.drawable = drawable;
        this.gl = drawable.getGL().getGL2();
        this.surfaceInstanceNumber = surfaceInstanceNumber;
    }

    @Override
    public void glActiveTexture(ActiveTexture texture)
    {
        gl.glActiveTexture(getActiveTextureMask(texture));
    }

    @Override
    public void glAttachShader(int program, int shader)
    {
        gl.glAttachShader(program, shader);
    }

    @Override
    public void glBlendColor(float r, float g, float b, float a)
    {
        gl.glBlendColor(r, g, b, a);
    }

    @Override
    public void glBlendEquation(BlendMode mode)
    {
        gl.glBlendEquation(getBlendModeMask(mode));
    }

    @Override
    public void glBlendEquationSeparate(BlendMode modeRgb, BlendMode modeAlpha)
    {
        gl.glBlendEquationSeparate(getBlendModeMask(modeRgb), getBlendModeMask(modeAlpha));
    }

    @Override
    public void glBlendFunc(BlendFactor src, BlendFactor dst)
    {
        gl.glBlendFunc(getBlendFactorMask(src), getBlendFactorMask(dst));
    }

    @Override
    public void glBlendFuncSeparate(BlendFactor srcRgb, BlendFactor dstRgb, BlendFactor srcAlpha, BlendFactor dstAlpha)
    {
        gl.glBlendFuncSeparate(
                getBlendFactorMask(srcRgb), getBlendFactorMask(dstRgb),
                getBlendFactorMask(srcAlpha), getBlendFactorMask(dstAlpha)
                );
    }

    @Override
    public void glBindAttribLocation(int program, int index, String name)
    {
        gl.glBindAttribLocation(program, index, name);
    }

    @Override
    public void glBindBuffer(BufferTarget target, int buffer)
    {
        gl.glBindBuffer(getBufferTargetMask(target), buffer);
    }

    @Override
    public void glBindFramebuffer(int framebuffer)
    {
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, framebuffer);
    }

    @Override
    public void glBindRenderbuffer(int renderbuffer)
    {
        gl.glBindRenderbuffer(GL2.GL_RENDERBUFFER, renderbuffer);
    }

    @Override
    public void glBindTexture(TexTarget target, int texture)
    {
        gl.glBindTexture(getTexTargetMask(target), texture);
    }

    @Override
    public void glBufferData(BufferTarget target, int size, Buffer data, BufferUsage usage)
    {
        gl.glBufferData(getBufferTargetMask(target), size, data,
                getBufferUsage(usage));
    }


    @Override
    public FramebufferStatus glCheckFramebufferStatus()
    {
        int val = gl.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER);
        switch (val)
        {
            case GL2.GL_FRAMEBUFFER_COMPLETE:
                return FramebufferStatus.GL_FRAMEBUFFER_COMPLETE;
            case GL2.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
                return FramebufferStatus.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT;
            case GL2.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
                return FramebufferStatus.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS;
            case GL2.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
                return FramebufferStatus.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER;
//            case GL2.GL_FRAMEBUFFER_INCOMPLETE_DUPLICATE_ATTACHMENT:
//                return FramebufferStatus.GL_FRAMEBUFFER_INCOMPLETE_DUPLICATE_ATTACHMENT;
            case GL2.GL_FRAMEBUFFER_INCOMPLETE_FORMATS:
                return FramebufferStatus.GL_FRAMEBUFFER_INCOMPLETE_FORMATS;
            case GL2.GL_FRAMEBUFFER_INCOMPLETE_LAYER_COUNT_EXT:
                return FramebufferStatus.GL_FRAMEBUFFER_INCOMPLETE_LAYER_COUNT;
            case GL2.GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS_EXT:
                return FramebufferStatus.GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS;
            case GL2.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
                return FramebufferStatus.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT;
            case GL2.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE:
                return FramebufferStatus.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE;
            case GL2.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
                return FramebufferStatus.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER;
            case GL2.GL_FRAMEBUFFER_UNSUPPORTED:
                return FramebufferStatus.GL_FRAMEBUFFER_UNSUPPORTED;
            default:
                throw new RuntimeException("Unknown framebuffer status: " + val);
                //return FramebufferStatus.UNKNOWN;
        }
    }

    @Override
    public void glClear(boolean color, boolean depth, boolean stencil)
    {
        int mask = (color ? GL.GL_COLOR_BUFFER_BIT : 0)
                | (depth ? GL.GL_DEPTH_BUFFER_BIT : 0)
                | (stencil ? GL.GL_STENCIL_BUFFER_BIT : 0);
        gl.glClear(mask);
    }

    @Override
    public void glClearColor(float r, float g, float b, float a)
    {
        gl.glClearColor(r, g, b, a);
    }

    @Override
    public void glCompileShader(int shader)
    {
        gl.glCompileShader(shader);
    }

    @Override
    public int glCreateProgram()
    {
        return gl.glCreateProgram();
    }

    @Override
    public int glCreateShader(ShaderType shaderType)
    {
        return gl.glCreateShader(getShaderTypeMask(shaderType));
    }

    @Override
    public void glDeleteBuffers(int size, IntBuffer ibuf)
    {
        gl.glDeleteBuffers(size, ibuf);
    }

    @Override
    public void glDeleteFramebuffers(int size, IntBuffer ibuf)
    {
        gl.glDeleteFramebuffers(size, ibuf);
    }

    @Override
    public void glDeleteProgram(int program)
    {
        gl.glDeleteProgram(program);
    }

    @Override
    public void glDeleteRenderbuffers(int size, IntBuffer ibuf)
    {
        gl.glDeleteRenderbuffers(size, ibuf);
    }

    @Override
    public void glDeleteShader(int shader)
    {
        gl.glDeleteShader(shader);
    }

    @Override
    public void glDeleteTextures(int size, IntBuffer ibuf)
    {
        gl.glDeleteTextures(size, ibuf);
    }

    @Override
    public void glDepthFunc(DepthFunc func)
    {
        gl.glDepthFunc(getDepthFuncMask(func));
    }

    @Override
    public void glDepthMask(boolean flag)
    {
        gl.glDepthMask(flag);
    }

    @Override
    public void glDepthRangef(float nearVal, float farVal)
    {
        gl.glDepthRange(nearVal, farVal);
    }

    @Override
    public void glDisable(Capability cap)
    {
        gl.glDisable(getCapabilityMask(cap));
    }

    @Override
    public void glDisableVertexAttribArray(int index)
    {
        gl.glDisableVertexAttribArray(index);
    }

    @Override
    public void glDrawArrays(DrawMode mode, int first, int count)
    {
        gl.glDrawArrays(glDrawModeMask(mode), first, count);
    }

    @Override
    public void glDrawElements(DrawMode mode, int count, IndiciesType type, Buffer indices)
    {
        gl.glDrawElements(glDrawModeMask(mode), count,
                getIndiciesTypeMask(type), indices);
    }

    @Override
    public void glDrawElements(DrawMode mode, int count, IndiciesType type, long offset)
    {
        gl.glDrawElements(glDrawModeMask(mode), count,
                getIndiciesTypeMask(type), offset);
    }

    @Override
    public void glEnable(Capability cap)
    {
        gl.glEnable(getCapabilityMask(cap));
    }

    @Override
    public void glEnableVertexAttribArray(int index)
    {
        gl.glEnableVertexAttribArray(index);
    }

    @Override
    public void glGenBuffers(int size, IntBuffer ibuf)
    {
        gl.glGenBuffers(size, ibuf);
    }

    @Override
    public void glGenFramebuffers(int size, IntBuffer ibuf)
    {
        gl.glGenFramebuffers(size, ibuf);
    }

    @Override
    public int glGetAttribLocation(int program, String name)
    {
        return gl.glGetAttribLocation(program, name);
    }

    @Override
    public void glGetProgramInfoLog(int program, int bufSize,
            IntBuffer length, ByteBuffer infoLog)
    {
        gl.glGetProgramInfoLog(program, bufSize, length, infoLog);
    }

    @Override
    public void glGetProgramiv(int program, ProgramParamName pname, IntBuffer params)
    {
        gl.glGetProgramiv(program, getProgramParamNameMask(pname), params);
    }

    @Override
    public void glGetTexImage(TexTarget target, int level,
            InternalFormatTex format, DataType type, Buffer buffer)
    {
        gl.glGetTexImage(getTexTargetMask(target), level,
                getInternalFormatTexMask(format), getDataTypeMask(type), buffer);
    }
    @Override
    public void glGenRenderbuffers(int size, IntBuffer ibuf)
    {
        gl.glGenRenderbuffers(size, ibuf);
    }

    @Override
    public void glGenTextures(int size, IntBuffer ibuf)
    {
        gl.glGenTextures(size, ibuf);
    }

    @Override
    public void glGenerateMipmap(TexTarget target)
    {
        gl.glGenerateMipmap(getTexTargetMask(target));
    }

    @Override
    public void glGetShaderInfoLog(int program, int bufSize,
            IntBuffer length, ByteBuffer infoLog)
    {
        gl.glGetShaderInfoLog(program, bufSize, length, infoLog);
    }

    @Override
    public void glGetShaderiv(int shader, ShaderParamName pname, IntBuffer params)
    {
        gl.glGetShaderiv(shader, getShaderParamNameMask(pname), params);
    }

    @Override
    public int glGetUniformLocation(int program, String name)
    {
        return gl.glGetUniformLocation(program, name);
    }

    @Override
    public void glFramebufferRenderbuffer(Attachment attachment, int renderbuffer)
    {
        gl.glFramebufferRenderbuffer(
                GL.GL_FRAMEBUFFER, getAttachmentMask(attachment),
                GL.GL_RENDERBUFFER, renderbuffer);
    }

    @Override
    public void glFramebufferTexture2D(Attachment attachment, TexSubTarget texTarget, int texture, int level)
    {
        gl.glFramebufferTexture2D(
                GL2.GL_FRAMEBUFFER, getAttachmentMask(attachment),
                getTexSubTargetMask(texTarget), texture, level);
    }

    @Override
    public void glLinkProgram(int program)
    {
        gl.glLinkProgram(program);
    }

    @Override
    public void glRenderbufferStorage(InternalFormatBuf internalFormat, int width, int height)
    {
        gl.glRenderbufferStorage(
                GL.GL_RENDERBUFFER,
                getInternalFormatBufMask(internalFormat),
                width, height);
    }

//    @Override
//    public void glShaderSource(int shader, String source)
//    {
////        IntBuffer ibuf = BufferUtil.allocateInt(1);
////        ibuf.put(0, source.length());
////        gl.glShaderSource(shader, 1, new String[]{source}, ibuf);
//        gl.glShaderSource(shader, 1, new String[]{source}, null);
//    }

    @Override
    public void glShaderSource(int shader, int count, String[] string, IntBuffer length)
    {
        gl.glShaderSource(shader, count, string, null);
    }

    @Override
    public void glTexImage2D(TexSubTarget target, int level, InternalFormatTex internalFormat, int width, int height, DataType type, Buffer data)
    {
        gl.glTexImage2D(
                getTexSubTargetMask(target),
                level,
                getInternalFormatTexMask(internalFormat),
                width, height, 0,
                getInternalFormatTexMask(internalFormat),
                getDataTypeMask(type),
                data);
    }

    @Override
    public void glTexParameter(TexTarget target, TexParamName pname, TexParam param)
    {
        gl.glTexParameteri(getTexTargetMask(target),
                getTexParamNameMask(pname),
                getTexParamMask(param));
    }

    @Override
    public void glUniform1f(int location, float v0)
    {
        gl.glUniform1f(location, v0);
    }

    @Override
    public void glUniform2f(int location, float v0, float v1)
    {
        gl.glUniform2f(location, v0, v1);
    }

    @Override
    public void glUniform3f(int location, float v0, float v1, float v2)
    {
        gl.glUniform3f(location, v0, v1, v2);
    }

    @Override
    public void glUniform4f(int location, float v0, float v1, float v2, float v3)
    {
        gl.glUniform4f(location, v0, v1, v2, v3);
    }

    @Override
    public void glUniform1i(int location, int v0)
    {
        gl.glUniform1i(location, v0);
    }

    @Override
    public void glUniform2i(int location, int v0, int v1)
    {
        gl.glUniform2i(location, v0, v1);
    }

    @Override
    public void glUniform3i(int location, int v0, int v1, int v2)
    {
        gl.glUniform3i(location, v0, v1, v2);
    }

    @Override
    public void glUniform4i(int location, int v0, int v1, int v2, int v3)
    {
        gl.glUniform4i(location, v0, v1, v2, v3);
    }

    @Override
    public void glUniform1fv(int location, int count, FloatBuffer value)
    {
        gl.glUniform1fv(location, count, value);
    }

    @Override
    public void glUniform2fv(int location, int count, FloatBuffer value)
    {
        gl.glUniform2fv(location, count, value);
    }

    @Override
    public void glUniform3fv(int location, int count, FloatBuffer value)
    {
        gl.glUniform3fv(location, count, value);
    }

    @Override
    public void glUniform4fv(int location, int count, FloatBuffer value)
    {
        gl.glUniform4fv(location, count, value);
    }

    @Override
    public void glUniform1iv(int location, int count, IntBuffer value)
    {
        gl.glUniform1iv(location, count, value);
    }

    @Override
    public void glUniform2iv(int location, int count, IntBuffer value)
    {
        gl.glUniform2iv(location, count, value);
    }

    @Override
    public void glUniform3iv(int location, int count, IntBuffer value)
    {
        gl.glUniform3iv(location, count, value);
    }

    @Override
    public void glUniform4iv(int location, int count, IntBuffer value)
    {
        gl.glUniform4iv(location, count, value);
    }

    @Override
    public void glUniformMatrix2fv(int location, int count, boolean transpose, FloatBuffer value)
    {
        if (transpose)
        {
            throw new RuntimeException("transpose must be false");
        }
        gl.glUniformMatrix2fv(location, count, transpose, value);
    }

    @Override
    public void glUniformMatrix3fv(int location, int count, boolean transpose, FloatBuffer value)
    {
        if (transpose)
        {
            throw new RuntimeException("transpose must be false");
        }
        gl.glUniformMatrix3fv(location, count, transpose, value);
    }

    @Override
    public void glUniformMatrix4fv(int location, int count, boolean transpose, FloatBuffer value)
    {
        if (transpose)
        {
            throw new RuntimeException("transpose must be false");
        }
        gl.glUniformMatrix4fv(location, count, transpose, value);
    }

    @Override
    public void glUseProgram(int program)
    {
        gl.glUseProgram(program);
    }

    @Override
    public void glVertexAttribPointer(int index, int size, VertexDataType type,
            boolean normalized, int stride, long offset)
    {
        gl.glVertexAttribPointer(index, size, getVertexDataTypeMask(type),
                normalized, stride, offset);
    }

    @Override
    public void glVertexAttribPointer(int index, int size, VertexDataType type,
            boolean normalized, int stride, Buffer pointer)
    {
        gl.glVertexAttribPointer(index, size, getVertexDataTypeMask(type),
                normalized, stride, pointer);
    }

    @Override
    public void glViewport(int x, int y, int width, int height)
    {
        drawable.getGL().glViewport(x, y, 
                Math.max(1, width),
                Math.max(1, height));
    }




    @Override
    public String loadSource(String path)
    {
        InputStream is = getClass().getResourceAsStream(path);
        byte[] buf = new byte[0x8000];
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        try
        {
            for (int len = is.read(buf); len != -1; len = is.read(buf))
            {
                bout.write(buf, 0, len);
            }

            return bout.toString();
        } catch (IOException ex)
        {
            Logger.getLogger(GLWrapperJOGL.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }


    /**
     * @return the reaquiredSurface
     */
    @Override
    public int getSurfaceInstanceNumber()
    {
        return surfaceInstanceNumber;
    }



    private int getBlendModeMask(BlendMode blendMode)
    {
        switch (blendMode)
        {
            case GL_FUNC_ADD:
                return GL.GL_FUNC_ADD;
            case GL_FUNC_REVERSE_SUBTRACT:
                return GL.GL_FUNC_REVERSE_SUBTRACT;
            case GL_FUNC_SUBTRACT:
                return GL.GL_FUNC_SUBTRACT;
            default:
                throw new RuntimeException();
        }
    }

    private int getBlendFactorMask(BlendFactor blendMode)
    {
        switch (blendMode)
        {
            case GL_CONSTANT_ALPHA:
                return GL2.GL_CONSTANT_ALPHA;
            case GL_CONSTANT_COLOR:
                return GL2.GL_CONSTANT_COLOR;
            case GL_DST_ALPHA:
                return GL.GL_DST_ALPHA;
            case GL_DST_COLOR:
                return GL.GL_DST_COLOR;
            case GL_ONE:
                return GL.GL_ONE;
            case GL_ONE_MINUS_CONSTANT_ALPHA:
                return GL2.GL_ONE_MINUS_CONSTANT_ALPHA;
            case GL_ONE_MINUS_CONSTANT_COLOR:
                return GL2.GL_ONE_MINUS_CONSTANT_COLOR;
            case GL_ONE_MINUS_DST_ALPHA:
                return GL.GL_ONE_MINUS_DST_ALPHA;
            case GL_ONE_MINUS_DST_COLOR:
                return GL.GL_ONE_MINUS_DST_COLOR;
            case GL_ONE_MINUS_SRC_ALPHA:
                return GL.GL_ONE_MINUS_SRC_ALPHA;
            case GL_ONE_MINUS_SRC_COLOR:
                return GL.GL_ONE_MINUS_SRC_COLOR;
            case GL_SRC_ALPHA:
                return GL.GL_SRC_ALPHA;
            case GL_SRC_ALPHA_SATURATE:
                return GL.GL_SRC_ALPHA_SATURATE;
            case GL_SRC_COLOR:
                return GL.GL_SRC_COLOR;
            case GL_ZERO:
                return GL.GL_ZERO;
            default:
                throw new RuntimeException();
        }
    }
    
    private int getAttachmentMask(Attachment attachment)
    {
        switch (attachment)
        {
            case GL_COLOR_ATTACHMENT0:
                return GL.GL_COLOR_ATTACHMENT0;
            case GL_DEPTH_ATTACHMENT:
                return GL.GL_DEPTH_ATTACHMENT;
            case GL_STENCIL_ATTACHMENT:
                return GL.GL_STENCIL_ATTACHMENT;
            default:
                throw new RuntimeException();
        }
    }

    private int getDataTypeMask(DataType dataType)
    {
        switch (dataType)
        {
            case GL_UNSIGNED_BYTE:
                return GL.GL_UNSIGNED_BYTE;
            case GL_UNSIGNED_SHORT_4_4_4_4:
                return GL.GL_UNSIGNED_SHORT_4_4_4_4;
            case GL_UNSIGNED_SHORT_5_5_5_1:
                return GL.GL_UNSIGNED_SHORT_5_5_5_1;
            case GL_UNSIGNED_SHORT_5_6_5:
                return GL.GL_UNSIGNED_SHORT_5_6_5;
            default:
                throw new RuntimeException();
        }
    }

    private int getInternalFormatBufMask(InternalFormatBuf format)
    {
        switch (format)
        {
            case GL_DEPTH_COMPONENT16:
//                return GL.GL_DEPTH_COMPONENT16;
                return GL2.GL_DEPTH_COMPONENT;
            case GL_RGB565:
                return GL2.GL_RGB5;
            case GL_RGB5_A1:
                return GL.GL_RGB5_A1;
            case GL_RGBA4:
//                return GL.GL_RGBA4;
                return GL.GL_RGBA;
            case GL_STENCIL_INDEX8:
                return GL2.GL_STENCIL_INDEX8;
            default:
                throw new RuntimeException();
        }
    }

    private int getInternalFormatTexMask(InternalFormatTex format)
    {
        switch (format)
        {
            case GL_ALPHA:
                return GL.GL_ALPHA;
            case GL_LUMINANCE:
                return GL.GL_LUMINANCE;
            case GL_LUMINANCE_ALPHA:
                return GL.GL_LUMINANCE_ALPHA;
            case GL_RGB:
                return GL.GL_RGB;
            case GL_RGBA:
                return GL.GL_RGBA;
            default:
                throw new RuntimeException();
        }
    }

    private int getTexSubTargetMask(TexSubTarget texTarget)
    {
        switch (texTarget)
        {
            case GL_TEXTURE_2D:
                return GL.GL_TEXTURE_2D;
            case GL_TEXTURE_CUBE_MAP_NEGATIVE_X:
                return GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
            case GL_TEXTURE_CUBE_MAP_NEGATIVE_Y:
                return GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
            case GL_TEXTURE_CUBE_MAP_NEGATIVE_Z:
                return GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
            case GL_TEXTURE_CUBE_MAP_POSITIVE_X:
                return GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
            case GL_TEXTURE_CUBE_MAP_POSITIVE_Y:
                return GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
            case GL_TEXTURE_CUBE_MAP_POSITIVE_Z:
                return GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
            default:
                throw new RuntimeException();
        }
    }

    private int getBufferTargetMask(BufferTarget bufferTarget)
    {
        switch (bufferTarget)
        {
            case GL_ARRAY_BUFFER:
                return GL.GL_ARRAY_BUFFER;
            case GL_ELEMENT_ARRAY_BUFFER:
                return GL.GL_ELEMENT_ARRAY_BUFFER;
            default:
                throw new RuntimeException();
        }
    }

    private int getTexTargetMask(TexTarget texTarget)
    {
        switch (texTarget)
        {
            case GL_TEXTURE_2D:
                return GL.GL_TEXTURE_2D;
            case GL_TEXTURE_CUBE_MAP:
                return GL.GL_TEXTURE_CUBE_MAP;
            default:
                throw new RuntimeException();
        }
    }

    private int getTexParamNameMask(TexParamName texParamName)
    {
        switch (texParamName)
        {
            case GL_TEXTURE_MAG_FILTER:
                return GL.GL_TEXTURE_MAG_FILTER;
            case GL_TEXTURE_MIN_FILTER:
                return GL.GL_TEXTURE_MIN_FILTER;
            case GL_TEXTURE_WRAP_S:
                return GL.GL_TEXTURE_WRAP_S;
            case GL_TEXTURE_WRAP_T:
                return GL.GL_TEXTURE_WRAP_T;
            default:
                throw new RuntimeException();
        }
    }

    private int getTexParamMask(TexParam texParam)
    {
        switch (texParam)
        {
            case GL_CLAMP_TO_EDGE:
                return GL.GL_CLAMP_TO_EDGE;
            case GL_LINEAR:
                return GL.GL_LINEAR;
            case GL_LINEAR_MIPMAP_LINEAR:
                return GL.GL_LINEAR_MIPMAP_LINEAR;
            case GL_LINEAR_MIPMAP_NEAREST:
                return GL.GL_LINEAR_MIPMAP_NEAREST;
            case GL_MIRRORED_REPEAT:
                return GL.GL_MIRRORED_REPEAT;
            case GL_NEAREST:
                return GL.GL_NEAREST;
            case GL_NEAREST_MIPMAP_LINEAR:
                return GL.GL_NEAREST_MIPMAP_LINEAR;
            case GL_NEAREST_MIPMAP_NEAREST:
                return GL.GL_NEAREST_MIPMAP_NEAREST;
            case GL_REPEAT:
                return GL.GL_REPEAT;
            default:
                throw new RuntimeException();
        }
    }

    private int getShaderTypeMask(ShaderType shaderType)
    {
        switch (shaderType)
        {
            case GL_FRAGMENT_SHADER:
                return GL2.GL_FRAGMENT_SHADER;
            case GL_VERTEX_SHADER:
                return GL2.GL_VERTEX_SHADER;
            default:
                throw new RuntimeException();
        }
    }

    private int getShaderParamNameMask(ShaderParamName shaderParamName)
    {
        switch (shaderParamName)
        {
            case GL_COMPILE_STATUS:
                return GL2.GL_COMPILE_STATUS;
            case GL_DELETE_STATUS:
                return GL2.GL_DELETE_STATUS;
            case GL_INFO_LOG_LENGTH:
                return GL2.GL_INFO_LOG_LENGTH;
            case GL_SHADER_SOURCE_LENGTH:
                return GL2.GL_SHADER_SOURCE_LENGTH;
            case GL_SHADER_TYPE:
                return GL2.GL_SHADER_TYPE;
            default:
                throw new RuntimeException();
        }
    }

    private int getProgramParamNameMask(ProgramParamName programParamName)
    {
        switch (programParamName)
        {
            case GL_ACTIVE_ATTRIBUTES:
                return GL2.GL_ACTIVE_ATTRIBUTES;
            case GL_ACTIVE_ATTRIBUTE_MAX_LENGTH:
                return GL2.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH;
            case GL_ACTIVE_UNIFORMS:
                return GL2.GL_ACTIVE_UNIFORMS;
            case GL_ACTIVE_UNIFORM_MAX_LENGTH:
                return GL2.GL_ACTIVE_UNIFORM_MAX_LENGTH;
            case GL_ATTACHED_SHADERS:
                return GL2.GL_ATTACHED_SHADERS;
            case GL_DELETE_STATUS:
                return GL2.GL_DELETE_STATUS;
            case GL_INFO_LOG_LENGTH:
                return GL2.GL_INFO_LOG_LENGTH;
            case GL_LINK_STATUS:
                return GL2.GL_LINK_STATUS;
            case GL_VALIDATE_STATUS:
                return GL2.GL_VALIDATE_STATUS;
            default:
                throw new RuntimeException();
        }
    }

    private int getVertexDataTypeMask(VertexDataType vertexDataType)
    {
        switch (vertexDataType)
        {
            case GL_BYTE:
                return GL.GL_BYTE;
            case GL_FIXED:
                return GL2.GL_FIXED_ONLY;
            case GL_FLOAT:
                return GL.GL_FLOAT;
            case GL_SHORT:
                return GL.GL_SHORT;
            case GL_UNSIGNED_BYTE:
                return GL.GL_UNSIGNED_BYTE;
            case GL_UNSIGNED_SHORT:
                return GL.GL_UNSIGNED_SHORT;
            default:
                throw new RuntimeException();
        }
    }

    private int getCapabilityMask(Capability capability)
    {
        switch (capability)
        {
            case GL_BLEND:
                return GL.GL_BLEND;
            case GL_CULL_FACE:
                return GL.GL_CULL_FACE;
            case GL_DEPTH_TEST:
                return GL.GL_DEPTH_TEST;
            case GL_DITHER:
                return GL.GL_DITHER;
            case GL_POLYGON_OFFSET_FILL:
                return GL.GL_POLYGON_OFFSET_FILL;
            case GL_SAMPLE_ALPHA_TO_COVERAGE:
                return GL.GL_SAMPLE_ALPHA_TO_COVERAGE;
            case GL_SAMPLE_COVERAGE:
                return GL.GL_SAMPLE_COVERAGE;
            case GL_SCISSOR_TEST:
                return GL.GL_SCISSOR_TEST;
            case GL_STENCIL_TEST:
                return GL.GL_STENCIL_TEST;
            default:
                throw new RuntimeException();
        }
    }

    private int getBufferUsage(BufferUsage bufferUsage)
    {
        switch (bufferUsage)
        {
            case GL_DYNAMIC_DRAW:
                return GL.GL_DYNAMIC_DRAW;
            case GL_STATIC_DRAW:
                return GL.GL_STATIC_DRAW;
            case GL_STREAM_DRAW:
                return GL2.GL_STREAM_DRAW;
            default:
                throw new RuntimeException();
        }
    }

    private int glDrawModeMask(DrawMode drawMode)
    {
        switch (drawMode)
        {
            case GL_LINES:
                return GL.GL_LINES;
            case GL_LINE_LOOP:
                return GL.GL_LINE_LOOP;
            case GL_LINE_STRIP:
                return GL.GL_LINE_STRIP;
            case GL_POINTS:
                return GL.GL_POINTS;
            case GL_TRIANGLES:
                return GL.GL_TRIANGLES;
            case GL_TRIANGLE_FAN:
                return GL.GL_TRIANGLE_FAN;
            case GL_TRIANGLE_STRIP:
                return GL.GL_TRIANGLE_STRIP;
            default:
                throw new RuntimeException();
        }
    }

    private int getIndiciesTypeMask(IndiciesType indiciesType)
    {
        switch (indiciesType)
        {
            case GL_UNSIGNED_BYTE:
                return GL.GL_UNSIGNED_BYTE;
            case GL_UNSIGNED_SHORT:
                return GL.GL_UNSIGNED_SHORT;
            default:
                throw new RuntimeException();
        }
    }

    private int getDepthFuncMask(DepthFunc depthFunc)
    {
        switch (depthFunc)
        {
            case GL_ALWAYS:
                return GL.GL_ALWAYS;
            case GL_EQUAL:
                return GL.GL_EQUAL;
            case GL_GEQUAL:
                return GL.GL_GEQUAL;
            case GL_GREATER:
                return GL.GL_GREATER;
            case GL_LEQUAL:
                return GL.GL_LEQUAL;
            case GL_LESS:
                return GL.GL_LESS;
            case GL_NEVER:
                return GL.GL_NEVER;
            case GL_NOTEQUAL:
                return GL.GL_NOTEQUAL;
            default:
                throw new RuntimeException();
        }
    }

    private int getActiveTextureMask(ActiveTexture activeTexture)
    {
        switch (activeTexture)
        {
            case GL_TEXTURE0:
                return GL.GL_TEXTURE0;
            case GL_TEXTURE1:
                return GL.GL_TEXTURE1;
            case GL_TEXTURE2:
                return GL.GL_TEXTURE2;
            case GL_TEXTURE3:
                return GL.GL_TEXTURE3;
            case GL_TEXTURE4:
                return GL.GL_TEXTURE4;
            case GL_TEXTURE5:
                return GL.GL_TEXTURE5;
            case GL_TEXTURE6:
                return GL.GL_TEXTURE6;
            case GL_TEXTURE7:
                return GL.GL_TEXTURE7;
            case GL_TEXTURE8:
                return GL.GL_TEXTURE8;
            case GL_TEXTURE9:
                return GL.GL_TEXTURE9;
            case GL_TEXTURE10:
                return GL.GL_TEXTURE10;
            case GL_TEXTURE11:
                return GL.GL_TEXTURE11;
            case GL_TEXTURE12:
                return GL.GL_TEXTURE12;
            case GL_TEXTURE13:
                return GL.GL_TEXTURE13;
            case GL_TEXTURE14:
                return GL.GL_TEXTURE14;
            case GL_TEXTURE15:
                return GL.GL_TEXTURE15;
            default:
                throw new RuntimeException();
        }
    }

    /**
     * @return the drawable
     */
    public GLAutoDrawable getDrawable()
    {
        return drawable;
    }

    /**
     * @return the gl
     */
    public GL getGl()
    {
        return gl;
    }

}
