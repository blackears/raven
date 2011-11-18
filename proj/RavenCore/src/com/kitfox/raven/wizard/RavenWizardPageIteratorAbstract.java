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

package com.kitfox.raven.wizard;

import java.util.ArrayList;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author kitfox
 */
abstract public class RavenWizardPageIteratorAbstract<T>
        implements RavenWizardPageIterator<T>
{
    ArrayList<RavenWizardPageListener> listeners = new ArrayList<RavenWizardPageListener>();

    @Override
    public void addRavenWizardPageListener(RavenWizardPageListener l)
    {
        listeners.add(l);
    }

    @Override
    public void removeRavenWizardPageListener(RavenWizardPageListener l)
    {
        listeners.remove(l);
    }

    protected void fireWizardPageNavigationChanged()
    {
        ChangeEvent evt = new ChangeEvent(this);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).wizardPageNavigationChanged(evt);
        }
    }
}
