/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.coyote.renderer.android;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import com.kitfox.coyote.drawRecord.CyDrawGroupZOrder;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyGLContext;
import com.kitfox.coyote.renderer.CyRendererListener;
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 *
 * @author kitfox
 */
public class CyGLView extends GLSurfaceView
        implements GLSurfaceView.Renderer
{
    CyGLContext glContext;

    ArrayList<CyRendererListener> listeners =
            new ArrayList<CyRendererListener>();
    
    public CyGLView(Context context)
    {
        super(context);

        setEGLContextClientVersion(2);

        setRenderer(this);
    }

    public void addCyRendererListener(CyRendererListener l)
    {
        listeners.add(l);
    }

    public void removeCyRendererListener(CyRendererListener l)
    {
        listeners.remove(l);
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config)
    {
        glContext = new CyGLContext();
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 unused)
    {
        CyGLWrapperAndroid gl = new CyGLWrapperAndroid();
        
        glContext.processActions(gl);

        CyDrawGroupZOrder drawGroup = new CyDrawGroupZOrder();
        CyDrawStack rend = new CyDrawStack(
                getWidth(), getHeight(),
                drawGroup);

        for (int k = 0; k < listeners.size(); ++k)
        {
            listeners.get(k).render(rend);
        }
        
        drawGroup.render(glContext, gl, null);
        drawGroup.dispose();
    }
    
}
