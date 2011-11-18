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

/**
 *
 * @author kitfox
 */
public interface RavenWizardPageIterator<T>
{
    /**
     * If true, thre are no pages in this wizard and finish() can be
     * called immediately.
     *
     * @return
     */
    public boolean isEmpty();

    public boolean isPrevPageAvailable();
    public boolean isNextPageAvailable();
    public boolean isFinishAvailable();
    public boolean isCancelAvailable();

    public RavenWizardPage prevPage();
    public RavenWizardPage nextPage();
    public T finish();
    public void cancel();

    public void addRavenWizardPageListener(RavenWizardPageListener l);
    public void removeRavenWizardPageListener(RavenWizardPageListener l);
}
