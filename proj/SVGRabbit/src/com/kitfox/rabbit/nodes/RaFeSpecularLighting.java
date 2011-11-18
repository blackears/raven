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
public class RaFeSpecularLighting extends RaFe
{
    @ServiceInst(service=RaElementLoader.class)
    public static class Loader extends RaElementLoader<RaFeSpecularLighting>
    {
        protected Loader(String tag)
        {
            super(tag);
        }

        public Loader()
        {
            super("feSpecularLighting");
        }

        @Override
        public RaFeSpecularLighting create(RabbitDocument builder, HashMap<String, String> attr, ArrayList<RaElement> nodes)
        {
            RaFeSpecularLighting haNode = new RaFeSpecularLighting();

            haNode.setStyleClasses(parseClasses(attr.get("class"), builder));
            haNode.setStyle(parseStyle(attr.get("style"), builder));
            haNode.setX(parseFloat(attr.get("x"), 0));
            haNode.setY(parseFloat(attr.get("y"), 0));
            haNode.setWidth(parseFloat(attr.get("width"), 0));
            haNode.setHeight(parseFloat(attr.get("height"), 0));
            haNode.setIn(parseFilterInput(attr.get("in"), builder));
            haNode.setResult(parseFilterResult(attr.get("result"), builder));
            haNode.setSurfaceScale(parseFloat(attr.get("surfaceScale"), 0));
            haNode.setSpecularConstant(parseFloat(attr.get("specularConstant"), 0));
            haNode.setSpecularExponent(parseFloat(attr.get("specularExponent"), 0));
            haNode.setKernelUnitLength(parseFloatArr(attr.get("kernelUnitLength"), null));

            builder.setDefaultFilter(haNode.getResult());

            haNode.addChildren(nodes);

            return haNode;
        }
    }

    private int in;
    private float surfaceScale;
    private float specularConstant;
    private float specularExponent;
    private float[] kernelUnitLength;

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
     * @return the surfaceScale
     */
    public float getSurfaceScale() {
        return surfaceScale;
    }

    /**
     * @param surfaceScale the surfaceScale to set
     */
    public void setSurfaceScale(float surfaceScale) {
        this.surfaceScale = surfaceScale;
    }

    /**
     * @return the kernelUnitLength
     */
    public float[] getKernelUnitLength() {
        return kernelUnitLength;
    }

    /**
     * @param kernelUnitLength the kernelUnitLength to set
     */
    public void setKernelUnitLength(float[] kernelUnitLength) {
        this.kernelUnitLength = kernelUnitLength;
    }

    /**
     * @return the specularConstant
     */
    public float getSpecularConstant() {
        return specularConstant;
    }

    /**
     * @param specularConstant the specularConstant to set
     */
    public void setSpecularConstant(float specularConstant) {
        this.specularConstant = specularConstant;
    }

    /**
     * @return the specularExponent
     */
    public float getSpecularExponent() {
        return specularExponent;
    }

    /**
     * @param specularExponent the specularExponent to set
     */
    public void setSpecularExponent(float specularExponent) {
        this.specularExponent = specularExponent;
    }

}
