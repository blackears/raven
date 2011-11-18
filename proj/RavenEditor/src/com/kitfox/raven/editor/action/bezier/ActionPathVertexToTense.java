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

package com.kitfox.raven.editor.action.bezier;

import com.kitfox.raven.editor.action.ActionProvider;
import com.kitfox.raven.shape.bezier.VertexSmooth;
import com.kitfox.raven.util.service.ServiceInst;
import javax.swing.KeyStroke;

/**
 *
 * @author kitfox
 */
public class ActionPathVertexToTense extends ActionPathVertexSmoothingChange
{
    private static final String name = "Vertex To Tense";
    private static final String id = "pathVertexToTense";
    private static final KeyStroke stroke = null;

    public ActionPathVertexToTense()
    {
        super(name, VertexSmooth.TENSE);
    }

    //-------------------------------------------

    @ServiceInst(service=ActionProvider.class)
    public static class Provider extends ActionProvider
    {
        public Provider()
        {
            super(id, name, stroke, new ActionPathVertexToTense());
        }
    }
}
