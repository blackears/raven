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
 * StringCustomEditor.java
 *
 * Created on Jul 9, 2009, 1:33:25 PM
 */

package com.kitfox.raven.util.tree.property;

import com.kitfox.raven.util.tree.NodeSymbol;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.NodeVisitor;
import com.kitfox.raven.util.tree.PropertyCustomEditor;
import com.kitfox.raven.util.tree.PropertyData;
import com.kitfox.raven.util.tree.PropertyDataReference;
import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class NodeObjectCustomEditor extends javax.swing.JPanel
        implements PropertyCustomEditor, PropertyChangeListener
{
    private static final long serialVersionUID = 1;

    final NodeObjectEditor editor;
//    boolean updating;

    PropertyData<NodeObject> initValue;
    PropertyData<NodeObject> curValue;

    NodeObjectPanel nodeObjectPanel = new NodeObjectPanel();
    boolean updating;

    /** Creates new form StringCustomEditor */
    public NodeObjectCustomEditor(NodeObjectEditor editor)
    {
        initComponents();
        this.editor = editor;
        curValue = initValue = editor.getValue();

//        combo_nodes.setRenderer(new CellRenderer());
        add(nodeObjectPanel, BorderLayout.CENTER);
        nodeObjectPanel.addPropertyChangeListener(this);

        updateFromEditorSwing();
    }

    private void updateFromEditorSwing()
    {
        updating = true;

        FindNodes find = new FindNodes();
        NodeSymbol doc = editor.getWrapper().getNode().getSymbol();
        doc.visit(find);

        nodeObjectPanel.setNodes(find.getList());
        nodeObjectPanel.setNode(editor.getValueFlat());

        updating = false;
    }

    private void setNode(NodeObject node)
    {
        curValue = node == null ? null
                : new PropertyDataReference<NodeObject>(node.getUid());
        editor.setValue(curValue, false);
    }

    @Override
    public Component getCustomEditor()
    {
        return this;
    }

    @Override
    public void customEditorCommit()
    {
        editor.setValue(curValue);
    }

    @Override
    public void customEditorCancel()
    {
        editor.setValue(initValue, false);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (updating)
        {
            return;
        }

        setNode(nodeObjectPanel.getSelectedNode());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    class FindNodes implements NodeVisitor
    {
        private ArrayList<NodeObject> list = new ArrayList<NodeObject>();

        @Override
        public void visit(NodeObject node)
        {
            if (editor.getWrapper().getPropertyType().isAssignableFrom(
                    node.getClass()))
            {
                list.add(node);
            }
        }

        /**
         * @return the list
         */
        public ArrayList<NodeObject> getList() {
            return list;
        }
    }

}
