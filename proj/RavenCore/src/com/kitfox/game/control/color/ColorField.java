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

import java.awt.geom.Point2D;

/**
 *
 * @author kitfox
 */
@Deprecated
public interface ColorField
{
    /**
     * Return rgba color for given coordinate in component
     * @param x coord on [0 1]
     * @param y coord on [0 1]
     * @return
     */
    public ColorStyle toColor(float x, float y);
    public ColorStyle toDisplayColor(float x, float y);
    public Point2D.Float toCoords(ColorStyle color);

    public void addColorFieldListener(ColorFieldListener listener);
    public void removeColorFieldListener(ColorFieldListener listener);

}
