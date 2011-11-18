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

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.game.control.color.PaintLayout;
import com.kitfox.raven.editor.node.tools.ToolService;
import com.kitfox.raven.editor.paint.RavenPaint;
import com.kitfox.raven.editor.stroke.RavenStroke;
import com.kitfox.raven.util.Intersection;

/**
 *
 * @author kitfox
 */
public interface ServiceMaterial extends ToolService
{
    /**
     * Find the local bounds of the component or subcomponent that can handle
     * materials.  If this component or subcomponent cannot handle materials,
     * this should return null.
     *
     * @param subselection
     * @return
     */
    public CyRectangle2d getMaterialFaceBounds(Integer subselection);
    /**
     *
     * @param subselection A particular face to query info for, or null to
     * see if this node has an over all paint value.  (the null value allows for
     * meshes to have many different face paints while a path can have one over all
     * face paint)
     * @return A RavenPaint if this node or subcomponent has a paint, null
     * if not (or if it does not apply)
     */
    public RavenPaint getMaterialFacePaint(Integer subselection);
    public PaintLayout getMaterialFaceLayout(Integer subselection);

    public CyRectangle2d getMaterialEdgeBounds(Integer subselection);
    public RavenStroke getMaterialEdgeStroke(Integer subselection);
    public RavenPaint getMaterialEdgePaint(Integer subselection);
    public PaintLayout getMaterialEdgeLayout(Integer subselection);

    public void setMaterialFacePaint(Integer subselection, RavenPaint value, boolean history);
    public void setMaterialFaceLayout(Integer subselection, PaintLayout value, boolean history);
    public void setMaterialEdgeStroke(Integer subselection, RavenStroke value, boolean history);
    public void setMaterialEdgePaint(Integer subselection, RavenPaint value, boolean history);
    public void setMaterialEdgeLayout(Integer subselection, PaintLayout value, boolean history);

    /**
     * Sets all components of the shape within given area to the given
     * paint.
     *
     * @param paint Paint to set shape and/or components to.  If null,
     * no change is applied.
     * @param pickArea Region in pick space where paint is applied
     * @param worldToPick Transform from world to pick space
     * @param isect How intersection testing with pick region should
     * be carried out
     */
    public void floodFill(RavenPaint paint, 
            CyRectangle2d pickArea,
            CyMatrix4d worldToPick, Intersection isect);
    public void floodStroke(RavenPaint paint, RavenStroke stroke, 
            CyRectangle2d pickArea,
            CyMatrix4d worldToPick, Intersection isect);

    /**
     * Find transform from local space to world space
     * @param xform
     */
//    public CyMatrix4d getLocalToWorldTransform(CyMatrix4d result);
}
