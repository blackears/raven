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

import com.kitfox.cache.CacheIdentifier;
import com.kitfox.cache.CacheList;
import com.kitfox.cache.CacheMap;
import com.kitfox.cache.parser.CacheParser;
import com.kitfox.cache.parser.ParseException;
import com.kitfox.coyote.shape.bezier.BezierCubic2i;
import com.kitfox.coyote.shape.bezier.mesh.BezierMesh2i;
import com.kitfox.coyote.shape.bezier.mesh.BezierMeshEdge2i;
import com.kitfox.coyote.shape.bezier.mesh.BezierMeshVertex2i;
import com.kitfox.coyote.shape.bezier.mesh.BezierVertexSmooth;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class NetworkMesh extends BezierMesh2i<NetworkDataVertex, NetworkDataEdge>
{
    public static final String CACHE_NAME = "networkMesh";
    public static final String PROP_COORDS = "coords";
    public static final String PROP_VERTICES = "vertices";
    public static final String PROP_EDGES = "edges";
    public static final String PROP_DATAKEYS = "dataKeys";
    public static final String PROP_DATAVALS = "dataVals";
    
    //Default resolution of 2 pixels (ie 200 * 200)
    public static final double FLATNESS_SQ = 10000;

    public NetworkMesh()
    {
        super(FLATNESS_SQ);
    }

    public NetworkMesh(NetworkMesh mesh)
    {
        super(mesh.getFlatnessSquared());

        for (BezierMeshVertex2i vert: mesh.getVertices())
        {
            BezierMeshVertex2i vertNew = getOrCreateVertex(vert.getCoord());
            
            vertNew.setData(new NetworkDataVertex((NetworkDataVertex)
                    vert.getData()));
        }
        
        for (BezierMeshEdge2i edge: mesh.getEdges())
        {
            addEdgeDirect(edge.asCurve(), 
                    new NetworkDataEdge((NetworkDataEdge)edge.getData()));
        }
    }

    @Override
    protected NetworkDataVertex createDefaultVertexData(Coord c)
    {
        return new NetworkDataVertex();
    }
    
    public static NetworkMesh create(String text)
    {
        if (text == null || "".equals(text))
        {
            return new NetworkMesh();
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


    //--------------------------
    static class CacheLoader
    {
        ArrayList<Coord> pointIdx = new ArrayList<Coord>();
        ArrayList<Class<? extends NetworkDataType>> dataKeyIdx 
                = new ArrayList<Class<? extends NetworkDataType>>();
        ArrayList<String> dataValueIdx = new ArrayList<String>();
        
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
        
        public NetworkMesh load(CacheMap cacheMap)
        {
            NetworkMesh mesh = new NetworkMesh();
            
            //Build indices
            {
                CacheList list = (CacheList)cacheMap.get(PROP_COORDS);
                for (int i = 0; i < list.size(); ++i)
                {
                    CacheList tuple = (CacheList)list.get(i);
                    Coord c = new Coord(tuple.getInteger(0, 0), tuple.getInteger(1, 0));
                    pointIdx.add(c);
                }
            }
            
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
                CacheList list = (CacheList)cacheMap.get(PROP_VERTICES);
                for (int i = 0; i < list.size(); ++i)
                {
                    CacheList tuple = (CacheList)list.get(i);
                    int coordIdx = tuple.getInteger(0, -1);
                    Coord coord = pointIdx.get(coordIdx);

                    HashMap<Class<? extends NetworkDataType>, Object> dataMap
                            = loadDataMap((CacheList)tuple.get(1));
                    NetworkDataVertex data = new NetworkDataVertex(dataMap);
                    BezierMeshVertex2i v = mesh.getOrCreateVertex(coord);
                    v.setData(data);
                }
            }
            
            //Build edges
            {
                CacheList list = (CacheList)cacheMap.get(PROP_EDGES);
                for (int i = 0; i < list.size(); ++i)
                {
                    CacheList tuple = (CacheList)list.get(i);
                    BezierVertexSmooth smooth0 
                            = getSmooth(((CacheIdentifier)tuple.get(0)).getName());
                    BezierVertexSmooth smooth1 
                            = getSmooth(((CacheIdentifier)tuple.get(1)).getName());

                    Coord c0 = pointIdx.get(tuple.getInteger(2, -1));
                    Coord ck0 = pointIdx.get(tuple.getInteger(3, -1));
                    Coord ck1 = pointIdx.get(tuple.getInteger(4, -1));
                    Coord c1 = pointIdx.get(tuple.getInteger(5, -1));
                    
                    HashMap<Class<? extends NetworkDataType>, Object> 
                            dataEdge = loadDataMap((CacheList)tuple.get(6));
                    HashMap<Class<? extends NetworkDataType>, Object> 
                            dataLeft = loadDataMap((CacheList)tuple.get(7));
                    HashMap<Class<? extends NetworkDataType>, Object> 
                            dataRight = loadDataMap((CacheList)tuple.get(8));

                    NetworkDataEdge data = new NetworkDataEdge(dataEdge, dataLeft, dataRight);

                    BezierMeshEdge2i e = mesh.addEdgeDirect(
                            new BezierCubic2i(
                                c0.x, c0.y,
                                ck0.x, ck0.y,
                                ck1.x, ck1.y,
                                c1.x, c1.y
                            ), data);
                    e.setSmooth0(smooth0);
                    e.setSmooth1(smooth1);
                }
            }
 
            return mesh;
        }
    }

    class CacheBuilder
    {
        HashMap<Coord, Integer> pointMap = new HashMap<Coord, Integer>();
        ArrayList<Coord> pointIdx = new ArrayList<Coord>();
        
        HashMap<Class<? extends NetworkDataType>, Integer> dataKeyMap
                = new HashMap<Class<? extends NetworkDataType>, Integer>();
        ArrayList<Class<? extends NetworkDataType>> dataKeyIdx 
                = new ArrayList<Class<? extends NetworkDataType>>();
        
        HashMap<String, Integer> dataValueMap = new HashMap<String, Integer>();
        ArrayList<String> dataValueIdx = new ArrayList<String>();
        
        private void appendCoord(Coord c, HashMap<Coord, Integer> pointMap, ArrayList<Coord> pointIdx)
        {
            if (pointMap.containsKey(c))
            {
                return;
            }
            Integer i = pointIdx.size();
            pointIdx.add(c);
            pointMap.put(c, i);
        }
        
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
        
        private CacheList buildData(HashMap<Class<? extends NetworkDataType>, Object> dataMap)
        {
            CacheList list = new CacheList();
            
            for (Class<? extends NetworkDataType> key: dataMap.keySet())
            {
                Integer keyIdx = getDataKeyIdx(key);

                NetworkDataType type = 
                        NetworkDataTypeIndex.inst().getByData(key);
                Object value = dataMap.get(key);
                String valText = type.asText(value);
                Integer valIdx = getDataValueIdx(valText);

                CacheList tuple = new CacheList(keyIdx, valIdx);
                list.add(tuple);
            }
            
            return list;
        }
        
        public CacheMap build()
        {
            CacheMap map = new CacheMap(CACHE_NAME);

            //Build coord list
            for (BezierMeshEdge2i e: getEdges())
            {
                appendCoord(e.getStart().getCoord(), pointMap, pointIdx);
                appendCoord(e.getK0(), pointMap, pointIdx);
                appendCoord(e.getK1(), pointMap, pointIdx);
                appendCoord(e.getEnd().getCoord(), pointMap, pointIdx);
            }

            //Store coord list
            {
                CacheList list = new CacheList();
                map.put(PROP_COORDS, list);

                for (Coord c: pointIdx)
                {
                    CacheList tuple = new CacheList(c.x, c.y);
                    list.add(tuple);
                }
            }

            //Build vertex data
            {
                CacheList list = new CacheList();
                map.put(PROP_VERTICES, list);
                for (BezierMeshVertex2i v: getVertices())
                {
                    NetworkDataVertex data = (NetworkDataVertex)v.getData();

                    CacheList tuple = new CacheList();
                    tuple.add(pointMap.get(v.getCoord()));

                    tuple.add(buildData(data.dataMap));
                }
            }

            //Build edge list
            {
                CacheList list = new CacheList();
                map.put(PROP_EDGES, list);
                for (BezierMeshEdge2i e: getEdges())
                {
                    CacheList tuple = new CacheList();
                    tuple.add(new CacheIdentifier(getSmoothCode(e.getSmooth0())));
                    tuple.add(new CacheIdentifier(getSmoothCode(e.getSmooth1())));
                    tuple.add(pointMap.get(e.getStart().getCoord()));
                    tuple.add(pointMap.get(e.getK0()));
                    tuple.add(pointMap.get(e.getK1()));
                    tuple.add(pointMap.get(e.getEnd().getCoord()));
                    
                    NetworkDataEdge data = (NetworkDataEdge)e.getData();
                    tuple.add(buildData(data.dataMapEdge));
                    tuple.add(buildData(data.dataMapLeft));
                    tuple.add(buildData(data.dataMapRight));
                }
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
}
