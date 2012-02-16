/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.coyote.shape.bezier;

/**
 *
 * @author kitfox
 */
public class PickPoint
{
    private final double x;
    private final double y;
    private final double t;
    private final double distSquared;

    public PickPoint(double x, double y, double t, double distSquared)
    {
        this.x = x;
        this.y = y;
        this.t = t;
        this.distSquared = distSquared;
    }

    /**
        * @return the x
        */
    public double getX()
    {
        return x;
    }

    /**
        * @return the y
        */
    public double getY()
    {
        return y;
    }

    /**
        * @return the t
        */
    public double getT()
    {
        return t;
    }

    /**
        * @return the distSquared
        */
    public double getDistSquared()
    {
        return distSquared;
    }

    @Override
    public String toString()
    {
        return "(" + x + ", " + y + ", " + t + ", " + distSquared + ")";
    }
    
    
}
