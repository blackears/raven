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
import com.kitfox.coyote.shape.*;
import com.kitfox.coyote.shape.bezier.mesh.BezierMeshEdge2i;
import com.kitfox.coyote.shape.bezier.mesh.BezierMeshVertex2i;
import com.kitfox.coyote.shape.bezier.mesh.CutLoop;
import com.kitfox.coyote.shape.bezier.mesh.CutLoop.PathVisitor;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import com.kitfox.raven.editor.node.renderer.RavenRenderer;
import com.kitfox.raven.editor.node.tools.common.pen.ServiceBezierMesh;
import com.kitfox.raven.shape.network.NetworkMesh;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.tree.PropertyWrapper;
import java.util.ArrayList;
import java.util.HashSet;

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
            this, PROP_MESH, NetworkMesh.class, new NetworkMesh());

    final double TESS_FLAT_SQ = 2500;
    
    protected RavenNodeMesh2(int uid)
    {
        super(uid);

//        path.addPropertyWrapperListener(clearCache);
    }

    @Override
    protected void renderContent(RenderContext ctx)
    {
        MeshLayout meshLayout = getFaceSet();
        if (meshLayout == null)
        {
            return;
        }
        
        CyDrawStack stack = ctx.getDrawStack();
        CyMatrix4d mvp = stack.getModelViewProjXform();
        mvp.scale(.01, .01, 1);
        
        for (FaceLayout lay: meshLayout.paths)
        {
            CyMaterialColorDrawRecord rec =
                    CyMaterialColorDrawRecordFactory.inst().allocRecord();

            rec.setColor(lay.color);
            rec.setMesh(lay.vertBuf);
            rec.setOpacity(1);
            rec.setMvpMatrix(mvp);
            
            stack.addDrawRecord(rec);
        }
        
        for (EdgeLayout lay: meshLayout.edgeLayouts)
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

    protected MeshLayout getFaceSet()
    {
        MeshLayout faceSet = mesh.getUserCacheValue(MeshLayout.class);
        if (faceSet == null)
        {
            NetworkMesh curMesh = mesh.getValue();
            if (curMesh == null)
            {
                return null;
            }
            faceSet = new MeshLayout(curMesh);
            mesh.setUserCacheValue(MeshLayout.class, faceSet);
        }
        return faceSet;
    }
    
    @Override
    public CyShape getShapePickLocal()
    {
        MeshLayout faceSet = getFaceSet();
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
    protected class MeshLayout implements PathVisitor
    {
        ArrayList<EdgeLayout> edgeLayouts = new ArrayList<EdgeLayout>();
        ArrayList<FaceLayout> paths = new ArrayList<FaceLayout>();
        CyPath2d combinedPath = new CyPath2d();

        public MeshLayout(NetworkMesh mesh)
        {
            buildEdges(mesh);
            
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
        
        private void buildEdges(NetworkMesh mesh)
        {
            ArrayList<BezierMeshEdge2i> edges = mesh.getEdges();
            HashSet<BezierMeshEdge2i> visited = new HashSet<BezierMeshEdge2i>();
            
            while (!edges.isEmpty())
            {
                ArrayList<BezierMeshEdge2i> edgeChain = new ArrayList<BezierMeshEdge2i>();
                
                BezierMeshEdge2i curEdge = edges.remove(edges.size() - 1);
                edgeChain.add(curEdge);
                visited.add(curEdge);
                
                //Trace back along continiously connected verts
                for (BezierMeshEdge2i leadEdge = curEdge;;)
                {
                    BezierMeshVertex2i v = curEdge.getStart();
                    if (v.getNumEdges() != 2)
                    {
                        break;
                    }
                    BezierMeshEdge2i adjEdge = v.getEdge(0);
                    if (adjEdge == leadEdge)
                    {
                        adjEdge = v.getEdge(1);
                    }
                    
                    if (visited.contains(adjEdge))
                    {
                        break;
                    }
                    
                    Object data0 = leadEdge.getData();
                    Object data1 = adjEdge.getData();
                    if (!((data0 == null && data1 == null) ||
                            (data0 != null && data0.equals(data1))))
                    {
                        //Break if data not equal
                        break;
                    }
                    
                    edgeChain.add(0, adjEdge);
                    visited.add(adjEdge);
                    edges.remove(adjEdge);
                }
                
                //Trace forward along continiously connected verts
                for (BezierMeshEdge2i leadEdge = curEdge;;)
                {
                    BezierMeshVertex2i v = curEdge.getEnd();
                    if (v.getNumEdges() != 2)
                    {
                        break;
                    }
                    BezierMeshEdge2i adjEdge = v.getEdge(0);
                    if (adjEdge == leadEdge)
                    {
                        adjEdge = v.getEdge(1);
                    }
                    
                    if (visited.contains(adjEdge))
                    {
                        break;
                    }
                    
                    Object data0 = leadEdge.getData();
                    Object data1 = adjEdge.getData();
                    if (!((data0 == null && data1 == null) ||
                            (data0 != null && data0.equals(data1))))
                    {
                        //Break if data not equal
                        break;
                    }
                    
                    edgeChain.add(adjEdge);
                    visited.add(adjEdge);
                    edges.remove(adjEdge);
                }
                
                edgeLayouts.add(new EdgeLayout(edgeChain));
            }
        }
    }
    
    class EdgeLayout
    {
        CyPath2d path;
        CyColor4f color;
        CyVertexBuffer vertBuf;
        
        EdgeLayout(ArrayList<BezierMeshEdge2i> edgeChain)
        {
            BezierMeshEdge2i edgeStart = edgeChain.get(0);
            
            //NetworkDataEdge data = (NetworkDataEdge)edgeStart.getData();

            path = new CyPath2d();
            Coord c0 = edgeStart.getStart().getCoord();
            path.moveTo(c0.x, c0.y);

            for (int i = 0; i < edgeChain.size(); ++i)
            {
                BezierMeshEdge2i e = edgeChain.get(i);
                if (e.isLine())
                {
                    Coord c1 = e.getEnd().getCoord();
                    path.lineTo(c1.x, c1.y);
                }
                else
                {
                    Coord c1 = e.getEnd().getCoord();
                    Coord k0 = e.getK0();
                    Coord k1 = e.getK1();
                    path.cubicTo(k0.x, k0.y, k1.x, k1.y, c1.x, c1.y);
                }
            }
            
//System.err.println("EdgeLayout In " + path.toString());

            CyStroke stroke = new CyStroke(100);
            CyPath2d pathStroked = stroke.outlineShape(path);
            
//System.err.println("Edge " + path.toString());
//System.err.println("Stroked " + pathStroked.toString());
//System.err.println("EdgeLayout Out " + path.toString());
            color = CyColor4f.randomRGB();
//            ShapeLinesProvider prov = new ShapeLinesProvider(path);
            ShapeMeshProvider prov = new ShapeMeshProvider(pathStroked, TESS_FLAT_SQ);
            vertBuf = new CyVertexBuffer(prov);
            
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
            ShapeMeshProvider prov = new ShapeMeshProvider(path, TESS_FLAT_SQ);
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
