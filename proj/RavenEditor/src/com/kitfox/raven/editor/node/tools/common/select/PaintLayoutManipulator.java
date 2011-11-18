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
import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.game.control.color.PaintLayout;
import com.kitfox.raven.editor.node.scene.RavenNodeRenderable;
import com.kitfox.raven.editor.node.scene.RavenNodeXformable;
import com.kitfox.raven.editor.node.tools.common.ServiceMaterial;
import com.kitfox.raven.editor.paint.RavenPaint;
import com.kitfox.raven.editor.stroke.RavenStroke;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
abstract public class PaintLayoutManipulator extends Manipulator
{
    ArrayList<MaterialElement> compList;
    final boolean strokeMode;

    public PaintLayoutManipulator(ArrayList<MaterialElement> compList, boolean strokeMode)
    {
        this.compList = compList;
        this.strokeMode = strokeMode;
    }

    abstract public PaintLayoutManipulatorHandle getManipulatorHandle(
            MouseEvent evt, CyMatrix4d w2d);

    abstract public void paint(Graphics2D g, CyMatrix4d w2d);

    abstract public void rebuild();

//    public CyMatrix4d getLocalToWorldTransform(Object object)
//    {
//        if (compList.isEmpty())
//        {
//            return CyMatrix4d.createIdentity();
//        }
//
//        MaterialElement first = compList.get(0);
//        return first.getLocalToWorld(null);
//    }

    //----------------------
    public static class MaterialElement
    {
        private final ServiceMaterial node;
        private final Integer subcomponent;
        private final CyRectangle2d bounds;

        public MaterialElement(ServiceMaterial node, Integer subcomponent, CyRectangle2d bounds)
        {
            this.node = node;
            this.subcomponent = subcomponent;
            this.bounds = bounds;
        }

        public RavenPaint getFacePaint()
        {
            return node.getMaterialFacePaint(subcomponent);
        }

        public PaintLayout getFaceLayout()
        {
            return node.getMaterialFaceLayout(subcomponent);
        }

        public RavenStroke getEdgeStroke()
        {
            return node.getMaterialEdgeStroke(subcomponent);
        }

        public RavenPaint getEdgePaint()
        {
            return node.getMaterialEdgePaint(subcomponent);
        }

        public PaintLayout getEdgeLayout()
        {
            return node.getMaterialEdgeLayout(subcomponent);
        }

        /**
         * @return the bounds
         */
        public CyRectangle2d getBounds()
        {
            return new CyRectangle2d(bounds);
        }

        /**
         * @return the bounds
         */
        public CyRectangle2d getBoundsWorld()
        {
            CyMatrix4d l2w = ((RavenNodeRenderable)node).getLocalToWorldTransform((CyMatrix4d)null);
            return bounds.createTransformedBounds(l2w);
        }

        /**
         * @return the node
         */
        public ServiceMaterial getNode()
        {
            return node;
        }

        /**
         * @return the subcomponent
         */
        public Integer getSubcomponent()
        {
            return subcomponent;
        }

        public PaintLayout getEdgeLayoutWorld()
        {
            CyMatrix4d l2w = ((RavenNodeXformable)node)
                    .getLocalToWorldTransform((CyMatrix4d)null);
            PaintLayout layout = getEdgeLayout();
            return layout.transform(l2w);
        }

        public PaintLayout getFaceLayoutWorld()
        {
            CyMatrix4d l2w = ((RavenNodeXformable)node)
                    .getLocalToWorldTransform((CyMatrix4d)null);
            PaintLayout layout = getFaceLayout();
            return layout.transform(l2w);
        }

        public void setEdgeLayoutWorld(PaintLayout layout, boolean history)
        {
            CyMatrix4d w2l = ((RavenNodeXformable)node)
                    .getLocalToWorldTransform((CyMatrix4d)null);
            w2l.invert();
            layout = layout.transform(w2l);
            node.setMaterialEdgeLayout(subcomponent, layout, history);
        }

        public void setFaceLayoutWorld(PaintLayout layout, boolean history)
        {
            CyMatrix4d w2l = ((RavenNodeXformable)node)
                    .getLocalToWorldTransform((CyMatrix4d)null);
            w2l.invert();
            layout = layout.transform(w2l);
            node.setMaterialFaceLayout(subcomponent, layout, history);
        }
    }
}
