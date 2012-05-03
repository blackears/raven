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

package com.kitfox.raven.swf.importer;

import com.kitfox.coyote.shape.bezier.BezierLine2i;
import com.kitfox.coyote.shape.bezier.BezierQuad2i;
import com.kitfox.raven.shape.network.NetworkMesh;

/**
 *
 * @author kitfox
 */
public class MeshBuilder extends MeshBuilderBase
{
    NetworkMesh mesh = new NetworkMesh();

    @Override
    public void moveTo(int x, int y)
    {
        //Convert TWIPS to centipixels
        x = x * 5;
        y = y * 5;

        px = x;
        py = y;
    }

    @Override
    public void lineTo(int x, int y)
    {
        //Convert TWIPS to centipixels
        x = px + x * 5;
        y = py + y * 5;

        mesh.addEdge(new BezierLine2i(px, py, x, y), 
                createEdgeData());

        px = x;
        py = y;
    }

    @Override
    public void quadTo(int kx, int ky, int x, int y)
    {
        //Convert TWIPS to centipixels
        kx = px + kx * 5;
        ky = py + ky * 5;
        x = px + x * 5;
        y = py + y * 5;

        mesh.addEdge(new BezierQuad2i(px, py, kx, ky, x, y), 
                createEdgeData());
        
        px = x;
        py = y;
    }

    @Override
    public void finishedVisitingShape()
    {
    }

}
