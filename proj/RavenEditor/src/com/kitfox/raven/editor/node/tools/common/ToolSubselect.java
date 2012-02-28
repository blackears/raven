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

package com.kitfox.raven.editor.node.tools.common;

import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.node.scene.RavenNodeXformable;
import com.kitfox.raven.editor.node.tools.ToolProvider;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.shape.bezier.BezierEdge;
import com.kitfox.raven.shape.bezier.BezierNetwork;
import com.kitfox.raven.shape.bezier.BezierNetwork.ManipComponent;
import com.kitfox.raven.shape.bezier.BezierNetwork.ManipKnot;
import com.kitfox.raven.shape.bezier.BezierNetwork.ManipVertex;
import com.kitfox.raven.shape.bezier.BezierNetworkManipulator;
import com.kitfox.raven.shape.bezier.BezierVertex;
import com.kitfox.raven.shape.bezier.VertexSmooth;
import com.kitfox.raven.util.Intersection;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.SelectionRecord;
import com.kitfox.raven.util.undo.History;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wolframalpha:
 * Query:
 * {{cos(a), -sin(a), 0}, {sin(a), cos(a), 0}, {0, 0, 1}}.{{s, 0, 0}, {0, t, 0}, {0, 0, 1}}.{{1, 0, x}, {0, 1, y}, {0, 0, 1}}
 *
 * Query:
 * {{s, 0, 0}, {0, t, 0}, {0, 0, 1}}.{{1, 0, x}, {0, 1, y}, {0, 0, 1}}
 * Result:
 * {{s, 0, sx}, {0, t, ty}, {0, 0, 1}}
 *
 * @author kitfox
 */
public class ToolSubselect extends ToolSubselectBase
{
    ManipSub manipulator;
    final ToolSubselect.Provider toolProvider;

    protected ToolSubselect(ToolUser user, ToolSubselect.Provider toolProvider)
    {
        super(user);
        this.toolProvider = toolProvider;
    }

    @Override
    protected void click(MouseEvent evt)
    {
        int mod = evt.getModifiersEx();
        boolean ctrl = (mod & MouseEvent.CTRL_DOWN_MASK) != 0;
        boolean alt = (mod & MouseEvent.ALT_DOWN_MASK) != 0;

        if (!ctrl && !alt)
        {
            super.click(evt);
            return;
        }
        
        if (ctrl)
        {
            addVertex(evt);
        }
        else if (alt)
        {
            removeVertex(evt);
        }
    }

    private void removePoints(BezierNetwork network, List<BezierVertex> list)
    {
        for (BezierVertex vtx: list)
        {
            network.removeVertex(vtx);
        }
    }

    public void deleteSelectedVertices()
    {
        //Add or remove points
        ServiceDocument provider = user.getToolService(ServiceDocument.class);
        if (provider == null)
        {
            return;
        }

        History hist = provider.getDocument().getHistory();
        hist.beginTransaction("Deleting vertices");

        Selection<NodeObject> sel = provider.getSelection();
        for (int i = 0; i < sel.size(); ++i)
        {
            NodeObject rec = sel.get(i);
            ServiceBezierNetwork provBez =
                    rec.getNodeService(ServiceBezierNetwork.class, false);

            if (provBez == null)
            {
                continue;
            }

            BezierNetwork network = provBez.getBezierNetwork();

            BezierNetwork.Subselection subsel =
                    sel.getSubselection(rec, BezierNetwork.Subselection.class);

            ArrayList<BezierVertex> list =
                    network.getVerticesByUid(subsel.getVertexUids());
            
            removePoints(network, list);

            provBez.updateFromBezierNetwork(network, true);
        }

        hist.commitTransaction();
        fireToolDisplayChanged();
    }

    protected void removeVertex(MouseEvent evt)
    {
        //Add or remove points
        ServiceDocument provider = user.getToolService(ServiceDocument.class);
        if (provider == null)
        {
            return;
        }

        final int rad = BezierNetwork.POINT_HANDLE_RADIUS;
        Rectangle selectRect = new Rectangle(
                evt.getX() - rad, evt.getY() - rad,
                rad * 2, rad * 2);

        Point2D.Double pt = new Point2D.Double();

        Selection<NodeObject> sel = provider.getSelection();
        for (int i = 0; i < sel.size(); ++i)
        {
            NodeObject rec = sel.get(i);
            ServiceBezierNetwork provBez =
                    rec.getNodeService(ServiceBezierNetwork.class, false);

            if (provBez == null)
            {
                continue;
            }

            RavenNodeXformable nodeSpatial = (RavenNodeXformable)rec;
            AffineTransform l2d = nodeSpatial.getLocalToDeviceTransform((AffineTransform)null);
            l2d.concatenate(BezierNetwork.toPixels);

            BezierNetwork network = provBez.getBezierNetwork();

            ArrayList<BezierVertex> vertices = network.getVertices();
            for (BezierVertex vtx: vertices)
            {
                pt.setLocation(vtx.getPoint().getX(), vtx.getPoint().getY());
                l2d.transform(pt, pt);

                if (selectRect.contains(pt))
                {
                    removePoints(network, Collections.singletonList(vtx));
                    provBez.updateFromBezierNetwork(network, true);
                    fireToolDisplayChanged();
                    return;
                }
            }
        }
    }

    protected void addVertex(MouseEvent evt)
    {
        //Add or remove points
        ServiceDocument provider = user.getToolService(ServiceDocument.class);
        if (provider == null)
        {
            return;
        }

        Selection<NodeObject> sel = provider.getSelection();
        for (int i = 0; i < sel.size(); ++i)
        {
            NodeObject rec = sel.get(i);
            ServiceBezierNetwork provBez =
                    rec.getNodeService(ServiceBezierNetwork.class, false);

            if (provBez == null)
            {
                continue;
            }

//            BezierNetwork.Subselection subsel =
//                    sel.getSubselection(rec, BezierNetwork.Subselection.class);

            RavenNodeXformable nodeSpatial = (RavenNodeXformable)rec;
            AffineTransform l2d = nodeSpatial.getLocalToDeviceTransform((AffineTransform)null);
            l2d.concatenate(BezierNetwork.toPixels);

            BezierNetwork network = provBez.getBezierNetwork();

            ArrayList<BezierEdge> edges = network.getEdges();

            //Rectangle rect = new Rectangle(evt.getX(), evt.getY(), 1, 1);
            for (BezierEdge edge: edges)
            {
                Float t = edge.getSegList().getHit(evt.getX(), evt.getY(), 4, l2d);
                if (t != null)
                {
                    //We have a hit.  Insert new point
                    BezierVertex vtx = network.splitEdge(edge, t);

                    BezierVertex vtxStart = edge.getStart();
                    BezierVertex vtxEnd = edge.getEnd();

                    VertexSmooth smoothStart = vtxStart.getData(VertexSmooth.PlaneData.class);
                    VertexSmooth smoothEnd = vtxEnd.getData(VertexSmooth.PlaneData.class);

                    VertexSmooth smoothVtx;
                    if (edge.getCurve().getDegree() == 1)
                    {
                        smoothVtx = VertexSmooth.CUSP;
                    }
                    else if (smoothStart == VertexSmooth.TENSE
                            || smoothEnd == VertexSmooth.TENSE)
                    {
                        smoothVtx = VertexSmooth.TENSE;
                    }
                    else
                    {
                        smoothVtx = VertexSmooth.SMOOTH;
                    }
                    vtx.setData(VertexSmooth.PlaneData.class, smoothVtx);
                    
                    network.applyVertexSmoothing(smoothVtx, vtx);

                    provBez.updateFromBezierNetwork(network, true);
                    fireToolDisplayChanged();
                    return;
                }
            }
        }
        
    }



    @Override
    protected boolean isDraggingSelectionArea()
    {
        return manipulator == null;
    }

    @Override
    protected void startDrag(MouseEvent evt)
    {
        super.startDrag(evt);

        ServiceDocument provider = user.getToolService(ServiceDocument.class);
        if (provider == null)
        {
            return;
        }


        boolean moveEntireSelection = false;
        Selection<NodeObject> sel = provider.getSelection();
        for (int i = 0; i < sel.size(); ++i)
        {
            NodeObject rec = sel.get(i);
            BezierNetwork.Subselection subsel =
                    sel.getSubselection(rec, BezierNetwork.Subselection.class);

            ServiceBezierNetwork provBez =
                    rec.getNodeService(ServiceBezierNetwork.class, false);

            if (provBez == null)
            {
                continue;
            }

            RavenNodeXformable nodeSpatial = (RavenNodeXformable)rec;
            AffineTransform l2d = nodeSpatial.getLocalToDeviceTransform((AffineTransform)null);


            BezierNetwork network = provBez.getBezierNetwork();
            ArrayList<ManipComponent> comps = network.selectPointManipulators(
                    new Rectangle(evt.getX(), evt.getY(), 1, 1),
                    subsel,
                    l2d,
                    Intersection.INTERSECTS);

            if (comps.isEmpty())
            {
                continue;
            }

            ManipComponent top = comps.get(0);
            if (top instanceof ManipKnot)
            {
                ManipKnot m = (ManipKnot)top;
                manipulator = new ManipSubKnot(rec, network, m.getVtx(), m.isKnotOut());
//                manipulator = new ManipSubKnot(rec, network, m.getEdge(), m.getType());
                return;
            }

//            boolean addAllSelectedVerts = true;
            for (ManipComponent comp: comps)
            {
                if (comp instanceof ManipVertex)
                {
                    ManipVertex mv = (ManipVertex)comp;
                    BezierVertex vtx = mv.getVertex();

                    if (subsel == null || !subsel.contains(vtx))
                    {
                        //We hit one vert not in the selection group.
                        // Make a unique group with just it
//                        ArrayList<BezierVertex> vertList = network.getLinkedVertices(vtx);
//                        vertList.add(vtx);
//                        manipulator = new ManipSubVertices(rec, network, vertList);
                        manipulator = new ManipSubVertices(rec, network, Collections.singletonList(vtx));
                        return;
                    }
                    else
                    {
                        moveEntireSelection = true;
                    }
                }
            }
        }


        if (moveEntireSelection)
        {
            ArrayList<ManipSubVertices> vertGroups = new ArrayList<ManipSubVertices>();
            for (int i = 0; i < sel.size(); ++i)
            {
                NodeObject rec = sel.get(i);
                BezierNetwork.Subselection subsel =
                        sel.getSubselection(rec, BezierNetwork.Subselection.class);

                ServiceBezierNetwork provBez =
                        rec.getNodeService(ServiceBezierNetwork.class, false);

                if (provBez == null)
                {
                    continue;
                }

                BezierNetwork network = provBez.getBezierNetwork();

                //Add all selected verts
                ArrayList<BezierVertex> vertList = new ArrayList<BezierVertex>();
                for (BezierVertex vtx: network.getVertices())
                {
                    if (subsel != null && subsel.contains(vtx))
                    {
                        vertList.add(vtx);
                    }
                }

                ManipSubVertices m = new ManipSubVertices(rec, network, vertList);
                vertGroups.add(m);
            }

            if (manipulator == null && !vertGroups.isEmpty())
            {
                manipulator = vertGroups.size() == 1 ? vertGroups.get(0)
                        : new ManipSubVerticesGroup(vertGroups);
            }
        }
    }

    @Override
    protected void dragTo(MouseEvent evt)
    {
        super.dragTo(evt);

        if (manipulator != null)
        {
            manipulator.dragBy(
                    evt.getX() - startEvt.getX(),
                    evt.getY() - startEvt.getY());
        }

        fireToolDisplayChanged();
    }

    @Override
    protected void endDrag(MouseEvent evt)
    {
        super.endDrag(evt);

        if (manipulator != null)
        {
            manipulator.apply(true);
            
            manipulator = null;
        }

        fireToolDisplayChanged();
    }


    @Override
    public void paint(Graphics2D g)
    {
        super.paint(g);

        ServiceDocument provider = user.getToolService(ServiceDocument.class);
        if (provider == null)
        {
            return;
        }

        if (manipulator != null)
        {
            manipulator.render(g, Color.red);
            return;
        }

        Selection<NodeObject> selection = provider.getSelection();
        for (int i = 0; i < selection.size(); ++i)
        {
            NodeObject rec = selection.get(i);
            ServiceBezierNetwork provBez = 
                    rec.getNodeService(ServiceBezierNetwork.class, false);

            if (provBez == null)
            {
                continue;
            }

            //AffineTransform w2d = getWorldToDeviceTransform(null);
            RavenNodeXformable nodeSpatial = (RavenNodeXformable)rec;
            AffineTransform l2d = nodeSpatial.getLocalToDeviceTransform((AffineTransform)null);

            BezierNetwork.Subselection subselection = 
                    selection.getSubselection(rec, BezierNetwork.Subselection.class);

            BezierNetwork network = provBez.getBezierNetwork();
            network.renderManipulator(g, subselection, l2d, Color.red);
        }
    }

    @Override
    public void keyPressed(KeyEvent evt)
    {
        evt.consume();

        switch (evt.getKeyCode())
        {
            case KeyEvent.VK_DELETE:
                deleteSelectedVertices();
                break;
            default:
                super.keyTyped(evt);
                break;
        }
    }


    //---------------------------------------

    abstract class ManipSub
    {
        abstract protected void render(Graphics2D g, Color color);

        abstract protected void dragBy(int dx, int dy);

        abstract protected void apply(boolean history);
    }

    class ManipSubKnot extends ManipSub
    {
        NodeObject rec;
        BezierNetwork network;
//        BezierEdge edge;
//        KnotType type;
        BezierVertex vtx;
        boolean knotOut;
        BezierNetworkManipulator manip;

//        public ManipSubKnot(SelectionRecord rec, BezierNetwork network, BezierEdge edge, KnotType type)
        public ManipSubKnot(NodeObject rec, BezierNetwork network, BezierVertex vtx, boolean knotOut)
        {
            this.rec = rec;
            this.network = network;
//            this.edge = edge;
//            this.type = type;
            this.vtx = vtx;
            this.knotOut = knotOut;
            this.manip = new BezierNetworkManipulator(network);
        }

        @Override
        protected void dragBy(int dx, int dy)
        {
            RavenNodeXformable nodeSpatial = (RavenNodeXformable)rec;
            AffineTransform devToPath = nodeSpatial.getLocalToDeviceTransform((AffineTransform)null);
            devToPath.concatenate(BezierNetwork.toPixels);
            try
            {
                devToPath.invert();
            } catch (NoninvertibleTransformException ex)
            {
                Logger.getLogger(ToolSubselect.class.getName()).log(Level.SEVERE, null, ex);
            }

            Point2D.Double vec = new Point2D.Double(dx, dy);
            devToPath.transform(vec, vec);
            vec.x -= devToPath.getTranslateX();
            vec.y -= devToPath.getTranslateY();

            if (knotOut)
            {
                BezierEdge edgeOut = vtx.getEdgeOut(0);
                if (edgeOut != null)
                {
//                    manip.offsetKnotFromInitBy(edgeOut, KnotType.START, dx, dy);
                    manip.offsetStartKnotBy(edgeOut, (int)vec.x, (int)vec.y);

                    VertexSmooth smooth = vtx.getData(VertexSmooth.PlaneData.class);
                    if (smooth == VertexSmooth.SMOOTH)
                    {
                        BezierEdge edgeIn = vtx.getEdgeIn(0);
                        if (edgeOut != null)
                        {
                            manip.alignSmoothKnotIn(edgeIn, edgeOut);
                        }
                    }
                }
            }
            else
            {
                BezierEdge edgeIn = vtx.getEdgeIn(0);
                if (edgeIn != null)
                {
                    manip.offsetEndKnotBy(edgeIn, (int)vec.x, (int)vec.y);

                    VertexSmooth smooth = vtx.getData(VertexSmooth.PlaneData.class);
                    if (smooth == VertexSmooth.SMOOTH)
                    {
                        BezierEdge edgeOut = vtx.getEdgeOut(0);
                        if (edgeOut != null)
                        {
                            manip.alignSmoothKnotOut(edgeIn, edgeOut);
                        }
                    }
                }
            }

            //manip.offsetKnotFromInitBy(edge, type, (int)vec.x, (int)vec.y);

        }

        @Override
        protected void render(Graphics2D g, Color color)
        {
            RavenNodeXformable nodeSpatial = (RavenNodeXformable)rec;
            AffineTransform l2d = nodeSpatial.getLocalToDeviceTransform((AffineTransform)null);

            ServiceDocument provider = user.getToolService(ServiceDocument.class);
            Selection<NodeObject> selection = provider.getSelection();
            BezierNetwork.Subselection subselection =
                    selection.getSubselection(rec, BezierNetwork.Subselection.class);

            manip.renderManipulator(g, subselection, l2d, color);
        }

        @Override
        protected void apply(boolean history)
        {
            manip.apply();

            ServiceBezierNetwork provBez =
                    rec.getNodeService(ServiceBezierNetwork.class, false);

            //network.toSVGPath();
            provBez.updateFromBezierNetwork(network, history);
//            network.toSerialForm();
        }
    }

    class ManipSubVertices extends ManipSub
    {
        NodeObject rec;
        BezierNetwork network;
        List<BezierVertex> vertices;
        BezierNetworkManipulator manip;

        public ManipSubVertices(NodeObject rec, BezierNetwork network, List<BezierVertex> vertices)
        {
            this.rec = rec;
            this.network = network;
            this.vertices = vertices;
            this.manip = new BezierNetworkManipulator(network);
        }

        public ManipSubVertices(NodeObject rec, BezierNetwork network, BezierVertex vtx)
        {
            this(rec, network, Arrays.asList(vtx));
        }

        @Override
        protected void dragBy(int dx, int dy)
        {
            RavenNodeXformable nodeSpatial = (RavenNodeXformable)rec;
            AffineTransform devToPath = nodeSpatial.getLocalToDeviceTransform((AffineTransform)null);
            devToPath.concatenate(BezierNetwork.toPixels);
            try
            {
                devToPath.invert();
            } catch (NoninvertibleTransformException ex)
            {
                Logger.getLogger(ToolSubselect.class.getName()).log(Level.SEVERE, null, ex);
            }

            Point2D.Double vec = new Point2D.Double(dx, dy);
            devToPath.transform(vec, vec);
            vec.x -= devToPath.getTranslateX();
            vec.y -= devToPath.getTranslateY();

            for (BezierVertex vtx: vertices)
            {
                manip.offsetVertexFromInitBy(vtx, (int)vec.x, (int)vec.y);
                for (int i = 0; i < vtx.getNumEdgesOut(); ++i)
                {
                    BezierEdge edge = vtx.getEdgeOut(i);
                    manip.offsetStartKnotBy(edge,
                            (int)vec.x,
                            (int)vec.y);
                }
                for (int i = 0; i < vtx.getNumEdgesIn(); ++i)
                {
                    BezierEdge edge = vtx.getEdgeIn(i);
                    manip.offsetEndKnotBy(edge,
                            (int)vec.x,
                            (int)vec.y);
                }
            }

            for (BezierVertex vtx: network.getVertices())
            {
                BezierEdge edgeIn = vtx.getEdgeIn(0);
                BezierEdge edgeOut = vtx.getEdgeOut(0);

                if (edgeIn == null || edgeOut == null)
                {
                    continue;
                }

                VertexSmooth smooth = vtx.getData(VertexSmooth.PlaneData.class);
                manip.smooth(smooth, edgeIn, edgeOut);
            }
        }

        @Override
        protected void render(Graphics2D g, Color color)
        {
            RavenNodeXformable nodeSpatial = (RavenNodeXformable)rec;
            AffineTransform l2d = nodeSpatial.getLocalToDeviceTransform((AffineTransform)null);

            ServiceDocument provider = user.getToolService(ServiceDocument.class);
            Selection<NodeObject> selection = provider.getSelection();
            BezierNetwork.Subselection subselection =
                    selection.getSubselection(rec, BezierNetwork.Subselection.class);

            manip.renderManipulator(g, subselection, l2d, color);
        }

        @Override
        protected void apply(boolean history)
        {
            manip.apply();
            ServiceBezierNetwork provBez =
                    rec.getNodeService(ServiceBezierNetwork.class, false);

            provBez.updateFromBezierNetwork(network, history);
        }
    }

    class ManipSubVerticesGroup extends ManipSub
    {
        ArrayList<ManipSubVertices> groups;

        public ManipSubVerticesGroup(ArrayList<ManipSubVertices> groups)
        {
            this.groups = groups;
        }

        @Override
        protected void dragBy(int dx, int dy)
        {
            for (ManipSubVertices group: groups)
            {
                group.dragBy(dx, dy);
            }
        }

        @Override
        protected void render(Graphics2D g, Color color)
        {
            for (ManipSubVertices group: groups)
            {
                AffineTransform xform = g.getTransform();
                group.render(g, color);
                g.setTransform(xform);
            }
        }

        @Override
        protected void apply(boolean history)
        {
            if (groups.isEmpty())
            {
                return;
            }

            ServiceDocument provider = user.getToolService(ServiceDocument.class);
            if (provider != null)
            {
                provider.getDocument().getHistory().beginTransaction("Move mesh point groups");
            }
            
            for (ManipSubVertices group: groups)
            {
                group.apply(history);
            }

            if (provider != null)
            {
                provider.getDocument().getHistory().commitTransaction();
            }
        }
    }

    @ServiceInst(service=ToolProvider.class)
    static public class Provider extends ToolProvider<ToolSubselect>
    {
        public Provider()
        {
            super("Sub-Select", "/icons/tools/subselect.png", "/manual/tools/subselect.html");
        }

        @Override
        public ToolSubselect create(ToolUser user)
        {
            return new ToolSubselect(user, this);
        }

        @Override
        public Component createToolSettingsEditor(RavenEditor editor)
        {
            return new ToolSubselectSettings(editor, this);
        }
    }

}
