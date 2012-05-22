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

package com.kitfox.raven.editor.node.tools.common.shape.curveEdit;

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.raven.editor.node.tools.common.shape.pen.ServiceBezierPath;
import com.kitfox.raven.shape.network.pick.NetworkPathHandles;

/**
 *
 * @author kitfox
 */
abstract public class PathDragSet
{
    ServiceBezierPath servPath;
    NetworkPathHandles handles;
    CyMatrix4d g2d;
    CyMatrix4d d2g;

    public PathDragSet(ServiceBezierPath servPath,
            NetworkPathHandles handles, CyMatrix4d g2d)
    {
        this.servPath = servPath;
        this.handles = handles;
        this.g2d = g2d;
        
        d2g = new CyMatrix4d(g2d);
        d2g.invert();
    }

    abstract public void dragBy(int dx, int dy, boolean history);
    
    protected void xformPointDev2Graph(CyVector2d delta)
    {
        d2g.transformPoint(delta);
    }
    
    protected void xformVectorDev2Graph(CyVector2d delta)
    {
        d2g.transformVector(delta);
    }
    
}
