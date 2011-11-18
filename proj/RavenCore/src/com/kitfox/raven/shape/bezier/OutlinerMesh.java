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

package com.kitfox.raven.shape.bezier;

import com.kitfox.game.control.color.StrokeStyle.Cap;
import com.kitfox.game.control.color.StrokeStyle.Join;
import com.kitfox.raven.shape.bezier.BezierEdge.EdgeVisitor;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
@Deprecated
public class OutlinerMesh
{
//    BezierMesh mesh = new BezierMesh();
    BezierMesh mesh;
    final Cap cap;
    final Join join;
//    final double flatnessSquared;
    public static final String KEY_EDGE_WEIGHTS = "edgeWeights";

    public OutlinerMesh(Cap cap, Join join, double flatnessSquared)
    {
        this.cap = cap;
        this.join = join;
        mesh = new BezierMesh(flatnessSquared);
    }

    public void addCurve(BezierCurve curve, double weightStart, double weightEnd)
    {
//        mesh.addCurve(curve, new EdgeAdder(weightStart, weightEnd));
        mesh.addCurve(curve, null);
    }

    protected static double lerp(double v0, double v1, double t)
    {
        return (1 - t) * v0 + t * v1;
    }

    public void getContour()
    {
//        BezierFace face = mesh.getFaceOutside();
//        ContourBuilder builder = new ContourBuilder(face);
//        face.visitEdgesCCW(builder);
        
    }

    private static double square(double value)
    {
        return value * value;
    }

    //----------------------------------

    class ContourBuilder implements EdgeVisitor
    {
        ArrayList<BezierCurve> contour = new ArrayList<BezierCurve>();
        final BezierFace face;
        BezierEdge lastEdge;
        BezierEdge firstEdge;

        public ContourBuilder(BezierFace face)
        {
            this.face = face;
        }

        @Override
        public void visit(BezierEdge edge, boolean againstWinding)
        {
//            if (firstEdge == null)
//            {
//                firstEdge = edge;
//            }
//
//            EdgeWeights w = (EdgeWeights)edge.getData(KEY_EDGE_WEIGHTS);
//
//            double w0 = w.weightStart;
//            double w1 = w.weightEnd;
//            BezierCurve curve = edge.getCurve();
//
//            if (againstWinding)
//            {
//                curve = curve.reverse();
//                double tmp = w0;
//                w0 = w1;
//                w1 = tmp;
//            }
//
//            FlatSegmentList segList = curve.createOffset(w0, w1, edge.getFlatnessSquared());
//            BezierCurve curveOff = segList.fitCurve();
//
//            if (lastEdge != null)
//            {
//                BezierCurve lastCurve = contour.get(contour.size() - 1);
//                if (lastEdge == edge)
//                {
//                    addCap(lastCurve, curveOff);
//                }
//                else
//                {
//                    addJoin(lastCurve, curveOff);
//                }
//            }
//
//            lastEdge = edge;
//            contour.add(curveOff);
        }

        @Override
        public void finishedVisiting()
        {
            BezierCurve lastCurve = contour.get(contour.size() - 1);
            BezierCurve firstCurve = contour.get(0);
            if (lastEdge == firstEdge)
            {
                addCap(lastCurve, firstCurve);
            }
            else
            {
                addJoin(lastCurve, firstCurve);
            }
        }

        private void addCap(BezierCurve curve0, BezierCurve curve1)
        {
            int p0x = curve0.getEndX();
            int p0y = curve0.getEndY();
            int p1x = curve1.getStartX();
            int p1y = curve1.getStartY();

            double span = Math.sqrt(square(p0x - p1x) + square(p0y - p1y));

            //Normalize tan in
            double dx0 = curve0.getEndX() - curve0.getEndKnotX();
            double dy0 = curve0.getEndY() - curve0.getEndKnotY();
            double mag0I = 1 / Math.sqrt(dx0 * dx0 + dy0 * dy0);
            dx0 *= mag0I;
            dy0 *= mag0I;

            //Normalize tanOut
            double dx1 = curve0.getStartX() - curve1.getStartKnotX();
            double dy1 = curve0.getStartY() - curve1.getStartKnotY();
            double mag1I = 1 / Math.sqrt(dx1 * dx1 + dy1 * dy1);
            dx1 *= mag1I;
            dy1 *= mag1I;

            switch (cap)
            {
                case BUTT:
                {
                    BezierCurveLine curve = new BezierCurveLine(
                            p0x, p0y,
                            p1x, p1y);
                    contour.add(curve);
                    break;
                }
                case ROUND:
                {
                    BezierCurveCubic curve = new BezierCurveCubic(
                            p0x, p0y,
                            p0x + dx0 * span * 2 / 3, p0y + dy0 * span * 2 / 3,

                            p1x + dx1 * span * 2 / 3, p1y + dy1 * span * 2 / 3,
                            p1x, p1y);
                    contour.add(curve);
                    break;
                }
                case SQUARE:
                {
                    {
                        BezierCurveLine curve = new BezierCurveLine(
                                p0x, p0y,
                                p0x + dx0 * span / 2, p0y + dy0 * span / 2);
                        contour.add(curve);
                    }
                    {
                        BezierCurveLine curve = new BezierCurveLine(
                                p0x + dx0 * span / 2, p0y + dy0 * span / 2,
                                p1x + dx1 * span / 2, p1y + dy1 * span / 2
                                );
                        contour.add(curve);
                    }
                    {
                        BezierCurveLine curve = new BezierCurveLine(
                                p1x + dx1 * span / 2, p1y + dy1 * span / 2,
                                p1x, p1y);
                        contour.add(curve);
                    }
                    break;
                }
            }
        }

        private void addJoin(BezierCurve curve0, BezierCurve curve1)
        {
            int p0x = curve0.getEndX();
            int p0y = curve0.getEndY();
            int p1x = curve1.getStartX();
            int p1y = curve1.getStartY();

            double span = Math.sqrt(square(p0x - p1x) + square(p0y - p1y));

            //Normalize tan in
            double dx0 = curve0.getEndX() - curve0.getEndKnotX();
            double dy0 = curve0.getEndY() - curve0.getEndKnotY();
            double mag0I = 1 / Math.sqrt(dx0 * dx0 + dy0 * dy0);
            dx0 *= mag0I;
            dy0 *= mag0I;

            //Normalize tanOut
            double dx1 = curve0.getStartX() - curve1.getStartKnotX();
            double dy1 = curve0.getStartY() - curve1.getStartKnotY();
            double mag1I = 1 / Math.sqrt(dx1 * dx1 + dy1 * dy1);
            dx1 *= mag1I;
            dy1 *= mag1I;

            switch (join)
            {
                case BEVEL:
                {
                    BezierCurveLine curve = new BezierCurveLine(
                            p0x, p0y,
                            p1x, p1y);
                    contour.add(curve);
                    break;
                }
                case ROUND:
                {
                    //TODO: Incorrect - should scale to angle of tangent with base
                    BezierCurveCubic curve = new BezierCurveCubic(
                            p0x, p0y,
                            p0x + dx0 * span * 1 / 3, p0y + dy0 * span * 1 / 3,

                            p1x + dx1 * span * 1 / 3, p1y + dy1 * span * 1 / 3,
                            p1x, p1y);
                    contour.add(curve);
                    break;
                }
                case MITER:
                {
                    double[] time = BezierMath.intersectLines(
                            p0x, p0y, dx0, dy0,
                            p1x, p1y, dx1, dy1,
                            null);

                    if (time[0] <= 0 || time[1] <= 0)
                    {
                        //If tangents are negative, do not miter
                        BezierCurveLine curve = new BezierCurveLine(
                                p0x, p0y,
                                p1x, p1y);
                        contour.add(curve);
                        break;
                    }

                    {
                        BezierCurveLine curve = new BezierCurveLine(
                                p0x, p0y,
                                p0x + dx0 * time[0], p0y + dy0 * time[0]);
                        contour.add(curve);
                    }
                    {
                        BezierCurveLine curve = new BezierCurveLine(
                                p0x + dx0 * time[0], p0y + dy0 * time[0],
                                p1x, p1y);
                        contour.add(curve);
                    }
                    break;
                }
            }
        }


    }

    public static class EdgeWeights
    {
        final double weightStart;
        final double weightEnd;

        public EdgeWeights(double weightStart, double weightEnd)
        {
            this.weightStart = weightStart;
            this.weightEnd = weightEnd;
        }
    }

//    class EdgeAdder
//        implements BezierMesh.NetworkUpdateCallback
//    {
//        final double weightStart;
//        final double weightEnd;
//
//        EdgeWeights replaceWeights;
//
//        public EdgeAdder(double weightStart, double weightEnd)
//        {
//            this.weightStart = weightStart;
//            this.weightEnd = weightEnd;
//        }
//
////        @Override
////        public void startReplaceExistingEdge(BezierEdge edge)
////        {
//////            replaceWeights = (EdgeWeights)edge.getData(KEY_EDGE_WEIGHTS);
////        }
////
////        @Override
////        public void replaceExistingEdge(double tOffset, double tSpan, BezierEdge edge)
////        {
//////            edge.setData(KEY_EDGE_WEIGHTS, new EdgeWeights(
//////                    lerp(replaceWeights.weightStart, replaceWeights.weightEnd, tOffset),
//////                    lerp(replaceWeights.weightStart, replaceWeights.weightEnd, tOffset + tSpan)
//////                    ));
////        }
////
////        @Override
////        public void endReplaceExistingEdge()
////        {
////            replaceWeights = null;
////        }
//
//        @Override
//        public void addedEdge(double tOffset, double tSpan, BezierEdge edge)
//        {
////            edge.setData(KEY_EDGE_WEIGHTS, new EdgeWeights(
////                    lerp(weightStart, weightEnd, tOffset),
////                    lerp(weightStart, weightEnd, tOffset + tSpan)
////                    ));
//        }
//    }
}
