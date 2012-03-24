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

package com.kitfox.raven.editor.node.tools.common.shape.layout;

import com.kitfox.raven.editor.node.scene.RenderContext;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.editor.node.tools.common.ServiceDocument;
import com.kitfox.raven.editor.node.tools.common.ToolDisplay;
import com.kitfox.raven.editor.node.tools.common.shape.pen.ServiceBezierMesh;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.tree.NodeObject;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 *
 * @author kitfox
 */
public class ToolPaintLayoutDispatch extends ToolDisplay
{
    final ToolPaintLayoutProvider toolProvider;
    
    ToolPaintLayoutDelegate delegate;
    NodeObject delegateNode;
    
    protected ToolPaintLayoutDispatch(ToolUser user, ToolPaintLayoutProvider toolProvider)
    {
        super(user);
        this.toolProvider = toolProvider;
        setEnableRestrictAxis(false);
    }
    
    private void updateDelegate()
    {
        ServiceDocument servDoc = user.getToolService(ServiceDocument.class);
        Selection<NodeObject> sel = servDoc.getSelection();
        NodeObject node = sel.getTopSelected();
        if (node != delegateNode)
        {
            //Clear delegate if top selection changed
            delegateNode = null;
            delegate = null;
        }
            
        if (delegate == null)
        {
            if (node == null)
            {
                return;
            }
            
            //Check if we're on a selection layer
            ServiceBezierMesh servMesh = node.getNodeService(ServiceBezierMesh.class, false);
            if (servMesh != null)
            {
                delegate = new ToolPaintLayoutMesh(this, node, servMesh);
                delegateNode = node;
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
        updateDelegate();

        if (delegate != null)
        {
            delegate.click(evt);
        }
    }
    
    @Override
    protected void startDrag(MouseEvent evt)
    {
        updateDelegate();

        if (delegate != null)
        {
            delegate.startDrag(evt);
        }
    }
    
    @Override
    protected void dragTo(MouseEvent evt)
    {
        updateDelegate();

        if (delegate != null)
        {
            delegate.dragTo(evt);
        }
    }
    
    @Override
    protected void endDrag(MouseEvent evt)
    {
        updateDelegate();

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
        
        updateDelegate();

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
        updateDelegate();

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
