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

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.renderer.CyDrawRecord;
import com.kitfox.coyote.renderer.CyVertexBuffer;

/**
 *
 * @author kitfox
 */
abstract public class CyDrawRecordMaterialSimple extends CyDrawRecord
{
    protected CyVertexBuffer mesh;
    protected CyMatrix4d mvpMatrix = CyMatrix4d.createIdentity();
    protected float opacity;

    /**
     * @return the mvpMatrix
     */
    public CyMatrix4d getMvpMatrix()
    {
        return new CyMatrix4d(mvpMatrix);
    }

    /**
     * @param mvpMatrix the mvpMatrix to set
     */
    public void setMvpMatrix(CyMatrix4d mvpMatrix)
    {
        this.mvpMatrix.set(mvpMatrix);
    }

    /**
     * @return the opacity
     */
    public float getOpacity()
    {
        return opacity;
    }

    /**
     * @param opacity the opacity to set
     */
    public void setOpacity(float opacity)
    {
        this.opacity = opacity;
    }

    /**
     * @return the mesh
     */
    public CyVertexBuffer getMesh()
    {
        return mesh;
    }

    /**
     * @param mesh the mesh to set
     */
    public void setMesh(CyVertexBuffer mesh)
    {
        this.mesh = mesh;
    }
}
