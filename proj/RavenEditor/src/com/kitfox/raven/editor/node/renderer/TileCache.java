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

package com.kitfox.raven.editor.node.renderer;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
@Deprecated
public class TileCache<T>
{
    HashMap<WeakReference<T>, BufferedImage> tilePool
            = new HashMap<WeakReference<T>, BufferedImage>();
    ReferenceQueue<T> deadTileQueue = new ReferenceQueue<T>();

    final GraphicsConfiguration gc;
    final int width;
    final int height;
    final boolean cleanTiles;

    public TileCache(GraphicsConfiguration gc, int width, int height, boolean cleanTiles)
    {
        this.gc = gc;
        this.width = width;
        this.height = height;
        this.cleanTiles = cleanTiles;
    }

//int numNew;
//int numRecycle;

    public BufferedImage allocTile(T owner)
    {
        WeakReference<T> ref = (WeakReference<T>)deadTileQueue.poll();
        BufferedImage buffer;
        if (ref != null)
        {
            buffer = tilePool.remove(ref);

            if (cleanTiles)
            {
                Graphics2D g = buffer.createGraphics();
                g.setComposite(AlphaComposite.Clear);
                g.fillRect(0, 0, width, height);
                g.dispose();
            }
//++numRecycle;
        }
        else
        {
            buffer = gc.createCompatibleImage(width, height,
                    Transparency.TRANSLUCENT);
//++numNew;
        }
//System.err.println("Display tiles: new/recycle " + numNew + " / " + numRecycle);

        ref = new WeakReference<T>(owner, deadTileQueue);
        tilePool.put(ref, buffer);

        return buffer;
    }

}
