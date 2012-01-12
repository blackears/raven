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
 * TextPanel.java
 *
 * Created on Jan 24, 2011, 7:19:09 PM
 */

package com.kitfox.raven.editor.view.text;

import com.kitfox.raven.editor.RavenDocument;
import com.kitfox.raven.editor.RavenDocumentEvent;
import com.kitfox.raven.editor.RavenDocumentListener;
import com.kitfox.raven.editor.RavenDocumentWeakListener;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.RavenEditorListener;
import com.kitfox.raven.editor.RavenEditorWeakListener;
import com.kitfox.raven.editor.node.scene.RavenNodeRoot;
import com.kitfox.raven.editor.node.tools.common.ServiceText;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.SelectionEvent;
import com.kitfox.raven.util.SelectionListener;
import com.kitfox.raven.util.SelectionSubEvent;
import com.kitfox.raven.util.SelectionWeakListener;
import com.kitfox.raven.util.text.TextPropertiesPanel;
import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.util.tree.SelectionRecord;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.EventObject;
import javax.swing.SwingUtilities;

/**
 *
 * @author kitfox
 */
public class TextPanel extends javax.swing.JPanel
        implements RavenEditorListener, RavenDocumentListener,
        PropertyChangeListener, SelectionListener
{
    RavenEditor editor;
    TextPropertiesPanel panel = new TextPropertiesPanel();

    private RavenEditorWeakListener edListener;
    private RavenDocumentWeakListener listenerRavenDoc;
    private SelectionWeakListener selListener;

    ArrayList<ServiceText> textRecords = new ArrayList<ServiceText>();

    boolean updating = true;

    /** Creates new form TextPanel */
    public TextPanel(RavenEditor editor)
    {
        this.editor = editor;
        initComponents();

        edListener = new RavenEditorWeakListener(this, editor);
        editor.addRavenEditorListener(edListener);

        panel.addPropertyChangeListener(this);
        add(panel, BorderLayout.CENTER);

        updateDocument();
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
            listenerRavenDoc = new RavenDocumentWeakListener(this, doc);
            doc.addRavenDocumentListener(listenerRavenDoc);
        }
        
        updateSymbol();
    }

    private void updateSymbol()
    {
        if (selListener != null)
        {
            selListener.remove();
            selListener = null;
        }

        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return;
        }

        NodeDocument root = doc.getCurDocument();
        Selection<SelectionRecord> sel = root.getSelection();
        selListener = new SelectionWeakListener(this, sel);
        sel.addSelectionListener(selListener);


        SwingUtilities.invokeLater(
            new Runnable() {
                @Override
                public void run()
                {
                    updateSwing();
                }
            }
        );
    }

    private void updateSwing()
    {
        RavenDocument doc = editor.getDocument();
        if (doc == null)
        {
            return;
        }

        NodeDocument root = doc.getCurDocument();
        Selection<SelectionRecord> sel = root.getSelection();

        textRecords.clear();
        for (SelectionRecord rec: sel.getSelection())
        {
            ServiceText text = rec.getNode().getNodeService(ServiceText.class, false);
            if (text != null)
            {
                textRecords.add(text);
            }
        }

        if (textRecords.indexOf(root) == -1)
        {
            //Make sure root is always in update list
            ServiceText service = root.getNodeService(ServiceText.class, false);
            textRecords.add(service);
        }

        updating = true;
        {
            ServiceText topText = textRecords.get(0);
            if (topText != null)
            {
                panel.setJustify(topText.getTextJustify());
                panel.setFontValue(topText.getTextFont());
            }
        }
        updating = false;
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

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (updating == true)
        {
            return;
        }

        if (TextPropertiesPanel.PROP_FONTVALUE.equals(evt.getPropertyName()))
        {
            for (ServiceText text: textRecords)
            {
                text.setTextFont(panel.getFontValue());
            }
        }
        else if (TextPropertiesPanel.PROP_JUSTIFY.equals(evt.getPropertyName()))
        {
            for (ServiceText text: textRecords)
            {
                text.setTextJustify(panel.getJustify());
            }
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
    public void selectionChanged(SelectionEvent evt)
    {
        updateSymbol();
    }

    @Override
    public void subselectionChanged(SelectionSubEvent evt)
    {
    }

    @Override
    public void documentSourceChanged(EventObject evt)
    {
    }

    @Override
    public void documentAdded(RavenDocumentEvent evt)
    {
    }

    @Override
    public void documentRemoved(RavenDocumentEvent evt)
    {
    }

    @Override
    public void currentDocumentChanged(RavenDocumentEvent evt)
    {
        updateSymbol();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
