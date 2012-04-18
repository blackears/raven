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

package com.kitfox.raven.raster;

import com.kitfox.raven.util.Grid;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author kitfox
 */
public class TiledRasterData
{
    Grid<byte[]> data;
    int tileWidth;
    int tileHeight;

    public TiledRasterData(Grid<byte[]> data, int tileWidth, int tileHeight)
    {
        this.data = data;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    public void dump()
    {
        BufferedImage img = toBufferedImage();
        
        try
        {
            ImageIO.write(img, "png", new File("dumpRasterTile.png"));
        } catch (IOException ex)
        {
            Logger.getLogger(TiledRasterData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public BufferedImage toBufferedImage()
    {
        BufferedImage img = new BufferedImage(tileWidth * data.getWidth(),
                tileHeight * data.getHeight(), 
                BufferedImage.TYPE_INT_ARGB);
        
        for (int j = 0; j < data.getHeight(); ++j)
        {
            for (int i = 0; i < data.getWidth(); ++i)
            {
                byte[] buf = data.getValue(
                        data.getOffsetX() + i,
                        data.getOffsetY() + j);
                if (buf != null)
                {
                    writeTileData(buf, img, i * tileWidth, j * tileHeight);
                }
            }
        }
        
        return img;
    }
    
    private void writeTileData(byte[] buf,
            BufferedImage img, int imgX, int imgY)
    {
        for (int j = 0; j < tileHeight; ++j)
        {
            for (int i = 0; i < tileWidth; ++i)
            {
                int offset = (j * tileWidth + i) * 4;
                byte r = buf[offset];
                byte g = buf[offset + 1];
                byte b = buf[offset + 2];
                byte a = buf[offset + 3];
                
                int col = ((a & 0xff) << 24)
                        | ((r & 0xff) << 16)
                        | ((g & 0xff) << 8)
                        | (b & 0xff);

                img.setRGB(i + imgX, j + imgY, col);
            }
        }
    }

}