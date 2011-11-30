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

package com.kitfox.raven.shape.path;

import com.kitfox.rabbit.codegen.ShapeToSVGUtil;
import com.kitfox.rabbit.parser.path.PathParser;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author kitfox
 */
@Deprecated
public class PathCurve
{
    private final PathSeg[] segments;

    public PathCurve(String text)
    {
        this(PathParser.parse(text));
    }

    public PathCurve(Path2D path)
    {
        if (path == null)
        {
            segments = new PathSeg[0];
            return;
        }

        ArrayList<PathSeg> segList = new ArrayList<PathSeg>();

        double[] coords = new double[6];
        for (PathIterator it = path.getPathIterator(null); !it.isDone(); it.next())
        {
            switch (it.currentSegment(coords))
            {
                case PathIterator.SEG_CLOSE:
                    segList.add(new PathSegClosePath());
                    break;
                case PathIterator.SEG_MOVETO:
                    segList.add(new PathSegMoveTo((int)coords[0], (int)coords[1]));
                    break;
                case PathIterator.SEG_LINETO:
                    segList.add(new PathSegLineTo((int)coords[0], (int)coords[1]));
                    break;
                case PathIterator.SEG_QUADTO:
                    segList.add(new PathSegQuadTo((int)coords[0], (int)coords[1],
                            (int)coords[2], (int)coords[3]));
                    break;
                case PathIterator.SEG_CUBICTO:
                    segList.add(new PathSegCubicTo((int)coords[0], (int)coords[1],
                            (int)coords[2], (int)coords[3],
                            (int)coords[4], (int)coords[5]));
                    break;
            }
        }

        segments = segList.toArray(new PathSeg[segList.size()]);
    }

    public Path2D.Double asPath2D()
    {
        Path2D.Double path = new Path2D.Double();

        for (int i = 0; i < segments.length; ++i)
        {
            PathSeg seg = segments[i];
            seg.append(path);
        }

        return path;
    }

    @Override
    public String toString()
    {
        return ShapeToSVGUtil.toPathString(asPath2D());
    }

    public String toSVGPathLongForm()
    {
        StringWriter sw = new StringWriter();

        for (PathSeg seg: segments)
        {
            sw.append(seg.toSVGPath());
        }

        return sw.toString();
    }

    /**
     * @return the segments
     */
    public PathSeg[] getSegments()
    {
        return segments.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PathCurve other = (PathCurve) obj;
        if (!Arrays.deepEquals(this.segments, other.segments)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Arrays.deepHashCode(this.segments);
        return hash;
    }


}
