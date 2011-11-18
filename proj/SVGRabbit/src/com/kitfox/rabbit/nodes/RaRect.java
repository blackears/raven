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
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class RaRect extends RaShape
{
    @ServiceInst(service=RaElementLoader.class)
    public static class Loader extends RaElementLoader<RaRect>
    {
        protected Loader(String tag)
        {
            super(tag);
        }

        public Loader()
        {
            super("rect");
        }

        @Override
        public RaRect create(RabbitDocument builder, HashMap<String, String> attr, ArrayList<RaElement> nodes)
        {
            RaRect haNode = new RaRect();

            haNode.setStyleClasses(parseClasses(attr.get("class"), builder));
            haNode.setTransform(parseTransform(attr.get("transform"), null));
            haNode.setStyle(parseStyle(attr.get("style"), builder));
            haNode.setX(parseFloat(attr.get("x"), 0));
            haNode.setY(parseFloat(attr.get("y"), 0));
            haNode.setWidth(parseFloat(attr.get("width"), 0));
            haNode.setHeight(parseFloat(attr.get("height"), 0));
            haNode.setRx(parseFloat(attr.get("rx"), 0));
            haNode.setRy(parseFloat(attr.get("ry"), 0));


            return haNode;

        }
    }

    private float x;
    private float y;
    private float width;
    private float height;
    private float rx;
    private float ry;

   RectangularShape shape;

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
     * @return the rx
     */
    public float getRx() {
        return rx;
    }

    /**
     * @param rx the rx to set
     */
    public void setRx(float rx) {
        this.rx = rx;
    }

    /**
     * @return the ry
     */
    public float getRy() {
        return ry;
    }

    /**
     * @param ry the ry to set
     */
    public void setRy(float ry) {
        this.ry = ry;
    }

    @Override
    public Shape getShape()
    {
        if (shape == null)
        {
            if (rx == 0 && ry == 0)
            {
                shape = new Rectangle2D.Float(x, y, width, height);
            }
            else
            {
                shape = new RoundRectangle2D.Float(x, y, width, height, rx, ry);
            }
        }
        return shape;
    }
}
