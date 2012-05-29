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

package com.kitfox.coyote.shape;

/**
 *
 * @author kitfox
 */
abstract public class PathConsumer
{
    abstract public void beginPath();

    abstract public void beginSubpath(double x0, double y0);

    abstract public void lineTo(double x0, double y0);

    abstract public void quadTo(double x0, double y0, double x1, double y1);

    abstract public void cubicTo(double x0, double y0, double x1, double y1, double x2, double y2);

    abstract public void closeSubpath();

    abstract public void endPath();

    public void feedShape(CyShape shape)
    {
        beginPath();

        double[] coords = new double[6];
        for (CyPathIterator it = shape.getIterator(); it.hasNext();)
        {
            switch (it.next(coords))
            {
                case MOVETO:
                    beginSubpath(coords[0], coords[1]);
                    break;
                case LINETO:
                    lineTo(coords[0], coords[1]);
                    break;
                case QUADTO:
                    quadTo(coords[0], coords[1], coords[2], coords[3]);
                    break;
                case CUBICTO:
                    cubicTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
                    break;
                case CLOSE:
                    closeSubpath();
                    break;
            }

        }

        endPath();
    }
}
