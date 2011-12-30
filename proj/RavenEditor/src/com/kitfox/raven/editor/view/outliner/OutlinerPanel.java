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

package com.kitfox.raven.editor.view.outliner;

import com.kitfox.raven.editor.RavenDocument;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.RavenEditorListener;
import com.kitfox.raven.editor.RavenEditorWeakListener;
import com.kitfox.raven.util.SelectionSubEvent;
import com.kitfox.raven.util.tree.SelectionRecord;
import com.kitfox.raven.editor.action.ActionManager;
import com.kitfox.raven.editor.action.ActionManagerListener;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.SelectionEvent;
import com.kitfox.raven.util.SelectionListener;
import com.kitfox.raven.util.SelectionWeakListener;
import com.kitfox.raven.util.tree.NodeObject;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

/**
 *
 * @author kitfox
 */
public class OutlinerPanel extends javax.swing.JPanel
        implements RavenEditorListener, OutlinerTreeModelListener,
        ActionManagerListener, SelectionListener
{
    final RavenEditor editor;
    private RavenEditorWeakListener listenerEditor;
    private SelectionWeakListener listenerSelection;
    private OutlinerTreeModel model;

    boolean pullingSelection;

    /** Creates new form DisplayPanel */
    public OutlinerPanel(RavenEditor editor)
    {
        this.editor = editor;

        initComponents();

        listenerEditor = new RavenEditorWeakListener(this, editor);
        editor.addRavenEditorListener(listenerEditor);

        tree_outline.setModel(null);
        updateDocument();

        tree_outline.setCellRenderer(new OutlinerCellRenderer());
        tree_outline.setCellEditor(new OutlinerCellEditor());

        buildHotkeys();
        editor.getViewManager().getActionManager().addActionManagerListener(this);
    }

    private void updateDocument()
    {
        if (listenerSelection != null)
        {
            listenerSelection.remove();
            listenerSelection = null;
        }

        RavenDocument doc = editor.getDocument();
        if (doc != null)
        {
            listenerSelection = new SelectionWeakListener(this, doc.getCurDocument().getSelection());
            doc.getCurDocument().getSelection().addSelectionListener(listenerSelection);
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
            tree_outline.setModel(null);
        }
        else
        {
            model = new OutlinerTreeModel(doc.getCurDocument());
            model.addOutlinerTreeModelListener(OutlinerPanel.this);
            tree_outline.setModel(model);
        }
        tree_outline.setTransferHandler(new OutlinerTransferHandler(model));
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

    private void showPopup(MouseEvent evt)
    {
        TreePath path = tree_outline.getClosestPathForLocation(evt.getX(), evt.getY());

        OutlinerNode node = (OutlinerNode)path.getLastPathComponent();
        JPopupMenu popup = node.getPopupMenu();
        if (popup == null)
        {
            return;
        }

        popup.show(this, evt.getX(), evt.getY());
    }

    @Override
    public void requestExpandNode(OutlinerTreeModelEvent evt)
    {
        OutlinerNode node = evt.getNode();
        tree_outline.expandPath(node.getPath());
    }

    private void buildHotkeys()
    {
        ActionManager actionManager = editor.getViewManager().getActionManager();
        actionManager.buildInputs(tree_outline.getInputMap());
        actionManager.buildActions(tree_outline.getActionMap());
    }

    private void pullSelectionSwing()
    {
        pullingSelection = true;

        RavenDocument doc = editor.getDocument();
        if (doc != null)
        {
            Selection<SelectionRecord> sel = doc.getCurDocument().getSelection();
            ArrayList<SelectionRecord> recList = sel.getSelection();

            TreePath[] paths = new TreePath[recList.size()];
            for (int i = 0; i < paths.length; ++i)
            {
                SelectionRecord rec = recList.get(i);
                NodeObject node = rec.getNode();
                OutlinerNode nodeOut = model.findNode(node);

                paths[i] = nodeOut.getPath();
            }

            tree_outline.setSelectionPaths(paths);
        }


        pullingSelection = false;
    }
    
    private void pushSelection()
    {
        if (!pullingSelection)
        {
            //Push selected nodes to document selection

            RavenDocument doc = editor.getDocument();
            if (doc == null)
            {
                return;
            }

            Selection<SelectionRecord> sel = doc.getCurDocument().getSelection();
            ArrayList<SelectionRecord> recList = new ArrayList<SelectionRecord>();

            TreePath[] paths = tree_outline.getSelectionPaths();
            if (paths != null)
            {
                for (TreePath path: paths)
                {
                    OutlinerNode nodeOut = (OutlinerNode)path.getLastPathComponent();
                    if (nodeOut instanceof OutlinerNodeNodeFixed)
                    {
                        NodeObject node = ((OutlinerNodeNodeFixed)nodeOut).getNode();
                        SelectionRecord rec = new SelectionRecord(node);
                        recList.add(rec);
                    }
                    else if (nodeOut instanceof OutlinerNodeNodeList)
                    {
                        NodeObject node = ((OutlinerNodeNodeList)nodeOut).getNode();
                        SelectionRecord rec = new SelectionRecord(node);
                        recList.add(rec);
                    }
                }
            }
            sel.select(Selection.Type.REPLACE, recList);
        }
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tree_outline = new javax.swing.JTree();

        setLayout(new java.awt.BorderLayout());

        tree_outline.setDragEnabled(true);
        tree_outline.setDropMode(javax.swing.DropMode.ON_OR_INSERT);
        tree_outline.setEditable(true);
        tree_outline.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tree_outlineMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tree_outlineMouseReleased(evt);
            }
        });
        tree_outline.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                tree_outlineValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(tree_outline);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void tree_outlineMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tree_outlineMousePressed
        if (evt.isPopupTrigger())
        {
            showPopup(evt);
        }
    }//GEN-LAST:event_tree_outlineMousePressed

    private void tree_outlineMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tree_outlineMouseReleased
        if (evt.isPopupTrigger())
        {
            showPopup(evt);
        }
        else
        {
            pushSelection();
        }
    }//GEN-LAST:event_tree_outlineMouseReleased

    private void tree_outlineValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_tree_outlineValueChanged

    }//GEN-LAST:event_tree_outlineValueChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree tree_outline;
    // End of variables declaration//GEN-END:variables

    @Override
    public void hotkeyLayoutChanged(EventObject evt)
    {
        buildHotkeys();
    }

    @Override
    public void hotkeyActionsChanged(EventObject evt)
    {
    }

    @Override
    public void selectionChanged(SelectionEvent evt)
    {
//        SwingUtilities.invokeLater(new Runnable() {
//
//            @Override
//            public void run() {
//                pullSelectionSwing();
//            }
//        });
    }

    @Override
    public void subselectionChanged(SelectionSubEvent evt)
    {
//        throw new UnsupportedOperationException("Not supported yet.");
    }




}
