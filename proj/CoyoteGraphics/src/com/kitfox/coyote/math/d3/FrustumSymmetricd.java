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

package com.kitfox.coyote.math.d3;

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector3d;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * A frustum that is symmetric about the XZ and YZ planes
 *
 * @author kitfox
 */
public class FrustumSymmetricd extends Frustumd
{
    //Describes bounding sphere of this frustum.
    private final CyVector3d boundsCenter = new CyVector3d();
    private double boundsRadius;

    //These points are characteristic of the points that compose this frutsum.
    // Since the frutsum is symmetric along the X and Y axes, only two 
    // characteristic points need to be stored.
    private final CyVector3d leftBottomNear = new CyVector3d();
    private final CyVector3d rightTopNear = new CyVector3d();
    private final CyVector3d rightTopFar = new CyVector3d();
    
    /** Creates a new instance of FrustumSymmetricf */
    public FrustumSymmetricd()
    {
    }

    public static FrustumSymmetricd create(CyMatrix4d viewProjection)
    {
        FrustumSymmetricd frustum = new FrustumSymmetricd();
        frustum.extractPlanes(viewProjection);
        return frustum;
    }
    
    /**
     * Extracts frustum from symmetric frustum matrix and calculates 
     * characteristic points.  Characteristic points will be incorrect if
     * frustum is not symmetric.
     */
    @Override
    public void extractPlanes(CyMatrix4d viewProjection)
    {
        super.extractPlanes(viewProjection);
        
        CyPlane4d left = planes[Plane.LEFT.ordinal()];
        CyPlane4d right = planes[Plane.RIGHT.ordinal()];
        CyPlane4d bottom = planes[Plane.BOTTOM.ordinal()];
        CyPlane4d top = planes[Plane.TOP.ordinal()];
        CyPlane4d near = planes[Plane.NEAR.ordinal()];
        CyPlane4d far = planes[Plane.FAR.ordinal()];
        
        //Calculate bounding sphere of frustum
        CyPlane4d.intersectionOf3Planes(left, bottom, near, leftBottomNear);
        CyPlane4d.intersectionOf3Planes(right, top, near, rightTopNear);
        CyPlane4d.intersectionOf3Planes(right, top, far, rightTopFar);

        //Midpoint separation plane of chord on bounding sphere contains center
        CyPlane4d sepPlane = CyPlane4d.separationPlane(leftBottomNear, rightTopFar, null);
        //We know center of bounding sphere lies on the line passing through center of near facet of frustum
        CyVector3d origin = new CyVector3d(rightTopNear);
        origin.add(leftBottomNear);
        origin.scale(.5f);
        final CyVector3d axis = new CyVector3d(near.x, near.y, near.z);
        sepPlane.intersectionLinePlane(origin, axis, boundsCenter);
        boundsRadius = boundsCenter.distance(leftBottomNear);
        
    }
    
    public CyVector3d getBoundsCenter()
    {
        return boundsCenter;
    }

    public double getBoundsRadius()
    {
        return boundsRadius;
    }

    public CyVector3d getLeftBottomNear()
    {
        return leftBottomNear;
    }

    public CyVector3d getRightTopFar()
    {
        return rightTopFar;
    }

    public CyVector3d getRightTopNear()
    {
        return rightTopNear;
    }
    
    @Override
    public String toString()
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.print(super.toString());
        pw.println("boundingCenter: " + boundsCenter);
        pw.println("boundingRadius: " + boundsRadius);
        
        pw.close();
        return sw.toString();
    }
}
