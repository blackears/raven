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

package com.kitfox.raven.editor.node.tools.common;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

/**
 *
 * @author kitfox
 */
public class MaskPaint
{
    final BufferedImage texture;

    final int SIDE_LEN = 8;
    final int STRIPE_CLIP = SIDE_LEN >> 1;
    TexturePaint[] paints;

    static MaskPaint instance = new MaskPaint();

    private MaskPaint()
    {
        this(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
    }

    public MaskPaint(GraphicsConfiguration gc)
    {
        texture = gc.createCompatibleImage(SIDE_LEN, SIDE_LEN);

        for (int j = 0; j < SIDE_LEN; ++j)
        {
            for (int i = 0; i < SIDE_LEN; ++i)
            {
                texture.setRGB(i, j, (((i + j) & STRIPE_CLIP) == STRIPE_CLIP) ? 0xff000000 : 0xffffffff);
            }
        }

        paints = new TexturePaint[SIDE_LEN];
        for (int i = 0; i < SIDE_LEN; ++i)
        {
            paints[i] = new TexturePaint(texture, new Rectangle(i, i, SIDE_LEN, SIDE_LEN));
        }
    }

    public static MaskPaint inst()
    {
        return instance;
    }

    public TexturePaint getPaint(int idx)
    {
        return paints[idx & (SIDE_LEN - 1)];
    }

    public TexturePaint getPaint()
    {
        return getPaint((int)System.currentTimeMillis() / 128);
    }

}
