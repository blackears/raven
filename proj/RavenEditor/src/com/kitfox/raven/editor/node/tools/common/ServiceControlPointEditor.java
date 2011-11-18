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

import com.kitfox.raven.editor.node.tools.ToolService;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.Track;
import com.kitfox.raven.util.tree.TrackCurveComponent;
import com.kitfox.raven.util.tree.TrackCurveComponentCurve;
import com.kitfox.raven.util.tree.TrackCurveComponentKey;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import javax.swing.JPopupMenu;

/**
 *
 * @author kitfox
 */
public interface ServiceControlPointEditor extends ToolService
{
    /**
     * Return an object that can be used to modify control points as a
     * group.  If there are no control points to move, this should
     * return null.  Returning null will cause the control to enter
     * selection mode instead of move mode.
     *
     * @param minX
     * @param maxX
     * @param minY
     * @param maxY 
     * @return
     */
//    public ControlPointDragSet getControlPointDragSet(double minX, double maxX, double minY, double maxY);
//    public void clearControlPointSelection();
//    public void modifyControlPointSelection(Selection.Type selectType, double minX, double maxX, double minY, double maxY);
//    public JPopupMenu getEditPointMenu(double minX, double maxX, double minY, double maxY);
//    public void deleteControlPointSelection();
//    public EdgeControlPoint getBoundaryPoint(double minX, double maxX, double minY, double maxY);
//
//    public AffineTransform getLocalToWorldTransform(AffineTransform value);

    public Selection<TrackCurveComponent> getSelection();
    public AffineTransform getWorldToDeviceTransform(AffineTransform value);

    public Track getTrack();

    public NodeDocument getDocument();

    public ArrayList<PropertyWrapper> getEditableProperties();

    public JPopupMenu getPopupMenuKeys(ArrayList<TrackCurveComponentKey> pickList);
    public JPopupMenu getPopupMenuCurves(ArrayList<TrackCurveComponentCurve> pickList);
}
