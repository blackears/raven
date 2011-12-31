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

package com.kitfox.raven.editor.view;

import com.kitfox.raven.editor.RavenEditor;
import java.awt.Component;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Properties;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author kitfox
 */
abstract public class ViewProvider
{
    private final String name;
    private final Icon icon;

    static final ImageIcon DEFAULT_ICON = new ImageIcon(ViewProvider.class.getResource("/icons/view/eye.png"));

    ArrayList<ViewProviderListener> listeners = new ArrayList<ViewProviderListener>();
    private Properties preferences = new Properties();

    public ViewProvider(String name)
    {
        this(name, DEFAULT_ICON);
    }

    public ViewProvider(String name, String iconPath)
    {
        this(name, loadIcon(iconPath));
    }

    public ViewProvider(String name, Icon icon)
    {
        this.name = name;
        this.icon = icon;
    }

    public void addViewProviderListener(ViewProviderListener l)
    {
        listeners.add(l);
    }

    public void removeViewProviderListener(ViewProviderListener l)
    {
        listeners.remove(l);
    }

    protected void firePreferencesLoaded()
    {
        EventObject evt = new EventObject(this);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).viewProviderPreferencesChanged(evt);
        }
    }

    public void loadPreferences(Properties properties)
    {
        this.preferences = properties;
        firePreferencesLoaded();

    }

    public Properties savePreferences()
    {
        return preferences;
    }

    /**
     * @return the preferences
     */
    public Properties getPreferences()
    {
        return preferences;
    }

    private static ImageIcon loadIcon(String path)
    {
        return new ImageIcon(ViewProvider.class.getResource(path));
    }

    public String getID()
    {
        return getClass().getCanonicalName();
    }

    public Icon getIcon()
    {
        return icon;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    abstract public Component createComponent(RavenEditor editor);

    /**
     * Array of actions sorted into rows and columns.  These will appear
     * to the left of the Min/Max/Close window decorations.
     *
     * Null if no actions provided.
     */
    public Action[][] getActions()
    {
        return null;
    }

}
