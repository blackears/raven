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
import com.kitfox.raven.util.service.ServiceInst;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class RaPath extends RaShape
{
    @ServiceInst(service=RaElementLoader.class)
    public static class Loader extends RaElementLoader<RaPath>
    {
        protected Loader(String tag)
        {
            super(tag);
        }

        public Loader()
        {
            super("path");
        }

        @Override
        public RaPath create(RabbitDocument builder, HashMap<String, String> attr, ArrayList<RaElement> nodes)
        {
            RaPath haNode = new RaPath();

            haNode.setStyleClasses(parseClasses(attr.get("class"), builder));
            haNode.setTransform(parseTransform(attr.get("transform"), null));
            haNode.setStyle(parseStyle(attr.get("style"), builder));
            haNode.setPath(parsePath(attr.get("d"), null));
            haNode.setPathLength(parseFloat(attr.get("pathLength"), 0));

            return haNode;

        }
    }

    private Path2D.Double path;
    private float pathLength;

    /**
     * @return the path
     */
    public Path2D.Double getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(Path2D.Double path) {
        this.path = path;
    }

    /**
     * @return the pathLength
     */
    public float getPathLength() {
        return pathLength;
    }

    /**
     * @param pathLength the pathLength to set
     */
    public void setPathLength(float pathLength) {
        this.pathLength = pathLength;
    }

    @Override
    public Shape getShape()
    {
        return path;
    }


}
