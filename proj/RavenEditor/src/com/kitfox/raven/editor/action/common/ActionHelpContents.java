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

import com.kitfox.raven.editor.RavenFrame;
import com.kitfox.raven.editor.action.ActionProvider;
import com.kitfox.raven.util.service.ServiceInst;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

/**
 *
 * @author kitfox
 */
public class ActionHelpContents extends AbstractAction
{
    private static final String name = "Contents...";
    private static final String id = "helpContents";
    private static final KeyStroke stroke = null;

    public ActionHelpContents()
    {
        super(name);
    }

    private void showWebpage(URI uri)
    {
        if (Desktop.isDesktopSupported())
        {
            Desktop desktop = Desktop.getDesktop();
            try
            {
                desktop.browse(uri);
            } catch (IOException ex)
            {
                Logger.getLogger(RavenFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        try
        {
            showWebpage(new URI("http://www.kitfox.com"));
        } catch (URISyntaxException ex)
        {
            Logger.getLogger(RavenFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @ServiceInst(service=ActionProvider.class)
    public static class Provider extends ActionProvider
    {
        public Provider()
        {
            super(id, name, stroke, new ActionHelpContents());
        }
    }
}
