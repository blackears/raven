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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author kitfox
 */
public interface GLWrapper
{
    public void glActiveTexture(ActiveTexture texture);
    public void glAttachShader(int program, int shader);

    public void glBindAttribLocation(int program, int index, String name);
    public void glBindBuffer(BufferTarget target, int buffer);
    public void glBindFramebuffer(int framebuffer);
    public void glBindRenderbuffer(int renderbuffer);
    public void glBindTexture(TexTarget target, int texture);
    public void glBlendColor(float r, float g, float b, float a);
    public void glBlendEquation(BlendMode mode);
    public void glBlendEquationSeparate(BlendMode modeRgb, BlendMode modeAlpha);
    public void glBlendFunc(BlendFactor src, BlendFactor dst);
    public void glBlendFuncSeparate(BlendFactor srcRgb, BlendFactor dstRgb, BlendFactor srcAlpha, BlendFactor dstAlpha);
    public void glBufferData(BufferTarget target, int size, Buffer data, BufferUsage usage);

    public FramebufferStatus glCheckFramebufferStatus();
    public void glClear(boolean color, boolean depth, boolean stencil);
    public void glClearColor(float r, float g, float b, float a);
    public void glCompileShader(int shader);
    public int glCreateProgram();
    public int glCreateShader(ShaderType shaderType);

    public void glDeleteBuffers(int size, IntBuffer ibuf);
    public void glDeleteFramebuffers(int size, IntBuffer ibuf);
    public void glDeleteProgram(int program);
    public void glDeleteRenderbuffers(int size, IntBuffer ibuf);
    public void glDeleteShader(int shader);
    public void glDeleteTextures(int size, IntBuffer ibuf);
    public void glDepthFunc(DepthFunc func);
    public void glDepthMask(boolean flag);
    public void glDepthRangef(float nearVal, float farVal);
    public void glDisable(Capability cap);
    public void glDisableVertexAttribArray(int index);
    public void glDrawArrays(DrawMode mode, int first, int count);
    public void glDrawElements(DrawMode mode, int count, IndiciesType type, long offset);
    public void glDrawElements(DrawMode mode, int count, IndiciesType type, Buffer indices);

    public void glEnable(Capability cap);
    public void glEnableVertexAttribArray(int index);

    public void glGenBuffers(int size, IntBuffer ibuf);
    public void glGenFramebuffers(int size, IntBuffer ibuf);
    public void glGetProgramiv(int program, ProgramParamName pname, IntBuffer params);
    public void glGenRenderbuffers(int size, IntBuffer ibuf);
    public void glGenTextures(int size, IntBuffer ibuf);
    public void glGenerateMipmap(TexTarget target);
    public int glGetAttribLocation(int program, String name);
    public void glGetProgramInfoLog(int program, int bufSize,
            IntBuffer length, ByteBuffer infoLog);
    public void glGetTexImage(TexTarget target, int level,
            InternalFormatTex format, DataType type, Buffer buffer);
    public void glGetShaderInfoLog(int program, int bufSize,
            IntBuffer length, ByteBuffer infoLog);
    public void glGetShaderiv(int shader, ShaderParamName pname, IntBuffer params);
    public int glGetUniformLocation(int program, String name);

    public void glFramebufferRenderbuffer(
            Attachment attachment, int renderbuffer);
    public void glFramebufferTexture2D(
            Attachment attachment, TexSubTarget texTarget,
            int renderbuffer, int level);

    public void glLinkProgram(int program);

    public void glRenderbufferStorage(InternalFormatBuf internalFormat,
            int width, int height);

    public void glShaderSource(int shader, int count, String[] string, IntBuffer length);

    public void glTexImage2D(TexSubTarget target, int level,
            InternalFormatTex internalFormat,
            int width, int height,
            DataType type, Buffer data);
    public void glTexParameter(TexTarget target, TexParamName pname, TexParam param);

    public void glUniform1f(int location, float v0);
    public void glUniform2f(int location, float v0, float v1);
    public void glUniform3f(int location, float v0, float v1, float v2);
    public void glUniform4f(int location, float v0, float v1, float v2, float v3);
    public void glUniform1i(int location, int v0);
    public void glUniform2i(int location, int v0, int v1);
    public void glUniform3i(int location, int v0, int v1, int v2);
    public void glUniform4i(int location, int v0, int v1, int v2, int v3);
    public void glUniform1fv(int location, int count, FloatBuffer value);
    public void glUniform2fv(int location, int count, FloatBuffer value);
    public void glUniform3fv(int location, int count, FloatBuffer value);
    public void glUniform4fv(int location, int count, FloatBuffer value);
    public void glUniform1iv(int location, int count, IntBuffer value);
    public void glUniform2iv(int location, int count, IntBuffer value);
    public void glUniform3iv(int location, int count, IntBuffer value);
    public void glUniform4iv(int location, int count, IntBuffer value);
    public void glUniformMatrix2fv(int location, int count, boolean transpose, FloatBuffer value);
    public void glUniformMatrix3fv(int location, int count, boolean transpose, FloatBuffer value);
    public void glUniformMatrix4fv(int location, int count, boolean transpose, FloatBuffer value);
    public void glUseProgram(int program);

    public void glVertexAttribPointer(int index, int size, VertexDataType type,
            boolean normalized, int stride, long offset);
    public void glVertexAttribPointer(int index, int size, VertexDataType type,
            boolean normalized, int stride, Buffer pointer);
    public void glViewport(int x, int y, int width, int height);


    public String loadSource(String path);

    /**
     * Something horrible may happen that causes the current rendering surface
     * to be lost and all resources allocated with it destroyed.  When this
     * happens, you will need to reallocate everything you need from
     * the GL context.
     *
     * Each time the surface is recreated, this number increments.  If you're
     * generating textures, buffers or shaders, and this number has changed
     * since the last time you allocated, you will need to reallocate.
     *
     * @return
     */
    @Deprecated
    public int getSurfaceInstanceNumber();

    //--------------------------------------
    public static enum BlendMode
    {
        GL_FUNC_ADD,
        GL_FUNC_SUBTRACT,
        GL_FUNC_REVERSE_SUBTRACT
    }

    public static enum BlendFactor
    {
        GL_ZERO,
        GL_ONE,
        GL_SRC_COLOR,
        GL_ONE_MINUS_SRC_COLOR,
        GL_DST_COLOR,
        GL_ONE_MINUS_DST_COLOR,
        GL_SRC_ALPHA,
        GL_ONE_MINUS_SRC_ALPHA,
        GL_DST_ALPHA,
        GL_ONE_MINUS_DST_ALPHA,
        GL_CONSTANT_COLOR,
        GL_ONE_MINUS_CONSTANT_COLOR,
        GL_CONSTANT_ALPHA,
        GL_ONE_MINUS_CONSTANT_ALPHA,
        GL_SRC_ALPHA_SATURATE
    }

    public static enum FramebufferStatus
    {
        GL_FRAMEBUFFER_COMPLETE,
		GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT,
		GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS,
		GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER,
		GL_FRAMEBUFFER_INCOMPLETE_DUPLICATE_ATTACHMENT,
		GL_FRAMEBUFFER_INCOMPLETE_FORMATS,
        GL_FRAMEBUFFER_INCOMPLETE_LAYER_COUNT,
        GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS,
		GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT,
        GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE,
		GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER,
		GL_FRAMEBUFFER_UNSUPPORTED,
		UNKNOWN
    }

    public static enum ActiveTexture
    {
        GL_TEXTURE0,
        GL_TEXTURE1,
        GL_TEXTURE2,
        GL_TEXTURE3,
        GL_TEXTURE4,
        GL_TEXTURE5,
        GL_TEXTURE6,
        GL_TEXTURE7,
        GL_TEXTURE8,
        GL_TEXTURE9,
        GL_TEXTURE10,
        GL_TEXTURE11,
        GL_TEXTURE12,
        GL_TEXTURE13,
        GL_TEXTURE14,
        GL_TEXTURE15
    }

    public static enum DepthFunc
    {
        GL_NEVER,
        GL_LESS,
        GL_EQUAL,
        GL_LEQUAL,
        GL_GREATER,
        GL_NOTEQUAL,
        GL_GEQUAL,
        GL_ALWAYS
    }

    public static enum IndiciesType
    {
        GL_UNSIGNED_BYTE,
        GL_UNSIGNED_SHORT
    }

    public static enum DrawMode
    {
        GL_POINTS,
        GL_LINE_STRIP,
        GL_LINE_LOOP,
        GL_LINES,
        GL_TRIANGLE_STRIP,
        GL_TRIANGLE_FAN,
        GL_TRIANGLES
    }

    public static enum BufferUsage
    {
        GL_STREAM_DRAW,
        GL_STATIC_DRAW,
        GL_DYNAMIC_DRAW
    }

    public static enum Capability
    {
        GL_BLEND,
        GL_CULL_FACE,
        GL_DEPTH_TEST,
        GL_DITHER,
        GL_POLYGON_OFFSET_FILL,
        GL_SAMPLE_ALPHA_TO_COVERAGE,
        GL_SAMPLE_COVERAGE,
        GL_SCISSOR_TEST,
        GL_STENCIL_TEST
    }

    public static enum VertexDataType
    {
        GL_BYTE,
        GL_UNSIGNED_BYTE,
        GL_SHORT,
        GL_UNSIGNED_SHORT,
        GL_FIXED,
        GL_FLOAT
    }

    public static enum ProgramParamName
    {
        GL_DELETE_STATUS,
        GL_LINK_STATUS,
        GL_VALIDATE_STATUS,
        GL_INFO_LOG_LENGTH,
        GL_ATTACHED_SHADERS,
        GL_ACTIVE_ATTRIBUTES,
        GL_ACTIVE_ATTRIBUTE_MAX_LENGTH,
        GL_ACTIVE_UNIFORMS,
        GL_ACTIVE_UNIFORM_MAX_LENGTH
    }

    public static enum ShaderParamName
    {
        GL_SHADER_TYPE,
        GL_DELETE_STATUS,
        GL_COMPILE_STATUS,
        GL_INFO_LOG_LENGTH,
        GL_SHADER_SOURCE_LENGTH
    }

    public static enum ShaderType
    {
        GL_VERTEX_SHADER,
        GL_FRAGMENT_SHADER
    }

    public static enum TexParam
    {
        GL_NEAREST,
        GL_LINEAR,
        GL_NEAREST_MIPMAP_NEAREST,
        GL_LINEAR_MIPMAP_NEAREST,
        GL_NEAREST_MIPMAP_LINEAR,
        GL_LINEAR_MIPMAP_LINEAR,
        
        GL_CLAMP_TO_EDGE,
        GL_MIRRORED_REPEAT,
        GL_REPEAT
    }

    public static enum TexParamName
    {
        GL_TEXTURE_MIN_FILTER,
        GL_TEXTURE_MAG_FILTER,
        GL_TEXTURE_WRAP_S,
        GL_TEXTURE_WRAP_T
    }

    public static enum BufferTarget
    {
        GL_ARRAY_BUFFER,
        GL_ELEMENT_ARRAY_BUFFER
    }

    public static enum TexTarget
    {
        GL_TEXTURE_2D,
        GL_TEXTURE_CUBE_MAP
    }

    public static enum TexSubTarget
    {
        GL_TEXTURE_2D,
        GL_TEXTURE_CUBE_MAP_POSITIVE_X,
        GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
        GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
        GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
        GL_TEXTURE_CUBE_MAP_POSITIVE_Z,
        GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
    }

    public static enum Attachment
    {
        GL_COLOR_ATTACHMENT0,
        GL_DEPTH_ATTACHMENT,
        GL_STENCIL_ATTACHMENT
    }

    public static enum InternalFormatBuf
    {
        GL_RGBA4,
        GL_RGB565,
        GL_RGB5_A1,
        GL_DEPTH_COMPONENT16,
        GL_STENCIL_INDEX8
    }

    public static enum InternalFormatTex
    {
        GL_ALPHA,
        GL_LUMINANCE,
        GL_LUMINANCE_ALPHA,
        GL_RGB,
        GL_RGBA
    }

    public static enum DataType
    {
        GL_UNSIGNED_BYTE,
        GL_UNSIGNED_SHORT_5_6_5,
        GL_UNSIGNED_SHORT_4_4_4_4,
        GL_UNSIGNED_SHORT_5_5_5_1
    }
}
