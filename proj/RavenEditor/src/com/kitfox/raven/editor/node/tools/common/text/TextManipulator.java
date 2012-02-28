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

package com.kitfox.raven.editor.node.tools.common.text;

import com.kitfox.raven.editor.node.scene.RavenNodeGroup;
import com.kitfox.raven.editor.node.scene.RavenNodeRoot;
import com.kitfox.raven.editor.node.scene.RavenNodeText;
import com.kitfox.raven.paint.RavenPaint;
import com.kitfox.raven.paint.RavenPaintLayout;
import com.kitfox.raven.paint.RavenStroke;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.text.Justify;
import com.kitfox.raven.util.text.TextDocumentEditor;
import com.kitfox.raven.util.text.TextFormatter2.GlyphToken;
import com.kitfox.raven.util.text.TextFormatter2.LineSetToken;
import com.kitfox.raven.util.text.TextFormatterFont;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.NodeObjectProviderIndex;
import com.kitfox.raven.util.tree.SelectionRecord;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class TextManipulator
{
    RavenNodeRoot root;

    AffineTransform textLocalToDevice;
    AffineTransform textLocalToWorld;
    NodeObject parent;
    Point2D.Double localToParentXlate;

    Font textFont;
    Justify textJustify;
    RavenPaint textFillPaint;
    RavenPaintLayout textFillPaintLayout;
    RavenPaint textStrokePaint;
    RavenStroke textStroke;
    RavenPaintLayout textStrokePaintLayout;
    
    LineSetToken textLines;
    TextDocumentEditor textDoc;
    Path2D.Double textShape;


    RavenNodeText sourceNode;
    HashMap<Integer, GlyphToken> cursorMap = new HashMap<Integer, GlyphToken>();


    public TextManipulator(int x, int y, RavenNodeRoot root)
    {
        this(null, x, y, root);
    }

    public TextManipulator(RavenNodeText sourceNode, int x, int y, RavenNodeRoot root)
    {
        this.root = root;
        this.sourceNode = sourceNode;

        if (sourceNode == null)
        {
            textDoc = new TextDocumentEditor();

            textFont = root.getTextFont();
            textJustify = root.getTextJustify();
            textFillPaint = root.fillPaint.getValue();
            textFillPaintLayout = new RavenPaintLayout();

            //Find parent to attach new text node to
            Selection<NodeObject> sel = root.getSelection();
            NodeObject top = sel.getTopSelected();
            if (top != null)
            {
                parent = top;
                while (!(parent instanceof RavenNodeGroup
                        || parent instanceof RavenNodeRoot))
                {
                    parent = parent.getParent().getNode();
                }
            }
            if (parent == null)
            {
                parent = root;
            }

            
            AffineTransform textParentToWorld;
            if (parent instanceof RavenNodeRoot)
            {
                textParentToWorld = new AffineTransform();
            }
            else
            {
                RavenNodeGroup group = (RavenNodeGroup)parent;
                textParentToWorld = group.getLocalToWorldTransform((AffineTransform)null);
            }

            AffineTransform textParentToDevice = root.getWorldToDeviceTransform((AffineTransform)null);
            textParentToDevice.concatenate(textParentToWorld);

            localToParentXlate = new Point2D.Double(x, y);
            try
            {
                textParentToDevice.inverseTransform(localToParentXlate,
                        localToParentXlate);
            } catch (NoninvertibleTransformException ex)
            {
                Logger.getLogger(TextManipulator.class.getName()).log(Level.SEVERE, null, ex);
            }

            textLocalToWorld = new AffineTransform(textParentToWorld);
            textLocalToWorld.translate(localToParentXlate.x, localToParentXlate.y);

            textLocalToDevice = new AffineTransform(textParentToDevice);
            textLocalToDevice.translate(localToParentXlate.x, localToParentXlate.y);
        }
        else
        {
//            sourceNode.setEditorSupressRendering(true);
            sourceNode.visible.setValue(false, false);
            textDoc = new TextDocumentEditor(sourceNode.getText());

            textFont = sourceNode.getTextFont();
            textJustify = sourceNode.getTextJustify();
            textFillPaint = sourceNode.paint.getValue();
            textFillPaintLayout = sourceNode.paintLayout.getValue();
            textStrokePaint = sourceNode.strokePaint.getValue();
            textStroke = sourceNode.stroke.getValue();
            textStrokePaintLayout = sourceNode.strokePaintLayout.getValue();

            textLocalToDevice = sourceNode.getLocalToDeviceTransform((AffineTransform)null);
            textLocalToWorld = sourceNode.getLocalToWorldTransform((AffineTransform)null);
        }

    }

    public void keyPressed(KeyEvent evt)
    {
        textDoc.process(evt);

        textShape = null;
        textLines = null;
    }

    private Path2D.Double getTextShape()
    {
        if (textShape == null)
        {
            cursorMap.clear();
            textShape = new Path2D.Double();
            String text = textDoc.toString();

            FontRenderContext frc = new FontRenderContext(
                    new AffineTransform(), false, true);

            TextFormatterFont formatter = new TextFormatterFont(textFont, frc);
            textLines = formatter.layout(text, 0, textJustify);

            textLines.append(textShape);

            cursorMap = textLines.getGlyphIndexMap();
        }
        return textShape;
    }

    public void paint(Graphics2D g, boolean drawCursor)
    {
        if (textDoc == null)
        {
            return;
        }

        if (root.antialiased.getValue())
        {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        //String text = textDoc.toString();
        Path2D.Double path = getTextShape();
        Shape devShape = textLocalToDevice.createTransformedShape(path);

        if (textFillPaint != null)
        {
            Paint paint = textFillPaint.getPaintSwatch(null);
            g.setPaint(paint);
//            g.setPaint(textFillPaint.getPaint(textFillPaintLayout, textLocalToWorld));
            g.fill(devShape);
        }

        //Draw cursor
        if (drawCursor)
        {
            int index = textDoc.getCursorTextOffset();
            GlyphToken glyph = cursorMap.get(index);

            Rectangle2D bounds = glyph == null ? textLines.getEndCursorBounds()
                    : glyph.getBounds();
            Line2D.Double line = new Line2D.Double(
                    bounds.getX(), bounds.getY(),
                    bounds.getX(), bounds.getY() + bounds.getHeight());
            Shape cursorShape = textLocalToDevice.createTransformedShape(line);
            g.draw(cursorShape);
        }
    }

    public boolean moveCursorToPointDevice(int x, int y)
    {
        //Make sure that cursorMap is built
        getTextShape();

        Point2D.Double pt = new Point2D.Double(x, y);
        try
        {
            textLocalToDevice.inverseTransform(pt, pt);
        } catch (NoninvertibleTransformException ex)
        {
            Logger.getLogger(TextManipulator.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (Integer i: cursorMap.keySet())
        {
            GlyphToken glyph = cursorMap.get(i);
            if (glyph.getBounds().contains(pt))
            {
                textDoc.setCursorTextOffset(i);
                return true;
            }
        }

        return false;
    }

    public void commit()
    {
        if (sourceNode == null)
        {
            RavenNodeText node =
                    NodeObjectProviderIndex.inst().createNode(RavenNodeText.class, root);

            node.setText(textDoc.toString());
            node.setTextFont(textFont);
            node.setTextJustify(textJustify);
            node.paint.setValue(textFillPaint);
            node.paintLayout.setValue(textFillPaintLayout);
            node.transX.setValue((float)localToParentXlate.x);
            node.transY.setValue((float)localToParentXlate.y);

            if (parent == null)
            {
                root.sceneGraph.add(node);
            }
            else
            {
                if (parent instanceof RavenNodeGroup)
                {
                    ((RavenNodeGroup)parent).children.add(node);
                }
                else
                {
                    root.sceneGraph.add(node);
                }
            }
        }
        else
        {
            sourceNode.setText(textDoc.toString());
//            sourceNode.setEditorSupressRendering(false);
            sourceNode.visible.setValue(true, false);
        }
    }

    public void cancel()
    {
        if (sourceNode != null)
        {
//            sourceNode.setEditorSupressRendering(false);
            sourceNode.visible.setValue(true, false);
        }
    }

}
