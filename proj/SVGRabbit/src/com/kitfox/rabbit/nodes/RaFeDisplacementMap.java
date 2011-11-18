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
public class RaFeDisplacementMap extends RaFe
{
    @ServiceInst(service=RaElementLoader.class)
    public static class Loader extends RaElementLoader<RaFeDisplacementMap>
    {
        protected Loader(String tag)
        {
            super(tag);
        }

        public Loader()
        {
            super("feDisplacementMap");
        }

        @Override
        public RaFeDisplacementMap create(RabbitDocument builder, HashMap<String, String> attr, ArrayList<RaElement> nodes)
        {
            RaFeDisplacementMap haNode = new RaFeDisplacementMap();

            haNode.setStyleClasses(parseClasses(attr.get("class"), builder));
            haNode.setStyle(parseStyle(attr.get("style"), builder));
            haNode.setX(parseFloat(attr.get("x"), 0));
            haNode.setY(parseFloat(attr.get("y"), 0));
            haNode.setWidth(parseFloat(attr.get("width"), 0));
            haNode.setHeight(parseFloat(attr.get("height"), 0));
            haNode.setIn(parseFilterInput(attr.get("in"), builder));
            haNode.setIn2(parseFilterInput(attr.get("in2"), builder));
            haNode.setResult(parseFilterResult(attr.get("result"), builder));
            haNode.setScale(parseFloat(attr.get("scale"), 0));
            haNode.setXChannelSelector(parseChannelSelector(attr.get("xChannelSelector"), null));
            haNode.setYChannelSelector(parseChannelSelector(attr.get("yChannelSelector"), null));

            builder.setDefaultFilter(haNode.getResult());

            return haNode;
        }
    }

    private int in;
    private int in2;
    private float scale;
    private ChanelSelector xChannelSelector;
    private ChanelSelector yChannelSelector;

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
     * @return the scale
     */
    public float getScale() {
        return scale;
    }

    /**
     * @param scale the scale to set
     */
    public void setScale(float scale) {
        this.scale = scale;
    }

    /**
     * @return the xChannelSelector
     */
    public ChanelSelector getXChannelSelector() {
        return xChannelSelector;
    }

    /**
     * @param xChannelSelector the xChannelSelector to set
     */
    public void setXChannelSelector(ChanelSelector xChannelSelector) {
        this.xChannelSelector = xChannelSelector;
    }

    /**
     * @return the yChannelSelector
     */
    public ChanelSelector getYChannelSelector() {
        return yChannelSelector;
    }

    /**
     * @param yChannelSelector the yChannelSelector to set
     */
    public void setYChannelSelector(ChanelSelector yChannelSelector) {
        this.yChannelSelector = yChannelSelector;
    }
}
