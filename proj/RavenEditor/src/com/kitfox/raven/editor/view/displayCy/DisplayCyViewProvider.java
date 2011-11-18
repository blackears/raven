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

import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.view.ViewProvider;
import com.kitfox.raven.util.service.ServiceInst;
import java.awt.Component;

/**
 *
 * @author kitfox
 */
@ServiceInst(service=ViewProvider.class)
public class DisplayCyViewProvider extends ViewProvider
{

    public DisplayCyViewProvider()
    {
        super("Display Coyote", "/icons/view/display.png");
    }

    @Override
    public Component createComponent(RavenEditor editor)
    {
        return new DisplayCyPanel(editor);
    }

}
