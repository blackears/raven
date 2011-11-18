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
 * RavenFrame.java
 *
 * Created on Nov 12, 2010, 9:14:50 AM
 */

package com.kitfox.raven.editor;

import com.kitfox.docking.DockingContent;
import com.kitfox.docking.DockingPathRecord;
import com.kitfox.docking.DockingRegionRoot;
import com.kitfox.docking.DockingRegionTabbed;
import com.kitfox.docking.RestoreRecord;
import com.kitfox.raven.util.undo.History;
import com.kitfox.raven.util.undo.HistoryListener;
import com.kitfox.raven.util.undo.HistoryWeakListener;
import com.kitfox.xml.ns.raveneditorpreferences.MainFrameType;
import com.kitfox.xml.ns.raveneditorpreferences.ViewLayoutType;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.EventObject;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

/**
 *
 * @author kitfox
 */
public class RavenFrame extends javax.swing.JFrame
        implements RavenEditorListener,
        RavenViewManagerListener,
        RavenDocumentListener, HistoryListener
{
    private final RavenViewManager viewManager;

    RavenDocumentWeakListener projectListener;
    HistoryWeakListener historyListener;

    private DockingRegionRoot dockingRoot = new DockingRegionRoot();

    /** Creates new form RavenFrame */
    public RavenFrame(RavenViewManager viewManager)
    {
        this.viewManager = viewManager;
        initComponents();

        add(dockingRoot, BorderLayout.CENTER);

        viewManager.getEditor().addRavenEditorListener(this);
//        viewManager.addRavenViewManagerListener(this);
        refreshTitle();
//        refreshRecentFileListSwing();
//        refreshUndoHistory();

        rebuildMenus();
    }

    public DockingRegionRoot getDockingRoot()
    {
        return dockingRoot;
    }

    public void rebuildMenus()
    {
//        JMenuBar menu = new JMenuBar();
        menuBar.removeAll();
        viewManager.getMenuManager().buildMenu(menuBar);
//        setJMenuBar(menu);
        validate();
    }

    @Override
    public void historyChanged(EventObject evt)
    {
        refreshUndoHistory();
    }

    private void refreshUndoHistory()
    {
//        RavenDocument doc = viewManager.getEditor().getDocument();
//        if (doc == null)
//        {
//            cm_undo.setEnabled(false);
//            cm_redo.setEnabled(false);
//            return;
//        }
//
//        History history = doc.getRoot().getHistory();
//        cm_undo.setEnabled(history.canUndo());
//        cm_redo.setEnabled(history.canRedo());
    }

    public void setDefaultSize()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        setSize(screenSize.width * 3 / 4, screenSize.height * 3 / 4);
        setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2);
    }

    void setupFrame(MainFrameType mainFrame)
    {
        if (mainFrame.getMaximized())
        {
            setExtendedState(Frame.MAXIMIZED_BOTH);
        }
        else
        {
            setExtendedState(Frame.NORMAL);
            setBounds(mainFrame.getX(), mainFrame.getY(),
                    mainFrame.getWidth(), mainFrame.getHeight());
        }
    }

//    void setViewMenuItems(Collection<Dockable> values)
//    {
//        menu_view.removeAll();
//        for (Dockable dockable: values)
//        {
//            DockableMenuItem item = new DockableMenuItem(dockable);
//            menu_view.add(item);
//        }
//    }
//
//    void refreshViewMenuItemSelectedState()
//    {
//        for (int i = 0; i < menu_view.getItemCount(); ++i)
//        {
//            DockableMenuItem item = (DockableMenuItem)menu_view.getItem(i);
//            item.refreshSelected();
//        }
//    }
//
//    @Override
//    public void viewLayoutChanged(EventObject evt)
//    {
//        SwingUtilities.invokeLater(new Runnable()
//        {
//            @Override
//            public void run() {
//                refreshViewMenuItemSelectedState();
//            }
//        });
//    }

    MainFrameType exportPreferences()
    {
        MainFrameType type = new MainFrameType();

        type.setMaximized(getExtendedState() == Frame.MAXIMIZED_BOTH);
        type.setX(getX());
        type.setY(getY());
        type.setWidth(getWidth());
        type.setHeight(getHeight());

        return type;
    }

//    private void refreshRecentFileListSwing()
//    {
//        menu_openRecent.removeAll();
//
//        for (File file: viewManager.getEditor().getRecentFileList())
//        {
//            menu_openRecent.add(new RecentFileAction(file));
//        }
//    }

    public void refreshRecentFileList()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run() {
//                refreshRecentFileListSwing();
                rebuildMenus();
            }
        });
    }

    @Override
    public void recentFilesChanged(EventObject evt)
    {
        refreshRecentFileList();
    }


//    private void refreshLayoutList()
//    {
//        menu_layoutList.removeAll();
//
//        for (ViewLayoutType info: viewManager.getLayoutList())
//        {
//            menu_layoutList.add(new ViewLayoutAction(info));
//        }
//    }

    @Override
    public void layoutListChanged(EventObject evt)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run() {
//                refreshLayoutList();
                rebuildMenus();
            }
        });
    }

    @Override
    public void viewLayoutChanged(EventObject evt)
    {
//        SwingUtilities.invokeLater(new Runnable()
//        {
//            @Override
//            public void run() {
//                rebuildMenus();
//            }
//        });
    }

    @Override
    public void documentSourceChanged(EventObject evt)
    {
        refreshTitle();
    }

    @Override
    public void documentChanged(EventObject evt)
    {
        if (projectListener != null)
        {
            projectListener.remove();
            projectListener = null;

            historyListener.remove();
            historyListener = null;
        }

        RavenDocument proj = viewManager.getEditor().getDocument();

        if (proj != null)
        {
            projectListener = new RavenDocumentWeakListener(this, proj);
            proj.addRavenDocumentListener(this);

            History hist = proj.getRoot().getHistory();
            historyListener = new HistoryWeakListener(this, hist);
            hist.addHistoryListener(historyListener);
        }
        refreshTitle();
    }

    private void refreshTitle()
    {
        StringBuilder sb = new StringBuilder("Raven Editor");

        RavenDocument proj = viewManager.getEditor().getDocument();
        if (proj != null)
        {
            File file = proj.getSource();
            if (file == null)
            {
                sb.append(" - (New Project)");
            }
            else
            {
                sb.append(" - ").append(file.getAbsolutePath());
            }
        }

        setTitle(sb.toString());
    }

//    void setImportActions(ArrayList<ImportAction> actions)
//    {
//        menu_import.removeAll();
//        for (ImportAction action: actions)
//        {
//            menu_import.add(action);
//        }
//    }
//
//    void setExportActions(ArrayList<ExportAction> actions)
//    {
//        menu_export.removeAll();
//        for (ExportAction action: actions)
//        {
//            menu_export.add(action);
//        }
//    }
//
//    private void showWebpage(URI uri)
//    {
//        if (Desktop.isDesktopSupported())
//        {
//            Desktop desktop = Desktop.getDesktop();
//            try
//            {
//                desktop.browse(uri);
//            } catch (IOException ex)
//            {
//                Logger.getLogger(RavenFrame.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }

    public void exit()
    {
        this.setVisible(false);
        dispose();
    }

    public void showView(DockingContent dockable)
    {
        // Restore the dockable.
        if (dockable.getParent() != null)
        {
            //Already showing
            return;
        }

        RestoreRecord restoreRec = dockable.getRestoreRecord();
        if (restoreRec != null)
        {
            //Restore from minimize
            DockingPathRecord path = restoreRec.getPath();
            DockingRegionTabbed tab =
                    (DockingRegionTabbed)dockingRoot.getDockingRoot()
                    .getDockingChild(path);
            tab.restore(dockable, path.getLast());
        }
        else
        {
            //Open new panel
            dockingRoot.getDockingRoot().addDockContent(dockable);
        }
    }

    public void closeView(DockingContent dockable)
    {
        DockingRegionTabbed parent = dockable.getParent();
        if (parent != null)
        {
            parent.removeTab(dockable);
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

        menuBar = new javax.swing.JMenuBar();
        menu_file = new javax.swing.JMenu();
        cm_new = new javax.swing.JMenuItem();
        cm_open = new javax.swing.JMenuItem();
        cm_save = new javax.swing.JMenuItem();
        cm_saveAs = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menu_openRecent = new javax.swing.JMenu();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        menu_import = new javax.swing.JMenu();
        menu_export = new javax.swing.JMenu();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        cm_exit = new javax.swing.JMenuItem();
        menu_edit = new javax.swing.JMenu();
        cm_undo = new javax.swing.JMenuItem();
        cm_redo = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        cm_cut = new javax.swing.JMenuItem();
        cm_copy = new javax.swing.JMenuItem();
        cm_paste = new javax.swing.JMenuItem();
        cm_delete = new javax.swing.JMenuItem();
        menu_layout = new javax.swing.JMenu();
        cm_saveLayout = new javax.swing.JMenuItem();
        cm_manageLayouts = new javax.swing.JMenuItem();
        menu_layoutList = new javax.swing.JMenu();
        menu_view = new javax.swing.JMenu();
        menu_help = new javax.swing.JMenu();
        cm_helpContents = new javax.swing.JMenuItem();
        cm_helpAbout = new javax.swing.JMenuItem();
        cm_helpWebsite = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        menu_file.setMnemonic('f');
        menu_file.setText("File");

        cm_new.setMnemonic('n');
        cm_new.setText("New...");
        cm_new.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cm_newActionPerformed(evt);
            }
        });
        menu_file.add(cm_new);

        cm_open.setMnemonic('o');
        cm_open.setText("Open...");
        cm_open.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cm_openActionPerformed(evt);
            }
        });
        menu_file.add(cm_open);

        cm_save.setMnemonic('s');
        cm_save.setText("Save");
        cm_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cm_saveActionPerformed(evt);
            }
        });
        menu_file.add(cm_save);

        cm_saveAs.setMnemonic('a');
        cm_saveAs.setText("Save As...");
        cm_saveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cm_saveAsActionPerformed(evt);
            }
        });
        menu_file.add(cm_saveAs);
        menu_file.add(jSeparator1);

        menu_openRecent.setText("Open Recent");
        menu_file.add(menu_openRecent);
        menu_file.add(jSeparator4);

        menu_import.setText("Import");
        menu_file.add(menu_import);

        menu_export.setText("Export");
        menu_file.add(menu_export);
        menu_file.add(jSeparator2);

        cm_exit.setMnemonic('x');
        cm_exit.setText("Exit");
        cm_exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cm_exitActionPerformed(evt);
            }
        });
        menu_file.add(cm_exit);

        menuBar.add(menu_file);

        menu_edit.setMnemonic('e');

        cm_undo.setText("Undo");
        cm_undo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cm_undoActionPerformed(evt);
            }
        });
        menu_edit.add(cm_undo);

        cm_redo.setText("Redo");
        cm_redo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cm_redoActionPerformed(evt);
            }
        });
        menu_edit.add(cm_redo);
        menu_edit.add(jSeparator3);

        cm_cut.setText("Cut");
        menu_edit.add(cm_cut);

        cm_copy.setText("Copy");
        menu_edit.add(cm_copy);

        cm_paste.setText("Paste");
        menu_edit.add(cm_paste);

        cm_delete.setText("Delete");
        menu_edit.add(cm_delete);

        menuBar.add(menu_edit);

        menu_layout.setText("Layout");

        cm_saveLayout.setMnemonic('s');
        cm_saveLayout.setText("Save Layout...");
        cm_saveLayout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cm_saveLayoutActionPerformed(evt);
            }
        });
        menu_layout.add(cm_saveLayout);

        cm_manageLayouts.setMnemonic('m');
        cm_manageLayouts.setText("Manage Layouts...");
        cm_manageLayouts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cm_manageLayoutsActionPerformed(evt);
            }
        });
        menu_layout.add(cm_manageLayouts);

        menu_layoutList.setMnemonic('l');
        menu_layoutList.setText("Layouts");
        menu_layout.add(menu_layoutList);

        menuBar.add(menu_layout);

        menu_view.setMnemonic('v');
        menu_view.setText("View");
        menuBar.add(menu_view);

        menu_help.setMnemonic('h');
        menu_help.setText("Help");

        cm_helpContents.setText("Contents...");
        cm_helpContents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cm_helpContentsActionPerformed(evt);
            }
        });
        menu_help.add(cm_helpContents);

        cm_helpAbout.setText("About...");
        cm_helpAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cm_helpAboutActionPerformed(evt);
            }
        });
        menu_help.add(cm_helpAbout);

        cm_helpWebsite.setText("Website...");
        cm_helpWebsite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cm_helpWebsiteActionPerformed(evt);
            }
        });
        menu_help.add(cm_helpWebsite);

        menuBar.add(menu_help);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cm_newActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cm_newActionPerformed
//        viewManager.getEditor().getDocumentIOHelper().newFile();
    }//GEN-LAST:event_cm_newActionPerformed

    private void cm_openActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cm_openActionPerformed
//        viewManager.getEditor().getDocumentIOHelper().openFile();
    }//GEN-LAST:event_cm_openActionPerformed

    private void cm_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cm_saveActionPerformed
//        viewManager.getEditor().getDocumentIOHelper().saveFile();
}//GEN-LAST:event_cm_saveActionPerformed

    private void cm_saveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cm_saveAsActionPerformed
//        viewManager.getEditor().getDocumentIOHelper().saveAsFile();
    }//GEN-LAST:event_cm_saveAsActionPerformed

    private void cm_exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cm_exitActionPerformed
//        this.setVisible(false);
//        dispose();
    }//GEN-LAST:event_cm_exitActionPerformed

    private void cm_saveLayoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cm_saveLayoutActionPerformed
//        String name = JOptionPane.showInputDialog(this, "Save Layout As");
//
//        if (name == null)
//        {
//            return;
//        }
//
//        viewManager.saveLayout(name);
    }//GEN-LAST:event_cm_saveLayoutActionPerformed

    private void cm_manageLayoutsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cm_manageLayoutsActionPerformed
//        JDialog dlg = new JDialog(this, "Manage Layouts", true);
//        dlg.getContentPane().add(new LayoutManagerPanel(viewManager));
//        dlg.pack();
//
//        dlg.setLocation(getX() + (getWidth() - dlg.getWidth()) / 2,
//                getY() + (getHeight() - dlg.getHeight()) / 2);
//
//        dlg.setVisible(true);
    }//GEN-LAST:event_cm_manageLayoutsActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        viewManager.getEditor().savePreferences();
    }//GEN-LAST:event_formWindowClosing

    private void cm_undoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cm_undoActionPerformed
//        RavenDocument doc = viewManager.getEditor().getDocument();
//        History hist = doc.getRoot().getHistory();
//        hist.undo();
    }//GEN-LAST:event_cm_undoActionPerformed

    private void cm_redoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cm_redoActionPerformed
//        RavenDocument doc = viewManager.getEditor().getDocument();
//        History hist = doc.getRoot().getHistory();
//        hist.redo();
    }//GEN-LAST:event_cm_redoActionPerformed

    private void cm_helpAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cm_helpAboutActionPerformed
//        RavenAboutDialog dlg = new RavenAboutDialog(this);
//
//        dlg.pack();
//        RavenSwingUtil.centerWindow(dlg, getBounds());
//
//        dlg.setVisible(true);

    }//GEN-LAST:event_cm_helpAboutActionPerformed

    private void cm_helpContentsActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cm_helpContentsActionPerformed
    {//GEN-HEADEREND:event_cm_helpContentsActionPerformed
//        try
//        {
//            showWebpage(new URI("http://www.kitfox.com"));
//        } catch (URISyntaxException ex)
//        {
//            Logger.getLogger(RavenFrame.class.getName()).log(Level.SEVERE, null, ex);
//        }

    }//GEN-LAST:event_cm_helpContentsActionPerformed

    private void cm_helpWebsiteActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cm_helpWebsiteActionPerformed
    {//GEN-HEADEREND:event_cm_helpWebsiteActionPerformed
//        try
//        {
//            showWebpage(new URI("http://www.kitfox.com"));
//        } catch (URISyntaxException ex)
//        {
//            Logger.getLogger(RavenFrame.class.getName()).log(Level.SEVERE, null, ex);
//        }

    }//GEN-LAST:event_cm_helpWebsiteActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem cm_copy;
    private javax.swing.JMenuItem cm_cut;
    private javax.swing.JMenuItem cm_delete;
    private javax.swing.JMenuItem cm_exit;
    private javax.swing.JMenuItem cm_helpAbout;
    private javax.swing.JMenuItem cm_helpContents;
    private javax.swing.JMenuItem cm_helpWebsite;
    private javax.swing.JMenuItem cm_manageLayouts;
    private javax.swing.JMenuItem cm_new;
    private javax.swing.JMenuItem cm_open;
    private javax.swing.JMenuItem cm_paste;
    private javax.swing.JMenuItem cm_redo;
    private javax.swing.JMenuItem cm_save;
    private javax.swing.JMenuItem cm_saveAs;
    private javax.swing.JMenuItem cm_saveLayout;
    private javax.swing.JMenuItem cm_undo;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menu_edit;
    private javax.swing.JMenu menu_export;
    private javax.swing.JMenu menu_file;
    private javax.swing.JMenu menu_help;
    private javax.swing.JMenu menu_import;
    private javax.swing.JMenu menu_layout;
    private javax.swing.JMenu menu_layoutList;
    private javax.swing.JMenu menu_openRecent;
    private javax.swing.JMenu menu_view;
    // End of variables declaration//GEN-END:variables
    // End of variables declaration

    //---------------------------------------------

    class RecentFileAction extends AbstractAction
    {
        File file;

        public RecentFileAction(File file)
        {
            super(file.getAbsolutePath());
            this.file = file;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            viewManager.getEditor().open(file);
        }
    }

    class ViewLayoutAction extends AbstractAction
    {
        ViewLayoutType info;

        public ViewLayoutAction(ViewLayoutType info)
        {
            super(info.getName());
            this.info = info;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            viewManager.layoutViews(info);
        }
    }

//	private class DockableMenuItem extends JCheckBoxMenuItem
//            implements ItemListener, PropertyChangeListener
//	{
//		private DockingContent dockable;
//
//		public DockableMenuItem(DockingContent dockable)
//		{
//			super(dockable.getTitle());
//
//			setSelected(dockable.getParent() != null);
//
//			dockable.addPropertyChangeListener(this);
//			addItemListener(this);
//
//			this.dockable = dockable;
//        }
//
//        @Override
//		public void itemStateChanged(ItemEvent itemEvent)
//		{
//			if (itemEvent.getStateChange() == ItemEvent.DESELECTED)
//			{
//				// Close the dockable.
//				dockable.getParent().removeTab(dockable);
//			}
//			else
//			{
//				// Restore the dockable.
//                DockingPathRecord path = dockable.getRestoreRecord().getPath();
//                DockingRegionTabbed tab =
//                        (DockingRegionTabbed)getDockingRoot().getDockingRoot()
//                        .getDockingChild(path);
//                tab.restore(dockable, path.getLast());
//			}
//		}
//
//        @Override
//        public void propertyChange(PropertyChangeEvent evt)
//        {
//            setSelected(dockable.getParent() != null);
//        }
//
//	}


}
