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
public class RaFeDiffuseLighting extends RaFe
{
    @ServiceInst(service=RaElementLoader.class)
    public static class Loader extends RaElementLoader<RaFeDiffuseLighting>
    {
        protected Loader(String tag)
        {
            super(tag);
        }

        public Loader()
        {
            super("feDiffuseLighting");
        }

        @Override
        public RaFeDiffuseLighting create(RabbitDocument builder, HashMap<String, String> attr, ArrayList<RaElement> nodes)
        {
            RaFeDiffuseLighting haNode = new RaFeDiffuseLighting();

            haNode.setStyleClasses(parseClasses(attr.get("class"), builder));
            haNode.setStyle(parseStyle(attr.get("style"), builder));
            haNode.setX(parseFloat(attr.get("x"), 0));
            haNode.setY(parseFloat(attr.get("y"), 0));
            haNode.setWidth(parseFloat(attr.get("width"), 0));
            haNode.setHeight(parseFloat(attr.get("height"), 0));
            haNode.setIn(parseFilterInput(attr.get("in"), builder));
            haNode.setResult(parseFilterResult(attr.get("result"), builder));
            haNode.setSurfaceScale(parseFloat(attr.get("surfaceScale"), 0));
            haNode.setDiffuseConstant(parseFloat(attr.get("diffuseConstant"), 0));
            haNode.setKernelUnitLength(parseFloatArr(attr.get("kernelUnitLength"), null));

            builder.setDefaultFilter(haNode.getResult());

            haNode.addChildren(nodes);

            return haNode;
        }
    }

    private int in;
    private float surfaceScale;
    private float diffuseConstant;
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
     * @return the diffuseConstant
     */
    public float getDiffuseConstant() {
        return diffuseConstant;
    }

    /**
     * @param diffuseConstant the diffuseConstant to set
     */
    public void setDiffuseConstant(float diffuseConstant) {
        this.diffuseConstant = diffuseConstant;
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
}
