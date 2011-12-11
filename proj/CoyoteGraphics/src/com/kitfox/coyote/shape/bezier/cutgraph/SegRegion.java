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

/**
 *
 * @author kitfox
 */
public class SegRegion implements Comparable<SegRegion>
{
    Segment seg;
    double t0;
    double t1;
    
    //Characteristic point of this segment
    Coord coord;

    public SegRegion(Segment s0, double t0, double t1, Coord coord)
    {
        this.seg = s0;
        this.t0 = t0;
        this.t1 = t1;
        this.coord = coord;
        assert t1 >= t0;
    }

    @Override
    public int compareTo(SegRegion oth)
    {
        int value = Double.compare(seg.t0, oth.seg.t0);
        if (value != 0)
        {
            return value;
        }
        return Double.compare(t0, oth.t0);
    }
    
}
