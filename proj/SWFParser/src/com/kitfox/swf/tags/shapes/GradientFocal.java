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

package com.kitfox.swf.tags.shapes;

import com.kitfox.swf.dataType.FIXED8;
import com.kitfox.swf.dataType.MATRIX;
import com.kitfox.swf.dataType.SWFDataReader;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.io.IOException;

/**
 *
 * @author kitfox
 */
public class GradientFocal extends Gradient
{
    private FIXED8 focalPoint;

    public GradientFocal(SWFDataReader in, int shapeType) throws IOException
    {
        super(in, shapeType);

        focalPoint = in.getFIXED8();
    }

    @Override
    public RadialGradientPaint createRadialPaint(MATRIX gradMtx)
    {
        return createRadialPaint(gradMtx, 
                new Point2D.Float(focalPoint.asFloat() * 16384, 0));
    }

    /**
     * @return the focalPoint
     */
    public FIXED8 getFocalPoint() {
        return focalPoint;
    }

    /**
     * @param focalPoint the focalPoint to set
     */
    public void setFocalPoint(FIXED8 focalPoint) {
        this.focalPoint = focalPoint;
    }

}
