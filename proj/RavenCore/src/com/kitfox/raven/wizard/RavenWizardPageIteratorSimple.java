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
abstract public class RavenWizardPageIteratorSimple<T>
        extends RavenWizardPageIteratorAbstract<T>
{
    RavenWizardPage[] pages;
    int index;

    public RavenWizardPageIteratorSimple(RavenWizardPage... pages)
    {
        this.pages = pages;
    }

    @Override
    public boolean isFinishAvailable()
    {
        return index == pages.length;
    }

    public int getNumPages()
    {
        return pages.length;
    }

    public RavenWizardPage getPage(int index)
    {
        return pages[index];
    }

    @Override
    public boolean isEmpty()
    {
        return pages == null || pages.length == 0;
    }

    @Override
    public boolean isPrevPageAvailable()
    {
        return index != 0;
    }

    @Override
    public boolean isNextPageAvailable()
    {
        return index < pages.length;
    }

    @Override
    public boolean isCancelAvailable()
    {
        return true;
    }

    @Override
    public RavenWizardPage prevPage()
    {
        return pages[--index];
    }

    @Override
    public RavenWizardPage nextPage()
    {
        return pages[index++];
    }

    @Override
    public void cancel()
    {
    }

}
