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

package com.kitfox.coyote.drawRecord;

import com.kitfox.coyote.renderer.CyDrawRecord;
import com.kitfox.coyote.renderer.CyGLContext;
import com.kitfox.coyote.renderer.CyGLWrapper;

/**
 *
 * @author kitfox
 */
public class CyDrawRecordClear extends CyDrawRecord
{
    float r;
    float g;
    float b;
    float a;
    boolean color;
    boolean depth;
    boolean stencil;

    public CyDrawRecordClear(float r, float g, float b, float a, boolean color, boolean depth, boolean stencil)
    {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.color = color;
        this.depth = depth;
        this.stencil = stencil;
    }

    @Override
    public void render(CyGLContext ctx, CyGLWrapper gl, CyDrawRecord prevRecord)
    {
        if (color)
        {
            gl.glClearColor(r, g, b, a);
        }
        gl.glClear(color, depth, stencil);
    }

    @Override
    public void dispose()
    {
    }
    
}
