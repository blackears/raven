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

package com.kitfox.rabbit.codegen;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author kitfox
 */
public class ShapeToSVGUtil
{
    public static String toPathString(Shape s)
    {
        AffineTransform at = new AffineTransform();
        double[] coords = new double[6];
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        char cmdLast = '?';
        int ax = 0, ay = 0;
        int aqx = 0, aqy = 0;
        int acx = 0, acy = 0;
        for (PathIterator it = s.getPathIterator(at); !it.isDone(); it.next())
        {
            int seg = it.currentSegment(coords);
            switch (seg)
            {
                case PathIterator.SEG_MOVETO:
                {
                    int px = (int)coords[0];
                    int py = (int)coords[1];
                    if (cmdLast == '?')
                    {
                        pw.print('M');
                        pw.print(px);
                        pw.print(' ');
                        pw.print(py);
                        cmdLast = 'M';
                    }
                    else
                    {
                        pw.print(cmdLast == 'm' ? ' ' : 'm');
                        pw.print(px - ax);
                        pw.print(' ');
                        pw.print(py - ay);
                        cmdLast = 'm';
                    }
                    ax = aqx = acx = px;
                    ay = aqy = acy = py;
                    break;
                }
                case PathIterator.SEG_LINETO:
                {
                    int px = (int)coords[0];
                    int py = (int)coords[1];
                    if (px == ax)
                    {
                        //Vert line
                        pw.print(cmdLast == 'v' ? ' ' : 'v');
                        pw.print(py - ay);
                        cmdLast = 'v';
                    }
                    else if (py == ay)
                    {
                        //Horiz line
                        pw.print(cmdLast == 'h' ? ' ' : 'h');
                        pw.print(px - ax);
                        cmdLast = 'h';
                    }
                    else
                    {
                        //Angled line
                        pw.print(cmdLast == 'l' ? ' ' : 'l');
                        pw.print(px - ax);
                        pw.print(' ');
                        pw.print(py - ay);
                        cmdLast = 'l';
                    }
                    ax = aqx = acx = px;
                    ay = aqy = acy = py;
                    break;
                }
                case PathIterator.SEG_QUADTO:
                {
                    int k1x = (int)coords[0];
                    int k1y = (int)coords[1];
                    int px = (int)coords[2];
                    int py = (int)coords[3];

                    if (aqx == 2 * ax - k1x && aqy == 2 * ay - k1y)
                    {
                        pw.print(cmdLast == 't' ? ' ' : 't');
                        pw.print(px - ax);
                        pw.print(' ');
                        pw.print(py - ay);
                        cmdLast = 't';
                    }
                    else
                    {
                        pw.print(cmdLast == 'q' ? ' ' : 'q');
                        pw.print(k1x - ax);
                        pw.print(' ');
                        pw.print(k1y - ay);
                        pw.print(' ');
                        pw.print(px - ax);
                        pw.print(' ');
                        pw.print(py - ay);
                        cmdLast = 'q';
                    }

                    ax = acx = px;
                    ay = acy = py;
                    aqx = k1x;
                    aqy = k1y;
                    break;
                }
                case PathIterator.SEG_CUBICTO:
                {
                    int k1x = (int)coords[0];
                    int k1y = (int)coords[1];
                    int k2x = (int)coords[2];
                    int k2y = (int)coords[3];
                    int px = (int)coords[4];
                    int py = (int)coords[5];

                    if (acx == 2 * ax - k1x && acy == 2 * ay - k1y)
                    {
                        pw.print(cmdLast == 's' ? ' ' : 's');
                        pw.print(k2x - ax);
                        pw.print(' ');
                        pw.print(k2y - ay);
                        pw.print(' ');
                        pw.print(px - ax);
                        pw.print(' ');
                        pw.print(py - ay);
                        cmdLast = 's';
                    }
                    else
                    {
                        pw.print(cmdLast == 'c' ? ' ' : 'c');
                        pw.print(k1x - ax);
                        pw.print(' ');
                        pw.print(k1y - ay);
                        pw.print(' ');
                        pw.print(k2x - ax);
                        pw.print(' ');
                        pw.print(k2y - ay);
                        pw.print(' ');
                        pw.print(px - ax);
                        pw.print(' ');
                        pw.print(py - ay);
                        cmdLast = 'c';
                    }

                    ax = aqx = px;
                    ay = aqy = py;
                    acx = k2x;
                    acy = k2y;
                    break;
                }
                case PathIterator.SEG_CLOSE:
                {
                    pw.print('z');
                    cmdLast = 'z';
                    break;
                }
            }
        }

        return sw.toString();
    }


}
