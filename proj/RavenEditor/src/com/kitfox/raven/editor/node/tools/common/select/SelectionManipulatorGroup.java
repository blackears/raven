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
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.coyote.shape.CyShape;
import com.kitfox.raven.editor.node.tools.common.ServiceTransformable;
import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.util.undo.History;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kitfox
 */
public class SelectionManipulatorGroup extends SelectionManipulator
{
    final ArrayList<RootState> rootStates = new ArrayList<RootState>();

    CyVector2d groupPivot;

    private SelectionManipulatorGroup(ArrayList<ServiceTransformable> roots)
    {
        for (ServiceTransformable root: roots)
        {
            rootStates.add(new RootState(root));

            listenTo(root.getTransX());
            listenTo(root.getTransY());
            listenTo(root.getRotation());
            listenTo(root.getSkewAngle());
            listenTo(root.getScaleX());
            listenTo(root.getScaleY());
        }
    }

    public static SelectionManipulatorGroup create(ArrayList<ServiceTransformable> roots)
    {
        SelectionManipulatorGroup manip = new SelectionManipulatorGroup(roots);
        manip.rebuild();
        return manip;
    }

    @Override
    public void reset()
    {
        for (RootState state: rootStates)
        {
            state.rebuild();
        }

        rebuild();
    }

    @Override
    protected void rebuild()
    {
        CyPath2d cPath = new CyPath2d();

//        subBounds.clear();
        for (RootState state: rootStates)
        {
            ServiceTransformable node = state.getRoot();
            CyShape shape = node.getShapeWorld();
//            subBounds.add(shape.getBounds2D());
            cPath.append(shape);
        }

        CyRectangle2d cBounds = cPath.getBounds();
        if (groupPivot == null)
        {
            groupPivot = new CyVector2d(cBounds.getCenterX(), cBounds.getCenterY());
        }
        
        setComponents(cPath, cBounds, groupPivot);
        fireManipulatorChanged();
    }

    @Override
    protected List<RootState> getRoots()
    {
        return new ArrayList<RootState>(rootStates);
    }

    @Override
    protected void applyTransform(
            CyMatrix4d toolWorld, CyVector2d pivotWorld, boolean history)
    {
        groupPivot.set(pivotWorld);
        toolWorld.transformPoint(groupPivot, groupPivot);

        RootState firstRoot = rootStates.get(0);
        NodeDocument doc = firstRoot.getRoot().getNodeObject().getDocument();
        History hist = doc.getHistory();
        if (history)
        {
            hist.beginTransaction("Transform group");
        }

        for (RootState state: rootStates)
        {
            CyMatrix4d l2w = state.getL2w();
            CyMatrix4d w2l = state.getW2l();

            CyMatrix4d toolLocal = new CyMatrix4d(w2l);
            toolLocal.mul(toolWorld);
            toolLocal.mul(l2w);

            CyMatrix4d l2p = state.getL2p();
            CyMatrix4d newLocal = new CyMatrix4d(l2p);
            newLocal.mul(toolLocal);

            //Adjust pivot
            //w2l.transform(pivot, pivot);
//            Point2D pivotLocal = state.getRoot().getPivotLocal();
            CyVector2d pivotLocal = new CyVector2d(
                    state.getRoot().getPivotX().getValueNumeric(),
                    state.getRoot().getPivotY().getValueNumeric());

            state.getRoot().setLocalToParentTransform(
                    newLocal, pivotLocal, history);
        }

        if (history)
        {
            hist.commitTransaction();
        }
    }
}
