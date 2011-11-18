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

package com.kitfox.coyote.renderer.jogl;

import com.kitfox.coyote.math.BufferUtil;
import com.kitfox.coyote.renderer.CyTextureDataProvider;
import com.kitfox.coyote.renderer.CyTransparency;
import com.kitfox.coyote.renderer.GLWrapper.TexSubTarget;
import java.awt.Color;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 *
 * @author kitfox
 */
abstract public class TexSourceAWT extends CyTextureDataProvider
{
    abstract protected BufferedImage getImg();

    public int getWidth()
    {
        return getImg().getWidth();
    }

    public int getHeight()
    {
        return getImg().getHeight();
    }

    public CyTransparency getTransparency()
    {
        switch (getImg().getTransparency())
        {
            case Transparency.OPAQUE:
                return CyTransparency.OPAQUE;
            case Transparency.BITMASK:
                return CyTransparency.BITMASK;
            default:
            case Transparency.TRANSLUCENT:
                return CyTransparency.TRANSLUCENT;
        }
    }

    @Override
    public Buffer getData(TexSubTarget target)
    {
        BufferedImage img = getImg();

        ByteBuffer buf = BufferUtil.allocateByte(img.getWidth() * img.getHeight() * 4);
        for (int j = img.getHeight() - 1; j >= 0; --j)
        {
            for (int i = 0; i < img.getWidth(); ++i)
            {
                int argb = img.getRGB(i, j);

                buf.put((byte)((argb >> 16) & 0xff));
                buf.put((byte)((argb >> 8) & 0xff));
                buf.put((byte)((argb) & 0xff));
                buf.put((byte)((argb >> 24) & 0xff));
            }
        }
        buf.rewind();
        return buf;
    }

    protected BufferedImage clearKnockoutColor(BufferedImage img, Color knockoutColor)
    {
        if (knockoutColor == null)
        {
            return img;
        }

        BufferedImage ret = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int j = 0; j < img.getHeight(); ++j)
        {
            for (int i = 0; i < img.getWidth(); ++i)
            {
                int argb = img.getRGB(i, j);
                if (argb == knockoutColor.getRGB())
                {
                    argb = 0;
                }
                ret.setRGB(i, j, argb);
            }
        }
        return ret;
    }
}
