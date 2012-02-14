/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.raven.editor.node.scene.snap;

import com.kitfox.cache.CacheMap;
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import com.kitfox.coyote.math.CyColor4f;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class GraphLayout
{
    //Radius for picking/snapping to points in screen pixels
    public static final String PROP_RADIUS_PICK = "radiusPick";
    private final int radiusPick;
    public static final String PROP_RADIUS_DISPLAY = "radiusDisplay";
    private final int radiusDisplay;
    
    public static final String PROP_EDGE_COLOR = "edgeColor";
    private CyColor4f edgeColor;
    public static final String PROP_EDGE_COLOR_HI = "edgeColorHi";
    private CyColor4f edgeColorHilight;
    public static final String PROP_VERTEX_COLOR = "vertexColor";
    private CyColor4f vertexColor;
    public static final String PROP_VERTEX_COLOR_SEL = "vertexColorSel";
    private CyColor4f vertexColorSelected;

    public GraphLayout(int pointRadiusPick, int pointRadiusDisplay, CyColor4f edgeColor, CyColor4f edgeColorHilight, CyColor4f vertexColor, CyColor4f vertexColorSelected)
    {
        this.radiusPick = pointRadiusPick;
        this.radiusDisplay = pointRadiusDisplay;
        this.edgeColor = edgeColor;
        this.edgeColorHilight = edgeColorHilight;
        this.vertexColor = vertexColor;
        this.vertexColorSelected = vertexColorSelected;
    }

    public GraphLayout(int pointRadiusPick, int pointRadiusDisplay)
    {
        this(pointRadiusPick, pointRadiusDisplay,
                CyColor4f.BLUE, CyColor4f.RED, CyColor4f.GREY, CyColor4f.YELLOW);
    }

    public GraphLayout()
    {
        this(4, 3);
    }

    public static GraphLayout create(String text)
    {
        try
        {
            CacheMap map = (CacheMap)CacheParser.parse(text);
            
            int radPick = map.getInteger(PROP_RADIUS_PICK, 6);
            int radDisplay = map.getInteger(PROP_RADIUS_DISPLAY, 4);
            
            CyColor4f edgeCol = map.getCyColor4f(PROP_EDGE_COLOR, CyColor4f.BLUE);
            CyColor4f edgeColHi = map.getCyColor4f(PROP_EDGE_COLOR_HI, CyColor4f.RED);
            CyColor4f vertCol = map.getCyColor4f(PROP_VERTEX_COLOR, CyColor4f.GREY);
            CyColor4f vertColSel = map.getCyColor4f(PROP_VERTEX_COLOR_SEL, CyColor4f.YELLOW);
            
            return new GraphLayout(radPick, radDisplay, edgeCol, edgeColHi, vertCol, vertColSel);
        } catch (ParseException ex)
        {
            Logger.getLogger(GraphLayout.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    public CacheMap toCache()
    {
        CacheMap map = new CacheMap();

        map.put(PROP_RADIUS_PICK, radiusPick);
        map.put(PROP_RADIUS_DISPLAY, radiusDisplay);
        
        map.put(PROP_EDGE_COLOR, edgeColor);
        map.put(PROP_EDGE_COLOR_HI, edgeColorHilight);
        map.put(PROP_VERTEX_COLOR, vertexColor);
        map.put(PROP_VERTEX_COLOR_SEL, vertexColorSelected);
        
        return map;
    }

    @Override
    public String toString()
    {
        return toCache().toString();
    }

    /**
     * @return the pointRadiusPick
     */
    public int getPointRadiusPick()
    {
        return radiusPick;
    }

    /**
     * @return the pointRadiusPick
     */
    public int getPointRadiusPickSq()
    {
        return radiusPick * radiusPick;
    }

    /**
     * @return the pointRadiusDisplay
     */
    public int getPointRadiusDisplay()
    {
        return radiusDisplay;
    }

    /**
     * @return the edgeColor
     */
    public CyColor4f getEdgeColor()
    {
        return edgeColor;
    }

    /**
     * @return the edgeColorHilight
     */
    public CyColor4f getEdgeColorHilight()
    {
        return edgeColorHilight;
    }

    /**
     * @return the vertexColor
     */
    public CyColor4f getVertexColor()
    {
        return vertexColor;
    }

    /**
     * @return the vertexColorSelected
     */
    public CyColor4f getVertexColorSelected()
    {
        return vertexColorSelected;
    }
}
