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

package com.kitfox.coyote.shape.bezier.mesh;

/**
 *
 * @author kitfox
 */
public enum BezierVertexSmooth
{
    //Terminates as a corner.  If both ends of edge are corners, is a 
    // straight line
    CORNER, 
    //If the vertex this edge-vertex terminates in has another edge-vertex
    // with an opposite tangent, both this and opposite tangents should
    // be adjusted simeltaneously
    SMOOTH, 
    //Knots will be automatically calculated to provide a smooth join at
    // vertices
    AUTO_SMOOTH, 
    //Knot is completely independent and set by user
    FREE
}
