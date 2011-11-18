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
    int xlateX;
    int xlateY;
    FIXED rotSkew0;
    FIXED rotSkew1;
    FIXED scaleX;
    FIXED scaleY;

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
}
