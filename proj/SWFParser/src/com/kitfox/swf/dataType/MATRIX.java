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

package com.kitfox.swf.dataType;

import java.awt.geom.AffineTransform;

/**
 *
 * @author kitfox
 */
public class MATRIX
{
    private int xlateX;
    private int xlateY;
    private FIXED rotSkew0;
    private FIXED rotSkew1;
    private FIXED scaleX;
    private FIXED scaleY;

    public MATRIX(int xlateX, int xlateY, FIXED rotSkew0, FIXED rotSkew1, FIXED scaleX, FIXED scaleY)
    {
        this.xlateX = xlateX;
        this.xlateY = xlateY;
        this.rotSkew0 = rotSkew0;
        this.rotSkew1 = rotSkew1;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public AffineTransform asAffineTransform()
    {
        double m00 = scaleX == null ? 1 : scaleX.doubleValue();
        double m01 = rotSkew0 == null ? 0 : rotSkew0.doubleValue();
        double m02 = xlateX;
        double m10 = rotSkew1 == null ? 0 : rotSkew1.doubleValue();
        double m11 = scaleY == null ? 1 : scaleY.doubleValue();
        double m12 = xlateY;
        
        return new AffineTransform(m00, m10, m01, m11, m02, m12);
    }
    
    public double getM00()
    {
        return scaleX == null ? 1 : scaleX.doubleValue();
    }
    
    public double getM01()
    {
        return rotSkew0 == null ? 0 : rotSkew0.doubleValue();
    }
    
    public double getM02()
    {
        return xlateX;
    }
    
    public double getM10()
    {
        return rotSkew1 == null ? 0 : rotSkew1.doubleValue();
    }
    
    public double getM11()
    {
        return scaleY == null ? 1 : scaleY.doubleValue();
    }
    
    public double getM12()
    {
        return xlateY;
    }

    /**
     * @return the xlateX
     */
    public int getXlateX()
    {
        return xlateX;
    }

    /**
     * @param xlateX the xlateX to set
     */
    public void setXlateX(int xlateX)
    {
        this.xlateX = xlateX;
    }

    /**
     * @return the xlateY
     */
    public int getXlateY()
    {
        return xlateY;
    }

    /**
     * @param xlateY the xlateY to set
     */
    public void setXlateY(int xlateY)
    {
        this.xlateY = xlateY;
    }

    /**
     * @return the rotSkew0
     */
    public FIXED getRotSkew0()
    {
        return rotSkew0;
    }

    /**
     * @param rotSkew0 the rotSkew0 to set
     */
    public void setRotSkew0(FIXED rotSkew0)
    {
        this.rotSkew0 = rotSkew0;
    }

    /**
     * @return the rotSkew1
     */
    public FIXED getRotSkew1()
    {
        return rotSkew1;
    }

    /**
     * @param rotSkew1 the rotSkew1 to set
     */
    public void setRotSkew1(FIXED rotSkew1)
    {
        this.rotSkew1 = rotSkew1;
    }

    /**
     * @return the scaleX
     */
    public FIXED getScaleX()
    {
        return scaleX;
    }

    /**
     * @param scaleX the scaleX to set
     */
    public void setScaleX(FIXED scaleX)
    {
        this.scaleX = scaleX;
    }

    /**
     * @return the scaleY
     */
    public FIXED getScaleY()
    {
        return scaleY;
    }

    /**
     * @param scaleY the scaleY to set
     */
    public void setScaleY(FIXED scaleY)
    {
        this.scaleY = scaleY;
    }
}
