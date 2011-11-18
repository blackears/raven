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
public class RaFeTurbulence extends RaFe
{
    @ServiceInst(service=RaElementLoader.class)
    public static class Loader extends RaElementLoader<RaFeTurbulence>
    {
        protected Loader(String tag)
        {
            super(tag);
        }

        public Loader()
        {
            super("feTurbulence");
        }

        @Override
        public RaFeTurbulence create(RabbitDocument builder, HashMap<String, String> attr, ArrayList<RaElement> nodes)
        {
            RaFeTurbulence haNode = new RaFeTurbulence();

            haNode.setStyleClasses(parseClasses(attr.get("class"), builder));
            haNode.setStyle(parseStyle(attr.get("style"), builder));
            haNode.setX(parseFloat(attr.get("x"), 0));
            haNode.setY(parseFloat(attr.get("y"), 0));
            haNode.setWidth(parseFloat(attr.get("width"), 0));
            haNode.setHeight(parseFloat(attr.get("height"), 0));
            haNode.setResult(parseFilterResult(attr.get("result"), builder));
            haNode.setBaseFrequency(parseFloatArr(attr.get("baseFrequency"), null));
            haNode.setNumOctaves(parseInt(attr.get("numOctaves"), 0));
            haNode.setStitchTiles(parseStitchTiles(attr.get("stitchTiles")));
            haNode.setType(parseType(attr.get("type")));

            builder.setDefaultFilter(haNode.getResult());

            return haNode;
        }

        protected boolean parseStitchTiles(String text)
        {
            if (text == null)
            {
                return false;
            }

            if ("stitch".equals(text))
            {
                return true;
            }
            return false;
        }

        protected Type parseType(String text)
        {
            if (text == null)
            {
                return Type.FRACTAL_NOISE;
            }

            if ("turbulence".equals(text))
            {
                return Type.TURBULENCE;
            }
            return Type.FRACTAL_NOISE;
        }
    }

    public static enum Type { FRACTAL_NOISE, TURBULENCE }
    private float[] baseFrequency;
    private int numOctaves;
    private int seed;
    private boolean stitchTiles;
    private Type type;


    /**
     * @return the baseFrequency
     */
    public float[] getBaseFrequency() {
        return baseFrequency;
    }

    /**
     * @param baseFrequency the baseFrequency to set
     */
    public void setBaseFrequency(float[] baseFrequency) {
        this.baseFrequency = baseFrequency;
    }

    /**
     * @return the numOctaves
     */
    public int getNumOctaves() {
        return numOctaves;
    }

    /**
     * @param numOctaves the numOctaves to set
     */
    public void setNumOctaves(int numOctaves) {
        this.numOctaves = numOctaves;
    }

    /**
     * @return the seed
     */
    public int getSeed() {
        return seed;
    }

    /**
     * @param seed the seed to set
     */
    public void setSeed(int seed) {
        this.seed = seed;
    }

    /**
     * @return the stitchTiles
     */
    public boolean isStitchTiles() {
        return stitchTiles;
    }

    /**
     * @param stitchTiles the stitchTiles to set
     */
    public void setStitchTiles(boolean stitchTiles) {
        this.stitchTiles = stitchTiles;
    }

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }
}
