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
 * CameraDisplayPanel.java
 *
 * Created on Jul 28, 2011, 2:08:51 PM
 */
package com.kitfox.raven.editor.view.camera;

import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyRendererListener;
import com.kitfox.coyote.renderer.jogl.CoyotePanel;
import com.kitfox.raven.editor.RavenDocument;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.RavenEditorListener;
import com.kitfox.raven.editor.RavenEditorWeakListener;
import com.kitfox.raven.editor.node.scene.RavenNodeCamera;
import com.kitfox.raven.editor.node.scene.RavenNodeComposition;
import com.kitfox.raven.editor.node.scene.RavenNodeCompositionLibrary;
import com.kitfox.raven.util.tree.FrameKey;
import com.kitfox.raven.editor.node.scene.RenderContext;
import com.kitfox.raven.editor.node.scene.RenderDevice;
import com.kitfox.raven.editor.view.displayCy.CyRenderService;
import com.kitfox.raven.util.tree.ChildWrapperEvent;
import com.kitfox.raven.util.tree.NodeSymbolListener;
import com.kitfox.raven.util.tree.NodeSymbolWeakListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.util.EventObject;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 *
 * @author kitfox
 */
public class CameraDisplayPanel
        extends JPanel
        implements RavenEditorListener, NodeSymbolListener,
        CyRendererListener
{
    final RavenEditor editor;
    RavenEditorWeakListener listenerEditor;
    NodeSymbolWeakListener listenerDoc;

    CoyotePanel renderPanel = new CoyotePanel();
    
    RenderDevice renderDevice;
    
    boolean updating;
    
    /** Creates new form CameraDisplayPanel */
    public CameraDisplayPanel(RavenEditor editor)
    {
        this.editor = editor;

        initComponents();

        panel_renderArea.add(renderPanel, BorderLayout.CENTER);
        
        listenerEditor = new RavenEditorWeakListener(this, editor);
        editor.addRavenEditorListener(listenerEditor);
        
        renderPanel.addCyRendererListener(this);

        combo_display.setRenderer(new Renderer());
        
        updateDocument();
    }

    private void updateDocument()
    {
        //Listen to right document
        if (listenerDoc != null)
        {
            listenerDoc.remove();
            listenerDoc = null;
        }
        
        RavenDocument doc = editor.getDocument();

        if (doc != null)
        {
            listenerDoc = new NodeSymbolWeakListener(this, doc.getCurSymbol());
            doc.getCurSymbol().addNodeSymbolListener(listenerDoc);
        }

        renderDevice = null;

        //Update drop down
        rebuildMenu();
        
        repaint();
    }

    private void rebuildMenu()
    {
        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return;
        }
        
        updating = true;
        
        Object sel = combo_display.getSelectedItem();
        
        combo_display.removeAllItems();
        
        CyRenderService serv = doc == null ? null 
                : doc.getCurSymbol().getRoot()
                .getNodeService(CyRenderService.class, false);
        if (serv != null)
        {
            RavenNodeCompositionLibrary lib =
                    serv.getCompositionLibrary();
            for (RavenNodeComposition comp: lib.getCompositions())
            {
                combo_display.addItem(comp);
            }
            
//            combo_display.addItem(new JSeparator());
            
            for (RavenNodeCamera cam: serv.getCameras())
            {
                combo_display.addItem(cam);
            }            
        }
        combo_display.setSelectedItem(sel);

        //Force selection if empty
        int curIdx = combo_display.getSelectedIndex();
        if (curIdx == -1)
        {
            if (combo_display.getItemCount() == 0)
            {
                renderDevice = null;
            }
            else
            {
                combo_display.setSelectedItem(0);
                renderDevice = (RenderDevice)combo_display.getItemAt(0);
            }
        }
        
        updating = false;
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
    public void symbolNameChanged(PropertyChangeEvent evt)
    {
    }

    @Override
    public void symbolPropertyChanged(PropertyChangeEvent evt)
    {
    }

    @Override
    public void symbolNodeChildAdded(ChildWrapperEvent evt)
    {
        if (evt.getNode() instanceof RenderDevice)
        {
            rebuildMenu();
        }
    }

    @Override
    public void symbolNodeChildRemoved(ChildWrapperEvent evt)
    {
        if (evt.getNode() instanceof RenderDevice)
        {
            rebuildMenu();
        }
    }

    @Override
    public void render(CyDrawStack rend)
    {
        if (renderDevice == null)
        {
            return;
        }

        RenderContext context = new RenderContext(rend, FrameKey.DIRECT, false);
        renderDevice.renderComposition(context);

        
//        RavenDocument doc = editor.getDocument();
//
//        if (doc == null)
//        {
//            return;
//        }
//
//        CyRenderService serv = doc.getCurDocument().getNodeService(CyRenderService.class, false);
//        if (serv != null)
//        {
//            RenderContext ctx = new RenderContext(rend, FrameKey.DIRECT);
//            serv.renderCamerasAll(ctx);
//        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jPanel1 = new javax.swing.JPanel();
        combo_display = new javax.swing.JComboBox();
        panel_renderArea = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        combo_display.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                combo_displayActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(combo_display, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(198, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(combo_display, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel1, java.awt.BorderLayout.NORTH);

        panel_renderArea.setLayout(new java.awt.BorderLayout());
        add(panel_renderArea, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void combo_displayActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_combo_displayActionPerformed
    {//GEN-HEADEREND:event_combo_displayActionPerformed
        if (updating)
        {
            return;
        }
        
        renderDevice = (RenderDevice)combo_display.getSelectedItem();
    }//GEN-LAST:event_combo_displayActionPerformed

    //----------------------------------

    class Renderer extends JLabel implements ListCellRenderer
    {
        private static final long serialVersionUID = 1;

        public Renderer()
        {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
        {
            if (value instanceof String)
            {
                //Empty lists will provide an empty string
                setText("");
                return this;
            }

            if (isSelected)
            {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else
            {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            
            RenderDevice device = (RenderDevice)value;
            setIcon(device == null ? null : device.getIcon());
            setText(device == null ? "" : device.getName());
            return this;
        }
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox combo_display;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel panel_renderArea;
    // End of variables declaration//GEN-END:variables
}
