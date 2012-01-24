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

    CyDrawGroup drawGroup;

    //Local to world
    private CyMatrix4d modelXform;
    //Camera to world.  model * view^-1 forms the modelview matrix.
    private CyMatrix4d viewXform;
    //Projection
    private CyMatrix4d projXform;

    //Derrived matrices
    boolean modelViewDirty;
    boolean modelViewProjDirty;
    boolean viewProjDirty;
    private CyMatrix4d modelViewXform;
    private CyMatrix4d modelViewProjXform;
    private CyMatrix4d viewProjXform;
    private CyMatrix4d viewIXform;

    boolean frustumDirty;
    private Frustumd frustum;

    private float opacity;

    /**
     * Called by RavenRenderer the when the first frame is allocated
     *
     * @param stack
     * @param tileArea
     * @param tileBuffer
     */
    public CyDrawStackFrame(CyDrawStack stack,
            CyDrawGroup drawGroup)
    {
        this.parent = null;
        this.stack = stack;
        this.drawGroup = drawGroup;

        //Defaults
        modelXform = CyMatrix4d.createIdentity();
        viewXform = CyMatrix4d.createIdentity();
        projXform = CyMatrix4d.createIdentity();

        modelViewXform = CyMatrix4d.createIdentity();
        viewIXform = CyMatrix4d.createIdentity();
        modelViewProjXform = CyMatrix4d.createIdentity();
        viewProjXform = CyMatrix4d.createIdentity();
        frustum = Frustumd.create(viewProjXform);

        opacity = 1;
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

        modelXform = new CyMatrix4d(frame.modelXform);
        viewXform = new CyMatrix4d(frame.viewXform);
        projXform = new CyMatrix4d(frame.projXform);

        modelViewXform = new CyMatrix4d(frame.modelViewXform);
        viewIXform = new CyMatrix4d(frame.viewIXform);
        modelViewProjXform = new CyMatrix4d(frame.modelViewProjXform);
        viewProjXform = new CyMatrix4d(frame.viewProjXform);
        frustum = new Frustumd(frame.frustum);

        opacity = frame.opacity;
        modelViewDirty = frame.modelViewDirty;
        modelViewProjDirty = frame.modelViewProjDirty;
        viewProjDirty = frame.viewProjDirty;
        frustumDirty = frame.frustumDirty;
    }

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
