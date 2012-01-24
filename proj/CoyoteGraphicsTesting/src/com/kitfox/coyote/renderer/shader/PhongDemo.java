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

package com.kitfox.coyote.renderer.shader;

import com.kitfox.coyote.drawRecord.CyDrawRecordClear;
import com.kitfox.coyote.drawRecord.CyDrawRecordViewport;
import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector3d;
import com.kitfox.coyote.renderer.CyDrawRecord;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyRendererListener;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.coyote.renderer.shader.mat.phong.CyMaterialPhongDrawRecord;
import com.kitfox.coyote.renderer.shader.mat.phong.CyMaterialPhongDrawRecordFactory;
import java.net.URL;
import java.util.Random;

/**
 *
 * @author kitfox
 */
public class PhongDemo implements CyRendererListener
{
    CyVertexBuffer buffer;
    private double yaw = 90;
    private double pitch;

    public PhongDemo()
    {
        init();
    }
    
    private void init()
    {
        URL url = PhongDemo.class.getResource("/stanford_bunny.obj");
        ObjMeshProvider loader = new ObjMeshProvider(url);
        buffer = new CyVertexBuffer(loader);
    }

    Random rand = new Random();
    
    @Override
    public void render(CyDrawStack rend)
    {
        {
//            rend.addDrawRecord(new CyDrawRecordClear(0, 1, 1, 1,
//                    true, true, true));
            rend.addDrawRecord(new CyDrawRecordClear(
                    rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 1,
                    true, true, true));
        }
        
        //CyDrawRecord d;
        {
            CyDrawRecordViewport rec = new CyDrawRecordViewport(
                    0, 0, rend.getDeviceWidth(), rend.getDeviceHeight());
            rend.addDrawRecord(rec);
        }

        {
            CyMatrix4d xform = new CyMatrix4d();
            
            xform.gluPerspective(60, 1, .1, 1000);
            rend.setProjXform(xform);
            
            double cosPitch = Math.cos(Math.toRadians(pitch));
            double eyeX = Math.cos(Math.toRadians(yaw)) * cosPitch;
            double eyeZ = Math.sin(Math.toRadians(yaw)) * cosPitch;
            double eyeY = Math.sin(Math.toRadians(pitch));
            
            eyeX *= 5;
            eyeY *= 5;
            eyeZ *= 5;
            
            xform.gluLookAt(eyeX, eyeY, eyeZ,
                    0, 0, 0,
                    0, 1, 0);
            rend.setViewXform(xform);
        }
        
        {
            CyMaterialPhongDrawRecord rec = 
                    CyMaterialPhongDrawRecordFactory.inst().allocRecord();
            
            rec.setColor(CyColor4f.RED);
            rec.setLightPos(new CyVector3d(3, 3, -2));
            rec.setMvMatrix(rend.getModelViewXform());
            rec.setMvpMatrix(rend.getModelViewProjXform());
            rec.setMesh(buffer);
            
            rend.addDrawRecord(rec);
        }
    }

    /**
     * @return the yaw
     */
    public double getYaw()
    {
        return yaw;
    }

    /**
     * @param yaw the yaw to set
     */
    public void setYaw(double yaw)
    {
        this.yaw = yaw;
    }

    /**
     * @return the pitch
     */
    public double getPitch()
    {
        return pitch;
    }

    /**
     * @param pitch the pitch to set
     */
    public void setPitch(double pitch)
    {
        this.pitch = pitch;
    }
    
}
