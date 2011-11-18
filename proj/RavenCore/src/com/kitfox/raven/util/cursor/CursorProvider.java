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

package com.kitfox.raven.util.cursor;

import com.kitfox.raven.util.ImageUtil;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;


/**
 *
 * @author kitfox
 */
abstract public class CursorProvider
{
    private final Cursor cursor;

    public CursorProvider(Image img, Point hotSpot, String name)
    {
        Toolkit tk = Toolkit.getDefaultToolkit();
        cursor = tk.createCustomCursor(img, hotSpot, name);
    }

    public CursorProvider(String resource, String name)
    {
        this(resource, null, name);
    }

    public CursorProvider(String resource, Point hotSpot, String name)
    {
        BufferedImage img = null;
        try
        {
            img = ImageIO.read(CursorProvider.class.getResource(resource));
        }
        catch (IOException ex)
        {
            Logger.getLogger(CursorProvider.class.getName()).log(Level.SEVERE, null, ex);
        }

        BufferedImage img2 = ImageUtil.createDeviceCompatableImage(img, Color.magenta);
        
//        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        GraphicsDevice gd = ge.getDefaultScreenDevice();
//        GraphicsConfiguration gc = gd.getDefaultConfiguration();
////        BufferedImage img2 = gc.createCompatibleImage(img.getWidth(), img.getHeight(), img.getTransparency());
//        BufferedImage img2 = gc.createCompatibleImage(img.getWidth(), img.getHeight(), Transparency.TRANSLUCENT);
//
//        Graphics2D g = img2.createGraphics();
//        g.drawImage(img, 0, 0, null);
//        g.dispose();
//
//        for (int j = 0; j < img2.getHeight(); ++j)
//        {
//            for (int i = 0; i < img2.getWidth(); ++i)
//            {
//                int val = img2.getRGB(i, j);
//                if (val == 0xffff00ff)
//                {
//                    img2.setRGB(i, j, 0);
//                }
//            }
//        }

        //GraphicsConfiguration gc = GraphicsConfiguration.class

        if (hotSpot == null)
        {
            hotSpot = new Point(img.getWidth() / 2, img.getHeight() / 2);
        }

        Toolkit tk = Toolkit.getDefaultToolkit();
        tk.getImage(CursorProvider.class.getResource(resource));
        cursor = tk.createCustomCursor(img2, hotSpot, name);
    }

    /**
     * @return the cursor
     */
    public Cursor getCursor()
    {
        return cursor;
    }


}
