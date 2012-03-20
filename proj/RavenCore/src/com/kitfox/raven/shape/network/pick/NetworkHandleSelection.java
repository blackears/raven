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
    Selection<NetworkSubselElement> subsel;
    
    public NetworkHandleSelection(NetworkHandleSelection sel)
    {
        subsel = new Selection<NetworkSubselElement>(sel.subsel);
    }

    public NetworkHandleSelection()
    {
        subsel = new Selection<NetworkSubselElement>();
    }

    public int size()
    {
        return subsel.size();
    }
    
    public NetworkSubselElement get(int index)
    {
        return subsel.get(index);
    }
    
    private void selectEles(Collection<Integer> idList, 
            Selection.Operator op, NetworkSubselType type)
    {
        ArrayList<NetworkSubselElement> list = new ArrayList<NetworkSubselElement>();
        for (Integer id: idList)
        {
            list.add(new NetworkSubselElement(id, type));
        }
        subsel.select(list, op);
    }
    
    private boolean containsEle(Integer curVertex, NetworkSubselType type)
    {
        return subsel.isSelected(
                new NetworkSubselElement(curVertex, type));
    }
    
    private ArrayList<Integer> getEleIds(NetworkSubselType type)
    {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < subsel.size(); ++i)
        {
            NetworkSubselElement ele = subsel.get(i);
            if (ele.getType() == type)
            {
                list.add(ele.getId());
            }
        }
        return list;
    }

    public int getNumEles(NetworkSubselType type)
    {
        int count = 0;
        for (int i = 0; i < subsel.size(); ++i)
        {
            NetworkSubselElement ele = subsel.get(i);
            if (ele.getType() == type)
            {
                ++count;
            }
        }
        return count;
    }
    
    public void selectVertices(Collection<Integer> idList, Selection.Operator op)
    {
        selectEles(idList, op, NetworkSubselType.VERTEX);
    }
    
    public boolean containsVertex(Integer curVertex)
    {
        return containsEle(curVertex, NetworkSubselType.VERTEX);
    }
    
    public ArrayList<Integer> getVertexIds()
    {
        return getEleIds(NetworkSubselType.VERTEX);
    }

    public int getNumVertices()
    {
        return getNumEles(NetworkSubselType.VERTEX);
    }


    public void selectEdges(Collection<Integer> idList, Selection.Operator op)
    {
        selectEles(idList, op, NetworkSubselType.EDGE);
    }
    
    public boolean containsEdge(Integer curEdge)
    {
        return containsEle(curEdge, NetworkSubselType.EDGE);
    }
    
    public ArrayList<Integer> getEdgeIds()
    {
        return getEleIds(NetworkSubselType.EDGE);
    }

    public int getNumEdges()
    {
        return getNumEles(NetworkSubselType.EDGE);
    }


    public void selectFaces(Collection<Integer> idList, Selection.Operator op)
    {
        selectEles(idList, op, NetworkSubselType.FACE);
    }
    
    public boolean containsFace(Integer curEdge)
    {
        return containsEle(curEdge, NetworkSubselType.FACE);
    }
    
    public ArrayList<Integer> getFaceIds()
    {
        return getEleIds(NetworkSubselType.FACE);
    }

    public int getNumFaces()
    {
        return getNumEles(NetworkSubselType.FACE);
    }

    
    public void selectKnots(Collection<Integer> idList, Selection.Operator op)
    {
        selectEles(idList, op, NetworkSubselType.KNOT);
    }
    
    public boolean containsKnot(Integer id)
    {
        return containsEle(id, NetworkSubselType.KNOT);
    }
    
    public ArrayList<Integer> getKnotIds()
    {
        return getEleIds(NetworkSubselType.KNOT);
    }

    public int getNumKnots()
    {
        return getNumEles(NetworkSubselType.KNOT);
    }
}
