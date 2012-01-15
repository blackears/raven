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

package com.kitfox.coyote.test.bezier;

import com.kitfox.coyote.shape.bezier.BezierCubic2i;
import com.kitfox.coyote.shape.bezier.BezierLine2i;
import com.kitfox.coyote.shape.bezier.mesh2.BezierMesh2i;
import com.kitfox.coyote.shape.bezier.mesh2.CutLoop;

/**
 *
 * @author kitfox
 */
public class BezierMeshTestMain
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        BezierMesh2i mesh = new BezierMesh2i(2);
        
        mesh.addEdge(new BezierLine2i(0, 0, 100, 0), null);
        mesh.addEdge(new BezierLine2i(100, 0, 100, 100), null);
        mesh.addEdge(new BezierLine2i(100, 100, 0, 100), null);
        mesh.addEdge(new BezierLine2i(0, 100, 0, 0), null);
        
        mesh.addEdge(new BezierCubic2i(0, 50, 25, 25, 75, 25, 100, 50), null);
        mesh.addEdge(new BezierLine2i(50, 100, 50, 0), null);

        mesh.addEdge(new BezierLine2i(50, 100, 50, 200), null);
        mesh.addEdge(new BezierLine2i(50, 200, 50, 300), null);
        
        CutLoop faceTree = mesh.createFaces();
        faceTree.contains(15, 15);
    }
}
