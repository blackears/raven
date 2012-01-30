/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.raven.paint.control;

import java.awt.Color;
import java.util.EventObject;

/**
 *
 * @author kitfox
 */
public class ColorSamplerOverlayEvent extends EventObject
{
    private final Color color;

    public ColorSamplerOverlayEvent(Object source, Color color)
    {
        super(source);
        this.color = color;
    }

    /**
     * @return the color
     */
    public Color getColor()
    {
        return color;
    }
    
}
