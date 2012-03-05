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

package com.kitfox.raven.editor.view.color;

import com.kitfox.raven.editor.RavenDocument;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.RavenEditorListener;
import com.kitfox.raven.editor.RavenEditorWeakListener;
import com.kitfox.raven.editor.node.scene.RavenNodeRoot;
import com.kitfox.raven.editor.node.tools.common.ServiceColors2D;
import com.kitfox.raven.editor.view.properties.PropertyCustomEditorPanel;
import com.kitfox.raven.paint.RavenPaint;
import com.kitfox.raven.paint.RavenPaintEditor;
import com.kitfox.raven.paint.RavenStroke;
import com.kitfox.raven.paint.RavenStrokeEditor;
import com.kitfox.raven.util.tree.*;
import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.EventObject;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/**
 *
 * @author kitfox
 */
public class ColorPanel2 extends javax.swing.JPanel
        implements RavenEditorListener
{
    RavenEditor editor;

    RavenEditorWeakListener edListener;
    StrokeStyleMonitor strokeStyleMonitor;
    StrokePaintMonitor strokePaintMonitor;
    FillPaintMonitor fillPaintMonitor;
    
    RavenStrokePreviewPanel previewStrokeStyle = new RavenStrokePreviewPanel();
    RavenPaintPreviewPanel previewStrokePaint = new RavenPaintPreviewPanel();
    RavenPaintPreviewPanel previewFillPaint = new RavenPaintPreviewPanel();
    
    FillPaintIOMonitor fillPaintInlineMonitor = new FillPaintIOMonitor();
    StrokePaintIOMonitor strokePaintInlineMonitor = new StrokePaintIOMonitor();
    StrokeStyleIOMonitor strokeStyleInlineMonitor = new StrokeStyleIOMonitor();

    /**
     * Creates new form ColorPanel2
     */
    public ColorPanel2(RavenEditor editor)
    {
        this.editor = editor;
        initComponents();

        edListener = new RavenEditorWeakListener(this, editor);
        editor.addRavenEditorListener(edListener);

        panel_strokeShape.add(previewStrokeStyle, BorderLayout.CENTER);
        panel_strokePaint.add(previewStrokePaint, BorderLayout.CENTER);
        panel_fillPaint.add(previewFillPaint, BorderLayout.CENTER);

        previewFillPaint.addMouseListener(fillPaintInlineMonitor);
        previewStrokePaint.addMouseListener(strokePaintInlineMonitor);
        previewStrokeStyle.addMouseListener(strokeStyleInlineMonitor);

        rebuildDocument();
    }

    private void rebuildDocument()
    {
        if (strokeStyleMonitor != null)
        {
            strokeStyleMonitor.remove();
            strokeStyleMonitor = null;

            strokePaintMonitor.remove();
            strokePaintMonitor = null;

            fillPaintMonitor.remove();
            fillPaintMonitor = null;
        }

        
        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return;
        }

        NodeDocument root = doc.getCurDocument();
        ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);

        if (service != null)
        {
            strokeStyleMonitor = new StrokeStyleMonitor(service.getStrokeStyleProp());
            strokePaintMonitor = new StrokePaintMonitor(service.getStrokePaintProp());
            fillPaintMonitor = new FillPaintMonitor(service.getFillPaintProp());

            updateFromDocument();
        }
    }

    private void updateFromDocument()
    {
        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return;
        }

        updateFillPaint();
        updateStrokePaint();
        updateStrokeStyle();
    }

    private void updateStrokeStyle()
    {
        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return;
        }

        NodeDocument root = doc.getCurDocument();
        ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);

        PropertyData<RavenStroke> data = service.getStrokeStyleProp().getData();
        previewStrokeStyle.setStroke(data.getValue(root));
    }

    private void updateStrokePaint()
    {
        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return;
        }

        NodeDocument root = doc.getCurDocument();
        ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);

        PropertyData<RavenPaint> data = service.getStrokePaintProp().getData();
        previewStrokePaint.setPaint(data.getValue(root));
    }

    private void updateFillPaint()
    {
        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return;
        }

        NodeDocument root = doc.getCurDocument();
        ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);

        PropertyData<RavenPaint> data = service.getFillPaintProp().getData();
        previewFillPaint.setPaint(data.getValue(root));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        panel_fillPaint = new javax.swing.JPanel();
        panel_strokePaint = new javax.swing.JPanel();
        panel_strokeShape = new javax.swing.JPanel();

        setMinimumSize(new java.awt.Dimension(100, 100));
        setPreferredSize(new java.awt.Dimension(100, 100));
        setLayout(new java.awt.GridLayout(3, 0));

        panel_fillPaint.setBorder(javax.swing.BorderFactory.createTitledBorder("Fill Paint"));
        panel_fillPaint.setLayout(new java.awt.BorderLayout());
        add(panel_fillPaint);

        panel_strokePaint.setBorder(javax.swing.BorderFactory.createTitledBorder("Stroke Paint"));
        panel_strokePaint.setLayout(new java.awt.BorderLayout());
        add(panel_strokePaint);

        panel_strokeShape.setBorder(javax.swing.BorderFactory.createTitledBorder("Stroke Shape"));
        panel_strokeShape.setLayout(new java.awt.BorderLayout());
        add(panel_strokeShape);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel panel_fillPaint;
    private javax.swing.JPanel panel_strokePaint;
    private javax.swing.JPanel panel_strokeShape;
    // End of variables declaration//GEN-END:variables


    @Override
    public void recentFilesChanged(EventObject evt)
    {
    }

    @Override
    public void documentChanged(EventObject evt)
    {
        rebuildDocument();
    }

    private void runEditor(PropertyWrapperEditor editor)
    {
        JDialog dlg =
                new JDialog(SwingUtilities.getWindowAncestor(ColorPanel2.this), 
                JDialog.DEFAULT_MODALITY_TYPE);
        PropertyCustomEditor xact = editor.createCustomEditor();
        PropertyCustomEditorPanel custom = new PropertyCustomEditorPanel(
                xact.getCustomEditor(), dlg);
        dlg.getContentPane().add(custom, BorderLayout.CENTER);

        dlg.pack();
        Window win = SwingUtilities.getWindowAncestor(this);
        if (win != null)
        {
            dlg.setLocation(win.getX(), win.getY());
        }

        dlg.setVisible(true);

        if (custom.isOkay())
        {
            xact.customEditorCommit();
        }
        else
        {
            xact.customEditorCancel();
        }
    }
    
    //-------------------------------------
    class StrokeStyleIOMonitor extends MouseAdapter
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            RavenDocument doc = editor.getDocument();
            if (doc == null)
            {
                return;
            }
            
            NodeDocument root = doc.getCurDocument();
            ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);
            PropertyWrapper wrapper = service.getStrokeStyleProp();
            
            RavenStrokeEditor editor = new RavenStrokeEditor(wrapper);
            runEditor(editor);
        }
    }

    class StrokePaintIOMonitor extends MouseAdapter
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            RavenDocument doc = editor.getDocument();
            if (doc == null)
            {
                return;
            }
            
            NodeDocument root = doc.getCurDocument();
            ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);
            PropertyWrapper wrapper = service.getStrokePaintProp();
            
            RavenPaintEditor editor = new RavenPaintEditor(wrapper);
            runEditor(editor);
        }
    }

    class FillPaintIOMonitor extends MouseAdapter
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            RavenDocument doc = editor.getDocument();
            if (doc == null)
            {
                return;
            }
            
            NodeDocument root = doc.getCurDocument();
            ServiceColors2D service = root.getNodeService(ServiceColors2D.class, false);
            PropertyWrapper wrapper = service.getFillPaintProp();
            
            RavenPaintEditor editor = new RavenPaintEditor(wrapper);
            runEditor(editor);
        }
    }
    
    
    class StrokeStyleMonitor extends PropertyWrapperAdapter
    {
        final PropertyWrapper<RavenNodeRoot, RavenStroke> source;
        PropertyWrapperWeakListener listener;

        public StrokeStyleMonitor(PropertyWrapper<RavenNodeRoot, RavenStroke> source)
        {
            this.source = source;
            listener = new PropertyWrapperWeakListener(this, source);
            source.addPropertyWrapperListener(listener);
        }

        @Override
        public void propertyWrapperDataChanged(PropertyChangeEvent evt)
        {
            updateStrokeStyle();
        }

        private void remove()
        {
            listener.remove();
            listener = null;
        }
    }

    class StrokePaintMonitor extends PropertyWrapperAdapter
    {
        final PropertyWrapper<RavenNodeRoot, RavenPaint> source;
        PropertyWrapperWeakListener listener;

        public StrokePaintMonitor(PropertyWrapper<RavenNodeRoot, RavenPaint> source)
        {
            this.source = source;
            listener = new PropertyWrapperWeakListener(this, source);
            source.addPropertyWrapperListener(listener);
        }

        @Override
        public void propertyWrapperDataChanged(PropertyChangeEvent evt)
        {
            updateStrokePaint();
        }

        private void remove()
        {
            listener.remove();
            listener = null;
        }
    }

    class FillPaintMonitor extends PropertyWrapperAdapter
    {
        final PropertyWrapper<RavenNodeRoot, RavenPaint> source;
        PropertyWrapperWeakListener listener;

        public FillPaintMonitor(PropertyWrapper<RavenNodeRoot, RavenPaint> source)
        {
            this.source = source;
            listener = new PropertyWrapperWeakListener(this, source);
            source.addPropertyWrapperListener(listener);
        }

        @Override
        public void propertyWrapperDataChanged(PropertyChangeEvent evt)
        {
            updateFillPaint();
        }

        private void remove()
        {
            listener.remove();
            listener = null;
        }
    }
}
