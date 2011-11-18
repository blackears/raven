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

package com.kitfox.raven.editor.node.tools.common.select;

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.shape.CyRectangle2d;
import java.awt.geom.NoninvertibleTransformException;

/**
 *
 * @author kitfox
 */
public class SelectionManipulatorHandle extends ManipulatorHandle
{
    private final Type type;
    private final CyRectangle2d worldBounds;
    private final CyVector2d worldPivot;
    private final int dirX;
    private final int dirY;

    public SelectionManipulatorHandle(CyVector2d anchor, CyMatrix4d w2d,
            Type type, CyRectangle2d worldBounds, CyVector2d worldPivot,
            int dirX, int dirY)
    {
        super(anchor, w2d);
        this.type = type;
        this.worldBounds = worldBounds;
        this.worldPivot = worldPivot;
        this.dirX = dirX;
        this.dirY = dirY;
    }

    /**
     * @return the type
     */
    public Type getType()
    {
        return type;
    }

    /**
     * @return the worldBounds
     */
    public CyRectangle2d getWorldBounds()
    {
        return new CyRectangle2d(worldBounds);
    }

    /**
     * @return the pivot
     */
    public CyVector2d getWorldPivot()
    {
        return new CyVector2d(worldPivot);
    }

    public void apply(int x, int y, SelectionManipulator manip,
            boolean centered, boolean symmetric, boolean history) throws NoninvertibleTransformException
    {
        CyVector2d startWorld = new CyVector2d(anchor.getX(), anchor.getY());
        CyVector2d endWorld = new CyVector2d(x, y);

//        CyMatrix4d d2w = new CyMatrix4d(w2d);
//        d2w.invert();

        d2w.transformPoint(startWorld);
        d2w.transformPoint(endWorld);


        double sx = startWorld.x;
        double sy = startWorld.y;

        double ex = endWorld.x;
        double ey = endWorld.y;

        double dx = ex - sx;
        double dy = ey - sy;

        double cx = worldBounds.getCenterX();
        double cy = worldBounds.getCenterY();

        //Anchor point that does not move during scaling
        double width = worldBounds.getWidth();
        double height = worldBounds.getHeight();
        double ax = centered ? cx : cx - dirX * width / 2;
        double ay = centered ? cy : cy - dirY * height / 2;

        double px = worldPivot.getX();
        double py = worldPivot.getY();

//        for (RootState state : manip.getRoots())
//        {
            //RavenNodeSpatial node
            CyMatrix4d toolWorld = CyMatrix4d.createIdentity();
            CyVector2d pivot = new CyVector2d(worldPivot);
            //AffineTransform at;
            //at.shear(sx, sy);
            switch (type)
            {
                case TRANSLATE:
                    toolWorld.translate(dx, dy, 0);
                    break;
                case ROTATE:
                {
                    toolWorld.translate(px, py, 0);
                    double startAngle = Math.atan2(sy - py, sx - px);
                    double endAngle = Math.atan2(ey - py, ex - px);
                    toolWorld.rotZ(endAngle - startAngle);
                    toolWorld.translate(-px, -py, 0);
                    break;
                }
                case SKEWX:
                {
                    toolWorld.translate(ax, ay, 0);
                    //toolWorld.shear((ex - sx) / ((cy + dirY * height / 2) - ay), 0);
//                    toolWorld.m10 *= (ex - sx) / ((cy + dirY * height / 2) - ay);
                    toolWorld.shear((ex - sx) / ((cy + dirY * height / 2) - ay), 0);
                    toolWorld.translate(-ax, -ay, 0);
                    break;
                }
                case SKEWY:
                {
                    toolWorld.translate(ax, ay, 0);
                    //toolWorld.shear(0, (ey - sy) / ((cx + dirX * width / 2) - ax));
//                    toolWorld.m01 *= (ey - sy) / ((cx + dirX * width / 2) - ax);
                    toolWorld.shear(0, (ey - sy) / ((cx + dirX * width / 2) - ax));
                    toolWorld.translate(-ax, -ay, 0);
                    break;
                }
                case SCALE:
                case SCALEX:
                case SCALEY:
                {
                    if (type == Type.SCALEX)
                    {
                        sy = ey = cy;
                    }
                    else if (type == Type.SCALEY)
                    {
                        sx = ex = cx;
                    }

                    if (symmetric)
                    {
                        dx = ex - ax;
                        dy = ey - ay;
                        if (dx * dx > dy * dy)
                        {
                            dy = Math.signum(dy) * Math.abs(dx);
                        } else
                        {
                            dx = Math.signum(dx) * Math.abs(dy);
                        }
                        ex = dx + ax;
                        ey = dy + ay;
                    }

                    double scaleX = sx - ax == 0 ? 1 : (ex - ax) / (sx - ax);
                    double scaleY = sy - ay == 0 ? 1 : (ey - ay) / (sy - ay);

                    toolWorld.translate(ax, ay, 0);
                    toolWorld.scale(scaleX, scaleY, 1);
                    toolWorld.translate(-ax, -ay, 0);

                    break;
                }
                case PIVOT:
                    pivot.set(px + dx, py + dy);
                    break;
            }

            manip.applyTransform(toolWorld, pivot, history);

//        }

    }

    /**
     * @return the dirX
     */
    public int getDirX()
    {
        return dirX;
    }

    /**
     * @return the dirY
     */
    public int getDirY()
    {
        return dirY;
    }

    //-----------------------
    public static enum Type
    {
        TRANSLATE,
        ROTATE,
        SKEWX,
        SKEWY,
        SCALE,
        SCALEX,
        SCALEY,
        PIVOT
    }
}
