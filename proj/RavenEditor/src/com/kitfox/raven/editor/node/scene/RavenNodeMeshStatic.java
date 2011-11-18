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

package com.kitfox.raven.editor.node.scene;

import com.kitfox.cache.CacheElement;
import com.kitfox.cache.CacheInteger;
import com.kitfox.cache.CacheNull;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.shape.CyShape;
import com.kitfox.game.control.color.PaintLayout;
import com.kitfox.raven.editor.node.renderer.RavenRenderer;
import com.kitfox.raven.editor.paint.RavenPaint;
import com.kitfox.raven.editor.paint.RavenPaintNone;
import com.kitfox.raven.editor.paint.RavenPaintProxy;
import com.kitfox.raven.editor.paintLayout.PaintLayoutProxy;
import com.kitfox.raven.editor.stroke.RavenStroke;
import com.kitfox.raven.editor.stroke.RavenStrokeNone;
import com.kitfox.raven.editor.stroke.RavenStrokeProxy;
import com.kitfox.raven.shape.bezier.BezierCurve;
import com.kitfox.raven.shape.bezier.BezierCurveCubic;
import com.kitfox.raven.shape.bezier.BezierCurveLine;
import com.kitfox.raven.shape.bezier.BezierCurveQuadratic;
import com.kitfox.raven.shape.path.PathCurve;
import com.kitfox.raven.util.planeData.PlaneDataProvider;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.ChildWrapperList;
import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.util.tree.PropertyWrapper;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author kitfox
 */
public class RavenNodeMeshStatic extends RavenNodeXformable
{
    public static final String PROP_PATH = "path";
    public final PropertyWrapper<RavenNodeMeshStatic, PathCurve> path =
            new PropertyWrapper(
            this, PROP_PATH, PathCurve.class);

    public static final String CHILD_EDGE_PLANES = "edgePlanes";
    public final ChildWrapperList<RavenNodeMeshStatic, RavenNodeDataPlane> edgePlanes =
            new ChildWrapperList<RavenNodeMeshStatic, RavenNodeDataPlane>(
            this, CHILD_EDGE_PLANES, RavenNodeDataPlane.class);

    public static final String CHILD_FACE_PLANES = "facePlanes";
    public final ChildWrapperList<RavenNodeMeshStatic, RavenNodeDataPlane> facePlanes =
            new ChildWrapperList<RavenNodeMeshStatic, RavenNodeDataPlane>(
            this, CHILD_FACE_PLANES, RavenNodeDataPlane.class);

    //ShapeInfo meshContours;
    ArrayList<RenderableComponent> edgeContours;
    ArrayList<RenderableComponent> faceContours;
    Path2D.Double pickShape;

    static final AffineTransform toPixels = new AffineTransform(1.0 / 100, 0, 0, 1.0 / 100, 0, 0);

    static final int flatnessSquared = 10000;

    protected RavenNodeMeshStatic(int uid)
    {
        super(uid);

        path.addPropertyWrapperListener(clearCache);
    }

    @Override
    protected void clearCache()
    {
        super.clearCache();
        edgeContours = null;
        faceContours = null;
        pickShape = null;
    }

    private void build()
    {
        //Get plane data
        HashMap<Class<? extends PlaneDataProvider>, ArrayList> edgePlaneMap
                = new HashMap<Class<? extends PlaneDataProvider>, ArrayList>();

        for (int i = 0; i < edgePlanes.size(); ++i)
        {
            RavenNodeDataPlane plane = edgePlanes.get(i);
            edgePlaneMap.put(plane.getPlaneDataType(), plane.getPlaneData());
        }

        //Build edges
        PathCurve pathCurve = path.getValue();
        Path2D.Double pathShape = pathCurve.asPath2D();
        NodeDocument doc = getDocument();

        ArrayList<RavenPaintProxy> paintList = edgePlaneMap.get(RavenPaintProxy.PlaneData.class);
        ArrayList<PaintLayoutProxy> layoutList = edgePlaneMap.get(PaintLayoutProxy.PlaneData.class);
        ArrayList<RavenStrokeProxy> strokeList = edgePlaneMap.get(RavenStrokeProxy.PlaneData.class);
        ArrayList<Integer> faceLeftList = edgePlaneMap.get(FaceLeftProxy.class);
        ArrayList<Integer> faceRightList = edgePlaneMap.get(FaceRightProxy.class);

        ArrayList<EdgeInfo> edgeInfos = new ArrayList<EdgeInfo>();

        int edgeIndex = 0;
        double[] coords = new double[6];
        int px = 0, py = 0;

        for (PathIterator it = pathShape.getPathIterator(null); !it.isDone(); it.next())
        {
            BezierCurve curve = null;

            switch (it.currentSegment(coords))
            {
                case PathIterator.SEG_MOVETO:
                {
                    px = (int)coords[0];
                    py = (int)coords[1];
                    break;
                }
                case PathIterator.SEG_LINETO:
                {
                    int x = (int)coords[0];
                    int y = (int)coords[1];

                    curve = new BezierCurveLine(px, py, x, y);

                    px = x;
                    py = y;
                    break;
                }
                case PathIterator.SEG_QUADTO:
                {
                    int k0x = (int)coords[0];
                    int k0y = (int)coords[1];
                    int x = (int)coords[2];
                    int y = (int)coords[3];

                    curve = new BezierCurveQuadratic(px, py, k0x, k0y, x, y);

                    px = x;
                    py = y;
                    break;
                }
                case PathIterator.SEG_CUBICTO:
                {
                    int k0x = (int)coords[0];
                    int k0y = (int)coords[1];
                    int k1x = (int)coords[2];
                    int k1y = (int)coords[3];
                    int x = (int)coords[4];
                    int y = (int)coords[5];

                    curve = new BezierCurveCubic(px, py, k0x, k0y, k1x, k1y, x, y);

                    px = x;
                    py = y;
                    break;
                }
            }

            if (curve != null)
            {
                edgeInfos.add(new EdgeInfo(curve,
                        paintList.get(edgeIndex).getPaint(doc),
                        layoutList.get(edgeIndex).getLayout(),
                        strokeList.get(edgeIndex).getStroke(doc),
                        faceLeftList.get(edgeIndex),
                        faceRightList.get(edgeIndex)
                        ));
                ++edgeIndex;
                curve = null;
            }
        }

        //Build strokes
        buildEdges(edgeInfos);
        buildFaces(edgeInfos);
    }

    private void buildEdges(ArrayList<EdgeInfo> edgeInfos)
    {
        edgeContours = new ArrayList<RenderableComponent>();

        Path2D.Double pathStroke = null;
        EdgeInfo lastEdge = null;
        for (EdgeInfo info: edgeInfos)
        {
            if (lastEdge == null || !lastEdge.sameEdgeStyle(info))
            {
                pathStroke = new Path2D.Double();
                pathStroke.moveTo(info.curve.getStartX(), info.curve.getStartY());
                edgeContours.add(new RenderableComponent(pathStroke, 
                        info.paint, info.layout, info.stroke));
            }

            info.curve.appendToPath(pathStroke);
            lastEdge = info;
        }
    }

    private void buildFaces(ArrayList<EdgeInfo> edgeInfos)
    {
        HashMap<Integer, ArrayList<HalfEdge>> halfEdgeGroups =
                new HashMap<Integer, ArrayList<HalfEdge>>();

        for (EdgeInfo info: edgeInfos)
        {
            if (info.edgeLeft != null)
            {
                ArrayList<HalfEdge> list = halfEdgeGroups.get(info.edgeLeft);
                if (list == null)
                {
                    list = new ArrayList<HalfEdge>();
                    halfEdgeGroups.put(info.edgeLeft, list);
                }
                list.add(new HalfEdge(info.curve, info.edgeLeft));
            }
            
            if (info.edgeRight != null)
            {
                ArrayList<HalfEdge> list = halfEdgeGroups.get(info.edgeRight);
                if (list == null)
                {
                    list = new ArrayList<HalfEdge>();
                    halfEdgeGroups.put(info.edgeRight, list);
                }
                list.add(new HalfEdge(info.curve.reverse(), info.edgeRight));
            }
        }

        //Get plane data
        HashMap<Class<? extends PlaneDataProvider>, ArrayList> facePlaneMap
                = new HashMap<Class<? extends PlaneDataProvider>, ArrayList>();

        for (int i = 0; i < facePlanes.size(); ++i)
        {
            RavenNodeDataPlane plane = facePlanes.get(i);
            facePlaneMap.put(plane.getPlaneDataType(), plane.getPlaneData());
        }
        ArrayList<RavenPaintProxy> paintList = facePlaneMap.get(RavenPaintProxy.PlaneData.class);
        ArrayList<PaintLayoutProxy> layoutList = facePlaneMap.get(PaintLayoutProxy.PlaneData.class);
        NodeDocument doc = getDocument();

        //Pull half edges into faces
        faceContours = new ArrayList<RenderableComponent>();
        for (Integer faceIndex: halfEdgeGroups.keySet())
        {
            ArrayList<HalfEdge> halfEdgeList = halfEdgeGroups.get(faceIndex);

            Path2D.Double pathContour = new Path2D.Double();

            while (!halfEdgeList.isEmpty())
            {
                HalfEdge firstEdge = halfEdgeList.remove(0);
                HalfEdge lastEdge = firstEdge;

                pathContour.moveTo(firstEdge.getStartX(), firstEdge.getStartY());
                firstEdge.curve.appendToPath(pathContour);

                while (!lastEdge.joinsAtEnd(firstEdge))
                {
                    int size = halfEdgeList.size();
                    for (Iterator<HalfEdge> it = halfEdgeList.iterator(); it.hasNext();)
                    {
                        HalfEdge curEdge = it.next();
                        if (lastEdge.joinsAtEnd(curEdge)
                                && lastEdge.faceIndex.equals(curEdge.faceIndex))
                        {
                            curEdge.curve.appendToPath(pathContour);
                            lastEdge = curEdge;
                            it.remove();
                            break;
                        }
                    }

                    if (size == halfEdgeList.size())
                    {
                        throw new IllegalStateException("We failed to extend the contour this pass");
                    }
                }

                pathContour.closePath();
            }

            faceContours.add(new RenderableComponent(pathContour,
                    paintList.get(faceIndex).getPaint(doc),
                    layoutList.get(faceIndex).getLayout()));
        }
    }


    private ArrayList<RenderableComponent> getEdgeContours()
    {
        if (edgeContours == null)
        {
            build();
        }
        return edgeContours;
    }

    private ArrayList<RenderableComponent> getFaceContours()
    {
        if (faceContours == null)
        {
            build();
        }
        return faceContours;
    }

    @Override
    protected void renderContent(RavenRenderer renderer)
    {
        for (RenderableComponent comp: getFaceContours())
        {
            RavenPaint paint = comp.paint;
            PaintLayout layout = comp.layout;
            if (paint == null || paint == RavenPaintNone.PAINT)
            {
                continue;
            }

            Path2D.Double pathFace =
                    (Path2D.Double)toPixels.createTransformedShape(comp.path);

            renderer.setPaint(paint);
            renderer.setPaintLayout(layout);
            renderer.fill(pathFace);
        }

        for (RenderableComponent comp: getEdgeContours())
        {
            RavenStroke stroke = comp.stroke;
            RavenPaint paint = comp.paint;
            PaintLayout layout = comp.layout;
            if (paint == null || paint == RavenPaintNone.PAINT
                    || stroke == null || stroke == RavenStrokeNone.STROKE)
            {
                continue;
            }

            Path2D.Double pathEdge =
                    (Path2D.Double)toPixels.createTransformedShape(comp.path);

            renderer.setPaint(paint);
            renderer.setPaintLayout(layout);
            renderer.setStroke(stroke.getStroke());
            renderer.draw(pathEdge);
        }

    }

//    @Override
    public Shape getPickShapeLocal()
    {
        if (pickShape == null)
        {
            pickShape = new Path2D.Double();
            for (RenderableComponent comp: getFaceContours())
            {
                pickShape.append(comp.path, false);
            }

            for (RenderableComponent comp: getEdgeContours())
            {
                Stroke stroke = comp.stroke.getStroke();
                if (stroke != null)
                {
                    pickShape.append(stroke.createStrokedShape(comp.path), false);
                }
            }

            pickShape = (Path2D.Double)toPixels.createTransformedShape(pickShape);
        }
        return pickShape;
    }

    @Override
    protected void renderContent(RenderContext ctx)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CyShape getShapePickLocal()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //-----------------------------------------------

    class HalfEdge
    {
        BezierCurve curve;
        Integer faceIndex;

        public HalfEdge(BezierCurve curve, Integer faceIndex)
        {
            this.curve = curve;
            this.faceIndex = faceIndex;
        }

        int getStartX()
        {
            return curve.getStartX();
        }

        int getStartY()
        {
            return curve.getStartY();
        }

        int getEndX()
        {
            return curve.getEndX();
        }

        int getEndY()
        {
            return curve.getEndY();
        }

        boolean joinsAtStart(HalfEdge other)
        {
            return other.faceIndex.equals(faceIndex)
                    && other.getEndX() == getStartX()
                    && other.getEndY() == getStartY();
        }

        boolean joinsAtEnd(HalfEdge other)
        {
            return other.faceIndex.equals(faceIndex)
                    && other.getStartX() == getEndX()
                    && other.getStartY() == getEndY();
        }
    }

    class EdgeInfo
    {
        BezierCurve curve;

        RavenPaint paint;
        PaintLayout layout;
        RavenStroke stroke;
        Integer edgeLeft;
        Integer edgeRight;

        public EdgeInfo(BezierCurve curve, RavenPaint paint, PaintLayout layout, RavenStroke stroke, Integer edgeLeft, Integer edgeRight)
        {
            this.curve = curve;
            this.paint = paint;
            this.layout = layout;
            this.stroke = stroke;
            this.edgeLeft = edgeLeft;
            this.edgeRight = edgeRight;
        }

        public boolean sameEdgeStyle(EdgeInfo other)
        {
            boolean samePaint = (paint == null && other.paint == null) ||
                    ((paint != null && paint.equals(other.paint)));
            boolean sameLayout = (layout == null && other.layout == null) ||
                    ((layout != null && layout.equals(other.layout)));
            boolean sameStroke = (stroke == null && other.stroke == null) ||
                    ((stroke != null && stroke.equals(other.stroke)));
            return samePaint && sameLayout && sameStroke;
        }
    }
    
    class RenderableComponent
    {
        Path2D.Double path;
        RavenPaint paint;
        PaintLayout layout;
        RavenStroke stroke;

        public RenderableComponent(Path2D.Double path,
                RavenPaint paint, PaintLayout layout, RavenStroke stroke)
        {
            this.path = path;
            this.paint = paint;
            this.layout = layout;
            this.stroke = stroke;
        }

        public RenderableComponent(Path2D.Double path,
                RavenPaint paint, PaintLayout layout)
        {
            this(path, paint, layout, null);
        }
    }

    @ServiceInst(service=PlaneDataProvider.class)
    public static class FaceLeftProxy extends PlaneDataProvider<Integer>
    {
        public FaceLeftProxy()
        {
            super(Integer.class, "FaceLeft");
        }

        @Override
        public CacheElement asCache(Integer data)
        {
            CacheInteger cache = new CacheInteger(data);
            return cache;
        }

        @Override
        public Integer parse(CacheElement cacheElement)
        {
            if (cacheElement instanceof CacheNull)
            {
                return null;
            }
            return ((CacheInteger)cacheElement).getValue();
        }
    }

    @ServiceInst(service=PlaneDataProvider.class)
    public static class FaceRightProxy extends PlaneDataProvider<Integer>
    {
        public FaceRightProxy()
        {
            super(Integer.class, "FaceRight");
        }

        @Override
        public CacheElement asCache(Integer data)
        {
            CacheInteger cache = new CacheInteger(data);
            return cache;
        }

        @Override
        public Integer parse(CacheElement cacheElement)
        {
            if (cacheElement instanceof CacheNull)
            {
                return null;
            }
            return ((CacheInteger)cacheElement).getValue();
        }
    }


    @ServiceInst(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<RavenNodeMeshStatic>
    {
        public Provider()
        {
            super(RavenNodeMeshStatic.class, "Mesh Static", "/icons/node/meshStatic.png");
        }

        @Override
        public RavenNodeMeshStatic createNode(int uid)
        {
            return new RavenNodeMeshStatic(uid);
        }
    }
}
