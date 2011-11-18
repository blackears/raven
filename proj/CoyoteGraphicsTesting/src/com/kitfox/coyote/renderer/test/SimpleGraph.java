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

package com.kitfox.coyote.renderer.test;

import com.kitfox.coyote.renderer.jogl.TexSourceURLAWT;
import com.kitfox.coyote.material.textureBlit.CyMaterialTextureBlit;
import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyRendererListener;
import com.kitfox.coyote.renderer.CyRendererUtil2D;
import com.kitfox.coyote.renderer.CyTextureImage;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.coyote.renderer.GLWrapper.DataType;
import com.kitfox.coyote.renderer.GLWrapper.InternalFormatTex;
import com.kitfox.coyote.renderer.GLWrapper.TexTarget;
import com.kitfox.coyote.renderer.jogl.GLWrapperJOGL;
import com.kitfox.coyote.shape.CyEllipse2d;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.ShapeMeshProvider;
import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import javax.media.opengl.GL;

/**
 *
 * @author kitfox
 */
public class SimpleGraph implements CyRendererListener
{
    TexSourceURLAWT texture;
    CyMaterialTextureBlit mat;
//    CyMaterialColor mat;
//    CyMaterialShowUv mat;

    CyTextureImage img;
    CyVertexBuffer shapeVertBuffer;


    public SimpleGraph()
    {
        texture = new TexSourceURLAWT(getClass().getResource("/forestBase.png"));

        {
//        mat = new CyMaterialShowUv();
//        mat.setColor(CyColor4f.RED);
        }

        {
            mat = new CyMaterialTextureBlit();
            img = new CyTextureImage(TexTarget.GL_TEXTURE_2D,
                    InternalFormatTex.GL_RGBA, DataType.GL_UNSIGNED_BYTE,
                    texture.getWidth(), texture.getHeight(), texture);
            mat.setTexture(img);
        }
    }

    @Override
    public void render(CyDrawStack rend)
    {
//        rend.clear(CyColor4f.CYAN);
        rend.clear(new CyColor4f(.2, .3, .3, 1));

        {
            GL gl = ((GLWrapperJOGL)rend.getGl()).getGl();
            gl.glUseProgram(0);
            gl.glColor4d(1, 1, 0, 1);
            gl.glBegin(GL.GL_TRIANGLES);
                gl.glVertex2d(-1, -1);
                gl.glVertex2d(-.8, -1);
                gl.glVertex2d(-.8, -.8);
            gl.glEnd();
        }

        CyMatrix4d proj = new CyMatrix4d();
//        proj.gluOrtho2D(0, rend.getViewportWidth(), 0, rend.getViewportHeight());
        proj.gluOrtho2D(0, rend.getDeviceWidth(), rend.getDeviceHeight(), 0);
        rend.setProjXform(proj);

//        {
//            rend.translate(-1, -1, 0);
//            CyMatrix4d proj = rend.getModelViewProjTileXform();
//
//            mat.setMvpMatrix(proj);
//            mat.bind(rend.getGl());
//            mat.draw(rend.getGl(), CyVertexBufferDataSquare.inst().getBuffer());
//        }

//        renderImage(rend);
//        renderShape(rend);
//        renderShapeHole(rend);
        renderGlyph(rend);
    }

    private void renderShape(CyDrawStack rend)
    {
        if (shapeVertBuffer == null)
        {
//            CyRectangle2d rect = new CyRectangle2d(0, 0, 100, 100);
            CyEllipse2d rect = new CyEllipse2d(0, 0, 100, 100);

            ShapeMeshProvider prov = new ShapeMeshProvider(rect);
            shapeVertBuffer = new CyVertexBuffer(prov);
        }

        CyRendererUtil2D.inst().fillShape(rend, CyColor4f.RED, shapeVertBuffer);
    }

    private void renderGlyph(CyDrawStack rend)
    {
        if (shapeVertBuffer == null)
        {
            Font font = new Font(Font.SERIF, Font.PLAIN, 100);
            FontRenderContext ctx = new FontRenderContext(new AffineTransform(), true, true);
            GlyphVector vec = font.createGlyphVector(ctx, "Beware:");

            Shape shape = vec.getOutline();
            Rectangle2D bounds = shape.getBounds2D();
            AffineTransform xform = new AffineTransform();
            xform.translate(-bounds.getX(), -bounds.getY());
            Shape path = xform.createTransformedShape(shape);


            ShapeMeshProvider prov = new ShapeMeshProvider(CyPath2d.create(path));
            shapeVertBuffer = new CyVertexBuffer(prov);
        }

        CyRendererUtil2D.inst().fillShape(rend, CyColor4f.RED, shapeVertBuffer);
    }

    private void renderShapeHole(CyDrawStack rend)
    {
        if (shapeVertBuffer == null)
        {
            CyPath2d path = new CyPath2d();
            path.moveTo(0, 0);
            path.lineTo(100, 0);
            path.lineTo(100, 100);
            path.lineTo(0, 100);
            path.close();

            path.moveTo(20, 20);
            path.lineTo(20, 40);
            path.lineTo(40, 40);
            path.lineTo(40, 20);
            path.close();

            path.moveTo(60, 20);
            path.lineTo(60, 40);
            path.lineTo(80, 40);
            path.lineTo(80, 20);
            path.close();

            path.moveTo(160, 20);
            path.lineTo(160, 40);
            path.lineTo(180, 40);
            path.lineTo(180, 20);
            path.close();

            ShapeMeshProvider prov = new ShapeMeshProvider(path);
            shapeVertBuffer = new CyVertexBuffer(prov);
        }

        CyRendererUtil2D.inst().fillShape(rend, CyColor4f.RED, shapeVertBuffer);
    }

    private void renderImage(CyDrawStack rend)
    {
        CyRendererUtil2D.inst().drawImage(rend, img,
                16, 16, 16, 16,
                0, 0, 32, 32);
        CyRendererUtil2D.inst().drawImage(rend, img,
                32, 16, 16, 16,
                32, 0, 32, 32);
        CyRendererUtil2D.inst().drawImage(rend, img,
                48, 16, 16, 16,
                64, 0, 32, 32);
        CyRendererUtil2D.inst().drawImage(rend, img,
                0, 32, 16, 16,
                0, 32, 32, 32);
    }
}
