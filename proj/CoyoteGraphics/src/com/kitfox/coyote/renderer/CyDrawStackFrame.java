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

package com.kitfox.coyote.renderer;

import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.d3.Frustumd;

/**
 *
 * @author kitfox
 */
public class CyDrawStackFrame
{
    private CyDrawStackFrame parent;
    CyDrawStack stack;

//    private CyRectangle2i tileArea;
//    private final CyFramebuffer tileBuffer;
//
//    //If filtering is used, an extra tile buffer is used
//    private final CyFilter filter;
//    private CyRectangle2i tileAreaPostFilter;
//    private final CyFramebuffer tileBufferPostFilter;
//    private final FilteredTile filteredTile;
    CyDrawGroup drawGroup;

    //Local to world
    private CyMatrix4d modelXform;
    //Camera to world.  model * view^-1 forms the modelview matrix.
    private CyMatrix4d viewXform;
    //Projection
    private CyMatrix4d projXform;
    //Expands clip to just show the bit our tile needs
//    private CyMatrix4d tileXform;

    //Derrived matrices
//    boolean viewIDirty;
    boolean modelViewDirty;
    boolean modelViewProjDirty;
    boolean viewProjDirty;
//    boolean projDirty;
    private CyMatrix4d modelViewXform;
    private CyMatrix4d modelViewProjXform;
    private CyMatrix4d viewProjXform;
    private CyMatrix4d viewIXform;
//    private CyMatrix4d projTileXform;

    boolean frustumDirty;
    private Frustumd frustum;

    private float opacity;

//    CyMaterial material;
//    //Transform applied to tex coords
//    private CyMatrix4d textureXform;

    /**
     * Called by RavenRenderer the when the first frame is allocated
     *
     * @param stack
     * @param tileArea
     * @param tileBuffer
     */
    public CyDrawStackFrame(CyDrawStack stack,
            CyDrawGroup drawGroup)
//            CyRectangle2i tileArea,
//            CyFramebuffer tileBuffer,
//            CyFilter filter)
    {
        this.parent = null;
        this.stack = stack;
        this.drawGroup = drawGroup;
//        this.filter = filter;
//        if (filter == null)
//        {
//            this.tileArea = tileArea;
//            this.tileBuffer = tileBuffer;
//            this.tileBufferPostFilter = null;
//            this.tileAreaPostFilter = null;
//            this.filteredTile = null;
//        }
//        else
//        {
//            //Passed in tile will be desination of data after filtering
//            this.tileAreaPostFilter = tileArea;
//            this.tileBufferPostFilter = tileBuffer;
//
//            //Create a new tile for pre-filter info
//            this.tileArea =
//                    filter.calcSampleRegion(tileArea, null);
//            this.filteredTile = stack.getTileCache().allocTile(
//                    this.tileArea.getWidth(),
//                    this.tileArea.getHeight());
//            this.tileBuffer = filteredTile.getBuffer();
//        }
//
//        //We are now writing to this buffer
//        tileBuffer.bind(stack.getGl());

        //Defaults
        modelXform = CyMatrix4d.createIdentity();
        viewXform = CyMatrix4d.createIdentity();
        projXform = CyMatrix4d.createIdentity();

//        tileXform = calcTileXform(this.tileArea);

        modelViewXform = CyMatrix4d.createIdentity();
        viewIXform = CyMatrix4d.createIdentity();
//        projTileXform = new CyMatrix4d(tileXform);
        modelViewProjXform = CyMatrix4d.createIdentity();
        viewProjXform = CyMatrix4d.createIdentity();
        frustum = Frustumd.create(viewProjXform);

        opacity = 1;
//        material = CyMaterialColor.WHITE;
//        textureXform = CyMatrix4d.createIdentity();

//        updateLocalXform();
    }

    /**
     * Called by RavenRenderer in response to pushing a frame
     *
     * @param frame
     * @param filter
     */
    CyDrawStackFrame(CyDrawStackFrame frame,
            CyDrawGroup drawGroup)
    {
        this.parent = frame;
        this.stack = frame.stack;

        if (drawGroup == null)
        {
            this.drawGroup = frame.drawGroup;
        }
        else
        {
            frame.drawGroup.addRecord(drawGroup);
            this.drawGroup = drawGroup;
        }

//        this.filter = filter;
//        if (filter == null)
//        {
//            this.tileBuffer = frame.tileBuffer;
//            this.tileArea = frame.tileArea;
//            this.tileBufferPostFilter = null;
//            this.tileAreaPostFilter = null;
//            this.filteredTile = null;
//        }
//        else
//        {
//            //Passed in tile will be desination of data after filtering
//            this.tileAreaPostFilter = frame.tileArea;
//            this.tileBufferPostFilter = frame.tileBuffer;
//
//            //Create a new tile for pre-filter info
//            this.tileArea =
//                    filter.calcSampleRegion(frame.tileArea, null);
//            this.filteredTile = stack.getTileCache().allocTile(
//                    frame.tileArea.getWidth(),
//                    frame.tileArea.getHeight());
//            this.tileBuffer = filteredTile.getBuffer();
//
//            //Write to prefilter buffer
//            tileBuffer.bind(stack.getGl());
////            renderer.gl.glViewport(0, 0, tileArea.getWidth(), tileArea.getHeight());
//        }

        modelXform = new CyMatrix4d(frame.modelXform);
        viewXform = new CyMatrix4d(frame.viewXform);
        projXform = new CyMatrix4d(frame.projXform);

//        tileXform = calcTileXform(this.tileArea);

        modelViewXform = new CyMatrix4d(frame.modelViewXform);
        viewIXform = new CyMatrix4d(frame.viewIXform);
//        projTileXform = new CyMatrix4d(tileXform);
//        projTileXform.mul(projXform);
        modelViewProjXform = new CyMatrix4d(frame.modelViewProjXform);
        viewProjXform = new CyMatrix4d(frame.viewProjXform);
        frustum = new Frustumd(frame.frustum);

        opacity = frame.opacity;
//        material = frame.material;
//        textureXform = new CyMatrix4d(frame.textureXform);

//        viewIDirty = frame.viewIDirty;
        modelViewDirty = frame.modelViewDirty;
        modelViewProjDirty = frame.modelViewProjDirty;
        viewProjDirty = frame.viewProjDirty;
//        projDirty = true;
        frustumDirty = frame.frustumDirty;
//        updateLocalXform();
    }

//    private CyMatrix4d calcTileXform(CyRectangle2i tileArea)
//    {
//        tileXform = CyMatrix4d.createIdentity();
//        double tx = tileArea.getMinX() / (double)stack.getViewportWidth();
//        double ty = tileArea.getMinY() / (double)stack.getViewportHeight();
//        double tw = tileArea.getWidth() / (double)stack.getViewportWidth();
//        double th = tileArea.getHeight() / (double)stack.getViewportHeight();
//
//        tileXform.translate(-1, -1, 0);
//        tileXform.scale(2, 2, 1);
//
//        tileXform.scale(1 / tw, 1 / th, 1);
////        tileXform.scale(tw, th, 1);
//        tileXform.translate(-tx, -ty, 0);
//
//        tileXform.scale(.5, .5, 1);
//        tileXform.translate(1, 1, 0);
//
////            renderer.gl.glViewport(0, 0, tileArea.getWidth(), tileArea.getHeight());
//        return tileXform;
//    }

    public void popping()
    {
//        if (filter != null)
//        {
//            filter.apply(tileBuffer, tileArea,
//                    tileBufferPostFilter, tileAreaPostFilter);
//            filteredTile.returnTile();
//            tileBufferPostFilter.bind(stack.getGl());
//        }
    }

    /**
     * @return the xform
     */
    CyMatrix4d getModelXform()
    {
        return modelXform;
    }

    /**
     * @param xform the xform to set
     */
    void mulModelXform(CyMatrix4d xform)
    {
        this.modelXform.mul(xform);
        modelViewDirty = true;
        modelViewProjDirty = true;
//        frustumDirty = true;
    }

    /**
     * @param xform the xform to set
     */
    void setModelXform(CyMatrix4d xform)
    {
        this.modelXform.set(xform);
        modelViewDirty = true;
        modelViewProjDirty = true;
//        frustumDirty = true;
    }

    /**
     * @return the xform
     */
    CyMatrix4d getViewXform()
    {
        return viewXform;
    }

    void mulViewXform(CyMatrix4d xform)
    {
        this.viewXform.mul(xform);
        modelViewDirty = true;
//        viewIDirty = true;
        modelViewProjDirty = true;
        viewProjDirty = true;
        frustumDirty = true;
    }

    void setViewXform(CyMatrix4d xform)
    {
        this.viewXform.set(xform);
        modelViewDirty = true;
//        viewIDirty = true;
        modelViewProjDirty = true;
        viewProjDirty = true;
        frustumDirty = true;
    }

    /**
     * @return the xform
     */
    CyMatrix4d getProjXform()
    {
        return projXform;
    }

    void mulProjXform(CyMatrix4d xform)
    {
        this.projXform.mul(xform);
//        projDirty = true;
        modelViewProjDirty = true;
        viewProjDirty = true;
        frustumDirty = true;
    }

    void setProjXform(CyMatrix4d xform)
    {
        this.projXform.set(xform);
//        projDirty = true;
        modelViewProjDirty = true;
        viewProjDirty = true;
        frustumDirty = true;
    }

//    CyMatrix4d getViewI()
//    {
//        if (viewIDirty)
//        {
//            viewIDirty = false;
//            viewIXform.set(viewXform);
//            viewIXform.invert();
//        }
//        return viewIXform;
//    }

    CyMatrix4d getModelView()
    {
        if (modelViewDirty)
        {
            modelViewDirty = false;
            modelViewXform.set(viewXform);
            modelViewXform.mul(modelXform);
        }
        return modelViewXform;
    }
//
//    CyMatrix4d getProjTile()
//    {
//        if (projDirty)
//        {
//            projDirty = false;
//            projTileXform.set(tileXform);
//            projTileXform.mul(projXform);
//        }
//        return projTileXform;
//    }

    CyMatrix4d getModelViewProj()
    {
        if (modelViewProjDirty)
        {
            modelViewProjDirty = false;
            modelViewProjXform.set(getProjXform());
            modelViewProjXform.mul(getModelView());
        }
        return modelViewProjXform;
    }

    CyMatrix4d getViewProj()
    {
        if (viewProjDirty)
        {
            viewProjDirty = false;
            viewProjXform.set(getProjXform());
            viewProjXform.mul(viewXform);
        }
        return viewProjXform;
    }

    Frustumd getFrustum()
    {
        if (frustumDirty)
        {
            frustumDirty = false;
            CyMatrix4d m = getViewProj();
            frustum.extractPlanes(m);
        }
        return frustum;
    }

    /**
     * @return the opacity
     */
    float getOpacity()
    {
        return opacity;
    }

    /**
     * @param opacity the opacity to set
     */
    void setOpacity(float opacity)
    {
        this.opacity = opacity;
    }

    /**
     * @param opacity the opacity to set
     */
    void mulOpacity(float opacity)
    {
        this.opacity *= opacity;
    }

    /**
     * @return the parent
     */
    CyDrawStackFrame getParent()
    {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    void setParent(CyDrawStackFrame parent)
    {
        this.parent = parent;
    }

//    void clear(CyColor4f color)
//    {
//        stack.getGl().glClearColor(color.r, color.g, color.b, color.a);
//        stack.getGl().glClear(true, true, true);
//    }

    void translate(double x, double y, double z)
    {
        modelXform.translate(x, y, z);
        modelViewDirty = true;
        modelViewProjDirty = true;
//        frustumDirty = true;
    }

    void rotX(double angle)
    {
        modelXform.rotX(angle);
        modelViewDirty = true;
        modelViewProjDirty = true;
//        frustumDirty = true;
    }

    void rotY(double angle)
    {
        modelXform.rotY(angle);
        modelViewDirty = true;
        modelViewProjDirty = true;
//        frustumDirty = true;
    }

    void rotZ(double angle)
    {
        modelXform.rotZ(angle);
        modelViewDirty = true;
        modelViewProjDirty = true;
//        frustumDirty = true;
    }

    void scale(double x, double y, double z)
    {
        modelXform.scale(x, y, z);
        modelViewDirty = true;
        modelViewProjDirty = true;
//        frustumDirty = true;
    }

    void addDrawRecord(CyDrawRecord rec)
    {
        drawGroup.addRecord(rec);
    }

}
