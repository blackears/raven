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
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class RaCircle extends RaShape
{
    @ServiceInst(service=RaElementLoader.class)
    public static class Loader extends RaElementLoader<RaCircle>
    {
        protected Loader(String tag)
        {
            super(tag);
        }

        public Loader()
        {
            super("circle");
        }

        @Override
        public RaCircle create(RabbitDocument builder, HashMap<String, String> attr, ArrayList<RaElement> nodes)
        {
            RaCircle haNode = new RaCircle();

            haNode.setStyleClasses(parseClasses(attr.get("class"), builder));
            haNode.setTransform(parseTransform(attr.get("transform"), null));
            haNode.setStyle(parseStyle(attr.get("style"), builder));
            haNode.setCx(parseFloat(attr.get("cx"), 0));
            haNode.setCy(parseFloat(attr.get("cy"), 0));
            haNode.setR(parseFloat(attr.get("r"), 0));

            return haNode;

        }
    }

    private float cx;
    private float cy;
    private float r;

    Ellipse2D.Float shape;

    /**
     * @return the cx
     */
    public float getCx() {
        return cx;
    }

    /**
     * @param cx the cx to set
     */
    public void setCx(float cx) {
        this.cx = cx;
    }

    /**
     * @return the cy
     */
    public float getCy() {
        return cy;
    }

    /**
     * @param cy the cy to set
     */
    public void setCy(float cy) {
        this.cy = cy;
    }

    /**
     * @return the r
     */
    public float getR() {
        return r;
    }

    /**
     * @param r the r to set
     */
    public void setR(float r) {
        this.r = r;
    }

    @Override
    public Shape getShape()
    {
        if (shape == null)
        {
            shape = new Ellipse2D.Float(cx, cy, r, r);
        }
        return shape;
    }


}
