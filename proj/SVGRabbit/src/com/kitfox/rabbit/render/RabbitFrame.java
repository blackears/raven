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

package com.kitfox.rabbit.render;

import com.kitfox.rabbit.nodes.RaElement;
import com.kitfox.rabbit.style.FillRule;
import com.kitfox.rabbit.font.FontStyle;
import com.kitfox.rabbit.font.FontVariant;
import com.kitfox.rabbit.font.FontWeight;
import com.kitfox.rabbit.style.StrokeLineCap;
import com.kitfox.rabbit.style.StrokeLineJoin;
import com.kitfox.rabbit.style.StyleColor;
import com.kitfox.rabbit.style.StylePaint;
import com.kitfox.rabbit.text.TextAlign;
import com.kitfox.rabbit.style.Visibility;
import com.kitfox.rabbit.text.Direction;
import com.kitfox.rabbit.text.TextAnchor;
import com.kitfox.rabbit.text.WritingMode;
import com.kitfox.rabbit.types.RaLength;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.AffineTransform;

/**
 *
 * @author kitfox
 */
public class RabbitFrame
{
    private RaElement source;
    private Surface2D surface;
    private RabbitFrame last;

    private AffineTransform xform;
    private Visibility visibility;
    private float opacity;

    private float fillOpacity;
    private StylePaint fillPaint;
    private FillRule fillRule;

    private float strokeOpacity;
    private StylePaint strokePaint;
    private RaLength strokeWidth;
    private StrokeLineCap strokeLineCap;
    private StrokeLineJoin strokeLineJoin;
    private float strokeMiterLimit;
    private float[] strokeDashArray;
    private float strokeDashOffset;

    private String[] fontFamily;
    private RaLength fontSize;
    private FontStyle fontStyle;
    private FontVariant fontVariant;
    private FontWeight fontWeight;

    private TextAlign textAlign;
    private TextAnchor textAnchor;
    private Direction direction;
    private WritingMode writingMode;

    private Color stopColor;
    private float stopOpacity;

    BasicStroke stroke;

    public RabbitFrame(Surface2D surf)
    {
        this.surface = surf;

        //Defaults
        xform = new AffineTransform();
        visibility = Visibility.VISIBLE;
        opacity = 1;

        stopColor = Color.BLACK;
        stopOpacity = 1;

        fillPaint = new StyleColor(Color.BLACK);
        fillRule = FillRule.NONZERO;
        fillOpacity = 1;

        strokePaint = null;
        strokeWidth = new RaLength(1, RaLength.Type.NONE);
        strokeLineCap = StrokeLineCap.BUTT;
        strokeLineJoin = strokeLineJoin.MITER;
        strokeMiterLimit = 4;
        strokeDashArray = null;
        strokeDashOffset = 0;
        strokeOpacity = 1;

        fontFamily = new String[]{"Serif"};
        fontStyle = FontStyle.NORMAL;
        fontVariant = FontVariant.NORMAL;
        fontWeight = FontWeight.NORMAL;
        fontSize = new RaLength(12);

        textAlign = TextAlign.START;
        textAnchor = TextAnchor.START;
        direction = Direction.LTR;
        writingMode = WritingMode.LR_TB;
    }

    public RabbitFrame(RabbitFrame frame, RaElement source)
    {
        this.source = source;
        this.last = frame;
        this.surface = frame.surface;

        xform = new AffineTransform(frame.xform);
        visibility = frame.visibility;
        opacity = frame.opacity;

        stopColor = frame.stopColor;
        stopOpacity = frame.stopOpacity;

        fillPaint = frame.fillPaint;
        fillRule = frame.fillRule;
        fillOpacity = frame.fillOpacity;

        strokePaint = frame.strokePaint;
        strokeWidth = frame.strokeWidth;
        strokeLineCap = frame.strokeLineCap;
        strokeLineJoin = frame.strokeLineJoin;
        strokeMiterLimit = frame.strokeMiterLimit;
        strokeDashArray = frame.strokeDashArray;
        strokeDashOffset = frame.strokeDashOffset;
        strokeOpacity = frame.strokeOpacity;

        fontFamily = frame.fontFamily;
        fontStyle = frame.fontStyle;
        fontVariant = frame.fontVariant;
        fontWeight = frame.fontWeight;
        fontSize = frame.fontSize;

        textAlign = frame.textAlign;
        textAnchor = frame.textAnchor;
        direction = frame.direction;
        writingMode = frame.writingMode;
    }

    public BasicStroke getStroke()
    {
        if (stroke == null)
        {
            stroke = new BasicStroke(strokeWidth.getValue(),
                    strokeLineCap.getStrokeCap(),
                    strokeLineJoin.getStrokeJoin(),
                    strokeMiterLimit, strokeDashArray, strokeDashOffset);
        }
        return stroke;
    }

    /**
     * @return the surface
     */
    public Surface2D getSurface() {
        return surface;
    }

    /**
     * @param surface the surface to set
     */
    public void setSurface(Surface2D surface) {
        this.surface = surface;
    }

    /**
     * @return the last
     */
    public RabbitFrame getLast() {
        return last;
    }

    /**
     * @param last the last to set
     */
    public void setLast(RabbitFrame last) {
        this.last = last;
    }

    /**
     * @return the xform
     */
    public AffineTransform getXform() {
        return xform;
    }

    /**
     * @param xform the xform to set
     */
    public void setXform(AffineTransform xform) {
        this.xform = xform;
    }

    /**
     * @return the visibility
     */
    public Visibility getVisibility() {
        return visibility;
    }

    /**
     * @param visibility the visibility to set
     */
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    /**
     * @return the opacity
     */
    public float getOpacity() {
        return opacity;
    }

    /**
     * @param opacity the opacity to set
     */
    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    /**
     * @return the fillOpacity
     */
    public float getFillOpacity() {
        return fillOpacity;
    }

    /**
     * @param fillOpacity the fillOpacity to set
     */
    public void setFillOpacity(float fillOpacity) {
        this.fillOpacity = fillOpacity;
    }

    /**
     * @return the fillPaint
     */
    public StylePaint getFillPaint() {
        return fillPaint;
    }

    /**
     * @param fillPaint the fillPaint to set
     */
    public void setFillPaint(StylePaint fillPaint) {
        this.fillPaint = fillPaint;
    }

    /**
     * @return the fillRule
     */
    public FillRule getFillRule() {
        return fillRule;
    }

    /**
     * @param fillRule the fillRule to set
     */
    public void setFillRule(FillRule fillRule) {
        this.fillRule = fillRule;
    }

    /**
     * @return the strokeOpacity
     */
    public float getStrokeOpacity() {
        return strokeOpacity;
    }

    /**
     * @param strokeOpacity the strokeOpacity to set
     */
    public void setStrokeOpacity(float strokeOpacity) {
        this.strokeOpacity = strokeOpacity;
    }

    /**
     * @return the strokePaint
     */
    public StylePaint getStrokePaint() {
        return strokePaint;
    }

    /**
     * @param strokePaint the strokePaint to set
     */
    public void setStrokePaint(StylePaint strokePaint) {
        this.strokePaint = strokePaint;
    }

    /**
     * @return the strokeWidth
     */
    public RaLength getStrokeWidth() {
        return strokeWidth;
    }

    /**
     * @param strokeWidth the strokeWidth to set
     */
    public void setStrokeWidth(RaLength strokeWidth) {
        this.strokeWidth = strokeWidth;
        stroke = null;
    }

    /**
     * @return the strokeLineCap
     */
    public StrokeLineCap getStrokeLineCap() {
        return strokeLineCap;
    }

    /**
     * @param strokeLineCap the strokeLineCap to set
     */
    public void setStrokeLineCap(StrokeLineCap strokeLineCap) {
        this.strokeLineCap = strokeLineCap;
        stroke = null;
    }

    /**
     * @return the strokeLineJoin
     */
    public StrokeLineJoin getStrokeLineJoin() {
        return strokeLineJoin;
    }

    /**
     * @param strokeLineJoin the strokeLineJoin to set
     */
    public void setStrokeLineJoin(StrokeLineJoin strokeLineJoin) {
        this.strokeLineJoin = strokeLineJoin;
        stroke = null;
    }

    /**
     * @return the strokeMiterLimit
     */
    public float getStrokeMiterLimit() {
        return strokeMiterLimit;
    }

    /**
     * @param strokeMiterLimit the strokeMiterLimit to set
     */
    public void setStrokeMiterLimit(float strokeMiterLimit) {
        this.strokeMiterLimit = strokeMiterLimit;
        stroke = null;
    }

    /**
     * @return the strokeDashArray
     */
    public float[] getStrokeDashArray() {
        return strokeDashArray;
    }

    /**
     * @param strokeDashArray the strokeDashArray to set
     */
    public void setStrokeDashArray(float[] strokeDashArray) {
        this.strokeDashArray = strokeDashArray;
        stroke = null;
    }

    /**
     * @return the strokeDashOffset
     */
    public float getStrokeDashOffset() {
        return strokeDashOffset;
    }

    /**
     * @param strokeDashOffset the strokeDashOffset to set
     */
    public void setStrokeDashOffset(float strokeDashOffset) {
        this.strokeDashOffset = strokeDashOffset;
        stroke = null;
    }

    /**
     * @return the fontFamily
     */
    public String[] getFontFamily() {
        return fontFamily;
    }

    /**
     * @param fontFamily the fontFamily to set
     */
    public void setFontFamily(String[] fontFamily) {
        this.fontFamily = fontFamily;
    }

    /**
     * @return the fontSize
     */
    public RaLength getFontSize() {
        return fontSize;
    }

    /**
     * @param fontSize the fontSize to set
     */
    public void setFontSize(RaLength fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * @return the fontStyle
     */
    public FontStyle getFontStyle() {
        return fontStyle;
    }

    /**
     * @param fontStyle the fontStyle to set
     */
    public void setFontStyle(FontStyle fontStyle) {
        this.fontStyle = fontStyle;
    }

    /**
     * @return the fontVariant
     */
    public FontVariant getFontVariant() {
        return fontVariant;
    }

    /**
     * @param fontVariant the fontVariant to set
     */
    public void setFontVariant(FontVariant fontVariant) {
        this.fontVariant = fontVariant;
    }

    /**
     * @return the fontWeight
     */
    public FontWeight getFontWeight() {
        return fontWeight;
    }

    /**
     * @param fontWeight the fontWeight to set
     */
    public void setFontWeight(FontWeight fontWeight) {
        this.fontWeight = fontWeight;
    }

    /**
     * @return the textAlign
     */
    public TextAlign getTextAlign() {
        return textAlign;
    }

    /**
     * @param textAlign the textAlign to set
     */
    public void setTextAlign(TextAlign textAlign) {
        this.textAlign = textAlign;
    }

    /**
     * @return the stopColor
     */
    public Color getStopColor() {
        return stopColor;
    }

    /**
     * @param stopColor the stopColor to set
     */
    public void setStopColor(Color stopColor) {
        this.stopColor = stopColor;
    }

    /**
     * @return the stopOpacity
     */
    public float getStopOpacity() {
        return stopOpacity;
    }

    /**
     * @param stopOpacity the stopOpacity to set
     */
    public void setStopOpacity(float stopOpacity) {
        this.stopOpacity = stopOpacity;
    }

    /**
     * @return the source
     */
    public RaElement getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(RaElement source) {
        this.source = source;
    }

    /**
     * @return the textAnchor
     */
    public TextAnchor getTextAnchor() {
        return textAnchor;
    }

    /**
     * @param textAnchor the textAnchor to set
     */
    public void setTextAnchor(TextAnchor textAnchor) {
        this.textAnchor = textAnchor;
    }

    /**
     * @return the direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * @return the writingMode
     */
    public WritingMode getWritingMode() {
        return writingMode;
    }

    /**
     * @param writingMode the writingMode to set
     */
    public void setWritingMode(WritingMode writingMode) {
        this.writingMode = writingMode;
    }

}
