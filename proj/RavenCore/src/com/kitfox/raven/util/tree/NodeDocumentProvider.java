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

package com.kitfox.raven.util.tree;

import com.kitfox.raven.wizard.RavenWizardPageIterator;
import com.kitfox.xml.schema.ravendocumentschema.NodeSymbolType;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author kitfox
 */
abstract public class NodeDocumentProvider<T extends NodeDocument>
        implements Comparable<NodeDocumentProvider>
{
    private final Class<T> nodeType;
    private final String name;
    private final ImageIcon icon;

    private static ImageIcon DEFAULT_ICON
            = new ImageIcon(NodeObjectProvider.class.getResource("/icons/node/stack.png"));

    public NodeDocumentProvider(Class<T> nodeType, String name)
    {
        this(nodeType, name, DEFAULT_ICON);
    }

    public NodeDocumentProvider(Class<T> nodeType, String name, String iconPath)
    {
        this(nodeType, name, loadIcon(iconPath));
    }

    public NodeDocumentProvider(Class<T> nodeType, String name, ImageIcon icon)
    {
        this.nodeType = nodeType;
        this.name = name;
        this.icon = icon;
    }

    private static ImageIcon loadIcon(String path)
    {
        return new ImageIcon(NodeObjectProvider.class.getResource(path));
    }

    public Class<T> getNodeType()
    {
        return nodeType;
    }

    public String getName()
    {
        return name;
    }

    public Icon getIcon()
    {
        return icon;
    }

    abstract public RavenWizardPageIterator<T> createDocumentWizard();

    abstract public NodeDocument loadDocument(NodeSymbolType docTree);

    @Override
    public int compareTo(NodeDocumentProvider obj)
    {
        return name.compareTo(obj.getName());
    }

}
