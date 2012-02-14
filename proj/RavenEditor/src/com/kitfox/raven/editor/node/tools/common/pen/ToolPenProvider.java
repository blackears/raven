/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.raven.editor.node.tools.common.pen;

import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.node.tools.ToolProvider;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.util.PropertiesData;
import com.kitfox.raven.util.service.ServiceInst;
import java.awt.Component;
import java.util.Properties;

/**
 *
 * @author kitfox
 */
@ServiceInst(service=ToolProvider.class)
public class ToolPenProvider extends ToolProvider<ToolPenDispatch>
{
    private PenEditMode editMode = PenEditMode.EDIT;
    public static final String PROP_EDITMODE = "editMode";

    public ToolPenProvider()
    {
        super("Pen2", "/icons/tools/pen.png", "/manual/tools/pen.html");
    }

    @Override
    public void loadPreferences(Properties properties)
    {
        super.loadPreferences(properties);

        PropertiesData prop = new PropertiesData(properties);
        editMode = prop.getEnum(PROP_EDITMODE, PenEditMode.EDIT);
    }

    @Override
    public Properties savePreferences()
    {
        Properties properties = new Properties();
        PropertiesData prop = new PropertiesData(properties);
        
        prop.setEnum(PROP_EDITMODE, editMode);
        
        return properties;
    }

    @Override
    public ToolPenDispatch create(ToolUser user)
    {
        return new ToolPenDispatch(user, this);
    }

    @Override
    public Component createToolSettingsEditor(RavenEditor editor)
    {
        return new ToolPenSettings(editor, this);
    }

    /**
     * @return the editMode
     */
    public PenEditMode getEditMode()
    {
        return editMode;
    }

    /**
     * @param editMode the editMode to set
     */
    public void setEditMode(PenEditMode editMode)
    {
        this.editMode = editMode;
    }
    
}
