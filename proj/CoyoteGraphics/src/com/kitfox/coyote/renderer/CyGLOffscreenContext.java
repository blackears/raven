/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.coyote.renderer;

/**
 *
 * @author kitfox
 */
public interface CyGLOffscreenContext
{
    public CyGLWrapper getGL();
    public CyGLContext getGLContext();
    public void dispose();    
}
