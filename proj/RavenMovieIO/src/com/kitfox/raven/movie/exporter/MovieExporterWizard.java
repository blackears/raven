/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.kitfox.raven.movie.exporter;

import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.wizard.RavenWizardPageIteratorSimple;
import java.util.Properties;

/**
 *
 * @author kitfox
 */
public class MovieExporterWizard extends RavenWizardPageIteratorSimple
{
    MovieExporterContext ctx;
    ExportMoviePanel panel;

    private MovieExporterWizard(MovieExporterContext ctx, ExportMoviePanel panel)
    {
        super(panel);

        this.ctx = ctx;
        this.panel = panel;
    }

    public static MovieExporterWizard create(NodeDocument doc, Properties preferences)
    {
        MovieExporterContext ctx = new MovieExporterContext(doc, preferences);
        
        ExportMoviePanel panel = new ExportMoviePanel(ctx);
        return new MovieExporterWizard(ctx, panel);
    }
    
    @Override
    public Object finish()
    {
        ctx.savePreferences();
        ctx.doExport();
        return null;
    }
    
}
