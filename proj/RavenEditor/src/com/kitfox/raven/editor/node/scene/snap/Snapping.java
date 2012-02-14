/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.raven.editor.node.scene.snap;

import com.kitfox.cache.CacheMap;
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class Snapping
{
    public static final String PROP_SNAP_VERTEX = "snapVertex";
    private final boolean snapVertex;
    public static final String PROP_SNAP_GRID = "snapGrid";
    private final boolean snapGrid;
    public static final String PROP_SHOW_GRID = "showGrid";
    private final boolean showGrid;
    
    //Grid spacing in 1/100 of pixels
    public static final String PROP_GRID_SPACE_MAJ = "gridSpaceMaj";
    private final int gridSpacingMajor;
    public static final String PROP_GRID_SPACE_MIN = "gridSpaceMin";
    private final int gridSpacingMinor;
    public static final String PROP_GRID_SPACE_OFFX = "gridSpaceOffX";
    private final int gridSpacingOffsetX;
    public static final String PROP_GRID_SPACE_OFFY = "gridSpaceOffY";
    private final int gridSpacingOffsetY;

    public Snapping(boolean snapVertex, boolean snapGrid, boolean showGrid, int gridSpacingMajor, int gridSpacingMinor, int gridSpacingOffsetX, int gridSpacingOffsetY)
    {
        this.snapVertex = snapVertex;
        this.snapGrid = snapGrid;
        this.showGrid = showGrid;
        this.gridSpacingMajor = gridSpacingMajor;
        this.gridSpacingMinor = gridSpacingMinor;
        this.gridSpacingOffsetX = gridSpacingOffsetX;
        this.gridSpacingOffsetY = gridSpacingOffsetY;
    }

    public Snapping()
    {
        this(false, false, false,
                10000, 1000, 0, 0
                );
    }

    public static Snapping create(String text)
    {
        try
        {
            CacheMap map = (CacheMap)CacheParser.parse(text);
            
            boolean snapVertex = map.getBoolean(PROP_SNAP_VERTEX, false);
            boolean snapGrid = map.getBoolean(PROP_SNAP_GRID, false);
            boolean showGrid = map.getBoolean(PROP_SHOW_GRID, false);
            
            int gridSpaceMaj = map.getInteger(PROP_GRID_SPACE_MAJ, 100);
            int gridSpaceMin = map.getInteger(PROP_GRID_SPACE_MIN, 10);
            int gridSpaceOffX = map.getInteger(PROP_GRID_SPACE_OFFX, 0);
            int gridSpaceOffY = map.getInteger(PROP_GRID_SPACE_OFFY, 0);
            
            return new Snapping(snapVertex, snapGrid, showGrid, gridSpaceMaj, gridSpaceMin, gridSpaceOffX, gridSpaceOffY);
        } catch (ParseException ex)
        {
            Logger.getLogger(GraphLayout.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    public CacheMap toCache()
    {
        CacheMap map = new CacheMap();

        map.put(PROP_SNAP_VERTEX, snapVertex);
        map.put(PROP_SNAP_GRID, snapGrid);
        map.put(PROP_SHOW_GRID, showGrid);

        map.put(PROP_GRID_SPACE_MAJ, gridSpacingMajor);
        map.put(PROP_GRID_SPACE_MIN, gridSpacingMinor);
        map.put(PROP_GRID_SPACE_OFFX, gridSpacingOffsetX);
        map.put(PROP_GRID_SPACE_OFFY, gridSpacingOffsetY);
        
        return map;
    }

    @Override
    public String toString()
    {
        return toCache().toString();
    }

    /**
     * @return the snapVertex
     */
    public boolean isSnapVertex()
    {
        return snapVertex;
    }

    /**
     * @return the snapGrid
     */
    public boolean isSnapGrid()
    {
        return snapGrid;
    }

    /**
     * @return the showGrid
     */
    public boolean isShowGrid()
    {
        return showGrid;
    }

    /**
     * @return the gridSpacingMajor
     */
    public int getGridSpacingMajor()
    {
        return gridSpacingMajor;
    }

    /**
     * @return the gridSpacingMinor
     */
    public int getGridSpacingMinor()
    {
        return gridSpacingMinor;
    }

    /**
     * @return the gridSpacingOffsetX
     */
    public int getGridSpacingOffsetX()
    {
        return gridSpacingOffsetX;
    }

    /**
     * @return the gridSpacingOffsetY
     */
    public int getGridSpacingOffsetY()
    {
        return gridSpacingOffsetY;
    }

}
