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

package com.kitfox.raven.movie.exporter;

import com.kitfox.raven.editor.node.exporter.ExporterProvider;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.wizard.RavenWizardPageIterator;
import java.util.Properties;

/**
 *
 * @author kitfox
 */
@ServiceInst(service=ExporterProvider.class)
public class MovieExporter extends ExporterProvider
{
    Properties preferences = new Properties();

    public MovieExporter()
    {
        super("Movie Exporter");
    }

    @Override
    public RavenWizardPageIterator createWizard(NodeDocument doc)
    {
        return MovieExporterWizard.create(doc, preferences);
    }

    @Override
    public void loadPreferences(Properties properties)
    {
        this.preferences = properties;
    }

    @Override
    public Properties savePreferences()
    {
        return preferences;
    }
    
}
