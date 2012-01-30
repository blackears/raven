/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.raven.paint.control;

import java.util.EventListener;

/**
 *
 * @author kitfox
 */
public interface ColorSamplerOverlayListener extends EventListener
{
    public void colorPicked(ColorSamplerOverlayEvent evt);
}
