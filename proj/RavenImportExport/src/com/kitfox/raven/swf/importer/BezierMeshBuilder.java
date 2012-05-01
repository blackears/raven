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

package com.kitfox.raven.swf.importer;

import com.kitfox.raven.shape.bezier.BezierCurveCubic;
import com.kitfox.raven.shape.bezier.BezierCurveLine;
import com.kitfox.raven.shape.bezier.BezierCurveQuadratic;
import com.kitfox.raven.shape.bezier.BezierEdge;
import com.kitfox.raven.shape.bezier.BezierMesh;
import com.kitfox.raven.shape.bezier.BezierNetwork.NetworkUpdateCallback;
import com.kitfox.raven.shape.mesh.MeshCurvesVisitor;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class BezierMeshBuilder
        implements MeshCurvesVisitor, NetworkUpdateCallback
{
    int paintLeft;
    int paintRight;
    int paintLine;
    int strokeLine;

    int px;
    int py;

//    HashMap<Integer, ArrayList<Segment>> fillPaths = new HashMap<Integer, ArrayList<Segment>>();
//    HashMap<LineStyle, ArrayList<Segment>> linePaths = new HashMap<LineStyle, ArrayList<Segment>>();

    BezierMesh mesh = new BezierMesh(10000);
    ArrayList<PaintRecord> paintRecords = new ArrayList<PaintRecord>();


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
    public void moveTo(int px, int py)
    {
        this.px = px;
        this.py = py;
    }

    @Override
    public void lineTo(int ex, int ey)
    {
        //Convert to absolute coords
        ex += px;
        ey += py;

        BezierCurveLine curve = new BezierCurveLine(px, py, ex, ey);
        mesh.addCurve(curve, this);
//        addSeg(new Segment(px, py, ex, ey));
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

//        addSeg(new Segment(px, py, kx0, ky0, ex, ey));
        BezierCurveQuadratic curve = new BezierCurveQuadratic(px, py, kx0, ky0, ex, ey);
        mesh.addCurve(curve, this);
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

//        addSeg(new Segment(px, py, kx0, ky0, kx1, ky1, ex, ey));
        BezierCurveCubic curve = new BezierCurveCubic(px, py, kx0, ky0, kx1, ky1, ex, ey);
        mesh.addCurve(curve, this);
        px = ex;
        py = ey;
    }

    @Override
    public void addedEdge(double tOffset, double tSpan, BezierEdge edge)
    {
        PaintRecord rec =
                new PaintRecord(paintLeft, paintRight, paintLine, strokeLine, edge);
        paintRecords.add(rec);
    }

    //----------------------------------------
    class PaintRecord
    {
        int paintLeft;
        int paintRight;
        int paintLine;
        int strokeLine;
        BezierEdge edge;

        public PaintRecord(int paintLeft, int paintRight, int paintLine, int strokeLine, BezierEdge edge)
        {
            this.paintLeft = paintLeft;
            this.paintRight = paintRight;
            this.paintLine = paintLine;
            this.strokeLine = strokeLine;
            this.edge = edge;
        }

    }
}
