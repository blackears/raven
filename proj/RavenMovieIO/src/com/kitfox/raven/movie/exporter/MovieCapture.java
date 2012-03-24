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

package com.kitfox.raven.movie.exporter;

import com.kitfox.coyote.drawRecord.CyDrawGroupZOrder;
import com.kitfox.coyote.math.BufferUtil;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyGLContext;
import com.kitfox.coyote.renderer.CyGLWrapper;
import com.kitfox.coyote.renderer.jogl.CyGLWrapperJOGL;
import com.kitfox.raven.editor.node.scene.RenderContext;
import com.kitfox.raven.editor.view.displayCy.CyRenderService;
import com.kitfox.raven.util.tree.FrameKey;
import com.kitfox.raven.util.tree.NodeDocument;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import javax.media.nativewindow.AbstractGraphicsDevice;
import javax.media.opengl.DefaultGLCapabilitiesChooser;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLCapabilitiesChooser;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLPbuffer;
import javax.media.opengl.GLProfile;
import javax.swing.JOptionPane;

/**
 *
 * @author kitfox
 */
public class MovieCapture
{
    NodeDocument doc;
    int width;
    int height;
    CyGLContext glContext;
    ByteBuffer buf;
    GLPbuffer drawable;
    
    public MovieCapture(NodeDocument doc, int width, int height)
    {
        this.doc = doc;
        this.width = width;
        this.height = height;
        buf = BufferUtil.allocateByte(width * height * 4);
        glContext = new CyGLContext();

        GLDrawableFactory fact = GLDrawableFactory.getDesktopFactory();
        
        if (!fact.canCreateGLPbuffer(null))
        {
            JOptionPane.showMessageDialog(doc.getEnv().getSwingRoot(),
                "Cannot create pbuffer to accellerate offscreen rendering", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        GLCapabilities caps = new GLCapabilities(GLProfile.getDefault());
        caps.setBackgroundOpaque(false);
        
        GLCapabilitiesChooser glCapsChooser = new DefaultGLCapabilitiesChooser();
        AbstractGraphicsDevice agd = fact.getDefaultDevice();
        
        drawable = fact.createGLPbuffer(agd, 
                caps, glCapsChooser, 
                width, height, 
                null);
        
    }
    
    private void buildImageBytes(FrameKey key)
    {
        CyDrawGroupZOrder drawgroup = buildDrawlist(key);
        
        GLContext context = drawable.getContext();
        context.makeCurrent();
        
        CyGLWrapperJOGL glWrap = new CyGLWrapperJOGL(
                drawable.getGL().getGL2());
        
        drawgroup.render(glContext, glWrap, drawgroup);

        buf.rewind();
        glWrap.glReadPixels(0, 0, width, height, 
                CyGLWrapper.ReadPixelsFormat.GL_RGBA, 
                CyGLWrapper.DataType.GL_UNSIGNED_BYTE, buf);

        context.release();
        drawgroup.dispose();
    }

    public BufferedImage getImage(FrameKey key, boolean hasAlpha)
    {
        buildImageBytes(key);
        
        BufferedImage img = new BufferedImage(
                width, height, 
                hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        
        for (int j = 0; j < height; ++j)
        {
            for (int i = 0; i < width; ++i)
            {
                byte r = buf.get();
                byte g = buf.get();
                byte b = buf.get();
                byte a = buf.get();
                if (hasAlpha)
                {
                    a = -1;
                }

                int rgba = ((a & 0xff) << 24) 
                        | ((r & 0xff) << 16) 
                        | ((g & 0xff) << 8) 
                        | (b & 0xff);

                img.setRGB(i, (height - j - 1), rgba);
            }
        }

        return img;
    }

    private CyDrawGroupZOrder buildDrawlist(FrameKey key)
    {
        CyDrawGroupZOrder drawGroup = new CyDrawGroupZOrder();
        
        CyDrawStack stack = new CyDrawStack(width, height, 
                drawGroup);
        
        RenderContext ctx = new RenderContext(stack, key);

        CyRenderService serv = doc.getNodeService(CyRenderService.class, false);
        if (serv != null)
        {
            if (serv.getNumCameras() == 0)
            {
                serv.renderEditor(ctx);
            }
            else
            {
                serv.renderCamerasAll(ctx);
            }
        }
        
        return drawGroup;
    }
}
