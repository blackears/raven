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

package com.kitfox.coyote.material.color;

import com.kitfox.coyote.material.textureBlit.CyMaterialTextureBlit;
import com.kitfox.coyote.math.BufferUtil;
import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.renderer.CyMaterial;
import com.kitfox.coyote.renderer.CyProgramException;
import com.kitfox.coyote.renderer.CyShaderException;
import com.kitfox.coyote.renderer.CyVertexArrayInfo;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.coyote.renderer.CyGLContext;
import com.kitfox.coyote.renderer.CyGLWrapper;
import com.kitfox.coyote.renderer.CyGLWrapper.Capability;
import com.kitfox.coyote.renderer.CyGLWrapper.IndiciesType;
import com.kitfox.coyote.renderer.CyGLWrapper.ShaderType;
import com.kitfox.coyote.renderer.CyGLWrapper.VertexDataType;
import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class CyMaterialColor extends CyMaterial
{
    private int shaderIdVert;
    private int shaderIdFrag;
    private int programId;

    private int a_position;
    private int u_color;
    private int u_mvpMatrix;

    FloatBuffer matrixBuf = BufferUtil.allocateFloat(16);

    public CyMaterialColor()
    {
    }

    private void init(CyGLWrapper gl)
    {
        try
        {
            shaderIdVert =
                    loadShader(gl, ShaderType.GL_VERTEX_SHADER,
                    "/material/color.vert");
            shaderIdFrag =
                    loadShader(gl, ShaderType.GL_FRAGMENT_SHADER,
                    "/material/color.frag");

            programId = gl.glCreateProgram();
            gl.glAttachShader(programId, shaderIdVert);
            gl.glAttachShader(programId, shaderIdFrag);
            gl.glLinkProgram(programId);

            checkProgramValid(gl, programId);

            a_position = gl.glGetAttribLocation(programId, "a_position");
            u_color = gl.glGetUniformLocation(programId, "u_color");
            u_mvpMatrix = gl.glGetUniformLocation(programId, "u_mvpMatrix");
        } catch (CyProgramException ex)
        {
            Logger.getLogger(CyMaterialTextureBlit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CyShaderException ex)
        {
            Logger.getLogger(CyMaterialTextureBlit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void bind(CyGLWrapper gl)
    {
        if (programId == 0)
        {
            init(gl);
        }
//        gl.glDisable(Capability.GL_DEPTH_TEST);
        gl.glDepthMask(false);

        gl.glUseProgram(programId);
    }

    protected void render(CyGLContext ctx, CyGLWrapper gl, CyMaterialColorDrawRecord rec)
    {
        CyColor4f color = rec.getColor();
        float alpha = color.a * rec.getOpacity();

        if (alpha >= 1)
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
        mvpMatrix.toBufferc(matrixBuf);
        gl.glUniformMatrix4fv(u_mvpMatrix, 1, false, matrixBuf);
        gl.glUniform4f(u_color, color.r, color.g, color.b, alpha);

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
