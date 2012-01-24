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

package com.kitfox.coyote.renderer.android;

import android.opengl.GLES20;
import com.kitfox.coyote.renderer.CyGLWrapper;
import com.kitfox.coyote.renderer.jogl.CyGLWrapperJOGL;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class CyGLWrapperAndroid
        implements CyGLWrapper
{
    public CyGLWrapperAndroid()
    {
    }

    @Override
    public void glActiveTexture(ActiveTexture texture)
    {
        GLES20.glActiveTexture(getActiveTextureMask(texture));
    }

    @Override
    public void glAttachShader(int program, int shader)
    {
        GLES20.glAttachShader(program, shader);
    }

    @Override
    public void glBlendColor(float r, float g, float b, float a)
    {
        GLES20.glBlendColor(r, g, b, a);
    }

    @Override
    public void glBlendEquation(BlendMode mode)
    {
        GLES20.glBlendEquation(getBlendModeMask(mode));
    }

    @Override
    public void glBlendEquationSeparate(BlendMode modeRgb, BlendMode modeAlpha)
    {
        GLES20.glBlendEquationSeparate(getBlendModeMask(modeRgb), getBlendModeMask(modeAlpha));
    }

    @Override
    public void glBlendFunc(BlendFactor src, BlendFactor dst)
    {
        GLES20.glBlendFunc(getBlendFactorMask(src), getBlendFactorMask(dst));
    }

    @Override
    public void glBlendFuncSeparate(BlendFactor srcRgb, BlendFactor dstRgb, BlendFactor srcAlpha, BlendFactor dstAlpha)
    {
        GLES20.glBlendFuncSeparate(
                getBlendFactorMask(srcRgb), getBlendFactorMask(dstRgb),
                getBlendFactorMask(srcAlpha), getBlendFactorMask(dstAlpha)
                );
    }

    @Override
    public void glBindAttribLocation(int program, int index, String name)
    {
        GLES20.glBindAttribLocation(program, index, name);
    }

    @Override
    public void glBindBuffer(BufferTarget target, int buffer)
    {
        GLES20.glBindBuffer(getBufferTargetMask(target), buffer);
    }

    @Override
    public void glBindFramebuffer(int framebuffer)
    {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer);
    }

    @Override
    public void glBindRenderbuffer(int renderbuffer)
    {
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderbuffer);
    }

    @Override
    public void glBindTexture(TexTarget target, int texture)
    {
        GLES20.glBindTexture(getTexTargetMask(target), texture);
    }

    @Override
    public void glBufferData(BufferTarget target, int size, Buffer data, BufferUsage usage)
    {
        GLES20.glBufferData(getBufferTargetMask(target), size, data,
                getBufferUsage(usage));
    }

    @Override
    public void glBufferSubData(BufferTarget target, int offset, int size, Buffer data)
    {
        GLES20.glBufferSubData(getBufferTargetMask(target), offset, size, data);
    }

    @Override
    public FramebufferStatus glCheckFramebufferStatus()
    {
        int val = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        switch (val)
        {
            case GLES20.GL_FRAMEBUFFER_COMPLETE:
                return FramebufferStatus.GL_FRAMEBUFFER_COMPLETE;
            case GLES20.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
                return FramebufferStatus.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT;
            case GLES20.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
                return FramebufferStatus.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS;
//            case GLES20.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONSUFFER_INCOMPLETE_DRAW_BUFFER:
//                return FramebufferStatus.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER;
//            case GLES20.GL_FRAMEBUFFER_INCOMPLETE_DUPLICATE_ATTACHMENT:
//                return FramebufferStatus.GL_FRAMEBUFFER_INCOMPLETE_DUPLICATE_ATTACHMENT;
//            case GLES20.GL_FRAMEBUFFER_INCOMPLETE_FORMATS:
//                return FramebufferStatus.GL_FRAMEBUFFER_INCOMPLETE_FORMATS;
//            case GLES20.GL_FRAMEBUFFER_INCOMPLETE_LAYER_COUNT_EXT:
//                return FramebufferStatus.GL_FRAMEBUFFER_INCOMPLETE_LAYER_COUNT;
//            case GLES20.GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS_EXT:
//                return FramebufferStatus.GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS;
            case GLES20.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
                return FramebufferStatus.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT;
//            case GLES20.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE:
//                return FramebufferStatus.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE;
//            case GLES20.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
//                return FramebufferStatus.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER;
            case GLES20.GL_FRAMEBUFFER_UNSUPPORTED:
                return FramebufferStatus.GL_FRAMEBUFFER_UNSUPPORTED;
            default:
                throw new RuntimeException("Unknown framebuffer status: " + val);
                //return FramebufferStatus.UNKNOWN;
        }
    }

    @Override
    public void glClear(boolean color, boolean depth, boolean stencil)
    {
        int mask = (color ? GLES20.GL_COLOR_BUFFER_BIT : 0)
                | (depth ? GLES20.GL_DEPTH_BUFFER_BIT : 0)
                | (stencil ? GLES20.GL_STENCIL_BUFFER_BIT : 0);
        GLES20.glClear(mask);
    }

    @Override
    public void glClearColor(float r, float g, float b, float a)
    {
        GLES20.glClearColor(r, g, b, a);
    }

    @Override
    public void glClearDepthf(float depth)
    {
        GLES20.glClearDepthf(depth);
    }
    
    @Override
    public void glClearStencil(int s)
    {
        GLES20.glClearStencil(s);
    }
    
    @Override
    public void glColorMask(boolean r, boolean g, boolean b, boolean a)
    {
        GLES20.glColorMask(r, g, b, a);
    }
    
    @Override
    public void glCompileShader(int shader)
    {
        GLES20.glCompileShader(shader);
    }

    @Override
    public void glCompressedTexImage2D(TexSubTarget target, int level, 
            InternalFormatTex internalFormat, int width, int height, 
            int border, int imageSize, Buffer data)
    {
        GLES20.glCompressedTexImage2D(
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
        GLES20.glCompressedTexSubImage2D(
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
        GLES20.glCopyTexImage2D(
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
        GLES20.glCopyTexSubImage2D(
                getTexSubTargetMask(target),
                level,
                xoffset, yoffset,
                x, y, width, height);
    }

    @Override
    public void glCullFace(CullFaceMode mode)
    {
        GLES20.glCullFace(getCullFaceModeMask(mode));
    }

    @Override
    public int glCreateProgram()
    {
        return GLES20.glCreateProgram();
    }

    @Override
    public int glCreateShader(ShaderType shaderType)
    {
        return GLES20.glCreateShader(getShaderTypeMask(shaderType));
    }

    @Override
    public void glDeleteBuffers(int size, IntBuffer ibuf)
    {
        GLES20.glDeleteBuffers(size, ibuf);
    }

    @Override
    public void glDeleteFramebuffers(int size, IntBuffer ibuf)
    {
        GLES20.glDeleteFramebuffers(size, ibuf);
    }

    @Override
    public void glDeleteProgram(int program)
    {
        GLES20.glDeleteProgram(program);
    }

    @Override
    public void glDeleteRenderbuffers(int size, IntBuffer ibuf)
    {
        GLES20.glDeleteRenderbuffers(size, ibuf);
    }

    @Override
    public void glDeleteShader(int shader)
    {
        GLES20.glDeleteShader(shader);
    }

    @Override
    public void glDeleteTextures(int size, IntBuffer ibuf)
    {
        GLES20.glDeleteTextures(size, ibuf);
    }

    @Override
    public void glDepthFunc(DepthFunc func)
    {
        GLES20.glDepthFunc(getDepthFuncMask(func));
    }

    @Override
    public void glDepthMask(boolean flag)
    {
        GLES20.glDepthMask(flag);
    }

    @Override
    public void glDepthRangef(float nearVal, float farVal)
    {
        GLES20.glDepthRangef(nearVal, farVal);
    }

    @Override
    public void glDetachShader(int program, int shader)
    {
        GLES20.glDetachShader(program, shader);
    }
    
    @Override
    public void glDisable(Capability cap)
    {
        GLES20.glDisable(getCapabilityMask(cap));
    }

    @Override
    public void glDisableVertexAttribArray(int index)
    {
        GLES20.glDisableVertexAttribArray(index);
    }

    @Override
    public void glDrawArrays(DrawMode mode, int first, int count)
    {
        GLES20.glDrawArrays(glDrawModeMask(mode), first, count);
    }

    @Override
    public void glDrawElements(DrawMode mode, int count, IndiciesType type, Buffer indices)
    {
        GLES20.glDrawElements(glDrawModeMask(mode), count,
                getIndiciesTypeMask(type), indices);
    }

    @Override
    public void glDrawElements(DrawMode mode, int count, IndiciesType type, long offset)
    {
        GLES20.glDrawElements(glDrawModeMask(mode), count,
                getIndiciesTypeMask(type), (int)offset);
    }

    @Override
    public void glEnable(Capability cap)
    {
        GLES20.glEnable(getCapabilityMask(cap));
    }

    @Override
    public void glEnableVertexAttribArray(int index)
    {
        GLES20.glEnableVertexAttribArray(index);
    }

    @Override
    public void glFinish()
    {
        GLES20.glFinish();
    }

    @Override
    public void glFlush()
    {
        GLES20.glFlush();
    }
    
    @Override
    public void glFramebufferRenderbuffer(Attachment attachment, int renderbuffer)
    {
        GLES20.glFramebufferRenderbuffer(
                GLES20.GL_FRAMEBUFFER, getAttachmentMask(attachment),
                GLES20.GL_RENDERBUFFER, renderbuffer);
    }

    @Override
    public void glFramebufferTexture2D(Attachment attachment, TexSubTarget texTarget, int texture, int level)
    {
        GLES20.glFramebufferTexture2D(
                GLES20.GL_FRAMEBUFFER, getAttachmentMask(attachment),
                getTexSubTargetMask(texTarget), texture, level);
    }

    @Override
    public void glFrontFace(FrontFaceMode mode)
    {
        GLES20.glFrontFace(getFrontFaceModeMask(mode));
    }

    @Override
    public void glGenBuffers(int size, IntBuffer ibuf)
    {
        GLES20.glGenBuffers(size, ibuf);
    }

    @Override
    public void glGenFramebuffers(int size, IntBuffer ibuf)
    {
        GLES20.glGenFramebuffers(size, ibuf);
    }
    
    @Override
    public void glGenRenderbuffers(int size, IntBuffer ibuf)
    {
        GLES20.glGenRenderbuffers(size, ibuf);
    }

    @Override
    public void glGenTextures(int size, IntBuffer ibuf)
    {
        GLES20.glGenTextures(size, ibuf);
    }

    @Override
    public void glGenerateMipmap(TexTarget target)
    {
        GLES20.glGenerateMipmap(getTexTargetMask(target));
    }
    
    @Override
    public void glGetBooleanv(GetParam param, ByteBuffer bbuf)
    {
        IntBuffer ibuf = bbuf.asIntBuffer();
        GLES20.glGetBooleanv(getGetParamMask(param), ibuf);
    }
    
    @Override
    public void glGetFloatv(GetParam param, FloatBuffer fbuf)
    {
        GLES20.glGetFloatv(getGetParamMask(param), fbuf);
    }
    
    @Override
    public void glGetIntegerv(GetParam param, IntBuffer ibuf)
    {
        GLES20.glGetIntegerv(getGetParamMask(param), ibuf);
    }

    @Override
    public void glGetActiveAttrib(int program, int index, int bufSize, 
            IntBuffer length, IntBuffer size, IntBuffer type,
            ByteBuffer name)
    {
        throw new UnsupportedOperationException("Androind does not implement this function correctly");
        
//        GLES20.glGetActiveAttrib(program, index, 
//                bufSize, length, size, 
//                type, name);
    }
    
    @Override
    public void glGetActiveUniform(int program, int index,
            int bufSize, IntBuffer length, IntBuffer size, IntBuffer type,
            ByteBuffer name)
    {
        throw new UnsupportedOperationException("Androind does not implement this function correctly");
        
//        GLES20.glGetActiveUniform(program, index, bufSize, length, size, type, name);
    }
    
    @Override
    public void glGetAttachedShaders(int program, int maxCount,
            IntBuffer count, IntBuffer shaders)
    {
        GLES20.glGetAttachedShaders(program, maxCount, count, shaders);
    }
    
    @Override
    public int glGetAttribLocation(int program, String name)
    {
        return GLES20.glGetAttribLocation(program, name);
    }

    @Override
    public void glGetBufferParameteriv(BufferTarget target, 
            BufferValue value, IntBuffer data)
    {
        GLES20.glGetBufferParameteriv(getBufferTargetMask(target),
                getBufferValueMask(value), data);
    }
    
    @Override
    public ErrorType glGetError()
    {
        switch (GLES20.glGetError())
        {
            case GLES20.GL_NO_ERROR:
                return ErrorType.GL_NO_ERROR;
            case GLES20.GL_INVALID_ENUM:
                return ErrorType.GL_INVALID_ENUM;
            case GLES20.GL_INVALID_VALUE:
                return ErrorType.GL_INVALID_VALUE;
            case GLES20.GL_INVALID_OPERATION:
                return ErrorType.GL_INVALID_OPERATION;
            case GLES20.GL_INVALID_FRAMEBUFFER_OPERATION:
                return ErrorType.GL_INVALID_FRAMEBUFFER_OPERATION;
            case GLES20.GL_OUT_OF_MEMORY:
                return ErrorType.GL_OUT_OF_MEMORY;
            default:
                throw new RuntimeException();
        }
    }
    
    @Override
    public void glGetFramebufferAttachmentParameteriv(
            Attachment attachment, FramebufferAttachmentParameter pname, IntBuffer params)
    {
        GLES20.glGetFramebufferAttachmentParameteriv(GLES20.GL_FRAMEBUFFER,
                getAttachmentMask(attachment),
                getFramebufferAttachmentParameterMask(pname),
                params);
    }
    
    @Override
    public void glGetProgramInfoLog(int program, int bufSize,
            IntBuffer length, ByteBuffer infoLog)
    {
        String log = GLES20.glGetProgramInfoLog(program);
        infoLog.put(log.getBytes());
        
//        GLES20.glGetProgramInfoLog(program, bufSize, length, infoLog);
    }

    @Override
    public void glGetProgramiv(int program, ProgramParamName pname, IntBuffer params)
    {
        GLES20.glGetProgramiv(program, getProgramParamNameMask(pname), params);
    }
    
    @Override
    public void glGetRenderbufferParameteriv(
            RenderbufferParameter pname, IntBuffer params)
    {
        GLES20.glGetRenderbufferParameteriv(GLES20.GL_RENDERBUFFER,
                getRenderbufferParameterMask(pname),
                params);
    }

    @Override
    public void glGetShaderInfoLog(int program, int bufSize,
            IntBuffer length, ByteBuffer infoLog)
    {
        String log = GLES20.glGetShaderInfoLog(program);
        infoLog.put(log.getBytes());
        
//        GLES20.glGetShaderInfoLog(program, bufSize, length, infoLog);
    }
    
    @Override
    public void glGetShaderPrecisionFormat(ShaderType shaderType,
            PrecisionType precisionType, IntBuffer range, IntBuffer precision)
    {
        GLES20.glGetShaderPrecisionFormat(
                getShaderTypeMask(shaderType),
                getPrecisionTypeMask(precisionType),
                range, precision);
    }
    
    @Override
    public void glGetShaderSource(int shader, int bufSize, IntBuffer length, ByteBuffer source)
    {
        throw new UnsupportedOperationException("Android does not support this method");
        
//        GLES20.glGetShaderSource(shader, bufSize, length, source);
    }

    @Override
    public void glGetShaderiv(int shader, ShaderParamName pname, IntBuffer params)
    {
        GLES20.glGetShaderiv(shader, getShaderParamNameMask(pname), params);
    }
    
    @Override
    public String glGetString(StringName name)
    {
        return GLES20.glGetString(getStringNameMask(name));
    }
    
    @Override
    public void glGetTexParameterfv(TexTarget target, TexParamName param, FloatBuffer params)
    {
        GLES20.glGetTexParameterfv(getTexTargetMask(target),
                getTexParamNameMask(param), params);
    }
    
    @Override
    public void glGetTexParameteriv(TexTarget target, TexParamName param, IntBuffer params)
    {
        GLES20.glGetTexParameteriv(getTexTargetMask(target),
                getTexParamNameMask(param), params);
    }

    @Override
    public void glGetTexImage(TexTarget target, int level,
            InternalFormatTex format, DataType type, Buffer buffer)
    {
        throw new UnsupportedOperationException("Android does not support this method");
        
//        GLES20.glGetTexImage(getTexTargetMask(target), level,
//                getInternalFormatTexMask(format), getDataTypeMask(type), buffer);
    }

    @Override
    public void glGetUniformfv(int program, int location, FloatBuffer params)
    {
        GLES20.glGetUniformfv(program, location, params);
    }
    
    @Override
    public void glGetUniformiv(int program, int location, IntBuffer params)
    {
        GLES20.glGetUniformiv(program, location, params);
    }

    @Override
    public int glGetUniformLocation(int program, String name)
    {
        return GLES20.glGetUniformLocation(program, name);
    }
    
    @Override
    public void glGetVertexAttribfv(int index, VertexAttribName pname, FloatBuffer params)
    {
        GLES20.glGetVertexAttribfv(index, getVertexAttribNameMask(pname), params);
    }
    
    @Override
    public void glGetVertexAttribiv(int index, VertexAttribName pname, IntBuffer params)
    {
        GLES20.glGetVertexAttribiv(index, getVertexAttribNameMask(pname), params);
    }
    
//    @Override
//    public void glGetVertexAttribPointerv(int index, VertexAttribPointerName pname, IntBuffer params)
//    {
//        GLES20.glGetVertexAttribPointerv(index, getVertexAttribPointerNameMask(pname), params);
//    }

    @Override
    public void glHint(HintMode mode)
    {
        GLES20.glHint(GLES20.GL_GENERATE_MIPMAP_HINT, getHintModeMask(mode));
    }

    @Override
    public boolean glIsBuffer(int buffer)
    {
        return GLES20.glIsBuffer(buffer);
    }
    
    @Override
    public boolean glIsEnabled(Capability cap)
    {
        return GLES20.glIsEnabled(getCapabilityMask(cap));
    }
    
    @Override
    public boolean glIsFramebuffer(int framebuffer)
    {
        return GLES20.glIsFramebuffer(framebuffer);
    }    
    
    @Override
    public boolean glIsProgram(int program)
    {
        return GLES20.glIsProgram(program);
    }
    
    @Override
    public boolean glIsRenderbuffer(int renderbuffer)
    {
        return GLES20.glIsRenderbuffer(renderbuffer);
    }
    
    @Override
    public boolean glIsShader(int shader)
    {
        return GLES20.glIsShader(shader);
    }
    
    @Override
    public boolean glIsTexture(int texture)
    {
        return GLES20.glIsTexture(texture);
    }
    
    @Override
    public void glLineWidth(float width)
    {
        GLES20.glLineWidth(width);
    }
    
    @Override
    public void glLinkProgram(int program)
    {
        GLES20.glLinkProgram(program);
    }

    @Override
    public void glPixelStorei(PixelStoreParam pname, int param)
    {
        GLES20.glPixelStorei(getPixelStoreParamMask(pname), param);
    }

    @Override
    public void glPolygonOffset(float factor, float units)
    {
        GLES20.glPolygonOffset(factor, units);
    }

    @Override
    public void glReadPixels(int x, int y, int width, int height, 
            ReadPixelsFormat format, DataType type, Buffer data)
    {
        GLES20.glReadPixels(x, y, width, height,
                getReadPixelsFormatMask(format), getDataTypeMask(type), data);
    }
    
    @Override
    public void glReleaseShaderCompiler()
    {
        GLES20.glReleaseShaderCompiler();
    }
    
    @Override
    public void glRenderbufferStorage(InternalFormatBuf internalFormat, int width, int height)
    {
        GLES20.glRenderbufferStorage(
                GLES20.GL_RENDERBUFFER,
                getInternalFormatBufMask(internalFormat),
                width, height);
    }

    @Override
    public void glSampleCoverage(float value, boolean invert)
    {
        GLES20.glSampleCoverage(value, invert);
    }
    
    @Override
    public void glScissor(int x, int y, int width, int height)
    {
        GLES20.glScissor(x, y, width, height);
    }
    
    
    @Override
    public void glShaderBinary(int n, IntBuffer shaders, int binaryFormat,
            Buffer binary, int length)
    {
        GLES20.glShaderBinary(n, shaders, binaryFormat, binary, length);
    }
    
    @Override
    public void glShaderSource(int shader, int count, String[] string, IntBuffer length)
    {
        throw new UnsupportedOperationException("Android does not support this method");
        
//        GLES20.glShaderSource(shader, count, string, null);
    }
    
    @Override
    public void glStencilFunc(DepthFunc func, int ref, int mask)
    {
        GLES20.glStencilFunc(getDepthFuncMask(func), ref, mask);
    }
    
    @Override
    public void glStencilFuncSeparate(CullFaceMode face, DepthFunc func,
            int ref, int mask)
    {
        GLES20.glStencilFuncSeparate(getCullFaceModeMask(face),
                getDepthFuncMask(func), ref, mask);
    }
    
    @Override
    public void glStencilMask(int mask)
    {
        GLES20.glStencilMask(mask);
    }

    @Override
    public void glStencilMaskSeparate(CullFaceMode face, int mask)
    {
        GLES20.glStencilMaskSeparate(getCullFaceModeMask(face), mask);
    }

    @Override
    public void glStencilOp(StencilOp sfail, StencilOp dpfail, StencilOp dppass)
    {
        GLES20.glStencilOp(
                getStencilOpMask(sfail),
                getStencilOpMask(dpfail),
                getStencilOpMask(dppass)
                );
    }

    @Override
    public void glStencilOpSeparate(CullFaceMode face, StencilOp sfail, StencilOp dpfail, StencilOp dppass)
    {
        GLES20.glStencilOpSeparate(getCullFaceModeMask(face), 
                getStencilOpMask(sfail),
                getStencilOpMask(dpfail),
                getStencilOpMask(dppass)
                );
    }
    
    @Override
    public void glTexImage2D(TexSubTarget target, int level, InternalFormatTex internalFormat, int width, int height, DataType type, Buffer data)
    {
        GLES20.glTexImage2D(
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
        GLES20.glTexParameteri(getTexTargetMask(target),
                getTexParamNameMask(pname),
                getTexParamMask(param));
    }

    @Override
    public void glTexSubImage2D(TexSubTarget target, int level,
            int xoffset, int yoffset, int width, int height,
            InternalFormatTex internalFormat,
            DataType type, Buffer data)
    {
        GLES20.glTexImage2D(
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
        GLES20.glUniform1f(location, v0);
    }

    @Override
    public void glUniform2f(int location, float v0, float v1)
    {
        GLES20.glUniform2f(location, v0, v1);
    }

    @Override
    public void glUniform3f(int location, float v0, float v1, float v2)
    {
        GLES20.glUniform3f(location, v0, v1, v2);
    }

    @Override
    public void glUniform4f(int location, float v0, float v1, float v2, float v3)
    {
        GLES20.glUniform4f(location, v0, v1, v2, v3);
    }

    @Override
    public void glUniform1i(int location, int v0)
    {
        GLES20.glUniform1i(location, v0);
    }

    @Override
    public void glUniform2i(int location, int v0, int v1)
    {
        GLES20.glUniform2i(location, v0, v1);
    }

    @Override
    public void glUniform3i(int location, int v0, int v1, int v2)
    {
        GLES20.glUniform3i(location, v0, v1, v2);
    }

    @Override
    public void glUniform4i(int location, int v0, int v1, int v2, int v3)
    {
        GLES20.glUniform4i(location, v0, v1, v2, v3);
    }

    @Override
    public void glUniform1fv(int location, int count, FloatBuffer value)
    {
        GLES20.glUniform1fv(location, count, value);
    }

    @Override
    public void glUniform2fv(int location, int count, FloatBuffer value)
    {
        GLES20.glUniform2fv(location, count, value);
    }

    @Override
    public void glUniform3fv(int location, int count, FloatBuffer value)
    {
        GLES20.glUniform3fv(location, count, value);
    }

    @Override
    public void glUniform4fv(int location, int count, FloatBuffer value)
    {
        GLES20.glUniform4fv(location, count, value);
    }

    @Override
    public void glUniform1iv(int location, int count, IntBuffer value)
    {
        GLES20.glUniform1iv(location, count, value);
    }

    @Override
    public void glUniform2iv(int location, int count, IntBuffer value)
    {
        GLES20.glUniform2iv(location, count, value);
    }

    @Override
    public void glUniform3iv(int location, int count, IntBuffer value)
    {
        GLES20.glUniform3iv(location, count, value);
    }

    @Override
    public void glUniform4iv(int location, int count, IntBuffer value)
    {
        GLES20.glUniform4iv(location, count, value);
    }

    @Override
    public void glUniformMatrix2fv(int location, int count, boolean transpose, FloatBuffer value)
    {
        if (transpose)
        {
            throw new RuntimeException("transpose must be false");
        }
        GLES20.glUniformMatrix2fv(location, count, transpose, value);
    }

    @Override
    public void glUniformMatrix3fv(int location, int count, boolean transpose, FloatBuffer value)
    {
        if (transpose)
        {
            throw new RuntimeException("transpose must be false");
        }
        GLES20.glUniformMatrix3fv(location, count, transpose, value);
    }

    @Override
    public void glUniformMatrix4fv(int location, int count, boolean transpose, FloatBuffer value)
    {
        if (transpose)
        {
            throw new RuntimeException("transpose must be false");
        }
        GLES20.glUniformMatrix4fv(location, count, transpose, value);
    }

    @Override
    public void glUseProgram(int program)
    {
        GLES20.glUseProgram(program);
    }

    @Override
    public void glValidateProgram(int program)
    {
        GLES20.glValidateProgram(program);
    }

    @Override
    public void glVertexAttrib1f(int index, float v0)
    {
        GLES20.glVertexAttrib1f(index, v0);
    }

    @Override
    public void glVertexAttrib2f(int index, float v0, float v1)
    {
        GLES20.glVertexAttrib2f(index, v0, v1);
    }

    @Override
    public void glVertexAttrib3f(int index, float v0, float v1, float v2)
    {
        GLES20.glVertexAttrib3f(index, v0, v1, v2);
    }

    @Override
    public void glVertexAttrib4f(int index, float v0, float v1, float v2, float v3)
    {
        GLES20.glVertexAttrib4f(index, v0, v1, v2, v3);
    }

    @Override
    public void glVertexAttribPointer(int index, int size, 
            VertexDataType type, boolean normalized, 
            int stride, long offset)
    {
        GLES20.glVertexAttribPointer(index, size, getVertexDataTypeMask(type),
                normalized, stride, (int)offset);
    }

    @Override
    public void glVertexAttribPointer(int index, int size, VertexDataType type,
            boolean normalized, int stride, Buffer pointer)
    {
        GLES20.glVertexAttribPointer(index, size, getVertexDataTypeMask(type),
                normalized, stride, pointer);
    }

    @Override
    public void glViewport(int x, int y, int width, int height)
    {
        GLES20.glViewport(x, y, 
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


    private int getBlendModeMask(BlendMode blendMode)
    {
        switch (blendMode)
        {
            case GL_FUNC_ADD:
                return GLES20.GL_FUNC_ADD;
            case GL_FUNC_REVERSE_SUBTRACT:
                return GLES20.GL_FUNC_REVERSE_SUBTRACT;
            case GL_FUNC_SUBTRACT:
                return GLES20.GL_FUNC_SUBTRACT;
            default:
                throw new RuntimeException();
        }
    }

    private int getBlendFactorMask(BlendFactor blendMode)
    {
        switch (blendMode)
        {
            case GL_CONSTANT_ALPHA:
                return GLES20.GL_CONSTANT_ALPHA;
            case GL_CONSTANT_COLOR:
                return GLES20.GL_CONSTANT_COLOR;
            case GL_DST_ALPHA:
                return GLES20.GL_DST_ALPHA;
            case GL_DST_COLOR:
                return GLES20.GL_DST_COLOR;
            case GL_ONE:
                return GLES20.GL_ONE;
            case GL_ONE_MINUS_CONSTANT_ALPHA:
                return GLES20.GL_ONE_MINUS_CONSTANT_ALPHA;
            case GL_ONE_MINUS_CONSTANT_COLOR:
                return GLES20.GL_ONE_MINUS_CONSTANT_COLOR;
            case GL_ONE_MINUS_DST_ALPHA:
                return GLES20.GL_ONE_MINUS_DST_ALPHA;
            case GL_ONE_MINUS_DST_COLOR:
                return GLES20.GL_ONE_MINUS_DST_COLOR;
            case GL_ONE_MINUS_SRC_ALPHA:
                return GLES20.GL_ONE_MINUS_SRC_ALPHA;
            case GL_ONE_MINUS_SRC_COLOR:
                return GLES20.GL_ONE_MINUS_SRC_COLOR;
            case GL_SRC_ALPHA:
                return GLES20.GL_SRC_ALPHA;
            case GL_SRC_ALPHA_SATURATE:
                return GLES20.GL_SRC_ALPHA_SATURATE;
            case GL_SRC_COLOR:
                return GLES20.GL_SRC_COLOR;
            case GL_ZERO:
                return GLES20.GL_ZERO;
            default:
                throw new RuntimeException();
        }
    }
    
    private int getAttachmentMask(Attachment attachment)
    {
        switch (attachment)
        {
            case GL_COLOR_ATTACHMENT0:
                return GLES20.GL_COLOR_ATTACHMENT0;
            case GL_DEPTH_ATTACHMENT:
                return GLES20.GL_DEPTH_ATTACHMENT;
            case GL_STENCIL_ATTACHMENT:
                return GLES20.GL_STENCIL_ATTACHMENT;
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
                return GLES20.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE;
            case GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME:
                return GLES20.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME;
            case GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL:
                return GLES20.GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL;
            case GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE:
                return GLES20.GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE;
            default:
                throw new RuntimeException();
        }
    }

    private int getRenderbufferParameterMask(RenderbufferParameter param)
    {
        switch (param)
        {
            case GL_RENDERBUFFER_WIDTH:
                return GLES20.GL_RENDERBUFFER_WIDTH;
            case GL_RENDERBUFFER_HEIGHT:
                return GLES20.GL_RENDERBUFFER_HEIGHT;
            case GL_RENDERBUFFER_INTERNAL_FORMAT:
                return GLES20.GL_RENDERBUFFER_INTERNAL_FORMAT;
            case GL_RENDERBUFFER_RED_SIZE:
                return GLES20.GL_RENDERBUFFER_RED_SIZE;
            case GL_RENDERBUFFER_GREEN_SIZE:
                return GLES20.GL_RENDERBUFFER_GREEN_SIZE;
            case GL_RENDERBUFFER_BLUE_SIZE:
                return GLES20.GL_RENDERBUFFER_BLUE_SIZE;
            case GL_RENDERBUFFER_ALPHA_SIZE:
                return GLES20.GL_RENDERBUFFER_ALPHA_SIZE;
            case GL_RENDERBUFFER_DEPTH_SIZE:
                return GLES20.GL_RENDERBUFFER_DEPTH_SIZE;
            case GL_RENDERBUFFER_STENCIL_SIZE:
                return GLES20.GL_RENDERBUFFER_STENCIL_SIZE;
            default:
                throw new RuntimeException();
        }
    }

    private int getDataTypeMask(DataType dataType)
    {
        switch (dataType)
        {
            case GL_UNSIGNED_BYTE:
                return GLES20.GL_UNSIGNED_BYTE;
            case GL_UNSIGNED_SHORT_4_4_4_4:
                return GLES20.GL_UNSIGNED_SHORT_4_4_4_4;
            case GL_UNSIGNED_SHORT_5_5_5_1:
                return GLES20.GL_UNSIGNED_SHORT_5_5_5_1;
            case GL_UNSIGNED_SHORT_5_6_5:
                return GLES20.GL_UNSIGNED_SHORT_5_6_5;
            default:
                throw new RuntimeException();
        }
    }

    private int getInternalFormatBufMask(InternalFormatBuf format)
    {
        switch (format)
        {
            case GL_DEPTH_COMPONENT16:
//                return GLES20.GL_DEPTH_COMPONENT16;
                return GLES20.GL_DEPTH_COMPONENT;
            case GL_RGB565:
                return GLES20.GL_RGB565;
            case GL_RGB5_A1:
                return GLES20.GL_RGB5_A1;
            case GL_RGBA4:
//                return GLES20.GL_RGBA4;
                return GLES20.GL_RGBA;
            case GL_STENCIL_INDEX8:
                return GLES20.GL_STENCIL_INDEX8;
            default:
                throw new RuntimeException();
        }
    }

    private int getInternalFormatTexMask(InternalFormatTex format)
    {
        switch (format)
        {
            case GL_ALPHA:
                return GLES20.GL_ALPHA;
            case GL_LUMINANCE:
                return GLES20.GL_LUMINANCE;
            case GL_LUMINANCE_ALPHA:
                return GLES20.GL_LUMINANCE_ALPHA;
            case GL_RGB:
                return GLES20.GL_RGB;
            case GL_RGBA:
                return GLES20.GL_RGBA;
            default:
                throw new RuntimeException();
        }
    }

    private int getTexSubTargetMask(TexSubTarget texTarget)
    {
        switch (texTarget)
        {
            case GL_TEXTURE_2D:
                return GLES20.GL_TEXTURE_2D;
            case GL_TEXTURE_CUBE_MAP_NEGATIVE_X:
                return GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
            case GL_TEXTURE_CUBE_MAP_NEGATIVE_Y:
                return GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
            case GL_TEXTURE_CUBE_MAP_NEGATIVE_Z:
                return GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
            case GL_TEXTURE_CUBE_MAP_POSITIVE_X:
                return GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
            case GL_TEXTURE_CUBE_MAP_POSITIVE_Y:
                return GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
            case GL_TEXTURE_CUBE_MAP_POSITIVE_Z:
                return GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
            default:
                throw new RuntimeException();
        }
    }

    private int getBufferTargetMask(BufferTarget bufferTarget)
    {
        switch (bufferTarget)
        {
            case GL_ARRAY_BUFFER:
                return GLES20.GL_ARRAY_BUFFER;
            case GL_ELEMENT_ARRAY_BUFFER:
                return GLES20.GL_ELEMENT_ARRAY_BUFFER;
            default:
                throw new RuntimeException();
        }
    }

    private int getBufferValueMask(BufferValue bufferValue)
    {
        switch (bufferValue)
        {
            case GL_BUFFER_SIZE:
                return GLES20.GL_BUFFER_SIZE;
            case GL_BUFFER_USAGE:
                return GLES20.GL_BUFFER_USAGE;
            default:
                throw new RuntimeException();
        }
    }

    private int getTexTargetMask(TexTarget texTarget)
    {
        switch (texTarget)
        {
            case GL_TEXTURE_2D:
                return GLES20.GL_TEXTURE_2D;
            case GL_TEXTURE_CUBE_MAP:
                return GLES20.GL_TEXTURE_CUBE_MAP;
            default:
                throw new RuntimeException();
        }
    }

    private int getTexParamNameMask(TexParamName texParamName)
    {
        switch (texParamName)
        {
            case GL_TEXTURE_MAG_FILTER:
                return GLES20.GL_TEXTURE_MAG_FILTER;
            case GL_TEXTURE_MIN_FILTER:
                return GLES20.GL_TEXTURE_MIN_FILTER;
            case GL_TEXTURE_WRAP_S:
                return GLES20.GL_TEXTURE_WRAP_S;
            case GL_TEXTURE_WRAP_T:
                return GLES20.GL_TEXTURE_WRAP_T;
            default:
                throw new RuntimeException();
        }
    }

    private int getTexParamMask(TexParam texParam)
    {
        switch (texParam)
        {
            case GL_CLAMP_TO_EDGE:
                return GLES20.GL_CLAMP_TO_EDGE;
            case GL_LINEAR:
                return GLES20.GL_LINEAR;
            case GL_LINEAR_MIPMAP_LINEAR:
                return GLES20.GL_LINEAR_MIPMAP_LINEAR;
            case GL_LINEAR_MIPMAP_NEAREST:
                return GLES20.GL_LINEAR_MIPMAP_NEAREST;
            case GL_MIRRORED_REPEAT:
                return GLES20.GL_MIRRORED_REPEAT;
            case GL_NEAREST:
                return GLES20.GL_NEAREST;
            case GL_NEAREST_MIPMAP_LINEAR:
                return GLES20.GL_NEAREST_MIPMAP_LINEAR;
            case GL_NEAREST_MIPMAP_NEAREST:
                return GLES20.GL_NEAREST_MIPMAP_NEAREST;
            case GL_REPEAT:
                return GLES20.GL_REPEAT;
            default:
                throw new RuntimeException();
        }
    }

    private int getShaderTypeMask(ShaderType shaderType)
    {
        switch (shaderType)
        {
            case GL_FRAGMENT_SHADER:
                return GLES20.GL_FRAGMENT_SHADER;
            case GL_VERTEX_SHADER:
                return GLES20.GL_VERTEX_SHADER;
            default:
                throw new RuntimeException();
        }
    }

    private int getPrecisionTypeMask(PrecisionType precisionType)
    {
        switch (precisionType)
        {
            case GL_LOW_FLOAT:
                return GLES20.GL_LOW_FLOAT;
            case GL_MEDIUM_FLOAT:
                return GLES20.GL_MEDIUM_FLOAT;
            case GL_HIGH_FLOAT:
                return GLES20.GL_HIGH_FLOAT;
            case GL_LOW_INT:
                return GLES20.GL_LOW_INT;
            case GL_MEDIUM_INT:
                return GLES20.GL_MEDIUM_INT;
            case GL_HIGH_INT:
                return GLES20.GL_HIGH_INT;
            default:
                throw new RuntimeException();
        }
    }

    private int getShaderParamNameMask(ShaderParamName shaderParamName)
    {
        switch (shaderParamName)
        {
            case GL_COMPILE_STATUS:
                return GLES20.GL_COMPILE_STATUS;
            case GL_DELETE_STATUS:
                return GLES20.GL_DELETE_STATUS;
            case GL_INFO_LOG_LENGTH:
                return GLES20.GL_INFO_LOG_LENGTH;
            case GL_SHADER_SOURCE_LENGTH:
                return GLES20.GL_SHADER_SOURCE_LENGTH;
            case GL_SHADER_TYPE:
                return GLES20.GL_SHADER_TYPE;
            default:
                throw new RuntimeException();
        }
    }

    private int getProgramParamNameMask(ProgramParamName programParamName)
    {
        switch (programParamName)
        {
            case GL_ACTIVE_ATTRIBUTES:
                return GLES20.GL_ACTIVE_ATTRIBUTES;
            case GL_ACTIVE_ATTRIBUTE_MAX_LENGTH:
                return GLES20.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH;
            case GL_ACTIVE_UNIFORMS:
                return GLES20.GL_ACTIVE_UNIFORMS;
            case GL_ACTIVE_UNIFORM_MAX_LENGTH:
                return GLES20.GL_ACTIVE_UNIFORM_MAX_LENGTH;
            case GL_ATTACHED_SHADERS:
                return GLES20.GL_ATTACHED_SHADERS;
            case GL_DELETE_STATUS:
                return GLES20.GL_DELETE_STATUS;
            case GL_INFO_LOG_LENGTH:
                return GLES20.GL_INFO_LOG_LENGTH;
            case GL_LINK_STATUS:
                return GLES20.GL_LINK_STATUS;
            case GL_VALIDATE_STATUS:
                return GLES20.GL_VALIDATE_STATUS;
            default:
                throw new RuntimeException();
        }
    }

    private int getVertexDataTypeMask(VertexDataType vertexDataType)
    {
        switch (vertexDataType)
        {
            case GL_BYTE:
                return GLES20.GL_BYTE;
            case GL_FIXED:
                return GLES20.GL_FIXED;
            case GL_FLOAT:
                return GLES20.GL_FLOAT;
            case GL_SHORT:
                return GLES20.GL_SHORT;
            case GL_UNSIGNED_BYTE:
                return GLES20.GL_UNSIGNED_BYTE;
            case GL_UNSIGNED_SHORT:
                return GLES20.GL_UNSIGNED_SHORT;
            default:
                throw new RuntimeException();
        }
    }

    private int getCapabilityMask(Capability capability)
    {
        switch (capability)
        {
            case GL_BLEND:
                return GLES20.GL_BLEND;
            case GL_CULL_FACE:
                return GLES20.GL_CULL_FACE;
            case GL_DEPTH_TEST:
                return GLES20.GL_DEPTH_TEST;
            case GL_DITHER:
                return GLES20.GL_DITHER;
            case GL_POLYGON_OFFSET_FILL:
                return GLES20.GL_POLYGON_OFFSET_FILL;
            case GL_SAMPLE_ALPHA_TO_COVERAGE:
                return GLES20.GL_SAMPLE_ALPHA_TO_COVERAGE;
            case GL_SAMPLE_COVERAGE:
                return GLES20.GL_SAMPLE_COVERAGE;
            case GL_SCISSOR_TEST:
                return GLES20.GL_SCISSOR_TEST;
            case GL_STENCIL_TEST:
                return GLES20.GL_STENCIL_TEST;
            default:
                throw new RuntimeException();
        }
    }

    private int getBufferUsage(BufferUsage bufferUsage)
    {
        switch (bufferUsage)
        {
            case GL_DYNAMIC_DRAW:
                return GLES20.GL_DYNAMIC_DRAW;
            case GL_STATIC_DRAW:
                return GLES20.GL_STATIC_DRAW;
            case GL_STREAM_DRAW:
                return GLES20.GL_STREAM_DRAW;
            default:
                throw new RuntimeException();
        }
    }

    private int glDrawModeMask(DrawMode drawMode)
    {
        switch (drawMode)
        {
            case GL_LINES:
                return GLES20.GL_LINES;
            case GL_LINE_LOOP:
                return GLES20.GL_LINE_LOOP;
            case GL_LINE_STRIP:
                return GLES20.GL_LINE_STRIP;
            case GL_POINTS:
                return GLES20.GL_POINTS;
            case GL_TRIANGLES:
                return GLES20.GL_TRIANGLES;
            case GL_TRIANGLE_FAN:
                return GLES20.GL_TRIANGLE_FAN;
            case GL_TRIANGLE_STRIP:
                return GLES20.GL_TRIANGLE_STRIP;
            default:
                throw new RuntimeException();
        }
    }

    private int getIndiciesTypeMask(IndiciesType indiciesType)
    {
        switch (indiciesType)
        {
            case GL_UNSIGNED_BYTE:
                return GLES20.GL_UNSIGNED_BYTE;
            case GL_UNSIGNED_SHORT:
                return GLES20.GL_UNSIGNED_SHORT;
            default:
                throw new RuntimeException();
        }
    }

    private int getDepthFuncMask(DepthFunc depthFunc)
    {
        switch (depthFunc)
        {
            case GL_ALWAYS:
                return GLES20.GL_ALWAYS;
            case GL_EQUAL:
                return GLES20.GL_EQUAL;
            case GL_GEQUAL:
                return GLES20.GL_GEQUAL;
            case GL_GREATER:
                return GLES20.GL_GREATER;
            case GL_LEQUAL:
                return GLES20.GL_LEQUAL;
            case GL_LESS:
                return GLES20.GL_LESS;
            case GL_NEVER:
                return GLES20.GL_NEVER;
            case GL_NOTEQUAL:
                return GLES20.GL_NOTEQUAL;
            default:
                throw new RuntimeException();
        }
    }

    private int getActiveTextureMask(ActiveTexture activeTexture)
    {
        switch (activeTexture)
        {
            case GL_TEXTURE0:
                return GLES20.GL_TEXTURE0;
            case GL_TEXTURE1:
                return GLES20.GL_TEXTURE1;
            case GL_TEXTURE2:
                return GLES20.GL_TEXTURE2;
            case GL_TEXTURE3:
                return GLES20.GL_TEXTURE3;
            case GL_TEXTURE4:
                return GLES20.GL_TEXTURE4;
            case GL_TEXTURE5:
                return GLES20.GL_TEXTURE5;
            case GL_TEXTURE6:
                return GLES20.GL_TEXTURE6;
            case GL_TEXTURE7:
                return GLES20.GL_TEXTURE7;
            case GL_TEXTURE8:
                return GLES20.GL_TEXTURE8;
            case GL_TEXTURE9:
                return GLES20.GL_TEXTURE9;
            case GL_TEXTURE10:
                return GLES20.GL_TEXTURE10;
            case GL_TEXTURE11:
                return GLES20.GL_TEXTURE11;
            case GL_TEXTURE12:
                return GLES20.GL_TEXTURE12;
            case GL_TEXTURE13:
                return GLES20.GL_TEXTURE13;
            case GL_TEXTURE14:
                return GLES20.GL_TEXTURE14;
            case GL_TEXTURE15:
                return GLES20.GL_TEXTURE15;
            default:
                throw new RuntimeException();
        }
    }

    private int getCullFaceModeMask(CullFaceMode mode)
    {
        switch (mode)
        {
            case GL_BACK:
                return GLES20.GL_BACK;
            case GL_FRONT:
                return GLES20.GL_FRONT;
            case GL_FRONT_AND_BACK:
                return GLES20.GL_FRONT_AND_BACK;
            default:
                throw new RuntimeException();
        }
    }

    private int getFrontFaceModeMask(FrontFaceMode mode)
    {
        switch (mode)
        {
            case GL_CW:
                return GLES20.GL_CW;
            case GL_CCW:
                return GLES20.GL_CCW;
            default:
                throw new RuntimeException();
        }
    }

    private int getVertexAttribNameMask(VertexAttribName name)
    {
        switch (name)
        {
            case GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING:
                return GLES20.GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING;
            case GL_VERTEX_ATTRIB_ARRAY_ENABLED:
                return GLES20.GL_VERTEX_ATTRIB_ARRAY_ENABLED;
            case GL_VERTEX_ATTRIB_ARRAY_SIZE:
                return GLES20.GL_VERTEX_ATTRIB_ARRAY_SIZE;
            case GL_VERTEX_ATTRIB_ARRAY_STRIDE:
                return GLES20.GL_VERTEX_ATTRIB_ARRAY_STRIDE;
            case GL_VERTEX_ATTRIB_ARRAY_TYPE:
                return GLES20.GL_VERTEX_ATTRIB_ARRAY_TYPE;
            case GL_VERTEX_ATTRIB_ARRAY_NORMALIZED:
                return GLES20.GL_VERTEX_ATTRIB_ARRAY_NORMALIZED;
            case GL_CURRENT_VERTEX_ATTRIB:
                return GLES20.GL_CURRENT_VERTEX_ATTRIB;
            default:
                throw new RuntimeException();
        }
    }

    private int getVertexAttribPointerNameMask(VertexAttribPointerName name)
    {
        switch (name)
        {
            case GL_VERTEX_ATTRIB_ARRAY_POINTER:
                return GLES20.GL_VERTEX_ATTRIB_ARRAY_POINTER;
            default:
                throw new RuntimeException();
        }
    }
    
    private int getHintModeMask(HintMode mode)
    {
        switch (mode)
        {
            case GL_FASTEST:
                return GLES20.GL_FASTEST;
            case GL_NICEST:
                return GLES20.GL_NICEST;
            case GL_DONT_CARE:
                return GLES20.GL_DONT_CARE;
            default:
                throw new RuntimeException();
        }
    }

    private int getPixelStoreParamMask(PixelStoreParam pname)
    {
        switch (pname)
        {
            case GL_PACK_ALIGNMENT:
                return GLES20.GL_PACK_ALIGNMENT;
            case GL_UNPACK_ALIGNMENT:
                return GLES20.GL_UNPACK_ALIGNMENT;
            default:
                throw new RuntimeException();
        }
    }

    private int getStringNameMask(StringName name)
    {
        switch (name)
        {
            case GL_VENDOR:
                return GLES20.GL_VENDOR;
            case GL_RENDERER:
                return GLES20.GL_RENDERER;
            case GL_VERSION:
                return GLES20.GL_VERSION;
            case GL_SHADING_LANGUAGE_VERSION:
                return GLES20.GL_SHADING_LANGUAGE_VERSION;
            case GL_EXTENSIONS:
                return GLES20.GL_EXTENSIONS;
            default:
                throw new RuntimeException();            
        }
    }

    private int getReadPixelsFormatMask(ReadPixelsFormat format)
    {
        switch (format)
        {
            case GL_ALPHA:
                return GLES20.GL_ALPHA;
            case GL_RGB:
                return GLES20.GL_RGB;
            case GL_RGBA:
                return GLES20.GL_RGBA;
            default:
                throw new RuntimeException();            
        }
    }

    private int getStencilOpMask(StencilOp op)
    {
        switch (op)
        {
            case GL_KEEP:
                return GLES20.GL_KEEP;
            case GL_ZERO:
                return GLES20.GL_ZERO;
            case GL_REPLACE:
                return GLES20.GL_REPLACE;
            case GL_INCR:
                return GLES20.GL_INCR;
            case GL_INCR_WRAP:
                return GLES20.GL_INCR_WRAP;
            case GL_DECR:
                return GLES20.GL_DECR;
            case GL_DECR_WRAP:
                return GLES20.GL_DECR_WRAP;
            case GL_INVERT:
                return GLES20.GL_INVERT;
            default:
                throw new RuntimeException();            
        }
    }
            
    private int getGetParamMask(GetParam param)
    {
        switch (param)
        {
            case GL_ACTIVE_TEXTURE:
                return GLES20.GL_ACTIVE_TEXTURE;
            case GL_ALIASED_LINE_WIDTH_RANGE:
                return GLES20.GL_ALIASED_LINE_WIDTH_RANGE;
            case GL_ALIASED_POINT_SIZE_RANGE:
                return GLES20.GL_ALIASED_POINT_SIZE_RANGE;
            case GL_ALPHA_BITS:
                return GLES20.GL_ALPHA_BITS;
            case GL_ARRAY_BUFFER_BINDING:
                return GLES20.GL_ARRAY_BUFFER_BINDING;
            case GL_BLEND:
                return GLES20.GL_BLEND;
            case GL_BLEND_COLOR:
                return GLES20.GL_BLEND_COLOR;
            case GL_BLEND_DST_ALPHA:
                return GLES20.GL_BLEND_DST_ALPHA;
            case GL_BLEND_DST_RGB:
                return GLES20.GL_BLEND_DST_RGB;
            case GL_BLEND_EQUATION_ALPHA:
                return GLES20.GL_BLEND_EQUATION_ALPHA;
            case GL_BLEND_EQUATION_RGB:
                return GLES20.GL_BLEND_EQUATION_RGB;
            case GL_BLEND_SRC_ALPHA:
                return GLES20.GL_BLEND_SRC_ALPHA;
            case GL_BLEND_SRC_RGB:
                return GLES20.GL_BLEND_SRC_RGB;
            case GL_BLUE_BITS:
                return GLES20.GL_BLUE_BITS;
            case GL_COLOR_CLEAR_VALUE:
                return GLES20.GL_COLOR_CLEAR_VALUE;
            case GL_COLOR_WRITEMASK:
                return GLES20.GL_COLOR_WRITEMASK;
            case GL_COMPRESSED_TEXTURE_FORMATS:
                return GLES20.GL_COMPRESSED_TEXTURE_FORMATS;
            case GL_CULL_FACE:
                return GLES20.GL_CULL_FACE;
            case GL_CULL_FACE_MODE:
                return GLES20.GL_CULL_FACE_MODE;
            case GL_CURRENT_PROGRAM:
                return GLES20.GL_CURRENT_PROGRAM;
            case GL_DEPTH_BITS:
                return GLES20.GL_DEPTH_BITS;
            case GL_DEPTH_CLEAR_VALUE:
                return GLES20.GL_DEPTH_CLEAR_VALUE;
            case GL_DEPTH_FUNC:
                return GLES20.GL_DEPTH_FUNC;
            case GL_DEPTH_RANGE:
                return GLES20.GL_DEPTH_RANGE;
            case GL_DEPTH_TEST:
                return GLES20.GL_DEPTH_TEST;
            case GL_DEPTH_WRITEMASK:
                return GLES20.GL_DEPTH_WRITEMASK;
            case GL_DITHER:
                return GLES20.GL_DITHER;
            case GL_ELEMENT_ARRAY_BUFFER_BINDING:
                return GLES20.GL_ELEMENT_ARRAY_BUFFER_BINDING;
            case GL_FRAMEBUFFER_BINDING:
                return GLES20.GL_FRAMEBUFFER_BINDING;
            case GL_FRONT_FACE:
                return GLES20.GL_FRONT_FACE;
            case GL_GENERATE_MIPMAP_HINT:
                return GLES20.GL_GENERATE_MIPMAP_HINT;
            case GL_GREEN_BITS:
                return GLES20.GL_GREEN_BITS;
            case GL_IMPLEMENTATION_COLOR_READ_FORMAT:
                return GLES20.GL_IMPLEMENTATION_COLOR_READ_FORMAT;
            case GL_IMPLEMENTATION_COLOR_READ_TYPE:
                return GLES20.GL_IMPLEMENTATION_COLOR_READ_TYPE;
            case GL_LINE_WIDTH:
                return GLES20.GL_LINE_WIDTH;
            case GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS:
                return GLES20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS;
            case GL_MAX_CUBE_MAP_TEXTURE_SIZE:
                return GLES20.GL_MAX_CUBE_MAP_TEXTURE_SIZE;
            case GL_MAX_FRAGMENT_UNIFORM_VECTORS:
                return GLES20.GL_MAX_FRAGMENT_UNIFORM_VECTORS;
            case GL_MAX_RENDERBUFFER_SIZE:
                return GLES20.GL_MAX_RENDERBUFFER_SIZE;
            case GL_MAX_TEXTURE_IMAGE_UNITS:
                return GLES20.GL_MAX_TEXTURE_IMAGE_UNITS;
            case GL_MAX_TEXTURE_SIZE:
                return GLES20.GL_MAX_TEXTURE_SIZE;
            case GL_MAX_VARYING_VECTORS:
                return GLES20.GL_MAX_VARYING_VECTORS;
            case GL_MAX_VERTEX_ATTRIBS:
                return GLES20.GL_MAX_VERTEX_ATTRIBS;
            case GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS:
                return GLES20.GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS;
            case GL_MAX_VERTEX_UNIFORM_VECTORS:
                return GLES20.GL_MAX_VERTEX_UNIFORM_VECTORS;
            case GL_MAX_VIEWPORT_DIMS:
                return GLES20.GL_MAX_VIEWPORT_DIMS;
            case GL_NUM_COMPRESSED_TEXTURE_FORMATS:
                return GLES20.GL_NUM_COMPRESSED_TEXTURE_FORMATS;
            case GL_NUM_SHADER_BINARY_FORMATS:
                return GLES20.GL_NUM_SHADER_BINARY_FORMATS;
            case GL_PACK_ALIGNMENT:
                return GLES20.GL_PACK_ALIGNMENT;
            case GL_POLYGON_OFFSET_FACTOR:
                return GLES20.GL_POLYGON_OFFSET_FACTOR;
            case GL_POLYGON_OFFSET_FILL:
                return GLES20.GL_POLYGON_OFFSET_FILL;
            case GL_POLYGON_OFFSET_UNITS:
                return GLES20.GL_POLYGON_OFFSET_UNITS;
            case GL_RED_BITS:
                return GLES20.GL_RED_BITS;
            case GL_RENDERBUFFER_BINDING:
                return GLES20.GL_RENDERBUFFER_BINDING;
            case GL_SAMPLE_ALPHA_TO_COVERAGE:
                return GLES20.GL_SAMPLE_ALPHA_TO_COVERAGE;
            case GL_SAMPLE_BUFFERS:
                return GLES20.GL_SAMPLE_BUFFERS;
            case GL_SAMPLE_COVERAGE:
                return GLES20.GL_SAMPLE_COVERAGE;
            case GL_SAMPLE_COVERAGE_INVERT:
                return GLES20.GL_SAMPLE_COVERAGE_INVERT;
            case GL_SAMPLE_COVERAGE_VALUE:
                return GLES20.GL_SAMPLE_COVERAGE_VALUE;
            case GL_SAMPLES:
                return GLES20.GL_SAMPLES;
            case GL_SCISSOR_BOX:
                return GLES20.GL_SCISSOR_BOX;
            case GL_SCISSOR_TEST:
                return GLES20.GL_SCISSOR_TEST;
            case GL_SHADER_BINARY_FORMATS:
                return GLES20.GL_SHADER_BINARY_FORMATS;
            case GL_SHADER_COMPILER:
                return GLES20.GL_SHADER_COMPILER;
            case GL_STENCIL_BACK_FAIL:
                return GLES20.GL_STENCIL_BACK_FAIL;
            case GL_STENCIL_BACK_FUNC:
                return GLES20.GL_STENCIL_BACK_FUNC;
            case GL_STENCIL_BACK_PASS_DEPTH_FAIL:
                return GLES20.GL_STENCIL_BACK_PASS_DEPTH_FAIL;
            case GL_STENCIL_BACK_PASS_DEPTH_PASS:
                return GLES20.GL_STENCIL_BACK_PASS_DEPTH_PASS;
            case GL_STENCIL_BACK_REF:
                return GLES20.GL_STENCIL_BACK_REF;
            case GL_STENCIL_BACK_VALUE_MASK:
                return GLES20.GL_STENCIL_BACK_VALUE_MASK;
            case GL_STENCIL_BACK_WRITEMASK:
                return GLES20.GL_STENCIL_BACK_WRITEMASK;
            case GL_STENCIL_BITS:
                return GLES20.GL_STENCIL_BITS;
            case GL_STENCIL_CLEAR_VALUE:
                return GLES20.GL_STENCIL_CLEAR_VALUE;
            case GL_STENCIL_FAIL:
                return GLES20.GL_STENCIL_FAIL;
            case GL_STENCIL_FUNC:
                return GLES20.GL_STENCIL_FUNC;
            case GL_STENCIL_PASS_DEPTH_FAIL:
                return GLES20.GL_STENCIL_PASS_DEPTH_FAIL;
            case GL_STENCIL_PASS_DEPTH_PASS:
                return GLES20.GL_STENCIL_PASS_DEPTH_PASS;
            case GL_STENCIL_REF:
                return GLES20.GL_STENCIL_REF;
            case GL_STENCIL_TEST:
                return GLES20.GL_STENCIL_TEST;
            case GL_STENCIL_VALUE_MASK:
                return GLES20.GL_STENCIL_VALUE_MASK;
            case GL_STENCIL_WRITEMASK:
                return GLES20.GL_STENCIL_WRITEMASK;
            case GL_SUBPIXEL_BITS:
                return GLES20.GL_SUBPIXEL_BITS;
            case GL_TEXTURE_BINDING_2D:
                return GLES20.GL_TEXTURE_BINDING_2D;
            case GL_TEXTURE_BINDING_CUBE_MAP:
                return GLES20.GL_TEXTURE_BINDING_CUBE_MAP;
            case GL_UNPACK_ALIGNMENT:
                return GLES20.GL_UNPACK_ALIGNMENT;
            case GL_VIEWPORT:
                return GLES20.GL_VIEWPORT;
            default:
                throw new RuntimeException();
        }
    }
    
}
