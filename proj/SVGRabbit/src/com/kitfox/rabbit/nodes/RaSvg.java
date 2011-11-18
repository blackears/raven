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
import com.kitfox.rabbit.render.RabbitRenderer;
import com.kitfox.rabbit.types.RaLength;
import com.kitfox.raven.util.service.ServiceInst;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class RaSvg extends RaElement
{
    @ServiceInst(service=RaElementLoader.class)
    public static class Loader extends RaElementLoader<RaSvg>
    {
        public Loader()
        {
            super("svg");
        }

        @Override
        public RaSvg create(RabbitDocument builder, HashMap<String, String> attr, ArrayList<RaElement> nodes)
        {
            RaSvg haNode = new RaSvg();

            haNode.setStyleClasses(parseClasses(attr.get("class"), builder));
            haNode.setX(parseLength(attr.get("x"), null));
            haNode.setY(parseLength(attr.get("y"), null));
            haNode.setWidth(parseLength(attr.get("width"), null));
            haNode.setHeight(parseLength(attr.get("height"), null));
            haNode.setViewBox(parseRectangle(attr.get("viewBox"), null));
            haNode.setStyle(parseStyle(attr.get("style"), builder));

            haNode.addChildren(nodes);

            return haNode;
        }
    }
    
    private Rectangle2D.Float viewBox;
    private RaLength x;
    private RaLength y;
    private RaLength width;
    private RaLength height;

    @Override
    public void renderContent(RabbitRenderer renderer)
    {
        super.renderContent(renderer);

        for (int i = 0; i < children.size(); ++i)
        {
            RaElement ele = children.get(i);

            renderer.pushFrame(ele);

            ele.render(renderer);

            renderer.popFrame();
        }
    }


    /**
     * @return the width
     */
    public RaLength getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(RaLength width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public RaLength getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(RaLength height) {
        this.height = height;
    }

    /**
     * @return the viewBox
     */
    public Rectangle2D.Float getViewBox() {
        return viewBox;
    }

    /**
     * @param viewBox the viewBox to set
     */
    public void setViewBox(Rectangle2D.Float viewBox) {
        this.viewBox = viewBox;
    }

    /**
     * @return the x
     */
    public RaLength getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(RaLength x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public RaLength getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(RaLength y) {
        this.y = y;
    }
    
}
