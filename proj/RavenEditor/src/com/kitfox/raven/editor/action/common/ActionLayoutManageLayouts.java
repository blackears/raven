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

import com.kitfox.raven.editor.LayoutManagerPanel;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.util.RavenSwingUtil;
import com.kitfox.raven.editor.RavenViewManager;
import com.kitfox.raven.editor.action.ActionProvider;
import com.kitfox.raven.util.service.ServiceInst;
import java.awt.Dialog.ModalityType;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

/**
 *
 * @author kitfox
 */
public class ActionLayoutManageLayouts extends AbstractAction
{
    private static final String name = "Manage Layouts...";
    private static final String id = "layoutManageLayouts";
    private static final KeyStroke stroke = null;

    public ActionLayoutManageLayouts()
    {
        super(name);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        RavenViewManager viewManager = RavenEditor.inst().getViewManager();
//        String name = JOptionPane.showInputDialog(viewManager.getSwingRoot(),
//                "Save Layout As");
        Window win = viewManager.getSwingRoot();

        JDialog dlg = new JDialog(win, "Manage Layouts", JDialog.DEFAULT_MODALITY_TYPE);
        dlg.getContentPane().add(new LayoutManagerPanel(viewManager));
        dlg.pack();

        RavenSwingUtil.centerWindow(dlg, win.getBounds());
//        dlg.setLocation(getX() + (getWidth() - dlg.getWidth()) / 2,
//                getY() + (getHeight() - dlg.getHeight()) / 2);

        dlg.setVisible(true);
    }

    @ServiceInst(service=ActionProvider.class)
    public static class Provider extends ActionProvider
    {
        public Provider()
        {
            super(id, name, stroke, new ActionLayoutManageLayouts());
        }
    }
}
