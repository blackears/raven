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

package com.kitfox.raven.editor.node.tools.common.shape.brush;

import com.kitfox.coyote.math.BufferUtil;
import com.kitfox.coyote.math.Math2DUtil;
import com.kitfox.coyote.renderer.CyGLWrapper.TexSubTarget;
import com.kitfox.coyote.renderer.CyTextureDataProvider;
import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 *
 * @author kitfox
 */
public class RoundBrushSource extends CyTextureDataProvider
{
    final double brushWidth;
    final boolean antialias;
    final double hardness;
    
    private final int size;
    private ByteBuffer buffer;

    /**
     * 
     * @param brushWidth Width of the brush tip image
     * @param hardness Value on [0 1] which indicates fraction of brush
     * which is solid and which is gradient
     * @param antialias If true, feathering will be adjusted so that 
     * there is at least 1 pixel of feathering.
     */
    public RoundBrushSource(double brushWidth, double hardness, boolean antialias)
    {
        this.brushWidth = brushWidth;
        this.antialias = antialias;
        this.hardness = Math2DUtil.clamp(hardness, 0, 1);
        
        this.size = (int)Math.ceil(brushWidth);
        
        createBuffer();
    }
    
    private void createBuffer()
    {
        double mid = size / 2f;
        
        double brushRad = brushWidth / 2;
        double hardRad = brushRad * this.hardness;
        if (antialias)
        {
            hardRad = Math2DUtil.clamp(brushWidth - 1, 0, hardRad);
        }
        
        buffer = BufferUtil.allocateByte(size * size * 4);
        
        for (int j = 0; j < size; ++j)
        {
            for (int i = 0; i < size; ++i)
            {
                double rad = Math2DUtil.dist(i, j, mid, mid);
                double lum = brushRad == hardRad 
                        ? (rad > brushRad ? 0 : 1)
                        : 1 - (rad - hardRad) / (brushRad - hardRad);

//                lum = Math2DUtil.clamp(lum, 0, 1);
//                lum = Math.pow(lum, 4);
                
                //Create alpha mask
                byte b = (byte)(Math2DUtil.clamp(lum * 255, 0, 255));
                buffer.put((byte)0);
                buffer.put((byte)0);
                buffer.put((byte)0);
                buffer.put(b);
            }
        }
    }


    @Override
    public Buffer getData(TexSubTarget target)
    {
        buffer.rewind();
        return buffer;
    }

    /**
     * @return the size
     */
    public int getSize()
    {
        return size;
    }
    
    
}
