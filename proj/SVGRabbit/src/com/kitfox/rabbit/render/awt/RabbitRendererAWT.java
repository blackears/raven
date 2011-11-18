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

package com.kitfox.rabbit.render.awt;

import com.kitfox.rabbit.render.RabbitFrame;
import com.kitfox.rabbit.render.RabbitRenderer;
import com.kitfox.rabbit.render.RabbitUniverse;
import com.kitfox.rabbit.render.Surface2D;
import com.kitfox.rabbit.types.ImageRef;
import java.awt.Shape;

/**
 *
 * @author kitfox
 */
public class RabbitRendererAWT extends RabbitRenderer
{
    public RabbitRendererAWT(RabbitUniverse universe, Surface2DAwt surf)
    {
        super(universe, surf);
        mulTransform(surf.getGraphics().getTransform());
    }

    @Override
    public void render(Shape shape)
    {
        RabbitFrame frame = getCurFrame();

        frame.getSurface().render(this, shape);
    }

    @Override
    public void drawImage(ImageRef image, float x, float y, float width, float height)
    {
        RabbitFrame frame = getCurFrame();

        frame.getSurface().render(this, image, x, y, width, height);
    }

    @Override
    public void drawSurface(Surface2D surface)
    {
        RabbitFrame frame = getCurFrame();
        frame.getSurface().drawSurface(this, surface);
    }
    
}
