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

package com.kitfox.raven.editor.menu.common;

import com.kitfox.raven.editor.RavenDocument;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.menu.MenuListProvider;
import com.kitfox.raven.editor.node.exporter.ExporterProvider;
import com.kitfox.raven.editor.node.exporter.ExporterProviderIndex;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.wizard.RavenWizardDialog;
import com.kitfox.raven.wizard.RavenWizardPageIterator;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenu;

/**
 *
 * @author kitfox
 */
@ServiceInst(service=MenuListProvider.class)
public class MenuListFileExport extends MenuListProvider
{

    @Override
    public void buildMenu(JMenu parent)
    {
//        ArrayList<ImportAction> actions = new ArrayList<ImportAction>();
        for (ExporterProvider prov: ExporterProviderIndex.inst().getProviders())
        {
            parent.add(new ExportAction(prov));
        }

//        ServiceLoader<ExporterProvider> loader = ServiceLoader.load(ExporterProvider.class);
//        for (Iterator<ExporterProvider> it = loader.iterator(); it.hasNext();)
//        {
//            ExporterProvider provider = it.next();
////            actions.add(new ImportAction(provider));
//            parent.add(new ExportAction(provider));
//        }

//        frame.setImportActions(actions);
    }

    //------------------------------
    class ExportAction extends AbstractAction
    {
        final ExporterProvider prov;

        public ExportAction(ExporterProvider prov)
        {
            super(prov.getName());
            this.prov = prov;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            RavenEditor editor = RavenEditor.inst();

            RavenDocument doc = editor.getDocument();
            if (doc == null)
            {
                return;
            }

            RavenWizardPageIterator wiz = prov.createWizard(doc.getCurDocument());
            if (wiz.isEmpty())
            {
                wiz.finish();
            }
            else
            {
                RavenWizardDialog dlg = new RavenWizardDialog(doc.getCurDocument().getEnv().getSwingRoot(), wiz);
                dlg.setVisible(true);
            }
        }

    }
}
