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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Calculate a structure that represents the stroke and shape areas of the
 * mesh as paths.  These can be used by Java2D to render.
 *
 * @author kitfox
 */
public class MeshContourBuilder implements MeshCurvesVisitor
{
    int paintLeft;
    int paintRight;
    int paintLine;
    int strokeLine;

    int px;
    int py;
    
    HashMap<Integer, ArrayList<Segment>> fillPaths = new HashMap<Integer, ArrayList<Segment>>();
    HashMap<LineStyle, ArrayList<Segment>> linePaths = new HashMap<LineStyle, ArrayList<Segment>>();

    ShapeInfo shapeInfo = new ShapeInfo();

    private MeshContourBuilder()
    {
    }

    public static ShapeInfo build(MeshCurves curves)
    {
        MeshContourBuilder builder = new MeshContourBuilder();
        curves.visit(builder);
        builder.buildShapes();
        return builder.shapeInfo;
    }

    /**
     * Build a set of Path2D strokes and filled areas based on the shape mesh.
     * These can be used directly by the Java2D API to render this mesh.
     *
     * @return
     */
    public void buildShapes()
    {
        AffineTransform toPixels = new AffineTransform();
        toPixels.setToScale(.01, .01);

        for (Integer style: fillPaths.keySet())
        {
            Path2D.Double path = new Path2D.Double();

            ArrayList<Segment> contourList = buildContours(fillPaths.get(style));
            for (Segment seg: contourList)
            {
                seg.appendContour(path);
            }
            shapeInfo.contours.put(style,
                    toPixels.createTransformedShape(path));
        }

        for (LineStyle style: linePaths.keySet())
        {
            Path2D.Double path = new Path2D.Double();

            ArrayList<Segment> contourList = buildContours(linePaths.get(style));
            for (Segment seg: contourList)
            {
                seg.appendContour(path);
            }
            shapeInfo.strokes.put(style,
                    toPixels.createTransformedShape(path));
        }
    }

    @Override
    public void paintLeft(int id)
    {
        this.paintLeft = id;
    }

    @Override
    public void paintRight(int id)
    {
        this.paintRight = id;
    }

    @Override
    public void paintLine(int id)
    {
        this.paintLine = id;
    }

    @Override
    public void strokeLine(int id)
    {
        this.strokeLine = id;
    }

    @Override
    public void moveTo(int x, int y)
    {
        this.px = x;
        this.py = y;
    }

    private void addSeg(Segment seg)
    {
        if (paintLeft != 0)
        {
            ArrayList<Segment> segList = fillPaths.get(paintLeft);
            if (segList == null)
            {
                segList = new ArrayList<Segment>();
                fillPaths.put(paintLeft, segList);
            }

            segList.add(seg);
        }

        if (paintRight != 0)
        {
            ArrayList<Segment> segList = fillPaths.get(paintRight);
            if (segList == null)
            {
                segList = new ArrayList<Segment>();
                fillPaths.put(paintRight, segList);
            }

            segList.add(seg.reverse());
        }

        if (paintLine != 0 && strokeLine != 0)
        {
            LineStyle style = new LineStyle(paintLine, 0, strokeLine);

            ArrayList<Segment> segList = linePaths.get(style);
            if (segList == null)
            {
                segList = new ArrayList<Segment>();
                linePaths.put(style, segList);
            }

            segList.add(seg);
        }

    }

    @Override
    public void lineTo(int ex, int ey)
    {
        //Convert to absolute coords
        ex += px;
        ey += py;

        addSeg(new Segment(px, py, ex, ey));
        px = ex;
        py = ey;
    }

    @Override
    public void quadTo(int kx0, int ky0, int ex, int ey)
    {
        //Convert to absolute coords
        ex += px;
        ey += py;
        kx0 += px;
        ky0 += py;

        addSeg(new Segment(px, py, kx0, ky0, ex, ey));
        px = ex;
        py = ey;
    }

    @Override
    public void cubicTo(int kx0, int ky0, int kx1, int ky1, int ex, int ey)
    {
        //Convert to absolute coords
        ex += px;
        ey += py;
        kx0 += px;
        ky0 += py;
        kx1 += px;
        ky1 += py;

        addSeg(new Segment(px, py, kx0, ky0, kx1, ky1, ex, ey));
        px = ex;
        py = ey;
    }


    private ArrayList<Segment> buildContours(List<Segment> segs)
    {
        ArrayList<Segment> segsToConnect = new ArrayList<Segment>(segs);
        ArrayList<Segment> contourList = new ArrayList<Segment>();

        while (!segsToConnect.isEmpty())
        {
            Segment headSeg = segsToConnect.remove(segsToConnect.size() - 1);
            Segment tailSeg = headSeg;

            int numSegsAtStart;

            do
            {
                numSegsAtStart = segsToConnect.size();

                for (int i = 0; i < segsToConnect.size(); ++i)
                {
                    Segment scanSeg = segsToConnect.get(i);
                    if (scanSeg.startX == headSeg.endX && scanSeg.startY == headSeg.endY)
                    {
                        headSeg.next = scanSeg;
                        scanSeg.prev = headSeg;
                        headSeg = scanSeg;
                        segsToConnect.remove(i);
                    }
                    else if (scanSeg.endX == tailSeg.startX && scanSeg.endY == tailSeg.startY)
                    {
                        tailSeg.prev = scanSeg;
                        scanSeg.next = tailSeg;
                        tailSeg = scanSeg;
                        segsToConnect.remove(i);
                    }
                }
            } while (segsToConnect.size() != numSegsAtStart);

            if (headSeg.endX == tailSeg.startX && headSeg.endY == tailSeg.startY)
            {
                headSeg.next = tailSeg;
                tailSeg.prev = headSeg;
            }
            contourList.add(tailSeg);
        }

        return contourList;
    }

    class Segment
    {
        int startX;
        int startY;
        int knotX0;
        int knotY0;
        int knotX1;
        int knotY1;
        int endX;
        int endY;
        int numKnots;

        Segment next;
        Segment prev;

        public Segment(int startX, int startY, int knotX0, int knotY0, int knotX1, int knotY1, int endX, int endY)
        {
            this.startX = startX;
            this.startY = startY;
            this.knotX0 = knotX0;
            this.knotY0 = knotY0;
            this.knotX1 = knotX1;
            this.knotY1 = knotY1;
            this.endX = endX;
            this.endY = endY;
            this.numKnots = 1;
        }

        public Segment(int startX, int startY, int knotX0, int knotY0, int endX, int endY)
        {
            this.startX = startX;
            this.startY = startY;
            this.knotX0 = knotX0;
            this.knotY0 = knotY0;
            this.endX = endX;
            this.endY = endY;
            this.numKnots = 1;
        }

        public Segment(int startX, int startY, int endX, int endY)
        {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.numKnots = 0;
        }

        public Segment reverse()
        {
            switch (numKnots)
            {
                case 2:
                    return new Segment(
                            endX, endY,
                            knotX1, knotY1,
                            knotX0, knotY0,
                            startX, startY
                            );
                case 1:
                    return new Segment(
                            endX, endY,
                            knotX0, knotY0,
                            startX, startY
                            );
                case 0:
                    return new Segment(
                            endX, endY,
                            startX, startY
                            );
            }
            return null;
        }

        private void appendContour(Path2D.Double path)
        {
            path.moveTo(startX, startY);
            appendContour(path, this);
        }

        private void appendContour(Path2D.Double path, Segment head)
        {
            switch (numKnots)
            {
                case 2:
                    path.curveTo(knotX0, knotY0, knotX1, knotY1, endX, endY);
                    break;
                case 1:
                    path.quadTo(knotX0, knotY0, endX, endY);
                    break;
                case 0:
                    path.lineTo(endX, endY);
                    break;
            }

            if (next == head)
            {
                path.closePath();
                return;
            }
            if (next != null)
            {
                next.appendContour(path, head);
            }
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append("(").append(startX).append(" ").append(startY).append(")");
            if (numKnots >= 1)
            {
                sb.append(" (").append(knotX0).append(" ").append(knotY0).append(")");
            }
            if (numKnots >= 2)
            {
                sb.append(" (").append(knotX1).append(" ").append(knotY1).append(")");
            }
            sb.append(" (").append(endX).append(" ").append(endY).append(")");
            return sb.toString();
        }
    }

    public static class FillStyle
    {
        private final int paint;
        private final int paintLayout;

        public FillStyle(int paint, int paintLayout)
        {
            this.paint = paint;
            this.paintLayout = paintLayout;
        }

        /**
         * @return the paint
         */
        public int getPaint()
        {
            return paint;
        }

        /**
         * @return the paintLayout
         */
        public int getPaintLayout()
        {
            return paintLayout;
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
            final FillStyle other = (FillStyle) obj;
            if (this.paint != other.paint)
            {
                return false;
            }
            if (this.paintLayout != other.paintLayout)
            {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode()
        {
            int hash = 7;
            hash = 67 * hash + this.paint;
            hash = 67 * hash + this.paintLayout;
            return hash;
        }

    }

    /**
     * A (paint, stroke) tuple for describing the appearance of stroked lines.
     */
    public static class LineStyle
    {
        private final int paint;
        private final int paintLayout;
        private final int stroke;

        public LineStyle(int paint, int paintLayout, int stroke)
        {
            this.paint = paint;
            this.paintLayout = paintLayout;
            this.stroke = stroke;
        }

        /**
         * @return the paint
         */
        public int getPaint() {
            return paint;
        }

        /**
         * @return the stroke
         */
        public int getStroke() {
            return stroke;
        }

        /**
         * @return the paintLayout
         */
        public int getPaintLayout()
        {
            return paintLayout;
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
            final LineStyle other = (LineStyle) obj;
            if (this.paint != other.paint)
            {
                return false;
            }
            if (this.paintLayout != other.paintLayout)
            {
                return false;
            }
            if (this.stroke != other.stroke)
            {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode()
        {
            int hash = 7;
            hash = 89 * hash + this.paint;
            hash = 89 * hash + this.paintLayout;
            hash = 89 * hash + this.stroke;
            return hash;
        }

    }

    /**
     * A structure containing computed paths.
     */
    public static class ShapeInfo
    {
        private HashMap<Integer, Shape> contours = new HashMap<Integer, Shape>();
        private HashMap<LineStyle, Shape> strokes = new HashMap<LineStyle, Shape>();

        /**
         * @return the contours
         */
        public HashMap<Integer, Shape> getContours()
        {
            return new HashMap(contours);
        }

        /**
         * @return the strokes
         */
        public HashMap<LineStyle, Shape> getStrokes()
        {
            return new HashMap(strokes);
        }

    }
}
