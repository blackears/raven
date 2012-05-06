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
import com.kitfox.raven.editor.node.scene.RavenNodeRoot;
import com.kitfox.raven.util.tree.ChildWrapperEvent;
import com.kitfox.raven.util.tree.NodeSymbolListener;
import com.kitfox.raven.util.tree.NodeSymbolWeakListener;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;

/**
 * Organize a set of tiles to calcuate the rendered state of the
 * scene graph in a separate thread.
 *
 * @author kitfox
 */
@Deprecated
public class DisplayTiles implements NodeSymbolListener
{
    public static final int TILE_SIZE = 256;

    final GraphicsConfiguration gc;
    private Rectangle deviceBounds;
    private RavenNodeRoot scene;
    NodeSymbolWeakListener listenerScene;

    HashMap<WeakReference<Tile>, BufferedImage> tilePool
            = new HashMap<WeakReference<Tile>, BufferedImage>();
    ReferenceQueue<Tile> deadTileQueue = new ReferenceQueue<Tile>();

    HashMap<TileCoord, Tile> tiles = new HashMap<TileCoord, Tile>();
    boolean dirty;
    int minTileX = Integer.MAX_VALUE;
    int minTileY = Integer.MAX_VALUE;
    int maxTileX = Integer.MIN_VALUE;
    int maxTileY = Integer.MIN_VALUE;

    Update updateThread;
    boolean exitThread;

    ArrayList<DisplayTilesListener> listeners = new ArrayList<DisplayTilesListener>();

    private DisplayTiles(GraphicsConfiguration gc)
    {
        this.gc = gc;
        this.updateThread = new Update();
    }

    public static DisplayTiles create(GraphicsConfiguration gc)
    {
        DisplayTiles tiles = new DisplayTiles(gc);
        tiles.updateThread.start();
        return tiles;
    }

//    int numNew;
//    int numRecycle;

    private Tile allocTile(TileCoord coord)
    {
        WeakReference<Tile> ref = (WeakReference<Tile>)deadTileQueue.poll();
        BufferedImage buffer;
        if (ref != null)
        {
            buffer = tilePool.remove(ref);
//            ++numRecycle;
        }
        else
        {
            buffer = gc.createCompatibleImage(TILE_SIZE, TILE_SIZE,
                    Transparency.TRANSLUCENT);
//            ++numNew;
        }
//            System.err.println("Display tiles: new/recycle " + numNew + " / " + numRecycle);

        Tile tile = new Tile(coord, buffer);
        ref = new WeakReference<Tile>(tile, deadTileQueue);
        tilePool.put(ref, buffer);
        
        return tile;
    }

    private void reallocTiles()
    {
        if (deviceBounds == null || scene == null)
        {
            tiles.clear();
            dirty = false;
            minTileX = minTileY = Integer.MAX_VALUE;
            maxTileX = maxTileY = Integer.MIN_VALUE;
            return;
        }

        minTileX = (int)Math.floor(deviceBounds.getMinX() / TILE_SIZE);
        minTileY = (int)Math.floor(deviceBounds.getMinY() / TILE_SIZE);
        maxTileX = (int)Math.ceil(deviceBounds.getMaxX() / TILE_SIZE);
        maxTileY = (int)Math.ceil(deviceBounds.getMaxY() / TILE_SIZE);
        
        dirty = true;

        tiles.clear();
    }

    public void setAllTilesDirty()
    {
        dirty = true;
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

    public void exit()
    {
        exitThread = true;
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
    public RavenNodeRoot getScene()
    {
        return scene;
    }

    /**
     * @param scene the scene to set
     */
    public synchronized void setScene(RavenNodeRoot scene)
    {
        if (listenerScene != null)
        {
            listenerScene.remove();
            listenerScene = null;
        }
        
        this.scene = scene;

        if (this.scene != null)
        {
            listenerScene = new NodeSymbolWeakListener(this, scene);
            scene.addNodeSymbolListener(listenerScene);
        }

        reallocTiles();
    }

    public synchronized ArrayList<Tile> getTiles()
    {
        return new ArrayList<Tile>(tiles.values());
    }

    @Override
    public void symbolNameChanged(PropertyChangeEvent evt)
    {
    }

    @Override
    public void symbolPropertyChanged(PropertyChangeEvent evt)
    {
        setAllTilesDirty();
    }

    @Override
    public void symbolNodeChildAdded(ChildWrapperEvent evt)
    {
        setAllTilesDirty();
    }

    @Override
    public void symbolNodeChildRemoved(ChildWrapperEvent evt)
    {
        setAllTilesDirty();
    }

    //--------------------------------------

    public class Tile
    {
        private final BufferedImage tile;
        private final TileCoord coord;

        public Tile(TileCoord coord, BufferedImage tile)
        {
            this.coord = coord;
            this.tile = tile;
        }

        /**
         * @return the tile
         */
        public BufferedImage getTile() {
            return tile;
        }

        /**
         * @return the coord
         */
        public TileCoord getCoord() {
            return coord;
        }
    }

    class Update extends Thread
    {
        Rectangle tileBounds = new Rectangle(0, 0, TILE_SIZE, TILE_SIZE);

        public Update()
        {
            super("Raven Tile Update");
            setDaemon(true);
            setPriority(MIN_PRIORITY);
//            setPriority(NORM_PRIORITY);
//            setPriority(MAX_PRIORITY);
        }

        @Override
        public void run()
        {
            int curPass = 0;
            long startTime = System.currentTimeMillis();

            while (!exitThread)
            {
                if (!dirty)
                {
                    continue;
                }

                dirty = false;
                RavenNodeRoot root = getScene();
                if (root == null)
                {
                    continue;
                }

                long curTime = System.currentTimeMillis();

                for (int j = minTileY; j <= maxTileY; ++j)
                {
                    for (int i = minTileX; i <= maxTileX; ++i)
                    {
                        /*
                        TileCoord coord = new TileCoord(i, j);
                        Tile tile = allocTile(coord);
                        tileBounds.x = tile.coord.x * TILE_SIZE;
                        tileBounds.y = tile.coord.y * TILE_SIZE;

                        RavenRenderer renderer = new RavenRenderer(
                                gc, tileBounds, tile.getTile(),
                                startTime, curTime, curPass);
                        root.render(renderer);
                        renderer.dispose();

                        synchronized (DisplayTiles.this)
                        {
                            tiles.put(coord, tile);
                        }
                        */
                    }
                }

                fireDisplayTilesComputed();
//                Thread.yield();
                ++curPass;
            }
        }
    }
}
