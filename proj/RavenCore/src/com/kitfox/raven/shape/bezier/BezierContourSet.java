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

package com.kitfox.raven.shape.bezier;

import java.awt.geom.Path2D;
import java.util.ArrayList;

/**
 * A face can have more than one contour - one exterior and zero or
 * more holes.  This provides a view of the edge data partitioned into
 * contours and sorted into winding order.
 * 
 * @author kitfox
 */
public class BezierContourSet
{
    ArrayList<BezierContour> contourList;

    public BezierContourSet(ArrayList<BezierContour> contourList)
    {
        this.contourList = contourList;
    }

    public Path2D.Double createPath()
    {
        Path2D.Double path = new Path2D.Double();

        for (BezierContour contour: contourList)
        {
            path.append(contour.createPath(), false);
        }

        return path;
    }

}
