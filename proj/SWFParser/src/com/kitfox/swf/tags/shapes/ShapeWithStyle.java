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

package com.kitfox.swf.tags.shapes;

import com.kitfox.swf.dataType.SWFDataReader;
import java.awt.geom.Path2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class ShapeWithStyle
{

    private FillStyleArray fillStyles;
    private LineStyleArray lineStyles;
    private int numFillBits;
    private int numLineBits;
    private ArrayList<ShapeRecord> shapeRecords = new ArrayList<ShapeRecord>();

    public ShapeWithStyle(SWFDataReader in, int shapeType) throws IOException
    {
        fillStyles = new FillStyleArray(in, shapeType);
        lineStyles = new LineStyleArray(in, shapeType);

        numFillBits = (int)in.getUB(4);
        numLineBits = (int)in.getUB(4);

        while (true)
        {
            ShapeRecord record = ShapeRecord.create(in, shapeType, this);
            if (record == null)
            {
                break;
            }
            shapeRecords.add(record);
//            in.flushToByteBoundary();
        }

        in.flushToByteBoundary();
    }

    /**
     * @return the fillStyles
     */
    public FillStyleArray getFillStyles() {
        return fillStyles;
    }

    /**
     * @return the lineStyles
     */
    public LineStyleArray getLineStyles() {
        return lineStyles;
    }

    /**
     * @return the numFillBits
     */
    public int getNumFillBits() {
        return numFillBits;
    }

    /**
     * @return the numLineBits
     */
    public int getNumLineBits() {
        return numLineBits;
    }

    /**
     * @return the shapeRecords
     */
    public ArrayList<ShapeRecord> getShapeRecords()
    {
        return new ArrayList<ShapeRecord>(shapeRecords);
    }

    public void buildShapes(ShapeVisitor visitor)
    {
        FillStyleArray fillStyleArr = fillStyles;
        LineStyleArray lineStyleArr = lineStyles;

        for (int i = 0; i < shapeRecords.size(); ++i)
        {
            ShapeRecord rec = shapeRecords.get(i);
            if (rec instanceof StyleChangeRecord)
            {
                StyleChangeRecord scr = (StyleChangeRecord)rec;
                if (scr.newStyles)
                {
                    fillStyleArr = scr.fillStyles;
                    lineStyleArr = scr.lineStyles;
                }
                if (scr.fill0Style)
                {
                    visitor.setFillStyleLeft(
                            fillStyleArr.get(scr.fillStyle0Idx));
                }
                if (scr.fill1Style)
                {
                    visitor.setFillStyleRight(
                            fillStyleArr.get(scr.fillStyle1Idx));
                }
                if (scr.lineStyle)
                {
                    visitor.setLineStyle(
                            lineStyleArr.get(scr.lineStyleIdx));
                }
                if (scr.stateMoveTo)
                {
                    visitor.moveTo(scr.moveDx, scr.moveDy);
                }
            }
            else if (rec instanceof StraightEdgeRecord)
            {
                StraightEdgeRecord ser = (StraightEdgeRecord)rec;

                if (ser.generalLine)
                {
                    visitor.lineTo(ser.dx, ser.dy);
                }
                else if (ser.vertLine)
                {
                    visitor.lineTo(0, ser.dy);
                }
                else
                {
                    visitor.lineTo(ser.dx, 0);
                }
            }
            else if (rec instanceof CurvedEdgeRecord)
            {
                CurvedEdgeRecord ser = (CurvedEdgeRecord)rec;
                visitor.quadTo(ser.cdx, ser.cdy, 
                        ser.cdx + ser.adx, ser.cdy + ser.ady);
            }
        }
        visitor.finishedVisitingShape();
    }

    /**
     * Build a set of Path2D strokes and filled areas based on the shape mesh.
     * These can be used directly by the Java2D API to render this mesh.
     *
     * @return Set of strokes parsed from the mesh.
     */
    public ShapeInfo buildShapes()
    {
        FillStyleArray fillStyleArr = fillStyles;
        LineStyleArray lineStyleArr = lineStyles;

        //Style arrays indexed starting at 1.  0 indicates no style.
        int fillIdx0 = 0;
        int fillIdx1 = 0;
        int lineIdx = 0;

        int px = 0;
        int py = 0;

        HashMap<FillStyle, ArrayList<Segment>> fillPaths = new HashMap<FillStyle, ArrayList<Segment>>();
        HashMap<LineStyle, ArrayList<Segment>> linePaths = new HashMap<LineStyle, ArrayList<Segment>>();

        for (int i = 0; i < shapeRecords.size(); ++i)
        {
            ShapeRecord rec = shapeRecords.get(i);
            if (rec instanceof StyleChangeRecord)
            {
                StyleChangeRecord scr = (StyleChangeRecord)rec;
                if (scr.newStyles)
                {
                    fillStyleArr = scr.fillStyles;
                    lineStyleArr = scr.lineStyles;
                }
                if (scr.fill0Style)
                {
                    fillIdx0 = scr.fillStyle0Idx;
                }
                if (scr.fill1Style)
                {
                    fillIdx1 = scr.fillStyle1Idx;
                }
                if (scr.lineStyle)
                {
                    lineIdx = scr.lineStyleIdx;
                }
                if (scr.stateMoveTo)
                {
//                    px += scr.moveDx;
//                    py += scr.moveDy;
                    px = scr.moveDx;
                    py = scr.moveDy;
                }
            }
            else if (rec instanceof StraightEdgeRecord)
            {
                StraightEdgeRecord ser = (StraightEdgeRecord)rec;
                int ex = px;
                int ey = py;

                if (ser.generalLine)
                {
                    ex += ser.dx;
                    ey += ser.dy;
                }
                else if (ser.vertLine)
                {
                    ey += ser.dy;
                }
                else
                {
                    ex += ser.dx;
                }

                if (fillIdx0 != 0)
                {
                    FillStyle style = fillStyleArr.get(fillIdx0);

                    ArrayList<Segment> segList = fillPaths.get(style);
                    if (segList == null)
                    {
                        segList = new ArrayList<Segment>();
                        fillPaths.put(style, segList);
                    }

                    Segment seg = new Segment(px, py, ex, ey);
                    segList.add(seg);
                }
                if (fillIdx1 != 0)
                {
                    FillStyle style = fillStyleArr.get(fillIdx1);

                    ArrayList<Segment> segList = fillPaths.get(style);
                    if (segList == null)
                    {
                        segList = new ArrayList<Segment>();
                        fillPaths.put(style, segList);
                    }

                    //Counter clockwise winding
                    Segment seg = new Segment(ex, ey, px, py);
                    segList.add(seg);
                }
                if (lineIdx != 0)
                {
                    LineStyle style = lineStyleArr.get(lineIdx);

                    ArrayList<Segment> segList = linePaths.get(style);
                    if (segList == null)
                    {
                        segList = new ArrayList<Segment>();
                        linePaths.put(style, segList);
                    }

                    Segment seg = new Segment(px, py, ex, ey);
                    segList.add(seg);
                }

                px = ex;
                py = ey;
            }
            else if (rec instanceof CurvedEdgeRecord)
            {
                CurvedEdgeRecord ser = (CurvedEdgeRecord)rec;
                int kx = px + ser.cdx;
                int ky = py + ser.cdy;
                int ex = px + ser.cdx + ser.adx;
                int ey = py + ser.cdy + ser.ady;

                if (fillIdx0 != 0)
                {
                    FillStyle style = fillStyleArr.get(fillIdx0);

                    ArrayList<Segment> segList = fillPaths.get(style);
                    if (segList == null)
                    {
                        segList = new ArrayList<Segment>();
                        fillPaths.put(style, segList);
                    }

                    Segment seg = new Segment(px, py, kx, ky, ex, ey);
                    segList.add(seg);
                }
                if (fillIdx1 != 0)
                {
                    FillStyle style = fillStyleArr.get(fillIdx1);

                    ArrayList<Segment> segList = fillPaths.get(style);
                    if (segList == null)
                    {
                        segList = new ArrayList<Segment>();
                        fillPaths.put(style, segList);
                    }

                    //Counter clockwise winding
                    Segment seg = new Segment(ex, ey, kx, ky, px, py);
                    segList.add(seg);
                }
                if (lineIdx != 0)
                {
                    LineStyle style = lineStyleArr.get(lineIdx);

                    ArrayList<Segment> segList = linePaths.get(style);
                    if (segList == null)
                    {
                        segList = new ArrayList<Segment>();
                        linePaths.put(style, segList);
                    }

                    Segment seg = new Segment(ex, ey, kx, ky, px, py);
                    segList.add(seg);
                }

                px = ex;
                py = ey;
            }
        }

        ShapeInfo info = new ShapeInfo();
        for (FillStyle style: fillPaths.keySet())
        {
            Path2D.Float path = new Path2D.Float();

            ArrayList<Segment> contourList = buildContours(fillPaths.get(style));
            for (Segment seg: contourList)
            {
                seg.appendContour(path);
            }
            info.getContours().put(style, path);
        }
        for (LineStyle style: linePaths.keySet())
        {
            Path2D.Float path = new Path2D.Float();

            ArrayList<Segment> contourList = buildContours(linePaths.get(style));
            for (Segment seg: contourList)
            {
                seg.appendContour(path);
            }
            info.getStrokes().put(style, path);
        }
        return info;
    }

    private ArrayList<Segment> buildContours(ArrayList<Segment> segs)
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

    /**
     * @param numFillBits the numFillBits to set
     */
    public void setNumFillBits(int numFillBits) {
        this.numFillBits = numFillBits;
    }

    /**
     * @param numLineBits the numLineBits to set
     */
    public void setNumLineBits(int numLineBits) {
        this.numLineBits = numLineBits;
    }

    //----------------------

    /**
     * Scan through shape exporting records for lines and changes in paint
     * or stroke.  Line units are in TWIPS (ie, 1/20 of a pixel).
     */
    public static interface ShapeVisitor
    {
        public void setFillStyleLeft(FillStyle fillStyle);
        public void setFillStyleRight(FillStyle fillStyle);
        public void setLineStyle(LineStyle lineStyle);

        /**
         * Move cursor to point in absolute coords
         * @param x
         * @param y
         */
        public void moveTo(int x, int y);

        /**
         * Draw line to point in relative coords
         * @param x
         * @param y
         */
        public void lineTo(int x, int y);
        
        /**
         * Draw quadratic to point in relative coords
         * @param x
         * @param y
         */
        public void quadTo(int kx, int ky, int x, int y);

        public void finishedVisitingShape();
    }
    
    class Segment
    {
        int startX;
        int startY;
        int knotX;
        int knotY;
        int endX;
        int endY;
        boolean hasKnot;

        Segment next;
        Segment prev;

        public Segment(int startX, int startY, int knotX, int knotY, int endX, int endY)
        {
            this.startX = startX;
            this.startY = startY;
            this.knotX = knotX;
            this.knotY = knotY;
            this.endX = endX;
            this.endY = endY;
            this.hasKnot = true;
        }

        public Segment(int startX, int startY, int endX, int endY)
        {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.hasKnot = false;
        }

        private void appendContour(Path2D.Float path)
        {
            path.moveTo(startX, startY);
            appendContour(path, this);
        }

        private void appendContour(Path2D.Float path, Segment head)
        {
            if (hasKnot)
            {
                path.quadTo(knotX, knotY, endX, endY);
            }
            else
            {
                path.lineTo(endX, endY);
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
    }

    public class ShapeInfo
    {
        private HashMap<FillStyle, Path2D.Float> contours = new HashMap<FillStyle, Path2D.Float>();
        private HashMap<LineStyle, Path2D.Float> strokes = new HashMap<LineStyle, Path2D.Float>();

        /**
         * @return the contours
         */
        public HashMap<FillStyle, Path2D.Float> getContours()
        {
            return contours;
        }

        /**
         * @return the strokes
         */
        public HashMap<LineStyle, Path2D.Float> getStrokes()
        {
            return strokes;
        }
    }
}
