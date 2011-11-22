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
public interface CyGLWrapper
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
    public void glBufferSubData(BufferTarget target, int offset, int size, Buffer data);

    public FramebufferStatus glCheckFramebufferStatus();
    public void glClear(boolean color, boolean depth, boolean stencil);
    public void glClearColor(float r, float g, float b, float a);
    public void glClearDepthf(float depth);
    public void glClearStencil(int s);
    public void glColorMask(boolean r, boolean g, boolean b, boolean a);
    public void glCompileShader(int shader);
    public void glCompressedTexImage2D(TexSubTarget target, int level, 
            InternalFormatTex internalFormat, int width, int height, 
            int border, int imageSize, Buffer data);
    public void glCompressedTexSubImage2D(TexSubTarget target, int level, 
            int xoffset, int yoffset, int width, int height, 
            InternalFormatTex internalFormat, int imageSize, Buffer data);
    public void glCopyTexImage2D(TexSubTarget target, int level, 
            InternalFormatTex internalFormat, 
            int x, int y, int width, int height, 
            int border);
    public void glCopyTexSubImage2D(TexSubTarget target, int level, 
            int xoffset, int yoffset, 
            int x, int y, int width, int height);
    public int glCreateProgram();
    public int glCreateShader(ShaderType shaderType);
    public void glCullFace(CullFaceMode mode);

    public void glDeleteBuffers(int size, IntBuffer ibuf);
    public void glDeleteFramebuffers(int size, IntBuffer ibuf);
    public void glDeleteProgram(int program);
    public void glDeleteRenderbuffers(int size, IntBuffer ibuf);
    public void glDeleteShader(int shader);
    public void glDeleteTextures(int size, IntBuffer ibuf);
    public void glDepthFunc(DepthFunc func);
    public void glDepthMask(boolean flag);
    public void glDepthRangef(float nearVal, float farVal);
    public void glDetachShader(int program, int shader);
    public void glDisable(Capability cap);
    public void glDisableVertexAttribArray(int index);
    public void glDrawArrays(DrawMode mode, int first, int count);
    public void glDrawElements(DrawMode mode, int count, IndiciesType type, long offset);
    public void glDrawElements(DrawMode mode, int count, IndiciesType type, Buffer indices);

    public void glEnable(Capability cap);
    public void glEnableVertexAttribArray(int index);

    public void glFinish();
    public void glFlush();
    public void glFramebufferRenderbuffer(
            Attachment attachment, int renderbuffer);
    public void glFramebufferTexture2D(
            Attachment attachment, TexSubTarget texTarget,
            int renderbuffer, int level);
    public void glFrontFace(FrontFaceMode mode);

    public void glGenBuffers(int size, IntBuffer ibuf);
    public void glGenFramebuffers(int size, IntBuffer ibuf);
    public void glGenRenderbuffers(int size, IntBuffer ibuf);
    public void glGenTextures(int size, IntBuffer ibuf);
    public void glGenerateMipmap(TexTarget target);
    public void glGetBooleanv(GetParam param, ByteBuffer bbuf);
    public void glGetFloatv(GetParam param, FloatBuffer bbuf);
    public void glGetIntegerv(GetParam param, IntBuffer bbuf);
    public void glGetActiveAttrib(int program, int index,
            int bufSize, IntBuffer length, IntBuffer size, IntBuffer type,
            ByteBuffer name);
    public void glGetActiveUniform(int program, int index,
            int bufSize, IntBuffer length, IntBuffer size, IntBuffer type,
            ByteBuffer name);
    public void glGetAttachedShaders(int program, int maxCount,
            IntBuffer count, IntBuffer shaders);
    public int glGetAttribLocation(int program, String name);
    public void glGetBufferParameteriv(BufferTarget target, BufferValue value, IntBuffer data);
    public ErrorType glGetError();
    public void glGetFramebufferAttachmentParameteriv(
            Attachment attachment, FramebufferAttachmentParameter pname, IntBuffer params);
    public void glGetProgramInfoLog(int program, int bufSize,
            IntBuffer length, ByteBuffer infoLog);
    public void glGetProgramiv(int program, ProgramParamName pname, IntBuffer params);
    public void glGetRenderbufferParameteriv(
            RenderbufferParameter pname, IntBuffer params);
    public void glGetShaderInfoLog(int program, int bufSize,
            IntBuffer length, ByteBuffer infoLog);
    public void glGetShaderPrecisionFormat(ShaderType shaderType,
            PrecisionType precisionType, IntBuffer range, IntBuffer precision);
    public void glGetShaderSource(int shader, int bufSize, IntBuffer length, ByteBuffer source);
    public void glGetShaderiv(int shader, ShaderParamName pname, IntBuffer params);
    public String glGetString(StringName name);
    public void glGetTexParameterfv(TexTarget target, TexParamName param, FloatBuffer params);
    public void glGetTexParameteriv(TexTarget target, TexParamName param, IntBuffer params);
    public void glGetTexImage(TexTarget target, int level,
            InternalFormatTex format, DataType type, Buffer buffer);
    public void glGetUniformfv(int program, int location, FloatBuffer params);
    public void glGetUniformiv(int program, int location, IntBuffer params);
    public int glGetUniformLocation(int program, String name);
    public void glGetVertexAttribfv(int index, VertexAttribName pname, FloatBuffer params);
    public void glGetVertexAttribiv(int index, VertexAttribName pname, IntBuffer params);
//    public void glGetVertexAttribPointerv(int index, VertexAttribPointerName pname, IntBuffer params);

    public void glHint(HintMode mode);

    public boolean glIsBuffer(int buffer);
    public boolean glIsEnabled(Capability cap);
    public boolean glIsFramebuffer(int framebuffer);
    public boolean glIsProgram(int program);
    public boolean glIsRenderbuffer(int renderbuffer);
    public boolean glIsShader(int shader);
    public boolean glIsTexture(int texture);
    
    public void glLineWidth(float width);
    public void glLinkProgram(int program);

    public void glPixelStorei(PixelStoreParam pname, int param);
    public void glPolygonOffset(float factor, float units);
    
    public void glReadPixels(int x, int y, int width, int height, 
            ReadPixelsFormat format, DataType type, Buffer data);
    public void glReleaseShaderCompiler();
    public void glRenderbufferStorage(InternalFormatBuf internalFormat,
            int width, int height);

    public void glSampleCoverage(float value, boolean invert);
    public void glScissor(int x, int y, int width, int height);
    public void glShaderBinary(int n, IntBuffer shaders, int binaryFormat,
            Buffer binary, int length);
    public void glShaderSource(int shader, int count, String[] string, IntBuffer length);
    public void glStencilFunc(DepthFunc func, int ref, int mask);
    public void glStencilFuncSeparate(CullFaceMode face, DepthFunc func, 
            int ref, int mask);
    public void glStencilMask(int mask);
    public void glStencilMaskSeparate(CullFaceMode face, int mask);
    public void glStencilOp(StencilOp sfail, StencilOp dpfail, StencilOp dppass);
    public void glStencilOpSeparate(CullFaceMode face, StencilOp sfail, 
            StencilOp dpfail, StencilOp dppass);

    public void glTexImage2D(TexSubTarget target, int level,
            InternalFormatTex internalFormat,
            int width, int height,
            DataType type, Buffer data);
    public void glTexParameter(TexTarget target, TexParamName pname, TexParam param);
    public void glTexSubImage2D(TexSubTarget target, int level,
            int xoffset, int yoffset, int width, int height,
            InternalFormatTex internalFormat,
            DataType type, Buffer data);

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

    public void glValidateProgram(int program);
    public void glVertexAttrib1f(int index, float v0);
    public void glVertexAttrib2f(int index, float v0, float v1);
    public void glVertexAttrib3f(int index, float v0, float v1, float v2);
    public void glVertexAttrib4f(int index, float v0, float v1, float v2, float v3);
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
//    @Deprecated
//    public int getSurfaceInstanceNumber();

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

    public static enum CullFaceMode
    {
        GL_FRONT,
        GL_BACK,
        GL_FRONT_AND_BACK
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
    
    public static enum FrontFaceMode
    {
        GL_CW,
        GL_CCW
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
    
    public static enum PrecisionType
    {
        GL_LOW_FLOAT,
        GL_MEDIUM_FLOAT, 
        GL_HIGH_FLOAT, 
        GL_LOW_INT, 
        GL_MEDIUM_INT, 
        GL_HIGH_INT
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

    public static enum BufferValue
    {
        GL_BUFFER_SIZE,
        GL_BUFFER_USAGE
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

    public static enum FramebufferAttachmentParameter
    {
        GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE,
        GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME,
        GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL,
        GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE
    }

    public static enum RenderbufferParameter
    {
        GL_RENDERBUFFER_WIDTH,
        GL_RENDERBUFFER_HEIGHT,
        GL_RENDERBUFFER_INTERNAL_FORMAT,
        GL_RENDERBUFFER_RED_SIZE,
        GL_RENDERBUFFER_GREEN_SIZE,
        GL_RENDERBUFFER_BLUE_SIZE,
        GL_RENDERBUFFER_ALPHA_SIZE,
        GL_RENDERBUFFER_DEPTH_SIZE,
        GL_RENDERBUFFER_STENCIL_SIZE
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

    public static enum VertexAttribName
    {
        GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING,
        GL_VERTEX_ATTRIB_ARRAY_ENABLED,
        GL_VERTEX_ATTRIB_ARRAY_SIZE,
        GL_VERTEX_ATTRIB_ARRAY_STRIDE,
        GL_VERTEX_ATTRIB_ARRAY_TYPE,
        GL_VERTEX_ATTRIB_ARRAY_NORMALIZED,
        GL_CURRENT_VERTEX_ATTRIB        
    }

    public static enum VertexAttribPointerName
    {
        GL_VERTEX_ATTRIB_ARRAY_POINTER
    }

    public static enum HintMode
    {
        GL_FASTEST,
        GL_NICEST,
        GL_DONT_CARE
    }

    public static enum PixelStoreParam
    {
        GL_PACK_ALIGNMENT,
        GL_UNPACK_ALIGNMENT
    }
    
    public static enum ReadPixelsFormat
    {
        GL_ALPHA,
        GL_RGB,
        GL_RGBA
    }
    
    public static enum StencilOp
    {
        GL_KEEP,
        GL_ZERO,
        GL_REPLACE,
        GL_INCR,
        GL_INCR_WRAP,
        GL_DECR,
        GL_DECR_WRAP,
        GL_INVERT
    }
            
    public static enum ErrorType
    {
        GL_NO_ERROR,
        GL_INVALID_ENUM,
        GL_INVALID_VALUE,
        GL_INVALID_OPERATION,
        GL_INVALID_FRAMEBUFFER_OPERATION,
        GL_OUT_OF_MEMORY
    }
    
    public static enum StringName
    {
        GL_VENDOR,
        GL_RENDERER,
        GL_VERSION,
        GL_SHADING_LANGUAGE_VERSION,
        GL_EXTENSIONS
    }
    
    public static enum GetParam
    {
        GL_ACTIVE_TEXTURE,
        GL_ALIASED_LINE_WIDTH_RANGE,
        GL_ALIASED_POINT_SIZE_RANGE,
        GL_ALPHA_BITS,
        GL_ARRAY_BUFFER_BINDING,
        GL_BLEND,
        GL_BLEND_COLOR,
        GL_BLEND_DST_ALPHA,
        GL_BLEND_DST_RGB,
        GL_BLEND_EQUATION_ALPHA,
        GL_BLEND_EQUATION_RGB,
        GL_BLEND_SRC_ALPHA,
        GL_BLEND_SRC_RGB,
        GL_BLUE_BITS,
        GL_COLOR_CLEAR_VALUE,
        GL_COLOR_WRITEMASK,
        GL_COMPRESSED_TEXTURE_FORMATS,
        GL_CULL_FACE,
        GL_CULL_FACE_MODE,
        GL_CURRENT_PROGRAM,
        GL_DEPTH_BITS,
        GL_DEPTH_CLEAR_VALUE,
        GL_DEPTH_FUNC,
        GL_DEPTH_RANGE,
        GL_DEPTH_TEST,
        GL_DEPTH_WRITEMASK,
        GL_DITHER,
        GL_ELEMENT_ARRAY_BUFFER_BINDING,
        GL_FRAMEBUFFER_BINDING,
        GL_FRONT_FACE,
        GL_GENERATE_MIPMAP_HINT,
        GL_GREEN_BITS,
        GL_IMPLEMENTATION_COLOR_READ_FORMAT,
        GL_IMPLEMENTATION_COLOR_READ_TYPE,
        GL_LINE_WIDTH,
        GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS,
        GL_MAX_CUBE_MAP_TEXTURE_SIZE,
        GL_MAX_FRAGMENT_UNIFORM_VECTORS,
        GL_MAX_RENDERBUFFER_SIZE,
        GL_MAX_TEXTURE_IMAGE_UNITS,
        GL_MAX_TEXTURE_SIZE,
        GL_MAX_VARYING_VECTORS,
        GL_MAX_VERTEX_ATTRIBS,
        GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS,
        GL_MAX_VERTEX_UNIFORM_VECTORS,
        GL_MAX_VIEWPORT_DIMS,
        GL_NUM_COMPRESSED_TEXTURE_FORMATS,
        GL_NUM_SHADER_BINARY_FORMATS,
        GL_PACK_ALIGNMENT,
        GL_POLYGON_OFFSET_FACTOR,
        GL_POLYGON_OFFSET_FILL,
        GL_POLYGON_OFFSET_UNITS,
        GL_RED_BITS,
        GL_RENDERBUFFER_BINDING,
        GL_SAMPLE_ALPHA_TO_COVERAGE,
        GL_SAMPLE_BUFFERS,
        GL_SAMPLE_COVERAGE,
        GL_SAMPLE_COVERAGE_INVERT,
        GL_SAMPLE_COVERAGE_VALUE,
        GL_SAMPLES,
        GL_SCISSOR_BOX,
        GL_SCISSOR_TEST,
        GL_SHADER_BINARY_FORMATS,
        GL_SHADER_COMPILER,
        GL_STENCIL_BACK_FAIL,
        GL_STENCIL_BACK_FUNC,
        GL_STENCIL_BACK_PASS_DEPTH_FAIL,
        GL_STENCIL_BACK_PASS_DEPTH_PASS,
        GL_STENCIL_BACK_REF,
        GL_STENCIL_BACK_VALUE_MASK,
        GL_STENCIL_BACK_WRITEMASK,
        GL_STENCIL_BITS,
        GL_STENCIL_CLEAR_VALUE,
        GL_STENCIL_FAIL,
        GL_STENCIL_FUNC,
        GL_STENCIL_PASS_DEPTH_FAIL,
        GL_STENCIL_PASS_DEPTH_PASS,
        GL_STENCIL_REF,
        GL_STENCIL_TEST,
        GL_STENCIL_VALUE_MASK,
        GL_STENCIL_WRITEMASK,
        GL_SUBPIXEL_BITS,
        GL_TEXTURE_BINDING_2D,
        GL_TEXTURE_BINDING_CUBE_MAP,
        GL_UNPACK_ALIGNMENT,
        GL_VIEWPORT                            
    }
}
