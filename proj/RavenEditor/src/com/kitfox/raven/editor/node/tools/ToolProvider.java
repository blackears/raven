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

package com.kitfox.raven.editor.node.tools;

import com.kitfox.raven.editor.RavenEditor;
import java.awt.Component;
import java.net.URL;
import java.util.Properties;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;

/**
 *
 * @author kitfox
 */
abstract public class ToolProvider<ToolType extends Tool>
{
    private final String name;
    private final Icon icon;
    final URL helpTopic;

    static final ImageIcon DEFAULT_ICON = new ImageIcon(ToolProvider.class.getResource("/icons/tools/default.png"));

    public ToolProvider(String name)
    {
        this(name, (Icon)null, null);
    }

    public ToolProvider(String name, String icon)
    {
        this(name, new ImageIcon(ToolProvider.class.getResource(icon)), null);
    }

    public ToolProvider(String name, String icon, String helpTopic)
    {
        this(name, new ImageIcon(ToolProvider.class.getResource(icon)),
                ToolProvider.class.getResource(helpTopic));
    }

    public ToolProvider(String name, String icon, URL helpTopic)
    {
        this(name, new ImageIcon(ToolProvider.class.getResource(icon)), helpTopic);
    }

    public ToolProvider(String name, Icon icon, URL helpTopic)
    {
        this.name = name;
        this.icon = icon == null ? DEFAULT_ICON : icon;
        this.helpTopic = helpTopic;
    }


    abstract public ToolType create(ToolUser user);

    public JPopupMenu createPopup()
    {
        return null;
    }

    public Component createToolSettingsEditor(RavenEditor editor)
    {
        JLabel label = new JLabel("Tool settings not available", JLabel.CENTER);
        return label;
    }

    public void loadPreferences(Properties properties)
    {
    }

    public Properties savePreferences()
    {
        return new Properties();
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the icon
     */
    public Icon getIcon()
    {
        return icon;
    }

    public URL getHelpTopic()
    {
        return helpTopic;
    }
}
