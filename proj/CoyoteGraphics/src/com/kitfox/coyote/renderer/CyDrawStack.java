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

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector4d;
import com.kitfox.coyote.math.d3.BoundingBox3d;
import com.kitfox.coyote.math.d3.Frustumd;
import com.kitfox.coyote.math.d3.PartitionPlacement;
import com.kitfox.coyote.shape.CyRectangle2d;
import java.util.ArrayList;

/**
 * Tool to ease the process of building CyDrawRecord trees.
 * 
 * <p>CyDrawStack maintains a stack of CyDrawGroups.  Whenever a 
 * record is submitted to the stack via addDrawRecord(), it is
 * added to the top level group.  New groups can be added by
 * calling pushFrame().  When this happens, the submitted group is 
 * appended as a child to the current top level group, and then 
 * replaces it.  When popFrame() is called, the topmost group is
 * taken off the stack and its parent is restored.</p>
 * 
 * <p>In addition to tracking groups, information that is typically
 * sensitive to ancestors is maintained.  Whenever the top group
 * is pushed, a backup copy of all matrix and opacity 
 * data is made.  This data is restored when the corresponding
 * pop call is made.</p>
 *
 * @author kitfox
 */
public class CyDrawStack
{
    private CyDrawStackFrame curFrame;
    private final int deviceWidth;
    private final int deviceHeight;

    ArrayList<CyMatrix4d> matrixPool = new ArrayList<CyMatrix4d>();

    /**
     *
     * @param deviceWidth Width of device we are drawing to
     * @param deviceHeight Height of device we are drawing to
     * @param drawGroup Top level group that will be the ancestor of
     * all draw records submitted
     */
    public CyDrawStack(
            int deviceWidth,
            int deviceHeight,
            CyDrawGroup drawGroup)
    {
        this.deviceWidth = deviceWidth;
        this.deviceHeight = deviceHeight;

        curFrame = new CyDrawStackFrame(this, drawGroup);
    }

    CyMatrix4d allocMatrix()
    {
        if (matrixPool.isEmpty())
        {
            return new CyMatrix4d();
        }
        return matrixPool.remove(matrixPool.size() - 1);
    }

    void freeMatrix(CyMatrix4d m)
    {
        matrixPool.add(m);
    }

    /**
     * Push the current state.  A new, blank rendering tile will be
     * allocated for all subsequent drawing operations.
     *
     * @param filter If not null, the tile will be processed by this
     * filter when it is popped.
     */
    public void pushFrame(CyDrawGroup drawGroup)
    {
        curFrame = new CyDrawStackFrame(curFrame, drawGroup);
    }

    /**
     * Composites the tile built from the current frame over its parent.
     * Pops the frame stack so that drawing operations revert to the
     * parent tile.
     */
    public void popFrame()
    {
        CyDrawStackFrame oldFrame = curFrame;
        //Deallocate frame and apply filter
        oldFrame.popping();
        curFrame = oldFrame.getParent();

//        curFrame.compositeTile(oldFrame);
    }

    public void addDrawRecord(CyDrawRecord rec)
    {
        curFrame.addDrawRecord(rec);
    }

    public float getOpacity()
    {
        return curFrame.getOpacity();
    }

    public void setOpacity(float opacity)
    {
        curFrame.setOpacity(opacity);
    }

    public void mulOpacity(float opacity)
    {
        curFrame.mulOpacity(opacity);
    }

    public CyMatrix4d getModelViewXform()
    {
        return new CyMatrix4d(curFrame.getModelView());
    }

    public CyMatrix4d getModelViewProjXform()
    {
        return new CyMatrix4d(curFrame.getModelViewProj());
    }

    public CyMatrix4d getModelViewProjXform(CyMatrix4d m)
    {
        m.set(curFrame.getModelViewProj());
        return m;
    }

    public Frustumd getFrustum()
    {
        return new Frustumd(curFrame.getFrustum());
    }

    public CyMatrix4d getModelXform(CyMatrix4d m)
    {
        m.set(curFrame.getModelXform());
        return m;
    }

    public CyMatrix4d getModelXform()
    {
        return new CyMatrix4d(curFrame.getModelXform());
    }

    public void setModelXform(CyMatrix4d m)
    {
        curFrame.setModelXform(m);
    }

    public void mulModelXform(CyMatrix4d m)
    {
        curFrame.mulModelXform(m);
    }

    public CyMatrix4d getViewXform()
    {
        return new CyMatrix4d(curFrame.getViewXform());
    }

    public void setViewXform(CyMatrix4d m)
    {
        curFrame.setViewXform(m);
    }

    public void mulViewXform(CyMatrix4d m)
    {
        curFrame.mulViewXform(m);
    }

    public CyMatrix4d getProjXform()
    {
        return new CyMatrix4d(curFrame.getProjXform());
    }

    public void setProjXform(CyMatrix4d m)
    {
        curFrame.setProjXform(m);
    }

    public void mulProjXform(CyMatrix4d m)
    {
        curFrame.mulProjXform(m);
    }

    public void dispose()
    {
        curFrame.popping();
    }

    public void translate(double x, double y, double z)
    {
        curFrame.translate(x, y, z);
    }

    public void rotX(double angle)
    {
        curFrame.rotX(angle);
    }

    public void rotY(double angle)
    {
        curFrame.rotY(angle);
    }

    public void rotZ(double angle)
    {
        curFrame.rotZ(angle);
    }

    public void scale(double x, double y, double z)
    {
        curFrame.scale(x, y, z);
    }

    /**
     * @return the viewportWidth
     */
    public int getDeviceWidth()
    {
        return deviceWidth;
    }

    /**
     * @return the viewportHeight
     */
    public int getDeviceHeight()
    {
        return deviceHeight;
    }

    /**
     * Rough test to see if passed box intersects with frustum under
     * model view transform.
     *
     * @param bounds
     * @return true if intersection occurs
     */
    public boolean intersectsFrustum(BoundingBox3d bounds)
    {
        CyVector4d center = new CyVector4d(
                bounds.getCenterX(),
                bounds.getCenterY(),
                bounds.getCenterZ(),
                1);
        CyVector4d dir = new CyVector4d(
                bounds.getX0() - center.x,
                bounds.getY0() - center.y,
                bounds.getZ0() - center.z,
                0);

        return intersectsFrustum(center, dir);
    }

    public boolean intersectsFrustum(CyRectangle2d rect)
    {
        CyVector4d center = new CyVector4d(
                rect.getCenterX(),
                rect.getCenterY(),
                0,
                1);
        CyVector4d radius = new CyVector4d(
                rect.getWidth() / 2, rect.getHeight() / 2, 0, 0);

        return intersectsFrustum(center, radius);
    }


    public boolean intersectsFrustum(CyVector4d center, CyVector4d radius)
    {
        CyMatrix4d model = curFrame.getModelXform();

        model.transform(radius);
        model.transform(center);
        if (center.w != 1)
        {
            center.scale(1 / center.w);
        }

        Frustumd frustum = curFrame.getFrustum();
        PartitionPlacement place =
                frustum.testBoundingSphere(
                radius.lengthSquared(), center.x, center.y, center.z);

        return place != PartitionPlacement.OUTSIDE;
    }

}
