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

package com.kitfox.raven.math.test;

import com.kitfox.raven.shape.path.PathCurve;
import java.awt.Point;
import java.awt.geom.Path2D;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class StrokeBuilder2
{
    ArrayList<Point> points = new ArrayList<Point>();
    private PathCurve displayPath;

    public void addPoint(int x, int y)
    {
        points.add(new Point(x, y));
        buildDisplayCurve();
    }

    private void buildDisplayCurve()
    {
        Path2D.Double path = new Path2D.Double();

        if (points.size() <= 1)
        {
            displayPath = null;
            return;
        }

        Point head = points.get(0);
        path.moveTo(head.x, head.y);

        for (int i = 1; i < points.size(); ++i)
        {
            Point pt = points.get(i);
            path.lineTo(pt.x, pt.y);
        }
        displayPath = new PathCurve(path);
    }

    /**
     * @return the displayPath
     */
    public PathCurve getDisplayPath()
    {
        return displayPath;
    }

}
