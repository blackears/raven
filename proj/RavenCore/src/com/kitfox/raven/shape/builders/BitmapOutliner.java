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

package com.kitfox.raven.shape.builders;

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.CyRectangle2d;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author kitfox
 */
public class BitmapOutliner
{
    final Sampler sampler;
    private CyRectangle2d region;
    int numSampX;
    int numSampY;

    int upperMark;
    int lowerMark;
    boolean upperPenDown;
    boolean lowerPenDown;
    BuildState buildState;
    HashMap<Integer, Contour> contourEnds = new HashMap<Integer, Contour>();
    ArrayList<Contour> contours;

    public BitmapOutliner(Sampler sampler, CyRectangle2d region, 
            int numSampX, int numSampY)
    {
        this.sampler = sampler;
        this.region = region;
        this.numSampX = numSampX;
        this.numSampY = numSampY;
    }

//    public Path2D.Double createSmoothedPath(double maxError)
//    {
//        Path2D.Double path = new Path2D.Double();
//
//        ArrayList<Contour> ctrs = getContours();
//        for (Contour ctr: ctrs)
//        {
//            ctr.cutSegments(4);
//            ctr.appendSmoothedPath(maxError, path);
//
//            path.closePath();
//        }
//
//        double sampDxI = (numSampX - 1) / region.getWidth();
//        double sampDyI = (numSampY - 1) / region.getHeight();
//
//        AffineTransform xform = new AffineTransform();
//        xform.translate(region.getX(), region.getY());
//        xform.scale(sampDxI, sampDyI);
//
//        return (Path2D.Double)xform.createTransformedShape(path);
//    }
//
//    public Path2D.Double createPath()
//    {
//        Path2D.Double path = new Path2D.Double();
//
//        ArrayList<Contour> ctrs = getContours();
//        for (Contour ctr: ctrs)
//        {
//            ctr.appendTo(path);
//        }
//
//        double sampDxI = (numSampX - 1) / region.getWidth();
//        double sampDyI = (numSampY - 1) / region.getHeight();
//
////        return path;
//        AffineTransform xform = new AffineTransform();
//        xform.translate(region.getX(), region.getY());
////        xform.scale(region.getWidth(), region.getHeight());
//        xform.scale(sampDxI, sampDyI);
//
//        return (Path2D.Double)xform.createTransformedShape(path);
//    }

    public CyPath2d getPath(double maxError)
    {
        CyPath2d path = new CyPath2d();
        
        for (Contour ctr: getContours())
        {
            path.append(ctr.getPath(maxError));
        }
        
        CyMatrix4d m = CyMatrix4d.createIdentity();
        m.translate(region.getX(), region.getY(), 0);
        path = path.createTransformedPath(m);
        
        return path;
    }
    
    public ArrayList<Contour> getContours()
    {
        if (contours == null)
        {
            buildContours();
        }
        return new ArrayList<Contour>(contours);
    }

    public void dumpBitmap()
    {
//        contours = new ArrayList<Contour>();

        double sampDx = region.getWidth() / numSampX;
        double sampDy = region.getHeight() / numSampY;

        BufferedImage img = new BufferedImage(numSampX + 2, numSampY + 2, Transparency.OPAQUE);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.dispose();

        //Include 1 unit border
        for (int j = -1; j <= numSampY; ++j)
        {
            //Include 1 unit border
            for (int i = -1; i <= numSampX; ++i)
            {
                boolean lowerHit = sampler.isOpaque(
                        i * sampDx + region.getX(),
                        j * sampDy + region.getY());

                if (lowerHit)
                {
                    img.setRGB(i + 1, j + 1, Color.black.getRGB());
                }
            }
        }
        
        try
        {
            ImageIO.write(img, "png", new File("bitmapOutliner.png"));
        } catch (IOException ex)
        {
            Logger.getLogger(BitmapOutliner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Scan over all bubbles, building the best fitting piecewise linear
     * outline.
     */
    private void buildContours()
    {
//dumpBitmap();

        contours = new ArrayList<Contour>();

        double sampDx = region.getWidth() / numSampX;
        double sampDy = region.getHeight() / numSampY;

        //Include 1 unit border
        for (int j = -1; j <= numSampY; ++j)
        {
            upperMark = 0;
            lowerMark = 0;
            upperPenDown = false;
            lowerPenDown = false;
            buildState = BuildState.NONE;

            //Include 1 unit border
            for (int i = -1; i <= numSampX; ++i)
            {
                boolean upperHit = sampler.isOpaque(
                        i * sampDx + region.getX(),
                        (j - 1) * sampDy + region.getY());
                boolean lowerHit = sampler.isOpaque(
                        i * sampDx + region.getX(),
                        j * sampDy + region.getY());

                if (upperHit != upperPenDown)
                {
                    changePenState(upperHit
                            ? BuildState.UPPER_DOWN : BuildState.UPPER_UP,
                            i, j);
                    upperPenDown = upperHit;
                    upperMark = i;
                }

                if (lowerHit != lowerPenDown)
                {
                    changePenState(lowerHit
                            ? BuildState.LOWER_DOWN : BuildState.LOWER_UP,
                            i, j);
                    lowerPenDown = lowerHit;
                    lowerMark = i;
                }

            }

            assert (buildState == BuildState.NONE);
            assert (!upperPenDown);
            assert (!lowerPenDown);
        }

        assert (contourEnds.isEmpty());
    }

    private void changePenState(BuildState state, int x, int y)
    {
        switch (buildState)
        {
            case NONE:
                //Queue the first half of the tuple for the next call
                buildState = state;
                return;
            case LOWER_DOWN:
            {
                if (state == BuildState.LOWER_UP)
                {
                    //Cup down solid - create new contour
                    //Wind CW
                    Contour ctr = new Contour();
                    ctr.addStart(lowerMark, y);
                    ctr.addEnd(x, y);
                    contourEnds.put(lowerMark, ctr);
                    contourEnds.put(x, ctr);
                }
                else if (state == BuildState.UPPER_DOWN)
                {
                    //Add trailing unit
                    Contour ctr = contourEnds.remove(x);
                    ctr.addStart(lowerMark, y);
                    contourEnds.put(lowerMark, ctr);
                }
                else
                {
                    assert false;
                }
                break;
            }
            case LOWER_UP:
            {
                if (state == BuildState.LOWER_DOWN)
                {
                    //Cup down hole - create new contour
                    //Wind CCW
                    Contour ctr = new Contour();
                    ctr.addStart(x, y);
                    ctr.addEnd(lowerMark, y);
                    contourEnds.put(x, ctr);
                    contourEnds.put(lowerMark, ctr);
                }
                else if (state == BuildState.UPPER_UP)
                {
                    //Add leading unit
                    Contour ctr = contourEnds.remove(x);
                    ctr.addEnd(lowerMark, y);
                    contourEnds.put(lowerMark, ctr);
                }
                else
                {
                    assert false;
                }
                break;
            }
            case UPPER_DOWN:
            {
                if (state == BuildState.UPPER_UP)
                {
                    //Cup up solid - join contours
                    //Wind CW
                    Contour ctrAfter = contourEnds.remove(upperMark);
                    Contour ctrBefore = contourEnds.remove(x);
                    if (ctrBefore == ctrAfter)
                    {
                        contours.add(ctrAfter);
//                        ctrAfter.removeColinearPoints();
                    }
                    else
                    {
                        ctrBefore.append(ctrAfter);
                        //Replace with joined contour
                        contourEnds.put(ctrAfter.getLastPoint().x, ctrBefore);
                    }
                }
                else if (state == BuildState.LOWER_DOWN)
                {
                    //Add trailing unit
                    Contour ctr = contourEnds.remove(upperMark);
                    ctr.addStart(x, y);
                    contourEnds.put(x, ctr);
                }
                else
                {
                    assert false;
                }
                break;
            }
            case UPPER_UP:
            {
                if (state == BuildState.UPPER_DOWN)
                {
                    //Cup up hole - join contours
                    //Wind CW
                    Contour ctrAfter = contourEnds.remove(x);
                    Contour ctrBefore = contourEnds.remove(upperMark);
                    if (ctrBefore == ctrAfter)
                    {
                        contours.add(ctrAfter);
//                        ctrAfter.removeColinearPoints();
                    }
                    else
                    {
                        ctrBefore.append(ctrAfter);
                        //Replace with joined contour
                        contourEnds.put(ctrAfter.getLastPoint().x, ctrBefore);
                    }
                }
                else if (state == BuildState.LOWER_UP)
                {
                    //Add leading unit
                    Contour ctr = contourEnds.remove(upperMark);
                    ctr.addEnd(x, y);
                    contourEnds.put(x, ctr);
                }
                else
                {
                    assert false;
                }
                break;
            }
            default:
                assert false;
        }

        buildState = BuildState.NONE;
    }

    /**
     * @return the region
     */
    public CyRectangle2d getRegion()
    {
        return new CyRectangle2d(region);
    }


    //------------------------

    static enum BuildState
    {
        NONE, UPPER_DOWN, LOWER_DOWN, UPPER_UP, LOWER_UP
    }

    public interface Sampler
    {
        public boolean isOpaque(double x, double y);
    }
}
