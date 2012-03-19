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

package com.kitfox.raven.paint.common;

import com.kitfox.cache.CacheElement;
import com.kitfox.cache.CacheMap;
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import com.kitfox.coyote.material.textureBlit.CyMaterialTextureBlitDrawRecord;
import com.kitfox.coyote.material.textureBlit.CyMaterialTextureBlitDrawRecordFactory;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyGLWrapper;
import com.kitfox.coyote.renderer.CyTextureImage;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.coyote.renderer.jogl.TexSourceAWT;
import com.kitfox.coyote.renderer.jogl.TexSourceAWTBufferedImage;
import com.kitfox.raven.paint.RavenPaint;
import com.kitfox.raven.paint.control.RavenPaintControl;
import com.kitfox.raven.paint.RavenPaintLayout;
import com.kitfox.raven.paint.RavenPaintProvider;
import com.kitfox.raven.paint.control.TextureEditorPanel;
import com.kitfox.raven.util.resource.ResourceCache;
import com.kitfox.raven.util.service.ServiceInst;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.beans.PropertyEditor;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class RavenPaintTexture implements RavenPaint
{
    public static final String CACHE_NAME = "tex";
    public static final String PROP_URI = "uri";
    
    private final URI texture;
    CyTextureImage texSrc;

    public RavenPaintTexture()
    {
        this(null);
    }
    
    public RavenPaintTexture(URI texture)
    {
        this.texture = texture;
        
        BufferedImage img = (BufferedImage)ResourceCache.inst().getResource(texture);
        
        TexSourceAWT src = new TexSourceAWTBufferedImage(img);
        
        texSrc = new CyTextureImage(
                CyGLWrapper.TexTarget.GL_TEXTURE_2D, 
                CyGLWrapper.InternalFormatTex.GL_RGBA, 
                CyGLWrapper.DataType.GL_UNSIGNED_BYTE, 
                src.getWidth(), src.getHeight(),
                src.getTransparency(), src);
    }

    @Override
    public Paint getPaintSwatch(Rectangle box)
    {
        Object res = ResourceCache.inst().getResource(texture);
        if (!(res instanceof BufferedImage))
        {
            return null;
        }
        
        TexturePaint paint = new TexturePaint((BufferedImage)res, box);
        return paint;
    }

    /**
     * @return the texture
     */
    public URI getTexture()
    {
        return texture;
    }

    @Override
    public void fillShape(CyDrawStack stack, 
        RavenPaintLayout layout, CyVertexBuffer mesh, CyMatrix4d meshToLocal)
    {
        CyMaterialTextureBlitDrawRecord rec =
                CyMaterialTextureBlitDrawRecordFactory.inst().allocRecord();
        
        rec.setMagFilter(CyGLWrapper.TexParam.GL_NEAREST);
        rec.setMinFilter(CyGLWrapper.TexParam.GL_NEAREST_MIPMAP_NEAREST);
        rec.setWrapS(CyGLWrapper.TexParam.GL_REPEAT);
        rec.setWrapT(CyGLWrapper.TexParam.GL_REPEAT);
        rec.setTexture(texSrc);
        rec.setMesh(mesh);
        rec.setOpacity(1);
//        rec.setLocalToTexMatrix(layout.getPaintToLocal());
        CyMatrix4d mvp = stack.getModelViewProjXform();
        CyMatrix4d l2p = layout.getLocalToPaint();
        if (meshToLocal != null)
        {
            mvp.mul(meshToLocal);
            l2p.mul(meshToLocal);
        }
        rec.setMvpMatrix(mvp);
        rec.setLocalToTexMatrix(l2p);
        
        stack.addDrawRecord(rec);
    }

    public static RavenPaintTexture create(String text)
    {
        try
        {
            CacheElement ele = CacheParser.parse(text);
            if (!(ele instanceof CacheMap))
            {
                return null;
            }
            CacheMap map = (CacheMap)ele;
            return create(map);
        } catch (ParseException ex)
        {
            Logger.getLogger(RavenPaintGradient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static RavenPaintTexture create(CacheMap map)
    {
        if (!CACHE_NAME.equals(map.getName()))
        {
            return null;
        }
        
        String text = map.getString(PROP_URI, "");
        try
        {
            URI uri = new URI(text);
            return new RavenPaintTexture(uri);
        } catch (URISyntaxException ex)
        {
            Logger.getLogger(RavenPaintTexture.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public CacheMap toCache()
    {
        CacheMap map = new CacheMap(CACHE_NAME);
        
        map.put(PROP_URI, texture.toASCIIString());
        
        return map;
    }

    @Override
    public String toString()
    {
        return toCache().toString();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final RavenPaintTexture other = (RavenPaintTexture) obj;
        if (this.texture != other.texture && (this.texture == null || !this.texture.equals(other.texture)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 83 * hash + (this.texture != null ? this.texture.hashCode() : 0);
        return hash;
    }
    
    //--------------------------------------
    @ServiceInst(service=RavenPaintProvider.class)
    public static class Provider extends RavenPaintProvider<RavenPaintTexture>
    {
        public Provider()
        {
            super("Texture", RavenPaintTexture.class);
        }

        @Override
        public boolean canParse(String text)
        {
            return text.startsWith(CACHE_NAME);
        }

        @Override
        public RavenPaintTexture fromText(String text)
        {
            return create(text);
        }

        @Override
        public String asText(RavenPaintTexture value)
        {
            return value.toString();
        }

        @Override
        public RavenPaintTexture getDefaultValue()
        {
            return new RavenPaintTexture();
        }

        @Override
        public RavenPaintControl createEditor()
        {
            return new TextureEditorPanel();
        }
    }
}
