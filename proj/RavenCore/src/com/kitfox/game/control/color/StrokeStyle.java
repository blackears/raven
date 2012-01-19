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

import java.awt.BasicStroke;

/**
 *
 * @author kitfox
 */
@Deprecated
public class StrokeStyle
{

    public static enum Cap { BUTT, ROUND, SQUARE }
    public static enum Join { BEVEL, MITER, ROUND }
    private final float width;
    private final Cap cap;
    private final Join join;
    private final float miterlimit;
    private final float[] dash;
    private final float dashPhase;

    public StrokeStyle()
    {
        this(1, Cap.SQUARE, Join.MITER, 10, null, 0);
    }

    public StrokeStyle(float width, Cap cap, Join join, float miterlimit, float[] dash, float dashPhase)
    {
        this.width = width;
        this.cap = cap;
        this.join = join;
        this.miterlimit = miterlimit;
        this.dash = dash;
        this.dashPhase = dashPhase;
    }

    /**
     * @return the width
     */
    public float getWidth() {
        return width;
    }

    /**
     * @return the cap
     */
    public Cap getCap() {
        return cap;
    }

    /**
     * @return the join
     */
    public Join getJoin() {
        return join;
    }

    /**
     * @return the miterlimit
     */
    public float getMiterLimit() {
        return miterlimit;
    }

    /**
     * @return the dash
     */
    public float[] getDash() {
        return dash == null ? null : dash.clone();
    }

    /**
     * @return the dash_phase
     */
    public float getDashPhase() {
        return dashPhase;
    }

    public BasicStroke get()
    {
        int capVal;
        int joinVal;

        switch (cap)
        {
            case BUTT:
                capVal = BasicStroke.CAP_BUTT;
                break;
            case ROUND:
                capVal = BasicStroke.CAP_ROUND;
                break;
            case SQUARE:
                capVal = BasicStroke.CAP_SQUARE;
                break;
            default:
                throw new RuntimeException();
        }

        switch (join)
        {
            case BEVEL:
                joinVal = BasicStroke.JOIN_BEVEL;
                break;
            case MITER:
                joinVal = BasicStroke.JOIN_MITER;
                break;
            case ROUND:
                joinVal = BasicStroke.JOIN_ROUND;
                break;
            default:
                throw new RuntimeException();
        }

        return new BasicStroke(width, capVal, joinVal, miterlimit, getDash(), dashPhase);
    }
}
