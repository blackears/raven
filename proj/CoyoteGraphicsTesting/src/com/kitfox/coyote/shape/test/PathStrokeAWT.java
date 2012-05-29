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

package com.kitfox.coyote.shape.test;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;

/**
 *
 * @author kitfox
 */
public class PathStrokeAWT
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        Path2D.Double p = new Path2D.Double();
        p.moveTo(0, 0);
//        p.curveTo(100, 0, 0, 100, 100, 100);
//        p.curveTo(1, 99, 00, 1, 1, 1);
        p.lineTo(100, 0);
        p.lineTo(000, 100);
        
//        sun.java2d.pipe.RenderingEngine re =
//            sun.java2d.pipe.RenderingEngine.getInstance();
//        BasicStroke stroke = new BasicStroke(6);
        BasicStroke stroke = new BasicStroke(6, 
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        Shape strokeShape = stroke.createStrokedShape(p);

        double[] coords = new double[6];
        for (PathIterator it = strokeShape.getPathIterator(null);
                !it.isDone(); it.next())
        {
            switch (it.currentSegment(coords))
            {
                case PathIterator.SEG_MOVETO:
                {
                    System.err.println(String.format("M %f %f ",
                            coords[0], coords[1]));
                    break;
                }
                case PathIterator.SEG_LINETO:
                {
                    System.err.println(String.format("L %f %f ",
                            coords[0], coords[1]));
                    break;
                }
                case PathIterator.SEG_QUADTO:
                {
                    System.err.println(String.format("Q %f %f %f %f ",
                            coords[0], coords[1],
                            coords[2], coords[3]));
                    break;
                }
                case PathIterator.SEG_CUBICTO:
                {
                    System.err.println(String.format("C %f %f %f %f %f %f ",
                            coords[0], coords[1],
                            coords[2], coords[3],
                            coords[4], coords[5]));
                    break;
                }
                case PathIterator.SEG_CLOSE:
                {
                    System.err.println(String.format("z "));
                    break;
                }
            }
        }
    }
}
