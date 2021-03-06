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

import com.kitfox.coyote.shape.bezier.mesh.BezierVertexSmooth;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;

/**
 *
 * @author kitfox
 */
public interface NetworkHandleKnot
{
    public int getIndex();
    public NetworkHandleVertex getVertex();
    public NetworkHandleEdge getEdge();
    public Coord getCoord();

    /**
     * 
     * @return true if this is the knot at the head of the curve (ie, k1).
     * false if at the tail (ie, k0).
     */
    public boolean isHead();

    public BezierVertexSmooth getSmoothing();

    public NetworkHandleKnot getSmoothingPeer();

    public void setSmoothing(BezierVertexSmooth smoothing);
}
