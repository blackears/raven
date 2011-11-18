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

import com.kitfox.raven.editor.action.ActionProvider;
import com.kitfox.raven.util.service.ServiceInst;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.UIManager;

/**
 * Extracted from the JDK TransferHandler
 *
 * @author kitfox
 */
public class ActionEditPaste extends ActionTransfer
{
    private static final String name = "Paste";
    private static final String id = "editPaste";
    private static final KeyStroke stroke = KeyStroke.getKeyStroke(
            KeyEvent.VK_V,
            ActionEvent.CTRL_MASK);

    public ActionEditPaste()
    {
        super(name);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
	    Object src = e.getSource();
	    if (src instanceof JComponent)
        {
            JComponent c = (JComponent) src;
            TransferHandler th = c.getTransferHandler();
            Clipboard clipboard = getClipboard(c);

            Transferable trans = null;

            // any of these calls may throw IllegalStateException
            try {
                if ((clipboard != null) && (th != null) && (name != null))
                {
                    trans = clipboard.getContents(null);
                }
            } catch (IllegalStateException ise) {
                // clipboard was unavailable
                UIManager.getLookAndFeel().provideErrorFeedback(c);
                return;
            }

            // this is a paste action, import data into the component
            if (trans != null) {
                th.importData(new TransferSupport(c, trans));
            }
	    }
    }

    //---------------------------------
    @ServiceInst(service=ActionProvider.class)
    public static class Provider extends ActionProvider
    {
        public Provider()
        {
            super(id, name, stroke, new ActionEditPaste());
        }
    }
}
