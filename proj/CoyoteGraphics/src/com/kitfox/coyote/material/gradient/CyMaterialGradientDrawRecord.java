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

import com.kitfox.coyote.material.CyDrawRecordMaterialSimple;
import com.kitfox.coyote.math.CyGradientStops;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.renderer.CyDrawRecord;
import com.kitfox.coyote.renderer.CyGLContext;
import com.kitfox.coyote.renderer.CyGLWrapper;

/**
 *
 * @author kitfox
 */
public class CyMaterialGradientDrawRecord extends CyDrawRecordMaterialSimple
{
    private CyMatrix4d localToTexMatrix = CyMatrix4d.createIdentity();

    private CyGradientStops stops;

    @Override
    public void render(CyGLContext ctx, CyGLWrapper gl, CyDrawRecord prevRecord)
    {
        CyMaterialGradient mat = ctx.getMaterial(CyMaterialGradient.class);
        if (mat == null)
        {
            mat = new CyMaterialGradient();
            ctx.registerMaterial(mat);
        }

        mat.bind(gl);

        mat.render(ctx, gl, this);
    }

    @Override
    public void dispose()
    {
        CyMaterialGradientDrawRecordFactory.inst().recycleRecord(this);
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
     * @return the stops
     */
    public CyGradientStops getStops()
    {
        return stops;
    }

    /**
     * @param stops the stops to set
     */
    public void setStops(CyGradientStops stops)
    {
        this.stops = stops;
    }


}
