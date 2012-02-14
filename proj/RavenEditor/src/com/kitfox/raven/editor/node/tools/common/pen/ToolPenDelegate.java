/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.raven.editor.node.tools.common.pen;

import com.kitfox.raven.editor.node.scene.snap.GraphLayout;
import com.kitfox.raven.editor.node.tools.common.ServiceDocument;
import com.kitfox.raven.editor.node.tools.common.ToolDisplay;
import java.awt.event.MouseEvent;

/**
 *
 * @author kitfox
 */
abstract public class ToolPenDelegate extends ToolDisplay
{
    protected final ToolPenProvider toolProvider;
    protected final ToolPenDispatch dispatch;
    
    public ToolPenDelegate(ToolPenDispatch dispatch)
    {
        super(dispatch.getUser());
        this.toolProvider = dispatch.toolProvider;
        this.dispatch = dispatch;
    }

    protected GraphLayout getGraphLayout()
    {
        ServiceDocument provider = user.getToolService(ServiceDocument.class);
        if (provider != null)
        {
            return provider.getGraphLayout();
        }
        return null;
    }

    @Override
    abstract protected void click(MouseEvent evt);
    
    @Override
    abstract protected void startDrag(MouseEvent evt);
    
    @Override
    abstract protected void dragTo(MouseEvent evt);
    
    @Override
    abstract protected void endDrag(MouseEvent evt);
    
}
