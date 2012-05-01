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

package com.kitfox.raven.bitmap.importer;

import com.kitfox.coyote.math.MathColorUtil;
import com.kitfox.coyote.shape.outliner.LevelSampler;
import java.awt.image.BufferedImage;

/**
 *
 * @author kitfox
 */
public class BitmapSampler implements LevelSampler
{
    final BufferedImage img;
    final BitmapColorMatrix matrixType;
    final float threshold;

    public BitmapSampler(BufferedImage img, BitmapColorMatrix matrixType, float threshold)
    {
        this.img = img;
        this.matrixType = matrixType;
        this.threshold = threshold;
    }

    @Override
    public int getLevel(int x, int y)
    {
        int col = img.getRGB(x, y);
        int a = (col >> 24) & 0xff;
        int r = (col >> 16) & 0xff;
        int g = (col >> 8) & 0xff;
        int b = (col) & 0xff;
        
        float value = 0;
        switch (matrixType)
        {
            case ALPHA:
                value = b / 255f;
                break;
            case LUMINANCE:
            {
                float[] yuv = MathColorUtil.RGBtoYUV(r / 255f, g / 255f, b / 255f);
                value = yuv[0];
                break;
            }
        }
        
        return value > threshold ? 1 : 0;
    }
    
}
