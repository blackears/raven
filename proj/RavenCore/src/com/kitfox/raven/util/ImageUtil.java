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

package com.kitfox.raven.util;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author kitfox
 */
public class ImageUtil
{
    static public BufferedImage loadImageResource(String resName, Class resClass)
    {
        return loadImageResource(resName, resClass, null);
    }

    static public BufferedImage loadImageResource(URL url)
    {
        return loadImageResource(url, null);
    }

    static public BufferedImage loadImageResource(String resName, Class resClass, Color knockoutColor)
    {
        return loadImageResource(resClass.getResource(resName), knockoutColor);
    }

    static public BufferedImage loadImageResource(String resName)
    {
        return loadImageResource(resName, (Color)null);
    }

    static public BufferedImage loadImageResource(String resName, Color knockoutColor)
    {
        return loadImageResource(ImageUtil.class.getResource(resName), knockoutColor);
    }

    public static BufferedImage loadImageResource(File file, Color knockoutColor)
    {
        try {
            return loadImageResource(file.toURI().toURL(), knockoutColor);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ImageUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    static public BufferedImage loadImageResource(URL url, Color knockoutColor)
    {
        try {
            BufferedImage img = ImageIO.read(url);
            return createDeviceCompatableImage(img, knockoutColor);
        } catch (IOException ex) {
            Logger.getLogger(ImageUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    static public BufferedImage createDeviceCompatableImage(int width, int height)
    {
        return createDeviceCompatableImage(width, height, Transparency.OPAQUE);
    }

    static public BufferedImage createDeviceCompatableImage(int width, int height, int transparency)
    {
        GraphicsEnvironment graphEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice graphDevice = graphEnv.getDefaultScreenDevice();
        GraphicsConfiguration graphicConf = graphDevice.getDefaultConfiguration();

        BufferedImage img = graphicConf.createCompatibleImage(width, height, transparency);

        return img;
    }

    static public BufferedImage createDeviceCompatableImage(BufferedImage srcImg)
    {
        return createDeviceCompatableImage(srcImg, null);
    }

    static public BufferedImage createDeviceCompatableImage(BufferedImage srcImg, Color knockoutColor)
    {
        GraphicsEnvironment graphEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice graphDevice = graphEnv.getDefaultScreenDevice();
        GraphicsConfiguration graphicConf = graphDevice.getDefaultConfiguration();

        if (knockoutColor != null)
        {
            int koRgb = knockoutColor.getRGB() & 0xffffff;

            BufferedImage newFrameImg = graphicConf.createCompatibleImage(srcImg.getWidth(), srcImg.getHeight(), Transparency.BITMASK);
            for (int j = 0; j < srcImg.getHeight(); ++j)
            {
                for (int i = 0; i < srcImg.getWidth(); ++i)
                {
                    int rgb = srcImg.getRGB(i, j) & 0xffffff;
                    if (rgb == koRgb)
                    {
                        newFrameImg.setRGB(i, j, 0);
                    }
                    else
                    {
                        newFrameImg.setRGB(i, j, rgb | 0xff000000);
                    }
                }
            }

            srcImg = newFrameImg;
        }

        BufferedImage img = graphicConf.createCompatibleImage(srcImg.getWidth(), srcImg.getHeight(), srcImg.getTransparency());
        Graphics2D g = img.createGraphics();
        g.drawImage(srcImg, 0, 0, null);
        g.dispose();

        return img;
    }

    public static BufferedImage createErrorImage()
    {
        return createErrorImage(32, 32, "Error");
    }

    public static BufferedImage createErrorImage(int width, int height, String text)
    {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.RED);
        g.drawLine(0, 0, width, height);
        g.drawLine(0, height, width, 0);
        g.setColor(Color.BLACK);

        FontMetrics fm = g.getFontMetrics();
        Rectangle2D textBounds = fm.getStringBounds(text, g);

        g.drawString(text,
                (int)(width - textBounds.getWidth()) / 2,
                (int)(height - textBounds.getHeight()) / 2 + fm.getAscent());

        g.dispose();

        return img;
    }
}
