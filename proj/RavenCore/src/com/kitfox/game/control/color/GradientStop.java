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

package com.kitfox.game.control.color;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author kitfox
 */
@Deprecated
public class GradientStop implements Comparable<GradientStop>
{
    protected ColorStyle color;
    public static final String PROP_COLOR = "color";
    protected float offset;
    public static final String PROP_OFFSET = "offset";
    
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);


    public GradientStop()
    {
    }

    public GradientStop(ColorStyle color, float offset)
    {
        this.color = color;
        this.offset = offset;
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Get the value of offset
     *
     * @return the value of offset
     */
    public float getOffset() {
        return offset;
    }

    /**
     * Set the value of offset
     *
     * @param offset new value of offset
     */
    public void setOffset(float offset) {
        float oldOffset = this.offset;
        this.offset = offset;
        propertyChangeSupport.firePropertyChange(PROP_OFFSET, oldOffset, offset);
    }

    /**
     * Get the value of color
     *
     * @return the value of color
     */
    public ColorStyle getColor() {
        return color;
    }

    /**
     * Set the value of color
     *
     * @param color new value of color
     */
    public void setColor(ColorStyle color) {
        ColorStyle oldColor = this.color;
        this.color = color;
        propertyChangeSupport.firePropertyChange(PROP_COLOR, oldColor, color);
    }

    @Override
    public int compareTo(GradientStop obj)
    {
        return Float.compare(offset, obj.offset);
    }
}
