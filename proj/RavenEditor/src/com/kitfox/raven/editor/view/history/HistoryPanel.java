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

package com.kitfox.raven.editor.view.history;

import com.kitfox.raven.editor.RavenDocument;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.RavenEditorListener;
import com.kitfox.raven.editor.RavenEditorWeakListener;
import com.kitfox.raven.util.undo.History;
import com.kitfox.raven.util.undo.HistoryAction;
import com.kitfox.raven.util.undo.HistoryListener;
import com.kitfox.raven.util.undo.HistoryWeakListener;
import java.awt.Component;
import java.util.EventObject;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

/**
 *
 * @author kitfox
 */
public class HistoryPanel extends javax.swing.JPanel
        implements RavenEditorListener, HistoryListener
{
    final RavenEditor editor;
    RavenEditorWeakListener listenerEditor;
    HistoryWeakListener listenerHistory;

    boolean updating;

    /** Creates new form DisplayPanel */
    public HistoryPanel(RavenEditor editor)
    {
        this.editor = editor;

        initComponents();

        listenerEditor = new RavenEditorWeakListener(this, editor);
        editor.addRavenEditorListener(listenerEditor);
        updateDocument();

        list_history.setCellRenderer(new Renderer());
    }

    private void updateDocument()
    {
        if (listenerHistory != null)
        {
            listenerHistory.remove();
            listenerHistory = null;
        }

        RavenDocument doc = editor.getDocument();
        if (doc != null)
        {
            listenerHistory = new HistoryWeakListener(this, doc.getHistory());
            doc.getHistory().addHistoryListener(listenerHistory);
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
        updating = true;

        RavenDocument doc = editor.getDocument();

        if (doc == null)
        {
            list_history.setListData(new Object[0]);
        }
        else
        {
            History hist = doc.getHistory();
            list_history.setListData(hist.getActionListAsArray());

            int idx = hist.getUndoCursor() - 1;
            if (idx < 0)
            {
                list_history.clearSelection();
            }
            else
            {
                list_history.setSelectedIndex(idx);
            }
        }

        updating = false;
    }

    private void pushUndoCursor()
    {
        if (updating)
        {
            return;
        }

        RavenDocument doc = editor.getDocument();

        if (doc == null)
        {
            return;
        }

        int idx = list_history.getSelectedIndex();
        History hist = doc.getCurDocument().getHistory();
        hist.moveCursorTo(idx + 1);
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
    public void historyChanged(EventObject evt)
    {
        updateDocument();
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
        list_history = new javax.swing.JList();

        list_history.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        list_history.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                list_historyValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(list_history);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void list_historyValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_list_historyValueChanged
        pushUndoCursor();
    }//GEN-LAST:event_list_historyValueChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList list_history;
    // End of variables declaration//GEN-END:variables
    // End of variables declaration

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

            HistoryAction histAction = (HistoryAction)value;
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
            setText(histAction == null ? "" : histAction.getTitle());
            return this;
        }
    }


}
