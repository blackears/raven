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

package com.kitfox.raven.editor.node.tools.common.shape.curveEdit;

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.raven.editor.node.tools.common.pen.ServiceBezierMesh;
import com.kitfox.raven.shape.network.pick.NetworkHandleEdge;
import com.kitfox.raven.shape.network.pick.NetworkMeshHandles;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class MeshDragSetEdge extends MeshDragSet
{
    ArrayList<NetworkHandleEdge> pickEdge;

    public MeshDragSetEdge(ServiceBezierMesh servMesh,
            NetworkMeshHandles handles,
            CyMatrix4d g2d,
            ArrayList<NetworkHandleEdge> pickEdge)
    {
        super(servMesh, handles, g2d);
        this.pickEdge = pickEdge;
    }

    @Override
    public void dragBy(int dx, int dy, boolean history)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
}
