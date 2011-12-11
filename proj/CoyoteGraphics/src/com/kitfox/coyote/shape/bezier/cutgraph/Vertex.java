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

import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class Vertex
{
    Coord coord;
    ArrayList<Edge> edgesIn = new ArrayList<Edge>();
    ArrayList<Edge> edgesOut = new ArrayList<Edge>();

    public Vertex(Coord coord)
    {
        this.coord = coord;
    }
    
}
