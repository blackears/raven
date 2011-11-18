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

package com.kitfox.raven.editor.node.tools.common.select;

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.coyote.shape.CyShape;
import com.kitfox.raven.editor.node.scene.RavenNodeXformable;
import com.kitfox.raven.editor.node.tools.common.ServiceTransformable;
import com.kitfox.raven.util.tree.PropertyTrackChangeEvent;
import com.kitfox.raven.util.tree.PropertyTrackKeyChangeEvent;
import com.kitfox.raven.util.tree.PropertyWrapperFloat;
import com.kitfox.raven.util.tree.PropertyWrapperListener;
import com.kitfox.raven.util.tree.PropertyWrapperWeakListener;
import java.awt.Color;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import java.util.List;

/**
 *
 * @author kitfox
 */
abstract public class SelectionManipulator extends Manipulator
        implements PropertyWrapperListener
{
    CyShape path;
    CyRectangle2d bounds;
    CyVector2d pivot;

    public SelectionManipulator()
    {
    }

    abstract protected List<RootState> getRoots();

    public SelectionManipulatorHandle getManipulatorHandle(
            double x, double y, CyMatrix4d w2d)
    {
        if (bounds == null)
        {
            return null;
        }

        CyRectangle2d cBounds = new CyRectangle2d(this.bounds);
        CyVector2d cPivot = new CyVector2d(this.pivot);

        CyVector2d cursorPt = new CyVector2d(x, y);

        CyVector2d refPt = new CyVector2d();

        //Check pivot
        {
            refPt.set(cPivot);
            w2d.transformPoint(refPt);
            CyRectangle2d pivotRegion = new CyRectangle2d(
                    refPt.getX() - DRAG_HANDLE_HALF,
                    refPt.getY() - DRAG_HANDLE_HALF,
                    DRAG_HANDLE_SIZE, DRAG_HANDLE_SIZE);
            if (pivotRegion.contains(cursorPt))
            {
                return new SelectionManipulatorHandle(
                        cursorPt, w2d, 
                        SelectionManipulatorHandle.Type.PIVOT,
                        cBounds, cPivot, 0, 0);
            }
        }

        //Check bounds handles
        {
            CyVector2d xDir = new CyVector2d(w2d.m00, w2d.m10);
            CyVector2d yDir = new CyVector2d(w2d.m01, w2d.m11);

            CyRectangle2d scaleRegion = new CyRectangle2d(
                    0, 0, DRAG_HANDLE_SIZE, DRAG_HANDLE_SIZE);
            CyRectangle2d rotRegion = new CyRectangle2d(
                    0, 0, DRAG_HANDLE_SIZE, DRAG_HANDLE_SIZE);

            double width = cBounds.getWidth();
            double height = cBounds.getHeight();
            double cx = cBounds.getCenterX();
            double cy = cBounds.getCenterY();

            for (int j = -1; j <= 1; ++j)
            {
                for (int i = -1; i <= 1; ++i)
                {
                    if (i == 0 && j == 0)
                    {
                        continue;
                    }

                    CyVector2d scalePt =
                            new CyVector2d(cx + (i * width / 2), cy + (j * height / 2));
                    w2d.transformPoint(scalePt);

                    scaleRegion.setX(scalePt.x - DRAG_HANDLE_HALF);
                    scaleRegion.setY(scalePt.y - DRAG_HANDLE_HALF);

                    rotRegion.setX(scalePt.x - DRAG_HANDLE_HALF +
                            (i * xDir.x + j * yDir.x) * DRAG_HANDLE_SIZE);
                    rotRegion.setY(scalePt.y - DRAG_HANDLE_HALF +
                            (i * xDir.y + j * yDir.y) * DRAG_HANDLE_SIZE);

                    if (scaleRegion.contains(cursorPt))
                    {
                        if (j == 0)
                        {
                            return new SelectionManipulatorHandle(
                                    cursorPt, w2d,
                                    SelectionManipulatorHandle.Type.SCALEX,
                                    cBounds, cPivot, i, j);
                        }
                        else if (i == 0)
                        {
                            return new SelectionManipulatorHandle(
                                    cursorPt, w2d,
                                    SelectionManipulatorHandle.Type.SCALEY,
                                    cBounds, cPivot, i, j);
                        }
                        else
                        {
                            return new SelectionManipulatorHandle(
                                    cursorPt, w2d,
                                    SelectionManipulatorHandle.Type.SCALE,
                                    cBounds, cPivot, i, j);
                        }
                    }

                    if (rotRegion.contains(cursorPt))
                    {
                        if (j == 0)
                        {
                            return new SelectionManipulatorHandle(
                                    cursorPt, w2d,
                                    SelectionManipulatorHandle.Type.SKEWY,
                                    cBounds, cPivot, i, j);
                        }
                        else if (i == 0)
                        {
                            return new SelectionManipulatorHandle(
                                    cursorPt, w2d,
                                    SelectionManipulatorHandle.Type.SKEWX,
                                    cBounds, cPivot, i, j);
                        }
                        else
                        {
                            return new SelectionManipulatorHandle(
                                    cursorPt, w2d,
                                    SelectionManipulatorHandle.Type.ROTATE,
                                    cBounds, cPivot, i, j);
                        }
                    }
                }
            }
        }

        //Check translate
        {
            refPt.set(cursorPt);

            CyMatrix4d d2w = new CyMatrix4d(w2d);
            d2w.invert();
            d2w.transformPoint(cursorPt, refPt);
//            try
//            {
//                w2d.inverseTransform(cursorPt, refPt);
//            } catch (NoninvertibleTransformException ex)
//            {
//                Logger.getLogger(SelectionManipulator.class.getName()).log(Level.SEVERE, null, ex);
//            }

            if (path.contains(refPt))
            {
                return new SelectionManipulatorHandle(
                        cursorPt, w2d, SelectionManipulatorHandle.Type.TRANSLATE,
                        cBounds, cPivot, 0, 0);
            }
        }

        return null;
    }

    protected void setComponents(CyShape path, CyRectangle2d bounds, CyVector2d pivot)
    {
        this.path = path;
        this.bounds = bounds;
        this.pivot = pivot;
    }

    /**
     * Rebuild the metrics that change as a manipulator handle is dragged
     */
    abstract protected void rebuild();

    /**
     * Rebuild everything, including the RootState
     */
    abstract public void reset();

    @Override
    public void propertyWrapperDataChanged(PropertyChangeEvent evt)
    {
        rebuild();
    }

    @Override
    public void propertyWrapperTrackChanged(PropertyTrackChangeEvent evt)
    {
    }

    @Override
    public void propertyWrapperTrackKeyChanged(PropertyTrackKeyChangeEvent evt)
    {
    }

    protected void listenTo(PropertyWrapperFloat<RavenNodeXformable> prop)
    {
        PropertyWrapperWeakListener listener = new PropertyWrapperWeakListener(this, prop);
        prop.addPropertyWrapperListener(listener);
    }

    public void paint(Graphics2D g, CyMatrix4d w2d)
    {
        if (bounds == null)
        {
            return;
        }

        //Draw selection bounds
        {
            CyShape bnd = bounds.createTransformedPath(w2d);
            g.setColor(Color.blue);
            g.draw(bnd.asPathAWT());
        }

        //Draw drag handles
        CyVector2d pt = new CyVector2d();

        double midX = bounds.getCenterX();
        double midY = bounds.getCenterY();
        double w2 = bounds.getWidth() / 2;
        double h2 = bounds.getHeight() / 2;
        for (int j = -1; j <= 1; ++j)
        {
            for (int i = -1; i <= 1; ++i)
            {
                if (i == 0 && j == 0)
                {
                    continue;
                }

                pt.set(midX + i * w2, midY + j * h2);
                w2d.transformPoint(pt, pt);

                paintHandleShape(g, pt, DRAG_HANDLE_SHAPE,
                        Color.white, Color.black);
            }
        }

        //Draw pivot
        {
            pt.set(pivot);
            w2d.transformPoint(pt);
            
            paintHandleShape(g, pt, PIVOT_HANDLE_SHAPE, Color.white, Color.black);
        }
    }

//    private Point2D.Double unitVec(double dx, double dy)
//    {
//        double invLen = 1 / Math.sqrt(dx * dx + dy * dy);
//        return new Point2D.Double(dx * invLen, dy * invLen);
//    }

    abstract protected void applyTransform(CyMatrix4d toolWorld,
            CyVector2d pivot, boolean history);


    //----------------------------------
    
    /**
     * Wrapper & meta info for each selectable component managed by
     * this manipulator
     */
    public class RootState
    {
        private final ServiceTransformable root;
        private CyRectangle2d worldBounds;
        private CyMatrix4d l2p;
        private CyMatrix4d l2w;
        private CyMatrix4d w2l;

        public RootState(ServiceTransformable root)
        {
            this.root = root;
            rebuild();
        }

        protected void rebuild()
        {
            this.worldBounds = root.getBoundsWorld();
            this.l2p = root.getLocalToParentTransform((CyMatrix4d)null);
            this.l2w = root.getLocalToWorldTransform((CyMatrix4d)null);
            this.w2l = new CyMatrix4d(l2w);

//            try
//            {
                w2l.invert();
//            } catch (NoninvertibleTransformException ex)
//            {
//                Logger.getLogger(SelectionManipulator.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }

        /**
         * @return the root
         */
        public ServiceTransformable getRoot()
        {
            return root;
        }

        /**
         * @return the worldBounds
         */
        public CyRectangle2d getWorldBounds()
        {
            return new CyRectangle2d(worldBounds);
        }

        /**
         * @return the l2p
         */
        public CyMatrix4d getL2p()
        {
            return new CyMatrix4d(l2p);
        }

        /**
         * @return the l2w
         */
        public CyMatrix4d getL2w()
        {
            return new CyMatrix4d(l2w);
        }

        /**
         * @return the l2w
         */
        public CyMatrix4d getW2l()
        {
            return new CyMatrix4d(w2l);
        }
    }

}
