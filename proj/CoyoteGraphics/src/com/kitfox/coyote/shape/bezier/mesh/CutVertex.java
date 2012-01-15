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

import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author kitfox
 */
public class CutVertex
{
    Coord coord;
    ArrayList<CutSegment> segIn = new ArrayList<CutSegment>();
    ArrayList<CutSegment> segOut = new ArrayList<CutSegment>();

    ArrayList<CutSegHalf> segRadial = new ArrayList<CutSegHalf>();

    public CutVertex(Coord coord)
    {
        this.coord = coord;
    }

    public int size()
    {
        return segIn.size() + segOut.size();
    }

    public boolean isEmpty()
    {
        return segIn.isEmpty() && segOut.isEmpty();
    }

    @Override
    public String toString()
    {
        return "" + coord;
    }

    public int radialIndex(CutSegHalf curSeg)
    {
        return segRadial.indexOf(curSeg);
    }

    public void sortRadial()
    {
        Collections.sort(segRadial, new CompareSeg());
    }

    public CutSegHalf nextSegCCW(CutSegHalf curSeg)
    {
        int idx = segRadial.indexOf(curSeg);
        if (idx == -1)
        {
            throw new IllegalArgumentException("Half segment is not managed by this vertex");
        }
        
        idx = idx == segRadial.size() - 1 ? 0 : idx + 1;
        return segRadial.get(idx);
    }

    public CutSegHalf nextSegCW(CutSegHalf curSeg)
    {
        int idx = segRadial.indexOf(curSeg);
        if (idx == -1)
        {
            throw new IllegalArgumentException("Half segment is not managed by this vertex");
        }
        
        idx = idx == 0 ? segRadial.size() - 1 : idx - 1;
        return segRadial.get(idx);
    }
    
    private int rank(int dx, int dy)
    {
        if (dx > 0)
        {
            return 1;
        }
        else if (dx < 0)
        {
            return 3;
        }
        else if (dy < 0)
        {
            return 0;
        }
        else 
        {
            return 2;
        }
    }

    //-----------------------------------
    class CompareSeg implements Comparator<CutSegHalf>
    {
        @Override
        public int compare(CutSegHalf seg0, CutSegHalf seg1)
        {
            int dx0 = seg0.getDx();
            int dy0 = seg0.getDy();
            int dx1 = seg1.getDx();
            int dy1 = seg1.getDy();

            if ((dx0 == 0 && dy0 == 0) || (dx1 == 0 && dy1 == 0))
            {
                throw new RuntimeException("Cannot sort zero length segments");
            }

            //Sort based on sector
            int dRank = rank(dx0, dy0) - rank(dx1, dy1);
            if (dRank != 0)
            {
                return dRank;
            }
            
            //Both on same side of line x = 0.  Sort ccw by
            // comparing slopes
            return dy0 * dx1 - dx0 * dy1;
        }

    }
        
}
