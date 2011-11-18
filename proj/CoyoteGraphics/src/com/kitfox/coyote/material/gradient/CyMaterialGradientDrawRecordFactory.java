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

package com.kitfox.coyote.material.gradient;

import com.kitfox.coyote.drawRecord.CyDrawRecordFactory;

/**
 *
 * @author kitfox
 */
public class CyMaterialGradientDrawRecordFactory
        extends CyDrawRecordFactory<CyMaterialGradientDrawRecord>
{
    static CyMaterialGradientDrawRecordFactory instance = new CyMaterialGradientDrawRecordFactory();

    private CyMaterialGradientDrawRecordFactory()
    {
    }

    public static CyMaterialGradientDrawRecordFactory inst()
    {
        return instance;
    }

    @Override
    protected CyMaterialGradientDrawRecord createRecord()
    {
        CyMaterialGradientDrawRecord rec = new CyMaterialGradientDrawRecord();
        return rec;
    }

    @Override
    protected void recycleRecord(CyMaterialGradientDrawRecord rec)
    {
        super.recycleRecord(rec);
    }

}
