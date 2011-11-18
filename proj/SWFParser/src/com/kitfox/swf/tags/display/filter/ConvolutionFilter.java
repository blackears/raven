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

package com.kitfox.swf.tags.display.filter;

import com.kitfox.swf.dataType.RGBA;
import com.kitfox.swf.dataType.SWFDataReader;
import java.io.IOException;

/**
 *
 * @author kitfox
 */
public class ConvolutionFilter extends Filter
{
    int matrixX;
    int matrixY;
    float divisor;
    float bias;
    float[] matrix;
    RGBA defaultColor;
    boolean clamp;
    boolean preserveAlpha;

    public ConvolutionFilter(SWFDataReader data) throws IOException
    {
        matrixX = data.getUI8();
        matrixY = data.getUI8();
        divisor = data.getFLOAT();
        bias = data.getFLOAT();

        matrix = new float[matrixX * matrixY];
        for (int i = 0; i < matrix.length; ++i)
        {
            matrix[i] = data.getFLOAT();
        }

        defaultColor = data.getRGBA();
        data.getUB(6);
        clamp = data.getUB(1) != 0;
        preserveAlpha = data.getUB(1) != 0;
        data.flushToByteBoundary();
    }
}
