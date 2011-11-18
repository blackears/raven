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
import com.kitfox.raven.editor.node.scene.RavenNodeMeshStatic.FaceLeftProxy;
import com.kitfox.raven.editor.node.scene.RavenNodeMeshStatic.FaceRightProxy;
import com.kitfox.raven.editor.paint.RavenPaintNone;
import com.kitfox.raven.editor.paint.RavenPaintProxy;
import com.kitfox.raven.editor.paintLayout.PaintLayoutProxy;
import com.kitfox.raven.editor.stroke.RavenStrokeInline;
import com.kitfox.raven.editor.stroke.RavenStrokeNone;
import com.kitfox.raven.editor.stroke.RavenStrokeProxy;
import com.kitfox.raven.shape.path.PathCurve;
import com.kitfox.raven.util.planeData.PlaneDataProvider;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class MeshStaticBuilder extends MeshBuilderBase
{
    PathCurve pathCurve;
    Path2D.Double path = new Path2D.Double();

    ArrayList<PaintRecord> records = new ArrayList<PaintRecord>();

    HashMap<Class<? extends PlaneDataProvider>, ArrayList> faceMap =
            new HashMap<Class<? extends PlaneDataProvider>, ArrayList>();
    HashMap<Class<? extends PlaneDataProvider>, ArrayList> edgeMap =
            new HashMap<Class<? extends PlaneDataProvider>, ArrayList>();

    @Override
    public void moveTo(int x, int y)
    {
        //Convert TWIPS to centipixels
        x = x * 5;
        y = y * 5;

        path.moveTo(x, y);
        px = x;
        py = y;
    }

    @Override
    public void lineTo(int x, int y)
    {
        //Convert TWIPS to centipixels
        x = px + x * 5;
        y = py + y * 5;

//        BezierCurveLine curve = new BezierCurveLine(px, py, x, y);
//        mesh.addCurve(curve, this);
        path.lineTo(x, y);
        records.add(new PaintRecord(paintLeft, paintRight, paintLine, strokeLine));

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

//        BezierCurveQuadratic curve =
//                new BezierCurveQuadratic(px, py, kx, ky, x, y);
//        mesh.addCurve(curve, this);
        path.quadTo(kx, ky, x, y);
        records.add(new PaintRecord(paintLeft, paintRight, paintLine, strokeLine));
        
        px = x;
        py = y;
    }

    @Override
    public void finishedVisitingShape()
    {
        pathCurve = new PathCurve(path);

        ArrayList<RavenPaintProxy> edgePaint = new ArrayList<RavenPaintProxy>();
        ArrayList<PaintLayoutProxy> edgeLayout = new ArrayList<PaintLayoutProxy>();
        ArrayList<RavenStrokeProxy> edgeStroke = new ArrayList<RavenStrokeProxy>();
        ArrayList<Integer> edgeFaceLeft = new ArrayList<Integer>();
        ArrayList<Integer> edgeFaceRight = new ArrayList<Integer>();

        ArrayList<RavenPaintProxy> facePaint = new ArrayList<RavenPaintProxy>();
        ArrayList<PaintLayoutProxy> faceLayout = new ArrayList<PaintLayoutProxy>();

        edgeMap.put(RavenPaintProxy.PlaneData.class, edgePaint);
        edgeMap.put(PaintLayoutProxy.PlaneData.class, edgeLayout);
        edgeMap.put(RavenStrokeProxy.PlaneData.class, edgeStroke);
        edgeMap.put(FaceLeftProxy.class, edgeFaceLeft);
        edgeMap.put(FaceRightProxy.class, edgeFaceRight);

        faceMap.put(RavenPaintProxy.PlaneData.class, facePaint);
        faceMap.put(PaintLayoutProxy.PlaneData.class, faceLayout);

        HashMap<PaintEntry, Integer> facePaints = new HashMap<PaintEntry, Integer>();
        for (PaintRecord rec: records)
        {
            if (rec.paintLeft != null)
            {
                Integer faceId = facePaints.get(rec.paintLeft);
                if (faceId == null)
                {
                    faceId = facePaints.size();
                    facePaints.put(rec.paintLeft, faceId);

                    facePaint.add(rec.getPaintLeftProxy());
                    faceLayout.add(rec.getLayoutLeftProxy());
                }
            }

            if (rec.paintRight != null)
            {
                Integer faceId = facePaints.get(rec.paintRight);
                if (faceId == null)
                {
                    faceId = facePaints.size();
                    facePaints.put(rec.paintRight, faceId);

                    facePaint.add(rec.getPaintRightProxy());
                    faceLayout.add(rec.getLayoutRightProxy());
                }
            }
        }

        for (PaintRecord rec: records)
        {
            edgePaint.add(rec.getPaintLineProxy());
            edgeLayout.add(rec.getLayoutLineProxy());
            edgeStroke.add(rec.getStrokeLineProxy());
            edgeFaceLeft.add(facePaints.get(rec.paintLeft));
            edgeFaceRight.add(facePaints.get(rec.paintRight));
        }
    }

    //---------------------------------------
    public class PaintRecord
    {
        PaintEntry paintLeft;
        PaintEntry paintRight;
        PaintEntry paintLine;
        RavenStrokeInline strokeLine;

        public PaintRecord(PaintEntry paintLeft, PaintEntry paintRight, PaintEntry paintLine, RavenStrokeInline strokeLine)
        {
            this.paintLeft = paintLeft;
            this.paintRight = paintRight;
            this.paintLine = paintLine;
            this.strokeLine = strokeLine;
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

        private PaintEntry[] getFacePaints()
        {
            return new PaintEntry[]{paintLeft, paintRight};
        }
    }

}
