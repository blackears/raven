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
public class ActionEditCopy extends ActionTransfer
{
    private static final String name = "Copy";
    private static final String id = "editCopy";
//    private static final String id =
//            (String)TransferHandler.getCopyAction().getValue(Action.NAME);
    private static final KeyStroke stroke = KeyStroke.getKeyStroke(
            KeyEvent.VK_C,
            ActionEvent.CTRL_MASK);

    public ActionEditCopy()
    {
        super(name);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
	    Object src = e.getSource();
	    if (src instanceof JComponent)
        {
            JComponent c = (JComponent)src;
            TransferHandler th = c.getTransferHandler();
            Clipboard clipboard = getClipboard(c);

            // any of these calls may throw IllegalStateException
            try {
                if ((clipboard != null) && (th != null) && (name != null))
                {
                    th.exportToClipboard(c, clipboard, TransferHandler.COPY);
                }
            } catch (IllegalStateException ise) {
                // clipboard was unavailable
                UIManager.getLookAndFeel().provideErrorFeedback(c);
                return;
            }
	    }
    }

    //---------------------------------
    @ServiceInst(service=ActionProvider.class)
    public static class Provider extends ActionProvider
    {
        public Provider()
        {
            super(id, name, stroke, new ActionEditCopy());
        }
    }
}
