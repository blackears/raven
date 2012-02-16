/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.raven.editor.node.tools.common.pen;

import com.kitfox.raven.editor.node.scene.RenderContext;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.editor.node.tools.common.*;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.SelectionRecord;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 *
 * @author kitfox
 */
public class ToolPenDispatch extends ToolDisplay
{
    final ToolPenProvider toolProvider;
    
    //NetworkGraph curGraph;
    ToolPenDelegate delegate;
    
    protected ToolPenDispatch(ToolUser user, ToolPenProvider toolProvider)
    {
        super(user);
        this.toolProvider = toolProvider;
        
    }
    
    private void touchDelegate()
    {
        if (delegate == null)
        {
            ServiceDocument servDoc = user.getToolService(ServiceDocument.class);
            Selection<SelectionRecord> sel = servDoc.getSelection();
            SelectionRecord rec = sel.getTopSelected();
            if (rec == null)
            {
                return;
            }
            
            NodeObject node = rec.getNode();
            
            //Check if we're on a selection layer
            ServiceBezierMesh servMesh = node.getNodeService(ServiceBezierMesh.class, false);
            if (servMesh != null)
            {
                delegate = new ToolPenMesh(this, servMesh);
            }
        }
    }
    
    @Override
    public void dispose()
    {
    }

    @Override
    protected void click(MouseEvent evt)
    {
        touchDelegate();

        if (delegate != null)
        {
            delegate.click(evt);
        }
    }
    
    @Override
    protected void startDrag(MouseEvent evt)
    {
        touchDelegate();

        if (delegate != null)
        {
            delegate.startDrag(evt);
        }
    }
    
    @Override
    protected void dragTo(MouseEvent evt)
    {
        touchDelegate();

        if (delegate != null)
        {
            delegate.dragTo(evt);
        }
    }
    
    @Override
    protected void endDrag(MouseEvent evt)
    {
        touchDelegate();

        if (delegate != null)
        {
            delegate.endDrag(evt);
        }
    }

    @Override
    public void mouseMoved(MouseEvent evt)
    {
        if (delegate != null)
        {
            delegate.mouseMoved(evt);
        }
    }
    
    @Override
    public void render(RenderContext ctx)
    {
        super.render(ctx);
        
//        updateDelegate();

        if (delegate != null)
        {
            delegate.render(ctx);
        }
    }
    
    @Override
    public void cancel()
    {
    }

    @Override
    public void keyPressed(KeyEvent evt)
    {
        touchDelegate();

        if (delegate != null)
        {
            delegate.keyPressed(evt);
        }
    }

    void delegateDone()
    {
        delegate = null;
    }

    
}
