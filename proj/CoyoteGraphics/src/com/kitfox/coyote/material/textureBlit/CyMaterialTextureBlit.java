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

package com.kitfox.coyote.material.textureBlit;

import com.kitfox.coyote.renderer.CyProgramException;
import com.kitfox.coyote.renderer.CyShaderException;
import com.kitfox.coyote.renderer.CyMaterial;
import com.kitfox.coyote.math.BufferUtil;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.renderer.CyTextureSource;
import com.kitfox.coyote.renderer.CyTransparency;
import com.kitfox.coyote.renderer.CyVertexArrayInfo;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.coyote.renderer.CyGLContext;
import com.kitfox.coyote.renderer.CyGLWrapper;
import com.kitfox.coyote.renderer.CyGLWrapper.ActiveTexture;
import com.kitfox.coyote.renderer.CyGLWrapper.Capability;
import com.kitfox.coyote.renderer.CyGLWrapper.IndiciesType;
import com.kitfox.coyote.renderer.CyGLWrapper.ShaderType;
import com.kitfox.coyote.renderer.CyGLWrapper.TexParam;
import com.kitfox.coyote.renderer.CyGLWrapper.TexParamName;
import com.kitfox.coyote.renderer.CyGLWrapper.TexTarget;
import com.kitfox.coyote.renderer.CyGLWrapper.VertexDataType;
import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class CyMaterialTextureBlit extends CyMaterial
{
    private int shaderIdVert;
    private int shaderIdFrag;
    private int programId;

    private int a_position;
    private int u_mvpMatrix;
    private int u_texMatrix;
    private int u_tex0;
    private int u_opacity;

    FloatBuffer matrixBuf = BufferUtil.allocateFloat(16);

    public CyMaterialTextureBlit()
    {
    }

    private void init(CyGLWrapper gl)
    {
        try
        {
            shaderIdVert =
                    loadShader(gl, ShaderType.GL_VERTEX_SHADER,
                    "/material/textureBlit.vert");
            shaderIdFrag =
                    loadShader(gl, ShaderType.GL_FRAGMENT_SHADER,
                    "/material/textureBlit.frag");

            programId = gl.glCreateProgram();
            gl.glAttachShader(programId, shaderIdVert);
            gl.glAttachShader(programId, shaderIdFrag);
            gl.glLinkProgram(programId);

            checkProgramValid(gl, programId);

            a_position = gl.glGetAttribLocation(programId, "a_position");
            u_mvpMatrix = gl.glGetUniformLocation(programId, "u_mvpMatrix");
            u_texMatrix = gl.glGetUniformLocation(programId, "u_texMatrix");
            u_tex0 = gl.glGetUniformLocation(programId, "u_tex0");
            u_opacity = gl.glGetUniformLocation(programId, "u_opacity");
        } catch (CyProgramException ex)
        {
            Logger.getLogger(CyMaterialTextureBlit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CyShaderException ex)
        {
            Logger.getLogger(CyMaterialTextureBlit.class.getName()).log(Level.SEVERE, null, ex);
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


    public void render(CyGLContext ctx, CyGLWrapper gl, CyMaterialTextureBlitDrawRecord rec)
    {
        float opacity = rec.getOpacity();
        CyTextureSource texture = rec.getTexture();

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

        //Upload uniforms
        CyMatrix4d mvpMatrix = rec.getMvpMatrix();
        CyMatrix4d texMatrix = rec.getTexToLocalMatrix();
        texMatrix.invert();
        mvpMatrix.toBufferc(matrixBuf);
        gl.glUniformMatrix4fv(u_mvpMatrix, 1, false, matrixBuf);
        texMatrix.toBufferc(matrixBuf);
        gl.glUniformMatrix4fv(u_texMatrix, 1, false, matrixBuf);
        gl.glUniform1f(u_opacity, opacity);

        gl.glActiveTexture(ActiveTexture.GL_TEXTURE0);
        if (texture != null)
        {
            texture.bind(ctx, gl);
        }
        else
        {
            gl.glBindTexture(TexTarget.GL_TEXTURE_2D, 0);
        }
        gl.glUniform1i(u_tex0, 0);

        TexParam minFilter = rec.getMinFilter();
        TexParam magFilter = rec.getMagFilter();
        TexParam wrapS = rec.getWrapS();
        TexParam wrapT = rec.getWrapT();
        gl.glTexParameter(texture.getTarget(),
                TexParamName.GL_TEXTURE_MIN_FILTER, minFilter);
        gl.glTexParameter(texture.getTarget(),
                TexParamName.GL_TEXTURE_MAG_FILTER, magFilter);
        gl.glTexParameter(texture.getTarget(),
                TexParamName.GL_TEXTURE_WRAP_S, wrapS);
        gl.glTexParameter(texture.getTarget(),
                TexParamName.GL_TEXTURE_WRAP_T, wrapT);

        gl.glGenerateMipmap(texture.getTarget());


        //Bind vertex buffers
        CyVertexBuffer mesh = rec.getMesh();
        mesh.bind(ctx, gl);
        {
            CyVertexArrayInfo info = mesh.getVertexArrayInfo(KEY_POSITION);
            gl.glVertexAttribPointer(a_position, info.getSize(),
                    VertexDataType.GL_FLOAT, false, 0, info.getOffset());
            gl.glEnableVertexAttribArray(a_position);
        }

        gl.glDrawElements(mesh.getDrawMode(), mesh.getIndexCount(),
                IndiciesType.GL_UNSIGNED_SHORT, 0);
    }
}
