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

import com.kitfox.coyote.material.CyDrawRecordMaterialSimple;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.renderer.CyDrawRecord;
import com.kitfox.coyote.renderer.CyTextureSource;
import com.kitfox.coyote.renderer.CyGLContext;
import com.kitfox.coyote.renderer.CyGLWrapper;
import com.kitfox.coyote.renderer.CyGLWrapper.TexParam;

/**
 *
 * @author kitfox
 */
public class CyMaterialTextureBlitDrawRecord extends CyDrawRecordMaterialSimple
{
//    boolean disposed;

    private CyMatrix4d localToTexMatrix = CyMatrix4d.createIdentity();

    private CyTextureSource texture;
    private TexParam minFilter = TexParam.GL_NEAREST;
    private TexParam magFilter = TexParam.GL_NEAREST;
    private TexParam wrapS = TexParam.GL_CLAMP_TO_EDGE;
    private TexParam wrapT = TexParam.GL_CLAMP_TO_EDGE;

    @Override
    public void render(CyGLContext ctx, CyGLWrapper gl, CyDrawRecord prevRecord)
    {
        CyMaterialTextureBlit mat = ctx.getMaterial(CyMaterialTextureBlit.class);
        if (mat == null)
        {
            mat = new CyMaterialTextureBlit();
            ctx.registerMaterial(mat);
        }


//        if (!(prevRecord instanceof DrawRecord))
//        {
            mat.bind(gl);
//        }

            mat.render(ctx, gl, this);
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
        CyMaterialTextureBlitDrawRecordFactory.inst().recycleRecord(this);
    }

    /**
     * @return the texture
     */
    public CyTextureSource getTexture()
    {
        return texture;
    }

    /**
     * @param texture the texture to set
     */
    public void setTexture(CyTextureSource texture)
    {
        this.texture = texture;
    }

    /**
     * @return the texMatrix
     */
    public CyMatrix4d getLocalToTexMatrix()
    {
        return new CyMatrix4d(localToTexMatrix);
    }

    /**
     * @param localToTexMatrix the texMatrix to set
     */
    public void setLocalToTexMatrix(CyMatrix4d localToTexMatrix)
    {
        this.localToTexMatrix.set(localToTexMatrix);
    }

    /**
     * @return the minFilter
     */
    public TexParam getMinFilter()
    {
        return minFilter;
    }

    /**
     * @param minFilter the minFilter to set
     */
    public void setMinFilter(TexParam minFilter)
    {
        this.minFilter = minFilter;
    }

    /**
     * @return the magFilter
     */
    public TexParam getMagFilter()
    {
        return magFilter;
    }

    /**
     * @param magFilter the magFilter to set
     */
    public void setMagFilter(TexParam magFilter)
    {
        this.magFilter = magFilter;
    }

    /**
     * @return the wrapS
     */
    public TexParam getWrapS()
    {
        return wrapS;
    }

    /**
     * @param wrapS the wrapS to set
     */
    public void setWrapS(TexParam wrapS)
    {
        this.wrapS = wrapS;
    }

    /**
     * @return the wrapT
     */
    public TexParam getWrapT()
    {
        return wrapT;
    }

    /**
     * @param wrapT the wrapT to set
     */
    public void setWrapT(TexParam wrapT)
    {
        this.wrapT = wrapT;
    }
}
