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

package com.kitfox.raven.editor.node.scene;

import com.kitfox.coyote.material.textureBlit.CyMaterialTextureBlitDrawRecord;
import com.kitfox.coyote.material.textureBlit.CyMaterialTextureBlitDrawRecordFactory;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyTextureImage;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.coyote.renderer.CyGLWrapper.DataType;
import com.kitfox.coyote.renderer.CyGLWrapper.InternalFormatTex;
import com.kitfox.coyote.renderer.CyGLWrapper.TexParam;
import com.kitfox.coyote.renderer.CyGLWrapper.TexTarget;
import com.kitfox.coyote.renderer.jogl.TexSourceAWTBufferedImage;
import com.kitfox.coyote.renderer.vertex.CyVertexBufferDataSquare;
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.raven.editor.node.renderer.RavenRenderer;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.PropertyWrapper;
import java.awt.image.BufferedImage;

/**
 *
 * @author kitfox
 */
public class RavenNodeImage extends RavenNodeXformable
{

    public static final String PROP_IMAGE = "image";
    public final PropertyWrapper<RavenNodeImage, BufferedImage> image =
            new PropertyWrapper(
            this, PROP_IMAGE, BufferedImage.class);


    CyVertexBuffer mesh;

    protected RavenNodeImage(int uid)
    {
        super(uid);
        
        mesh = new CyVertexBuffer(CyVertexBufferDataSquare.inst());
    }

    @Override
    protected void renderContent(RavenRenderer renderer)
    {
        BufferedImage img = image.getValue();
        if (img == null)
        {
            return;
        }

        renderer.drawImage(img);
    }

//    @Override
//    public Rectangle getPickShapeLocal()
//    {
//        BufferedImage img = image.getValue();
//        return img == null ? null : new Rectangle(0, 0, img.getWidth(), img.getHeight());
//    }

    @Override
    protected void renderContent(RenderContext ctx)
    {
        CyDrawStack renderer = ctx.getDrawStack();
        
        BufferedImage img = image.getValue();
        if (img == null)
        {
            return;
        }

        CyMaterialTextureBlitDrawRecord rec =
                CyMaterialTextureBlitDrawRecordFactory.inst().allocRecord();

        rec.setMagFilter(TexParam.GL_NEAREST);
        rec.setMinFilter(TexParam.GL_NEAREST);

        rec.setMesh(mesh);

        CyMatrix4d mvp = renderer.getModelViewProjXform();
        mvp.scale(img.getWidth(), img.getHeight(), 1);
        rec.setMvpMatrix(mvp);

        rec.setOpacity(renderer.getOpacity());

        rec.setLocalToTexMatrix(CyMatrix4d.createIdentity());

        TexSourceAWTBufferedImage bi = new TexSourceAWTBufferedImage(img);
        CyTextureImage texImg = new CyTextureImage(
                TexTarget.GL_TEXTURE_2D, 
                InternalFormatTex.GL_RGBA,
                DataType.GL_UNSIGNED_BYTE,
                bi.getWidth(), bi.getHeight(),
                bi.getTransparency(), bi);
        rec.setTexture(texImg);

        rec.setWrapS(TexParam.GL_CLAMP_TO_EDGE);
        rec.setWrapT(TexParam.GL_CLAMP_TO_EDGE);
        
        renderer.addDrawRecord(rec);
    }

    @Override
    public CyRectangle2d getShapePickLocal()
    {
        BufferedImage img = image.getValue();
        return img == null ? null : new CyRectangle2d(0, 0, img.getWidth(), img.getHeight());
    }


//    protected BufferedImage image;
//    public static final String PROP_IMAGE = "image";
//
//    /**
//     * Get the value of image
//     *
//     * @return the value of image
//     */
//    public BufferedImage getImage() {
//        return image;
//    }
//
//    /**
//     * Set the value of image
//     *
//     * @param image new value of image
//     */
//    public void setImage(BufferedImage image) {
//        BufferedImage oldImage = this.image;
//        this.image = image;
//        propertyChangeSupport.firePropertyChange(PROP_IMAGE, oldImage, image);
//    }


//    @Override
//    public void getPropertySheet() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
    
    //-----------------------------------------------
    
    @ServiceInst(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<RavenNodeImage>
    {
        public Provider()
        {
            super(RavenNodeImage.class, "Image", "/icons/node/image.png");
        }

        @Override
        public RavenNodeImage createNode(int uid)
        {
            return new RavenNodeImage(uid);
        }
    }
}
