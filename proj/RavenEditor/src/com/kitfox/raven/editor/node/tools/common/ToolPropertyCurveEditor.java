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

import com.kitfox.raven.editor.node.tools.ToolProvider;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.util.tree.PropertyDataInline;
import com.kitfox.raven.util.tree.PropertyProvider;
import com.kitfox.raven.util.tree.PropertyProviderIndex;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.Track;
import com.kitfox.raven.util.tree.TrackCurve;
import com.kitfox.raven.util.tree.TrackCurveComponent;
import com.kitfox.raven.util.tree.TrackCurveComponentCurve;
import com.kitfox.raven.util.tree.TrackCurveComponentKey;
import com.kitfox.raven.util.tree.TrackKey;
import com.kitfox.raven.util.tree.TrackKey.Interp;
import com.kitfox.raven.util.undo.History;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPopupMenu;

/**
 *
 * @author kitfox
 */
public class ToolPropertyCurveEditor extends ToolDisplay
{
    private static final int POINT_RADIUS = 3;
    MouseEvent startEvt;
    MouseEvent endEvt;

    Manipulator manip;
//    ControlPointDragSet dragSet;

//    Point2D.Double pt0 = new Point2D.Double();
//    Point2D.Double pt1 = new Point2D.Double();
//    Matrix3f xformDevToLayer = new Matrix3f();

    private static final double MAX_TAN_ANGLE = Math.toRadians(89);
    private static final double MIN_TAN_ANGLE = Math.toRadians(-89);

    public static final int TANGENT_HANDLE_LENGTH = 40;

    protected ToolPropertyCurveEditor(ToolUser user)
    {
        super(user);
    }

    private ArrayList<TrackCurveComponentKey> pickKnots(
            Selection<TrackCurveComponent> sel, Rectangle selectRect,
            boolean knotsOut)
    {
        ServiceControlPointEditor provider = user.getToolService(ServiceControlPointEditor.class);
        if (provider == null)
        {
            return null;
        }

        Track track = provider.getTrack();
        if (track == null)
        {
            return null;
        }

        NodeDocument doc = provider.getDocument();
        AffineTransform w2d = provider.getWorldToDeviceTransform(null);
        ArrayList<TrackCurveComponentKey> pickList = new ArrayList<TrackCurveComponentKey>();

        List<TrackCurveComponentKey> selKeys = sel.getSelection(TrackCurveComponentKey.class, null);
        for (TrackCurveComponentKey keyComp: selKeys)
        {
            int frame = keyComp.getFrame();
            PropertyWrapper wrapper = keyComp.getWrapper();
            PointInfo ptInfo = new PointInfo(
                    frame, doc, w2d, track, wrapper);

            if (knotsOut)
            {
                if (ptInfo.k1Dev != null && selectRect.contains(ptInfo.k1Dev))
                {
                    pickList.add(new TrackCurveComponentKey(wrapper, frame));
                }
            }
            else
            {
                if (ptInfo.k0Dev != null && selectRect.contains(ptInfo.k0Dev))
                {
                    pickList.add(new TrackCurveComponentKey(wrapper, frame));
                }
            }            
        }

        return pickList;
    }

    protected ArrayList<TrackCurveComponentKey> pickKeys(Rectangle selectRect)
    {
        ServiceControlPointEditor provider = user.getToolService(ServiceControlPointEditor.class);
        if (provider == null)
        {
            return null;
        }

        Track track = provider.getTrack();
        if (track == null)
        {
            return null;
        }

        NodeDocument doc = provider.getDocument();

        ArrayList<PropertyWrapper> wrappers = provider.getEditableProperties();

        ArrayList<TrackCurveComponentKey> pickList = new ArrayList<TrackCurveComponentKey>();
        AffineTransform w2d = provider.getWorldToDeviceTransform(null);
        for (PropertyWrapper wrapper: wrappers)
        {
            TrackCurve trackCurve = wrapper.getTrackCurve(track.getUid());
            for (Integer frame: (ArrayList<Integer>)trackCurve.getFrames())
            {
                PointInfo ptInfo = new PointInfo(
                        frame, doc, w2d, track, wrapper);

                if (selectRect.contains(ptInfo.ptDev))
                {
                    pickList.add(new TrackCurveComponentKey(
                            wrapper, frame));
                }

//                if (ptInfo.k0Dev != null && selectRect.contains(ptInfo.k0Dev))
//                {
//                    pickList.add(new TrackCurveComponent(
//                            wrapper, frame, TrackCurveComponent.Type.KNOT_IN));
//                }
//
//                if (ptInfo.k1Dev != null && selectRect.contains(ptInfo.k1Dev))
//                {
//                    pickList.add(new TrackCurveComponent(
//                            wrapper, frame, TrackCurveComponent.Type.KNOT_OUT));
//                }
            }
        }

        return pickList;
    }

    protected ArrayList<TrackCurveComponentCurve> pickCurves(Rectangle selectRect)
    {
        ServiceControlPointEditor provider = user.getToolService(ServiceControlPointEditor.class);
        if (provider == null)
        {
            return null;
        }

        Track track = provider.getTrack();
        if (track == null)
        {
            return null;
        }

        NodeDocument doc = provider.getDocument();

        ArrayList<PropertyWrapper> wrappers = provider.getEditableProperties();

        ArrayList<TrackCurveComponentCurve> pickList = new ArrayList<TrackCurveComponentCurve>();
        AffineTransform w2d = provider.getWorldToDeviceTransform(null);

        for (PropertyWrapper wrapper: wrappers)
        {
            TrackCurve trackCurve = wrapper.getTrackCurve(track.getUid());
            Path2D.Double curve = trackCurve.getCurvePath(doc);
            Path2D.Double dcurve = (Path2D.Double)
                    w2d.createTransformedShape(curve);

            if (intersection(dcurve, selectRect))
            {
                pickList.add(new TrackCurveComponentCurve(wrapper));
            }
        }

        return pickList;
    }

//    private ArrayList<TrackCurveComponent> buildSelectionList(
//            Selection<TrackCurveComponent> sel, TrackCurveComponent comp)
//    {
//        ArrayList<TrackCurveComponent> menuList = new ArrayList<TrackCurveComponent>();
//
//        if (sel.isSelected(comp))
//        {
//            for (TrackCurveComponent selComp: sel.getSelection())
//            {
//                if (selComp.getType() == comp.getType())
//                {
//                    menuList.add(selComp);
//                }
//            }
//            return menuList;
//        }
//
//        menuList.add(comp);
//        return menuList;
//    }

    @Override
    protected void click(MouseEvent evt)
    {
        ServiceControlPointEditor provider = user.getToolService(ServiceControlPointEditor.class);
        if (provider == null)
        {
            return;
        }

        boolean popup = evt.getButton() == MouseEvent.BUTTON3;
        Selection<TrackCurveComponent> sel = provider.getSelection();

        Rectangle selectRect = new Rectangle(evt.getX() - POINT_RADIUS,
                evt.getY() - POINT_RADIUS,
                POINT_RADIUS * 2, POINT_RADIUS * 2);

//        ArrayList<TrackCurveComponent> pickList = pick(selectRect);
        ArrayList<TrackCurveComponentKey> pickListKeys = pickKeys(selectRect);
        ArrayList<TrackCurveComponentCurve> pickListCurves = pickCurves(selectRect);

        ArrayList<TrackCurveComponentKey> pickListKnotsIn = pickKnots(sel, selectRect, false);
        ArrayList<TrackCurveComponentKey> pickListKnotsOut = pickKnots(sel, selectRect, true);

        if (pickListKeys.isEmpty() && pickListCurves.isEmpty()
                && pickListKnotsIn.isEmpty() && pickListKnotsOut.isEmpty())
        {
            if (!popup)
            {
                sel.clear();
                fireToolDisplayChanged();
            }
            return;
        }


        if (popup && !pickListKeys.isEmpty())
        {
            ArrayList<TrackCurveComponentKey> menuList = new ArrayList<TrackCurveComponentKey>();
            TrackCurveComponentKey comp = pickListKeys.get(0);
            if (sel.isSelected(comp))
            {
                sel.getSelection(TrackCurveComponentKey.class, menuList);
            }
            else
            {
                menuList.add(comp);
            }

//            ArrayList<TrackCurveComponent> menuList =
//                    buildSelectionList(sel, comp);

            JPopupMenu menu = provider.getPopupMenuKeys(menuList);
            if (menu != null)
            {
                menu.show(evt.getComponent(), evt.getX(), evt.getY());
            }
            return;
        }

        Selection.Operator selType = Selection.suggestSelectType(evt);

        if (!pickListKeys.isEmpty())
        {
            sel.select(pickListKeys.get(0), selType);
        }
        else if (!pickListKnotsIn.isEmpty())
        {
            TrackCurveComponentKey key = pickListKnotsIn.get(0);
            TrackCurveComponentKey.Subselect sub =
                    sel.getSubselection(key, TrackCurveComponentKey.Subselect.class);
            if (sub == null)
            {
                sub = new TrackCurveComponentKey.Subselect();
            }

            sub = sub.selectKnotIn(selType);
            sel.setSubselection(key,
                    TrackCurveComponentKey.Subselect.class, sub);
        }
        else if (!pickListKnotsOut.isEmpty())
        {
            TrackCurveComponentKey key = pickListKnotsOut.get(0);
            TrackCurveComponentKey.Subselect sub =
                    sel.getSubselection(key, TrackCurveComponentKey.Subselect.class);
            if (sub == null)
            {
                sub = new TrackCurveComponentKey.Subselect();
            }

            sub = sub.selectKnotOut(selType);
            sel.setSubselection(key,
                    TrackCurveComponentKey.Subselect.class, sub);
        }
        else if (pickListCurves.isEmpty())
        {
            sel.select(pickListCurves.get(0), selType);
        }

        fireToolDisplayChanged();
    }

    @Override
    protected void startDrag(MouseEvent evt)
    {
        ServiceControlPointEditor provider = user.getToolService(ServiceControlPointEditor.class);
        if (provider == null)
        {
            return;
        }

        Selection<TrackCurveComponent> sel = provider.getSelection();
        
        Rectangle selectRect = new Rectangle(evt.getX() - POINT_RADIUS,
                evt.getY() - POINT_RADIUS,
                POINT_RADIUS * 2, POINT_RADIUS * 2);

        startEvt = endEvt = evt;

        NodeDocument doc = provider.getDocument();
        AffineTransform w2d = provider.getWorldToDeviceTransform(null);
        Track track = provider.getTrack();

        ArrayList<TrackCurveComponentKey> pickListKeys = pickKeys(selectRect);
        if (!pickListKeys.isEmpty())
        {
            TrackCurveComponentKey comp = pickListKeys.get(0);
            ArrayList<TrackCurveComponentKey> pickList = new ArrayList<TrackCurveComponentKey>();
            boolean selected;
            if (sel.isSelected(comp))
            {
                sel.getSelection(TrackCurveComponentKey.class, pickList);
                selected = true;
            }
            else
            {
                pickList.add(comp);
                selected = false;
            }

            Manipulator newManip = new Manipulator(sel, ManipType.KEY);
            for (TrackCurveComponentKey curComp: pickList)
            {
                TrackCurveComponentKey.Subselect sub =
                        sel.getSubselection(curComp, TrackCurveComponentKey.Subselect.class);
                if (sub == null)
                {
                    sub = new TrackCurveComponentKey.Subselect();
                    sel.setSubselection(comp,
                            TrackCurveComponentKey.Subselect.class, sub);
                }

                newManip.add(new ManipKey(doc, w2d, track,
                        curComp, selected, sub.isKnotIn(), sub.isKnotOut()));
            }
            manip = newManip;
            return;
        }

        ArrayList<TrackCurveComponentKey> pickListKnotsIn = pickKnots(sel, selectRect, false);
        if (!pickListKnotsIn.isEmpty())
        {
            TrackCurveComponentKey key = pickListKnotsIn.get(0);
            Manipulator newManip = new Manipulator(sel, ManipType.KNOT_IN);
            TrackCurveComponentKey.Subselect sub =
                    sel.getSubselection(key, TrackCurveComponentKey.Subselect.class);
            newManip.add(new ManipKey(doc, w2d, track,
                        key, true, sub.isKnotIn(), sub.isKnotOut()));
            manip = newManip;
            return;
        }

        ArrayList<TrackCurveComponentKey> pickListKnotsOut = pickKnots(sel, selectRect, true);
        if (!pickListKnotsOut.isEmpty())
        {
            TrackCurveComponentKey key = pickListKnotsOut.get(0);
            Manipulator newManip = new Manipulator(sel, ManipType.KNOT_OUT);
            TrackCurveComponentKey.Subselect sub =
                    sel.getSubselection(key, TrackCurveComponentKey.Subselect.class);
            newManip.add(new ManipKey(doc, w2d, track,
                        key, true, sub.isKnotIn(), sub.isKnotOut()));
            manip = newManip;
            return;
        }


        //Dragging selection
        manip = null;
    }

    @Override
    protected void dragTo(MouseEvent evt)
    {
        ServiceControlPointEditor provider = user.getToolService(ServiceControlPointEditor.class);
        if (provider == null)
        {
            return;
        }

        endEvt = evt;

        if (manip != null)
        {
            manip.dragBy(evt.getX() - startEvt.getX(),
                    evt.getY() - startEvt.getY(), false);
        }

        fireToolDisplayChanged();
    }

    @Override
    protected void endDrag(MouseEvent evt)
    {
        ServiceControlPointEditor provider = user.getToolService(ServiceControlPointEditor.class);
        if (provider == null)
        {
            return;
        }

        if (manip != null)
        {
            //Finish dragging manipulator
            manip.dragBy(evt.getX() - startEvt.getX(),
                    evt.getY() - startEvt.getY(), true);
            manip = null;
        }
        else
        {
            //Select drag region
            int x0 = Math.min(startEvt.getX(), endEvt.getX());
            int y0 = Math.min(startEvt.getY(), endEvt.getY());
            int x1 = Math.max(startEvt.getX(), endEvt.getX());
            int y1 = Math.max(startEvt.getY(), endEvt.getY());
            Rectangle selectRect = new Rectangle(
                    x0, y0, x1 - x0, y1 - y0);

            ArrayList<TrackCurveComponentKey> pickList = pickKeys(selectRect);

            Selection<TrackCurveComponent> sel = provider.getSelection();

            Selection.Operator selType = Selection.suggestSelectType(evt);
            sel.select(pickList, selType);
            fireToolDisplayChanged();
        }

        //Clean up
        startEvt = endEvt = null;
        fireToolDisplayChanged();
    }

    @Override
    public void cancel()
    {
        manip.dragBy(0, 0, false);

        manip = null;
        startEvt = endEvt = null;
        fireToolDisplayChanged();
    }

    @Override
    public void paint(Graphics2D g)
    {
        ServiceControlPointEditor provider = user.getToolService(ServiceControlPointEditor.class);
        if (provider == null)
        {
            return;
        }

        if (manip == null && startEvt != null)
        {
            //Bounding rect
            g.setPaint(MaskPaint.inst().getPaint());

            int x0 = Math.min(startEvt.getX(), endEvt.getX());
            int y0 = Math.min(startEvt.getY(), endEvt.getY());
            int x1 = Math.max(startEvt.getX(), endEvt.getX());
            int y1 = Math.max(startEvt.getY(), endEvt.getY());

            g.drawRect(x0, y0, x1 - x0, y1 - y0);
        }
    }

    @Override
    public void keyPressed(KeyEvent evt)
    {
        ServiceControlPointEditor provider = user.getToolService(ServiceControlPointEditor.class);
        if (provider == null)
        {
            return;
        }

        Track track = provider.getTrack();
        NodeDocument doc = provider.getDocument();

        switch (evt.getKeyCode())
        {
            case KeyEvent.VK_DELETE:
            {
                History hist = doc.getHistory();
                hist.beginTransaction("Delete track components");

                Selection<TrackCurveComponent> sel = provider.getSelection();
                List<TrackCurveComponentCurve> curveList = sel.getSelection(
                        TrackCurveComponentCurve.class, null);
                List<TrackCurveComponentKey> keyList = sel.getSelection(
                        TrackCurveComponentKey.class, null);

                {
                    HashMap<PropertyWrapper, TrackCurve> trackCache =
                            new HashMap<PropertyWrapper, TrackCurve>();
                    for (TrackCurveComponentKey key: keyList)
                    {
                        PropertyWrapper wrap = key.getWrapper();
                        TrackCurve curve = trackCache.get(wrap);
                        if (curve == null)
                        {
                            curve = wrap.getTrackCurve(track.getUid());
                            trackCache.put(wrap, curve);
                        }
                        curve.removeKey(key.getFrame());
                    }

                    for (PropertyWrapper wrap: trackCache.keySet())
                    {
                        TrackCurve curve = trackCache.get(wrap);
                        wrap.setTrackCurve(track.getUid(), curve);
                    }
                }

                for (TrackCurveComponentCurve curve: curveList)
                {
                    PropertyWrapper wrap = curve.getWrapper();
                    wrap.deleteTrackCurve(track.getUid());
                }

                hist.commitTransaction();

                fireToolDisplayChanged();
                break;
            }
            default:
                super.keyPressed(evt);
        }
    }

    private boolean intersection(Path2D.Double dcurve, Rectangle selectRect)
    {
        double[] coords = new double[6];
        Line2D.Double line = new Line2D.Double();

        double px = 0, py = 0;
        for (PathIterator it = dcurve.getPathIterator(null);
            it.isDone(); it.next())
        {
            switch (it.currentSegment(coords))
            {
                case PathIterator.SEG_MOVETO:
                    px = coords[0];
                    py = coords[1];
                    break;
                case PathIterator.SEG_LINETO:
                {
                    double x = coords[0];
                    double y = coords[1];
                    line.setLine(px, py, x, y);

                    if (line.intersects(selectRect))
                    {
                        return true;
                    }

                    px = x;
                    py = y;
                    break;
                }
            }
        }
        return false;
    }

    @Override
    public void dispose()
    {
    }

    //---------------------------------------
//    public static interface ControlPointDragSet
//    {
//        /**
//         * Set translation relative to positions coordinates occupied
//         * when this set was first created.
//         * @param x
//         * @param y
//         */
//        public void setTranslation(float x, float y);
//
//        public void commit();
//
//        public void cancel();
//    }
    class Manipulator
    {
        ManipType manipType;
        HashMap<PropertyWrapper, ManipPropCurve> manipMap =
                new HashMap<PropertyWrapper, ManipPropCurve>();
        Selection<TrackCurveComponent> selection;
        Selection<TrackCurveComponent> selBase;

        public Manipulator(Selection<TrackCurveComponent> sel, ManipType manipType)
        {
            this.manipType = manipType;
            this.selection = sel;
            selBase = new Selection<TrackCurveComponent>(sel);
        }

        public void add(ManipKey manip)
        {
            selBase.select(manip.comp, Selection.Operator.SUB);

            PropertyWrapper wrapper = manip.comp.getWrapper();
            ManipPropCurve propManip = manipMap.get(wrapper);
            if (propManip == null)
            {
                propManip = new ManipPropCurve(manip.startLayout.track,
                        wrapper);
                manipMap.put(wrapper, propManip);
            }

            propManip.addManip(manip);
        }

        protected void dragBy(int dx, int dy, boolean history)
        {
            Selection<TrackCurveComponent> selNew =
                    new Selection<TrackCurveComponent>(selBase);

            for (ManipPropCurve manip: manipMap.values())
            {
                manip.dragBy(selNew, manipType, dx, dy, history);
            }

            selection.set(selNew);
        }
    }

    static enum ManipType
    {
        KEY, KNOT_IN, KNOT_OUT
    }

    class ManipPropCurve
    {
        TrackCurve trackCurveStart;
        TrackCurve trackCurveBase;
        PropertyWrapper wrapper;
        Track track;

        ArrayList<ManipKey> manipList = new ArrayList<ManipKey>();

        public ManipPropCurve(Track track, PropertyWrapper wrapper)
        {
            this.track = track;
            this.wrapper = wrapper;

            trackCurveStart = wrapper.getTrackCurve(track.getUid());
            trackCurveBase = wrapper.getTrackCurve(track.getUid());
        }

        public void addManip(ManipKey manip)
        {
            trackCurveBase.removeKey(manip.comp.getFrame());
            manipList.add(manip);
        }

        private void dragBy(Selection<TrackCurveComponent> selection,
                ManipType manipType, int dx, int dy, boolean history)
        {
            TrackCurve newCurve = new TrackCurve(trackCurveBase);

            for (ManipKey manip: manipList)
            {
                manip.dragBy(selection, manipType, newCurve, dx, dy);
            }

            wrapper.setTrackCurve(track.getUid(), newCurve, history);
        }
    }

    class ManipKey
    {
        PointInfo startLayout;
        TrackCurveComponentKey comp;
        AffineTransform d2w;
        boolean selected;
        boolean knotIn;
        boolean knotOut;

        public ManipKey(NodeDocument doc, AffineTransform w2d, Track track, 
                TrackCurveComponentKey comp,
                boolean selected, boolean knotIn, boolean knotOut)
        {
            startLayout = new PointInfo(comp.getFrame(), doc, w2d, track, comp.getWrapper());
            this.comp = comp;
            this.selected = selected;
            this.knotIn = knotIn;
            this.knotOut = knotOut;
            d2w = new AffineTransform(w2d);
            try
            {
                d2w.invert();
            } catch (NoninvertibleTransformException ex)
            {
                Logger.getLogger(ToolPropertyCurveEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private void dragBy(Selection<TrackCurveComponent> selection,
                ManipType manipType, TrackCurve newCurve, int dx, int dy)
        {
            TrackKey keyStart = startLayout.key;

            switch (manipType)
            {
                case KNOT_IN:
                {
                    Point2D.Double newKnot = new Point2D.Double(
                            startLayout.k0Dev.x + dx,
                            startLayout.k0Dev.y + dy);
                    d2w.transform(newKnot, newKnot);

                    double angle = Math.atan2(
                            startLayout.value - newKnot.y,
                            startLayout.frame - newKnot.x);
                    angle = Math.min(angle, MAX_TAN_ANGLE);
                    angle = Math.max(angle, MIN_TAN_ANGLE);

                    double k0x = Math.cos(angle);
                    double k0y = Math.sin(angle);
                    double k1x = keyStart.getTanOutX();
                    double k1y = keyStart.getTanOutY();

                    Interp interp = keyStart.getInterp();
                    if (interp == Interp.SMOOTH)
                    {
                        k1x = k0x;
                        k1y = k0y;
                    }

                    TrackKey keyNew = new TrackKey(keyStart.getData(),
                            interp,
                            k0x, k0y, k1x, k1y);

                    newCurve.setKey(startLayout.frame, keyNew);

                    //Selection
                    if (selected)
                    {
                        TrackCurveComponentKey compKey =
                                new TrackCurveComponentKey(comp.getWrapper(), startLayout.frame);
                        selection.select(compKey, Selection.Operator.ADD);
                        selection.setSubselection(compKey,
                                TrackCurveComponentKey.Subselect.class,
                                new TrackCurveComponentKey.Subselect(knotIn, knotOut));
                    }
                    break;
                }
                case KNOT_OUT:
                {
                    Point2D.Double newKnot = new Point2D.Double(
                            startLayout.k1Dev.x + dx,
                            startLayout.k1Dev.y + dy);
                    d2w.transform(newKnot, newKnot);

                    double angle = Math.atan2(
                            newKnot.y - startLayout.value,
                            newKnot.x - startLayout.frame);
                    angle = Math.min(angle, MAX_TAN_ANGLE);
                    angle = Math.max(angle, MIN_TAN_ANGLE);

                    double k1x = Math.cos(angle);
                    double k1y = Math.sin(angle);
                    double k0x = keyStart.getTanInX();
                    double k0y = keyStart.getTanInY();

                    Interp interp = keyStart.getInterp();
                    if (interp == Interp.SMOOTH)
                    {
                        k0x = k1x;
                        k0y = k1y;
                    }

                    TrackKey keyNew = new TrackKey(keyStart.getData(),
                            interp,
                            k0x, k0y, k1x, k1y);

                    newCurve.setKey(startLayout.frame, keyNew);

                    //Selection
                    if (selected)
                    {
                        TrackCurveComponentKey compKey =
                                new TrackCurveComponentKey(comp.getWrapper(), startLayout.frame);
                        selection.select(compKey, Selection.Operator.ADD);
                        selection.setSubselection(compKey,
                                TrackCurveComponentKey.Subselect.class,
                                new TrackCurveComponentKey.Subselect(knotIn, knotOut));
                    }
                    break;
                }
                case KEY:
                {
                    Point2D.Double newPt = new Point2D.Double(
                            startLayout.ptDev.x + dx,
                            startLayout.ptDev.y + dy);

                    d2w.transform(newPt, newPt);
                    int newFrame = (int)newPt.x;

                    PropertyWrapper wrapper = comp.getWrapper();
                    PropertyProvider prov = PropertyProviderIndex.inst()
                            .getProviderBest(wrapper.getPropertyType());
                    Object value = prov.createNumericValue(wrapper, newPt.y);

                    Interp interp = keyStart.getInterp();
                    TrackKey keyNew = new TrackKey(new PropertyDataInline(value),
                            interp,
                            keyStart.getTanInX(), keyStart.getTanInY(),
                            keyStart.getTanOutX(), keyStart.getTanOutY());

                    newCurve.setKey(newFrame, keyNew);
                    

                    //Selection
                    if (selected)
                    {
                        TrackCurveComponentKey compKey =
                                new TrackCurveComponentKey(comp.getWrapper(), newFrame);
                        selection.select(compKey, Selection.Operator.ADD);
                        selection.setSubselection(compKey,
                                TrackCurveComponentKey.Subselect.class,
                                new TrackCurveComponentKey.Subselect(knotIn, knotOut));
                    }
                    break;
                }
            }
        }

    }

    class PointInfo
    {
        Point2D.Double ptDev;  //Point location
        Point2D.Double k0Dev;  //Knot in
        Point2D.Double k1Dev;  //Knot out
        int frame;
        double value;
        TrackKey key;

        NodeDocument doc;
        AffineTransform w2d;
        Track track;
        PropertyWrapper wrapper;

        public PointInfo(int frame, NodeDocument doc,
                AffineTransform w2d, Track track, PropertyWrapper wrapper)
        {
            this.frame = frame;
            this.w2d = w2d;
            this.track = track;
            this.wrapper = wrapper;

            TrackCurve trackCurve = wrapper.getTrackCurve(track.getUid());
            this.value = trackCurve.getNumericValue(frame, doc);

            ptDev = new Point2D.Double(frame, value);
            w2d.transform(ptDev, ptDev);

            key = trackCurve.getKey(frame);
//if (key == null)
//{
//    assert false;
//}
            Interp interp = key.getInterp();

            if (interp == Interp.BEZIER
                    || interp == Interp.SMOOTH)
            {
                k0Dev = new Point2D.Double(frame - key.getTanInX(), value - key.getTanInY());
                w2d.transform(k0Dev, k0Dev);

                k1Dev = new Point2D.Double(frame + key.getTanOutX(), value + key.getTanOutY());
                w2d.transform(k1Dev, k1Dev);

                normalizeKnotHandles();
            }
        }

        private void normalizeKnotHandles()
        {
            {
                double dx = k0Dev.x - ptDev.x;
                double dy = k0Dev.y - ptDev.y;
                double mag2 = dx * dx + dy * dy;
                if (mag2 > 0)
                {
                    double scalar = TANGENT_HANDLE_LENGTH / Math.sqrt(mag2);
                    k0Dev = new Point2D.Double(dx * scalar + ptDev.x,
                            dy * scalar + ptDev.y);
                }
            }

            {
                double dx = k1Dev.x - ptDev.x;
                double dy = k1Dev.y - ptDev.y;
                double mag2 = dx * dx + dy * dy;
                if (mag2 > 0)
                {
                    double scalar = TANGENT_HANDLE_LENGTH / Math.sqrt(mag2);
                    k1Dev = new Point2D.Double(dx * scalar + ptDev.x,
                            dy * scalar + ptDev.y);
                }
            }
        }
    }

    @ServiceInst(service=ToolProvider.class)
    static public class Provider extends ToolProvider<ToolPropertyCurveEditor>
    {
        public Provider()
        {
            super("Control Point Editor");
        }

        @Override
        public ToolPropertyCurveEditor create(ToolUser user)
        {
            return new ToolPropertyCurveEditor(user);
        }

//        @Override
//        public Component createToolSettingsEditor(RavenEditor editor)
//        {
//            return new ToolPanSettings(editor);
//        }
    }
}
