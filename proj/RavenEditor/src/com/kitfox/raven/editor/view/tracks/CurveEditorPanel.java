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

/*
 * CurveEditorPanel.java
 *
 * Created on Aug 20, 2009, 10:20:51 PM
 */

package com.kitfox.raven.editor.view.tracks;

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.raven.editor.RavenDocument;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.node.tools.Tool;
import com.kitfox.raven.editor.node.tools.ToolListener;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.editor.node.tools.common.ServiceControlPointEditor;
import com.kitfox.raven.editor.node.tools.common.ServiceDeviceCamera;
import com.kitfox.raven.editor.node.tools.common.ServiceEditor;
import com.kitfox.raven.editor.node.tools.common.ToolPropertyCurveEditor;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.SelectionEvent;
import com.kitfox.raven.util.SelectionListener;
import com.kitfox.raven.util.SelectionSubEvent;
import com.kitfox.raven.util.tree.NodeSymbol;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.Track;
import com.kitfox.raven.util.tree.TrackCurve;
import com.kitfox.raven.util.tree.TrackCurve.Repeat;
import com.kitfox.raven.util.tree.TrackCurveComponent;
import com.kitfox.raven.util.tree.TrackCurveComponentCurve;
import com.kitfox.raven.util.tree.TrackCurveComponentKey;
import com.kitfox.raven.util.tree.TrackKey;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;

/**
 *
 * @author kitfox
 */
public class CurveEditorPanel extends JPanel
        implements SelectionListener,
        ToolUser, ToolListener,
        ServiceDeviceCamera, ServiceControlPointEditor,
        ServiceEditor

{
    final RavenEditor editor;

    final Selection<TrackCurveComponent> selection =
            new Selection<TrackCurveComponent>();

    AffineTransform coordToDeviceXform = new AffineTransform();
    int minLineSpacingX = 40;
    int minLineSpacingY = 22;
    Color gridColor = Color.GRAY;
    Color axisColor = Color.BLACK;
    Color unitTextColor = Color.BLACK;
    Color frameMarkerColor = Color.RED;
    Color vertexColor = Color.BLACK;
    Color vertexColorSelected = Color.WHITE;

    final static BasicStroke strokeDotted = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10, new float[]{0, 3}, 0);
    final static BasicStroke strokeSolid = new BasicStroke();

    Color pointSelectionColor = Color.GREEN;
    Color pointColor = Color.WHITE;

    ArrayList<PropertyWrapper> propertyWrappers;
    private int frame;
    Track track;

    float focusMargin = .1f;

    private Tool tool;


    /** Creates new form CurveEditorPanel */
    public CurveEditorPanel(RavenEditor editor)
    {
        this.editor = editor;
        initComponents();

        coordToDeviceXform.setToScale(1, -1);

        selection.addSelectionListener(this);
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    @Override
    public <T> T getToolService(Class<T> serviceClass)
    {
        //Provide objects tool can use to communicate with this panel
        if (serviceClass.isAssignableFrom(getClass()))
        {
            return (T)this;
        }
        return null;
    }

    @Override
    public <T> void getToolServices(Class<T> serviceClass, ArrayList<T> appendList)
    {
        T service = getToolService(serviceClass);
        if (service != null)
        {
            appendList.add(service);
        }
    }

    @Deprecated
    @Override
    public AffineTransform getWorldToDeviceTransform(AffineTransform xform)
    {
        if (xform == null)
        {
            return new AffineTransform(coordToDeviceXform);
        }
        xform.setTransform(coordToDeviceXform);
        return xform;
    }

    @Deprecated
    @Override
    public void setWorldToDeviceTransform(AffineTransform xform)
    {
        coordToDeviceXform.setTransform(xform);
        repaint();
    }

    @Override
    public CyMatrix4d getWorldToDeviceTransform(CyMatrix4d xform)
    {
        if (xform == null)
        {
            return new CyMatrix4d(coordToDeviceXform);
        }
        xform.setTransform(coordToDeviceXform);
        return xform;
    }

    @Override
    public void setWorldToDeviceTransform(CyMatrix4d xform)
    {
        coordToDeviceXform.setTransform(xform.asAffineTransform());
        repaint();
    }

    public void fitToFrame()
    {
        if (propertyWrappers == null || track == null)
        {
            return;
        }

        int minFrame = Integer.MAX_VALUE;
        int maxFrame = Integer.MIN_VALUE;
        double minValue = Double.POSITIVE_INFINITY;
        double maxValue = Double.NEGATIVE_INFINITY;

        for (PropertyWrapper wrap: propertyWrappers)
        {
            NodeSymbol doc = wrap.getNode().getSymbol();

            TrackCurve curve = wrap.getTrackCurve(track.getUid());
            if (curve == null)
            {
                continue;
            }
            ArrayList<Integer> frames = curve.getFrames();
            for (Integer curFrame: frames)
            {
                double value = curve.getNumericValue(curFrame, doc);

                minFrame = Math.min(curFrame, minFrame);
                maxFrame = Math.max(curFrame, maxFrame);
                minValue = Math.min(value, minValue);
                maxValue = Math.max(value, maxValue);
            }
        }

        if (minFrame > maxFrame || minValue > maxValue)
        {
            return;
        }

        int dFrame = maxFrame - minFrame;
        double dValue = maxValue - minValue;

        double width = dFrame * (1 + focusMargin * 2);
        double height = dValue * (1 + focusMargin * 2);
        float x = minFrame - dFrame * focusMargin;
        double y = minValue - dValue * focusMargin;

        if (!isZero(width) && !isZero(height))
        {
            coordToDeviceXform.setToTranslation(0, getHeight());
            coordToDeviceXform.scale(getWidth() / width, -getHeight() / height);
            coordToDeviceXform.translate(-x, -y);
        }
        else
        {
            //Center on screen, but do not scale
//            MatrixOps.setToTranslation(coordToDeviceXform, 0, getHeight());
//            MatrixOps.translate(coordToDeviceXform, -x, -y);
        }

        repaint();
    }

    private boolean isZero(double value)
    {
        return Math.abs(value) < .0001f;
    }

    @Override
    public void paint(Graphics gg)
    {
        super.paint(gg);
        
        Graphics2D g = (Graphics2D)gg;

        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        //Find window in coord space
        Point2D.Double r0 = new Point2D.Double(0, 0);
        Point2D.Double r1 = new Point2D.Double(getWidth(), getHeight());
        AffineTransform deviceToCoordXform = new AffineTransform(coordToDeviceXform);
        try
        {
            deviceToCoordXform.invert();
        } catch (NoninvertibleTransformException ex)
        {
            Logger.getLogger(CurveEditorPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        deviceToCoordXform.transform(r0, r0);
        deviceToCoordXform.transform(r1, r1);

        if (r0.x > r1.x)
        {
            double tmp = r1.x;
            r1.x = r0.x;
            r0.x = tmp;
        }

        if (r0.y > r1.y)
        {
            double tmp = r1.y;
            r1.y = r0.y;
            r0.y = tmp;
        }

        //Draw grid
        Point2D.Double p0 = new Point2D.Double();
        Point2D.Double p1 = new Point2D.Double();

        //Draw horiz lines
        double unitY = roundUnit(minLineSpacingY 
                / Math.abs(coordToDeviceXform.getScaleY()));
        double offsetY = Math.ceil(r0.y / unitY) * unitY;
        for (double i = offsetY; i < r1.y; i += unitY)
        {
            if (Math.abs(i) < .000001)
            {
                g.setColor(axisColor);
                g.setStroke(strokeSolid);
            }
            else
            {
                g.setColor(gridColor);
                g.setStroke(strokeDotted);
            }
            p0.setLocation(r0.x, i);
            p1.setLocation(r1.x, i);

            coordToDeviceXform.transform(p0, p0);
            coordToDeviceXform.transform(p1, p1);
            g.drawLine((int)p0.x, (int)p0.y, (int)p1.x, (int)p1.y);

            g.setColor(unitTextColor);
            int exp = (int)Math.floor(Math.log10(unitY));
            String value = exp < 0 ? String.format("%." + -exp + "f", i) : "" + (int)i;
            g.drawString(value, (int)p0.x, (int)p0.y);
        }

        //Draw vert lines
        double unitX = roundUnit(minLineSpacingX
                / Math.abs(coordToDeviceXform.getScaleX()));
        double offsetX = Math.ceil(r0.x / unitX) * unitX;
        for (double i = offsetX; i < r1.x; i += unitX)
        {
            if (Math.abs(i) < .000001)
            {
                g.setColor(axisColor);
                g.setStroke(strokeSolid);
            }
            else
            {
                g.setColor(gridColor);
                g.setStroke(strokeDotted);
            }
            p0.setLocation(i, r0.y);
            p1.setLocation(i, r1.y);

            coordToDeviceXform.transform(p0, p0);
            coordToDeviceXform.transform(p1, p1);
            g.drawLine((int)p0.x, (int)p0.y, (int)p1.x, (int)p1.y);

            g.setColor(unitTextColor);
            int exp = (int)Math.floor(Math.log10(unitX));
            String value = exp < 0 ? String.format("%." + -exp + "f", i) : "" + (int)i;
            int width = g.getFontMetrics().stringWidth(value);
            g.drawString(value, (int)p0.x - width / 2, (int)p0.y);
        }

        //Frame marker
        g.setStroke(strokeSolid);
        p0.setLocation(frame, r0.y);
        p1.setLocation(frame, r1.y);
        coordToDeviceXform.transform(p0, p0);
        coordToDeviceXform.transform(p1, p1);
        g.setColor(frameMarkerColor);
        g.drawLine((int)p0.x, (int)p0.y, (int)p1.x, (int)p1.y);

        if (propertyWrappers == null || track == null)
        {
            return;
        }
        
        //Draw curves
//        AffineTransform coordToDeviceAffine = new AffineTransform();
//        MatrixOps.toAffineTransform(coordToDeviceXform, coordToDeviceAffine);
//        Curve[] drawCurves = curves;
//        if (drawCurves != null)
        {
            //Draw curves
            for (int i = 0; i < propertyWrappers.size(); ++i)
            {
                PropertyWrapper wrap = propertyWrappers.get(i);
                TrackCurve curve = wrap.getTrackCurve(track.getUid());
                if (curve == null)
                {
                    continue;
                }

//                Curve curve = drawCurves[i];
                NodeSymbol doc = wrap.getNode().getSymbol();
                Path2D path = curve.getCurvePath(doc);
//                Shape pathDev = path.createTransformedShape(coordToDeviceXform);
                Shape pathDev = coordToDeviceXform.createTransformedShape(path);
                g.setColor(wrap.getDisplayColor());
                g.draw(pathDev);
            }

            //Draw keyframe vertices
            for (int i = 0; i < propertyWrappers.size(); ++i)
            {
                PropertyWrapper wrap = propertyWrappers.get(i);
                TrackCurve curve = wrap.getTrackCurve(track.getUid());
                if (curve == null)
                {
                    continue;
                }

//                Curve curve = drawCurves[i];
                NodeSymbol doc = wrap.getNode().getSymbol();
                for (Integer curFrame: (ArrayList<Integer>)curve.getFrames())
                {
                    double val = curve.getNumericValue(curFrame, doc);
                    Point2D.Double pt = new Point2D.Double(curFrame, val);
                    coordToDeviceXform.transform(pt, pt);

                    TrackCurveComponent compKey = new TrackCurveComponentKey(
                            wrap, curFrame);
                    boolean keySelected = selection.isSelected(compKey);

                    //Draw tangents
                    if (keySelected)
                    {
                        TrackCurveComponentKey.Subselect sub =
                                selection.getSubselection(compKey, TrackCurveComponentKey.Subselect.class);
                        if (sub == null)
                        {
                            //Create default subselection
                            sub = new TrackCurveComponentKey.Subselect();
                            selection.setSubselection(compKey,
                                    TrackCurveComponentKey.Subselect.class, sub);
                        }

                        TrackKey key = curve.getKey(curFrame);

                        //Find position of in knot
                        double dx0 = key.getTanInX();
                        double dy0 = key.getTanInY();
                        Point2D.Double k0 = new Point2D.Double(curFrame - dx0, val - dy0);
                        coordToDeviceXform.transform(k0, k0);
                        Point2D.Double dir0 = new Point2D.Double(k0.x - pt.x, k0.y - pt.y);
                        setToLen(dir0, ToolPropertyCurveEditor.TANGENT_HANDLE_LENGTH);
                        k0.setLocation(pt.x + dir0.x, pt.y + dir0.y);

                        //Draw
                        g.setColor(sub.isKnotIn()
                                ? vertexColorSelected : vertexColor);
                        g.drawLine((int)pt.x, (int)pt.y, (int)k0.x, (int)k0.y);
                        g.fillRect((int)k0.x - 1, (int)k0.y - 1, 3, 3);


                        //Find position of out knot
                        double dx1 = key.getTanOutX();
                        double dy1 = key.getTanOutY();
                        Point2D.Double k1 = new Point2D.Double(curFrame + dx1, val + dy1);
                        coordToDeviceXform.transform(k1, k1);
                        Point2D.Double dir1 = new Point2D.Double(k1.x - pt.x, k1.y - pt.y);
                        setToLen(dir1, ToolPropertyCurveEditor.TANGENT_HANDLE_LENGTH);
                        k1.setLocation(pt.x + dir1.x, pt.y + dir1.y);

                        //Draw
                        g.setColor(sub.isKnotOut()
                                ? vertexColorSelected : vertexColor);
                        g.drawLine((int)pt.x, (int)pt.y, (int)k1.x, (int)k1.y);
                        g.fillRect((int)k1.x - 1, (int)k1.y - 1, 3, 3);
                        
                    }

                    //Draw curve vertex
                    g.setColor(keySelected ? vertexColorSelected : vertexColor);
                    int x = (int)pt.x;
                    int y = (int)pt.y;
                    g.fillRect(x - 1, y - 1, 3, 3);

                }
            }
        }


        //Draw tool
        if (tool != null)
        {
            tool.paint((Graphics2D)g);
        }
    }

    private double roundUnit(double unit)
    {
        //Round up to the nearest 10^x, 2 * 10^x or 5 * 10^x, where x is integer
        double ones = Math.pow(10, Math.ceil(Math.log10(unit)));
        double twos = Math.pow(10, Math.ceil(Math.log10(unit / 2))) * 2;
        double fives = Math.pow(10, Math.ceil(Math.log10(unit / 5))) * 5;

        return Math.min(Math.min(ones, twos), fives);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        requestFocus();
    }//GEN-LAST:event_formMousePressed


    /**
     * @return the frame
     */
    public int getFrame()
    {
        return frame;
    }

    /**
     * @param frame the frame to set
     */
    public void setFrame(int frame)
    {
        this.frame = frame;
        repaint();
    }

    @Override
    public Track getTrack()
    {
        return track;
    }

    public void setTrack(Track track)
    {
        this.track = track;
        repaint();
    }

    public void setPropertyWrappers(ArrayList<PropertyWrapper> propList)
    {
        this.propertyWrappers = propList;
        repaint();
    }

    @Override
    public String getToolTipText(MouseEvent event)
    {
        if (propertyWrappers == null || track == null)
        {
            return null;
        }

        Rectangle region = new Rectangle(
                event.getX() - 3, event.getY() - 3, 6, 6);

        Point2D.Double pt = new Point2D.Double();
        for (PropertyWrapper wrap: propertyWrappers)
        {
            NodeSymbol doc = wrap.getNode().getSymbol();
            TrackCurve curve = wrap.getTrackCurve(track.getUid());
            for (Integer curFrame: (ArrayList<Integer>)curve.getFrames())
            {
                double val = curve.getNumericValue(frame, doc);
                pt.setLocation(frame, val);
                coordToDeviceXform.transform(pt, pt);

                if (region.contains(pt))
                {
                    return String.format(
                            "Frm: %d  Val: %f", curFrame, val);
                }
            }
        }

        return null;
    }


    /**
     * @return the tool
     */
    public Tool getTool()
    {
        return tool;
    }

    /**
     * @param tool the tool to set
     */
    public void setTool(Tool newTool)
    {
        if (tool != null)
        {
            removeMouseListener(tool.getListener(MouseListener.class));
            removeMouseMotionListener(tool.getListener(MouseMotionListener.class));
            removeKeyListener(tool.getListener(KeyListener.class));
            tool.removeToolListener(this);
            tool.dispose();
        }

        tool = newTool;

        if (tool != null)
        {
            //Provide input to tool
            tool.addToolListener(this);
            addMouseListener(tool.getListener(MouseListener.class));
            addMouseMotionListener(tool.getListener(MouseMotionListener.class));
            addKeyListener(tool.getListener(KeyListener.class));
        }

        repaint();
    }

    @Override
    public void toolDisplayChanged(EventObject evt)
    {
        repaint();
    }

    @Override
    public Selection<TrackCurveComponent> getSelection()
    {
        return selection;
    }

    @Override
    public NodeSymbol getDocument()
    {
        RavenDocument doc = editor.getDocument();
        return doc == null ? null : doc.getCurSymbol();
    }

    @Override
    public ArrayList<PropertyWrapper> getEditableProperties()
    {
        return propertyWrappers;
    }

//    @Override
//    public JPopupMenu getPopupMenu(ArrayList<TrackCurveComponent> pickList)
//    {
//        if (pickList.isEmpty())
//        {
//            return null;
//        }
//        TrackCurveComponent comp = pickList.get(0);
//        if (comp.getType() == TrackCurveComponent.Type.KEY)
//        {
//            return getPopupMenuKey(pickList);
//        }
//        if(comp.getType() == TrackCurveComponent.Type.CURVE)
//        {
//            return getPopupMenuCurve(pickList);
//        }
//        return null;
//    }

    @Override
    public JPopupMenu getPopupMenuKeys(ArrayList<TrackCurveComponentKey> pickList)
    {
//        ArrayList<TrackCurveComponentKey> modList = new ArrayList<TrackCurveComponentKey>();

        TrackCurveComponentKey compFirst = pickList.get(0);
        PropertyWrapper wrap = compFirst.getWrapper();
        TrackCurve curve = wrap.getTrackCurve(track.getUid());
        TrackKey key = curve.getKey(compFirst.getFrame());
        TrackKey.Interp interp = key.getInterp();

        //Build menu
        JPopupMenu menu = new JPopupMenu();
        for (TrackKey.Interp interpVal: TrackKey.Interp.values())
        {
            KeyMenuAction action = new KeyMenuAction(interpVal, pickList);
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(action);
            if (interpVal == interp)
            {
                item.setSelected(true);
            }
            menu.add(item);
        }

        return menu;
    }

    @Override
    public JPopupMenu getPopupMenuCurves(ArrayList<TrackCurveComponentCurve> pickList)
    {
//        ArrayList<TrackCurveComponent> modList = new ArrayList<TrackCurveComponent>();

        TrackCurveComponent compFirst = pickList.get(0);
        PropertyWrapper wrap = compFirst.getWrapper();
        TrackCurve curve = wrap.getTrackCurve(track.getUid());
        TrackCurve.Repeat before = curve.getBefore();
        TrackCurve.Repeat after = curve.getAfter();

        //Build menu
        JPopupMenu menu = new JPopupMenu();
        JPopupMenu menuBefore = new JPopupMenu("Before");
        menu.add(menuBefore);
        JPopupMenu menuAfter = new JPopupMenu("After");
        menu.add(menuAfter);

        for (TrackCurve.Repeat repeatVal: TrackCurve.Repeat.values())
        {
            {
                CurveMenuAction action = new CurveMenuAction(repeatVal, pickList, false);
                JCheckBoxMenuItem item = new JCheckBoxMenuItem(action);
                if (repeatVal == before)
                {
                    item.setSelected(true);
                }
                menuBefore.add(item);
            }
            {
                CurveMenuAction action = new CurveMenuAction(repeatVal, pickList, true);
                JCheckBoxMenuItem item = new JCheckBoxMenuItem(action);
                if (repeatVal == after)
                {
                    item.setSelected(true);
                }
                menuAfter.add(item);
            }
        }

        return menu;
    }

    @Override
    public RavenEditor getEditor()
    {
        return editor;
    }

    @Override
    public void selectionChanged(SelectionEvent evt)
    {
        repaint();
    }

    @Override
    public void subselectionChanged(SelectionSubEvent evt)
    {
        repaint();
    }

    private void setToLen(Point2D.Double dir0, int len)
    {
        double mag = Math.sqrt(dir0.x * dir0.x + dir0.y * dir0.y);
        double scalar = len / mag;
        dir0.x *= scalar;
        dir0.y *= scalar;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    //------------------------------
    class CurveMenuAction extends AbstractAction
    {
        Repeat repeat;
        ArrayList<TrackCurveComponentCurve> modList;
        boolean after;

        private CurveMenuAction(Repeat repeat, ArrayList<TrackCurveComponentCurve> modList, boolean after)
        {
            this.repeat = repeat;
            this.modList = modList;
            this.after = after;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            for (TrackCurveComponent comp: modList)
            {
                PropertyWrapper wrap = comp.getWrapper();
                TrackCurve curve = wrap.getTrackCurve(track.getUid());

                if (after)
                {
                    curve.setAfter(repeat);
                }
                else
                {
                    curve.setBefore(repeat);
                }

                wrap.setTrackCurve(track.getUid(), curve);
            }

            repaint();
        }
    }

    class KeyMenuAction extends AbstractAction
    {
        TrackKey.Interp interpVal;
        ArrayList<TrackCurveComponentKey> modList;

        private KeyMenuAction(TrackKey.Interp interpVal, ArrayList<TrackCurveComponentKey> modList)
        {
            super(interpVal.name());
            this.interpVal = interpVal;
            this.modList = modList;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            for (TrackCurveComponentKey comp: modList)
            {
                PropertyWrapper wrap = comp.getWrapper();
                TrackCurve curve = wrap.getTrackCurve(track.getUid());

                int curFrame = comp.getFrame();

                TrackKey key = curve.getKey(curFrame);
                double tanInX = 1;
                double tanInY = 0;
                double tanOutX = 1;
                double tanOutY = 0;

                switch (interpVal)
                {
                    case LINEAR:
                    {
//                        wrap.getNextKeyFrame(frame, frame);

                        int prevFrame = curve.getPrevKeyFrame(comp.getFrame());
                        if (prevFrame != Integer.MIN_VALUE)
                        {
                            NodeSymbol doc = wrap.getNode().getSymbol();
                            tanInX = curFrame - prevFrame;
                            tanInY = curve.getNumericValue(curFrame, doc)
                                    - curve.getNumericValue(prevFrame, doc);
                        }

                        int nextFrame = curve.getNextKeyFrame(comp.getFrame());

                        if (nextFrame != Integer.MAX_VALUE)
                        {
                            NodeSymbol doc = wrap.getNode().getSymbol();
                            tanOutX = nextFrame - curFrame;
                            tanOutY = curve.getNumericValue(nextFrame, doc)
                                    - curve.getNumericValue(curFrame, doc);
                        }

                        break;
                    }
                    case SMOOTH:
                        tanInX = tanOutX = key.getTanInX();
                        tanInY = tanOutY = key.getTanInY();
                        break;
                    case BEZIER:
                        tanInX = key.getTanInX();
                        tanInY = key.getTanInY();
                        tanOutX = key.getTanOutX();
                        tanOutY = key.getTanOutY();
                        break;
                }

                TrackKey newKey = new TrackKey(key.getData(), interpVal,
                        tanInX, tanInY, tanOutX, tanOutY);
                curve.setKey(curFrame, newKey);

                wrap.setTrackCurve(track.getUid(), curve);
            }

            repaint();
        }
    }

}
