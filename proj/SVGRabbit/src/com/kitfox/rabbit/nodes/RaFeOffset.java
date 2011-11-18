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
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class RaFeOffset extends RaFe
{
    @ServiceInst(service=RaElementLoader.class)
    public static class Loader extends RaElementLoader<RaFeOffset>
    {
        protected Loader(String tag)
        {
            super(tag);
        }

        public Loader()
        {
            super("feOffset");
        }

        @Override
        public RaFeOffset create(RabbitDocument builder, HashMap<String, String> attr, ArrayList<RaElement> nodes)
        {
            RaFeOffset haNode = new RaFeOffset();

            haNode.setStyleClasses(parseClasses(attr.get("class"), builder));
            haNode.setStyle(parseStyle(attr.get("style"), builder));
            haNode.setX(parseFloat(attr.get("x"), 0));
            haNode.setY(parseFloat(attr.get("y"), 0));
            haNode.setWidth(parseFloat(attr.get("width"), 0));
            haNode.setHeight(parseFloat(attr.get("height"), 0));
            haNode.setIn(parseFilterInput(attr.get("in"), builder));
            haNode.setResult(parseFilterResult(attr.get("result"), builder));
            haNode.setDx(parseFloat(attr.get("dx"), 0));
            haNode.setDy(parseFloat(attr.get("dy"), 0));

            builder.setDefaultFilter(haNode.getResult());

            return haNode;
        }
    }

    private int in;
    private float dx;
    private float dy;

    /**
     * @return the in
     */
    public int getIn() {
        return in;
    }

    /**
     * @param in the in to set
     */
    public void setIn(int in) {
        this.in = in;
    }

    /**
     * @return the dx
     */
    public float getDx() {
        return dx;
    }

    /**
     * @param dx the dx to set
     */
    public void setDx(float dx) {
        this.dx = dx;
    }

    /**
     * @return the dy
     */
    public float getDy() {
        return dy;
    }

    /**
     * @param dy the dy to set
     */
    public void setDy(float dy) {
        this.dy = dy;
    }
}
