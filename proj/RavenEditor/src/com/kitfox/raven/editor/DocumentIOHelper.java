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

package com.kitfox.raven.editor;

import com.kitfox.raven.util.FileFilterSuffix;
import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.wizard.RavenWizardDialog;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author kitfox
 */
public class DocumentIOHelper
{
//    private RavenFileChooser fileChooser = new RavenFileChooser();
    private JFileChooser fileChooser = new JFileChooser();
    private final RavenViewManager viewManager;

    public DocumentIOHelper(RavenViewManager viewManager)
    {
        this.viewManager = viewManager;

        fileChooser.addChoosableFileFilter(new FileFilterSuffix(
                "Raven File", RavenDocument.RAVEN_FILE_SUFFIX));
        fileChooser.addChoosableFileFilter(new FileFilterSuffix(
                "XML File", "xml"));
    }

    public void newFile()
    {
        NewDocumentWizard wiz = new NewDocumentWizard(viewManager.getEditor());

        RavenWizardDialog dlg = new RavenWizardDialog(viewManager.getSwingRoot(), wiz);
        RavenSwingUtil.centerWindow(dlg);
        dlg.setVisible(true);

        NodeDocument root = dlg.getNodeDocument();

        if (root == null)
        {
            return;
        }

        RavenEditor editor = viewManager.getEditor();
        RavenDocument doc = new RavenDocument(editor, root);

        editor.setDocument(doc);

    }

    public void openFile()
    {
        int val = fileChooser.showOpenDialog(viewManager.getSwingRoot());
        if (val != JFileChooser.APPROVE_OPTION)
        {
            return;
        }

        File file = fileChooser.getSelectedFile();
        viewManager.getEditor().open(file);
    }

    public void saveFile()
    {
        RavenDocument proj = viewManager.getEditor().getDocument();
        if (proj == null)
        {
            JOptionPane.showMessageDialog(viewManager.getSwingRoot(),
                    "No project is loaded",
                    "Save Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File source = proj.getSource();
        if (source == null)
        {
            saveAsFile();
            return;
        }

        proj.save(source);
        viewManager.getEditor().setMostRecentFile(source);
    }

    public void saveAsFile()
    {
        RavenDocument proj = viewManager.getEditor().getDocument();
        if (proj == null)
        {
            JOptionPane.showMessageDialog(viewManager.getSwingRoot(),
                    "No project is loaded",
                    "Save Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int val = fileChooser.showSaveDialog(viewManager.getSwingRoot());
        if (val != JFileChooser.APPROVE_OPTION)
        {
            return;
        }

        File file = fileChooser.getSelectedFile();
        if (file.getName().indexOf('.') == -1)
        {
            try {
                file = new File(file.getCanonicalPath() + "." + RavenDocument.RAVEN_FILE_SUFFIX);
            } catch (IOException ex) {
                Logger.getLogger(RavenFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        proj.setSource(file);
        proj.save(file);
        viewManager.getEditor().setMostRecentFile(file);
    }

}
