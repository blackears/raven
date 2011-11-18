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

package com.kitfox.rabbit.render.awt;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author kitfox
 */
public class Surface2DAwtImage extends Surface2DAwt
{
    private final BufferedImage image;

    private Surface2DAwtImage(Graphics2D g, BufferedImage image, Rectangle2D region)
    {
        super(g, region);
        this.image = image;
    }

    public static Surface2DAwtImage create(GraphicsConfiguration config, Rectangle2D region)
    {
        BufferedImage img = config.createCompatibleImage(
                (int)Math.ceil(region.getWidth()),
                (int)Math.ceil(region.getHeight()),
                Transparency.TRANSLUCENT);

        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

//        g.translate(-region.getX(), -region.getY());
//        g.translate(region.getX(), region.getY());

        return new Surface2DAwtImage(g, img, region);
    }

    /**
     * @return the image
     */
    public BufferedImage getImage() {
        return image;
    }
}
