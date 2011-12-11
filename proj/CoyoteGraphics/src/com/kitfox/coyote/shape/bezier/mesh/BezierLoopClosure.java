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
public enum BezierLoopClosure
{
    /**
     * Ends points of this path are independent
     */
    OPEN, 
    /**
     *Loop is closed by connecting the tail to the head with a straight line.
     * The individual end vertices are free to move where they like
     * */
    CLOSED_FREE, 
    /**
     * Loop is closed by considering the tail vertex to be coincident with 
     * the head vertex.  In this case, the final vertex position, smoothing
     * and exit edge are ignored and considered to be identical to that of
     * the head vertex.  The head vertex's enter edge is ignored and the
     * tail's enter edge is used instead.
     */
    CLOSED_CLAMPED
}
