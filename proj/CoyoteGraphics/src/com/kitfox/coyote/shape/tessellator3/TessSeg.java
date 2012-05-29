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

package com.kitfox.coyote.shape.tessellator3;

import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class TessSeg
{
    final Coord v0;
    final Coord v1;

    public TessSeg(Coord v0, Coord v1)
    {
        this.v0 = v0;
        this.v1 = v1;
    }
    
    public TessSeg reverse()
    {
        return new TessSeg(v1, v0);
    }
    
    public int dx()
    {
        return v1.x - v0.x;
    }
    
    public int dy()
    {
        return v1.y - v0.y;
    }

    public boolean isPointOnLine(Coord p)
    {
        int dx0 = v1.x - v0.x;
        int dy0 = v1.y - v0.y;
        int dx1 = p.x - v0.x;
        int dy1 = p.y - v0.y;
        
        return dx0 * dy1 == dx1 * dy0; 
    }

    private double fractionAlongRay(Coord c)
    {
        if (c.equals(v0))
        {
            return 0;
        }
        if (c.equals(v1))
        {
            return 1;
        }
        
        int dx0 = v1.x - v0.x;
        int dy0 = v1.y - v0.y;
        int dx1 = c.x - v0.x;
        int dy1 = c.y - v0.y;

        return Math2DUtil.dot(dx1, dy1, dx0, dy0) / 
                (double)Math2DUtil.dot(dx0, dy0, dx0, dy0);
    }
    
    private boolean isIn01(double v)
    {
        return v > 0 && v < 1;
    }
    
    public boolean split(TessSeg s, ArrayList<TessSeg> newSegs)
    {
        boolean touch00 = isPointOnLine(s.v0);
        boolean touch01 = isPointOnLine(s.v1);

        if (touch00 && touch01)
        {
            if (equals(s) || isReverseOf(s))
            {
                //Lines exactly overlap - do not split
                return false;
            }
            
            boolean split0 = partition(s.v0, s.v1, newSegs);
            boolean split1 = s.partition(v0, v1, newSegs);
            
            return split0 || split1;
        }
        
        //Check if this touches other seg
        if (touch00)
        {
            newSegs.add(s);
            return partition(s.v0, newSegs);
        }
        if (touch01)
        {
            newSegs.add(s);
            return partition(s.v1, newSegs);
        }
        
        //Check if other touches this seg
        boolean touch10 = s.isPointOnLine(v0);
        boolean touch11 = s.isPointOnLine(v1);

        if (touch10)
        {
            newSegs.add(this);
            return s.partition(v0, newSegs);
        }
        if (touch11)
        {
            newSegs.add(this);
            return s.partition(v1, newSegs);
        }
        
        //There are no point-on-line splits.
        //Check for crossing split
        double[] frac = Math2DUtil.lineIsectFractions(
                v0.x, v0.y, dx(), dy(),
                s.v0.x, s.v0.y, s.dx(), s.dy(), null);
        
        if (frac == null || !isIn01(frac[0]) || !isIn01(frac[1]))
        {
            newSegs.add(this);
            newSegs.add(s);
            return false;
        }
        
        Coord c = new Coord(
                (int)Math2DUtil.lerp(v0.x, v1.x, frac[0]),
                (int)Math2DUtil.lerp(v0.y, v1.y, frac[0]));
        
        newSegs.add(new TessSeg(v0, c));
        newSegs.add(new TessSeg(c, v1));
        newSegs.add(new TessSeg(s.v0, c));
        newSegs.add(new TessSeg(c, s.v1));
        return true;
    }

    private boolean partition(Coord w0, ArrayList<TessSeg> newSegs)
    {
        double t0 = fractionAlongRay(w0);
        
        if (isIn01(t0))
        {
            newSegs.add(new TessSeg(v0, w0));
            newSegs.add(new TessSeg(w0, v1));            
            return true;
        }
        
        newSegs.add(this);
        return false;
    }

    private boolean partition(Coord w0, Coord w1, ArrayList<TessSeg> newSegs)
    {
        double t0 = fractionAlongRay(w0);
        double t1 = fractionAlongRay(w1);
        
        if (t0 > t1)
        {
            double tt = t0;
            t0 = t1;
            t1 = tt;
            Coord tw = w0;
            w0 = w1;
            w1 = tw;
        }
        
        if (isIn01(t0) && isIn01(t1))
        {
            newSegs.add(new TessSeg(v0, w0));
            newSegs.add(new TessSeg(w0, w1));
            newSegs.add(new TessSeg(w1, v1));
            return true;
        }
        
        if (isIn01(t0))
        {
            newSegs.add(new TessSeg(v0, w0));
            newSegs.add(new TessSeg(w0, v1));            
            return true;
        }
        
        if (isIn01(t1))
        {
            newSegs.add(new TessSeg(v0, w1));
            newSegs.add(new TessSeg(w1, v1));            
            return true;
        }
        
        newSegs.add(this);
        return false;
    }

    public boolean isReverseOf(TessSeg seg)
    {
        return v0.equals(seg.v1) && v1.equals(seg.v0);
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final TessSeg other = (TessSeg)obj;
        if (this.v0 != other.v0 && (this.v0 == null || !this.v0.equals(other.v0)))
        {
            return false;
        }
        if (this.v1 != other.v1 && (this.v1 == null || !this.v1.equals(other.v1)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 83 * hash + (this.v0 != null ? this.v0.hashCode() : 0);
        hash = 83 * hash + (this.v1 != null ? this.v1.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString()
    {
        return "[" + v0 + " " + v1 + "]";
    }

    public Coord getOtherVertex(Coord c)
    {
        return v0.equals(c) ? v1 : v0;
    }
    
    
}
