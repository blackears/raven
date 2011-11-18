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

package com.kitfox.rabbit.util;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;


/**
 *
 * @author kitfox
 */
public class PathBuilder
{
    public static Path2D.Double create(int[] modes, double[] data)
    {
        Path2D.Double path = new Path2D.Double();

        int ptr = 0;
        for (int mode: modes)
        {
            switch (mode)
            {
                case PathIterator.SEG_CLOSE:
                    path.closePath();
                    break;
                case PathIterator.SEG_CUBICTO:
                    path.curveTo(data[ptr++], data[ptr++],
                            data[ptr++], data[ptr++],
                            data[ptr++], data[ptr++]);
                    break;
                case PathIterator.SEG_LINETO:
                    path.lineTo(data[ptr++], data[ptr++]);
                    break;
                case PathIterator.SEG_MOVETO:
                    path.moveTo(data[ptr++], data[ptr++]);
                    break;
                case PathIterator.SEG_QUADTO:
                    path.quadTo(data[ptr++], data[ptr++],
                            data[ptr++], data[ptr++]);
                    break;
            }
        }

        return path;
    }
}
