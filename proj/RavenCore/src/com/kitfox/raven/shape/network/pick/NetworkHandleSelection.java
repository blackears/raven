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

package com.kitfox.raven.shape.network.pick;

import com.kitfox.raven.util.Selection;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class NetworkHandleSelection
{
    Selection<NetworkHandleEdge> selEdge = new Selection<NetworkHandleEdge>();
    Selection<NetworkHandleFace> selFace = new Selection<NetworkHandleFace>();
    Selection<NetworkHandleVertex> selVert = new Selection<NetworkHandleVertex>();

    public boolean containsVertex(NetworkHandleVertex curVertex)
    {
        return selVert.isSelected(curVertex);
    }
    
    public ArrayList<NetworkHandleVertex> getVertices()
    {
        return selVert.getSelection();
    }

    public boolean containsEdge(NetworkHandleEdge curEdge)
    {
        return selEdge.isSelected(curEdge);
    }
    
    public ArrayList<NetworkHandleEdge> getEdges()
    {
        return selEdge.getSelection();
    }

    public boolean containsFace(NetworkHandleFace curEdge)
    {
        return selFace.isSelected(curEdge);
    }
    
    public ArrayList<NetworkHandleFace> getFaces()
    {
        return selFace.getSelection();
    }
}
