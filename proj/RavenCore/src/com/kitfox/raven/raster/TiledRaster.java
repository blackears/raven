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

package com.kitfox.raven.raster;

import com.kitfox.coyote.material.textureBlit.CyMaterialTextureBlitDrawRecord;
import com.kitfox.coyote.material.textureBlit.CyMaterialTextureBlitDrawRecordFactory;
import com.kitfox.coyote.math.BufferUtil;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyFramebuffer;
import com.kitfox.coyote.renderer.CyFramebufferTexture;
import com.kitfox.coyote.renderer.CyGLContext;
import com.kitfox.coyote.renderer.CyGLWrapper;
import com.kitfox.coyote.renderer.CyTextureImage;
import com.kitfox.coyote.renderer.CyTextureSource;
import com.kitfox.coyote.renderer.CyTransparency;
import com.kitfox.coyote.renderer.vertex.CyVertexBufferDataSquare;
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.raven.util.Grid;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * A tiled image that other images can be blitted to.
 *
 * @author kitfox
 */
public class TiledRaster
{
    Grid<CyFramebufferTexture> tileGrid = new Grid<CyFramebufferTexture>();
    final int tileWidth;
    final int tileHeight;
    
    CyFramebuffer frameBuf;
    CyMatrix4d proj = new CyMatrix4d();
    static final CyMatrix4d local2Tex = CyMatrix4d.createIdentity();

    //Framebuffer will be copied here before any compositing ops 
    // to allow for destination sensitive compositing
    CyTextureImage destBuffer;
    
    public TiledRaster(int tileWidth, int tileHeight)
    {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.frameBuf = new CyFramebuffer(
                tileWidth, tileHeight);

        destBuffer = new CyTextureImage(
                CyGLWrapper.TexTarget.GL_TEXTURE_2D, 
                CyGLWrapper.InternalFormatTex.GL_RGBA,
                CyGLWrapper.DataType.GL_UNSIGNED_BYTE, 
                tileWidth, tileHeight, 
                CyTransparency.TRANSLUCENT, null);
        
        //Tile projection matrix
        proj.setIdentity();
        proj.translate(-1, -1, 0);
        proj.scale(2.0 / tileWidth, 2.0 / tileHeight, 1);

    }
    
    /**
     * Draws an image over top of this one.
     * 
     * @param ctx
     * @param gl
     * @param source Image to draw
     * @param xform Transformation to apply to the image.  This maps
     * the image from the [0 1] square to the raster's pixel space.
     * 
     */
    public void drawImage(CyGLContext ctx, CyGLWrapper gl,
            CyTextureSource source, CyMatrix4d xform)
    {
//        int width = source.getWidth();
//        int height = source.getHeight();
        
        CyRectangle2d bounds = new CyRectangle2d(0, 0, 1, 1);
        CyRectangle2d newBounds = bounds.createTransformedBounds(xform);
        
        int tx0 = (int)Math.floor(newBounds.getMinX() / tileWidth);
        int ty0 = (int)Math.floor(newBounds.getMinY() / tileHeight);
        int tx1 = (int)Math.floor(newBounds.getMaxX() / tileWidth);
        int ty1 = (int)Math.floor(newBounds.getMaxY() / tileHeight);

        tileGrid.includeRegion(tx0, ty0, tx1 - tx0 + 1, ty1 - ty0 + 1, null);
        
        //Prepare draw record
        CyMaterialTextureBlitDrawRecord rec = 
                CyMaterialTextureBlitDrawRecordFactory.inst().allocRecord();
                
        rec.setMesh(CyVertexBufferDataSquare.inst().getBuffer());
        rec.setLocalToTexMatrix(local2Tex);
        rec.setTexture(source);
        rec.setMagFilter(CyGLWrapper.TexParam.GL_LINEAR);
        rec.setMinFilter(CyGLWrapper.TexParam.GL_LINEAR_MIPMAP_LINEAR);
        rec.setOpacity(1);
        rec.setWrapS(CyGLWrapper.TexParam.GL_CLAMP_TO_EDGE);
        rec.setWrapT(CyGLWrapper.TexParam.GL_CLAMP_TO_EDGE);

FloatBuffer fbuf = BufferUtil.allocateFloat(1);
gl.glGetFloatv(CyGLWrapper.GetParam.GL_FRAMEBUFFER_BINDING, fbuf);
int cacheBuf = (int)fbuf.get(0);
        //Render across all tiles
        CyMatrix4d mvp = new CyMatrix4d();
        for (int ty = ty0; ty <= ty1; ++ty)
        {
            for (int tx = tx0; tx <= tx1; ++tx)
            {
                CyFramebufferTexture tile = tileGrid.getValue(tx, ty);
                boolean clearTile = false;
                if (tile == null)
                {
                    tile = new CyFramebufferTexture(
                            CyGLWrapper.Attachment.GL_COLOR_ATTACHMENT0,
                            CyGLWrapper.TexTarget.GL_TEXTURE_2D, 
                            CyGLWrapper.InternalFormatTex.GL_RGBA,
                            CyGLWrapper.DataType.GL_UNSIGNED_BYTE, 
                            tileWidth, tileHeight);
                    tileGrid.setValue(tx, ty, tile);
                    clearTile = true;
                }
                frameBuf.setAttachments(ctx, gl, tile);
                frameBuf.bind(ctx, gl);
                
                if (clearTile)
                {
                    gl.glClearColor(0, 0, 0, 0);
                    gl.glClear(true, true, true);
                }
                
                //Copy framebuffer into 'dst' texture
                destBuffer.bindTexture(ctx, gl);
                gl.glCopyTexSubImage2D(
                        CyGLWrapper.TexSubTarget.GL_TEXTURE_2D, 
                        0, 
                        0, 0, 
                        0, 0, tileWidth, tileHeight);
                
                //Draw image
                mvp.set(proj);
                mvp.translate(tx * -tileWidth, ty * -tileHeight, 0);
                mvp.mul(xform);
                
                rec.setMvpMatrix(mvp);
                
                rec.render(ctx, gl, null);
            }
        }
gl.glBindFramebuffer(cacheBuf);
//gl.glBindFramebuffer(0);
        
    }
    
    public void render(CyDrawStack stack)
    {
        CyMatrix4d mvp = stack.getModelViewProjXform();
        
        int tx0 = tileGrid.getOffsetX();
        int ty0 = tileGrid.getOffsetY();
        int tx1 = tx0 + tileGrid.getWidth();
        int ty1 = ty0 + tileGrid.getHeight();
        
        CyMatrix4d xform = stack.getModelViewProjXform();
        for (int ty = ty0; ty <= ty1; ++ty)
        {
            for (int tx = tx0; tx <= tx1; ++tx)
            {
                CyFramebufferTexture tile = tileGrid.getValue(tx, ty);
                if (tile == null)
                {
                    continue;
                }
                
                //Prepare draw record
                CyMaterialTextureBlitDrawRecord rec = 
                        CyMaterialTextureBlitDrawRecordFactory.inst().allocRecord();

                rec.setMesh(CyVertexBufferDataSquare.inst().getBuffer());
                rec.setLocalToTexMatrix(local2Tex);
                rec.setTexture(tile);
                rec.setMagFilter(CyGLWrapper.TexParam.GL_LINEAR);
                rec.setMinFilter(CyGLWrapper.TexParam.GL_LINEAR_MIPMAP_LINEAR);
                rec.setOpacity(1);
                rec.setWrapS(CyGLWrapper.TexParam.GL_CLAMP_TO_EDGE);
                rec.setWrapT(CyGLWrapper.TexParam.GL_CLAMP_TO_EDGE);

                xform.set(mvp);
                xform.translate(tx * tileWidth, ty * tileHeight, 0);
                xform.scale(tileWidth, tileHeight, 1);
                rec.setMvpMatrix(xform);
                
                stack.addDrawRecord(rec);
            }
        }
        
    }

    public TiledRasterData getData(CyGLContext ctx, CyGLWrapper gl)
    {
        Grid<byte[]> newGrid = new Grid<byte[]>();
        
        ByteBuffer pixelData = BufferUtil.allocateByte(tileWidth * tileHeight * 4);
        
FloatBuffer fbuf = BufferUtil.allocateFloat(1);
gl.glGetFloatv(CyGLWrapper.GetParam.GL_FRAMEBUFFER_BINDING, fbuf);
int cacheBuf = (int)fbuf.get(0);
        
        for (int j = 0; j < tileGrid.getHeight(); ++j)
        {
            for (int i = 0; i < tileGrid.getWidth(); ++i)
            {
                CyFramebufferTexture tile = tileGrid.getValue(
                        i + tileGrid.getOffsetX(), 
                        j + tileGrid.getOffsetY());
                
                if (tile == null)
                {
                    continue;
                }
                
                frameBuf.setAttachments(ctx, gl, tile);
                frameBuf.bind(ctx, gl);
                
//gl.glClearColor(.25f, .5f, .75f, 1);
//gl.glClear(true, true, true);
                
                pixelData.rewind();
                gl.glReadPixels(
                        0, 0, tileWidth, tileHeight,
                        CyGLWrapper.ReadPixelsFormat.GL_RGBA, 
                        CyGLWrapper.DataType.GL_UNSIGNED_BYTE, pixelData);
                
                byte[] buf = new byte[tileWidth * tileHeight * 4];
                pixelData.rewind();
                pixelData.get(buf);
                
                newGrid.includeRegion(i, j, 1, 1, null);
                newGrid.setValue(i, j, buf);
            }
        }

gl.glBindFramebuffer(cacheBuf);

        return new TiledRasterData(newGrid, tileWidth, tileHeight);
    }

    public void clear()
    {
        tileGrid.clear();
    }
}
