/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import com.kitfox.coyote.shape.ShapeLinesProvider;
import com.kitfox.coyote.shape.bezier.BezierCubic2i;
import com.kitfox.coyote.shape.bezier.BezierCurve2i;
import com.kitfox.coyote.shape.bezier.BezierLine2i;
import com.kitfox.coyote.shape.bezier.PickPoint;
import com.kitfox.coyote.shape.bezier.mesh.BezierMeshEdge2i;
import com.kitfox.coyote.shape.bezier.mesh.BezierMeshVertex2i;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import com.kitfox.raven.editor.node.scene.RenderContext;
import com.kitfox.raven.editor.node.scene.snap.GraphLayout;
import com.kitfox.raven.shape.network.NetworkDataEdge;
import com.kitfox.raven.shape.network.NetworkDataVertex;
import com.kitfox.raven.shape.network.NetworkMesh;
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
        GraphLayout layout = getGraphLayout();
        
        CyVector2d pickPt = xformDev2MeshPoint(
                new CyVector2d(evt.getX(), evt.getY()), true);

        //Look for existing mesh vertex to clamp to
        BezierMeshVertex2i<NetworkDataVertex> v = mesh.getClosestVertex(pickPt.x, pickPt.y);
        double pickRadiusSq = layout.getPointRadiusPickSq() * 100 * 100;
        
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
        int dx = evt.getX() - mouseStart.getX();
        int dy = evt.getY() - mouseStart.getY();
        
        Step step = plan.get(plan.size() - 1);
        CyVector2d tan = xformDev2MeshPoint(new CyVector2d(dx, dy), false);
        step.tangent = tan;
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
        
        //Add curves
        for (int i = 0; i < plan.size() - 1; ++i)
        {
            Step s0 = plan.get(i);
            Step s1 = plan.get(i + 1);
            
            BezierCurve2i curve = buildCurve(s0, s1);
            
            mesh.addEdge(curve, new NetworkDataEdge());
        }
        
        //Set value
        setMesh(mesh, true);
        
        dispatch.delegateDone();
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

        GraphLayout graphLayout = getGraphLayout();
        
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
            rec.setColor(graphLayout.getEdgeColor());
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
            for (CyVector2d v: verts)
            {
                CyMaterialColorDrawRecord rec =
                        CyMaterialColorDrawRecordFactory.inst().allocRecord();

                CyMatrix4d l2w = getLocalToWorld(null);
                stack.setModelXform(l2w);

                CyMatrix4d mvp = stack.getModelViewProjXform();
                mvp.scale(1 / 100.0, 1 / 100.0, 1);
                
                mvp.translate(v.x, v.y, 0);
                int ptRad = graphLayout.getPointRadiusDisplay() * 200;
                mvp.scale(ptRad, ptRad, ptRad);
                mvp.translate(-.5, -.5, 0);

                rec.setMesh(CyVertexBufferDataSquare.inst().getBuffer());
                rec.setColor(graphLayout.getEdgeColor());
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
