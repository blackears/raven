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

package com.kitfox.raven.editor.menu;

import com.kitfox.raven.editor.action.ActionProvider;
import com.kitfox.raven.editor.action.ActionProviderIndex;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 * @author kitfox
 */
public class MenuEntryActionReference extends MenuEntry
{
    final Class<? extends ActionProvider> provClass;

    public MenuEntryActionReference(Class<? extends ActionProvider> provClass)
    {
        this.provClass = provClass;
    }

    @Override
    public void buildMenu(JMenu parent)
    {
        ActionProvider ap = ActionProviderIndex.inst().getProvider(provClass);
        JMenuItem item = new JMenuItem(ap.getAction());
//        ap.getDefaultKeyStroke();
        parent.add(item);
    }

    
}
