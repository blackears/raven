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
public class RaFeMorphology extends RaFe
{
    @ServiceInst(service=RaElementLoader.class)
    public static class Loader extends RaElementLoader<RaFeMorphology>
    {
        protected Loader(String tag)
        {
            super(tag);
        }

        public Loader()
        {
            super("feMorphology");
        }

        @Override
        public RaFeMorphology create(RabbitDocument builder, HashMap<String, String> attr, ArrayList<RaElement> nodes)
        {
            RaFeMorphology haNode = new RaFeMorphology();

            haNode.setStyleClasses(parseClasses(attr.get("class"), builder));
            haNode.setStyle(parseStyle(attr.get("style"), builder));
            haNode.setX(parseFloat(attr.get("x"), 0));
            haNode.setY(parseFloat(attr.get("y"), 0));
            haNode.setWidth(parseFloat(attr.get("width"), 0));
            haNode.setHeight(parseFloat(attr.get("height"), 0));
            haNode.setIn(parseFilterInput(attr.get("in"), builder));
            haNode.setResult(parseFilterResult(attr.get("result"), builder));
            haNode.setRadius(parseFloat(attr.get("radius"), 0));
            haNode.setOperator(parseOperator(attr.get("operator"), null));

            builder.setDefaultFilter(haNode.getResult());

            return haNode;
        }

        protected Operator parseOperator(String text, Operator def)
        {
            if (text == null)
            {
                return def;
            }

            if ("erode".equals(text))
            {
                return Operator.ERODE;
            }
            if ("dilate".equals(text))
            {
                return Operator.DILATE;
            }
            return def;
        }
    }

    public static enum Operator { ERODE, DILATE }

    private int in;
    private Operator operator;
    private float radius;

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
     * @return the operator
     */
    public Operator getOperator() {
        return operator;
    }

    /**
     * @param operator the operator to set
     */
    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    /**
     * @return the radius
     */
    public float getRadius() {
        return radius;
    }

    /**
     * @param radius the radius to set
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }

}
