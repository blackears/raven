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

package com.kitfox.raven.editor.stroke;

import com.kitfox.cache.CacheElement;
import com.kitfox.cache.CacheFloat;
import com.kitfox.cache.CacheIdentifier;
import com.kitfox.cache.CacheList;
import com.kitfox.cache.CacheMap;
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import com.kitfox.rabbit.util.NumberText;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.util.Arrays;

/**
 *
 * @author kitfox
 */
@Deprecated
public class RavenStrokeBasic extends RavenStrokeInline
{
    private final float width;
    private final Cap cap;
    private final Join join;
    private final float miterLimit;
    private final float[] dashes;
    private final float dashPhase;

    public static final String CACHE_NAME = "stroke";
    
    public static final String PROP_WIDTH = "width";
    public static final String PROP_CAP = "cap";
    public static final String PROP_JOIN = "join";
    public static final String PROP_MITERLIMIT = "miterLimit";
    public static final String PROP_DASH = "dash";
    public static final String PROP_DASHPHASE = "dashPhase";

    BasicStroke stroke;

    public RavenStrokeBasic(float width, Cap cap, Join join, float miterLimit, float[] dashes, float dashPhase)
    {
        this.width = width;
        this.cap = cap;
        this.join = join;
        this.miterLimit = miterLimit;
        this.dashes = dashes == null ? new float[0] : dashes;
        this.dashPhase = dashPhase;
    }

    public RavenStrokeBasic()
    {
        this(1, Cap.ROUND, Join.ROUND, 10, null, 0);
    }

    public RavenStrokeBasic(CacheMap map)
    {
        this.width = map.getFloat(PROP_WIDTH, 1);
        this.cap = Cap.valueOf(map.getIdentifierName(PROP_CAP, Cap.ROUND.name()));
        this.join = Join.valueOf(map.getIdentifierName(PROP_JOIN, Join.ROUND.name()));
        this.miterLimit = map.getFloat(PROP_MITERLIMIT, 10);
        this.dashes = map.getFloatArray(PROP_DASH, new float[0]);
        this.dashPhase = map.getFloat(PROP_DASHPHASE, 0);
    }

    public static RavenStrokeBasic create(String text)
    {
        try {
            CacheElement ele = CacheParser.parse(text);
            if (!(ele instanceof CacheMap))
            {
                return null;
            }
            return new RavenStrokeBasic((CacheMap)ele);
        } catch (ParseException ex) {
            return null;
        }
    }

    /**
     * @return the width
     */
    public float getWidth() {
        return width;
    }

    /**
     * @return the miterLimit
     */
    public float getMiterLimit() {
        return miterLimit;
    }

    /**
     * @return the join
     */
    public Join getJoin() {
        return join;
    }

    /**
     * @return the cap
     */
    public Cap getCap() {
        return cap;
    }

    /**
     * @return the dashes
     */
    public float[] getDashes() {
        return dashes.clone();
    }

    /**
     * @return the dashPhase
     */
    public float getDashPhase() {
        return dashPhase;
    }

//    public static RavenStrokeBasic create(String text)
//    {
//        StringReader reader = new StringReader(text);
//        CacheParser parser = new CacheParser(reader);
//        try
//        {
//            return create(parser.Map());
//        } catch (ParseException ex) {
//            Logger.getLogger(StrokeStyleEditor.class.getName()).log(Level.WARNING, null, ex);
//            return null;
//        }
//    }
//
//    public static RavenStrokeBasic create(CacheMap map)
//    {
//        float width = map.getFloat(PROP_WIDTH, 1);
//        Cap cap = Cap.valueOf(map.getIdentifierName(PROP_CAP, Cap.SQUARE.name()));
//        Join join = Join.valueOf(map.getIdentifierName(PROP_JOIN, Join.BEVEL.name()));
//        float miterLimit = map.getFloat(PROP_MITERLIMIT, 10);
//        float dashPhase = map.getFloat(PROP_DASHPHASE, 0);
//
//        CacheList dashList = (CacheList)map.get(PROP_DASH);
//        float[] dash = dashList == null
//                ? null
//                : dashList.toFloatArray(0);
//
//        if (dash != null)
//        {
//            boolean allZero = true;
//            for (int i = 0; i < dash.length; ++i)
//            {
//                if (dash[i] != 0)
//                {
//                    allZero = false;
//                    break;
//                }
//            }
//            if (allZero)
//            {
//                dash = null;
//            }
//        }
//
//        return new RavenStrokeBasic(width, cap, join, miterLimit, dash, dashPhase);
//    }
//

    @Override
    public CacheMap toCache()
    {
//        float width = value.getWidth();
//        Cap cap = value.getCap();
//        Join join = value.getJoin();
//        float miterLimit = value.getMiterLimit();
//        float[] dash = value.getDash();
//        float dashPhase = value.getDashPhase();

        CacheMap map = new CacheMap(CACHE_NAME);
        if (width != 1)
        {
            map.put(PROP_WIDTH, new CacheFloat(width));
        }
        if (cap != Cap.ROUND)
        {
            map.put(PROP_CAP, new CacheIdentifier(cap.toString()));
        }
        if (join != Join.ROUND)
        {
            map.put(PROP_JOIN, new CacheIdentifier(join.toString()));
        }
        if (miterLimit != 10)
        {
            map.put(PROP_MITERLIMIT, new CacheFloat(miterLimit));
        }
        if (dashes != null && dashes.length != 0)
        {
            CacheList list = new CacheList();
            for (float dashValue: dashes)
            {
                list.add(new CacheFloat(dashValue));
            }
            map.put(PROP_DASH, list);
        }
        if (dashPhase != 0)
        {
            map.put(PROP_DASHPHASE, new CacheFloat(dashPhase));
        }
        return map;
    }

    public String toCodeGen()
    {
        return String.format("new java.awt.BasicStroke(%f, %s, %s, %f, %s, %f)",
                width,
                cap.toString(),
                join.toString(),
                miterLimit,
                NumberText.asStringCodeGen(dashes),
                dashPhase);
    }

    @Override
    public BasicStroke getStroke()
    {
        if (stroke == null)
        {
            int vCap;
            switch (cap)
            {
                case BUTT:
                    vCap = BasicStroke.CAP_BUTT;
                    break;
                default:
                case ROUND:
                    vCap = BasicStroke.CAP_ROUND;
                    break;
                case SQUARE:
                    vCap = BasicStroke.CAP_SQUARE;
                    break;
            }

            int vJoin;
            switch (join)
            {
                case BEVEL:
                    vJoin = BasicStroke.JOIN_BEVEL;
                    break;
                default:
                case ROUND:
                    vJoin = BasicStroke.JOIN_ROUND;
                    break;
                case SQUARE:
                    vJoin = BasicStroke.JOIN_MITER;
                    break;
            }

            //Cannot pass dash array with all 0 entries
            boolean allDashes0 = true;
            if (dashes != null)
            {
                for (int i = 0; i < dashes.length; ++i)
                {
                    if (dashes[i] != 0)
                    {
                        allDashes0 = false;
                        break;
                    }
                }
            }
            stroke = new BasicStroke(
                    width, vCap, vJoin, Math.max(miterLimit, 1),
                    allDashes0 ? null : dashes, dashPhase);
        }
        return stroke;
    }

    @Override
    public String toString()
    {
        return "" + toCache();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RavenStrokeBasic other = (RavenStrokeBasic) obj;
        if (Float.floatToIntBits(this.width) != Float.floatToIntBits(other.width)) {
            return false;
        }
        if (this.cap != other.cap) {
            return false;
        }
        if (this.join != other.join) {
            return false;
        }
        if (Float.floatToIntBits(this.miterLimit) != Float.floatToIntBits(other.miterLimit)) {
            return false;
        }
        if (!Arrays.equals(this.dashes, other.dashes)) {
            return false;
        }
        if (Float.floatToIntBits(this.dashPhase) != Float.floatToIntBits(other.dashPhase)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Float.floatToIntBits(this.width);
        hash = 89 * hash + (this.cap != null ? this.cap.hashCode() : 0);
        hash = 89 * hash + (this.join != null ? this.join.hashCode() : 0);
        hash = 89 * hash + Float.floatToIntBits(this.miterLimit);
        hash = 89 * hash + Arrays.hashCode(this.dashes);
        hash = 89 * hash + Float.floatToIntBits(this.dashPhase);
        return hash;
    }

    @Override
    public void drawPreview(Graphics2D g, Rectangle box)
    {
        BasicStroke bs = getStroke();

        float inset = Math.min(box.width, box.height) / 4f;
        float dispWidth = Math.min(bs.getLineWidth(), inset * 2);
        float pathLenX = box.width - 2 * inset;

        Path2D.Double path = new Path2D.Double();
        path.moveTo(inset, box.height / 2);
        path.curveTo(
                (pathLenX / 3) + inset, inset * 0,
                (pathLenX * 2 / 3) + inset, box.height - (inset * 0),
                box.width - inset, box.height / 2
                );

        BasicStroke dispStroke = new BasicStroke(dispWidth,
                bs.getEndCap(), bs.getLineJoin(),
                bs.getMiterLimit(),
                bs.getDashArray(), bs.getDashPhase());

        g.setStroke(dispStroke);
        g.draw(path);
    }


    //----------------------------------------------
    public static enum Join
    {
        ROUND, SQUARE, BEVEL
    }

    public static enum Cap
    {
        ROUND, BUTT, SQUARE
    }

}
