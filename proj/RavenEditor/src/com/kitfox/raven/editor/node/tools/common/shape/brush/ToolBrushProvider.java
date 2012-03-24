/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.raven.editor.node.tools.common.shape.brush;

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
public class ToolBrushProvider extends ToolProvider<ToolBrush>
{
    private float strokeWidthMax = 4;
    public static final String PROP_STROKE_WIDTH_MAX = "strokeWidthMax";

    private float strokeWidthMin = 2;
    public static final String PROP_STROKE_WIDTH_MIN = "strokeWidthMin";

    private float strokeSpacing = .2f;
    public static final String PROP_STROKE_SPACING = "strokeSpacing";

    private float strokeSmoothing = 10;
    public static final String PROP_STROKE_SMOOTHING = "strokeSmoothing";

    private float vertexSmoothAngle = 10;
    public static final String PROP_VERTEX_SMOOTH_ANGLE = "vertexSmoothAngle";

    public ToolBrushProvider()
    {
        super("Brush", "/icons/tools/paintStroke.png", "/manual/tools/paintStroke.html");
    }

    @Override
    public void loadPreferences(Properties properties)
    {
        super.loadPreferences(properties);

        PropertiesData prop = new PropertiesData(properties);

        strokeWidthMax = prop.getFloat(PROP_STROKE_WIDTH_MAX, 4);
        strokeWidthMin = prop.getFloat(PROP_STROKE_WIDTH_MIN, 2);
        strokeSpacing = prop.getFloat(PROP_STROKE_SPACING, .2f);
        strokeSmoothing = prop.getFloat(PROP_STROKE_SMOOTHING, 10);
        vertexSmoothAngle = prop.getFloat(PROP_VERTEX_SMOOTH_ANGLE, 10);
//        editMode = prop.getEnum(PROP_EDITMODE, PenEditMode.EDIT);
    }

    @Override
    public Properties savePreferences()
    {
        Properties properties = new Properties();
        PropertiesData prop = new PropertiesData(properties);
        
        prop.setFloat(PROP_STROKE_WIDTH_MAX, strokeWidthMax);
        prop.setFloat(PROP_STROKE_WIDTH_MIN, strokeWidthMin);
        prop.setFloat(PROP_STROKE_SPACING, strokeSpacing);
        prop.setFloat(PROP_STROKE_SMOOTHING, strokeSmoothing);
        prop.setFloat(PROP_VERTEX_SMOOTH_ANGLE, vertexSmoothAngle);
//        prop.setEnum(PROP_EDITMODE, editMode);
        
        return properties;
    }

    @Override
    public ToolBrush create(ToolUser user)
    {
        return new ToolBrush(user, this);
    }

    @Override
    public Component createToolSettingsEditor(RavenEditor editor)
    {
        return new ToolBrushSettings(editor, this);
    }

    /**
     * @return the strokeWidthMax
     */
    public float getStrokeWidthMax()
    {
        return strokeWidthMax;
    }

    /**
     * @param strokeWidthMax the strokeWidthMax to set
     */
    public void setStrokeWidthMax(float strokeWidthMax)
    {
        this.strokeWidthMax = strokeWidthMax;
    }

    /**
     * @return the strokeWidthMin
     */
    public float getStrokeWidthMin()
    {
        return strokeWidthMin;
    }

    /**
     * @param strokeWidthMin the strokeWidthMin to set
     */
    public void setStrokeWidthMin(float strokeWidthMin)
    {
        this.strokeWidthMin = strokeWidthMin;
    }

    /**
     * @return the strokeSpacing
     */
    public float getStrokeSpacing()
    {
        return strokeSpacing;
    }

    /**
     * @param strokeSpacing the strokeSpacing to set
     */
    public void setStrokeSpacing(float strokeSpacing)
    {
        this.strokeSpacing = strokeSpacing;
    }

    /**
     * @return the strokeSmoothing
     */
    public float getStrokeSmoothing()
    {
        return strokeSmoothing;
    }

    /**
     * @param strokeSmoothing the strokeSmoothing to set
     */
    public void setStrokeSmoothing(float strokeSmoothing)
    {
        this.strokeSmoothing = strokeSmoothing;
    }

    /**
     * @return the vertexSmoothAngle
     */
    public float getVertexSmoothAngle()
    {
        return vertexSmoothAngle;
    }

    /**
     * @param vertexSmoothAngle the vertexSmoothAngle to set
     */
    public void setVertexSmoothAngle(float vertexSmoothAngle)
    {
        this.vertexSmoothAngle = vertexSmoothAngle;
    }
    
}
