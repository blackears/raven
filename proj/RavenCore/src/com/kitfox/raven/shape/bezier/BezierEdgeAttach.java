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

package com.kitfox.raven.shape.bezier;

/**
 * Keep track of which side an edge is attached to the face on.  It is
 * possible for a face to be attached on both the left and right side.
 *
 * @author kitfox
 */
public class BezierEdgeAttach
{
    BezierEdge edge;
    boolean rightJoin;

    public BezierEdgeAttach(BezierEdge edge, boolean rightJoin)
    {
        this.edge = edge;
        this.rightJoin = rightJoin;
    }

    @Override
    public String toString()
    {
        return (rightJoin ? "rightJoin  " : "leftJoin  ") +  edge;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final BezierEdgeAttach other = (BezierEdgeAttach) obj;
        if (this.edge != other.edge && (this.edge == null || !this.edge.equals(other.edge)))
        {
            return false;
        }
        if (this.rightJoin != other.rightJoin)
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 59 * hash + (this.edge != null ? this.edge.hashCode() : 0);
        hash = 59 * hash + (this.rightJoin ? 1 : 0);
        return hash;
    }

    protected void attachToFace(BezierFace newFace)
    {
        if (rightJoin)
        {
            edge.faceRight.removeEdgeRight(edge);
            edge.faceRight = newFace;
            newFace.addEdgeRight(edge);
        }
        else
        {
            edge.faceLeft.removeEdgeLeft(edge);
            edge.faceLeft = newFace;
            newFace.addEdgeLeft(edge);
        }
    }

}
