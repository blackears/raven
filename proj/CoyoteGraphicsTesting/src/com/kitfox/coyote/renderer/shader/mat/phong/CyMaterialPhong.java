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

package com.kitfox.coyote.renderer.shader.mat.phong;

import com.kitfox.coyote.material.textureBlit.CyMaterialTextureBlit;
import com.kitfox.coyote.math.BufferUtil;
import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector3d;
import com.kitfox.coyote.renderer.CyMaterial;
import com.kitfox.coyote.renderer.CyProgramException;
import com.kitfox.coyote.renderer.CyShaderException;
import com.kitfox.coyote.renderer.CyVertexArrayInfo;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.coyote.renderer.CyGLContext;
import com.kitfox.coyote.renderer.CyGLWrapper;
import com.kitfox.coyote.renderer.CyGLWrapper.Capability;
import com.kitfox.coyote.renderer.CyGLWrapper.DepthFunc;
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
public class CyMaterialPhong extends CyMaterial
{
    private int shaderIdVert;
    private int shaderIdFrag;
    private int programId;

    private int a_position;
    private int a_normal;
    private int u_colorDif;
    private int u_colorSpec;
    private int u_lightPos;
    private int u_shininess;
    private int u_mvpMatrix;
    private int u_mvMatrix;
    private int u_mvITMatrix;

    FloatBuffer matrixBuf = BufferUtil.allocateFloat(16);

    public CyMaterialPhong()
    {
    }

    private void init(CyGLWrapper gl)
    {
        System.err.println("Init CyMaterialPhong");
        
        try
        {
            shaderIdVert =
                    loadShader(gl, ShaderType.GL_VERTEX_SHADER,
                    "/material/phong.vert");
            shaderIdFrag =
                    loadShader(gl, ShaderType.GL_FRAGMENT_SHADER,
                    "/material/phong.frag");

            programId = gl.glCreateProgram();
            gl.glAttachShader(programId, shaderIdVert);
            gl.glAttachShader(programId, shaderIdFrag);
            gl.glLinkProgram(programId);

            checkProgramValid(gl, programId);

            a_position = gl.glGetAttribLocation(programId, "a_position");
            a_normal = gl.glGetAttribLocation(programId, "a_normal");
            u_colorDif = gl.glGetUniformLocation(programId, "u_colorDif");
            u_colorSpec = gl.glGetUniformLocation(programId, "u_colorSpec");
            u_lightPos = gl.glGetUniformLocation(programId, "u_lightPos");
            u_shininess = gl.glGetUniformLocation(programId, "u_shininess");
            u_mvpMatrix = gl.glGetUniformLocation(programId, "u_mvpMatrix");
            u_mvMatrix = gl.glGetUniformLocation(programId, "u_mvMatrix");
            u_mvITMatrix = gl.glGetUniformLocation(programId, "u_mvITMatrix");
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
        gl.glDepthMask(false);

        gl.glUseProgram(programId);
    }

    protected void render(CyGLContext ctx, CyGLWrapper gl, CyMaterialPhongDrawRecord rec)
    {
        CyColor4f colorDif = rec.getColorDiffuse();
        CyColor4f colorSpec = rec.getColorSpecular();
        
        float alpha = colorDif.a * rec.getOpacity();

//        if (alpha >= 1)
//        {
            gl.glDisable(Capability.GL_BLEND);
//        }
//        else
//        {
//            gl.glEnable(Capability.GL_BLEND);
//            gl.glBlendFunc(CyGLWrapper.BlendFactor.GL_SRC_ALPHA,
//                    CyGLWrapper.BlendFactor.GL_ONE_MINUS_SRC_ALPHA);
//        }
        
        gl.glEnable(Capability.GL_CULL_FACE);
        gl.glEnable(Capability.GL_DEPTH_TEST);
        gl.glDepthFunc(DepthFunc.GL_LEQUAL);
        gl.glDepthMask(true);
        

        //Upload uniforms
        CyMatrix4d mvpMatrix = rec.getMvpMatrix();
        mvpMatrix.toBufferc(matrixBuf);
        gl.glUniformMatrix4fv(u_mvpMatrix, 1, false, matrixBuf);
        
        CyMatrix4d mvMatrix = rec.getMvMatrix();
        mvMatrix.toBufferc(matrixBuf);
        gl.glUniformMatrix4fv(u_mvMatrix, 1, false, matrixBuf);
        
        //CyMatrix4d mvITMatrix = rec.getMvMatrix();
        mvMatrix.invert();
        mvMatrix.transpose();
        mvMatrix.toBufferc(matrixBuf);
        gl.glUniformMatrix4fv(u_mvITMatrix, 1, false, matrixBuf);

        gl.glUniform4f(u_colorDif, colorDif.r, colorDif.g, colorDif.b, alpha);
        gl.glUniform4f(u_colorSpec, colorSpec.r, colorSpec.g, colorSpec.b, alpha);
        gl.glUniform1f(u_shininess, rec.getShininess());
        
        {
            CyVector3d lightPos = rec.getLightPos();
            gl.glUniform3f(u_lightPos, 
                    (float)lightPos.x, (float)lightPos.y, (float)lightPos.z);
        }

        //Bind vertex buffers
        CyVertexBuffer mesh = rec.getMesh();
        mesh.bind(ctx, gl);
        {
            CyVertexArrayInfo info = mesh.getVertexArrayInfo(KEY_POSITION);
            gl.glVertexAttribPointer(a_position, info.getSize(),
                    VertexDataType.GL_FLOAT, false, 0, info.getOffset());
            gl.glEnableVertexAttribArray(a_position);
        }
        
        if (a_normal != -1)
        {
            //During debugging, a_normal may be removed by the compiler
            
            CyVertexArrayInfo info = mesh.getVertexArrayInfo(KEY_NORMAL);
            gl.glVertexAttribPointer(a_normal, info.getSize(),
                    VertexDataType.GL_FLOAT, false, 0, info.getOffset());
            gl.glEnableVertexAttribArray(a_normal);
        }

        gl.glDrawElements(mesh.getDrawMode(), mesh.getIndexCount(),
                IndiciesType.GL_UNSIGNED_SHORT, 0);
    }

}
