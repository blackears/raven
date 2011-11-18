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

import java.awt.datatransfer.Clipboard;
import javax.swing.AbstractAction;
import javax.swing.JComponent;

/**
 *
 * @author kitfox
 */
abstract public class ActionTransfer extends AbstractAction
{
//    private static Object SandboxClipboardKey = new Object();

    public ActionTransfer(String name)
    {
        super(name);
    }

    protected Clipboard getClipboard(JComponent c)
    {
        return c.getToolkit().getSystemClipboard();

//	    if (SwingUtilities2.canAccessSystemClipboard())
//        {
//        	return c.getToolkit().getSystemClipboard();
//	    }
//
//	    Clipboard clipboard =
//                (Clipboard)sun.awt.AppContext.getAppContext().get(SandboxClipboardKey);
//	    if (clipboard == null)
//        {
//            clipboard = new Clipboard("Sandboxed Component Clipboard");
//            sun.awt.AppContext.getAppContext().put(SandboxClipboardKey,
//                   clipboard);
//	    }
//	    return clipboard;
	}

}
