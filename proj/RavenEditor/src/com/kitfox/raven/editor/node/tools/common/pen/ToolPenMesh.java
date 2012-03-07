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

package com.kitfox.raven.editor.node.tools.common.pen;

import com.kitfox.coyote.material.color.CyMaterialColorDrawRecord;
import com.kitfox.coyote.material.color.CyMaterialColorDrawRecordFactory;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.coyote.renderer.vertex.CyVertexBufferDataSquare;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.CyRectangle2i;
import com.kitfox.coyote.shape.ShapeLinesProvider;
import com.kitfox.coyote.shape.bezier.BezierCubic2i;
import com.kitfox.coyote.shape.bezier.BezierCurve2i;
import com.kitfox.coyote.shape.bezier.BezierLine2i;
import com.kitfox.coyote.shape.bezier.PickPoint;
import com.kitfox.coyote.shape.bezier.mesh.BezierMeshEdge2i;
import com.kitfox.coyote.shape.bezier.mesh.BezierMeshVertex2i;
import com.kitfox.coyote.shape.bezier.mesh.CutLoop;
import com.kitfox.coyote.shape.bezier.mesh.CutSegHalf;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import com.kitfox.raven.editor.node.scene.RavenNodeRoot;
import com.kitfox.raven.editor.node.scene.RenderContext;
import com.kitfox.raven.editor.node.scene.snap.GraphLayout;
import com.kitfox.raven.paint.RavenPaint;
import com.kitfox.raven.paint.RavenPaintLayout;
import com.kitfox.raven.paint.RavenStroke;
import com.kitfox.raven.shape.network.NetworkDataEdge;
import com.kitfox.raven.shape.network.NetworkDataVertex;
import com.kitfox.raven.shape.network.NetworkMesh;
import com.kitfox.raven.shape.network.keys.NetworkDataTypePaint;
import com.kitfox.raven.shape.network.keys.NetworkDataTypePaintLayout;
import com.kitfox.raven.shape.network.keys.NetworkDataTypeStroke;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class ToolPenMesh extends ToolPenDelegate
{
    ArrayList<Step> plan = new ArrayList<Step>();
    MouseEvent mouseStart;
    MouseEvent mouseTrackEvt;
    final ServiceBezierMesh servMesh;
    
    protected ToolPenMesh(ToolPenDispatch dispatch, ServiceBezierMesh servMesh)
    {
        super(dispatch);
        this.servMesh = servMesh;
    }

    private NetworkMesh getMesh()
    {
        return servMesh.getNetworkMesh();
    }
    
    private void setMesh(NetworkMesh mesh, boolean history)
    {
        servMesh.setNetworkMesh(mesh, history);
    }

    protected CyVector2d xformDev2MeshPoint(CyVector2d v, boolean snapGrid)
    {
        v = xformDev2LocalPoint(v, snapGrid);
        v.scale(100);
        return v;
    }

    private Step startStep(MouseEvent evt)
    {
        NetworkMesh mesh = getMesh();
        RavenNodeRoot root = getDocument();
        //GraphLayout layout = getGraphLayout();
        
        CyVector2d pickPt = xformDev2MeshPoint(
                new CyVector2d(evt.getX(), evt.getY()), true);

        //Look for existing mesh vertex to clamp to
        BezierMeshVertex2i<NetworkDataVertex> v = mesh.getClosestVertex(pickPt.x, pickPt.y);
        double pickRad = root.getGraphRadiusPick() * 100;
        double pickRadiusSq = pickRad * pickRad;
        
        if (v != null &&
                v.getCoord().getDistSquared(pickPt) < pickRadiusSq)
        {
            Coord c = v.getCoord();
            return new Step(new CyVector2d(c.x, c.y));
        }
        
        //Try clamping to point we are currently plotting
        for (Step step: plan)
        {
            if (step.point.distanceSquared(pickPt) < pickRadiusSq)
            {
                return new Step(new CyVector2d(step.point));
            }
        }
        
        //Check for clamp to edge in mesh
        BezierMeshEdge2i<NetworkDataEdge> e = 
                mesh.getClosestEdge(pickPt.x, pickPt.y, pickRadiusSq);
        if (e != null)
        {
            BezierCurve2i curve = e.asCurve();
            PickPoint pt = curve.getClosestPoint(pickPt.x, pickPt.y);

            if (pt.getDistSquared() <= pickRadiusSq)
            {
                Step s = new Step(new CyVector2d(pt.getX(), pt.getY()));
                s.splitEdge = e;
                s.splitPoint = pt;
                return s;
            }
        }
        
        //We're in free space.  Just use pick coord
        return new Step(pickPt);
    }

    private void applyTangent(MouseEvent evt)
    {
        CyVector2d dragPt = xformDev2MeshPoint(
                new CyVector2d(evt.getX(), evt.getY()), false);
        
//        int dx = evt.getX() - mouseStart.getX();
//        int dy = evt.getY() - mouseStart.getY();
        
        Step step = plan.get(plan.size() - 1);
//        CyVector2d tan = xformDev2MeshPoint(new CyVector2d(dx, dy), false);
        dragPt.sub(step.point);
        step.tangent = dragPt;
    }
    
    private void commit()
    {
        NetworkMesh mesh = getMesh();
        
        //First, run through and cut any curves that need it
        for (Step s: plan)
        {
            if (s.splitPoint != null)
            {
                PickPoint pt = s.splitPoint;
                BezierMeshEdge2i<NetworkDataEdge> e = s.splitEdge;
                
                BezierCurve2i curve = e.asCurve();
                mesh.removeEdge(e);
                BezierCurve2i[] curves = curve.split(pt.getT());
                
                ArrayList<BezierMeshEdge2i> newEdges0 
                        = mesh.addEdge(curves[0], new NetworkDataEdge(e.getData()));
                mesh.addEdge(curves[1], new NetworkDataEdge(e.getData()));
                
                BezierMeshEdge2i newEdge0 = newEdges0.get(0);
                BezierMeshVertex2i v = newEdge0.getEnd();
                v.setData(new NetworkDataVertex(
                        (NetworkDataVertex)e.getStart().getData()));
                
                Coord coord = v.getCoord();
                s.point.set(coord.x, coord.y);
            }
        }
        
        //Get decoration info
        CyRectangle2i bounds = mesh.getBounds();
        for (int i = 0; i < plan.size() - 1; ++i)
        {
            Step s0 = plan.get(i);
            
            if (bounds == null)
            {
                bounds = new CyRectangle2i((int)s0.point.x, (int)s0.point.y);
            }
            bounds.union((int)s0.point.x, (int)s0.point.y);
            if (s0.tangent != null)
            {
                bounds.union((int)(s0.point.x + s0.tangent.x), 
                        (int)(s0.point.y + s0.tangent.y));
                bounds.union((int)(s0.point.x - s0.tangent.x), 
                        (int)(s0.point.y - s0.tangent.y));
            }
        }
        
        RavenNodeRoot root = (RavenNodeRoot)getDocument();
        RavenPaint strokePaint = root.getStrokePaint();
        RavenStroke stroke = root.getStrokeShape();
        RavenPaintLayout layout = new RavenPaintLayout(bounds);
        RavenPaint fillPaint = root.getFillPaint();
        
        //Add curves
        for (int i = 0; i < plan.size() - 1; ++i)
        {
            Step s0 = plan.get(i);
            Step s1 = plan.get(i + 1);
            
            BezierCurve2i curve = buildCurve(s0, s1);
            
            NetworkDataEdge data = new NetworkDataEdge();
            data.putEdge(NetworkDataTypePaint.class, strokePaint);
            data.putEdge(NetworkDataTypeStroke.class, stroke);
            data.putEdge(NetworkDataTypePaintLayout.class, layout);
            mesh.addEdge(curve, data);
        }

        //Build faces
        ArrayList<CutLoop> faces = mesh.createFaces();
        for (int i = 0; i < faces.size(); ++i)
        {
            CutLoop loop = faces.get(i);
            if (loop.isCcw())
            {
                decorateFace(loop, fillPaint, layout);
            }
        }
        
        //Set value
        setMesh(mesh, true);
        
        dispatch.delegateDone();
    }
    
    private void decorateFace(CutLoop face, RavenPaint fillPaint, RavenPaintLayout fillLayout)
    {
        RavenPaint curPaint = null;
        RavenPaintLayout curLayout = null;
        
        //Check existing face edges to see if a color is already set
        ArrayList<CutSegHalf> segs = face.getSegs();
        for (CutSegHalf half: segs)
        {
            BezierMeshEdge2i<NetworkDataEdge> e 
                    = (BezierMeshEdge2i)half.getEdge();
            if (e == null)
            {
                //Skip extra segments that were added by cutter to
                // connect graph
                continue;
            }
            NetworkDataEdge data = e.getData();
            RavenPaint edgePaint;
            RavenPaintLayout edgeLayout;
            if (half.isRight())
            {
                edgePaint = data.getRight(NetworkDataTypePaint.class);
                edgeLayout = data.getRight(NetworkDataTypePaintLayout.class);
            }
            else
            {
                edgePaint = data.getLeft(NetworkDataTypePaint.class);
                edgeLayout = data.getLeft(NetworkDataTypePaintLayout.class);
            }
            
            if (curPaint == null)
            {
                curPaint = edgePaint;
            }
            if (curLayout == null)
            {
                curLayout = edgeLayout;
            }
        }
        
        //Use default color if face has none
        if (curPaint == null)
        {
            curPaint = fillPaint;
        }
        if (curLayout == null)
        {
            curLayout = fillLayout;
        }
        
        //Decorate face
        for (CutSegHalf half: segs)
        {
            BezierMeshEdge2i<NetworkDataEdge> e 
                    = (BezierMeshEdge2i)half.getEdge();
            if (e == null)
            {
                continue;
            }
            NetworkDataEdge data = e.getData();
            if (half.isRight())
            {
                data.putRight(NetworkDataTypePaint.class, curPaint);
                data.putRight(NetworkDataTypePaintLayout.class, curLayout);
            }
            else
            {
                data.putLeft(NetworkDataTypePaint.class, curPaint);
                data.putLeft(NetworkDataTypePaintLayout.class, curLayout);
            }
        }
        
        //Decorate child faces
//        for (int i = 0; i < face.getNumChildren(); ++i)
//        {
//            CutLoop hole = face.getChild(i);
//            for (int j = 0; j < hole.getNumChildren(); ++j)
//            {
//                CutLoop subface = hole.getChild(j);
//                decorateFace(subface, fillPaint, fillLayout);
//            }
//        }
    }
    
    @Override
    protected void click(MouseEvent evt)
    {
        Step step = startStep(evt);
        plan.add(step);
        
        mouseTrackEvt = null;
    }

    @Override
    protected void startDrag(MouseEvent evt)
    {
        Step step = startStep(evt);
        plan.add(step);
        
        mouseStart = evt;
    }
    
    @Override
    protected void dragTo(MouseEvent evt)
    {
        applyTangent(evt);
    }

    @Override
    protected void endDrag(MouseEvent evt)
    {
        applyTangent(evt);
        mouseStart = null;
    }

    @Override
    public void mouseMoved(MouseEvent evt)
    {
//        super.mouseMoved(evt);
        mouseTrackEvt = evt;
    }

    @Override
    public void keyPressed(KeyEvent evt)
    {
        switch (evt.getKeyCode())
        {
            case KeyEvent.VK_DELETE:
                if (!plan.isEmpty())
                {
                    plan.remove(plan.size() - 1);
                }
                break;
            case KeyEvent.VK_ESCAPE:
                cancel();
                break;
            case KeyEvent.VK_ENTER:
                commit();
                break;
        }
        
        super.keyPressed(evt);
    }

    @Override
    public void cancel()
    {
        dispatch.delegateDone();
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void render(RenderContext ctx)
    {
        super.render(ctx);
        
        if (plan.isEmpty())
        {
            return;
        }
        
        //Build graph
        CyPath2d path = new CyPath2d();
        ArrayList<CyVector2d> verts = new ArrayList<CyVector2d>();
        
        Step firstStep = plan.get(0);
        path.moveTo(firstStep.point.x, firstStep.point.y);
        verts.add(new CyVector2d(firstStep.point.x, firstStep.point.y));

        for (int i = 0; i < plan.size() - 1; ++i)
        {
            Step s0 = plan.get(i);
            Step s1 = plan.get(i + 1);
            
            BezierCurve2i curve = buildCurve(s0, s1);

            if (curve instanceof BezierLine2i)
            {
                path.lineTo(curve.getEndX(), curve.getEndY());
                verts.add(new CyVector2d(curve.getEndX(), curve.getEndY()));
            }
            else
            {
                BezierCubic2i c = (BezierCubic2i)curve;
                path.cubicTo(c.getAx1(), c.getAy1(), 
                        c.getAx2(), c.getAy2(), 
                        c.getAx3(), c.getAy3());
                verts.add(new CyVector2d(c.getAx3(), c.getAy3()));
            }
        }
        
        //Draw to cursor
        if (mouseTrackEvt != null)
        {
            CyVector2d pt = xformDev2MeshPoint(
                    new CyVector2d(mouseTrackEvt.getX(), mouseTrackEvt.getY()), true);
            path.lineTo(pt.x, pt.y);
        }
        
        //Add tangents
        {
            Step lastStep = plan.get(plan.size() - 1);
            Step exitStep = lastStep;
            
            //Dragging through current line
            if (mouseStart != null)
            {
                //Tangent of dragging
                CyVector2d p = lastStep.point;
                CyVector2d t = lastStep.tangent;
                path.moveTo(p.x - t.x, p.y - t.y);
                path.lineTo(p.x + t.x, p.y + t.y);

                verts.add(new CyVector2d(p.x - t.x, p.y - t.y));
                verts.add(new CyVector2d(p.x + t.x, p.y + t.y));
                
                exitStep = plan.size() >= 2 ? plan.get(plan.size() - 2) : null;
            }

            if (exitStep != null && exitStep.tangent != null)
            {
                //End tangent of prev point
                CyVector2d p = exitStep.point;
                CyVector2d t = exitStep.tangent;
                path.moveTo(p.x, p.y);
                path.lineTo(p.x + t.x, p.y + t.y);

                verts.add(new CyVector2d(p.x + t.x, p.y + t.y));
            }
        }
        
        
        CyDrawStack stack = ctx.getDrawStack();
        stack.pushFrame(null);

//        GraphLayout graphLayout = getGraphLayout();
        RavenNodeRoot root = getDocument();
        
        //Draw curves and handles
        {
            ShapeLinesProvider prov = new ShapeLinesProvider(path);
            CyVertexBuffer lineMesh = new CyVertexBuffer(prov);

            CyMatrix4d l2d = getLocalToDevice(null);
            l2d.scale(1 / 100.0, 1 / 100.0, 1);

            CyMaterialColorDrawRecord rec =
                    CyMaterialColorDrawRecordFactory.inst().allocRecord();

            CyMatrix4d l2w = getLocalToWorld(null);
            stack.setModelXform(l2w);

            CyMatrix4d mvp = stack.getModelViewProjXform();
            mvp.scale(1 / 100.0, 1 / 100.0, 1);

            rec.setMesh(lineMesh);
            rec.setColor(root.getGraphColorEdge().asColor());
            rec.setOpacity(1);
    //mvp.scale(10, 100, 1);
            rec.setMvpMatrix(mvp);

    //rec.setMesh(CyVertexBufferDataSquare.inst().getBuffer());
    //l2d.setIdentity();
    //l2d.scale(10, 100, 1);
    //rec.setMvpMatrix(l2d);

            stack.addDrawRecord(rec);
        }
        
        //Draw verts
        {
            CyMatrix4d l2w = getLocalToWorld(null);
            stack.setModelXform(l2w);
            
            CyMatrix4d mv = stack.getModelViewXform();
            CyMatrix4d proj = stack.getProjXform();

            CyMatrix4d mvp = new CyMatrix4d();
                
            for (CyVector2d v: verts)
            {
                CyMaterialColorDrawRecord rec =
                        CyMaterialColorDrawRecordFactory.inst().allocRecord();

                CyVector2d viewVert = new CyVector2d(v);
                //From mesh space to local space
                viewVert.scale(1 / 100f);
                mv.transformPoint(viewVert);

                mvp.set(proj);
//                mvp.scale(1 / 100.0, 1 / 100.0, 1);
                
                mvp.translate(viewVert.x, viewVert.y, 0);
//                int ptRad = graphLayout.getPointRadiusDisplay() * 200;
                float ptRad = root.getGraphRadiusDisplay() * 2;
                mvp.scale(ptRad, ptRad, ptRad);
                mvp.translate(-.5, -.5, 0);

                rec.setMesh(CyVertexBufferDataSquare.inst().getBuffer());
                rec.setColor(root.getGraphColorEdge().asColor());
                rec.setOpacity(1);
                rec.setMvpMatrix(mvp);

                stack.addDrawRecord(rec);                
            }
        }
        
        stack.popFrame();
    }
    
    private BezierCurve2i buildCurve(Step s0, Step s1)
    {
        if (s0.tangent == null && s1.tangent == null)
        {
            return new BezierLine2i(
                    (int)s0.point.x, (int)s0.point.y,
                    (int)s1.point.x, (int)s1.point.y);
        }

        double tx0 = s0.tangent == null ? 0 : s0.tangent.x;
        double ty0 = s0.tangent == null ? 0 : s0.tangent.y;
        double tx1 = s1.tangent == null ? 0 : s1.tangent.x;
        double ty1 = s1.tangent == null ? 0 : s1.tangent.y;

        return new BezierCubic2i(
                (int)s0.point.x, (int)s0.point.y,
                (int)(s0.point.x + tx0), (int)(s0.point.y + ty0), 
                (int)(s1.point.x - tx1), (int)(s1.point.y - ty1), 
                (int)s1.point.x, (int)s1.point.y);
    }
    
    //-------------------------
    private class Step
    {
        CyVector2d point;
        
        //If not null, creating smooth point with tangent
        CyVector2d tangent;
        
        //If not null, cut edge at this point to get 
        BezierMeshEdge2i<NetworkDataEdge> splitEdge;
        PickPoint splitPoint;

        public Step(CyVector2d point)
        {
            this.point = point;
        }
    }
}
