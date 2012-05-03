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

import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.coyote.math.CyGradientStops;
import com.kitfox.coyote.math.CyGradientStops.Cycle;
import com.kitfox.coyote.math.CyGradientStops.Style;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.shape.CyStroke;
import com.kitfox.coyote.shape.CyStrokeCap;
import com.kitfox.coyote.shape.CyStrokeJoin;
import com.kitfox.raven.paint.RavenPaintLayout;
import com.kitfox.raven.paint.RavenStroke;
import com.kitfox.raven.paint.common.RavenPaintColor;
import com.kitfox.raven.paint.common.RavenPaintGradient;
import com.kitfox.raven.shape.network.NetworkDataEdge;
import com.kitfox.raven.shape.network.keys.NetworkDataTypePaint;
import com.kitfox.raven.shape.network.keys.NetworkDataTypePaintLayout;
import com.kitfox.raven.shape.network.keys.NetworkDataTypeStroke;
import com.kitfox.swf.tags.shapes.FillStyle;
import com.kitfox.swf.tags.shapes.Gradient;
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
    RavenStroke strokeLine;

    int px;
    int py;
    
    protected NetworkDataEdge createEdgeData()
    {
        NetworkDataEdge data = new NetworkDataEdge();
        data.putEdge(NetworkDataTypePaint.class, 
                paintLine == null ? null : paintLine.getPaint());
        data.putEdge(NetworkDataTypePaintLayout.class, 
                paintLine == null ? null : paintLine.getLayout());
        data.putEdge(NetworkDataTypeStroke.class, 
                strokeLine);
        
        data.putLeft(NetworkDataTypePaint.class, 
                paintLeft == null ? null : paintLeft.getPaint());
        data.putLeft(NetworkDataTypePaintLayout.class, 
                paintLeft == null ? null : paintLeft.getLayout());
        
        data.putRight(NetworkDataTypePaint.class, 
                paintRight == null ? null : paintRight.getPaint());
        data.putRight(NetworkDataTypePaintLayout.class, 
                paintRight == null ? null : paintRight.getLayout());
        
        return data;
    }
    
    private PaintEntry parsePaint(Color color)
    {
        return new PaintEntry(new RavenPaintColor(color), null);
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
                        null);
            case GRAD_LINEAR:
            {
                RavenPaintLayout layout = new RavenPaintLayout()
                        .transform(swfPaintToRaven(style.getGradMtx()
                            .asAffineTransform()));
                RavenPaintGradient grad = new RavenPaintGradient(
                        parseGradientStops(style.getGrad(), Style.LINEAR));
                return new PaintEntry(grad, layout);
            }
            case GRAD_RADIAL:
            {
                RavenPaintLayout layout =
                        new RavenPaintLayout(.5f, .5f, 5f, 0, 0, 90)
                        .transform(swfPaintToRaven(style.getGradMtx()
                            .asAffineTransform()));
                RavenPaintGradient grad = new RavenPaintGradient(
                        parseGradientStops(style.getGrad(), Style.RADIAL));
                return new PaintEntry(grad, layout);
            }
            case GRAD_RADIAL_FOCAL:
            {
                RavenPaintLayout layout =
                        new RavenPaintLayout(.5f, .5f, 5f, 0, 0, 90)
                        .transform(swfPaintToRaven(style.getGradMtx()
                            .asAffineTransform()));
                RavenPaintGradient grad = new RavenPaintGradient(
                        parseGradientStops(style.getGrad(), Style.RADIAL));
                return new PaintEntry(grad, layout);
            }
//            case BITMAP_CLIPPED:
//                break;
            default:
                throw new UnsupportedOperationException("Not implemented");
        }
    }

    private CyGradientStops parseGradientStops(Gradient grad, Style style)
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
        CyColor4f[] colStyles = new CyColor4f[cols.length];
        for (int i = 0; i < cols.length; ++i)
        {
            colStyles[i] = new CyColor4f(cols[i]);
        }
        return new CyGradientStops(
                grad.getFractions(), colStyles, cycle, style);
    }

    @Override
    public void setFillStyleLeft(FillStyle fillStyle)
    {
//        paintLeft = parsePaint(fillStyle);
        paintRight = parsePaint(fillStyle);
    }

    @Override
    public void setFillStyleRight(FillStyle fillStyle)
    {
//        paintRight = parsePaint(fillStyle);
        paintLeft = parsePaint(fillStyle);
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

            CyStrokeCap cap;
            switch (style.getCapStart())
            {
                case FLAT:
                    cap = CyStrokeCap.BUTT;
                    break;
                default:
                case ROUND:
                    cap = CyStrokeCap.ROUND;
                    break;
                case SQUARE:
                    cap = CyStrokeCap.SQUARE;
                    break;
            }

            CyStrokeJoin join;
            switch (style.getJoin())
            {
                case BEVEL:
                    join = CyStrokeJoin.MITER;
                    break;
                default:
                case ROUND:
                    join = CyStrokeJoin.ROUND;
                    break;
                case MITER:
                    join = CyStrokeJoin.BEVEL;
                    break;
            }

            CyStroke cyStroke = new CyStroke(
                    style.getWidth() / 20f,
                    cap,
                    join,
                    style.getMiterLimit(),
                    (float[])null, 0);
            RavenStroke stroke = new RavenStroke(cyStroke);

            strokeLine = stroke;
//            builder.strokeLine(getStrokeIndex(stroke));
            return;
        }

        paintLine = parsePaint(lineStyle.getColor());
        strokeLine = new RavenStroke(new CyStroke(
                lineStyle.getWidth() / 20f,
                CyStrokeCap.ROUND,
                CyStrokeJoin.ROUND,
                10, (float[])null, 0));
    }

}
