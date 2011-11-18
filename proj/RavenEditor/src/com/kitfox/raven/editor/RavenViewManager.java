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

package com.kitfox.raven.editor;

import com.kitfox.docking.DockingChild;
import com.kitfox.docking.DockingContent;
import com.kitfox.docking.DockingPathRecord;
import com.kitfox.docking.DockingRegionContainer;
import com.kitfox.docking.DockingRegionSplit;
import com.kitfox.docking.DockingRegionTabbed;
import com.kitfox.docking.DockingRegionWindow;
import com.kitfox.docking.RestoreRecord;
import com.kitfox.raven.editor.view.ViewProvider;
import com.kitfox.raven.editor.action.ActionManager;
import com.kitfox.raven.editor.action.ActionManagerListener;
import com.kitfox.raven.editor.menu.MenuManager;
import com.kitfox.raven.editor.view.ViewProviderIndex;
import com.kitfox.xml.ns.raveneditorpreferences.MainFrameType;
import com.kitfox.xml.ns.raveneditorpreferences.PropertiesSetType;
import com.kitfox.xml.ns.raveneditorpreferences.PropertiesType;
import com.kitfox.xml.ns.raveneditorpreferences.PropertyType;
import com.kitfox.xml.ns.raveneditorpreferences.RavenEditorPreferencesType;
import com.kitfox.xml.ns.raveneditorpreferences.ViewDockSplitType;
import com.kitfox.xml.ns.raveneditorpreferences.ViewDockTabbedType;
import com.kitfox.xml.ns.raveneditorpreferences.ViewDockType;
import com.kitfox.xml.ns.raveneditorpreferences.ViewDockableContentType;
import com.kitfox.xml.ns.raveneditorpreferences.ViewLayoutType;
import com.kitfox.xml.ns.raveneditorpreferences.ViewWindowType;
import com.kitfox.xml.ns.raveneditorpreferences.ViewsType;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Properties;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author kitfox
 */
public class RavenViewManager
        implements ActionManagerListener
{
    private final RavenEditor editor;

    RavenFrame frame;
    private final ActionManager actionManager = new ActionManager();
    private final MenuManager menuManager = new MenuManager();

    static final String frameId = "frame0";
    HashMap<String, DockingContent> dockablesMap =
            new HashMap<String, DockingContent>();


    HashMap<String, ViewLayoutType> layoutInfoMap = new HashMap<String, ViewLayoutType>();

    ArrayList<RavenViewManagerListener> listeners = new ArrayList<RavenViewManagerListener>();

    public RavenViewManager(RavenEditor editor)
    {
        this.editor = editor;

        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                initSwing();
            }
        });
    }

    public ArrayList<ViewLayoutType> getLayoutList()
    {
        return new ArrayList<ViewLayoutType>(layoutInfoMap.values());
    }

    public void addRavenViewManagerListener(RavenViewManagerListener l)
    {
        listeners.add(l);
    }

    public void removeRavenViewManagerListener(RavenViewManagerListener l)
    {
        listeners.remove(l);
    }

    private void fireLayoutListChanged()
    {
        EventObject evt = new EventObject(this);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).layoutListChanged(evt);
        }
    }

    private void fireViewLayoutChanged()
    {
        EventObject evt = new EventObject(this);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).viewLayoutChanged(evt);
        }
    }

    private void initSwing()
    {
        frame = new RavenFrame(this);
        frame.setDefaultSize();

        
        //Create views
        for (ViewProvider provider: ViewProviderIndex.inst().getProviders())
        {
    		DockingContent dockable = new DockingContent(
                    provider.getClass().getCanonicalName(),
                    provider.getName(),
                    provider.getIcon(),
                    provider.createComponent(editor));

            dockablesMap.put(provider.getClass().getCanonicalName(), dockable);
        }

        frame.rebuildMenus();

        frame.setDefaultSize();

        actionManager.addActionManagerListener(this);
        buildHotkeys();
    }

    public ArrayList<DockableMenuItem> getViewMenuItems()
    {
        ArrayList<DockableMenuItem> list = new ArrayList<DockableMenuItem>();
        for (DockingContent dock: dockablesMap.values())
        {
            list.add(new DockableMenuItem(dock));
        }
        return list;
    }

    public <T extends ViewProvider> void showView(Class<T> cls)
    {
        DockingContent dockable = dockablesMap.get(cls.getCanonicalName());
        frame.showView(dockable);
    }

    public <T extends ViewProvider> void hideView(Class<T> cls)
    {
        DockingContent dockable = dockablesMap.get(cls.getCanonicalName());
        frame.closeView(dockable);
    }

    private DockingChild layoutView(ViewDockType dockType)
    {
        if (dockType instanceof ViewDockTabbedType)
        {
            DockingRegionTabbed region = new DockingRegionTabbed();

            ViewDockTabbedType tabbedType = (ViewDockTabbedType)dockType;
            for (ViewDockableContentType contType: tabbedType.getDockables())
            {
                String className = contType.getClazz();
                DockingContent cont = dockablesMap.get(className);
                if (cont == null)
                {
                    System.err.println("Could not load docking content for " + className);
                    continue;
                }
                region.addTab(cont);
            }
            return region;
        }
        else if (dockType instanceof ViewDockSplitType)
        {
            ViewDockSplitType splitType = (ViewDockSplitType)dockType;
            DockingChild left = layoutView(splitType.getLeft());
            DockingChild right = layoutView(splitType.getRight());

            DockingRegionSplit region = new DockingRegionSplit(left, right);
            region.setDividerLocation(splitType.getDivider());
            region.setOrientation(splitType.getVertical()
                    ? JSplitPane.VERTICAL_SPLIT
                    : JSplitPane.HORIZONTAL_SPLIT
                    );

            return region;
        }

        throw new UnsupportedOperationException();
    }

    public void layoutViews(final ViewLayoutType layoutProperties)
    {
        //Close all existing views
        frame.getDockingRoot().closeAll();

        //Restore views
        {
            DockingChild root = layoutView(layoutProperties.getRoot());
            DockingRegionContainer cont = frame.getDockingRoot()
                    .getDockingRoot().getContainerRoot();

            cont.setRoot(root);
        }

        for (ViewWindowType winType: layoutProperties.getWindow())
        {
            DockingChild root = layoutView(winType.getRoot());
            if (root instanceof DockingRegionTabbed
                    && ((DockingRegionTabbed)root).getTabCount() == 0)
            {
                //Skip empty windows.  Not sure why these are being created in
                // the first place.
                continue;
            }

            DockingRegionWindow win = frame.getDockingRoot().createFloatingWindow();
            win.setBounds(winType.getX(), winType.getY(), winType.getWidth(), winType.getHeight());

            DockingRegionContainer cont = win.getRoot().getContainerRoot();
            cont.setRoot(root);
        }

        fireViewLayoutChanged();
    }

    public void show()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                frame.setVisible(true);
            }
        });
    }

    /**
     * @return the editor
     */
    public RavenEditor getEditor() {
        return editor;
    }

    protected void loadPreferences(final RavenEditorPreferencesType views)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                loadPreferencesSwing(views);
            }
        });
    }

    private void loadPreferencesSwing(RavenEditorPreferencesType pref)
    {
        layoutInfoMap.clear();

        if (pref == null)
        {
            layoutViews(null);
            fireLayoutListChanged();
            return;
        }

        //Load settings
        PropertiesSetType propViews = pref.getViewProperties();
        if (propViews != null)
        {
            for (PropertiesType prop: propViews.getProperties())
            {
                Properties props = new Properties();
                for (PropertyType type: prop.getProperty())
                {
                    props.setProperty(type.getName(), type.getValue());
                }

                ViewProvider prov = ViewProviderIndex.inst().getProvider(prop.getClazz());
                if (prov != null)
                {
                    prov.loadPreferences(props);
                }
                else
                {
                    System.err.println("Problem loading view: Could not find loader for " + prop.getClazz());
//                    throw new UnsupportedOperationException("Could not find property loader for " + prop.getClazz());
                }
            }
        }

        ViewsType views = pref.getViews();

        //Position frame
        MainFrameType mainFrame = views.getMainFrame();
        frame.setupFrame(mainFrame);

        //Load layouts
        for (ViewLayoutType layout: views.getViewLayout())
        {
            layoutInfoMap.put(layout.getName(), layout);
        }
        fireLayoutListChanged();

        layoutViews(views.getWorkingLayout());
    }

    void exportViewLayout(RavenEditorPreferencesType pref)
    {
        {
            ViewsType views = new ViewsType();
            pref.setViews(views);

            views.setMainFrame(frame.exportPreferences());

            for (ViewLayoutType info: layoutInfoMap.values())
            {
                views.getViewLayout().add(info);
            }
            views.setWorkingLayout(exportLayout());
        }

        {
            PropertiesSetType propViews = new PropertiesSetType();
            pref.setViewProperties(propViews);

            for (ViewProvider prov: ViewProviderIndex.inst().getProviders())
            {
                PropertiesType propsType = new PropertiesType();
                propsType.setClazz(prov.getClass().getCanonicalName().replace('$', '.'));
                propViews.getProperties().add(propsType);

                Properties saveProp = prov.savePreferences();
                for (String name: saveProp.stringPropertyNames())
                {
                    PropertyType prop = new PropertyType();
                    propsType.getProperty().add(prop);

                    prop.setName(name);
                    prop.setValue(saveProp.getProperty(name));
                }
            }
        }
    }

    private ViewDockType exportLayout(DockingChild child)
    {
        if (child instanceof DockingRegionTabbed)
        {
            DockingRegionTabbed region = (DockingRegionTabbed)child;

            ViewDockTabbedType tabbedType = new ViewDockTabbedType();
            for (int i = 0; i < region.getTabCount(); ++i)
            {
                DockingContent cont = region.getDockingContent(i);
                ViewDockableContentType dockType = new ViewDockableContentType();
                tabbedType.getDockables().add(dockType);
                dockType.setClazz(cont.getUid());
            }
            return tabbedType;
        }

        if (child instanceof DockingRegionSplit)
        {
            DockingRegionSplit region = (DockingRegionSplit)child;

            ViewDockType left = exportLayout(region.getLeft());
            ViewDockType right = exportLayout(region.getRight());

            ViewDockSplitType splitType = new ViewDockSplitType();
            splitType.setLeft(left);
            splitType.setRight(right);
            splitType.setDivider(region.getDividerLocation());
            splitType.setVertical(region.getOrientation() == JSplitPane.VERTICAL_SPLIT);

            return splitType;
        }

        throw new UnsupportedOperationException();
    }

    ViewLayoutType exportLayout()
    {
        ViewLayoutType layoutType = new ViewLayoutType();

        {
            DockingChild child = frame.getDockingRoot().getDockingRoot().getRoot();
            ViewDockType root = exportLayout(child);
            layoutType.setRoot(root);
        }

        for (int i = 0; i < frame.getDockingRoot().getNumFloatingWindows(); ++i)
        {
            DockingRegionWindow win = frame.getDockingRoot().getFloatingWindow(i);

            ViewWindowType winType = new ViewWindowType();
            winType.setX(win.getX());
            winType.setY(win.getY());
            winType.setWidth(win.getWidth());
            winType.setHeight(win.getHeight());
            winType.setRoot(exportLayout(win.getRoot().getRoot()));

            layoutType.getWindow().add(winType);
        }

        return layoutType;
    }

    public void saveLayout(String name)
    {
        ViewLayoutType layout = exportLayout();
        layout.setName(name);
        layoutInfoMap.put(name, layout);
        fireLayoutListChanged();
    }

    public void removeLayout(String name)
    {
        layoutInfoMap.remove(name);
        fireLayoutListChanged();
    }

    public Window getSwingRoot()
    {
        return frame;
    }

    public void buildHotkeys()
    {
//        actionManager.buildInputs(maximizer.getInputMap());
//        actionManager.buildActions(maximizer.getActionMap());
    }

    @Override
    public void hotkeyLayoutChanged(EventObject evt)
    {
        buildHotkeys();
    }

    @Override
    public void hotkeyActionsChanged(EventObject evt)
    {
        
    }

    /**
     * @return the actionManager
     */
    public ActionManager getActionManager()
    {
        return actionManager;
    }

    public void exit()
    {
        frame.exit();
    }

    /**
     * @return the menuManager
     */
    public MenuManager getMenuManager()
    {
        return menuManager;
    }

    //-------------------------------------------

	public class DockableMenuItem extends JCheckBoxMenuItem
            implements ItemListener, PropertyChangeListener
	{
		private DockingContent dockable;
        private boolean updating;

		public DockableMenuItem(DockingContent dockable)
		{
			super(dockable.getTitle());

			setSelected(dockable.getParent() != null);

			dockable.addPropertyChangeListener(this);
			addItemListener(this);

			this.dockable = dockable;
        }

        @Override
		public void itemStateChanged(ItemEvent itemEvent)
		{
            if (updating)
            {
                return;
            }

			if (itemEvent.getStateChange() == ItemEvent.DESELECTED)
			{
				// Close the dockable.
				dockable.getParent().removeTab(dockable);
			}
			else
			{
				// Restore the dockable.
                RestoreRecord rec = dockable.getRestoreRecord();
                if (rec != null)
                {
                    DockingPathRecord path = rec.getPath();
                    DockingRegionTabbed tab =
                            (DockingRegionTabbed)frame.getDockingRoot().getDockingRoot()
                            .getDockingChild(path);
                    tab.restore(dockable, path.getLast());
                }
                else
                {
                    frame.getDockingRoot().getDockingRoot().addDockContent(dockable);
                }
			}
		}

        @Override
        public void propertyChange(PropertyChangeEvent evt)
        {
            updating = true;

            setSelected(dockable.getParent() != null);

            updating = false;
        }

	}

}
