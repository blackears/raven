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

package com.kitfox.raven.editor.node.tools.common.shape.pen;

import com.kitfox.raven.editor.node.scene.RenderContext;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.editor.node.tools.common.*;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.tree.NodeObject;
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
    NodeObject delegateNode;
    
    protected ToolPenDispatch(ToolUser user, ToolPenProvider toolProvider)
    {
        super(user);
        this.toolProvider = toolProvider;
        
    }
//    
//    private void touchDelegate()
//    {
//        if (delegate == null)
//        {
//            ServiceDocument servDoc = user.getToolService(ServiceDocument.class);
//            Selection<NodeObject> sel = servDoc.getSelection();
//            NodeObject node = sel.getTopSelected();
//            if (node == null)
//            {
//                return;
//            }
//            
//            //Check if we're on a selection layer
//            ServiceBezierMesh servMesh = node.getNodeService(ServiceBezierMesh.class, false);
//            if (servMesh != null)
//            {
//                delegate = new ToolPenMesh(this, node, servMesh);
//            }
//        }
//    }
    
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
            {
                ServiceBezierMesh servMesh = node.getNodeService(ServiceBezierMesh.class, false);
                if (servMesh != null)
                {
                    delegate = new ToolPenMesh(this, node, servMesh);
                    delegateNode = node;
                    return;
                }
            }

            {
                ServiceBezierPath servPath = node.getNodeService(ServiceBezierPath.class, false);
                if (servPath != null)
                {
                    delegate = new ToolPenPath(this, node, servPath);
                    delegateNode = node;
                    return;
                }
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
        updateDelegate();

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
