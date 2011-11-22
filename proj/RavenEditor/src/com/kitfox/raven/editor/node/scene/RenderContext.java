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

package com.kitfox.raven.editor.node.scene;

import com.kitfox.raven.util.tree.FrameKey;
import com.kitfox.coyote.renderer.CyDrawStack;

/**
 *
 * @author kitfox
 */
public class RenderContext
{
    //Stack everything is written to
    private final CyDrawStack drawStack;
    //Current frame we are rendering for
    private final FrameKey frame;

    public RenderContext(CyDrawStack drawStack, FrameKey frame)
    {
        this.drawStack = drawStack;
        this.frame = frame;
    }

    /**
     * @return the drawStack
     */
    public CyDrawStack getDrawStack()
    {
        return drawStack;
    }

    /**
     * @return the frame
     */
    public FrameKey getFrame()
    {
        return frame;
    }

    
}
