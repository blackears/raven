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

package com.kitfox.coyote.renderer;

/**
 * This is a basic unit of rendering used to draw Coyote scenes.
 * 
 * <p>CyDrawRecords form an important part of the Coyote rendering
 * process.  In the first phase of this process, CyDrawRecords are 
 * created and configured.  They represent the different objects
 * that can be rendered and OpenGL environment configurations that can
 * be applied.  These records are collected and queued
 * (and possibly preprocessed).  The second phase takes place
 * when the physical drawing surface starts its draw process.  At
 * this time, the queue of CyDrawRecords is walked and the
 * render() methods called.</p>
 * 
 * <p>Most implementing classes will provide a set a properties
 * that can be tweaked to adjust the effect.</p>
 * 
 * <p>You may wish to
 * implement memory pooling to avoid reallocating hundreds of these
 * for every frame of animation.</p>
 *
 * @author kitfox
 */
abstract public class CyDrawRecord
{
    /**
     * Draw this record to the physical rendering surface.
     * 
     * @param ctx Keeps track of allocated resources
     * @param gl An OpenGL wrapper
     * @param prevRecord 
     */
    abstract public void render(CyGLContext ctx, CyGLWrapper gl, CyDrawRecord prevRecord);
    
    /**
     * Called after rendering the frame completes to indicate that 
     * resources can be freed.
     */
    abstract public void dispose();
}
