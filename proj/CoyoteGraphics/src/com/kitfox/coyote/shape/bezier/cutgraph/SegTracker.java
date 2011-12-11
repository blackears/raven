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

import com.kitfox.coyote.math.Math2DUtil;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author kitfox
 */
public class SegTracker
{
    ArrayList<SegRegion> regions0 = new ArrayList<SegRegion>();
    ArrayList<SegRegion> regions1 = new ArrayList<SegRegion>();

    /**
     * Add a region of crossover.
     * 
     * @param s0 Segment from curve 0 involved in crossover
     * @param s0t0 t value where crossover begins
     * @param s0t1 t value where crossover ends
     * @param s1 Segment from curve 1 involved in crossover
     * @param s1t0 t value where crossover begins
     * @param s1t1 t value where crossover ends
     * @param coord Coordinate characteristic of this segment.
     * Coordinates input here are used as the coordinates in
     * the final SegCrossovers.
     */
    public void addRegion(Segment s0, double s0t0, double s0t1,
            Segment s1, double s1t0, double s1t1,
            Coord coord)
    {
        regions0.add(new SegRegion(s0, 
                Math.min(s0t0, s0t1), Math.max(s0t0, s0t1), coord));

        regions1.add(new SegRegion(s1, 
                Math.min(s1t0, s1t1), Math.max(s1t0, s1t1), coord));
    }

    /**
     * Add point of crossover.
     * 
     * @param s0 Segment from curve 0 involved in crossover
     * @param t0 t value of crossover
     * @param s1 Segment from curve 1 involved in crossover
     * @param t1 t value of crossover
     * @param coord Coordinate characteristic of this segment.
     * Coordinates input here are used as the coordinates in
     * the final SegCrossovers.
     */
    public void addPoint(Segment s0, double t0, Segment s1, double t1,
            Coord coord)
    {
        regions0.add(new SegRegion(s0, t0, t0, coord));
        regions1.add(new SegRegion(s1, t1, t1, coord));
    }

    public ArrayList<SegCrossover> getCrossovers0()
    {
        return getCrossovers(regions0);
    }

    public ArrayList<SegCrossover> getCrossovers1()
    {
        return getCrossovers(regions1);
    }

    private ArrayList<SegCrossover> getCrossovers(ArrayList<SegRegion> regions)
    {
        Collections.sort(regions);

        ArrayList<SegCrossover> crossovers = new ArrayList<SegCrossover>();
        double tStart = 0, tEnd = 0;
        SegRegion unfinished = null;
        Coord coord = null;

        for (int i = 0; i < regions.size(); ++i)
        {
            SegRegion region = regions.get(i);

            if (unfinished != null)
            {
                coord = Coord.min(coord, region.coord);
                
                if (region.t0 != 0)
                {
                    //We do not start where the last one stopped
                    // Finish up last crossover and start a new
                    // region
                    crossovers.add(new SegCrossover(
                            tStart, region.seg.t0, coord));
                    unfinished = null;
                    coord = null;
                }
                else if (region.t1 == 1)
                {
                    //This spans [0 1] and attaches to the 
                    // unfinished region.  Skip and move to
                    // next segment
                    continue;
                }
                else
                {
                    //This region finishes off a previous region
                    tEnd = Math2DUtil.lerp(
                            region.seg.t0, region.seg.t1, region.t1);
                    crossovers.add(new SegCrossover(
                            tStart, tEnd, coord));
                    unfinished = null;
                    coord = null;
                    continue;
                }
            }

            //Ready to start a new region
            tStart = Math2DUtil.lerp(
                    region.seg.t0, region.seg.t1, region.t0);
            coord = region.coord;

            if (region.t1 == 1)
            {
                //Might join to following regions
                unfinished = region;
            }
            else
            {
                tEnd = Math2DUtil.lerp(
                        region.seg.t0, region.seg.t1, region.t1);
                crossovers.add(new SegCrossover(tStart, tEnd, coord));
            }
        }

        if (unfinished != null)
        {
            crossovers.add(new SegCrossover(tStart, 1, coord));
        }

        return crossovers;
    }
    
}
