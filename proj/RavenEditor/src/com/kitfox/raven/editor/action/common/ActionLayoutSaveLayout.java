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

package com.kitfox.raven.editor.action.common;

import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.RavenViewManager;
import com.kitfox.raven.editor.action.ActionProvider;
import com.kitfox.raven.util.service.ServiceInst;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

/**
 *
 * @author kitfox
 */
public class ActionLayoutSaveLayout extends AbstractAction
{
    private static final String name = "Save Layout...";
    private static final String id = "layoutSaveLayout";
    private static final KeyStroke stroke = null;

    public ActionLayoutSaveLayout()
    {
        super(name);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        RavenViewManager viewManager = RavenEditor.inst().getViewManager();
        String name = JOptionPane.showInputDialog(viewManager.getSwingRoot(),
                "Save Layout As");

        if (name == null)
        {
            return;
        }

        viewManager.saveLayout(name);
    }

    @ServiceInst(service=ActionProvider.class)
    public static class Provider extends ActionProvider
    {
        public Provider()
        {
            super(id, name, stroke, new ActionLayoutSaveLayout());
        }
    }
}
