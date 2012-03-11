/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.raven.editor.node.tools.common.shape.curveEdit;

import com.kitfox.raven.editor.node.tools.common.ToolDisplay;
import java.awt.event.MouseEvent;

/**
 *
 * @author kitfox
 */
abstract public class ToolCurveEditDelegate extends ToolDisplay
{
    protected final ToolCurveEditProvider toolProvider;
    protected final ToolCurveEditDispatch dispatch;
    
    public ToolCurveEditDelegate(ToolCurveEditDispatch dispatch)
    {
        super(dispatch.getUser());
        this.toolProvider = dispatch.toolProvider;
        this.dispatch = dispatch;
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
