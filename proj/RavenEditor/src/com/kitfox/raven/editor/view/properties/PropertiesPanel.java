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

package com.kitfox.raven.editor.view.properties;

import com.kitfox.raven.editor.RavenDocument;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.RavenEditorListener;
import com.kitfox.raven.editor.RavenEditorWeakListener;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.SelectionEvent;
import com.kitfox.raven.util.SelectionListener;
import com.kitfox.raven.util.SelectionSubEvent;
import com.kitfox.raven.util.SelectionWeakListener;
import com.kitfox.raven.util.tree.NodeDocumentEvent;
import com.kitfox.raven.util.tree.NodeDocumentListener;
import com.kitfox.raven.util.tree.NodeDocumentWeakListener;
import com.kitfox.raven.util.tree.NodeObject;
import java.util.EventObject;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author kitfox
 */
public class PropertiesPanel extends javax.swing.JPanel
        implements RavenEditorListener, NodeDocumentListener,
        SelectionListener
{
    final RavenEditor editor;
    RavenEditorWeakListener listenerEditor;
    NodeDocumentWeakListener listenerRavenDoc;
    SelectionWeakListener selectionListener;

    PropertyModel model;

    /** Creates new form DisplayPanel */
    public PropertiesPanel(RavenEditor editor)
    {
        this.editor = editor;

        initComponents();

        table_params.setDefaultEditor(PropertyModelLine.class, new PropertyCellEditor());
        table_params.setDefaultRenderer(PropertyModelLine.class, new PropertyCellRenderer());

        listenerEditor = new RavenEditorWeakListener(this, editor);
        editor.addRavenEditorListener(listenerEditor);
//        buildHotkeys();
//        editor.getViewManager().getActionManager().addActionManagerListener(this);
        
    }

    private void updateDocument()
    {
        if (listenerRavenDoc != null)
        {
            listenerRavenDoc.remove();
            listenerRavenDoc = null;
        }

        RavenDocument doc = editor.getDocument();
        if (doc != null)
        {
            listenerRavenDoc = new NodeDocumentWeakListener(this, doc);
            doc.addNodeDocumentListener(listenerRavenDoc);
        }
        
        
        updateSymbol();
    }

    private void updateSymbol()
    {
        if (selectionListener != null)
        {
            selectionListener.remove();
            selectionListener = null;
        }

        RavenDocument doc = editor.getDocument();
        if (doc != null)
        {
            selectionListener = new SelectionWeakListener(this, doc.getCurSymbol().getSelection());
            doc.getCurSymbol().getSelection().addSelectionListener(selectionListener);
        }

        SwingUtilities.invokeLater(
            new Runnable() {
                @Override
                public void run()
                {
                    updateModelSwing();
                }
            }
        );
    }

    private void updateModelSwing()
    {
        RavenDocument doc = editor.getDocument();

        if (doc == null)
        {
            model = null;
        }
        else
        {
            Selection<NodeObject> sel = doc.getCurSymbol().getSelection();
            NodeObject top = sel.getTopSelected();
            if (top == null)
            {
                model = null;
            }
            else
            {
                model = new PropertyModel(top);
            }
        }
        table_params.setModel(model == null
                ? new DefaultTableModel(new Object[]{"Name", "Value"}, 0)
                : model);
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
    public void selectionChanged(SelectionEvent evt)
    {
        updateModelSwing();
    }

    @Override
    public void subselectionChanged(SelectionSubEvent evt)
    {
    }

    @Override
    public void symbolAdded(NodeDocumentEvent evt)
    {
    }

    @Override
    public void symbolRemoved(NodeDocumentEvent evt)
    {
    }

    @Override
    public void currentSymbolChanged(NodeDocumentEvent evt)
    {
        updateSymbol();
    }


//    private void buildHotkeys()
//    {
//        ActionManager actionManager = editor.getViewManager().getActionManager();
//        actionManager.buildInputs(tree_outline.getInputMap());
//        actionManager.buildActions(tree_outline.getActionMap());
//    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        table_params = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setViewportView(table_params);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable table_params;
    // End of variables declaration//GEN-END:variables

}
