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
        
{
    CyGLContext glContext;

    ArrayList<CyRendererListener> listeners =
            new ArrayList<CyRendererListener>();
    
    public CyGLView(Context context)
    {
        super(context);

        System.out.println("+++ View created!");
        setEGLContextClientVersion(2);

        setRenderer(new Renderer());
    }

    public void addCyRendererListener(CyRendererListener l)
    {
        listeners.add(l);
    }

    public void removeCyRendererListener(CyRendererListener l)
    {
        listeners.remove(l);
    }
    
    //----------------------------
    class Renderer implements GLSurfaceView.Renderer
    {
        @Override
        public void onSurfaceCreated(GL10 unused, EGLConfig config)
        {
            System.out.println("+++ Surf Created!");
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
            System.out.println("+++ Draw Frame!");
            /*
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
            * 
            */
        }
    }
}
