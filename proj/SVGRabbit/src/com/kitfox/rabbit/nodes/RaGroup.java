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

import com.kitfox.rabbit.parser.RabbitDocument;
import com.kitfox.rabbit.render.RabbitRenderer;
import com.kitfox.raven.util.service.ServiceInst;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class RaGroup extends RaElement
{
    @ServiceInst(service=RaElementLoader.class)
    public static class Loader extends RaElementLoader<RaGroup>
    {
        protected Loader(String tag)
        {
            super(tag);
        }

        public Loader()
        {
            super("g");
        }

        @Override
        public RaGroup create(RabbitDocument builder, HashMap<String, String> attr, ArrayList<RaElement> nodes)
        {
            RaGroup haNode = new RaGroup();

            haNode.setStyleClasses(parseClasses(attr.get("class"), builder));
            haNode.setTransform(parseTransform(attr.get("transform"), null));
            haNode.setStyle(parseStyle(attr.get("style"), builder));

            haNode.addChildren(nodes);

            return haNode;

        }
    }
    
    private AffineTransform transform;

    @Override
    public Shape getOutline(RabbitRenderer renderer)
    {
        Area outline = new Area();

        for (int i = 0; i < getNumChildren(); ++i)
        {
            RaElement ele = getChild(i);
            Shape childOutline = ele.getOutline(renderer);
            if (childOutline != null)
            {
                outline.add(new Area(childOutline));
            }
        }

        if (transform != null && !outline.isEmpty())
        {
            return transform.createTransformedShape(outline);
        }
        return outline.isEmpty() ? null : outline;
    }

    /**
     * @return the transform
     */
    public AffineTransform getTransform() {
        return transform;
    }

    /**
     * @param transform the transform to set
     */
    public void setTransform(AffineTransform transform) {
        this.transform = transform;
    }

    @Override
    public void renderContent(RabbitRenderer renderer)
    {
        super.renderContent(renderer);
        
        if (transform != null)
        {
            renderer.mulTransform(transform);
        }

        for (int i = 0; i < children.size(); ++i)
        {
            RaElement ele = children.get(i);

            renderer.pushFrame(ele);

            ele.render(renderer);

            renderer.popFrame();
        }

    }


}
