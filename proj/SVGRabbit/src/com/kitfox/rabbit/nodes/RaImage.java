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
import com.kitfox.rabbit.types.ImageRef;
import com.kitfox.raven.util.service.ServiceInst;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class RaImage extends RaElement
{
    @ServiceInst(service=RaElementLoader.class)
    public static class Loader extends RaElementLoader<RaImage>
    {
        protected Loader(String tag)
        {
            super(tag);
        }

        public Loader()
        {
            super("image");
        }

        @Override
        public RaImage create(RabbitDocument builder, HashMap<String, String> attr, ArrayList<RaElement> nodes)
        {
            RaImage haNode = new RaImage();

            haNode.setStyleClasses(parseClasses(attr.get("class"), builder));
            haNode.setTransform(parseTransform(attr.get("transform"), null));
            haNode.setStyle(parseStyle(attr.get("style"), builder));
            haNode.setX(parseFloat(attr.get("x"), 0));
            haNode.setY(parseFloat(attr.get("y"), 0));
            haNode.setWidth(parseFloat(attr.get("width"), 0));
            haNode.setHeight(parseFloat(attr.get("height"), 0));
            haNode.setImage(parseImageRef(attr.get("href"), builder));

            return haNode;

        }
    }

    private float x;
    private float y;
    private float width;
    private float height;
    private ImageRef image;
    private AffineTransform transform;

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
     * @return the image
     */
    public ImageRef getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(ImageRef image) {
        this.image = image;
    }

    @Override
    public void renderContent(RabbitRenderer renderer)
    {
        super.renderContent(renderer);

        if (transform != null)
        {
            renderer.mulTransform(transform);
        }

        renderer.drawImage(image, x, y, width, height);
    }


}
