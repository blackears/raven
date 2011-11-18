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
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class RaTextArea extends RaElement
{
    @ServiceInst(service=RaElementLoader.class)
    public static class Loader extends RaElementLoader<RaTextArea>
    {
        protected Loader(String tag)
        {
            super(tag);
        }

        public Loader()
        {
            super("textArea");
        }

        @Override
        public RaTextArea create(RabbitDocument builder, HashMap<String, String> attr, ArrayList<RaElement> nodes)
        {
            RaTextArea haNode = new RaTextArea();

            haNode.setStyleClasses(parseClasses(attr.get("class"), builder));
            haNode.setTransform(parseTransform(attr.get("transform"), null));
            haNode.setStyle(parseStyle(attr.get("style"), builder));
            haNode.setX(parseFloat(attr.get("x"), 0));
            haNode.setY(parseFloat(attr.get("y"), 0));
            haNode.setWidth(parseFloat(attr.get("width"), 0));
            haNode.setHeight(parseFloat(attr.get("height"), 0));
            haNode.setEditable(parseEditable(attr.get("editable")));
            haNode.setFocusable(parseFocusable(attr.get("focusable")));

            haNode.addChildren(nodes);

            return haNode;

        }

        protected Editable parseEditable(String text)
        {
            if (text == null)
            {
                return Editable.NONE;
            }

            if ("none".equals(text))
            {
                return Editable.NONE;
            }
            if ("simple".equals(text))
            {
                return Editable.SIMPLE;
            }
            return Editable.NONE;
        }

        protected Focusable parseFocusable(String text)
        {
            if (text == null)
            {
                return Focusable.AUTO;
            }

            if ("true".equals(text))
            {
                return Focusable.TRUE;
            }
            if ("false".equals(text))
            {
                return Focusable.FALSE;
            }
            return Focusable.AUTO;
        }
    }

    public static enum Editable { NONE, SIMPLE }
    public static enum Focusable { TRUE, FALSE, AUTO }

    private float x;
    private float y;
    private float width;
    private float height;
    private Editable editable;
    private Focusable focusable;

    private AffineTransform transform = new AffineTransform();

    /**
     * @return the transform
     */
    public AffineTransform getTransform() {
        return transform;
    }

    /**
     * @param transform the transform to set
     */
    public void setTransform(AffineTransform transform) {
        this.transform = transform;
    }

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
     * @return the editable
     */
    public Editable getEditable() {
        return editable;
    }

    /**
     * @param editable the editable to set
     */
    public void setEditable(Editable editable) {
        this.editable = editable;
    }

    /**
     * @return the focusable
     */
    public Focusable getFocusable() {
        return focusable;
    }

    /**
     * @param focusable the focusable to set
     */
    public void setFocusable(Focusable focusable) {
        this.focusable = focusable;
    }

}
