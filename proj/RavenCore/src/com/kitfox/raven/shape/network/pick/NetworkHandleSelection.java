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
import java.util.Collection;

/**
 * Keeps track of the subselection of mesh parts.  The indices used here are the
 * same ones calculated by the {@link NetworkMeshHandles}.
 *
 * @author kitfox
 */
public class NetworkHandleSelection
{
    Selection<Integer> selVert;
    Selection<Integer> selEdge;
    Selection<Integer> selFace;
    Selection<Integer> selKnot;

    public NetworkHandleSelection(NetworkHandleSelection sel)
    {
        selVert = new Selection<Integer>(sel.selVert);
        selEdge = new Selection<Integer>(sel.selEdge);
        selFace = new Selection<Integer>(sel.selFace);
        selKnot = new Selection<Integer>(sel.selKnot);
    }

    public NetworkHandleSelection()
    {
        selVert = new Selection<Integer>();
        selEdge = new Selection<Integer>();
        selFace = new Selection<Integer>();
        selKnot = new Selection<Integer>();
    }

    
    public void selectVertices(Collection<Integer> idList, Selection.Operator op)
    {
        selVert.select(idList, op);
    }
    
    public boolean containsVertex(Integer curVertex)
    {
        return selVert.isSelected(curVertex);
    }
    
    public ArrayList<Integer> getVertexIds()
    {
        return selVert.getSelection();
    }


    public void selectEdges(Collection<Integer> idList, Selection.Operator op)
    {
        selEdge.select(idList, op);
    }
    
    public boolean containsEdge(Integer curEdge)
    {
        return selEdge.isSelected(curEdge);
    }
    
    public ArrayList<Integer> getEdgeIds()
    {
        return selEdge.getSelection();
    }


    public void selectFaces(Collection<Integer> idList, Selection.Operator op)
    {
        selFace.select(idList, op);
    }
    
    public boolean containsFace(Integer curEdge)
    {
        return selFace.isSelected(curEdge);
    }
    
    public ArrayList<Integer> getFaceIds()
    {
        return selFace.getSelection();
    }

    
    public void selectKnots(Collection<Integer> idList, Selection.Operator op)
    {
        selKnot.select(idList, op);
    }
    
    public boolean containsKnot(Integer id)
    {
        return selKnot.isSelected(id);
    }
    
    public ArrayList<Integer> getKnotIds()
    {
        return selKnot.getSelection();
    }

    public int getNumVertices()
    {
        return selVert.size();
    }

    public int getNumEdges()
    {
        return selEdge.size();
    }

    public int getNumFaces()
    {
        return selFace.size();
    }
}
