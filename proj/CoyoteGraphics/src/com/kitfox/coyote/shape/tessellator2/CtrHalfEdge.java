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

package com.kitfox.coyote.shape.tessellator2;

import com.kitfox.coyote.renderer.CyFramebufferTexture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Left edges flow in the same direction as their parent and
 * right edges flow backward.  The left side of a line is
 * always 1 winding level higher than the right side.
 *
 * @author kitfox
 */
public class CtrHalfEdge
{
    CtrEdge parent;
    boolean right;
    int winding = Integer.MIN_VALUE;

    public CtrHalfEdge(CtrEdge parent, boolean right)
    {
        this.parent = parent;
        this.right = right;
    }

    CtrVertex getHeadVert()
    {
        return right ? parent.v0 : parent.v1;
    }
    
    CtrVertex getTailVert()
    {
        return right ? parent.v1 : parent.v0;
    }
    
    void floodWinding(int winding)
    {
        if (winding == this.winding)
        {
            //We've already been here
            return;
        }

        if (this.winding != Integer.MIN_VALUE)
        {
            //If everything is going well, we should only call this method when
            // the winding level is not set yet, or with a value equal to the 
            // already assigned winding value.
            //Some badly formed shapes seem to be breaking this.
            Logger.getLogger(CtrHalfEdge.class.getName()).log(Level.WARNING, "Setting edge to different winding");
            return;
        }
//        assert this.winding == Integer.MIN_VALUE : "Setting edge to different winding";
        
        this.winding = winding;
        if (right)
        {
            parent.left.floodWinding(winding + 1);
        }
        else
        {
            parent.right.floodWinding(winding - 1);
        }

        CtrHalfEdge half = nextLoopEdge();
        half.floodWinding(winding);
    }

    CtrHalfEdge nextLoopEdge()
    {
        CtrVertex head = getHeadVert();
        CtrEdge next = head.nextEdgeCW(parent);
        return head.isEdgeIn(next) ? next.right : next.left;
    }

    @Override
    public String toString()
    {
        return "[" + getTailVert() + "->" + getHeadVert() + 
                (right ? "right" : "left") + " wind:" + winding + "]";
    }

    boolean isNextToZero()
    {
        return parent.left.winding == 0 || parent.right.winding == 0;
    }

}
