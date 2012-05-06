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

package com.kitfox.raven.editor.view.code;

import com.kitfox.raven.editor.RavenDocument;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.RavenEditorListener;
import com.kitfox.raven.editor.RavenEditorWeakListener;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.SelectionEvent;
import com.kitfox.raven.util.SelectionListener;
import com.kitfox.raven.util.SelectionSubEvent;
import com.kitfox.raven.util.SelectionWeakListener;
import com.kitfox.raven.util.tree.DocumentCode;
import com.kitfox.raven.util.tree.EventWrapper;
import com.kitfox.raven.util.tree.NodeSymbol;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.SelectionRecord;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.EventObject;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author kitfox
 */
public class CodePanel extends javax.swing.JPanel
        implements RavenEditorListener, SelectionListener
{
    final RavenEditor editor;
    RavenEditorWeakListener listenerEditor;
    SelectionWeakListener listenerSelection;

    SourceCodePanel panelSourceDocument = new SourceCodePanel();
    SourceCodePanel panelSourceObject = new SourceCodePanel();
    SourceCodePanel panelSourceImports = new SourceCodePanel();

    boolean updating;

    /** Creates new form DisplayPanel */
    public CodePanel(RavenEditor editor)
    {
        this.editor = editor;

        initComponents();

        listenerEditor = new RavenEditorWeakListener(this, editor);
        editor.addRavenEditorListener(listenerEditor);

        panel_sourceDocument.add(panelSourceDocument, BorderLayout.CENTER);
        panel_sourceObject.add(panelSourceObject, BorderLayout.CENTER);
        panel_sourceImports.add(panelSourceImports, BorderLayout.CENTER);

        combo_event.setRenderer(new EventWrapperRenderer());
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

    private void updateDocument()
    {
        if (listenerSelection != null)
        {
            listenerSelection.remove();
            listenerSelection = null;
        }

        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            panelSourceDocument.setSourceCode(null);
            panelSourceObject.setSourceCode(null);
            text_extends.setText("");
            textArea_implements.setText("");
            textArea_annotations.setText("");
            return;
        }
        NodeSymbol root = doc.getCurSymbol();
        Selection<NodeObject> sel = root.getSelection();
        listenerSelection = new SelectionWeakListener(this, sel);
        sel.addSelectionListener(listenerSelection);

        DocumentCode docCode = root.getDocumentCode();
        panelSourceDocument.setSourceCode(docCode.getSource());
        panelSourceImports.setSourceCode(docCode.getImports());
        text_extends.setText(docCode.getExtendsClass().getSource());
        textArea_implements.setText(docCode.getImplementsClasses().getSource());
        textArea_annotations.setText(docCode.getAnnotations().getSource());
        updateSelection();
    }

    private void updateSelection()
    {
        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return;
        }
        NodeSymbol root = doc.getCurSymbol();
        Selection<NodeObject> sel = root.getSelection();

        NodeObject top = sel.getTopSelected();
        if (top == null)
        {
            updating = true;
            combo_event.removeAllItems();
            updating = false;
            
            updateObjectCode();
            return;
        }

        label_object.setText(top.getName());

        updating = true;
        combo_event.removeAllItems();
        ArrayList<EventWrapper> wrappers = top.getEventWrappers();
        for (EventWrapper wrapper: wrappers)
        {
            combo_event.addItem(wrapper);
        }
        updating = false;

        updateObjectCode();
    }

    private void updateObjectCode()
    {
        EventWrapper wrapper = (EventWrapper)combo_event.getSelectedItem();
        panelSourceObject.setSourceCode(
                wrapper == null ? null : wrapper.getSource());
    }

    @Override
    public void selectionChanged(SelectionEvent evt)
    {
        updateSelection();
    }

    @Override
    public void subselectionChanged(SelectionSubEvent evt)
    {
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup_tools = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        panel_sourceObject = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        label_object = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        combo_event = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        panel_sourceDocument = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        text_extends = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textArea_implements = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        textArea_annotations = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        panel_sourceImports = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        panel_sourceObject.setLayout(new java.awt.BorderLayout());
        jPanel2.add(panel_sourceObject, java.awt.BorderLayout.CENTER);

        label_object.setText("Object");

        jLabel1.setText("Event");

        combo_event.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_eventActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_object)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(combo_event, 0, 336, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(label_object)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(combo_event, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel3, java.awt.BorderLayout.NORTH);

        jTabbedPane1.addTab("Object", jPanel2);

        panel_sourceDocument.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 388, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(panel_sourceDocument, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 354, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(panel_sourceDocument, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Document", jPanel1);

        jLabel2.setText("Extends");

        text_extends.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                text_extendsKeyReleased(evt);
            }
        });

        jLabel3.setText("Implements");

        textArea_implements.setColumns(20);
        textArea_implements.setRows(5);
        textArea_implements.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                textArea_implementsCaretUpdate(evt);
            }
        });
        jScrollPane1.setViewportView(textArea_implements);

        jLabel4.setText("Annotations");

        textArea_annotations.setColumns(20);
        textArea_annotations.setRows(5);
        textArea_annotations.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                textArea_annotationsCaretUpdate(evt);
            }
        });
        jScrollPane2.setViewportView(textArea_annotations);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                    .addComponent(text_extends, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(text_extends, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Declaration", jPanel4);

        panel_sourceImports.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 388, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(panel_sourceImports, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 354, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(panel_sourceImports, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Imports", jPanel5);

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void combo_eventActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_combo_eventActionPerformed
    {//GEN-HEADEREND:event_combo_eventActionPerformed
        if (updating)
        {
            return;
        }

        updateObjectCode();
}//GEN-LAST:event_combo_eventActionPerformed

    private void textArea_implementsCaretUpdate(javax.swing.event.CaretEvent evt)//GEN-FIRST:event_textArea_implementsCaretUpdate
    {//GEN-HEADEREND:event_textArea_implementsCaretUpdate
        RavenDocument doc = editor.getDocument();
        if (doc != null)
        {
            NodeSymbol root = doc.getCurSymbol();
            root.getDocumentCode().getImplementsClasses()
                    .setSource(textArea_implements.getText());
        }

    }//GEN-LAST:event_textArea_implementsCaretUpdate

    private void textArea_annotationsCaretUpdate(javax.swing.event.CaretEvent evt)//GEN-FIRST:event_textArea_annotationsCaretUpdate
    {//GEN-HEADEREND:event_textArea_annotationsCaretUpdate
        RavenDocument doc = editor.getDocument();
        if (doc != null)
        {
            NodeSymbol root = doc.getCurSymbol();
            root.getDocumentCode().getAnnotations()
                    .setSource(textArea_annotations.getText());
        }

    }//GEN-LAST:event_textArea_annotationsCaretUpdate

    private void text_extendsKeyReleased(java.awt.event.KeyEvent evt)//GEN-FIRST:event_text_extendsKeyReleased
    {//GEN-HEADEREND:event_text_extendsKeyReleased
        RavenDocument doc = editor.getDocument();
        if (doc != null)
        {
            NodeSymbol root = doc.getCurSymbol();
            root.getDocumentCode().getExtendsClass()
                    .setSource(text_extends.getText());
        }

    }//GEN-LAST:event_text_extendsKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup_tools;
    private javax.swing.JComboBox combo_event;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_object;
    private javax.swing.JPanel panel_sourceDocument;
    private javax.swing.JPanel panel_sourceImports;
    private javax.swing.JPanel panel_sourceObject;
    private javax.swing.JTextArea textArea_annotations;
    private javax.swing.JTextArea textArea_implements;
    private javax.swing.JTextField text_extends;
    // End of variables declaration//GEN-END:variables

    class EventWrapperRenderer extends JLabel implements ListCellRenderer
    {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
        {
            if (value instanceof String)
            {
                //Empty lists will provide an empty string
                setText("");
                return this;
            }

            EventWrapper wrap = (EventWrapper)value;
            setOpaque(true);
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
            
            String text = "";
            
            if (wrap != null)
            {
                text = wrap.getName();
                if (wrap.getSource().isEmpty())
                {
                    text = "<html><i>" + text + "</i></html>";
                }
            }
            setText(text);
            return this;
        }
    }

}
