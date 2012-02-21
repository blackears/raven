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

import com.kitfox.coyote.shape.CyPathIterator.Type;
import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author kitfox
 */
public class CyPath2d extends CyShape
{
    Type[] types;
    double[] values;

    int numTypes;
    int numValues;
    static final int ARRAY_INC = 20;

    public CyPath2d()
    {
        types = new Type[ARRAY_INC];
        values = new double[ARRAY_INC];
    }

    public static CyPath2d create(Shape shape)
    {
        CyPath2d path = new CyPath2d();

        double[] coords = new double[6];
        for (PathIterator it = shape.getPathIterator(null);  !it.isDone(); it.next())
        {
            switch (it.currentSegment(coords))
            {
                case PathIterator.SEG_MOVETO:
                    path.moveTo(coords[0], coords[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    path.lineTo(coords[0], coords[1]);
                    break;
                case PathIterator.SEG_QUADTO:
                    path.quadTo(coords[0], coords[1], coords[2], coords[3]);
                    break;
                case PathIterator.SEG_CUBICTO:
                    path.cubicTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
                    break;
                case PathIterator.SEG_CLOSE:
                    path.close();
                    break;
            }
        }

        return path;
    }

    public void allocEntry(int size)
    {
        if (numTypes + 1 >= types.length)
        {
            Type[] tmp = new Type[types.length + ARRAY_INC];
            System.arraycopy(types, 0, tmp, 0, types.length);
            types = tmp;
        }

        if (numValues + size >= values.length)
        {
            double[] tmp = new double[values.length + ARRAY_INC];
            System.arraycopy(values, 0, tmp, 0, values.length);
            values = tmp;
        }
    }

    public void moveTo(double x, double y)
    {
        allocEntry(2);
        types[numTypes + 0] = Type.MOVETO;
        values[numValues + 0] = x;
        values[numValues + 1] = y;
        ++numTypes;
        numValues += 2;
    }

    public void lineTo(double x, double y)
    {
        allocEntry(2);
        types[numTypes + 0] = Type.LINETO;
        values[numValues + 0] = x;
        values[numValues + 1] = y;
        ++numTypes;
        numValues += 2;
    }

    public void quadTo(double kx, double ky, double x, double y)
    {
        allocEntry(4);
        types[numTypes + 0] = Type.QUADTO;
        values[numValues + 0] = kx;
        values[numValues + 1] = ky;
        values[numValues + 2] = x;
        values[numValues + 3] = y;
        ++numTypes;
        numValues += 4;
    }

    public void cubicTo(double k0x, double k0y, double k1x, double k1y, double x, double y)
    {
        allocEntry(6);
        types[numTypes + 0] = Type.CUBICTO;
        values[numValues + 0] = k0x;
        values[numValues + 1] = k0y;
        values[numValues + 2] = k1x;
        values[numValues + 3] = k1y;
        values[numValues + 4] = x;
        values[numValues + 5] = y;
        ++numTypes;
        numValues += 6;
    }

    public void close()
    {
        allocEntry(0);
        types[numTypes + 0] = Type.CLOSE;
        ++numTypes;
    }

    @Override
    public CyPathIterator2d getIterator()
    {
        return new CyPathIterator2d();
    }

    @Override
    public CyRectangle2d getBounds()
    {
        //Aproximate with bounds of hull
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < numValues; i += 2)
        {
            minX = Math.min(minX, values[i]);
            maxX = Math.max(maxX, values[i]);
            minY = Math.min(minY, values[i + 1]);
            maxY = Math.max(maxY, values[i + 1]);
        }

        return new CyRectangle2d(minX, minY, maxX - minX, maxY - minY);
    }

    public void append(CyShape shape)
    {
        double[] coords = new double[6];
        for (CyPathIterator it = shape.getIterator(); it.hasNext();)
        {
            switch (it.next(coords))
            {
                case MOVETO:
                    moveTo(coords[0], coords[1]);
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
                    close();
                    break;
            }
        }
    }

    public int numParts()
    {
        return types.length;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final CyPath2d other = (CyPath2d) obj;
        if (this.numTypes != other.numTypes)
        {
            return false;
        }
        if (this.numValues != other.numValues)
        {
            return false;
        }
        for (int i = 0; i < numTypes; ++i)
        {
            if (types[i] != other.types[i])
            {
                return false;
            }
        }
        for (int i = 0; i < numValues; ++i)
        {
            if (values[i] != other.values[i])
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        for (int i = 0; i < numTypes; ++i)
        {
            hash = 29 * hash + types[i].ordinal();
        }
        for (int i = 0; i < numValues; ++i)
        {
            long val = Double.doubleToLongBits(values[i]);
            hash = 29 * hash + (int)(val ^ val >>> 32);
        }
        hash = 29 * hash + this.numTypes;
        hash = 29 * hash + this.numValues;
        return hash;
    }

    @Override
    public String toString()
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
                
        double[] coords = new double[6];
        
        for (CyPathIterator2d it = getIterator(); it.hasNext();)
        {
            switch (it.next(coords))
            {
                case MOVETO:
                    pw.print("m " + coords[0] + " " + coords[1]);
                    break;
                case LINETO:
                    pw.print("l " + coords[0] + " " + coords[1]);
                    break;
                case QUADTO:
                    pw.print("q " 
                            + coords[0] + " " + coords[1] + ", "
                            + coords[2] + " " + coords[3]
                            );
                    break;
                case CUBICTO:
                    pw.print("c " 
                            + coords[0] + " " + coords[1] + ", "
                            + coords[2] + " " + coords[3] + ", "
                            + coords[4] + " " + coords[5]
                            );
                    break;
                case CLOSE:
                    pw.print("z");
                    break;
            }
        }
        pw.close();
        
        return sw.toString();
    }

    public void dump(PrintStream ps)
    {
        ps.println(toString());
    }

    //------------------------------

    public class CyPathIterator2d implements CyPathIterator
    {
        int ptrType;
        int ptrValue;

        @Override
        public boolean hasNext()
        {
            return ptrType < numTypes;
        }

        @Override
        public Type next(double[] coords)
        {
            Type type = types[ptrType];
            ++ptrType;

            switch (type)
            {
                default:
                    break;
                case MOVETO:
                case LINETO:
                    coords[0] = values[ptrValue];
                    coords[1] = values[ptrValue + 1];
                    ptrValue += 2;
                    break;
                case QUADTO:
                    coords[0] = values[ptrValue];
                    coords[1] = values[ptrValue + 1];
                    coords[2] = values[ptrValue + 2];
                    coords[3] = values[ptrValue + 3];
                    ptrValue += 4;
                    break;
                case CUBICTO:
                    coords[0] = values[ptrValue];
                    coords[1] = values[ptrValue + 1];
                    coords[2] = values[ptrValue + 2];
                    coords[3] = values[ptrValue + 3];
                    coords[4] = values[ptrValue + 4];
                    coords[5] = values[ptrValue + 5];
                    ptrValue += 6;
                    break;
            }

            return type;
        }
    }
}
