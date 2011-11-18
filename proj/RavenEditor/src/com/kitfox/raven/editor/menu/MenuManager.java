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

import com.kitfox.raven.editor.action.bezier.ActionPathVertexToCusp;
import com.kitfox.raven.editor.action.bezier.ActionPathVertexToSmooth;
import com.kitfox.raven.editor.action.bezier.ActionPathVertexToTense;
import com.kitfox.raven.editor.action.common.ActionEditCopy;
import com.kitfox.raven.editor.action.common.ActionEditCut;
import com.kitfox.raven.editor.action.common.ActionEditDelete;
import com.kitfox.raven.editor.action.common.ActionEditPaste;
import com.kitfox.raven.editor.action.common.ActionEditRedo;
import com.kitfox.raven.editor.action.common.ActionEditUndo;
import com.kitfox.raven.editor.action.common.ActionFileExit;
import com.kitfox.raven.editor.action.common.ActionFileNew;
import com.kitfox.raven.editor.action.common.ActionFileOpen;
import com.kitfox.raven.editor.action.common.ActionFileSave;
import com.kitfox.raven.editor.action.common.ActionFileSaveAs;
import com.kitfox.raven.editor.action.common.ActionHelpAbout;
import com.kitfox.raven.editor.action.common.ActionHelpContents;
import com.kitfox.raven.editor.action.common.ActionHelpWebsite;
import com.kitfox.raven.editor.action.common.ActionLayoutManageLayouts;
import com.kitfox.raven.editor.action.common.ActionLayoutSaveLayout;
import com.kitfox.raven.editor.menu.common.MenuListFileExport;
import com.kitfox.raven.editor.menu.common.MenuListFileImport;
import com.kitfox.raven.editor.menu.common.MenuListFileOpenRecent;
import com.kitfox.raven.editor.menu.common.MenuListLayoutLayouts;
import com.kitfox.raven.editor.menu.common.MenuListViewViews;
import java.util.ArrayList;
import javax.swing.JMenuBar;

/**
 *
 * @author kitfox
 */
public class MenuManager
{
    ArrayList<MenuEntryGroup> children = new ArrayList<MenuEntryGroup>();

    public MenuManager()
    {
        //TODO: This is being used to create a default menu until the
        // tool for configuring it in the user interface is written.
        createDefaultLayout();
    }


    public void buildMenu(JMenuBar parent)
    {
        for (MenuEntryGroup entry: children)
        {
            parent.add(entry.build());
        }
    }

    /**
     * TODO: This is being used to create a default menu until the
     * tool for configuring it in the user interface is written.
     */
    private void createDefaultLayout()
    {
        {
            MenuEntryGroup group = new MenuEntryGroup("File", "f");
            children.add(group);

            group.add(new MenuEntryActionReference(ActionFileNew.Provider.class));
            group.add(new MenuEntryActionReference(ActionFileOpen.Provider.class));
            group.add(new MenuEntryActionReference(ActionFileSave.Provider.class));
            group.add(new MenuEntryActionReference(ActionFileSaveAs.Provider.class));

            group.add(new MenuEntrySeparator());

            //open recent
            {
                MenuEntryGroup group1 = new MenuEntryGroup(
                        "Open Recent");
                group.add(group1);

                group1.add(new MenuEntryListProvider(MenuListFileOpenRecent.class));
            }

            group.add(new MenuEntrySeparator());

            //import
            {
                MenuEntryGroup group1 = new MenuEntryGroup(
                        "Import");
                group.add(group1);

                group1.add(new MenuEntryListProvider(MenuListFileImport.class));
            }

            //export
            {
                MenuEntryGroup group1 = new MenuEntryGroup(
                        "Export");
                group.add(group1);

                group1.add(new MenuEntryListProvider(MenuListFileExport.class));
            }

            group.add(new MenuEntrySeparator());

            //exit
            group.add(new MenuEntryActionReference(ActionFileExit.Provider.class));
        }

        {
            MenuEntryGroup group = new MenuEntryGroup("Edit", "e");
            children.add(group);

            group.add(new MenuEntryActionReference(ActionEditUndo.Provider.class));
            group.add(new MenuEntryActionReference(ActionEditRedo.Provider.class));

            group.add(new MenuEntrySeparator());

            group.add(new MenuEntryActionReference(ActionEditCut.Provider.class));
            group.add(new MenuEntryActionReference(ActionEditCopy.Provider.class));
            group.add(new MenuEntryActionReference(ActionEditPaste.Provider.class));
            group.add(new MenuEntryActionReference(ActionEditDelete.Provider.class));
        }

        {
            MenuEntryGroup group = new MenuEntryGroup("Path", "p");
            children.add(group);

            group.add(new MenuEntryActionReference(ActionPathVertexToCusp.Provider.class));
            group.add(new MenuEntryActionReference(ActionPathVertexToSmooth.Provider.class));
            group.add(new MenuEntryActionReference(ActionPathVertexToTense.Provider.class));
        }

        {
            MenuEntryGroup group = new MenuEntryGroup("Layout", "l");
            children.add(group);

            group.add(new MenuEntryActionReference(ActionLayoutSaveLayout.Provider.class));
            group.add(new MenuEntryActionReference(ActionLayoutManageLayouts.Provider.class));

            {
                MenuEntryGroup group1 = new MenuEntryGroup(
                        "Layouts");
                group.add(group1);

                group1.add(new MenuEntryListProvider(MenuListLayoutLayouts.class));
            }
        }

        {
            MenuEntryGroup group = new MenuEntryGroup("View", "v");
            children.add(group);

            group.add(new MenuEntryListProvider(MenuListViewViews.class));
        }

        {
            MenuEntryGroup group = new MenuEntryGroup("Help", "h");
            children.add(group);

            group.add(new MenuEntryActionReference(ActionHelpContents.Provider.class));
            group.add(new MenuEntryActionReference(ActionHelpAbout.Provider.class));
            group.add(new MenuEntryActionReference(ActionHelpWebsite.Provider.class));
        }
    }
}
