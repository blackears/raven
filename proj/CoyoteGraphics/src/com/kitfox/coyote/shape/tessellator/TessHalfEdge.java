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

package com.kitfox.coyote.shape.tessellator;

/**
 *
 * @author kitfox
 */
public class TessHalfEdge
{
    TessEdge parent;
    boolean rightSide;

    public TessHalfEdge(TessEdge parent, boolean rightSide)
    {
        this.parent = parent;
        this.rightSide = rightSide;
    }

    int getWinding()
    {
        return parent.windingLevel + (rightSide ? 0 : 1);
    }

    TessVertex getHead()
    {
        return rightSide ? parent.p1 : parent.p0;
    }

    TessVertex getTail()
    {
        return rightSide ? parent.p0 : parent.p1;
    }

    TessHalfEdge getOtherSide()
    {
        return rightSide ? parent.halfLeft : parent.halfRight;
    }

    @Override
    public String toString()
    {
        return "[" + (rightSide ? "right" : "left") + parent.toString() + "]";
    }


}
