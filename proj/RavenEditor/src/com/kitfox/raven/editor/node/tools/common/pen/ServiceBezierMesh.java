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

package com.kitfox.raven.editor.node.tools.common.pen;

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.raven.editor.node.tools.ToolService;
import com.kitfox.raven.shape.network.NetworkMesh;

/**
 *
 * @author kitfox
 */
public interface ServiceBezierMesh extends ToolService
{
    public NetworkMesh getNetworkMesh();
    public void setNetworkMesh(NetworkMesh mesh, boolean history);
    public CyMatrix4d getLocalToWorldTransform(CyMatrix4d xform);

    public CyMatrix4d getGraphToWorldXform();
}
