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

import com.kitfox.raven.editor.node.tools.ToolPalette;
import com.kitfox.raven.editor.node.tools.ToolProvider;
import com.kitfox.raven.editor.node.tools.ToolProviderIndex;
import com.kitfox.xml.ns.raveneditorpreferences.PropertiesSetType;
import com.kitfox.xml.ns.raveneditorpreferences.PropertiesType;
import com.kitfox.xml.ns.raveneditorpreferences.PropertyType;
import com.kitfox.xml.ns.raveneditorpreferences.RavenEditorPreferencesType;
import java.util.Properties;
import javax.swing.SwingUtilities;

/**
 *
 * @author kitfox
 */
public class RavenToolManager
{
    private final RavenEditor editor;
    private final ToolPalette palette = new ToolPalette();

    public RavenToolManager(RavenEditor editor)
    {
        this.editor = editor;


        for (ToolProvider prov:
            ToolProviderIndex.inst().getProviders())
        {
            palette.addProvider(prov);
        }
    }

    /**
     * @return the palette
     */
    public ToolPalette getPalette() {
        return palette;
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
        if (pref == null)
        {
            return;
        }

        //Load settings
        PropertiesSetType propTools = pref.getToolProperties();
        if (propTools != null)
        {
            for (PropertiesType prop: propTools.getProperties())
            {
                Properties props = new Properties();
                for (PropertyType type: prop.getProperty())
                {
                    props.setProperty(type.getName(), type.getValue());
                }

                ToolProvider prov =
                        ToolProviderIndex.inst().getProvider(prop.getClazz());
                if (prov != null)
                {
                    prov.loadPreferences(props);
                }
            }
        }
    }

    void export(RavenEditorPreferencesType pref)
    {
        PropertiesSetType propTools = new PropertiesSetType();
        pref.setToolProperties(propTools);

        for (ToolProvider prov: ToolProviderIndex.inst().getProviders())
        {
            PropertiesType propsType = new PropertiesType();
            propsType.setClazz(prov.getClass().getCanonicalName().replace('$', '.'));
            propTools.getProperties().add(propsType);

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
