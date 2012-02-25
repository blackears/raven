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

package com.kitfox.raven.editor.view.console;

import com.kitfox.raven.editor.RavenEditor;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author kitfox
 */
public class ConsolePanel extends javax.swing.JPanel
{
    final RavenEditor editor;

    ArrayList<String> cmdHistory = new ArrayList<String>();
    int cmdCursor;
    boolean init = false;

    ScriptEngine engine;

    ScriptWriter writeOut = new ScriptWriter();
    ScriptWriter writeErr = new ScriptWriter();

    /** Creates new form DisplayPanel */
    public ConsolePanel(RavenEditor editor)
    {
        this.editor = editor;

        initComponents();

        combo_language.setRenderer(new FactoryRenderer());

        String prefEngineName = "ECMAScript";
        ScriptEngineFactory prefFact = null;

        ScriptEngineManager mgr = new ScriptEngineManager();
        for (ScriptEngineFactory fact: mgr.getEngineFactories())
        {
            combo_language.addItem(fact);
            if (fact.getLanguageName().equals(prefEngineName))
            {
                prefFact = fact;
            }
        }
        if (prefFact == null)
        {
            prefFact = (ScriptEngineFactory)combo_language.getItemAt(0);
        }
        combo_language.setSelectedItem(prefFact);

        switchToEngine(prefFact);

        init = true;
    }

    private void cmdPrev()
    {
        if (cmdCursor == 0)
        {
            return;
        }
        --cmdCursor;
        text_command.setText(cmdHistory.get(cmdCursor));
    }

    private void cmdNext()
    {
        if (cmdCursor == cmdHistory.size())
        {
            return;
        }
        ++cmdCursor;
        text_command.setText(cmdCursor == cmdHistory.size()
                ? "" : cmdHistory.get(cmdCursor));
    }

    private void cmdRun()
    {
        String cmd = text_command.getText().trim();

        if ("".equals(cmd))
        {
            return;
        }

        text_command.setText("");
        textArea_output.append("> " + cmd + "\n");

        cmdHistory.add(cmd);
        cmdCursor = cmdHistory.size();

        try
        {
            Object res = engine.eval(cmd);
            if (res != null)
            {
                textArea_output.append(res.toString() + "\n");
            }
        } catch (ScriptException ex)
        {
            textArea_output.append(ex.getMessage() + "\n");
            //Logger.getLogger(ConsolePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            writeOut.flush();
            writeErr.flush();
        } catch (IOException ex) {
            Logger.getLogger(ConsolePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    private void switchToEngine(ScriptEngineFactory fact)
    {
        cmdHistory.clear();
        cmdCursor = 0;

        engine = fact.getScriptEngine();
        ScriptContext ctx = engine.getContext();
        ctx.setWriter(new PrintWriter(writeOut));
        ctx.setWriter(new PrintWriter(writeErr));

        engine.put("editor", editor);

        try {
            writeOut.flush();
            writeErr.flush();
        } catch (IOException ex) {
            Logger.getLogger(ConsolePanel.class.getName()).log(Level.SEVERE, null, ex);
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
        textArea_output = new javax.swing.JTextArea();
        text_command = new javax.swing.JTextField();
        bn_clear = new javax.swing.JButton();
        combo_language = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        check_lineWrap = new javax.swing.JCheckBox();

        textArea_output.setColumns(20);
        textArea_output.setEditable(false);
        textArea_output.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        textArea_output.setRows(5);
        jScrollPane1.setViewportView(textArea_output);

        text_command.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        text_command.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                text_commandKeyPressed(evt);
            }
        });

        bn_clear.setText("Clear");
        bn_clear.setToolTipText("Clear");
        bn_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bn_clearActionPerformed(evt);
            }
        });

        combo_language.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_languageActionPerformed(evt);
            }
        });

        jLabel1.setText("Language:");

        jLabel2.setText("Command Line:");

        check_lineWrap.setText("Line Wrap");
        check_lineWrap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                check_lineWrapActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                            .addComponent(text_command, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(bn_clear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(check_lineWrap)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 72, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(combo_language, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addContainerGap(312, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bn_clear)
                    .addComponent(combo_language, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(check_lineWrap))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(text_command, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void text_commandKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_text_commandKeyPressed
        switch (evt.getKeyCode())
        {
            case KeyEvent.VK_UP:
                cmdPrev();
                break;
            case KeyEvent.VK_DOWN:
                cmdNext();
                break;
            case KeyEvent.VK_ENTER:
                cmdRun();
                break;
        }
    }//GEN-LAST:event_text_commandKeyPressed

    private void bn_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bn_clearActionPerformed
        textArea_output.setText("");
    }//GEN-LAST:event_bn_clearActionPerformed

    private void combo_languageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_languageActionPerformed
        if (!init)
        {
            return;
        }

        ScriptEngineFactory fact = (ScriptEngineFactory)combo_language.getSelectedItem();
        switchToEngine(fact);
    }//GEN-LAST:event_combo_languageActionPerformed

    private void check_lineWrapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_check_lineWrapActionPerformed
        textArea_output.setLineWrap(check_lineWrap.isSelected());
    }//GEN-LAST:event_check_lineWrapActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bn_clear;
    private javax.swing.JCheckBox check_lineWrap;
    private javax.swing.JComboBox combo_language;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea textArea_output;
    private javax.swing.JTextField text_command;
    // End of variables declaration//GEN-END:variables
    // End of variables declaration


    class ScriptWriter extends Writer
    {
        StringBuilder sb;

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException
        {
            if (sb == null)
            {
                sb = new StringBuilder();
            }
            sb.append(cbuf, off, len);
        }

        @Override
        public void flush() throws IOException
        {
            if (sb == null)
            {
                return;
            }
            String text = sb.toString();
            textArea_output.append(text);
            if (text.charAt(text.length() - 1) != '\n')
            {
                textArea_output.append("\n");
            }
            sb = null;
        }

        @Override
        public void close() throws IOException
        {
        }
    }

    class FactoryRenderer extends JLabel implements ListCellRenderer
    {
        private static final long serialVersionUID = 1;

        public FactoryRenderer()
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

            ScriptEngineFactory fact = (ScriptEngineFactory)value;
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
            setText(fact == null ? ""
                    : fact.getLanguageName() + " (" + fact.getLanguageVersion() + ")");
            return this;
        }
    }

}
