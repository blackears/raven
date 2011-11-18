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

package com.kitfox.raven.swf.importer;

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.game.control.color.ColorStyle;
import com.kitfox.game.control.color.MultipleGradientStops;
import com.kitfox.game.control.color.MultipleGradientStops.Cycle;
import com.kitfox.game.control.color.MultipleGradientStops.Style;
import com.kitfox.game.control.color.MultipleGradientStyle;
import com.kitfox.game.control.color.PaintLayoutLinear;
import com.kitfox.game.control.color.PaintLayoutNone;
import com.kitfox.game.control.color.PaintLayoutRadial;
import com.kitfox.raven.editor.paint.RavenPaintColor;
import com.kitfox.raven.editor.paint.RavenPaintGradient;
import com.kitfox.raven.editor.stroke.RavenStrokeBasic;
import com.kitfox.raven.editor.stroke.RavenStrokeInline;
import com.kitfox.swf.tags.shapes.FillStyle;
import com.kitfox.swf.tags.shapes.Gradient;
import com.kitfox.swf.tags.shapes.GradientFocal;
import com.kitfox.swf.tags.shapes.LineStyle;
import com.kitfox.swf.tags.shapes.LineStyle2;
import com.kitfox.swf.tags.shapes.ShapeWithStyle.ShapeVisitor;
import java.awt.Color;
import java.awt.geom.AffineTransform;

/**
 *
 * @author kitfox
 */
abstract public class MeshBuilderBase
        implements ShapeVisitor
{
    PaintEntry paintLeft;
    PaintEntry paintRight;
    PaintEntry paintLine;
    RavenStrokeInline strokeLine;

    int px;
    int py;
    
    private PaintEntry parsePaint(Color color)
    {
        return new PaintEntry(new RavenPaintColor(color), PaintLayoutNone.LAYOUT);
    }

    private CyMatrix4d swfPaintToRaven(AffineTransform swfPaintToStage)
    {
        AffineTransform xform = new AffineTransform();
        //Twips to pixels
        xform.scale(1 / 20.0, 1 / 20.0);
        //swf paint to stage
        xform.concatenate(swfPaintToStage);
        //Raven paint to swf paint
        xform.scale(16384 * 2, 16384 * 2);
        xform.translate(-.5, -.5);

        return new CyMatrix4d(xform);
    }

    private PaintEntry parsePaint(FillStyle style)
    {
        if (style == null)
        {
            return null;
        }

        switch (style.getFillStyleType())
        {
            case SOLID:
                return new PaintEntry(new RavenPaintColor(style.getColor()),
                        PaintLayoutNone.LAYOUT);
            case GRAD_LINEAR:
            {
                PaintLayoutLinear layout = new PaintLayoutLinear()
                        .transform(swfPaintToRaven(style.getGradMtx()
                            .asAffineTransform()));
                RavenPaintGradient grad = new RavenPaintGradient(
                        new MultipleGradientStyle(
                        parseGradientStops(style.getGrad(), Style.LINEAR)));
                return new PaintEntry(grad, layout);
            }
            case GRAD_RADIAL:
            {
                PaintLayoutRadial layout =
                        new PaintLayoutRadial(.5f, .5f, 5f, 0, 0, 90, .5f, .5f)
                        .transform(swfPaintToRaven(style.getGradMtx()
                            .asAffineTransform()));
                RavenPaintGradient grad = new RavenPaintGradient(new MultipleGradientStyle(
                        parseGradientStops(style.getGrad(), Style.RADIAL)));
                return new PaintEntry(grad, layout);
            }
            case GRAD_RADIAL_FOCAL:
            {
                float focalPt = ((GradientFocal)style.getGrad()).getFocalPoint().asFloat();
                PaintLayoutRadial layout =
                        new PaintLayoutRadial(.5f, .5f, 5f, 0, 0, 90, (focalPt + 1) / 2, .5f)
                        .transform(swfPaintToRaven(style.getGradMtx()
                            .asAffineTransform()));
                RavenPaintGradient grad = new RavenPaintGradient(new MultipleGradientStyle(
                        parseGradientStops(style.getGrad(), Style.RADIAL)));
                return new PaintEntry(grad, layout);
            }
//            case BITMAP_CLIPPED:
//                break;
            default:
                throw new UnsupportedOperationException("Not implemented");
        }
    }

    private MultipleGradientStops parseGradientStops(Gradient grad, Style style)
    {
        Cycle cycle;
        switch (grad.getCycleMethodAwt())
        {
            default:
            case NO_CYCLE:
                cycle = Cycle.NO_CYCLE;
                break;
            case REPEAT:
                cycle = Cycle.REPEAT;
                break;
            case REFLECT:
                cycle = Cycle.REFLECT;
                break;
        }

        Color[] cols = grad.getColors();
        ColorStyle[] colStyles = new ColorStyle[cols.length];
        for (int i = 0; i < cols.length; ++i)
        {
            colStyles[i] = new ColorStyle(cols[i]);
        }
        return new MultipleGradientStops(
                grad.getFractions(), colStyles, cycle, style,
                grad.getColorSpace());
    }

    @Override
    public void setFillStyleLeft(FillStyle fillStyle)
    {
        paintLeft = parsePaint(fillStyle);
    }

    @Override
    public void setFillStyleRight(FillStyle fillStyle)
    {
        paintRight = parsePaint(fillStyle);
    }

    @Override
    public void setLineStyle(LineStyle lineStyle)
    {

        if (lineStyle == null)
        {
            paintLine = null;
            strokeLine = null;
            return;
        }

        if (lineStyle instanceof LineStyle2)
        {
            LineStyle2 style = (LineStyle2)lineStyle;
            if (style.isHasFill())
            {
                paintLine = parsePaint(style.getFill());
            }
            else
            {
                paintLine = parsePaint(style.getColor());
            }

            RavenStrokeBasic.Cap cap;
            switch (style.getCapStart())
            {
                case FLAT:
                    cap = RavenStrokeBasic.Cap.BUTT;
                    break;
                default:
                case ROUND:
                    cap = RavenStrokeBasic.Cap.ROUND;
                    break;
                case SQUARE:
                    cap = RavenStrokeBasic.Cap.SQUARE;
                    break;
            }

            RavenStrokeBasic.Join join;
            switch (style.getJoin())
            {
                case BEVEL:
                    join = RavenStrokeBasic.Join.SQUARE;
                    break;
                default:
                case ROUND:
                    join = RavenStrokeBasic.Join.ROUND;
                    break;
                case MITER:
                    join = RavenStrokeBasic.Join.BEVEL;
                    break;
            }

            RavenStrokeBasic stroke = new RavenStrokeBasic(
                    style.getWidth(),
                    cap,
                    join,
                    style.getMiterLimit(),
                    null, 0);

            strokeLine = stroke;
//            builder.strokeLine(getStrokeIndex(stroke));
            return;
        }

        paintLine = parsePaint(lineStyle.getColor());
        strokeLine = new RavenStrokeBasic(
                lineStyle.getWidth() / 20f,
                RavenStrokeBasic.Cap.ROUND,
                RavenStrokeBasic.Join.ROUND,
                10, null, 0);
    }

}
