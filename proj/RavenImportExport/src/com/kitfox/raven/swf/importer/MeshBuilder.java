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

import com.kitfox.game.control.color.PaintLayoutNone;
import com.kitfox.raven.editor.paint.RavenPaintNone;
import com.kitfox.raven.editor.paint.RavenPaintProxy;
import com.kitfox.raven.editor.paintLayout.PaintLayoutProxy;
import com.kitfox.raven.editor.stroke.RavenStrokeInline;
import com.kitfox.raven.editor.stroke.RavenStrokeNone;
import com.kitfox.raven.editor.stroke.RavenStrokeProxy;
import com.kitfox.raven.shape.bezier.BezierCurveLine;
import com.kitfox.raven.shape.bezier.BezierCurveQuadratic;
import com.kitfox.raven.shape.bezier.BezierEdge;
import com.kitfox.raven.shape.bezier.BezierFace;
import com.kitfox.raven.shape.bezier.BezierMesh;
import com.kitfox.raven.shape.bezier.BezierNetwork.NetworkUpdateCallback;
import com.kitfox.raven.shape.bezier.BezierVertex;
import com.kitfox.raven.shape.bezier.VertexSmooth;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class MeshBuilder extends MeshBuilderBase
        implements NetworkUpdateCallback
{
    private BezierMesh mesh = new BezierMesh(10000);
    ArrayList<PaintRecord> paintRecords = new ArrayList<PaintRecord>();

    @Override
    public void moveTo(int x, int y)
    {
        //Convert TWIPS to centipixels
        x = x * 5;
        y = y * 5;

        px = x;
        py = y;
    }

    @Override
    public void lineTo(int x, int y)
    {
        //Convert TWIPS to centipixels
        x = px + x * 5;
        y = py + y * 5;

        BezierCurveLine curve = new BezierCurveLine(px, py, x, y);
        mesh.addCurve(curve, this);
        px = x;
        py = y;
    }

    @Override
    public void quadTo(int kx, int ky, int x, int y)
    {
        //Convert TWIPS to centipixels
        kx = px + kx * 5;
        ky = py + ky * 5;
        x = px + x * 5;
        y = py + y * 5;

        BezierCurveQuadratic curve =
                new BezierCurveQuadratic(px, py, kx, ky, x, y);
        mesh.addCurve(curve, this);
        px = x;
        py = y;
    }

    @Override
    public void addedEdge(double tOffset, double tSpan, BezierEdge edge)
    {
        PaintRecord rec =
                new PaintRecord(paintLeft, paintRight, paintLine, strokeLine, edge);
        paintRecords.add(rec);
    }

    @Override
    public void finishedVisitingShape()
    {
        for (PaintRecord rec: paintRecords)
        {
            rec.edge.setData(RavenPaintProxy.PlaneData.class,
                    rec.getPaintLineProxy());
            rec.edge.setData(PaintLayoutProxy.PlaneData.class,
                    rec.getLayoutLineProxy());
            rec.edge.setData(RavenStrokeProxy.PlaneData.class,
                    rec.getStrokeLineProxy());

            BezierFace faceLeft = rec.edge.getFaceLeft();
            BezierFace faceRight = rec.edge.getFaceRight();

            faceLeft.setData(RavenPaintProxy.PlaneData.class,
                    rec.getPaintLeftProxy());
            faceLeft.setData(PaintLayoutProxy.PlaneData.class,
                    rec.getLayoutLeftProxy());

            faceRight.setData(RavenPaintProxy.PlaneData.class,
                    rec.getPaintRightProxy());
            faceRight.setData(PaintLayoutProxy.PlaneData.class,
                    rec.getLayoutRightProxy());
        }


        for (BezierVertex vtx: mesh.getVertices())
        {
            vtx.setData(VertexSmooth.PlaneData.class, VertexSmooth.CUSP);
        }
    }

    /**
     * @return the mesh
     */
    public BezierMesh getMesh()
    {
        return mesh;
    }

    //--------------------------------------
    public class PaintRecord
    {
        PaintEntry paintLeft;
        PaintEntry paintRight;
        PaintEntry paintLine;
        RavenStrokeInline strokeLine;
        BezierEdge edge;

        public PaintRecord(PaintEntry paintLeft, PaintEntry paintRight, PaintEntry paintLine, RavenStrokeInline strokeLine, BezierEdge edge)
        {
            this.paintLeft = paintLeft;
            this.paintRight = paintRight;
            this.paintLine = paintLine;
            this.strokeLine = strokeLine;
            this.edge = edge;
        }

        public RavenPaintProxy getPaintLeftProxy()
        {
            return paintLeft == null ? new RavenPaintProxy(RavenPaintNone.PAINT)
                    : new RavenPaintProxy(paintLeft.getPaint());
        }

        public RavenPaintProxy getPaintRightProxy()
        {
            return paintRight == null ? new RavenPaintProxy(RavenPaintNone.PAINT)
                    : new RavenPaintProxy(paintRight.getPaint());
        }

        public RavenPaintProxy getPaintLineProxy()
        {
            return paintLine == null ? new RavenPaintProxy(RavenPaintNone.PAINT)
                    : new RavenPaintProxy(paintLine.getPaint());
        }

        public PaintLayoutProxy getLayoutLeftProxy()
        {
            return paintLeft == null ? new PaintLayoutProxy(PaintLayoutNone.LAYOUT)
                    : new PaintLayoutProxy(paintLeft.getLayout());
        }

        public PaintLayoutProxy getLayoutRightProxy()
        {
            return paintRight == null ? new PaintLayoutProxy(PaintLayoutNone.LAYOUT)
                    : new PaintLayoutProxy(paintRight.getLayout());
        }

        public PaintLayoutProxy getLayoutLineProxy()
        {
            return paintLine == null ? new PaintLayoutProxy(PaintLayoutNone.LAYOUT)
                    : new PaintLayoutProxy(paintLine.getLayout());
        }

        public RavenStrokeProxy getStrokeLineProxy()
        {
            return strokeLine == null ? new RavenStrokeProxy(RavenStrokeNone.STROKE)
                    : new RavenStrokeProxy(strokeLine);
        }

    }

}
