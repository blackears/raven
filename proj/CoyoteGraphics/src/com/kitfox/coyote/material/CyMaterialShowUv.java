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

package com.kitfox.coyote.material;

import com.kitfox.coyote.renderer.CyMaterial;

/**
 *
 * @author kitfox
 */
@Deprecated
public class CyMaterialShowUv extends CyMaterial
{
//    private int shaderIdVert;
//    private int shaderIdFrag;
//    private int programId;
//    int surfInst;
//
//    private int a_position;
//    private int a_texCoord;
//    private int u_mvpMatrix;
//    private int u_texMatrix;
//
//    private CyMatrix4d mvpMatrix = CyMatrix4d.createIdentity();
//    private CyMatrix4d texMatrix = CyMatrix4d.createIdentity();
//
//    FloatBuffer matrixBuf = BufferUtil.allocateFloat(16);
//
//    private CyTextureSource texture;
//
//    private void init(GLWrapper gl)
//    {
//        surfInst = gl.getSurfaceInstanceNumber();
//
//        try
//        {
//            shaderIdVert =
//                    loadShader(gl, ShaderType.GL_VERTEX_SHADER,
//                    "/material/showUv.vert");
//            shaderIdFrag =
//                    loadShader(gl, ShaderType.GL_FRAGMENT_SHADER,
//                    "/material/showUv.frag");
//
//            programId = gl.glCreateProgram();
//            gl.glAttachShader(programId, shaderIdVert);
//            gl.glAttachShader(programId, shaderIdFrag);
//            gl.glLinkProgram(programId);
//
//            checkProgramValid(gl, programId);
//
//            a_position = gl.glGetAttribLocation(programId, "a_position");
//            a_texCoord = gl.glGetAttribLocation(programId, "a_texCoord");
//            u_mvpMatrix = gl.glGetUniformLocation(programId, "u_mvpMatrix");
//            u_texMatrix = gl.glGetUniformLocation(programId, "u_texMatrix");
//        } catch (CyProgramException ex)
//        {
//            Logger.getLogger(CyMaterialShowUv.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (CyShaderException ex)
//        {
//            Logger.getLogger(CyMaterialShowUv.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    @Override
//    public void bind(GLWrapper gl)
//    {
//        if (programId == 0 || gl.getSurfaceInstanceNumber() != surfInst)
//        {
//            init(gl);
//        }
//
////        gl.glEnable(Capability.GL_BLEND);
////        gl.glDisable(Capability.GL_DEPTH_TEST);
////        gl.glDepthMask(false);
//
//        gl.glUseProgram(programId);
//
//        //Upload uniforms
//        mvpMatrix.toBufferc(matrixBuf);
//        gl.glUniformMatrix4fv(u_mvpMatrix, 1, false, matrixBuf);
//        texMatrix.toBufferc(matrixBuf);
//        gl.glUniformMatrix4fv(u_texMatrix, 1, false, matrixBuf);
//
//        gl.glActiveTexture(ActiveTexture.GL_TEXTURE0);
//        if (texture != null)
//        {
//            texture.bind(gl);
//        }
//        else
//        {
//            gl.glBindTexture(TexTarget.GL_TEXTURE_2D, 0);
//        }
//    }
//
//    public void draw(GLWrapper gl, CyVertexBuffer mesh)
//    {
//        //Bind vertex buffers
//        mesh.bind(gl);
//        {
//            CyVertexArrayInfo info = mesh.getVertexArrayInfo(KEY_POSITION);
//            gl.glVertexAttribPointer(a_position, info.getSize(),
//                    VertexDataType.GL_FLOAT, false, 0, info.getOffset());
//            gl.glEnableVertexAttribArray(a_position);
//        }
//
//        {
//            CyVertexArrayInfo info = mesh.getVertexArrayInfo(KEY_TEXCOORD);
//            gl.glVertexAttribPointer(a_texCoord, info.getSize(),
//                    VertexDataType.GL_FLOAT, false, 0, info.getOffset());
//            gl.glEnableVertexAttribArray(a_texCoord);
//        }
//
//        gl.glDrawElements(mesh.getDrawMode(), mesh.getIndexCount(),
//                IndiciesType.GL_UNSIGNED_SHORT, 0);
//    }
//
//    /**
//     * @return the mvpMatrix
//     */
//    public CyMatrix4d getMvpMatrix()
//    {
//        return new CyMatrix4d(mvpMatrix);
//    }
//
//    /**
//     * @param mvpMatrix the mvpMatrix to set
//     */
//    public void setMvpMatrix(CyMatrix4d mvpMatrix)
//    {
//        this.mvpMatrix.set(mvpMatrix);
//    }
//
//    /**
//     * @return the texture
//     */
//    public CyTextureSource getTexture()
//    {
//        return texture;
//    }
//
//    /**
//     * @param texture the texture to set
//     */
//    public void setTexture(CyTextureSource texture)
//    {
//        this.texture = texture;
//    }
//
//    /**
//     * @return the texMatrix
//     */
//    public CyMatrix4d getTexMatrix()
//    {
//        return new CyMatrix4d(texMatrix);
//    }
//
//    /**
//     * @param texMatrix the texMatrix to set
//     */
//    public void setTexMatrix(CyMatrix4d texMatrix)
//    {
//        this.texMatrix.set(texMatrix);
//    }
}
