/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.raven.swf.importer;

import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.wizard.RavenWizardPageIteratorSimple;
import java.util.Properties;

/**
 *
 * @author kitfox
 */
public class SWFImporterWizard extends RavenWizardPageIteratorSimple
{
    SWFImporterContext ctx;
    SWFImporterPanel panel;

    private SWFImporterWizard(SWFImporterContext ctx, SWFImporterPanel panel)
    {
        super(panel);

        this.ctx = ctx;
        this.panel = panel;
    }

    public static SWFImporterWizard create(NodeDocument doc, Properties preferences)
    {
        SWFImporterContext ctx = new SWFImporterContext(doc, preferences);
        
        SWFImporterPanel panel = new SWFImporterPanel(ctx);
        return new SWFImporterWizard(ctx, panel);
    }
    
    @Override
    public Object finish()
    {
        ctx.savePreferences();
        ctx.doImport();
        return null;
    }
    
}
