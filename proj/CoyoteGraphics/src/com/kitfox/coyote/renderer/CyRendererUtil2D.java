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

package com.kitfox.coyote.renderer;

import com.kitfox.coyote.drawRecord.CyDrawRecordClear;
import com.kitfox.coyote.material.color.CyMaterialColorDrawRecord;
import com.kitfox.coyote.material.color.CyMaterialColorDrawRecordFactory;
import com.kitfox.coyote.material.textureBlit.CyMaterialTextureBlit;
import com.kitfox.coyote.material.textureBlit.CyMaterialTextureBlitDrawRecord;
import com.kitfox.coyote.material.textureBlit.CyMaterialTextureBlitDrawRecordFactory;
import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.renderer.vertex.CyVertexBufferDataSquare;

/**
 *
 * @author kitfox
 */
public class CyRendererUtil2D
{
//    CyMaterialTextureBlit texMat;
//    CyMaterialColor colorMat;

    private static CyRendererUtil2D instance = new CyRendererUtil2D();

    CyMatrix4d m = CyMatrix4d.createIdentity();

    private CyRendererUtil2D()
    {
//        texMat = new CyMaterialTextureBlit();
//        colorMat = new CyMaterialColor();
    }

    public static CyRendererUtil2D inst()
    {
        return instance;
    }

    public static void clear(CyDrawStack rend, float r, float g, float b, float a)
    {
        rend.addDrawRecord(new CyDrawRecordClear(r, g, b, a,
                true, true, true));
    }

    static CyMatrix4d modelCache = new CyMatrix4d();
    static CyMatrix4d mvpXform = new CyMatrix4d();
    static CyMatrix4d tmpMtx = new CyMatrix4d();

    /**
     * This routine draws images in much the same way that a regular
     * graphics API does.  You give it an image, a source region in image
     * pixels and a destination region, and it will draw the subregion
     * there.
     *
     * This is intended for use with OpenGL layouts that mimic a traditional
     * 2D API.  While the input texture should be initialized in the proper
     * OpenGL way (that is, the bottom left corner of the texture coresponds
     * to the ST coord (0, 0)), the source (sx, sy, sw, sh) is interpreted
     * as if the image represented a typical raster image with the top left
     * corner being (0, 0) and rows increasing in value as you move to the
     * bottom of the texture.  Also, coordinates are inperpreted relative
     * to the width and height of the image, and not the OpenGL unit square.
     *
     * The destination rectangle is simply transformed by the CoyoteRenderer
     * modevViewProj matrix.  However, this routine is intended for use in
     * coordinate system that mimic a 2D screen layout, so the projection
     * matrix should probably map (0, 0) to the top left cirner of the screen
     * and have Y increase as you go down the screen.
     *
     * @param rend
     * @param img
     * @param sx Source region x coord
     * @param sy Source region y coord
     * @param sw Source region width
     * @param sh Source region height
     * @param dx
     * @param dy
     * @param dw
     * @param dh
     */
    public static void drawImage(CyDrawStack rend, CyTextureSource img,
            double sx, double sy, double sw, double sh,
            double dx, double dy, double dw, double dh)
    {
        CyVertexBuffer mesh = CyVertexBufferDataSquare.inst().getBuffer();

        //rend.getViewportWidth();
        rend.getModelXform(modelCache);
        rend.translate(dx, dy, 0);
        rend.scale(dw, dh, 1);

        if (!rend.intersectsFrustum(mesh.getBounds()))
        {
            rend.setModelXform(modelCache);
            return;
        }

        mvpXform = rend.getModelViewProjXform(mvpXform);
//        xform.translate(dx, dy, 0);
//        xform.scale(dw, dh, 1);

        CyMaterialTextureBlitDrawRecord rec =
                CyMaterialTextureBlitDrawRecordFactory.inst().allocRecord();
//        CyMaterialTextureBlit.DrawRecord rec =
//                CyMaterialTextureBlit.inst().createRecord();
        

        rec.setMvpMatrix(mvpXform);
        rec.setOpacity(rend.getOpacity());
        rec.setMesh(mesh);

        int width = img.getWidth();
        int height = img.getHeight();

        {
            double rx = sx / width;
            double ry = sy / height;
            double rw = sw / width;
            double rh = sh / height;

            //Flip y axis
            ry = 1 - ry;
            rh = -rh;

//            CyMatrix4d tmpMtx = CyMatrix4d.createIdentity();
            tmpMtx.setIdentity();
            tmpMtx.translate(rx, ry, 0);
            tmpMtx.scale(rw, rh, 1);
            rec.setTexToLocalMatrix(tmpMtx);
        }

//        {
//        m.setIdentity();
////        m.scale(sw / width, sh / height, 1);
//        m.translate(sx / width, 1 - (sy + sh) / height, 0);
//        m.scale(sw / width, sh / height, 1);
////        m.translate(.5, .5, 0);
//        }


        rec.setTexture(img);

        rend.addDrawRecord(rec);
//        texMat.bind(rend.getGl());
//        texMat.draw(rend.getGl(),
//                CyVertexBufferDataSquare.inst().getBuffer());

        rend.setModelXform(modelCache);
    }

    public static void drawTileImage(GLContext ctx, GLWrapper gl, CyFramebufferTexture img, CyMatrix4d tileXform)
    {
        CyVertexBuffer mesh = CyVertexBufferDataSquare.inst().getBuffer();

//        mvpXform.setIdentity();
//        mvpXform.scale(tileWidth, tileHeight, 1);

        CyMaterialTextureBlitDrawRecord rec =
                CyMaterialTextureBlitDrawRecordFactory.inst().allocRecord();
//        CyMaterialTextureBlit.DrawRecord rec =
//                CyMaterialTextureBlit.inst().createRecord();

        rec.setMvpMatrix(tileXform);
        rec.setOpacity(1);
        rec.setMesh(mesh);

        tmpMtx.setIdentity();
        rec.setTexToLocalMatrix(tmpMtx);
        rec.setTexture(img);

        rec.render(ctx, gl, null);
    }

    public static void fillShape(CyDrawStack renderer, CyColor4f color,
            CyVertexBuffer mesh)
    {
        //CyMaterialColor col;
        //CyMaterialColorRecord rec = new
//        CyDrawRecord rec = CyMaterialColor.inst().createRecord(renderer, color, mesh);
        if (!renderer.intersectsFrustum(mesh.getBounds()))
        {
            return;
        }

        CyMatrix4d xform = renderer.getModelViewProjXform();

        CyMaterialColorDrawRecord rec =
                CyMaterialColorDrawRecordFactory.inst().allocRecord();
//        CyMaterialColor.DrawRecord rec = CyMaterialColor.inst().createRecord();
        rec.setMvpMatrix(xform);
        rec.setOpacity(renderer.getOpacity());
        rec.setColor(color);
        rec.setMesh(mesh);

        renderer.addDrawRecord(rec);

        

//        CyMatrix4d xform = renderer.getModelViewProjXform();
//        colorMat.setMvpMatrix(xform);
//
//        colorMat.setColor(cyColor4f);
//
//        colorMat.bind(renderer.getGl());
//        colorMat.draw(renderer.getGl(), mesh);
    }
}
