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
public class TessEdge
{
    TessVertex p0;
    TessVertex p1;

    TessHalfEdge halfRight;
    TessHalfEdge halfLeft;
    
    //Winding level of right side of line.  Winding of left side 
    // is windingLevel + 1.
    int windingLevel = Integer.MIN_VALUE;

    public TessEdge(TessVertex p0, TessVertex p1)
    {
        this.p0 = p0;
        this.p1 = p1;

        halfRight = new TessHalfEdge(this, true);
        halfLeft = new TessHalfEdge(this, false);
    }

    @Override
    public String toString()
    {
        return "(" + p0 + "->" + p1 + ") wind:" + windingLevel;
    }
    
}
