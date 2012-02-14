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

import com.kitfox.coyote.material.color.CyMaterialColorDrawRecord;
import com.kitfox.coyote.material.color.CyMaterialColorDrawRecordFactory;
import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.CyShape;
import com.kitfox.coyote.shape.ShapeMeshProvider;
import com.kitfox.coyote.shape.bezier.mesh.CutLoop;
import com.kitfox.coyote.shape.bezier.mesh.CutLoop.PathVisitor;
import com.kitfox.raven.editor.node.renderer.RavenRenderer;
import com.kitfox.raven.editor.node.tools.common.pen.ServiceBezierMesh;
import com.kitfox.raven.shape.network.NetworkMesh;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.tree.PropertyWrapper;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class RavenNodeMesh2 extends RavenNodeXformable
        implements ServiceBezierMesh
{
    public static final String PROP_MESH = "mesh";
    public final PropertyWrapper<RavenNodeMesh2, NetworkMesh> mesh =
            new PropertyWrapper(
            this, PROP_MESH, NetworkMesh.class);

    protected RavenNodeMesh2(int uid)
    {
        super(uid);

//        path.addPropertyWrapperListener(clearCache);
    }

    @Override
    protected void renderContent(RenderContext ctx)
    {
        FaceSet faceSet = getFaceSet();
        
        CyDrawStack stack = ctx.getDrawStack();
        CyMatrix4d mvp = stack.getModelViewProjXform();
        
        for (FaceLayout lay: faceSet.paths)
        {
            CyMaterialColorDrawRecord rec =
                    CyMaterialColorDrawRecordFactory.inst().allocRecord();

            rec.setColor(lay.color);
            rec.setMesh(lay.vertBuf);
            rec.setOpacity(1);
            rec.setMvpMatrix(mvp);
            
            stack.addDrawRecord(rec);
        }
    }

    protected FaceSet getFaceSet()
    {
        FaceSet faceSet = mesh.getUserCacheValue(FaceSet.class);
        if (faceSet == null)
        {
            NetworkMesh curMesh = mesh.getValue();
            faceSet = new FaceSet(curMesh);
            mesh.setUserCacheValue(FaceSet.class, faceSet);
        }
        return faceSet;
    }
    
    @Override
    public CyShape getShapePickLocal()
    {
        FaceSet faceSet = getFaceSet();
        return faceSet.combinedPath;
        //return null;
        
//        NetworkMesh curMesh = mesh.getValue();
//        //CutGraph.createFaces(null)
//        CutLoop faces = curMesh.createFaces();
        
        //Outer loop should be bounds
        
    }

    @Override
    protected void renderContent(RavenRenderer renderer)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NetworkMesh getNetworkMesh()
    {
        return new NetworkMesh(mesh.getValue());
    }
    
    @Override
    public void setNetworkMesh(NetworkMesh mesh, boolean history)
    {
        this.mesh.setValue(mesh, history);
    }

    
    //----------------------------------------
    protected class FaceSet implements PathVisitor
    {
        ArrayList<FaceLayout> paths = new ArrayList<FaceLayout>();
        CyPath2d combinedPath = new CyPath2d();

        public FaceSet(NetworkMesh mesh)
        {
            CutLoop faces = mesh.createFaces();

            if (faces != null)
            {
                for (int i = 0; i < faces.getNumChildren(); ++i)
                {
                    faces.getChild(i).buildFaces(this);
                }
            }
        }

        @Override
        public void emitFace(CutLoop parent, CyPath2d path)
        {
            paths.add(new FaceLayout(path, CyColor4f.randomRGB()));
            combinedPath.append(path);
        }
    }
    
    class FaceLayout
    {
        CyPath2d path;
        CyColor4f color;
        CyVertexBuffer vertBuf;

        public FaceLayout(CyPath2d path, CyColor4f color)
        {
            this.path = path;
            this.color = color;
            ShapeMeshProvider prov = new ShapeMeshProvider(path);
            vertBuf = new CyVertexBuffer(prov);
        }        
    }
    
    @ServiceInst(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<RavenNodeMesh2>
    {
        public Provider()
        {
            super(RavenNodeMesh2.class, "Mesh2", "/icons/node/mesh.png");
        }

        @Override
        public RavenNodeMesh2 createNode(int uid)
        {
            return new RavenNodeMesh2(uid);
        }
    }
    
}
