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

package com.kitfox.coyote.renderer.shader.mat.phong;

import com.kitfox.coyote.drawRecord.CyDrawRecordFactory;

/**
 *
 * @author kitfox
 */
public class CyMaterialPhongDrawRecordFactory
        extends CyDrawRecordFactory<CyMaterialPhongDrawRecord>
{
    static CyMaterialPhongDrawRecordFactory instance = new CyMaterialPhongDrawRecordFactory();

    private CyMaterialPhongDrawRecordFactory()
    {
    }

    public static CyMaterialPhongDrawRecordFactory inst()
    {
        return instance;
    }

    @Override
    protected CyMaterialPhongDrawRecord createRecord()
    {
        CyMaterialPhongDrawRecord rec = new CyMaterialPhongDrawRecord();
        return rec;
    }

    @Override
    protected void recycleRecord(CyMaterialPhongDrawRecord rec)
    {
        super.recycleRecord(rec);
    }

}
