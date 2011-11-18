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

package com.kitfox.rabbit.render;

import com.kitfox.rabbit.types.ImageRef;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author kitfox
 */
abstract public class Surface2D
{
    abstract public Rectangle2D getBounds();
    abstract public void dispose();

    abstract public void render(RabbitRenderer renderer, Shape shape);

    abstract public void render(RabbitRenderer renderer, ImageRef image, float x, float y, float width, float height);

    abstract public Surface2D createBlankSurface(Rectangle2D region);

    abstract public void drawSurface(RabbitRenderer renderer, Surface2D surface);
}
