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

import com.kitfox.coyote.math.CyMatrix3d;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.math.CyVector3d;
import java.awt.geom.Path2D;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author kitfox
 */
abstract public class CyShape
{
    abstract public CyRectangle2d getBounds();

    abstract public CyPathIterator getIterator();

    public CyPath2d createTransformedPath(CyMatrix3d m)
    {
        return createTransformedPath(m, false);
    }

    public CyPath2d createTransformedPath(CyMatrix3d m, boolean normalize)
    {
        CyPath2d newPath = new CyPath2d();
        CyVector2d pt = new CyVector2d();
        CyVector2d k0 = new CyVector2d();
        CyVector2d k1 = new CyVector2d();

        double[] coords = new double[6];
        for (CyPathIterator it = getIterator(); it.hasNext();)
        {
            switch (it.next(coords))
            {
                case MOVETO:
                    pt.set(coords[0], coords[1]);
                    m.transformPoint(pt, normalize);
                    newPath.moveTo(pt.x, pt.y);
                    break;
                case LINETO:
                    pt.set(coords[0], coords[1]);
                    m.transformPoint(pt, normalize);
                    newPath.lineTo(pt.x, pt.y);
                    break;
                case QUADTO:
                    pt.set(coords[0], coords[1]);
                    k0.set(coords[2], coords[3]);
                    m.transformPoint(pt, normalize);
                    m.transformPoint(k0, normalize);
                    newPath.quadTo(k0.x, k0.y, pt.x, pt.y);
                    break;
                case CUBICTO:
                    pt.set(coords[0], coords[1]);
                    k0.set(coords[2], coords[3]);
                    k1.set(coords[4], coords[5]);
                    m.transformPoint(pt, normalize);
                    m.transformPoint(k0, normalize);
                    m.transformPoint(k1, normalize);
                    newPath.cubicTo(k0.x, k0.y, k1.x, k1.y, pt.x, pt.y);
                    break;
            }
        }

        return newPath;
    }

    public CyPath2d createTransformedPath(CyMatrix4d m)
    {
        return createTransformedPath(m, false);
    }

    public CyPath2d createTransformedPath(CyMatrix4d m, boolean normalize)
    {
        CyPath2d newPath = new CyPath2d();
        CyVector3d pt = new CyVector3d();
        CyVector3d k0 = new CyVector3d();
        CyVector3d k1 = new CyVector3d();

        double[] coords = new double[6];
        for (CyPathIterator it = getIterator(); it.hasNext();)
        {
            switch (it.next(coords))
            {
                case MOVETO:
                    pt.set(coords[0], coords[1], 0);
                    m.transformPoint(pt, normalize);
                    newPath.moveTo(pt.x, pt.y);
                    break;
                case LINETO:
                    pt.set(coords[0], coords[1], 0);
                    m.transformPoint(pt, normalize);
                    newPath.lineTo(pt.x, pt.y);
                    break;
                case QUADTO:
                    k0.set(coords[0], coords[1], 0);
                    pt.set(coords[2], coords[3], 0);
                    m.transformPoint(pt, normalize);
                    m.transformPoint(k0, normalize);
                    newPath.quadTo(k0.x, k0.y, pt.x, pt.y);
                    break;
                case CUBICTO:
                    k0.set(coords[0], coords[1], 0);
                    k1.set(coords[2], coords[3], 0);
                    pt.set(coords[4], coords[5], 0);
                    m.transformPoint(pt, normalize);
                    m.transformPoint(k0, normalize);
                    m.transformPoint(k1, normalize);
                    newPath.cubicTo(k0.x, k0.y, k1.x, k1.y, pt.x, pt.y);
                    break;
            }
        }

        return newPath;
    }

    public CyRectangle2d createTransformedBounds(CyMatrix4d m)
    {
        return createTransformedBounds(m, false);
    }

    public CyRectangle2d createTransformedBounds(CyMatrix4d m, boolean normalize)
    {
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        CyVector3d pt = new CyVector3d();
        CyVector3d k0 = new CyVector3d();
        CyVector3d k1 = new CyVector3d();

        double[] coords = new double[6];
        for (CyPathIterator it = getIterator(); it.hasNext();)
        {
            switch (it.next(coords))
            {
                case MOVETO:
                case LINETO:
                    pt.set(coords[0], coords[1], 0);
                    m.transformPoint(pt, normalize);

                    minX = Math.min(minX, pt.x);
                    maxX = Math.max(maxX, pt.x);
                    minY = Math.min(minY, pt.y);
                    maxY = Math.max(maxY, pt.y);
                    break;
                case QUADTO:
                    pt.set(coords[0], coords[1], 0);
                    k0.set(coords[2], coords[3], 0);
                    m.transformPoint(pt, normalize);
                    m.transformPoint(k0, normalize);

                    minX = Math.min(minX, pt.x);
                    maxX = Math.max(maxX, pt.x);
                    minY = Math.min(minY, pt.y);
                    maxY = Math.max(maxY, pt.y);

                    minX = Math.min(minX, k0.x);
                    maxX = Math.max(maxX, k0.x);
                    minY = Math.min(minY, k0.y);
                    maxY = Math.max(maxY, k0.y);
                    break;
                case CUBICTO:
                    pt.set(coords[0], coords[1], 0);
                    k0.set(coords[2], coords[3], 0);
                    k1.set(coords[4], coords[5], 0);
                    m.transformPoint(pt, normalize);
                    m.transformPoint(k0, normalize);
                    m.transformPoint(k1, normalize);

                    minX = Math.min(minX, pt.x);
                    maxX = Math.max(maxX, pt.x);
                    minY = Math.min(minY, pt.y);
                    maxY = Math.max(maxY, pt.y);

                    minX = Math.min(minX, k0.x);
                    maxX = Math.max(maxX, k0.x);
                    minY = Math.min(minY, k0.y);
                    maxY = Math.max(maxY, k0.y);

                    minX = Math.min(minX, k1.x);
                    maxX = Math.max(maxX, k1.x);
                    minY = Math.min(minY, k1.y);
                    maxY = Math.max(maxY, k1.y);
                    break;
            }
        }

        return new CyRectangle2d(minX, minY, maxX - minX, maxY - minY);
    }

    public Path2D.Double asPathAWT()
    {
        Path2D.Double path = new Path2D.Double();

        double[] coords = new double[6];
        for (CyPathIterator it = getIterator(); it.hasNext();)
        {
            switch (it.next(coords))
            {
                case MOVETO:
                    path.moveTo(coords[0], coords[1]);
                    break;
                case LINETO:
                    path.lineTo(coords[0], coords[1]);
                    break;
                case QUADTO:
                    path.quadTo(coords[0], coords[1], coords[2], coords[3]);
                    break;
                case CUBICTO:
                    path.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
                    break;
                case CLOSE:
                    path.closePath();
                    break;
            }
        }

        return path;
    }

    public boolean contains(CyVector2d v)
    {
        return contains(v.x, v.y);
    }

    public boolean contains(double x, double y)
    {
        PathLines lineTess = new PathLines();
        PathFlattener flat = new PathFlattener(lineTess);

        flat.feedShape(this);

        return lineTess.contains(x, y);
    }

    public boolean contains(CyRectangle2d rectangle)
    {
        PathLines lineTess = new PathLines();
        PathFlattener flat = new PathFlattener(lineTess);

        flat.feedShape(this);

        return lineTess.contains(rectangle);
    }

    public boolean intersects(CyRectangle2d rectangle)
    {
        PathLines lineTess = new PathLines();
        PathFlattener flat = new PathFlattener(lineTess);

        flat.feedShape(this);

        return lineTess.intersects(rectangle);
    }

    public void export(DataOutputStream dout) throws IOException
    {
        double[] coords = new double[6];

        for (CyPathIterator it = getIterator(); it.hasNext();)
        {
            CyPathIterator.Type type = it.next(coords);
            dout.writeByte(type.ordinal());

            switch (type)
            {
                case MOVETO:
                    dout.writeDouble(coords[0]);
                    dout.writeDouble(coords[1]);
                    break;
                case LINETO:
                    dout.writeDouble(coords[0]);
                    dout.writeDouble(coords[1]);
                    break;
                case QUADTO:
                    dout.writeDouble(coords[0]);
                    dout.writeDouble(coords[1]);
                    dout.writeDouble(coords[2]);
                    dout.writeDouble(coords[3]);
                    break;
                case CUBICTO:
                    dout.writeDouble(coords[0]);
                    dout.writeDouble(coords[1]);
                    dout.writeDouble(coords[2]);
                    dout.writeDouble(coords[3]);
                    dout.writeDouble(coords[4]);
                    dout.writeDouble(coords[5]);
                    break;
                case CLOSE:
                    break;
            }
        }
    }
}
