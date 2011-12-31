/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.kitfox.raven.editor.view.symbol;

import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.view.ViewProvider;
import com.kitfox.raven.util.service.ServiceInst;
import java.awt.Component;

/**
 *
 * @author kitfox
 */
@ServiceInst(service=ViewProvider.class)
public class SymbolViewProvider extends ViewProvider
{
    public SymbolViewProvider()
    {
        super("Symbols", "/icons/view/symbols.png");
    }

    @Override
    public Component createComponent(RavenEditor editor)
    {
        return new SymbolPanel(editor);
    }    
}
