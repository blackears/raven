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

import com.kitfox.coyote.renderer.CyGLWrapper;
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

/**
 *
 * @author kitfox
 */
public class CyGLWrapperJOGL implements CyGLWrapper
{
//    private final GLAutoDrawable drawable;
    private final GL2 gl;

    public CyGLWrapperJOGL(GL2 gl)
    {
        this.gl = gl;
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
    public void glBufferSubData(BufferTarget target, int offset, int size, Buffer data)
    {
        gl.glBufferSubData(getBufferTargetMask(target), offset, size, data);
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
    public void glClearDepthf(float depth)
    {
        gl.glClearDepthf(depth);
    }
    
    @Override
    public void glClearStencil(int s)
    {
        gl.glClearStencil(s);
    }
    
    @Override
    public void glColorMask(boolean r, boolean g, boolean b, boolean a)
    {
        gl.glColorMask(r, g, b, a);
    }
    
    @Override
    public void glCompileShader(int shader)
    {
        gl.glCompileShader(shader);
    }

    @Override
    public void glCompressedTexImage2D(TexSubTarget target, int level, 
            InternalFormatTex internalFormat, int width, int height, 
            int border, int imageSize, Buffer data)
    {
        gl.glCompressedTexImage2D(
                getTexSubTargetMask(target),
                level,
                getInternalFormatTexMask(internalFormat),
                width, height, border,
                imageSize,
                data);
    }

    @Override
    public void glCompressedTexSubImage2D(TexSubTarget target, int level, 
            int xoffset, int yoffset, int width, int height, 
            InternalFormatTex internalFormat, int imageSize, Buffer data)
    {
        gl.glCompressedTexSubImage2D(
                getTexSubTargetMask(target),
                level,
                xoffset, yoffset, width, height,
                getInternalFormatTexMask(internalFormat),
                imageSize,
                data);
    }
    
    @Override
    public void glCopyTexImage2D(TexSubTarget target, int level, 
            InternalFormatTex internalFormat, 
            int x, int y, int width, int height, 
            int border)
    {
        gl.glCopyTexImage2D(
                getTexSubTargetMask(target),
                level,
                getInternalFormatTexMask(internalFormat),
                x, y, width, height,
                border);
    }
    
    @Override
    public void glCopyTexSubImage2D(TexSubTarget target, int level, 
            int xoffset, int yoffset, 
            int x, int y, int width, int height)
    {
        gl.glCopyTexSubImage2D(
                getTexSubTargetMask(target),
                level,
                xoffset, yoffset,
                x, y, width, height);
    }

    @Override
    public void glCullFace(CullFaceMode mode)
    {
        gl.glCullFace(getCullFaceModeMask(mode));
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
    public void glDetachShader(int program, int shader)
    {
        gl.glDetachShader(program, shader);
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
    public void glFinish()
    {
        gl.glFinish();
    }

    @Override
    public void glFlush()
    {
        gl.glFlush();
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
    public void glFrontFace(FrontFaceMode mode)
    {
        gl.glFrontFace(getFrontFaceModeMask(mode));
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
    public void glGetBooleanv(GetParam param, ByteBuffer bbuf)
    {
        gl.glGetBooleanv(getGetParamMask(param), bbuf);
    }
    
    @Override
    public void glGetFloatv(GetParam param, FloatBuffer fbuf)
    {
        gl.glGetFloatv(getGetParamMask(param), fbuf);
    }
    
    @Override
    public void glGetIntegerv(GetParam param, IntBuffer ibuf)
    {
        gl.glGetIntegerv(getGetParamMask(param), ibuf);
    }

    @Override
    public void glGetActiveAttrib(int program, int index,
            int bufSize, IntBuffer length, IntBuffer size, IntBuffer type,
            ByteBuffer name)
    {
        gl.glGetActiveAttrib(program, index, bufSize, length, size, type, name);
    }
    
    @Override
    public void glGetActiveUniform(int program, int index,
            int bufSize, IntBuffer length, IntBuffer size, IntBuffer type,
            ByteBuffer name)
    {
        gl.glGetActiveUniform(program, index, bufSize, length, size, type, name);
    }
    
    @Override
    public void glGetAttachedShaders(int program, int maxCount,
            IntBuffer count, IntBuffer shaders)
    {
        gl.glGetAttachedShaders(program, maxCount, count, shaders);
    }
    
    @Override
    public int glGetAttribLocation(int program, String name)
    {
        return gl.glGetAttribLocation(program, name);
    }

    @Override
    public void glGetBufferParameteriv(BufferTarget target, 
            BufferValue value, IntBuffer data)
    {
        gl.glGetBufferParameteriv(getBufferTargetMask(target),
                getBufferValueMask(value), data);
    }
    
    @Override
    public ErrorType glGetError()
    {
        switch (gl.glGetError())
        {
            case GL.GL_NO_ERROR:
                return ErrorType.GL_NO_ERROR;
            case GL.GL_INVALID_ENUM:
                return ErrorType.GL_INVALID_ENUM;
            case GL.GL_INVALID_VALUE:
                return ErrorType.GL_INVALID_VALUE;
            case GL.GL_INVALID_OPERATION:
                return ErrorType.GL_INVALID_OPERATION;
            case GL.GL_INVALID_FRAMEBUFFER_OPERATION:
                return ErrorType.GL_INVALID_FRAMEBUFFER_OPERATION;
            case GL.GL_OUT_OF_MEMORY:
                return ErrorType.GL_OUT_OF_MEMORY;
            default:
                throw new RuntimeException();
        }
    }
    
    @Override
    public void glGetFramebufferAttachmentParameteriv(
            Attachment attachment, FramebufferAttachmentParameter pname, IntBuffer params)
    {
        gl.glGetFramebufferAttachmentParameteriv(GL.GL_FRAMEBUFFER,
                getAttachmentMask(attachment),
                getFramebufferAttachmentParameterMask(pname),
                params);
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
    public void glGetRenderbufferParameteriv(
            RenderbufferParameter pname, IntBuffer params)
    {
        gl.glGetRenderbufferParameteriv(GL.GL_RENDERBUFFER,
                getRenderbufferParameterMask(pname),
                params);
    }

    @Override
    public void glGetShaderInfoLog(int program, int bufSize,
            IntBuffer length, ByteBuffer infoLog)
    {
        gl.glGetShaderInfoLog(program, bufSize, length, infoLog);
    }
    
    @Override
    public void glGetShaderPrecisionFormat(ShaderType shaderType,
            PrecisionType precisionType, IntBuffer range, IntBuffer precision)
    {
        gl.glGetShaderPrecisionFormat(
                getShaderTypeMask(shaderType),
                getPrecisionTypeMask(precisionType),
                range, precision);
    }
    
    @Override
    public void glGetShaderSource(int shader, int bufSize, IntBuffer length, ByteBuffer source)
    {
        gl.glGetShaderSource(shader, bufSize, length, source);
    }

    @Override
    public void glGetShaderiv(int shader, ShaderParamName pname, IntBuffer params)
    {
        gl.glGetShaderiv(shader, getShaderParamNameMask(pname), params);
    }
    
    @Override
    public String glGetString(StringName name)
    {
        return gl.glGetString(getStringNameMask(name));
    }
    
    @Override
    public void glGetTexParameterfv(TexTarget target, TexParamName param, FloatBuffer params)
    {
        gl.glGetTexParameterfv(getTexTargetMask(target),
                getTexParamNameMask(param), params);
    }
    
    @Override
    public void glGetTexParameteriv(TexTarget target, TexParamName param, IntBuffer params)
    {
        gl.glGetTexParameteriv(getTexTargetMask(target),
                getTexParamNameMask(param), params);
    }

    @Override
    public void glGetTexImage(TexTarget target, int level,
            InternalFormatTex format, DataType type, Buffer buffer)
    {
        gl.glGetTexImage(getTexTargetMask(target), level,
                getInternalFormatTexMask(format), getDataTypeMask(type), buffer);
    }

    @Override
    public void glGetUniformfv(int program, int location, FloatBuffer params)
    {
        gl.glGetUniformfv(program, location, params);
    }
    
    @Override
    public void glGetUniformiv(int program, int location, IntBuffer params)
    {
        gl.glGetUniformiv(program, location, params);
    }

    @Override
    public int glGetUniformLocation(int program, String name)
    {
        return gl.glGetUniformLocation(program, name);
    }
    
    @Override
    public void glGetVertexAttribfv(int index, VertexAttribName pname, FloatBuffer params)
    {
        gl.glGetVertexAttribfv(index, getVertexAttribNameMask(pname), params);
    }
    
    @Override
    public void glGetVertexAttribiv(int index, VertexAttribName pname, IntBuffer params)
    {
        gl.glGetVertexAttribiv(index, getVertexAttribNameMask(pname), params);
    }
    
//    @Override
//    public void glGetVertexAttribPointerv(int index, VertexAttribPointerName pname, IntBuffer params)
//    {
//        gl.glGetVertexAttribPointerv(index, getVertexAttribPointerNameMask(pname), params);
//    }

    @Override
    public void glHint(HintMode mode)
    {
        gl.glHint(GL2.GL_GENERATE_MIPMAP_HINT, getHintModeMask(mode));
    }

    @Override
    public boolean glIsBuffer(int buffer)
    {
        return gl.glIsBuffer(buffer);
    }
    
    @Override
    public boolean glIsEnabled(Capability cap)
    {
        return gl.glIsEnabled(getCapabilityMask(cap));
    }
    
    @Override
    public boolean glIsFramebuffer(int framebuffer)
    {
        return gl.glIsFramebuffer(framebuffer);
    }    
    
    @Override
    public boolean glIsProgram(int program)
    {
        return gl.glIsProgram(program);
    }
    
    @Override
    public boolean glIsRenderbuffer(int renderbuffer)
    {
        return gl.glIsRenderbuffer(renderbuffer);
    }
    
    @Override
    public boolean glIsShader(int shader)
    {
        return gl.glIsShader(shader);
    }
    
    @Override
    public boolean glIsTexture(int texture)
    {
        return gl.glIsTexture(texture);
    }
    
    @Override
    public void glLineWidth(float width)
    {
        gl.glLineWidth(width);
    }
    
    @Override
    public void glLinkProgram(int program)
    {
        gl.glLinkProgram(program);
    }

    @Override
    public void glPixelStorei(PixelStoreParam pname, int param)
    {
        gl.glPixelStorei(getPixelStoreParamMask(pname), param);
    }

    @Override
    public void glPolygonOffset(float factor, float units)
    {
        gl.glPolygonOffset(factor, units);
    }

    @Override
    public void glReadPixels(int x, int y, int width, int height, 
            ReadPixelsFormat format, DataType type, Buffer data)
    {
        gl.glReadPixels(x, y, width, height,
                getReadPixelsFormatMask(format), getDataTypeMask(type), data);
    }
    
    @Override
    public void glReleaseShaderCompiler()
    {
        gl.glReleaseShaderCompiler();
    }
    
    @Override
    public void glRenderbufferStorage(InternalFormatBuf internalFormat, int width, int height)
    {
        gl.glRenderbufferStorage(
                GL.GL_RENDERBUFFER,
                getInternalFormatBufMask(internalFormat),
                width, height);
    }

    @Override
    public void glSampleCoverage(float value, boolean invert)
    {
        gl.glSampleCoverage(value, invert);
    }
    
    @Override
    public void glScissor(int x, int y, int width, int height)
    {
        gl.glScissor(x, y, width, height);
    }
    
    
    @Override
    public void glShaderBinary(int n, IntBuffer shaders, int binaryFormat,
            Buffer binary, int length)
    {
        gl.glShaderBinary(n, shaders, binaryFormat, binary, length);
    }
    
    @Override
    public void glShaderSource(int shader, int count, String[] string, IntBuffer length)
    {
        gl.glShaderSource(shader, count, string, null);
    }
    
    @Override
    public void glStencilFunc(DepthFunc func, int ref, int mask)
    {
        gl.glStencilFunc(getDepthFuncMask(func), ref, mask);
    }
    
    @Override
    public void glStencilFuncSeparate(CullFaceMode face, DepthFunc func,
            int ref, int mask)
    {
        gl.glStencilFuncSeparate(getCullFaceModeMask(face),
                getDepthFuncMask(func), ref, mask);
    }
    
    @Override
    public void glStencilMask(int mask)
    {
        gl.glStencilMask(mask);
    }

    @Override
    public void glStencilMaskSeparate(CullFaceMode face, int mask)
    {
        gl.glStencilMaskSeparate(getCullFaceModeMask(face), mask);
    }

    @Override
    public void glStencilOp(StencilOp sfail, StencilOp dpfail, StencilOp dppass)
    {
        gl.glStencilOp(
                getStencilOpMask(sfail),
                getStencilOpMask(dpfail),
                getStencilOpMask(dppass)
                );
    }

    @Override
    public void glStencilOpSeparate(CullFaceMode face, StencilOp sfail, StencilOp dpfail, StencilOp dppass)
    {
        gl.glStencilOpSeparate(getCullFaceModeMask(face), 
                getStencilOpMask(sfail),
                getStencilOpMask(dpfail),
                getStencilOpMask(dppass)
                );
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
    public void glTexSubImage2D(TexSubTarget target, int level,
            int xoffset, int yoffset, int width, int height,
            InternalFormatTex internalFormat,
            DataType type, Buffer data)
    {
        gl.glTexImage2D(
                getTexSubTargetMask(target),
                level,
                xoffset, yoffset, width, height,
                getInternalFormatTexMask(internalFormat),
                getDataTypeMask(type),
                data);
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
    public void glValidateProgram(int program)
    {
        gl.glValidateProgram(program);
    }

    @Override
    public void glVertexAttrib1f(int index, float v0)
    {
        gl.glVertexAttrib1f(index, v0);
    }

    @Override
    public void glVertexAttrib2f(int index, float v0, float v1)
    {
        gl.glVertexAttrib2f(index, v0, v1);
    }

    @Override
    public void glVertexAttrib3f(int index, float v0, float v1, float v2)
    {
        gl.glVertexAttrib3f(index, v0, v1, v2);
    }

    @Override
    public void glVertexAttrib4f(int index, float v0, float v1, float v2, float v3)
    {
        gl.glVertexAttrib4f(index, v0, v1, v2, v3);
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
        gl.glViewport(x, y, 
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
            Logger.getLogger(CyGLWrapperJOGL.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }


//    /**
//     * @return the reaquiredSurface
//     */
//    @Override
//    public int getSurfaceInstanceNumber()
//    {
//        return surfaceInstanceNumber;
//    }



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
    
    private int getFramebufferAttachmentParameterMask(
            FramebufferAttachmentParameter param)
    {
        switch (param)
        {
            case GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE:
                return GL.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE;
            case GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME:
                return GL.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME;
            case GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL:
                return GL.GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL;
            case GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE:
                return GL.GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE;
            default:
                throw new RuntimeException();
        }
    }

    private int getRenderbufferParameterMask(RenderbufferParameter param)
    {
        switch (param)
        {
            case GL_RENDERBUFFER_WIDTH:
                return GL2.GL_RENDERBUFFER_WIDTH;
            case GL_RENDERBUFFER_HEIGHT:
                return GL2.GL_RENDERBUFFER_HEIGHT;
            case GL_RENDERBUFFER_INTERNAL_FORMAT:
                return GL2.GL_RENDERBUFFER_INTERNAL_FORMAT;
            case GL_RENDERBUFFER_RED_SIZE:
                return GL2.GL_RENDERBUFFER_RED_SIZE;
            case GL_RENDERBUFFER_GREEN_SIZE:
                return GL2.GL_RENDERBUFFER_GREEN_SIZE;
            case GL_RENDERBUFFER_BLUE_SIZE:
                return GL2.GL_RENDERBUFFER_BLUE_SIZE;
            case GL_RENDERBUFFER_ALPHA_SIZE:
                return GL2.GL_RENDERBUFFER_ALPHA_SIZE;
            case GL_RENDERBUFFER_DEPTH_SIZE:
                return GL2.GL_RENDERBUFFER_DEPTH_SIZE;
            case GL_RENDERBUFFER_STENCIL_SIZE:
                return GL2.GL_RENDERBUFFER_STENCIL_SIZE;
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

    private int getBufferValueMask(BufferValue bufferValue)
    {
        switch (bufferValue)
        {
            case GL_BUFFER_SIZE:
                return GL.GL_BUFFER_SIZE;
            case GL_BUFFER_USAGE:
                return GL.GL_BUFFER_USAGE;
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

    private int getPrecisionTypeMask(PrecisionType precisionType)
    {
        switch (precisionType)
        {
            case GL_LOW_FLOAT:
                return GL2.GL_LOW_FLOAT;
            case GL_MEDIUM_FLOAT:
                return GL2.GL_MEDIUM_FLOAT;
            case GL_HIGH_FLOAT:
                return GL2.GL_HIGH_FLOAT;
            case GL_LOW_INT:
                return GL2.GL_LOW_INT;
            case GL_MEDIUM_INT:
                return GL2.GL_MEDIUM_INT;
            case GL_HIGH_INT:
                return GL2.GL_HIGH_INT;
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

    private int getCullFaceModeMask(CullFaceMode mode)
    {
        switch (mode)
        {
            case GL_BACK:
                return GL.GL_BACK;
            case GL_FRONT:
                return GL.GL_FRONT;
            case GL_FRONT_AND_BACK:
                return GL.GL_FRONT_AND_BACK;
            default:
                throw new RuntimeException();
        }
    }

    private int getFrontFaceModeMask(FrontFaceMode mode)
    {
        switch (mode)
        {
            case GL_CW:
                return GL2.GL_CW;
            case GL_CCW:
                return GL2.GL_CCW;
            default:
                throw new RuntimeException();
        }
    }

    private int getVertexAttribNameMask(VertexAttribName name)
    {
        switch (name)
        {
            case GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING:
                return GL2.GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING;
            case GL_VERTEX_ATTRIB_ARRAY_ENABLED:
                return GL2.GL_VERTEX_ATTRIB_ARRAY_ENABLED;
            case GL_VERTEX_ATTRIB_ARRAY_SIZE:
                return GL2.GL_VERTEX_ATTRIB_ARRAY_SIZE;
            case GL_VERTEX_ATTRIB_ARRAY_STRIDE:
                return GL2.GL_VERTEX_ATTRIB_ARRAY_STRIDE;
            case GL_VERTEX_ATTRIB_ARRAY_TYPE:
                return GL2.GL_VERTEX_ATTRIB_ARRAY_TYPE;
            case GL_VERTEX_ATTRIB_ARRAY_NORMALIZED:
                return GL2.GL_VERTEX_ATTRIB_ARRAY_NORMALIZED;
            case GL_CURRENT_VERTEX_ATTRIB:
                return GL2.GL_CURRENT_VERTEX_ATTRIB;
            default:
                throw new RuntimeException();
        }
    }

    private int getVertexAttribPointerNameMask(VertexAttribPointerName name)
    {
        switch (name)
        {
            case GL_VERTEX_ATTRIB_ARRAY_POINTER:
                return GL2.GL_VERTEX_ATTRIB_ARRAY_POINTER;
            default:
                throw new RuntimeException();
        }
    }
    
    private int getHintModeMask(HintMode mode)
    {
        switch (mode)
        {
            case GL_FASTEST:
                return GL2.GL_FASTEST;
            case GL_NICEST:
                return GL2.GL_NICEST;
            case GL_DONT_CARE:
                return GL2.GL_DONT_CARE;
            default:
                throw new RuntimeException();
        }
    }

    private int getPixelStoreParamMask(PixelStoreParam pname)
    {
        switch (pname)
        {
            case GL_PACK_ALIGNMENT:
                return GL2.GL_PACK_ALIGNMENT;
            case GL_UNPACK_ALIGNMENT:
                return GL2.GL_UNPACK_ALIGNMENT;
            default:
                throw new RuntimeException();
        }
    }

    private int getStringNameMask(StringName name)
    {
        switch (name)
        {
            case GL_VENDOR:
                return GL2.GL_VENDOR;
            case GL_RENDERER:
                return GL2.GL_RENDERER;
            case GL_VERSION:
                return GL2.GL_VERSION;
            case GL_SHADING_LANGUAGE_VERSION:
                return GL2.GL_SHADING_LANGUAGE_VERSION;
            case GL_EXTENSIONS:
                return GL2.GL_EXTENSIONS;
            default:
                throw new RuntimeException();            
        }
    }

    private int getReadPixelsFormatMask(ReadPixelsFormat format)
    {
        switch (format)
        {
            case GL_ALPHA:
                return GL2.GL_ALPHA;
            case GL_RGB:
                return GL2.GL_RGB;
            case GL_RGBA:
                return GL2.GL_RGBA;
            default:
                throw new RuntimeException();            
        }
    }

    private int getStencilOpMask(StencilOp op)
    {
        switch (op)
        {
            case GL_KEEP:
                return GL2.GL_KEEP;
            case GL_ZERO:
                return GL2.GL_ZERO;
            case GL_REPLACE:
                return GL2.GL_REPLACE;
            case GL_INCR:
                return GL2.GL_INCR;
            case GL_INCR_WRAP:
                return GL2.GL_INCR_WRAP;
            case GL_DECR:
                return GL2.GL_DECR;
            case GL_DECR_WRAP:
                return GL2.GL_DECR_WRAP;
            case GL_INVERT:
                return GL2.GL_INVERT;
            default:
                throw new RuntimeException();            
        }
    }
            
    private int getGetParamMask(GetParam param)
    {
        switch (param)
        {
            case GL_ACTIVE_TEXTURE:
                return GL2.GL_ACTIVE_TEXTURE;
            case GL_ALIASED_LINE_WIDTH_RANGE:
                return GL2.GL_ALIASED_LINE_WIDTH_RANGE;
            case GL_ALIASED_POINT_SIZE_RANGE:
                return GL2.GL_ALIASED_POINT_SIZE_RANGE;
            case GL_ALPHA_BITS:
                return GL2.GL_ALPHA_BITS;
            case GL_ARRAY_BUFFER_BINDING:
                return GL2.GL_ARRAY_BUFFER_BINDING;
            case GL_BLEND:
                return GL2.GL_BLEND;
            case GL_BLEND_COLOR:
                return GL2.GL_BLEND_COLOR;
            case GL_BLEND_DST_ALPHA:
                return GL2.GL_BLEND_DST_ALPHA;
            case GL_BLEND_DST_RGB:
                return GL2.GL_BLEND_DST_RGB;
            case GL_BLEND_EQUATION_ALPHA:
                return GL2.GL_BLEND_EQUATION_ALPHA;
            case GL_BLEND_EQUATION_RGB:
                return GL2.GL_BLEND_EQUATION_RGB;
            case GL_BLEND_SRC_ALPHA:
                return GL2.GL_BLEND_SRC_ALPHA;
            case GL_BLEND_SRC_RGB:
                return GL2.GL_BLEND_SRC_RGB;
            case GL_BLUE_BITS:
                return GL2.GL_BLUE_BITS;
            case GL_COLOR_CLEAR_VALUE:
                return GL2.GL_COLOR_CLEAR_VALUE;
            case GL_COLOR_WRITEMASK:
                return GL2.GL_COLOR_WRITEMASK;
            case GL_COMPRESSED_TEXTURE_FORMATS:
                return GL2.GL_COMPRESSED_TEXTURE_FORMATS;
            case GL_CULL_FACE:
                return GL2.GL_CULL_FACE;
            case GL_CULL_FACE_MODE:
                return GL2.GL_CULL_FACE_MODE;
            case GL_CURRENT_PROGRAM:
                return GL2.GL_CURRENT_PROGRAM;
            case GL_DEPTH_BITS:
                return GL2.GL_DEPTH_BITS;
            case GL_DEPTH_CLEAR_VALUE:
                return GL2.GL_DEPTH_CLEAR_VALUE;
            case GL_DEPTH_FUNC:
                return GL2.GL_DEPTH_FUNC;
            case GL_DEPTH_RANGE:
                return GL2.GL_DEPTH_RANGE;
            case GL_DEPTH_TEST:
                return GL2.GL_DEPTH_TEST;
            case GL_DEPTH_WRITEMASK:
                return GL2.GL_DEPTH_WRITEMASK;
            case GL_DITHER:
                return GL2.GL_DITHER;
            case GL_ELEMENT_ARRAY_BUFFER_BINDING:
                return GL2.GL_ELEMENT_ARRAY_BUFFER_BINDING;
            case GL_FRAMEBUFFER_BINDING:
                return GL2.GL_FRAMEBUFFER_BINDING;
            case GL_FRONT_FACE:
                return GL2.GL_FRONT_FACE;
            case GL_GENERATE_MIPMAP_HINT:
                return GL2.GL_GENERATE_MIPMAP_HINT;
            case GL_GREEN_BITS:
                return GL2.GL_GREEN_BITS;
            case GL_IMPLEMENTATION_COLOR_READ_FORMAT:
                return GL2.GL_IMPLEMENTATION_COLOR_READ_FORMAT;
            case GL_IMPLEMENTATION_COLOR_READ_TYPE:
                return GL2.GL_IMPLEMENTATION_COLOR_READ_TYPE;
            case GL_LINE_WIDTH:
                return GL2.GL_LINE_WIDTH;
            case GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS:
                return GL2.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS;
            case GL_MAX_CUBE_MAP_TEXTURE_SIZE:
                return GL2.GL_MAX_CUBE_MAP_TEXTURE_SIZE;
            case GL_MAX_FRAGMENT_UNIFORM_VECTORS:
                return GL2.GL_MAX_FRAGMENT_UNIFORM_VECTORS;
            case GL_MAX_RENDERBUFFER_SIZE:
                return GL2.GL_MAX_RENDERBUFFER_SIZE;
            case GL_MAX_TEXTURE_IMAGE_UNITS:
                return GL2.GL_MAX_TEXTURE_IMAGE_UNITS;
            case GL_MAX_TEXTURE_SIZE:
                return GL2.GL_MAX_TEXTURE_SIZE;
            case GL_MAX_VARYING_VECTORS:
                return GL2.GL_MAX_VARYING_VECTORS;
            case GL_MAX_VERTEX_ATTRIBS:
                return GL2.GL_MAX_VERTEX_ATTRIBS;
            case GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS:
                return GL2.GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS;
            case GL_MAX_VERTEX_UNIFORM_VECTORS:
                return GL2.GL_MAX_VERTEX_UNIFORM_VECTORS;
            case GL_MAX_VIEWPORT_DIMS:
                return GL2.GL_MAX_VIEWPORT_DIMS;
            case GL_NUM_COMPRESSED_TEXTURE_FORMATS:
                return GL2.GL_NUM_COMPRESSED_TEXTURE_FORMATS;
            case GL_NUM_SHADER_BINARY_FORMATS:
                return GL2.GL_NUM_SHADER_BINARY_FORMATS;
            case GL_PACK_ALIGNMENT:
                return GL2.GL_PACK_ALIGNMENT;
            case GL_POLYGON_OFFSET_FACTOR:
                return GL2.GL_POLYGON_OFFSET_FACTOR;
            case GL_POLYGON_OFFSET_FILL:
                return GL2.GL_POLYGON_OFFSET_FILL;
            case GL_POLYGON_OFFSET_UNITS:
                return GL2.GL_POLYGON_OFFSET_UNITS;
            case GL_RED_BITS:
                return GL2.GL_RED_BITS;
            case GL_RENDERBUFFER_BINDING:
                return GL2.GL_RENDERBUFFER_BINDING;
            case GL_SAMPLE_ALPHA_TO_COVERAGE:
                return GL2.GL_SAMPLE_ALPHA_TO_COVERAGE;
            case GL_SAMPLE_BUFFERS:
                return GL2.GL_SAMPLE_BUFFERS;
            case GL_SAMPLE_COVERAGE:
                return GL2.GL_SAMPLE_COVERAGE;
            case GL_SAMPLE_COVERAGE_INVERT:
                return GL2.GL_SAMPLE_COVERAGE_INVERT;
            case GL_SAMPLE_COVERAGE_VALUE:
                return GL2.GL_SAMPLE_COVERAGE_VALUE;
            case GL_SAMPLES:
                return GL2.GL_SAMPLES;
            case GL_SCISSOR_BOX:
                return GL2.GL_SCISSOR_BOX;
            case GL_SCISSOR_TEST:
                return GL2.GL_SCISSOR_TEST;
            case GL_SHADER_BINARY_FORMATS:
                return GL2.GL_SHADER_BINARY_FORMATS;
            case GL_SHADER_COMPILER:
                return GL2.GL_SHADER_COMPILER;
            case GL_STENCIL_BACK_FAIL:
                return GL2.GL_STENCIL_BACK_FAIL;
            case GL_STENCIL_BACK_FUNC:
                return GL2.GL_STENCIL_BACK_FUNC;
            case GL_STENCIL_BACK_PASS_DEPTH_FAIL:
                return GL2.GL_STENCIL_BACK_PASS_DEPTH_FAIL;
            case GL_STENCIL_BACK_PASS_DEPTH_PASS:
                return GL2.GL_STENCIL_BACK_PASS_DEPTH_PASS;
            case GL_STENCIL_BACK_REF:
                return GL2.GL_STENCIL_BACK_REF;
            case GL_STENCIL_BACK_VALUE_MASK:
                return GL2.GL_STENCIL_BACK_VALUE_MASK;
            case GL_STENCIL_BACK_WRITEMASK:
                return GL2.GL_STENCIL_BACK_WRITEMASK;
            case GL_STENCIL_BITS:
                return GL2.GL_STENCIL_BITS;
            case GL_STENCIL_CLEAR_VALUE:
                return GL2.GL_STENCIL_CLEAR_VALUE;
            case GL_STENCIL_FAIL:
                return GL2.GL_STENCIL_FAIL;
            case GL_STENCIL_FUNC:
                return GL2.GL_STENCIL_FUNC;
            case GL_STENCIL_PASS_DEPTH_FAIL:
                return GL2.GL_STENCIL_PASS_DEPTH_FAIL;
            case GL_STENCIL_PASS_DEPTH_PASS:
                return GL2.GL_STENCIL_PASS_DEPTH_PASS;
            case GL_STENCIL_REF:
                return GL2.GL_STENCIL_REF;
            case GL_STENCIL_TEST:
                return GL2.GL_STENCIL_TEST;
            case GL_STENCIL_VALUE_MASK:
                return GL2.GL_STENCIL_VALUE_MASK;
            case GL_STENCIL_WRITEMASK:
                return GL2.GL_STENCIL_WRITEMASK;
            case GL_SUBPIXEL_BITS:
                return GL2.GL_SUBPIXEL_BITS;
            case GL_TEXTURE_BINDING_2D:
                return GL2.GL_TEXTURE_BINDING_2D;
            case GL_TEXTURE_BINDING_CUBE_MAP:
                return GL2.GL_TEXTURE_BINDING_CUBE_MAP;
            case GL_UNPACK_ALIGNMENT:
                return GL2.GL_UNPACK_ALIGNMENT;
            case GL_VIEWPORT:
                return GL2.GL_VIEWPORT;
            default:
                throw new RuntimeException();
        }
    }

    /**
     * @return the gl
     */
    public GL getGl()
    {
        return gl;
    }

}
