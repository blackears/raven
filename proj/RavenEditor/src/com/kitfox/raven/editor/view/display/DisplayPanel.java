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

/*
 * DisplayPanel.java
 *
 * Created on Nov 12, 2010, 10:46:41 PM
 */

package com.kitfox.raven.editor.view.display;

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.renderer.CyGLWrapper;
import com.kitfox.game.control.color.UnderlayPaint;
import com.kitfox.raven.editor.RavenDocument;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.RavenEditorListener;
import com.kitfox.raven.editor.RavenEditorWeakListener;
import com.kitfox.raven.editor.node.tools.Tool;
import com.kitfox.raven.editor.node.tools.ToolListener;
import com.kitfox.raven.editor.node.tools.ToolPalette;
import com.kitfox.raven.editor.node.tools.ToolPaletteEvent;
import com.kitfox.raven.editor.node.tools.ToolPaletteListener;
import com.kitfox.raven.editor.node.tools.ToolPaletteWeakListener;
import com.kitfox.raven.editor.node.tools.ToolProvider;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.editor.node.tools.common.ServiceBackground;
import com.kitfox.raven.editor.node.tools.common.ServiceDevice;
import com.kitfox.raven.editor.node.tools.common.ServiceDeviceCamera;
import com.kitfox.raven.editor.node.tools.common.ServiceEditor;
import com.kitfox.raven.editor.node.tools.common.ServicePen;
import com.kitfox.raven.editor.paint.RavenPaintColor;
import com.kitfox.raven.util.tree.ChildWrapperEvent;
import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.util.tree.NodeDocumentListener;
import com.kitfox.raven.util.tree.NodeDocumentWeakListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.EventObject;
import jpen.PenManager;

/**
 *
 * @author kitfox
 */
@Deprecated
public class DisplayPanel extends javax.swing.JPanel
        implements RavenEditorListener, NodeDocumentListener,
        DisplayTilesListener, ToolUser, ToolPaletteListener,
        ServiceDevice, ServiceDeviceCamera, ToolListener, ServicePen,
        ServiceEditor
{
    final RavenEditor editor;
    RavenEditorWeakListener listenerEditor;
    NodeDocumentWeakListener listenerDoc;
    ToolPaletteWeakListener listenerTool;
    
    Tool tool;

    DisplayTileUpdater displayTiles;
    private final PenManager penManager;

    /** Creates new form DisplayPanel */
    public DisplayPanel(RavenEditor editor)
    {
        this.editor = editor;

        initComponents();

        listenerEditor = new RavenEditorWeakListener(this, editor);
        editor.addRavenEditorListener(listenerEditor);

        ToolPalette pal = editor.getToolManager().getPalette();
        listenerTool = new ToolPaletteWeakListener(this, pal);
        pal.addToolPaletteListener(listenerTool);

        penManager = new PenManager(this);
    }

    @Override
    public void addNotify()
    {
        super.addNotify();
        
        displayTiles = DisplayTileUpdater.create(getGraphicsConfiguration());
        displayTiles.setDeviceBounds(getBounds());
        displayTiles.addDisplayTilesListener(this);
        updateDocument();
    }

    private void updateDocument()
    {
        if (listenerDoc != null)
        {
            listenerDoc.remove();
            listenerDoc = null;
        }

        RavenDocument doc = editor.getDocument();
        if (displayTiles != null && doc != null)
        {
            NodeDocument root = doc.getCurDocument();
            displayTiles.setScene(root);
        }

        if (doc != null)
        {
            listenerDoc = new NodeDocumentWeakListener(this, doc.getCurDocument());
            doc.getCurDocument().addNodeDocumentListener(listenerDoc);
        }

        repaint();
    }

    @Override
    public void currentToolChanged(ToolPaletteEvent evt)
    {
        ToolPalette pal = editor.getToolManager().getPalette();
        ToolProvider prov = pal.getCurrentTool();
        setTool(prov.create(this));
    }

    protected void setTool(Tool newTool)
    {
        if (tool != null)
        {
            removeMouseListener(tool.getListener(MouseListener.class));
            removeMouseMotionListener(tool.getListener(MouseMotionListener.class));
            removeKeyListener(tool.getListener(KeyListener.class));
            tool.removeToolListener(this);
            tool.dispose();
        }

        tool = newTool;

        if (tool != null)
        {
            //Provide input to tool
            tool.addToolListener(this);
            addMouseListener(tool.getListener(MouseListener.class));
            addMouseMotionListener(tool.getListener(MouseMotionListener.class));
            addKeyListener(tool.getListener(KeyListener.class));
        }
        
        repaint();
    }

    @Override
    public <T> T getToolService(Class<T> serviceClass)
    {
        //Provide objects tool can use to communicate with this panel
        if (serviceClass.isAssignableFrom(getClass()))
        {
            return (T)this;
        }


        RavenDocument doc = editor.getDocument();
        if (doc != null)
        {
            return doc.getCurDocument().getNodeService(serviceClass, false);
        }

        return null;
    }

    @Override
    public <T> void getToolServices(Class<T> serviceClass, ArrayList<T> appendList)
    {
        T service = getToolService(serviceClass);
        if (service != null)
        {
            appendList.add(service);
        }

        RavenDocument doc = editor.getDocument();
        if (doc != null)
        {
            doc.getCurDocument().getNodeServices(serviceClass, appendList, false);
        }
    }

    @Override
    public void recentFilesChanged(EventObject evt)
    {
    }

    @Override
    public void documentChanged(EventObject evt)
    {
        updateDocument();
    }

    @Override
    public void documentNameChanged(PropertyChangeEvent evt)
    {
    }

    @Override
    public void documentPropertyChanged(PropertyChangeEvent evt)
    {
        repaint();
    }

    @Override
    public void documentNodeChildAdded(ChildWrapperEvent evt)
    {
        repaint();
    }

    @Override
    public void documentNodeChildRemoved(ChildWrapperEvent evt)
    {
        repaint();
    }

    @Override
    public void displayTilesComputed(EventObject evt)
    {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics gg)
    {
        Graphics2D g = (Graphics2D)gg;

        //Draw scene
        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            g.setColor(Color.GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
            return;
        }

        NodeDocument root = doc.getCurDocument();
        ServiceBackground service = root.getNodeService(ServiceBackground.class, false);
        RavenPaintColor bg = RavenPaintColor.TRANSPARENT;
//        RavenPaintColor bg = service == null
//                ? RavenPaintColor.TRANSPARENT
//                : service.getBackgroundColor();
        if (bg.getColor().isTransparent())
        {
            g.setPaint(UnderlayPaint.inst().getPaint());
            g.fillRect(0, 0, getWidth(), getHeight());
            //ravenNodeRoot.background
        }

        //Draw background
        displayTiles.blit(g);

        //Draw tool
        if (tool != null)
        {
            tool.paint((Graphics2D)g);
        }
    }

    @Override
    public void toolDisplayChanged(EventObject evt)
    {
        repaint();
    }

    @Override
    public void getDeviceBounds(Rectangle bounds)
    {
        bounds.setBounds(getX(), getY(), getWidth(), getHeight());
    }

    @Deprecated
    @Override
    public AffineTransform getWorldToDeviceTransform(AffineTransform xform)
    {
        if (xform == null)
        {
            xform = new AffineTransform();
        }

        RavenDocument doc = editor.getDocument();
        if (doc != null)
        {
            NodeDocument root = doc.getCurDocument();
            ServiceDeviceCamera service = root.getNodeService(ServiceDeviceCamera.class, false);
            return service.getWorldToDeviceTransform(xform);
        }
        
        xform.setToIdentity();
        return xform;
    }

    @Deprecated
    @Override
    public void setWorldToDeviceTransform(AffineTransform xform)
    {
        RavenDocument doc = editor.getDocument();
        if (doc != null)
        {
            NodeDocument root = doc.getCurDocument();
            ServiceDeviceCamera service = root.getNodeService(ServiceDeviceCamera.class, false);
            service.setWorldToDeviceTransform(xform);
        }
    }

    @Override
    public CyMatrix4d getWorldToDeviceTransform(CyMatrix4d xform)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setWorldToDeviceTransform(CyMatrix4d xform)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Component getComponent()
    {
        return this;
    }

    /**
     * @return the penManager
     */
    @Override
    public PenManager getPenManager()
    {
        return penManager;
    }

    @Override
    public RavenEditor getEditor()
    {
        return editor;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        displayTiles.setDeviceBounds(getBounds());
        repaint();

    }//GEN-LAST:event_formComponentResized

    private void formMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMousePressed
    {//GEN-HEADEREND:event_formMousePressed
        requestFocus();
    }//GEN-LAST:event_formMousePressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    @Override
    public CyGLWrapper createOffscreenGL()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
