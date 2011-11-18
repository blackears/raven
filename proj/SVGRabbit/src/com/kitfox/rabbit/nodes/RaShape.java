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

package com.kitfox.rabbit.nodes;

import com.kitfox.rabbit.render.RabbitRenderer;
import java.awt.Shape;
import java.awt.geom.AffineTransform;


/**
 *
 * @author kitfox
 */
abstract public class RaShape extends RaElement
{
    private AffineTransform transform;

    abstract public Shape getShape();

    @Override
    public Shape getOutline(RabbitRenderer renderer)
    {
        Shape shape = getShape();
        if (transform != null && shape != null)
        {
            return transform.createTransformedShape(shape);
        }
        return shape;
    }


    @Override
    public void renderContent(RabbitRenderer renderer)
    {
        super.renderContent(renderer);

        if (transform != null)
        {
            renderer.mulTransform(transform);
        }

        renderer.render(getShape());
    }


    /**
     * @return the transform
     */
    public AffineTransform getTransform()
    {
        return transform;
    }

    /**
     * @param transform the transform to set
     */
    public void setTransform(AffineTransform transform)
    {
        this.transform = transform;
    }

}
