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
public class RaFeComposite extends RaFe
{
    @ServiceInst(service=RaElementLoader.class)
    public static class Loader extends RaElementLoader<RaFeComposite>
    {
        protected Loader(String tag)
        {
            super(tag);
        }

        public Loader()
        {
            super("feComposite");
        }

        @Override
        public RaFeComposite create(RabbitDocument builder, HashMap<String, String> attr, ArrayList<RaElement> nodes)
        {
            RaFeComposite haNode = new RaFeComposite();

            haNode.setStyleClasses(parseClasses(attr.get("class"), builder));
            haNode.setStyle(parseStyle(attr.get("style"), builder));
            haNode.setX(parseFloat(attr.get("x"), 0));
            haNode.setY(parseFloat(attr.get("y"), 0));
            haNode.setWidth(parseFloat(attr.get("width"), 0));
            haNode.setHeight(parseFloat(attr.get("height"), 0));
            haNode.setIn(parseFilterInput(attr.get("in"), builder));
            haNode.setIn2(parseFilterInput(attr.get("in2"), builder));
            haNode.setResult(parseFilterResult(attr.get("result"), builder));
            haNode.setOperator(parseCompositeOperator(attr.get("operator"), null));
            haNode.setK1(parseFloat(attr.get("k1"), 0));
            haNode.setK2(parseFloat(attr.get("k2"), 0));
            haNode.setK3(parseFloat(attr.get("k3"), 0));
            haNode.setK4(parseFloat(attr.get("k4"), 0));

            builder.setDefaultFilter(haNode.getResult());

            return haNode;
        }

        protected CompositeOperator parseCompositeOperator(String text, CompositeOperator def)
        {
            if (text == null)
            {
                return def;
            }

            if ("over".equals(text))
            {
                return CompositeOperator.OVER;
            }
            if ("in".equals(text))
            {
                return CompositeOperator.IN;
            }
            if ("out".equals(text))
            {
                return CompositeOperator.OUT;
            }
            if ("atop".equals(text))
            {
                return CompositeOperator.ATOP;
            }
            if ("xor".equals(text))
            {
                return CompositeOperator.XOR;
            }
            if ("arithmetic".equals(text))
            {
                return CompositeOperator.ARITMETIC;
            }
            return def;
        }
    }

    public static enum CompositeOperator { OVER, IN, OUT, ATOP, XOR, ARITMETIC }

    private int in;
    private int in2;
    private CompositeOperator operator;
    private float k1;
    private float k2;
    private float k3;
    private float k4;

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
     * @return the in2
     */
    public int getIn2() {
        return in2;
    }

    /**
     * @param in2 the in2 to set
     */
    public void setIn2(int in2) {
        this.in2 = in2;
    }

    /**
     * @return the operator
     */
    public CompositeOperator getOperator() {
        return operator;
    }

    /**
     * @param operator the operator to set
     */
    public void setOperator(CompositeOperator operator) {
        this.operator = operator;
    }

    /**
     * @return the k1
     */
    public float getK1() {
        return k1;
    }

    /**
     * @param k1 the k1 to set
     */
    public void setK1(float k1) {
        this.k1 = k1;
    }

    /**
     * @return the k2
     */
    public float getK2() {
        return k2;
    }

    /**
     * @param k2 the k2 to set
     */
    public void setK2(float k2) {
        this.k2 = k2;
    }

    /**
     * @return the k3
     */
    public float getK3() {
        return k3;
    }

    /**
     * @param k3 the k3 to set
     */
    public void setK3(float k3) {
        this.k3 = k3;
    }

    /**
     * @return the k4
     */
    public float getK4() {
        return k4;
    }

    /**
     * @param k4 the k4 to set
     */
    public void setK4(float k4) {
        this.k4 = k4;
    }

}
