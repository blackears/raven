/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
