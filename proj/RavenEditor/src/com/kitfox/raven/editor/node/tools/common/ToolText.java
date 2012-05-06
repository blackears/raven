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

package com.kitfox.raven.editor.node.tools.common;

import com.kitfox.coyote.shape.CyRectangle2d;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.node.scene.RavenNodeRoot;
import com.kitfox.raven.editor.node.scene.RavenNodeText;
import com.kitfox.raven.editor.node.tools.ToolProvider;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.editor.node.tools.common.text.TextManipulator;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeSymbol;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.NodeVisitor;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ToolText extends ToolDisplay
{
    ToolText.Provider toolProvider;

    TimerTask repaintTask;
    int timerCount;

    TextManipulator manip;


    protected ToolText(ToolUser user, ToolText.Provider toolProvider)
    {
        super(user);
        this.toolProvider = toolProvider;

        repaintTask = new TimerTask()
        {
            @Override
            public void run() {
                ++timerCount;
                if (manip != null)
                {
                    fireToolDisplayChanged();
                }
            }
        };
        timer.schedule(repaintTask, 0, 400);
    }

    @Override
    public void click(MouseEvent evt)
    {
        ServiceDocument provider = user.getToolService(ServiceDocument.class);
        if (provider == null)
        {
            return;
        }

        NodeSymbol doc = provider.getSymbol();
        if (doc == null)
        {
            return;
        }

        int x = evt.getX();
        int y = evt.getY();

        if (manip != null)
        {
            if (manip.moveCursorToPointDevice(x, y))
            {
                //Position cursor within existing manipulator
                fireToolDisplayChanged();
                return;
            }
            else
            {
                //Finish up current editing
                manip.commit();
                manip = null;
                fireToolDisplayChanged();
                return;
            }
        }

        TextNodePicker picker = new TextNodePicker(x, y);
        doc.visit(picker);

        if (picker.picked == null)
        {
            //Start new node
            manip = new TextManipulator(x, y, (RavenNodeRoot)doc);
        }
        else
        {
            manip = new TextManipulator(picker.picked, x, y, (RavenNodeRoot)doc);
            manip.moveCursorToPointDevice(x, y);
        }
        

        fireToolDisplayChanged();
    }

    @Override
    protected void startDrag(MouseEvent evt)
    {
    }

    @Override
    protected void dragTo(MouseEvent evt)
    {
    }

    @Override
    protected void endDrag(MouseEvent evt)
    {
    }

    @Override
    public void cancel()
    {
        //Cancel editing
        manip.cancel();
        manip = null;

        fireToolDisplayChanged();
    }

    @Override
    public void dispose()
    {
        repaintTask.cancel();
        repaintTask = null;

        if (manip != null)
        {
            manip.commit();
            manip = null;
        }
    }

    @Override
    public void keyPressed(KeyEvent evt)
    {
        if (manip == null)
        {
            super.keyPressed(evt);
            return;
        }

        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
        {
            cancel();
            return;
        }

        manip.keyPressed(evt);
        fireToolDisplayChanged();
    }

    @Override
    public void paint(Graphics2D g)
    {
        if (manip == null)
        {
            return;
        }

        manip.paint(g, (timerCount & 0x1) != 0);
    }

    //---------------------------------------

    class TextNodePicker implements NodeVisitor
    {
        RavenNodeText picked;
        Rectangle pickRect;

        Point2D.Double pt;
        Point2D.Double ptRes = new Point2D.Double();

        public TextNodePicker(int x, int y)
        {
            pt = new Point2D.Double(x, y);
        }

        @Override
        public void visit(NodeObject node)
        {
            if (!(node instanceof RavenNodeText))
            {
                return;
            }

            RavenNodeText textNode = (RavenNodeText)node;
            AffineTransform l2d = textNode.getLocalToDeviceTransform((AffineTransform)null);

            try
            {
                l2d.inverseTransform(pt, ptRes);
            } catch (NoninvertibleTransformException ex)
            {
                Logger.getLogger(ToolText.class.getName()).log(Level.SEVERE, null, ex);
            }

            CyRectangle2d bounds = textNode.getBoundsLocal();
            if (bounds.contains(ptRes.x, ptRes.y))
            {
                picked = textNode;
            }
        }
    }


    @ServiceInst(service=ToolProvider.class)
    static public class Provider extends ToolProvider<ToolText>
    {
        public Provider()
        {
            super("Text", "/icons/tools/text.png", "/manual/tools/text.html");
        }

        @Override
        public ToolText create(ToolUser user)
        {
            return new ToolText(user, this);
        }

        @Override
        public Component createToolSettingsEditor(RavenEditor editor)
        {
            return new ToolTextSettings(editor, this);
        }
    }

}
