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

package com.kitfox.raven.shape.network;

import com.kitfox.cache.CacheList;
import com.kitfox.cache.CacheMap;
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import com.kitfox.coyote.shape.bezier.BezierCurve2i;
import com.kitfox.coyote.shape.bezier.mesh.BezierVertexSmooth;
import com.kitfox.coyote.shape.bezier.path.BezierLoop2i;
import com.kitfox.coyote.shape.bezier.path.BezierLoopClosure;
import com.kitfox.coyote.shape.bezier.path.BezierPath2i;
import com.kitfox.coyote.shape.bezier.path.BezierPathEdge2i;
import com.kitfox.coyote.shape.bezier.path.BezierPathVertex2i;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import com.kitfox.coyote.shape.bezier.path.cut.GraphDataManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class NetworkPath extends BezierPath2i<NetworkDataVertex, NetworkDataEdge>
{
    public static final String CACHE_NAME = "networkPath";
    public static final String PROP_VERTS = "verts";
    public static final String PROP_EDGES = "edges";
    public static final String PROP_LOOPS = "loops";
    public static final String PROP_LOOP_ID = "loopId";
    public static final String PROP_LOOP_HEAD = "loopHead";
    public static final String PROP_LOOP_TAIL = "loopTail";
    public static final String PROP_LOOP_CLOSURE = "loopClosure";
    public static final String PROP_DATAKEYS = "dataKeys";
    public static final String PROP_DATAVALS = "dataVals";

    public static final double FLATNESS_SQ = 10000;

    static final DataManager dataMgr = new DataManager();
    
    public NetworkPath()
    {
        super(new DataManager());
    }

    public NetworkPath(NetworkPath path)
    {
        super(path);
    }
    
    public static NetworkPath create(String text)
    {
        if (text == null || "".equals(text))
        {
            return new NetworkPath();
        }
        
        try
        {
            CacheMap map = (CacheMap)CacheParser.parse(text);

            CacheLoader loader = new CacheLoader();
            return loader.load(map);
        }
        catch (ParseException ex)
        {
            Logger.getLogger(NetworkMesh.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
    public CacheMap toCache()
    {
        CacheBuilder builder = new CacheBuilder();
        return builder.build();
    }
    
    @Override
    public String toString()
    {
        return toCache().toString();
    }
    
    @Override
    protected void createVertex(int id, int x, int y, NetworkDataVertex dataMap)
    {
        super.createVertex(id, x, y, dataMap);
    }
    
    @Override
    protected BezierPathEdge2i<NetworkDataEdge> createEdge(int id, 
            BezierPathVertex2i start, BezierPathVertex2i end,
            NetworkDataEdge data,
            BezierVertexSmooth smooth0,
            BezierVertexSmooth smooth1,
            int k0x, int k0y, int k1x, int k1y)
    {
        return super.createEdge(id, start, end, 
                data, smooth0, smooth1, k0x, k0y, k1x, k1y);
    }
    
    @Override
    protected void createLoop(int id, 
            BezierPathVertex2i head, BezierPathVertex2i tail,
            BezierLoopClosure closure)
    {
        super.createLoop(id, head, tail, closure);
    }

    public BezierPathEdge2i getEdge(int index)
    {
        return edgeMap.get(index);
    }

    //---------------------------
    static class DataManager
            implements GraphDataManager<NetworkDataVertex, NetworkDataEdge>
    {
        @Override
        public NetworkDataVertex copyVertexData(NetworkDataVertex data)
        {
            return data == null ? null : new NetworkDataVertex(data);
        }

        @Override
        public NetworkDataEdge copyEdgeData(NetworkDataEdge data)
        {
            return data == null ? null : new NetworkDataEdge(data);
        }

        @Override
        public NetworkDataEdge createDefaultEdgeData(BezierCurve2i c)
        {
            return new NetworkDataEdge();
        }

        @Override
        public NetworkDataVertex createDefaultVertexData(Coord c)
        {
            return new NetworkDataVertex();
        }
    }

    class CacheBuilder
    {
        HashMap<Class<? extends NetworkDataType>, Integer> dataKeyMap
                = new HashMap<Class<? extends NetworkDataType>, Integer>();
        ArrayList<Class<? extends NetworkDataType>> dataKeyIdx 
                = new ArrayList<Class<? extends NetworkDataType>>();
        
        HashMap<String, Integer> dataValueMap = new HashMap<String, Integer>();
        ArrayList<String> dataValueIdx = new ArrayList<String>();
        
        private Integer getDataValueIdx(String val)
        {
            Integer i = dataValueMap.get(val);
            if (i == null)
            {
                i = dataValueIdx.size();
                dataValueIdx.add(val);
                dataValueMap.put(val, i);
            }
            return i;
        }

        private Integer getDataKeyIdx(Class<? extends NetworkDataType> key)
        {
            Integer i = dataKeyMap.get(key);
            if (i == null)
            {
                i = dataKeyIdx.size();
                dataKeyIdx.add(key);
                dataKeyMap.put(key, i);
            }
            return i;
        }

        private String getSmoothCode(BezierVertexSmooth smooth)
        {
            switch (smooth)
            {
                case CORNER:
                    return "c";
                case SMOOTH:
                    return "s";
                case AUTO_SMOOTH:
                    return "a";
                case FREE:
                    return "f";
            }
            throw new IllegalArgumentException();
        }

        private String getClosureCode(BezierLoopClosure closure)
        {
            switch (closure)
            {
                case OPEN:
                    return "o";
                case CLOSED_CLAMPED:
                    return "c";
                case CLOSED_FREE:
                    return "f";
            }
            throw new IllegalArgumentException();
        }
        
        private CacheList buildData(HashMap<Class<? extends NetworkDataType>, Object> dataMap)
        {
            CacheList list = new CacheList();

            if (dataMap == null)
            {
                return list;
            }
            
            for (Class<? extends NetworkDataType> key: dataMap.keySet())
            {
                Integer keyIdx = getDataKeyIdx(key);

                NetworkDataType type = 
                        NetworkDataTypeIndex.inst().getServiceByClass(key);
                Object value = dataMap.get(key);
                String valText = type.asText(value);
                Integer valIdx = getDataValueIdx(valText);

                CacheList tuple = new CacheList(keyIdx, valIdx);
                list.add(tuple);
            }
            
            return list;
        }
        
        private CacheList buildVert(BezierPathVertex2i v)
        {
            CacheList list = new CacheList();
            
            list.add(v.getId());
            Coord c = v.getCoord();
            list.add(c.x);
            list.add(c.y);
            NetworkDataVertex data = (NetworkDataVertex)v.getData();
            list.add(buildData(data == null ? null : data.dataMap));
            
            return list;
        }
        
        private CacheList buildEdge(BezierPathEdge2i e)
        {
            CacheList list = new CacheList();
            
            list.add(e.getId());
            list.add(e.getStart().getId());
            list.add(e.getEnd().getId());
            list.add(getSmoothCode(e.getSmooth0()));
            list.add(getSmoothCode(e.getSmooth1()));
            list.add(e.getK0x());
            list.add(e.getK0y());
            list.add(e.getK1x());
            list.add(e.getK1y());
            NetworkDataEdge data = (NetworkDataEdge)e.getData();
            list.add(buildData(data == null ? null : data.dataMapEdge));
            list.add(buildData(data == null ? null : data.dataMapLeft));
            list.add(buildData(data == null ? null : data.dataMapRight));
            
            return list;
        }
        
        private CacheMap buildLoop(BezierLoop2i loop)
        {
            CacheMap map = new CacheMap();
            
            map.put(PROP_LOOP_ID, loop.getId());
            map.put(PROP_LOOP_HEAD, loop.getHead().getId());
            map.put(PROP_LOOP_TAIL, loop.getTail().getId());
            map.put(PROP_LOOP_CLOSURE, getClosureCode(loop.getClosure()));
            
            return map;
        }
        
        public CacheMap build()
        {
            CacheMap map = new CacheMap(CACHE_NAME);

            CacheList vertList = new CacheList();
            map.put(PROP_VERTS, vertList);
            for (BezierPathVertex2i v: vertMap.values())
            {
                vertList.add(buildVert(v));
            }
            
            CacheList edgeList = new CacheList();
            map.put(PROP_EDGES, edgeList);
            for (BezierPathEdge2i e: edgeMap.values())
            {
                edgeList.add(buildEdge(e));
            }
            
            CacheList loopList = new CacheList();
            map.put(PROP_LOOPS, loopList);
            for (BezierLoop2i loop: loops)
            {
                CacheMap loopMap = buildLoop(loop);
                loopList.add(loopMap);
            }

            //Store data keys
            {
                CacheList list = new CacheList();
                map.put(PROP_DATAKEYS, list);

                for (Class<? extends NetworkDataType> key: dataKeyIdx)
                {
                    list.add(key.getName());
                }
            }

            //Store data values
            {
                CacheList list = new CacheList();
                map.put(PROP_DATAVALS, list);

                for (String val: dataValueIdx)
                {
                    list.add(val);
                }
            }

            return map;
        }
    }
    
    static class CacheLoader
    {
        ArrayList<Class<? extends NetworkDataType>> dataKeyIdx 
                = new ArrayList<Class<? extends NetworkDataType>>();
        ArrayList<String> dataValueIdx = new ArrayList<String>();
        
//        int vertX;
//        int vertY;
//        BezierVertexSmooth vertSmooth;
//        HashMap<Class<? extends NetworkDataType>, Object> vertDataMap;
//        
//        int order;
//        int k0x;
//        int k0y;
//        int k1x;
//        int k1y;
//        HashMap<Class<? extends NetworkDataType>, Object> dataMapEdge;
//        HashMap<Class<? extends NetworkDataType>, Object> dataMapLeft;
//        HashMap<Class<? extends NetworkDataType>, Object> dataMapRight;

        
        private BezierVertexSmooth getSmooth(String text)
        {
            switch (text.charAt(0))
            {
                case 'c':
                    return BezierVertexSmooth.CORNER;
                case 's':
                    return BezierVertexSmooth.SMOOTH;
                case 'a':
                    return BezierVertexSmooth.AUTO_SMOOTH;
                case 'f':
                    return BezierVertexSmooth.FREE;
            }
            throw new IllegalArgumentException();
        }
        
        private BezierLoopClosure getClosure(String text)
        {
            switch (text.charAt(0))
            {
                case 'o':
                    return BezierLoopClosure.OPEN;
                case 'c':
                    return BezierLoopClosure.CLOSED_CLAMPED;
                case 'f':
                    return BezierLoopClosure.CLOSED_FREE;
            }
            throw new IllegalArgumentException();
        }
        
        private HashMap<Class<? extends NetworkDataType>, Object>
                loadDataMap(CacheList list)
        {
            HashMap<Class<? extends NetworkDataType>, Object> dataMap
                    = new HashMap<Class<? extends NetworkDataType>, Object>();
            
            for (int i = 0; i < list.size(); ++i)
            {
                CacheList tuple = (CacheList)list.get(i);
                Class<? extends NetworkDataType> key = 
                        dataKeyIdx.get(tuple.getInteger(0, -1));
                String text = 
                        dataValueIdx.get(tuple.getInteger(1, -1));
                
                NetworkDataType type = 
                        NetworkDataTypeIndex.inst().getServiceByClass(key);
                Object value = type.fromText(text);
                
                dataMap.put(key, value);
            }
            
            return dataMap;
        }
        
        private void loadVertex(NetworkPath path, CacheList list)
        {
            int id = list.getInteger(0, -1);
            int x = list.getInteger(1, 0);
            int y = list.getInteger(2, 0);
            
            HashMap<Class<? extends NetworkDataType>, Object> dataMap =
                    loadDataMap((CacheList)list.get(3));
            
            path.createVertex(id, x, y, new NetworkDataVertex(dataMap));
        }
        
        private void loadEdge(NetworkPath path, CacheList list)
        {
            int id = list.getInteger(0, -1);
            int idV0 = list.getInteger(1, -1);
            int idV1 = list.getInteger(2, -1);
            BezierPathVertex2i head = path.getVertex(idV0);
            BezierPathVertex2i tail = path.getVertex(idV1);
            
            BezierVertexSmooth smooth0 = getSmooth(
                    list.getString(3, "s"));
            BezierVertexSmooth smooth1 = getSmooth(
                    list.getString(4, "s"));
            int k0x = list.getInteger(5, 0);
            int k0y = list.getInteger(6, 0);
            int k1x = list.getInteger(7, 0);
            int k1y = list.getInteger(8, 0);
            
            HashMap<Class<? extends NetworkDataType>, Object> 
                    dataMapEdge = loadDataMap((CacheList)list.get(9));
            HashMap<Class<? extends NetworkDataType>, Object> 
                    dataMapLeft = loadDataMap((CacheList)list.get(9));
            HashMap<Class<? extends NetworkDataType>, Object> 
                    dataMapRight = loadDataMap((CacheList)list.get(9));
            
            NetworkDataEdge edgeData = new NetworkDataEdge(
                    dataMapEdge, dataMapLeft, dataMapRight);
            
            BezierPathEdge2i e = path.createEdge(id, 
                    head, tail,
                    edgeData, 
                    smooth0, smooth1,
                    k0x, k0y, k1x, k1y);
            head.setEdgeOut(e);
            tail.setEdgeIn(e);
        }

        private void loadLoop(NetworkPath path, CacheMap map)
        {   
            int id = map.getInteger(PROP_LOOP_ID, -1);
            int idHead = map.getInteger(PROP_LOOP_HEAD, -1);
            int idTail = map.getInteger(PROP_LOOP_TAIL, -1);
            BezierLoopClosure closure =
                    getClosure(map.getString(PROP_LOOP_CLOSURE, "o"));
            
            BezierPathVertex2i head = path.getVertex(idHead);
            BezierPathVertex2i tail = path.getVertex(idTail);
            
            path.createLoop(id, head, tail, closure);
        }
        
        public NetworkPath load(CacheMap cacheMap)
        {
            NetworkPath path = new NetworkPath();
            
            {
                CacheList list = (CacheList)cacheMap.get(PROP_DATAKEYS);
                for (int i = 0; i < list.size(); ++i)
                {
                    String name = list.getString(i, null);
                    NetworkDataType type = NetworkDataTypeIndex.inst()
                            .getServiceByClass(name);
                    dataKeyIdx.add(type.getClass());
                }
            }
            
            {
                CacheList list = (CacheList)cacheMap.get(PROP_DATAVALS);
                for (int i = 0; i < list.size(); ++i)
                {
                    String val = list.getString(i, null);
                    dataValueIdx.add(val);
                }
            }
            
            //Build vertices
            {
                CacheList list = (CacheList)cacheMap.get(PROP_VERTS);
                for (int i = 0; i < list.size(); ++i)
                {
                    CacheList map = (CacheList)list.get(i);
                    loadVertex(path, map);
                }
            }
            
            {
                CacheList list = (CacheList)cacheMap.get(PROP_EDGES);
                for (int i = 0; i < list.size(); ++i)
                {
                    CacheList map = (CacheList)list.get(i);
                    loadEdge(path, map);
                }
            }
            
            {
                CacheList list = (CacheList)cacheMap.get(PROP_LOOPS);
                for (int i = 0; i < list.size(); ++i)
                {
                    CacheMap map = (CacheMap)list.get(i);
                    loadLoop(path, map);
                }
            }
            
            return path;
        }
    }
    
}
