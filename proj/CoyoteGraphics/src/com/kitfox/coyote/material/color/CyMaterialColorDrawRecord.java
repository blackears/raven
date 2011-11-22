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

import com.kitfox.coyote.material.CyDrawRecordMaterialSimple;
import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.coyote.renderer.CyDrawRecord;
import com.kitfox.coyote.renderer.CyGLContext;
import com.kitfox.coyote.renderer.CyGLWrapper;

/**
 *
 * @author kitfox
 */
public class CyMaterialColorDrawRecord extends CyDrawRecordMaterialSimple
{
//    boolean disposed;

//        private CyMatrix4d mvpMatrix = CyMatrix4d.createIdentity();
    protected CyColor4f color = CyColor4f.WHITE;

//        public void init(CyDrawStack renderer,
//            CyColor4f color, CyVertexBuffer mesh)
//        {
//            this.color = color;
//            init(renderer, mesh);
//        }

    @Override
    public void render(CyGLContext ctx, CyGLWrapper gl, CyDrawRecord prevRecord)
    {
        CyMaterialColor mat = ctx.getMaterial(CyMaterialColor.class);
        if (mat == null)
        {
            mat = new CyMaterialColor();
            ctx.registerMaterial(mat);
        }


//        if (!(prevRecord instanceof DrawRecord))
//        {
            mat.bind(gl);
//        }

            mat.render(ctx, gl, this);
//        float alpha = color.a * opacity;
//
//        if (alpha >= 1)
//        {
//            gl.glDisable(Capability.GL_BLEND);
//        }
//        else
//        {
//            gl.glEnable(Capability.GL_BLEND);
//            gl.glBlendFunc(GLWrapper.BlendFactor.GL_SRC_ALPHA,
//                    GLWrapper.BlendFactor.GL_ONE_MINUS_SRC_ALPHA);
//        }
//
//        //Upload uniforms
//        mvpMatrix.toBufferc(matrixBuf);
//        gl.glUniformMatrix4fv(u_mvpMatrix, 1, false, matrixBuf);
//        gl.glUniform4f(u_color, color.r, color.g, color.b, alpha);
//
//        //Bind vertex buffers
//        getMesh().bind(gl);
//        {
//            CyVertexArrayInfo info = getMesh().getVertexArrayInfo(KEY_POSITION);
//            gl.glVertexAttribPointer(a_position, info.getSize(),
//                    VertexDataType.GL_FLOAT, false, 0, info.getOffset());
//            gl.glEnableVertexAttribArray(a_position);
//        }
//
//        gl.glDrawElements(getMesh().getDrawMode(), getMesh().getIndexCount(),
//                IndiciesType.GL_UNSIGNED_SHORT, 0);
    }

//    public void recycle()
//    {
//        disposed = false;
//    }

    @Override
    public void dispose()
    {
//        if (disposed)
//        {
//            throw new RuntimeException("Disposing twice");
//        }
//
//        disposed = true;
        //recordPool.add(this);
        CyMaterialColorDrawRecordFactory.inst().recycleRecord(this);
    }

    /**
     * @return the color
     */
    public CyColor4f getColor()
    {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(CyColor4f color)
    {
        this.color = color;
    }
    
}
