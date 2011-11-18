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

import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.util.tree.NodeDocumentProvider;
import com.kitfox.raven.wizard.RavenWizardPage;
import com.kitfox.raven.wizard.RavenWizardPageIterator;
import com.kitfox.raven.wizard.RavenWizardPageIteratorAbstract;
import com.kitfox.raven.wizard.RavenWizardPageListener;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author kitfox
 */
public class NewDocumentWizard extends RavenWizardPageIteratorAbstract<NodeDocument>
        implements RavenWizardPageListener
{
    final RavenEditor editor;
    NodeDocumentProvider prov;
    int pageIndex;
    RavenWizardPageIterator<NodeDocument> subIt;

    NewDocumentWizardPanel wizPanel = new NewDocumentWizardPanel(this);

    public NewDocumentWizard(RavenEditor editor)
    {
        this.editor = editor;
    }

    @Override
    public RavenWizardPage prevPage()
    {
        switch (pageIndex)
        {
            default:
            case 0:
            {
                return null;
            }
            case 1:
            {
                if (subIt.isPrevPageAvailable())
                {
                    return subIt.prevPage();
                }
                else
                {
                    pageIndex = 0;
                    fireWizardPageNavigationChanged();
                    return wizPanel;
                }
            }
        }
    }

    @Override
    public RavenWizardPage nextPage()
    {
        switch (pageIndex)
        {
            default:
            case 0:
            {
                pageIndex = 1;
                fireWizardPageNavigationChanged();
                return wizPanel;
            }
            case 1:
            {
                fireWizardPageNavigationChanged();
                return subIt.nextPage();
            }
        }
    }

    @Override
    public NodeDocument finish()
    {
        return subIt.finish();
    }

    @Override
    public void cancel()
    {
        if (pageIndex != 0)
        {
            subIt.cancel();
        }
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public boolean isPrevPageAvailable()
    {
        return pageIndex != 0;
    }

    @Override
    public boolean isNextPageAvailable()
    {
        return subIt == null ? false : subIt.isNextPageAvailable();
    }

    @Override
    public boolean isFinishAvailable()
    {
        return subIt == null ? false : subIt.isFinishAvailable();
    }

    @Override
    public boolean isCancelAvailable()
    {
        return (pageIndex == 0 || subIt == null) ? true : subIt.isCancelAvailable();
    }

    void setProvider(NodeDocumentProvider prov)
    {
        this.prov = prov;
        
        if (prov == null)
        {
            subIt = null;
        }
        else
        {
            subIt = prov.createDocumentWizard();
            subIt.addRavenWizardPageListener(this);
        }

        fireWizardPageNavigationChanged();
    }

    @Override
    public void wizardPageNavigationChanged(ChangeEvent evt)
    {
        fireWizardPageNavigationChanged();
    }

}
