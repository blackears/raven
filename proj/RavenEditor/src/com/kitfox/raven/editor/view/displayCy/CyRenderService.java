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

package com.kitfox.raven.editor.view.displayCy;

import com.kitfox.raven.editor.node.scene.RavenNodeCamera;
import com.kitfox.raven.editor.node.scene.RavenNodeCompositionLibrary;
import com.kitfox.raven.editor.node.scene.RenderContext;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public interface CyRenderService
{
    /**
     * Render environment from editor's point of view
     * 
     * @param ctx Context to render to
     */
    public void renderEditor(RenderContext ctx);

    public RavenNodeCompositionLibrary getCompositionLibrary();
    public ArrayList<RavenNodeCamera> getCameras();
    
//    /**
//     * Render scene with all enabled cameras
//     * 
//     * @param ctx Context to render to
//     */
//    public void renderCamerasAll(RenderContext ctx);
//    
//    /**
//     * @return Number of cameras available
//     */
//    public int getNumCameras();
}
