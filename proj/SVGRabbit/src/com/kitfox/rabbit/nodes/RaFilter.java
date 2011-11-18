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

import com.kitfox.rabbit.types.ElementRef;
import com.kitfox.rabbit.parser.RabbitDocument;
import com.kitfox.raven.util.service.ServiceInst;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class RaFilter extends RaElement
{
    @ServiceInst(service=RaElementLoader.class)
    public static class Loader extends RaElementLoader<RaFilter>
    {
        protected Loader(String tag)
        {
            super(tag);
        }

        public Loader()
        {
            super("filter");
        }

        @Override
        public RaFilter create(RabbitDocument builder, HashMap<String, String> attr, ArrayList<RaElement> nodes)
        {
            RaFilter haNode = new RaFilter();

            haNode.setStyleClasses(parseClasses(attr.get("class"), builder));
            haNode.setStyle(parseStyle(attr.get("style"), builder));
            haNode.setX(parseFloat(attr.get("x"), -.1f));
            haNode.setY(parseFloat(attr.get("y"), -.1f));
            haNode.setWidth(parseFloat(attr.get("width"), 1.2f));
            haNode.setHeight(parseFloat(attr.get("height"), 1.2f));
            haNode.setFilterRes(parseFloatArr(attr.get("filterRes"), null));
            haNode.setFilterUnits(parseGradientUnits(attr.get("filterUnits")));
            haNode.setPrimitiveUnits(parseGradientUnits(attr.get("primitiveUnits")));
            haNode.setHref(parseElementRef(attr.get("href"), builder));


            haNode.addChildren(nodes);

            haNode.setResult(builder.getLastResultFilter());
            builder.resetFilterIds();

            return haNode;

        }
    }

    private float x;
    private float y;
    private float width;
    private float height;
    private float[] filterRes;
    private GradientUnits filterUnits;
    private GradientUnits primitiveUnits;
    private ElementRef href;

    private int result;

    /**
     * @return the x
     */
    public float getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public float getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * @return the width
     */
    public float getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public float getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * @return the filterRes
     */
    public float[] getFilterRes() {
        return filterRes;
    }

    /**
     * @param filterRes the filterRes to set
     */
    public void setFilterRes(float[] filterRes) {
        this.filterRes = filterRes;
    }

    /**
     * @return the filterUnits
     */
    public GradientUnits getFilterUnits() {
        return filterUnits;
    }

    /**
     * @param filterUnits the filterUnits to set
     */
    public void setFilterUnits(GradientUnits filterUnits) {
        this.filterUnits = filterUnits;
    }

    /**
     * @return the primitiveUnits
     */
    public GradientUnits getPrimitiveUnits() {
        return primitiveUnits;
    }

    /**
     * @param primitiveUnits the primitiveUnits to set
     */
    public void setPrimitiveUnits(GradientUnits primitiveUnits) {
        this.primitiveUnits = primitiveUnits;
    }

    /**
     * @return the href
     */
    public ElementRef getHref() {
        return href;
    }

    /**
     * @param href the href to set
     */
    public void setHref(ElementRef href) {
        this.href = href;
    }

    /**
     * @return the result
     */
    public int getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(int result) {
        this.result = result;
    }
    
}
