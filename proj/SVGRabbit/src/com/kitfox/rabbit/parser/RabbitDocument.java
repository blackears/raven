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

package com.kitfox.rabbit.parser;

import com.kitfox.rabbit.types.ImageRef;
import com.kitfox.rabbit.types.ElementRef;
import com.kitfox.rabbit.nodes.RaElement;
import com.kitfox.rabbit.nodes.RaElementLoader;
import com.kitfox.rabbit.nodes.RaFe;
import com.kitfox.rabbit.nodes.RaSvg;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class RabbitDocument
{

    class NodeInfo
    {
//        final int index;
        final String localName;
        RaElement node;

//        public NodeInfo(int index, String localName, HaElement node)
        public NodeInfo(String localName, RaElement node)
        {
//            this.index = index;
            this.localName = localName;
            this.node = node;
        }
    }

    class ElementRefInfo
    {
        final ElementRef ref;
        final String name;

        public ElementRefInfo(ElementRef ref, String name)
        {
            this.ref = ref;
            this.name = name;
        }
    }

    HashMap<String, NodeInfo> nodeNameMap = new HashMap<String, NodeInfo>();
//    HashMap<Integer, NodeInfo> nodeIndexMap = new HashMap<Integer, NodeInfo>();
    HashMap<RaElement, NodeInfo> nodeMap = new HashMap<RaElement, NodeInfo>();
    HashMap<String, ElementRefInfo> elementRefServerNames = new HashMap<String, ElementRefInfo>();
    HashMap<ElementRef, ElementRefInfo> elementRefServer = new HashMap<ElementRef, ElementRefInfo>();
    ArrayList<RaElement> unnamedNodes = new ArrayList<RaElement>();

    HashMap<String, Integer> filterNames = new HashMap<String, Integer>();
    int nextFilterIndex;
    private int defaultFilter;

    private final URL source;
    private final URL base;
    private final int documentId;
    private final RabbitUniverseDom universe;
    private RaSvg rootNode;

    RabbitDocument(URL source, int documentId, RabbitUniverseDom universe)
    {
        this.source = source;
        this.documentId = documentId;
        this.universe = universe;

        String src = source.toExternalForm();
        int idx = src.lastIndexOf('/');
        URL baseUrl = null;
        try {
            baseUrl = new URL(src.substring(0, idx));
        } catch (MalformedURLException ex) {
            Logger.getLogger(RabbitDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        base = baseUrl;

        resetFilterIds();
    }
    
    public RaElement lookupElement(int elementIndex)
    {
        ElementRef ref = new ElementRef(documentId, elementIndex);
        //elementRefServer
        ElementRefInfo info = elementRefServer.get(ref);
        if (info == null)
        {
            return null;
        }
        return getNode(info.name);
    }

    public void registerNode(String id, RaElement node)
    {
        if (id == null || nodeNameMap.containsKey(id))
        {
            unnamedNodes.add(node);
        }
        else
        {
//            NodeInfo info = new NodeInfo(nodeNameMap.size(), id, node);
            NodeInfo info = new NodeInfo(id, node);
            nodeNameMap.put(id, info);
//            nodeIndexMap.put(info.index, info);
            nodeMap.put(node, info);
        }
    }

    public void fillInMissingNames(String rootName)
    {
        int idx = 0;
        for (RaElement ele: unnamedNodes)
        {
            String name;
            do
            {
                name = rootName + idx++;
            } while (nodeNameMap.containsKey(name));
            registerNode(name, ele);
        }
        unnamedNodes.clear();
    }

    public RaElement getNode(String localName)
    {
        NodeInfo info = nodeNameMap.get(localName);
        return info == null ? null : info.node;
    }

    public String getNodeName(RaElement ele)
    {
        NodeInfo info = nodeMap.get(ele);
        return info == null ? null : info.localName;
    }

    public int getNodeId(RaElement ele)
    {
        String name = getNodeName(ele);
        ElementRef ref = getLocalElementRef(name);
        return ref == null ? -1 : ref.getElementIndex();
    }

    public ArrayList<String> getReferencedElements()
    {
        return new ArrayList<String>(elementRefServerNames.keySet());
    }

    public ElementRef getLocalElementRef(String elementId)
    {
        ElementRefInfo info = elementRefServerNames.get(elementId);
        if (info == null)
        {
            ElementRef ref = new ElementRef(documentId, elementRefServer.size());
            info = new ElementRefInfo(ref, elementId);

            elementRefServer.put(ref, info);
            elementRefServerNames.put(elementId, info);
        }
        return info.ref;
    }

    public void resetFilterIds()
    {
        filterNames.clear();

        filterNames.put("SourceGraphic", RaFe.IMAGE_SOURCEGRAPHIC);
        filterNames.put("SourceAlpha", RaFe.IMAGE_SOURCEALPHA);
        filterNames.put("BackgroundImage", RaFe.IMAGE_BACKGROUNDIMAGE);
        filterNames.put("BackgroundAlpha", RaFe.IMAGE_BACKGROUNDALPHA);
        filterNames.put("FillPaint", RaFe.IMAGE_FILLPAINT);
        filterNames.put("StrokePaint", RaFe.IMAGE_STROKEPAINT);
        defaultFilter = RaFe.IMAGE_SOURCEGRAPHIC;

        nextFilterIndex = RaFe.IMAGE_STROKEPAINT + 1;
    }

    public int getFilterId(String name)
    {
        Integer val = filterNames.get(name);
        if (val == null)
        {
            val = nextFilterIndex++;
            filterNames.put(name, val);
        }
        return val;
    }

    /**
     * @return the defaultFilter
     */
    public int getLastResultFilter() {
        return defaultFilter;
    }

    /**
     * @param defaultFilter the defaultFilter to set
     */
    public void setDefaultFilter(int defaultFilter) {
        this.defaultFilter = defaultFilter;
    }

    public ElementRef getElementRef(String urlText)
    {
        urlText = urlText.replace('-', '_');
        int idx = urlText.indexOf('#');
        String eleId = urlText.substring(idx);
        if (idx == 0)
        {
            return getLocalElementRef(eleId.substring(1));
        }
        try {
            String pathPart = urlText.substring(0, idx);
            URL url = new URL(base, pathPart);
            RabbitDocument refBuilder = universe.getDocument(url);
            return refBuilder.getLocalElementRef(eleId + 1);
        } catch (MalformedURLException ex) {
            Logger.getLogger(RaElementLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the source
     */
    public URL getSource() {
        return source;
    }

    /**
     * @return the documentId
     */
    public int getDocumentId() {
        return documentId;
    }

    /**
     * @return the builder
     */
    public RabbitUniverseDom getUniverse() {
        return universe;
    }

    public ImageRef getImageRef(String urlText)
    {
        try {
            URL url = new URL(base, urlText);
            return universe.getImageRef(url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(RabbitDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the rootNode
     */
    public RaSvg getRootNode() {
        return rootNode;
    }

    /**
     * @param rootNode the rootNode to set
     */
    public void setRootNode(RaSvg rootNode) {
        this.rootNode = rootNode;
    }

    /**
     * @return the packageName
     */
    public String getPackageName(File baseDirFile)
    {
        try {
            URL baseDir = baseDirFile.toURI().toURL();
            String baseForm = baseDir.toExternalForm();
            String sourceForm = source.toExternalForm();

            if (!sourceForm.startsWith(baseForm))
            {
                return null;
            }

            String pkgName;
            int idx = sourceForm.lastIndexOf('/');
            pkgName = sourceForm.substring(baseForm.length(), idx);
            return pkgName.replace('/', '.');

        } catch (MalformedURLException ex) {
            Logger.getLogger(RabbitDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * @return the className
     */
    public String getClassName()
    {
        String sourceForm = source.toExternalForm();
        int idx = sourceForm.lastIndexOf('/');
        if (!sourceForm.endsWith(".svg"))
        {
            return null;
        }
        return sourceForm.substring(idx + 1, sourceForm.length() - 4);
    }
}
