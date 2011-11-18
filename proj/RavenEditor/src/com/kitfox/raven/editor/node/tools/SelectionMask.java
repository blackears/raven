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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * White - selected
 * Black - unselected
 * Grey - Partially selected
 *
 * @author kitfox
 */
public class SelectionMask
{
    static final int selectionPaintSize = 8;

    static final BufferedImage texture;
    static {
        texture = new BufferedImage(selectionPaintSize, selectionPaintSize, BufferedImage.TYPE_INT_ARGB);
        WritableRaster raster = texture.getRaster();
        for (int j = 0; j < selectionPaintSize; ++j)
        {
            for (int i = 0; i < selectionPaintSize; ++i)
            {
                for (int b = 0; b < 3; ++b)
                {
                    raster.setSample(i, j, b, (((i + j) & 4) == 0) ? 0 : 0xff);
                }
                raster.setSample(i, j, 3, 0xff);
            }
        }
    }

    public static TexturePaint createPaint(int offset)
    {
        return new TexturePaint(texture, new Rectangle(offset, offset, selectionPaintSize, selectionPaintSize));
    }

    ArrayList<ChangeListener> changeListeners = new ArrayList<ChangeListener>();

    WritableRaster mask;
    WritableRaster maskDragSource;
    private Dimension size = new Dimension();

    public static enum SelectType { REPLACE, ADD, SUBTRACT, INTERSECT };

    public SelectionMask()
    {
        build();
    }

    public void addChangeListener(ChangeListener listener)
    {
        changeListeners.add(listener);
    }

    public void removeChangeListener(ChangeListener listener)
    {
        changeListeners.remove(listener);
    }

    protected void fireChange()
    {
        ChangeEvent evt = new ChangeEvent(this);
        for (ChangeListener l: new ArrayList<ChangeListener>(changeListeners))
        {
            l.stateChanged(evt);
        }
    }

    public void setPixel(int x, int y, int value)
    {
        mask.setSample(x, y, 0, value);
        fireChange();
    }

    boolean isClear(int threshold)
    {
        if (mask == null)
        {
            return true;
        }

        for (int j = 0; j < mask.getHeight(); ++j)
        {
            for (int i = 0; i < mask.getWidth(); ++i)
            {
                if (mask.getSample(i, j, 0) > threshold)
                {
                    return false;
                }
            }
        }
        return true;
    }
/*
    private SinglePixelPackedSampleModel createSampleModel()
    {
        return createSampleModel(size);
    }

    private SinglePixelPackedSampleModel createSampleModel(Dimension dim)
    {
        return new SinglePixelPackedSampleModel(DataBuffer.TYPE_BYTE, dim.width, dim.height, new int[]{8});
    }
*/
    private void build()
    {
        //Cancel any drag that may be in progress
        endDrag(true);
        if (mask != null && size.width == mask.getWidth() && size.height == mask.getHeight())
        {
            return;
        }
        else if (size.width <= 0 || size.height <= 0)
        {
            mask = null;
//            maskDragging = null;
            fireChange();
            return;
        }

        Raster oldMask = mask;
        mask = Raster.createBandedRaster(DataBuffer.TYPE_BYTE, size.width, size.height, 1, null);
//        mask = Raster.createWritableRaster(createSampleModel(), new Point(0, 0));
        if (oldMask != null)
        {
            mask.setRect(oldMask);
        }

        fireChange();
        //sk = new BufferedImage(size.width, size.height, BufferedImage.TYPE_BYTE_GRAY);
//        maskDragging = new BufferedImage(size.width, size.height, BufferedImage.TYPE_BYTE_GRAY);
    }

    /**
     * @return the size
     */
    public Dimension getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(Dimension size) {
        this.size = size;
        build();
    }

    public void beginDrag()
    {
        if (maskDragSource != null)
        {
            //Drag already started
            return;
        }

//        maskDragSource = Raster.createWritableRaster(createSampleModel(), new Point(0, 0));
        maskDragSource = Raster.createBandedRaster(DataBuffer.TYPE_BYTE, size.width, size.height, 1, null);
        maskDragSource.setRect(mask);
    }

    public void endDrag(boolean cancel)
    {
        if (maskDragSource == null)
        {
            //No drag in progress
            return;
        }

        if (cancel)
        {
            mask = maskDragSource;
        }
        maskDragSource = null;
        fireChange();
    }

    public void translate(int x, int y)
    {
        if (mask == null)
        {
            //Mask has invalid size
            return;
        }

        WritableRaster newMask = mask.createCompatibleWritableRaster();
        newMask.setRect(x, y, maskDragSource != null ? maskDragSource : mask);
        mask = newMask;
        fireChange();
    }

    public void select(Raster updateMask, SelectType type)
    {
        if (mask == null)
        {
            //Mask has invalid size
            return;
        }

        if (type == SelectType.REPLACE)
        {
            //Early exit for special case
            clear();
            mask.setRect(updateMask);
            fireChange();
            return;
        }

        if (maskDragSource != null)
        {
            mask.setRect(maskDragSource);
        }

        Rectangle updateBounds = updateMask.getBounds();
        Rectangle maskBounds = mask.getBounds();
        Rectangle isect = maskBounds.intersection(updateBounds);

        if (isect.width == 0)
        {
            //Out of drawing area
            return;
        }

        for (int j = 0; j < isect.height; ++j)
        {
            int y = isect.y + j;
            for (int i = 0; i < isect.width; ++i)
            {
                int x = isect.x + i;

                int maskValue = mask.getSample(x, y, 0);
                int updateValue = updateBounds.contains(x, y)
                        ? updateMask.getSample(x, y, 0)
                        : 0;

                switch (type)
                {
                    case ADD:
                        if (updateValue != 0)
                        {
                            mask.setSample(x, y, 0, Math.max(maskValue, updateValue));
//                            mask.setSample(x, y, 0, maskValue + updateValue);
                        }
                        break;
                    case SUBTRACT:
                        if (updateValue != 0)
                        {
                            mask.setSample(x, y, 0, Math.min(maskValue, 255 - updateValue));
//                            int value = maskValue - updateValue;
//                            mask.setSample(x, y, 0, value < 0 ? 0 : value);
                        }
                        break;
                    case INTERSECT:
//                        mask.setSample(x, y, 0, updateValue != 0 && maskValue != 0 ? 1 : 0);
                        mask.setSample(x, y, 0, maskValue ^ updateValue);
                        break;
                }
            }
        }
/*
        if (type == SelectType.INTERSECT)
        {
            //Clear everything outside of intersection area
            for (int j = 0; j < mask.getHeight(); ++j)
            {
                for (int i = 0; i < mask.getWidth(); ++i)
                {
                    if (!isect.contains(i, j))
                    {
                        mask.setSample(i, j, 0, 0);
                    }
                }
            }
        }
 */
        fireChange();
    }

    public void clear()
    {
        for (int j = 0; j < mask.getHeight(); ++j)
        {
            for (int i = 0; i < mask.getWidth(); ++i)
            {
                mask.setSample(i, j, 0, 0);
            }
        }
        fireChange();
    }

    public void invert()
    {
        for (int j = 0; j < mask.getHeight(); ++j)
        {
            for (int i = 0; i < mask.getWidth(); ++i)
            {
                int value = mask.getSample(i, j, 0);
                mask.setSample(i, j, 0, 255 - value);
            }
        }
        fireChange();
    }

    WritableRaster sampleBuffer;

    /**
     * Creates a marquis selection from the mask in the destination raster using
     * the region of interest specified.  A pixel is included in the marquis if
     * it maps to a position in the mask that is above the given threshold and
     * adjaced to a pixel that is not above this threshold.  Pixels beyond the
     * edge of the mask are considered to be below any threshold.
     *
     * You can use this to draw a maquis by using this to set the alpha channel
     * of a masking image and then painting over it using AlphaComposite.SrcIn
     *
     * This algoritm provides an early out if it detects that all pixels in the
     * region of interest are identical and so no marquis will be drawn.  In 
     * this case, the method returns false and dest is not written to.  This
     * can make tile based rendering strategies more efficient.
     *
     * @param roi Region of interest in mask.  Area will be scaled to match size of
     * the destination raster.
     * @param threshold Threshold value tested against
     * @param dest Raster marquis selection will be stored in.
     * @param destBand Band of destination raster marquis will be stored in
     * @return true if a marquis has been drawn to dest and false if not.
     */
    public boolean drawMarquis(Rectangle2D roi, int threshold, WritableRaster dest, int destBand)
    {
        AffineTransform dest2roi = new AffineTransform();
        dest2roi.translate(roi.getX(), roi.getY());
        dest2roi.scale(roi.getWidth() / dest.getWidth(), roi.getHeight() / dest.getHeight());
        dest2roi.translate(-dest.getMinX(), -dest.getMinY());

        //Create a scaled copy of the source image with a one pixel border on each side
        if (!(sampleBuffer != null &&
                sampleBuffer.getWidth() == dest.getWidth() + 2 &&
                sampleBuffer.getHeight() == dest.getHeight() + 2)
                )
        {
            sampleBuffer = Raster.createBandedRaster(DataBuffer.TYPE_BYTE, dest.getWidth() + 2, dest.getHeight() + 2, 1, null);
//            sampleBuffer = Raster.createWritableRaster(
//                    createSampleModel(new Dimension(dest.getWidth() + 2, dest.getHeight() + 2)),
//                    new Point(0, 0));
        }

        int lastValue = -1;
        boolean allValuesSame = true;
        for (int j = 0; j < dest.getHeight() + 2; ++j)
        {
            for (int i = 0; i < dest.getWidth() + 2; ++i)
            {
                Point2D srcPt = dest2roi.transform(
                        new Point2D.Double(i - 1 + dest.getMinX(), j - 1 + dest.getMinY()),
                        null);
                int x = (int)Math.floor(srcPt.getX());
                int y = (int)Math.floor(srcPt.getY());
                int value;
                if (x < 0 || x >= mask.getWidth() || y < 0 || y >= mask.getHeight())
                {
                    value = 0;
                }
                else
                {
                    value = mask.getSample(x, y, 0) & 0xff;
                }
                sampleBuffer.setSample(i, j, 0, value);

                //Check if all values identical
                if (lastValue != -1 && value != lastValue)
                {
                    allValuesSame = false;
                }
                lastValue = value;
            }
        }

        if (allValuesSame)
        {
            return false;
        }

        //Write marquis
        for (int j = 0; j < dest.getHeight(); ++j)
        {
            for (int i = 0; i < dest.getWidth(); ++i)
            {
                if (sampleBuffer.getSample(i + 1, j + 1, 0) <= threshold)
                {
                    //Pixel not in selection
                    dest.setSample(dest.getMinX() + i, dest.getMinY() + j, destBand, 0);
                    continue;
                }

                boolean foundLow =
                        sampleBuffer.getSample(i + 0, j + 0, 0) <= threshold ||
                        sampleBuffer.getSample(i + 1, j + 0, 0) <= threshold ||
                        sampleBuffer.getSample(i + 2, j + 0, 0) <= threshold ||
                        sampleBuffer.getSample(i + 0, j + 1, 0) <= threshold ||
                        sampleBuffer.getSample(i + 2, j + 1, 0) <= threshold ||
                        sampleBuffer.getSample(i + 0, j + 2, 0) <= threshold ||
                        sampleBuffer.getSample(i + 1, j + 2, 0) <= threshold ||
                        sampleBuffer.getSample(i + 2, j + 2, 0) <= threshold
                        ;

                dest.setSample(dest.getMinX() + i, dest.getMinY() + j, destBand, foundLow ? 255 : 0);
            }
        }

        return true;
    }

    /**
     * Check if picking chooses a selected region of the mask.
     * @param x
     * @param y
     * @param threshold
     * @return
     */
    public boolean hit(int x, int y, int threshold)
    {
        if (x < 0 || x >= mask.getWidth() || y < 0 || y >= mask.getHeight())
        {
            return false;
        }
        return mask.getSample(x, y, 0) > threshold;
    }

    public ByteBuffer getData(Rectangle region, ByteBuffer buf)
    {
        buf.rewind();
        Rectangle maskBounds = mask.getBounds();
        for (int j = 0; j < region.height; ++j)
        {
            int y = region.y + j;
            for (int i = 0; i < region.width; ++i)
            {
                int x = region.x + i;

                if (maskBounds.contains(x, y))
                {
                    buf.put((byte)mask.getSample(x, y, 0));
                }
                else
                {
                    buf.put((byte)0);
                }
            }
        }
        return buf;
    }

    public byte[] getData(Rectangle region, byte[] buf)
    {
        Rectangle maskBounds = mask.getBounds();
        for (int j = 0; j < region.height; ++j)
        {
            int y = region.y + j;
            for (int i = 0; i < region.width; ++i)
            {
                int x = region.x + i;

                if (maskBounds.contains(x, y))
                {
                    buf[j * region.width + i] = (byte)mask.getSample(x, y, 0);
                }
                else
                {
                    buf[j * region.width + i] = 0;
                }
            }
        }
        return buf;
    }
}
