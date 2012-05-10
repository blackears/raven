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

package com.kitfox.raven.editor.node.tools.common;

import com.kitfox.raven.editor.node.scene.RavenNodePaintLibrary;
import com.kitfox.raven.editor.node.scene.RavenSymbolRoot;
import com.kitfox.raven.editor.node.scene.RavenNodeStrokeLibrary;
import com.kitfox.raven.editor.node.tools.ToolService;
import com.kitfox.raven.paint.RavenPaint;
import com.kitfox.raven.paint.RavenStroke;
import com.kitfox.raven.util.tree.PropertyWrapper;

/**
 *
 * @author kitfox
 */
public interface ServiceColors2D extends ToolService
{

    public PropertyWrapper<RavenSymbolRoot, RavenStroke> getStrokeStyleProp();

    public PropertyWrapper<RavenSymbolRoot, RavenPaint> getStrokePaintProp();

    public PropertyWrapper<RavenSymbolRoot, RavenPaint> getFillPaintProp();

    public RavenNodePaintLibrary getPaintLibrary();

    public RavenNodeStrokeLibrary getStrokeLibrary();

}
