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

package com.kitfox.raven.editor.view.display;

import com.kitfox.raven.editor.node.renderer.RavenRenderer;
import com.kitfox.raven.editor.node.tools.common.ServiceRenderer2D;
import com.kitfox.raven.util.tree.ChildWrapperEvent;
import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.util.tree.NodeDocumentListener;
import com.kitfox.raven.util.tree.NodeDocumentWeakListener;
import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.EventObject;

/**
 * Organize a set of tiles to calcuate the rendered state of the
 * scene graph in a separate thread.
 *
 * Uses double buffering and does not submit a tile for rerendering
 * until all younger tiles have had a chance to regenerate.
 * 
 * @author kitfox
 */
@Deprecated
public class DisplayTileUpdater implements NodeDocumentListener
{
    public static final int NUM_THREADS = 1;
    public static final int TILE_SIZE = 256;

    TileState[] tiles;
    int lastChosenIndex;
    boolean needsCleaning;
    
    final GraphicsConfiguration gc;
    private Rectangle deviceBounds;
    private NodeDocument scene;
//    private RavenNodeRoot scene;
    NodeDocumentWeakListener listenerScene;
    UpdateThread thread;

//    TileCache<TileState> tileCache;

    ArrayList<DisplayTilesListener> listeners = new ArrayList<DisplayTilesListener>();

    private DisplayTileUpdater(GraphicsConfiguration gc)
    {
        this.gc = gc;
//        this.tileCache = new TileCache<TileState>(gc, TILE_SIZE, TILE_SIZE, true);
    }

    public static DisplayTileUpdater create(GraphicsConfiguration gc)
    {
        DisplayTileUpdater updater = new DisplayTileUpdater(gc);
        for (int i = 0; i < NUM_THREADS; ++i)
        {
            updater.startThread(i);
        }
        return updater;
    }

    private void startThread(int index)
    {
        UpdateThread thread = new UpdateThread(index);
        thread.start();
    }

    public void addDisplayTilesListener(DisplayTilesListener l)
    {
        listeners.add(l);
    }

    public void removeDisplayTilesListener(DisplayTilesListener l)
    {
        listeners.remove(l);
    }

    private void fireDisplayTilesComputed()
    {
        EventObject evt = new EventObject(this);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).displayTilesComputed(evt);
        }
    }

    public synchronized void blit(Graphics g)
    {
        if (tiles == null)
        {
            return;
        }

        for (int i = 0; i < tiles.length; ++i)
        {
            TileState tile = tiles[i];
            g.drawImage(tile.tileComplete, tile.xpos, tile.ypos, null);
        }
    }

    /**
     * @return the deviceBounds
     */
    public Rectangle getDeviceBounds()
    {
        return deviceBounds;
    }

    /**
     * @param deviceBounds the deviceBounds to set
     */
    public synchronized void setDeviceBounds(Rectangle deviceBounds)
    {
        this.deviceBounds = deviceBounds;
        reallocTiles();
    }

    /**
     * @return the scene
     */
    public NodeDocument getScene()
    {
        return scene;
    }

    /**
     * @param scene the scene to set
     */
    public synchronized void setScene(NodeDocument scene)
    {
        if (listenerScene != null)
        {
            listenerScene.remove();
            listenerScene = null;
        }

        this.scene = scene;

        if (this.scene != null)
        {
            listenerScene = new NodeDocumentWeakListener(this, scene);
            scene.addNodeDocumentListener(listenerScene);
        }

        reallocTiles();
    }

    @Override
    public void documentPropertyChanged(PropertyChangeEvent evt)
    {
        setDirty();
    }

    @Override
    public void documentNodeChildAdded(ChildWrapperEvent evt)
    {
        setDirty();
    }

    @Override
    public void documentNodeChildRemoved(ChildWrapperEvent evt)
    {
        setDirty();
    }

    private void reallocTiles()
    {
        if (scene == null || deviceBounds == null)
        {
            tiles = null;
            return;
        }

        int minTileX = (int)Math.floor(deviceBounds.getMinX() / TILE_SIZE);
        int minTileY = (int)Math.floor(deviceBounds.getMinY() / TILE_SIZE);
        int maxTileX = (int)Math.ceil(deviceBounds.getMaxX() / TILE_SIZE);
        int maxTileY = (int)Math.ceil(deviceBounds.getMaxY() / TILE_SIZE);

        int width = maxTileX - minTileX + 1;
        int height = maxTileY - minTileY + 1;

        tiles = new TileState[width * height];

        for (int j = 0; j < height; ++j)
        {
            for (int i = 0; i < width; ++i)
            {
                tiles[j * width + i] = new TileState(
                        (i + minTileX) * TILE_SIZE, (j + minTileY) * TILE_SIZE);
            }
        }

        needsCleaning = true;
    }
    
    public synchronized void setDirty()
    {
        for (int i = 0; i < tiles.length; ++i)
        {
            tiles[i].needsCleaning = true;
        }
        needsCleaning = true;
    }


    private synchronized void cleanNextTile(UpdateThread thread)
    {
        //Scan for next index
        int idx = lastChosenIndex + 1;
        if (idx == tiles.length)
        {
            idx = 0;
        }

        for (int i = 0; i < tiles.length; ++i)
        {
            TileState tile = tiles[idx];
            if (tile.needsCleaning && tile.cleaner == null)
            {
                tile.needsCleaning = false;
                tile.cleaner = thread;
                thread.startCleaning(tile);
                lastChosenIndex = idx;
                return;
            }

            //Try next one
            ++idx;
            if (idx == tiles.length)
            {
                idx = 0;
            }
        }

        //All tiles are clean or being cleaned
        needsCleaning = false;
        fireDisplayTilesComputed();
    }

    private synchronized void finishCleaning(UpdateThread thread)
    {
        TileState target = thread.target;
        target.swapBuffers();

        if (!target.needsCleaning)
        {
            //All done
            target.cleaner = null;
            thread.startCleaning(null);
            return;
        }

        //Tile became dirty while it was being cleaned.  Clean it
        // again

        //First scan to see if we might want to swap cleaning this
        // tile with another
        int idx = lastChosenIndex + 1;
        if (idx == tiles.length)
        {
            idx = 0;
        }

        for (int i = 0; i < tiles.length; ++i)
        {
            TileState tile = tiles[idx];
            if (tile.needsCleaning && tile.cleaner == null)
            {
                target.cleaner = null;
                target = tile;
                target.cleaner = thread;
                break;
            }

            ++idx;
            if (idx == tiles.length)
            {
                idx = 0;
            }
        }

        //Clean it
        target.needsCleaning = false;
        thread.startCleaning(target);
    }

    //---------------------------------------

    class UpdateThread extends Thread
    {
        TileState target;
        Rectangle tileBounds = new Rectangle(0, 0, TILE_SIZE, TILE_SIZE);

        public UpdateThread(int index)
        {
            super("Display Tile Updater #" + index);
            setDaemon(true);
            setPriority(MIN_PRIORITY);
        }

        @Override
        public void run()
        {
            int curPass = 0;
            long startTime = System.currentTimeMillis();

            while (true)
            {
                if (!needsCleaning)
                {
                    continue;
                }

                long curTime = System.currentTimeMillis();

                cleanNextTile(this);
                while (target != null)
                {
                    //build tile
                    target.clearBackBuffer();
                    NodeDocument root = getScene();
                    if (root != null)
                    {
                        tileBounds.x = target.xpos;
                        tileBounds.y = target.ypos;

                        RavenRenderer renderer = new RavenRenderer(
                                gc, tileBounds, target.tileBack,
                                startTime, curTime, curPass);
                        ServiceRenderer2D service =
                                root.getNodeService(ServiceRenderer2D.class, false);
                        if (service != null)
                        {
                            service.render(renderer);
                        }
                        renderer.dispose();
                    }

                    finishCleaning(this);
                }

                Thread.yield();
//                try
//                {
//                    Thread.sleep(10);
//                } catch (InterruptedException ex)
//                {
//                    Logger.getLogger(DisplayTileUpdater.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }
        }

        private void startCleaning(TileState tile)
        {
            this.target = tile;
        }

    }

int tileNum;

    class TileState
    {
        final int xpos;
        final int ypos;

        //If true, tile has become dirty and needs an update.
        // Will be set true as soon as a cleaner graps it.  Note that
        // it can be set false againwhile it is in the process of being
        // cleaned.  If this happens, the cleaning thread is responsible
        // for handling re-cleaning once the current cleaning pass is
        /// finished.
        boolean needsCleaning;

        //If set, this thread is working to clean this tile.
        UpdateThread cleaner;

        //Tile being built
        BufferedImage tileBack;

        //Last tile to have been calculated
        BufferedImage tileComplete;

        public TileState(int xpos, int ypos,
                boolean needsCleaning, UpdateThread cleaner)
        {
            this.xpos = xpos;
            this.ypos = ypos;
            this.needsCleaning = needsCleaning;
            this.cleaner = cleaner;
            this.tileBack = gc.createCompatibleImage(TILE_SIZE, TILE_SIZE,
                    Transparency.TRANSLUCENT);
            this.tileComplete = gc.createCompatibleImage(TILE_SIZE, TILE_SIZE,
                    Transparency.TRANSLUCENT);
//System.err.println("TileState contruct " + tileNum++);
        }


        public TileState(int xpos, int ypos)
        {
            this(xpos, ypos, true, null);
        }

        private void swapBuffers()
        {
            BufferedImage img = tileBack;
            tileBack = tileComplete;
            tileComplete = img;
        }

        private void clearBackBuffer()
        {
            Graphics2D g = tileBack.createGraphics();
            g.setComposite(AlphaComposite.Clear);
            g.fillRect(0, 0, TILE_SIZE, TILE_SIZE);
            g.dispose();
        }


    }
}
