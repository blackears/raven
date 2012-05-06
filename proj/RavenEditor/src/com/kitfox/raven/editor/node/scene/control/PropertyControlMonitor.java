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

package com.kitfox.raven.editor.node.scene.control;

import com.kitfox.raven.editor.RavenDocument;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.node.scene.RavenNodeRoot;
import com.kitfox.raven.editor.view.properties.PropertyCustomEditorPanel;
import com.kitfox.raven.paint.common.RavenPaintColor;
import com.kitfox.raven.paint.common.RavenPaintColorEditor;
import com.kitfox.raven.util.tree.PropertyCustomEditor;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperEditor;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/**
 *
 * @author kitfox
 */
public class PropertyControlMonitor extends MouseAdapter
{
    final RavenEditor editor;
    Component component;
    private PropertyWrapper<RavenNodeRoot, RavenPaintColor> wrapper;

    public PropertyControlMonitor(RavenEditor editor, Component component)
    {
        this.editor = editor;
        this.component = component;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        e.consume();
        
        if (wrapper == null)
        {
            return;
        }
        
        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return;
        }
        
        RavenNodeRoot root = (RavenNodeRoot)doc.getCurSymbol();
        if (root == null)
        {
            return;
        }

        RavenPaintColorEditor editor = new RavenPaintColorEditor(
                wrapper);
        runCustomEditor(editor);
    }

    private void runCustomEditor(PropertyWrapperEditor editor)
    {
        JDialog dlg =
                new JDialog(SwingUtilities.getWindowAncestor(component), 
                JDialog.DEFAULT_MODALITY_TYPE);
        PropertyCustomEditor xact = editor.createCustomEditor();
        PropertyCustomEditorPanel custom = new PropertyCustomEditorPanel(
                xact.getCustomEditor(), dlg);
        dlg.getContentPane().add(custom, BorderLayout.CENTER);

        dlg.pack();
        Window win = SwingUtilities.getWindowAncestor(component);
        if (win != null)
        {
            dlg.setLocation(win.getX(), win.getY());
        }

        dlg.setVisible(true);

        if (custom.isOkay())
        {
            xact.customEditorCommit();
        }
        else
        {
            xact.customEditorCancel();
        }
    }

    /**
     * @return the wrapper
     */
    public PropertyWrapper<RavenNodeRoot, RavenPaintColor> getWrapper()
    {
        return wrapper;
    }

    /**
     * @param wrapper the wrapper to set
     */
    public void setWrapper(PropertyWrapper<RavenNodeRoot, RavenPaintColor> wrapper)
    {
        this.wrapper = wrapper;
    }
    
}
