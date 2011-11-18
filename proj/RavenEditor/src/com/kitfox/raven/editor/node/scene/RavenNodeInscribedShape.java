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

package com.kitfox.raven.editor.node.scene;

import com.kitfox.coyote.shape.CyShape;
import com.kitfox.raven.util.tree.PropertyWrapperFloat;

/**
 *
 * @author kitfox
 */
abstract public class RavenNodeInscribedShape extends RavenNodeShape
{
    public static final String PROP_X = "x";
    public final PropertyWrapperFloat<RavenNodeInscribedShape> x =
            new PropertyWrapperFloat(this, PROP_X);

    public static final String PROP_Y = "y";
    public final PropertyWrapperFloat<RavenNodeInscribedShape> y =
            new PropertyWrapperFloat(this, PROP_Y);

    public static final String PROP_WIDTH = "width";
    public final PropertyWrapperFloat<RavenNodeInscribedShape> width =
            new PropertyWrapperFloat(this, PROP_WIDTH, 100);

    public static final String PROP_HEIGHT = "height";
    public final PropertyWrapperFloat<RavenNodeInscribedShape> height =
            new PropertyWrapperFloat(this, PROP_HEIGHT, 100);

//    CyShape shape;

    public RavenNodeInscribedShape(int uid)
    {
        super(uid);

//        ShapeChanged listener = new ShapeChanged();
//        x.addPropertyWrapperListener(listener);
//        y.addPropertyWrapperListener(listener);
//        width.addPropertyWrapperListener(listener);
//        height.addPropertyWrapperListener(listener);
    }

//    abstract protected CyShape createShape();

//    @Override
//    public CyShape getShapeLocal()
//    {
//        if (shape == null)
//        {
//            shape = createShape();
//        }
//        return shape;
//    }
//
//    @Override
//    protected void clearCache()
//    {
//        super.clearCache();
//        shape = null;
//    }
//
//    public class ShapeChanged implements PropertyWrapperListener
//    {
//        @Override
//        public void propertyWrapperDataChanged(PropertyChangeEvent evt)
//        {
//            clearCache();
//        }
//
//        @Override
//        public void propertyWrapperTrackChanged(EventObject evt)
//        {
//        }
//
//        @Override
//        public void propertyWrapperTrackKeyChanged(EventObject evt)
//        {
//        }
//    }

}
