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

package com.kitfox.raven.paint.control;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

/**
 *
 * @author kitfox
 */
public class UnderlayPaint
{
    private static final UnderlayPaint instance = new UnderlayPaint();

    private final TexturePaint paint;

    private UnderlayPaint()
    {
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDefaultConfiguration();
        
        BufferedImage img = gc.createCompatibleImage(16, 16, Transparency.OPAQUE);
        Graphics2D pg = img.createGraphics();

        pg.setColor(Color.gray);
        pg.fillRect(0, 0, 16, 16);

        pg.setColor(Color.lightGray);
        pg.fillRect(0, 0, 8, 8);
        pg.fillRect(8, 8, 8, 8);

        pg.dispose();

        paint = new TexturePaint(img, new Rectangle(0, 0, 16, 16));
    }

    /**
     * @return the instance
     */
    public static UnderlayPaint inst()
    {
        return instance;
    }

    /**
     * @return the paint
     */
    public TexturePaint getPaint()
    {
        return paint;
    }


}
