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

import com.kitfox.coyote.math.BufferUtil;
import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.math.CyVector3d;
import com.kitfox.coyote.math.d3.BoundingBox3d;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class ObjLoaderData
{
    ArrayList<CyVector3d> points = new ArrayList<CyVector3d>();
    ArrayList<CyVector2d> texCoords = new ArrayList<CyVector2d>();
    ArrayList<CyVector3d> norms = new ArrayList<CyVector3d>();

    HashMap<ObjVertex, Integer> vertIdxMap = new HashMap<ObjVertex, Integer>();
    ArrayList<ObjVertex> vertList = new ArrayList<ObjVertex>();
    ArrayList<Integer> idxList = new ArrayList<Integer>();

    double minX = Double.POSITIVE_INFINITY;
    double maxX = Double.NEGATIVE_INFINITY;
    double minY = Double.POSITIVE_INFINITY;
    double maxY = Double.NEGATIVE_INFINITY;
    double minZ = Double.POSITIVE_INFINITY;
    double maxZ = Double.NEGATIVE_INFINITY;
    
    public ObjLoaderData(Reader reader) throws IOException
    {
        load(reader);
    }

    private void load(Reader reader) throws IOException
    {
        BufferedReader br = new BufferedReader(reader);
        
        for (String line = br.readLine(); line != null;
                line = br.readLine())
        {
            line = line.trim();
            if (line.length() == 0 || line.charAt(0) == '#')
            {
                continue;
            }
            
            String[] tokens = line.split(" ");
            
            String cmd = tokens[0];
            if ("v".equals(cmd))
            {
                double x = Double.parseDouble(tokens[1]);
                double y = Double.parseDouble(tokens[2]);
                double z = Double.parseDouble(tokens[3]);
                points.add(new CyVector3d(x, y, z));
                
                minX = Math.min(minX, x);
                maxX = Math.max(maxX, x);
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);
                minZ = Math.min(minZ, z);
                maxZ = Math.max(maxZ, z);
            }
            else if ("vt".equals(cmd))
            {
                double u = Double.parseDouble(tokens[1]);
                double v = Double.parseDouble(tokens[2]);
                texCoords.add(new CyVector2d(u, v));
            }
            else if ("vn".equals(cmd))
            {
                double x = Double.parseDouble(tokens[1]);
                double y = Double.parseDouble(tokens[2]);
                double z = Double.parseDouble(tokens[3]);
                norms.add(new CyVector3d(x, y, z));
            }
            else if ("f".equals(cmd))
            {
                addVertex(tokens[1]);
                addVertex(tokens[2]);
                addVertex(tokens[3]);
            }
        }
    }
    
    private void addVertex(String indices)
    {
        String[] idx = indices.split("/");
        
        int idxPt = Integer.parseInt(idx[0]) - 1;
        int idxUv = Integer.parseInt(idx[1]) - 1;
        int idxNorm = Integer.parseInt(idx[2]) - 1;
        
        ObjVertex v = new ObjVertex(points.get(idxPt), 
                texCoords.get(idxUv), 
                norms.get(idxNorm));
        
        Integer vIdx = vertIdxMap.get(v);
        if (vIdx == null)
        {
            vIdx = vertList.size();
            vertIdxMap.put(v, vIdx);
            vertList.add(v);
        }
        idxList.add(vIdx);
    }

    FloatBuffer createVertexData()
    {
        FloatBuffer buf = BufferUtil.allocateFloat(vertList.size() * 8);
        for (int i = 0; i < vertList.size(); ++i)
        {
            ObjVertex v = vertList.get(i);
            buf.put((float)v.pt.x);
            buf.put((float)v.pt.y);
            buf.put((float)v.pt.z);
        }

        for (int i = 0; i < vertList.size(); ++i)
        {
            ObjVertex v = vertList.get(i);
            buf.put((float)v.uv.x);
            buf.put((float)v.uv.y);
        }

        for (int i = 0; i < vertList.size(); ++i)
        {

            ObjVertex v = vertList.get(i);
            buf.put((float)v.norm.x);
            buf.put((float)v.norm.y);
            buf.put((float)v.norm.z);
        }
        buf.rewind();
        return buf;
    }

    ShortBuffer createIndexData()
    {
        ShortBuffer buf = BufferUtil.allocateShort(idxList.size());
        for (int i = 0; i < idxList.size(); ++i)
        {
            buf.put((short)(int)idxList.get(i));
        }
        buf.rewind();
        return buf;
    }
    
    int getVertexCount()
    {
        return vertList.size();
    }

    int getIndexCount()
    {
        return idxList.size();
    }

    BoundingBox3d getBounds()
    {
        return new BoundingBox3d(minX, minY, minZ, maxX, maxY, maxZ);
    }
    
}
