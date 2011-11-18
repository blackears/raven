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
public class RaFeBlend extends RaFe
{
    @ServiceInst(service=RaElementLoader.class)
    public static class Loader extends RaElementLoader<RaFeBlend>
    {
        protected Loader(String tag)
        {
            super(tag);
        }

        public Loader()
        {
            super("feBlend");
        }

        @Override
        public RaFeBlend create(RabbitDocument builder, HashMap<String, String> attr, ArrayList<RaElement> nodes)
        {
            RaFeBlend haNode = new RaFeBlend();

            haNode.setStyleClasses(parseClasses(attr.get("class"), builder));
            haNode.setStyle(parseStyle(attr.get("style"), builder));
            haNode.setX(parseFloat(attr.get("x"), 0));
            haNode.setY(parseFloat(attr.get("y"), 0));
            haNode.setWidth(parseFloat(attr.get("width"), 0));
            haNode.setHeight(parseFloat(attr.get("height"), 0));
            haNode.setIn(parseFilterInput(attr.get("in"), builder));
            haNode.setIn2(parseFilterInput(attr.get("in2"), builder));
            haNode.setResult(parseFilterResult(attr.get("result"), builder));
            haNode.setMode(parseBlendMode(attr.get("mode"), null));

            builder.setDefaultFilter(haNode.getResult());

            return haNode;
        }

        protected BlendMode parseBlendMode(String text, BlendMode def)
        {
            if (text == null)
            {
                return def;
            }

            if ("normal".equals(text))
            {
                return BlendMode.NORMAL;
            }
            if ("multiply".equals(text))
            {
                return BlendMode.MULTIPLY;
            }
            if ("screen".equals(text))
            {
                return BlendMode.SCREEN;
            }
            if ("darken".equals(text))
            {
                return BlendMode.DARKEN;
            }
            if ("lighten".equals(text))
            {
                return BlendMode.LIGHTEN;
            }
            return def;
        }
    }

    public static enum BlendMode { NORMAL, MULTIPLY, SCREEN, DARKEN, LIGHTEN }

    private int in;
    private int in2;
    private BlendMode mode;

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
     * @return the mode
     */
    public BlendMode getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(BlendMode mode) {
        this.mode = mode;
    }
    
}
