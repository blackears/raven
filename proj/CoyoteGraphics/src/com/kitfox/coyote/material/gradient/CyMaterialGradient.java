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

package com.kitfox.coyote.material.gradient;

import com.kitfox.coyote.renderer.CyProgramException;
import com.kitfox.coyote.renderer.CyShaderException;
import com.kitfox.coyote.renderer.CyMaterial;
import com.kitfox.coyote.math.BufferUtil;
import com.kitfox.coyote.math.CyGradientStops;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.renderer.CyTextureDataProvider;
import com.kitfox.coyote.renderer.CyTextureImage;
import com.kitfox.coyote.renderer.CyTransparency;
import com.kitfox.coyote.renderer.CyVertexArrayInfo;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.coyote.renderer.CyGLContext;
import com.kitfox.coyote.renderer.CyGLWrapper;
import com.kitfox.coyote.renderer.CyGLWrapper.ActiveTexture;
import com.kitfox.coyote.renderer.CyGLWrapper.Capability;
import com.kitfox.coyote.renderer.CyGLWrapper.DataType;
import com.kitfox.coyote.renderer.CyGLWrapper.IndiciesType;
import com.kitfox.coyote.renderer.CyGLWrapper.InternalFormatTex;
import com.kitfox.coyote.renderer.CyGLWrapper.ShaderType;
import com.kitfox.coyote.renderer.CyGLWrapper.TexParam;
import com.kitfox.coyote.renderer.CyGLWrapper.TexParamName;
import com.kitfox.coyote.renderer.CyGLWrapper.TexSubTarget;
import com.kitfox.coyote.renderer.CyGLWrapper.TexTarget;
import com.kitfox.coyote.renderer.CyGLWrapper.VertexDataType;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements a radial gradient.  Linear gradients can be
 * implemented with CyMaterialTextureBlit.
 *
 * @author kitfox
 */
public class CyMaterialGradient extends CyMaterial
{
    private int shaderIdVert;
    private int shaderIdFrag;
    private int programId;

    private int a_position;
//    private int a_texCoord;
    private int u_mvpMatrix;
    private int u_texMatrix;
    private int u_tex0;
    private int u_type;
    private int u_opacity;

    FloatBuffer matrixBuf = BufferUtil.allocateFloat(16);

    GradientTex gradientTexData;
    CyTextureImage texture;

    //Gradient resolution
    private static final int GRAD_RES = 256;

    public CyMaterialGradient()
    {
        gradientTexData = new GradientTex();
        texture = new CyTextureImage(
                TexTarget.GL_TEXTURE_2D,
                InternalFormatTex.GL_RGBA,
                DataType.GL_UNSIGNED_BYTE,
                GRAD_RES, 1,
                CyTransparency.TRANSLUCENT,
                gradientTexData);
    }

    private void init(CyGLWrapper gl)
    {
        try
        {
            shaderIdVert =
                    loadShader(gl, ShaderType.GL_VERTEX_SHADER,
                    "/material/gradient.vert");
            shaderIdFrag =
                    loadShader(gl, ShaderType.GL_FRAGMENT_SHADER,
                    "/material/gradient.frag");

            programId = gl.glCreateProgram();
            gl.glAttachShader(programId, shaderIdVert);
            gl.glAttachShader(programId, shaderIdFrag);
            gl.glLinkProgram(programId);

            checkProgramValid(gl, programId);

            a_position = gl.glGetAttribLocation(programId, "a_position");
//            a_texCoord = gl.glGetAttribLocation(programId, "a_texCoord");
            u_mvpMatrix = gl.glGetUniformLocation(programId, "u_mvpMatrix");
            u_texMatrix = gl.glGetUniformLocation(programId, "u_texMatrix");
            u_tex0 = gl.glGetUniformLocation(programId, "u_tex0");
            u_type = gl.glGetUniformLocation(programId, "u_type");
            u_opacity = gl.glGetUniformLocation(programId, "u_opacity");
        } catch (CyProgramException ex)
        {
            Logger.getLogger(CyMaterialGradient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CyShaderException ex)
        {
            Logger.getLogger(CyMaterialGradient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void bind(CyGLWrapper gl)
    {
        if (programId == 0)
        {
            init(gl);
        }

        gl.glDisable(Capability.GL_DEPTH_TEST);

        gl.glUseProgram(programId);
    }

    public void render(CyGLContext ctx, CyGLWrapper gl, CyMaterialGradientDrawRecord rec)
    {
        float opacity = rec.getOpacity();
//        CyTextureSource texture = rec.getTexture();

        if (opacity >= 1 && texture.getTransparency() == CyTransparency.OPAQUE)
        {
            gl.glDisable(Capability.GL_BLEND);
        }
        else
        {
            gl.glEnable(Capability.GL_BLEND);
            gl.glBlendFunc(CyGLWrapper.BlendFactor.GL_SRC_ALPHA,
                    CyGLWrapper.BlendFactor.GL_ONE_MINUS_SRC_ALPHA);
        }

        CyGradientStops stops = rec.getStops();
        
        //Upload uniforms
        CyMatrix4d mvpMatrix = rec.getMvpMatrix();
        CyMatrix4d texMatrix = rec.getLocalToTexMatrix();
//        try
//        {
//            texMatrix.invert();
//        }
//        catch (UnsupportedOperationException ex)
//        {
//            texMatrix = CyMatrix4d.createIdentity();
//        }
        mvpMatrix.toBufferc(matrixBuf);
        gl.glUniformMatrix4fv(u_mvpMatrix, 1, false, matrixBuf);
        texMatrix.toBufferc(matrixBuf);
        gl.glUniformMatrix4fv(u_texMatrix, 1, false, matrixBuf);
        gl.glUniform1f(u_opacity, opacity);

        gl.glActiveTexture(ActiveTexture.GL_TEXTURE0);
        if (texture != null)
        {
            texture.bindTexture(ctx, gl);
        }
        else
        {
            gl.glBindTexture(TexTarget.GL_TEXTURE_2D, 0);
        }
        gl.glUniform1i(u_tex0, 0);

        gl.glTexParameter(texture.getTarget(),
                TexParamName.GL_TEXTURE_MIN_FILTER, 
                TexParam.GL_LINEAR_MIPMAP_LINEAR);
        gl.glTexParameter(texture.getTarget(),
                TexParamName.GL_TEXTURE_MAG_FILTER, 
                TexParam.GL_LINEAR);

        switch (stops.getCycleMethod())
        {
            default:
            case NO_CYCLE:
                gl.glTexParameter(texture.getTarget(),
                        TexParamName.GL_TEXTURE_WRAP_S,
                        TexParam.GL_CLAMP_TO_EDGE);
                break;
            case REPEAT:
                gl.glTexParameter(texture.getTarget(),
                        TexParamName.GL_TEXTURE_WRAP_S,
                        TexParam.GL_REPEAT);
                break;
            case REFLECT:
                gl.glTexParameter(texture.getTarget(),
                        TexParamName.GL_TEXTURE_WRAP_S,
                        TexParam.GL_MIRRORED_REPEAT);
                break;
        }
        gl.glTexParameter(texture.getTarget(),
                TexParamName.GL_TEXTURE_WRAP_T, TexParam.GL_CLAMP_TO_EDGE);
        
        gl.glGenerateMipmap(texture.getTarget());

        switch (stops.getStyle())
        {
            default:
            case LINEAR:
                gl.glUniform1i(u_type, 0);
                break;
            case RADIAL:
                gl.glUniform1i(u_type, 1);
                break;
        }

        gradientTexData.update(stops);

        //Bind vertex buffers
        CyVertexBuffer mesh = rec.getMesh();
        mesh.bind(ctx, gl);
        {
            CyVertexArrayInfo info = mesh.getVertexArrayInfo(KEY_POSITION);
            gl.glVertexAttribPointer(a_position, info.getSize(),
                    VertexDataType.GL_FLOAT, false, 0, info.getOffset());
            gl.glEnableVertexAttribArray(a_position);
        }

//        {
//            CyVertexArrayInfo info = mesh.getVertexArrayInfo(KEY_TEXCOORD);
//            gl.glVertexAttribPointer(a_texCoord, info.getSize(),
//                    VertexDataType.GL_FLOAT, false, 0, info.getOffset());
//            gl.glEnableVertexAttribArray(a_texCoord);
//        }

        gl.glDrawElements(mesh.getDrawMode(), mesh.getIndexCount(),
                IndiciesType.GL_UNSIGNED_SHORT, 0);
    }

    //---------------------------------------
    class GradientTex extends CyTextureDataProvider
    {
        ByteBuffer buf;

        public GradientTex()
        {
            buf = BufferUtil.allocateByte(GRAD_RES * 4);
        }

        private void update(CyGradientStops stops)
        {
            float[] color = new float[4];

            float dc = 1f / GRAD_RES;
            for (int i = 0; i < GRAD_RES; ++i)
            {
                stops.sampleRaw(i * dc, color);
                buf.put((byte)(int)(color[0] * 255));
                buf.put((byte)(int)(color[1] * 255));
                buf.put((byte)(int)(color[2] * 255));
                buf.put((byte)(int)(color[3] * 255));
            }

            buf.rewind();

            fireTextureDataChanged();
        }

        @Override
        public Buffer getData(TexSubTarget target)
        {
            return buf;
        }
    }
}
