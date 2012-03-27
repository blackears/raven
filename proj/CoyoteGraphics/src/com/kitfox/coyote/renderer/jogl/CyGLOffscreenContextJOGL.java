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

package com.kitfox.coyote.renderer.jogl;

import com.kitfox.coyote.renderer.CyGLContext;
import com.kitfox.coyote.renderer.CyGLOffscreenContext;
import javax.media.opengl.GL2;
import javax.media.opengl.GLContext;

/**
 *
 * @author kitfox
 */
public class CyGLOffscreenContextJOGL
        implements CyGLOffscreenContext
{
    GLContext context;
    GL2 gl;
    private CyGLContext glContext;

    public CyGLOffscreenContextJOGL(GLContext context, GL2 gl,
            CyGLContext glContext)
    {
        this.context = context;
        this.gl = gl;
        this.glContext = glContext;

        context.makeCurrent();
    }

    @Override
    public CyGLWrapperJOGL getGL()
    {
        return new CyGLWrapperJOGL(gl);
    }

    /**
     * @return the glContext
     */
    @Override
    public CyGLContext getGLContext()
    {
        return glContext;
    }

    @Override
    public void dispose()
    {
        context.release();
    }
}
