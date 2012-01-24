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

import com.kitfox.coyote.material.CyDrawRecordMaterialSimple;
import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector3d;
import com.kitfox.coyote.renderer.CyDrawRecord;
import com.kitfox.coyote.renderer.CyGLContext;
import com.kitfox.coyote.renderer.CyGLWrapper;

/**
 *
 * @author kitfox
 */
public class CyMaterialPhongDrawRecord extends CyDrawRecordMaterialSimple
{
    protected CyColor4f color = CyColor4f.WHITE;
    private CyVector3d lightPos = new CyVector3d();
    protected CyMatrix4d mvMatrix = CyMatrix4d.createIdentity();

    @Override
    public void render(CyGLContext ctx, CyGLWrapper gl, CyDrawRecord prevRecord)
    {
        CyMaterialPhong mat = ctx.getMaterial(CyMaterialPhong.class);
        if (mat == null)
        {
            mat = new CyMaterialPhong();
            ctx.registerMaterial(mat);
        }

        mat.bind(gl);

        mat.render(ctx, gl, this);
    }

    @Override
    public void dispose()
    {
        CyMaterialPhongDrawRecordFactory.inst().recycleRecord(this);
    }

    /**
     * @return the mvpMatrix
     */
    public CyMatrix4d getMvMatrix()
    {
        return new CyMatrix4d(mvMatrix);
    }

    /**
     * @param mvpMatrix the mvpMatrix to set
     */
    public void setMvMatrix(CyMatrix4d mvMatrix)
    {
        this.mvMatrix.set(mvMatrix);
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

    /**
     * @return the lightPos
     */
    public CyVector3d getLightPos()
    {
        return lightPos;
    }

    /**
     * @param lightPos the lightPos to set
     */
    public void setLightPos(CyVector3d lightPos)
    {
        this.lightPos = lightPos;
    }
    
}
