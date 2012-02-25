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

package com.kitfox.coyote.material.checker;

import com.kitfox.coyote.material.CyDrawRecordMaterialSimple;
import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.coyote.renderer.CyDrawRecord;
import com.kitfox.coyote.renderer.CyGLContext;
import com.kitfox.coyote.renderer.CyGLWrapper;

/**
 *
 * @author kitfox
 */
public class CyMaterialCheckerDrawRecord extends CyDrawRecordMaterialSimple
{
    private CyColor4f colorFg = CyColor4f.WHITE;
    private CyColor4f colorBg = CyColor4f.BLACK;
    private float lineWidth = 8;

    @Override
    public void render(CyGLContext ctx, CyGLWrapper gl, CyDrawRecord prevRecord)
    {
        CyMaterialChecker mat = ctx.getMaterial(CyMaterialChecker.class);
        if (mat == null)
        {
            mat = new CyMaterialChecker();
            ctx.registerMaterial(mat);
        }


        mat.bind(gl);

        mat.render(ctx, gl, this);
    }

    @Override
    public void dispose()
    {
        CyMaterialCheckerDrawRecordFactory.inst().recycleRecord(this);
    }

    /**
     * @return the colorFg
     */
    public CyColor4f getColorFg()
    {
        return colorFg;
    }

    /**
     * @param colorFg the colorFg to set
     */
    public void setColorFg(CyColor4f colorFg)
    {
        this.colorFg = colorFg;
    }

    /**
     * @return the colorBg
     */
    public CyColor4f getColorBg()
    {
        return colorBg;
    }

    /**
     * @param colorBg the colorBg to set
     */
    public void setColorBg(CyColor4f colorBg)
    {
        this.colorBg = colorBg;
    }

    /**
     * @return the lineWidth
     */
    public float getLineWidth()
    {
        return lineWidth;
    }

    /**
     * @param lineWidth the lineWidth to set
     */
    public void setLineWidth(float lineWidth)
    {
        this.lineWidth = lineWidth;
    }
    
}
