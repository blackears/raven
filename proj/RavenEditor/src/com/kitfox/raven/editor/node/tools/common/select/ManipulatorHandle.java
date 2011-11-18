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

/**
 *
 * @author kitfox
 */
public class ManipulatorHandle
{
    //Drag start pos in device space
    protected final CyVector2d anchor;
    //World to device transform when manip created
    protected final CyMatrix4d w2d;
    protected final CyMatrix4d d2w;

    public ManipulatorHandle(CyVector2d anchor, CyMatrix4d w2d)
    {
        this.anchor = anchor;
        this.w2d = w2d;
        this.d2w = new CyMatrix4d(w2d);
//        try
//        {
            d2w.invert();
//        } catch (NoninvertibleTransformException ex)
//        {
//            Logger.getLogger(ManipulatorHandle.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    /**
     * @return the anchor
     */
    public CyVector2d getAnchor()
    {
        return new CyVector2d(anchor);
    }

    /**
     * @return the w2d
     */
    public CyMatrix4d getW2d()
    {
        return new CyMatrix4d(w2d);
    }

//    public CyVector2d mulVector(CyMatrix4d xform, CyVector2d delta, CyVector2d result)
//    {
//        if (result == null)
//        {
//            result = new CyVector2d();
//        }
//
//        //We'll need to mul by the inverse of the inverse transpose
//        // ((w2d)^-1)^-1^T == w2d^T
//        double m00 = xform.getScaleX();
//        double m10 = xform.getShearY();
//        double m01 = xform.getShearX();
//        double m11 = xform.getScaleY();
////        double m02 = xform.getTranslateX();
////        double m12 = xform.getTranslateY();
//
//        //Mul by transpose
//        double tx = delta.x * m00 + delta.y * m01;
//        double ty = delta.x * m10 + delta.y * m11;
//
//        //Normalize
////        tx /= tw;
////        ty /= tw;
//
//        result.x = tx;
//        result.y = ty;
//        return result;
//    }

}
