/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
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
