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
import com.kitfox.raven.editor.node.tools.common.ServiceTransformable;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author kitfox
 */
public class SelectionManipulatorSingle extends SelectionManipulator
{
    //ServiceTransformable root;
    private final RootState rootState;

    private SelectionManipulatorSingle(ServiceTransformable root)
    {
        super();
        this.rootState = new RootState(root);

        listenTo(root.getTransX());
        listenTo(root.getTransY());
        listenTo(root.getRotation());
        listenTo(root.getSkewAngle());
        listenTo(root.getScaleX());
        listenTo(root.getScaleY());
        listenTo(root.getPivotX());
        listenTo(root.getPivotY());
    }

    public static SelectionManipulatorSingle create(ServiceTransformable root)
    {
        SelectionManipulatorSingle manip = new SelectionManipulatorSingle(root);
        manip.rebuild();
        return manip;
    }

    @Override
    public void reset()
    {
        rootState.rebuild();
        rebuild();
    }

    @Override
    public void rebuild()
    {
        ServiceTransformable root = rootState.getRoot();
        CyShape cPath = root.getShapeWorld();
        if (cPath == null)
        {
            setComponents(null, null, null);
            fireManipulatorChanged();
            return;
        }

        CyRectangle2d cBounds = cPath.getBounds();

        CyVector2d cPivot = new CyVector2d(root.getPivotX().getValueNumeric(),
                root.getPivotY().getValueNumeric());
        CyMatrix4d l2w = root.getLocalToWorldTransform(null);
        l2w.transformPoint(cPivot);

        setComponents(cPath, cBounds, cPivot);
//                new CyVector2d(
//                root.getPivotX().getValueNumeric() + root.getTransX().getValueNumeric(),
//                root.getPivotY().getValueNumeric() + root.getTransY().getValueNumeric()));
        
        fireManipulatorChanged();
    }

    @Override
    public void propertyWrapperDataChanged(PropertyChangeEvent evt)
    {
        rebuild();
    }

    @Override
    protected List<RootState> getRoots()
    {
        return Collections.singletonList(rootState);
    }

    @Override
    protected void applyTransform(CyMatrix4d toolWorld, CyVector2d pivot, boolean history)
    {
        CyMatrix4d l2w = rootState.getL2w();
        CyMatrix4d w2l = rootState.getW2l();

        CyMatrix4d toolLocal = new CyMatrix4d(w2l);
        toolLocal.mul(toolWorld);
        toolLocal.mul(l2w);

        CyMatrix4d l2p = rootState.getL2p();
//            AffineTransform newLocal = new AffineTransform(toolLocal);
//            newLocal.concatenate(l2p);
        CyMatrix4d newLocal = new CyMatrix4d(l2p);
        newLocal.mul(toolLocal);

        //Adjust pivot
//            toolWorld.transform(pivot, pivot);
        w2l.transformPoint(pivot);

        rootState.getRoot().setLocalToParentTransform(newLocal, pivot, history);
    }

}
