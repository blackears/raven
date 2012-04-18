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

package com.kitfox.raven.editor.node.tools.common.shape.brush;

import com.kitfox.raven.raster.TiledRaster;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyGLContext;
import com.kitfox.coyote.renderer.CyGLOffscreenContext;
import com.kitfox.coyote.renderer.CyGLWrapper;
import com.kitfox.coyote.renderer.CyTextureSource;
import com.kitfox.raven.raster.TiledRasterData;

/**
 *
 * @author kitfox
 */
public class StrokeBuilder
{
    TiledRaster raster = new TiledRaster(128, 128);
    CyTextureSource brushTip;
    CyMatrix4d xform = new CyMatrix4d();

    public StrokeBuilder(CyTextureSource brushTip)
    {
        this.brushTip = brushTip;
    }

    public void render(CyDrawStack stack)
    {
        raster.render(stack);
    }

    public void daubBrush(CyGLOffscreenContext ctxOff, 
            double x, double y, double scale)
    {
        CyGLWrapper gl = ctxOff.getGL();
        CyGLContext ctx = ctxOff.getGLContext();
        
        xform.setIdentity();
        xform.translate(x, y, 0);
        xform.scale(scale, scale, 1);
//        xform.translate(-brushTip.getWidth() / 2, -brushTip.getHeight() / 2, 0);
        xform.scale(brushTip.getWidth(), brushTip.getHeight(), 1);
        xform.translate(-.5, -.5, 0);
        
        raster.drawImage(ctx, gl, brushTip, xform);
    }

    public void clear()
    {
        raster.clear();
    }

    public TiledRasterData getData(CyGLOffscreenContext ctxOff)
    {
        CyGLWrapper gl = ctxOff.getGL();
        CyGLContext ctx = ctxOff.getGLContext();
        
        return raster.getData(ctx, gl);
    }
    
}
