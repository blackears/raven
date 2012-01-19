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

import com.kitfox.coyote.shape.CyShape;
import com.kitfox.raven.editor.node.renderer.RavenRenderer;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.editor.node.tools.common.ServiceBezierNetwork2;
import com.kitfox.raven.shape.network.NetworkMesh;
import com.kitfox.raven.util.tree.PropertyWrapper;

/**
 *
 * @author kitfox
 */
public class RavenNodeMesh2 extends RavenNodeXformable
        implements ServiceBezierNetwork2
{
    public static final String PROP_MESH = "mesh";
    public final PropertyWrapper<RavenNodeMesh, NetworkMesh> mesh =
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CyShape getShapePickLocal()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void renderContent(RavenRenderer renderer)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    //----------------------------------------
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
