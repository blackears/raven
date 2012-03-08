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

package com.kitfox.raven.editor.node.tools.common.shape;

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.raven.editor.node.tools.ToolService;
import com.kitfox.raven.paint.RavenPaint;
import com.kitfox.raven.paint.RavenStroke;
import com.kitfox.raven.shape.network.pick.NetworkHandleEdge;
import com.kitfox.raven.shape.network.pick.NetworkHandleFace;
import com.kitfox.raven.shape.network.pick.NetworkHandleVertex;
import com.kitfox.raven.util.Intersection;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author kitfox
 */
public interface ServiceShapeManip extends ToolService
{
    public ArrayList<NetworkHandleVertex>
            pickVertices(CyRectangle2d region, CyMatrix4d l2d, Intersection isect);
    public ArrayList<NetworkHandleEdge>
            pickEdges(CyRectangle2d region, CyMatrix4d l2d, Intersection isect);
    public ArrayList<NetworkHandleFace>
            pickFaces(CyRectangle2d region, CyMatrix4d l2d, Intersection isect);
    
    public ArrayList<NetworkHandleEdge> getConnectedEdges(NetworkHandleEdge edge);
    
    public void setEdgePaintAndStroke(RavenPaint paint, RavenStroke stroke, Collection<NetworkHandleEdge> edges, boolean history);
    public void setEdgePaint(RavenPaint paint, Collection<NetworkHandleEdge> edges, boolean history);
    public void setEdgeStroke(RavenStroke stroke, Collection<NetworkHandleEdge> edges, boolean history);
    public void setFacePaint(RavenPaint paint, Collection<NetworkHandleFace> faces, boolean history);

    public CyMatrix4d getGraphToWorldXform();

    public ArrayList<? extends NetworkHandleEdge> getEdges();

    public ArrayList<? extends NetworkHandleVertex> getVertices();
    
}
