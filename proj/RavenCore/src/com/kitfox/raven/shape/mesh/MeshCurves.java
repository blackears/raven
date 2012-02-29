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

package com.kitfox.raven.shape.mesh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author kitfox
 */
@Deprecated
public class MeshCurves
{
    private final MeshSegment[] curves;

    public MeshCurves(MeshSegment... curves)
    {
        this.curves = curves;
    }

    public static MeshCurves create(String text)
    {
        ArrayList<MeshSegment> entries = new ArrayList<MeshSegment>();

        Matcher m = Pattern.compile("[Mlhvqcdefg][-0-9 ]*").matcher(text);
        while (m.find())
        {
            String strn = m.group();
            switch (strn.charAt(0))
            {
                case 'M':
                    entries.add(MeshSegMoveTo.create(strn));
                    break;
                case 'l':
                    entries.add(MeshSegLineTo.create(strn));
                    break;
                case 'h':
                    entries.add(MeshSegLineTo.create(strn));
                    break;
                case 'v':
                    entries.add(MeshSegLineTo.create(strn));
                    break;
                case 'q':
                    entries.add(MeshSegQuadTo.create(strn));
                    break;
                case 'c':
                    entries.add(MeshSegCubicTo.create(strn));
                    break;
                case 'd':
                    entries.add(MeshSegPaintLeft.create(strn));
                    break;
                case 'e':
                    entries.add(MeshSegPaintRight.create(strn));
                    break;
                case 'f':
                    entries.add(MeshSegPaintLine.create(strn));
                    break;
                case 'g':
                    entries.add(MeshSegStrokeLine.create(strn));
                    break;
            }
        }

        return new MeshCurves(entries.toArray(new MeshSegment[entries.size()]));
    }

    /**
     * @return the curves
     */
    public MeshSegment[] getCurves()
    {
        return curves.clone();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < curves.length; ++i)
        {
            sb.append(curves[i].toString());
        }

        return sb.toString();
    }

    public void visit(MeshCurvesVisitor visitor)
    {
        for (MeshSegment seg: curves)
        {
            seg.visit(visitor);
        }
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MeshCurves other = (MeshCurves) obj;
        if (!Arrays.deepEquals(this.curves, other.curves)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Arrays.deepHashCode(this.curves);
        return hash;
    }

    //------------------------------

    public static class Builder
    {
        ArrayList<MeshSegment> entries = new ArrayList<MeshSegment>();

        public MeshCurves toCurves()
        {
            return new MeshCurves(entries.toArray(new MeshSegment[entries.size()]));
        }

        public void paintLeft(int id)
        {
            entries.add(new MeshSegPaintLeft(id));
        }

        public void paintRight(int id)
        {
            entries.add(new MeshSegPaintRight(id));
        }

        public void paintLine(int id)
        {
            entries.add(new MeshSegPaintLine(id));
        }

        public void strokeLine(int id)
        {
            entries.add(new MeshSegStrokeLine(id));
        }

        public void moveTo(int px, int py)
        {
            entries.add(new MeshSegMoveTo(px, py));
        }

        public void lineToRel(int ex, int ey)
        {
            entries.add(new MeshSegLineTo(ex, ey));
        }

        public void quadToRel(int kx0, int ky0, int ex, int ey)
        {
            entries.add(new MeshSegQuadTo(kx0, ky0, ex, ey));
        }

        public void cubicToRel(int kx0, int ky0, int kx1, int ky1, int ex, int ey)
        {
            entries.add(new MeshSegCubicTo(kx0, ky0, kx1, ky1, ex, ey));
        }
    }
}
