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

package com.kitfox.raven.editor.node.scene;

import com.kitfox.cache.CacheElement;
import com.kitfox.cache.CacheList;
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import com.kitfox.raven.editor.node.RavenNode;
import com.kitfox.raven.util.planeData.PlaneDataProvider;
import com.kitfox.raven.util.planeData.PlaneDataProviderIndex;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperString;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class RavenNodeDataPlane extends RavenNode
{
    //Used to lookup provider for data type that can be serialized with CacheMap
    public static final String PROP_DATA_TYPE = "dataType";
    public final PropertyWrapperString<RavenNodeDataPlane> dataType =
            new PropertyWrapperString(this, PROP_DATA_TYPE, PropertyWrapper.FLAGS_NOANIM);

    //Blob of CacheMap data that defines array of type 'dataType'
    public static final String PROP_DATA_VALUES = "dataValues";
    public final PropertyWrapperString<RavenNodeDataPlane> dataValues =
            new PropertyWrapperString(this, PROP_DATA_VALUES);

    protected RavenNodeDataPlane(int uid)
    {
        super(uid);

//        PropertyWrapperAdapter adapt = new PropertyWrapperAdapter()
//        {
//            @Override
//            public void propertyWrapperDataChanged(PropertyChangeEvent evt) {
//                clearCache();
//            }
//        };
//
//        dataType.addPropertyWrapperListener(adapt);
//        dataValues.addPropertyWrapperListener(adapt);
    }

    public Class<? extends PlaneDataProvider> getPlaneDataType()
    {
        String strn = dataType.getValue();
        PlaneDataProvider prov = PlaneDataProviderIndex.inst().getProvider(strn);
        return prov == null ? null : prov.getClass();
    }

    public ArrayList getPlaneData()
    {
        String strn = dataType.getValue();
        PlaneDataProvider prov = PlaneDataProviderIndex.inst().getProvider(strn);
        if (prov == null)
        {
            return null;
        }

        String dataText = dataValues.getValue();
        CacheList cacheList = null;
        try
        {
            CacheElement ele = CacheParser.parse(dataText);
            if (ele instanceof CacheList)
            {
                cacheList = (CacheList)ele;
            }
        } catch (ParseException ex)
        {
            Logger.getLogger(RavenNodeDataPlane.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (cacheList == null)
        {
            return null;
        }

        ArrayList ret = new ArrayList(cacheList.size());
        for (int i = 0; i < cacheList.size(); ++i)
        {
            ret.add(prov.parse(cacheList.get(i)));
        }

        return ret;
    }

    public <T extends PlaneDataProvider<R>, R> void setPlaneData(
            Class<T> type,
            List<R> data, boolean history)
    {
        NodeDocument doc = getDocument();
        if (history && doc != null)
        {
            doc.getHistory().beginTransaction("Set plane data");
        }

        dataType.setValue(type.getCanonicalName(), history);

        CacheList cacheList = new CacheList();
        PlaneDataProvider prov = PlaneDataProviderIndex.inst().getProvider(type);
        for (int i = 0; i < data.size(); ++i)
        {
            R val = data.get(i);
            CacheElement ele = prov.asCache(val);

            cacheList.add(ele);
        }

        dataValues.setValue(cacheList.toString(), history);

        if (history && doc != null)
        {
            doc.getHistory().commitTransaction();
        }
    }

//    private void clearCache()
//    {
//    }

    
    //-----------------------------------------------
    
    @ServiceInst(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<RavenNodeDataPlane>
    {
        public Provider()
        {
            super(RavenNodeDataPlane.class, "Data Plane", "/icons/node/dataPlane.png");
        }

        @Override
        public RavenNodeDataPlane createNode(int uid)
        {
            return new RavenNodeDataPlane(uid);
        }
    }
}
