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

package com.kitfox.coyote.renderer.shader;

import com.kitfox.coyote.math.BufferUtil;
import com.kitfox.coyote.math.d3.BoundingBox3d;
import com.kitfox.coyote.renderer.CyGLWrapper.BufferUsage;
import com.kitfox.coyote.renderer.CyGLWrapper.DrawMode;
import com.kitfox.coyote.renderer.CyMaterial;
import com.kitfox.coyote.renderer.CyVertexArrayInfo;
import com.kitfox.coyote.renderer.CyVertexBufferDataProvider;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A vert simple Wavefront Obj loader.  Assumes mesh is made of triangles
 * and has texture and normal coords.
 *
 * @author kitfox
 */
public class ObjMeshProvider extends CyVertexBufferDataProvider
{
    //ObjLoaderData data;
    FloatBuffer vertBuf;
    ShortBuffer idxBuf;
    int numIdx;
    BoundingBox3d bounds;
    
    public ObjMeshProvider(URL source)
    {
        try
        {
            InputStreamReader reader =
                    new InputStreamReader(source.openStream());
            ObjLoaderData data = new ObjLoaderData(reader);
            
            vertBuf = data.createVertexData();
            idxBuf = data.createIndexData();
            numIdx = data.getIndexCount();
            bounds = data.getBounds();
            
            int vCount = data.getVertexCount();
            
            setVertexArrayInfo(CyMaterial.KEY_POSITION, 
                    vCount * 0 * BufferUtil.SIZEOF_FLOAT, 3);
            setVertexArrayInfo(CyMaterial.KEY_TEXCOORD0, 
                    vCount * 3 * BufferUtil.SIZEOF_FLOAT, 2);
            setVertexArrayInfo(CyMaterial.KEY_NORMAL, 
                    vCount * 5 * BufferUtil.SIZEOF_FLOAT, 3);
            
        } catch (IOException ex)
        {
            Logger.getLogger(ObjMeshProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public FloatBuffer getVertexData()
    {
        return vertBuf;
    }

    @Override
    public ShortBuffer getIndexData()
    {
        return idxBuf;
    }

    @Override
    public BufferUsage getVertexUsage()
    {
        return BufferUsage.GL_STATIC_DRAW;
    }

    @Override
    public BufferUsage getIndexUsage()
    {
        return BufferUsage.GL_STATIC_DRAW;
    }

    @Override
    public DrawMode getDrawMode()
    {
        return DrawMode.GL_TRIANGLES;
    }

    @Override
    public int getIndexCount()
    {
        return numIdx;
    }

    @Override
    public BoundingBox3d getBounds()
    {
        return bounds;
    }
}
