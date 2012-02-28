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

import com.kitfox.raven.shape.bezier.BezierCurve;
import com.kitfox.raven.shape.bezier.BezierVertex;
import java.awt.geom.Path2D;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
@Deprecated
public class BezierContour
{
    ArrayList<BezierEdgeAttach> edges;

    public BezierContour(ArrayList<BezierEdgeAttach> edges)
    {
        this.edges = edges;
    }

    public Path2D.Double createPath()
    {
        Path2D.Double path = new Path2D.Double();

        BezierEdgeAttach attStart = edges.get(0);
        BezierVertex vtxStart = attStart.rightJoin
                ? attStart.edge.getEnd(): attStart.edge.getStart();
        path.moveTo(vtxStart.getPoint().getX(), vtxStart.getPoint().getY());

        for (int i = 0; i < edges.size(); ++i)
        {
            BezierEdgeAttach attCur = edges.get(i);
            BezierCurve curve = attCur.rightJoin
                    ? attCur.edge.getCurve().reverse()
                    : attCur.edge.getCurve();
            curve.appendToPath(path);
        }
        path.closePath();

        return path;
    }

    protected void attachToFace(BezierFace newFace)
    {
        for (BezierEdgeAttach edge: edges)
        {
            edge.attachToFace(newFace);
        }
    }

    public ArrayList<BezierVertex> getVertices()
    {
        ArrayList<BezierVertex> list = new ArrayList<BezierVertex>();
        for (int i = 0; i < edges.size(); ++i)
        {
            BezierEdgeAttach edgeAtt = edges.get(i);
            if (edgeAtt.rightJoin)
            {
                list.add(edgeAtt.edge.end);
            }
            else
            {
                list.add(edgeAtt.edge.start);
            }
        }

        return list;
    }

    public boolean isCCW()
    {
        //If we're walking CCW
        ArrayList<BezierVertex> verts = getVertices();

        BezierVertex v0 = verts.get(0);
        long area = 0;
        for (int i = 1; i < verts.size() - 1; ++i)
        {
            BezierVertex v1 = verts.get(i);
            BezierVertex v2 = verts.get(i + 1);

            long dx0 = v1.point.getX() - v0.point.getX();
            long dy0 = v1.point.getY() - v0.point.getY();
            long dx1 = v2.point.getX() - v0.point.getX();
            long dy1 = v2.point.getY() - v0.point.getY();

            //Tri area is half of cross product
            long triArea = dy0 * dx1 - dx0 * dy1;
            area += triArea;
        }

        //CCW shapes have +ve area, CW have -ve
        return area > 0;
    }

    public boolean contains(int x, int y)
    {
        BezierInsideOutsideCheck check = new BezierInsideOutsideCheck(x, y);

        for (BezierEdgeAttach edgeAtt: edges)
        {
            edgeAtt.edge.pointInsideCheck(check);
        }
        check.finish();

        return check.isInside();
    }

}
