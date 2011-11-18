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

import java.util.ArrayList;
import javax.swing.JMenu;

/**
 *
 * @author kitfox
 */
public class MenuEntryGroup extends MenuEntry
{
    final String name;
    final String mnemonic;
    ArrayList<MenuEntry> children = new ArrayList<MenuEntry>();

    public MenuEntryGroup(String name)
    {
        this(name, null);
    }

    public MenuEntryGroup(String name, String mnemonic)
    {
        this.name = name;
        this.mnemonic = mnemonic;
    }

    public void add(MenuEntry child)
    {
        children.add(child);
    }

    public JMenu build()
    {
        JMenu group = new JMenu(name);

        for (MenuEntry entry: children)
        {
            entry.buildMenu(group);
        }

        return group;
    }

    @Override
    public void buildMenu(JMenu parent)
    {
        JMenu group = new JMenu(name);
        parent.add(group);

        for (MenuEntry entry: children)
        {
            entry.buildMenu(group);
        }
    }
}
