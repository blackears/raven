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

package com.kitfox.coyote.shape.bezier.cutgraph;

import com.kitfox.coyote.shape.bezier.BezierCurve2i;

/**
 *
 * @author kitfox
 */
public class Edge<DataType>
{
    DataType source;
    BezierCurve2i curve;
    Segment[] flat;

    Vertex v0;
    Vertex v1;
    double t0;
    double t1;
    
    final double flatnessSquared;

    public Edge(Vertex v0, Vertex v1, 
            BezierCurve2i curve, double flatnessSquared,
            DataType source)
    {
        this.v0 = v0;
        this.v1 = v1;
        this.curve = curve;
        this.flatnessSquared = flatnessSquared;
        this.flat = Segment.flatten(curve, flatnessSquared);
        this.source = source;

        t0 = 0;
        t1 = 1;
    }
    
}
